package ncsa.d2k.modules.core.prediction.instancebased;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class CreateInstanceBasedParameterPoint
    extends ComputeModule {

  int numBiasDimensions = 3;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "neighborhoodSize",
        "Neighborhood Size",
        "The number of examples to use for fitting the prediction module.  " +
        "This must be set to 1 or greater.  ");

    pds[1] = new PropertyDescription(
        "distanceWeightingPower",
        "Distance Weighting Power",
        "The value of the power term in the inverse distance weighting formula.  " +
        "Setting this to zero causes equal weighting of all examples.  " +
        "Setting it two 1.0 gives inverse distance weighting.  " +
        "Setting it two 2.0 gives inverse distance squared weighting and so on.  ");

    pds[2] = new PropertyDescription(
        "zeroDistanceValue",
        "Zero Distance Value",
        "What weight to associate to a stored example which has zero distance to example to be predicted.  " +
        "Since division by zero is not permitted, some value must be assigned to examples with zero distance.  "  +
        "This value is the weight and exact match should be given.  ");
    return pds;
  }

  private int NeighborhoodSize = 20;
  public void setNeighborhoodSize(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.NeighborhoodSize = value;
  }

  public int getNeighborhoodSize() {
    return this.NeighborhoodSize;
  }

  private double DistanceWeightingPower = 0.0;
  public void setDistanceWeightingPower(double value) {
    this.DistanceWeightingPower = value;
  }

  public double getDistanceWeightingPower() {
    return this.DistanceWeightingPower;
  }

  private double ZeroDistanceValue = 0.0;
  public void setZeroDistanceValue(double value) throws PropertyVetoException {
    if (value < 0.0) {
      throw new PropertyVetoException(" < 0.0", null);
    }
    this.ZeroDistanceValue = value;
  }

  public double getZeroDistanceValue() {
    return this.ZeroDistanceValue;
  }


  public String getModuleName() {
    return "Create Instance Based Parameter Point";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module allows the user to specify the parameter settings which ";
    s += "control the behavior of the Instance Based Inducer Opt module.  ";
    s += "The first output is a Parameter Point which can be used as input into a Instance Based Inducer Opt module.  ";
    s += "The second output is the Instance Based Inducer Opt Module which can be used as input for Apply Function Inducer module.  ";
    s += "This instance based learning algorithm implements n-nearest neighbor with variable weights for each stored example.  ";
    s += "The weight of each example set is determined by the inverse weighting formula.  ";
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
        return "Function Inducer Module";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A ParameterPoint which specifies the control settings of the Instance Based Inducer Opt Module";
      case 1:
        return "The Instance Based Inducer Opt module for use in Apply Function Inducer";
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

    biasNames[biasIndex] = "NeighborhoodSize";
    bias[biasIndex] = NeighborhoodSize;
    biasIndex++;
    biasNames[biasIndex] = "DistanceWeightingPower";
    bias[biasIndex] = DistanceWeightingPower;
    biasIndex++;
    biasNames[biasIndex] = "ZeroDistanceValue";
    bias[biasIndex] = ZeroDistanceValue;
    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.instancebased.InstanceBasedInducerOpt");
    }
    catch (Exception e) {
	//System.out.println("could not find class");
	throw new Exception(getAlias() + ": could not find class InstanceBasedInducerOpt");
	//throw new Exception();
    }

    this.pushOutput(parameterPoint, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}

//QA changes Anca: 
//typos and Exception is now giving a message 
//replaced Decision Tree with Instance Based in getOutputInfo


