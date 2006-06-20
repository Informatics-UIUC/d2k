package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.ComputeModule;

public class BurnCycles extends ComputeModule {

  public String getModuleInfo() {
    return "BurnCycles";
  }

  public String getModuleName() {
    return "BurnCycles";
  }
  public String[] getInputTypes()
  {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getInputName(int i)
  {
    switch(i) {
      case 0:
        return "Object";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }
  public String[] getOutputTypes()
  {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:
        return "Object";
      default: return "No such input";
    }
  }
  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }


  public void doit() {

    Object object = (Object) this.pullInput(0);
    
    
     long numRefs = 10000000000L;  
     int size = 100;
     int numTimes = (int) (numRefs / size);
    double data[] = new double[size];
    
    

    double sum = 0.0;
    for (int ii = 0; ii < numTimes; ii++) {
      for (int i = 0; i < size; i++) {
        sum += data[i];
      }

    }
    

    this.pushOutput(object, 0);
  }
}