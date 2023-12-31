package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class CreateDecisionTreeParameterSpace extends ComputeModule {


  public String getModuleName() {
    return "Create Decision Tree Parameter Space";
  }

  public String getModuleInfo() {
    return "This creates a Parameter Space for an optimizer to search.  " +
           "It takes as input two parameter points, one representing the minimum allowable parameter values and one " +
           "representing the maximum allowable parameter values.  "  +
           "It outputs a Parameter Space for input into an optimizer and the Decision Tree Inducer Opt module for input " +
           "into Generate Function Inducer.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "Minimum Parameter Point";
      case 1: return "Maximum Parameter Point";
      default: return "";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The minimum allowable parameter values";
      case 1: return "The maximum allowable parameter values";
      default: return "";
    }
  }
  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Control Parameter Space";
      case 1: return "Function Inducer Class";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameter Space";
      case 1: return "Function Inducer Class";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
      "java.lang.Class"};
    return out;
  }



  public void doit() throws Exception {

    ParameterPoint minParameterPoint = (ParameterPoint)  this.pullInput(0);
    ParameterPoint maxParameterPoint = (ParameterPoint)  this.pullInput(1);

    int         numControlParameters = 9;
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];

    int biasIndex = 0;

    biasNames       [biasIndex] = "MinDecompositionPopulation";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames       [biasIndex] = "MinErrorReduction";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 1000000000;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    biasNames       [biasIndex] = "UseOneHalfSplit";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames       [biasIndex] = "UseMidPointBasedSplit";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames       [biasIndex] = "UseMeanBasedSplit";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames       [biasIndex] = "UsePopulationBasedSplit";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;

    biasIndex++;
    biasNames       [biasIndex] = "SaveNodeExamples";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;

    biasIndex++;
    biasNames       [biasIndex] = "UseMeanNodeModels";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames       [biasIndex] = "UseLinearNodeModels";
    minControlValues[biasIndex] = minParameterPoint.getValue(biasNames[biasIndex]);
    maxControlValues[biasIndex] = maxParameterPoint.getValue(biasNames[biasIndex]);
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    ParameterSpaceImpl parameterSpace = new ParameterSpaceImpl();
    parameterSpace.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
    }
    catch (Exception e) {
	// System.out.println("could not find class");
	// throw new Exception();
	throw new Exception(getAlias() + ": could not find class DecisionTreeInducerOpt "); 
    }

    this.pushOutput(parameterSpace,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}

//QA changes Anca: 
// Exception is now giving a message 
