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
package ncsa.d2k.modules.core.discovery.cluster.util;


import ncsa.d2k.core.modules.ModelModule;
import ncsa.d2k.core.modules.ModelSelectorModule;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;


/**
 * Description of class ClusterModelSelector.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */

/**
 * <p>Title:</p>
 *
 * <p>Description: A very simple ModelSelector that takes a ClusterModel as
 * input and returns it in the getModel() method. The model is passed as output,
 * unchanged.</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  $Author$
 * @version 1.0
 */
public class ClusterModelSelector extends ModelSelectorModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 1524727626171367583L;

   //~ Instance fields *********************************************************

   /**
    * A reference to the clustering model, to be retrieved via getModel method.
    */
   private ModelModule theModel;

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Set the module's model instnace to null.
    */
   public void beginExecution() { theModel = null; }


   /**
    * pulls in the cluster model in order to keep a reference to it. Pushes out
    * this model.
    */
   public void doit() {
      ClusterModel mm = (ClusterModel) pullInput(0);
      theModel = (ModelModule) mm;
      pushOutput(mm, 0);
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "A ClusterModel";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "ClusterModel";

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
      { "ncsa.d2k.modules.core.discovery.cluster.ClusterModel" };

      return types;
   }

   /**
    * Return the model that was passed in.
    *
    * @return The model that was passed in.
    */
   public ModelModule getModel() {
      ModelModule mod = theModel;
      theModel = null;

      return mod;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "A simple ModelSelector that takes a ClusterModel and passes it to "
         +"the model ";
      s += "jump-up pane. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "See the user's guide for information on how to work with models in "
         + "the workspace ";
      s += "jump-up pane.";
      s += "</p>";

      s += "<p>Data Handling: ";
      s += "The ClusterModel is passed as output unchanged. ";
      s += "</p>";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Cluster Model Selector"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "The model that was passed in, unchanged.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "ClusterModel";

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
      String[] types =
      { "ncsa.d2k.modules.core.discovery.cluster.ClusterModel" };

      return types;
   }
} // end class ClusterModelSelector
