package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.Construction;

public class EMOPopulationInfo implements java.io.Serializable {

  // a Table with zero rows.  the columns are the variables
  public Table varNames;

  public Construction[] fitnessVariableConstructions;
  public Construction[] fitnessFunctionConstructions;

  public Construction[] constraintVariableConstructions;
  public Construction[] constraintFunctionConstructions;

  public Table boundsAndPrecision;

  private int type;
  public boolean useExternalFitnessEvaluation;
  public boolean useExternalConstraintEvaluation;

  public Table externalFitnessInfo;
  public Table externalConstraintInfo;

  public double mutationRate;
  public double crossoverRate;
  public int tournamentSize;

  public int populationSize;
  public int maxGenerations;

  public double generationGap;

  public EMOPopulationInfo() {
    fitnessVariableConstructions = new Construction[0];
    fitnessFunctionConstructions = new Construction[0];
    constraintVariableConstructions = new Construction[0];
    constraintFunctionConstructions = new Construction[0];
  }
}