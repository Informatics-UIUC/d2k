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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;

import java.beans.PropertyVetoException;
import java.util.Random;


/**
 * used to be ncsa.d2k.modules.core.optimize.random.RandomGriddedSampling.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class GriddedSampler extends RandomSampler {

   //~ Instance fields *********************************************************

   /** numSamples. Default = 100 */
   private int numSamples = 100;

   /**
    * this is the increment, the indexed distance between points, not the actual
    * euclidean distance.
    */
   double increment;


   /** indices. */
   int[] indices = null;

   /**
    * This is the dimension multiplier, contains the cardinality of that level
    * of the space.
    */
   long[] multiplier = null;

   /** Random number generator used to compute the delta from the offset. */
   Random random = new Random();

   //~ Methods *****************************************************************

   /**
    * This is the method that computes the next paramamter point and pushes it
    * out. To do this the N dimension space is reduced to a 1D space. It is
    * simply a range, starting at the first point on the line and continuing to
    * the last possible point in the space. This mapping is easy, that is what
    * the resolution of each parameter in the space provides. Once the next
    * equally spaced point is selected, we offset the point by a random value
    * from zero to the distance to the next point on the line. This provides
    * better coverage than a regularly spaced grid.
    */
   protected void pushParameterPoint() {

      int numParams = space.getNumParameters();

      // This array will contain the indexes of the increments
      // for each of the parameters. We want our points equidistant
      // throughout the space.
      if (multiplier == null) {

         // Compute the total number of points in the space.
         long integerTotal = 1;
         indices = new int[numParams];
         multiplier = new long[numParams];

         for (int i = numParams - 1; i >= 0; i--) {
            multiplier[i] = integerTotal;
            integerTotal *= space.getNumRegions(i);
         }

         increment = (double) integerTotal / (double) this.getNumSamples();
      }

      // Select the begining of the next interval.
      double current = (int) (increment * pointsPushed);

      // Now select the random offset within that interval
      current += increment * random.nextDouble();

      // Compute the intervals of the indices.
      for (int i = 0; i < numParams; i++) {
         indices[i] = (int) (current / multiplier[i]);
         current = current % multiplier[i];
      }

      // Now we have the indices of the increments, compute the floating point
      // value
      // at that interval.
      double[] point = new double[numParams];

      for (int i = numParams - 1; i >= 0; i--) {

         if (space.getNumRegions(i) == 1) {
            point[i] = space.getMinValue(i);
         } else {
            point[i] = ((double) indices[i]) /
                          (space.getNumRegions(i) - 1.0);
            point[i] *= space.getMaxValue(i) - space.getMinValue(i);
            point[i] += space.getMinValue(i);
         }
      }

      // we have data, construct a paramter point.
      String[] names = new String[space.getNumParameters()];

      for (int i = 0; i < space.getNumParameters(); i++) {
         names[i] = space.getName(i);
      }

      ParameterPointImpl parameterPoint =
         (ParameterPointImpl) ParameterPointImpl.getParameterPoint(names,
                                                                   point);

      if (trace) {
         System.out.println("Equidistant Sampling Pushed point " +
                            pointsPushed + " " + parameterPoint);
      }

      this.pushOutput(parameterPoint, 0);
   } // end method pushParameterPoint

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * <In this case, the code does these specific activities>.
    */
   public void beginExecution() {
      super.beginExecution();
      multiplier = null;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module will generate equally spaced " +
             "points (within a computed tolerance) in a parameter " +
             "      space." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: This module is given a parameter " +
             "space. It will " +
             "      generate a number of points in the paramter space " +
             "equally distanced from " +
             "      one another, then offset them by a random offset such " +
             "that any point will not" +
             "		 exceed the next possible point." +
             "		 The maximum offset value is actually the the number of " +
             "      possible values between two points if the points were " +
             "evenly spaced." +
             "      For example, imagine you have one parameter to optimize " +
             "in the space," +
             "		 and the resolution for that parameter is 150. Further, " +
             "say maxIterations is set to" +
             "		 10. If the spaces were exactly equally space, there " +
             "would be 15 points with 10 points" +
             "		 between each value." +
             "      In this case the tolerance is actually 10. When we " +
             "produce an exactly equally space point" +
             "		 in parameter space," +
             "		 we offset it by a random value from zero to the " +
             "tolerance. In this way, we are sure that" +
             "		 we get a fair distribution of points in the space." +
             "      The number of parameters generated is defined by the " +
             "      property maxIterations. The Trace property will turn on " +
             "debugging output, " +
             "      displayed when each point is generated." + "    </p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Gridded Sampler"; }

   /**
    * Get NumSamples.
    *
    * @return The value.
    */
   public int getNumSamples() { return this.numSamples; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[2];
      descriptions[0] =
         new PropertyDescription("numSamples",
                                 "Number of Samples",
                                 "The number of samples to generate.  ");
      descriptions[1] =
         new PropertyDescription("trace", "Trace",
                                 "Report each scored point in parameter " +
                                 "space as it becomes available.");

      return descriptions;
   }

   /**
    * Set NumSamples.
    *
    * @param  value The value.
    *
    * @throws PropertyVetoException I value < 1.
    */
   public void setNumSamples(int value) throws PropertyVetoException {

      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }

      this.numSamples = value;
   }
} // end class GriddedSampler
