package ncsa.d2k.modules.projects.dtcheng.ssa;

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


public class FindDCFRecordPointers extends InputModule {
  
  final int    maxNumNominalValues = 1000;
  
  final double missingGroupFieldValue = 0;
  
  // which method should be used to define positive and negative examples
  
  
  int numFilesToProcess = 10;
  
  
  
  //  selection time is when the dcf data files were created;  it is read from the dcf file itself;
  //  selectionTimeInMillis is the select time in milliseconds
  boolean selectionTimeSet = false;
  long    selectionTimeInMillis;
  
  // used to convert time in milliseconds to time in years
  long    millisPerYear = (long) (365.25 * 24 * 60 * 60 * 1000);
  
  
  
  public String getModuleName() {
    return "PrintDCFRecords";
  }
  
  public String getModuleInfo() {
    return "PrintDCFRecords";
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
        return "RecordByteIndices";
      default:
        return "error";
    }
  }
  
  public String[] getOutputTypes() {
    String[] out = { "[[J"};
    return out;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RecordByteIndices";
      default:
        return "No such output";
    }
  }
  
  final int  keyFieldIndex = 1;
  
  
  static byte [] byteBuffer = new byte[9];
  public String readRecord(int fileIndex) throws Exception {
    
    
    BufferedInputStream bufferedInputStream = bufferedInputStreams[fileIndex];
    
    recordGroupFilePositions[fileIndex][recordIndex] =  groupFilePositions[fileIndex];
    
    
    
    String key = null;
    
    // read each field
    
    int byteIndex = 0;
    for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
      
      
      // read field one byte at a time
      int fieldByteIndex = 0;
      while (true) {
        int byteInt;
        
        // read next byte
        try {
          byteInt = bufferedInputStream.read();
          if (byteInt == -1) {
            //System.out.println ("EOF");
            EOFs[fileIndex] = true;
            break;
          }
          byteIndex++;
        } catch (Exception e) {
          //System.out.println ("EOF");
          EOFs[fileIndex] = true;
          break;
        }
        
        
        //check for end of field hexB0
        if (byteInt == 176) {
          break;
        }
        
        if (fieldIndex == keyFieldIndex)
          byteBuffer[fieldByteIndex] = (byte) byteInt;
        
        fieldByteIndex++;
      }
      
      
      
      // stop file reading of EOF has been reached
      if (EOFs[fileIndex]) {
        break;
      }
      
      
      if (fieldIndex == keyFieldIndex) {  // if on universal key field
        key = new String(byteBuffer, 0, byteBuffer.length);
      }
      
    }
    
    groupFilePositions[fileIndex] += byteIndex;
    
    recordIndices[fileIndex]++;
    
    return key;
  }
  
  
  
  
  DCFFileSpecification spec = new DCFFileSpecification();
  
  int[]      totalNumFields = new    int[numFilesToProcess];
  
  String[][]    fieldNames     = new     String[numFilesToProcess][];
  String[][]    fieldValues    = new     String[numFilesToProcess][];
  Hashtable[][] fieldValueHTs  = new  Hashtable[numFilesToProcess][];
  int[][]       fieldNumValues = new        int[numFilesToProcess][];
  int[][]       fieldSizes     = new        int[numFilesToProcess][];
  byte[][][]    fieldBytes     = new       byte[numFilesToProcess][][];
  boolean[][]   readField      = new    boolean[numFilesToProcess][];
  boolean[][]   inputField     = new    boolean[numFilesToProcess][];
  int[][]       fieldInputIndex= new        int[numFilesToProcess][];
  
  
  int[]     recordIndices      = new     int[numFilesToProcess];
  boolean[] EOFs               = new boolean[numFilesToProcess];
  int[]     numPersonIDRepeats = new     int[numFilesToProcess];
  long[]    masterSums         = new    long[numFilesToProcess];
  String[]  lastKeys           = new  String[numFilesToProcess];
  
  BufferedInputStream[] bufferedInputStreams = new BufferedInputStream[numFilesToProcess];
  
  Integer StaticObject = new Integer(1);
  
  
  
  
  int exampleIndex        = 0;
  int numPositiveExamples = 0;
  int numNegativeExamples = 0;
  int inputFeatureIndex   = -1;
  int outputFeatureIndex  = -1;
  
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
      
      fieldSizes   [fileIndex] = new        int[totalNumFields[fileIndex]];
      fieldNames   [fileIndex] = new     String[totalNumFields[fileIndex]];
      fieldValues  [fileIndex] = new     String[totalNumFields[fileIndex]];
      fieldValueHTs[fileIndex] = new  Hashtable[totalNumFields[fileIndex]];
      for (int i = 0; i < totalNumFields[fileIndex]; i++) {
        fieldValueHTs[fileIndex][i] = new  Hashtable();
      }
      fieldNumValues[fileIndex] = new     int[totalNumFields[fileIndex]];
      readField     [fileIndex] = new boolean[totalNumFields[fileIndex]];
      fieldBytes    [fileIndex] = new    byte[totalNumFields[fileIndex]][];
      inputField    [fileIndex] = new boolean[totalNumFields[fileIndex]];
      
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
      
      
      
      
      
      
    }
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  final    int fixedClaimSelectionDateIndex = 0;
  final    int fixedTicketStatusFieldIndex = 19;
  
  public boolean  acceptRecord() throws Exception {
    
    //  data set creattion date for age a duration calculations  //
    
    if (!selectionTimeSet) {
      int year  = Integer.parseInt(fieldValues[0][fixedClaimSelectionDateIndex].substring(0, 4));
      int month = Integer.parseInt(fieldValues[0][fixedClaimSelectionDateIndex].substring(4, 6));
      int day   = Integer.parseInt(fieldValues[0][fixedClaimSelectionDateIndex].substring(6, 8));
      
      GregorianCalendar birthTimeCalendar = new GregorianCalendar(year, month, day);
      
      Date date = birthTimeCalendar.getTime();
      
      selectionTimeInMillis = birthTimeCalendar.getTimeInMillis();
      selectionTimeSet = true;
    }
    
    
    
    
    /***************************************************************************************/
    /*  decide wether to use the example for learning, and if so, what class it should be  */
    /***************************************************************************************/
    
    
    /* determine whether to include the person in the study how to classify the example */
    
    boolean acceptExample = false;
    
    String ticketStatusString = fieldValues[0][fixedTicketStatusFieldIndex];
    
    if (ticketStatusString.equals(" ")) {
      acceptExample = false;
    }
    
    if (ticketStatusString.equals("E")) {
      acceptExample = true;
    }
    
    if (ticketStatusString.equals("M")) {
      acceptExample = true;
    }
    
    if (ticketStatusString.equals("I")) {
      acceptExample = true;
    }
    
    if (ticketStatusString.equals("N")) {
      acceptExample = true;
    }
    
    if (ticketStatusString.equals("T")) {
      acceptExample = false;
    }
    
    
    return acceptExample;
  }
  
  
  
  public void openFiles() throws Exception {
    
    // for each file create field name, get structure and open file
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      
      /*********************/
      /*  open file files  */
      /*********************/
      
      if (bufferedInputStreams[fileIndex] == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals("none")) {
            bufferedInputStreams[fileIndex] = new BufferedInputStream(new FileInputStream(spec.FilePaths[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("zip")) {
            ZipFile zipFile = new ZipFile(spec.FilePaths[fileIndex]);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = (ZipEntry) e.nextElement();
            bufferedInputStreams[fileIndex] = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStreams[fileIndex] = new BufferedInputStream(new GZIPInputStream(new FileInputStream(spec.FilePaths[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println("couldn't open file: " + spec.FilePaths[fileIndex]);
          throw e;
        }
      }
      
    }
  }
  
  int recordIndex = -1;
  int maxNumRecords = 23000000;
  long [][] recordGroupFilePositions = null;
  long [] groupFilePositions = null;
  public void doit() throws Exception {
    
    
    int numNotTicketEligible = 0;
    int numEligible          = 0;
    int numMailed            = 0;
    int numInUse             = 0;
    int numNotInUse          = 0;
    int numTerminated        = 0;
    
    //////////////////////
    //  initialization  //
    //////////////////////
    
    
    calculateFileStructure();
    
    openFiles();
    
    
    recordIndex = 0;
    recordGroupFilePositions = new long[numFilesToProcess][maxNumRecords];
    groupFilePositions = new long[numFilesToProcess];
    
    
    
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      lastKeys[fileIndex] = readRecord(fileIndex);
    }
    
    
    ///////////////////////
    //  main processing  //
    ///////////////////////
    
    // initialize system by reading first record of each group file
    
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      lastKeys[fileIndex] = readRecord(fileIndex);
    }
    
    
    // read all records syncronizing with the group 1 key
    long firstTimeInMS = System.currentTimeMillis();
    long lastTimeInMS = firstTimeInMS;
    while (recordIndex < maxNumRecords) {
      
      // read a subset of the group files, other than group 1 to read in order to keep syncronized
      for (int fileIndex = 1; fileIndex < numFilesToProcess; fileIndex++) {
        if (lastKeys[0].equals(lastKeys[fileIndex])) {
          lastKeys[fileIndex] = readRecord(fileIndex);
        }
      }
      lastKeys[0] = readRecord(0);
      
      if (lastKeys[0] == null)
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
    
    System.out.println("final record count = " + recordIndex);
    
    this.pushOutput(recordGroupFilePositions, 0);
    
  }
}
