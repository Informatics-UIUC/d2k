package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * Generate a population.
 */
public class GeneratePopulation
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "Params";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "Population";
  }

  public String getModuleInfo() {
    return "";
  }

  /** the total number of fitness functions defined */
  private int numFF;
  /** the number of fitness functions defined using a formula
   * (using a transformation on a table) */
  private int numFormulaFF;
  /** the number of fitness functions calculated using an executable */
  private int numExternalFF;

  /** the total number of constraints imposed on the prob */
  private int numConstraints;
  /** the number of constraints defined using a formula
   * (using a transformation on a table) */
  private int numFormulaConstraints;
  /** the number of constraints calculated using an executable */
  private int numExternalConstraints;

  /** the parameters for the problem */
  private Parameters params;

  /** is this the first time the module has been run? */
  private boolean firstTime;

  private static final String X = "x";

  public void beginExecution() {
    firstTime = true;
    params = null;
  }

  public void endExecution() {
    params = null;
  }

  public void doit() throws Exception {
    if (firstTime) {
      // pull in the parameters
      params = (Parameters) pullInput(0);

      // count the number of fitness functions
      FitnessFunctions ff = params.fitnessFunctions;
      numFF = ff.getTotalNumFitnessFunctions();
      numFormulaFF = ff.getNumFitnessFunctions();
      numExternalFF = ff.getNumExternFitnessFunctions();

      // count the number of constraints
      Constraints con = params.constraints;
      numConstraints = con.getTotalNumConstraints();
      numFormulaConstraints = con.getNumConstraintFunctions();
      numExternalConstraints = con.getNumExternConstraints();

      // if there were no fitness functions, just die
      if (numFF == 0) {
        throw new Exception("No Fitness Functions were defined.");
      }

      firstTime = false;
    }
    else {
      // this is a dummy input signaling a new population that has
      // twice as many members
      pullInput(0);
      // double the population size, but use the same parameters
      params.populationSize *= 2;
    }

    // the decision variables for the problem
    DecisionVariables dv = params.decisionVariables;
    // the fitness functions
    FitnessFunctions ff = params.fitnessFunctions;

    int numVariables = dv.getNumVariables();
    
    Range[] xyz;

    // use BinaryRange for binary-coded individuals
    if (params.createBinaryIndividuals) {
      xyz = new BinaryRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        xyz[i] = new BinaryRange(X, (int)dv.getVariableStringLength(i));
      }
    }
    // use DoubleRange for real-coded individuals
    else {
      xyz = new DoubleRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        xyz[i] = new DoubleRange(dv.getVariableName(i),
                                 dv.getVariableMax(i),
                                 dv.getVariableMin(i));
      }
    }

    // the objective constraints for FF by formula
    ObjectiveConstraints[] formulas = new ObjectiveConstraints[numFormulaFF];
    // the objective constraints for FF by executable
    ObjectiveConstraints[] externs = new ObjectiveConstraints[numExternalFF];

    // first create the objective constraints for FF by formula
    for (int i = 0; i < numFormulaFF; i++) {
       if(ff.functionIsMinimizing(i)) {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ff.getFitnessFunctionName(i), 0.0, 1.0);
      }
      else {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ff.getFitnessFunctionName(i), 1.0, 0.0);
      }
    }

    // now create the objective constraints for FF by extern
    for (int i = 0; i < numExternalFF; i++) {
      String name = ff.getExternFitnessFunctionName(i);
      if(ff.getExternIsMinimizing(i)) {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 0.0, 1.0);
      }
      else {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 1.0, 0.0);
      }
    }

    // now copy them into one big array
    ObjectiveConstraints[] fit =
        new ObjectiveConstraints[numFormulaFF + numExternalFF];
    System.arraycopy(formulas, 0, fit, 0, numFormulaFF);
    System.arraycopy(externs, 0, fit, numFormulaFF, numExternalFF);

    // for an SO problem (one fitness function) create an SO population
    if (numFF == 1) {
      boolean constrained = (numConstraints > 0);

      EMOPopulation pop;
      // create a constrained SO pop
      if(constrained) {
        pop = new EMOConstrainedSOPopulation(xyz, fit[0],
                                             params.populationSize, 0.01, 
                                             this.numConstraints);
      }
      // create an unconstrained SO pop
      else {
        pop = new EMOUnconstrainedSOPopulation(xyz, fit[0], 
                                               params.populationSize, 0.01);
      }
      
      // the parameters tag along with the population
      pop.setParameters(params);
      //set the maximum number of generations
      ((Population)pop).setMaxGenerations(params.maxGenerations);
      
      pushOutput(pop, 0);
    }
    // otherwise, for an MO problem (mulitple fitness functions) create
    // either a constrained or unconstrained nsga population
    else {
      EMOPopulation pop = null;
      boolean constrained = (numConstraints > 0);

      // if there were constraints, we create a constrained pop
      if (constrained) {
        pop = new EMOConstrainedNsgaPopulation(xyz, fit,
                                            params.populationSize, 0.01, this.numConstraints);
      }
      // if there are no constraints, we create an Unconstrained pop
      else {
        pop = new EMOUnconstrainedNsgaPopulation(xyz, fit,
                                              params.populationSize, 0.01);
      }
      // the parameters tag along with the population
      pop.setParameters(params);
      //set the maximum number of generations
      ((NsgaPopulation)pop).setMaxGenerations(params.maxGenerations);

      pushOutput(pop, 0);
    }
  }
}