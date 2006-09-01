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
package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;


import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.prediction.decisiontree.NominalViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Encapsulates a decision tree built from a database cube. Takes an
 * ExampleTable as input and uses the decision tree to predict the outcome for
 * each row in the data set. This module is created based on DecisionTreeModel
 * written by David Clutter
 *
 * @author  Dora Cai
 * @version $Revision$, $Date$
 */
public class DecisionForestModel extends PredictionModelModule
   implements Serializable, NominalViewableDTModel {

   //~ Static fields/initializers **********************************************

   /** Description of field serialVersionUID. */
   static final long serialVersionUID = -473168938003511128L;

   //~ Instance fields *********************************************************

   /** names of unique classes */
   private String[] classNames;

   /** labels of input columns */
   private String[] inputColumnLabels;

   /** The number of examples in the data set. */
   private int numExamples;

   /** labels of output columns */
   private String[] outputColumnLabels;

   /** The root of the decision tree. */
   private DecisionForestNode root;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    *
    * @param rt          the root of the decision tree
    * @param table       training table
    * @param totalRow    number of examples
    * @param classValues unique class values
    */
   public DecisionForestModel(DecisionForestNode rt, ExampleTable table,
                              int totalRow, String[] classValues) {

      super(table);

      setName("DecisionForestModel");

      outputColumnLabels = new String[table.getOutputFeatures().length];

      for (int i = 0; i < outputColumnLabels.length; i++) {

         String label = table.getColumnLabel(table.getOutputFeatures()[i]);

         // column label may contain the table name as the prefix if more than
         // one tables are used

         int idx = label.indexOf(".");

         if (idx >= 0) {

            outputColumnLabels[i] = label.substring(idx + 1, label.length());
         } else {

            outputColumnLabels[i] = label;
         }

      }

      inputColumnLabels = new String[table.getInputFeatures().length];

      for (int i = 0; i < inputColumnLabels.length; i++) {

         String label = table.getColumnLabel(table.getInputFeatures()[i]);

         // column label may contain the table name as the prefix if more than
         // one tables are used

         int idx = label.indexOf(".");

         if (idx >= 0) {

            inputColumnLabels[i] = label.substring(idx + 1, label.length());
         } else {

            inputColumnLabels[i] = label;
         }

      }

      root = rt;

      numExamples = totalRow;

      classNames = classValues;

   }

   //~ Methods *****************************************************************

   /**
    * Get the unique values in a column of a Table.
    *
    * @param  vt  the Table
    * @param  col the column we are interested in
    *
    * @return a String array containing the unique values of the column
    */
   static public String[] uniqueValues(Table vt, int col) {

      int numRows = vt.getNumRows();


      // count the number of unique items in this column

      HashSet set = new HashSet();

      for (int i = 0; i < numRows; i++) {

         String s = vt.getString(i, col);

         if (!set.contains(s)) {

            set.add(s);
         }

      }


      String[] retVal = new String[set.size()];

      int idx = 0;

      Iterator it = set.iterator();

      while (it.hasNext()) {

         retVal[idx] = (String) it.next();

         idx++;

      }


      return retVal;

   } // end method uniqueValues

   /**
    * Make predictions on the examples in a table.
    *
    * @param pt prediction table
    */
   protected void makePredictions(PredictionTable pt) {

      // When building model by RainForest algorithm, prediction table

      // does not exist, the date type of the prediction column is "object".

      // When using model to predict testing examples, the prediction

      // table is created, and the data type of the prediction column is
      // "String".

      if (pt.getColumnType(pt.getNumColumns() - 1) != 9) {

         for (int i = 0; i < pt.getNumRows(); i++) {

            String pred = (String) root.evaluate(root, pt, i);

            pt.setStringPrediction(pred, i, 0);

         }

      }

   }

   /**
    * Pull in the table and pass it to predict.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      ExampleTable et = (ExampleTable) pullInput(0);

      PredictionTable retVal = predict(et);

      pushOutput(retVal, 0);

      pushOutput(this, 1);

   }

   /**
    * Get the class names.
    *
    * @return the class names
    */
   public String[] getClassNames() { return classNames; }

   /**
    * Get the labels of the input columns.
    *
    * @return the labels of the input columns
    */
   public String[] getInputColumnLabels() { return inputColumnLabels; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @param  i Description of parameter i.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "DecisionForestNode.";

         case 1:
            return "ExampleTable.";

         case 2:
            return "Total number of rows.";

         default:
            return "No such input.";

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

      switch (i) {

         case 0:
            return "DecisionForestNode.";

         case 1:
            return "ExampleTable.";

         case 2:
            return "Total number of rows.";

         default:
            return "No such input.";

      }

   }

   /**
    * Get the names of the inputs.
    *
    * @return the names of the inputs
    */
   public String[] getInputs() { return getInputColumnLabels(); }

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
         "ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestNode",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable", "java.lang.int"
      };

      return in;

   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s = "Encapsulates a decision tree.  Takes an ";

      s += "ExampleTable as input and uses the decision tree to ";

      s += "predict the outcome for each row in the data set.";

      return s;

   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Rain Forest Decision Tree Model"; }

   /**
    * Get the number of examples from the data set passed to the predict method.
    *
    * @return the number of examples.
    */

   public int getNumExamples() { return numExamples; }

   /**
    * Get the labels of the output columns.
    *
    * @return the labels of the output columns
    */
   public String[] getOutputColumnLabels() { return outputColumnLabels; }

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
            return "The data set with an extra column of predictions.";

         case 1:
            return "Decision tree model.";

         default:
            return "No such output.";

      }

   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Predictions";

         case 1:
            return "DTModel";

         default:
            return "No such output.";

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

      String[] out =
      {
         "ncsa.d2k.modules.core.datatype.table.PredictionTable",

      "ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestModel"
      };

      return out;

   }

   /**
    * Get the root of this decision tree.
    *
    * @return the root of the tree
    */

   public DecisionForestNode getRoot() { return root; }


   /**
    * Get the unique values for the specified input attribute.
    *
    * @param  index input attribute
    *
    * @return The unique values for the specified input
    */
   public String[] getUniqueInputValues(int index) {

      // this method has only been used to get the unique values in the class
      // column.

      return classNames;

   }

   /**
    * Get the unique values for the specified output attribute.
    *
    * @return The unique values for the specified output
    */
   public String[] getUniqueOutputValues() {

      return classNames;

      // return uniqueOutputs;

   }

   /**
    * Get the Viewable root of this decision tree.
    *
    * @return the root of the tree
    */
   public ViewableDTNode getViewableRoot() { return root; }

   /**
    * Return true iff the input attribute at index is scalar.
    *
    * @param  index input index
    *
    * @return true iff the input attribute at index is scalar
    */
   public boolean scalarInput(int index) {

      // return inputIsScalar[index];
      return super.getScalarInputs()[index];

   }

   /**
    * Root of the tree.
    *
    * @param newRoot root of the tree
    */
   public void setRoot(DecisionForestNode newRoot) { root = newRoot; }


} // end class DecisionForestModel
