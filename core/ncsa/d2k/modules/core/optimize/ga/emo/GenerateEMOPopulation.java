package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * EMOGeneratePopulation
 */
public class GenerateEMOPopulation
    extends PopulationPrep
    implements Serializable {

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[6];

    pds[0] = new PropertyDescription(
        "nonDominatedFronts",
        "Number of nondominated solutions",
        "The number of nondominated solutions desired");

    pds[1] = new PropertyDescription(
        "useRecommendedPopSize",
        "Use Recommended Population Size",
        "Use the recommended population size");

    pds[2] = new PropertyDescription(
        "populationSize",
        "Overridden population size",
        "When the recommended population size is not used, use this population size");

    pds[3] = new PropertyDescription(
        "useRecommendedNumGenerations",
        "Use the recommended number of generations",
        "Use the recommended number of generations");

    pds[4] = new PropertyDescription(
        "maxGenerations",
        "Overridden number of generations",
        "When the recommended number of generations is not used, use this number of generations");

    pds[5] = new PropertyDescription(
        "createBinaryIndividuals",
        "Create binary individuals",
        "Create binary individuals if true, create real-coded individuals if false");

    return pds;
  }

  private int nonDominatedFronts = 50;
  public void setNonDominatedFronts(int i) {
    nonDominatedFronts = i;
  }
  public int getNonDominatedFronts() {
    return nonDominatedFronts;
  }

  private boolean useRecommendedPopSize = true;
  public void setUseRecommendedPopSize(boolean b) {
    useRecommendedPopSize = b;
  }
  public boolean getUseRecommendedPopSize() {
    return useRecommendedPopSize;
  }

  private boolean useRecommendedNumGenerations = true;
  public void setUseRecommendedNumGenerations(boolean b) {
    useRecommendedNumGenerations = b;
  }
  public boolean getUseRecommendedNumGenerations() {
    return useRecommendedNumGenerations;
  }

  public int getMaxGenerations() {
    return this.maxGenerations;
  }
  public void setMaxGenerations(int i) {
    maxGenerations = i;
  }

  private boolean createBinaryIndividuals = true;
  public void setCreateBinaryIndividuals(boolean b) {
    createBinaryIndividuals = b;
  }
  public boolean getCreateBinaryIndividuals() {
    return createBinaryIndividuals;
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Population Info";
      default:
        return "No such output";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        String s = "This is a data structure containing all the information "+
          "about the decision variables, fitness functions, and constraints.";
        return s;
      default:
        return "No such Input";
    }
  }

  /**
     This method returns an array of strings that contains the data types for the inputs.
     @return the data types of all inputs.
   */
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo"};
    return types;
  }

  /**
     This method returns the description of the outputs.
     @return the description of the indexed output.
   */
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "population";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        String s = "This is the initial population ";
        s += "generated for the GA run ";
        return s;
      default:
        return "No such output";
    }
  }

  /**
     This method returns an array of strings that contains the data types for the outputs.
     @return the data types of all outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
    return types;
  }

  /**
          This method returns the description of the module.
          @return the description of the module.
  */
  public String getModuleInfo () {
          return "This module will produce a population for input to the genetic algorithm.";
  }

  public void beginExecution() {
    firstRun = true;
    variableNames = null;
  }

  public void endExecution() {
    firstRun = true;
    variableNames = null;
    popInfo = null;
  }

  private boolean firstRun;
  private EMOPopulationInfo popInfo;
  private MutableTable variableNames;

  public void doit() throws Exception {
    MutableTable bounds;
    MutableTable populationTbl;

    if(firstRun) {
      popInfo = (EMOPopulationInfo) pullInput(0);
      populationTbl = (MutableTable) popInfo.varNames;
      variableNames = (MutableTable)populationTbl.copy();

      if(this.getUseRecommendedPopSize())
        this.populationSize = 2* this.getNonDominatedFronts();

      bounds = (MutableTable) popInfo.boundsAndPrecision;
      firstRun = false;

      if(this.getUseRecommendedNumGenerations()) {
        int stringLength = 0;
        for (int i = 0; i < bounds.getNumRows(); i++) {
          stringLength += bounds.getInt(i, bounds.getNumColumns() - 1);
        }
        this.maxGenerations = 2 * stringLength;
      }
    }
    else {
      // consume the dummy input
      Object o = pullInput(0);
      bounds = (MutableTable)popInfo.boundsAndPrecision;
      populationTbl = (MutableTable)variableNames.copy();
      // Double the population size
      this.populationSize = 2 * (this.populationSize);
    }

    // the number of variables in the encoded problem
    int numOfvar = bounds.getNumRows();

    // generate DoubleRange for variables
    //use the first input, the default table model, to get the
    //information about decision variables of the problem
    Range[] xyz;
    if(!createBinaryIndividuals) {
      xyz = new DoubleRange[numOfvar];
      for (int i = 0; i < numOfvar; i++) {
        xyz[i] = new DoubleRange(bounds.getString(i, 0), bounds.getFloat(i, 2),
                                 bounds.getFloat(i, 1));
      }
    }
    else if(createBinaryIndividuals) {
      xyz = new BinaryRange[numOfvar];
      for(int i = 0; i < numOfvar; i++) {
        xyz[i] = new BinaryRange("x", bounds.getInt(i, 4));
      }
    }
    else
      throw new Exception("Could not determine population type.");

    NsgaPopulation pop = null;

    // if we are using an external fitness eval..
    if(popInfo.useExternalFitnessEvaluation) {
      Table functionTable = popInfo.externalFitnessInfo;
      int numFunctions = functionTable.getNumRows();
      ObjectiveConstraints[] oc = new ObjectiveConstraints[numFunctions];

      for(int i = 0; i < numFunctions; i++) {
        String name = functionTable.getString(i, 0);
          if(functionTable.getString(i, 4).equals("Minimize")) {
            oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
                name, 0.0, 100.0);
          }
          else {
            oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
                name, 100.0, 0.0);
          }
      }
      pop = new ConstrainedNsgaPopulation(xyz, oc,
           this.populationSize, this.getTargetFitness());
      pop.setMaxGenerations(this.maxGenerations);
      pop.setPopulationInfo(popInfo);
    }
    else {
      // fitness variables of the problem
      Construction[] fitvarConstructions = popInfo.
          fitnessVariableConstructions;
      // fitness functions of the problem
      Construction[] fitnessFunctionConstructions = popInfo.
          fitnessFunctionConstructions;

      //number of objective functions
      int numFunctions = fitnessFunctionConstructions.length;

      // set up the fitness function
      ObjectiveConstraints[] oc = new ObjectiveConstraints[numFunctions];

      for (int i = 0; i < numFunctions; i++) {
        if (((FitnessFunctionConstruction)fitnessFunctionConstructions[i]).getIsMinimizing()) {
          oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
              fitnessFunctionConstructions[i].label, 0.0, 100.0);
        }
        else {
          oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
              fitnessFunctionConstructions[i].label, 100.0, 0.0);
        }
      }

      //after getting information regarding the decision variables
      //and fitness function, generate the NSGA population.
      pop = new ConstrainedNsgaPopulation(xyz, oc,
          this.populationSize, this.getTargetFitness());
      pop.setPopulationInfo(popInfo);
      //set the maximum number of generation
      pop.setMaxGenerations(this.maxGenerations);
      // remove all columns that are created by EMOConstructions

      if (fitvarConstructions != null) {
        for (int i = 0; i < fitvarConstructions.length; i++) {
          removeColumn(variableNames, fitvarConstructions[i].label);
        }
      }
      if (fitnessFunctionConstructions != null) {
        for (int i = 0; i < fitnessFunctionConstructions.length; i++) {
          removeColumn(variableNames, fitnessFunctionConstructions[i].label);
        }
      }
      if (popInfo.constraintVariableConstructions != null) {
        for (int i = 0; i < popInfo.constraintVariableConstructions.length; i++) {
          removeColumn(variableNames,
                       popInfo.constraintVariableConstructions[i].label);
        }
      }
      if (popInfo.constraintFunctionConstructions != null) {
        for (int i = 0; i < popInfo.constraintFunctionConstructions.length; i++) {
          removeColumn(variableNames,
                       popInfo.constraintFunctionConstructions[i].label);
        }
      }
    }

    this.pushOutput( (Population) pop, 0);
  }

  private void removeColumn(MutableTable t, String lbl) {
    for(int i = 0; i < t.getNumColumns(); i++) {
      String colLabel = t.getColumnLabel(i);
      if(colLabel.equals(lbl)) {
        t.removeColumn(i);
        return;
      }
    }
  }
}