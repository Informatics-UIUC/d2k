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
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;
import ncsa.d2k.modules.core.prediction.FunctionInducerOpt;


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
public class InstanceBasedInducerOpt extends FunctionInducerOpt {

   //~ Instance fields *********************************************************

   /**
    * The value of the power term in the inverse distance weighting formula.
    * Setting this to zero causes equal weighting of all examples.  Setting
    * it to 1.0 gives inverse distance weighting.  Setting it to 2.0 gives
    * inverse distance squared weighting and so on.
    */
   protected double DistanceWeightingPower = 0.0;

    /**
    * The number of examples to use for fitting the prediction module.  This
    * must be set to 1 or greater.
    */
   protected int NeighborhoodSize = 1;

   /**
    * What weight to associate to a stored example which has zero distance to
    * example to be predicted.  Since division by zero is not permitted, some
    * value must be assigned to examples with zero distance.  This value is the
    * weight and exact match should be given.
    */
   protected double ZeroDistanceWeight = 1E-9;

   //~ Methods *****************************************************************

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
                                ZeroDistanceWeight,
                                examples);

      return (Model) model;
   } // end method generateModel

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s = "";
      s += "<p>";
      s += "Overview: ";
      s += "This module builds an instance based model from an example table. ";
      s += "</p>";
      s += "<p>";
      s += "Detailed Description: ";
      s += "The module implements the instance based learning algorithm. ";
      s += "The instance based learning algorithm is also known as n-nearest ";
      s += "neighbor or kernel density weighting.  ";
      s += "During the training phase, the instance based inducer simply ";
      s += "memorizes (makes a copy of) the training example table.  ";
      s += "Given a target point in input space to classify, an instance based ";
      s += "model first finds the n (<i>Neighborhood Size</i>) nearest ";
      s += "examples using Euclidean distance, and weights each example to ";
      s += "make the final prediction.  ";
      s += "The formula used to weight each example is 1.0 / ";
      s += "distance^<i>Distance Weighting Power</i>.  ";
      s += "Distance is Euclidean (square root of the sum of squared differences).  ";
      s += "To prevent division by zero, a constant weight ";
      s += "(<i>Zero Distance Weight</i>) is assigned to any example with a ";
      s += "zero distance.  ";
      s += "When <i>Distance Weighting Power</i> is 0.0, and ";
      s += "<i>Zero Distance Value</i> is 1.0, every example in the ";
      s += "neighborhood is given equal consideration.  ";
      s += "<p>Restrictions: ";
      s += "This module will only classify examples with numeric input and ";
      s += "output attributes.";
      s += "<p>";
      s += "Data Handling: This module does not modify the input data. </p>";
      s += "<p>";
      s += "Scalability: This module can efficiently process a data set that ";
      s += "can be stored in memory.  ";
      s += "The ultimate limit is how much virtual memory java can access. </p> ";
      s += "Model prediction speed can be increased by reducing ";
      s += "<i>Neighborhood Size</i>.  ";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() {
      return "Instance Based Inducer Optimizable";
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // so that "ordered and _trace" property are invisible
      return new PropertyDescription[0];
   }


   /**
    * Set the parameters, using the values in a ParameterPoint.
    *
    * @param parameterPoint ParameterPoint containing control parameters
    */
   public void setControlParameters(ParameterPoint parameterPoint) {

      NeighborhoodSize = (int) parameterPoint.getValue(0);
      DistanceWeightingPower = parameterPoint.getValue(1);
      ZeroDistanceWeight = parameterPoint.getValue(2);

   }

} // end class InstanceBasedInducerOpt
