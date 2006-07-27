package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: BuckshotParamsOPT</p>
 * <p>Description: Module to input parameters for Buckshot</p>
 *
 * <P>Overview:<BR>
The Buckshot clustering algorithm is a type of kmeans approach where a sample
of size Sqrt(<i>Number of Clusters</i> * <i>Number of Examples</i>) is chosen at random from the table
of examples.  This sampling is sent through the hierarchical agglomerative clustering
module to form <i>Number of Clusters</i> clusters.  These clusters' centroids are used as the initial
"means" for the cluster assignment module.
    The assignment module, once it has made refinements, outputs the final cluster model.
 </p>

<p>Detailed Description:
This algorithm is comprised of four modules: this module (BuckshotParams), the sampler
(SampleTableRowsOPT), the clusterer (HierAgglomClustererOPT) and the cluster refiner
(ClusterAssignment).
</p>

<p>Data Handling:
The input table is not modified by this algorithm, however it is include as part of
the ClusterModel that is created.
</p>

    <p>Scalability:
This algorithm runs in time O(<i>Number of Examples</i> * <i>Number of Clusters</i>).  See the component modules
information to understand the memory requirements overall.

 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class BuckshotParamsOPT
    extends DataPrepModule implements ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public BuckshotParamsOPT() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  protected int N = 3;

  /** the seed for the random number generator */
  protected int seed = 0;

  /** true if the first N rows should be the sample, false if the sample
          should be random rows from the table */
  protected boolean useFirst = false;

  /**
   * A clustering method ID, as defined in <code>ncsa.d2k.modules.core.discovery.cluster.hac.HAC<code>
   */
  protected int _clusterMethod = HAC.s_WardsMethod_CLUSTER;


  /**
   * A distanc emetric ID, as defined in <code>ncsa.d2k.modules.core.discovery.cluster.hac.HAC<code>
   */
  protected int _distanceMetric = HAC.s_Euclidean_DISTANCE;

  protected int _maxIterations = 5;

  protected int _thresh = 0;

  //========================
  // D2K Abstract Overrides

  /**
          * Returns the name of the module that is appropriate for end user consumption.
          *
          * @return The name of the module.
          */

  public String getModuleName() {
    return "Buckshot Parameters";
  }


  /**
   * Returns a description of the input at the specified index.
   *
   * @param inputIndex Index of the input for which a description should be returned.
   *
   * @return <code>String</code> describing the input at the specified index.
   */
  public String getInputInfo(int inputIndex) {

    if (inputIndex == 0) {
      return "Control Parameters";
    } else if (inputIndex == 1) {
      return "Table of examples to cluster";
    } else {
      return "";
    }
  }

  /**
         * Returns the name of the input at the specified index.
         *
         * @param inputIndex Index of the input for which a name should be returned.
         *
         * @return <code>String</code> containing the name of the input at the specified index.
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
   * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
   *
   * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
   */
 public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
         * Describes the purpose of the module.
         *
         * @return <code>String</code> describing the purpose of the module.
         */

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "The Buckshot clustering algorithm is a type of kmeans approach where a sample ";
    s += "of size Sqrt(<i>" + NUM_CLUSTERS + "</i> * <i>Number of Examples</i>) is chosen at random from the table ";
    s += "of examples.  This sampling is sent through the hierarchical agglomerative clustering ";
    s += "module to form <i>" + NUM_CLUSTERS + "</i> clusters.  These clusters' centroids are used as the initial ";
    s += "\"means\" for the cluster assignment module. ";
    s += "The assignment module, once it has made refinements, outputs the final cluster model. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This algorithm is comprised of four modules: this module (BuckshotParams), the sampler ";
    s += "(SampleTableRowsOPT), the clusterer (HierAgglomClustererOPT) and the cluster refiner ";
    s += "(ClusterAssignment).";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input table is not modified by this algorithm, however it is include as part of ";
    s += "the ClusterModel that is created.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This algorithm runs in time O(<i>Number of Examples</i> * <i>" + NUM_CLUSTERS + "</i>).  See the component modules ";
    s += "information to understand the memory requirements overall.";
    s += "</p>";
    return s;
  }

  /**
         * Returns a description of the output at the specified index.
         *
         * @param outputIndex Index of the output for which a description should be returned.
         *
         * @return <code>String</code> describing the output at the specified index.
         */
        public String getOutputInfo(int outputIndex) {


    if (outputIndex == 0) {
      return "Parameters for Cluster Assignment";
    } else if (outputIndex == 1) {
      return "Parameters for Hier. Agglom. Clusterer";
    } else if (outputIndex == 2) {
      return "Parameters for Sample Table Rows";
    } else if (outputIndex == 3) {
      return "Table of examples to cluster";
    } else {
      return "";
    }
  }

  /**
          * Returns the name of the output at the specified index.
          *
          * @param outputIndex Index of the output for which a description should be returned.
          *
          * @return <code>String</code> containing the name of the output at the specified index.
          */
         public String getOutputName(int outputIndex) {

    if (outputIndex == 0) {
      return "Parameters for Cluster Assignment";
    } else if (outputIndex == 1) {
      return "Parameters for Hier. Agglom. Clusterer";
    } else if (outputIndex == 2) {
      return "Parameters for Sample Table Rows";
    } else if (outputIndex == 3) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
   * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
   *
   * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
   */
public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
         * Performs the main work of the module: Create <cdoe>
         * ncsa.d2k.modules.core.datatype.parameter.ParameterPoint</code>
         * objects based on the input ParameterPoint and the input Table.	 *
         * @throws Exception if a problem occurs while performing the work of the module.
         */

  protected void doit() throws Exception {

    ParameterPoint pp = (ParameterPoint)this.pullInput(0);
    this.N = (int) pp.getValue(0);
    this.seed = (int) pp.getValue(1);
    this.useFirst = ( ( (int) pp.getValue(2)) == 1) ? true : false;
    this._clusterMethod = (int) pp.getValue(3);
    this._distanceMetric = (int) pp.getValue(4);
    this._thresh = (int) pp.getValue(5);
    this._maxIterations = (int) pp.getValue(6);

    Table tab = (Table)this.pullInput(1);
    doingit(tab);
  }


  /**
   * Creates <code>ncsa.d2k.modules.core.datatype.parameter.ParameterPoint
   * </code> objects according to the values of the properties,
   * and outputs these objects. Also outputs the <codE>tab</code>
   * @param tab The input Table of this module.
   */
  protected void doingit(Table tab) {

    String[] names1 = {
        "clusterMethod", "distanceMetric", "maxIterations"};
    double[] values1 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric,
        (double)this._maxIterations};
    ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

    this.pushOutput(pp, 0);

    String[] names2 = {
        "clusterMethod", "distanceMetric", "numClusters", "distanceThreshold"};
    double[] values2 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric, (double)this.N,
        (double)this._thresh};
    pp = ParameterPointImpl.getParameterPoint(names2, values2);

    this.pushOutput(pp, 1);

    double uf = 0;
    if (this.useFirst) {
      uf = 1;
    }
    long sampsz = Math.round(Math.sqrt(N * tab.getNumRows()));
    sampsz = (sampsz > (tab.getNumRows()-1)) ? (tab.getNumRows()-1) : sampsz;
    String[] names3 = {
        "sampleSize", "seed", "useFirst"};
    double[] values3 = {
         (double) sampsz,
        (double) seed, uf};
    pp = ParameterPointImpl.getParameterPoint(names3, values3);

    this.pushOutput(pp, 2);
    this.pushOutput(tab, 3);
  }

}
