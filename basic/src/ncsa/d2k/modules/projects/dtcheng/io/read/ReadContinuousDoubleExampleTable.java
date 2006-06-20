package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;

public class ReadContinuousDoubleExampleTable extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private int DelimiterByte = ',';

  public void setDelimiterByte(int value) {
    this.DelimiterByte = value;
  }

  public int getDelimiterByte() {
    return this.DelimiterByte;
  }

  private int NumRowsToSkip = 1;

  public void setNumRowsToSkip(int value) {
    this.NumRowsToSkip = value;
  }

  public int getNumRowsToSkip() {
    return this.NumRowsToSkip;
  }

  private int numRowsToRead = 60;

  public void setNumRowsToRead(int value) {
    this.numRowsToRead = value;
  }

  public int getNumRowsToRead() {
    return this.numRowsToRead;
  }

  private boolean fixedFormat = false;
  
  public void setFixedFormat(boolean value) {
    this.fixedFormat = value;
  }
  public  boolean  getFixedFormat() {
    return this.fixedFormat;
  }

  public  boolean        UseStandardFeatureOrdering = true;
  public  void    setUseStandardFeatureOrdering (boolean value) {       this.UseStandardFeatureOrdering = value;}
  public  boolean     getUseStandardFeatureOrdering ()          {return this.UseStandardFeatureOrdering;}


  
  

  private boolean UseJavaDoubleParser = false;
  public void setUseJavaDoubleParser(boolean value) {
    this.UseJavaDoubleParser = value;
  }

  public boolean getUseJavaDoubleParser() {
    return this.UseJavaDoubleParser;
  }

  private boolean ConvertStringsToIndices = false;

  public void setConvertStringsToIndices(boolean value) {
    this.ConvertStringsToIndices = value;
  }

  public boolean getConvertStringsToIndices() {
    return this.ConvertStringsToIndices;
  }

  private int EndOfLineByte1 = 10;

  public void setEndOfLineByte1(int value) {
    this.EndOfLineByte1 = value;
  }

  public int getEndOfLineByte1() {
    return this.EndOfLineByte1;
  }

  private int CharsPerColumn = 16;

  public void setCharsPerColumn(int value) {
    this.CharsPerColumn = value;
  }

  public int getCharsPerColumn() {
    return this.CharsPerColumn;
  }

  private int ReadBufferSize = 1000000;

  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }

  public int getReadBufferSize() {
    return this.ReadBufferSize;
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

  public String getModuleName() {
    return "ReadContinuousDoubleExampleTable";
  }

  public String getModuleInfo() {
    return "ReadContinuousExampleTable comma = 44; space = 32; tab = 9; ";
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
      return "NO SUCH INPUT!";
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

        if (fixedFormat)
          ColumnNames[f] = line.substring(f * CharsPerColumn, CharsPerColumn);
        else
          ColumnNames[f] = strings[f];

      }

      for (int f = 0; f < NumColumns; f++) {
        System.out.println(" ColumnNames[" + (f + 1) + "] = " + ColumnNames[f]);

      }
      bufferedReader.close();
    }
    System.out.println("NumColumns = " + NumColumns);

    //System.out.println("user.dir = " + System.getProperty("user.dir"));

    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////
    
    
      boolean[] InputColumnSelected = new boolean[NumColumns];
      boolean[] OutputColumnSelected = new boolean[NumColumns];
      
      
      String[] NominalFeatureNumberListStrings = Utility.parseCSVList(NominalFeatureNumberList);
      int numNominalFeatures = NominalFeatureNumberListStrings.length;
      
      boolean[] NominalColumn = new boolean[NumColumns];
      for (int i = 0; i < numNominalFeatures; i++) {
        NominalColumn[Integer.parseInt(NominalFeatureNumberListStrings[i]) - 1] = true;
      }
      
      
      int[] InputColumnIndices  = null;
      int[] OutputColumnIndices = null;
    
      int numInputFeatures = -1;
      int numOutputFeatures = -1;
      
      if (UseStandardFeatureOrdering) {
        
        numInputFeatures  = NumColumns - 1;
        numOutputFeatures = 1;
        
        
        InputColumnIndices = new int[numInputFeatures];
        for (int i = 0; i < numInputFeatures; i++) {
          InputColumnSelected[i] = true;
          InputColumnIndices[i] = i;
        }
        OutputColumnIndices                  = new int[numOutputFeatures];
        OutputColumnSelected[NumColumns - 1] = true;
        OutputColumnIndices[0]               = NumColumns - 1;
        
      } else {
        String[] InputFeatureNumberListStrings = Utility.parseCSVList(InputFeatureNumberList);
        numInputFeatures = InputFeatureNumberListStrings.length;
        
        String[] OutputFeatureNumberListStrings = Utility.parseCSVList(OutputFeatureNumberList);
        numOutputFeatures = OutputFeatureNumberListStrings.length;
        
        InputColumnIndices = new int[numInputFeatures];
        for (int i = 0; i < numInputFeatures; i++) {
          int index = Integer.parseInt(InputFeatureNumberListStrings[i]) - 1;
          InputColumnSelected[index] = true;
          InputColumnIndices[i] = index;
        }
        OutputColumnIndices = new int[numOutputFeatures];
        for (int i = 0; i < numOutputFeatures; i++) {
          int index = Integer.parseInt(OutputFeatureNumberListStrings[i]) - 1;
          OutputColumnSelected[index] = true;
          OutputColumnIndices[i] = index;
        }
      }


    int NumExamples = -1;

    if (numRowsToRead == -1)
      NumExamples = numLines - NumRowsToSkip;
    else
      NumExamples = numRowsToRead;

    int NumFeatures = numInputFeatures + numOutputFeatures;

    double[] Data = new double[NumExamples * NumFeatures];


    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;

    int example_index = 0;


    //////////////////////////////////////////////////////////
    //  READ WHOLE FILE AND MAP STRINGS TO NUMERIC INDICES  //
    //////////////////////////////////////////////////////////

    if (ConvertStringsToIndices) {

      FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = Rio.Buffer;
      Rio.DelimiterByte = (byte) DelimiterByte;
      Rio.EOLByte1 = (byte) EndOfLineByte1;

      // skip header if necessary
      for (int r = 0; r < NumRowsToSkip; r++) {
        Rio.readLine();
      }

      Hashtable[] InputFeatureHashTable = new Hashtable[numInputFeatures];
      Hashtable[] OutputFeatureHashTable = new Hashtable[numOutputFeatures];

      for (int i = 0; i < numInputFeatures; i++)
        InputFeatureHashTable[i] = new Hashtable();
      for (int i = 0; i < numOutputFeatures; i++)
        OutputFeatureHashTable[i] = new Hashtable();

      int[] InputUniqueValueCount = new int[numInputFeatures];
      int[] OutputUniqueValueCount = new int[numOutputFeatures];

      double[] columnValues = new double[NumColumns];

      for (int e = 0; e < NumExamples; e++) {

        Rio.readLine();

        int InputFeatureIndex = 0;
        int OutputFeatureIndex = 0;
        for (int f = 0; f < NumColumns; f++) {

          if (NominalColumn[f]) {

            String Name;

            if (fixedFormat)
              Name = new String(buffer, Rio.LineStart + (f * CharsPerColumn), CharsPerColumn);
            else
              Name = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);

            double value = 0.0;

            if (InputColumnSelected[f]) {

              if (InputFeatureHashTable[InputFeatureIndex].containsKey(Name)) {
                value = ((Integer) InputFeatureHashTable[InputFeatureIndex].get(Name)).intValue();
              } else {
                value = InputUniqueValueCount[InputFeatureIndex];
                InputFeatureHashTable[InputFeatureIndex].put(Name, new Integer(InputUniqueValueCount[InputFeatureIndex]));
                InputUniqueValueCount[InputFeatureIndex]++;
              }

              InputFeatureIndex++;

            }

            if (OutputColumnSelected[f]) {

              if (OutputFeatureHashTable[OutputFeatureIndex].containsKey(Name)) {
                value = ((Integer) OutputFeatureHashTable[OutputFeatureIndex].get(Name)).intValue();
              } else {
                value = OutputUniqueValueCount[OutputFeatureIndex];
                OutputFeatureHashTable[OutputFeatureIndex].put(Name, new Integer(OutputUniqueValueCount[OutputFeatureIndex]));
                OutputUniqueValueCount[OutputFeatureIndex]++;
              }

              OutputFeatureIndex++;

            }

            columnValues[f] = value;

          } else {
            if (fixedFormat) {
              if (UseJavaDoubleParser) {
                String string = new String(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
                columnValues[f] = Double.parseDouble(string);
              } else {
                columnValues[f] = FlatFile.ByteStringToDouble(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
              }
            } else {
              if (UseJavaDoubleParser) {
                String string = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);
                if (Rio.ColumnEnds[f] == Rio.ColumnStarts[f])
                  columnValues[f] = 0.0;
                else
                {
                  System.out.println("featureNumber = " + (f+1));
                  columnValues[f] = Double.parseDouble(string);
                }

              } else {
                columnValues[f] = FlatFile.ByteStringToDouble(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
              }
            }

          }
        }

        for (int i = 0; i < numInputFeatures; i++) {
          Data[e * NumFeatures + i] = (double) columnValues[InputColumnIndices[i]];
        }
        for (int i = 0; i < numOutputFeatures; i++) {
          Data[e * NumFeatures + numInputFeatures + i] = (double) columnValues[OutputColumnIndices[i]];
        }

      }
      Rio.close();

      String[][] InputNominalNames = new String[numInputFeatures][];

      for (int i = 0; i < numInputFeatures; i++) {

        InputNominalNames[i] = new String[InputUniqueValueCount[i]];

        Enumeration InputKeys = InputFeatureHashTable[i].keys();
        for (int v = 0; v < InputUniqueValueCount[i]; v++) {
          String key = (String) InputKeys.nextElement();
          int index = ((Integer) InputFeatureHashTable[i].get(key)).intValue();
          InputNominalNames[i][index] = key;
        }
      }

      String[][] OutputNominalNames = new String[numOutputFeatures][];

      for (int i = 0; i < numOutputFeatures; i++) {

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

    }

    else {

      FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = Rio.Buffer;
      Rio.DelimiterByte = (byte) DelimiterByte;
      Rio.EOLByte1 = (byte) EndOfLineByte1;

      for (int r = 0; r < NumRowsToSkip; r++) {
        Rio.readLine();
      }

      double[] columnValues = new double[NumColumns];
      for (int e = 0; e < NumExamples; e++) {

        Rio.readLine();

        for (int i = 0; i < numInputFeatures; i++) {
          int f = InputColumnIndices[i];
          double value;
          if (fixedFormat) {
            if (UseJavaDoubleParser) {
              String string = new String(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
              value = Double.parseDouble(string);
            } else {
              value = FlatFile.ByteStringToDouble(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
            }
          } else {
            if (UseJavaDoubleParser) {
              
              String string = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);

              if (Rio.ColumnEnds[f] == Rio.ColumnStarts[f])
                value = 0;
              else
                value = Double.parseDouble(string);

            } else {
              value = FlatFile.ByteStringToDouble(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
            }
          }

          Data[e * NumFeatures + i] = value;
        }
        for (int i = 0; i < numOutputFeatures; i++) {
          int f = OutputColumnIndices[i];
          double value;
          if (fixedFormat) {
            if (UseJavaDoubleParser) {
              String string = new String(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
              value = Double.parseDouble(string);
            } else {
              value = FlatFile.ByteStringToDouble(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
            }
          } else {
            if (UseJavaDoubleParser) {
              String string = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);
              if (Rio.ColumnEnds[f] == Rio.ColumnStarts[f])
                value = 0;
              else
                value = Double.parseDouble(string);

            } else {
              value = FlatFile.ByteStringToDouble(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
            }
          }

          Data[e * NumFeatures + numInputFeatures + i] = value;
        }
      }
      Rio.close();
    }

    String[] inputNames = new String[numInputFeatures];
    String[] outputNames = new String[numOutputFeatures];

    for (int i = 0; i < numInputFeatures; i++) {
      inputNames[i] = ColumnNames[InputColumnIndices[i]];
    }
    for (int i = 0; i < numOutputFeatures; i++) {
      outputNames[i] = ColumnNames[OutputColumnIndices[i]];
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, NumExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);

    this.pushOutput((ExampleTable) exampleSet, 0);

  }

}