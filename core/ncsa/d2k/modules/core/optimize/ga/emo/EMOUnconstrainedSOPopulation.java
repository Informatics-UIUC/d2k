package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

public class EMOUnconstrainedSOPopulation extends SOPopulation 
    implements EMOPopulation {

  private Parameters parameters;

      public EMOUnconstrainedSOPopulation(Range[] ranges,
                                     ObjectiveConstraints objConstraints,
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
