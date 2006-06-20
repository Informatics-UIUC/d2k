package ncsa.d2k.modules.projects.dtcheng.inducers;

import java.beans.PropertyVetoException;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

public class DecisionTreeInducer extends DecisionTreeInducerOpt {
  
  public void setMinDecompositionPopulation(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.MinDecompositionPopulation = value;
  }

  public int getMinDecompositionPopulation() {
    return this.MinDecompositionPopulation;
  }

  public void setMaxTreeDepth(int value) throws PropertyVetoException {
    if (value < 0) {
      throw new PropertyVetoException(" < 0", null);
    }
    this.MaxTreeDepth = value;
  }

  public int getMaxTreeDepth() {
    return this.MaxTreeDepth;
  }

  public void setMinErrorReduction(double value) {
    this.MinErrorReduction = value;
  }

  public double getMinErrorReduction() {
    return this.MinErrorReduction;
  }

  public void setUseOneHalfSplit(boolean value) {
    this.UseOneHalfSplit = value;
  }

  public boolean getUseOneHalfSplit() {
    return this.UseOneHalfSplit;
  }

  public void setUseMidPointBasedSplit(boolean value) {
    this.UseMidPointBasedSplit = value;
  }
  public boolean getUseMidPointBasedSplit() {
    return this.UseMidPointBasedSplit;
  }
  
  public void setUseAllBetweenValueSplits(boolean value) {
    this.UseAllBetweenValueSplits = value;
  }
  public boolean getUseAllBetweenValueSplits() {
    return this.UseAllBetweenValueSplits;
  }


  public void setUseMeanBasedSplit(boolean value) {
    this.UseMeanBasedSplit = value;
  }

  public boolean getUseMeanBasedSplit() {
    return this.UseMeanBasedSplit;
  }

  public void setUsePopulationBasedSplit(boolean value) {
    this.UsePopulationBasedSplit = value;
  }

  public boolean getUsePopulationBasedSplit() {
    return this.UsePopulationBasedSplit;
  }

  public void setSaveNodeExamples(boolean value) {
    this.SaveNodeExamples = value;
  }

  public boolean getSaveNodeExamples() {
    return this.SaveNodeExamples;
  }

  public void setUseMeanNodeModels(boolean value) {
    this.UseMeanNodeModels = value;
    this.UseLinearNodeModels = !value;
  }

  public boolean getUseMeanNodeModels() {
    return this.UseMeanNodeModels;
  }

  public void setUseLinearNodeModels(boolean value) {
    this.UseLinearNodeModels = value;
    this.UseMeanNodeModels = !value;
  }

  public boolean getUseLinearNodeModels() {
    return this.UseLinearNodeModels;
  }

  public void setDirection(int value) throws PropertyVetoException {
    if (!((value == 1) || (value == -1))) {
      throw new PropertyVetoException("-1 or 1", null);
    }
    this.Direction = value;
  }

  public int getDirection() {
    return this.Direction;
  }

  public void setNumRounds(int value) throws PropertyVetoException {
    if (value < 0) {
      throw new PropertyVetoException(" > 0", null);
    }
    this.NumRounds = value;
  }
  public int getNumRounds() {
    return this.NumRounds;
  }

 public void setHistNumRegions(int value) throws PropertyVetoException {
    if (value < 0) {
      throw new PropertyVetoException(" > 0", null);
    }
    this.HistNumRegions = value;
  }
  public int getHistNumRegions() {
    return this.HistNumRegions;
  }


 public void setHistMinValue(double value) throws PropertyVetoException {
    if (value >= this.HistMaxValue) {
      throw new PropertyVetoException(" >= HistMaxValue", null);
    }
    this.HistMinValue = value;
  }
  public double getHistMinValue() {
    return this.HistMinValue;
  }

 public void setHistMaxValue(double value) throws PropertyVetoException {
    if (value <= this.HistMinValue) {
      throw new PropertyVetoException(" <= HistMinValue", null);
    }
    this.HistMaxValue = value;
  }
  public double getHistMaxValue() {
    return this.HistMaxValue;
  }

