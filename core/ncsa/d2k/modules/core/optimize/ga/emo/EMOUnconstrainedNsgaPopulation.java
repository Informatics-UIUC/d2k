package ncsa.d2k.modules.core.optimize.ga.emo;


import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
     * An extension of UnconstrainedNsgaPopulation that implements the EMOPopulation
 * interface.  The Parameters for an EMO problem are kept in this population.
 * @author David Clutter
 */
public class EMOUnconstrainedNsgaPopulation
    extends ConstrainedNsgaPopulation
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
  public EMOUnconstrainedNsgaPopulation(Range[] ranges,
                                        ObjectiveConstraints[] objConstraints,
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