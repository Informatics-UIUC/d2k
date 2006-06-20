package ncsa.d2k.modules.projects.dtcheng.ssa;

import ncsa.d2k.modules.projects.dtcheng.matrix.*;
import ncsa.d2k.modules.projects.dtcheng.examples.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.*;
import java.util.*;



/*
 *  This modules reads selected fields from the DCF group files and produces an example table.
 */


public class ReadDCFMatrix extends InputModule {
  final int    numGroupsToProcess     = 1;
  final int    bufferSize             = (int) 1e8;
  final int    gzipBufferSize         = (int) 1e6;
  final int    reportInterval         = 10000;
  
  
  String matrixDirectory = "c:/data/ssa/matrix/";
  public void setMatrixDirectory(String value) {
    if (value.lastIndexOf("/") != value.length() - 1)
      value += "/";
    this.matrixDirectory = value;
  }
  public String getMatrixDirectory() {
    return this.matrixDirectory;
  }
  
  String ssaDirectory = "c:/data/ssa/";
  public void setSsaDirectory(String value) {
    if (value.lastIndexOf("/") != value.length() - 1)
      value += "/";
    this.ssaDirectory = value;
  }
  public String getSsaDirectory() {
    return this.ssaDirectory;
  }
  
  
  private int groupNumber = 1;
  public void setGroupNumber(int value) {
    this.groupNumber = value;
  }
  public int getGroupNumber() {
    return this.groupNumber;
  }

  
  // which method should be used to define positive and negative examples
  final int PREDICT_ELIGIBILITY            = 0;
  final int PREDICT_MAILING_RESPONSE       = 1;
  final int PREDICT_ACTIVE_PARTICIPANT     = 2;
  final int PREDICT_TERMINATED_PARTICIPANT = 3;
  
  int exampleDefinitionMode = PREDICT_ACTIVE_PARTICIPANT;
  
  int numFilesToProcess = 10;
  
  
  
  //  selection time is when the dcf data files were created;  it is read from the dcf file itself;
  //  selectionTimeInMillis is the select time in milliseconds
  boolean selectionTimeSet = false;
  long    selectionTimeInMillis;
  
  // used to convert time in milliseconds to time in years
  long    millisPerYear = (long) (365.25 * 24 * 60 * 60 * 1000);
  
  
  
  public String getModuleName() {
    return "ReadDCFMatrix";
  }
  
  public String getModuleInfo() {
    return "ReadDCFMatrix";
  }
  
  
  //  should record structure information be reported to the console
  boolean reportRecordStructrure = false;
  
  //  should progress be reported to the console
  boolean reportProgress = true;
  
  //  how often, in terms of number of records, should progress be reportd
  int ReportInterval = 10000;
  
  //  for fine tuning I/O performancee
  int BufferSize = 1000000;
  
