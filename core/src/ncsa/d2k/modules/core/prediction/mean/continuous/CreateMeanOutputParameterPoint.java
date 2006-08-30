package ncsa.d2k.modules.core.prediction.mean.continuous;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class CreateMeanOutputParameterPoint
    extends ComputeModule {

  int numBiasDimensions = 0;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    return pds;
  }

  public String getModuleName() {
    return "Create Mean Output Parameter Point";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module allows the user to specify the parameter settings which ";
    s += "control the behavior of the Mean Output Inducer Opt module.  ";
    s += "There are no parameters to the Mean Output Inducer module at this time.  ";
    s += "";
    s += "</p>";
    return s;
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] in = {};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Point";
      case 1:
        return "Function Inducer Class";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A ParameterPoint which specifies the control settings of the Mean Output Inducer Opt Module";
      case 1:
        return "The Mean Output Inducer Opt Module for use in Apply Function Inducer";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "java.lang.Class"
    };
    return out;
  }

  public void doit() throws Exception {

    double[] bias = new double[numBiasDimensions];
    String[] biasNames = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.mean.continuous.MeanOutputInducerOpt");
    }
    catch (Exception e) {
	//System.out.println("could not find class");
      throw new Exception(getAlias() + ": could not find class MeanOutputInducerOpt ");
    }

    this.pushOutput(parameterPoint, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}

//QA changes Anca:
//typos and Exception is now giving a message
