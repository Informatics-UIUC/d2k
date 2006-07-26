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
package ncsa.d2k.modules;

import ncsa.d2k.core.modules.ModelModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.ReversibleTransformation;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Abstract class to manage a prediction model.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class PredictionModelModule extends ModelModule
   implements java.io.Serializable {

   //~ Instance fields *********************************************************

   /** the labels for the input columns. */
   private String[] inputColumnLabels;

   /** the datatypes for the input features. */
   private int[] inputFeatureTypes;

   /** the labels for the outputs columns. */
   private String[] outputColumnLabels;

   /** the datatypes for the output features. */
   private int[] outputFeatureTypes;

   /** the scalar inputs are marked true. */
   private boolean[] scalarInputs;

   /** the scalar outputs are marked true. */
   private boolean[] scalarOutputs;

   /** the size of the training set for this model. */
   private int trainingSetSize;

   private List transformations;

   protected boolean _checkFormat = true;
   
   protected boolean applyReverseTransformationsAfterPredict = false;

   /**
    * A flag used to determine whether transformations should be applied in the
    * predict method or not.
    */
   protected boolean applyTransformationsInPredict = false;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    */
   protected PredictionModelModule() { }

   /**
    * Constructor.
    *
    * @param train the training data
    */
   protected PredictionModelModule(ExampleTable train) {
      setTrainingTable(train);
   }

   /**
    * Constructor.
    *
    * @param trainingSetSize    size of the training set
    * @param inputColumnLabels  labels of the input columns
    * @param outputColumnLabels labels of the output columns
    * @param inputFeatureTypes  datatypes of the inputs
    * @param outputFeatureTypes datatypes of the outputs
    */
   protected PredictionModelModule(int trainingSetSize,
                                   String[] inputColumnLabels,
                                   String[] outputColumnLabels,
                                   int[] inputFeatureTypes,
                                   int[] outputFeatureTypes ) {
      this.trainingSetSize = trainingSetSize;
      this.inputColumnLabels = inputColumnLabels;
      this.outputColumnLabels = outputColumnLabels;
      this.inputFeatureTypes = inputFeatureTypes;
      this.outputFeatureTypes = outputFeatureTypes;
   }

   //~ Methods *****************************************************************

   /**
    * put your documentation comment here.
    *
    * @param     pt Description of parameter $param.name$.
    *
    * @exception Exception
    */
   protected abstract void makePredictions(PredictionTable pt) throws Exception;

   /**
    * put your documentation comment here.
    *
    * @param et Description of parameter $param.name$.
    */
   protected void applyReverseTransformations(ExampleTable et) {

      if (transformations != null) {

         for (int i = 0; i < transformations.size(); i++) {
            Transformation trans = (Transformation) transformations.get(i);

            if (trans instanceof ReversibleTransformation) {

               ((ReversibleTransformation) trans).untransform(et);
            }
         }
      }
   }

   /**
    * Apply all the transformations in the Transformations list to the given
    * ExampleTable.
    *
    * @param et the ExampleTable to transform
    */
   protected void applyTransformations(ExampleTable et) {

      if (transformations != null) {

         for (int i = 0; i < transformations.size(); i++) {
            Transformation trans = (Transformation) transformations.get(i);
            trans.transform(et);
         }
      }
   }

   /**
    * put your documentation comment here.
    *
    * @return Information about the training dataset in html format
    */
   protected String getTrainingInfoHtml() {
      StringBuffer sb = new StringBuffer();
      sb.append("<b>Number Training Examples</b>:" + trainingSetSize +
                "<br><br>");
      sb.append("<b>Input Features:</b>: <br>");
      sb.append("<table><tr><td><u>Name</u><td><u>Type</u>");
      sb.append("<td><u>S/N</u></tr>");

      for (int i = 0; i < inputColumnLabels.length; i++) {
         sb.append("<tr><td>" + inputColumnLabels[i]);
         sb.append("<td>" + ColumnTypes.getTypeName(inputFeatureTypes[i]));

         if (scalarInputs[i]) {
            sb.append("<td>sclr");
         } else {
            sb.append("<td>nom");
         }

         sb.append("</tr>");
      }

      sb.append("</table><br>");
      sb.append("<b>Output (Predicted) Features</b>: <br><br>");
      sb.append("<table><tr><td><u>Name</u><td><u>Type</u>");
      sb.append("<td><u>S/N</u></tr>");

      for (int i = 0; i < outputColumnLabels.length; i++) {
         sb.append("<tr><td>" + outputColumnLabels[i]);
         sb.append("<td>" + ColumnTypes.getTypeName(outputFeatureTypes[i]));

         if (scalarOutputs[i]) {
            sb.append("<td>sclr");
         } else {
            sb.append("<td>nom");
         }

         sb.append("</tr>");
      }

      sb.append("</table>");

      return sb.toString();
   } // end method getTrainingInfoHtml

   /**
    * Set up all the meta-data related to the training set for this model.
    *
    * @param et Description of parameter $param.name$.
    */
   protected void setTrainingTable(ExampleTable et) {
      trainingSetSize = et.getNumRows();

      int[] inputs = et.getInputFeatures();
      inputColumnLabels = new String[inputs.length];

      for (int i = 0; i < inputColumnLabels.length; i++) {
         inputColumnLabels[i] = et.getColumnLabel(inputs[i]);
      }

      int[] outputs = et.getOutputFeatures();
      outputColumnLabels = new String[outputs.length];

      for (int i = 0; i < outputColumnLabels.length; i++) {
         outputColumnLabels[i] = et.getColumnLabel(outputs[i]);
      }

      inputFeatureTypes = new int[inputs.length];

      for (int i = 0; i < inputs.length; i++) {
         inputFeatureTypes[i] = et.getColumnType(inputs[i]);
      }

      outputFeatureTypes = new int[outputs.length];

      for (int i = 0; i < outputs.length; i++) {
         outputFeatureTypes[i] = et.getColumnType(outputs[i]);
      }

      scalarInputs = new boolean[inputs.length];

      for (int i = 0; i < inputs.length; i++) {
         scalarInputs[i] = et.isInputScalar(i);
      }

      scalarOutputs = new boolean[outputs.length];

      for (int i = 0; i < outputs.length; i++) {
         scalarOutputs[i] = et.isOutputScalar(i);
      }

      // copy the transformations
      try {
         List trans = et.getTransformations();

         if (trans instanceof ArrayList) {
            transformations =
               (ArrayList) ((ArrayList) (et).getTransformations()).clone();
         } else if (trans instanceof LinkedList) {
            transformations =
               (LinkedList) ((LinkedList) (et).getTransformations()).clone();
         } else if (trans instanceof Vector) {
            transformations =
               (Vector) ((Vector) (et).getTransformations()).clone();
         } else {
            transformations = null;
         }
      } catch (Exception e) {
         e.printStackTrace();
         transformations = null;
      }
   } // end method setTrainingTable

   /**
    * Pull in the ExampleTable, pass it to the predict() method, and push out
    * the PredictionTable returned by predict();
    *
    */
   public void doit() throws Exception {
      Table et = (Table) pullInput(0);
      pushOutput(predict(et), 0);
   }

   /**
    * Should transformations be reversed after predictions are made?
    *
    * @return true if transformations should be reversed after predictions are
    *         made, false otherwise
    */
   public boolean getApplyReverseTransformationsAfterPredict() {
      return applyReverseTransformationsAfterPredict;
   }

   /**
    * Should transformations be applied in the predict method?
    *
    * @return true if transformations should be applied to the dataset in the
    *         predict() method, false otherwise
    */
   public boolean getApplyTransformationsInPredict() {
      return applyTransformationsInPredict;
   }

   /**
    * Should the format of the table be checked to see if it is the same format
    * as the data we trained on?
    *
    * @return true if the format should be checked, false otherwise
    */
   public boolean getCheckTableFormat() { return _checkFormat; }

   /**
    * Get the labels of the input columns.
    *
    * @return the labels of the input columns
    */
   public String[] getInputColumnLabels() { return inputColumnLabels; }

   /**
    * Get the data types of the input columns in the training table.
    *
    * @return the datatypes of the input columns in the training table
    *
    * @see    ncsa.d2k.modules.core.datatype.table.ColumnTypes
    */
   public int[] getInputFeatureTypes() { return inputFeatureTypes; }

   /**
    * The input is an ExampleTable.
    *
    * @param  i the index of the input
    *
    * @return the description of the input
    */
   public String getInputInfo(int i) {
      return "A set of examples to make predicitons on.";
   }

   /**
    * Get the name of the input.
    *
    * @param  i the index of the input
    *
    * @return the name of the input
    */
   public String getInputName(int i) { return "Examples"; }

   /**
    * The input is a Table.
    *
    * @return the input types
    */
   public String[] getInputTypes() {
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Describe the function of this module.
    *
    * @return the description of this module.
    */
   public String getModuleInfo() {
      return "Makes predictions on a set of examples.";
   }

   /**
    * Get the labels of the output columns.
    *
    * @return the labels of the output columns
    */
   public String[] getOutputColumnLabels() { return outputColumnLabels; }

   /**
    * Get the data types of the output columns in the training table.
    *
    * @return the data types of the output columns in the training table
    *
    * @see    ncsa.d2k.modules.core.datatype.table.ColumnTypes
    */
   public int[] getOutputFeatureTypes() { return outputFeatureTypes; }

   /**
    * Describe the output.
    *
    * @param  i the index of the output
    *
    * @return the description of the output
    */
   public String getOutputInfo(int i) {
      String out =
         "The input set of examples, with extra columns of " +
         "predicitions added to the end.";

      return out;
   }

   /**
    * Get the name of the output.
    *
    * @param  i the index of the output
    *
    * @return the name of the output
    */
   public String getOutputName(int i) { return "Predictions"; }

   /**
    * The output is a PredictionTable.
    *
    * @return the output types
    */
   public String[] getOutputTypes() {
      String[] out = {
         "ncsa.d2k.modules.core.datatype.table.PredictionTable"
      };

      return out;
   }

   /**
    * Return a list of the property descriptions.
    *
    * @return a list of the property descriptions.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] =
         new PropertyDescription("applyTransformationsInPredict",
                                 "Apply all transformations before predicting",
                                 "Set this value to true if the data should" +
                                 "be transformed before attemping " +
                                 "to make predictions.");
      pds[1] =
         new PropertyDescription("applyReverseTransformationsAfterPredict",
                                 "Apply all reverse transformations after " +
                                 "predicting",
                                 "Set this value to true to reverse all " +
                                 "transformations after making " +
                                 "predictions.");
      pds[2] =
         new PropertyDescription("checkTableFormat",
                                 "Check format of incoming table to model.",
                                 "Check format of incoming table to model.");

      return pds;
   }

   /**
    * Determine which inputs were scalar.
    *
    * @return a boolean map with inputs that are scalar marked 'true'
    */
   public boolean[] getScalarInputs() { return scalarInputs; }

   /**
    * Determine which outputs were scalar.
    *
    * @return a boolean map with outputs that are scalar marked 'true'
    */
   public boolean[] getScalarOutputs() { return scalarOutputs; }

   /**
    * Get the size of the training set that built this model.
    *
    * @return the size of the training set used to build this model
    */
   public int getTrainingSetSize() { return trainingSetSize; }

   /**
    * @return a list of all the transformations that were performed
    */
   public List getTransformations() { return transformations; }

   /**
    * Predict the outcomes given a set of examples. The return value is a
    * PredictionTable, which is the same as the input set with extra column(s)
    * of predictions added to the end.
    *
    * @param  table the set of examples to make predictions on
    *
    * @return the input table, with extra columns for predictions
    *
    * @throws Exception Description of exception Exception.
    */
   public PredictionTable predict(Table table) throws Exception {
      PredictionTable pt = null;

      if (getCheckTableFormat()) {

         if (table instanceof PredictionTable) {

            // ensure that the inputFeatures and predictionSet are correct..
            pt = (PredictionTable) table;

            if (transformations != null && getApplyTransformationsInPredict()) {
               applyTransformations(pt);
            }

            Map columnToIndexMap = new HashMap(pt.getNumColumns());

            for (int i = 0; i < pt.getNumColumns(); i++) {
               columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));
               // ensure that the input features of pt are set correctly.
            }

            int[] curInputFeat = pt.getInputFeatures();
            boolean inok = true;

            if (curInputFeat != null) {

               if (curInputFeat.length != inputColumnLabels.length) {
                  inok = false;
               } else {

                  // for each input feature
                  for (int i = 0; i < curInputFeat.length; i++) {
                     String lbl = pt.getColumnLabel(curInputFeat[i]);

                     if (!lbl.equals(inputColumnLabels[i])) {
                        inok = false;
                     }

                     if (!inok) {
                        break;
                     }
                  }
               }
            } else {
               inok = false;
            }

            if (!inok) {

               // if the inputs are not ok, redo them
               int[] newInputFeat = new int[inputColumnLabels.length];

               for (int i = 0; i < inputColumnLabels.length; i++) {
                  Integer idx =
                     (Integer) columnToIndexMap.get(inputColumnLabels[i]);

                  if (idx == null) {

                     // the input column did not exist!!
                     throw new Exception("input column missing:index=" +
                                         i + ":label=" + inputColumnLabels[i]);
                  } else {
                     newInputFeat[i] = idx.intValue();
                  }
               }

               pt.setInputFeatures(newInputFeat);
            }

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;

            if (curPredFeat != null) {

               if (curPredFeat.length != outputColumnLabels.length) {
                  predok = false;
               } else {

                  // for each input feature
                  for (int i = 0; i < curPredFeat.length; i++) {
                     String lbl = pt.getColumnLabel(curPredFeat[i]);

                     if (
                         !lbl.equals(outputColumnLabels[i] +
                            PredictionTable.PREDICTION_COLUMN_APPEND_TEXT)) {
                        predok = false;
                     }

                     if (!predok) {
                        break;
                     }
                  }
               }
            } else {
               predok = false;
            }
         }
         // it was not a prediction table.  make it one and set the input
         // features and prediction set accordingly
         else {
            ExampleTable et = table.toExampleTable();

            if (transformations != null && getApplyTransformationsInPredict()) {
               applyTransformations(et);
            }

            // turn it into a prediction table
            pt = et.toPredictionTable();

            Map columnToIndexMap = new HashMap(pt.getNumColumns());

            for (int i = 0; i < pt.getNumColumns(); i++) {
               columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));
            }

            int[] inputFeat = new int[inputColumnLabels.length];

            for (int i = 0; i < inputColumnLabels.length; i++) {
               Integer idx =
                  (Integer) columnToIndexMap.get(inputColumnLabels[i]);

               if (idx == null) {

                  // the input column was missing, throw exception
                  throw new Exception("input column missing:index=" + i +
                                      ":label=" + inputColumnLabels[i]);
               } else {
                  inputFeat[i] = idx.intValue();
               }
            }

            pt.setInputFeatures(inputFeat);

            boolean outOk = true;
            int[] outputFeat = new int[outputColumnLabels.length];

            for (int i = 0; i < outputColumnLabels.length; i++) {
               Integer idx =
                  (Integer) columnToIndexMap.get(outputColumnLabels[i]);

               if (idx == null) {

                  // the input column was missing, throw exception
                  outOk = false;
               } else {
                  outputFeat[i] = idx.intValue();
               }
            }

            if (outOk) {
               pt.setOutputFeatures(outputFeat);
            }

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;

            if (curPredFeat != null) {

               if (curPredFeat.length != outputColumnLabels.length) {
                  predok = false;
               } else {

                  // for each input feature
                  for (int i = 0; i < curPredFeat.length; i++) {
                     String lbl = pt.getColumnLabel(curPredFeat[i]);

                     if (
                         !lbl.equals(outputColumnLabels[i] +
                              PredictionTable.PREDICTION_COLUMN_APPEND_TEXT)) {
                        predok = false;
                     }

                     if (!predok) {
                        break;
                     }
                  }
               }
            } else {
               predok = false;
            }

            if (!predok) {
               int[] predSet = new int[outputFeatureTypes.length];

               // add as many prediction columns as there are outputs
               for (int i = 0; i < outputFeatureTypes.length; i++) {

                  // add the prediction columns
                  int type = outputFeatureTypes[i];

                  TableFactory factory = pt.getTableFactory();
                  Column c = factory.createColumn(type);
                  c.addRows(pt.getNumRows());
                  pt.addColumn(c);
                  predSet[i] = pt.getNumColumns() - 1;
               } // end for

               pt.setPredictionSet(predSet);
            } // end if
         } // end if
      } else {

         /*
          * If we don't check the format.
          */
         if (pt instanceof PredictionTable) {
            pt = (PredictionTable) table;

            if (transformations != null && getApplyTransformationsInPredict()) {
               applyTransformations(pt);
            }
         } else {
            ExampleTable et = table.toExampleTable();

            if (transformations != null && getApplyTransformationsInPredict()) {
               applyTransformations(et);
            }

            // turn it into a prediction table
            pt = et.toPredictionTable();
         }
      }

      makePredictions(pt);

      if (
          transformations != null &&
             this.getApplyReverseTransformationsAfterPredict()) {
         applyReverseTransformations(pt);
      }

      return pt;
   } // end method predict

   public void setApplyReverseTransformationsAfterPredict(boolean b) {
      applyReverseTransformationsAfterPredict = b;
   }

   /**
    * Set the flag to indicate if transformations should be applied in the
    * predict method.
    *
    * @param b true if transformations should be applied to the dataset in the
    *          predict() method, false otherwise
    */
   public void setApplyTransformationsInPredict(boolean b) {
      applyTransformationsInPredict = b;
   }

   /**
    * Should the format of the table be checked to see if it is the same format
    * as the data we trained on?
    *
    * @param b true if the format should be checked, false otherwise
    */
   public void setCheckTableFormat(boolean b) { _checkFormat = b; }

  
   public void setTransformations(List trans) { transformations = trans; }
} // end class PredictionModelModule
