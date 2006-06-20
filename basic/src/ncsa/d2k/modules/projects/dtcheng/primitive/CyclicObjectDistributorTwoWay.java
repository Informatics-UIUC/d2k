package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CyclicObjectDistributorTwoWay extends ComputeModule {
  
  public String getModuleInfo() {
    return "CyclicObjectDistributorTwoWay";
  }
  public String getModuleName() {
    return "CyclicObjectDistributorTwoWay";
  }
  
  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object", "java.lang.Object"};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "No such input";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such output";
    }
  }
  
  int availableInputIndex;
  
  int nextOutputIndexToWrite;
  public void beginExecution() {
    nextOutputIndexToWrite = 0;
  }
  
  Object objectRead;
  public void readInputs() {
    objectRead = this.pullInput(0);
  }
  
  
  public boolean modulePredictsOutputBehavior() {
    return true;
  }
  
  int [] outputBehavior = new int[2]; 
  
  public int[] predictOutputBehavior() {
    
    if (nextOutputIndexToWrite == 0) {
      outputBehavior[0] = 1;
      outputBehavior[1] = 0;
    }
    else {
      outputBehavior[0] = 0;
      outputBehavior[1] = 1;
    }
    
    return outputBehavior;
  }
  
  public void executeModule() {
    
    this.pushOutput(objectRead, nextOutputIndexToWrite);
    
    if (nextOutputIndexToWrite == 0)
      nextOutputIndexToWrite = 1;
    else
      nextOutputIndexToWrite = 0;
    
  }
  
  public void doit()  {
    
    readInputs();
    
    executeModule();
    
  }
}