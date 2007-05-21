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
package ncsa.d2k.modules.core.optimize.optimizers;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.beans.PropertyVetoException;
import java.util.Random;
import ncsa.d2k.modules.core.util.*;


/**
 * used to be ncsa.d2k.modules.core.optimize.random.UniformSampling.
 *
 * @author  DavidTcheng
 * @version 1.0
 */
public class RandomOptimizer extends ComputeModule
   implements java.io.Serializable {

   //~ Instance fields *********************************************************

   /** InitialExecution? Default = true. */
   private boolean InitialExecution = true;

   /** MaxNumIterations. Default = 10. */
   private int MaxNumIterations = 10;

   /** ObjectiveScoreDirection. Default = -1. */
   private int ObjectiveScoreDirection = -1;

   /** ObjectiveScoreOutputFeatureNumber. Default = 1. */
   private int ObjectiveScoreOutputFeatureNumber = 1;

   /** randomNumberGenerator. */
   private Random randomNumberGenerator = null;

   /** RandomSeed. Default = 123. */
   private int RandomSeed = 123;

   /** StopObjectiveScoreThreshold. Default = 0.0. */
   private double StopObjectiveScoreThreshold = 0.0;

   /** Trace? Default = false. */
   private boolean Trace = false;

   /** BestExampleIndex. Initialy MIN_VALUE */
   int BestExampleIndex = Integer.MIN_VALUE;

   /** BestUtility. Iniitiall = 0 */
   double BestUtility = 0;

   /** String [] BiasSpaceDimensionNames;. */
   double[] Bias;

   /** BiasSpace. */
   ParameterSpace BiasSpace;

   /** ExampleData. */
   double[][] ExampleData;

   /** int BiasSpaceNumDimensions;. */
   double[][][] InitialExampleSet;

   /** InitialNumExamples. */
   int InitialNumExamples;

   /** inputNames. */
   String[] inputNames;

   /** inputs. */
   int[] inputs;

   /** ContinuousDoubleExampleTable ExampleSet; ExampleTable ExampleSet;. */
   int NumExamples;

   /** NumExperimentsCompleted. Initially = 0. */
   int NumExperimentsCompleted = 0;

   /** outputNames. */
   String[] outputNames;

   /** outputs. */
   int[] outputs;

   //~ Methods *****************************************************************

   /**
    * Given a two d array of doubles, create a table.
    *
    * @param  data        Data.
    * @param  inputNames  Input Names.
    * @param  outputNames Output Names.
    * @param  inputs      inputs.
    * @param  outputs     outputs.
    * @param  count       count.
    *
    * @return given a two d array of doubles, create a table.
    */
   static public ExampleTable getTable(double[][] data, String[] inputNames,
                                       String[] outputNames, int[] inputs,
                                       int[] outputs,
                                       int count) {
      Column[] cols = new Column[data.length];
      int index = 0;

      for (int i = 0; i < inputs.length; i++, index++) {

         if (data.length != count) {
            double[] tmp = new double[count];
            System.arraycopy(data[index], 0, tmp, 0, count);
            data[index] = tmp;
         }

         cols[index] = new DoubleColumn(data[index]);
         cols[index].setLabel(inputNames[i]);
      }

      for (int i = 0; i < outputs.length; i++, index++) {

         if (data.length != count) {
            double[] tmp = new double[count];
            System.arraycopy(data[index], 0, tmp, 0, count);
            data[index] = tmp;
         }

         cols[index] = new DoubleColumn(data[index]);
         cols[index].setLabel(outputNames[i]);
      }

      MutableTable mt = new MutableTableImpl(cols);
      ExampleTable et = mt.toExampleTable();
      et.setInputFeatures(inputs);
      et.setOutputFeatures(outputs);

      return et;
   } // end method getTable

   private D2KModuleLogger myLogger;

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Initialize the data structures.
    */
   public void beginExecution() {
	   
	   myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

      InitialExecution = true;
      ExampleData = null;
      NumExamples = 0;

      if (ObjectiveScoreDirection == 1) {
         BestUtility = Double.NEGATIVE_INFINITY;
      } else {
         BestUtility = Double.POSITIVE_INFINITY;
      }

      BestExampleIndex = Integer.MIN_VALUE;
      randomNumberGenerator = new Random(RandomSeed);
   }

   /**
    * Performs the main work of the module. {explain}
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module. <In this case, exceptions will be thrown in the
    *                   following scenarios>.
    */
   public void doit() {

      if (InitialExecution) {
         BiasSpace = (ParameterSpace) this.pullInput(0);
         InitialExecution = false;
      } else {

         Example example = (Example) this.pullInput(1);

         if (ExampleData == null) {
            NumExamples = 0;
            ExampleData =
               new double[BiasSpace.getNumParameters() +
                  ((ExampleTable) example.getTable()).getNumOutputFeatures()][MaxNumIterations];
            inputs = new int[BiasSpace.getNumParameters()];
            outputs =
               new int[((ExampleTable) example.getTable())
                     .getNumOutputFeatures()];

            int index = 0;

            for (; index < inputs.length; index++) {
               inputs[index] = index;
            }

            for (int i = 0; i < outputs.length; index++, i++) {
               outputs[i] = index;
            }

            inputNames = new String[BiasSpace.getNumParameters()];

            for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
               inputNames[i] = BiasSpace.getName(i);
            }

            outputNames =
               new String[((ExampleTable) example.getTable())
                     .getNumOutputFeatures()];

            for (
                 int i = 0;
                    i <
                    ((ExampleTable) example.getTable()).getNumOutputFeatures();
                    i++) {
               outputNames[i] =
                  ((ExampleTable) example.getTable()).getOutputName(i);
            }
         } // end if

         // add example to set
         int index = 0;

         for (
              int i = 0;
                 i < ((ExampleTable) example.getTable()).getNumInputFeatures();
                 i++) {
            ExampleData[index++][NumExamples] = example.getInputDouble(i);
         }

         for (
              int i = 0;
                 i < ((ExampleTable) example.getTable()).getNumOutputFeatures();
                 i++) {
            ExampleData[index++][NumExamples] = example.getOutputDouble(i);
         }

         NumExamples++;

         // update best solution so far
         int outputFeature2Score =
            inputs.length + (ObjectiveScoreOutputFeatureNumber - 1);

         for (int e = NumExamples - 1; e < NumExamples; e++) {
            double utility = ExampleData[outputFeature2Score][e];

            if (ObjectiveScoreDirection == 1) {

               if (utility > BestUtility) {
                  BestUtility = utility;
                  BestExampleIndex = e;
               }
            } else {

               if (utility < BestUtility) {
                  BestUtility = utility;
                  BestExampleIndex = e;
               }
            }
         }

      } // end if

      // //////////////////////////
      // test stopping criteria //
      // //////////////////////////

      boolean stop = false;

      if (NumExamples > 0) {

         if (
             (ObjectiveScoreDirection == 1) &&
                (BestUtility >= StopObjectiveScoreThreshold)) {
            stop = true;
         }

         if (
             (ObjectiveScoreDirection == -1) &&
                (BestUtility <= StopObjectiveScoreThreshold)) {
            stop = true;
         }

         if (NumExamples >= MaxNumIterations) {
            stop = true;
         }

         if (BiasSpace.getNumParameters() == 0) {
        	 myLogger.debug("Halting execution of optimizer after on " +
                     "iteration because numParameters = 0.  ");
            stop = true;
         }
      }

      // ///////////////////////////////////////
      // quit when necessary and push result //
      // ///////////////////////////////////////
      if (stop) {

         if (Trace) {

        	 myLogger.debug("Optimization Completed");
        	 myLogger.debug("  Number of Experiments = " + NumExamples);
        	 
        	 myLogger.debug("NumExamples............ " + NumExamples);
        	 myLogger.debug("ObjectiveScoreDirection....... " +
                     ObjectiveScoreDirection);
        	 myLogger.debug("BestUtility............ " + BestUtility);
        	 myLogger.debug("BestExampleNumber...... " +
                     (BestExampleIndex + 1));
         }

         // add example to set
         double[][] data = new double[ExampleData.length][1];
         int index = 0;

         for (int i = 0; i < ExampleData.length; i++) {
            data[index++][0] = ExampleData[i][BestExampleIndex];
         }

         // ANCA: was this.getTable()
         ExampleTable optimalExampleSet =
            getTable(data, inputNames, outputNames, inputs, outputs, 1);
         ExampleTable exampleSet =
            getTable(ExampleData, inputNames, outputNames, inputs, outputs,
                     NumExamples);
         this.pushOutput(optimalExampleSet, 1);
         this.pushOutput(exampleSet, 2);
         beginExecution();

         return;
      } // end if

      // ////////////////////////////////////////////
      // generate next point in bias space to try //
      // ////////////////////////////////////////////

      double[] point = new double[BiasSpace.getNumParameters()];

      // use uniform random sampling to constuct point
      for (int d = 0; d < BiasSpace.getNumParameters(); d++) {

         double range = BiasSpace.getMaxValue(d) - BiasSpace.getMinValue(d);

         switch (BiasSpace.getType(d)) {

            case ColumnTypes.DOUBLE:
               point[d] =
                  BiasSpace.getMinValue(d) +
                  range * randomNumberGenerator.nextDouble();

               break;

            case ColumnTypes.FLOAT:
               point[d] =
                  BiasSpace.getMinValue(d) +
                  range * randomNumberGenerator.nextFloat();

               break;

            case ColumnTypes.INTEGER:

               if ((int) range == 0) {
                  point[d] = BiasSpace.getMinValue(d);
               } else {
                  point[d] =
                     BiasSpace.getMinValue(d) +
                     randomNumberGenerator.nextInt((int) (range + 1));
               }

               break;

            case ColumnTypes.BOOLEAN:

               if ((int) range == 0) {
                  point[d] = BiasSpace.getMinValue(d);
               } else {
                  point[d] =
                     BiasSpace.getMinValue(d) +
                     randomNumberGenerator.nextInt((int) (range + 1));
               }

               break;

         }

      } // end for

      String[] names = new String[BiasSpace.getNumParameters()];

      for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
         names[i] = BiasSpace.getName(i);
      }

      ParameterPoint parameterPoint =
         ParameterPointImpl.getParameterPoint(names, point);
      this.pushOutput(parameterPoint, 0);

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
            return "The Control Parameter Space to search";

         case 1:
            return "The Example created by combining the Parameter Point and " +
                   "the objective scores";
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
            return "Control Parameter Space";

         case 1:
            return "Example";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
         "ncsa.d2k.modules.core.datatype.table.Example"
      };

      return in;
   }

   /**
    * Get MaxNumIterations.
    *
    * @return The value.
    */
   public int getMaxNumIterations() { return this.MaxNumIterations; }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleInfo() {
      return "This module implements a simple random sampling optimizer which " +
             "selects points according to a uniform " +
             "distribution over the parameter space.  Every point in the space " +
             "has equal likelihood of being selected.  ";

   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Random Optimizer"; }

   /**
    * Get ObjectiveScoreDirection.
    *
    * @return The value.
    */
   public int getObjectiveScoreDirection() {
      return this.ObjectiveScoreDirection;
   }

   /**
    * Get ObjectiveScoreOutputFeatureNumber.
    *
    * @return The value.
    */
   public int getObjectiveScoreOutputFeatureNumber() {
      return this.ObjectiveScoreOutputFeatureNumber;
   }

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
            return "The next Parameter Point selected for evaluation";

         case 1:
            return "An example table consisting of only the Optimal Example(s)";

         case 2:
            return "An example table consisting of all Examples generated " +
                   "during optimization";
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
            return "Parameter Point";

         case 1:
            return "Optimal Example Table";

         case 2:
            return "Complete Example Table";
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
         "ncsa.d2k.modules.core.datatype.table.ExampleTable",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
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

      PropertyDescription[] pds = new PropertyDescription[6];

      pds[0] =
         new PropertyDescription("objectiveScoreOutputFeatureNumber",
                                 "Objective Score Output Feature Number",
                                 "Selects which example output feature is " +
                                 "used to denote the objective score of " +
                                 "the Parameter Point.  ");

      pds[1] =
         new PropertyDescription("objectiveScoreDirection",
                                 "Objective Score Direction",
                                 "Determines whether the objective score " +
                                 "is to be minimized (-1) or maximized (1).  ");

      pds[2] =
         new PropertyDescription("stopObjectiveScoreThreshold",
                                 "Stop Utility Threshold",
                                 "Optimization halts when an example is " +
                                 "generated with an objective score which is " +
                                 "greater or less than threshold depending " +
                                 "on Objective Score Direction.  ");

      pds[3] =
         new PropertyDescription("maxNumIterations",
                                 "Maximum Number of Iterations",
                                 "Optimization halts when this limit on the " +
                                 "number of iterations is exceeded.  ");

      pds[4] =
         new PropertyDescription("randomSeed",
                                 "Random Number Generator Initial Seed",
                                 "This integer is use to seed the random " +
                                 "number generator which is used to select " +
                                 "points in parameter space.  ");

      pds[5] =
         new PropertyDescription("trace", "Trace",
                                 "Report extra information during execution " +
                                 "to trace the modules execution.  ");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Get RandomSeed.
    *
    * @return The value.
    */
   public int getRandomSeed() { return this.RandomSeed; }

   /**
    * Get StopObjectiveScoreThreshold.
    *
    * @return The value.
    */
   public double getStopObjectiveScoreThreshold() {
      return this.StopObjectiveScoreThreshold;
   }

   /**
    * Get Trace.
    *
    * @return The value.
    */
   public boolean getTrace() { return this.Trace; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {
      boolean value = false;

      if (InitialExecution) {
         value = (this.getFlags()[0] > 0);
      } else {
         value = (this.getFlags()[1] > 0);
      }

      return value;
   }

   /**
    * Set MaxNumIterations.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value < 1..
    */
   public void setMaxNumIterations(int value) throws PropertyVetoException {
     try {
       Integer.parseInt("" + value);
     }
     catch (Exception e) {
       throw new PropertyVetoException(
           "Maximum Number of Iterations should be an integer greater than zero.", null);
     }
      if (value < 1) {
         throw new PropertyVetoException("Maximum Number of Iterations should be an integer greater than zero.", null);
      }

      this.MaxNumIterations = value;
   }

   /**
    * Set ObjectiveScoreDirection.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value is not -1 or 1.
    */
   public void setObjectiveScoreDirection(int value)
      throws PropertyVetoException {

      if (!((value == -1) || (value == 1))) {
         throw new PropertyVetoException("Objective Score Direction must be -1 or 1", null);
      }

      this.ObjectiveScoreDirection = value;
   }

   /**
    * Set ObjectiveScoreOutputFeatureNumber.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value < 1.
    */
   public void setObjectiveScoreOutputFeatureNumber(int value)
      throws PropertyVetoException {
    try{
           Integer.parseInt(""+value);
         }catch(Exception e){
           throw new PropertyVetoException("Objective Score Output Feature Number should be an integer greater than zero.", null);
     }
      if (value < 1) {
         throw new PropertyVetoException("Objective Score Output Feature Number should be an integer greater than zero.", null);
      }

      this.ObjectiveScoreOutputFeatureNumber = value;
   }

   /**
    * Set RandomSeed.
    *
    * @param value The value.
    */
   public void setRandomSeed(int value) throws PropertyVetoException{
     try{
       Integer.parseInt(""+value);
     }catch(Exception e){
       throw new PropertyVetoException("Random Seed should be an integer value.", null);
     }
     this.RandomSeed = value;
   }

   /**
    * Set StopObjectiveScoreThreshold.
    *
    * @param value The value.
    */
   public void setStopObjectiveScoreThreshold(double value) {
      this.StopObjectiveScoreThreshold = value;
   }

   /**
    * Set Trace.
    *
    * @param value The value.
    */
   public void setTrace(boolean value) { this.Trace = value; }
} // end class RandomOptimizer
