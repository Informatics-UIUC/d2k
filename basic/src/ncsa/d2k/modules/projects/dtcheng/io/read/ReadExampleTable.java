package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;

public class ReadExampleTable extends InputModule {
  
  //////////////////
  //  PROPERTIES  //
  //////////////////
  
  private String DataPath = "c:/data/table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }
  
  public String getDataPath() {
    return this.DataPath;
  }
  
  private int numExamplesToRead = 60;
  
  public void setNumExamplesToRead(int value) {
    this.numExamplesToRead = value;
  }
  
  public int getNumExamplesToRead() {
    return this.numExamplesToRead;
  }
  
  private int EndOfLineByte1 = 10;
  
  public void setEndOfLineByte1(int value) {
    this.EndOfLineByte1 = value;
  }
  
  public int getEndOfLineByte1() {
    return this.EndOfLineByte1;
  }
  
  
  private int ReadBufferSize = 1000000;
  
  private String inputFeatureNameList = "In1,In2,In3";
  public void setInputFeatureNameList(String value) {
    this.inputFeatureNameList = value;
  }
  public String getInputFeatureNameList() {
    return this.inputFeatureNameList;
  }
  
  private String outputFeatureNameList = "Out1";
  public void setOutputFeatureNameList(String value) {
    this.outputFeatureNameList = value;
  }
  public String getOutputFeatureNameList() {
    return this.outputFeatureNameList;
  }
  
  private String nominalFeatureNameList = "Out1";
  public void setNominalFeatureNameList(String value) {
    this.nominalFeatureNameList = value;
  }
  public String getnominalFeatureNameList() {
    return this.nominalFeatureNameList;
  }
  
  
  public String getModuleName() {
    return "ReadExampleTable";
  }
  
