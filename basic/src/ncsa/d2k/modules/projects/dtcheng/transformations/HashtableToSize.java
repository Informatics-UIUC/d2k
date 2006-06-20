package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class HashtableToSize extends InputModule {
  
  
  public String getModuleName() {
    return "HashtableToSize";
  }
  
  public String getModuleInfo() {
    return "HashtableToSize";
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
        return "Integer";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "java.lang.Integer"};
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
    
     this.pushOutput(new Integer(stringIndexCountHashtable.size()), 0);
    
  }
}