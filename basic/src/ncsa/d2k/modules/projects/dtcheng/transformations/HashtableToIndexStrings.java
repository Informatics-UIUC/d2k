package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class HashtableToIndexStrings extends InputModule {
  
  
  public String getModuleName() {
    return "HashtableToIndexStrings";
  }
  
  public String getModuleInfo() {
    return "HashtableToIndexStrings";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Hashtable";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Hashtable";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.util.Hashtable" };
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
  
  Class streamMarkerClass = (new StreamMarker()).getClass();
  
  public void doit() throws Exception {
    
    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
    Hashtable hashtable = (Hashtable) object;
    
    int numInputs = hashtable.size();
    
    String [] indexStrings = new String[numInputs];
    
    Enumeration enumeration = hashtable.keys();
    
    while (enumeration.hasMoreElements()) {
      
      Object key = enumeration.nextElement();
      
      int [] indexAndCount = (int []) hashtable.get(key);
      
      int index = indexAndCount[0];
      int count = indexAndCount[1];
      
      indexStrings[index] = (String) key;
      
      
    }
    
    this.pushOutput(indexStrings, 0);
    
  }
}