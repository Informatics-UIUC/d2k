package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.Construction;

/**
 * Contains all the parameters to run EMO.  This includes the parameters to
 * create the population, calculate fitness functions, calculate constraints,
 * and mutation, selection and crossover parameters.
 */
public class EMOPopulationParams implements java.io.Serializable {

  /**
   * This table only contains the variable names.  The column labels are
   * the variable names; the table has zero rows of data.
   */
  public Table varNames;

  /**
   * Contains the bounds and precision for the decision variables.
   */
  public Table boundsAndPrecision;

  /**
   * The Constructions to calculate variables related to fitness function.
   */
  public Construction[] fitnessVariableConstructions;
  /**
   * The Constructions to calculate the fitness functions.
   */
  public Construction[] fitnessFunctionConstructions;

  /**
   * The Constructions to calculate variables related to constraints.
   */
  public Construction[] constraintVariableConstructions;

  /**
   * The Constructions to calculate constraints.
   */
  public Construction[] constraintFunctionConstructions;

  /**
   * If an external executable should be used to calculate the fitness
   * functions, this will be true.  If false, the fitnessFunctionConstructions
   * will be used to calculate the fitness functions.
   */
  public boolean useExternalFitnessEvaluation = false;

  /**
   * If an external executable is used to calculate fitness functions,
   * this table will contain the info about the executables.
   *
   *
   */
  public Table externalFitnessInfo;

  /**
   * If an external executable should be used to calculate the constraints,
   * this will be true.  If false, the constraintFunctionConstructions
   * will be used to calculate the constraints.
   */
  public boolean useExternalConstraintEvaluation = false;

  /**
   * If an external executable is used to calculate constraints,
   * this table will contain the info about the executables.
   */
  public Table externalConstraintInfo;

  /**
   * The mutation rate.
   */
  public double mutationRate = 0.1;

  /**
   * The crossover rate.
   */
  public double crossoverRate = 0.25;

  /**
   * The generation gap.
   */
  public double generationGap = 0.6;

  /**
   * n ?
   */
  public double nSimulatedBinaryCrossover;
  public double nRealMutation;

  /**
   * The tournament size.
   */
  public int tournamentSize = 4;

  /**
   * The selectioni pressure.
   */
  public double selection_pressure;

  /**
   * The inital population size.
   */
  public int populationSize = 100;

  /**
   * The maximum number of generations.
   */
  public int maxGenerations;

  public int selectionType = 2;
  public int crossoverType = 1;
  public int mutationType = 0;

  public EMOPopulationParams() {
    fitnessVariableConstructions = new Construction[0];
    fitnessFunctionConstructions = new Construction[0];
    constraintVariableConstructions = new Construction[0];
    constraintFunctionConstructions = new Construction[0];
  }
}