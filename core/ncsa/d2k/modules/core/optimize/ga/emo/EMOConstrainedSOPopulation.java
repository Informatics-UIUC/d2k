package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * An extension of ConstrainedSOPopulation that implements the EMOPopulation
 * interface.  The Parameters for an EMO problem are kept in this population.
 * @author David Clutter
 */

public class EMOConstrainedSOPopulation
    extends ConstrainedSOPopulation
    implements EMOPopulation {

  /** the parameters for the EMO problem */
  private Parameters parameters;

  /**
   * 
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param numConstraints
   * @param targ
   */
  public EMOConstrainedSOPopulation(Range[] ranges,
                                    ObjectiveConstraints objConstraints,
                                    int numMembers, double targ, 
                                    int numConstraints) {
    super(ranges, objConstraints, numMembers, numConstraints, targ);
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