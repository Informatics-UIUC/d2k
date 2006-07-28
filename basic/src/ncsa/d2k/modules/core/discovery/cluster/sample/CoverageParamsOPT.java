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
 * <p>Title: CoverageParamsOPT</p>
 *
 * <p>Overview: The Coverage clustering algorithm is a type of kmeans approach
 * where a sample set is chosen from the input table such that the set formed is
 * approximately the minimum number of samples needed such that for every
 * example in the input table there is at least one example in the sample set of
 * distance &lt;= <i>Distance Threshold (% of Maximum)</i>. This sampling is
 * sent through the hierarchical agglomerative clustering module to form <i>
 * Number of Clusters</i> clusters. These clusters' centroids are used as the
 * initial "means" for the cluster assignment module. The assignment module,
 * once it has made refinements, outputs the final <i>Cluster Model</i>.</p>
 *
 * <p>Detailed Description: This algorithm is comprised of four modules: this
 * module (CoverageParams), the sampler (CoverageSamplerOPT), the clusterer
 * (HierAgglomClustererOPT) and the cluster refiner (ClusterAssignment)</p>
 *
 * <p>Data Handling: The input table is not modified by this algorithm, however
 * it is include as part of the <i>Cluster Model</i> that is created.</p>
 *
 * <p>Scalability: This algorithm runs in time O(num_examples^2) worst case but
 * in most cases it runs in O(<i>Sample Size</i> * <i>Number of Clusters</i>).
 * See the component modules information to understand the memory requirements
 * overall.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class CoverageParamsOPT extends DataPrepModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 3809058994722709142L;

   //~ Instance fields *********************************************************

   /** The method to use for determining the distance between two clusters. */
   protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /**
    * An integer value specifying the maximum number of points to sample.(>0).
    */
   protected int _coverageMaxNumSamples = 500;

   /**
    * Specifies the percent of the max distance to use as a cutoff value to
    * forming new samples [1...100].
    */
   protected int _covthresh = 25;

   /**
    * Determine the type of distance function used to calculate distance between
    * two examples.
    */
   protected int _distanceMetric = HAC.s_Euclidean_DISTANCE;

   /**
    * Specifies the percent of the max distance to use as a cutoff value to halt
    * clustering [1...100].
    */
   protected int _hacDistanceThreshold = 0;

   /** maximum number of iteration to perform. */
   protected int _maxIterations = 5;


   /**
    * The number of rows to sample, also the number of clusters to be formed.
    */
   protected int N = 3;

   //~ Constructors ************************************************************


   /**
    * Creates a new CoverageParamsOPT object.
    */
   public CoverageParamsOPT() { }

   //~ Methods *****************************************************************

   /**
    * Creates <code>
    * ncsa.d2k.modules.core.datatype.parameter.ParameterPoint</code> object for
    * <code>tab</codE> according to the values of the properties.
    *
    * @param tab The data to be clustered according to the values of the output
    *            <code>
    *            ncsa.d2k.modules.core.datatype.parameter.ParameterPoint</code>
    *            objects.
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
         "maxNumCenters", "cutOff", "distanceMetric"
      };
      double[] values3 =
      {
         (double) this._coverageMaxNumSamples,
         (double) this._covthresh,
         (double) this._distanceMetric
      };
      pp = ParameterPointImpl.getParameterPoint(names3, values3);

      this.pushOutput(pp, 2);

      this.pushOutput(tab, 3);

   } // end method doingit


   /**
    * Performs the main work of the module: Creates 4 different parameter points
    * using the properties values, as set by the user.
    */
   protected void doit() {

      ParameterPoint pp = (ParameterPoint) this.pullInput(0);
      this.N = (int) pp.getValue(0);
      this._clusterMethod = (int) pp.getValue(1);
      this._distanceMetric = (int) pp.getValue(2);
      this._hacDistanceThreshold = (int) pp.getValue(3);
      this._covthresh = (int) pp.getValue(4);
      this._coverageMaxNumSamples = (int) pp.getValue(5);
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
      s +=
         "is chosen from the input table such that the set formed is approximately the minimum number of samples ";
      s +=
         "needed such that for every example in the input table there is at least one example in the sample set of distance ";
      s +=
         "<= <i>" + DISTANCE_THRESHOLD +
         "</i>. This sampling is sent through the hierarchical agglomerative clustering ";
      s +=
         "module to form <i>" + NUM_CLUSTERS +
         "</i> clusters.  These clusters' centroids are used as the initial ";
      s += "\"means\" for the cluster assignment module. ";
      s +=
         "The assignment module, once it has made refinements, outputs the final <i>Cluster Model</i>. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "This algorithm is comprised of four modules: this module (CoverageParams), the sampler ";
      s +=
         "(CoverageSamplerOPT), the clusterer (HierAgglomClustererOPT) and the cluster refiner ";
      s += "(ClusterAssignment).";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The input table is not modified by this algorithm, however it is include as part of ";
      s += "the <i>Cluster Model</i> that is created.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This algorithm runs in time O(num_examples^2) worst case but in most cases it runs in O(<i>Sample Size</i> * <i>" +
         NUM_CLUSTERS + "</i>). ";
      s +=
         "See the component modules information to understand the memory requirements overall.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Coverage Parameters"; }


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
         return "Parameters for Coverage Sampler";
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
         return "Parameters for Coverage Sampler";
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
} // end class CoverageParamsOPT
