package ncsa.d2k.modules.core.discovery.cluster.hac;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;

/**
 * <p>Title: HierAgglomClustererParamSpaceGenerator</p>
 * <p>Description: Generates the optimization space and initial
 * values for the HAC learning module.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class HierAgglomClustererParamSpaceGenerator extends AbstractParamSpaceGenerator {

  //==============
  // Data Members
  //==============

  public static final String CLUSTERING_METHOD = "Clustering Method";
  public static final String DISTANCE_METRIC = "Distance Metric";
  public static final String NUMBER_OF_CLUSTERS = "Number of Clusters";
  public static final String HAC_DISTANCE_THRESHOLD = "HAC Distance Threshold";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
        ParameterSpace psi = new ParameterSpaceImpl();
        String[] names = { CLUSTERING_METHOD,
            DISTANCE_METRIC,
            NUMBER_OF_CLUSTERS,
            HAC_DISTANCE_THRESHOLD};
        double[] min = { 0, 0, 1, 0};
        double[] max = { 6, 2, Integer.MAX_VALUE, 100};
        double[] def = { 0, 0, 5, 0};
        int[] res = { 1, 1, 1, 1};
        int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
        psi.createFromData(names, min, max, def, res, types);
        return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "HAC Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[4];
    descriptions[0] = new PropertyDescription(CLUSTERING_METHOD,
                                              "Clustering Method",
        "The method to use for determining the distance between two clusters. " +
        "<p>0 WARDS METHOD: Use a minimum variance approach that sums the squared error " +
        "(distance) for every point in the cluster to the cluster centroid.</p>" +
        "<p>1 SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
        "<p>2 COMPLETE LINK: Distance of furthest pair (one from each cluster).</p>" +
        "<p>3 UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
        "<p>4 WPGMA: Weighted pair group method using arithmetic averages.</p>" +
        "<p>5 UPGMC: Unweighted pair group method using centroids.</p>" +
        "<p>6 WPGMC: Weighted pair group method using centroids.</p>");
    descriptions[1] = new PropertyDescription(DISTANCE_METRIC,
                                              "Distance Metric",
        "This property determine the type of distance fucntion used to calculate " +
        "distance between two examples." +
        "<p>0 EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>1 MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>2 COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    descriptions[2] = new PropertyDescription(NUMBER_OF_CLUSTERS,
                                              "Number of Clusters",
                                     "This property specifies the number of clusters to form (>= 2).");
    descriptions[3] = new PropertyDescription(HAC_DISTANCE_THRESHOLD,
                                              "Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([0...100]).  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two.");
    return descriptions;
  }

}