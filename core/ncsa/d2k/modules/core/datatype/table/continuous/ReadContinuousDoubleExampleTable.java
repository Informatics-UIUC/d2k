package ncsa.d2k.modules.core.datatype.table.continuous;

import ncsa.d2k.modules.core.io.file.FlatFile;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class ReadContinuousDoubleExampleTable
    extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "C:\\cygwin\\home\\Administrator\\data\\table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private byte EOLByte = 10;
  public void setEOLByte(byte value) {
    this.EOLByte = value;
  }

  public byte getEOLByte() {
    return this.EOLByte;
  }

  private String DelimiterCharacter = ",";
  public void setDelimiterCharacter(String value) {
    this.DelimiterCharacter = value;

    if (value.equalsIgnoreCase("tab") || value.equalsIgnoreCase("\t")) {
      setDelimiterByte( (byte) 9);
      return;
    }
    if (value.equalsIgnoreCase("comma") || value.equalsIgnoreCase(",")) {
      setDelimiterByte( (byte) ',');
      return;
    }
    if (value.equalsIgnoreCase("space") || value.equalsIgnoreCase(" ")) {
      setDelimiterByte( (byte) ' ');
      return;
    }

    setDelimiterByte( (byte) (this.DelimiterCharacter.charAt(0)));
  }

  public String getDelimiterCharacter() {
    return this.DelimiterCharacter;
  }

  private byte DelimiterByte = (byte) ',';
  public void setDelimiterByte(byte value) {
    this.DelimiterByte = value;
  }

  public byte getDelimiterByte() {
    return this.DelimiterByte;
  }

  private int NumRowsToSkip = 1;
  public void setNumRowsToSkip(int value) {
    this.NumRowsToSkip = value;
  }

  public int getNumRowsToSkip() {
    return this.NumRowsToSkip;
  }

  private int NumRowsToRead = 60;
  public void setNumRowsToRead(int value) {
    this.NumRowsToRead = value;
  }

  public int getNumRowsToRead() {
    return this.NumRowsToRead;
  }

  private boolean FixedFormat = false;
  public void setFixedFormat(boolean value) {
    this.FixedFormat = value;
  }

  public boolean getFixedFormat() {
    return this.FixedFormat;
  }

  private boolean ConvertStringsToIndices = false;
  public void setConvertStringsToIndices(boolean value) {
    this.ConvertStringsToIndices = value;
  }

  public boolean getConvertStringsToIndices() {
    return this.ConvertStringsToIndices;
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

  private String FeatureTypeString = "IO";
  public void setFeatureTypeString(String value) {
    this.FeatureTypeString = value;
  }

  public String getFeatureTypeString() {
    return this.FeatureTypeString;
  }

  public String getModuleName() {
    return "ReadContinuousExampleTable";
  }
  public String getModuleInfo() {
    return "ReadContinuousExampleTable";
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
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[S",
        "[S"};
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
    int numExamples = NumRowsToRead;
    byte[] featureTypeBytes = null;

    featureTypeBytes = FeatureTypeString.getBytes();
    int numColumns = featureTypeBytes.length;

    boolean[] inputColumnSelected = new boolean[numColumns];
    boolean[] outputColumnSelected = new boolean[numColumns];
    int[] inputColumnIndices = new int[numColumns];
    int[] outputColumnIndices = new int[numColumns];

    int numInputs = 0;
    int numOutputs = 0;
    for (int i = 0; i < numColumns; i++) {
      switch (featureTypeBytes[i]) {
        case (byte) 'I':
        case (byte) 'i':
          inputColumnSelected[i] = true;
          inputColumnIndices[numInputs++] = i;
          break;
        case (byte) 'O':
        case (byte) 'o':
          outputColumnSelected[i] = true;
          outputColumnIndices[numOutputs++] = i;
          break;
      }
    }

    int numFeatures = numInputs + numOutputs;

    double[] data = new double[numExamples * numFeatures];
    String[] columnNames = new String[numColumns];

    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;

    int example_index = 0;

    // read column names

    {
      FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = rio.Buffer;
      rio.DelimiterByte = DelimiterByte;
      rio.EOLByte = EOLByte;

      rio.readLine();
      String name;

      for (int f = 0; f < numColumns; f++) {
        if (FixedFormat)
          name = new String(buffer, rio.LineStart + (f * CharsPerColumn),
                            CharsPerColumn);
        else
          name = new String(buffer, rio.ColumnStarts[f],
                            rio.ColumnEnds[f] - rio.ColumnStarts[f]);

        columnNames[f] = name;

      }

      rio.close();
    }


    if (ConvertStringsToIndices)

    {
      FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = rio.Buffer;
      rio.DelimiterByte = DelimiterByte;
      rio.EOLByte = EOLByte;

      for (int r = 0; r < NumRowsToSkip; r++) {
        rio.readLine();
      }

      Hashtable inputFeatureHashTable = new Hashtable();
      Hashtable outputFeatureHashTable = new Hashtable();
      int inputUniqueValueCount = 0;
      int outputUniqueValueCount = 0;
      double[] columnValues = new double[numColumns];
      for (int e = 0; e < NumRowsToRead; e++) {

        rio.readLine();

        for (int f = 0; f < numColumns; f++) {

          String name;
          if (FixedFormat)
            name = new String(buffer, rio.LineStart + (f * CharsPerColumn),
                              CharsPerColumn);
          else
            name = new String(buffer, rio.ColumnStarts[f],
                              rio.ColumnEnds[f] - rio.ColumnStarts[f]);

          double value = 0.0;

          if (inputColumnSelected[f]) {
            if (inputFeatureHashTable.containsKey(name)) {
              value = ( (Integer) inputFeatureHashTable.get(name)).intValue();
            }
            else {
              value = inputUniqueValueCount;
              inputFeatureHashTable.put(name, new Integer(inputUniqueValueCount));
              inputUniqueValueCount++;
            }
          }

          if (outputColumnSelected[f]) {
            if (outputFeatureHashTable.containsKey(name)) {
              value = ( (Integer) outputFeatureHashTable.get(name)).intValue();
            }
            else {
              value = outputUniqueValueCount;
              outputFeatureHashTable.put(name, new Integer(outputUniqueValueCount));
              outputUniqueValueCount++;
            }
          }

          columnValues[f] = value;

        }

        for (int i = 0; i < numInputs; i++)
          data[e * numFeatures +
              i] = (double) columnValues[inputColumnIndices[i]];
        for (int i = 0; i < numOutputs; i++)
          data[e * numFeatures + numInputs +
              i] = (double) columnValues[outputColumnIndices[i]];

      }
      rio.close();

      String inputNominalNames[] = new String[inputUniqueValueCount];
      Enumeration inputKeys = inputFeatureHashTable.keys();
      for (int i = 0; i < inputUniqueValueCount; i++) {
        String key = (String) inputKeys.nextElement();
        int index = ( (Integer) inputFeatureHashTable.get(key)).intValue();
        inputNominalNames[index] = key;
      }

      this.pushOutput(inputNominalNames, 1);

      String outputNominalNames[] = new String[outputUniqueValueCount];
      Enumeration keys = outputFeatureHashTable.keys();
      for (int i = 0; i < outputUniqueValueCount; i++) {
        String key = (String) keys.nextElement();
        int index = ( (Integer) outputFeatureHashTable.get(key)).intValue();
        outputNominalNames[index] = key;
      }

      this.pushOutput(outputNominalNames, 2);


    }
    else {
      FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = rio.Buffer;
      rio.DelimiterByte = DelimiterByte;
      rio.EOLByte = EOLByte;

      for (int r = 0; r < NumRowsToSkip; r++) {
        rio.readLine();
      }

      double[] columnValues = new double[numColumns];
      for (int e = 0; e < NumRowsToRead; e++) {

        rio.readLine();

        for (int f = 0; f < numColumns; f++) {

          if (FixedFormat)
            columnValues[f] = rio.ByteStringToDouble(buffer,
                                           rio.LineStart +
                                           (f * CharsPerColumn),
                                           rio.LineStart +
                                           ( (f + 1) * CharsPerColumn) - 1);
          else
            columnValues[f] = rio.ByteStringToDouble(buffer, rio.ColumnStarts[f],
                                           rio.ColumnEnds[f]);

        }

        for (int i = 0; i < numInputs; i++)
          data[e * numFeatures +
              i] = (double) columnValues[inputColumnIndices[i]];
        for (int i = 0; i < numOutputs; i++)
          data[e * numFeatures + numInputs +
              i] = (double) columnValues[outputColumnIndices[i]];

      }
      rio.close();
    }


    String[] inputNames = new String[numInputs];
    String[] outputNames = new String[numOutputs];

    for (int i = 0; i < numInputs; i++) {
      inputNames[i] = columnNames[inputColumnIndices[i]];
    }
    for (int i = 0; i < numOutputs; i++) {
      outputNames[i] = columnNames[outputColumnIndices[i]];
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(
        data, numExamples, numInputs, numOutputs, inputNames, outputNames);
    this.pushOutput( (ExampleTable) exampleSet, 0);

  }

}