package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class InstanceBasedInducer extends InstanceBasedInducerOpt {
  
  
  int numBiasDimensions = 8;
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];
    
    int i = 0;
    
    pds[i++] = new PropertyDescription(
     "neighborhoodSize",
     "Neighborhood Size",
     "The number of examples to use for fitting the prediction module.  " +
     "This must be set to 1 or greater.  ");
    
    pds[i++] = new PropertyDescription(
     "distanceWeightingPower",
     "Distance Weighting Power",
     "The value of the power term in the inverse distance weighting formula.  " +
     "Setting this to zero causes equal weighting of all examples.  " +
     "Setting it to 1.0 gives inverse distance weighting.  " +
     "Setting it to 2.0 gives inverse distance squared weighting and so on.  ");
    
    pds[i++] = new PropertyDescription(
     "zeroDistanceValue",
     "Zero Distance Value",
     "What weight to associate to a stored example which has zero distance to example to be predicted.  " +
     "Since division by zero is not permitted, some value must be assigned to examples with zero distance.  "  +
     "This value is the weight and exact match should be given.  ");
    
    
    pds[i++] = new PropertyDescription(
     "useStepwiseRegression",
     "Use stepwise regression for models",
     "This results in a locally linear model (with equal weighting of each example).  ");
    
    pds[i++] = new PropertyDescription(
     "direction",
     "Direction of Search",
     "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");
    
    pds[i++] = new PropertyDescription(
     "numRounds",
     "Number of Feature Selection Rounds",
     "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");
    
    
    pds[i++] = new PropertyDescription(
     "expandNeighborhoodToIncludeTies",
     "expand neighborhood to include ties",
     "This adds variability to the neighborhood size because it can increase with respect to size and "  +
     "will eliminate example set order effects and reduce model prediction variability.  ");
    
    
    pds[i++] = new PropertyDescription(
     "ExpansionFactor",
     "expansion factor",
     "This controls how much larger the neighborhood can expand to include ties.  ");
    
    
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
  
  private boolean UseStepwiseRegression = false;
  public void setUseStepwiseRegression(boolean value) {
    this.UseStepwiseRegression = value;
  }
  public boolean getUseStepwiseRegression() {
    return this.UseStepwiseRegression;
  }
  

  private int Direction = -1;
  public void setDirection(int value) throws PropertyVetoException {
    if (!(value == -1 || value == +1)) {
      throw new PropertyVetoException("!(value == -1 || value == +1)", null);
    }
    this.Direction = value;
  }
  public int getDirection() {
    return this.Direction;
  }
  

  private int NumRounds = 1;
  public void setNumRounds(int value) throws PropertyVetoException {
    if (!(value == -1 || value == +1)) {
      throw new PropertyVetoException("!(value == -1 || value == +1)", null);
    }
    this.NumRounds = value;
  }
  public int getNumRounds() {
    return this.NumRounds;
  }
  
  
  private boolean ExpandNeighborhoodToIncludeTies = false;
  public void setExpandNeighborhoodToIncludeTies(boolean value) {
    this.ExpandNeighborhoodToIncludeTies = value;
  }
  public boolean getExpandNeighborhoodToIncludeTies() {
    return this.ExpandNeighborhoodToIncludeTies;
  }
  
  
  
  private double MaxExpansionFactor = 2.0;
  public void setMaxExpansionFactor(double value) throws PropertyVetoException {
    if (value < 1.0) {
      throw new PropertyVetoException(" < 1.0", null);
    }
    this.MaxExpansionFactor = value;
  }
  
  public double getMaxExpansionFactor() {
    return this.MaxExpansionFactor;
  }
  
  
  
  
  
  
  public String getModuleName() {
    return "Instance Based Inducer";
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
       "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }
  
 
  
  public void doit() throws Exception {
    
    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);
    
    Model model = null;
    
    model = generateModel(exampleSet, errorFunction);
    
    this.pushOutput(model, 0);
  }
  
}
