package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ReadStringArrayStream extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "data/data.txt";

  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private byte EOLByte1 = 10;

  public void setEOLByte1(byte value) {
    this.EOLByte1 = value;
  }

  public byte getEOLByte1() {
    return this.EOLByte1;
  }

  private String DelimiterCharacter = ",";

  public void setDelimiterCharacter(String value) {
    this.DelimiterCharacter = value;
    if (value.equalsIgnoreCase("tab") || value.equalsIgnoreCase("\t")) {
      setDelimiterByte((byte) 9);
      return;
    }
    if (value.equalsIgnoreCase("comma") || value.equalsIgnoreCase(",")) {
      setDelimiterByte((byte) ',');
      return;
    }
    if (value.equalsIgnoreCase("space") || value.equalsIgnoreCase(" ")) {
      setDelimiterByte((byte) ' ');
      return;
    }
    setDelimiterByte((byte) (this.DelimiterCharacter.charAt(0)));
  }

  public String getDelimiterCharacter() {
    return this.DelimiterCharacter;
  }

  private byte DelimiterByte = (byte) (this.DelimiterCharacter.charAt(0));

  public void setDelimiterByte(byte value) {
    this.DelimiterByte = value;
  }

  public byte getDelimiterByte() {
    return this.DelimiterByte;
  }

  private int MaxNumLines = 1000;

  public void setMaxNumLines(int value) {
    this.MaxNumLines = value;
  }

  public int getMaxNumLines() {
    return this.MaxNumLines;
  }

  private int ReportInterval = 1000;

  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
  }

  private int NumRowsToSkip = 0;

  public void setNumRowsToSkip(int value) {
    this.NumRowsToSkip = value;
  }

  public int getNumRowsToSkip() {
    return this.NumRowsToSkip;
  }

  private int ReadBufferSize = 1000000;

  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }

  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }

  private int NumFields = 1;

  public void setNumFields(int value) {
    this.NumFields = value;
  }

  public int getNumFields() {
    return this.NumFields;
  }

  private boolean Trace = false;

  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  private boolean MicrosoftTextFileFormat = false;

  public void setMicrosoftTextFileFormat(boolean value) {
    this.MicrosoftTextFileFormat = value;
  }

  public boolean getMicrosoftTextFileFormat() {
    return this.MicrosoftTextFileFormat;
  }

  public String getModuleName() {
    return "ReadStringArrayStream";
  }

  public String getModuleInfo() {
    return "This module reads lines from a text file and outputs them one at a time.  "
        + "When the specified number (MaxNumLines)or all of lines are read, it outputs a "
        + "null pointer to mark the end of available input. If MaxNumLines is -1, all lines are read.  "
        + "The DataPath property is the path "
        + "name to the file to be read. The DelimiterByte property is the byte value of the term "
        + "separator (e.g., tab, comma, space). The EOLByte property is the byte value marking the "
        + "end of the line. The MaxNumLines property is the maximum number of lines that will be "
        + "read from the file. The NumRowsToSkip property is the number of rows to skip initially "
        + "when reading the file. The ReadBufferSize property is size in bytes of the read buffer. "
        + "The Trace property is turns on an off the printing of status information for tracing execution.";
  }

  public String getInputName(int i) {
    switch (i) {
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "A string array representing the next line read.  "
          + "The length of the array is variable and depends on the number of strings in the line.";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  private boolean InitialExecution = true;

  int Count;

  FlatFile Input;

  boolean EOF;
  boolean ErrorOnOpen;

  public void beginExecution() {

    try {

      InitialExecution = true;
      EOF = false;
      ErrorOnOpen = false;
      Count = 0;

      boolean ReadWholeLines = true;

      Input = new FlatFile(DataPath, "r", ReadBufferSize, ReadWholeLines);
      Input.DelimiterByte = DelimiterByte;
      Input.EOLByte1 = EOLByte1;
    } catch (Exception e) {
      ErrorOnOpen = true;
    }

  }

  public void endExecution() {

    try {
      Input.close();
      Input = null;
    } catch (Exception e) {
    }

  }
  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = true;
    } else {
      if (!EOF)
        value = true;
    }
    return value;
  }

  public void doit() throws Exception {

    byte[] featureTypeBytes = null;

    int example_index = 0;

    if (InitialExecution) {
      if (Trace)
        System.out.println("reading " + DataPath);

      for (int r = 0; r < NumRowsToSkip; r++) {
        if (Trace)
          System.out.println("Skipping row " + (r + 1));

        Input.readLine();
      }

      InitialExecution = false;
    }


    if (ErrorOnOpen) {
      EOF = true;
      this.pushOutput(null, 0);
      return;
    }
    
    Input.readLine();

    if (Input.EOF) {
      EOF = true;
      Input.close();
      this.pushOutput(null, 0);
      return;
    }

    String[] string1DArray = new String[NumFields];
    int numParsedColumns = Input.NumColumns;

    if (numParsedColumns != NumFields) {
      System.out.println("Warning!  numParsedColumns (" + numParsedColumns + ") != NumFields(" + NumFields + ").  ");
    }

    if (Trace && (Count % ReportInterval == 0))
      System.out.println("line " + Count + " = " + new String(Input.Buffer, Input.LineStart, (Input.LineEnd - Input.LineStart - 1)));

    String value = null;
    for (int f = 0; f < NumFields; f++) {
      if ((f == NumFields - 1) && MicrosoftTextFileFormat)
        value = new String(Input.Buffer, Input.ColumnStarts[f], Input.ColumnEnds[f] - Input.ColumnStarts[f] - 1);
      else
        value = new String(Input.Buffer, Input.ColumnStarts[f], Input.ColumnEnds[f] - Input.ColumnStarts[f]);
      string1DArray[f] = value;
      if (Trace)
        System.out.println("f = " + f + " v = " + value + " l = " + value.getBytes().length);
    }

    this.pushOutput(string1DArray, 0);

    Count++;

    if ((Count == MaxNumLines)) {
      EOF = true;
      Input.close();
      this.pushOutput(null, 0);
      return;
    }

  }
}