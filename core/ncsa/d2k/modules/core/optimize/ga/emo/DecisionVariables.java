package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class DecisionVariables {

  private static final int NAME = 0;
  private static final int MIN = 1;
  private static final int MAX = 2;
  private static final int PRECISION = 3;
  private static final int STR_LEN = 4;

  private MutableTable table;
  private MutableTable variableNameTable;

  public DecisionVariables() {
    table = new MutableTableImpl(5);

    table.setColumn(new String[0], 0);
    table.setColumn(new double[0], 1);
    table.setColumn(new double[0], 2);
    table.setColumn(new double[0], 3);
    table.setColumn(new double[0], 4);
  }

  public void addVariable(String name, double min, double max,
                          double precision, double len) {
    Object[] row = new Object[5];
    row[0] = name;
    row[1] = new Double(min);
    row[2] = new Double(max);
    row[3] = new Double(precision);
    row[4] = new Double(len);

    table.addRow(row);
  }

  public int getNumVariables() {
    return table.getNumRows();
  }

  public String getVariableName(int idx) {
    return table.getString(idx, NAME);
  }

  public double getVariableMin(int idx) {
    return table.getDouble(idx, MIN);
  }

  public double getVariableMax(int idx) {
    return table.getDouble(idx, MAX);
  }

  public double getVariablePrecision(int idx) {
    return table.getDouble(idx, PRECISION);
  }

  public double getVariableStringLength(int idx) {
    return table.getDouble(idx, STR_LEN);
  }

  public double getTotalStringLength() {
    double retVal = 0;
    int numVar = getNumVariables();
    for(int i = 0; i < numVar; i++) {
      retVal += getVariableStringLength(i);
    }
    return retVal;
  }

  public MutableTable createVariableNameTable() {
    if(variableNameTable != null)
      return variableNameTable;
    int numVars = getNumVariables();
    variableNameTable = new MutableTableImpl(numVars);

    for(int i = 0; i < numVars; i++) {
      variableNameTable.setColumn(new String[0], i);
      variableNameTable.setColumnLabel(getVariableName(i), i);
    }
    return variableNameTable;
  }
}