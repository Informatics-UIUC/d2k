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

  //public static final int BINARY_TYPE = 1;
  //public static final int DOUBLE_TYPE = 0;

  private int type;
  private boolean useExternalFitnessEvaluation;
  private boolean useExternalConstraintEvaluation;

  public Table externalFitnessInfo;
  public Table externalConstraintInfo;

  public EMOPopulationInfo() {
    fitnessVariableConstructions = new Construction[0];
    fitnessFunctionConstructions = new Construction[0];
    constraintVariableConstructions = new Construction[0];
    constraintFunctionConstructions = new Construction[0];
  }

  /*public void setType(int t) {
    type = t;
  }

  public int getType() {
    return type;
  }*/

  public void setUseExternalFitnessEvaluation(boolean b) {
    useExternalFitnessEvaluation = b;
  }
  public boolean getUseExternalFitnessEvaluation() {
    return useExternalFitnessEvaluation;
  }

  public void setUseExternalConstrainEvaluation(boolean b) {
    useExternalConstraintEvaluation = b;
  }
  public boolean getUseExternalConstraintEvaluation() {
    return useExternalConstraintEvaluation;
  }
}