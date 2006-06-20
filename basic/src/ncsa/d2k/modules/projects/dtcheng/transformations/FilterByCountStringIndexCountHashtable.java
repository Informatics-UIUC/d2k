package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class FilterByCountStringIndexCountHashtable extends InputModule {
  
  private int minimumCount = 1;

  public void setMinimumCount(int value) {
    this.minimumCount = value;
  }

  public int getMinimumCount() {
    return this.minimumCount;
  }
  
  public String getModuleName() {
    return "FilterByCountStringIndexCountHashtable";
  }
  
  public String getModuleInfo() {
    return "FilterByCountStringIndexCountHashtable";
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
    String[] out = { "java.util.Hashtable"};
    return out;
  }
  
  Class streamMarkerClass = (new StreamMarker()).getClass();
  public void doit() throws Exception {
    
    
    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
    Hashtable stringIndexCountHashtable = (Hashtable) object;
    Hashtable newStringIndexCountHashtable = new Hashtable();
    
    Enumeration enumeration = stringIndexCountHashtable.keys();
    
    int newNumUniqueValues = 0;
    while (enumeration.hasMoreElements()) {
      
      Object key = enumeration.nextElement();
      
      int [] indexAndCount = (int []) stringIndexCountHashtable.get(key);
      
      int count = indexAndCount[1];
      
      if (count >= minimumCount) {
        int [] newIndexAndCount = new int[2];
        newIndexAndCount[0] = newNumUniqueValues++;
        newIndexAndCount[1] = count;
        newStringIndexCountHashtable.put(key, newIndexAndCount);
      }
      
    }
    
     this.pushOutput(newStringIndexCountHashtable, 0);
    
  }
}