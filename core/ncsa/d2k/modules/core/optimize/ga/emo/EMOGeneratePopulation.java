package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 PopulationPrep.java
 */
public class EMOGeneratePopulation
    extends PopulationPrep
    implements Serializable {

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "variable information";
      case 1:
        return "function information";
      case 2:
        return "population table";
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
      case 1:
        s = "This data structure contains information ";
        s += "regarding which fitness function";
        s += "has to minimized or maximized";
        return s;
      case 2:
        s = "This is the mutable table with no rows.";
        s += "The columns of the mutable table corresponds to";
        s += "variables, fitness functions and constraints of the problem.";
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
    //String [] types =  {"javax.swing.table.DefaultTableModel","ncsa.d2k.modules.core.transform.attribute.ArrEMOConstruction","ncsa.d2k.modules.core.transform.attribute.ArrEMOConstruction", "ncsa.d2k.modules.core.datatype.table.MutableTable"};
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
        return "Population table";
      case 1:
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
    //String [] types =  {"ncsa.d2k.modules.core.datatype.table.MutableTable","ncsa.d2k.modules.core.optimize.ga.Population"};
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
    return types;
  }

  /**
     This method returns the description of the module.
     @return the description of the module.
   */
  /*public String getModuleInfo () {
      String s ="<p>Overview: ";
      s +="This module generates the initial population";
      s +="for the GA run.";
      s +="</p><p>Detailed Description: ";
      s +="This module generates the initial population for the GA run.";
      s +="It can be called either right after user finishes filling all the ";
      s +="required information in the GUI or after user makes a ";
      s +="decision to have another run. The various properties of ";
       s +="this method are to be set by the user and based on these properties";
      s +=" an estimate of required population size is made using Mahfoud";
      s +="population sizing model for multimodal functions.";
      s +="</p><p>Data Type Restrictions:";
      s +="The variables of the problems are assumed to be of float";
       s +="Hence the interactive GA problem encoding might not work in this case";
      s +="</p>";
      return s;
       }*/

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

  public int NoOfDesiredPeaks = 5;

  public int getNoOfDesiredPeaks() {
    return this.NoOfDesiredPeaks;
  }

  /**
   */
  public void setNoOfDesiredPeaks(int nodp) {
    this.NoOfDesiredPeaks = nodp;
  }

  /**
     This is the problem size user wants to use
   */

  public int ProblemSize = 30;
  public void setProblemSize(int psize) {
    this.ProblemSize = psize;
  }

  /**
     get the population size.
     @returns the population size.
   */
  public int getProblemSize() {
    return this.ProblemSize;
  }

  /**
     This is the probability of maintaining the desired number of points on the Pareto optimal front
   */
  protected double ProbabilityOfMaintainingQNiches = 0.9;
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

//    protected DefaultTableModel model;
//    protected ArrEMOConstruction arremoconstructions;
//    protected ArrEMOConstruction constraints;
//    protected MutableTable table;
  public int populationSize;
  public void thispopulationSize(int psize) {
    this.populationSize = psize;
  }

  /**
     get the population size.
     @returns the population size.
   */
  public int getpopulationSize() {
    return this.populationSize;
  }

  /** generation counter. */
  public int maxGenerations;

  /*&%^8 Do not modify this section. */
  /*#end^8 Continue editing. ^#&*/

  /**
     This variable keeps track of the run. flagRun is 1 at the first run and information needs to be extracted from the GUI.
   */
//    int flagRun;
  public void beginExecution() {
//        flagRun =1;
//    model =null;
//    arremoconstructions =null;

    /**
       Calculate the number of generations for the GA run based on the properties set by the user.
     */
    CalculateAndSetNumberOfGenerations();
    /**
       Calculate the population size for the GA run based on the properties set by the user.
     */
    CalculateAndSetPopulationSize();

  }

  /**
     Reset the flagRun at the end of the itinerary.
   */
