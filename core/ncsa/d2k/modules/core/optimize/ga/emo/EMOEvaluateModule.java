package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.transform.table.*;

/**
    Evaluate the new population. The population object does all the work,
    this module will simply invoke the <code>evaluateAll</code> method of the population.
 */
public class EMOEvaluateModule
    extends EvaluateModule {

  //////////////////////////////////
  // Type definitions.
  //////////////////////////////////

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
    return types;
  }

  //private int iiEval;
  //private ArrEMOConstruction arremoconstructions;
  //private ArrEMOConstruction arremoconstructions1;
  //private MutableTable newmt;

  //initialize the variables at the begining of the itinerary
  //public void beginExecution() {
    //iiEval = 1;
  //}

  // reset the value of variables at the end of itinerary
  //public void endExecution() {
    //int iiEval = 1;
  //}

  /*public boolean isReady() {
    if ( (newmt == null) && (inputFlags[0] > 0)) {
      return true;
    }
    if ( (arremoconstructions == null) && (inputFlags[1] > 0)) {
      return true;
    }
    if ( (arremoconstructions1 == null) && (inputFlags[2] > 0)) {
      return true;
    }
    else if (inputFlags[3] > 0) {
      return true;
    }
    return false;
  }*/

  /**
     Do the evaluation, using the evaluate function provided by the population
     subclass we received.
     @param outV the array to contain output object.
   */
  public void doit() throws Exception {
    /*if (newmt == null) {
      newmt = (MutableTable) pullInput(0);
      return;
    }
    else if (arremoconstructions == null) {
      arremoconstructions = (ArrEMOConstruction)this.pullInput(1);
      return;
    }
    else if (arremoconstructions1 == null) {
      arremoconstructions1 = (ArrEMOConstruction)this.pullInput(2);
      return;
    }*/
    //else {
    {

      // the mutable containing the population
      // MutableTable newmttemp = (MutableTable) this.pullInput(0);
//	if(iiEval ==1){
//	    newmt = newmttemp;
//	}

      // the data structure containing the fitness function information
//	ArrEMOConstruction arremoconstructionstemp1 = (ArrEMOConstruction)this.pullInput(1);
      // if this is the first time the module is executed
      // the incoming inputs are from the GUI and
      //the correct input to be used and hence use them
//	if(iiEval ==1){
//	    arremoconstructions = arremoconstructionstemp1;
//	}
      //data structure containing the constraints information
//	ArrEMOConstruction arremoconstructionstemp2 = (ArrEMOConstruction)this.pullInput(2);
      // if this is the first time the module is executed
      // the incoming inputs are from the GUI and
      //the correct input to be used and hence use them
//	if(iiEval ==1){
//	    arremoconstructions1 = arremoconstructionstemp2;
//	}
      // increase the module execution counter
      //iiEval++;
      // the current population of the GA run
      NsgaPopulation pop = (NsgaPopulation)this.pullInput(0);

      MutableTable newmt = pop.getTbl();//pop.getAllVarNames();

      int numOfvar = pop.getNumGenes();
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          newmt.setFloat( (float) tabrows[j], i, j);
        }
      }

      //extract the fitness function variables information
      //Construction[] fitvarConstructions = arremoconstructions.
      //    getVarConstructions();
      EMOConstruction[] fitvarConstructions = pop.getFitnessVariables();

      //extract the fitness function information
      //Construction[] fitConstructions = arremoconstructions.
      //    getFuncConstructions();
      EMOConstruction[] fitConstructions = pop.getFitnessFunctions();

      // update the mutable table by calculating the
      //fitness function varaibles
      for (int i = 0; i < fitvarConstructions.length; i++) {
        int fitvarpos = 0;
        while ( ( (fitvarConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitvarpos)) != 0) {
          fitvarpos++;
        }
        //fitpos has the column number of the corresponding
        //fitness function variable
        //remove that row
        newmt.removeColumn(fitvarpos);
        //create a column transform for the corresponding
        //fitness function variable
        //ColumnTransformation myvarct = new ColumnTransformation(
        AttributeTransform myvarct = new AttributeTransform(
            new Object[]{fitvarConstructions[i]});
        //apply the transformation
        // It is important to note that transform function adds
        // a new column to the table and hence we deleted the column
        // corresponding to the fitness function before transforming
        myvarct.transform(newmt);
      }

      // update the mutable table by calculating the
      //fitness function
      for (int i = 0; i < fitConstructions.length; i++) {
        int fitpos = 0;
        while ( ( (fitConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitpos)) != 0) {
          fitpos++;
        }
        //fitpos has the column number of the corresponding
        //fitness function
        //remove that row
        newmt.removeColumn(fitpos);
        //create a column transform for the corresponding
        //fitness function
        //ColumnTransformation myfitct = new ColumnTransformation(
        AttributeTransform myfitct = new AttributeTransform(
            new Object[]{fitConstructions[i]});
        //apply the transformation
        // It is important to note that transform function adds
        // a new column to the table and hence we deleted the column
        // corresponding to the fitness function before transforming
        myfitct.transform(newmt);
        // after updating the mutable table, we update the
        //population by filling in the corresponding fitness
        //function values into it.
        for (int mynewii = 0; mynewii < pop.size(); mynewii++) {
          Individual mymember = pop.getMember(i);
          MONumericIndividual myni = (MONumericIndividual) mymember;
          myni.setObjective(i, newmt.getFloat(mynewii, fitpos));
        }

      }

      //extract the fitness constraints variables information
      //Construction[] fitvarConstructions1 = arremoconstructions1.
      //    getVarConstructions();
      EMOConstruction[] constraintVarConstructions = pop.getConstraintVariables();

      //extract the fitness constraints information
      //Construction[] fitConstructions1 = arremoconstructions1.
      //    getFuncConstructions();
      EMOConstruction[] constraintVariableConstructions = pop.getConstraintFunctions();

      // update the mutable table by calculating the
      //constraint varaibles
      for (int i = 0; i < constraintVarConstructions.length; i++) {
        int fitvarpos = 0;
        while ( ( (constraintVarConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitvarpos)) != 0) {
          fitvarpos++;
        }
        //fitpos has the column number of the corresponding
        //constraint variable
        //remove that row
        newmt.removeColumn(fitvarpos);
        //create a column transform for the corresponding
        //fitness function
        //ColumnTransformation myvarct = new ColumnTransformation(
        AttributeTransform myvarct = new AttributeTransform(
            new Object[]{constraintVarConstructions[i]});
        //apply the transformation
        // It is important to note that transform function adds
        // a new column to the table and hence we deleted the column
        // corresponding to the constraint variable before transforming
        myvarct.transform(newmt);
      }

      // update the mutable table by calculating the
      //constraints
      for (int i = 0; i < constraintVariableConstructions.length; i++) {
        int fitpos = 0;
        while ( ( (constraintVariableConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitpos)) != 0) {
          fitpos++;
        }
        //fitpos has the column number of the corresponding
        //constraint
        //remove that row
        newmt.removeColumn(fitpos);
        //create a column transform for the corresponding
        //constraint
        EMOFilter myfitct = new EMOFilter(
            constraintVariableConstructions[i]);
        //apply the transformation
        // It is important to note that transform function adds
        // a new column to the table and hence we deleted the column
        // corresponding to the constraint before transforming
        myfitct.transform(newmt);

      }
      ((ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl)newmt).print();
      //this is the array of integers that contain the column
      // number of the constraints
      int[] myconstraintpos = new int[constraintVariableConstructions.length];
      for (int i = 0; i < constraintVariableConstructions.length; i++) {
        int fitpos = 0;
        while ( ( (constraintVariableConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitpos)) != 0) {
          fitpos++;
        }
        myconstraintpos[i] = fitpos;
      }
      //myconstraintpos now contains the column number of the constraints
      float constrvalue = 0;
      //calculate the constraint for each member
      // and update the population accordingly
      for (int mynewii = 0; mynewii < pop.size(); mynewii++) {
        constrvalue = 0;
        for (int iijj = 0; iijj < constraintVariableConstructions.length; iijj++) {
          constrvalue += newmt.getFloat(mynewii, myconstraintpos[iijj]);
        }
        Individual mymember = pop.getMember(mynewii);
        MONumericIndividual myni = (MONumericIndividual) mymember;
        ( (NsgaSolution) myni).setConstraint(constrvalue);
      }
      //push the output
      //this.pushOutput(newmt, 0);
      //this.pushOutput(pop, 1);
      this.pushOutput(pop, 0);
    }
  }

  /**
     Compute the fitness for a single individual.
     @param ind the member to compute the fitness of.
   */
  public void evaluateIndividual(Individual memb) {}

  private class EMOFilter implements Transformation {
      // this is the construction that contains
      // the mathematical expression and the name of varaible
      private Object constructions;

      public EMOFilter(Object constructions) {
          this.constructions = constructions;
      }
      // this function transforms the mutable table passed to it
      // using the construction belonging to this class
      public boolean transform(MutableTable table) {
          // table is the mutable table to be transformed
          // create a filter expression for the table
          FilterExpression exp = new FilterExpression(table);
          // create a construction
          Construction current = (Construction)constructions;
          //this will contain the evaluated values of the expression
          Object evaluation = null;
          // an exception will be thrown if the expression is
          // not fit for the table
          try {
              exp.setExpression(current.expression);
              evaluation = exp.evaluate();
          }
          catch(Exception e) {
              e.printStackTrace();
              return false;
              // return false if failed to transform
          }
          // if expression is fit for the table then transform the table
          boolean[] b = (boolean[]) evaluation;
          float[] myf = new float[b.length];
          for(int myi =0; myi <b.length;myi++){
              if(b[myi]==true) myf[myi]=0;
              else myf[myi]=1;
          }
          table.addColumn(myf);
          // set the column label
          table.setColumnLabel(current.label, table.getNumColumns() - 1);
          // table is successfully transformed and hence return true
          return true;

      }

  }

}
