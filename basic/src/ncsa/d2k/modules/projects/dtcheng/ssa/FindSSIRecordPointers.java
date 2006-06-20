package ncsa.d2k.modules.projects.dtcheng.ssa;

import ncsa.d2k.modules.projects.dtcheng.examples.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.*;
import java.util.*;


/*
 *  This modules reads selected fields from the SSI group files and produces an example table.
 */


public class FindSSIRecordPointers extends InputModule {
  
  final double missingGroupFieldValue = 0;
  final int reportInterval      = 1000;
  
  
  int numFilesToProcess = 14;
  
  
  
  //  selection time is when the dcf data files were created;  it is read from the dcf file itself;
  //  selectionTimeInMillis is the select time in milliseconds
  boolean selectionTimeSet = false;
  long    selectionTimeInMillis;
  
  // used to convert time in milliseconds to time in years
  long    millisPerYear = (long) (365.25 * 24 * 60 * 60 * 1000);
  
  
  
  public String getModuleName() {
    return "FindSSIRecordPointers";
  }
  
  public String getModuleInfo() {
    return "FindSSIRecordPointers";
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
        return "results";
      default:
        return "error";
    }
  }
  
  public String[] getOutputTypes() {
    String[] out = { "java.lang.Object"};
    return out;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "results";
      default:
        return "No such output";
    }
  }
  
  
  final int  keyFieldIndex = 1;
  
  static byte [] byteBuffer = new byte[9];
  long bytePosition = -1;
  public String readRecord(int fileIndex) throws Exception {
    
    
    recordFileIndices  [recordIndex] = (byte) fileIndex;
    recordBytePositions[recordIndex] = bytePosition;
    
    String key = null;
    
    
    for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
      
      boolean decode = false;
      
      if (fieldIndex == keyFieldIndex) {
        decode = true;
      }
      
      // read field one byte at a time
      int fieldByteIndex = 0;
      while (true) {
        int byteInt;
        
        // read next byte
        try {
          byteInt = bufferedInputStream.read();
          if (byteInt == -1) {
            //System.out.println ("EOF");
            EOF = true;
            break;
          }
        } catch (Exception e) {
          //System.out.println ("EOF");
          EOF = true;
          break;
        }
        
        byteIndices[fileIndex]++;
        
        // process byte depending on format type
        if (spec.FixedFormat[fileIndex]) {
          
          if (decode)
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
          
          if (decode)
            byteBuffer[fieldByteIndex] = (byte) byteInt;
          
          fieldByteIndex++;
        }
        
        
      }
      
      
      // stop file reading of EOF has been reached
      if (EOF) {
        break;
      }
      
      
      // transate all fields except SSN from ibm to ascii character set and create field string
      
      
      if (fieldIndex == keyFieldIndex) {  // if on universal key field
        
        key = new String(byteBuffer, 0, byteBuffer.length, "cp037");
      }
      
    }
    
    recordIndices[fileIndex]++;
    
    return key;
  }
  
  
  
  
  SSIFileSpecification spec = new SSIFileSpecification();
  
  int[]      totalNumFields = new    int[numFilesToProcess];
  
  String[][]  fieldNames     = new  String[numFilesToProcess][];
  String[][]  fieldValues    = new  String[numFilesToProcess][];
  int[][]     fieldSizes     = new     int[numFilesToProcess][];
  byte[][][]  fieldBytes     = new    byte[numFilesToProcess][][];
  boolean[][] readField      = new boolean[numFilesToProcess][];
  boolean[][] inputField     = new boolean[numFilesToProcess][];
  int[][] fieldInputIndex    = new int[numFilesToProcess][];
  
  
  
  long[]    byteIndices        = new    long[numFilesToProcess];
  int[]     recordIndices      = new     int[numFilesToProcess];
  boolean   EOF = false;
  int[]     numPersonIDRepeats = new     int[numFilesToProcess];
  long[]    masterSums         = new    long[numFilesToProcess];
  String[]  lastKeys           = new  String[numFilesToProcess];
  
  BufferedInputStream bufferedInputStream = null;
  
  Integer StaticObject = new Integer(1);
  
  
  
  
  int exampleIndex        = 0;
  int inputFeatureIndex   = -1;
  
  int [][] ComponentNumFields = new int[spec.NumFiles][];
  
  public void calculateFileStructure() throws Exception {
    
    
    
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
      
      int TotalSize = 0;
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
      
      //System.out.println ("TotalSize = " + TotalSize);
      
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
      
      if (bufferedInputStream == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals("none")) {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(spec.FilePaths[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("zip")) {
            ZipFile zipFile = new ZipFile(spec.FilePaths[fileIndex]);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = (ZipEntry) e.nextElement();
            bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(FilePaths[FileIndex]))));
            bufferedInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(spec.FilePaths[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println("couldn't open file: " + spec.FilePaths[fileIndex]);
          throw e;
        }
      }
      
    }
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public void openFile(int fileIndex) throws Exception {
    
    // for each file create field name, get structure and open file
    
    /*********************/
    /*  open file files  */
    /*********************/
    
    
    try {
      if (spec.CompressionMode.equals("none")) {
        bufferedInputStream= new BufferedInputStream(new FileInputStream(spec.FilePaths[fileIndex]), BufferSize);
      }
      if (spec.CompressionMode[fileIndex].equals("zip")) {
        ZipFile zipFile = new ZipFile(spec.FilePaths[fileIndex]);
        Enumeration e = zipFile.entries();
        ZipEntry zipEntry = (ZipEntry) e.nextElement();
        bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
      }
      if (spec.CompressionMode[fileIndex].equals("gzip")) {
        //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(FilePaths[FileIndex]))));
        bufferedInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(spec.FilePaths[fileIndex])), BufferSize);
      }
      
    } catch (Exception e) {
      System.out.println("couldn't open file: " + spec.FilePaths[fileIndex]);
      throw e;
    }
    
    EOF = false;
    
  }
  
  
  public void closeFile(int fileIndex) throws Exception {
    bufferedInputStream.close();
  }
  
  
  int recordIndex = -1;
  int maxNumRecords = 40000000;
  byte [] recordFileIndices = null;
  long [] recordBytePositions = null;
  public void doit() throws Exception {
    
    
    
    //////////////////////
    //  initialization  //
    //////////////////////
    
    
    calculateFileStructure();
    
    
    
    
    recordIndex = 0;
    bytePosition = 0;
    recordFileIndices   = new byte[maxNumRecords];
    recordBytePositions = new long[maxNumRecords];
    
    
    
    
    
    // read all records syncronizing with the group 1 key
    long firstTimeInMS = System.currentTimeMillis();
    long lastTimeInMS = firstTimeInMS;
    
    for (int fileIndex = 0; fileIndex < numFilesToProcess && recordIndex < maxNumRecords; fileIndex++) {
      
      openFile(fileIndex);
      
      
      ///////////////////////
      //  main processing  //
      ///////////////////////
      
      boolean masterEOF = false;
      String masterKeyString = null;
      
      // initialize keys by reading first record of each group file
      
      
      
      lastKeys[0] = readRecord(fileIndex);
      
      // read all records syncronizing with the group 1 key
      while (recordIndex < maxNumRecords) {
        
        lastKeys[0] = readRecord(fileIndex);
        
        if (EOF)
          break;
        
        
        recordIndex++;
        
        
        
        int reportInterval = 10000;
        if (recordIndex % reportInterval == 0) {
          long currentimeInMS = System.currentTimeMillis();
          double durationInSeconds = (currentimeInMS - lastTimeInMS) / 1000.0;
          double overalDurationInSeconds = (currentimeInMS - firstTimeInMS) / 1000.0;
          lastTimeInMS = currentimeInMS;
          double rate = (double) reportInterval / durationInSeconds;
          double overallRate = (double) recordIndex / overalDurationInSeconds;
          System.out.println(lastKeys[0] + ":" + recordIndex +
           "  :  current rec/s = " + rate  + "  :  overall rec/s = " + overallRate );
        }
        
      }
      
      
      System.out.println("file " + (fileIndex + 1) + " record count = " + recordIndex);
      closeFile(fileIndex);
    }
    
    
    
    System.out.println("final record count = " + recordIndex);
    
    Object [] results = new Object[2];
    results[0] = recordFileIndices;
    results[1] = recordBytePositions;
    this.pushOutput(results, 0);
  }
}
