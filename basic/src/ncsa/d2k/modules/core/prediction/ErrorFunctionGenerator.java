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


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;

import javax.swing.*;


/**
 *  <p>Overview: This module produces a string that identifies an error function
 * to be used with model builders.</p>
 * <p>Detailed Description: Error functions are used to measure the accuracy of
 * a model. There are a number of different ways the accuracy can be measured
 * or represented. The name of the error function is user selectable via a
 * custom properties editor. The properties editor provides the names of all
 * the supported error functions. The currently supported error functions are
 * <i>Absolute</i>, <i>Classification</i>, <i>Likelihood</i> and
 * <i>Variance</i>.</p><p>The absolute error is the sum of all the differences
 * in the predicted and actual values. Classification will only work if there
 * is one output feature. It will yield 0 if there is no classification error,
 * 1 otherwise. Likelihood returns the negative of the sum of the log of
 * probabilities of the actual classes. This is useful when using Likelihood
 * to guide the formation of the Probability Density Function based models.
 * Variance returns the sum of all the squared differences between predicted
 * and actual output values.</p
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ErrorFunctionGenerator extends ComputeModule {

   //~ Instance fields *********************************************************

   /** the name of the error function to use */
   private String ErrorFunctionName = "Absolute";

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      ErrorFunction errorFunction =
         new ErrorFunction(ErrorFunction.getErrorFunctionIndex(ErrorFunctionName));

      this.pushOutput(errorFunction, 0);

   }

   /**
    * Description of method getErrorFunctionName.
    *
    * @return Description of return value.
    */
   public String getErrorFunctionName() { return this.ErrorFunctionName; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

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
      String[] types = {};

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module produces a string that identifies an error function to" +
             " be used with model builders.</p>" +
             "<p>Detailed Description: Error functions are" +
             " used to measure the accuracy of a model. There are a number of different ways the accuracy" +
             " can be measured or represented. The name of the error function is user selectable" +
             " via a custom properties editor. The properties editor" +
             " provides the names of all the supported error functions. The currently supported" +
             " error functions are <i>Absolute</i>, <i>Classification</i>, <i>Likelihood</i> and <i>Variance</i>." +
             " </p><p>The absolute error is the sum of all the differences in the predicted and actual " +
             " values. Classification will only work if there is one output feature. It will yield" +
             " 0 if there is no classification error, 1 otherwise. Likelihood returns the negative of" +
             " the sum of the log of probabilities of the actual classes. This is useful when using" +
             " Likelihood to guide the formation of the Probability Density Function based models. Variance" +
             " returns the sum of all the squared differences between predicted and actual output" +
             " values.</p>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Error Function"; }

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
            return "<p>This string identifies the error function by name.</p>";

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
            return "Error Function";

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
      String[] types = { "ncsa.d2k.modules.core.prediction.ErrorFunction" };

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
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] =
         new PropertyDescription("errorFunctionName", "Error Function",
                                 "The name of the error function, can be Absolute, Classification, Likelihood or Variance.");

      return pds;
   }

   /**
    * return a reference a custom property editor to select the percent test and
    * train.
    *
    * @return reference a custom property editor
    */
   public CustomModuleEditor getPropertyEditor() {
      return new SetErrorFunction();
   }

   /**
    * Description of method setErrorFunctionName.
    *
    * @param value Description of parameter value.
    */
   public void setErrorFunctionName(String value) {
      this.ErrorFunctionName = value;
   }

   //~ Inner Classes ***********************************************************

   /**
    * This panel displays the editable properties of the SimpleTestTrain
    * modules.
    *
    * @author  Thomas Redman
    * @version $Revision$, $Date$
    */
   class SetErrorFunction extends JPanel implements CustomModuleEditor {
      final String[] errors =
      { "Absolute", "Classification", "Likelihood", "Variance" };
      JComboBox errorsSelection = new JComboBox(errors);

      SetErrorFunction() {
         JLabel tt = new JLabel("Error Function");
         tt.setToolTipText(ErrorFunctionGenerator.this
                              .getPropertiesDescriptions()[0].getDescription());
         this.add("West", tt);
         errorsSelection.setSelectedItem(ErrorFunctionName);
         this.add("Center", errorsSelection);
      }

      /**
       * Update the fields of the module.
       *
       * @return a string indicating why the properties could not be set, or
       *         null if successfully set.
       *
       * @throws Exception Description of exception Exception.
       */
      public boolean updateModule() throws Exception {
         String newError = (String) errorsSelection.getSelectedItem();

         if (ErrorFunctionName.equals(newError)) {
            return false;
         }

         // we have a new error function name.
         ErrorFunctionName = newError;

         return true;
      }
   } // end class SetErrorFunction
} // end class ErrorFunctionGenerator
