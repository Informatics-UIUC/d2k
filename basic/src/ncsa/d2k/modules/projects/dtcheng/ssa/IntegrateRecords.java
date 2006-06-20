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


public class IntegrateRecords extends InputModule {
  
  int maxNumTrainExamples = 8200000;
  int maxNumTestExamples = 1100000;
  final int reportInterval      = 1000;
  
  
  public String getModuleName () {
    return "IntegrateRecords";
  }
  
  public String getModuleInfo () {
    return "IntegrateRecords";
  }
  
  
  //  should progress be reported to the console
  boolean reportProgress = true;
  
  //  how often, in terms of number of records, should progress be reportd
  int ReportInterval = 10000;
  
  //  for fine tuning I/O performancee
  int BufferSize = 1000000;
  
  public String getInputName (int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }
  
  public String[] getInputTypes () {
    String[] types = {};
    return types;
  }
  
  public String getInputInfo (int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }
  
  public String getOutputName (int i) {
    switch (i) {
      case 0:
        return "TrainingExampleTable";
      case 1:
        return "TestingExampleTable";
      default:
        return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] out = {
       "ncsa.d2k.modules.core.datatype.table.ExampleTable",
       "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
       return out;
  }

  public String getOutputInfo (int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "ExampleTable";
      default:
        return "No such output";
    }
  }
  
  
  IntegratedFileSpecification spec = new IntegratedFileSpecification ();
  
  BufferedInputStream[] bufferedInputStream = new BufferedInputStream[spec.NumFiles];
  
  
  
  
  
