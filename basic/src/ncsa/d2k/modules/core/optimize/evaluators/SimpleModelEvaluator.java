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
package ncsa.d2k.modules.core.optimize.evaluators;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.sort.QuickSort;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;

import java.beans.PropertyVetoException;
import java.util.ArrayList;


/**
 * used to be ncsa.d2k.modules.core.optimize.SimpleModelEvaluator.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class SimpleModelEvaluator extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** ilterByPredictedOutput? Default false */
   private boolean filterByPredictedOutput = false;

   /** filterOutputLowerFraction, default = 0.2. */
   private double filterOutputLowerFraction = 0.2;

   /** filterOutputUpperFraction, default = 0.2. */
   private double filterOutputUpperFraction = 0.2;

   /** reportAverageError? Default false */
   private boolean reportAverageError = false;


   /** reportEveryPrediction? Default false */
   private boolean reportEveryPrediction = false;

   /** reportLineLabel. */
   private String reportLineLabel = "SimpleModelEvaluator: ";


   /** numProperties, default = 6. */
   int numProperties = 6;

   /** testTables. */
   ArrayList testTables = null;

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Initialize the testTables tow 200 entries.
    */
   public void beginExecution() { testTables = new ArrayList(200); }

   /**
    * Performs the main work of the module. {explain}
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module. <In this case, exceptions will be thrown in the
    *                   following scenarios>.
    */
   public void doit() throws Exception {

      if (this.getInputPipeSize(2) > 0) {
         testTables.add(this.pullInput(2));

         return;
      }

      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(0);
      PredictionModelModule model = (PredictionModelModule) this.pullInput(1);
      ExampleTable exampleTable = (ExampleTable) testTables.remove(0);

      ExampleTable examples = exampleTable;

      int numExamples = examples.getNumRows();
      int numInputs = examples.getNumInputFeatures();
      int numOutputs = examples.getNumOutputFeatures();

      // change to call make prediction

      PredictionTable predictionTable = exampleTable.toPredictionTable();
      model.predict(predictionTable);

      for (int e = 0; e < numExamples; e++) {

         if (reportEveryPrediction) {

            for (int o = 0; o < numOutputs; o++) {

               synchronized (System.out) {
                  System.out.println(reportLineLabel + "e = " + e +
                                     "  predicted = " +
                                     predictionTable.getDoublePrediction(e, o) +
                                     "  actual = " +
                                     exampleTable.getOutputDouble(e, o));
               }
            }
         }

      }

      // mark examples to be used for error calculation
      boolean[] useExamples = null;

      if (filterByPredictedOutput) {

         useExamples = new boolean[numExamples];

         double[][] results = new double[numExamples][2];

         for (int e = 0; e < numExamples; e++) {
            double outputSum = 0.0;

            for (int o = 0; o < numOutputs; o++) {
               outputSum += predictionTable.getDoublePrediction(e, 0);
            }

            double predicted = outputSum / numOutputs;

            results[e][0] = predicted;
            results[e][1] = e;
         }

         QuickSort.sort(results);

         int lowerNumExamples =
            (int) Math.round(filterOutputLowerFraction * numExamples);
         int upperNumExamples =
            (int) Math.round(filterOutputUpperFraction * numExamples);

         for (int e = 0; e < lowerNumExamples; e++) {
            useExamples[(int) results[e][1]] = true;
         }

         for (int e = 0; e < upperNumExamples; e++) {
            useExamples[(int) results[numExamples - 1 - e][1]] = true;
         }
      } // end if

      double errorSum = 0.0;
      int numPredictions = 0;

      for (int e = 0; e < numExamples; e++) {

         if (filterByPredictedOutput && !useExamples[e]) {
            continue;
         }

         errorSum += errorFunction.evaluate(examples, e, predictionTable);

         numPredictions++;
      }

      double error = errorSum / numPredictions;

      double[] utility = new double[1];
      utility[0] = error;

      if (reportAverageError) {

         synchronized (System.out) {

            if (exampleTable.getLabel() != null) {
               System.out.println(reportLineLabel + "(" +
                                  exampleTable.getLabel() + ")" +
                                  ErrorFunction.getErrorFunctionName(errorFunction.errorFunctionIndex) +
                                  " = " + utility[0]);
            } else {
               System.out.println(reportLineLabel +
                                  ErrorFunction.getErrorFunctionName(errorFunction.errorFunctionIndex) +
                                  " = " + utility[0]);
            }
         }
      }

      // push outputs //


      String[] names = new String[1];
      names[0] =
         ErrorFunction.getErrorFunctionName(errorFunction.errorFunctionIndex);

      ParameterPoint objectivePoint =
         ParameterPointImpl.getParameterPoint(names, utility);

      // objectivePoint.createFromData(names, utility);
      this.pushOutput(objectivePoint, 0);
      this.pushOutput(predictionTable, 1);

   } // end method doit

   /**
    * getFilterByPredictedOutput.
    *
    * @return The value.
    */
   public boolean getFilterByPredictedOutput() {
      return this.filterByPredictedOutput;
   }

   /**
    * getFilterOutputLowerFraction.
    *
    * @return The value.
    */
   public double getFilterOutputLowerFraction() {
      return this.filterOutputLowerFraction;
   }

   /**
    * getFilterOutputUpperFraction.
    *
    * @return The value.
    */
   public double getFilterOutputUpperFraction() {
      return this.filterOutputUpperFraction;
   }

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
            return "The error function to apply to predictions made by " +
                   "the model";

         case 1:
            return "The model used to make predictions";

         case 2:
            return "The examples to use for predictive error calculations";
      }

      return "";
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
            return "Error Function";

         case 1:
            return "Model";

         case 2:
            return "Example Table";
      }

      return "";
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
         "ncsa.d2k.modules.core.prediction.ErrorFunction",
         "ncsa.d2k.modules.PredictionModelModule",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "This module measures the predictive error of a model relative " +
             "to the given error function and example set.  " +
             "If Filter by Predicted Output is true, then only a subset of " +
             "the examples are used for the error measurement.  " +
             "This subset is selected by first applying the model to every " +
             "example, sorting the examples based on predicted output, " +
             "and then selecting the top and/or bottom fraction of examples " +
             "for error calculations.  ";
   }


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Simple Model Evaluator"; }

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
            return "A point in objective space indicating the predictive " +
                   "error of model";

         case 1:
            return "The prediction table generated to analyze the results.";
      }

      return "";
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
            return "Objective Parameter Point";

         case 1:
            return "Prediction Table";

      }

      return "";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.PredictionTable"
      };

      return out;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[numProperties];

      pds[0] =
         new PropertyDescription("reportEveryPrediction",
                                 "Report Every Prediction",
                                 "Report the error for each prediction for " +
                                 "each example");

      pds[1] =
         new PropertyDescription("reportAverageError",
                                 "Report Average Error",
                                 "Report the average error over all examples");

      pds[2] =
         new PropertyDescription("filterByPredictedOutput",
                                 "Filter By Predicted Output",
                                 "When only the top and/or bottom fraction " +
                                 "of the examples, ranked by predicted " +
                                 "output, are used for error assessment");

      pds[3] =
         new PropertyDescription("filterOutputLowerFraction",
                                 "Filter Output Lower Fraction",
                                 "The lower fraction of examples, ranked by " +
                                 "predicted output, to be used for error " +
                                 "assessment");

      pds[4] =
         new PropertyDescription("filterOutputUpperFraction",
                                 "Filter Output Upper Fraction",
                                 "The upper fraction of examples, ranked by " +
                                 "predicted output, to be used for error " +
                                 "assessment");

      pds[5] =
         new PropertyDescription("reportLineLabel",
                                 "Report Line Label",
                                 "The label printed at the beginning of each " +
                                 "report line");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * getReportAverageError.
    *
    * @return The value.
    */
   public boolean getReportAverageError() { return this.reportAverageError; }

   /**
    * getReportEveryPrediction.
    *
    * @return The value.
    */
   public boolean getReportEveryPrediction() {
      return this.reportEveryPrediction;
   }

   /**
    * getReportLineLabel.
    *
    * @return The value.
    */
   public String getReportLineLabel() { return this.reportLineLabel; }

   /**
    * getTestTables.
    *
    * @return The value.
    */
   public ArrayList getTestTables() { return testTables; }

   /**
    * This module is the collection point for a gather operation. The scatter
    * module is typically n-fold. In order to keep the input pipe receiving the
    * test tables from filling, and preventing the n-fold module from the the
    * input pipes to th reentrant compute modules full, we need to make sure we
    * suck all the input data out of this input as soon as it becomes available.
    *
    * @return this module is the collection point for a gather operation. The
    *         scatter module is typically n-fold. In order to keep the input
    *         pipe receiving the test tables from filling, and preventing the
    *         n-fold module from the the input pipes to th reentrant compute
    *         modules full, we need to make sure we suck all the input data out
    *         of this input as soon as it becomes available.
    */
   public boolean isReady() {

      if (this.getInputPipeSize(2) > 0) {
         return true;
      }

      // we have no test tables, check to see if we
      // have an error function and a model
      if (
          this.getInputPipeSize(0) > 0 &&
             this.getInputPipeSize(1) > 0 &&
             testTables.size() > 0) {
         return true;
      }

      return false;
   }

   /**
    * Set FilterByPredictedOutput.
    *
    * @param value The value.
    */
   public void setFilterByPredictedOutput(boolean value) {
      this.filterByPredictedOutput = value;
   }

   /**
    * Set FilterOutputLowerFraction.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException Illegal value.
    */
   public void setFilterOutputLowerFraction(double value)
      throws PropertyVetoException {

      if (value < 0.0) {
         throw new PropertyVetoException(" < 0.0", null);
      }

      if (value > 1.0) {
         throw new PropertyVetoException(" > 1.0", null);
      }

      if (value + filterOutputUpperFraction > 1.0) {
         throw new PropertyVetoException(" + filterOutputUpperFraction > 1.0",
                                         null);
      }

      this.filterOutputLowerFraction = value;
   }

   /**
    * Set FilterOutputUpperFraction.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException Illegal value.
    */
   public void setFilterOutputUpperFraction(double value)
      throws PropertyVetoException {

      if (value < 0.0) {
         throw new PropertyVetoException(" < 0.0", null);
      }

      if (value > 1.0) {
         throw new PropertyVetoException(" > 1.0", null);
      }

      if (value + filterOutputLowerFraction > 1.0) {
         throw new PropertyVetoException(" + filterOutputLowerFraction > 1.0",
                                         null);
      }

      this.filterOutputUpperFraction = value;
   }

   /**
    * Set ReportAverageError.
    *
    * @param value The value.
    */
   public void setReportAverageError(boolean value) {
      this.reportAverageError = value;
   }

   /**
    * Set ReportEveryPrediction.
    *
    * @param value The value.
    */
   public void setReportEveryPrediction(boolean value) {
      this.reportEveryPrediction = value;
   }

   /**
    * Set ReportLineLabel.
    *
    * @param value The value.
    */
   public void setReportLineLabel(String value) {
      this.reportLineLabel = value;
   }

   /**
    * Set TestTables.
    *
    * @param tt The table.
    */
   public void setTestTables(ArrayList tt) { this.testTables = tt; }
} // end class SimpleModelEvaluator
