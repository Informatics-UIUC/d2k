package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;

public class DecisionTreeBiasGenerator
    extends ComputeModule {

  int numBiasDimensions = 9;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "minDecompositionPopulation",
        "Minimum examples per leaf",
        "Prevents the generation of splits that result in leaf nodes with " +
        "than the specified number of examples.  ");

    pds[1] = new PropertyDescription(
        "minErrorReduction",
        "Minimum split error reduction",
        "The units of this error reduction are relative to the error function passed to the " +
        "decision tree inducer.  ");

    pds[2] = new PropertyDescription(
        "useSimpleBooleanSplit",
        "Generate boolean attributes splits",
        "Since booleans are numericaly coded as 0.0 = false and 1.0 = true, it generates split " +
        "points at 0.5.  ");

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
  public void setMinDecompositionPopulation(int value) throws Exception {
    if (value < 1) {
      System.out.println("Error!  MinDecompositionPopulation < 1");
      throw new Exception();
    }
    this.MinDecompositionPopulation = value;
  }

  public int getMinDecompositionPopulation() {
    return this.MinDecompositionPopulation;
  }

  private double MinErrorReduction = 0.0;
  public void setMinErrorReduction(double value) throws Exception {
    /*
    if (value < 0.0) {
      System.out.println("Error!  MinErrorReduction < 0.0");
      throw new Exception();
    }
    */
    this.MinErrorReduction = value;
  }

  public double getMinErrorReduction() {
    return this.MinErrorReduction;
  }

  public boolean UseSimpleBooleanSplit = false;
  public void setUseSimpleBooleanSplit(boolean value) throws Exception {
    this.UseSimpleBooleanSplit = value;
  }

  public boolean getUseSimpleBooleanSplit() {
    return this.UseSimpleBooleanSplit;
  }

  public boolean UseMidPointBasedSplit = false;
  public void setUseMidPointBasedSplit(boolean value) throws Exception {
    this.UseMidPointBasedSplit = value;
  }

  public boolean getUseMidPointBasedSplit() {
    return this.UseMidPointBasedSplit;
  }

  public boolean UseMeanBasedSplit = false;
  public void setUseMeanBasedSplit(boolean value) throws Exception {
    this.UseMeanBasedSplit = value;
  }

  public boolean getUseMeanBasedSplit() {
    return this.UseMeanBasedSplit;
  }

  public boolean UsePopulationBasedSplit = false;
  public void setUsePopulationBasedSplit(boolean value) throws Exception {
    this.UsePopulationBasedSplit = value;
  }

  public boolean getUsePopulationBasedSplit() {
    return this.UsePopulationBasedSplit;
  }

  public boolean SaveNodeExamples = false;
  public void setSaveNodeExamples(boolean value) throws Exception {
    this.SaveNodeExamples = value;
  }

  public boolean getSaveNodeExamples() {
    return this.SaveNodeExamples;
  }

  public boolean UseMeanNodeModels = true;
  public void setUseMeanNodeModels(boolean value) throws Exception {
    this.UseMeanNodeModels = value;
    this.UseLinearNodeModels = !value;
  }

  public boolean getUseMeanNodeModels() {
    return this.UseMeanNodeModels;
  }

  public boolean UseLinearNodeModels = false;
  public void setUseLinearNodeModels(boolean value) throws Exception {
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
    s += "control the behavior of the DecisionTreeInducerOpt. ";
    s += "The first output is a ParameterPoint which can be used as input into a DecisionTreeInducerOpt. ";
    s += "The second output is the DecisionTreeInducerOpt class which can be used as input for ApplyFunctionInducer. ";
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
        return "A ParameterPoint which specifies the control settings of the Decision Tree Inducer";
      case 1:
        return "The DecisionTreeInducerOpt Class";
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
    biasNames[biasIndex] = "UseSimpleBooleanSplit";
    if (UseSimpleBooleanSplit)
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