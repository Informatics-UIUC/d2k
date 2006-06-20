package ncsa.d2k.modules.projects.dtcheng.io.read;


import ncsa.d2k.core.modules.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;


public class ReadUniqueColumnStrings extends InputModule {

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

  
  
  private String dataPath = "table.csv";

  public void setDataPath(String value) {
    this.dataPath = value;
  }


  public String getDataPath() {
    return this.dataPath;
  }


  private int delimiterByte = (int) ',';

  public void setDelimiterByte(int value) {
    this.delimiterByte = value;
  }


  public int getDelimiterByte() {
    return this.delimiterByte;
  }


  private int numRowsToSkip = 1;

  public void setNumRowsToSkip(int value) {
    this.numRowsToSkip = value;
  }


  public int getNumRowsToSkip() {
    return this.numRowsToSkip;
  }
  
  
  private int numColumns = 6;

  public void setNumColumns(int value) {
    this.numColumns = value;
  }


  public int getNumColumns() {
    return this.numColumns;
  }


  private int numRowsToRead = 60;

  public void setNumRowsToRead(int value) {
    this.numRowsToRead = value;
  }


  public int getNumRowsToRead() {
    return this.numRowsToRead;
  }


  private int readBufferSize = 1000000;

  public void setReadBufferSize(int value) {
    this.readBufferSize = value;
  }


  public int getReadBufferSize() {
    return this.readBufferSize;
  }


  private String nominalFeatureNumberList = "1,2,3";

  public void setNominalFeatureNumberList(String value) {
    this.nominalFeatureNumberList = value;
  }


  public String getNominalFeatureNumberList() {
    return this.nominalFeatureNumberList;
  }



  private String excludeFeatureNumberList = "4,5,6";

  public void setExcludeFeatureNumberList(String value) {
    this.excludeFeatureNumberList = value;
  }


  public String getExcludeFeatureNumberList() {
    return this.excludeFeatureNumberList;
  }


  public String getModuleName() {
    return "ReadUniqueColumnStrings";
  }


  public String getModuleInfo() {
    return "ReadUniqueColumnStrings comma = 44; space = 32; tab = 9; ";
  }


  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ColumnUniqueStrings";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ColumnUniqueStrings";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = { "[[Ljava.lang.String;" };
    return out;
  }

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


  public final String[] SortStrings(String[] labels) {
    Arrays.sort(labels, new StringComp());
    return labels;
  }


  private final class StringComp implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }

    public boolean equals(Object o) {
      return super.equals(o);
    }
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    //System.out.println("user.dir = " + System.getProperty("user.dir"));



    char delimiterChar = (char) delimiterByte;
    
    
    
    

    /////////////////////////////
    //  COUNT NUMBER OF LINES  //
    /////////////////////////////

    int numLines = -1;
    {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dataPath)));
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

      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dataPath)));

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

    
    
    

    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////

    String[] NominalFeatureNumberListStrings = Utility.parseCSVList(nominalFeatureNumberList);
    int numNominalFeatures = NominalFeatureNumberListStrings.length;


    boolean[] NominalColumn = new boolean[NumColumns];
    for (int i = 0; i < numNominalFeatures; i++) {
      NominalColumn[Integer.parseInt(NominalFeatureNumberListStrings[i]) - 1] = true;
    }


    String[] excludeFeatureNumberListStrings = Utility.parseCSVList(excludeFeatureNumberList);
    int numExcludeFeatures = excludeFeatureNumberListStrings.length;

    boolean[] excludeColumn = new boolean[NumColumns];
    for (int i = 0; i < numExcludeFeatures; i++) {
      excludeColumn[Integer.parseInt(excludeFeatureNumberListStrings[i]) - 1] = true;
    }

  

    
    

    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dataPath)));    
    
    int example_index = 0;

    Hashtable[] FeatureHashTable = new Hashtable[NumColumns];

    for (int i = 0; i < NumColumns; i++)
      FeatureHashTable[i] = new Hashtable();

    // set UniqueValueCount to zero
    int[] UniqueValueCount = new int[NumColumns];

    // skip header if necessary
    for (int r = 0; r < numRowsToSkip; r++) {
      bufferedReader.readLine();
    }

    /////////////////
    //  READ FILE  //
    /////////////////
    
    int numDataRows = numLines - numRowsToSkip;
    
    if (numRowsToRead == -1) {
      numRowsToRead = numDataRows;
    }
    
    for (int e = 0; e < numRowsToRead; e++) {

      String line = bufferedReader.readLine();

      String[] strings = Utility.parseList(line, delimiterChar, useQuotes);
      
      if (strings.length != numColumns) {
        System.out.println("Error!!! numColumns read = " + strings.length);
        System.out.println("input=" + line);
      }

      for (int f = 0; f < NumColumns; f++) {

        if (NominalColumn[f] && !excludeColumn[f]) {

          String Name;

          Name = strings[f];

          double value = 0.0;

          if (!FeatureHashTable[f].containsKey(Name)) {
            FeatureHashTable[f].put(Name, new Integer(UniqueValueCount[f]));
            UniqueValueCount[f]++;
          }

        }

      }

    }
    
    bufferedReader.close();

    String[][] NominalNames = new String[NumColumns][];

    for (int f = 0; f < NumColumns; f++) {

      NominalNames[f] = new String[UniqueValueCount[f]];

      Enumeration InputKeys = FeatureHashTable[f].keys();
      for (int v = 0; v < UniqueValueCount[f]; v++) {
        String key = (String) InputKeys.nextElement();
        int index = ((Integer) FeatureHashTable[f].get(key)).intValue();
        NominalNames[f][index] = key;
      }

      NominalNames[f] = SortStrings(NominalNames[f]);
    }

    this.pushOutput(NominalNames, 0);

  }

}