  public String getInputName(int i) {
    switch (i) {
      default:
        return "error";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "error";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SSNHashtable";
      default:
        return "error";
    }
  }
  
  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      default:
        return "No such output";
    }
  }
  
  final int  keyFieldIndex = 1;
  
  public String readRecord(int fileIndex) throws Exception {
    
    String key = null;
    
    for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
      
      // allocate and clear buffer for reading field
      //byte[] byteBuffer = null;
      byte [] byteBuffer = null;
      byteBuffer = fieldBytes[fileIndex][fieldIndex];
      for (int i = 0; i < fieldSizes[fileIndex][fieldIndex]; i++) {
        byteBuffer[i] = 64;  // fill with blanks
      }
      
      // read field one byte at a time
      int fieldByteIndex = 0;
      while (true) {
        int byteInt;
        
        // read next byte
        try {
          byteInt = bufferedInputStream[fileIndex].read();
          if (byteInt == -1) {
            System.out.println("EOF");
            EOFs[fileIndex] = true;
            break;
          }
        } catch (Exception e) {
          System.out.println("EOF");
          EOFs[fileIndex] = true;
          break;
        }
        
        // for error checking
        masterSums[fileIndex] += byteInt;
        
        byteIndices[fileIndex]++;
        
        // process byte depending on format type
        if (spec.FixedFormat[fileIndex]) {
          
          byteBuffer[fieldByteIndex] = (byte) byteInt;
          
          fieldByteIndex++;
          
          // check for fixed format end of field
          if (fieldByteIndex == fieldSizes[fileIndex][fieldIndex]) {
            break;
          }
        } else // if free format
        {
          //check for end of field hexB0
          if (byteInt == 176) {
            break;
          }
          
          if (fieldByteIndex == fieldSizes[fileIndex][fieldIndex]) {
            System.out.println("Error!   fieldByteIndex == byteBuffer.length ");
          }
          
          byteBuffer[fieldByteIndex] = (byte) byteInt;
          
          fieldByteIndex++;
        }
        
        
      }
      
      
      // stop file reading of EOF has been reached
      if (EOFs[fileIndex]) {
        break;
      }
      
      
      // transate all fields except SSN from ibm to ascii character set and create field string
      
      String byteString = null;
      
      byteString = new String(byteBuffer, 0, byteBuffer.length, "cp037");
      
      // store field data in to record structure
      fieldValues[fileIndex][fieldIndex] = byteString;
      
      
      if (fieldIndex == keyFieldIndex) {  // if on universal key field
        key = byteString;
      }
      
    }
    
    recordIndices[fileIndex]++;
    
    return key;
  }
  
  
  
  
  
  
  
  
  DCFFileSpecification spec = new DCFFileSpecification();
  
  int[]      totalNumFields = new    int[numFilesToProcess];
  
  String[][]  fieldNames     = new  String[numFilesToProcess][];
  String[][]  fieldValues    = new  String[numFilesToProcess][];
  int[][]     fieldSizes     = new     int[numFilesToProcess][];
  byte[][][]  fieldBytes     = new    byte[numFilesToProcess][][];
  int[]       recordSizes    = new     int[numFilesToProcess];
  boolean[][] readField      = new boolean[numFilesToProcess][];
  boolean[][] inputField     = new boolean[numFilesToProcess][];
  int[][] fieldInputIndex    = new int[numFilesToProcess][];
  
  
  
  long[]    byteIndices        = new    long[numFilesToProcess];
  int[]     recordIndices      = new     int[numFilesToProcess];
  boolean[] EOFs               = new boolean[numFilesToProcess];
  int[]     numPersonIDRepeats = new     int[numFilesToProcess];
  long[]    masterSums         = new    long[numFilesToProcess];
  String[]  lastKeys           = new  String[numFilesToProcess];
  
  BufferedInputStream[] bufferedInputStream = new BufferedInputStream[numFilesToProcess];
  
  Integer StaticObject = new Integer(1);
  
  
  
  
  int exampleIndex        = 0;
  int numPositiveExamples = 0;
  int numNegativeExamples = 0;
  int inputFeatureIndex   = -1;
  int outputFeatureIndex  = -1;
  
  byte [] data;
  
  OldContinuousByteExampleTable exampleSet;
  
  
  int [][] ComponentNumFields = new int[spec.NumFiles][];
  
  public int calculateFileStructures() throws Exception {
    
    
    
    int TotalSize = 0;
    // for each file create field name, get structure and open file
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      
      
      //
      //  get file specific information information
      //
      
      int NumParts = spec.ComponentFieldNames[fileIndex].length;
      
      if (NumParts != spec.ComponentFieldNames[fileIndex].length) {
        throw new Exception();
      }
      
      ComponentNumFields[fileIndex] = new int[NumParts];
      for (int i = 0; i < NumParts; i++) {
        ComponentNumFields[fileIndex][i] = spec.ComponentFieldNames[fileIndex][i].length;
      }
      
      totalNumFields[fileIndex] = 0;
      for (int p = 0; p < NumParts; p++) {
        totalNumFields[fileIndex] += ComponentNumFields[fileIndex][p] * spec.ComponentNumRepetitions[fileIndex][p];
      }
      
      fieldSizes [fileIndex] = new     int[totalNumFields[fileIndex]];
      fieldNames [fileIndex] = new  String[totalNumFields[fileIndex]];
      fieldValues[fileIndex] = new  String[totalNumFields[fileIndex]];
      readField  [fileIndex] = new boolean[totalNumFields[fileIndex]];
      fieldBytes [fileIndex] = new byte[totalNumFields[fileIndex]][];
      inputField [fileIndex] = new boolean[totalNumFields[fileIndex]];
      
      int index = 0;
      for (int p = 0; p < NumParts; p++) {
        
        if (spec.NestingMode[fileIndex].equals("normal")) {
          for (int r = 0; r < spec.ComponentNumRepetitions[fileIndex][p]; r++) {
            for (int f = 0; f < ComponentNumFields[fileIndex][p]; f++) {
              fieldSizes[fileIndex][index] = spec.ComponentFieldSizes[fileIndex][p][f];
              TotalSize += spec.ComponentFieldSizes[fileIndex][p][f];
              if (spec.ComponentNumRepetitions[fileIndex][p] == 1)
                fieldNames[fileIndex][index] = spec.ComponentFieldNames[fileIndex][p][f];
              else
                fieldNames[fileIndex][index] = spec.ComponentFieldNames[fileIndex][p][f] + "_" + (r+1);
              index++;
            }
          }
        } else {
          for (int f = 0; f < ComponentNumFields[fileIndex][p]; f++) {
            for (int r = 0; r < spec.ComponentNumRepetitions[fileIndex][p]; r++) {
              fieldSizes[fileIndex][index] = spec.ComponentFieldSizes[fileIndex][p][f];
              TotalSize += spec.ComponentFieldSizes[fileIndex][p][f];
              if (spec.ComponentNumRepetitions[fileIndex][p] == 1)
                fieldNames[fileIndex][index] = spec.ComponentFieldNames[fileIndex][p][f];
              else
                fieldNames[fileIndex][index] = spec.ComponentFieldNames[fileIndex][p][f] + "_" + (r+1);
              index++;
            }
          }
        }
      }
      
      // allocate field memory
      for (int i = 0; i < totalNumFields[fileIndex]; i++) {
        fieldBytes[fileIndex][i] = new byte[fieldSizes[fileIndex][i]];
      }
      
      
      if (reportRecordStructrure) {
        int byteIndex = 0;
        for (int i = 0; i < totalNumFields[fileIndex]; i++) {
          System.out.println("Field #" + (i + 1) + "  Byte #" + (byteIndex + 1) + "  Name = " +
           fieldNames[fileIndex][i] + "  Size = " + fieldSizes[fileIndex][i]);
          byteIndex += fieldSizes[fileIndex][i];
        }
        
      }
      
      
      
      
      
      /*********************/
      /*  open file files  */
      /*********************/
      
      if (bufferedInputStream[fileIndex] == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals("none")) {
            bufferedInputStream[fileIndex] = new BufferedInputStream(new FileInputStream(spec.FilePaths[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("zip")) {
            ZipFile zipFile = new ZipFile(spec.FilePaths[fileIndex]);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = (ZipEntry) e.nextElement();
            bufferedInputStream[fileIndex] = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStream[fileIndex] = new BufferedInputStream(new GZIPInputStream(new FileInputStream(spec.FilePaths[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println("couldn't open file: " + spec.FilePaths[fileIndex]);
          throw e;
        }
      }
      
    }
    
    return  TotalSize;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  int maxNumInputFeatures = 100;
  int numInputFeatures = -1;
  int numOutputFeatures = -1;
  
  public void openFile(int fileIndex) throws Exception {
    
    /*********************/
    /*  open file files  */
    /*********************/
    
    if (bufferedInputStream[fileIndex] == null) {
      
      try {
        if (spec.CompressionMode[fileIndex].equals("none")) {
          bufferedInputStream[fileIndex] = new BufferedInputStream(new FileInputStream(spec.FilePaths[fileIndex]), BufferSize);
        }
        if (spec.CompressionMode[fileIndex].equals("zip")) {
          ZipFile zipFile = new ZipFile(spec.FilePaths[fileIndex]);
          Enumeration e = zipFile.entries();
          ZipEntry zipEntry = (ZipEntry) e.nextElement();
          bufferedInputStream[fileIndex] = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
        }
        if (spec.CompressionMode[fileIndex].equals("gzip")) {
          //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
          bufferedInputStream[fileIndex] = new BufferedInputStream(new GZIPInputStream(new FileInputStream(spec.FilePaths[fileIndex])), BufferSize);
        }
        
      } catch (Exception e) {
        System.out.println("couldn't open file: " + spec.FilePaths[fileIndex]);
        throw e;
      }
    }
  }
  

  public void reportProgress(int fileIndex, int recordIndex) throws Exception {
    
    System.out.println();
    System.out.println();
    System.out.println(recordIndex);
    
    System.out.println();
    System.out.println("position: ");
      System.out.println(
       "  group " + (fileIndex + 1) +
       "  record " + recordIndices[fileIndex] +
       "  byte " + byteIndices[fileIndex]
       );
    
    long currentimeInMS = System.currentTimeMillis();
    double durationInSeconds = (currentimeInMS - lastTimeInMS) / 1000.0;
    double overalDurationInSeconds = (currentimeInMS - firstTimeInMS) / 1000.0;
    lastTimeInMS = currentimeInMS;
    double rate = (double) reportInterval / durationInSeconds;
    double overallRate = (double) recordIndex / overalDurationInSeconds;
    double hoursLeft = (double) (spec.NumRecords[fileIndex] - recordIndex) / rate / 3600;
    System.out.println(lastKeys[0] + ":" + recordIndex +
     "  :  current rec/s = " + rate  + "  :  overall rec/s = " + overallRate  + 
     "  :  hoursLeft = " + hoursLeft );
    
  }
    
  long firstTimeInMS;
  long lastTimeInMS;
  public void doit() throws Exception {
    
    spec.pathHeader = ssaDirectory;
    
    
    //readPositiveExampleSSNs();
    
    int numNotTicketEligible = 0;
    int numEligible = 0;
    int numMailed = 0;
    int numInUse = 0;
    int numNotInUse = 0;
    int numTerminated = 0;
    
    //////////////////////
    //  initialization  //
    //////////////////////
    
    
    calculateFileStructures();
    
    
    ///////////////////////
    //  main processing  //
    ///////////////////////
    
    
    System.out.println("Reading Files");
    boolean masterEOF = false;
    String masterKeyString = null;
    
    
    
    for (int fileIndex = (groupNumber - 1); fileIndex < (groupNumber - 1) + numGroupsToProcess; fileIndex++) {
      
      
      
      int numRecordsToRead = spec.NumRecords[fileIndex];
        
      
      int recordSize = 0;
      for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
        recordSize += fieldSizes[fileIndex][fieldIndex];
      }

      
      
      System.out.println();
      
      long totalFileSize = (long) numRecordsToRead * (long) recordSize;
      int numBuffers = (int) (recordSize / (long) bufferSize + 1);
      System.out.println("recordSize       = " + recordSize);
      System.out.println("numRecordsToRead = " + numRecordsToRead);
      System.out.println("totalFileSize    = " + totalFileSize);
      System.out.println("bufferSize       = " + bufferSize);
      System.out.println("numBuffers       = " + numBuffers);
      
      long [] dimensions = new long[] {numRecordsToRead, recordSize};
      long [] coordinates = new long[2];
      String matrixName = spec.MatrixNames[fileIndex];
      
      
      ByteMatrix byteMatrix = new ByteMatrix(ByteMatrix.dataInGzipObjectFiles, dimensions, bufferSize, gzipBufferSize, matrixDirectory, matrixName, false);

      
      
      
      firstTimeInMS = System.currentTimeMillis();
      lastTimeInMS = firstTimeInMS;
      
      // read each record in file
      for (int recordIndex = 0; recordIndex < spec.NumRecords[fileIndex]; recordIndex++) {
        
        // report progress
        if (reportProgress && recordIndex % reportInterval == 0) {
          reportProgress(fileIndex, recordIndex);
        }
        
        
        // update matrix
        coordinates[0] = recordIndex;
        
        int coordinate2 = 0;
        
        for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
          
          byte [] byteBuffer = null;
          byteBuffer = fieldBytes[fileIndex][fieldIndex];
          for (int i = 0; i < fieldSizes[fileIndex][fieldIndex]; i++) {
            coordinates[1] = coordinate2++;
            byteBuffer[i] = (byte) byteMatrix.getValue(coordinates);
            
          }
          
          if (fieldIndex == 1) {
            String byteString = new String(byteBuffer, 0, byteBuffer.length, "cp037");
            
            //System.out.println(byteString);
          }
        }
        
        
      }
      
    
    reportProgress(fileIndex, spec.NumRecords[fileIndex]);
    }
    
    
  }
}
