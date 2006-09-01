/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.beans.PropertyVetoException;
import java.util.Random;


/**
 * NFoldTrainTest (previously NFoldTTest then NFoldCrossValidation) Takes in a
 * single table, makes N ExampleTables that have different, exhaustive subsets
 * set to the trainSet and testSet, but pushes out a TrainTable and TestTable
 * made from the ExampleTable.
 *
 * @author  Peter Groves w/ much cut and paste from Tom Redman's code;
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class NFoldTrainTest extends ncsa.d2k.core.modules.DataPrepModule {

   //~ Instance fields *********************************************************

   /** the break points. */
   protected int[] breaks = null;

   /** true if in debug mode. */
   protected boolean debug = false;

   /** This is an array of all the indices iso we can do an arraycopy. */
   protected int[] indices = null;

   /** the number of folds.* */
   protected int nfolds = 10;

   /**
    * the number of folds when we started execution, just in case user
    * properties mid-run.
    */
   protected int Nfolds;

   /** number of times we have fired for current input data.* */
   protected int numFires = 0;

   /** The number of rows in the data table. */
   protected int numRows;

   /** seed for random number generation. */
   protected long seed = (long) 0.00;

   /** the data table. */
   protected Table table = null;

   //~ Methods *****************************************************************

   /**
    * Returns a list of indices to the first element of Nfold + 1 sets. The
    * index of the first set is always 0; The index of the last set (set N+1) is
    * always one past the end of the examples. We do N+1 rather than N or N-1
    * because then we don't have to special case processing of first and last
    * sets elsewhere in the code.
    *
    * @param  nExamples The number of examples.
    *
    * @return returns a list of indices to the first element of Nfold + 1 sets.
    *         The index of the first set is always 0; The index of the last set
    *         (set N+1) is always one past the end of the examples. We do N+1
    *         rather than N or N-1 because then we don't have to special case
    *         processing of first and last sets elsewhere in the code.
    */
   int[] getTableBreaks(int nExamples) {
      int[] tableBreaks;
      double numExamples = (double) nExamples;
      double n = (double) Nfolds;

      tableBreaks = new int[Nfolds + 1];
      tableBreaks[0] = 0;

      for (int i = 1; i < Nfolds; i++) {

         if (Nfolds == numExamples) {
            tableBreaks[i] = i;
         } else {
            tableBreaks[i] = (int) (((double) i / n) * numExamples);
         }
      }

      tableBreaks[Nfolds] = nExamples;

      return tableBreaks;
   }

   /**
    * Setup the indexint array and shuffle it randomly.
    *
    * @throws Exception Description of exception Exception.
    */
   protected void setup() throws Exception {

      // Save the number of folds currently set, in case user changes
      // properties mid-stream
      Nfolds = nfolds;

      numRows = table.getNumRows();

      if (numRows < Nfolds) {
         throw new Exception(this.getAlias() +
                             ": The input table must not contain fewer rows than the number of folds requested. \n" +
                             "The input table has only " + numRows +
                             " rows and " + Nfolds + " folds were requested. ");
      }

      // setup the indicies that break the table into N folds; handles rounding
      breaks = this.getTableBreaks(numRows);

      // here are the indices into the table that we'll shuffle
      indices = new int[numRows];

      for (int i = 0; i < numRows; i++) {
         indices[i] = i;
      }

      Random rand = new Random(seed);

      // Let's shuffle them
      for (int i = 0; i < numRows; i++) {
         int swap = (int) (rand.nextDouble() * numRows);

         if (swap != 0) {
            swap--;
         }

         int old = indices[swap];
         indices[swap] = indices[i];
         indices[i] = old;
      }

      if (debug) {
         System.out.println(getAlias() + ": Indicies after randomization: ");

         for (int j = 0; j < numRows; j++) {
            System.out.println(indices[j]);
         }
      }
   } // end method setup

   /**
    * Reset variables when we begin execution.
    */
   public void beginExecution() {
      numFires = 0;
      breaks = null;
      table = null;
      indices = null;
   }


   /**
    * Performs the main work of the module. Set up training and testing sets and
    * push out the sub-tables. If this is the first execution, push out the
    * number of folds.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      // If this is the first time with new data,
      // load the input, then call setup to
      // initialize variables we'll use across all folds
      // Finally, push the Number of Folds to output port
      //
      if (numFires == 0) {
         table = (Table) this.pullInput(0);
         setup();
         this.pushOutput(new Integer(Nfolds), 2);
      }

      // Generate the train/test for the current fold, which
      // is determined based on the value of numFires.
      //
      int testSize = breaks[numFires + 1] - breaks[numFires];
      int trainSize = numRows - testSize;

      // Set up the train and test sets indices;
      //
      int[] testing = new int[testSize];
      int[] training = new int[trainSize];

      // Copy appropriate entries from the randomized indices array
      // into test set
      //
      System.arraycopy(indices, breaks[numFires], testing, 0, testSize);

      // Copy remaining entries from the randomized indices array
      // into train set.   This is done in 2 steps, first copying
      // indicies before the test entries, then copying indicies after
      // the test entries.  Note that for first and last folds one
      // of these two copies will have 0 length and be a noop.
      //
      System.arraycopy(indices, 0, training, 0, breaks[numFires]);
      System.arraycopy(indices, breaks[numFires + 1],
                       training, breaks[numFires],
                       numRows - breaks[numFires + 1]);

      if (debug) {
         System.out.println(getAlias() + ": Fold " + numFires);
         System.out.println(getAlias() + ": Test Set size:  " +
                            testSize + " and indicies: ");

         for (int j = 0; j < testSize; j++) {
            System.out.println(testing[j]);
         }

         System.out.println(getAlias() + ": Train Set size:  " +
                            trainSize + " and indicies: ");

         for (int j = 0; j < trainSize; j++) {
            System.out.println(training[j]);
         }
      }

      // now create a new vertical table.
      ExampleTable examples = table.toExampleTable();
      examples.setTrainingSet(training);
      examples.setTestingSet(testing);

      Table testT = examples.getTestTable();
      Table trainT = examples.getTrainTable();

      // push train and test sets to output ports
      this.pushOutput(trainT, 0);
      this.pushOutput(testT, 1);

      numFires++;

      // are we done with all the train/test sets for this input?
      // if so, clean up.
      //
      if (numFires == Nfolds) {
         numFires = 0;
         breaks = null;
         indices = null;
      }
   } // end method doit

   /**
    * get the debug flag value.
    *
    * @return the debug flag value
    */
   public boolean getDebug() { return debug; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The table from which the train and test tables will be extracted.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Table";

         default:
            return "No such input";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module produces tables containing train and test examples that can be used to perform N-fold  ");
      sb.append("cross validation and estimate model accuracy. ");

      sb.append("</p><p>Detailed Description: ");
      sb.append("For each table input, this module randomly divides the examples in the table into N mutually exclusive ");
      sb.append("subsets, referred to as <i>folds</i>, and builds train and test example tables from those folds. ");
      sb.append("Each of the folds contains approximately the same number of examples, ");
      sb.append("and the user can control the number of folds via the properties editor. ");
      sb.append("The number of folds must be greater than 2, and the input table must contain at least N examples ");
      sb.append("or an exception will occur. ");

      sb.append("</p><p> ");
      sb.append("The examples in N-1 of the folds are gathered into a <i>Train Table</i> ");
      sb.append("and the examples in the remaining fold form a <i>Test Table</i>. ");
      sb.append("The module executes N times for each input table received, producing a new <i>Train Table</i> and ");
      sb.append("<i>Test Table</i> each time it executes.   A different fold of the examples is \"held out\" ");
      sb.append("for testing each time. ");

      sb.append("The number of folds is written to the <i>Number of Folds</i> output port ");
      sb.append("the first time the module executes after a new input table is received. ");

      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("This module has no explicit data type restrictions, however the majority of the predictive model ");
      sb.append("builders work on continuous values, so data conversion may be required.  If so, it should be done ");
      sb.append("prior to splitting the examples into train and test sets in this module.  ");
      sb.append("Otherwise, the conversion would ");
      sb.append("have to be performed over and over on the examples in the N train and test sets. ");

      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify the input table, but may reference the values in the table in the Train ");
      sb.append("and Test tables that are generated.  (The exact behavior depends on the type of table that is input.) ");
      sb.append("Because of this, it is recommended that the user not modify the original table in other modules in the ");
      sb.append("itinerary that will execute after this module, as doing so may impact the validity of the Train and Test ");
      sb.append("table contents. ");

      sb.append("</p><p>Scalability: ");
      sb.append("The memory requirements of the original table will likely dwarf the memory requirements of ");
      sb.append("this module, ");
      sb.append("which requires an array of integers with one entry for each row of the original table. ");
      sb.append("Additional memory may be required depending on the underlying implementation of the original table. ");

      sb.append("</p><p>Trigger Criteria: ");
      sb.append("When this module receives an input, it will execute <i>Number of Folds</i> times, where <i>Number ");
      sb.append("of Folds</i> is the property controlled by the user. ");


      return sb.toString();
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "N-Fold Train Test"; }


   /**
    * returns the number of folds.
    *
    * @return the number of folds.
    */
   public int getNumberFolds() { return nfolds; }

   /**
    * Get the number of times this module has fired.
    *
    * @return number of times module has fired
    */
   public int getNumFires() { return numFires; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "A table containing the training data.";

         case 1:
            return "A table containing the testing data.";

         case 2:
            return "The number of folds specified via the property editor.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Train Table";

         case 1:
            return "Test Table";

         case 2:
            return "Number of Folds";

         default:
            return "No such output";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.table.TrainTable",
         "ncsa.d2k.modules.core.datatype.table.TestTable",
         "java.lang.Integer"
      };

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] =
         new PropertyDescription("numberFolds",
                                 "Number of Folds",
                                 "The number of folds for the cross validation. This number must be greater than 2.");
      pds[1] =
         new PropertyDescription("seed",
                                 "Seed",
                                 "The seed for the random number generator used to shuffle the examples before folds are formed. \n" +
                                 "If the same seed is used across runs with the same input table, you will get the same train and test sets.");
      pds[2] =
         new PropertyDescription("debug",
                                 "Verbose output",
                                 "If this flag is set, the indices of the train and test sets are output to the console as the module runs.");

      return pds;
   }

   /**
    * get the random seed.
    *
    * @return the random seed
    */
   public long getSeed() { return seed; }

   /**
    * Fires N times, where N is the number of folds.
    *
    * @return true when ready to fire
    */
   public boolean isReady() {

      if (numFires == 0) {
         return super.isReady();
      } else {
         return true;
      }
   }

   /**
    * Set the debug flag value.
    *
    * @param b new debug value
    */
   public void setDebug(boolean b) { debug = b; }

   /**
    * Set the number of folds.
    *
    * @param  n number of folds
    *
    * @throws PropertyVetoException when n is less than 3
    */
   public void setNumberFolds(int n) throws PropertyVetoException {

      if (n < 3) {
         throw new PropertyVetoException(" There must be at least 3 folds",
                                         null);
      }

      nfolds = n;
   }

   /**
    * Get the random seed.
    *
    * @param d random seed
    */
   public void setSeed(long d) { seed = d; }
} // end class NFoldTrainTest
