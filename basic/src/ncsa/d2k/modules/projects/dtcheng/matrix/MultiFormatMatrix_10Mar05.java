package ncsa.d2k.modules.projects.dtcheng.matrix;


import java.io.*;


public class MultiFormatMatrix_10Mar05 implements Serializable {

  ByteArrayOutputStream byteArrayOutputStream;
  ObjectOutputStream objectOutputStream;
  ByteArrayInputStream byteArrayInputStream;
  ObjectInputStream objectInputStream;

//  final int BufferSize = 32000;
//  final int BufferSize = 3000;
  int BufferSize = 300000;
  
  int dataFormat;
/*  final int dataInNativeArray = 0;
  final int dataInVector = 1;
  final int dataInVectorFile = 2;
  final int dataInObjectFile = 3;
*/
  final int dataInNativeArray = 0;
  final int dataInVector = 1;
  final int dataInVectorFile = 2;
  final int dataInObjectFile = 3;

  int numDimensions;
  long[] dimensions;
  long dataVectorLength = -1;
  Object dataNativeArray = null;
  String fileName;
  File file;
  RandomAccessFile randomAccessFile;
  int serializedBufferSize = -1;
  int lastBufferIndex = -1;
  long lastVectorIndex = -1;
  double[] ReadWriteDoubleBuffer;
  byte[] ReadWriteByteBuffer;
  boolean BufferChanged;
  boolean GarbageCollectMe = true;

  public MultiFormatMatrix_10Mar05() {

  }

  public void setGarbageCollectionMode(boolean newValue) {
  	GarbageCollectMe = newValue;
  }
  
  

  public void writeBlock(int index) throws Exception {

    byteArrayOutputStream = new ByteArrayOutputStream();
    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

    objectOutputStream.writeObject(ReadWriteDoubleBuffer);
    ReadWriteByteBuffer = byteArrayOutputStream.toByteArray();

    serializedBufferSize = ReadWriteByteBuffer.length;

    //System.out.println("writeBlock bufferIndex = " + bufferIndex + "  serializedBufferSize = " + serializedBufferSize);

    randomAccessFile.seek((long) index * serializedBufferSize);
    randomAccessFile.write(ReadWriteByteBuffer);

  }


  public void readBlock(int bufferIndex, long vectorIndex) throws Exception {

    //System.out.println("In readBlock bufferIndex = " + bufferIndex + "  serializedBufferSize = " + serializedBufferSize);

    randomAccessFile.seek((long) bufferIndex * serializedBufferSize);

    randomAccessFile.readFully(ReadWriteByteBuffer);

    byteArrayInputStream = new ByteArrayInputStream(ReadWriteByteBuffer);
    objectInputStream = new ObjectInputStream(byteArrayInputStream);

    ReadWriteDoubleBuffer = (double[]) objectInputStream.readObject();

    lastVectorIndex = vectorIndex;
    lastBufferIndex = bufferIndex;
    BufferChanged = false;
  }


  public void initialize(int dataFormat, int[] IntDimensions) throws Exception {

    long [] LongDimensions = new long[IntDimensions.length];
    for (int i = 0; i < IntDimensions.length; i++) {
      LongDimensions[i] = IntDimensions[i];
    }
    initialize(dataFormat, LongDimensions);
  }

