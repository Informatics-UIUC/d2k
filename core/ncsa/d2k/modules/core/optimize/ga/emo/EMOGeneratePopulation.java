package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.util.*;

public class EMOGeneratePopulation
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationParams"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  private int populationSize;
  // the number of fitness functions defined
  private int numFF;
  private int numFormulaFF;
  private int numExternalFF;

  private int numConstraints;
  private int numFormulaConstraints;
  private int numExternalConstraints;

  private EMOPopulationParams params;
  private boolean firstTime;

  private static final String X = "x";

  public void beginExecution() {
    firstTime = true;
  }

  public void endExecution() {
    params = null;
  }

  public void doit() throws Exception {
    if (firstTime) {
      params = (EMOPopulationParams) pullInput(0);
      populationSize = params.populationSize;

      // determine what type of population to create.  a different pop is
      // created for SO and MO problems.  simply count the number of FF
      // to determine if it is an SO or MO problem.
      Construction[] ffs = params.fitnessFunctionConstructions;
      Table externalFfs = params.externalFitnessInfo;

      numFF = 0;
      numFormulaFF = 0;
      numExternalFF = 0;
      if (ffs != null) {
        numFF += ffs.length;
        numFormulaFF = ffs.length;
      }
      if (externalFfs != null) {
        numFF += externalFfs.getNumRows();
        numExternalFF = externalFfs.getNumRows();
      }

      // determine how many constraints are imposed on the problem
      Construction[] constraints = params.constraintFunctionConstructions;
      Table externalConstraints = params.externalConstraintInfo;
      numConstraints = 0;
      numFormulaConstraints = 0;
      numExternalConstraints = 0;

      if (constraints != null) {
        numConstraints += constraints.length;
        numFormulaConstraints = constraints.length;
      }
      if (externalConstraints != null) {
        numConstraints += externalConstraints.getNumRows();
        numExternalConstraints = externalConstraints.getNumRows();
      }

      if (numFF == 0) {
        throw new Exception("No Fitness Functions were defined");
      }

      firstTime = false;
    }

    // first create the individuals
    int numVariables = params.boundsAndPrecision.getNumRows();
    Range[] xyz;

    // use BinaryRange for binary-coded individuals
    if (params.createBinaryIndividuals) {
      xyz = new BinaryRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        xyz[i] = new BinaryRange(X, params.boundsAndPrecision.getInt(i, 4));
      }
    }
    // use DoubleRange for real-coded individuals
    else {
      xyz = new DoubleRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        xyz[i] = new DoubleRange(params.boundsAndPrecision.getString(i, 0),
                                 params.boundsAndPrecision.getFloat(i, 2),
                                 params.boundsAndPrecision.getFloat(i, 1));
      }
    }

    ObjectiveConstraints[] formulas = new ObjectiveConstraints[numFormulaFF];
    ObjectiveConstraints[] externs = new ObjectiveConstraints[numExternalFF];

    // first create the objective constraints for FF by formula
    for (int i = 0; i < numFormulaFF; i++) {
      if ( ( (FitnessFunctionConstruction) params.
            fitnessFunctionConstructions[i]).getIsMinimizing()) {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            params.fitnessFunctionConstructions[i].label, 0.0, 100.0);
      }
      else {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            params.fitnessFunctionConstructions[i].label, 100.0, 0.0);
      }
    }

    // now create the objective constraints for FF by extern
    for (int i = 0; i < numExternalFF; i++) {
      String name = params.externalFitnessInfo.getString(i, 0);
      if (params.externalFitnessInfo.getString(i, 4).equals("Minimize")) {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 0.0, 100.0);
      }
      else {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 100.0, 0.0);
      }
    }

    // now copy them into one big array
    ObjectiveConstraints[] fit =
        new ObjectiveConstraints[numFormulaFF + numExternalFF];
    System.arraycopy(formulas, 0, fit, 0, numFormulaFF);
    System.arraycopy(externs, 0, fit, numFormulaFF, numExternalFF);

    // for an SO problem (one fitness function) create
    if (numFF == 1) {
      boolean constrained = (numConstraints > 0);

      if(constrained) {
        // create a constrained SO pop  
      }
      else {
        // create an unconstrained SO pop  
      }
      
      // pop.setPopulationInfo(params);
      // pushOutput(pop, 0);
    }
    // otherwise, for an MO problem (mulitple fitness functions) create
    // either a constrained or unconstrained nsga population
    else {
      NsgaPopulation pop = null;
      boolean constrained = (numConstraints > 0);

      // if there were constraints, we create a constrained pop
      if (constrained) {
        pop = new ConstrainedNsgaPopulation(xyz, fit,
                                            this.populationSize, 0.01);

      }
      // if there are no constraints, we create an Unconstrained pop
      else {
        pop = new UnconstrainedNsgaPopulation(xyz, fit,
                                              this.populationSize, 0.01);
      }
      pop.setPopulationInfo(params);
      //set the maximum number of generation
      pop.setMaxGenerations(params.maxGenerations);
      
      pushOutput(pop, 0);
    }
  }
}