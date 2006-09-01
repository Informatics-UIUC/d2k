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
package ncsa.d2k.modules.core.prediction.instancebased;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;

import java.beans.PropertyVetoException;


/**
 * <p>Overview: This module builds an instance based model from an example
 * table. </p>
 * <p>
 * Detailed Description:
 * The module implements the instance based learning algorithm.  The instance
 * based learning algorithm is also known as n-nearest neighbor or kernel
 * density weighting.  During the training phase, the instance based inducer
 * simply memorizes (makes a copy of) the training example table.  Given a
 * target point in input space to classify, an instance based model first finds
 * the n (<i>Neighborhood Size</i>) nearest examples using Euclidean distance,
 * and weights each example to make the final prediction.  The formula used to
 * weight each example is 1.0 / distance^<i>Distance Weighting Power</i>.
 * Distance is Euclidean (square root of the sum of squared differences).  To
 * prevent division by zero, a constant weight (<i>Zero Distance Weight</i>) is
 * assigned to any example with a zero distance.  When
 * <i>Distance Weighting Power</i> is 0.0, and <i>Zero Distance Value</i> is
 * 1.0, every example in the neighborhood is given equal consideration.
 * <p>Restrictions:
 * This module will only classify examples with numeric input and output
 * attributes.<p>
 * Data Handling: This module does not modify the input data. </p>
 * <p>
 * Scalability: This module can efficiently process a data set that can be
 * stored in memory.
 * The ultimate limit is how much virtual memory java can access.
 * Model prediction speed can be increased by reducing <i>Neighborhood Size</i>.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class InstanceBasedInducer extends InstanceBasedInducerOpt {

   //~ Instance fields *********************************************************

   /**
    * The value of the power term in the inverse distance weighting formula.
    * Setting this to zero causes equal weighting of all examples.  Setting
    * it to 1.0 gives inverse distance weighting.  Setting it to 2.0 gives
    * inverse distance squared weighting and so on.
    */
   private double DistanceWeightingPower = 0.0;

   /**
    * The number of examples to use for fitting the prediction module.  This
    * must be set to 1 or greater.
    */
   private int NeighborhoodSize = 20;

   /**
    * What weight to associate to a stored example which has zero distance to
    * example to be predicted.  Since division by zero is not permitted, some
    * value must be assigned to examples with zero distance.  This value is the
    * weight and exact match should be given.
    */
   private double ZeroDistanceValue = 0.0;


   /** the number of bias parameters. */
   int numBiasDimensions = 3;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      // ANCA: added exceptions
      ExampleTable exampleSet;

      try {
         exampleSet = (ExampleTable) this.pullInput(0);
      } catch (java.lang.ClassCastException e) {
         throw new Exception("Input/Output attributes not selected.");
      }

      int[] inputs = exampleSet.getInputFeatures();
      int[] outputs = exampleSet.getOutputFeatures();

      for (int i = 0; i < inputs.length; i++) {

         if (!(exampleSet.getColumn(inputs[i])).getIsScalar()) {
            throw new Exception("input attributes like " +
                                exampleSet.getColumn(inputs[i]).getLabel() +
                                " must be numeric");
         }
      }

      for (int i = 0; i < outputs.length; i++) {

         if (!(exampleSet.getColumn(outputs[i])).getIsScalar()) {
            throw new Exception("output attribute must be numeric");
         }
      }


      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(1);

      instantiateBiasFromProperties();

      Model model = null;

      model = generateModel(exampleSet, errorFunction);

      this.pushOutput(model, 0);
   } // end method doit

   /**
    * Generate a model given a set of examples and an ErrorFunction.
    *
    * @param  examples      set of examples
    * @param  errorFunction an error function
    *
    * @return a Model
    */
   public Model generateModel(ExampleTable examples,
                              ErrorFunction errorFunction) {

      int numExamples = examples.getNumRows();
      int numInputs = examples.getNumInputFeatures();
      double[] inputMins = new double[numInputs];
      double[] inputMaxs = new double[numInputs];
      double[] inputRanges = new double[numInputs];

      for (int v = 0; v < numInputs; v++) {
         inputMins[v] = Double.POSITIVE_INFINITY;
         inputMaxs[v] = Double.NEGATIVE_INFINITY;
      }

      for (int e = 0; e < numExamples; e++) {

         for (int v = 0; v < numInputs; v++) {
            double value = examples.getInputDouble(e, v);

            if (value < inputMins[v]) {
               inputMins[v] = value;
            }

            if (value > inputMaxs[v]) {
               inputMaxs[v] = value;
            }
         }
      }

      for (int v = 0; v < numInputs; v++) {
         inputRanges[v] = inputMaxs[v] - inputMins[v];
      }

      InstanceBasedModel model =
         new InstanceBasedModel(examples,
                                inputRanges,
                                NeighborhoodSize,
                                DistanceWeightingPower,
                                ZeroDistanceValue,
                                examples);

      return (Model) model;
   } // end method generateModel

   /**
    * Get the value of distance weighting power
    *
    * @return distance weighting power
    */
   public double getDistanceWeightingPower() {
      return this.DistanceWeightingPower;
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
            return "Example Table";

         case 1:
            return "Error Function";

         default:
            return "Error!  No such input.";
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
            return "Example Table";

         case 1:
            return "Error Function";

         default:
            return "Error!  No such input.";
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
                        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
                        "ncsa.d2k.modules.core.prediction.ErrorFunction"
                };

        return types;
    }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Instance Based Inducer"; }

   /**
    * Get the neighborhood size
    *
    * @return neighborhood size
    */
   public int getNeighborhoodSize() { return this.NeighborhoodSize; }

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
            return "Model";

         default:
            return "Error!  No such output.";
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
            return "Error!  No such output.";
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

      PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

      pds[0] =
         new PropertyDescription("neighborhoodSize",
                                 "Neighborhood Size",
                                 "The number of examples to use for fitting the prediction module.  " +
                                 "This must be set to 1 or greater.  ");

      pds[1] =
         new PropertyDescription("distanceWeightingPower",
                                 "Distance Weighting Power",
                                 "The value of the power term in the inverse distance weighting formula.  " +
                                 "Setting this to zero causes equal weighting of all examples.  " +
                                 "Setting it to 1.0 gives inverse distance weighting.  " +
                                 "Setting it to 2.0 gives inverse distance squared weighting and so on.  ");

      pds[2] =
         new PropertyDescription("zeroDistanceValue",
                                 "Zero Distance Value",
                                 "What weight to associate to a stored example which has zero distance to example to be predicted.  " +
                                 "Since division by zero is not permitted, some value must be assigned to examples with zero distance.  " +
                                 "This value is the weight and exact match should be given.  ");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Get the zero distance value
    *
    * @return zero distance value
    */
   public double getZeroDistanceValue() { return this.ZeroDistanceValue; }

   /**
    * Nothing to do in this case since properties are reference directly by
    * the algorithm and no other control parameters need be set.  This may
    * not be the case in general so this stub is left open for future
    * development.
    */
   public void instantiateBiasFromProperties() {

   }

   /**
    * Set the distance weighting power
    *
    * @param value new value
    */
   public void setDistanceWeightingPower(double value) {
      this.DistanceWeightingPower = value;
   }

   /**
    * Set the neighborhood size.  Must be greater than or equal to one.
    *
    * @param  value new neighborhood size
    *
    * @throws PropertyVetoException when value is less than one
    */
   public void setNeighborhoodSize(int value) throws PropertyVetoException {

      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }

      this.NeighborhoodSize = value;
   }

   /**
    * Set the zero distance value.  Must be greater than or equal to zero.
    *
    * @param  value new zero distance value
    *
    * @throws PropertyVetoException when value is less than zero
    */
   public void setZeroDistanceValue(double value) throws PropertyVetoException {

      if (value < 0.0) {
         throw new PropertyVetoException(" < 0.0", null);
      }

      this.ZeroDistanceValue = value;
   }

} // end class InstanceBasedInducer
