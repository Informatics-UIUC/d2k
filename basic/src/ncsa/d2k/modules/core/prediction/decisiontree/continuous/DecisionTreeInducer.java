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
package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;

import java.beans.PropertyVetoException;


/**
 * Description of class DecisionTreeInducer.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeInducer extends DecisionTreeInducerOpt {

   //~ Instance fields *********************************************************

   /** the number of bias dimensions. */
   int numBiasDimensions = 9;

   //~ Methods *****************************************************************


    /**
     * Called by the D2K Infrastructure before the itinerary begins to execute.
     */
    public void beginExecution() {
    }



   public void doit() throws Exception {

      ExampleTable exampleSet = (ExampleTable) this.pullInput(0);
      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(1);

      instantiateBiasFromProperties();

      Model model = null;

      model = generateModel(exampleSet, errorFunction);

      this.pushOutput(model, 0);
   }

   /**
    * Description of method getInputInfo.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
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
    * Description of method getInputName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
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
    * Description of method getInputTypes.
    *
    * @return Description of return value.
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
    * Description of method getMaxTreeDepth.
    *
    * @return Description of return value.
    */
   public int getMaxTreeDepth() { return this.MaxTreeDepth; }

   /**
    * Description of method getMinDecompositionPopulation.
    *
    * @return Description of return value.
    */
   public int getMinDecompositionPopulation() {
      return this.MinDecompositionPopulation;
   }

   /**
    * Description of method getMinErrorReduction.
    *
    * @return Description of return value.
    */
   public double getMinErrorReduction() { return this.MinErrorReduction; }


   /**
    * Description of method getModuleName.
    *
    * @return Description of return value.
    */
   public String getModuleName() { return "Decision Tree Inducer"; }

   /**
    * Description of method getOutputInfo.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
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
    * Description of method getOutputName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
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
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.model.Model" };

      return types;
   }

   /**
    * Description of method getPropertiesDescriptions.
    *
    * @return Description of return value.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

      pds[0] =
         new PropertyDescription("minDecompositionPopulation",
                                 "Minimum examples per leaf",
                                 "Prevents the generation of splits that result in leaf nodes with " +
                                 "less than the specified number of examples.  ");

      pds[1] =
         new PropertyDescription("minErrorReduction",
                                 "Minimum split error reduction",
                                 "The units of this error reduction are relative to the error function passed to the " +
                                 "decision tree inducer.  " +
                                 "A split will not occur unless the error is reduced by at least the amount specified.");

      pds[2] =
         new PropertyDescription("useOneHalfSplit",
                                 "Generate splits only at 1/2",
                                 "This works fine for boolean and continuous values.  " +
                                 "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");

      pds[3] =
         new PropertyDescription("useMeanBasedSplit",
                                 "Generate mean splits",
                                 "The mean of each attribute value is calculated in the given node and used to generate " +
                                 "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[4] =
         new PropertyDescription("useMidPointBasedSplit",
                                 "Generate midpoint splits",
                                 "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  " +
                                 "The split occurs at the midpoint between the min and max values.  " +
                                 "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[5] =
         new PropertyDescription("usePopulationBasedSplit",
                                 "Generate median splits",
                                 "The median of each attribute value is calculated in the given node and used to generate " +
                                 "splits for that node.  This requires sorting of all the examples in a node and therefore " +
                                 "scales at n log n in time complexity.  " +
                                 "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

      pds[6] =
         new PropertyDescription("saveNodeExamples",
                                 "Save examples at each node",
                                 "In order to compute and print statistics of the node you must save the examples at the node " +
                                 "which increases the space and time complexity of the algorithm by a linear factor.  ");

      pds[7] =
         new PropertyDescription("useMeanNodeModels",
                                 "Use the mean averaging for models",
                                 "This results in a simple decision tree with constant functions at the leaves.  " +
                                 "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

      pds[8] =
         new PropertyDescription("useLinearNodeModels",
                                 "Use multiple regression for models",
                                 "This results in a decision tree with linear functions of the input attributes at the leaves.  " +
                                 "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Description of method getSaveNodeExamples.
    *
    * @return Description of return value.
    */
   public boolean getSaveNodeExamples() { return this.SaveNodeExamples; }

   /**
    * Description of method getTrace.
    *
    * @return Description of return value.
    */
   public boolean getTrace() { return this._Trace; }

   /**
    * Description of method getUseLinearNodeModels.
    *
    * @return Description of return value.
    */
   public boolean getUseLinearNodeModels() { return this.UseLinearNodeModels; }

   /**
    * Description of method getUseMeanBasedSplit.
    *
    * @return Description of return value.
    */
   public boolean getUseMeanBasedSplit() { return this.UseMeanBasedSplit; }

   /**
    * Description of method getUseMeanNodeModels.
    *
    * @return Description of return value.
    */
   public boolean getUseMeanNodeModels() { return this.UseMeanNodeModels; }

   /**
    * Description of method getUseMidPointBasedSplit.
    *
    * @return Description of return value.
    */
   public boolean getUseMidPointBasedSplit() {
      return this.UseMidPointBasedSplit;
   }

   /**
    * Description of method getUseOneHalfSplit.
    *
    * @return Description of return value.
    */
   public boolean getUseOneHalfSplit() { return this.UseOneHalfSplit; }

   /**
    * Description of method getUsePopulationBasedSplit.
    *
    * @return Description of return value.
    */
   public boolean getUsePopulationBasedSplit() {
      return this.UsePopulationBasedSplit;
   }


   /**
    * Description of method instantiateBiasFromProperties.
    */
   public void instantiateBiasFromProperties() {
      // Nothing to do in this case since properties are reference directly by
      // the algorithm and no other control parameters need be set.  This may
      // not be the case in general so this stub is left open for future
      // development.
   }

   /**
    * Description of method setMaxTreeDepth.
    *
    * @param  value Description of parameter value.
    *
    * @throws PropertyVetoException Description of exception
    *                               PropertyVetoException.
    */
   public void setMaxTreeDepth(int value) throws PropertyVetoException {

      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }

      this.MaxTreeDepth = value;
   }

   /**
    * public void setUseSimpleBooleanSplit (boolean value)
    * {this.UseSimpleBooleanSplit = value;} public boolean
    * getUseSimpleBooleanSplit () {return this.UseSimpleBooleanSplit;} public
    * void setPrintEvolvingModels (boolean value) { this.PrintEvolvingModels =
    * value;} public boolean getPrintEvolvingModels () {return
    * this.PrintEvolvingModels;} private int MinDecompositionPopulation = 20;
    *
    * @param  value Description of parameter value.
    *
    * @throws PropertyVetoException Description of exception
    *                               PropertyVetoException.
    */
   public void setMinDecompositionPopulation(int value)
      throws PropertyVetoException {

      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }

      this.MinDecompositionPopulation = value;
   }

   /**
    * private double MinErrorReduction = 0.0;
    *
    * @param value Description of parameter value.
    */
   public void setMinErrorReduction(double value) {
      this.MinErrorReduction = value;
   }

   /**
    * public boolean SaveNodeExamples = false;
    *
    * @param value Description of parameter value.
    */
   public void setSaveNodeExamples(boolean value) {
      this.SaveNodeExamples = value;
   }

   /**
    * public boolean UseLinearNodeModels = false;
    *
    * @param value Description of parameter value.
    */
   public void setTrace(boolean value) { this._Trace = value; }

   /**
    * public boolean UseLinearNodeModels = false;
    *
    * @param value Description of parameter value.
    */
   public void setUseLinearNodeModels(boolean value) {
      this.UseLinearNodeModels = value;
      this.UseMeanNodeModels = !value;
   }

   /**
    * public boolean UseMeanBasedSplit = true;
    *
    * @param value Description of parameter value.
    */
   public void setUseMeanBasedSplit(boolean value) {
      this.UseMeanBasedSplit = value;
   }

   /**
    * public boolean UseMeanNodeModels = true;
    *
    * @param value Description of parameter value.
    */
   public void setUseMeanNodeModels(boolean value) {
      this.UseMeanNodeModels = value;
      this.UseLinearNodeModels = !value;
   }

   /**
    * public boolean UseMidPointBasedSplit = false;
    *
    * @param value Description of parameter value.
    */
   public void setUseMidPointBasedSplit(boolean value) {
      this.UseMidPointBasedSplit = value;
   }

   /**
    * public boolean UseOneHalfSplit = false;
    *
    * @param value Description of parameter value.
    */
   public void setUseOneHalfSplit(boolean value) {
      this.UseOneHalfSplit = value;
   }

   /**
    * public boolean UsePopulationBasedSplit = false;
    *
    * @param value Description of parameter value.
    */
   public void setUsePopulationBasedSplit(boolean value) {
      this.UsePopulationBasedSplit = value;
   }


} // end class DecisionTreeInducer
