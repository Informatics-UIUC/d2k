package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * EMOGeneratePopulation
 */
public class EMOGeneratePopulation
    extends PopulationPrep
    implements Serializable {

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "EMOData";
      default:
        return "No such output";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        String s = "This is the default table model that will have ";
        s += "information regarding the decion variable bounds  ";
        s += "and precision.";
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
        return "Population";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        String s = "This is the updated mutable table that it received  ";
        s += "as an input. There are popsize number of rows in the table ";
        s += "and columns corresponding to the variables are filled";
        s += " according to the generated initial population";
        return s;
      case 1:
        s = "This is the initial population";
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

  //////////////////////////////////
  // The properties of this serializable module are the
  // parameters that are used to estimate population size.
  /////////////////////////////////

  ///////////////////////////
  // Getter and settter methods for properties
  ///////////////////////////
  /**
     This is the desired number of points user wants to see on the Pareto optimal front
   */
  /*public int NoOfDesiredPeaks = 5;

  public int getNoOfDesiredPeaks() {
    return this.NoOfDesiredPeaks;
  }

  /**
   */
  /*public void setNoOfDesiredPeaks(int nodp) {
    this.NoOfDesiredPeaks = nodp;
  }

  /**
     This is the problem size user wants to use
   */
  /*public int ProblemSize = 30;
  public void setProblemSize(int psize) {
    this.ProblemSize = psize;
  }*/

  /**
     get the population size.
     @returns the population size.
   */
  /*public int getProblemSize() {
    return this.ProblemSize;
  }*/

  /**
     This is the probability of maintaining the desired number of points on the Pareto optimal front
   */
  /*protected double ProbabilityOfMaintainingQNiches = 0.9;
  public void setProbabilityOfMaintainingQNiches(double pomqn) {
    this.ProbabilityOfMaintainingQNiches = pomqn;
  }

  public double getProbabilityOfMaintainingQNiches() {
    return this.ProbabilityOfMaintainingQNiches;
  }

  //protected double BestFitness = 0.00;
  //    public void setBestFitness (double targetFitness) {
  //  	this.BestFitness = targetFitness;
  //      }
  //public double getBestFitness () {
  //    return this.BestFitness;
  //}
  //protected double TargetFitness = 0.01;
  //     public void setTargetFitness (double targetFitness) {
  //  	this.TargetFitness = targetFitness;
  //      }
  //public double getTargetFitness () {
  //    return this.TargetFitness;
  //}

  protected int populationSize;
  public void thispopulationSize(int psize) {
    this.populationSize = psize;
  }

  /**
     get the population size.
     @returns the population size.
   */
  /*public int getpopulationSize() {
    return this.populationSize;
  }

  /** generation counter. */
  //private int maxGenerations;

  public void beginExecution() {
    firstRun = true;
    variableNames = null;

    // Calculate the number of generations for the GA run based on the properties set by the user.
    //CalculateAndSetNumberOfGenerations();

    // Calculate the population size for the GA run based on the properties set by the user.
    //CalculateAndSetPopulationSize();
  }

  private boolean firstRun;

  /**
     Calculate the number of generations for the GA run based on the properties set by the user.
   */
  /*public void CalculateAndSetNumberOfGenerations() {
    this.maxGenerations = 2 * (this.ProblemSize);
  }*/

  /**
     Calculate the population size for the GA run based on the properties set by the user. This is based on Patrick Reed proposal of using Mahfoud population sizing equation to multiobjective GAs.
   */

  public void CalculateAndSetPopulationSize() {
    /*double k2;
    double k1 = 1.0;
    double temp1;
    temp1 = Math.pow(this.maxGenerations, -1);
    temp1 = 1 - temp1;
    temp1 = Math.log(temp1);
    temp1 = ( -1) * temp1;
    k2 = temp1;
    this.populationSize = (int) (k1 * (this.NoOfDesiredPeaks) *
                                 (k2 + Math.log(this.NoOfDesiredPeaks)));
    this.populationSize /= 2;
        */


    // twice the number of non dominated fronts desired
    this.populationSize = 2*10;
  }

  private boolean createBinaryIndividuals = true;
  public void setCreateBinaryIndividuals(boolean b) {
    createBinaryIndividuals = b;
  }
  public boolean getCreateBinaryIndividuals() {
    return createBinaryIndividuals;
  }

  private EMOPopulationInfo popInfo;
  private MutableTable variableNames;

  public void doit() throws Exception {
    MutableTable bounds;
    MutableTable populationTbl;

    if(firstRun) {
      popInfo = (EMOPopulationInfo) pullInput(0);
      populationTbl = (MutableTable) popInfo.varNames;
      variableNames = (MutableTable)populationTbl.copy();

      bounds = (MutableTable) popInfo.boundsAndPrecision;
      firstRun = false;
      int stringLength = 0;
      for(int i = 0; i < bounds.getNumRows(); i++) {
        stringLength += bounds.getInt(i, bounds.getNumColumns()-1);
      }
      this.maxGenerations = 2*stringLength;
      System.out.println("MaxGen: "+maxGenerations);
      this.CalculateAndSetPopulationSize();
      //populationSize = 10;
      System.out.println("Pop Size: "+this.populationSize);
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
    //if(popInfo.getType() == EMOPopulationInfo.DOUBLE_TYPE) {
    if(!createBinaryIndividuals) {
      xyz = new DoubleRange[numOfvar];
      for (int i = 0; i < numOfvar; i++) {
        xyz[i] = new DoubleRange(bounds.getString(i, 0), bounds.getFloat(i, 2),
                                 bounds.getFloat(i, 1));
      }
    }
    //else if(popInfo.getType() == EMOPopulationInfo.BINARY_TYPE) {
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
    if(popInfo.getUseExternalFitnessEvaluation()) {
      Table functionTable = popInfo.externalFitnessInfo;
      int numFunctions = functionTable.getNumRows();
      ObjectiveConstraints[] oc = new ObjectiveConstraints[numFunctions];

      for(int i = 0; i < numFunctions; i++) {
        String name = functionTable.getString(i, 0);
          if(true) {
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
      pop.setPopulationInfo(popInfo);
    }
    else {
      // fitness variables of the problem
      EMOConstruction[] fitvarConstructions = popInfo.
          fitnessVariableConstructions;
      // fitness functions of the problem
      EMOConstruction[] fitnessFunctionConstructions = popInfo.
          fitnessFunctionConstructions;

      //number of objective functions
      int numFunctions = fitnessFunctionConstructions.length;

      // set up the fitness function
      ObjectiveConstraints[] oc = new ObjectiveConstraints[numFunctions];
      // if getmyflag() returns 0, the corresponding objective
      //function needs to be minimized
      // if getmyflag() returns 1, the corresponding objective
      //function needs to be maximized
      for (int i = 0; i < numFunctions; i++) {
        if (fitnessFunctionConstructions[i].getmyflag() == 0) {
          oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
              fitnessFunctionConstructions[i].getLabel(), 0.0, 100.0);
        }
        else {
          oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
              fitnessFunctionConstructions[i].getLabel(), 100.0, 0.0);
        }
      }

      //after getting information regarding the decision variables
      //and fitness function, generate the NSGA population.
      pop = new ConstrainedNsgaPopulation(xyz, oc,
          this.populationSize, this.getTargetFitness());
      pop.setPopulationInfo(popInfo);
      //pop.setFitnessVariables(fitvarConstructions);
//    pop.getPopulationInfo().fitnessVariableConstructions = fitvarConstructions;
      //pop.setFitnessFunctions(fitConstructions);
//    pop.getPopulationInfo().fitnessFunctionConstructions = fitnessFunctionConstructions;
      //pop.setConstraintVariables(popInfo.constraintVariableConstructions);
//    pop.getPopulationInfo().constraintVariableConstructions = popInfo.constraintVariableConstructions;
      //pop.setConstraintFunctions(popInfo.constraintFunctionConstructions);
//    pop.getPopulationInfo().constraintFunctionConstructions = popInfo.constraintFunctionConstructions;
      //pop.setAllVarNames(populationTbl);
//    pop.getPopulationInfo().varNames = variableNames;
      //set the maximum number of generation
      pop.setMaxGenerations(this.maxGenerations);

      // remove all columns that are created by EMOConstructions
      if (fitvarConstructions != null) {
        for (int i = 0; i < fitvarConstructions.length; i++) {
          removeColumn(variableNames, fitvarConstructions[i].getLabel());
        }
      }
      if (fitnessFunctionConstructions != null) {
        for (int i = 0; i < fitnessFunctionConstructions.length; i++) {
          removeColumn(variableNames, fitnessFunctionConstructions[i].getLabel());
        }
      }
      if (popInfo.constraintVariableConstructions != null) {
        for (int i = 0; i < popInfo.constraintVariableConstructions.length; i++) {
          removeColumn(variableNames,
                       popInfo.constraintVariableConstructions[i].getLabel());
        }
      }
      if (popInfo.constraintFunctionConstructions != null) {
        for (int i = 0; i < popInfo.constraintFunctionConstructions.length; i++) {
          removeColumn(variableNames,
                       popInfo.constraintFunctionConstructions[i].getLabel());
        }
      }
    }

    //update the third input, the mutable table,
    //by adding rows using the generated population
    //the columns of the mutable table corresponds to the
    //variables, fitness functions
    //the rows corresponds to the member of the population
    //therefore the number of rows equal to the the number of
    //members in the population
/*    populationTbl.setNumRows(populationSize);
    for (int i = 0; i < this.populationSize; i++) {
      double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
      float[] ftabrows = new float[populationTbl.getNumColumns()];
      for (int j = 0; j < numOfvar; j++) {
        ftabrows[j] = (float) tabrows[j];
      }
//      populationTbl.addRow(ftabrows);
      populationTbl.setRow(ftabrows, i);
    }

    pop.setTbl(populationTbl);*/
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