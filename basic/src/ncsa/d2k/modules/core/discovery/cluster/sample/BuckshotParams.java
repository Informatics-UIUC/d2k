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


// ==============
// Java Imports
// ==============

// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .BuckshotParams_Props;


/**
 * <p>Title: BuckshotParams</p>
 *
 * <p>Description: @see
 * ncsa.d2k.modules.core.discovery.cluster.sample.BuckshotParamsOPT</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class BuckshotParams extends BuckshotParamsOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4513356307434289442L;

   //~ Constructors ************************************************************

   /**
    * ============== Data Members ============== ================ Constructor(s)
    * ================.
    */
   public BuckshotParams() { }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module. *
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   protected void doit() throws Exception {

      Table tab = (Table) this.pullInput(0);
      doingit(tab);

   }


   /**
    * Returns the value of the clustering method ID property.
    *
    * @return int The value of the clustering method ID property
    */
   public int getClusterMethod() { return _clusterMethod; }


   /**
    * Returns the value of the distance metric ID property.
    *
    * @return int The value of the distance metric ID property
    */
   public int getDistanceMetric() { return _distanceMetric; }


   /**
    * Returns the value of distance threshold property.
    *
    * @return int The value of distance threshold property.
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
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }


   /**
    * Returns the value of the maximum iterations property.
    *
    * @return int The value of the maximum iterations property
    */
   public int getMaxIterations() { return _maxIterations; }


   /**
    * Returns the value of the number of clusters to form property.
    *
    * @return int The value of the number of clusters to form property.
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
         new PropertyDescription("numberOfClusters",
                                 "Number of Clusters",
                                 "This property specifies the number of " +
                                 "clusters to form (>= 2).");
      pds[1] =
         new PropertyDescription("seed",
                                 "Seed",
                                 "The seed for the random number generator " +
                                 "used to select the random sampling of table "
                                 + "rows. If this value is set to the same " +
                                 "value for different runs, the results be " +
                                 "the exact same.");
      pds[2] =
         new PropertyDescription("useFirst",
                                 "Use First",
                                 "If this option is selected, the first entries"
                                + " in the original table will be used as the "
                                + "sample.");
      pds[3] =
         new PropertyDescription("clusterMethod",
                                 "Clustering Method",
                                 "The method to use for determining the " +
                                 "distance between two clusters. " +
                                 "<p>WARDS METHOD: Use a minimum variance " +
                                 "approach that sums the squared error " +
                                 "(distance) for every point in the cluster " +
                                 "to the cluster centroid.</p>" +
                                 "<p>SINGLE LINK: Distance of closest pair " +
                                 "(one from each cluster).</p>" +
                                 "<p>COMPLETE LINK: Distance of furthest pair "
                                 +"(one from each cluster).</p>" +
                                 "<p>UPGMA: Unweighted pair group method using "
                                 + " arithmetic averages.</p>" +
                                 "<p>WPGMA: Weighted pair group method using "
                                 + "arithmetic averages.</p>" +
                                 "<p>UPGMC: Unweighted pair group method using "
                                 + "centroids.</p>" +
                                 "<p>WPGMC: Weighted pair group method using "
                                 + "centroids.</p>");
      pds[4] =
         new PropertyDescription("distanceMetric",
                                 "Distance Metric",
                                 "This property determines the type of distance"
                                + " function used to calculate " +
                                 "distance between two examples." +
                                 "<p>EUCLIDEAN: \"Straight\" line distance "
                                 + "between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points "
                                 + "measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle "
                                 + "between the norms of the vectors denoted "
                                 + "by two points.</p>");
      pds[5] =
         new PropertyDescription("distanceThreshold",
                                 "Distance Threshold",
                                 "This property specifies the percent of the "
                                 + "max distance to use " +
                                 "as a cutoff value to halt clustering ([1..."
                                 +"100].  The max distance between examples " +
                                 "is approximated by taking the min and max of "
                                 + " each attribute and forming a " +
                                 "min example and a max example -- then finding"
                                + " the distance between the two. " +
                                 "For this algorithm, the number of clusters "
                                 + "value is still used to generate the " +
                                 "initial sample size, even if auto is "
                                 + "selected.");
      pds[6] =
         new PropertyDescription("maxIterations",
                                 "Number of Assignment Passes",
                                 "This property specifies the number of "
                                 + "iterations of cluster refinement to "
                                 + "perform (> 0).");

      return pds;
   } // end method getPropertiesDescriptions

   // ========================
   // D2K Abstract Overrides

   /**
    * Return a custom properties editor for setting the properties of this
    * module.
    *
    * @return CustomModuleEditor a custom properties editor (GUI component), for
    *         this module.
    */
   public CustomModuleEditor getPropertyEditor() {
      return new BuckshotParams_Props(this);
   }


   /**
    * Returns the value of the seed property.
    *
    * @return int The value of the seed property
    */
   public int getSeed() { return seed; }


   /**
    * Returns the value of the useFirst property.
    *
    * @return boolean The value of the useFirst property.
    */
   public boolean getUseFirst() { return useFirst; }

   /**
    * Sets the value of the clustering method ID property.
    *
    * @param cm int The value of the clustering method ID. Must be one of the
    *           values defined in <code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setClusterMethod(int cm) { _clusterMethod = cm; }


   /**
    * Sets the value of the distance metric ID property.
    *
    * @param dm int The value of the distance metric ID. Must be one of the
    *           values defined in <code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }


   /**
    * Sets the value of distance threshold property.
    *
    * @param dist The value for the distance threshold property. Should be in
    *             the range of [1,100]
    */
   public void setDistanceThreshold(int dist) { _thresh = dist; }


   /**
    * Sets the value of the maximum iterations property.
    *
    * @param mi The value for the maximum iterations property. Should be greater
    *           than zero.
    */
   public void setMaxIterations(int mi) { _maxIterations = mi; }

   // ============
   // Properties
   // ============

   /**
    * Sets the number of clusters property.
    *
    * @param i int Number of clusters to be formed, should be greater than 1.
    */
   public void setNumClusters(int i) { N = i; }

   /**
    * Sets the seed property.
    *
    * @param i int The new value for the seed property
    */

   public void setSeed(int i) { seed = i; }


   /**
    * Sets the use first property (true if rows should be sampled sequentially
    * from the beginning of the Table (first N rows) false if N rows should be
    * sampled randomly.
    *
    * @param b boolean The value for the use first property.
    */
   public void setUseFirst(boolean b) { useFirst = b; }

} // end class BuckshotParams
