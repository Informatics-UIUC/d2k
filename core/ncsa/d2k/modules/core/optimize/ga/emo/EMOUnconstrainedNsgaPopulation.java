package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

public class EMOUnconstrainedNsgaPopulation 
    extends ConstrainedNsgaPopulation implements EMOPopulation {

  private Parameters parameters;

  public EMOUnconstrainedNsgaPopulation(Range[] ranges,
                                   ObjectiveConstraints[] objConstraints,
                                   int numMembers, double targ) {
    super(ranges, objConstraints, numMembers, targ);
  }

  public Parameters getParameters() {
    return parameters;
  }

  public void setParameters(Parameters params) {
    parameters = params;
  }
}
