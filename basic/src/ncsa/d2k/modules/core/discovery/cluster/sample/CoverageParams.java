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
          .CoverageParams_Props;


/**
 * <p>Title: CoverageParams</p>
 *
 * <p>Description: Module to input parameters for Coverage</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class CoverageParams extends CoverageParamsOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 5468712932809104210L;

   //~ Constructors ************************************************************


   /**
    * Creates a new CoverageParams object.
    */
   public CoverageParams() { }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module: Creates 4 different parameter points
    * using the properties values, as set by the user.
    */
   protected void doit() {
      Table tab = (Table) this.pullInput(0);
      doingit(tab);
   }

   /**
    * Returns the ID of the cluster method to be used.
    *
    * @return int The ID of the cluster method to be used
    */
   public int getClusterMethod() { return _clusterMethod; }

   /**
    * Returns the value of the coverage distance threshold.
    *
    * @return int The value of the coverage distance threshold
    */
   public int getCoverageDistanceThreshold() { return _covthresh; }

   /**
    * Returns the value of Coverage Max Num Samples property.
    *
    * @return int The value of Coverage Max Num Samples property
    */
   public int getCoverageMaxNumSamples() { return _coverageMaxNumSamples; }

   /**
    * Returns the value of the distance metric property.
    *
    * @return int The value of the distance metric property.
    */
   public int getDistanceMetric() { return _distanceMetric; }

   /**
    * Returns the value of the distance threshold property.
    *
    * @return int The value of the distance threshold property
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
         return "Table of entities to cluster.";
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
    * Returns the value of the number of clusters property.
    *
    * @return The value of the number of clusters property
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
      PropertyDescription[] pds = new PropertyDescription[7];
      pds[0] =
         new PropertyDescription("numClusters",
                                 //"Number of Clusters",
                                 CoverageParamSpaceGenerator.NUM_CLUSTERS,
                                 "This is the number of clusters you want to form (>= 2).");
      pds[1] =
         new PropertyDescription("clusterMethod",
                                 //"Clustering Method",
                                 CoverageParamSpaceGenerator.CLUSTER_METHOD,
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
                                 //"Distance Metric",
                                 CoverageParamSpaceGenerator.DISTANCE_METRIC,
                                 "This property determine the type of distance function used to calculate " +
                                 "distance between two examples." +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");
      pds[3] =
         new PropertyDescription("hacDistanceThreshold",
                                 //"HAC Distance Threshold",
                                 CoverageParamSpaceGenerator.HAC_DISTANCE_THRESHOLD,
                                 "This property specifies the percent of the max distance to use " +
                                 "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                 "is approximated by taking the min and max of each attribute and forming a " +
                                 "min example and a max example -- then finding the distance between the two.");
      pds[4] =
         new PropertyDescription("coverageDistanceThreshold",
                                 //"Coverage Distance Threshold",
                                 CoverageParamSpaceGenerator.COV_DIST_THRESH,
                                 "This property specifies the percent of the max distance to use " +
                                 "as a cutoff value to forming new samples ([1...100].  The max distance between examples " +
                                 "is approximated by taking the min and max of each attribute and forming a " +
                                 "min example and a max example -- then finding the distance between the two.");
      pds[5] =
         new PropertyDescription("coverageMaxNumSamples",
                                 //"Coverage Max Num Samples",
                                 CoverageParamSpaceGenerator.COV_MAX_NUM_SAMPLES,
                                 "An integer value specifying the maximum number of points to sample.(>0)");
      pds[6] =
         new PropertyDescription("refinementMaxIterations",
                                 //"Number of Assignment Passes",
                                 CoverageParamSpaceGenerator.MAX_ITERATIONS,
                                 "This property specifies the number of iterations of cluster refinement to perform (> 0).");

      return pds;
   } // end method getPropertiesDescriptions


   /**
    * Return a custom gui for setting properties.
    *
    * @return CustomModuleEditor a custom properties editor to allow the user to
    *         set the properties
    */
   public CustomModuleEditor getPropertyEditor() {
      return new CoverageParams_Props(this);
   }

   /**
    * Returns the value of Number of Assignment Passes property.
    *
    * @return int The value of Number of Assignment Passes property
    */
   public int getRefinementMaxIterations() { return _maxIterations; }

   /**
    * Sets the value of the clustering method property.
    *
    * @param cm Clustering method's ID, as defined in<code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setClusterMethod(int cm) { _clusterMethod = cm; }

   /**
    * Sets the value for the coverage distance threshold.
    *
    * @param cdt The value for the coverage distance threshold, should be in the range [1,100]
    */
   public void setCoverageDistanceThreshold(int cdt) { _covthresh = cdt; }

   /**
    * Sets the value of Coverage Max Num Samples property.
    *
    * @param num The value for Coverage Max Num Samples property, should be >0.
    */
   public void setCoverageMaxNumSamples(int num) {
      _coverageMaxNumSamples = num;
   }

   /**
    * Sets hte value for the distance metric property.
    *
    * @param dm The value for the distance metric ID. Must be one of the values
    *           defined by <codE>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }

   /**
    * Sets the value of the distance threshold property.
    *
    * @param hdt The value for the distance threshold property, should be in the range [1,100]
    */
   public void setHacDistanceThreshold(int hdt) { _hacDistanceThreshold = hdt; }


   /**
    * Sets the value for the number of clusters property.
    *
    * @param i The value for the number of clusters property, should be >=2
    */
   public void setNumClusters(int i) { N = i; }

   /**
    * Sets the value of the Number of Assignment Passes property.
    *
    * @param rmi The value for the Number of Assignment Passes property, should be >0.
    */
   public void setRefinementMaxIterations(int rmi) { _maxIterations = rmi; }

} // end class CoverageParams
