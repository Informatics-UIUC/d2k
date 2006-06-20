package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class FilterByNominalCounts extends InputModule {

  private int FeatureNumber = 1;

  public void setFeatureNumber(int value) {
    this.FeatureNumber = value;
  }

  public int getFeatureNumber() {
    return this.FeatureNumber;
  }

  private int MinimumCount = 10;

  public void setMinimumCount(int value) {
    this.MinimumCount = value;
  }

  public int getMinimumCount() {
    return this.MinimumCount;
  }

  public String getModuleName() {
    return "FilterByNominalCounts";
  }

  public String getModuleInfo() {
    return "FilterByNominalCounts";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "NominalStrings";
    case 2:
      return "NominalCounts";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "NominalCountHashtables";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] out = { "[Ljava.lang.String;", "[Ljava.util.Hashtable;" };
    return out;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = { "[Ljava.lang.String;" };
    return out;
  }

  boolean InitialExecution = true;
  Hashtable[] NominalIndexCountHashtable = null;

   public void beginExecution() {

    InitialExecution = true;
    NominalIndexCountHashtable = null;

  }


  public boolean isReady() {

    boolean value = false;

    if (InitialExecution && (this.getFlags()[1] > 0)) {
      value = true;
    } else {
      if (!InitialExecution && (this.getFlags()[0] > 0)) {
        value = true;
      }
    }
    return value;
  }
  
 

  public void doit() throws Exception {

    if (InitialExecution) {
      InitialExecution = false;
      NominalIndexCountHashtable = (Hashtable[]) this.pullInput(1);
      return;
    }
    
    String[] StringArray = (String[]) this.pullInput(0);

    if (StringArray == null) {
      this.pushOutput(null, 0);
      beginExecution();
      return;
    }
    
    int NumColumns = StringArray.length;


    int index = FeatureNumber - 1;
    String Name = StringArray[index];

    int[] ValueArray = (int[]) NominalIndexCountHashtable[index].get(Name);

    if (ValueArray == null) {
      System.out.println("Error!  Nominal name not found in hastable");
    } else {
      int count = ValueArray[1];
      if (count >= MinimumCount) {
        this.pushOutput(StringArray, 0);
      }
    }

  }

}