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
package ncsa.d2k.modules.core.datatype.model;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;


/**
 * A abstract class for information about a Model.
 * 
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class Model extends PredictionModelModule
   implements java.io.Serializable {

   //~ Constructors ************************************************************


   /**
    * Creates a new Model object.
    *
    * @param examples Description of parameter examples.
    */
   protected Model(ExampleTable examples) { super(examples); }

   /**
    * Creates a new Model object.
    *
    * @param trainingSetSize    Description of parameter trainingSetSize.
    * @param inputColumnLabels  Description of parameter inputColumnLabels.
    * @param outputColumnLabels Description of parameter outputColumnLabels.
    * @param inputFeatureTypes  Description of parameter inputFeatureTypes.
    * @param outputFeatureTypes Description of parameter outputFeatureTypes.
    */
   protected Model(int trainingSetSize, String[] inputColumnLabels,
                   String[] outputColumnLabels,
                   int[] inputFeatureTypes, int[] outputFeatureTypes) {
      super(trainingSetSize, inputColumnLabels, outputColumnLabels,
            inputFeatureTypes, outputFeatureTypes);
   }

   //~ Methods *****************************************************************

   /**
    * Description of method evaluate.
    *
    * @param  exampleSet Description of parameter exampleSet.
    * @param  e          Description of parameter e.
    *
    * @return Description of return value.
    *
    * @throws Exception Description of exception Exception.
    */
   public double[] evaluate(ExampleTable exampleSet, int e) throws Exception {
      System.out.println("must override this method");
      throw new Exception();
   }

   /**
    * Description of method evaluate.
    *
    * @param  exampleSet Description of parameter exampleSet.
    * @param  e          Description of parameter e.
    * @param  outputs    Description of parameter outputs.
    *
    * @throws Exception Description of exception Exception.
    */
   public void evaluate(ExampleTable exampleSet, int e, double[] outputs)
      throws Exception {
      int numOutputs = exampleSet.getNumOutputFeatures();
      double[] internalOutputs = evaluate(exampleSet, e);

      for (int i = 0; i < numOutputs; i++) {
         outputs[i] = internalOutputs[i];
      }
   }

   /**
    * Description of method getInputFeatureName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getInputFeatureName(int i) {
      return this.getInputColumnLabels()[i];
   }

   /**
    * renaming.
    *
    * @return Description of return value.
    */
   public String[] getInputFeatureNames() { return getInputColumnLabels(); }

   /**
    * Description of method getNumInputFeatures.
    *
    * @return Description of return value.
    */
   public int getNumInputFeatures() { return getInputColumnLabels().length; }

   /**
    * Description of method getNumOutputFeatures.
    *
    * @return Description of return value.
    */
   public int getNumOutputFeatures() { return getOutputColumnLabels().length; }

   /**
    * Description of method getOutputFeatureName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getOutputFeatureName(int i) {
      return this.getOutputColumnLabels()[i];
   }

   /**
    * Description of method getOutputFeatureNames.
    *
    * @return Description of return value.
    */
   public String[] getOutputFeatureNames() { return getOutputColumnLabels(); }


   /**
    * Description of method makePredictions.
    *
    * @param pt Description of parameter pt.
    */
   public void makePredictions(PredictionTable pt) {
      int numOutputs = pt.getNumOutputFeatures();
      double[] predictions = new double[numOutputs];

      try {

         for (int i = 0; i < pt.getNumRows(); i++) {
            this.evaluate(pt, i, predictions);

            for (int j = 0; j < numOutputs; j++) {
               pt.setDoublePrediction(predictions[j], i, j);
            }
         }
      } catch (Exception e) { }
   }

   /**
    * Description of method print.
    *
    * @param  options Description of parameter options.
    *
    * @throws Exception Description of exception Exception.
    */
   public void print(ModelPrintOptions options) throws Exception {
      System.out.println("must override this method");
      throw new Exception();
   }

} // end class Model
