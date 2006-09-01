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
package ncsa.d2k.modules.core.prediction.svm;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_problem;


/**
 * this module supports the incremental training of support vectors machine. its
 * inputs are the svm model from previous iteration and a new data set in the
 * format of an svm problem. it then merges the support vectors of the svm model
 * with the svm problem into a new svm problem, which is the output of this
 * module. In the first iteration we expect this module to receive only the new
 * data set and it is output as is.
 *
 * @author  vered goren
 * @version 1.0
 */
public class MergeSVMProblems extends ncsa.d2k.core.modules.DataPrepModule {

   //~ Instance fields *********************************************************

   /** true if first execution */
   private boolean first = true;

   //~ Methods *****************************************************************

   /**
    * copies the array of support vectors, so that the model from iteration i
    * won't be changed in iteration i+1.
    *
    * @param  original - Support Vectors of input Model.
    *
    * @return - exact copy of the support vectors.
    */
   private svm_node[][] copy(svm_node[][] original) {
      svm_node[][] copyOf = new svm_node[original.length][];

      for (int i = 0; i < original.length; i++) {
         copyOf[i] = new svm_node[original[i].length];

         for (int j = 0; j < original[i].length; j++) {
            copyOf[i][j] = new svm_node();
            copyOf[i][j].index = original[i][j].index;
            copyOf[i][j].value = original[i][j].value;
         }
      }

      return copyOf;
   }

   /**
    * merges <code>newData</codE> with the support vectors of <codE>
    * oldModel</code>.
    *
    * @param  newData  new training data set.
    * @param  oldModel support vectors machine model from previous iteration
    *
    * @return svm_problem consisting of <code>newData</code> and the vectors of
    *         <codE>oldModel</code>
    */
   private svm_problem merge(svm_problem newData, svm_model oldModel) {
      svm_problem merged = new svm_problem();

      // allocate the arrays.

      svm_node[][] sv = copy(svm.svm_get_support_vectors(oldModel)); // the support vectors.

      merged.l = newData.l + sv.length;
      merged.x = new svm_node[merged.l][];
      merged.y = new double[merged.l];

      // copy data from newData
      for (int i = 0; i < newData.l; i++) {
         merged.x[i] = newData.x[i];
         merged.y[i] = newData.y[i];
      }


// int[] labels = new int[sv.length];
// svm.svm_get_labels(oldModel, labels);
      // copy nodes from oldModel's vectors and its labels
      for (int j = 0, i = newData.l; i < merged.l && j < sv.length; i++, j++) {
         merged.x[i] = sv[j];
         merged.y[i] = svm.svm_predict(oldModel, sv[j]);
      }

      return merged;

   } // end method merge

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {

      if (first) {
         first = false;

         svm_problem prob = (svm_problem) pullInput(0);
         pushOutput(prob, 0);
      } else {
         svm_problem prob = (svm_problem) pullInput(0);
         svm_model model = (svm_model) pullInput(1);

         // svm_node[][] SV = (svm_node[][]) pullInput(2);
         // Integer numAtts = (Integer) pullInput(2);
         svm_problem merged = merge(prob, model);
         pushOutput(merged, 0);
      }

   } // doit

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { first = true; }


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
            return "New data set, to be merged with the Support Vectors of <i>SVM Model</i>";

         case 1:
            return "The Support Vectors of this model are to be merged with <i>Input SVM Problem</i>";
            // case 2: return "<P>The Support Vectors of <i>SVM Model</i>";

         /*   case 2: return "<P>Number of attributes in the data.</P>";*/
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
            return "Input SVM Problem";

         case 1:
            return "SVM Model";

         // case 2: return "Support Vectors";
         /*           case 2: return "Number of Attributes";*/
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
      String[] types = { "libsvm.svm_problem", "libsvm.svm_model" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: " +
             "Merges <i>Input SVM Problem</i> with the Support of Vectors of <i>SVM " +
             "Model</i> into one <i>Merged SVM Problem</i>." +
             "</p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Merge SVM Problems"; }


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
            return "Merged training data.";

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
            return "Merged SVM Problem";

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
      String[] types = { "libsvm.svm_problem" };

      return types;
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {
      return ((getInputPipeSize(0) > 0) && (first || getInputPipeSize(1) > 0));
   }
} // end class MergeSVMProblems
