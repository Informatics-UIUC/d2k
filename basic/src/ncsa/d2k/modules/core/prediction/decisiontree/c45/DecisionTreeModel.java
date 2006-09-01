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
package ncsa.d2k.modules.core.prediction.decisiontree.c45;

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
 * Encapsulates a decision tree. Takes an ExampleTable as input and uses the
 * decision tree to predict the outcome for each row in the data set.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeModel extends PredictionModelModule
   implements Serializable, NominalViewableDTModel {

   //~ Static fields/initializers **********************************************

   /** Description of field serialVersionUID. */
   static final long serialVersionUID = 6788778863299676465L;

   //~ Instance fields *********************************************************

    /** names of unique classes */
   private String[] classNames;

   /** the input features */
   private int[] inputFeatures;

   /** The number of examples in the data set. */
   private int numExamples;

   /** the output features */
   private int[] outputFeatures;

   /** The root of the decision tree. */
   private DecisionTreeNode root;

   /** unique values for all inputs */
   private String[][] uniqueInputs;

   /** The unique values in the output column of the table. */
   private String[] uniqueOutputs;

   //~ Constructors ************************************************************

    /**
     * Constructor
     */
   public DecisionTreeModel() { }

   /**
    * Constructor.
    *
    * @param rt    the root of the decision tree
    * @param table training table
    */
   public DecisionTreeModel(DecisionTreeNode rt, ExampleTable table) {
      super(table);
      setName("DecisionTreeModel");
      root = rt;
      inputFeatures = table.getInputFeatures();
      outputFeatures = table.getOutputFeatures();
      // setTrainingTable(table);              trainingSetSize =
      // table.getNumRows();

/*              inputColumnNames = new String[inputFeatures.length];
 *              //inputTypes = new String[inputFeatures.length];
 */
/*                inputIsScalar = new boolean[inputFeatures.length];
 *
 *           for(int i = 0; i < inputFeatures.length; i++) { //
 *     inputColumnNames[i] = table.getColumnLabel(inputFeatures[i]);
 *         //if(table.getColumn(inputFeatures[i]) instanceof NumericColumn)
 *                if(table.isColumnScalar(inputFeatures[i]))
 *         //inputTypes[i] = "Scalar";
 * inputIsScalar[i] = true;                     else
 * //inputTypes[i] = "Nominal";                             inputIsScalar[i] =
 * false;             }
 *
 * //              outputColumnNames = new String[outputFeatures.length];
 *    //outputTypes = new String[outputFeatures.length];
 * outputIsScalar = new boolean[outputFeatures.length];             for(int i =
 * 0; i < outputFeatures.length; i++) { //
 * outputColumnNames[i] = table.getColumnLabel(outputFeatures[i]);
 *       //if(table.getColumn(outputFeatures[i]) instanceof NumericColumn)
 *               //if(table.isColumnNumeric(outputFeatures[i]))
 *    if(table.isColumnScalar(outputFeatures[i]))
 * //outputTypes[i] = "Scalar";                             outputIsScalar[i] =
 * true;                     else                             //outputTypes[i] =
 * "Nominal";                             outputIsScalar[i] = false;
 * }*/

      classNames = uniqueValues(table, outputFeatures[0]);

      uniqueInputs = new String[inputFeatures.length][];

      for (int index = 0; index < inputFeatures.length; index++) {
         uniqueInputs[index] = uniqueValues(table, inputFeatures[index]);
      }

      // do unique outputs here
      uniqueOutputs = uniqueValues(table, outputFeatures[0]);

      try {
         predict(table);
      } catch (Exception e) { }
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
   }

   /**
    * Predict an outcome for each row of the table using the decision tree.
    *
    * @param pt the table
    */
   protected void makePredictions(PredictionTable pt) {

      for (int i = 0; i < pt.getNumRows(); i++) {
         String pred = (String) root.evaluate(pt, i);
         pt.setStringPrediction(pred, i, 0);
      }
   }

   /**
    * Pull in the table and pass it to predict.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      // ExampleTable et = (ExampleTable)pullInput(0);
      Table t = (Table) pullInput(0);
      PredictionTable retVal = predict(t);
      pushOutput(retVal, 0);
      pushOutput(this, 1);
   }

   /**
    * Get the class names.
    *
    * @return the class names
    */
   public final String[] getClassNames() { return classNames; }


    /**
     * The input is an ExampleTable.
     *
     * @param i the index of the input
     * @return the description of the input
     */
    public String getInputInfo(int i) {
        return "The data set to predict the outcomes for.";
    }


    /**
     * Get the name of the input.
     *
     * @param i the index of the input
     * @return the name of the input
     */
    public String getInputName(int i) {
        return "Dataset";
    }

   /**
    * Get the names of the input attributes
    *
    * @return names of input attributes
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
      String[] in = { "ncsa.d2k.modules.core.datatype.table.Table" };

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
   public String getModuleName() { return "C4.5 Decision Tree Model"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      if (i == 0) {
         return "The original data set with an extra column of predictions.";
      } else {
         return "A reference to this model.";
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

      if (i == 0) {
         return "Predictions";
      } else {
         return "DTModel";
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
         "ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeModel"
      };

      return out;
   }

   /**
    * Get the root of this decision tree.
    *
    * @return the root of the tree
    */
   public DecisionTreeNode getRoot() {
      return root;
   }

   /**
    * Get the unique input values at index
    *
    * @param  index attribute index
    *
    * @return unique input values
    */
   public String[] getUniqueInputValues(int index) {
      return uniqueInputs[index];
   }
 
   /**
    * Get the unique values of the output column.
    *
    * @return get the unique values of the output column.
    */
   public String[] getUniqueOutputValues() { return uniqueOutputs; }

   /**
    * Get the Viewable root of this decision tree.
    *
    * @return the root of the tree
    */
   public ViewableDTNode getViewableRoot() { return root; }

   /**
    * Return true if the input at index is scalar
    *
    * @param  index input index
    *
    * @return true if the input at index is scalar
    */
   public boolean scalarInput(int index) {

      // if (inputTypes[index].equals("Scalar"))
      /*if(inputIsScalar[index])
       *      return true;
       *
       * return false;
       */
// return inputIsScalar[index];
      return super.getScalarInputs()[index];
   }

   /**
    * Return true if the output at index is scalar
    *
    * @param  index output index
    *
    * @return true if the output at index is scalar
    */
   public boolean scalarOutput(int index) {
// return outputIsScalar[index];
      return super.getScalarOutputs()[index];
   }

   /**
    * Set the unique output values
    *
    * @param values unique output values
    */
   public void setUniqueOutputvalues(String[] values) {
      uniqueOutputs = values;
   }
} // end class DecisionTreeModel
