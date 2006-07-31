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

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .KMeansParams_Props;


/**
 * <p>Title: KMeansParams</p>
 *
 * <p>Description: Module to input parameters for KMeans</p>
 * <p>Description: Module to input parameters for KMeans</p>
 *
 * <p>Overview: This module is used to set control parameters for the KMeans
 * clustering algorithm.</p>
 *
 * <p>The complete KMeans algorithm is implemented by this module and two
 * others.</p>
 *
 * <p>Detailed Description: The KMeans clustering algorithm is an approach where
 * a sample set containing <i>Number of Clusters</i> rows is chosen from an
 * input table of examples and used as initial cluster centers. These initial
 * clusters undergo a series of assignment/refinement iterations, resulting in a
 * final cluster model.</p>
 *
 * <p>If an <i>Example Table</i> is passed to this module, the algorithm will
 * assign the examples (rows) to clusters based on the values of the input
 * attributes (columns) . If no input attributes have been specified, the
 * algorithm has no values to cluster on, and a single cluster will be formed.
 * The module <i>Choose Attributes</i> is typically used to form an <i>Example
 * Table</i>. In contrast, if a <i>Table</i> is passed to this module, the
 * algorithm will consider all attributes when forming the clusters.</p>
 *
 * <p> The KMeans algorithm implementation is comprised of three modules. This
 * module is used to set control parameters for the algorithm. A second module,
 * <i>Sample Table Rows</i>, builds the sample set from the input <i>Table</i>.
 * The third module, <i>Cluster Assignment</i>, refines the initial clusters in
 * a series of assignment passes. The control parameters set in this module are
 * passed as <i>Parameter Point</i>s to the the other two modules, and determine
 * their exact behavior.</p>
 *
 * <p>The <i>OPT</i>, optimizable, versions of the <i>Sample Table Rows</i> and
 * <i>Cluster Assignment</i></p>
 *
 * <p>modules must be used, as they can accept the <i>Parameter Point</i>
 * inputs.</p>
 * ";
 *
 * <p>Data Type Restrictions:</p>
 *
 * <p>The KMeans algorithm does not work if the input data being clustered
 * contains missing values. If missing values are detected an exception will be
 * raised.</p>
 *
 * <p>The KMeans algorithm operates on numeric and boolean data types. If the
 * data to be clustered</p>
 *
 * <p>contains nominal data types, it should be converted prior to performing
 * the KMeans clustering.</p>
 *
 * <p>The <i>Scalarize Nominals</i> module can be used to convert nominal types
 * into boolean values.</p>
 *
 * <p>Data Handling:</p>
 *
 * <p>The input table is not modified by this algorithm. However, it is passed
 * on to the other modules via the <i>Table</i> output port, and included as
 * part of the <i>Cluster Model</i> that is created.</p>
 *
 * <p>Scalability: This algorithm runs in time O(number of examples). See the
 * information for the component modules to understand the overall memory
 * requirements.</p>

 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class KMeansParams extends KMeansParamsOPT
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 1221005522234100368L;

   //~ Constructors ************************************************************


   /**
    * Creates a new KMeansParams object.
    */
   public KMeansParams() { }

   //~ Methods *****************************************************************

   /**
    * Perform the work of the module - creates 2 ParameterPoint objects abd
    * pushes them out.
    */
   protected void doit() {
      doingit();
      this.pushOutput(this.pullInput(0), 2);
   }


   /**
    * Returns the value of the Clustering Method property.
    *
    * @return int The value of the Clustering Method property
    */
   public int getClusterMethod() { return _clusterMethod; }

   /**
    * Returns the value of the Distance Metric property.
    *
    * @return int The value of the Distance Metric property
    */
   public int getDistanceMetric() { return _distanceMetric; }


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
         return "Table of examples to cluster.";
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
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Returns the value of the Maximum Number of Iterations property.
    *
    * @return int The value of the Maximum Number of Iterations property
    */
   public int getMaxIterations() { return _maxIterations; }

   /**
    * Returns the value of the Number of Clusters property.
    *
    * @return int The value of the Number of Clusters property
    */
   public int getNumClusters() { return N; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // presentation order (dictated by dialog layout): 0-CLUSTER_METHOD,
      // 1-SEED, 2-USE_FIRST, 3-NUM_CLUSTERS, 4-DISTANCE_METRIC,
      // 5-MAX_ITERATIONS Following code is cut/paste from
      // KMeansParamSpaceGenerator and indices of pds are adjusted for the
      // appropriate order.

      PropertyDescription[] pds = new PropertyDescription[6];
      pds[3] =
         new PropertyDescription("numClusters",
                                 NUM_CLUSTERS,
                                 "This property specifies the number of clusters to form. It must be greater than 1.");

      pds[1] =
         new PropertyDescription("seed",
                                 SEED,
                                 "The seed for the random number generator used to select the sample set of <i>" +
                                 NUM_CLUSTERS +
                                 "</i> table rows that defines the initial cluster centers. " +
                                 "It must be greater than or equal to 0. " +
                                 "If the same seed is used across runs with the same input table, the sample sets " +
                                 "will be identical. If <i>" +
                                 USE_FIRST +
                                 "</i> is selected, this seed is not used. ");

      pds[2] =
         new PropertyDescription("useFirst",
                                 USE_FIRST,
                                 "If this option is selected, the first <i>" +
                                 NUM_CLUSTERS +
                                 "</i> entries in the input table " +
                                 "will be used as the initial cluster centers, " +
                                 "rather than selecting a random sample set of table rows. ");

      pds[0] =
         new PropertyDescription("clusterMethod",
                                 CLUSTER_METHOD,
                                 "The method to use for determining the similarity between two clusters. " +
                                 "This similarity measure is used in formulating the tree that is part of the final cluster model. " +
                                 "<p>WARDS METHOD: Use a minimum variance approach that sums the squared error " +
                                 "(distance) for every point in the cluster to the cluster centroid.</p>" +
                                 "<p>SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
                                 "<p>COMPLETE LINK: Distance of furthest pair (one from each cluster).</p>" +
                                 "<p>UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
                                 "<p>WPGMA: Weighted pair group method using arithmetic averages.</p>" +
                                 "<p>UPGMC: Unweighted pair group method using centroids.</p>" +
                                 "<p>WPGMC: Weighted pair group method using centroids.</p>");

      pds[4] =
         new PropertyDescription("distanceMetric",
                                 DISTANCE_METRIC,
                                 "This property determines the type of distance function to use in calculating the " +
                                 "distance between two examples.  This distance is used in assigning points to clusters, and " +
                                 "in determining if there was sufficient movement since the last assignment iteration " +
                                 "to continue the refinement process. " +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

      pds[5] =
         new PropertyDescription("maxIterations",
                                 MAX_ITERATIONS,
                                 "This property specifies the maximum number of iterations of cluster " +
                                 "assignment/refinement to perform. " +
                                 "It must be greater than 0.  A check is performed after each iteration to determine if " +
                                 "the cluster centers have moved more than a small threshold amount.  If they have not, " +
                                 "the refinement process is stopped before the specified number of iterations. ");

      return pds;
   } // end method getPropertiesDescriptions

   // ========================
   // D2K Abstract Overrides

   /**
    * Return a custom gui for setting properties.
    *
    * @return CustomModuleEditor a GUI component to allow the user custom
    *         setting of the properties.
    */
   public CustomModuleEditor getPropertyEditor() {
      return new KMeansParams_Props(this);
   }

   /**
    * Returns the value of the seed for the random number generator.
    *
    * @return int The value of the seed for the random number generator
    */
   public int getSeed() { return seed; }

   /**
    * Returns the value of the value of Use First Rows property.
    *
    * @return boolean The value of Use First Rows property.
    */
   public boolean getUseFirst() { return useFirst; }

   /**
    * Sets the value of the Clustering Method property.
    *
    * @param noc int The value for the Clustering Method property
    */
   public void setClusterMethod(int noc) { _clusterMethod = noc; }


   /**
    * Sets The value of the Distance Metric property.
    *
    * @param dm int The value for the Distance Metric property
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }


   /**
    * Set the value of the Maximum Number of Iterations property.
    *
    * @param dm int The value of the Maximum Number of Iterations property
    */
   public void setMaxIterations(int dm) { _maxIterations = dm; }


   /**
    * Sets the value of the Number of Clusters property.
    *
    * @param i int The value for the Number of Clusters property
    */
   public void setNumClusters(int i) { N = i; }

   /**
    * Sets the seed for the random number generator.
    *
    * @param i int The seed for the random number generator
    */
   public void setSeed(int i) { seed = i; }


   /**
    * Sets the value of Use First Rows property.
    *
    * @param b boolean The value of Use First Rows property.
    */
   public void setUseFirst(boolean b) { useFirst = b; }

} // end class KMeansParams