  public String getModuleInfo() {
    return "comma = 44; space = 32; tab = 9; ";
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable", "[[S", "[[S" };
    return out;
  }
  
  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }
  
  public String getInputInfo(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  
  ////////////
  //  MAIN  //
  ////////////
  
  public void doit() throws Exception {
    
    char delimiterChar = ',';
    
    /////////////////////////////
    //  COUNT NUMBER OF LINES  //
    /////////////////////////////
    
    int numLines = -1;
    {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));
      numLines = 0;
      while (bufferedReader.readLine() != null) {
        numLines++;
      }
      bufferedReader.close();
      System.out.println("numLines = " + numLines);
    }
    
    
    /////////////////////////////////////////////////////////
    //  READ COLUMN NAMES AND CALCUALTE NUMBER OF COLUMNS  //
    /////////////////////////////////////////////////////////
    int numColumns = -1;
    String[] columnNames = null;
    {
      
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));
      
      String line = bufferedReader.readLine();
      
      String[] strings = Utility.parseList(line, delimiterChar);
      
      numColumns = strings.length;
      columnNames = new String[numColumns];
      for (int f = 0; f < numColumns; f++) {
        columnNames[f] = strings[f];
      }
      
      for (int f = 0; f < numColumns; f++) {
        System.out.println(" columnNames[" + (f + 1) + "] = " + columnNames[f]);
      }
      bufferedReader.close();
    }
    System.out.println("numColumns = " + numColumns);
    
    
    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////
    
    
    boolean[] inputColumnSelected = new boolean[numColumns];
    boolean[] outputColumnSelected = new boolean[numColumns];
    
    
    String[] nominalFeatureNameListStrings = Utility.parseCSVList(nominalFeatureNameList);
    int numNominalFeatures = nominalFeatureNameListStrings.length;
    
    boolean[] NominalColumn = new boolean[numColumns];
    for (int i = 0; i < numNominalFeatures; i++) {
      int index = Utility.getStringIndex(nominalFeatureNameListStrings[i], columnNames);
      if (index == -1) {
        System.out.println("Error!   " + nominalFeatureNameListStrings[i] + " not found in columnNames");
      }
      
      NominalColumn[index] = true;
    }
    
    
    
    
    
    
    Vector expandedNames;
    
    
    int[] inputColumnIndices  = null;
    
    String[] inputFeatureRangeStrings = Utility.parseCSVList(inputFeatureNameList);
    int numInputFeatureRanges = inputFeatureRangeStrings.length;
    
    expandedNames = new Vector();
    for (int i = 0; i < numInputFeatureRanges; i++) {
      String string = inputFeatureRangeStrings[i];
      if (string.contains("->")) {
        String [] parts = string.split("->");
        if (parts.length != 2) {
          System.out.println("Error!  parts.length != 2");
          throw new Exception();
        }
        int indexStart = Utility.getStringIndex(parts[0], columnNames);
        int indexEnd   = Utility.getStringIndex(parts[1], columnNames);
        for (int j = indexStart; j <= indexEnd; j++) {
          expandedNames.add(columnNames[j]);
        }
      } else {
        expandedNames.add(string);
      }
    }
    
    int numInputFeatures = expandedNames.size();
    String [] inputFeatureNames = new String[expandedNames.size()];
    for (int i = 0; i < expandedNames.size(); i++) {
      inputFeatureNames[i] = (String) expandedNames.elementAt(i);
    }

    
    inputColumnIndices = new int[numInputFeatures];
    for (int i = 0; i < numInputFeatures; i++) {
      int index = Utility.getStringIndex(inputFeatureNames[i], columnNames);
      if (index == -1) {
        System.out.println("Error!   " + inputFeatureNames[i] + " not found in columnNames");
      }
      inputColumnSelected[index] = true;
      inputColumnIndices[i] = index;
    }

    
    
    
    
    int[] outputColumnIndices = null;
    
    String[] outputFeatureRangeStrings = Utility.parseCSVList(outputFeatureNameList);
    int numOutputFeatureRanges = outputFeatureRangeStrings.length;
    
    expandedNames = new Vector();
    for (int i = 0; i < numOutputFeatureRanges; i++) {
      String string = outputFeatureRangeStrings[i];
      if (string.contains("->")) {
        String [] parts = string.split("->");
        if (parts.length != 2) {
          System.out.println("Error!  parts.length != 2");
          throw new Exception();
        }
        int indexStart = Utility.getStringIndex(parts[0], columnNames);
        int indexEnd   = Utility.getStringIndex(parts[1], columnNames);
        for (int j = indexStart; j <= indexEnd; j++) {
          expandedNames.add(columnNames[j]);
        }
      } else {
        expandedNames.add(string);
      }
    }
    
    int numOutputFeatures = expandedNames.size();
    String [] outputFeatureNames = new String[expandedNames.size()];
    for (int i = 0; i < expandedNames.size(); i++) {
      outputFeatureNames[i] = (String) expandedNames.elementAt(i);
    }
    
    
    outputColumnIndices = new int[numOutputFeatures];
    for (int i = 0; i < numOutputFeatures; i++) {
      int index = Utility.getStringIndex(outputFeatureNames[i], columnNames);
      if (index == -1) {
        System.out.println("Error!   " + outputFeatureNames[i] + " not found in columnNames");
      }
      outputColumnSelected[index] = true;
      outputColumnIndices[i] = index;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    int numExamples = -1;
    
    if (numExamplesToRead == -1)
      numExamples = numLines - 2;
    else
      numExamples = numExamplesToRead;
    
    int numFeatures = numInputFeatures + numOutputFeatures;
    
    double[] Data = new double[numExamples * numFeatures];
    
    
    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;
    
    int example_index = 0;
    
    
    
    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));
    
    // skip column names
    bufferedReader.readLine();
    // skip column types
    bufferedReader.readLine();
    
    
    double[] columnValues = new double[numColumns];
    
    for (int e = 0; e < numExamples; e++) {
      
      
      String   line    = bufferedReader.readLine();
      
      String[] strings = Utility.parseList(line, delimiterChar);
      
      
      for (int i = 0; i < numInputFeatures; i++) {
        int f = inputColumnIndices[i];
        try {
          Data[e * numFeatures + i] = Double.parseDouble(strings[f]);
        } catch (Exception exception) {
          System.out.println(exception);
          Data[e * numFeatures + i] = 0.0;
        }
      }
      
      for (int i = 0; i < numOutputFeatures; i++) {
        int f = outputColumnIndices[i];
        try {
          Data[e * numFeatures + numInputFeatures + i] = Double.parseDouble(strings[f]);
        } catch (Exception exception) {
          System.out.println(exception);
          Data[e * numFeatures + numInputFeatures + i] = 0.0;
        }
        
      }
    }

    bufferedReader.close();
    
    String[] inputNames = new String[numInputFeatures];
    String[] outputNames = new String[numOutputFeatures];
    
    for (int i = 0; i < numInputFeatures; i++) {
      inputNames[i] = columnNames[inputColumnIndices[i]];
    }
    for (int i = 0; i < numOutputFeatures; i++) {
      outputNames[i] = columnNames[outputColumnIndices[i]];
    }
    
    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, numExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
    
    this.pushOutput((ExampleTable) exampleSet, 0);
    
  }
  
}