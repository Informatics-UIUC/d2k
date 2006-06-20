package ncsa.d2k.modules.projects.dtcheng.io.read;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;


public class ReadNominalExampleTable extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private boolean useQuotes = false;

  public void setUseQuotes(boolean value) {
    this.useQuotes = value;
  }

  public boolean getUseQuotes() {
    return this.useQuotes;
  }


  private String InputFeatureNumberList = "1,2,3";

  public void setInputFeatureNumberList(String value) {
    this.InputFeatureNumberList = value;
  }

  public String getInputFeatureNumberList() {
    return this.InputFeatureNumberList;
  }

  private String OutputFeatureNumberList = "4";

  public void setOutputFeatureNumberList(String value) {
    this.OutputFeatureNumberList = value;
  }

  public String getOutputFeatureNumberList() {
    return this.OutputFeatureNumberList;
  }

  private String NominalFeatureNumberList = "";

  public void setNominalFeatureNumberList(String value) {
    this.NominalFeatureNumberList = value;
  }

  public String getNominalFeatureNumberList() {
    return this.NominalFeatureNumberList;
  }


  private String DataPath = "table.csv";

  public void setDataPath(String value) {
    this.DataPath = value;
  }


  public String getDataPath() {
    return this.DataPath;
  }


  private int DelimiterByte = (int) ',';

  public void setDelimiterByte(int value) {
    this.DelimiterByte = value;
  }


  public int getDelimiterByte() {
    return this.DelimiterByte;
  }


  private int numRowsToSkip = 1;

  public void setNumRowsToSkip(int value) {
    this.numRowsToSkip = value;
  }


  public int getNumRowsToSkip() {
    return this.numRowsToSkip;
  }


  private int NumRowsToRead = 60;

  public void setNumRowsToRead(int value) {
    this.NumRowsToRead = value;
  }


  public int getNumRowsToRead() {
    return this.NumRowsToRead;
  }


  private boolean NominalToBooleanExpansion = false;

  public void setNominalToBooleanExpansion(boolean value) {
    this.NominalToBooleanExpansion = value;
  }


  public boolean getNominalToBooleanExpansion() {
    return this.NominalToBooleanExpansion;
  }


  public String getModuleName() {
    return "ReadNominalExampleTable";
  }


  public String getModuleInfo() {
    return "ReadNominalExampleTable comma = 44; space = 32; tab = 9; ";
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
    case 0:
      return "UniqueColumnStrings";
    default:
      return "No such input";
    }
  }


  public String getInputInfo(int index) {
    switch (index) {
    case 0:
      return "UniqueColumnStrings";
    default:
      return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = { "[[Ljava.lang.String;" };
    return types;
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    char delimiterChar = (char) DelimiterByte;

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
    int NumColumns = -1;
    String[] ColumnNames = null;
    {

      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));

      String line = bufferedReader.readLine();

      String[] strings = Utility.parseList(line, delimiterChar);

      NumColumns = strings.length;
      ColumnNames = new String[NumColumns];
      for (int f = 0; f < NumColumns; f++) {

        ColumnNames[f] = strings[f];

      }

      for (int f = 0; f < NumColumns; f++) {
        System.out.println(" ColumnNames[" + (f + 1) + "] = " + ColumnNames[f]);

      }
      bufferedReader.close();
    }
    System.out.println("NumColumns = " + NumColumns);


    // read nominal feature strings
    String[][] UniqueColumnStrings = (String[][]) this.pullInput(0);

    int[] ColumnNumNominalValues = new int[UniqueColumnStrings.length];

    for (int i = 0; i < UniqueColumnStrings.length; i++) {
      ColumnNumNominalValues[i] = UniqueColumnStrings[i].length;
    }

    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////

    
    String[] InputFeatureRangesListStrings = Utility.parseCSVList(InputFeatureNumberList);
    
    int NumInputColumns = 0;
    int NumRanges       = InputFeatureRangesListStrings.length;
    
    for (int i = 0; i < NumRanges; i++) {
      
      String string = InputFeatureRangesListStrings[i];
      
      if (string.contains("-")) {
        
        System.out.println(string);
        
        String [] parts = string.split("-");
        
        int indexStart = Integer.parseInt(parts[0]) - 1;
        int indexEnd   = Integer.parseInt(parts[1]) - 1 + 1;
        
        for (int index = indexStart; index < indexEnd; index++) {
          
          NumInputColumns++;
          
        }
        
      } else {
        NumInputColumns++;
      }
    }
    
    String [] InputFeatureNumberListStrings  = new String[NumInputColumns];
    
    int inputColumnsIndex = 0;
    for (int i = 0; i < NumRanges; i++) {
      
      String string = InputFeatureRangesListStrings[i];
      
      if (string.contains("-")) {
        
        System.out.println(string);
        
        String [] parts = string.split("-");
        
        int indexStart = Integer.parseInt(parts[0]);
        int indexEnd   = Integer.parseInt(parts[1]) + 1;
        
        for (int index = indexStart; index < indexEnd; index++) {
          InputFeatureNumberListStrings[inputColumnsIndex] = Integer.toString(index);
          inputColumnsIndex++;
        }
        
      } else {
          InputFeatureNumberListStrings[inputColumnsIndex] = string;
        inputColumnsIndex++;
      }
    }
    

    String[] OutputFeatureNumberListStrings = Utility.parseCSVList(OutputFeatureNumberList);
    int NumOutputColumns = OutputFeatureNumberListStrings.length;

    String[] NominalFeatureNumberListStrings = Utility.parseCSVList(NominalFeatureNumberList);
    int numNominalFeatures = NominalFeatureNumberListStrings.length;

    boolean[] NominalColumn = new boolean[NumColumns];
    for (int i = 0; i < numNominalFeatures; i++) {
      NominalColumn[Integer.parseInt(NominalFeatureNumberListStrings[i]) - 1] = true;
    }

    int NumNominalInputFeatures = 0;
    int NumNominalOutputFeatures = 0;
    int NumBooleanNominalInputFeatures = 0;
    int NumBooleanNominalOutputFeatures = 0;

    boolean[] InputColumnSelected = new boolean[NumColumns];
    int[] InputColumnIndices = new int[NumInputColumns];
    for (int i = 0; i < NumInputColumns; i++) {
      
      String string = InputFeatureNumberListStrings[i];
      
      int index = Integer.parseInt(InputFeatureNumberListStrings[i]) - 1;
      InputColumnSelected[index] = true;
      InputColumnIndices[i] = index;
      if (NominalToBooleanExpansion && NominalColumn[index]) {
        NumNominalInputFeatures++;
        NumBooleanNominalInputFeatures += UniqueColumnStrings[index].length;
      }
      
    }
    boolean[] OutputColumnSelected = new boolean[NumColumns];
    int[] OutputColumnIndices = new int[NumOutputColumns];
    for (int i = 0; i < NumOutputColumns; i++) {
      int index = Integer.parseInt(OutputFeatureNumberListStrings[i]) - 1;
      OutputColumnSelected[index] = true;
      OutputColumnIndices[i] = index;

      if (NominalToBooleanExpansion && NominalColumn[index]) {
        NumNominalOutputFeatures++;
        NumBooleanNominalOutputFeatures += UniqueColumnStrings[index].length;
      }
    }


    int NumExamples = NumRowsToRead;


    if (NominalToBooleanExpansion) {
      System.out.println("NumNominalInputFeatures  = " + NumNominalInputFeatures);
      System.out.println("NumNominalOutputFeatures = " + NumNominalOutputFeatures);
      System.out.println("NumBooleanNominalInputFeatures  = " + NumBooleanNominalInputFeatures);
      System.out.println("NumBooleanNominalOutputFeatures = " + NumBooleanNominalOutputFeatures);
    }


    int NumFeatures;

    int NumInputs;
    int NumOutputs;
    if (NominalToBooleanExpansion) {
      NumInputs = NumInputColumns - NumNominalInputFeatures + NumBooleanNominalInputFeatures;
      NumOutputs = NumOutputColumns - NumNominalOutputFeatures + NumBooleanNominalOutputFeatures;
    } else {
      NumInputs = NumInputColumns;
      NumOutputs = NumOutputColumns;
    }


    System.out.println("NumInputs  = " + NumInputs);
    System.out.println("NumOutputs = " + NumOutputs);

    NumFeatures = NumInputs + NumOutputs;

    double[] Data = new double[NumExamples * NumFeatures];


    ///////////////////////////////////////////////////////////
    //  READ WHOLE FILE AND MAP NOMINALS TO NUMERIC INDICES  //
    ///////////////////////////////////////////////////////////

    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));


    // skip header if necessary
    for (int r = 0; r < numRowsToSkip; r++) {
      bufferedReader.readLine();
    }

    Hashtable[] InputFeatureHashTable = new Hashtable[NumInputColumns];
    Hashtable[] OutputFeatureHashTable = new Hashtable[NumOutputColumns];

    for (int i = 0; i < NumInputColumns; i++) {
      InputFeatureHashTable[i] = new Hashtable();
    }
    for (int i = 0; i < NumOutputColumns; i++) {
      OutputFeatureHashTable[i] = new Hashtable();
    }

    int[] InputUniqueValueCount = new int[NumInputColumns];
    int[] OutputUniqueValueCount = new int[NumOutputColumns];


    // initialize the hash tables mapping nominal strings to integer values
    {
      int InputFeatureIndex = 0;
      int OutputFeatureIndex = 0;
      for (int f = 0; f < NumColumns; f++) {


        if (NominalColumn[f]) {

          if (InputColumnSelected[f]) {

            for (int i = 0; i < UniqueColumnStrings[f].length; i++) {
              String Name = UniqueColumnStrings[f][i];
              if (!InputFeatureHashTable[InputFeatureIndex].containsKey(Name)) {
                InputFeatureHashTable[InputFeatureIndex].put(Name, new Integer(InputUniqueValueCount[InputFeatureIndex]));
                InputUniqueValueCount[InputFeatureIndex]++;
              }
            }

            InputFeatureIndex++;

          }

          if (OutputColumnSelected[f]) {

            for (int i = 0; i < UniqueColumnStrings[f].length; i++) {
              String Name = UniqueColumnStrings[f][i];
              if (!OutputFeatureHashTable[OutputFeatureIndex].containsKey(Name)) {
                OutputFeatureHashTable[OutputFeatureIndex].put(Name, new Integer(OutputUniqueValueCount[OutputFeatureIndex]));
                OutputUniqueValueCount[OutputFeatureIndex]++;
              }
            }

            OutputFeatureIndex++;

          }

        }
      }

    }


    double[] columnValues = new double[NumColumns];

    for (int e = 0; e < NumRowsToRead; e++) {

      String line = bufferedReader.readLine();

      String[] strings = Utility.parseList(line, delimiterChar, useQuotes);

      if (strings.length != NumColumns) {
        System.out.println("Error!!! numColumns read = " + strings.length);
        System.out.println("input=" + line);
      }


      int InputFeatureIndex = 0;
      int OutputFeatureIndex = 0;
      for (int f = 0; f < NumColumns; f++) {

        if (InputColumnSelected[f] || OutputColumnSelected[f]) {

          if (NominalColumn[f]) {

            String NominalString = strings[f];

            double value = 0.0;

            if (InputColumnSelected[f]) {

              if (InputFeatureHashTable[InputFeatureIndex].containsKey(NominalString)) {
                value = ((Integer) InputFeatureHashTable[InputFeatureIndex].get(NominalString)).intValue();
              } else {
                throw new Exception();
              }

              InputFeatureIndex++;

            }

            if (OutputColumnSelected[f]) {

              if (OutputFeatureHashTable[OutputFeatureIndex].containsKey(NominalString)) {
                value = ((Integer) OutputFeatureHashTable[OutputFeatureIndex].get(NominalString)).intValue();
              } else {
                throw new Exception();
              }

              OutputFeatureIndex++;

            }

            columnValues[f] = value;

          } else {


            //System.out.println("strings[" + (f+1) + "]=" + strings[f]);

            String valueString = strings[f];
            
            columnValues[f] = 0.0;
            
            if ((valueString.length() == 0) || valueString.equals("."))
              columnValues[f] = 0.0;
            else {
              try {
                columnValues[f] = Double.parseDouble(valueString);
              } catch (NumberFormatException ex) {
              }
            }

          }
        }
      }


      if (NominalToBooleanExpansion) {
        int InputIndex = 0;
        for (int i = 0; i < NumInputColumns; i++) {
          if (NominalColumn[InputColumnIndices[i]]) {
            for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[InputColumnIndices[i]]; NominalIndex++) {
              if (columnValues[InputColumnIndices[i]] == NominalIndex) {
                Data[e * NumFeatures + InputIndex] = 1;
              } else {
                Data[e * NumFeatures + InputIndex] = 0;
              }
              InputIndex++;
            }
          } else {
            Data[e * NumFeatures + InputIndex] = (double) columnValues[InputColumnIndices[i]];
            InputIndex++;
          }
        }
        int OutputIndex = 0;
        for (int i = 0; i < NumOutputColumns; i++) {
          if (NominalColumn[OutputColumnIndices[i]]) {
            for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[OutputColumnIndices[i]]; NominalIndex++) {
              if (columnValues[OutputColumnIndices[i]] == NominalIndex) {
                Data[e * NumFeatures + NumInputs + OutputIndex] = 1;
              } else {
                Data[e * NumFeatures + NumInputs + OutputIndex] = 0;
              }
              OutputIndex++;
            }
          } else {
            Data[e * NumFeatures + NumInputs + OutputIndex] = (double) columnValues[OutputColumnIndices[i]];
            OutputIndex++;
          }
        }
      } else {
        // add values to the example table raw data array
        for (int i = 0; i < NumInputColumns; i++) {
          Data[e * NumFeatures + i] = (double) columnValues[InputColumnIndices[i]];
        }
        for (int i = 0; i < NumOutputColumns; i++) {
          Data[e * NumFeatures + NumInputs + i] = (double) columnValues[OutputColumnIndices[i]];
        }
      }

    }
    bufferedReader.close();

    String[][] InputNominalNames = new String[NumInputColumns][];

    for (int i = 0; i < NumInputColumns; i++) {

      InputNominalNames[i] = new String[InputUniqueValueCount[i]];

      Enumeration InputKeys = InputFeatureHashTable[i].keys();
      for (int v = 0; v < InputUniqueValueCount[i]; v++) {
        String key = (String) InputKeys.nextElement();
        int index = ((Integer) InputFeatureHashTable[i].get(key)).intValue();
        InputNominalNames[i][index] = key;
      }
    }

    String[][] OutputNominalNames = new String[NumOutputColumns][];

    for (int i = 0; i < NumOutputColumns; i++) {

      OutputNominalNames[i] = new String[OutputUniqueValueCount[i]];

      Enumeration OutputKeys = OutputFeatureHashTable[i].keys();
      for (int v = 0; v < OutputUniqueValueCount[i]; v++) {
        String key = (String) OutputKeys.nextElement();
        int index = ((Integer) OutputFeatureHashTable[i].get(key)).intValue();
        OutputNominalNames[i][index] = key;
      }
    }

    this.pushOutput(InputNominalNames, 1);
    this.pushOutput(OutputNominalNames, 2);

    String[] InputNames = new String[NumInputs];
    String[] OutputNames = new String[NumOutputs];

    if (NominalToBooleanExpansion) {
      int InputIndex = 0;
      for (int i = 0; i < NumInputColumns; i++) {
        if (NominalColumn[InputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[InputColumnIndices[i]]; NominalIndex++) {
            InputNames[InputIndex] = ColumnNames[InputColumnIndices[i]] + "=" + UniqueColumnStrings[InputColumnIndices[i]][NominalIndex];
            InputIndex++;
          }
        } else {
          InputNames[InputIndex] = ColumnNames[InputColumnIndices[i]];
          InputIndex++;
        }
      }
      int OutputIndex = 0;
      for (int i = 0; i < NumOutputColumns; i++) {
        if (NominalColumn[OutputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[OutputColumnIndices[i]]; NominalIndex++) {
            OutputNames[OutputIndex] = ColumnNames[OutputColumnIndices[i]] + "=" + UniqueColumnStrings[OutputColumnIndices[i]][NominalIndex];
            OutputIndex++;
          }
        } else {
          OutputNames[OutputIndex] = ColumnNames[OutputColumnIndices[i]];
          OutputIndex++;
        }
      }

    } else {
      for (int i = 0; i < NumInputs; i++) {
        InputNames[i] = ColumnNames[InputColumnIndices[i]];
      }
      for (int i = 0; i < NumOutputs; i++) {
        OutputNames[i] = ColumnNames[OutputColumnIndices[i]];
      }
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, NumExamples, NumInputs, NumOutputs, InputNames, OutputNames);

    this.pushOutput((ExampleTable) exampleSet, 0);

  }

}
