package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.optimize.util.*;

public class ConstrainedSOPopulation
    extends SOPopulation
    implements ConstrainedPopulation {

  protected double[] constraintWeights;

  /**
   * Create a constrained single objective population.  The members of this
   * population will have the specified number of constraints
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param numConstraints
   * @param targ
   */
  public ConstrainedSOPopulation(Range[] ranges,
                                 ObjectiveConstraints objConstraints,
                                 int numMembers, int numConstraints,
                                 double targ) {
    super(ranges, objConstraints, targ);
    constraintWeights = new double[numConstraints];

    if (ranges[0] instanceof BinaryRange) {
      // Set up the members
      members = new BinaryIndividual[numMembers];
      nextMembers = new BinaryIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new BinaryIndividual( (BinaryRange[]) ranges,
                                          numConstraints);
        nextMembers[i] = new BinaryIndividual( (BinaryRange[]) ranges,
                                              numConstraints);
      }

    }
    else if (ranges[0] instanceof DoubleRange) {
      // Set up the members
      members = new NumericIndividual[numMembers];
      nextMembers = new NumericIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new NumericIndividual( (DoubleRange[]) ranges,
                                           numConstraints);
        nextMembers[i] = new NumericIndividual( (DoubleRange[]) ranges,
                                               numConstraints);
      }
    }
    else if (ranges[0] instanceof IntRange) {

      /*// Set up the members
                        members = new IntIndividual [numMembers];
                        nextMembers = new IntIndividual [numMembers];
                        for (int i = 0 ; i < numMembers ; i++) {
             members[i] = new IntIndividual (traits);
             nextMembers[i] = new IntIndividual (traits);
                        }*/
    }
    else {
      System.out.println("What kind of range is this?");
    }

  }

  public int getNumConstraints() {
    return constraintWeights.length;
  }

  public double getConstraintWeight(int index) {
    return constraintWeights[index];
  }

  public void setConstraintWeight(double newWeight, int index) {
    constraintWeights[index] = newWeight;
  }
}