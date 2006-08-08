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
package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.VisModule;


/**
 * <p>Title: DendogramClusterVis</p>
 *
 * <p>Description: This module visualizes a ClusterModel object that is the
 * output of an hierarchical agglomerative clustering algorithm. The
 * visualization is of the dendogram produced by the agglomerative (bottom-up)
 * process.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class DendogramClusterVis extends VisModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 8387040272731066130L;

   //~ Constructors ************************************************************


   /**
    * Creates a new DendogramClusterVis object.
    */
   public DendogramClusterVis() { }

   //~ Methods *****************************************************************

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new DendogramPanel(this); }

   /**
    * The array of Strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   protected String[] getFieldNameMapping() { return null; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      if (inputIndex == 0) {
         return "ncsa.d2k.modules.core.discovery.cluster.ClusterModel";
      } else {
         return "";
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
            return "Cluster Model";

         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * Return a String array containing the datatypes the inputs to this module.
    *
    * @return The datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] out = {
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"
      };

      return out;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module visualizes a ClusterModel object that is the output of an ";
      s +=
         "hierarchical agglomerative clustering algorithm.  The visualization is ";
      s += "of the dendogram produced by the agglomerative (bottom-up) process.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "The dendogram produced represents a hard clustering of a data table using ";
      s +=
         "a hierarchical agglomerative clustering algorithm.  Some cluster models will ";
      s +=
         "contain complete dendogram trees (from the actual table rows to the single ";
      s +=
         "root cluster.  Other models will contain trees that start at some cluster cut ";
      s +=
         "(for example the clusters returned from a KMeans algorithm) to the root. ";
      s +=
         "Also, not all trees are monotonic (i.e. the height of subclusters is always <= ";
      s +=
         "to the height of their parents where height is measured in cluster dissimilarity. ";
      s +=
         "In particular, the centroid clustering methods (see HierAgglomClusterer props) are ";
      s += "known to sometimes produce non-monotonic dendogram trees.";
      s += "</p>";

      s += "<p> GUI Controls: ";
      s +=
         "If you double click on a cluster in the tree the dendogram will be repainted with ";
      s +=
         "the chosen cluster as root.  If you hit the reset button the original root will be ";
      s +=
         "restored.  If you double click on a cluster while holding down the shift key a table ";
      s +=
         "of values for that cluster will be displayed.  If you double click on a cluster ";
      s +=
         "while holding down the control key the centroid for that cluster will be displayed. ";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s += "The input ClusterModel must contain a table that is serializable.";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The input ClusterModel will be saved as part of the visualization.  It is not modified.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "The entire data table is saved as part of this visualization.  Sufficient ";
      s += "memory resources must be available to stage this data.";
      s += "</p>";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Dendogram Vis"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) { return ""; }

   /**
    * Return a String array containing the datatypes of the outputs of this
    * module.
    *
    * @return The datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] in = null;

      return in;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // hide properties that the user shouldn't udpate
      return new PropertyDescription[0];
   }

} // end class DendogramClusterVis
