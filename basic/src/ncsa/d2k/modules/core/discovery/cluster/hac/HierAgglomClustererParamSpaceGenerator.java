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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterParameterDefns;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;


/**
 * <p>Title: HierAgglomClustererParamSpaceGenerator</p>
 *
 * <p>Description: Generates the optimization space and initial values for the
 * HAC learning module.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class HierAgglomClustererParamSpaceGenerator
   extends AbstractParamSpaceGenerator implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -5880343555293763497L;

   //~ Methods *****************************************************************

   /**
    * Returns a reference to the developer supplied defaults. These are like
    * factory settings, absolute ranges and definitions that are not mutable.
    *
    * @return The factory settings space.
    */
   protected ParameterSpace getDefaultSpace() {
      ParameterSpace psi = new ParameterSpaceImpl();
      String[] names =
      {
         CLUSTER_METHOD,
         DISTANCE_METRIC,
         NUM_CLUSTERS,
         DISTANCE_THRESHOLD
      };
      double[] min = { 0, 0, 1, 0 };
      double[] max = { 6, 2, Integer.MAX_VALUE, 100 };
      double[] def = { 0, 0, 5, 0 };
      int[] numRegions = { 6, 2, 20, 100 };
      int[] types =
      {
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
   public String getModuleName() { return "HAC Param Space Generator"; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // presentation order (dictated by dialog layout): 0-CLUSTER_METHOD,
      // 1-DISTANCE_METRIC, 2-NUM_CLUSTERS, 3-DISTANCE_THRESHOLD Following code
      // is (mostly) cut/paste across modules and indices of pds are adjusted
      // for the appropriate order for the dialog that is used in this module.
      // The first variable in each Property Description isn't used in this
      // case; just the 2nd and 3rd for dialog label and help.

      PropertyDescription[] pds = new PropertyDescription[4];
      pds[0] =
         new PropertyDescription(CLUSTER_METHOD,
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

      pds[1] =
         new PropertyDescription(DISTANCE_METRIC,
                                 DISTANCE_METRIC,
                                 "This property determines the type of distance function to use in calculating the " +
                                 "distance between two points in the space of example values. " +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

      pds[2] =
         new PropertyDescription(NUM_CLUSTERS,
                                 NUM_CLUSTERS,
                                 "This property specifies the number of clusters to form. It must be greater than 1. " +
                                 "If <i>" +
                                 DISTANCE_THRESHOLD +
                                 "</i> is greater than 0, that will be used to halt cluster agglomeration and " +
                                 "determine the number of clusters formed, " +
                                 "independent of this property's setting.  ");
      pds[3] =
         new PropertyDescription(DISTANCE_THRESHOLD,
                                 DISTANCE_THRESHOLD,
                                 "This property specifies the percentage of the <i>maximum distance</i> to use " +
                                 "as a cutoff value to halt cluster agglomeration.  " +
                                 "When the distance between the two clusters that are closest exceeds the cutoff value, " +
                                 "cluster agglomeration stops, " +
                                 "independent of the value of the <i>" +
                                 NUM_CLUSTERS +
                                 "</i> property.  Lower values for the <i>" +
                                 DISTANCE_THRESHOLD +
                                 "</i> result in more clusters.   " +
                                 "If the value is 0 (the default), then no cutoff occurs and cluster " +
                                 "agglomeration continues until <i>" +
                                 NUM_CLUSTERS +
                                 "</i> remain. " +
                                 "See the <i>Hier. Agglom. Clusterer</i> module information for further details on " +
                                 "this is property. ");

      return pds;
   } // end method getPropertiesDescriptions

} // end class HierAgglomClustererParamSpaceGenerator
