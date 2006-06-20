package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class ApplyOneWayMapping extends InputModule {

  public String getModuleName() {
    return "ApplyOneWayMapping";
  }

  public String getModuleInfo() {
    return "ApplyOneWayMapping";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "OneWayMappingHashtable";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "OneWayMappingHashtable";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] out = { "[Ljava.lang.String;",  "java.util.Hashtable" };
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
    String[] out = { "[Ljava.lang.String;"};
    return out;
  }


  boolean InitialExecution;
  Hashtable OneWayMappingHashtable;

  public void beginExecution() {

    InitialExecution = true;
    OneWayMappingHashtable = null;

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
      OneWayMappingHashtable = (Hashtable) this.pullInput(1);
      return;
    }

    
    String[] StringArray = (String[]) this.pullInput(0);

    if (StringArray == null) {
      this.pushOutput(null, 0);
      beginExecution();
      return;
    }
    
    for (int i = 0; i < StringArray.length; i++) {

      String Key = StringArray[i];
      String NewKey = (String) OneWayMappingHashtable.get(Key);
      
      String Result = NewKey + "(" + Key +")";
      
      Result = Result.replaceAll("  ", "");
      
      StringArray[i] = Result;

    }

    this.pushOutput(StringArray, 0);

  }

}