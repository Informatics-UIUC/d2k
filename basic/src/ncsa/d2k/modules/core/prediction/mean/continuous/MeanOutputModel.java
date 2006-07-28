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
package ncsa.d2k.modules.core.prediction.mean.continuous;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

import java.text.DecimalFormat;


/**
 * A model that makes predictions that are independent of the
 * input attribute values.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class MeanOutputModel extends Model implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -5680563657662443326L;

   //~ Instance fields *********************************************************


   /** formatting options */
   protected DecimalFormat Format = new DecimalFormat();

   /** output values */
   protected double[] meanOutputValues;

   //~ Constructors ************************************************************

   /**
    * Creates a new MeanOutputModel object.
    *
    * @param examples         set of examples
    * @param meanOutputValues output values
    */
   public MeanOutputModel(ExampleTable examples, double[] meanOutputValues) {
      super(examples);
      this.meanOutputValues = meanOutputValues;
   }

   /**
    * Creates a new MeanOutputModel object.
    *
    * @param trainingSetSize    size of training set
    * @param inputColumnLabels  labels of input columns
    * @param outputColumnLabels labels of output columns
    * @param inputFeatureTypes  datatypes of input columns
    * @param outputFeatureTypes datatypes of output columns
    * @param meanOutputValues   output values
    */
   public MeanOutputModel(int trainingSetSize, String[] inputColumnLabels,
                          String[] outputColumnLabels,
                          int[] inputFeatureTypes, int[] outputFeatureTypes,
                          double[] meanOutputValues) {
      super(trainingSetSize, inputColumnLabels, outputColumnLabels,
            inputFeatureTypes, outputFeatureTypes);
      this.meanOutputValues = meanOutputValues;
   }

   //~ Methods *****************************************************************

   /**
    * this is a dummy input since mean model does not have any; added only for
    * consistancy.
    *
    * @param  inputs inputs
    *
    * @return Results.
    */
   public double[] Evaluate(double[] inputs) {

      double[] outputs = new double[meanOutputValues.length];

      for (int f = 0; f < meanOutputValues.length; f++) {
         outputs[f] = meanOutputValues[f];
      }

      return outputs;
   }

   /**
    * Evaluate the model.
    *
    * @param  exampleSet set of examples
    * @param  e          example index
    *
    * @return Results.
    */
   public double[] evaluate(ExampleTable exampleSet, int e) {
      double[] outputs = new double[exampleSet.getNumOutputFeatures()];

      for (int f = 0; f < meanOutputValues.length; f++) {
         outputs[f] = meanOutputValues[f];
      }

      return outputs;
   }

   /**
    * Evaluate the model.
    *
    * @param exampleSet set of examples
    * @param e          example index
    * @param outputs    array to store the evaluations
    */
   public void evaluate(ExampleTable exampleSet, int e, double[] outputs) {

      for (int f = 0; f < meanOutputValues.length; f++) {
         outputs[f] = meanOutputValues[f];
      }
   }

   /**
    * Print model options.
    *
    * @param options The options.
    */
   public void print(ModelPrintOptions options) {

      Format.setMaximumFractionDigits(options.MaximumFractionDigits);

      for (int i = 0; i < getNumOutputFeatures(); i++) {

         if (i > 0) {
            System.out.print("  ");
         }

         System.out.print(this.getOutputFeatureName(i) + " = " +
                          Format.format(this.meanOutputValues[i]));
      }
      // System.out.println();
   }

} // end class MeanOutputModel
