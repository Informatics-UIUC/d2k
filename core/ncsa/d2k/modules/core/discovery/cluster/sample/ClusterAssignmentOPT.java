package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>
 * Title: ClusterAssignmentOPT
 * </p>
 * <p>
 * Description: Takes a set of centroids and a table and repeatedly assigns
     * table rows to clusters whose centroids are closest in vector space.  When one
 * assisgnment is completed new centroids are calulated and the process is repeated.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: NCSA Automated Learning Group
 * </p>
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
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class ClusterAssignmentOPT
    extends ComputeModule {

  //==============
  // Data Members
  //==============

  // Distance Metric
  static public final int s_Euclidean = 0;
  static public final int s_Manhattan = 1;
  static public final int s_Cosine = 2;

  private long m_start = 0;
  protected int[] _ifeatures = null;

  protected int _distanceMetric = s_Euclidean;
  protected int m_numAssignments = 5;
  protected int _clusterMethod = HAC.s_WardsMethod;

  //============
  // Properties
  //============

  protected boolean m_verbose = false;
  public boolean getVerbose() {
    return m_verbose;
  }

  public void setVerbose(boolean b) {
    m_verbose = b;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  //================
  // Constructor(s)
  //================
  public ClusterAssignmentOPT() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("checkMissingValues",
                                              "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");

    descriptions[1] = new PropertyDescription("verbose",
                                              "Verbose Ouput",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Cluster Assignment";
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
      return "Table of initial centroids";
    } else if (parm1 == 2) {
      return "Table of entities to cluster";
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
    } else if (parm1 == 2) {
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
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Finds a locally optimal clustering by iteratively assigning examples to ";
    s += "selected points in vector space.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "Takes a set of cluster centroids (a table) and a table of cluster entites and repeatedly assigns ";
    s += "the entities to the cluster whose centroid is closest in vector space. ";
    s += "When one assisgnment is completed new centroids are calulated form the clusters just formed and ";
    s += "the process is repeated.  The algorithm will iterate <i>number of iterations</i> times but will ";
    s += "halt if the current iteration produces results not significantly different from the previous iteration.";
    s += "</p>";
    s += "<p>The HAC algorithm is run on the final set of clusters to build the cluster tree from the ";
    s += "cut to the root.  This tree is stored in the newly formed model along with the initial table ";
    s += "of examples and the set of TableClusters formed.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The second table input should be a mutable implementation. ";
    s += "The tables must have the same structure -- attribute types and order (and input features ";
    s += "if example tables).";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The second input table is included in the ClusterModel.  Neither table ";
    s += "input is modified.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The time complexity is linear in the number of examples times the number of iterations. ";
    s += "The algorithm repeatedly builds two times <i>number of clusters</i> TableClusters from ";
    s += "\"number of examples\" TableClusters and requires heap resources to that extent.  A single ";
    s += "TableCluster's memory size will vary as the size of the individual examples being clustered.";
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
      return "Newly created ClusterModel";
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
      return "ClusterModel";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return out;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {
    ParameterPoint pp = (ParameterPoint) pullInput(0);
    _clusterMethod = (int) pp.getValue(0);
    _distanceMetric = (int) pp.getValue(1);
    m_numAssignments = (int) pp.getValue(2);

    Table initcenters = (Table)this.pullInput(1);
    Table initEntities = (Table)this.pullInput(2);

    ClusterRefinement refiner = new ClusterRefinement(_clusterMethod,
        _distanceMetric, m_numAssignments, this.getVerbose(),
        this.getCheckMissingValues());
    this.pushOutput(refiner.assign(initcenters, initEntities), 0);
  }
}
