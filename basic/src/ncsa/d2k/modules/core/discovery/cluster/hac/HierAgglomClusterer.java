package ncsa.d2k.modules.core.discovery.cluster.hac;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

/**
 *
 * <p>Title: HierAgglomClusterer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 *
 * TODO: change distance method to accomodate sparse matrices
 */

public class HierAgglomClusterer
    extends HierAgglomClustererOPT {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============

  protected int _clusterMethod = HAC.s_WardsMethod;
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  protected int _distanceMetric = HAC.s_Euclidean;
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  protected int _numberOfClusters = 5;
  public int getNumberOfClusters() {
    return _numberOfClusters;
  }

  public void setNumberOfClusters(int noc) {
    _numberOfClusters = noc;
  }

  protected int _thresh = 0;
  public int getDistanceThreshold() {
    return _thresh;
  }

  public void setDistanceThreshold(int noc) {
    _thresh = noc;
  }

  //================
  // Constructor(s)
  //================
  public HierAgglomClusterer() {
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
    PropertyDescription[] descriptions = new PropertyDescription[6];
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
    descriptions[2] = new PropertyDescription("numberOfClusters",
                                              "Number of Clusters",
                                     "This property specifies the number of clusters to form (>= 2).");
    descriptions[3] = new PropertyDescription("distanceThreshold",
                                              "Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two.");
    descriptions[4] = new PropertyDescription("CheckMissingValues",
        "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");
    descriptions[5] = new PropertyDescription("verbose",
        "Verbose Ouput",
        "Do you want verbose output to the console.");
    return descriptions;
  }

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new HierAgglomClusterer_Props(this, true, true);
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
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
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
     Perform the work of the module.
   */
  protected void doit() throws Exception {
    HAC hac = new HAC(this.getClusterMethod(), this.getDistanceMetric(),
                      this.getNumberOfClusters(), this.getDistanceThreshold(),
                      getVerbose(), this.getCheckMissingValues());
    this.pushOutput(hac.buildModel( (Table)this.pullInput(0)), 0);
  }

}
