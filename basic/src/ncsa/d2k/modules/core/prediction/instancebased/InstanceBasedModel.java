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

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.util.*;


/**
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
public class InstanceBasedModel extends Model implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4995283241254847447L;

   //~ Instance fields *********************************************************


   /** best distances */
   protected double[] bestDistances = null;

   /** best example indices */
   protected int[] bestExampleIndices = null;

    /**
    * The value of the power term in the inverse distance weighting formula.
    * Setting this to zero causes equal weighting of all examples.  Setting
    * it to 1.0 gives inverse distance weighting.  Setting it to 2.0 gives
    * inverse distance squared weighting and so on.
    */
   protected double DistanceWeightingPower;

   /** input ranges */
   protected double[] inputRanges;

   /**
    * The number of examples to use for fitting the prediction module.  This
    * must be set to 1 or greater.
    */
   protected int NeighborhoodSize;

   /** outputs */
   protected double[] outputs = null;

   /** training examples */
   protected ExampleTable trainExampleSet;

   /**
    * What weight to associate to a stored example which has zero distance to
    * example to be predicted.  Since division by zero is not permitted, some
    * value must be assigned to examples with zero distance.  This value is the
    * weight and exact match should be given.
    */
   protected double ZeroDistanceWeight;

   //~ Constructors ************************************************************


   /**
    * Creates a new InstanceBasedModel object.
    *
    * @param examples               set of examples
    * @param inputRanges            input ranges
    * @param NeighborhoodSize       neighborhood size
    * @param DistanceWeightingPower distance weighting power
    * @param ZeroDistanceWeight     zero distance weight
    * @param exampleSet             training examples
    */
   public InstanceBasedModel(ExampleTable examples,
                             double[] inputRanges,
                             int NeighborhoodSize,
                             double DistanceWeightingPower,
                             double ZeroDistanceWeight,
                             ExampleTable exampleSet) {

      super(examples);

      this.inputRanges = inputRanges;
      this.NeighborhoodSize = NeighborhoodSize;
      this.DistanceWeightingPower = DistanceWeightingPower;
      this.ZeroDistanceWeight = ZeroDistanceWeight;
      this.trainExampleSet = (ExampleTable) exampleSet.copy();

   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   //~ Methods *****************************************************************


    /**
     * Evaluate the model. Overridden by implementer.
     *
     * @param testExampleSet set of examples to evaluate
     * @param testE          example index
     * @return Results.
     */
    public double[] evaluate(ExampleTable testExampleSet, int testE) {
        int numExamples = trainExampleSet.getNumRows();
        int numInputs = trainExampleSet.getNumInputFeatures();
        int numOutputs = trainExampleSet.getNumOutputFeatures();


        int actualNeighborhoodSize = NeighborhoodSize;

        if (actualNeighborhoodSize > numExamples) {
            actualNeighborhoodSize = numExamples;
        }

        if (bestDistances == null) {
            bestDistances = new double[actualNeighborhoodSize];
            bestExampleIndices = new int[actualNeighborhoodSize];
        }

        for (int i = 0; i < actualNeighborhoodSize; i++) {
            bestDistances[i] = Double.POSITIVE_INFINITY;
            bestExampleIndices[i] = Integer.MIN_VALUE;
        }

        for (int e = 0; e < numExamples; e++) {
            double sumOfSquares = 0.0;
            double difference = Double.NaN;

            for (int f = 0; f < numInputs; f++) {

                if (inputRanges[f] != 0.0) {
                    difference =
                            (trainExampleSet.getInputDouble(e, f) -
                                    testExampleSet.getInputDouble(testE, f)) / inputRanges[f];
                    sumOfSquares += difference * difference;
                }
            }

            double distance = Math.sqrt(sumOfSquares / numInputs);

            if (distance <= bestDistances[0]) {

                // insert
                int i = 0;

                while (
                        (distance <= bestDistances[i]) &&
                                (i < actualNeighborhoodSize - 1)) {
                    bestDistances[i] = bestDistances[i + 1];
                    bestExampleIndices[i] = bestExampleIndices[i + 1];
                    i++;
                }

                bestDistances[i] = distance;
                bestExampleIndices[i] = e;
            }
        } // end for

        if (outputs == null) {
            outputs = new double[numOutputs];
        } else {

            for (int i = 0; i < numOutputs; i++) {
                outputs[i] = 0;
            }
        }

        double weightSum = 0.0;
        double weight;

        for (int i = 0; i < actualNeighborhoodSize; i++) {

            double distance = bestDistances[i];

            if (distance == 0) {
                weight = ZeroDistanceWeight;
            } else {
                weight = 1.0 / Math.pow(distance, DistanceWeightingPower);
            }

            weightSum += weight;

            for (int f = 0; f < numOutputs; f++) {
                outputs[f] +=
                        trainExampleSet.getOutputDouble(bestExampleIndices[i], f) *
                                weight;
            }
        }

        for (int f = 0; f < numOutputs; f++) {
            outputs[f] /= weightSum;
        }

        return outputs;
    } // end method evaluate


    /**
     * Print model options. 
     *
     * @param printOptions The options.
     * @throws Exception If exception occurs, exception is thrown.
     */
    public void print(ModelPrintOptions printOptions) throws Exception {
    	myLogger.debug("Instance Based Control Parameters:");
    	myLogger.debug("  NeighborhoodSize       = " + NeighborhoodSize);
    	myLogger.debug("  DistanceWeightingPower = " +
                DistanceWeightingPower);
    	myLogger.debug("  ZeroDistanceWeight      = " + ZeroDistanceWeight);
    	myLogger.debug("Example Set Attributes");
    	myLogger.debug("  NumExamples            = " +
                trainExampleSet.getNumRows());
    }


} // end class InstanceBasedModel
