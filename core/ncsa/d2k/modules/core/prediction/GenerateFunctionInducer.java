package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.ComputeModule;

public class GenerateFunctionInducer extends ComputeModule {

  public String getModuleInfo() {
    return "GenerateFunctionInducer";
  }
  public String getModuleName() {
    return "GenerateFunctionInducer";
  }

  public String getInputName(int i) {
    switch(i) {
      case  0: return "FunctionInducerClass";
      case  1: return "Control Parameters";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "FunctionInducerClass";
      case  1: return "ParameterPoint";
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
      case  0: return "FunctionInducer";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case  0: return "FunctionInducer";
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

    if (InitialExecution) {
      value = (inputFlags[0] > 0) && (inputFlags[1] > 0);
    }
    else {
      value = (inputFlags[1] > 0);
    }
    return value;
  }

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
    }
    catch (Exception e) {
      System.out.println("could not create class");
      throw new Exception();
    }

    functionInducerOpt.instantiateBias(parameterPoint);

    this.pushOutput(functionInducerOpt, 0);
  }
}