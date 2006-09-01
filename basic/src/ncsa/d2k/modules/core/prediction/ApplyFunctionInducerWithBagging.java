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
import ncsa.d2k.modules.core.prediction.evaluators.Utility;

import java.util.Random;


/**
 * This module applies a function inducer module to the given example table
 * using the given error function and with boosting to produce a ensemble of
 * models.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ApplyFunctionInducerWithBagging extends OrderedReentrantModule {

   //~ Instance fields *********************************************************

   /** This is the number of models that will be included in the final ensemble. */
   private int NumberOfModelsInEnsemble = 10;

   /** This determins the number of subsamples that will be taken to produce the models. */
   private int NumSubSampleExamples = 100;

   /** randomized instances. */
   private int[] RandomizedIndices = null;

   /** A random number generator. */
   private Random RandomNumberGenerator;

   /** If not -1, will generate the same sequence of values at each run. */
   private int RandomSeed = 123;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      ExampleTable OriginalExamples = null;
      ExampleTable Examples = null;

      FunctionInducerOpt FunctionInducer =
         (FunctionInducerOpt) this.pullInput(0);
      ErrorFunction ErrorFunction = (ErrorFunction) this.pullInput(1);

      OriginalExamples = (ExampleTable) this.pullInput(2);

      ////////////////////////////////////////////////////////////////////////
      // create copy of example set if destructive modification is required //
      ////////////////////////////////////////////////////////////////////////

      if (NumberOfModelsInEnsemble == 1) {
         Examples = OriginalExamples;
      } else {
         Examples = (ExampleTable) OriginalExamples.copy();
      }

      int NumExamples = Examples.getNumRows();

      // examples = OriginalExamples;

      // !!! do i need this?
      // exampleSet = (ExampleTable) exampleSet.copy();

      Model model = null;
      Model[] models = new Model[NumberOfModelsInEnsemble];

      RandomNumberGenerator = new Random(RandomSeed);

      if (
          (RandomizedIndices == null) ||
             (RandomizedIndices.length != NumExamples)) {
         RandomizedIndices = new int[NumExamples];
      }


      for (int i = 0; i < NumberOfModelsInEnsemble; i++) {

         // System.out.println("Round number " + (i + 1));

         ///////////////////////////////////////////
         // create random subsample to learn from //
         ///////////////////////////////////////////

         // Utility.randomizeIntArray(RandomNumberGenerator, RandomizedIndices,
         // NumExamples);
         for (int e = 0; e < NumExamples; e++) {
            RandomizedIndices[e] = e;
         }

         Utility.randomizeIntArray(RandomNumberGenerator, RandomizedIndices,
                                   NumExamples);

         int[] SubSampleExampleIndicies = new int[NumSubSampleExamples];

         for (int e = 0; e < NumSubSampleExamples; e++) {
            SubSampleExampleIndicies[e] = RandomizedIndices[e];
            // System.out.println(SubSampleExampleIndicies[e]);
         }

         ExampleTable CurrentTrainExamples;
         String ExampleSetClass = Examples.getClass().toString();

         if (
             ExampleSetClass.equals(
                     "class ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable")) {
            CurrentTrainExamples =
               (ExampleTable) Examples.getSubset(SubSampleExampleIndicies);
         } else {
            System.out.println("Warning!  Inefficient example table.");
            Examples.setTrainingSet(SubSampleExampleIndicies);
            CurrentTrainExamples = (ExampleTable) Examples.getTrainTable();
         }

         ///////////////////////////////////
         // create next model in ensemble //
         ///////////////////////////////////

         models[i] =
            FunctionInducer.generateModel(CurrentTrainExamples, ErrorFunction);

      } // end for

      this.pushOutput(new EnsembleModel(Examples, models,
                                        NumberOfModelsInEnsemble,
                                        EnsembleModel.AVERAGE), 0);
   } // end method doit

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
            return "The function inducer module used to generate the model.";

         case 1:
            return "The error function used to guide the function inducer.";

         case 2:
            return "The example table used for generating the model.";

         default:
            return "No such input";
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
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
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
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "Overview: This module applies a function inducer module to the "+
              "given example table using the given error function and with " +
              "boosting to produce a ensemble of models." +
             "</p>" +
             "<p>" +
             "Detailed Description: This module will generate several models "+
             "using the given function inducer and error function. The "+
             "resulting models are then combined into an ensemble model that "+
             "can be used to provide a possibly more accurate prediction. "+
             "The &quot;Number Subsamples&quot; determines how many times the "+
             "data is subsampled. The &quot;Number Final Models&quot; "+
             "determines how many models will exist in the final ensemble "+
             "model. Random set can be set to some non-negative value to set "+
             "the output of the random number generator to the same sequence "+
             "of values for each invocation.</p>" +
             "<p>References:<A name=\"Breiman:1994:bagging\"></A>Leo Breiman. " +
             "<A href=\"http://www.work.caltech.edu/cs156/01/papers/bagging.ps.gz\">" +
             "Bagging predictors</A>. Technical Report 421, Department of Statistics, " +
             "University of California at Berkeley, September 1994." +
             "		</p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() {
      return "Apply Function Inducer With Bagging";
   }

   /**
    * Get the number of models in the ensemble.
    *
    * @return the number of models in the ensemble
    */
   public int getNumberOfModelsInEnsemble() {
      return this.NumberOfModelsInEnsemble;
   }

   /**
    * Get the number of subsample examples.
    *
    * @return the number of subsample examples
    */
   public int getNumSubSampleExamples() { return this.NumSubSampleExamples; }


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
            return "The model generated from the example table and the error function.";

         default:
            return "No such output";
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
            return "Model";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.model.Model" };

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
         new PropertyDescription("numSubSampleExamples",
                                 "Number Subsamples",
                                 "This determins the number of subsamples that "+
                                         "will be taken to produce the models.");
      pds[1] =
         new PropertyDescription("numberOfModelsInEnsemble",
                                 "Number Final Models",
                                 "This is the number of models that will be "+
                                         "included in the final ensemble.");
      pds[2] =
         new PropertyDescription("randomSeed",
                                 "Random Seed",
                                 "If not -1, will generate the same sequence "+
                                         "of values at each run.");

      return pds;
   }

   /**
    * Get the random seed.
    *
    * @return the random seed value
    */
   public int getRandomSeed() { return this.RandomSeed; }

   /**
    * Set the number of models in the ensemble.
    *
    * @param value the number of models in the ensemble.
    */
   public void setNumberOfModelsInEnsemble(int value) {
      this.NumberOfModelsInEnsemble = value;
   }

   /**
    * Set the number of subsample examples.
    *
    * @param value the number of subsample examples
    */
   public void setNumSubSampleExamples(int value) {
      this.NumSubSampleExamples = value;
   }

   /**
    * Set the random seed.
    *
    * @param value the random seed value
    */
   public void setRandomSeed(int value) { this.RandomSeed = value; }


} // end class ApplyFunctionInducerWithBagging
