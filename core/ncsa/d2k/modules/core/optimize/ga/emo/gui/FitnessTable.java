package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Holds the values of the FF for each individual and a boolean flag
 * indentifying a selected individual.
 */
public class FitnessTable extends MutableTableImpl {

  private int flagColumn;

  public FitnessTable(int numIndividuals, int numObjectives) {
    super(numObjectives+1);
    int i = 0;
    for(; i < numObjectives; i++) {
      setColumn(new double[numIndividuals], i);
    }
    setColumn(new boolean[numIndividuals], i);
    flagColumn = i;
  }

  public void setFitnessValue(double val, int row, int objectiveIndex) {
    this.setDouble(val, row, objectiveIndex);
  }

  public double getFitnessValue(int row, int objectiveIndex) {
    return getDouble(row, objectiveIndex);
  }

  public void setSelectedFlag(boolean val, int row) {
    setBoolean(val, row, flagColumn);
  }

  public boolean getSelectedFlag(int row) {
    return getBoolean(row, flagColumn);
  }
}