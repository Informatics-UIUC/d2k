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

import ncsa.d2k.core.modules.OrderedReentrantModule;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;


/**
 * Generate a model given a set of examples and an error function.  Subclasses
 * must implement the generateModel() and instantiateBiasFromProperties()
 * abstract methods defined here, and should also define
 * getModuleInfo() and getModuleName().
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class FunctionInducer extends OrderedReentrantModule
   implements Cloneable {

   //~ Instance fields *********************************************************

   /** Description of field BiasParameters. */
   double[] BiasParameters;

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
   public abstract Model generateModel(ExampleTable examples,
                                       ErrorFunction errorFunction)
      throws Exception;

   /**
    * Description of method instantiateBiasFromProperties.
    *
    * @throws Exception Description of exception Exception.
    */
   public abstract void instantiateBiasFromProperties() throws Exception;

   /**
    * Enable cloning.
    *
    * @return Object a deep copy of this.
    *
    * @throws CloneNotSupportedException if cloning is not supported
    */
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }


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
            throw new Exception("output attribute " + i + " must be numeric");
         }
      }

      ErrorFunction errorFunction = (ErrorFunction) this.pullInput(1);


      instantiateBiasFromProperties();

      Model model = null;

      model = generateModel(exampleSet, errorFunction);

      this.pushOutput(model, 0);
   } // end method doit

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
            return "No such input";
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
            return "NO SUCH INPUT!";
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
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "FunctionInducer - not a functiona module in itself, it is a base class for MeanOutputInducer.";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "FunctionInducer"; }

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
            return "No such output";
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
            return "NO SUCH OUTPUT!";
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
} // end class FunctionInducer
