package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: KMeansParameterDefns</p>
 * <p>Description: A common place to define the parameter names used by the
 * KMeans modules.  Using these Strings in the Property Descriptions and dialogs
 * helps keep things consistent.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith & R. Aydt
 * @version 1.0
 */

public interface KMeansParameterDefns {

  public static final String NUM_CLUSTERS = "Number of Clusters";
  public static final String SEED = "Random Seed";
  public static final String USE_FIRST = "Use First Rows";
  public static final String CLUSTER_METHOD = "Clustering Method";
  public static final String DISTANCE_METRIC = "Distance Metric";
  public static final String MAX_ITERATIONS = "Maximum Number of Iterations";

}

// Start QA Comments
// 4/7/03 - Ruth created this file to make it easier to manage various
//          modules that used these strings.  Many were hard-coded before
//          and hard to keep consistent.
//        - Ready for Basic
// End QA Comments