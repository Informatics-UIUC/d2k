package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class CreateDecisionTreeParameterPoint
    extends ComputeModule {

  int numBiasDimensions = 9;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "minDecompositionPopulation",
        "Minimum examples per leaf",
        "Prevents the generation of splits that result in leaf nodes with " +
        "less than the specified number of examples.  ");

    pds[1] = new PropertyDescription(
        "minErrorReduction",
        "Minimum split error reduction",
        "The units of this error reduction are relative to the error function passed to the " +
        "decision tree inducer.  " +
        "A split will not occur unless the error is reduced by at least the amount specified.");

    pds[2] = new PropertyDescription(
        "useOneHalfSplit",
        "Generate splits only at 1/2",
        "This works fine for boolean values and continuous ones.  " +
        "If used as the sole decomposition strategy, it forces thes system to only split on a variable once.  ");

    pds[4] = new PropertyDescription(
        "useMeanBasedSplit",
        "Generate mean splits",
        "The mean of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[3] = new PropertyDescription(
        "useMidPointBasedSplit",
        "Generate midpoint splits",
        "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  " +
        "The split occurs at the midpoint between the min and max values.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[5] = new PropertyDescription(
        "usePopulationBasedSplit",
        "Generate median splits",
        "The median of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  This requires sorting of all the examples in a node and therefore " +
        "scales at n log n in time complexity.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[6] = new PropertyDescription(
        "saveNodeExamples",
        "Save examples at each node",
        "In order to compute and print statistics of the node you must save the examples at the node " +
        "which increases the space and time complexity of the algorithm by a linear factor.  ");

    pds[7] = new PropertyDescription(
        "useMeanNodeModels",
        "Use the mean averaging for models",
        "This results in a simple decision tree with constant functions at the leaves.  " +
        "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

    pds[8] = new PropertyDescription(
        "useLinearNodeModels",
        "Use multiple regression for models",
        "This results in a decision tree with linear functions of the input attributes at the leaves.  " +
        "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

    return pds;
  }

  private int MinDecompositionPopulation = 20;
  public void setMinDecompositionPopulation(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException("Minimum examples per leaf < 1", null);
    }
    this.MinDecompositionPopulation = value;
  }

  public int getMinDecompositionPopulation() {
    return this.MinDecompositionPopulation;
  }

  private double MinErrorReduction = 0.0;
  public void setMinErrorReduction(double value) {
    this.MinErrorReduction = value;
  }

  public double getMinErrorReduction() {
    return this.MinErrorReduction;
  }

  public boolean UseOneHalfSplit = false;
  public void setUseOneHalfSplit(boolean value) throws PropertyVetoException {
    this.UseOneHalfSplit = value;
  }

  public boolean getUseOneHalfSplit() {
    return this.UseOneHalfSplit;
  }

  public boolean UseMidPointBasedSplit = false;
  public void setUseMidPointBasedSplit(boolean value) {
    this.UseMidPointBasedSplit = value;
  }

  public boolean getUseMidPointBasedSplit() {
    return this.UseMidPointBasedSplit;
  }

  public boolean UseMeanBasedSplit = true;
  public void setUseMeanBasedSplit(boolean value) {
    this.UseMeanBasedSplit = value;
  }

  public boolean getUseMeanBasedSplit() {
    return this.UseMeanBasedSplit;
  }

  public boolean UsePopulationBasedSplit = false;
  public void setUsePopulationBasedSplit(boolean value) {
    this.UsePopulationBasedSplit = value;
  }

  public boolean getUsePopulationBasedSplit() {
    return this.UsePopulationBasedSplit;
  }

  public boolean SaveNodeExamples = false;
  public void setSaveNodeExamples(boolean value) {
    this.SaveNodeExamples = value;
  }

  public boolean getSaveNodeExamples() {
    return this.SaveNodeExamples;
  }

  public boolean UseMeanNodeModels = true;
  public void setUseMeanNodeModels(boolean value) {
    this.UseMeanNodeModels = value;
    this.UseLinearNodeModels = !value;
  }

  public boolean getUseMeanNodeModels() {
    return this.UseMeanNodeModels;
  }

  public boolean UseLinearNodeModels = false;
  public void setUseLinearNodeModels(boolean value) {
    this.UseLinearNodeModels = value;
    this.UseMeanNodeModels = !value;
  }

  public boolean getUseLinearNodeModels() {
    return this.UseLinearNodeModels;
  }

  public String getModuleName() {
    return "Create Decision Tree Parameter Point";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module allows the user to specify the parameter settings which ";
    s += "control the behavior of the Decision Tree Inducer Opt Module.  ";
    s += "The first output is a Parameter Point which can be used as input into a Decision Tree Inducer Opt module.  ";
    s += "The second output is the Decision Tree Inducer Opt Module which can be used as input for Apply Function Inducer module.  ";
    s += "This decision tree learning algorithm allows for multiple decomposition (splitting) strategies to be used ";
    s += "simultaneously.  It also allows for an exclusive choice between using mean averaging at the leaves or linear functions ";
    s += "created by multiple regression.  ";
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
        return "A ParameterPoint which specifies the control settings of the Decision Tree Inducer Opt Module";
      case 1:
        return "The Decision Tree Inducer Opt Module for use in Apply Function Inducer";
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

    biasNames[biasIndex] = "MinDecompositionPopulation";
    bias[biasIndex] = MinDecompositionPopulation;
    biasIndex++;
    biasNames[biasIndex] = "MinErrorReduction";
    bias[biasIndex] = MinErrorReduction;
    biasIndex++;
    biasNames[biasIndex] = "UseOneHalfSplit";
    if (UseOneHalfSplit)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;
    biasIndex++;
    biasNames[biasIndex] = "UseMidPointBasedSplit";
    if (UseMidPointBasedSplit)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;
    biasNames[biasIndex] = "UseMeanBasedSplit";
    if (UseMeanBasedSplit)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;
    biasNames[biasIndex] = "UsePopulationBasedSplit";
    if (UsePopulationBasedSplit)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;
    biasNames[biasIndex] = "SaveNodeExamples";
    if (SaveNodeExamples)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;
    biasNames[biasIndex] = "UseMeanNodeModels";
    if (UseMeanNodeModels)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;
    biasNames[biasIndex] = "UseLinearNodeModels";
    if (UseLinearNodeModels)
      bias[biasIndex] = 1.0;
    else
      bias[biasIndex] = 0.0;

    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterPoint, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}