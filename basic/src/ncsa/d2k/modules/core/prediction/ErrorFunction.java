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

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.util.*;


/**
 * Function used to compute error on a set of examples.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ErrorFunction implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -1202852617341442400L;

   /** classification error. */
   static public final int classificationErrorFunctionIndex = 0;

   /** absolute error */
   static public final int absoluteErrorFunctionIndex = 1;

   /** variance error */
   static public final int varianceErrorFunctionIndex = 2;

   /** likelihood error */
   static public final int likelihoodErrorFunctionIndex = 3;

   /** the names of the functions */
   static public final String[] errorFunctionNames =
   {
      "classification",
      "absolute",
      "variance",
      "likelihood",
   };

   //~ Instance fields *********************************************************

   /** all state ratios */
   //double[] AllstateRatios;

   /** outputs memory */
   //double[] outputsMemory = new double[0];

   /** the error function to use */
   public int errorFunctionIndex;

   /** Description of field errorFunctionObjectPathName. */
   //public String errorFunctionObjectPathName;

   //~ Constructors ************************************************************

   /**
    * Creates a new ErrorFunction object.
    *
    * @param errorFunctionIndex the error function to use, should be
    *   ErrorFunction.classificationErrorFunctionIndex,
    *   ErrorFunction.absoluteErrorFunctionIndex,
    *   ErrorFunction.varianceErrorFunctionIndex, or
    *   ErrorFunction.likelihoodErrorFunctionIndex
    */
   public ErrorFunction(int errorFunctionIndex) {
      this.errorFunctionIndex = errorFunctionIndex;
   }

   /**
    * Creates a new ErrorFunction object.
    *
    * @param parameterPoint ParameterPoint containing the error function
    */
   public ErrorFunction(ParameterPoint parameterPoint) {
      this.errorFunctionIndex = (int) parameterPoint.getValue(0);
   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   //~ Methods *****************************************************************

   /**
    * Find the index of an error function given the name
    *
    * @param  name name of error function
    *
    * @return error function index
    *
    * @throws Exception when something goes wrong
    */
   static public int getErrorFunctionIndex(String name) throws Exception {
      int index = -1;

      for (int i = 0; i < errorFunctionNames.length; i++) {

         if (name.equalsIgnoreCase(errorFunctionNames[i])) {
            index = i;

            break;
         }
      }

      return index;
   }

   /**
    * Get the name of the given error function
    *
    * @param  i error function
    *
    * @return Description of error function
    *
    * @throws Exception when something goes wrong
    */
   static public String getErrorFunctionName(int i) throws Exception {
      return errorFunctionNames[i];
   }

   /**
    * Get the names of the error functions
    *
    * @return the names of the error functions
    */
   static public String[] getErrorFunctionNames() { return errorFunctionNames; }

   /**
    * compute the error.
    *
    * @param  examples          the actual examples
    * @param  predictedExamples the predicted values
    *
    * @return the error
    *
    * @throws Exception when something goes wrong
    */
   public double evaluate(ExampleTable examples,
                          PredictionTable predictedExamples) throws Exception {
      int numExamples = examples.getNumRows();

      double errorSum = 0.0;

      for (int e = 0; e < numExamples; e++) {
         errorSum += evaluate(examples, e, predictedExamples);
      }

      return errorSum / numExamples;
   }

   /**
    * Compute the error.
    *
    * @param  actualExamples    the actual examples
    * @param  exampleIndex      the example of interest
    * @param  predictedExamples the predicted values
    *
    * @return the error
    *
    * @throws Exception when something goes wrong
    */
   public double evaluate(ExampleTable actualExamples, int exampleIndex,
                          PredictionTable predictedExamples) throws Exception {

//      int numInputs = actualExamples.getNumInputFeatures();
      int numOutputs = actualExamples.getNumOutputFeatures();

      double error = Double.NaN;

      // allocate temporary memory if necessary
      /*
       * if (outputsMemory.length != numOutputs) outputsMemory = new
       * double[numOutputs];
       */

      switch (errorFunctionIndex) {

         case classificationErrorFunctionIndex: {
            double errorSum = 0.0;

            // double[] outputs = outputsMemory;

            // model.evaluate(examples, exampleIndex, outputs);

            if (numOutputs == 1) {

               if (
                   Math.round(actualExamples.getOutputDouble(exampleIndex,
                                                                0)) !=
                      Math.round(predictedExamples.getDoublePrediction(exampleIndex,
                                                                          0))) {
                  errorSum++;
               }
            } else {
               double maxPredictedOutput = Double.NEGATIVE_INFINITY;
               double maxActualOutput = Double.NEGATIVE_INFINITY;
               int maxPredictedOutputIndex = -1;
               int maxActualOutputIndex = -1;

               for (int f = 0; f < numOutputs; f++) {
                  double predictedOutput =
                     predictedExamples.getDoublePrediction(exampleIndex, f);
                  double actualOutput =
                     actualExamples.getOutputDouble(exampleIndex, f);

                  if (predictedOutput > maxPredictedOutput) {
                     maxPredictedOutput = predictedOutput;
                     maxPredictedOutputIndex = f;
                  }

                  if (actualOutput > maxActualOutput) {
                     maxActualOutput = actualOutput;
                     maxActualOutputIndex = f;
                  }
               }

               if (maxPredictedOutputIndex != maxActualOutputIndex) {
                  errorSum++;
               }
            } // end if

            error = errorSum;
         }

         break;

         // Absolute Error //
         case absoluteErrorFunctionIndex: {
            double errorSum = 0.0;

            for (int f = 0; f < numOutputs; f++) {
               double difference =
                  actualExamples.getOutputDouble(exampleIndex, f) -
                  predictedExamples.getDoublePrediction(exampleIndex, f);
               errorSum += Math.abs(difference);
            }

            error = errorSum;
         }

         break;

         // Variance Error //
         case varianceErrorFunctionIndex: {
            double errorSum = 0.0;

            for (int f = 0; f < numOutputs; f++) {
               double difference =
                  actualExamples.getOutputDouble(exampleIndex, f) -
                  predictedExamples.getDoublePrediction(exampleIndex, f);
               errorSum += difference * difference;
            }

            error = errorSum;
         }

         break;

         case likelihoodErrorFunctionIndex: {

            double likelihoodSum = 0.0;

            for (int f = 0; f < numOutputs; f++) {

               double actualOutputClassProbability =
                  actualExamples.getOutputDouble(exampleIndex, f);
               double predictedOutputClassProbability =
                  predictedExamples.getDoublePrediction(exampleIndex, f);

               if (actualOutputClassProbability == 0.0) {
                  predictedOutputClassProbability =
                     1.0 - predictedOutputClassProbability;
               }

               likelihoodSum += Math.log(predictedOutputClassProbability);
            }

            error = -likelihoodSum;
         }

         break;

         default: {
        	 myLogger.debug("errorFunctionIndex (" + errorFunctionIndex +
                     ") not recognized");
            error = Double.NaN;
         }

         break;
      }

      return error;
   } // end method evaluate


} // end class ErrorFunction
