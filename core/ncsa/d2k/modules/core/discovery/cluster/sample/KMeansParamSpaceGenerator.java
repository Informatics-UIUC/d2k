package ncsa.d2k.modules.core.discovery.cluster.sample;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;

/**
 * <p>Title: KMeansParamSpaceGenerator</p>
 * <p>Description: Generates the optimization space and initial
 * values for the KMeans Clustering Algorithm.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class KMeansParamSpaceGenerator extends AbstractParamSpaceGenerator {

  //==============
  // Data Members
  //==============

  public static final String NUM_CLUSTERS = "Number of Clusters";
  public static final String SEED = "Random Seed";
  public static final String USE_FIRST = "Use First";
  public static final String CLUSTER_METHOD = "Cluster Method";
  public static final String DISTANCE_METRIC = "Distance Metric";
  public static final String MAX_ITERATIONS = "Max Refinement Iterations";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
        ParameterSpace psi = new ParameterSpaceImpl();
        String[] names = { NUM_CLUSTERS, SEED, USE_FIRST, CLUSTER_METHOD, DISTANCE_METRIC, MAX_ITERATIONS};
        double[] min = { 0, 0, 0, 0, 0, 1};
        double[] max = { Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 6, 2, Integer.MAX_VALUE};
        double[] def = { 5, 0, 0, 0, 0, 5};
        int[] res = { 1, 1, 1, 1, 1, 1};
        int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.BOOLEAN, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
        psi.createFromData(names, min, max, def, res, types);
        return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "KMeans Parameter Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[6];
    pds[0] = new PropertyDescription(CLUSTER_METHOD,
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
    pds[1] = new PropertyDescription(SEED,
                                     "Random Seed",
        "The seed for the random number generater used to select the set of <i>Number of Clusters</i> " +
        "table rows that defines the initial cluster centers. " +
        "If the same seed is used across runs with the same input table, the same sets will be identical . " +
        "If <i>Use First Rows</i> is selected, this seed is not used. ");
    pds[2] = new PropertyDescription(USE_FIRST,
                                     "Use First Rows",
        "If this option is selected, the first <i>Number of Clusters</i> entries in the input table " +
        "will be used as the initial cluster centers, " +
        "rather than selecting a random set of table rows. ");
    pds[3] = new PropertyDescription(NUM_CLUSTERS,
                                     "Number of Clusters",
        "This property specifies the number of clusters to form. It must be greater than 1.");
    pds[4] = new PropertyDescription(DISTANCE_METRIC,
                                     "Distance Metric",
        "This property determines the type of distance function to use in calculating the " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    pds[5] = new PropertyDescription(MAX_ITERATIONS,
                                     "Number of Assignment Passes",
        "This property specifies the number of iterations of cluster refinement to perform.  " +
        "It must be greater than 0.");
    return pds;
  }

}

// Start QA Comments
// 4/6/03 - Ruth started QA
//        - Reordered Property Descriptions & words to match KMeansParams.
//        - Updated Module Name
//
// End QA Comments