  public void setOutputModelIndices(boolean value) {
    this.OutputModelIndices = value;
  }
  public boolean getOutputModelIndices() {
    return this.OutputModelIndices;
  }


  public void setReportOnlyLeafModelIndices(boolean value) {
    this.ReportOnlyLeafModelIndices = value;
  }
   public boolean getReportOnlyLeafModelIndices() {
    return this.ReportOnlyLeafModelIndices;
  }

 
  public void setTrace(boolean value) {
    this._Trace = value;
  }

  public boolean getTrace() {
    return this._Trace;
  }

  
  
  int numBiasDimensions = 18;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    int i = 0;

    pds[i++] = new PropertyDescription("minDecompositionPopulation", "Minimum examples per leaf",
        "Prevents the generation of splits that result in leaf nodes with " + "less than the specified number of examples.  ");

    pds[i++] = new PropertyDescription("maxTreeDepth", "Maximum depth of tree",
        "Prevents the generation of splits that result in trees larger than maxTreeDepth.");

    pds[i++] = new PropertyDescription("minErrorReduction", "Minimum split error reduction",
     "The units of this error reduction are relative to the error function passed to the " + "decision tree inducer.  "
     + "A split will not occur unless the error is reduced by at least the amount specified.");
    
    pds[i++] = new PropertyDescription("useOneHalfSplit", "Generate splits only at 1/2", "This works fine for boolean and continuous values.  "
     + "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");
    
    pds[i++] = new PropertyDescription("useMidPointBasedSplit", "Generate midpoint splits",
     "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  "
     + "The split occurs at the midpoint between the min and max values.  "
     + "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");
    
    pds[i++] = new PropertyDescription(
     "useAllBetweenValueSplits",
     "Generate all between value splits",
     "Generate all between value splits");
     
     pds[i++] = new PropertyDescription("useMeanBasedSplit", "Generate mean splits",
      "The mean of each attribute value is calculated in the given node and used to generate "
      + "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");
     
     pds[i++] = new PropertyDescription("usePopulationBasedSplit", "Generate median splits",
        "The median of each attribute value is calculated in the given node and used to generate "
            + "splits for that node.  This requires sorting of all the examples in a node and therefore " + "scales at n log n in time complexity.  "
            + "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[i++] = new PropertyDescription("saveNodeExamples", "Save examples at each node",
        "In order to compute and print statistics of the node you must save the examples at the node "
            + "which increases the space and time complexity of the algorithm by a linear factor.  ");

    pds[i++] = new PropertyDescription("useMeanNodeModels", "Use the mean averaging for models",
        "This results in a simple decision tree with constant functions at the leaves.  "
            + "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

    pds[i++] = new PropertyDescription("useLinearNodeModels", "Use multiple regression for models",
        "This results in a decision tree with linear functions of the input attributes at the leaves.  "
            + "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

    pds[i++] = new PropertyDescription("direction", "Direction of Search",
        "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");

    pds[i++] = new PropertyDescription("numRounds", "Number of Feature Selection Rounds",
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

  public String getModuleName() {
    return "Decision Tree Inducer";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Example Table";
    case 1:
      return "Error Function";
    default:
      return "Error!  No such input.";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Example Table";
    case 1:
      return "Error Function";
    default:
      return "Error!  No such input.";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "ncsa.d2k.modules.core.datatype.table.ExampleTable", "ncsa.d2k.modules.core.prediction.ErrorFunction" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Model";
    default:
      return "Error!  No such output.";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Model";
    default:
      return "Error!  No such output.";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "ncsa.d2k.modules.core.datatype.model.Model" };
    return types;
  }

  public void beginExecution() {
  }

  

  public void doit() throws Exception {

    Object InputObject1 = this.pullInput(0);
    Object InputObject2 = this.pullInput(1);

    if ((InputObject1 == null) || (InputObject2 == null)) {
      this.pushOutput(null, 0);
      return;
    }

    ExampleTable exampleSet = (ExampleTable) InputObject1;
    ErrorFunction errorFunction = (ErrorFunction) InputObject2;
    
    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

}