  String[][] fieldNames   = new  String[spec.NumFiles][];
  boolean[]  EOFs          = new boolean[spec.NumFiles];
  String[]   lastKeys      = new  String[spec.NumFiles];
  String [][] fieldStrings = new String[spec.NumFiles][];
  double[][] fieldValues = new double[spec.NumFiles][];
  public void openFiles () throws Exception {
    
    // for each file create field name, get structure and open file
    for (int fileIndex = 0; fileIndex < spec.NumFiles; fileIndex++) {
      
      /*********************/
      /*  open file files  */
      /*********************/
      
      if (bufferedInputStream[fileIndex] == null) {
        
        try {
          if (spec.CompressionMode[fileIndex].equals ("none")) {
            bufferedInputStream[fileIndex] = new BufferedInputStream (new FileInputStream (spec.GroupFileNames[fileIndex]), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("zip")) {
            ZipFile zipFile = new ZipFile (spec.GroupFileNames[fileIndex]);
            Enumeration e = zipFile.entries ();
            ZipEntry zipEntry = (ZipEntry) e.nextElement ();
            bufferedInputStream[fileIndex] = new BufferedInputStream (zipFile.getInputStream (zipEntry), BufferSize);
          }
          if (spec.CompressionMode[fileIndex].equals ("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStream[fileIndex] = new BufferedInputStream (new GZIPInputStream (new FileInputStream (spec.GroupFileNames[fileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println ("couldn't open file: " + spec.GroupFileNames[fileIndex]);
          throw e;
        }
      }
      
    }
    
    for (int fileIndex = 0; fileIndex < spec.NumFiles; fileIndex++) {
      fieldStrings[fileIndex] = new String[spec.FieldNames[fileIndex].length];
      fieldValues[fileIndex] = new double[spec.FieldNames[fileIndex].length];
    }
  }
  
  
  
  
  final int  keyFieldIndex = 1;
  byte [] byteBuffer = new byte[256];
  
  
  public void readRecord (int fileIndex) throws Exception {
    
    fieldStrings[fileIndex][0] = null;
    
    for (int fieldIndex = 0; fieldIndex < spec.FieldNames[fileIndex].length; fieldIndex++) {
      
      // read field one byte at a time
      int fieldByteIndex = 0;
      while (true) {
        int byteInt;
        
        // read next byte
        try {
          byteInt = bufferedInputStream[fileIndex].read ();
          if (byteInt == -1) {
            //System.out.println ("EOF");
            EOFs[fileIndex] = true;
            break;
          }
        } catch (Exception e) {
          //System.out.println ("EOF");
          EOFs[fileIndex] = true;
          break;
        }
        
        //check for end of field TAB or end of line
        if (byteInt == 9) {
          break;
        }
        
        //check for end of field TAB or end of line
        if (byteInt == 13) {
          bufferedInputStream[fileIndex].read ();
          break;
        }
        
        byteBuffer[fieldByteIndex] = (byte) byteInt;
        
        fieldByteIndex++;
        
        
      }
      
      
      // stop file reading of EOF has been reached
      if (EOFs[fileIndex]) {
        break;
      }
      
      
      // transate all fields except SSN from ibm to ascii character set and create field string
      
      String string = new String (byteBuffer, 0, fieldByteIndex);
      
      //System.out.println("fileIndex = " + fileIndex + "  fieldIndex = " + fieldIndex + "  string = " + string);
      
      fieldStrings[fileIndex][fieldIndex] = string;
      
      try {
      fieldValues[fileIndex][fieldIndex] = Double.parseDouble (fieldStrings[fileIndex][fieldIndex]);
      } catch (Exception e) {
         fieldValues[fileIndex][fieldIndex] = 0;
      }
      
      
      
    }
    
  }
  
  
  
  
  byte [] trainData;
  byte [] testData;
  
  int  [] trainGroup;
  int  [] testGroup;
  
  OldContinuousByteExampleTable trainExampleSet;
  OldContinuousByteExampleTable testExampleSet;
  
  
  
  int trainExampleIndex = 0;
  int testExampleIndex = 0;
  
  int numInputFeatures = 0;
  int numOutputFeatures = 0;
  
  public void doit () throws Exception {
    
    int [] numMatches = new int[spec.NumFiles];
    
    
    
    // determine number of input and output features //
    
    numOutputFeatures = 1;
    
    
    numInputFeatures = 0;
    for (int i1 = 0; i1 < spec.NumFiles; i1++) {
      int end_i2;
      if (i1 == 0)
        end_i2 = spec.FieldNames[i1].length - 1;
      else
        end_i2 = spec.FieldNames[i1].length;
      for (int i2 = spec.InputStartIndices[i1]; i2 < end_i2; i2++) {
        numInputFeatures++;
      }
    }

    
    // create input and output feature names //
    
    String[] inputNames          = new String[numInputFeatures];
    String[] outputNames         = new String[1];
    
    outputNames[0] = spec.FieldNames[0][spec.FieldNames[0].length - 1];  // last feature in first file
    
    
    
    int inputIndex = 0;
    for (int i1 = 0; i1 < spec.NumFiles; i1++) {
      int end_i2;
      if (i1 == 0)
        end_i2 = spec.FieldNames[i1].length - 1;
      else
        end_i2 = spec.FieldNames[i1].length;
      for (int i2 = spec.InputStartIndices[i1]; i2 < end_i2; i2++) {
      
      inputNames[inputIndex++] = spec.FieldNames[i1][i2];
      }
    }
      
    
    
    /**************************/
    /*  Create Example Table  */
    /**************************/
    
    trainExampleIndex       = 0;
    testExampleIndex       = 0;
    
    trainData = new byte[maxNumTrainExamples * (numInputFeatures + numOutputFeatures)];
    testData = new byte[maxNumTestExamples * (numInputFeatures + numOutputFeatures)];
    
    trainGroup = new int[maxNumTrainExamples];
    testGroup  = null;
    
    trainExampleSet = new OldContinuousByteExampleTable (trainData, trainGroup, maxNumTrainExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
    testExampleSet = new OldContinuousByteExampleTable (testData, testGroup, maxNumTestExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
    
    
    
    //////////////////////
    //  initialization  //
    //////////////////////
    
    
    openFiles ();
    
    
    ///////////////////////
    //  main processing  //
    ///////////////////////
    
    boolean masterEOF = false;
    
    // initialize keys by reading first record of each group file
    
    for (int fileIndex = 0; fileIndex < spec.NumFiles; fileIndex++) {
      readRecord (fileIndex);
      lastKeys    [fileIndex] = fieldStrings[fileIndex][0];
    }
    
    
    // read all records syncronizing with the first file key
    while (true) {
      
      // process next record
      if (trainExampleIndex == maxNumTrainExamples)
        break;
      
      for (int fileIndex = 1; fileIndex < spec.NumFiles; fileIndex++) {
        while (lastKeys[0].compareTo (lastKeys[fileIndex]) > 0) {
          readRecord (fileIndex);
          lastKeys    [fileIndex] = fieldStrings[fileIndex][0];
        }
      }
      
      for (int fileIndex = 1; fileIndex < spec.NumFiles; fileIndex++) {
        if (lastKeys[0].equals (lastKeys[fileIndex])) {
          numMatches[fileIndex]++;
        }
      }
      
      
      // set output class
      
      int outputValue = (int) fieldValues[0][spec.FieldNames[0].length - 1];
      
      // determin if examples is training or testing
      if (outputValue == 0 || outputValue == 1) {
        
        // training example
        
        trainExampleSet.setOutput(trainExampleIndex, 0, outputValue);
       
        inputIndex = 0;
        for (int i1 = 0; i1 < spec.NumFiles; i1++) {
          int end_i2;
          if (i1 == 0)
            end_i2 = spec.FieldNames[i1].length - 1;
          else
            end_i2 = spec.FieldNames[i1].length;
          for (int i2 = spec.InputStartIndices[i1]; i2 < end_i2; i2++) {
            trainExampleSet.setInput(trainExampleIndex, inputIndex++, fieldValues[i1][i2]);
          }
        }
        
        int groupIndex = (int) fieldValues[spec.GroupFileNumber - 1][spec.GroupFeatureNumber - 1];
        trainExampleSet.setGroup(trainExampleIndex, groupIndex);
        
        //System.out.println (groupIndex + ":" + fieldValues[spec.GroupFileNumber - 1][spec.GroupFeatureNumber - 1]);
        
        trainExampleIndex++;
        
        
        if (trainExampleIndex % 1000 == 0) {
          
          System.out.println("Train: " + trainExampleIndex + "\t" + lastKeys[0]+ "\t" + lastKeys[1]+ "\t" + lastKeys[2] +
           "\t" + numMatches[1] + "\t" + numMatches[2]);
          
        }
      } 
      else {
        
        // testing example
        
        // set output to NaN indicating unknown
        testExampleSet.setOutput(testExampleIndex, 0, Double.NaN);
       
        inputIndex = 0;
        for (int i1 = 0; i1 < spec.NumFiles; i1++) {
          int end_i2;
          if (i1 == 0)
            end_i2 = spec.FieldNames[i1].length - 1;
          else
            end_i2 = spec.FieldNames[i1].length;
          for (int i2 = spec.InputStartIndices[i1]; i2 < end_i2; i2++) {
            testExampleSet.setInput(testExampleIndex, inputIndex++, fieldValues[i1][i2]);
          }
        }
        
        testExampleIndex++;
        
        
        if (testExampleIndex % 1000 == 0) {
          
          System.out.println("Test:  " + testExampleIndex + "\t" + lastKeys[0]+ "\t" + lastKeys[1]+ "\t" + lastKeys[2] +
           "\t" + numMatches[1] + "\t" + numMatches[2]);
          
        }
      }

      readRecord(0);
      lastKeys    [0] = fieldStrings[0][0];
      
      //System.out.println(lastKeys[0]);
      
      if (lastKeys[0] == null)
        break;
      
      
    }
    
    

    int numTrainExamples = trainExampleIndex;
    int numTestExamples = testExampleIndex;
    
    System.out.println ("numTrainExamples     = " + numTrainExamples);
    System.out.println ("numTestExamples     = " + numTestExamples);
    
    trainExampleSet.setNumRows (numTrainExamples);
    testExampleSet.setNumRows (numTestExamples);
    
    trainExampleSet.setNumGroups (spec.NumGroups);
    testExampleSet.setNumGroups (0);
    
    this.pushOutput ((ExampleTable) trainExampleSet, 0);
    this.pushOutput ((ExampleTable) testExampleSet, 1);
  }
}
