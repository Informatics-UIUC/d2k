package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>
 * Title: ClusterAssignment
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
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

public class ClusterAssignment
    extends ClusterAssignmentOPT {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  public int getNumAssignments() {
    return m_numAssignments;
  }

  public void setNumAssignments(int noc) {
    m_numAssignments = noc;
  }

  //just need these for the clustering at the end
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  //================
  // Constructor(s)
  //================
  public ClusterAssignment() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[5];
    descriptions[0] = new PropertyDescription("clusterMethod",
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
    descriptions[1] = new PropertyDescription("distanceMetric",
                                              "Distance Metric",
        "This property determine the type of distance fucntion used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    descriptions[2] = new PropertyDescription("numAssignments",
                                              "Number of Assignments",
        "This property specifies the number of iterations to perform (> 0).");
    descriptions[3] = new PropertyDescription("CheckMissingValues",
                                              "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");
    descriptions[4] = new PropertyDescription("verbose",
                                              "Verbose Ouput",
        "Do you want verbose output to the console.");
    return descriptions;
  }

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new ClusterAssignment_Props(this, true, true);
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Table of initial centroids";
    } else if (parm1 == 1) {
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
      return "Table";
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
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {
    Table initcenters = (Table)this.pullInput(0);
    Table initEntities = (Table)this.pullInput(1);
    ClusterRefinement refiner = new ClusterRefinement(this.getClusterMethod(),
        this.getDistanceMetric(), this.getNumAssignments(), this.getVerbose(),
        this.getCheckMissingValues());
    this.pushOutput(refiner.assign(initcenters, initEntities), 0);
  }
}
