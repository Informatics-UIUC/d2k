package ncsa.d2k.modules.projects.dtcheng.io.read;


import ncsa.d2k.core.modules.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;


public class ReadAndTranslateAIBFile extends InputModule {
  
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
  
  
  private int numColumns = 95;
  
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
  
  public String getModuleName() {
    return "ReadAndTranslateAIBFile";
  }
  
  
  public String getModuleInfo() {
    return "ReadAndTranslateAIBFile";
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
    
    
    
    
    
    
    
    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dataPath)));
    
    int NumExamples = numRowsToRead;
    
    int example_index = 0;
    
    
    // skip header if necessary
    for (int r = 0; r < numRowsToSkip; r++) {
      String string = bufferedReader.readLine();
      
      System.out.println(string);
    }
    
    /////////////////
    //  READ FILE  //
    /////////////////
    
    if (true)
      for (int e = 0; e < numRowsToRead; e++) {
      
      String line = bufferedReader.readLine();
      
      String[] strings = Utility.parseList(line, delimiterChar, useQuotes);
      
      if (strings.length != numColumns) {
        System.out.println("Error!!! numColumns read = " + strings.length);
        System.out.println("input=" + line);
      }
      
      
      int injuryTypeIndex = 13 - 1;
      
      if (strings[injuryTypeIndex].equals("04"))
        strings[injuryTypeIndex] = "SS";
      else if (strings[injuryTypeIndex].equals("05"))
        strings[injuryTypeIndex] = "SS";
      else if (strings[injuryTypeIndex].equals("06"))
        strings[injuryTypeIndex] = "SS";
      
      else if (strings[injuryTypeIndex].equals("01"))
        strings[injuryTypeIndex] = "MM";
      else if (strings[injuryTypeIndex].equals("22"))
        strings[injuryTypeIndex] = "MM";
      else if (strings[injuryTypeIndex].equals("30"))
        strings[injuryTypeIndex] = "MM";
      
      else if (strings[injuryTypeIndex].equals("02"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("03"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("08"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("10"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("17"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("20"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("21"))
        strings[injuryTypeIndex] = "SE";
      else if (strings[injuryTypeIndex].equals("99"))
        strings[injuryTypeIndex] = "SE";
      else strings[injuryTypeIndex] = "MJ";
      
      
      int IME_ResIndex = 16 - 1;
      
      if (!strings[IME_ResIndex].equals("")) {
        if (strings[IME_ResIndex].equals("1")) {
          strings[IME_ResIndex] = "UR";
        } else {
          strings[IME_ResIndex] = "FR";
        }
      }
      
      
      int MAResultIndex = 19 - 1;
      
      if (!strings[MAResultIndex].equals("")) {
        if (strings[MAResultIndex].equals("1")) {
          strings[MAResultIndex] = "UR";
        } else {
          strings[MAResultIndex] = "FR";
        }
      }
      
      
      int SI_ResIndex = 22 - 1;
      
      if (!strings[SI_ResIndex].equals("")) {
        if (strings[SI_ResIndex].equals("1")) {
          strings[SI_ResIndex] = "UR";
        } else {
          strings[SI_ResIndex] = "FR";
        }
      }
      
      
      int MP1_IDIndex = 25 - 1;
      int MP1_TYPEIndex = 26 - 1;
      
      if (strings[MP1_IDIndex].equals("000000")) {
        strings[MP1_TYPEIndex] = "NO";
      }
      else if (strings[MP1_IDIndex].equals("990000")) {
        strings[MP1_TYPEIndex] = "N1";
      }
      else if (strings[MP1_IDIndex].equals("999999")) {
        strings[MP1_TYPEIndex] = "N2";
      }
      
      
      int MP2_IDIndex = 33 - 1;
      int MP2_TYPEIndex = 34 - 1;
      
      if (strings[MP2_IDIndex].equals("000000")) {
        strings[MP2_TYPEIndex] = "NO";
      }
      else if (strings[MP2_IDIndex].equals("990000")) {
        strings[MP2_TYPEIndex] = "N1";
      }
      else if (strings[MP2_IDIndex].equals("999999")) {
        strings[MP2_TYPEIndex] = "N2";
      }
      
      int PRIMIDIndex = 52 - 1;
      int PRIMTYPEIndex = 53 - 1;
      
      if (strings[PRIMIDIndex].equals("000000")) {
        strings[PRIMTYPEIndex] = "NO";
      }
      else if (strings[PRIMIDIndex].equals("990000")) {
        strings[PRIMTYPEIndex] = "N1";
      }
      else if (strings[PRIMIDIndex].equals("999999")) {
        strings[PRIMTYPEIndex] = "N2";
      }
      
      
      if (true) {
        for (int f = 0; f < NumColumns; f++) {
          
          if (f > 0)
            System.out.print((char) delimiterByte);
          
          System.out.print(strings[f]);
          
        }
        
        System.out.println();
      }
      }
    
    bufferedReader.close();
    
    
  }
  
}
