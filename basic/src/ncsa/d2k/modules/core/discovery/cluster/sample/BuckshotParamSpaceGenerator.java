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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * <p>Title: BuckshotParamSpaceGenerator</p>
 *
 * <p>Description: Generates the optimization space and initial values for the
 * Buckshot Clustering Algorithm.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class BuckshotParamSpaceGenerator extends AbstractParamSpaceGenerator {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -1611459638298133767L;

   // ==============
   // Data Members
   // ==============


   /** Common name for number of clusters property. */
   static public final String NUM_CLUSTERS = "Number of Clusters";

   /** Common name for random seed property. */
   static public final String SEED = "Random Seed";

   /** Common name for use first property. */
   static public final String USE_FIRST = "Use First";

   /** Common name for clustering method property. */
   static public final String CLUSTER_METHOD = "Cluster Method";

   /** Common name for distance metric property. */
   static public final String DISTANCE_METRIC = "Distance Metric";

   /** Common name for distance threshold property. */
   static public final String DISTANCE_THRESHOLD = "Distance Threshold";


   /** Common name for maximum number of iterations property. */
   static public final String MAX_ITERATIONS = "Max Refinement Iterations";

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults for all properties.
    * These are like factory settings, absolute ranges and definitions that are
    * not mutable.
    *
    * @return the factory settings space for all the properties.
    */
   protected ParameterSpace getDefaultSpace() {
      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names =
      {
         NUM_CLUSTERS, SEED, USE_FIRST, CLUSTER_METHOD, DISTANCE_METRIC,
         DISTANCE_THRESHOLD, MAX_ITERATIONS
      };
      double[] min = { 1, 0, 0, 0, 0, 0, 1 };
      double[] max =
      { Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 6, 2, 100, Integer.MAX_VALUE };
      double[] def = { 5, 0, 0, 0, 0, 0, 5 };
      int[] numRegions = { 10, 10, 1, 6, 2, 100, 100 };
      int[] types =
      {
         ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.BOOLEAN,
         ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER,
         ColumnTypes.INTEGER
      };
      psi.createFromData(names, min, max, def, numRegions, types);

      return psi;
   }


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */

   public String getModuleName() {
      return "Buckshot Clusterer Space Generator";
   }


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
         new PropertyDescription(NUM_CLUSTERS,
                                 "Number of Clusters",
                                 "This property specifies the number of clusters to form (>= 2).");
      pds[1] =
         new PropertyDescription(SEED,
                                 "Seed",
                                 "The seed for the random number generator used to select the random sampling of table rows. If this value is set to the same value for different runs, the results could be exactly the same.");
      pds[2] =
         new PropertyDescription(USE_FIRST,
                                 "Use First",
                                 "If this option is selected, the first entries in the original table will be used as the sample.");
      pds[3] =
         new PropertyDescription(CLUSTER_METHOD,
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
      pds[4] =
         new PropertyDescription(DISTANCE_METRIC,
                                 "Distance Metric",
                                 "This property determine the type of distance function used to calculate " +
                                 "distance between two examples." +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");
      pds[5] =
         new PropertyDescription(DISTANCE_THRESHOLD,
                                 "Distance Threshold",
                                 "This property specifies the percent of the max distance to use " +
                                 "as a cutoff value to halt clustering ([0...100].  The max distance between examples " +
                                 "is approximated by taking the min and max of each attribute and forming a " +
                                 "min example and a max example -- then finding the distance between the two. " +
                                 "This property when set with a value > 0 becomes the dominant halting criteria for " +
                                 "clustering (overriding the <i>Number of Clusters</i> property.");
      pds[6] =
         new PropertyDescription(MAX_ITERATIONS,
                                 "Number of Assignment Passes",
                                 "This property specifies the number of iterations of cluster refinement to perform (> 0).");

      return pds;
   } // end method getPropertiesDescriptions

} // end class BuckshotParamSpaceGenerator
