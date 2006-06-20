package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.InputModule;

public class CirculateObject extends InputModule {
  
  public String getModuleName() {
    return "CirculateObject";
  }
  
  public String getModuleInfo() {
    return "CirculateObject";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumObjectsToPass";
      case 1:
        return "ObjectToPass";
      default:
        return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {"java.lang.Integer", "java.lang.Object"};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumObjectsToPass";
      case 1:
        return "ObjectToPass";
      default:
        return "No such input";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ObjectPassed";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object"};
    return types;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ObjectPassed";
      default:
        return "No such output";
    }
  }
  
  int Count;
  int NumObjectsToPass;
  
  boolean InitialExecution;
  
  public void beginExecution() {
    InitialExecution = true;
    Count = 0;
    NumObjectsToPass = 0;
  }
  
  public boolean isReady() {
    
    if (InitialExecution && (this.getFlags()[0] > 0)) {
      return true;
    } else {
      if ((this.getFlags()[1] > 0) && (Count <= NumObjectsToPass)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean verbose = true;
  
  Object objectLastRead;
  
  public void endExecution() {
    
    System.out.println(Count);
    
  }
  
  public void readInputs() {
    
    if (InitialExecution) {
      InitialExecution = false;
      NumObjectsToPass = ((Integer) this.pullInput(0)).intValue();
    }
    
    objectLastRead = this.pullInput(1);
    
  }
  
  public void executeModule() {
    
    this.pushOutput(objectLastRead , 0);
    Count++;
    
  }
  
  
  // generic form for celerity compatibility
  
  public void doit()  {
    
    readInputs();
    
    if (Count < NumObjectsToPass) {
      executeModule();
    }
    
  }
  
  
}