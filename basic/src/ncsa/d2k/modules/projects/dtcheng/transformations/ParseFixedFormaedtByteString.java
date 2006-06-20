package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class ParseFixedFormaedtByteString extends ComputeModule {


  private String FieldLengthString = "1,1,1";

  public void setFieldLengthString(String value) {
    this.FieldLengthString = value;
  }

  public String getFieldLengthString() {
    return this.FieldLengthString;
  }

  public String getModuleName() {
    return "ParseFixedFormaedtByteString";
  }

  public String getModuleInfo() {
    return "This module parses a fixed format byte array representing a string into an array of strings.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ByteArray";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "A byte array representing the record.  ";
    default:
      return "No such output";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "[B" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "A string array representing the data fields.  ";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[S" };
    return types;
  }

  
  
  


  int[] FieldLengths = null;

  int TotalLength = 0;
  int NumFields = 0;

  public void beginExecution() {

    String[] FieldLengthStrings = Utility.parseCSVList(FieldLengthString);
    NumFields = FieldLengthStrings.length;
    FieldLengths = new int[NumFields];

    TotalLength = 0;
    for (int i = 0; i < NumFields; i++) {
      FieldLengths[i] = Integer.parseInt(FieldLengthStrings[i]);
      TotalLength += FieldLengths[i];
    }


  }

  
  
  
  
  public void doit() throws Exception {


    byte[] LineBytes = (byte[]) this.pullInput(0);
    

    if (LineBytes == null)  {  
      return;
    }
    
    if (LineBytes.length != TotalLength)  {
      System.out.println("LineBytes.length (" + LineBytes.length + ") != TotalLength (" + TotalLength + ")");
    }

     String[] string1DArray = new String[NumFields];
      int index = 0;
      for (int i = 0; i < NumFields; i++) {
        string1DArray[i] = new String(LineBytes, index, FieldLengths[i]);
        index += FieldLengths[i];
      }


    this.pushOutput(string1DArray, 0);

  }
}