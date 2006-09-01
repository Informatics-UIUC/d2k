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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;

import java.beans.PropertyVetoException;
// import Jama.Matrix;


/**
 * Description of class StepwiseLinearInducer.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class StepwiseLinearInducer extends StepwiseLinearInducerOpt {

   //~ Instance fields *********************************************************

   /** number of bias dimensions */
   protected int numBiasDimensions = 3;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
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

// ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(1);

      instantiateBiasFromProperties();

      Model model = null;

      model = generateModel(exampleSet, errorFunction);

      this.pushOutput(model, 0);
   } // end method doit

   /**
    * Get direction
    *
    * @return direction
    */
   public int getDirection() { return this.Direction; }

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
   public String getModuleName() { return "Stepwise Linear Inducer"; }

   /**
    * Get the number of rounds
    *
    * @return number of rounds
    */
   public int getNumRounds() { return this.NumRounds; }

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
         new PropertyDescription("useStepwise",
                                 "Use Stepwise",
                                 "When true, a stepwise regression procedure is followed, otherwise normal regression is used on all features.");

      pds[1] =
         new PropertyDescription("direction",
                                 "Direction of Search",
                                 "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");

      pds[2] =
         new PropertyDescription("numRounds",
                                 "Number of Feature Selection Rounds",
                                 "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");


      return pds;
   }

   /**
    * Get the value of UseStepwise
    *
    * @return UseStepwise
    */
   public boolean getUseStepwise() { return this.UseStepwise; }

   /**
    * Nothing to do in this case since properties are reference directly by the
    * algorithm and no other control parameters need be set. This may not be the
    * case in general so this stub is left open for future development.
    */
   public void instantiateBiasFromProperties() {
      // Nothing to do in this case since properties are reference directly by
      // the algorithm and no other control parameters need be set.  This may
      // not be the case in general so this stub is left open for future
      // development.
   }


   /**
    * Set the direction.  Must be -1 or 1
    *
    * @param  value new direction
    *
    * @throws PropertyVetoException if value is not -1 or 1
    */
   public void setDirection(int value) throws PropertyVetoException {

      if (!((value == -1) || (value == 1))) {
         throw new PropertyVetoException(" must be -1 or 1", null);
      }

      this.Direction = value;
   }


   /**
    * Set the number of rounds, must be greater than or equal to 1.
    *
    * @param  value new number of rounds
    *
    * @throws PropertyVetoException if value is less than one
    */
   public void setNumRounds(int value) throws PropertyVetoException {

      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }

      this.NumRounds = value;
   }


   /**
    * Set UseStepwise
    *
    * @param  value new UseStepwise
    *
    * @throws PropertyVetoException never
    */
   public void setUseStepwise(boolean value) throws PropertyVetoException {
      this.UseStepwise = value;
   }


} // end class StepwiseLinearInducer
