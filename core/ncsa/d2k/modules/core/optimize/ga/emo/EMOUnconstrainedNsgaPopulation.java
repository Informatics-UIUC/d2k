package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

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

/*  private void copyToTable(MutableTable mt) {
    DecisionVariables dv = this.getParameters().decisionVariables;
    int size = this.size();
    boolean isBinary;

    if(! (size > 0))
      return;
    if(members[0] instanceof NumericIndividual)
      isBinary = false;
    else
      isBinary = true;

    int numOfvar = getNumGenes();
    // copy real-valued variables directly into the table
    if (!isBinary) {
      for (int i = 0; i < size; i++) {
        double[] tabrows = (double[]) ( (getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          mt.setDouble(tabrows[j], i, j);
        }
      }
    }

    // otherwise, decode binary variables so that the entries copied into the
    // table are real-valued
    else {
      int numTraits = 0;
      BinaryRange[] traits = (BinaryRange[]) getTraits();

      // for each individual
      for (int j = 0; j < size; j++) {
        double[] genes = ( (MOBinaryIndividual) getMember(j)).toDouble();

        int curPos = 0;
        for (int k = 0; k < traits.length; k++) {
          int numBits = traits[k].getNumBits();
          double num = 0.0d;
          double max = dv.getVariableMax(k);
          double min = dv.getVariableMin(k);
          double precision = dv.getVariablePrecision(k);

          double interval = (max - min) * precision;

          // this is one trait
          for (int l = 0; l < numBits; l++) {
            if (genes[curPos] != 0) {
              num = num + Math.pow(2.0, l);
            }
            curPos++;
          }

          // if it is above the max, scale it down
          num = num * precision + min;
          if (num > max) {
            num = max;
          }
          if (num < min) {
            num = min;
          }
          mt.setDouble(num, j, k);
        }
      }
    }
  }

  public Table getTable() {
    int size = size();

    Range[] range = this.getTraits();
    ObjectiveConstraints [] objCon = this.getObjectiveConstraints();
    int numVariables = range.length;
    int numObj = this.getNumObjectives();

    // a column for each variable, a column for each objective, a column for
    // rank, a column for crowding
    int numColumns = numVariables+numObjectives+1+1;
    MutableTableImpl vt = new MutableTableImpl(numColumns);
    int i = 0;
    for(; i < numVariables; i++) {
      vt.setColumn(new double[size], i);
      vt.setColumnLabel(range[i].getName(), i);
    }

    for(int j = 0; j < numObjectives; j++, i++) {
      vt.setColumn(new double[size], i);
      vt.setColumnLabel(objCon[i].getName(), i);
    }
    vt.setColumn(new double[size], i);
    vt.setColumnLabel("Rank", i);
    vt.setColumn(new double[size], i+1);
    vt.setColumnLabel("Crowding", i+1);

    this.copyToTable(vt);
    return vt;
  }*/
}