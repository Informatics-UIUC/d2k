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
package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.core.modules.ModelEvaluatorModule;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;


/**
 * <p>Overview: Evaluate the performance of a classifier.
 * <p>Detailed Description: Given a PredictionModelModule and a Table of
 * examples, use the model to predict a value for each example.  The percentage
 * of correct predictions for each output feature is calculated and output in a
 * ParameterPoint.
 * <p>Data Handling: This module does not modify the input data.
 * <p>Scalability: This module will make predictions for each example in the
 * Table; there must be sufficient memory to hold each prediction.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ClassificationErrorEvaluator extends ModelEvaluatorModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      PredictionModelModule pmm = (PredictionModelModule) pullInput(0);
      Table t = (Table) pullInput(1);

      PredictionTable pt = pmm.predict(t);
      int[] outputs = pt.getOutputFeatures();
      int[] preds = pt.getPredictionSet();

      int numRows = pt.getNumRows();

      String[] names = new String[preds.length];
      double[] errors = new double[preds.length];

      for (int i = 0; i < preds.length; i++) {
         int numCorrect = 0;

         for (int j = 0; j < numRows; j++) {
            String orig = pt.getString(j, outputs[i]);
            String pred = pt.getString(j, preds[i]);

            if (orig.equals(pred)) {
               numCorrect++;
            }
         }

         names[i] = pt.getColumnLabel(preds[i]);
         errors[i] = 1 - ((double) numCorrect) / ((double) numRows);
      }

      ParameterPoint pp = ParameterPointImpl.getParameterPoint(names, errors);
      pushOutput(pp, 0);
   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      if (i == 0) {
         return "A PredictionModelModule to evaluate";
      } else {
         return "The data to use to evaluate the model";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      if (i == 0) {
         return "Prediction Model Module";
      } else {
         return "Table";
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
      String[] in =
      {
         "ncsa.d2k.modules.PredictionModelModule",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      // return "";
      StringBuffer sb =
         new StringBuffer("<p>Overview: Evaluate the performance of ");
      sb.append("a classifier.");
      sb.append("<p>Detailed Description: Given a PredictionModelModule and a ");
      sb.append("Table of examples, use the model to predict a value for each ");
      sb.append("example.  The percentage of correct predictions for each output ");
      sb.append("feature is calculated and output in a ParameterPoint.");
      sb.append("<p>Data Handling: This module does not modify the input data. ");
      sb.append("<p>Scalability: This module will make predictions for each ");
      sb.append("example in the Table; there must be sufficient memory to hold ");
      sb.append("each prediction.");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Classification Error Evaluator"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
      return "A ParameterPoint with an entry for the percentage classification error";
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "Parameter Point"; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out =
      { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };

      return out;
   }
} // end class ClassificationErrorEvaluator
