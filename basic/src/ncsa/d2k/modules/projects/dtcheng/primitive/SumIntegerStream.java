package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class SumIntegerStream
    extends ComputeModule {

  public String getModuleInfo() {
    return "SumIntegerStream";
  }

  public String getModuleName() {
    return "SumIntegerStream";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {};
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
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      default:
        return "No such output";
    }
  }

  
  
  
  
  
  
  boolean DoAdditionalWork = false;
  public void setDoAdditionalWork(boolean value) {
    this.DoAdditionalWork = value;
  }

  public boolean getDoAdditionalWork() {
    return DoAdditionalWork;
  }
  
  
  
  
  int Size = 10000;
  public void setSize(int Size) {
    this.Size = Size;
  }

  public int getSize() {
    return Size;
  }
  
  
  
  int Times = 100000;
  public void setTimes(int Times) {
    this.Times = Times;
  }

  public int getTimes() {
    return Times;
  }
  
  
  
  
  
  
  double sum = 0;
  public void beginExecution() {
    sum = 0;
  }

  public void endExecution() {
    System.out.println("sum = " + sum);
  }
  
  
  Object objectLastRead;
  public void readInputs() {
    objectLastRead = this.pullInput(0);
  }
  
  public void executeModule() {
    
    if (objectLastRead != null) {
    
    sum = sum + ((Integer) objectLastRead).doubleValue();
    
    
    
    if (DoAdditionalWork) {
      int size = Size;
      int times = Times;
      int [] data = new int[size];
      for (int i = 0; i < size; i++) {
        data[i]= i;
      }
      int workSum = 0;
      for (int h = 0; h < times; h++) {
        for (int i = 0; i < size; i++) {
          workSum += data[i];
        }
      }
    }

    
    
    }
  }
  
  
  // generic form for celerity compatibility
  
  public void doit()  {
    
    readInputs();
    
    executeModule();
    
  }
}