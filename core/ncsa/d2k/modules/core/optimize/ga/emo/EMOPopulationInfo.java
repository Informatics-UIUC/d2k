package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;

public class EMOPopulationInfo implements java.io.Serializable {

  // a Table with zero rows.  the columns are the variables
  public Table varNames;

  public EMOConstruction[] fitnessVariableConstructions;
  public EMOConstruction[] fitnessFunctionConstructions;

  public EMOConstruction[] constraintVariableConstructions;
  public EMOConstruction[] constraintFunctionConstructions;

  public Table boundsAndPrecision;

  public static final int BINARY_TYPE = 1;
  public static final int DOUBLE_TYPE = 0;

  private int type;

  public EMOPopulationInfo() {
    fitnessVariableConstructions = new EMOConstruction[0];
    fitnessFunctionConstructions = new EMOConstruction[0];
    constraintVariableConstructions = new EMOConstruction[0];
    constraintFunctionConstructions = new EMOConstruction[0];
  }

  public void setType(int t) {
    type = t;
  }

  public int getType() {
    return type;
  }
}