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

// ==============
// Java Imports
// ==============
// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.OrderedReentrantModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterParameterDefns;


/**
 * <p>Title:</p>
 *
 * <p>Description: HierAgglomClustererOPT performs a bottom-up, hierarchical
 * clustering of the examples in the input table.</p>
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

public class HierAgglomClustererOPT extends OrderedReentrantModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 5888203421896799347L;

   //~ Instance fields *********************************************************

   /**
    * Check missing values flag. If set to true, this module shall handle
    * missing values in the input table in a special way.
    */
   protected boolean _mvCheck = true;

   /**
    * Flag for verbose mode - if true then this module outputs verbose info to
    * stdout.
    */
   protected boolean _verbose = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new HierAgglomClustererOPT object.
    */
   public HierAgglomClustererOPT() { }

   //~ Methods *****************************************************************

   /**
    * Performs a bottom-up, hierarchical clustering of the examples in the input
    * table.
    *
    * @throws Exception If a problem occurs while performing the work of the
    *                   module.
    */
   protected void doit() throws Exception {
      ParameterPoint pp = (ParameterPoint) pullInput(0);
      HAC hac =
         new HAC((int) pp.getValue(0), (int) pp.getValue(1),
                 (int) pp.getValue(2), (int) pp.getValue(3), getVerbose(),
                 this.getCheckMissingValues(), getAlias());
      this.pushOutput(hac.buildModel((Table) this.pullInput(1)), 0);
   }

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */

   public void beginExecution() {

      if (getVerbose()) {
         System.out.println(getAlias() + ": Beginning execution. ");
      }
   }


   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    */

   public void endExecution() { super.endExecution(); }

   /**
    * Return the value if the check missing values flag.
    *
    * @return true if this module was set to handle missing value in a different
    *         way than regular ones
    */
   public boolean getCheckMissingValues() { return _mvCheck; }

   /**
    * Return the information of input indexed parm1.
    *
    * @param  parm1 The index of the input
    *
    * @return The information of input indexed parm1
    */

   public String getInputInfo(int parm1) {

      if (parm1 == 0) {
         return "Control parameters, available as a Parameter Point.";
      } else if (parm1 == 1) {
         return "Table of entities to cluster.";
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
         "This module performs a bottom-up, hierarchical clustering of " +
         " the examples in the input ";
      s += "table.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s += "There are two versions of this module. ";
      s += "The <i>OPT</i>, optimizable, version uses control ";
      s +=
         "parameters encapsulated in a <i>Parameter Point</i> to direct " +
         " the clustering behavior. ";
      s += "The control parameters specify a <i>";
      s += CLUSTER_METHOD;
      s += "</i>, a <i>";
      s += NUM_CLUSTERS;
      s += "</i>, a <i>";
      s += DISTANCE_THRESHOLD;
      s += "</i>, and a <i>";
      s += DISTANCE_METRIC;
      s +=
         "</i>.  These parameters are set as properties in the non-OPT " +
         " version of the module. ";
      s += "</p>";

      s +=
         "<p>The Hierarchical Agglomerative Clustering (HAC) algorithm, " +
         "a bottom-up strategy, ";
      s +=
         "is run on the examples in the input <i>Table</i> to build the " +
         "cluster tree. ";
      s +=
         "The cluster tree is stored in a newly formed model, <i>Cluster " +
         "Model</i>, along with the initial table ";
      s += "of examples and the set of clusters formed.";
      s += "</p>";

      s += "<p>If the <i>";
      s += DISTANCE_THRESHOLD;
      s += "</i> is being used (non-zero in OPT case, controlled by property "
              +" in non-OPT case), ";
      s +=
         "then it determines a <i>distance cutoff</i> value that can halt "
         + "cluster agglomeration.  ";
      s += "The <i> ";
      s += DISTANCE_THRESHOLD;
      s += "</i> represents a certain percentage of the approximate ";
      s += "<i>maximum distance</i> of all example pairs.  ";
      s +=
         "The <i>maximum distance</i> between examples is approximated by " +
         "taking ";
      s += "the minimum and maximum of each attribute and forming a ";
      s += "minimum example and a maximum example.  ";
      s +=
         "The <i>maximum distance</i> is defined to be the distance " +
         "between these ";
      s +=
         "two constructed examples. The specified percentage of this " +
         "maximum distance ";
      s += "is the <i>distance cutoff</i> value. ";
      s +=
         "When the next two most similar clusters have distance greater " +
         "than the <i>distance cutoff</i> value, ";
      s += "the cluster agglomeration is stopped. ";
      s += "</p>";

      s += "<p>If the <i>";
      s += DISTANCE_THRESHOLD;
      s +=
         "</i> is NOT being used, then clustering is halted when the " +
         "number of clusters is equal to <i>";
      s += NUM_CLUSTERS;
      s += "</i>. ";
      s += "</p>";

      s +=
         "<p>In actuality, clustering continues all the way to the root " +
         "of the cluster tree regardless of the halting ";
      s +=
         "criteria.  This allows the complete cluster tree to be placed " +
         "in the <i>Cluster Model</i>. ";
      s +=
         "The clusters at the cutoff, also known as the \"cut\",  are " +
         "saved to the model separately. ";
      s += "</p>";

      s += "<p>References: ";
      s +=
         "A discussion of the Hierarchical Agglomerative Clustering can " +
         "be found in the book ";
      s +=
         "<i>Algorithms for Clustering Data</i>, A.K. Jain and R. C. " +
         "Dubes, Prentice Hall, 1988. ";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s +=
         "The clustering does not work if the input data contains missing" +
         " values. ";
      s +=
         "The algorithm operates on numeric and boolean data types.  " +
         "If the data to be clustered ";
      s +=
         "contains nominal data types, it should be converted prior to " +
         "performing the clustering. ";
      s +=
         "The <i>Scalarize Nominals</i> module can be used to convert " +
         "nominal types into boolean values. ";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The input <i>Table</i> is included in the <i>Cluster Model</i>.  "
         +" It is not ";
      s += "changed by this module.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "The algorithm is quadratic in time complexity and therefore will" +
         " run impossibly ";
      s +=
         "long for large numbers of examples.  It is recommended that one" +
         " of the sampling ";
      s += "cluster methods (i.e. KMeans) be used for very large datasets. ";
      s += "</p>";

      s += "<p>";
      s += "Two arrays of <i>Number of Examples</i> squared doubles ";
      s +=
         "are created. Sufficient heap space is required to accommodate " +
         "these arrays. ";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */

   public String getModuleName() { return "Hier. Agglom. Clusterer"; }

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
         return "Cluster Model";
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
         return "Cluster Model";
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
      String[] out = { "ncsa.d2k.modules.core.discovery.cluster.ClusterModel" };

      return out;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[2];

      descriptions[0] =
         new PropertyDescription("checkMissingValues",
                                 CHECK_MV,
                                 "If this property is true, the module will "
                                 + " perform a check for" +
                                 " missing values in the input table. ");

      descriptions[1] =
         new PropertyDescription("verbose", VERBOSE,
                                 "If this property is true, the module will "
                                 + " write verbose " +
                                 " status information to the console.");

      return descriptions;
   }

   /**
    * Return the value of the verbose flag.
    *
    * @return true if set to work in verbose mode.
    */
   public boolean getVerbose() { return _verbose; }

   /**
    * Sets the check missing values flag.
    *
    * @param b If true then this module treats the missing values in the input
    *          table in a special way. Otherwise treats missing values as if
    *          they were regular ones.
    */
   public void setCheckMissingValues(boolean b) { _mvCheck = b; }

   /**
    * Sets the verbose mode flag.
    *
    * @param b If true then this module outputs verbose info to stdout.
    */
   public void setVerbose(boolean b) { _verbose = b; }

} // end class HierAgglomClustererOPT
