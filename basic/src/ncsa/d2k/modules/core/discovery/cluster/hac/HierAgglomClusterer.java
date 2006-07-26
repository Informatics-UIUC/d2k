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
package ncsa.d2k.modules.core.discovery.cluster.hac;


import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .HierAgglomClusterer_Props;


/**
 * <p>Title: HierAgglomClusterer</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 *
 *          <p>TODO: change distance method to accommodate sparse matrices</p>
 */

public class HierAgglomClusterer extends HierAgglomClustererOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -4384454706350438151L;

   //~ Instance fields *********************************************************

   /** The clustering method to be used. */
   protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /** The distance metric to be used. */
   protected int _distanceMetric = HAC.s_Euclidean_DISTANCE;

   /** The number of clusters to create. */
   protected int _numberOfClusters = 5;

   /**
    * The percentage of the <i>maximum distance</i> to use as a cutoff value to
    * halt cluster agglomeration.
    */
   protected int _thresh = 0;

   //~ Constructors ************************************************************

   /**
    * Creates a new HierAgglomClusterer object.
    */
   public HierAgglomClusterer() { }

   //~ Methods *****************************************************************

   /**
    * Performs a bottom-up, hierarchical clustering of the examples in the input
    * table.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */

   protected void doit() throws Exception {
      HAC hac =
         new HAC(this.getClusterMethod(), this.getDistanceMetric(),
                 this.getNumberOfClusters(), this.getDistanceThreshold(),
                 getVerbose(), this.getCheckMissingValues(), getAlias());
      this.pushOutput(hac.buildModel((Table) this.pullInput(0)), 0);
   }

   /**
    * Returns the integer ID of the clustering method of this module. Clustering
    * method IDs are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    *
    * @return The integer ID of the clustering method of this module.
    */
   public int getClusterMethod() { return _clusterMethod; }

   /**
    * Returns the integer ID of the distance metric of this module. Distance
    * metric IDs are defined in <code>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    *
    * @return The integer ID of the distance metric.
    */
   public int getDistanceMetric() { return _distanceMetric; }

   /**
    * Returns the threshold value used by this module.
    *
    * @return The value of the threshold used by this module.
    */
   public int getDistanceThreshold() { return _thresh; }


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
         return "Table of entities to cluster";
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
    * Returns the number of clusters to be formed by this module.
    *
    * @return The number of cluster property's value.
    */
   public int getNumberOfClusters() { return _numberOfClusters; }

   // ========================
   // D2K Abstract Overrides

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // presentation order (dictated by dialog layout): 0-CLUSTER_METHOD,
      // 1-NUM_CLUSTERS, 2-AUTO_CLUSTER, 3-DISTANCE_THRESHOLD 4-DISTANCE_METRIC,
      // 5-CHECK_MV, 6-VERBOSE 3-HAC_DISTANCE_THRESHOLD Following code is
      // (almost) cut/paste across modules and indices of pds are adjusted for
      // the appropriate order for the dialog that is used in this module. The
      // first variable in each Property Description isn't used in this case;
      // just the 2nd and 3rd for dialog label and help.

      PropertyDescription[] pds = new PropertyDescription[7];
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
                                 "distance between two points in the space of example values. " +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

      pds[1] =
         new PropertyDescription("numClusters",
                                 NUM_CLUSTERS,
                                 "This property specifies the number of clusters to form. It must be greater than 1. " +
                                 "If <i>" +
                                 AUTO_CLUSTER +
                                 "</i> is enabled, the <i>" +
                                 DISTANCE_THRESHOLD +
                                 "</i> value will halt cluster agglomeration thus determining the number of clusters formed " +
                                 "independent of this property's setting.");

      pds[3] =
         new PropertyDescription("distanceThreshold",
                                 DISTANCE_THRESHOLD,
                                 "This property specifies the percentage of the <i>maximum distance</i> to use " +
                                 "as a cutoff value to halt cluster agglomeration.  " +
                                 "When the distance between the two clusters that are closest exceeds the cutoff value, cluster agglomeration stops, " +
                                 "independent of the value of the <i>" +
                                 NUM_CLUSTERS +
                                 "</i> property.  Lower values for the <i>" +
                                 DISTANCE_THRESHOLD +
                                 "</i> result in more clusters.   " +
                                 "See the <i>Hier. Agglom. Clusterer</i> module information for further details on " +
                                 "this is property. ");

      pds[2] =
         new PropertyDescription("autoCluster",
                                 AUTO_CLUSTER,
                                 "If this property is true, the <i>" +
                                 DISTANCE_THRESHOLD +
                                 "</i> will be used to control when the cluster formation process halts independent of the value set " +
                                 "for <i>" +
                                 NUM_CLUSTERS +
                                 "</i>");

      pds[5] =
         new PropertyDescription("checkMissingValues",
                                 CHECK_MV,
                                 "If this property is true, the module will perform a check for missing values in the input table. ");

      pds[6] =
         new PropertyDescription("verbose",
                                 VERBOSE,
                                 "If this property is true, the module will write verbose status information to the console.");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Return the Custom Properties Editor of this module.
    *
    * @return CustomModuleEditor The Custom Properties Editor of this module.
    */
   public CustomModuleEditor getPropertyEditor() {
      return new HierAgglomClusterer_Props(this, true, true);
   }

   /**
    * Sets the cluster method property.
    *
    * @param noc A cluster method ID. Must be a value in the boundaries of
    *            <code>
    *            ncsa.d2k.modules.core.discovery.cluster.hac.Hac.s_ClusterMethodLabels</code>
    *            array.
    *
    * @see   <code>ncsa.d2k.modules.core.discovery.cluster.hac.Hac</code>
    */
   public void setClusterMethod(int noc) { _clusterMethod = noc; }

   /**
    * Sets the distanc metric property.
    *
    * @param dm A distanc metric ID. Must be a value in the boundaries of <code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.Hac.s_DistanceMetricLabels</code>
    *           array.
    *
    * @see   <code>ncsa.d2k.modules.core.discovery.cluster.hac.Hac</code>
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }

   /**
    * Sets the distance threshold property.
    *
    * @param noc The new value for the distance threshold property. Must be an
    *            integer between 1 and 100 (represent a percentage).
    */
   public void setDistanceThreshold(int noc) { _thresh = noc; }

   /**
    * Sets the number of clusters property.
    *
    * @param noc Number of clusters to be formed by this module.
    */
   public void setNumberOfClusters(int noc) { _numberOfClusters = noc; }

} // end class HierAgglomClusterer
