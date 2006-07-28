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
package ncsa.d2k.modules.core.discovery.cluster.sample;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;


/**
 * <p>Title: ClusterAssignmentOPT</p>
 *
 * <p>Description: Takes a set of centroids and a table and repeatedly assigns
 * table rows to clusters whose centroids are closest in vector space. When one
 * assignment is completed new centroids are calculated and the process is
 * repeated.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class ClusterAssignmentOPT extends ComputeModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 49009208285557301L;

   /** The ID for euclidean distance metric. */
   static public final int s_Euclidean = 0;

   /** The ID for menhattan distance metric. */
   static public final int s_Manhattan = 1;

   /** The ID for cosine distance metric. */
   static public final int s_Cosine = 2;

   //~ Instance fields *********************************************************

   /** start time of computing. */
   private long m_start = 0;

   /** the clustering method to be used. */
   protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /** the distance metric to be used. */
   protected int _distanceMetric = s_Euclidean;

   /** indices into the input table's input features. */
   protected int[] _ifeatures = null;


   /**
    * Check missing values flag. If set to true, this module verifies that the
    * input table has no missing values. (in the precense of missing values this
    * module throws exception)
    */

   protected boolean _mvCheck = true;

   /** number of iteration to perform. */
   protected int m_numAssignments = 5;


   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   protected boolean m_verbose = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new ClusterAssignmentOPT object.
    */
   public ClusterAssignmentOPT() { }

   //~ Methods *****************************************************************

   /**
    * Perform the work of the module.
    *
    * @throws Exception
    */
   protected void doit() throws java.lang.Exception {
      ParameterPoint pp = (ParameterPoint) pullInput(0);
      _clusterMethod = (int) pp.getValue(0);
      _distanceMetric = (int) pp.getValue(1);
      m_numAssignments = (int) pp.getValue(2);

      Table initcenters = (Table) this.pullInput(1);
      Table initEntities = (Table) this.pullInput(2);

      ClusterRefinement refiner =
         new ClusterRefinement(_clusterMethod,
                               _distanceMetric, m_numAssignments,
                               this.getVerbose(),
                               this.getCheckMissingValues(), getAlias());
      this.pushOutput(refiner.assign(initcenters, initEntities), 0);
   }

   /**
    * Return the value if the check missing values flag.
    *
    * @return true if this module was set to check for missing values.
    */
   public boolean getCheckMissingValues() { return _mvCheck; }

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
         return "Control parameters for the module.";
      } else if (inputIndex == 1) {
         return "Cluster Model or Table containing the initial centroids.";
      } else if (inputIndex == 2) {
         return "Table containing all the examples to cluster.";
      } else {
         return "No such input.";
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


      if (inputIndex == 0) {
         return "Parameter Point";
      } else if (inputIndex == 1) {
         return "Cluster Model or Table";
      } else if (inputIndex == 2) {
         return "Table";
      } else {
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
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s = "<p>Overview: ";
      s +=
         "This module finds a locally optimal clustering by iteratively " +
         "assigning examples to ";
      s += "selected points in vector space. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s += "There are two versions of this module. ";
      s += "The <i>OPT</i>, optimizable, version uses control ";
      s +=
         "parameters encapsulated in a <i>Parameter Point</i> to direct the " +
         "cluster assignment and refinement behavior. ";
      s += "The control parameters specify a <i>";
      s += CLUSTER_METHOD;
      s += "</i>, a <i>";
      s += DISTANCE_METRIC;
      s += "</i>, and a <i>";
      s += MAX_ITERATIONS;
      s +=
         "</i>.  These parameters are set as properties in the non-OPT " +
         "version of the module. ";
      s += "</p>";

      s += "<p>";
      s +=
         "This module takes a set of cluster centroids and a set of examples " +
         "to be clustered ";
      s += "and repeatedly assigns ";
      s +=
         "the examples to the cluster whose centroid is closest in vector " +
         " space, where distance is ";
      s += "computed using the specified <i>";
      s += DISTANCE_METRIC;
      s +=
         "</i>.  When one assignment is completed, new centroids are " +
         "calculated from the clusters just formed and ";
      s += "the process is repeated.  The algorithm will iterate at most <i>";
      s += MAX_ITERATIONS;
      s +=
         "</i> times, halting sooner if the current iteration produces results " +
         " not significantly ";
      s += "different from the previous iteration. ";

      s +=
         "The initial centroids are input via the <i>Cluster Model or " +
         "Table</i> port, and the ";
      s +=
         "number of initial centroids determines the <i>Number of " +
         "Clusters</i> formed. ";
      s +=
         "The set of examples to be clustered are input via the <i>Table</i> " +
         "port, and ";
      s +=
         "the number of rows in that table determines the <i>Number of " +
         "Examples</i> in the final clusters. ";
      s += "</p>";

      s +=
         "<p>The Hierarchical Agglomerative Clustering (HAC) algorithm, a " +
         "bottom-up strategy, ";
      s +=
         "is run on the final set of clusters to build the cluster tree from " +
         "the cut to the root. ";
      s +=
         "The method used to determine intercluster similarity is controlled " +
         "by the <i>";
      s += CLUSTER_METHOD;
      s += "</i> parameter. ";
      s +=
         "The cluster tree is stored in a newly formed model, <i>Cluster Model" +
         "</i>, along with the initial table ";
      s += "of examples and the set of clusters formed.";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s +=
         "The <i>Cluster Model or Table</i> and <i>Table</i> inputs must have " +
         "the same underlying ";
      s += "table structure. ";
      s +=
         "That is, attribute types, order, and input features (if specified), " +
         "must be identical. ";
      s +=
         "The clustering does not work if the input data contains " +
         "missing values. ";
      s +=
         "The algorithm operates on numeric and boolean data types.  " +
         "If the data to be clustered ";
      s +=
         "contains nominal data types, it should be converted prior to " +
         "performing the clustering. ";
      s +=
         "The <i>Scalarize Nominals</i> module can be used to convert nominal " +
         "types into boolean values. ";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The second input table, <i>Table</i>, is included in the <i>Cluster " +
         "Model</i>.  Neither input table ";
      s += "is modified.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "The time complexity is linear in the <i>Number of Examples</i> times " +
         "the <i>Number of Iterations</i>. ";
      s +=
         "The algorithm repeatedly builds two times <i>Number of Clusters</i> " +
         "table clusters from ";
      s +=
         "<i>Number of Examples</i> table clusters, and requires heap " +
         "resources to that extent.  A single ";
      s +=
         "table cluster's memory size will vary as the size of the individual " +
         "examples being clustered.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Cluster Assignment"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {


      if (outputIndex == 0) {
         return "Newly created Cluster Model.";
      } else {
         return "No such output.";
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


      if (outputIndex == 0) {
         return "Cluster Model";
      } else {
         return "No such output";
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
      String[] out = {
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"
      };

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
      PropertyDescription[] descriptions = new PropertyDescription[2];

      descriptions[0] =
         new PropertyDescription("checkMissingValues",
                                 CHECK_MV,
                                 "If this property is true, the module will perform a check for missing" +
                                 " values in the input table. ");

      descriptions[1] =
         new PropertyDescription("verbose",
                                 VERBOSE,
                                 "If this property is true, the module will write verbose status " +
                                 " information to the console.");

      return descriptions;
   }

   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return m_verbose; }


   /**
    * Sets the check missing values flag.
    *
    * @param b If true then this module check for missing values in the input
    *          Table.
    */
   public void setCheckMissingValues(boolean b) { _mvCheck = b; }


   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { m_verbose = b; }
} // end class ClusterAssignmentOPT
