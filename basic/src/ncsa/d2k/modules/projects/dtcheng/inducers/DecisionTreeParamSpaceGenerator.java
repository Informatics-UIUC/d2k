package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class DecisionTreeParamSpaceGenerator extends
 AbstractParamSpaceGenerator {
  
  
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Space";
      case 1:
        return "Function Inducer Class";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Control Parameter Space";
      case 1:
        return "Function Inducer Class";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "java.lang.Class" };
       return out;
  }
  
  int numControlParameters = 18;
  protected ParameterSpace getDefaultSpace() {
    
    double[] minControlValues = new double[numControlParameters];
    double[] maxControlValues = new double[numControlParameters];
    double[] defaults = new double[numControlParameters];
    int[] resolutions = new int[numControlParameters];
    int[] types = new int[numControlParameters];
    String[] biasNames = new String[numControlParameters];
    
    int biasIndex = 0;
    
    biasNames[biasIndex] = "MinDecompositionPopulation";
    minControlValues[biasIndex] = 100;
    maxControlValues[biasIndex] = 100;
    defaults[biasIndex] = 20;
    resolutions[biasIndex] = (int) maxControlValues[biasIndex]
     - (int) minControlValues[biasIndex] + 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames[biasIndex] = "MaxTreeDepth";
    minControlValues[biasIndex] = 999;
    maxControlValues[biasIndex] = 999;
    defaults[biasIndex] = 20;
    resolutions[biasIndex] = (int) maxControlValues[biasIndex]
     - (int) minControlValues[biasIndex] + 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames[biasIndex] = "MinErrorReduction";
    minControlValues[biasIndex] = -999999.0;
    maxControlValues[biasIndex] = -999999.0;
    defaults[biasIndex] = -999999.0;
    resolutions[biasIndex] = 1000000000;
    types[biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    biasNames[biasIndex] = "UseOneHalfSplit";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UseMidPointBasedSplit";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UseAllBetweenValueSplits";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UseMeanBasedSplit";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 1;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UsePopulationBasedSplit";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "SaveNodeExamples";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 1;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UseMeanNodeModels";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 1;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "UseLinearNodeModels";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "Direction";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames[biasIndex] = "NumberOfRounds";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames[biasIndex] = "NumHistRegions";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 20;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames[biasIndex] = "HistMinValue";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 0.0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    biasNames[biasIndex] = "HistMaxValue";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 1;
    defaults[biasIndex] = 0.0;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    biasNames[biasIndex] = "OutputModelIndices";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames[biasIndex] = "ReportOnlyLeafModelIndices";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 0;
    defaults[biasIndex] = 1;
    resolutions[biasIndex] = 1;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    ParameterSpace psi = new ParameterSpaceImpl();
    psi.createFromData(biasNames, minControlValues, maxControlValues,
     defaults, resolutions, types);
    return psi;
    
  }
  
  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Decision Tree Param Space Generator";
  }
  
  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numControlParameters];
    
    int i = 0;
    pds[i++] = new PropertyDescription("minDecompositionPopulation",
     "Minimum examples per leaf",
     "Prevents the generation of splits that result in leaf nodes with "
     + "less than the specified number of examples.  ");
    
    pds[i++] = new PropertyDescription("maxTreeDepth",
     "Maximum depth of tree",
     "Prevents the generation of splits that result in trees with "
     + "more than the specified number depth.  ");
    
    pds[i++] = new PropertyDescription(
     "minErrorReduction",
     "Minimum split error reduction",
     "The units of this error reduction are relative to the error function passed to the "
     + "decision tree inducer.  "
     + "A split will not occur unless the error is reduced by at least the amount specified.");
    
    pds[i++] = new PropertyDescription(
     "useOneHalfSplit",
     "Generate splits only at 1/2",
     "This works fine for boolean and continuous values.  "
     + "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");
    
    pds[i++] = new PropertyDescription(
     "useMeanBasedSplit",
     "Generate mean splits",
     "The mean of each attribute value is calculated in the given node and used to generate "
     + "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");
    
    pds[i++] = new PropertyDescription(
     "useMidPointBasedSplit",
     "Generate midpoint splits",
     "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  "
     + "The split occurs at the midpoint between the min and max values.  "
     + "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");
    
    pds[i++] = new PropertyDescription(
     "useAllBetweenValueSplits",
     "Generate all between value splits",
     "Generate all between value splits");
    
    pds[i++] = new PropertyDescription(
     "usePopulationBasedSplit",
     "Generate median splits",
     "The median of each attribute value is calculated in the given node and used to generate "
     + "splits for that node.  This requires sorting of all the examples in a node and therefore "
     + "scales at n log n in time complexity.  "
     + "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");
    
    pds[i++] = new PropertyDescription(
     "saveNodeExamples",
     "Save examples at each node",
     "In order to compute and print statistics of the node you must save the examples at the node "
     + "which increases the space and time complexity of the algorithm by a linear factor.  ");
    
    pds[i++] = new PropertyDescription(
     "useMeanNodeModels",
     "Use the mean averaging for models",
     "This results in a simple decision tree with constant functions at the leaves.  "
     + "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");
    
    pds[i++] = new PropertyDescription(
     "useLinearNodeModels",
     "Use multiple regression for models",
     "This results in a decision tree with linear functions of the input attributes at the leaves.  "
     + "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");
    
    pds[i++] = new PropertyDescription(
     "direction",
     "Direction of Search",
     "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");
    
    pds[i++] = new PropertyDescription(
     "numRounds",
     "Number of Feature Selection Rounds",
     "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");
    
    pds[i++] = new PropertyDescription(
     "histNumRegions",
     "Number of histogram regions",
     "Number of histogram regions");
    
    pds[i++] = new PropertyDescription(
     "histogramMinValue",
     "Histogram Min Value",
     "Histogram Min Value");
    
    pds[i++] = new PropertyDescription(
     "histogramMaxValue",
     "Histogram Max Value",
     "Histogram Max Value");
    
    pds[i++] = new PropertyDescription("outputModelIndices", "Output Model Indices",
     "This causes the model to output the indices of decision tree node models instead of output values.  "  +
     "The model methods getMaxDepth(), getNumSubModels(), getSubModels(), and getSubModels(index) may be called to evaluate the tree " +
     "in this mode.");
    
    pds[i++] = new PropertyDescription("reportOnlyLeafModelIndices", "Report Only Leaf Model Indices",
     "If ReportOnlyLeafModelIndices is true then only one output will be provided which is the index of the final leaf model.  "  +
     "If ReportOnlyLeafModelIndices is false then the first output will be the number of nodes on the decision path followed by " +
     "an output for each node on the decision path up to the maximum depth of the decision tree and padded with -1 for paths that " +
     "are not of maximal depth.  ");
    
    return pds;
  }
  
  /**
   * All we have to do here is push the parameter space and function inducer class.
   */
  public void doit() throws Exception {
    
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.inducers.DecisionTreeInducerOpt");
    } catch (Exception e) {
      //System.out.println("could not find class");
      //throw new Exception();
      throw new Exception(getAlias()
      + ": could not find class DecisionTreeInducerOpt ");
    }
    
    if (space == null)
      space = this.getDefaultSpace();
    this.pushOutput(space, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}