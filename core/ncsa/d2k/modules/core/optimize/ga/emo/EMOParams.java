package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.Construction;

import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;

/**
 * Contains all the parameters to run EMO.  This includes the parameters to
 * create the population, calculate fitness functions, calculate constraints,
 * and mutation, selection and crossover parameters.
 */
public class EMOParams implements java.io.Serializable {

  /**
   * Contains the bounds and precision for the decision variables.
   */
  public DecisionVariables decisionVariables;

  public FitnessFunctions fitnessFunctions;

  public Constraints constraints;

  public Crossover crossover;
  public Mutation mutation;

  public Selection selection;

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

  public boolean createBinaryIndividuals;

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

  public EMOParams() {
    decisionVariables = new DecisionVariables();
    fitnessFunctions = new FitnessFunctions();
    constraints = new Constraints();
  }
}