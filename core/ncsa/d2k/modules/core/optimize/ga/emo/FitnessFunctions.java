package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.util.*;

/**
 * Define the fitness functions (FF) on a problem in EMO.  Both FF
 * by formula and FF by an external executable are supported.  Separate
 * counts of the number of FF defined are kept for FF defined
 * by formula and FF calculated by an executable.
 *
 * This class does not evaluate the FF on a population, just stores
 * the information about the FFs.
 *
 * A fitness variable is a transformation on a table that creates a variable,
 * most likely to be used in the calculation of the FFs.
 */
public class FitnessFunctions implements java.io.Serializable {

  /** keeps the descriptions of the executables */
  private MutableTable externalFunctions;

  //////////////////////////////////////////////////////////////
  // constants used to index specific columns in externalFunctions

  /** the name of the FF */
  private static final int NAME = 0;
  /** the path to the executable */
  private static final int EXEC = 4;
  /** the path to the input file */
  private static final int INPUT = 1;
  /** the path to the output file */
  private static final int OUTPUT = 2;
  /** maximizing or minimizing? */
  private static final int MAX_MIN = 3;

  /** a list of the Constructions to calculate fitness variables */
  private List fitnessVariables;
  /** a list of the Constructions to calculate FFs */
  private List fitnessFunctions;

  public FitnessFunctions() {
    externalFunctions = new MutableTableImpl(5);
//BASIC3
//    externalFunctions.setColumn(new String[0], 0);
//    externalFunctions.setColumn(new String[0], 1);
//    externalFunctions.setColumn(new String[0], 2);
//    externalFunctions.setColumn(new boolean[0], 3);
//    externalFunctions.setColumn(new String[0], 4);

    externalFunctions.setColumn(new StringColumn(0), 0);
    externalFunctions.setColumn(new StringColumn(0), 1);
    externalFunctions.setColumn(new StringColumn(0), 2);
    externalFunctions.setColumn(new BooleanColumn(0), 3);
    externalFunctions.setColumn(new StringColumn(0), 4);
    
    
    fitnessVariables = new ArrayList();
    fitnessFunctions = new ArrayList();
  }

  /**
   * Get the number of fitness functions defined by a transformation on a table.
   * @return the number of fitness functions by formula
   */
  public int getNumFitnessFunctions() {
    return fitnessFunctions.size();
  }

  /**
   * Get the number of fitness variables defined by a transformation on a table.
   * @return the number of fitness variables by formula
   */
  public int getNumFitnessVariables() {
    return fitnessVariables.size();
  }

  /**
   * Add a fitness variable
   * @param var
   */
  public void addFitnessVariable(Construction var) {
    fitnessVariables.add(var);
  }

  /**
   * Add a fitness function
   * @param ffc
   */
  public void addFitnessFunction(Construction c, boolean minimizing) {
    FFunction ff = new FFunction(c, minimizing);
    fitnessFunctions.add(ff);
  }

  /**
   * Get the ith fitness variable
   * @param i the index
   * @return the ith fitness variable
   */
  public Construction getFitnessVariable(int i) {
    return (Construction)fitnessVariables.get(i);
  }

  /**
   * Get the name of the ith fitness variable
   * @param i the index
   * @return the name of ith fitness variable
   */
  public String getFitnessVariableName(int i) {
    return getFitnessVariable(i).label;
  }

  /**
   * Get the Construction used to calculate the ith FF
   * @param i the index
   * @return the construction for the ith FF
   */
  public Construction getFitnessFunction(int i) {
    FFunction ff = (FFunction)fitnessFunctions.get(i);
    return ff.con;
  }

  /**
   * Get the name of the ith FF
   * @param i the index
   * @return the name of the ith FF
   */
  public String getFitnessFunctionName(int i) {
    FFunction ff = (FFunction)fitnessFunctions.get(i);
    return ff.con.label;
  }

  /**
   * Return true if the ith FF is minimizing, false if it is maximizing
   * @param i the index
   * @return true if the FF is minimizing, false if maximizing
   */
  public boolean functionIsMinimizing(int i) {
    FFunction ff = (FFunction)fitnessFunctions.get(i);
    return ff.minmax;
  }

  private class FFunction implements java.io.Serializable {
    Construction con;
    boolean minmax;

    FFunction(Construction c, boolean min) {
      con = c;
      minmax = min;
    }
  }

  /**
   * Add a fitness function calculated by executable.
   * @param nm the name of the FF
   * @param exec the path to the executable
   * @param in the input file that contains the genes
   * @param out the output file that will contain the fitness values
   * @param min true if this function is minimizing, false if maximizing
   */
  public void addExternFitnessFunction(String nm, String exec, String in, String out, boolean min) {
//BASIC3    Object[] row = new Object[5];
//    row[NAME] = nm;
//    row[INPUT] = in;
//    row[OUTPUT] = out;
//    row[MAX_MIN] = new Boolean(min);
//    row[EXEC] = exec;
//    externalFunctions.addRow(row);
  	int numRows = externalFunctions.getNumRows();
  	externalFunctions.addRows(1);
  	externalFunctions.setString(nm,numRows,NAME);
  	externalFunctions.setString(in,numRows,INPUT);
  	externalFunctions.setString(out,numRows,OUTPUT);
  	externalFunctions.setBoolean(min,numRows,MAX_MIN);
  	externalFunctions.setString(exec,numRows,EXEC);
  }

  /**
   * Get the number of FF calculated by executable.
   * @return the number of FF calculated by executable.
   */
  public int getNumExternFitnessFunctions() {
    return externalFunctions.getNumRows();
  }

  /**
   * Get the name of the ith FF by executable.
   * @param i the index
   * @return the name of the ith FF calculated by executable
   */
  public String getExternFitnessFunctionName(int i) {
    return externalFunctions.getString(i, NAME);
  }

  /**
   * Get the path to the input file for the ith FF calculated by executable
   * @param i teh index
   * @return the path
   */
  public String getExternFitnessFunctionInput(int i) {
    return externalFunctions.getString(i, INPUT);
  }

  /**
   * Get the path to the output file for the ith FF calculated by executable
   * @param i the index
   * @return the path
   */
  public String getExternFitnessFunctionOutput(int i) {
    return externalFunctions.getString(i, OUTPUT);
  }

  /**
   * Return true if the ith FF by executable is minimizing, false if maximizing
   * @param i the index
   * @return true if the FF is minimizing, false if maximizing
   */
  public boolean getExternIsMinimizing(int i) {
    return externalFunctions.getBoolean(i, MAX_MIN);
  }

  /**
   * get the path to the executable for the ith FF
   * @param i the index
   * @return the path to the executable
   */
  public String getExternFitnessFunctionExec(int i) {
    return externalFunctions.getString(i, EXEC);
  }

  /**
   * Get the total number of fitness functions defined.  The sum of
   * fitness functions defined by formula and the fitness functions defined
   * by executable
   * @return the total number of fitness functions defined
   */
  public int getTotalNumFitnessFunctions() {
    return fitnessFunctions.size() + externalFunctions.getNumRows();
  }
}