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
package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

import java.text.DecimalFormat;
import ncsa.d2k.modules.core.util.*;


/**
 * A model that contains other models, an ensemble.  Examples are evaluated by
 * each Model in the ensemble, and then the results are combined using the
 * specified combination method: either sum or average.
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class EnsembleModel extends Model implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -8555418205873204062L;

   /** constant for sum */
   static public final int SUM = 0;

   /** constant for average */
   static public final int AVERAGE = 1;

   //~ Instance fields *********************************************************

   /** the combination method; either SUM or AVERAGE */
   int CombinationMethod = 0; // 0 = sum, 1 = average

   /** formatting  */
   DecimalFormat Format = new DecimalFormat();

   /** the models in the ensemble */
   Model[] models;

   /** the number of models in the ensemble */
   int numModels = 0;

   //~ Constructors ************************************************************

   /**
    * Creates a new EnsembleModel object.
    *
    * @param examples          table of examples
    * @param models            the models in the ensemble
    * @param NumModels         the number of models
    * @param CombinationMethod combination method to use; SUM or AVERAGE
    */
   public EnsembleModel(ExampleTable examples, Model[] models, int NumModels,
                        int CombinationMethod) {
      super(examples);

      this.numModels = NumModels;
      this.CombinationMethod = CombinationMethod;

      Model[] ModelsCopy = new Model[NumModels];

      for (int i = 0; i < NumModels; i++) {
         ModelsCopy[i] = (Model) models[i].clone();
      }

      this.models = ModelsCopy;
   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   //~ Methods *****************************************************************

   /**
    * Evaluate an example using the ensemble.  The example is evaluated by
    * each model and then the result is either summed or averaged.
    *
    * @param  exampleSet set of examples
    * @param  e          index of example to evaluate
    *
    * @return double[] containing the results for each output
    *
    * @throws Exception when something goes wrong
    */
   public double[] evaluate(ExampleTable exampleSet, int e) throws Exception {
      double[] outputs;
      double[] combinedOutputs = new double[exampleSet.getNumOutputFeatures()];

      for (int m = 0; m < numModels; m++) {
         outputs = this.models[m].evaluate(exampleSet, e);

         for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
            combinedOutputs[v] += outputs[v];
         }
      }

      if (this.CombinationMethod == this.AVERAGE) {

         for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
            combinedOutputs[v] /= numModels;
         }
      }

      return combinedOutputs;
   }

   /**
    * Evaluate an example using the ensemble.  The example is evaluated by
    * each model and then the result is either summed or averaged.
    *
    * @param  exampleSet set of examples
    * @param  e          index of example to evaluate
    * @param combinedOutputs double[] containing the results for each output
    *
    * @throws Exception when something goes wrong
    */
   public void Evaluate(ExampleTable exampleSet, int e,
                        double[] combinedOutputs) throws Exception {
      double[] outputs;

      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
         combinedOutputs[v] = 0.0;
      }

      for (int m = 0; m < numModels; m++) {
         outputs = this.models[m].evaluate(exampleSet, e);

         for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
            combinedOutputs[v] += outputs[v];
         }
      }

      if (this.CombinationMethod == this.AVERAGE) {

         for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
            combinedOutputs[v] /= numModels;
         }
      }

   }

   /**
    * Print each model in the ensemble.
    *
    * @param  options Options for printing
    *
    * @throws Exception when something goes wrong
    */
   public void print(ModelPrintOptions options) throws Exception {

      Format.setMaximumFractionDigits(options.MaximumFractionDigits);
      myLogger.debug("Ensemble with " + numModels + " models");

      for (int m = 0; m < numModels; m++) {
         this.models[m].print(options);

         myLogger.debug("");
      }

   }

} // end class EnsembleModel
