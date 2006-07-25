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


import ncsa.d2k.core.modules.ModelModule;
import ncsa.d2k.core.modules.ModelSelectorModule;


/**
 * A very simple ModelSelector that takes a model as input and returns it in the
 * getModel() method, "catching" the model in the Generated Models pane.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class CatchModel extends ModelSelectorModule {

   //~ Instance fields *********************************************************

   /** the model module */
   private ModelModule theModel;

   //~ Methods *****************************************************************


   /**
    * Pre-execution initializations and cleanup.
    */
   public void beginExecution() { theModel = null; }

   /**
    * Pull in a model and keep a reference to it
    */
   public void doit() {
      ModelModule mm = (ModelModule) pullInput(0);

      if (mm.getAlias() == null) {
         mm.setAlias(this.getAlias());
         // LAM-tlr
      }

      theModel = mm;
   }

   /**
    * Get a description of an input
    *
    * @param  i input index
    *
    * @return Description of the input
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The Prediction Model to catch and make available in the Generated Models session panel.";

         default:
            return "No such input";
      }
   }

   /**
    * Get the name of an input
    *
    * @param  i the input index
    *
    * @return Name of the input
    */
   public String getInputName(int i) {

      switch (i) {

         case 0:
            return "Prediction Model";

         default:
            return "No such input.";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.modules.PredictionModelModule" };

      return types;
   }

   /**
    * Return the model that was passed in.
    *
    * @return the model that was passed in.
    */
   public ModelModule getModel() {
      ModelModule mod = theModel;
      theModel = null;

      return mod;
   }

   /**
    * Description of the module's function
    *
    * @return Description of the module's function
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module takes a predictive model as input and \"catches\" it in the <i>Generated Models</i> ");
      sb.append("session pane.  ");
      sb.append("From there, it may be permanently saved by the user for reuse in another session. ");

      sb.append("</p><p>Description: ");
      sb.append("D2K itineraries are often used to generate models that predict the value of one or more ");
      sb.append("target (output) attributes based on the values of one or more input attributes. ");
      sb.append("These predictive models can be saved and applied at a later time to other datasets ");
      sb.append("with the same input attributes, generating predictions of the target attribute values ");
      sb.append("for each of the examples in the dataset. ");

      sb.append("</p><p>This module can be used to capture a predictive model that has been generated ");
      sb.append("to the <i>Generated Models</i> session pane.  From there it can be saved permanently ");
      sb.append("and reloaded in another D2K session. ");

      return sb.toString();
   }

   /**
    * the name of this module
    *
    * @return the name of this module
    */
   public String getModuleName() { return "Catch Model"; }

   /**
    * Description of the outputs
    *
    * @param  i output index
    *
    * @return the description of the output
    */
   public String getOutputInfo(int i) {

      switch (i) {

         default:
            return "No such output";
      }
   }

   /**
    * the name of the output
    *
    * @param  i the output index
    *
    * @return name of the output
    */
   public String getOutputName(int i) {

      switch (i) {

         default:
            return "No such output";
      }
   }

   /**
    * The types of inputs to this module
    *
    * @return a String[] containing the classes of the inputs
    */
   public String[] getOutputTypes() { return null; }
} // end class CatchModel
