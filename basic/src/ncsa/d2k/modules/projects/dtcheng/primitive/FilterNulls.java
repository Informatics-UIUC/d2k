package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class FilterNulls extends OrderedReentrantModule {
  
  public String getModuleInfo() {
    return "FilterNulls";
  }
  
  public String getModuleName() {
    return "FilterNulls";
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object1";
      default:
        return "No such output";
    }
  }
  
  public void doit() {
    Object object = (Object) this.pullInput(0);
    
    if (object != null)
      this.pushOutput(object, 0);
  }
}