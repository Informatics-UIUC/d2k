package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

public class EMOConstrainedNsgaPopulation 
    extends ConstrainedNsgaPopulation implements EMOPopulation {
  
  private Parameters parameters;
  
  public EMOConstrainedNsgaPopulation(Range[] ranges,
                                   ObjectiveConstraints[] objConstraints,
                                   int numMembers, double targ, int numConstr) {
    super(ranges, objConstraints, numMembers, targ, numConstr);
  }
  
  public Parameters getParameters() {
    return parameters;
  }
  
  public void setParameters(Parameters params) {
    parameters = params;
  }
}