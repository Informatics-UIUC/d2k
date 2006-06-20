package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;


public class PassObject extends ComputeModule {
  
  public String getModuleInfo() {
    return "PassObject";
  }
  
  public String getModuleName() {
    return "PassObject";
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
        return "NO SUCH INPUT!";
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
  
  Object object;
  public void readInputs() {
    object = (Object) this.pullInput(0);
  }
  
  public void executeModule() {
    this.pushOutput(object, 0);
  }
  
  // generic form for celerity compatibility
  public void doit() throws Exception {
    readInputs();
    executeModule();
  }
  
  
}