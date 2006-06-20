package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class InstanceBasedParamSpaceGenerator_1 extends AbstractParamSpaceGenerator {
  
  int numControlParameters = 8;
  
  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Parameter Space";
      case 1: return "Function Inducer Class";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameter Space for InstanceBased Inducer";
      case 1: return "Instance Based Function Inducer Class";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
       "java.lang.Class"};
       return out;
  }
  
  
  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
    
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];
    
    int biasIndex = 0;
    
    biasNames       [biasIndex] = NEIGHBORHOOD_SIZE;//"NeighborhoodSize";
    minControlValues[biasIndex] = 1.0;
    maxControlValues[biasIndex] = 100;
    defaults        [biasIndex] = 20;
    resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames       [biasIndex] = "DistanceWeightingPower";
    minControlValues[biasIndex] = 0.0;
    maxControlValues[biasIndex] = 0.0;
    defaults        [biasIndex] = 0.0;
    resolutions     [biasIndex] = 0;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    biasNames       [biasIndex] = "ZeroDistanceWeight";
    minControlValues[biasIndex] = 0.0;
    maxControlValues[biasIndex] = 0.0;
    defaults        [biasIndex] = 0.0;
    resolutions     [biasIndex] = 0;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    
    biasNames       [biasIndex] = "UseStepwiseRegression";
    minControlValues[biasIndex] = 0.0;
    maxControlValues[biasIndex] = 1.0;
    defaults        [biasIndex] = 0.0;
    resolutions     [biasIndex] = 1;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames       [biasIndex] = "Direction";
    minControlValues[biasIndex] = -1.0;
    maxControlValues[biasIndex] = 1.0;
    defaults        [biasIndex] = 1.0;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames       [biasIndex] = "NumRounds";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 10.0;
    defaults        [biasIndex] = 1.0;
    resolutions     [biasIndex] = 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;
    
    biasNames       [biasIndex] = "ExpandNeighborhoodForTies";
    minControlValues[biasIndex] = 0.0;
    maxControlValues[biasIndex] = 1.0;
    defaults        [biasIndex] = 0.0;
    resolutions     [biasIndex] = 1;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;
    
    biasNames       [biasIndex] = "MaxExpansionFactor";
    minControlValues[biasIndex] = 1.0;
    maxControlValues[biasIndex] = 10.0;
    defaults        [biasIndex] = 2.0;
    resolutions     [biasIndex] = 1;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;
    
    
    ParameterSpace psi = new ParameterSpaceImpl();
    psi.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
    return psi;
  }
  
  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Instance Based Param Space Generator";
  }
  
  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  
  //!!! ??? !!!  
  public static final String NEIGHBORHOOD_SIZE = "NeighborhoodSize";
  
  
  
  
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numControlParameters];
    
    int i = 0;
    
    pds[i++] = new PropertyDescription(NEIGHBORHOOD_SIZE,
     "Neighborhood Size",
     "The number of examples to use for fitting the prediction module.  " +
     "This must be set to 1 or greater.  ");
    
    pds[i++] = new PropertyDescription(
     "DistanceWeightingPower",
     "Distance Weighting Power",
     "The value of the power term in the inverse distance weighting formula.  " +
     "Setting this to zero causes equal weighting of all examples.  " +
     "Setting it to 1.0 gives inverse distance weighting.  " +
     "Setting it to 2.0 gives inverse distance squared weighting and so on.  ");
    
    pds[i++] = new PropertyDescription(
     "zeroDistanceWeight",
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
  
  
  
  /**
   * All we have to do here is push the parameter space and function inducer class.
   */
  public void doit() throws Exception{
    
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.inducers.InstanceBasedInducerOpt");
    } catch (Exception e) {
      //System.out.println("could not find class");
      //throw new Exception();
      throw new Exception(getAlias() + ": could not find class InstanceBasedInducerOpt ");
    }
    
    if (space == null) space = getDefaultSpace();
    this.pushOutput(space, 0);
    this.pushOutput(functionInducerClass, 1);
  }
}
