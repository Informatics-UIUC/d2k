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

/**
 * <p>Title: ClusterParameterDefns</p>
 *
 * <p>Description: A common place to define the parameter names used by various
 * clustering modules. Using these Strings in the Property Descriptions and
 * dialogs helps keep things consistent.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith & R. Aydt
 * @version 1.0
 */

public interface ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Common name for the clustering method property. */
   static public final String CLUSTER_METHOD = "Clustering Method";

   /** Common name for the distance metric property. */
   static public final String DISTANCE_METRIC = "Distance Metric";

   /** Common name for the threshold property. */
   static public final String DISTANCE_THRESHOLD =
      "Distance Threshold (% of Maximum)";

   /** Common name for the covering distance threshold percentage property. */
   static public final String COV_DISTANCE_THRESHOLD =
      "Coverage Distance Threshold %";

   /** Common name for the number of clusters property. */
   static public final String NUM_CLUSTERS = "Number of Clusters";

   /** Common name for the random seed property. */
   static public final String SEED = "Random Seed";

   /** Common name for the use first property. */
   static public final String USE_FIRST = "Use First Rows";

   /** Common name for the number of iterations property. */
   static public final String MAX_ITERATIONS = "Maximum Number of Iterations";

   /** Common name for the verbose property. */
   static public final String VERBOSE = "Generate Verbose Output";

   /** Common name for the missing value check property. */
   static public final String CHECK_MV = "Check for Missing Values";

   /** Common name for the suto clustering property. */
   static public final String AUTO_CLUSTER =
      "Use Distance Threshold to Determine Number of Clusters";

} // end interface ClusterParameterDefns
