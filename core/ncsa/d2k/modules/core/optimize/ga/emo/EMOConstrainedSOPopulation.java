package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

public class EMOConstrainedSOPopulation extends ConstrainedSOPopulation 
    implements EMOPopulation {
  
  private Parameters parameters;
  
      public EMOConstrainedSOPopulation(Range[] ranges,
                                     ObjectiveConstraints objConstraints,
                                     int numMembers, int numConstraints,
                                     double targ) {
        super(ranges, objConstraints, numMembers, numConstraints, targ);
      }
      
      public Parameters getParameters() {
        return parameters;
      }
    
      public void setParameters(Parameters params) {
        parameters = params;
      }
}