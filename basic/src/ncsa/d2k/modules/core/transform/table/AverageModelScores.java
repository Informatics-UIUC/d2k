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
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.util.ArrayList;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Overview: This module takes an integer which indicates how many scores
 * it should accumulate, and on the other input, the scores which will be
 * averaged.
 * </p>
 * <p>
 * Detailed Description: This module will take N scores and average them,
 * produced the averaged score in the form of a ParameterPoint. The number
 * of scores to average is ascertained from the Integer passed in on the
 * first port. When all N scores have been received, the average is
 * calcuated and the result passed along.
 * </p>
 * <p>
 * Data Type Restrictions: None. This module will average any number of
 * objective scores, producing a Parameter Point with the same number of
 * objectives.
 * </p>
 * <p>
 * Data Handling: The original ParameterPoints are discarded and a new one
 * is produced.
 * </p>
 * <p>
 * Scalability: There must be sufficient memory to store all the
 * ParameterPoints to be averaged.
 * </p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class AverageModelScores extends ncsa.d2k.core.modules.DataPrepModule {

   //~ Instance fields *********************************************************

   /** true if verbose output should be used. */
   private boolean _verbose = false;

   /** used to store the average score for each of the objective features. */
   private double[] avgs = null;

   /** number of scores currently obtained. */
   private int counter = 0;

   /** folds contains the scores */
   private ArrayList folds = null;

   /** number of folds to average scores for. */
   private int numberFolds = -1;

   /** the accumulated points in objective space. */
   private ArrayList objectivePoints = new ArrayList();

   /** number of points in solution space. */
   private int solutionSpaceDimensions = 0;
   private D2KModuleLogger myLogger;
   
   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() {
      folds = new ArrayList(200);
      this.numberFolds = -1;
      counter = 0;
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   /**
    * Take each paramter point, accumulate the sum of the various solutions in
    * the solution space for each fold, and when we have the objective values
    * for each fold, compute the means, put that in new paramter point and pass
    * it on.
    */
   public void doit() {

      // If we have a number of folds, just put it into the
      // folds list and return.
      if (this.getFlags()[0] > 0) {
         this.folds.add(this.pullInput(0));

         return;
      }

      // Get the parameter point.
      ParameterPoint pp = (ParameterPoint) this.pullInput(1);

      if (numberFolds == -1) {

         // First time through, we need to get num folds, number of
         // attributes in solution space, allocate array for the
         // sums of all the solutions.
         this.numberFolds = ((Integer) this.folds.remove(0)).intValue();
         solutionSpaceDimensions = pp.getNumParameters();
         this.avgs = new double[solutionSpaceDimensions];

         for (int i = 0; i < solutionSpaceDimensions; i++) {
            this.avgs[i] = 0.0;
         }
      }

      // add each objective value to the sum of the objective value.
      for (int i = 0; i < this.solutionSpaceDimensions; i++) {
         this.avgs[i] += pp.getValue(i);
      }

      // Average the objective scores.
      this.objectivePoints.add(pp);
      this.counter++;

      if (this.counter == this.numberFolds) {


         // From the sum, compute the mean
         for (int i = 0; i < this.solutionSpaceDimensions; i++) {
            this.avgs[i] /= this.numberFolds;
         }

         // Reset the counter and the object points array.
         this.counter = 0;
         this.objectivePoints.clear();

         // create a new table from which we construct a new paramter point
         String[] names = new String[this.solutionSpaceDimensions];

         for (int i = 0; i < this.solutionSpaceDimensions; i++) {
            names[i] = pp.getColumnLabel(i);
         }

         int numColumns = names.length;
         Column[] cols = new Column[avgs.length];
         int[] outs = new int[cols.length];

         // compute the averages
         for (int i = 0; i < avgs.length; i++) {
            double[] vals = new double[1];
            vals[0] = avgs[i];

            DoubleColumn dc = new DoubleColumn(vals);
            dc.setLabel(names[i]);
            cols[i] = dc;
            outs[i] = i;
         }

         ExampleTable eti = new MutableTableImpl(cols).toExampleTable();

         if (this.getVerbose()) {
        	 myLogger.setDebugLoggingLevel();//temp set to debug
        	 myLogger.debug("\nAverageModelScores for " + this.numberFolds +
                     " folds.");
        	 myLogger.resetLoggingLevel();//re-set level to original level

            for (int i = 0, n = eti.getNumColumns(); i < n; i++) {
            	myLogger.setDebugLoggingLevel();//temp set to debug
            	myLogger.debug(eti.getColumnLabel(i) + " -- avg error: " +
                        eti.getDouble(0, i) + " avg correct: " +
                        (1 - eti.getDouble(0, i)));
            	myLogger.resetLoggingLevel();//re-set level to original level
            }

            System.out.println();
         }

         // Done.
         this.numberFolds = -1;

         eti.setOutputFeatures(outs);

         ParameterPoint objectivepp = pp.createFromTable(eti); // names, avgs);
         this.pushOutput(objectivepp, 0);
      } // end if
   } // end method doit


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>" +
                   "      This is the number of scores input to average." +
                   "    </p>";

         case 1:
            return "<p>" +
                   "      This paramter point contains the objective score." +
                   "    </p>";

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
            return "Input Count";

         case 1:
            return "Score";

         default:
            return "NO SUCH INPUT!";
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
      String[] types =
      {
         "java.lang.Integer",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module takes an integer which indicates how many scores " +
             "      it should accumulate, and on the other input, the scores which will be " +
             "      averaged." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: This module will take N scores and average them, " +
             "      produced the averaged score in the form of a ParameterPoint. The number " +
             "      of scores to average is ascertained from the Integer passed in on the " +
             "      first port. When all N scores have been received, the average is " +
             "      calcuated and the result passed along." +
             "    </p>" +
             "    <p>" +
             "      Data Type Restrictions: None. This module will average any number of " +
             "      objective scores, producing a Parameter Point with the same number of " +
             "      objectives. " +
             "    </p>" +
             "    <p>" +
             "      Data Handling: The original ParameterPoiints are discarded and a new one " +
             "      is produced." +
             "    </p>" +
             "    <p>" +
             "      Scalability: There must be sufficient memory to store all the " +
             "      ParameterPoints to be averaged." +
             "    </p>";
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Average Scores"; }

   /**
    * Get folds
    *
    * @return folds
    */
   public ArrayList getNumberFolds() { return folds; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>" +
                   "      This is the point in paramter space which contains the averaged " +
                   "      objective score." +
                   "    </p>";

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
            return "Averaged Objective";

         default:
            return "NO SUCH OUTPUT!";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
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
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] =
         new PropertyDescription("verbose",
                                 "Generate Verbose Output",
                                 "If this property is true, the module will write verbose information to the console.");

      return pds;
   }

   /**
    * Get verbose.  If this property is true, the module will write verbose
    * information to the console.
    *
    * @return verbose
    */
   public boolean getVerbose() { return _verbose; }

   /**
    * This module will enable if we have not already gotten the fold count and
    * we have inputs on pipe 0 and 1, or if we have gotten the fold count, if we
    * have an input on pipe 1.
    *
    * @return this module will enable if we have not already gotten the fold
    *         count and we have inputs on pipe 0 and 1, or if we have gotten the
    *         fold count, if we have an input on pipe 1.
    */
   public boolean isReady() {

      // If we have a fold number, we will run and queue it up.
      if (this.getFlags()[0] > 0) {
         return true;
      }

      if (numberFolds == -1) {

         // We are not currently processing a set of folds, we must
         // have some folds queued up before we can run.
         if (this.folds.size() > 0 && this.getFlags()[1] > 0) {
            return true;
         }
      } else {

         // we are already working on a set of folds.
         if (this.getFlags()[1] > 0) {
            return true;
         }
      }

      return false;
   }

   /**
    * Set folds
    *
    * @param tt new folds
    */
   public void setNumberFolds(ArrayList tt) { this.folds = tt; }

   /**
    * Set verbose
    *
    * @param b new verbose
    */
   public void setVerbose(boolean b) { _verbose = b; }
} // end class AverageModelScores
