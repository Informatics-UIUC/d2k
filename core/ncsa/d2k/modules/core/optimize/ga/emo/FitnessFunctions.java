package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

import java.util.*;

public class FitnessFunctions {

  private MutableTable externalFunctions;

  private static final int NAME = 0;
  private static final int EXEC = 4;
  private static final int INPUT = 1;
  private static final int OUTPUT = 2;
  private static final int MAX_MIN = 3;

  private List fitnessVariables;
  private List fitnessFunctions;

  public FitnessFunctions() {
    externalFunctions = new MutableTableImpl(5);
    externalFunctions.setColumn(new String[0], 0);
    externalFunctions.setColumn(new String[0], 1);
    externalFunctions.setColumn(new String[0], 2);
    externalFunctions.setColumn(new boolean[0], 3);
    externalFunctions.setColumn(new String[0], 4);

    fitnessVariables = new ArrayList();
    fitnessFunctions = new ArrayList();
  }

  public int getNumFitnessFunctions() {
    return fitnessFunctions.size();
  }
  public int getNumFitnessVariables() {
    return fitnessVariables.size();
  }
  public void addFitnessVariable(Construction var) {
    fitnessVariables.add(var);
  }
  public void addFitnessFunction(FitnessFunctionConstruction ffc) {
    fitnessFunctions.add(ffc);
  }
  public Construction getFitnessVariable(int i) {
    return (Construction)fitnessVariables.get(i);
  }
  public String getFitnessVariableName(int i) {
    return getFitnessVariable(i).label;
  }
  public Construction getFitnessFunction(int i) {
    return (Construction)fitnessFunctions.get(i);
  }
  public String getFitnessFunctionName(int i) {
    return getFitnessFunction(i).label;
  }
  public boolean functionIsMinimizing(int i) {
    FitnessFunctionConstruction ffc = (FitnessFunctionConstruction)getFitnessFunction(i);
    return ffc.getIsMinimizing();
  }

  public void addExternFitnessFunction(String nm, String exec, String in, String out, boolean min) {
    Object[] row = new Object[5];
    row[NAME] = nm;
    row[INPUT] = in;
    row[OUTPUT] = out;
    row[MAX_MIN] = new Boolean(min);
    row[EXEC] = exec;
    externalFunctions.addRow(row);
  }
  public int getNumExternFitnessFunctions() {
    return externalFunctions.getNumRows();
  }
  public String getExternFitnessFunctionName(int i) {
    return externalFunctions.getString(i, NAME);
  }
  public String getExternFitnessFunctionInput(int i) {
    return externalFunctions.getString(i, INPUT);
  }
  public String getExternFitnessFunctionOutput(int i) {
    return externalFunctions.getString(i, OUTPUT);
  }
  public boolean getExternIsMinimizing(int i) {
    return externalFunctions.getBoolean(i, MAX_MIN);
  }
  public String getExternFitnessFunctionExec(int i) {
    return externalFunctions.getString(i, EXEC);
  }

  public int getTotalNumFitnessFunctions() {
    return fitnessFunctions.size() + externalFunctions.getNumRows();
  }
}