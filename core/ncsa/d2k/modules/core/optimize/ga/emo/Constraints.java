package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.util.*;

public class Constraints {

  private MutableTable externalFunctions;

  private static final int NAME = 0;
  private static final int EXEC = 4;
  private static final int INPUT = 1;
  private static final int OUTPUT = 2;
  private static final int WEIGHT = 3;

  private List constraintVariables;
  private List constraintFunctions;

  public Constraints() {
    externalFunctions = new MutableTableImpl(5);
    externalFunctions.setColumn(new String[0], 0);
    externalFunctions.setColumn(new String[0], 1);
    externalFunctions.setColumn(new String[0], 2);
    externalFunctions.setColumn(new double[0], 3);
    externalFunctions.setColumn(new String[0], 4);

    constraintVariables = new ArrayList();
    constraintFunctions = new ArrayList();
  }

  public int getNumConstraintFunctions() {
    return constraintFunctions.size();
  }
  public int getNumConstraintVariables() {
    return constraintVariables.size();
  }
  public void addConstraintVariable(Construction var) {
    constraintVariables.add(var);
  }
  public void addConstraintFunction(Construction ffc) {
    constraintFunctions.add(ffc);
  }
  public Construction getConstraintVariable(int i) {
    return (Construction)constraintVariables.get(i);
  }
  public String getConstraintVariableName(int i) {
    return getConstraintVariable(i).label;
  }
  public Construction getConstraintFunction(int i) {
    return (Construction)constraintFunctions.get(i);
  }
  public String getConstraintFunctionName(int i) {
    return getConstraintFunction(i).label;
  }

  public void addExternConstraint(String nm, String exec, String in, String out, double w) {
    Object[] row = new Object[5];
    row[0] = nm;
    row[1] = in;
    row[2] = out;
    row[3] = new Double(w);
    row[4] = exec;
    externalFunctions.addRow(row);
  }
  public int getNumExternConstraints() {
    return externalFunctions.getNumRows();
  }
  public String getExternConstraintName(int i) {
    return externalFunctions.getString(i, NAME);
  }
  public String getExternConstraintInput(int i) {
    return externalFunctions.getString(i, INPUT);
  }
  public String getExternConstraintOutput(int i) {
    return externalFunctions.getString(i, OUTPUT);
  }
  public String getExternConstraintExec(int i) {
    return externalFunctions.getString(i, EXEC);
  }

  public int getTotalNumConstriants() {
    return constraintFunctions.size() + externalFunctions.getNumRows();
  }
}