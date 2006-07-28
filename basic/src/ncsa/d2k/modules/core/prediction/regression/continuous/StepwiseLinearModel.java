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
package ncsa.d2k.modules.core.prediction.regression.continuous;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;


/**
 * A model which creates linear prediction functions using one or more input
 * variables.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class StepwiseLinearModel extends Model implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 7896382023669452884L;

   //~ Instance fields *********************************************************

   /** Description of field numSelectedInputs. */
   protected int numSelectedInputs;

   /** Description of field selectedIndices. */
   protected int[] selectedIndices;

   /** Description of field weights. */
   protected double[][] weights;

   //~ Constructors ************************************************************

   /**
    * Creates a new StepwiseLinearModel object.
    *
    * @param examples       set of examples
    * @param selectedInputs the selected inputs
    * @param weights        weights
    */
   public StepwiseLinearModel(ExampleTable examples,
                              boolean[] selectedInputs,
                              double[][] weights) {
      super(examples);

      int numSelectedInputs = 0;
      int[] selectedInputIndices = new int[getNumInputFeatures()];

      for (int i = 0; i < getNumInputFeatures(); i++) {

         if (selectedInputs[i] == true) {
            selectedInputIndices[numSelectedInputs] = i;
            numSelectedInputs++;
         }
      }

      this.numSelectedInputs = numSelectedInputs;
      this.selectedIndices = selectedInputIndices;
      this.weights = (double[][]) weights.clone();

   }

   //~ Methods *****************************************************************


    /**
     * Evaluate the model. Overridden by implementer.
     *
     * @param examples set of examples
     * @param e        index of example to evaluate
     * @return evaluations for each output
     */
    public double[] evaluate(ExampleTable examples, int e) {
        double[] outputs = new double[getNumOutputFeatures()];

        for (int o = 0; o < weights.length; o++) {
            double sum = weights[o][numSelectedInputs];

            for (int i = 0; i < numSelectedInputs; i++) {
                sum +=
                        weights[o][i] * examples.getInputDouble(e, selectedIndices[i]);
            }

            outputs[o] = sum;
        }

        return outputs;
    }


    /**
     * Print model options. Must be overriden by implementation.
     *
     * @param printOptions The options.
     * @throws Exception If exception occurs, exception is thrown.
     */
    public void print(ModelPrintOptions printOptions) throws Exception {
        System.out.println("Linear Model");
        System.out.println("numSelectedInputs = " + numSelectedInputs);

        for (int o = 0; o < getNumOutputFeatures(); o++) {
            System.out.println(this.getOutputFeatureName(o) + " = ");

            for (int i = 0; i < numSelectedInputs; i++) {
                System.out.println(weights[o][i] + " * " +
                        this.getInputFeatureName(selectedIndices[i]) +
                        " + ");
            }

            System.out.println(weights[o][numSelectedInputs]);
        }
    }

} // end class StepwiseLinearModel
