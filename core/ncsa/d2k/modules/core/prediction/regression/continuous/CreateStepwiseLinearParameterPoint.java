package ncsa.d2k.modules.core.prediction.regression.continuous;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class CreateStepwiseLinearParameterPoint
    extends ComputeModule {

  int numBiasDimensions = 3;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "useStepwise",
        "Use Stepwise",
        "When true, a stepwise regression procedure is followed, otherwise normal regression is used on all features.");

    pds[1] = new PropertyDescription(
        "direction",
        "Direction of Search",
        "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");

    pds[2] = new PropertyDescription(
        "numRounds",
        "Number of Feature Selection Rounds",
        "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");


    return pds;
  }

  private boolean UseStepwise = false;
  public void setUseStepwise(boolean value) throws PropertyVetoException {
    this.UseStepwise = value;
  }
  public boolean getUseStepwise() {
    return this.UseStepwise;
  }

  private int Direction = 1;
  public void setDirection(int value) throws PropertyVetoException {
    if (!((value == -1) || (value == 1))) {
      throw new PropertyVetoException(" must be -1 or 1", null);
    }
    this.Direction = value;
  }
  public int getDirection() {
    return this.Direction;
  }

  private int NumRounds = 1;
  public void setNumRounds(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.NumRounds = value;
  }
  public int getNumRounds() {
    return this.NumRounds;
  }


  public String getModuleName() {
    return "Create Stepwise Linear Parameter Point";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module allows the user to specify the parameter settings which ";
    s += "control the behavior of the Stepwise Linear Inducer Opt module.  ";
    s += "The first output is a Parameter Point which can be used as input into a Stepwise Linear Inducer Opt module.  ";
    s += "The second output is the Stepwise Linear Inducer Opt Module which can be used as input for Apply Function Inducer module.  ";
    s += "This Stepwise Linear learning algorithm allows for both standard multiple regression using all the features ";
    s += "or stepwise regression, either step up or step down.  Step up regression, is a feature subset selection method that ";
    s += "addes features one at a time starting with no features.  Step down regression, which is slower, starts with all the ";
    s += "features and removes them one at a time.  ";
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
        return "A ParameterPoint which specifies the control settings of the Stepwise Linear Inducer Opt Module";
      case 1:
        return "The Stepwise Linear Inducer Opt Module for use in Apply Function Inducer";
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

    biasNames[biasIndex] = "UseStepwise";
    if (UseStepwise)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;
    biasIndex++;
    biasNames[biasIndex] = "Direction";
    bias[biasIndex] = Direction;
    biasIndex++;
    biasNames[biasIndex] = "NumRounds";
    bias[biasIndex] = NumRounds;
    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.regression.continuous.StepwiseLinearInducerOpt");
    }
    catch (Exception e) {
	//  System.out.println("could not find class");
	//throw new Exception();
	throw new Exception(getAlias() + ": could not find class StepwiseLinearInducerOpt "); 
    }

    this.pushOutput(parameterPoint, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}

//QA changes Anca: 
//typos and Exception is now giving a message 
