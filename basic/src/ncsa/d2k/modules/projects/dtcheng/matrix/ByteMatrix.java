package ncsa.d2k.modules.projects.dtcheng.matrix;


import java.io.*;
import java.util.zip.*;


public class ByteMatrix implements Serializable {
  
  ByteArrayOutputStream byteArrayOutputStream;
  ObjectOutputStream    objectOutputStream;
  ByteArrayInputStream  byteArrayInputStream;
  ObjectInputStream     objectInputStream;
  
  
  int bufferSize;
  int gzipBufferSize;
  int numBuffers;
  
  int dataFormat;
  
  // Beware the MAGIC NUMBERS!!! these are getting hardcoded to avoid memory bottlenecks...
  public final static int dataInNativeArray     = 0;
  public final static int dataInVector          = 1;
  public final static int dataInVectorFile      = 2;
  public final static int dataInObjectFile      = 3;
  public final static int dataInGzipObjectFiles = 4;
  
  int    numDimensions;
  long[] dimensions;
  long   dataVectorLength = -1;
  
  Object dataNativeArray = null;
  
  String           matrixName = "Matrix";
  boolean          clearMatrix;
  String           directoryPath;
  String           fileName;
  File             file;
  RandomAccessFile randomAccessFile;
  int              serializedBufferSize = -1;
  
  byte[]   readWriteDataBuffer;  // !! change for each type
  byte[]   readWriteByteBuffer;
  int      lastBufferIndex = -1;
  long     lastVectorIndex = -1;
  boolean  bufferChanged;
  boolean  deleteFileWhenFinalized = true;
  
  public ByteMatrix() {
    
  }
  
  public ByteMatrix(int FormatIndex, long[] dimensions, int bufferSize, int gzipBufferSize, String directoryPath, String matrixName, boolean clearMatrix) throws Exception {
    this.bufferSize     = bufferSize;
    this.gzipBufferSize = gzipBufferSize;
    if (directoryPath.lastIndexOf("/") != directoryPath.length() - 1)
      directoryPath  = directoryPath + "/"; 
    this.directoryPath  = directoryPath; 
    this.matrixName     = matrixName;
    this.clearMatrix    = clearMatrix;
    initialize(FormatIndex, dimensions);
  }
  
  
  
  
  
  public void initialize(int dataFormat, int[] IntDimensions) throws Exception {
    
    long [] LongDimensions = new long[IntDimensions.length];
    for (int i = 0; i < IntDimensions.length; i++) {
      LongDimensions[i] = IntDimensions[i];
    }
    initialize(dataFormat, LongDimensions);
  }
  
