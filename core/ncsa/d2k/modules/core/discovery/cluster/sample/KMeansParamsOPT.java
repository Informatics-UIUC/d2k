package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: KMeansParamsOPT</p>
 * <p>Description: MOdule to input parameters for KMeans</p>
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
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class KMeansParamsOPT
    extends DataPrepModule {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public KMeansParamsOPT() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  protected int N = 5;

  /** the seed for the random number generator */
  protected int seed = 0;

  /** true if the first N rows should be the sample, false if the sample
          should be random rows from the table */
  protected boolean useFirst = false;

  protected int _clusterMethod = HAC.s_WardsMethod;

  protected int _distanceMetric = HAC.s_Euclidean;

  protected int _maxIterations = 5;

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "KMeans Parameters";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control Parameters";
    } else if (parm1 == 1) {
      return "Table of Entities to Cluster";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "ParameterPoint";
    } else if (parm1 == 1) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }


  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "The KMeans clustering algorithm is an approach where a sample ";
    s += "of size num_clusters is chosen at random from the input table ";
    s += "of examples.  These samples form vectors in the eaxmple space and are used as the initial ";
    s += "\"means\" for the cluster assignment module. ";
    s += "The assignment module, once it has made refinements, outputs the final cluster model. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This algorithm is comprised of three modules: this module (KMeansParams), the sampler ";
    s += "(SampleTableRowsOPT), and the cluster refiner ";
    s += "(ClusterAssignment).";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input table is not modified by this algorithm, however it is include as part of ";
    s += "the ClusterModel that is created.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This algortihm runs in time O(num_examples).  See the component modules ";
    s += "information to understand the memory requirements overall.";
    s += "</p>";
    return s;
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return "Parameters for Cluster Assignment";
    } else if (parm1 == 1) {
      return "Parameters for Sample Table Rows";
    } else if (parm1 == 2) {
      return "Table of entities to cluster";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Parameters for Cluster Assignment";
    } else if (parm1 == 1) {
      return "Parameters for Sample Table Rows";
    } else if (parm1 == 2) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws Exception {

    ParameterPoint pp = (ParameterPoint)this.pullInput(0);
    this.N = (int) pp.getValue(0);
    this.seed = (int) pp.getValue(1);
    this.useFirst = ( ( (int) pp.getValue(2)) == 1) ? true : false;
    this._clusterMethod = (int) pp.getValue(3);
    this._distanceMetric = (int) pp.getValue(4);
    this._maxIterations = (int) pp.getValue(5);

    doingit();
    this.pushOutput(this.pullInput(1), 2);

  }

  protected void doingit() {
    String[] names1 = {
        "clusterMethod", "distanceMetric", "maxIterations"};
    double[] values1 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric,
        this._maxIterations};
    ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

    this.pushOutput(pp, 0);

    double uf = 0;
    if (this.useFirst) {
      uf = 1;
    }
    String[] names2 = {
        "sampleSize", "seed", "useFirst"};
    double[] values2 = {
         (double)this.N,
        (double)this.seed, uf};
    pp = ParameterPointImpl.getParameterPoint(names2, values2);

    this.pushOutput(pp, 1);
  }
}
