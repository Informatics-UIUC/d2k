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

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.beans.PropertyVetoException;
import java.util.Random;


/**
 * this used to be ncsa.d2k.modules.core.optimize.MultiTrainTestBiasEvaluator.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class ModelingParametersEvaluator extends ComputeModule {

   //~ Static fields/initializers **********************************************

   /** LAM-tlr this is a patch. */
   static private final boolean useContinuous = false;

   //~ Instance fields *********************************************************

   /** BatchSize, default = 1. */
   private int BatchSize = 1;

   /** NumRepetitions, default = 10. */
   private int NumRepetitions = 10;

   /** NumTestExamples, default = 999999999. */
   private int NumTestExamples = 999999999;

   /** NumTrainExamples, default = -1. */
   private int NumTrainExamples = -1;

   /** RandomSeed, default = 123. */
   private int RandomSeed = 123;

   /** RecycleExamples? Default false. */
   private boolean RecycleExamples = false;

   /** ControlPoint. */
   ParameterPoint ControlPoint;

   /** errorFunction. */
   Object errorFunction = null;

   /** ExampleSet. */
   ExampleTable ExampleSet;

   /** ExampleSetIndex. */
   int ExampleSetIndex;

   /** InitialExecution. */
   boolean InitialExecution;

   /** numExamples. */
   int numExamples;

   /** numInputs. */
   int numInputs;

   /** numOutputs. */
   int numOutputs;

   /** numProperties. */
   int numProperties = 6;

   /** PhaseIndex. */
   int PhaseIndex;

   /** RandomizedIndices. */
   int[] RandomizedIndices = null;

   /** RandomNumberGenerator. */
   Random RandomNumberGenerator;

   /** UtilityIndex. */
   int UtilityIndex;

   /** UtilitySums. */
   double[] UtilitySums;

   /** UtilityValues. */
   ParameterPoint[] UtilityValues;

   //~ Methods *****************************************************************

   /**
    * This function returns a random integer between min and max (both
    * inclusive).
    *
    * @param  min Lower limit.
    * @param  max Upper limit.
    *
    * @return Random value.
    */
   int randomInt(int min, int max) {
      return (int) ((RandomNumberGenerator.nextDouble() * (max - min + 1)) +
                    min);
   }


   /**
    * Iniialize array with random numbers.
    *
    * @param  data        The array.
    * @param  numElements Number of elements in array.
    *
    * @throws Exception Description of exception Exception.
    */
   void randomizeIntArray(int[] data, int numElements) throws Exception {
      int temp;
      int rand_index;

      for (int i = 0; i < numElements - 1; i++) {
         rand_index = randomInt(i + 1, numElements - 1);
         temp = data[i];
         data[i] = data[rand_index];
         data[rand_index] = temp;
      }
   }

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Resets.
    */
   public void beginExecution() {
      InitialExecution = true;
      reset();
   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module. <In this case, exceptions will be thrown in the
    *                   following scenarios>.
    */
   public void doit() throws Exception {

      switch (PhaseIndex) {

         case 0:

            ControlPoint = (ParameterPoint) this.pullInput(0);

            if (InitialExecution || (!RecycleExamples)) {

               ExampleSet = (ExampleTable) this.pullInput(1);

               numExamples = ExampleSet.getNumRows();
               numInputs = ExampleSet.getNumInputFeatures();
               numOutputs = ExampleSet.getNumOutputFeatures();

               if (NumTrainExamples == -1) {
                  NumTrainExamples = numExamples - NumTestExamples;
               }

               if (NumTestExamples == -1) {
                  NumTestExamples = numExamples - NumTrainExamples;
               }

               if (NumTrainExamples + 1 > numExamples) {
                  System.out.println("NumTrainExamples + 1 > numExamples");
                  throw new Exception();
               }

               if (NumTrainExamples + NumTestExamples > numExamples) {
                  NumTestExamples = numExamples - NumTrainExamples;
               }

               if (
                   (RandomizedIndices == null) ||
                      (RandomizedIndices.length < numExamples)) {
                  RandomizedIndices = new int[numExamples];
               }

               InitialExecution = false;
            } // end if

            RandomNumberGenerator = new Random(RandomSeed);

            PhaseIndex = 1;

            break;

         case 1:

            if (
                ExampleSetIndex - UtilityIndex < BatchSize &&
                   ExampleSetIndex < NumRepetitions) {

               for (int e = 0; e < numExamples; e++) {
                  RandomizedIndices[e] = e;
               }

               randomizeIntArray(RandomizedIndices, numExamples);

               int[] trainSetIndicies = new int[NumTrainExamples];
               int[] testSetIndicies = new int[NumTestExamples];

               for (int e = 0; e < NumTrainExamples; e++) {
                  trainSetIndicies[e] = RandomizedIndices[e];
               }

               for (int e = 0; e < NumTestExamples; e++) {
                  testSetIndicies[e] = RandomizedIndices[e + NumTrainExamples];
               }


               Table currentTrainExampleSet;
               Table currentTestExampleSet;

               // LAM-tlr this is part of the patch. updated by dkt 4/26 to
               // allow for continuous tables, but with now compilation
               // dependency
               if (
                   ExampleSet.getClass().toString().equals("class ncsa.d2k.modules.core.datatype.table.continuous.ContinuousDoubleExampleTable")) {
                  currentTrainExampleSet =
                     (ExampleTable) ExampleSet.getSubset(trainSetIndicies);
                  currentTestExampleSet =
                     (ExampleTable) ExampleSet.getSubset(testSetIndicies);
               } else {
                  ExampleSet.setTestingSet(testSetIndicies);
                  ExampleSet.setTrainingSet(trainSetIndicies);
                  currentTrainExampleSet = ExampleSet.getTrainTable();
                  currentTestExampleSet = ExampleSet.getTestTable();
               }

               this.pushOutput(ControlPoint, 0);
               this.pushOutput(currentTrainExampleSet, 1);
               this.pushOutput(currentTestExampleSet, 2);

               ExampleSetIndex++;
            } else {
               PhaseIndex = 2;
            }

            break;

         case 2:

            ParameterPoint objectivePoint = (ParameterPoint) this.pullInput(2);

            int numUtilities = objectivePoint.getNumParameters();

            if (UtilityIndex == 0) {
               UtilityValues = new ParameterPoint[NumRepetitions];
               UtilitySums = new double[numUtilities];
            }

            UtilityValues[UtilityIndex] = objectivePoint;

            for (int i = 0; i < numUtilities; i++) {
               UtilitySums[i] += objectivePoint.getValue(i);
            }

            UtilityIndex++;

            if (UtilityIndex == NumRepetitions) {

               String[] names = new String[numUtilities];

               for (int i = 0; i < numUtilities; i++) {
                  names[i] = objectivePoint.getName(i);
               }

               double[] meanUtilityArray = new double[numUtilities];

               for (int i = 0; i < numUtilities; i++) {
                  meanUtilityArray[i] = UtilitySums[i] / NumRepetitions;
               }

               // Anca: changed over to parameter.impl.*  ParameterPoint
               // meanObjectivePoint = new ParameterPointImpl();
               // meanObjectivePoint.createFromData(names, meanUtilityArray);
               ParameterPoint meanObjectivePoint =
                  ParameterPointImpl.getParameterPoint(names, meanUtilityArray);
               this.pushOutput(meanObjectivePoint, 3);
               this.pushOutput(UtilityValues, 4);

               reset();
            } else {
               PhaseIndex = 1;
            }

            break;
      }

   } // end method doit

   /**
    * As name implies.
    *
    * @return The value.
    */
   public int getBatchSize() { return this.BatchSize; }

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
            return "The point in control space which is currently being evaluated";

         case 1:
            return "The example table used to generate training and testing " +
                   "example tables";

         case 2:
            return "The point in objective space resulting from evaluating the " +
                   "last pair of train and test tables";

         default:
            return "No such input!";
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
            return "Control Parameter Point";

         case 1:
            return "Example Table";

         case 2:
            return "Individual Objective Space Point";

         default:
            return "No such input!";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable",
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
      return "This module is the control center for evaluating a control space point that involves the generation of training and " +
             "testing example tables.  Random sampling is performed without replacement.  " +
             "The sum of training and testing examples can be less than the total number of examples but not more.";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Modeling Parameters Evaluator"; }

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public int getNumRepetitions() { return this.NumRepetitions; }

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public int getNumTestExamples() { return this.NumTestExamples; }

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public int getNumTrainExamples() { return this.NumTrainExamples; }

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
            return "The point in control space which is currently being evaluated and is replicated for each train test table set";

         case 1:
            return "The example table used for the training phase of the external evaluation process";

         case 2:
            return "The example table used for the testing phase of the external evaluation process";

         case 3:
            return "The point in objective space resulting from averaging (i.e., computing the centroid) of all the individual " +
                   "train/test objective space points";

         case 4:
            return "An array of objective space points for each train/test pairing";

         default:
            return "No such output!";
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
            return "Control Parameter Point";

         case 1:
            return "Train Example Table";

         case 2:
            return "Test Example Table";

         case 3:
            return "Averaged Objective Space Point";

         case 4:
            return "All Objective Space Points";

         default:
            return "No such output!";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint;",
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

      PropertyDescription[] pds = new PropertyDescription[numProperties];

      pds[0] =
         new PropertyDescription("numTrainExamples",
                                 "Num Train Examples",
                                 "The number of examples in each training set generated and if set to -1 all but Num Test Examples are used");

      pds[1] =
         new PropertyDescription("numTestExamples",
                                 "Num Test Examples",
                                 "The number of examples in each testing set generated and if set to -1 all but Num Train Examples are used");

      pds[2] =
         new PropertyDescription("numRepetitions",
                                 "Num Repetitions",
                                 "Number of train/test set combinations to generate");

      pds[4] =
         new PropertyDescription("randomSeed",
                                 "Random Seed",
                                 "The initial seed to the random number generator used to randomly select examples for training and testing sets");

      pds[3] =
         new PropertyDescription("batchSize",
                                 "Batch Size",
                                 "The maximum number of parallel evaluations to allow and must be equal or less than Num Repetitions");

      pds[5] =
         new PropertyDescription("recycleExamples",
                                 "Recycle Examples",
                                 "If true, a single example set is used by the module repeatedly for all subsequent module firings, otherwise " +
                                 "a new example set is used for each module firing");


      return pds;
   } // end method getPropertiesDescriptions

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public int getRandomSeed() { return this.RandomSeed; }

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public boolean getRecycleExamples() { return this.RecycleExamples; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {
      boolean value = false;

      switch (PhaseIndex) {

         case 0:

            if (InitialExecution || (!RecycleExamples)) {
               value = (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0);
            } else {
               value = (this.getFlags()[0] > 0);
            }

            break;

         case 1:
            value = true;

            break;

         case 2:
            value = (this.getFlags()[2] > 0);

            break;
      }

      return value;
   } // end method isReady

   /**
    * Reset.
    */
   public void reset() {
      PhaseIndex = 0;
      ExampleSetIndex = 0;
      UtilityIndex = 0;
   }

   /**
    * Set BatchSize.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value is < 1.
    */
   public void setBatchSize(int value) throws PropertyVetoException {

      if (!(value >= 1)) {
         throw new PropertyVetoException("< 1", null);
      }

      this.BatchSize = value;
   }

   /**
    * Set NumRepetitions.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value < 1.
    */
   public void setNumRepetitions(int value) throws PropertyVetoException {

      if (!(value >= 1)) {
         throw new PropertyVetoException("< 1", null);
      }

      this.NumRepetitions = value;
   }

   /**
    * Set NumTestExamples.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException Illegal value.
    */
   public void setNumTestExamples(int value) throws PropertyVetoException {

      if ((value == -1) && (NumTrainExamples == -1)) {
         throw new PropertyVetoException("both Num Test Examples and Num Test Examples cannot be -1",
                                         null);
      }

      if (((value != -1) && (value < 1))) {
         throw new PropertyVetoException("< 1", null);
      }

      this.NumTestExamples = value;
   }

   /**
    * Description of method setNumTrainExamples.
    *
    * @param  value Description of parameter value.
    *
    * @throws PropertyVetoException Description of exception
    *                               PropertyVetoException.
    */
   public void setNumTrainExamples(int value) throws PropertyVetoException {

      if ((value == -1) && (NumTestExamples == -1)) {
         throw new PropertyVetoException("both Num Test Examples and Num Train Examples cannot be -1",
                                         null);
      }

      if (((value != -1) && (value < 1))) {
         throw new PropertyVetoException("< 1", null);
      }

      this.NumTrainExamples = value;
   }

   /**
    * Set RandomSeed.
    *
    * @param value The value.
    */
   public void setRandomSeed(int value) { this.RandomSeed = value; }

   /**
    * Set RecycleExamples.
    *
    * @param value The value.
    */
   public void setRecycleExamples(boolean value) {
      this.RecycleExamples = value;
   }
} // end class ModelingParametersEvaluator
