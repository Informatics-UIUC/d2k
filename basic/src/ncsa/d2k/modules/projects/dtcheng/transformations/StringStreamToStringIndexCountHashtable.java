package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class StringStreamToStringIndexCountHashtable extends InputModule {
  
  private int ApplicationDepth = 1;
  public void setApplicationDepth(int value) {
    this.ApplicationDepth = value;
  }
  public int getApplicationDepth() {
    return this.ApplicationDepth;
  }
  
  public String getModuleName() {
    return "StringStreamToStringIndexCountHashtable";
  }
  
  public String getModuleInfo() {
    return "StringStreamToStringIndexCountHashtable";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "String";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "String";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.lang.String" };
    return out;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Hashtable";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Hashtable: Keyed by string and returns int[2] {index, count}";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "java.util.Hashtable" };
    return out;
  }
  
  boolean InitialExecution;
  
  int OperatorIndex;
  final int MathSUMOperterIndex = 0;
  final int LogicOROperterIndex = 1;
  Hashtable NominalIndexCountHashtable = null;
  int uniqueValueCount;
  int NestingLevel;
  public void beginExecution() {
    NestingLevel = 0;    
  }
  
  
  Class streamStartMarkerClass = (new StreamStartMarker()).getClass();
  Class streamEndMarkerClass = (new StreamEndMarker()).getClass();
  
  public void doit() throws Exception {
    
    Object object = this.pullInput(0);
  
    if (streamStartMarkerClass.isInstance(object)) {
      
      NestingLevel++;
      
      if (NestingLevel == ApplicationDepth) {
        uniqueValueCount = 0;
        NominalIndexCountHashtable = new Hashtable();
      } else {
        this.pushOutput(object, 0);
      }
      return;
    }
    
    if (streamEndMarkerClass.isInstance(object)) {
      
      if (NestingLevel == ApplicationDepth) {
        this.pushOutput(NominalIndexCountHashtable, 0);
      } else {
        this.pushOutput(object, 0);
      }
      
      NestingLevel--;
      
      return;
    }
    
    
    if (NestingLevel == ApplicationDepth) {
      
      String string = (String) object;
      
      int [] ValueArray = ((int[]) NominalIndexCountHashtable.get(string));
      
      if (ValueArray == null) {
        int [] NewValueArray = new int[2];
        NewValueArray[0] = uniqueValueCount;
        NewValueArray[1] = 1;
        
        NominalIndexCountHashtable.put(string, NewValueArray);
        uniqueValueCount++;
      } else {
        ValueArray[1] = ValueArray[1] + 1;
      }
    }
    else {
        this.pushOutput(object, 0);
    }
    
  }
}