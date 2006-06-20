package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class HashtableToFeatureVector extends InputModule {
  
  
  public String getModuleName() {
    return "HashtableToFeatureVector";
  }
  
  public String getModuleInfo() {
    return "HashtableToFeatureVector";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MasterHashtable";
      case 1:
        return "Hashtable";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "MasterHashtable";
      case 1:
        return "Hashtable";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.util.Hashtable", "java.util.Hashtable" };
    return out;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "FeatureVector";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "FeatureVector";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "[D"};
    return out;
  }
  
  Class streamMarkerClass = (new StreamMarker()).getClass();
  Class hashtable = (new Hashtable()).getClass();
  public void doit() throws Exception {
    
    
    Object object1 = this.pullInput(0);
    Object object2 = this.pullInput(1);
    
    if (streamMarkerClass.isInstance(object2)) {
      this.pushOutput(object2, 0);
      return;
    }
    
    if (hashtable.isInstance(object2)) {
      
      Hashtable masterHashtable = (Hashtable) object1;
      Hashtable hashtable = (Hashtable) object2;
      
      int numFeatures = masterHashtable.size();
      
      double [] featureVector = new double[numFeatures];
      
      Enumeration enumeration = hashtable.keys();
      
      while (enumeration.hasMoreElements()) {
        
        Object key = enumeration.nextElement();
        
        int [] masterIndexAndCount = (int []) masterHashtable.get(key);
        int [] indexAndCount = (int []) hashtable.get(key);
        
        if (masterIndexAndCount != null) {
          
          int index = masterIndexAndCount[0];
          int count = indexAndCount[1];
          
          featureVector[index] = count;
        }
        
      }
      this.pushOutput(featureVector, 0);
    } else {
      this.pushOutput(object2, 0);
    }
    
    
  }
}