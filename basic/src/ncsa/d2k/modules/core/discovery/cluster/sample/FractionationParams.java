package ncsa.d2k.modules.core.discovery.cluster.sample;



import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

/**
 * <p>Title: FractionationParams</p>
 * <p>Description: MOdule to input parameters for KMeans</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class FractionationParams extends FractionationParamsOPT {


  public FractionationParams() {
  }



  /**
   * Sets the Number of Clusters property.
   * @param i The Number of Clusters property.
   */
  public void setNumClusters(int i) {N = i;}
  /**
   * Returns the Number of Clusters property.
   * @return int The Number of Clusters property.
   */
  public int getNumClusters() {return N;}


  /**
   * Returns the value of the Clustering Method property.
   * @return int The value of the Clustering Method property.
   */
  public int getClusterMethod () {return  _clusterMethod;}

  /**
   * Sets the value of the Clustering Method property.
   * @param noc The value for the Clustering Method property, should be >=2
   */
  public void setClusterMethod (int noc) {_clusterMethod = noc;}


  /**
   * Returns the value of the Distance Metric property
   * @return int The value of the Distance Metric property
   */
  public int getDistanceMetric() {return  _distanceMetric;}
  /**
   * Sets the value of the Distance Metric property.
   * @param dm The value for the Distance Metric property
   */
  public void setDistanceMetric(int dm) {_distanceMetric = dm;}


  /**
   * Returns the value of the HAC Distance Threshold property.
   * @return int The value of the HAC Distance Threshold property, should be in the range [1,100].
   */
  public int getHacDistanceThreshold() {return  _hacDistanceThreshold;}
  /**
   * Sets the value of the HAC Distance Threshold property.
   * @param noc The value for the HAC Distance Threshold property, should be in the range [1,100].
   */
  public void setHacDistanceThreshold (int noc) {_hacDistanceThreshold = noc;}


  /**
   * Returns the value of the Max Partition Size property.
   * @return int The value of the Max Partition Size property.
   */
  public int getPartitionSize() {return  _fracPart;}
  /**
   * Sets the value of the Max Partition Size property.
   * @param noc The value of the Max Partition Size property.
   */
  public void setPartitionSize (int noc) {_fracPart = noc;}


  /**
   * Returns the value of the Sort Attribute property.
   * @return int The value of the Sort Attribute property.
   */
  public int getNthSortTerm() {return _nthSortTerm;}
  /**
   * Sets the value of the Sort Attribute property.
   * @param mt The value for the Sort Attribute property.
   */
  public void setNthSortTerm(int mt) {_nthSortTerm = mt;}


  /**
   * Returns the value of the Number of Assignment Passes property.
   * @return int The value of the Number of Assignment Passes property.
   */
  public int getRefinementMaxIterations() {return  _maxIterations;}
  /**
   * Sets the value of the Number of Assignment Passes property.
   * @param dm The value for the Number of Assignment Passes property, should be >0.
   */
  public void setRefinementMaxIterations(int dm) {_maxIterations = dm;}

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[7];
    pds[0] = new PropertyDescription("numClusters",
                                     "Number of Clusters",
                                     "This is the number of clusters you want to form (>= 2).");
    pds[1] = new PropertyDescription("clusterMethod",
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
    pds[2] = new PropertyDescription("distanceMetric",
                                     "Distance Metric",
        "This property determine the type of distance function used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    pds[3] = new PropertyDescription("hacDistanceThreshold",
                                     "HAC Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two.");
    pds[4] = new PropertyDescription("partitionSize",
                                              "Max Partition Size",
        "The size of partitions to use in the sampling process."
        );
    pds[5] = new PropertyDescription("NthSortTerm",
                                              "Sort Attribute",
                                              "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
    pds[6] = new PropertyDescription("refinementMaxIterations",
                                              "Number of Assignment Passes",
        "This property specifies the number of iterations of cluster refinement to perform (> 0).");
    return pds;
  }


  //========================
  // D2K Abstract Overrides

/**
 Return a custom gui for setting properties.
 @return CustomModuleEditor
 */
  public CustomModuleEditor getPropertyEditor(){
    return new FractionationParams_Props(this);
  }

/**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
 */
  public String getInputInfo (int parm1) {
    if (parm1 == 0) {
      return  "Table of examples to cluster";
    } else {
      return  "";
    }
  }

/**
   Return the name of a specific input.
   @param i The index of the input.
   @return The name of the input
 */
  public String getInputName (int parm1) {
    if (parm1 == 0) {
      return  "Table";
    } else {
      return  "";
    }
  }

/**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
 */
  public String[] getInputTypes () {
    String[] in =  {"ncsa.d2k.modules.core.datatype.table.Table"};
    return  in;
  }


/**
 Perform the work of the module.
 @throws Exception
 */
  protected void doit() throws Exception {

    Table tab = (Table)this.pullInput(0);
    doingit(tab);

  }

}
