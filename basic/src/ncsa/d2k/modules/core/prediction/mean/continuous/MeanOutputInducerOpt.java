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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;
import ncsa.d2k.modules.core.prediction.FunctionInducerOpt;


/**
 * <p>
 * Overview:
 * This module builds a simple model by computing the average for the output
 * attribute.  </p>
 * <p>
 * Detailed Description:
 * The module implements the mean output learning algorithm.
 * It produces a model that makes predictions that are independent of the input
 * attribute values.  During the training phase, the mean output inducer sums
 * up the output values for each output attribute and then divides by the
 * number of examples.  There are no control parameters to this learning
 * algorithm and <i>Mean Output Inducer Optimizable</i> is added for uniformity.
 * <p>
 * Restrictions:
 * This module will only classify examples with numeric output attributes.
 * <p>
 * Data Handling: This module does not modify the input data. </p>
 * <p>
 * Scalability: This module can efficiently process a data set that can be
 * stored in memory.  The ultimate limit is how much virtual memory java can
 * access. </p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class MeanOutputInducerOpt extends FunctionInducerOpt {
   // int NumBiasParameters = 0;

   //~ Methods *****************************************************************

   /**
    * Generate a model given a set of examples and an ErrorFunction.
    *
    * @param  examples      set of examples
    * @param  errorFunction an error function
    *
    * @return a Model
    *
    * @throws Exception when something goes wrong
    */
   public Model generateModel(ExampleTable examples,
                              ErrorFunction errorFunction) throws Exception {

      int numExamples = examples.getNumRows();
      int numOutputs = examples.getNumOutputFeatures();

      double[] outputSums = new double[numOutputs];

      for (int e = 0; e < numExamples; e++) {

         for (int f = 0; f < numOutputs; f++) {
            outputSums[f] += examples.getOutputDouble(e, f);
         }
      }

      for (int f = 0; f < numOutputs; f++) {
         outputSums[f] /= numExamples;
      }


      MeanOutputModel model = new MeanOutputModel(examples, outputSums);

      return (Model) model;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s = "";
      s += "<p>";
      s += "Overview: ";
      s +=
         "This module builds a simple model by computing the average for the output attribute.  </p>";
      s += "<p>";
      s += "Detailed Description: ";
      s += "The module implements the mean output learning algorithm.  ";
      s +=
         "It produces a model that makes predictions that are independent of the input attribute values.  ";
      s += "During the training phase, the mean output inducer sums ";
      s +=
         "up the output values for each output attribute and then divides by the number of examples.  ";
      s +=
         "There are no control parameters to this learning algorithm and <i>Mean Output Inducer Optimizable</i> is added for uniformity.  ";
      s += "<p>";
      s += "Restrictions: ";
      s +=
         "This module will only classify examples with numeric output attributes.";
      s += "<p>";
      s += "Data Handling: This module does not modify the input data. </p>";
      s += "<p>";
      s +=
         "Scalability: This module can efficiently process a data set that can be stored in memory.  ";
      s +=
         "The ultimate limit is how much virtual memory java can access. </p> ";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Mean Output Inducer Optimizable"; }


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
    * Nothing to do in this case since properties are reference directly by the
    * algorithm and no other control parameters need be set. This may not be the
    * case in general so this stub is left open for future development.
    */
   public void instantiateBiasFromProperties() { }

   /**
    * Set the parameters, using the values in a ParameterPoint.
    *
    * @param  point ParameterPoint containing control parameters
    *
    * @throws Exception when something goes wrong
    */
   public void setControlParameters(ParameterPoint point) throws Exception { }

} // end class MeanOutputInducerOpt
