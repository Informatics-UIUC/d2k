package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class MergeHastableStream extends InputModule {
  
  private int ApplicationDepth = 1;
  public void setApplicationDepth(int value) {
    this.ApplicationDepth = value;
  }
  public int getApplicationDepth() {
    return this.ApplicationDepth;
  }
  
  public String getModuleName() {
    return "MergeHastableStream";
  }
  
  public String getModuleInfo() {
    return "MergeHastableStream";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ObjectStream";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ObjectStream";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.lang.Object" };
    return out;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ObjectStream";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ObjectStream";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "java.util.Hashtable" };
    return out;
  }
  
  boolean InitialExecution;
  
  Hashtable MergedHashtable = null;
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
        MergedHashtable = new Hashtable();
      }
      else
        this.pushOutput(object, 0);
      
      return;
    }
    
    if (streamEndMarkerClass.isInstance(object)) {
      
      if (NestingLevel == ApplicationDepth) {
        this.pushOutput(MergedHashtable, 0);
        NestingLevel--;
        return;
      }
      else
        this.pushOutput(object, 0);
    }
    
    if (NestingLevel == ApplicationDepth) {
      
      Hashtable hashtable = (Hashtable) object;
      
      Enumeration enumeration = hashtable.keys();
      
      while (enumeration.hasMoreElements()) {
        
        Object key = enumeration.nextElement();
        
        int [] ValueArray = ((int[]) MergedHashtable.get(key));
        
        if (ValueArray == null) {
          int [] NewValueArray = new int[2];
          NewValueArray[0] = uniqueValueCount;
          NewValueArray[1] = 1;
          
          MergedHashtable.put(key, NewValueArray);
          uniqueValueCount++;
        } else {
          ValueArray[1] = ValueArray[1] + 1;
        }
        
      }
      
    }
    
    return;
  }
  
}