package ncsa.d2k.modules.core.prediction.mean.continuous;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class MeanBiasGenerator extends ComputeModule {


  public String getModuleInfo() {
    return "MeanBiasGenerator";
  }
  public String getModuleName() {
    return "MeanBiasGenerator";
  }

  public String getInputName(int i) {
    return "";
  }
  public String getInputInfo(int i) {
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Control Parameters";
      case 1: return "Function Inducer Class";
      default: return "Error!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameters";
      case 1: return "Function Inducer Class";
      default: return "Error!";
    }
  }
  public String[] getOutputTypes() {
    String [] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "java.lang.Class"
    };
    return out;
  }


  public void doit() throws Exception {

    int     numBiasDimensions = 0;
    double [] bias      = new double[numBiasDimensions];
    String [] biasNames = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.mean.continuous.MeanInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterPoint,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}