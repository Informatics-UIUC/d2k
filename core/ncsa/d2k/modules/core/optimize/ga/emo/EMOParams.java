package ncsa.d2k.modules.core.optimize.ga.emo;

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
   * A structure describing the number and types of the decision variables.
   */
  public DecisionVariables decisionVariables;

  /**
   * A structure describing the number and types of fitness functions.
   */
  public FitnessFunctions fitnessFunctions;

  /**
   * A structure describing the number and types of constraint violation functions.
   */
  public Constraints constraints;

  /**
   * The crossover function to use.
   */
  public Crossover crossover;

  /**
   * The mutation function to use.
   */
  public Mutation mutation;

  /**
   * The selection function to use.
   */
  public Selection selection;

  /**
   * true if binary individuals should be created, false otherwise.
   */
  public boolean createBinaryIndividuals;

  /**
   * The population size to use.
   */
  public int populationSize;

  /**
   * The maximum number of generations.
   */
  public int maxGenerations;

  public EMOParams() {
    decisionVariables = new DecisionVariables();
    fitnessFunctions = new FitnessFunctions();
    constraints = new Constraints();
  }
}