  public void initialize(int dataFormat, long[] dimensions) throws Exception {
    
    this.dataFormat    = dataFormat;
    this.dimensions    = dimensions;
    this.numDimensions = dimensions.length;
    
    
    //////////////////////////////////////////////////
    // calcluate length of data vector if necessary //
    //////////////////////////////////////////////////
    
    if (dataFormat == dataInVector ||
     dataFormat == dataInVectorFile ||
     dataFormat == dataInObjectFile ||
     dataFormat == dataInGzipObjectFiles) {
      switch (numDimensions) {
        case 1:
          dataVectorLength = dimensions[0];
          break;
        case 2:
          dataVectorLength = dimensions[0] * dimensions[1];
          break;
        case 3:
          dataVectorLength = dimensions[0] * dimensions[1] * dimensions[2];
          break;
        default:
          System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
          break;
      }
    }
    
    // allocate space
    switch (dataFormat) {
      
      // case dataInNativeArray:
      case dataInNativeArray:
        // Beware the MAGIC NUMBER!!! 0 = dataInNativeArray
        switch (numDimensions) {
          case 1:
            dataNativeArray = new double[(int) dimensions[0]];
            break;
          case 2:
            dataNativeArray = new double[(int) dimensions[0]][(int) dimensions[1]];
            break;
          case 3:
            dataNativeArray = new double[(int) dimensions[0]][(int) dimensions[1]][(int) dimensions[2]];
            break;
          default:
            System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
            break;
        }
        break;
        
      // case dataInVector:
      case dataInVector:
        // Beware the MAGIC NUMBER!!! 1=dataInVector
        if (dataVectorLength > Integer.MAX_VALUE) {
          System.out.println("dataVectorLength > Integer.MAX_VALUE.");
        } else {
          readWriteDataBuffer = new byte[(int) dataVectorLength];  // !! change for each type
        }
        break;
        
      // case dataInVectorFile:
      case dataInVectorFile:
        // Beware the MAGIC NUMBER!!! 2=dataInVectorFile
        fileName = createMatrixFilePath();
        try {
          file = new File(fileName);
          randomAccessFile = new RandomAccessFile(file, "rw");
          for (long i = 0; i < dataVectorLength; i++) {
            randomAccessFile.writeByte(0);  // !! change for each type
          }
        } catch (Exception e) {
          System.out.println("Error!  File (" + fileName + ") could not be initialized.");
        }
        
        try {
          randomAccessFile.seek(0L);
          lastVectorIndex = 0;
        } catch (Exception e) {
          System.out.println("Error!  randomAccessFile.seek(0L) failed");
        }
        
        break;
        
      case dataInObjectFile:
      {
        // Beware the MAGIC NUMBER!!! 3=dataInObjectFile
        
        fileName = createMatrixFilePath();
//        int bufferPtr = 0;
//        int numBuffersWritten = 0;
        
        readWriteDataBuffer = new byte[bufferSize];  // !! change for each type
        
        file = new File(fileName);
        randomAccessFile = new RandomAccessFile(file, "rw");
        
        if (clearMatrix) {
          
          for (int i = 0; i < readWriteDataBuffer.length; i++) {
            readWriteDataBuffer[i] = 0;
          }
          
          int numBuffers = (int) ((dataVectorLength - 1) / bufferSize) + 1;
          
          for (int i = 0; i < numBuffers; i++) {
            writeBlock(i);
          }
        }
        
        try {
          randomAccessFile.seek(0L);
          lastVectorIndex = 0;
        } catch (Exception e) {
          System.out.println("Error!  randomAccessFile.seek(0L) failed");
        }
        
        lastBufferIndex = 0;
        lastVectorIndex = 0;
        bufferChanged = false;
      }
      break;
      
      //dataInGzipObjectFiles
      case dataInGzipObjectFiles: 
      {
        readWriteDataBuffer = new byte[bufferSize];  // !! change for each type
        readWriteByteBuffer = new byte[bufferSize + 1000];  // !!! may need to be larger than buffer size
        
        int numBuffers = (int) ((dataVectorLength - 1) / bufferSize) + 1;
        
        System.out.println("initialize numBuffers = " + numBuffers);
        
        if (clearMatrix) {
          
          for (int i = 0; i < readWriteDataBuffer.length; i++) {
            readWriteDataBuffer[i] = 0;
          }
          
          // write out first block
          writeBlock(0);
          
          // copy first block to initialize all other blocks
          String fileNameOriginal = createMatrixFilePath(0);
          for (int i = 1; i < numBuffers; i++) {
            String fileNameCopy = createMatrixFilePath(i);
            copyFileToFile(new File(fileNameOriginal), new File(fileNameCopy));
          }
        }
        else {
          readBlock(0, 0);
        }
        
        
        lastVectorIndex = 0;
        lastBufferIndex = 0;
        lastVectorIndex = 0;
        bufferChanged   = false;
      }
      break;
      
      default:
        System.out.println("Error!  Unknown dataFormat.");
        break;
    }
  }
  
  
   // Copies src file to dst file.
    // If the dst file does not exist, it is created
    void copyFileToFile(File src, File dst) throws IOException {
        InputStream  in  = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public void setGarbageCollectionMode(boolean newValue) {
    deleteFileWhenFinalized = newValue;
  }
  
  
  public String createMatrixFilePath () {
    if (matrixName == null)
    return directoryPath + "Matrix"   + "." + this.hashCode();
    else
    return directoryPath + matrixName;
  }
  
  public String createMatrixFilePath(int bufferIndex) {
    if (matrixName == null)
      return directoryPath + "Matrix"   + "." + this.hashCode() + "." + (bufferIndex);
    else
      return directoryPath + matrixName + "." + (bufferIndex);
  }

  public void writeBlock(int bufferIndex) throws Exception {
    
    BufferedOutputStream bufferedOutputStream = null;
    if (dataFormat == 4) {       
      fileName = createMatrixFilePath(bufferIndex);
        file = new File(fileName);
        
        bufferedOutputStream = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)), gzipBufferSize);

    }

    byteArrayOutputStream = new ByteArrayOutputStream();
    objectOutputStream    = new ObjectOutputStream(byteArrayOutputStream);
    
    objectOutputStream.writeObject(readWriteDataBuffer);
    readWriteByteBuffer = byteArrayOutputStream.toByteArray();
    
    serializedBufferSize = readWriteByteBuffer.length;
    
    //System.out.println("writeBlock bufferIndex = " + bufferIndex + "  serializedBufferSize = " + serializedBufferSize);
    
    
    if (dataFormat != 4) {   
    randomAccessFile.seek((long) bufferIndex * serializedBufferSize);
    randomAccessFile.write(readWriteByteBuffer);
    } else {
      
      bufferedOutputStream.write(readWriteByteBuffer);
      bufferedOutputStream.close();
    }
    
    
  }
  
  
  public void readBlock(int bufferIndex, long vectorIndex) throws Exception {
    
    BufferedInputStream bufferedInputStream = null;
    
    
    if (dataFormat != 4) {
      
      randomAccessFile.seek((long) bufferIndex * serializedBufferSize);
      
      randomAccessFile.readFully(readWriteByteBuffer);
      
      
      
    } else {
      fileName = createMatrixFilePath(bufferIndex);
      file = new File(fileName);
      bufferedInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(fileName)), bufferSize);
      
      System.out.println("In readBlock fileName = " + fileName);
      
      
        byte[] buf = new byte[1024];
        int len;
        int index = 0;
        while ((len = bufferedInputStream.read(buf)) > 0) {
          for (int i = 0; i < len; i++) {
            readWriteByteBuffer[index++] = buf[i];
          }
        }
      
      
      
      
