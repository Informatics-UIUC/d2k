package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * An extension of UnconstrainedSOPopulation that implements the EMOPopulation
 * interface.  The Parameters for an EMO problem are kept in this population.
 * @author David Clutter
 */
public class EMOUnconstrainedSOPopulation
    extends SOPopulation
    implements EMOPopulation {

  /** the parameters for the EMO problem */
  private Parameters parameters;

  /**
   * 
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param targ
   */
  public EMOUnconstrainedSOPopulation(Range[] ranges,
                                      ObjectiveConstraints objConstraints,
                                      int numMembers, double targ) {
    super(ranges, objConstraints, numMembers, targ);
  }

  /**
   * Get the parameters.
   * @return the parameters
   */
  public Parameters getParameters() {
    return parameters;
  }

  /**
   * Set the parameters
   * @param params the new parameters
   */
  public void setParameters(Parameters params) {
    parameters = params;
  }
}
