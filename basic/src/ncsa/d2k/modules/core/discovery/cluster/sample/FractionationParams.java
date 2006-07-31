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
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .FractionationParams_Props;


/**
 * <p>Title: FractionationParams</p>
 *
 * <p>Description: Module to input parameters for KMeans</p>
 *
 * The Coverage clustering algorithm is a type of kmeans approach where a sample
 * set is formed as follows: In the FractionationSampler, the initial examples
 * (converted to clusters) by a key attribute denoted by <i>Sort Attribute</i>.
 * The set of sorted clusters is then segmented into equal partitions of size
 * <i>maxPartitionsize</i> Each of these partitions is then passed through the
 * agglomerative clusterer to produce <i>numberOfClusters</i> clusters. All the
 * clusters are gathered together for all partitions and the entire process is
 * repeated until only <i>Number Of Clusters</i> clusters remain. The sorting
 * step is to encourage like clusters into same partitions. The final cluster's
 * centroids are used as the initial \"means\" for the cluster assignment
 * module. The assignment module, once it has made refinements, outputs the
 * final <i>Cluster Model</i>. This algorithm is comprised of four modules: this
 * module (FractionationParams), the sampler (FractionationSamplerOPT), the
 * clusterer (HierAgglomClustererOPT) and the cluster refiner
 * (ClusterAssignment).
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class FractionationParams extends FractionationParamsOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2450065861407201831L;

   //~ Constructors ************************************************************


   /**
    * Creates a new FractionationParams object.
    */
   public FractionationParams() { }

   //~ Methods *****************************************************************

   /**
    * Perform the work of the module.
    *
    * @throws Exception
    */
   protected void doit() throws Exception {

      Table tab = (Table) this.pullInput(0);
      doingit(tab);

   }


   /**
    * Returns the value of the Clustering Method property.
    *
    * @return int The value of the Clustering Method property.
    */
   public int getClusterMethod() { return _clusterMethod; }


   /**
    * Returns the value of the Distance Metric property.
    *
    * @return int The value of the Distance Metric property
    */
   public int getDistanceMetric() { return _distanceMetric; }


   /**
    * Returns the value of the HAC Distance Threshold property.
    *
    * @return int The value of the HAC Distance Threshold property, should be in
    *         the range [1,100].
    */
   public int getHacDistanceThreshold() { return _hacDistanceThreshold; }


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
         return "Table of examples to cluster";
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

      if (inputIndex == 0) {
         return "Table";
      } else {
         return "";
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
      String[] in = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return in;
   }


   /**
    * Returns the value of the Sort Attribute property.
    *
    * @return int The value of the Sort Attribute property.
    */
   public int getNthSortTerm() { return _nthSortTerm; }

   /**
    * Returns the Number of Clusters property.
    *
    * @return int The Number of Clusters property.
    */
   public int getNumClusters() { return N; }


   /**
    * Returns the value of the Max Partition Size property.
    *
    * @return int The value of the Max Partition Size property.
    */
   public int getPartitionSize() { return _fracPart; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[7];
      pds[0] =
         new PropertyDescription("numClusters",
                                 "Number of Clusters",
                                 "This is the number of clusters you want to form (>= 2).");
      pds[1] =
         new PropertyDescription("clusterMethod",
                                 "Clustering Method",
                                 "The method to use for determining the distance between two clusters. " +
                                 "<p>WARDS METHOD: Use a minimum variance approach that sums the squared error " +
                                 "(distance) for every point in the cluster to the cluster centroid.</p>" +
                                 "<p>SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
                                 "<p>COMPLETE LINK: Distance of furthest pair (one from each cluster).</p>" +
                                 "<p>UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
                                 "<p>WPGMA: Weighted pair group method using arithmetic averages.</p>" +
                                 "<p>UPGMC: Unweighted pair group method using centroids.</p>" +
                                 "<p>WPGMC: Weighted pair group method using centroids.</p>");
      pds[2] =
         new PropertyDescription("distanceMetric",
                                 "Distance Metric",
                                 "This property determine the type of distance function used to calculate " +
                                 "distance between two examples." +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");
      pds[3] =
         new PropertyDescription("hacDistanceThreshold",
                                 "HAC Distance Threshold",
                                 "This property specifies the percent of the max distance to use " +
                                 "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                 "is approximated by taking the min and max of each attribute and forming a " +
                                 "min example and a max example -- then finding the distance between the two.");
      pds[4] =
         new PropertyDescription("partitionSize",
                                 "Max Partition Size",
                                 "The size of partitions to use in the sampling process.");
      pds[5] =
         new PropertyDescription("NthSortTerm",
                                 "Sort Attribute",
                                 "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
      pds[6] =
         new PropertyDescription("refinementMaxIterations",
                                 "Number of Assignment Passes",
                                 "This property specifies the number of iterations of cluster refinement to perform (> 0).");

      return pds;
   } // end method getPropertiesDescriptions


   /**
    * Return a custom gui for setting properties.
    *
    * @return CustomModuleEditor A custorm properties editor to allow the user
    *         to set the properties.
    */
   public CustomModuleEditor getPropertyEditor() {
      return new FractionationParams_Props(this);
   }


   /**
    * Returns the value of the Number of Assignment Passes property.
    *
    * @return int The value of the Number of Assignment Passes property.
    */
   public int getRefinementMaxIterations() { return _maxIterations; }

   /**
    * Sets the value of the Clustering Method property.
    *
    * @param noc The value for the Clustering Method property, should be >=2
    */
   public void setClusterMethod(int noc) { _clusterMethod = noc; }

   /**
    * Sets the value of the Distance Metric property.
    *
    * @param dm The value for the Distance Metric property
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }

   /**
    * Sets the value of the HAC Distance Threshold property.
    *
    * @param noc The value for the HAC Distance Threshold property, should be in
    *            the range [1,100].
    */
   public void setHacDistanceThreshold(int noc) { _hacDistanceThreshold = noc; }

   /**
    * Sets the value of the Sort Attribute property.
    *
    * @param mt The value for the Sort Attribute property.
    */
   public void setNthSortTerm(int mt) { _nthSortTerm = mt; }


   /**
    * Sets the Number of Clusters property.
    *
    * @param i The Number of Clusters property.
    */
   public void setNumClusters(int i) { N = i; }

   /**
    * Sets the value of the Max Partition Size property.
    *
    * @param noc The value of the Max Partition Size property.
    */
   public void setPartitionSize(int noc) { _fracPart = noc; }

   /**
    * Sets the value of the Number of Assignment Passes property.
    *
    * @param dm The value for the Number of Assignment Passes property, should
    *           be >0.
    */
   public void setRefinementMaxIterations(int dm) { _maxIterations = dm; }

} // end class FractionationParams
