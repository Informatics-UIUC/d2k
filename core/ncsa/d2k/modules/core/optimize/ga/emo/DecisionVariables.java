package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * A structure to define the decision variables used in an EMO problem.  The
 * name, minimum, maximum, precision, and string length are kept.
 */
public class DecisionVariables {

  /////////////////////////////////////////////////////
  // constants used to index columns in the table
  
  /** the name of the variable */
  private static final int NAME = 0;
  /** the minimum value of the variable */
  private static final int MIN = 1;
  /** the maximum value of the variable */
  private static final int MAX = 2;
  /** the precision of the variable */
  private static final int PRECISION = 3;
  /** the string length of the variable */
  private static final int STR_LEN = 4;

  /** the table to hold the description of decision variables */
  private MutableTable table;

  /**
   * constructor 
   */
  public DecisionVariables() {
    table = new MutableTableImpl(5);

    table.setColumn(new String[0], 0);
    table.setColumn(new double[0], 1);
    table.setColumn(new double[0], 2);
    table.setColumn(new double[0], 3);
    table.setColumn(new double[0], 4);
  }

  /**
   * Add a new decision variable.
   * @param name the name of the variable
   * @param min the minimum value of the variable
   * @param max the maximum value of the variable
   * @param precision the precision of the variable
   * @param len the string length of the variable
   */
  public void addVariable(String name, double min, double max,
                          double precision, double len) {
    Object[] row = new Object[5];
    row[0] = name;
    row[1] = new Double(min);
    row[2] = new Double(max);
    row[3] = new Double(precision);
    row[4] = new Double(len);

    table.addRow(row);
  }

  /**
   * Get the number of variables defined
   * @return the number of variables
   */
  public int getNumVariables() {
    return table.getNumRows();
  }

  /**
   * Get the name of the ith variable.
   * @param i the index
   * @return the name
   */
  public String getVariableName(int i) {
    return table.getString(i, NAME);
  }

  /**
   * Get the minimum of the ith variable
   * @param i the index
   * @return the minimum
   */
  public double getVariableMin(int i) {
    return table.getDouble(i, MIN);
  }

  /**
   * Get the maximum of the ith variable
   * @param i the index
   * @return the maximum
   */
  public double getVariableMax(int i) {
    return table.getDouble(i, MAX);
  }

  /**
   * Get the precision of the ith variable
   * @param i the index
   * @return the precision
   */
  public double getVariablePrecision(int i) {
    return table.getDouble(i, PRECISION);
  }

  /**
   * Get the string length of the ith variable
   * @param i the index
   * @return the string length
   */
  public double getVariableStringLength(int i) {
    return table.getDouble(i, STR_LEN);
  }

  /**
   * Get the total string length for all defined variables.
   * @return the total string length
   */
  public double getTotalStringLength() {
    double retVal = 0;
    int numVar = getNumVariables();
    for(int i = 0; i < numVar; i++) {
      retVal += getVariableStringLength(i);
    }
    return retVal;
  }

  /**
   * Create a MutableTable with zero rows, and one column of doubles
   * for each decision variable.  A convenience for copying the individuals
   * in a population into a table and perform transformations on the table.
   * 
   * @return the new mutable table with a column for each decision variable
   */
  public MutableTable createVariableNameTable() {
    int numVars = getNumVariables();
    MutableTable variableNameTable = new MutableTableImpl(numVars);

    for(int i = 0; i < numVars; i++) {
      variableNameTable.setColumn(new double[0], i);
      variableNameTable.setColumnLabel(getVariableName(i), i);
    }
    return variableNameTable;
  }
}