    public void initialize(int dataFormat, long[] dimensions) throws Exception {

    this.dataFormat = dataFormat;
    this.dimensions = dimensions;
    this.numDimensions = dimensions.length;

    // calcluate length of data vector if necessary

    if (dataFormat == dataInVector ||
        dataFormat == dataInVectorFile ||
        dataFormat == dataInObjectFile) {
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

      case dataInNativeArray:
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

      case dataInVector:
        if (dataVectorLength > Integer.MAX_VALUE) {
          System.out.println("dataVectorLength > Integer.MAX_VALUE.");
        }
        else {
          ReadWriteDoubleBuffer = new double[(int) dataVectorLength];
        }
        break;

      case dataInVectorFile:
        fileName = "Matrix" + this.hashCode();
        try {
          file = new File(fileName);
          randomAccessFile = new RandomAccessFile(file, "rw");
          for (long i = 0; i < dataVectorLength; i++) {
            randomAccessFile.writeDouble(0.0);
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

        fileName = "Matrix" + this.hashCode();
//        int bufferPtr = 0;
//        int numBuffersWritten = 0;

        ReadWriteDoubleBuffer = new double[BufferSize];

        file = new File(fileName);
        randomAccessFile = new RandomAccessFile(file, "rw");

        for (int i = 0; i < ReadWriteDoubleBuffer.length; i++) {
          ReadWriteDoubleBuffer[i] = 0.0;
        }

        int numBuffers = (int) ((dataVectorLength - 1) / BufferSize) + 1;

        //System.out.println("initialize numBuffers = " + numBuffers);

        for (int i = 0; i < numBuffers; i++) {
          writeBlock(i);
        }

        try {
          randomAccessFile.seek(0L);
          lastVectorIndex = 0;
        } catch (Exception e) {
          System.out.println("Error!  randomAccessFile.seek(0L) failed");
        }

        lastBufferIndex = 0;
        lastVectorIndex = 0;
        BufferChanged = false;
        break;
      default:
        System.out.println("Error!  Unknown dataFormat.");
        break;
    }
  }


  public MultiFormatMatrix_10Mar05(int FormatIndex, long[] dimensions) throws Exception {
    initialize(FormatIndex, dimensions);
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
        dataFormat == dataInObjectFile) {

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

      case dataInNativeArray:
        switch (numDimensions) {
          case 1:
            return ((double[]) dataNativeArray)[(int) coordinates[0]];
          case 2:
            return ((double[][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]];
          case 3:
            return ((double[][][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]][(int) coordinates[2]];
          default:
            System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
            return Double.NaN;
        }

      case dataInVector:
        return ReadWriteDoubleBuffer[(int) vectorIndex];

      case dataInVectorFile: {
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
      case dataInObjectFile:
      	
        synchronized (this) {


        int bufferIndex = (int) (vectorIndex / BufferSize);

        if (bufferIndex != lastBufferIndex) {

          if (BufferChanged) {
            writeBlock(lastBufferIndex);
          }

          readBlock(bufferIndex, vectorIndex);
        }

        return ReadWriteDoubleBuffer[(int) (vectorIndex % BufferSize)];

        }

      default:
        System.out.println("Error!  Unkown data format (" + dataFormat + ").");
        return Double.NaN;
    }
  }


  public void setValue(long i, double value) throws Exception {
    long[] coordinates = {
        i};
    setValue(coordinates, value);
  }


  public void setValue(long i, long j, double value) throws Exception {
    long[] coordinates = {
        i, j};
    setValue(coordinates, value);
  }


  public void setValue(long i, long j, long k, double value) throws Exception {
    long[] coordinates = {
        i, j, k};
    setValue(coordinates, value);
  }


  public void setValue(long[] coordinates, double value) throws Exception {

    long vectorIndex = 0;

    if (dataFormat == dataInVector ||
        dataFormat == dataInVectorFile ||
        dataFormat == dataInObjectFile) {

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
      case dataInNativeArray:
        switch (numDimensions) {
          case 1:
            ((double[]) dataNativeArray)[(int) coordinates[0]] = value;
            break;
          case 2:
            ((double[][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]] = value;
            break;
          case 3:
            ((double[][][]) dataNativeArray)[(int) coordinates[0]][(int) coordinates[1]][(int) coordinates[2]] = value;
            break;
          default:
            System.out.println("Error!  Invalid number of dimensions (" + numDimensions + ").");
            break;
        }
        return;

      case dataInVector:
        ReadWriteDoubleBuffer[(int) vectorIndex] = value;
        return;

      case dataInVectorFile:
        try {
          randomAccessFile.seek( vectorIndex * 8);
          randomAccessFile.writeDouble(value);
          return;
        } catch (Exception e) {
          System.out.println("Error!  writing index (" + vectorIndex + ") failed");
        }
        return;

      case dataInObjectFile:
        synchronized (this) {

        int bufferIndex = (int) (vectorIndex / BufferSize);

        if (bufferIndex != lastBufferIndex) {

          if (BufferChanged) {
            writeBlock(lastBufferIndex);
          }

          readBlock(bufferIndex, vectorIndex);
        }

        if (ReadWriteDoubleBuffer[(int) (vectorIndex % BufferSize)] != value) {
          ReadWriteDoubleBuffer[(int) (vectorIndex % BufferSize)] = value;
          BufferChanged = true;
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
  	if (GarbageCollectMe) {
    switch (dataFormat) {

      case dataInNativeArray:
        break;

      case dataInVector:
        break;

      case dataInVectorFile:
        randomAccessFile.close();
        file.delete();
        break;

      case dataInObjectFile:
        File file = new File(fileName);
        randomAccessFile.close();
        file.delete();
        break;

      default:
        System.out.println("Error!  Unkown data format (" + dataFormat + ").");
        return;
    	}
    }
  }

}