//      int result = bufferedInputStream.read(readWriteByteBuffer);
//      
//      if (result == -1) {
//        throw new Exception();
//      }
      
      bufferedInputStream.close();
      
    }

    
    byteArrayInputStream = new ByteArrayInputStream(readWriteByteBuffer);
    objectInputStream = new ObjectInputStream(byteArrayInputStream);
    
    readWriteDataBuffer = (byte[]) objectInputStream.readObject();  // !! change for each type
    
    lastVectorIndex = vectorIndex;
    lastBufferIndex = bufferIndex;
    bufferChanged = false;
    
    
  }
  
  
  public int getNumDimensions() {
    return numDimensions;
  }
  
  public long[] getDimensions() {
    return dimensions;
  }
  
  
  public double getValue(long i) throws Exception {
    long[] coordinates = {
      i};
      return getValue(coordinates);
  }
  
  
  public double getValue(long i, long j) throws Exception {
    long[] coordinates = {
      i, j};
      return getValue(coordinates);
  }
  
  
  public double getValue(long i, long j, long k) throws Exception {
    long[] coordinates = {
      i, j, k};
      return getValue(coordinates);
  }
  
  
  public double getValue(long[] coordinates) throws Exception {
    
    long vectorIndex = -1;
    
    if (dataFormat == dataInVector ||
     dataFormat == dataInVectorFile ||
     dataFormat == dataInObjectFile||
     dataFormat == dataInGzipObjectFiles) {
      
      switch (numDimensions) {
        case 1:
          vectorIndex = coordinates[0];
          break;
        case 2:
          vectorIndex = (coordinates[0] * dimensions[1]) + coordinates[1];
          break;
        case 3:
          vectorIndex = ((coordinates[0] * dimensions[1]) + coordinates[1]) * dimensions[2] + coordinates[2];
          break;
        default:
          System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
          break;
      }
      
    }
    
    switch (dataFormat) {
      
//  case dataInNativeArray:
      case dataInNativeArray:
        // Beware the MAGIC NUMBER!!! 0 = dataInNativeArray
        switch (numDimensions) {
          case 1: return ((double[]) dataNativeArray)[(int) coordinates[0]];
          case 2: return ((double[][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]];
          case 3: return ((double[][][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]][(int) coordinates[2]];
          default:
            System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
            return Double.NaN;
        }
        
//      case dataInVector:
      case dataInVector:
        // Beware the MAGIC NUMBER!!! 1=dataInVector
        return readWriteDataBuffer[(int) vectorIndex];
        
//      case dataInVectorFile:
      case dataInVectorFile:
        // Beware the MAGIC NUMBER!!! 2=dataInVectorFile
        
      {
        synchronized (this) {
          
          
          if (vectorIndex != lastVectorIndex + 1) {
            try {
              randomAccessFile.seek(vectorIndex * 8);
            } catch (Exception e) {
              System.out.println("Error!  randomAccessFile.seek( (long) index * 8) failed");
            }
          }
          
          double value = Double.NaN;
          try {
            value = randomAccessFile.readDouble();
          } catch (Exception e) {
            System.out.println("Error!  randomAccessFile.readDouble() failed");
          }
          
          lastVectorIndex = vectorIndex;
          return value;
        }
        
      }
      
      //case dataInObjectFile:
      case dataInObjectFile:
        // Beware the MAGIC NUMBER!!! 3=dataInObjectFile
        
        synchronized (this) {
          
          
          int bufferIndex = (int) (vectorIndex / bufferSize);
          
          if (bufferIndex != lastBufferIndex) {
            
            if (bufferChanged) {
              writeBlock(lastBufferIndex);
            }
            
            readBlock(bufferIndex, vectorIndex);
          }
          
          return readWriteDataBuffer[(int) (vectorIndex % bufferSize)];
          
        }
        //case dataInGzipObjectFiles:
      case dataInGzipObjectFiles:
        // Beware the MAGIC NUMBER!!! 3=dataInGzipObjectFiles
        
        synchronized (this) {
          
          
          int bufferIndex = (int) (vectorIndex / bufferSize);
          
          if (bufferIndex != lastBufferIndex) {
            
            if (bufferChanged) {
              writeBlock(lastBufferIndex);
            }
            
            readBlock(bufferIndex, vectorIndex);
          }
          
          return readWriteDataBuffer[(int) (vectorIndex % bufferSize)];
          
        }
        
      default:
        System.out.println("Error!  Unkown data format (" + dataFormat + ").");
        return Double.NaN;
    }
  }
  
  
  
  long[] coordinates1D =  new long[1];
  public void setValue(long i, double value) throws Exception {
    coordinates1D[0] = i;
    setValue(coordinates1D, value);
  }
  long[] coordinates2D =  new long[2];
  public void setValue(long i, long j, double value) throws Exception {
    coordinates2D[0] = i;
    coordinates2D[1] = j;
    setValue(coordinates2D, value);
  }
  long[] coordinates3D =  new long[3];
  public void setValue(long i, long j, long k, double value) throws Exception {
    coordinates3D[0] = i;
    coordinates3D[1] = j;
    coordinates3D[2] = k;
    setValue(coordinates3D, value);
  }


  public void setValue(long[] coordinates, double value) throws Exception {
    
    long vectorIndex = 0;
    
    if (dataFormat == dataInVector ||
     dataFormat == dataInVectorFile ||
     dataFormat == dataInObjectFile||
     dataFormat == dataInGzipObjectFiles) {
      
      switch (numDimensions) {
        case 1:
          vectorIndex = coordinates[0];
          break;
        case 2:
          vectorIndex = (coordinates[0] * dimensions[1]) + coordinates[1];
          break;
        case 3:
          vectorIndex = ((coordinates[0] * dimensions[1]) + coordinates[1]) * dimensions[2] + coordinates[2];
          break;
        default:
          System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
          break;
      }
      
    }
    
    switch (dataFormat) {
//      case dataInNativeArray:
      case dataInNativeArray:
        // Beware the MAGIC NUMBER!!! 0 = dataInNativeArray
        switch (numDimensions) {
          case 1: ((double[])     dataNativeArray)[(int) coordinates[0]]                                             = value;
            break;
          case 2: ((double[][])   dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]]                       = value;
            break;
          case 3: ((double[][][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]][(int) coordinates[2]] = value;
            break;
          default:
            System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
            break;
        }
        return;
        
//      case dataInVector:
      case dataInVector:
        // Beware the MAGIC NUMBER!!! 1=dataInVector
        readWriteDataBuffer[(int) vectorIndex] = (byte) value;  // !! change for each type
        return;
        
//      case dataInVectorFile:
      case dataInVectorFile:
        // Beware the MAGIC NUMBER!!! 2=dataInVectorFile
        
        try {
          randomAccessFile.seek( vectorIndex * 8);
          randomAccessFile.writeDouble(value);
          return;
        } catch (Exception e) {
          System.out.println("Error!  writing index (" + vectorIndex + ") failed");
        }
        return;
        
//      case dataInObjectFile:
      case dataInObjectFile:
        // Beware the MAGIC NUMBER!!! 3=dataInObjectFile
        
        synchronized (this) {
          
          int bufferIndex = (int) (vectorIndex / bufferSize);
          
          if (bufferIndex != lastBufferIndex) {
            
            if (bufferChanged) {
              writeBlock(lastBufferIndex);
            }
            
            readBlock(bufferIndex, vectorIndex);
          }
          
          if (readWriteDataBuffer[(int) (vectorIndex % bufferSize)] != value) {
            readWriteDataBuffer[(int) (vectorIndex % bufferSize)] = (byte) value;  // !! change for each type
            bufferChanged = true;
          }
          
          return;
          
        }
        
//      case dataInGzipObjectFiles:
      case dataInGzipObjectFiles:
        // Beware the MAGIC NUMBER!!! 4=dataInGzipObjectFiles
        
        synchronized (this) {
          
          int bufferIndex = (int) (vectorIndex / bufferSize);
          
          if (bufferIndex != lastBufferIndex) {
            
            if (bufferChanged) {
              writeBlock(lastBufferIndex);
            }
            
            readBlock(bufferIndex, vectorIndex);
          }
          
          if (readWriteDataBuffer[(int) (vectorIndex % bufferSize)] != value) {
            readWriteDataBuffer[(int) (vectorIndex % bufferSize)] = (byte) value;  // !! change for each type
            bufferChanged = true;
          }
          
          return;
          
        }
        
      default:
        System.out.println("Error!  Unkown data format (" + dataFormat + ").");
        return;
    }
  }
  
  
  protected void finalize() throws Exception {
    //System.out.println("Finalizing Matrix" + this.hashCode());
    if (deleteFileWhenFinalized) {
      switch (dataFormat) {
        
//      case dataInNativeArray:
        case dataInNativeArray:
          // Beware the MAGIC NUMBER!!! 0 = dataInNativeArray
          
          break;
          
//      case dataInVector:
        case dataInVector:
          // Beware the MAGIC NUMBER!!! 1=dataInVector
          break;
          
//      case dataInVectorFile:
        case dataInVectorFile:
          // Beware the MAGIC NUMBER!!! 2=dataInVectorFile
          randomAccessFile.close();
          file.delete();
          break;
          
//      case dataInObjectFile:
        case dataInObjectFile:
          // Beware the MAGIC NUMBER!!! 3=dataInObjectFile
        {
          File file = new File(fileName);
          randomAccessFile.close();
          file.delete();
        }
        break;
        
//      case dataInGzipObjectFiles:
        case dataInGzipObjectFiles:
          // Beware the MAGIC NUMBER!!! 4=dataInGzipObjectFiles
        {
          for (int bufferIndex = 0; bufferIndex < numBuffers; bufferIndex++) {
            fileName = createMatrixFilePath(bufferIndex);
            file.delete();
          }
        }
        break;
        default:
          System.out.println("Error!  Unkown data format (" + dataFormat + ").");
          return;
      }
    }
  }
  
}
