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


public class ReadDCFRecords extends InputModule {
  
  final double missingGroupFieldValue = 0;
  final int maxNumRecordsToRead = 23000000;
//  int maxNumExamples            =  8800000;
  int maxNumExamples            =  8800000;
  final int reportInterval      = 1000;
  
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
  
  
  
  public String getModuleName () {
    return "ReadDCFRecords";
  }
  
  public String getModuleInfo () {
    return "ReadDCFRecords";
  }
  
  
  //  should record structure information be reported to the console
  boolean reportRecordStructrure = false;
  
  //  should progress be reported to the console
  boolean reportProgress = true;
  
  //  how often, in terms of number of records, should progress be reportd
  int ReportInterval = 10000;
  
  //  for fine tuning I/O performancee
  int BufferSize = 1000000;
  
  public String getInputName (int i) {
    switch (i) {
      default:
        return "error";
    }
  }
  
  public String[] getInputTypes () {
    String[] types = {};
    return types;
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      default:
        return "error";
    }
  }
  
  public String getOutputName (int i) {
    switch (i) {
      case 0:
        return "SSNHashtable";
      default:
        return "error";
    }
  }
  
  public String[] getOutputTypes () {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }
  
  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      default:
        return "No such output";
    }
  }
  
  final int  keyFieldIndex = 1;
  
  public String readRecord (int fileIndex) throws Exception {
    
    String key = null;
    
    for (int fieldIndex = 0; fieldIndex < totalNumFields[fileIndex]; fieldIndex++) {
      
      boolean decode = readField[fileIndex][fieldIndex];
      
      // allocate and clear buffer for reading field
      //byte[] byteBuffer = null;
      byte [] byteBuffer = null;
      if (decode) {
        byteBuffer = fieldBytes[fileIndex][fieldIndex];
        for (int i = 0; i < fieldSizes[fileIndex][fieldIndex]; i++) {
          byteBuffer[i] = 64;  // fill with blanks
        }
      }
      
      // read field one byte at a time
      int fieldByteIndex = 0;
      while (true) {
        int byteInt;
        
        // read next byte
        try {
          byteInt = bufferedInputStream[fileIndex].read ();
          if (byteInt == -1) {
            System.out.println ("EOF");
            EOFs[fileIndex] = true;
            break;
          }
        } catch (Exception e) {
          System.out.println ("EOF");
          EOFs[fileIndex] = true;
          break;
        }
        
        // for error checking
        masterSums[fileIndex] += byteInt;
        
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
            System.out.println ("Error!   fieldByteIndex == byteBuffer.length ");
          }
          
          if (decode)
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
      if (decode) {
        
        byteString = new String (byteBuffer, 0, byteBuffer.length, "cp037");
        
        // store field data in to record structure
        fieldValues[fileIndex][fieldIndex] = byteString;
      }
      
      
      if (fieldIndex == keyFieldIndex) {  // if on universal key field
        key = byteString;
      }
      
    }
    
    recordIndices[fileIndex]++;
    
    return key;
  }
  
  
  
  
  
  
  
  
  DCFFileSpecification spec = new DCFFileSpecification ();
  
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
  boolean[] EOFs               = new boolean[numFilesToProcess];
  int[]     numPersonIDRepeats = new     int[numFilesToProcess];
  long[]    masterSums         = new    long[numFilesToProcess];
  String[]  lastKeys           = new  String[numFilesToProcess];
  
  BufferedInputStream[] bufferedInputStream = new BufferedInputStream[numFilesToProcess];
  
  Integer StaticObject = new Integer (1);
  
  
  
  
  int exampleIndex        = 0;
  int numPositiveExamples = 0;
  int numNegativeExamples = 0;
  int inputFeatureIndex   = -1;
  int outputFeatureIndex  = -1;
  
  byte [] data;
  
  OldContinuousByteExampleTable exampleSet;
  
  
  int [][] ComponentNumFields = new int[spec.NumFiles][];
  
  public void calculateFileStructure () throws Exception {
    
    
    
    // for each file create field name, get structure and open file
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      
      
      //
      //  get file specific information information
      //
      
      int NumParts = spec.ComponentFieldNames[fileIndex].length;
      
      if (NumParts != spec.ComponentFieldNames[fileIndex].length) {
        throw new Exception ();
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
        
        if (spec.NestingMode[fileIndex].equals ("normal")) {
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

      System.out.println ("TotalSize = " + TotalSize);
      
      if (reportRecordStructrure) {
        int byteIndex = 0;
        for (int i = 0; i < totalNumFields[fileIndex]; i++) {
          System.out.println ("Field #" + (i + 1) + "  Byte #" + (byteIndex + 1) + "  Name = " +
           fieldNames[fileIndex][i] + "  Size = " + fieldSizes[fileIndex][i]);
          byteIndex += fieldSizes[fileIndex][i];
        }
        
      }
      
      
      
      
      
      /*********************/
      /*  open file files  */
      /*********************/
      
      if (bufferedInputStream[fileIndex] == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals ("none")) {
            bufferedInputStream[fileIndex] = new BufferedInputStream (new FileInputStream (spec.FilePaths[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("zip")) {
            ZipFile zipFile = new ZipFile (spec.FilePaths[fileIndex]);
            Enumeration e = zipFile.entries ();
            ZipEntry zipEntry = (ZipEntry) e.nextElement ();
            bufferedInputStream[fileIndex] = new BufferedInputStream (zipFile.getInputStream (zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStream[fileIndex] = new BufferedInputStream (new GZIPInputStream (new FileInputStream (spec.FilePaths[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println ("couldn't open file: " + spec.FilePaths[fileIndex]);
          throw e;
        }
      }
      
    }
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  int maxNumInputFeatures = 100;
  int numInputFeatures = -1;
  int numOutputFeatures = -1;
  
  
  
  int   [] inputFileIndices    = new int[maxNumInputFeatures];
  int   [] inputFieldIndices   = new int[maxNumInputFeatures];
  int   [] inputTransformType  = new int[maxNumInputFeatures];
  int   [] inputTransformIParm = new int[maxNumInputFeatures];
  String[] inputTransformSParm = new String[maxNumInputFeatures];
  String[] inputNames          = new String[maxNumInputFeatures];
  String[] outputNames         = new String[maxNumInputFeatures];
  
  final int GROUP_01 = 0;
  final int GROUP_02 = 1;
  final int GROUP_03 = 2;
  final int GROUP_04 = 3;
  final int GROUP_05 = 4;
  final int GROUP_06 = 5;
  final int GROUP_07 = 6;
  final int GROUP_08 = 7;
  final int GROUP_09 = 8;
  final int GROUP_10 = 9;
  
  final int GROUP_EXISTS      = 1;
  final int DOUBLE            = 2;
  final int EQUALS_CHARACTER  = 3;
  final int SELECT_CHARACTER  = 4;
  final int DURATION_IN_YEARS = 5;
  final int IS_NOT_ALL_BLANKS = 6;
  
  
  public void setInputFeature (int fileIndex, String fieldName, int transformType,
   int intParameter, String stringParameter, String inputName) {
    
    int fieldIndex = -1;
    if (fieldName != null) {
    
      for (int i = 0; i < fieldNames[fileIndex].length; i++) {
      if (fieldNames[fileIndex][i].equalsIgnoreCase (fieldName)) {
        fieldIndex = i;
        break;
      }
    }
    
    readField[fileIndex][fieldIndex] = true;
    inputField[fileIndex][fieldIndex] = true;
    
    }
    
    inputFileIndices   [inputFeatureIndex] = fileIndex;
    inputFieldIndices  [inputFeatureIndex] = fieldIndex;
    inputTransformType [inputFeatureIndex] = transformType;
    inputTransformIParm[inputFeatureIndex] = intParameter;
    inputTransformSParm[inputFeatureIndex] = stringParameter;
    inputNames         [inputFeatureIndex] = inputName;
    
    
    inputFeatureIndex++;
  }
  
  
  public void setInputFeatures () {
    
    inputFeatureIndex  = 0;
    outputFeatureIndex = 0;
    
    
    //  Define Input Features  //
    
    
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 0,    null, "Digit1");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 1,    null, "Digit2");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 2,    null, "Digit3");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 3,    null, "Digit4");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 4,    null, "Digit5");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 5,    null, "Digit6");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 6,    null, "Digit7");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 7,    null, "Digit8");
//    setInputFeature (GROUP_01, "CLM-FIXED-COSSN",     SELECT_CHARACTER, 8,    null, "Digit9");
    
    setInputFeature (GROUP_01, "FIXED-TKT-TITLE",     EQUALS_CHARACTER, -1,   "1",   "FixedTicketTitleIsT2Only?");
    setInputFeature (GROUP_01, "FIXED-TKT-TITLE",     EQUALS_CHARACTER, -1,   "2",   "FixedTicketTitleIsT2AndT16?");
    setInputFeature (GROUP_01, "FIXED-TKT-TITLE",     EQUALS_CHARACTER, -1,   "3",   "FixedTicketTitleIsT16Only?");
    setInputFeature (GROUP_01, "PRSN-DOB",            DURATION_IN_YEARS, 50,   null, "AgeOfPerson");
    setInputFeature (GROUP_01, "PRSN-BLND-DT",        IS_NOT_ALL_BLANKS, -1,   null, "HasPersonBlindAge?");  // indicator of blind program? unkown
    setInputFeature (GROUP_01, "PRSN-BLND-DT",        DURATION_IN_YEARS, 100,  null, "PersonBlindAge");      // t2 based on being blind, higher sga 840/860 1200 for blind
    setInputFeature (GROUP_01, "PRSN-NDDSS-DT",       IS_NOT_ALL_BLANKS, -1,   null, "HasPersonNationalDDSSystemAge?");
    setInputFeature (GROUP_01, "PRSN-NDDSS-DT",       DURATION_IN_YEARS, 100,  null, "PersonNationalDDSSystemAge");
    
    setInputFeature (GROUP_02, "CLAIM-NOE",           DOUBLE,            1,    null, "ClaimNumberOfEntries");
    
    setInputFeature (GROUP_03, null,                  GROUP_EXISTS,      -1,    null, "EarningsExists?");
    setInputFeature (GROUP_03, "ERNGS-MONTHS-NOE",    DOUBLE,            1,     null, "EarningsMonthsNumberOfEntries");
  //setInputFeature (GROUP_03, "ERNGS-ALLGD-AMT_1",   DOUBLE,            1,     null, "EarningsAllegedAmount_1");
    setInputFeature (GROUP_03, "T16-ERNGS-GRS-AMT_1", DOUBLE,            10000, null, "T16EarningsGrossAmount");
    setInputFeature (GROUP_03, "T2-ERNGS-GRS-AMT_1",  DOUBLE,            10000, null, "T2EarningsGrossAmount");
    
    
  //setInputFeature (GROUP_04, null,                  GROUP_EXISTS,      -1,   null, "Group04Exists?");
    
    setInputFeature (GROUP_05, null,                  GROUP_EXISTS,      -1,   null, "T2MedicalExists?");
    setInputFeature (GROUP_05, "T2MED-NOE",           DOUBLE,            1,    null, "T2MedicalNumberOfEntries ");
    setInputFeature (GROUP_05, "T2MED-DATA-NOE",      DOUBLE,            1,    null, "T2MedicalDataNumberOfEntries");
    
    setInputFeature (GROUP_06, null,                  GROUP_EXISTS,      -1,   null, "T16MedicalExists?");
    setInputFeature (GROUP_06, "MED-NOE",             DOUBLE,            1,    null, "MedicalNumberOfEntries");
    setInputFeature (GROUP_06, "MED-DATA-NOE",        DOUBLE,            1,    null, "MedicalDataNumberOfEntries");
    
    setInputFeature (GROUP_07, null,                  GROUP_EXISTS,      -1,   null, "T16ChildExists?");
    setInputFeature (GROUP_07, "CHLD-NOE",            DOUBLE,            1,    null, "ChildhoodNumberOfEntries");
    setInputFeature (GROUP_07, "CHLD-DATA-NOE",       DOUBLE,            1,    null, "ChildhoodDataNumberOfEntries");
    
    setInputFeature (GROUP_08, null,                  GROUP_EXISTS,      -1,   null, "T2WorkCDRExists?");
    setInputFeature (GROUP_08, "WORK-NOE",            DOUBLE,            1,    null, "WorkNumberOfEntries");
    setInputFeature (GROUP_08, "WORK-DATA-NOE",       DOUBLE,            1,    null, "WorkDataNumberOfEntries");
    
    
    setInputFeature (GROUP_09, null,                  GROUP_EXISTS,      -1,   null, "ExpeditedReinstatementExists?");
    setInputFeature (GROUP_09, "EXR-NOE",             DOUBLE,            1,    null, "ExpeditedReinstatementNumberOfEntries");
    setInputFeature (GROUP_09, "CDR-EXR-DATA-NOE",    DOUBLE,            1,    null, "ExpeditedReinstatementDataNumberOfEntries");
    
    setInputFeature (GROUP_10, null,                  GROUP_EXISTS,      -1,   null, "DevelopmentWorksheetExists?");
    setInputFeature (GROUP_10, "DWS-NOE",             DOUBLE,            1,    null, "DevelopmentWorksheetNumberOfEntries");
    
    
    //output features

    
    switch (exampleDefinitionMode) {
      case PREDICT_ELIGIBILITY:
        outputNames[outputFeatureIndex++] = "Eligble";
        break;
      case PREDICT_MAILING_RESPONSE:
        outputNames[outputFeatureIndex++] = "MailingResponse";
        break;
      case PREDICT_ACTIVE_PARTICIPANT:
        outputNames[outputFeatureIndex++] = "ActiveParticipant";
        break;
      case PREDICT_TERMINATED_PARTICIPANT:
        outputNames[outputFeatureIndex++] = "TerminatedParticipant";
        break;
    }
    
    
    numInputFeatures  = inputFeatureIndex;
    numOutputFeatures = outputFeatureIndex;
    
    
    // set up additional active features for output and syncronization
    
    // claim selection date for computing durations
    readField[0][fixedClaimSelectionDateIndex] = true;
    
    // ticket status for output feature
    readField[0][fixedTicketStatusFieldIndex] = true;
    
    // decode all keys
    for (int i = 0; i < 10; i++) {
      readField[i][keyFieldIndex] = true;
    }
    
  }
  
  
  
  
  
  final    int fixedClaimSelectionDateIndex = 0;
  final    int fixedTicketStatusFieldIndex = 19;
  
  public boolean  processRecord () throws Exception {
    
    //  data set creattion date for age a duration calculations  //
    
    if (!selectionTimeSet) {
      int year  = Integer.parseInt (fieldValues[0][fixedClaimSelectionDateIndex].substring (0, 4));
      int month = Integer.parseInt (fieldValues[0][fixedClaimSelectionDateIndex].substring (4, 6));
      int day   = Integer.parseInt (fieldValues[0][fixedClaimSelectionDateIndex].substring (6, 8));
      
      GregorianCalendar birthTimeCalendar = new GregorianCalendar (year, month, day);
      
      Date date = birthTimeCalendar.getTime ();
      
      selectionTimeInMillis = birthTimeCalendar.getTimeInMillis ();
      selectionTimeSet = true;
    }
    
    
    
    
    /***************************************************************************************/
    /*  decide wether to use the example for learning, and if so, what class it should be  */
    /***************************************************************************************/
    
    
    /* determine whether to include the person in the study how to classify the example */
    
    boolean rejectExample = false;
    int     exampleClass  = 0;
    
    String ticketStatusString = fieldValues[0][fixedTicketStatusFieldIndex];
    
    if (ticketStatusString.equals (" ")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        rejectExample = true;
      if (exampleDefinitionMode == PREDICT_MAILING_RESPONSE)
        rejectExample = true;
      if (exampleDefinitionMode == PREDICT_TERMINATED_PARTICIPANT)
        rejectExample = true;
    }
    
    if (ticketStatusString.equals ("E")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        rejectExample = true;
      if (exampleDefinitionMode == PREDICT_ELIGIBILITY)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_MAILING_RESPONSE)
        rejectExample = true;
      if (exampleDefinitionMode == PREDICT_TERMINATED_PARTICIPANT)
        rejectExample = true;
    }
    
    if (ticketStatusString.equals ("M")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        exampleClass = 0;
      if (exampleDefinitionMode == PREDICT_ELIGIBILITY)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_TERMINATED_PARTICIPANT)
        rejectExample = true;
    }
    
    if (ticketStatusString.equals ("I")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_ELIGIBILITY)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_MAILING_RESPONSE)
        exampleClass = 1;
    }
    
    if (ticketStatusString.equals ("N")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_ELIGIBILITY)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_MAILING_RESPONSE)
        exampleClass = 1;
    }
    
    if (ticketStatusString.equals ("T")) {
      if (exampleDefinitionMode == PREDICT_ACTIVE_PARTICIPANT)
        rejectExample = true;
      if (exampleDefinitionMode == PREDICT_ELIGIBILITY)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_MAILING_RESPONSE)
        exampleClass = 1;
      if (exampleDefinitionMode == PREDICT_TERMINATED_PARTICIPANT)
        exampleClass = 1;
    }
    
    
    if (!rejectExample) {
      
      
      
      /***********************/
      /*  set input features */
      /***********************/
      
      double newValue;
      
      for (int inputFeatureIndex = 0; inputFeatureIndex < numInputFeatures; inputFeatureIndex++) {
        
        int    fileIndex     = inputFileIndices[inputFeatureIndex];
        int    transformType = inputTransformType[inputFeatureIndex];
        int    fieldIndex    = -1;
        String fieldName     = null;
        String fieldValue    = null;
        if (transformType != GROUP_EXISTS) {
          fieldIndex = inputFieldIndices[inputFeatureIndex];
          fieldName  = fieldNames[fileIndex][fieldIndex];
          fieldValue = fieldValues[fileIndex][fieldIndex];
        }
        
        int    intParameter    = inputTransformIParm[inputFeatureIndex];
        String stringParameter = inputTransformSParm[inputFeatureIndex];
        String inputName       = inputNames[inputFeatureIndex];
        
        // check keys for validity of fields
        if ((fileIndex != 0) && (!lastKeys[0].equals (lastKeys[fileIndex]))) {
          if (transformType == GROUP_EXISTS) {
            exampleSet.setInput (exampleIndex, inputFeatureIndex, 0);
          } else {
            exampleSet.setInput (exampleIndex, inputFeatureIndex, missingGroupFieldValue);
          }
        } else {
          
          
          switch (transformType) {
            
            case GROUP_EXISTS:
              exampleSet.setInput (exampleIndex, inputFeatureIndex, 1);
              break;
              
            case DOUBLE:
              double value = Double.parseDouble (fieldValue) / intParameter;
              //System.out.println("fieldValue = " + fieldValue + "  name = " + inputName + "value = " + value);
              exampleSet.setInput (exampleIndex, inputFeatureIndex, value);
              break;
              
            case EQUALS_CHARACTER:
              if (fieldValue.equals (stringParameter))
                exampleSet.setInput (exampleIndex, inputFeatureIndex, 1);
              else
                exampleSet.setInput (exampleIndex, inputFeatureIndex, 0);
              break;
              
            case SELECT_CHARACTER:
              exampleSet.setInput (exampleIndex, inputFeatureIndex, fieldValue.charAt (intParameter));
              break;
              
            case DURATION_IN_YEARS:
              if (fieldValue.indexOf (" ") != -1) {
                exampleSet.setInput (exampleIndex, inputFeatureIndex, intParameter);
              } else {
                int year  = Integer.parseInt (fieldValue.substring (0, 4));
                int month = Integer.parseInt (fieldValue.substring (4, 6));
                int day   = Integer.parseInt (fieldValue.substring (6, 8));
                GregorianCalendar birthTimeCalendar = new GregorianCalendar (year, month, day);
                long birthTimeInMillis = birthTimeCalendar.getTimeInMillis ();
                double ageInYears = (double) (selectionTimeInMillis - birthTimeInMillis) /
                 (double) millisPerYear;
                exampleSet.setInput (exampleIndex, inputFeatureIndex, ageInYears);
              }
              break;
              
            case IS_NOT_ALL_BLANKS:
              if (fieldValue.indexOf (" ") != -1)
                exampleSet.setInput (exampleIndex, inputFeatureIndex, 0);
              else
                exampleSet.setInput (exampleIndex, inputFeatureIndex, 1);
              break;
              
            default:
              System.out.println("error, undefined transformation type: " + transformType);
              break;
              
          }  // switch
        }  // else
      }  // for
      
      
      
      /***********************/
      /*  set output feature */
      /***********************/
      
      
      
      int outputFeatureIndex = 0;
      exampleSet.setOutput (exampleIndex, outputFeatureIndex, exampleClass);
      outputFeatureIndex++;
      
      
      exampleIndex++;
      if (exampleClass == 1)
        numPositiveExamples++;
      else
        numNegativeExamples++;
      
      
      if (exampleIndex == maxNumExamples) {
        return true;
      } else {
        return false;
      }
      
    }
    return false;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public void openFiles () throws Exception {
    
    // for each file create field name, get structure and open file
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      
      /*********************/
      /*  open file files  */
      /*********************/
      
      if (bufferedInputStream[fileIndex] == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals ("none")) {
            bufferedInputStream[fileIndex] = new BufferedInputStream (new FileInputStream (spec.FilePaths[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("zip")) {
            ZipFile zipFile = new ZipFile (spec.FilePaths[fileIndex]);
            Enumeration e = zipFile.entries ();
            ZipEntry zipEntry = (ZipEntry) e.nextElement ();
            bufferedInputStream[fileIndex] = new BufferedInputStream (zipFile.getInputStream (zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStream[fileIndex] = new BufferedInputStream (new GZIPInputStream (new FileInputStream (spec.FilePaths[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println ("couldn't open file: " + spec.FilePaths[fileIndex]);
          throw e;
        }
      }
      
    }
  }
  
  public void reportProgress () throws Exception {
    
    System.out.println ();
    System.out.println ();
    System.out.println (recordIndex);
    
    System.out.print ("keys: ");
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      System.out.print (lastKeys[fileIndex] + ",");
    }
    System.out.println ();
    System.out.println ("position: ");
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      System.out.println (
       "  group " + (fileIndex + 1) +
       "  record " + recordIndices[fileIndex] +
       "  byte " + byteIndices[fileIndex]
       );
    }
    System.out.println ("exampleSet:");
    System.out.println ("  numExamples         = " + exampleIndex);
    System.out.println ("  numPositiveExamples = " + numPositiveExamples);
    System.out.println ("  numNegativeExamples = " + numNegativeExamples);
  }
  
  
  int recordIndex = 0;
  
  public void doit () throws Exception {
    
    
    //readPositiveExampleSSNs();
    
    
    Hashtable Hashtable = new Hashtable ();
    
    int numNotTicketEligible = 0;
    int numEligible = 0;
    int numMailed = 0;
    int numInUse = 0;
    int numNotInUse = 0;
    int numTerminated = 0;
    
    //////////////////////
    //  initialization  //
    //////////////////////
    
    
    calculateFileStructure ();
    
    setInputFeatures ();
    
    openFiles ();
    
    
    /**************************/
    /*  Create Example Table  */
    /**************************/
    
    exampleIndex       = 0;
    
    data = new byte[maxNumExamples * (numInputFeatures + numOutputFeatures)];
    
    exampleSet = new OldContinuousByteExampleTable (data, maxNumExamples,
     numInputFeatures, numOutputFeatures, inputNames, outputNames);
    
    ///////////////////////
    //  main processing  //
    ///////////////////////
    
    
    System.out.println ("Reading Files");
    boolean masterEOF = false;
    String masterKeyString = null;
    
    // initialize keys by reading first record of each group file
    
    for (int fileIndex = 0; fileIndex < numFilesToProcess; fileIndex++) {
      lastKeys[fileIndex] = readRecord (fileIndex);
    }
    
    
    // read all records syncronizing with the group 1 key
    while (true) {
      
      if (recordIndex == maxNumRecordsToRead)
        break;
      
      // report progress
      if (reportProgress && recordIndex % reportInterval == 0) {
        reportProgress ();
      }
      
      // process next record
      boolean maxNumRecordsProcessed = processRecord ();
      if (maxNumRecordsProcessed)
        break;
      
      // read a subset of the group files, other than group 1 to read in order to keep syncronized
      for (int fileIndex = 1; fileIndex < numFilesToProcess; fileIndex++) {
        if (lastKeys[0].equals (lastKeys[fileIndex])) {
          lastKeys[fileIndex] = readRecord (fileIndex);
        }
      }
      lastKeys[0] = readRecord (0);
      
      if (lastKeys[0] == null)
        break;
      
      recordIndex++;
    }
    
    
    
    reportProgress ();
    
    
    
    
    
    int numExamples = exampleIndex;
    System.out.println ("numExamples     = " + numExamples);
    exampleSet.setNumRows (numExamples);
    
    this.pushOutput ((ExampleTable) exampleSet, 0);
  }
}
