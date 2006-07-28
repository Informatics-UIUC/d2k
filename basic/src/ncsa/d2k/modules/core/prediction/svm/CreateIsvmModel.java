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
package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.ModelProducerModule;
import ncsa.d2k.core.modules.PropertyDescription;


/**
 * Produces an IsvmModel. This module generates a new IsvmModel and initializes
 * it according to the properties, as they were set by the user (Nu parameter
 * and number of input features). Please note that if number of input features
 * does not match the future input Tables of the output model, an exception will
 * be thrown.
 *
 * @author  vered goren
 * @version $Revision$, $Date$
 */
public class CreateIsvmModel extends ModelProducerModule {

   //~ Instance fields *********************************************************

   /** SVM nu Parameter. */
   protected double nu;

   /** Number of input features in the SVM problem. */
   protected int numAttributes;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws Exception {

      IsvmModel d2k_model = new IsvmModel(numAttributes);
      d2k_model.setNu(nu);
      d2k_model.init();

      pushOutput(d2k_model, 0);

   }

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { }

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    */
   public void endExecution() { super.endExecution(); }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         default:
            return "No such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         default:
            return "No such input";
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
      String[] in = {};

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<P>Overview: Produces an initialized IsvmModel.</P>" +
             "<P>Detailed Description: This module generates a new IsvmModel and initializes it " +
             "according to the properties, as they are set by the user." +
             "Please note that if <I>Number Input Features</I> does not match the future" +
             " input Tables of the output model, an exception will be thrown when this model attempts " +
             " to train or predict.</P>";

   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create Isvm Model"; }

   /**
    * Get nu.
    *
    * @return nu
    */
   public double getNu() { return nu; }

   /**
    * Get num attributes.
    *
    * @return number of attributes
    */
   public int getNumAttributes() { return numAttributes; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "Initialized Isvm prediction model.";

         default:
            return "no such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "SVM Predictor";

         default:
            return "no such output";
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
      String[] out = { "ncsa.d2k.modules.PredictionModelModule" };

      return out;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] = new PropertyDescription("nu", "Nu", "SVM nu Parameter");
      pds[1] =
         new PropertyDescription("numAttributes",
                                 "Number Input Features",
                                 "Number of input features in the SVM problem");

      return pds;
   }

   /**
    * Set nu.
    *
    * @param dbl new nu
    */
   public void setNu(double dbl) { nu = dbl; }

   /**
    * Set num attributes.
    *
    * @param num new num attributes
    */
   public void setNumAttributes(int num) { numAttributes = num; }
} // end class CreateIsvmModel
