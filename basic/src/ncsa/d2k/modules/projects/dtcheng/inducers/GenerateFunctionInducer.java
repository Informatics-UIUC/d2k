package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.ComputeModule;

public class GenerateFunctionInducer extends ComputeModule {
  
  public String getModuleName() {
    return "Generate Function Inducer";
  }
  public String getModuleInfo() {
    return "This module is used in the optimization of induction modules.  "  +
     "It role is to translate a point in control space into a fully specified learning algorithm" +
     "with all control paramters specifed which can be applied to an example set to yeild a model.  " +
     "It takes as input (1) an function inducer module class to be optimized "+
     "and (2) points in control parameter space from the optimizer.  " +
     "It begins by reading the module clase once.  "+
     "Then it pulls control paramter points, one at a time, and creates an fully instantiated " +
     "function inducer ready for application.  ";
  }
  
  public String getInputName(int i) {
    switch(i) {
      case  0: return "Function Inducer Opt Module";
      case  1: return "Control Parameter Point";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "A class that defines a optimizable module";
      case  1: return "A point in control parameter space for defining the module behavior";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "java.lang.Class",
       "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
    };
    return types;
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Function Inducer";
      default: return "No such output";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case  0: return "A function inducer with its control parameters set";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.FunctionInducerOpt"};
    return types;
  }
  
  boolean InitialExecution = true;
  public void beginExecution() {
    InitialExecution = true;
  }
  
  
  public boolean isReady() {
    
    boolean value = false;
    
    // first time, require both inputs, thereafter only input #2
    if (InitialExecution) {
      value = (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0);
    } else {
      value = (this.getFlags()[1] > 0);
    }
    return value;
  }
  
  // state memory
  Class functionInducerClass = null;
  
  
  public void doit() throws Exception {
    
    if (InitialExecution) {
      functionInducerClass = (Class) this.pullInput(0);
      InitialExecution = false;
    }
    
    ParameterPoint parameterPoint = (ParameterPoint) this.pullInput(1);
    
    FunctionInducerOpt functionInducerOpt = null;
    
    try {
      functionInducerOpt = (FunctionInducerOpt) functionInducerClass.newInstance();
    } catch (Exception e) {
      System.out.println("could not create class");
      throw new Exception();
    }
    
    functionInducerOpt.setControlParameters(parameterPoint);
    
    this.pushOutput(functionInducerOpt, 0);
  }
}