package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CyclicObjectIntegratorTwoWay extends ComputeModule {
  
  public String getModuleInfo() {
    return "CyclicObjectIntegratorTwoWay";
  }
  public String getModuleName() {
    return "CyclicObjectIntegratorTwoWay";
  }
  
  public String[] getInputTypes() {
    String[] types = {"java.lang.Object", "java.lang.Object"};
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such input";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "No such output";
    }
  }
  
  int availableInputIndex;
  
  int lastInputIndexChecked;
  public void beginExecution() {
    lastInputIndexChecked = 0;
  }
  
  public boolean isReady() {
    
    if (this.getFlags()[lastInputIndexChecked] > 0) {
      
      availableInputIndex = lastInputIndexChecked;
      
      if (lastInputIndexChecked == 0)
        lastInputIndexChecked = 1;
      else
        lastInputIndexChecked = 0;
      
      return true;
      
    } else {
      
      return false;
      
    }
    
  }
  
  
  Object objectRead;
  public void readInputs() {
    objectRead = this.pullInput(availableInputIndex);
  }
  
  public void executeModule() {
    
    this.pushOutput(objectRead, 0);
    
  }
  
  public void doit()  {
    
    readInputs();
    
    executeModule();
    
  }
}