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

import ncsa.d2k.core.modules.ModelProducerModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;


/**
 * Description of class IncrementingModule.
 *
 * @author  vered goren
 * @version 1.0
 *
 * <p>receives a model and a data set and updates the model with the new data. outputs the incremented model.</p>
 *
 * @todo    after this module is done, move to a suitable package
 *          ncsa.d2k.modules.core.prediction
 * @todo    revise of the output model. should it be a deep copy of the model?
 *          problem is when this module changes the output model in the next
 *          iteration while the model may be used by other modules..... right
 *          now this module makes a deep copy of the input model before updating
 *          it.
 */

public class IncrementingModule extends ModelProducerModule {

   //~ Instance fields *********************************************************

   /** true if a deep copy should be made */
   private boolean deepCopy = true;


   /** the updatable model module */
   protected UpdateableModelModule model;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   protected void doit() throws java.lang.Exception {
      ExampleTable tbl = (ExampleTable) pullInput(1);
      model = ((UpdateableModelModule) pullInput(0));

      if (deepCopy) {
         model = model.copy();
      }

      model.update(tbl);

      pushOutput(model, 0);
   }

   /**
    * Set this property to true if you wish the model to be copied before retraining
    *
    * @return value of deep copy
    */
   public boolean getDeepCopy() { return deepCopy; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  parm1 Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "An updateable model to be retrained incrementally";

         case 1:
            return "Training Data";

         default:
            return "no such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  parm1 Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int parm1) {

      switch (parm1) {

         case 0:
            return "Updateable Model";

         case 1:
            return "Example Table";

         default:
            return "no such input";
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
      String[] arr = new String[2];
      arr[0] = "ncsa.d2k.modules.core.prediction.UpdateableModelModule";
      arr[1] = "ncsa.d2k.modules.core.datatype.table.ExampleTable";

      return arr;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module receives an updateable model " +
             "and a data set and retrains the model.</p>" +
             "<p>Detailed Description: The module receives an <i>" +
             "Updateable Model</i> and an <i>Example Table</i> as inputs. " +
             "The module creates a deep copy of the model and then updates the model " +
             "with the input table. Thus the older version of the model can be used " +
             "by other modules in an itinerary without being affected by the retraining." +
             "After the retraining the model " +
             "is being output.</p>";

      /*"If the input model is to be used by other modules in the itinerary then
       * you should " +
       * "set 'Create Deep Copy' property to true, in order to not distort the
       * correctness " +"of the models, generated during runtime." */
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Incrementing Module"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "An updateable model after retraining.";

         default:
            return "no such output";
      }

   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int parm1) {

      switch (parm1) {

         case 0:
            return "Updateable Model";

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
      String[] arr = new String[1];
      arr[0] = "ncsa.d2k.modules.core.prediction.UpdateableModelModule";

      return arr;

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
         new PropertyDescription("deepCopy", "Create Deep Copy of Input Model",
                                 "Set this property to true if you wish the model to be copied before retraining.");

      return pds;
   }

   /**
    * Set this property to true if you wish the model to be copied before retraining.
    *
    * @param bl value
    */
   public void setDeepCopy(boolean bl) { deepCopy = bl; }


} // end class IncrementingModule
