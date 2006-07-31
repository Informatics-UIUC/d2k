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
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;


/**
 * <p>Title: KMeansParamsOPT</p>
 *
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
public class KMeansParamsOPT extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -477740609019616187L;

   //~ Instance fields *********************************************************

   /**
    * The clustering method ID. IDs are defined in <codE>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /**
    * The ID of the distance metric to be used. IDs are defined in <codE>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   protected int _distanceMetric = HAC.s_Euclidean_DISTANCE;

   /**
    * Specifies the number of iterations of cluster refinement to perform (>
    * 0).");
    */
   protected int _maxIterations = 5;


   /** Number of Clusters to be formed, also the number of rows to sample. */
   protected int N = 5;

   /** The seed for the random number generator. */
   protected int seed = 0;

   /**
    * True if the first N rows should be the sample, false if the sample should
    * be random rows from the table.
    */
   protected boolean useFirst = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new KMeansParamsOPT object.
    */
   public KMeansParamsOPT() { }

   //~ Methods *****************************************************************

   /**
    * Creates 2 ParameterPoint objects and outputs them.
    */
   protected void doingit() {
      String[] names1 = {
         "clusterMethod", "distanceMetric", "maxIterations"
      };
      double[] values1 =
      {
         (double) this._clusterMethod,
         (double) this._distanceMetric,
         this._maxIterations
      };
      ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

      this.pushOutput(pp, 0);

      double uf = 0;

      if (this.useFirst) {
         uf = 1;
      }

      String[] names2 = {
         "sampleSize", "seed", "useFirst"
      };
      double[] values2 = {
         (double) this.N,
         (double) this.seed, uf
      };
      pp = ParameterPointImpl.getParameterPoint(names2, values2);

      this.pushOutput(pp, 1);
   } // end method doingit

   /**
    * Perform the work of the module: According to the input ParameterPoint
    * object updates its properties and creates new ParameterPoints to be used
    * by other clustering modules.
    */
   protected void doit() {

      ParameterPoint pp = (ParameterPoint) this.pullInput(0);
      this.N = (int) pp.getValue(0);
      this.seed = (int) pp.getValue(1);
      this.useFirst = (((int) pp.getValue(2)) == 1) ? true : false;
      this._clusterMethod = (int) pp.getValue(3);
      this._distanceMetric = (int) pp.getValue(4);
      this._maxIterations = (int) pp.getValue(5);

      doingit();
      this.pushOutput(this.pullInput(1), 2);

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
         return "Control parameters, available as a Parameter Point.";
      } else if (inputIndex == 1) {
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
         return "Parameter Point";
      } else if (inputIndex == 1) {
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
         "This module is used to set control parameters for the KMeans clustering algorithm. ";
      s +=
         "The complete KMeans algorithm is implemented by this module and two others. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "The KMeans clustering algorithm is an approach where a sample set containing ";
      s +=
         "<i>Number of Clusters</i> rows is chosen from an input table of examples and ";
      s += "used as initial cluster centers. ";
      s +=
         "These initial clusters undergo a series of assignment/refinement iterations, ";
      s += "resulting in a final cluster model. ";
      s += "</p>";

      s += "<p>";
      s +=
         "If an <i>Example Table</i> is passed to this module, the algorithm will assign the examples (rows) ";
      s +=
         "to clusters based on the values of the input attributes (columns) . If no input attributes have been ";
      s +=
         "specified, the algorithm has no values to cluster on, and a single cluster will be formed. ";
      s +=
         "The module <i>Choose Attributes</i> is typically used to form an <i>Example Table</i>. ";
      s +=
         "In contrast, if a <i>Table</i> is passed to this module, the algorithm will consider all attributes ";
      s += "when forming the clusters. ";
      s += "</p>";

      s += "<p>";
      s += "The KMeans algorithm implementation is comprised of three modules. ";
      s += "This module is used to set control parameters for the algorithm. ";
      s +=
         "A second module, <i>Sample Table Rows</i>, builds the sample set from the input <i>Table</i>. ";
      s +=
         "The third module, <i>Cluster Assignment</i>, refines the initial clusters in a series of assignment passes. ";
      s +=
         "The control parameters set in this module are passed as <i>Parameter Point</i>s to the ";
      s += "the other two modules, and determine their exact behavior. ";
      s +=
         "The <i>OPT</i>, optimizable, versions of the <i>Sample Table Rows</i> and <i>Cluster Assignment</i> ";
      s +=
         "modules must be used, as they can accept the <i>Parameter Point</i> inputs. ";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s +=
         "The KMeans algorithm does not work if the input data being clustered contains missing values.  If ";
      s += "missing values are detected an exception will be raised. ";
      s +=
         "The KMeans algorithm operates on numeric and boolean data types.  If the data to be clustered ";
      s +=
         "contains nominal data types, it should be converted prior to performing the KMeans clustering. ";
      s +=
         "The <i>Scalarize Nominals</i> module can be used to convert nominal types into boolean values. ";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The input table is not modified by this algorithm. However, it is passed on to the other modules via ";
      s += "the <i>Table</i> output port, ";
      s += "and included as part of the <i>Cluster Model</i> that is created.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This algorithm runs in time O(number of examples).  See the information for the component modules ";
      s += "to understand the overall memory requirements.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "KMeans Parameters"; }


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
         return "Parameters for Cluster Assignment module, available as a Parameter Point.";
      } else if (outputIndex == 1) {
         return "Parameters for Sample Table Rows module, available as a Parameter Point.";
      } else if (outputIndex == 2) {
         return "Table of examples to cluster, unchanged by module.";
      } else {
         return "No such output.";
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
         return "Parameters for Sample Table Rows";
      } else if (outputIndex == 2) {
         return "Table";
      } else {
         return "No such output";
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
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return out;
   }
} // end class KMeansParamsOPT
