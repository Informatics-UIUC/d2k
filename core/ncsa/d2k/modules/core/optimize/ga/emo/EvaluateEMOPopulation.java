package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.transform.table.*;

/**
    Evaluate the new population. The population object does all the work,
    this module will simply invoke the <code>evaluateAll</code> method of the population.
 */
public class EvaluateEMOPopulation
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

  private MutableTable table;
  private NsgaPopulation population;
  private int numCols;

  public void endExecution() {
    table = null;
    population = null;
  }

  /**
     Do the evaluation, using the evaluate function provided by the population
     subclass we received.
     @param outV the array to contain output object.
   */
  public void doit() throws Exception {
      // the current population of the GA run
      NsgaPopulation pop = (NsgaPopulation)this.pullInput(0);
      if(population == null || pop != population) {
        table = (MutableTable) pop.getPopulationInfo().varNames.copy();
        table.setNumRows(pop.size());
        numCols = table.getNumColumns();
        population = pop;
      }

      Individual[] individuals = pop.getMembers();
      boolean binaryType = false;
      if(individuals.length > 0) {
        if(individuals[0] instanceof MOBinaryIndividual)
          binaryType = true;
        else
          binaryType = false;
      }

      int numOfvar = pop.getNumGenes();
      if(!binaryType) {
        for (int i = 0; i < pop.size(); i++) {
          double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
          for (int j = 0; j < numOfvar; j++) {
            table.setDouble(tabrows[j], i, j);
          }
        }
      }
      // do the decoding so that the entries copied into the table are
      // real-valued
      else {
          int numTraits = 0;
          BinaryRange[] traits = (BinaryRange[]) pop.getTraits();

          // for each individual
          for (int j = 0; j < pop.size(); j++) {
            double[] genes = ( (MOBinaryIndividual) pop.getMember(j)).toDouble();

            // the max and precision are contained in the boundsAndPrecision table
            Table bounds = ( (NsgaPopulation) pop).getPopulationInfo().boundsAndPrecision;

            int curPos = 0;
            for (int k = 0; k < traits.length; k++) {
              int numBits = traits[k].getNumBits();
              double num = 0.0d;
              double max = bounds.getDouble(k, 2);
              double min = bounds.getDouble(k, 1);
              double precision = bounds.getDouble(k, 3);

              double interval = (max - min) * precision;

              // this is one trait
              for (int l = 0; l < numBits; l++) {
                if (genes[curPos] != 0) {
                  num = num + Math.pow(2.0, l);
                }
                curPos++;
              }

              // if it is above the max, scale it down
              num = num * precision + min;
              if (num > max) {
                num = max;
              }
              if (num < min) {
                num = min;
              }
              table.setDouble(num, j, k);
            }
          }
      }

      //extract the fitness function variables information
      Construction[] fitvarConstructions = pop.getPopulationInfo().fitnessVariableConstructions;

      //extract the fitness function information
      Construction[] fitConstructions = pop.getPopulationInfo().fitnessFunctionConstructions;

      // update the mutable table by calculating the
      //fitness function varaibles
      for (int i = 0; i < fitvarConstructions.length; i++) {
        AttributeTransform myvarct = new AttributeTransform(
            new Object[]{fitvarConstructions[i]});
        //apply the transformation
        myvarct.transform(table);
      }

      // update the mutable table by calculating the
      // fitness function
      for (int i = 0; i < fitConstructions.length; i++) {
        AttributeTransform myfitct = new AttributeTransform(
            new Object[]{fitConstructions[i]});
        //apply the transformation
        boolean retVal = myfitct.transform(table);
        if(!retVal)
          throw new Exception("Couldn't transform.");
        int column = table.getNumColumns()-1;

        // after updating the mutable table, we update the
        // population by filling in the corresponding fitness
        // function values into it.
        for (int mynewii = 0; mynewii < pop.size(); mynewii++) {
          Individual mymember = pop.getMember(mynewii);
          MOSolution myni = (MOSolution) mymember;
          myni.setObjective(i, table.getFloat(mynewii, column));
        }
      }

      //extract the fitness constraints variables information
      Construction[] constraintVarConstructions = pop.getPopulationInfo().constraintVariableConstructions;

      //extract the fitness constraints information
      Construction[] constraintVariableConstructions = pop.getPopulationInfo().constraintFunctionConstructions;

      // update the mutable table by calculating the
      // constraint varaibles
      for (int i = 0; i < constraintVarConstructions.length; i++) {
        AttributeTransform myvarct = new AttributeTransform(
            new Object[]{constraintVarConstructions[i]});
        //apply the transformation
        myvarct.transform(table);
      }

      // update the mutable table by calculating the constraints
      for (int i = 0; i < constraintVariableConstructions.length; i++) {
        /*int fitpos = 0;
        while ( ( (constraintVariableConstructions[i]).getLabel()).compareTo(newmt.
            getColumnLabel(fitpos)) != 0) {
          fitpos++;
        }*/
        //fitpos has the column number of the corresponding
        //constraint
        //remove that row
        //newmt.removeColumn(fitpos);
        //create a column transform for the corresponding
        //constraint
        EMOFilter myfitct = new EMOFilter(
            constraintVariableConstructions[i]);
        //apply the transformation
        // It is important to note that transform function adds
        // a new column to the table and hence we deleted the column
        // corresponding to the constraint before transforming
        myfitct.transform(table);
      }

      //this is the array of integers that contain the column
      // number of the constraints
      int[] myconstraintpos = new int[constraintVariableConstructions.length];
      for (int i = 0; i < constraintVariableConstructions.length; i++) {
        int fitpos = 0;
        while ( ( (constraintVariableConstructions[i]).label).compareTo(table.
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
          constrvalue += table.getFloat(mynewii, myconstraintpos[iijj]);
        }
        Individual mymember = pop.getMember(mynewii);
        ( (NsgaSolution) mymember).setConstraint(constrvalue);
      }

      // now remove the added columns so we can reuse the table again
      int newNumCols = table.getNumColumns();
      for(int i = numCols; i < newNumCols; i++) {
        table.removeColumn(numCols);
      }

      this.pushOutput(pop, 0);
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
