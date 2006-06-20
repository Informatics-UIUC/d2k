package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.InputModule;

public class GenerateIndexStream extends InputModule {

  private boolean Stop = false;

  public void setStop(boolean value) {
    this.Stop = value;
  }

  public boolean getStop() {
    return this.Stop;
  }


  public String getModuleName() {
    return "GenerateIndexStream";
  }

  public String getModuleInfo() {
    return "GenerateIndexStream";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "NumToGenerate";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.Integer"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "NumToGenerate";
    default:
      return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Index";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.Integer", };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Index";
    default:
      return "No such output";
    }
  }

  int Count;
  int NumToGenerate;

  boolean InitialExecution;

  public void beginExecution() {
    InitialExecution = true;
    Count = 0;
    NumToGenerate = 0;
  }

  public boolean isReady() {

    if (InitialExecution) {
      return true;
    } else {
      if (Count < NumToGenerate) {
        return true;
      }
    }
    return false;
  }

  
  public void readInputs() {
    
    if (InitialExecution) {
      NumToGenerate = ((Integer) this.pullInput(0)).intValue();
    }
  }
  
  public void executeModule() {
    
    InitialExecution = false;

    this.pushOutput(new Integer(Count), 0);
    //this.pushOutput(StaticCount , 0);

    if (Stop) {
      Count = Integer.MAX_VALUE;
      return;
    }

    Count++;
    
    if (Count == NumToGenerate) {

      this.pushOutput(null, 0);
      
    }

  }
  
  
  // generic form for celerity compatibility
  public void doit()  {
    readInputs();
    executeModule();
  }
}