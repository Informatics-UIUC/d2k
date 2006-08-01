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

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;


/**
 * <p>Title: FractionationParamsOPT</p> <p>Description: Module to input
 * parameters for KMeans</p> * The Coverage clustering algorithm is a type of
 * kmeans approach where a sample set is formed as follows: In the
 * FractionationSampler, the initial examples (converted to clusters) by a key
 * attribute denoted by <i>Sort Attribute</i>. The set of sorted clusters is
 * then segmented into equal partitions of size <i>maxPartitionsize</i> Each of
 * these partitions is then passed through the agglomerative clusterer to
 * produce <i>numberOfClusters</i> clusters. All the clusters are gathered
 * together for all partitions and the entire process is repeated until only
 * <i>Number Of Clusters</i> clusters remain. The sorting step is to encourage
 * like clusters into same partitions. The final cluster's centroids are used as
 * the initial \"means\" for the cluster assignment module. The assignment
 * module, once it has made refinements, outputs the final <i>Cluster Model</i>.
 * This algorithm is comprised of four modules: this module
 * (FractionationParams), the sampler (FractionationSamplerOPT), the clusterer
 * (HierAgglomClustererOPT) and the cluster refiner (ClusterAssignment).
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning
 * Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class FractionationParamsOPT extends DataPrepModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4894239299873310566L;

   //~ Instance fields *********************************************************

   /**
    * The ID of the clustering method to be used. IDs are defined in <codE>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /**
    * The ID of the distance metric to be used. IDs are defined in <codE>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   protected int _distanceMetric = HAC.s_Euclidean_DISTANCE;

   /** The size of partitions to use in the sampling process. */
   protected int _fracPart = 200;

   /**
    * Specifies the percent of the max distance to use as a cutoff value to halt
    * clustering [1...100].
    */
   protected int _hacDistanceThreshold = 0;

   /**
    * Specifies the number of iterations of cluster refinement to perform (>
    * 0).");
    */
   protected int _maxIterations = 5;

   /**
    * The index of for the column denoting the attribute to be used to sort on
    * prior to partitioning.
    */
   protected int _nthSortTerm = 0;


   /** Number of clusters to form and also the number of rows to sample. */
   protected int N = 3;

   //~ Constructors ************************************************************


   /**
    * Creates a new FractionationParamsOPT object.
    */
   public FractionationParamsOPT() { }

   //~ Methods *****************************************************************

   /**
    * Creates 3 parameter point to be used by the different modules of the
    * fraction algorithm.
    *
    * @param tab The input table with the data to be clustered.
    */
   protected void doingit(Table tab) {

      String[] names1 = {
         "clusterMethod", "distanceMetric", "maxIterations"
      };
      double[] values1 =
      {
         (double) this._clusterMethod,
         (double) this._distanceMetric,
         (double) this._maxIterations
      };
      ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

      this.pushOutput(pp, 0);

      String[] names2 =
      {
         "clusterMethod", "distanceMetric", "numClusters", "distanceThreshold"
      };
      double[] values2 =
      {
         (double) this._clusterMethod,
         (double) this._distanceMetric,
         (double) this.N,
         (double) this._hacDistanceThreshold
      };
      pp = ParameterPointImpl.getParameterPoint(names2, values2);

      this.pushOutput(pp, 1);

      String[] names3 = {
         "numClusters", "nthSortTerm", "partitionSize"
      };
      double[] values3 =
      {
         (double) this.N,
         (double) this._nthSortTerm,
         (double) this._fracPart
      };
      pp = ParameterPointImpl.getParameterPoint(names3, values3);

      this.pushOutput(pp, 2);

      this.pushOutput(tab, 3);

   } // end method doingit

   /**
    * Perform the work of the module: Creates 3 parameter points with the input
    * table to be used by the modules of the fractionation algorithm.

    */
   protected void doit() {

      ParameterPoint pp = (ParameterPoint) this.pullInput(0);
      this.N = (int) pp.getValue(0);
      this._clusterMethod = (int) pp.getValue(1);
      this._distanceMetric = (int) pp.getValue(2);
      this._hacDistanceThreshold = (int) pp.getValue(3);
      this._fracPart = (int) pp.getValue(4);
      this._nthSortTerm = (int) pp.getValue(5);
      this._maxIterations = (int) pp.getValue(6);

      Table tab = (Table) this.pullInput(1);
      doingit(tab);

   }


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
         return "Control Parameters";
      } else if (inputIndex == 1) {
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
         return "ParameterPoint";
      } else if (inputIndex == 1) {
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
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
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
         "The Coverage clustering algorithm is a type of kmeans approach where a sample set ";
      s += "is formed as follows: <br>";
      s +=
         "In the FractionationSampler, the initial examples (converted to clusters) by a key attribute denoted by <i>Sort Attribute</i>. ";
      s +=
         "The set of sorted clusters is then segmented into equal partitions of size <i>maxPartitionsize</i>. ";
      s +=
         "Each of these partitions is then passed through the agglomerative clusterer to produce ";
      s +=
         "<i>numberOfClusters</i> clusters.  All the clusters are gathered together for all partitions and the ";
      s +=
         "entire process is repeated until only <i>" + NUM_CLUSTERS +
         "</i> clusters remain. ";
      s +=
         "The sorting step is to encourage like clusters into same partitions. ";
      s +=
         "The final cluster's centroids are used as the initial \"means\" for the cluster assignment module.";
      s +=
         "The assignment module, once it has made refinements, outputs the final <i>Cluster Model</i>. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "This algorithm is comprised of four modules: this module (FractionationParams), the sampler ";
      s +=
         "(FractionationSamplerOPT), the clusterer (HierAgglomClustererOPT) and the cluster refiner ";
      s += "(ClusterAssignment).";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The input table is not modified by this algorithm, however it is include as part of ";
      s += "the <i>Cluster Model</i> that is created.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This module time complexity of O(<i>Number of Examples</i> * <i>Partition Size</i>). ";
      s +=
         "Each iteration creates <i>Number of Examples</i> <i>Table Cluster</i> objects.";
      s += "</p>";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */

   public String getModuleName() { return "Fractionation Parameters"; }

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
         return "Parameters for Cluster Assignment";
      } else if (outputIndex == 1) {
         return "Parameters for Hier. Agglom. Clusterer";
      } else if (outputIndex == 2) {
         return "Parameters for Fractionation Sampler";
      } else if (outputIndex == 3) {
         return "Table of examples to cluster";
      } else {
         return "";
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
         return "Parameters for Cluster Assignment";
      } else if (outputIndex == 1) {
         return "Parameters for Hier. Agglom. Clusterer";
      } else if (outputIndex == 2) {
         return "Parameters for Fractionation Sampler";
      } else if (outputIndex == 3) {
         return "Table";
      } else {
         return "";
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
      String[] out =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return out;
   }
} // end class FractionationParamsOPT
