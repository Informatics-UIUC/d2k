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
package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.core.modules.OrderedReentrantModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;


/**
 * This module applies a function inducer module to the given example table using the given error function
 * and with boosting to produce a model.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ApplyFunctionInducerWithBoosting extends OrderedReentrantModule {

   //~ Instance fields *********************************************************

   /** the number of rounds */
   private int NumberOfRounds = 100;

   //~ Methods *****************************************************************

   /**
    * Apply a function inducer to an example table with boosting.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      ExampleTable OriginalExamples = null;
      ExampleTable examples = null;

      FunctionInducerOpt FunctionInducer =
         (FunctionInducerOpt) this.pullInput(0);
      ErrorFunction ErrorFunction = (ErrorFunction) this.pullInput(1);

      OriginalExamples = (ExampleTable) this.pullInput(2);

      if (NumberOfRounds == 1) {
         examples = OriginalExamples;
      } else {
         examples = (ExampleTable) OriginalExamples.copy();

         // examples = OriginalExamples;
      }

      // !!! do i need this?
      // exampleSet = (ExampleTable) exampleSet.copy();

      Model model = null;
      Model[] models = new Model[NumberOfRounds];

      Model BoostedModel = null;

      for (int i = 0; i < NumberOfRounds; i++) {

         System.out.println("Round number " + (i + 1));
         model = FunctionInducer.generateModel(examples, ErrorFunction);

         ///////////////////////////
         // add model to ensemble //
         ///////////////////////////

         models[i] = model;

         BoostedModel =
            new EnsembleModel(OriginalExamples, models, i + 1,
                              EnsembleModel.SUM);

         ///////////////////////////////////////////
         // quit when last round has been reached //
         ///////////////////////////////////////////

         if (i == NumberOfRounds - 1) {
            break;
         }

         ////////////////////////////////
         // apply model to example set //
         ////////////////////////////////

         int NumExamples = examples.getNumRows();
         int numInputs = examples.getNumInputFeatures();
         int NumOutputs = examples.getNumOutputFeatures();

         // PredictionTable predictionTable = model.predict(examples.copy());
         PredictionTable predictionTable = examples.toPredictionTable();
         BoostedModel.predict(predictionTable);

         double errorSum = 0.0;
         int numPredictions = 0;

         for (int e = 0; e < NumExamples; e++) {

            // errorSum += errorFunction.evaluate(examples, e, model);

            errorSum +=
               ErrorFunction.evaluate(OriginalExamples, e, predictionTable);

            numPredictions++;
         }

         double error = errorSum / numPredictions;

         double[] utility = new double[1];
         utility[0] = error;

         synchronized (System.out) {

            if (examples.getLabel() != null) {
               System.out.println("(" + examples.getLabel() + ")" +
                                  ErrorFunction.getErrorFunctionName(ErrorFunction.errorFunctionIndex) +
                                  " = " + utility[0]);
            } else {
               System.out.println(ErrorFunction.getErrorFunctionName(ErrorFunction.errorFunctionIndex) +
                                  " = " + utility[0]);
            }
         }

         //////////////////////////
         // tranform example set //
         //////////////////////////
         int[] outputIndices = examples.getOutputFeatures();

         for (int e = 0; e < NumExamples; e++) {

            for (int o = 0; o < NumOutputs; o++) {

               double predicted = predictionTable.getDoublePrediction(e, o);
               double actual = OriginalExamples.getOutputDouble(e, o);
               double residual = actual - predicted;
               examples.setDouble(residual, e, outputIndices[o]);
            }
         }

      } // end for

      this.pushOutput(BoostedModel, 0);
   } // end method doit

   /**
    * Get a description of an input
    *
    * @param  i input index
    *
    * @return Description of the input
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The function inducer module used to generate the model,";

         case 1:
            return "The error function used to guide the function inducer.";

         case 2:
            return "The example table used for generating the model.";

         default:
            return "No such input";
      }
   }

   /**
    * Get the name of an input
    *
    * @param  i the input index
    *
    * @return Name of the input
    */
   public String getInputName(int i) {

      switch (i) {

         case 0:
            return "Function Inducer";

         case 1:
            return "Error Function";

         case 2:
            return "Examples";

         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.prediction.FunctionInducerOpt",
         "ncsa.d2k.modules.core.prediction.ErrorFunction",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }

   /**
    * Description of the module's function
    *
    * @return Description of the module's function
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module applies a function inducer module to the given " +
             "      example table using the given error function and with boosting to " +
             "      produce a model." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: This module uses the given function inducer to " +
             "      build an ordered series of different models. Each round, a new set of " +
             "      examples is created by first forming a model with the current set of " +
             "      examples, then using the model to predict each output, and then replacing " +
             "      the current output variable with the difference between the prediction " +
             "      and actual output values. In this way, each model tries to predict the " +
             "      difference left over after applying the previous model in the series." +
             "    </p>";
   }

   /**
    * the name of this module
    *
    * @return the name of this module
    */
   public String getModuleName() {
      return "Apply Function Inducer With Boosting";
   }

   /**
    * Get the number of rounds
    *
    * @return the number of rounds
    */
   public int getNumberOfRounds() { return this.NumberOfRounds; }

   /**
    * Description of the outputs
    *
    * @param  i output index
    *
    * @return the description of the output
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The model generated from the example table and the error function.";

         default:
            return "No such output";
      }
   }

   /**
    * the name of the output
    *
    * @param  i the output index
    *
    * @return name of the output
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Model";

         default:
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.model.Model" };

      return types;
   }

   /**
    * Get the properties descriptions
    *
    * @return the properties descriptions
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] retVal = new PropertyDescription[1];
      retVal[0] =
         new PropertyDescription("numberOfRounds", "Number of rounds",
                                 "The number of models to generate.");

      return retVal;
   }

   /**
    * Set the number of rounds
    *
    * @param value the number of rounds
    */
   public void setNumberOfRounds(int value) { this.NumberOfRounds = value; }
} // end class ApplyFunctionInducerWithBoosting