//    public void endExecution(){
//        int flagRun=1;}

  /**
     Calculate the number of generations for the GA run based on the properties set by the user.
   */
  public void CalculateAndSetNumberOfGenerations() {
    this.maxGenerations = 2 * (this.ProblemSize);
  }

  /**
     Calculate the population size for the GA run based on the properties set by the user. This is based on Patrick Reed proposal of using Mahfoud population sizing equation to multiobjective GAs.
   */

  public void CalculateAndSetPopulationSize() {

    double k2;
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
  }

  /**
       Takes input from the GUI (in case of the first run) or the display front module
     (in case user decides to make another run) and generates NSGA population.
   */
  /*public boolean isReady(){
   if((model ==null)&&(inputFlags[0] >0)) return true;
   else if((arremoconstructions ==null)&&(inputFlags[1] >0)) return true;
   else if(inputFlags[2] >0) return true;
   return false;
   }*/

  public void doit() throws Exception {
    EMOPopulationInfo popInfo = (EMOPopulationInfo) pullInput(0);

    MutableTable temptabnew = (MutableTable) popInfo.varNames;
//      MutableTable table = (MutableTable)popInfo.boundsAndPrecision;
    MutableTable model = (MutableTable) popInfo.boundsAndPrecision;

//if(model ==null){
//model = (DefaultTableModel)pullInput(0);
//System.out.println("model initialized");
// return;
//}
//else if(arremoconstructions ==null){
//arremoconstructions = (ArrEMOConstruction)pullInput(1);
//System.out.println("arrEmoconstructions initialized");
//constraints = (ArrEMOConstruction)pullInput(2);
//return;
//} else {
    //DefaultTableModel tempdftm1 = (DefaultTableModel) this.pullInput(0);
    //ArrEMOConstruction arremoconstructionstemp = (ArrEMOConstruction) this.pullInput(1);
//        MutableTable temptabnew = (MutableTable) this.pullInput(3);
//System.out.println("muatble table pulled as an input");
//System.out.println("FlagRun "+flagRun);

    // if this is the first run use the input
    // to set class variables.
    /*        if(flagRun ==1){
              //  model = tempdftm1;
               // arremoconstructions = arremoconstructionstemp;
                table = temptabnew;
            }*/
//        flagRun++;
    // Double the population size
    this.populationSize = 2 * (this.populationSize);
    // the number of variables in the encoded problem
    //int numOfvar = model.getRowCount();
    int numOfvar = model.getNumRows();
    // generate DoubleRange for variables
    //use the first input, the default table model, to get the
    //information about decision variables of the problem
    DoubleRange[] xyz = new DoubleRange[numOfvar];
    for (int i = 0; i < numOfvar; i++) {
      //xyz[i] = new DoubleRange( (String)(model.getValueAt(i,1)),(Float.parseFloat((String)(model.getValueAt(i,3)))) ,(Float.parseFloat((String)(model.getValueAt(i,2)))) );
      xyz[i] = new DoubleRange(model.getString(i, 1), model.getFloat(i, 3),
                               model.getFloat(i, 2));
    }
    // use the second input, to set the information about
    //fitness function of the problem
    //EMOConstruction[] fitvarConstructions = arremoconstructions.getVarConstructions();
    EMOConstruction[] fitvarConstructions = popInfo.
        fitnessVariableConstructions;
    //EMOConstruction[] fitConstructions = arremoconstructions.getFuncConstructions();
    EMOConstruction[] fitConstructions = popInfo.fitnessFunctionConstructions;
    //number of objective functions
    int numOfFunc = fitConstructions.length;
    // set up the fitness function
    ObjectiveConstraints oc[] = new ObjectiveConstraints[numOfFunc];
    // if getmyflag() returns 0, the corresponding objective
    //function needs to be minimized
    // if getmyflag() returns 1, the corresponding objective
    //function needs to be maximized
    for (int i = 0; i < numOfFunc; i++) {
      if (fitConstructions[i].getmyflag() == 0) {
        oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            fitConstructions[i].getLabel(), 0.0, 100.0);
      }
      else {
        oc[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            fitConstructions[i].getLabel(), 100.0, 0.0);

      }
    }
    //after getting information regarding the decision variables
    //and fitness function, generate the NSGA population.
    NsgaPopulation pop = new ConstrainedNsgaPopulation(xyz, oc,
        this.populationSize, this.getTargetFitness());
    pop.setFitnessVariables(fitvarConstructions);
    pop.setFitnessFunctions(fitConstructions);
    //pop.setConstraintVariables(constraints.getVarConstructions());
    pop.setConstraintVariables(popInfo.constraintVariableConstructions);
    //pop.setConstraintFunctions(constraints.getFuncConstructions());
    pop.setConstraintFunctions(popInfo.constraintFunctionConstructions);
    pop.setAllVarNames(temptabnew);
    //set the maximum number of generation
    pop.setMaxGenerations(this.maxGenerations);
    //update the third input, the mutable table,
    //by aading rows using the generated population
    //the columns of the mutable table corresponds to the
    //variables, fitness functions
    //the rows corresponds to the member of the population
    //therefore the number of rows equal to the the number of
    //members in the population
    for (int i = 0; i < this.populationSize; i++) {
      double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
      float[] ftabrows = new float[temptabnew.getNumColumns()];
      for (int j = 0; j < numOfvar; j++) {
        ftabrows[j] = (float) tabrows[j];
      }
      temptabnew.addRow(ftabrows);
    }
    // push the output, mutable table
    // that contains the generated member values
    //but theses members are not yet evaluated
    //this.pushOutput ((MutableTable)table, 0);
    //push the output, population,
    //the population is not yet evaluated
    //this.pushOutput ((Population)pop, 1);
    //for(int i = 0; i < temptabnew.getNumColumns(); i++)
    //  System.out.println("GenPop Table: "+table.getColumnLabel(i));
    //for(int i = 0; i < temptabnew.getNumColumns(); i++)
    //  System.out.println("GenPop TempTab: "+temptabnew.getColumnLabel(i));

    pop.setTbl(temptabnew);
    this.pushOutput( (Population) pop, 0);
  }
//    }

}