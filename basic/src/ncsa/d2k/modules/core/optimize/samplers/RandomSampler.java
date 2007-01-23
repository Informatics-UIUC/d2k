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
package ncsa.d2k.modules.core.optimize.samplers;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;

import java.beans.PropertyVetoException;
import java.util.Random;


/**
 * used to be ncsa.d2k.modules.core.optimize.random.RandomSampling.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class RandomSampler extends ComputeModule {

   //~ Instance fields *********************************************************

   /** numSamples. Default = 100. */
   private int numSamples = 100;

   /** randomNumberGenerator. */
   private Random randomNumberGenerator = null;

   /** seed. Default = 1. */
   private int seed = 1;

   /** useGridOfRegions? Default = false. */
   private boolean useGridOfRegions = false;

   /** space. */
   ParameterSpace space;

   /** these are the paramter points to test. */
   protected int pointsPushed = 0;

   /** trace? Default = false. */
   protected boolean trace = false;

   //~ Methods *****************************************************************

   /**
    * Push another paramter point, and update the accounting.
    */
   protected void pushParameterPoint() {
      int numParams = space.getNumParameters();
      double[] point = new double[numParams];

      // Create one point in parameter space.
      for (int i = 0; i < numParams; i++) {
         double range = space.getMaxValue(i) - space.getMinValue(i);

         if (useGridOfRegions) {
            int numRegions = space.getNumRegions(i);

            // This would be an error on the users part, resolution should
            // never be zero.
            double increment;

            if (numRegions <= 0) {
               increment = 0;
               numRegions = 1;
            } else {
               increment = range / (double) numRegions;
            }

            point[i] =
               space.getMinValue(i) +
               increment * randomNumberGenerator.nextInt(numRegions + 1);
         } else {

            switch (space.getType(i)) {

               case ColumnTypes.DOUBLE:
                  point[i] =
                     space.getMinValue(i) +
                     range * randomNumberGenerator.nextDouble();

                  break;

               case ColumnTypes.FLOAT:
                  point[i] =
                     space.getMinValue(i) +
                     range * randomNumberGenerator.nextFloat();

                  break;

               case ColumnTypes.INTEGER:

                  if ((int) range == 0) {
                     point[i] = space.getMinValue(i);
                  } else {
                     point[i] =
                        space.getMinValue(i) +
                        randomNumberGenerator.nextInt((int) (range + 1));
                  }

                  break;

               case ColumnTypes.BOOLEAN:

                  if ((int) range == 0) {
                     point[i] = space.getMinValue(i);
                  } else {
                     point[i] =
                        space.getMinValue(i) +
                        randomNumberGenerator.nextInt((int) (range + 1));
                  }

                  break;
            }
         } // end if
      } // end for

      // we have data, construct a paramter point.
      String[] names = new String[space.getNumParameters()];

      for (int i = 0; i < space.getNumParameters(); i++) {
         names[i] = space.getName(i);
      }

      ParameterPointImpl parameterPoint =
         (ParameterPointImpl) ParameterPointImpl.getParameterPoint(names,
                                                                   point);

      if (trace) {
         System.out.println("RandomSampling: Pushed point " + pointsPushed +
                            " " + parameterPoint);

      }

      this.pushOutput(parameterPoint, 0);
   } // end method pushParameterPoint

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    *
    * <p>Init the standard fields.</p>
    */
   public void beginExecution() {
      pointsPushed = -1;
      randomNumberGenerator = new Random(seed);
   }

   /**
    * Performs the main work of the module.
    *
    * <p>We do one of two things, we either store the newly aquired space input,
    * and reset the pointsPushed value, or we push another point.</p>
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module. <In this case, exceptions will be thrown in the
    *                   following scenarios>.
    */
   public void doit() {

      if (this.pointsPushed == -1) {
         this.pointsPushed = 0;
         space = (ParameterSpace) this.pullInput(0);
         this.pushOutput(new Integer(this.numSamples), 1);
      } else {
         this.pushParameterPoint();
         this.pointsPushed++;

         if (this.pointsPushed == numSamples) {

            // we have traversed this space, start on the next one.
            this.pointsPushed = -1;
            space = null;
         }
      }
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
            return "The Control Parameter Space to search";

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
            return "Control Parameter Space";

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
      { "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>      Overview: Generate random points in a space defined " +
      		"by a parameter space       input" +
             " until we push a user defined maximum number of points, we " +
             "       converge to a user defined" +
             " optima.    </p>    <p>      Detailed Description: This module " +
             "will produce <i>Maximum Number" +
             " of       Iterations</i> points in parameter space, unless it " +
             "converges before       generating" +
             " that many points. It will produce only one point per       " +
             "invocation, unless it has already" +
             " produced all the points it is going to       and it is just " +
             "waiting for scored points to come" +
             " back. This module will       not wait for a scored point " +
             "to come back before producing the" +
             " next one,       it will produce as many poiints as it can, " +
             "and it will remain enabled     " +
             "  until all those points are produced, or it has converged. " +
             "The module       converges if a" +
             " score exceeds the property named <i>Objective " +
             "Threashhold</i>. The Random Seed can be set to" +
             " a positive value to cause this module to       produce " +
             "the same points, given the same inputs," +
             " on multiple runs. <i>      Trace</i> and <i>Verbose " +
             "Output</i> properties can be set to produce" +
             " a       little or a lot of console output respectively. " +
             "If <i>UseRegions</i>" +
             " is not set, the number of regions value from the parameter " +
             "space object will be ignored. We" +
             " can minimize the objective score by setting       the " +
             "<i>Minimize Objective Score</i> property" +
             " to true.    </p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Random Sampler"; }

   /**
    * Get NumSamples.
    *
    * @return The value.
    */
   public int getNumSamples() { return this.numSamples; }

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
            return "This is the number of parameter points to produce";

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
            return "Parameter Point";

         case 1:
            return "Number Points";

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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "java.lang.Integer"
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
      PropertyDescription[] descriptions = new PropertyDescription[4];
      descriptions[0] =
         new PropertyDescription("numSamples", "Number of Samples",
                                 "Number of points in parameter " +
                                 "space to sample.  ");
      descriptions[1] =
         new PropertyDescription("seed", "Random Number Seed",
                                 "This integer is use to seed the " +
                                 "random number generator which is " +
                                 "used to select points in parameter space.");
      descriptions[2] =
         new PropertyDescription("trace", "Trace",
                                 "Report each scored point in parameter " +
                                 "space as it becomes available.");
      descriptions[3] =
         new PropertyDescription("useGridOfRegions",
                                 "Use Grid Of Regions",
                                 "If this parameter is true, it will use " +
                                 "the number regions defined in the paramter " +
                                 "space to define the boundary points " +
                                 "that are sampled.  " +
                                 "If this parameter is false, then sampling " +
                                 "occurs by randomly selecting points " +
                                 "between the min and max values.  ");

      return descriptions;
   }

   /**
    * Get Seed.
    *
    * @return The value.
    */
   public int getSeed() { return this.seed; }

   /**
    * Get Trace.
    *
    * @return The value.
    */
   public boolean getTrace() { return this.trace; }

   /**
    * Get UseGridOfRegions.
    *
    * @return The value.
    */
   public boolean getUseGridOfRegions() { return this.useGridOfRegions; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run. There are two states, searching a space, and waiting for a space.
    * When we are waiting, we have no space to search, all previous spaces have
    * been searched. When we receive another paramter space, we will search it.
    * While searching, we have a space and we are pushing points in that space.
    * We will push <code>maxIteration</code> points, one periteration. When we
    * have pushed all of them, we are done.
    *
    * @return true if we are ready to execute.
    */
   public boolean isReady() {

      if (this.pointsPushed == -1 && this.getInputPipeSize(0) == 0) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * Set NumSamples.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException If value < 1.
    */
   public void setNumSamples(int value) throws PropertyVetoException {
     try {
            Integer.parseInt("" + value);
          }
          catch (Exception e) {
            throw new PropertyVetoException(
                "Number of Samples should be an integer greater than zero.", null);
     }
      if (value < 1) {
         throw new PropertyVetoException("Number of Samples should be an integer greater than zero.", null);
      }

      this.numSamples = value;
   }

   /**
    * Set Seed.
    *
    * @param value The value.
    */
   public void setSeed(int value) throws PropertyVetoException{

     try{
      Integer.parseInt(""+value);
    }catch(Exception e){
      throw new PropertyVetoException("Random Number Seed should be an integer value.", null);
    }


     this.seed = value;
   }

   /**
    * Set Trace.
    *
    * @param value The value.
    */
   public void setTrace(boolean value) { this.trace = value; }

   /**
    * Set UseGridOfRegions.
    *
    * @param value The value.
    */
   public void setUseGridOfRegions(boolean value) {
      this.useGridOfRegions = value;
   }

} // end class RandomSampler
