package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.util.*;

/**
 * Define the constraints to impose on a problem in EMO.  Both constraints
 * by formula and constraints by an external executable are supported.  Separate
 * counts of the number of constraints defined are kept for constraints defined
 * by formula and constraints calculated by an executable.
 *
 * This class does not evaluate the constraints on a population, just stores
 * the information about the constraints.
 *
 * A constraint variable is a transformation on a table that creates a variable,
 * most likely to be used in the calculation of the constraints.
 */
public class Constraints implements java.io.Serializable {

  /**
   * Keeps the information about the constraints evaluated by
   * external executable.
   */
  private MutableTable externalFunctions;

  //////////////////////////////////////////////////////
  // constants defined to index columns in externalFunctions table

  /** the name of the constraint */
  private static final int NAME = 0;
  /** the path to the executable for the constraint */
  private static final int EXEC = 4;
  /** the path to the input file for the constraint */
  private static final int INPUT = 1;
  /** the path to the output file for the constraint */
  private static final int OUTPUT = 2;
  /** the weight to assign to the constraint */
  private static final int WEIGHT = 3;

  ///////////////////////////////////////////////////////

  /**
   * a list of the variables used in constraint calculations.  these
   * are Constructions on a Table that contains a population
   */
  private List constraintVariables;

  /**
   * a list of the functions used in constraint calculations.  these
   * are Constructions on a Table that contains a population
   */
  private List constraintFunctions;

  /**
   * Create the constraints.
   */
  public Constraints() {
    // create the externalFunctions table
    externalFunctions = new MutableTableImpl(5);
//BASIC3    externalFunctions.setColumn(new String[0], 0);
//    externalFunctions.setColumn(new String[0], 1);
//    externalFunctions.setColumn(new String[0], 2);
//    externalFunctions.setColumn(new double[0], 3);
//    externalFunctions.setColumn(new String[0], 4);

    externalFunctions.setColumn(new StringColumn(0), 0);
    externalFunctions.setColumn(new StringColumn(0), 1);
    externalFunctions.setColumn(new StringColumn(0), 2);
    externalFunctions.setColumn(new DoubleColumn(0), 3);
    externalFunctions.setColumn(new StringColumn(0), 4);
    
    // create the lists
    constraintVariables = new ArrayList();
    constraintFunctions = new ArrayList();
  }

  ////////////////////////////////////////////////////////////////////////////
  // support for constraints and constraint variables by transformations on a table

  /**
   * Get the number of constraint functions defined by a formula.
   * @return the number of constraint functions defined by formula
   */
  public int getNumConstraintFunctions() {
    return constraintFunctions.size();
  }

  /**
   * Get the number of constraint variables defined by a formula.
   * @return the number of constraint variables defined by formula
   */
  public int getNumConstraintVariables() {
    return constraintVariables.size();
  }

  /**
   * Add a constraint variable.
   * @param var the Construction that creates the constraint variable.
   */
  public void addConstraintVariable(Construction var) {
    constraintVariables.add(var);
  }

  /**
   * Add a constraint function
   * @param var the Construction that creates the constraint function.
   */
  public void addConstraintFunction(Construction c, double weight) {
    Constr con = new Constr(c, weight);
    constraintFunctions.add(con);
  }

  /**
   * A class that ties together a constraint construction and a weight
   */
  private class Constr implements java.io.Serializable {
    Construction con;
    double weight;

    Constr(Construction c, double w) {
      con = c;
      weight = w;
    }
  }

  /**
   * Get the ith constraint variable
   * @param i the index
   * @return the ith constraint variable
   */
  public Construction getConstraintVariable(int i) {
    return (Construction)constraintVariables.get(i);
  }

  /**
   * Get the name of the ith constraint variable
   * @param i the index
   * @return the name of the ith constraint variable
   */
  public String getConstraintVariableName(int i) {
    return getConstraintVariable(i).label;
  }

  /**
   * Get the ith constraint function
   * @param i the index
   * @return the ith constraint function
   */
  public Construction getConstraintFunction(int i) {
    Constr c = (Constr)constraintFunctions.get(i);
    return c.con;
  }

  /**
   * Get the name of the ith constraint function
   * @param i the index
   * @return the ith constraint function
   */
  public String getConstraintFunctionName(int i) {
    return getConstraintFunction(i).label;
  }

  /**
   * Get the weight associated with the ith constraint function
   * @param i the index
   * @return the weight
   */
  public double getConstraintFunctionWeight(int i) {
    Constr c = (Constr)constraintFunctions.get(i);
    return c.weight;
  }

  //////////////////////////////////////////////////////////////////
  // support for constraints by executable

  /**
   * Add a ccnstraint that is evaluated by executable.
   * @param nm the name of the constraint
   * @param exec the path to the executable
   * @param in the path to the input file
   * @param out the path to the output file
   * @param w the weight assigned to the constraint
   */
  public void addExternConstraint(String nm, String exec, String in, String out, double w) {

//BASIC3    Object[] row = new Object[5];
//    row[0] = nm;
//    row[1] = in;
//    row[2] = out;
//    row[3] = new Double(w);
//    row[4] = exec;
//    externalFunctions.addRow(row);
  	int numRows = externalFunctions.getNumRows();
  	externalFunctions.addRows(1);
  	externalFunctions.setString(nm,numRows,0);
  	externalFunctions.setString(in,numRows,1);
  	externalFunctions.setString(out,numRows,2);
  	externalFunctions.setDouble(w,numRows,3);
  	externalFunctions.setString(exec,numRows,4);
  	
  	
  }

  /**
   * Get the number of constraints calculated by executables.
   * @return the number of constraints calculated by executables.
   */
  public int getNumExternConstraints() {
    return externalFunctions.getNumRows();
  }

  /**
   * Get the name of the ith constraint calculated by an executable
   * @param i the index
   * @return the name of the ith constraint calculated by an executable
   */
  public String getExternConstraintName(int i) {
    return externalFunctions.getString(i, NAME);
  }

  /**
   * Get the path to the input file for the ith constraint calculated by an
   * executable
   * @param i the index
   * @return the path to the input file
   */
  public String getExternConstraintInput(int i) {
    return externalFunctions.getString(i, INPUT);
  }

  /**
   * Get the path to the output file for the ith constraint calculated by
   * an executable
   * @param i the index
   * @return the path to the output file
   */
  public String getExternConstraintOutput(int i) {
    return externalFunctions.getString(i, OUTPUT);
  }

  /**
   * Get the path to the executable for the ith constraint.
   * @param i the index
   * @return the path to the executable
   */
  public String getExternConstraintExec(int i) {
    return externalFunctions.getString(i, EXEC);
  }

  /**
   * Get the weight for the ith executable constraint
   * @param i the index
   * @return the weight assigned to the ith executable constraint
   */
  public double getExternConstraintWeight(int i) {
    return externalFunctions.getDouble(i, this.WEIGHT);
  }

  /**
   * Get the total number of constraint functions defined.  This includes both
   * the constraints defined by formula and the constraints defined by executable.
   * @return the total number of constraints
   */
  public int getTotalNumConstraints() {
    return constraintFunctions.size() + externalFunctions.getNumRows();
  }
}