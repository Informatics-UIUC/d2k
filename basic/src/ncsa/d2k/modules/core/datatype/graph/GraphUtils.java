package ncsa.d2k.modules.core.datatype.graph;

/**
 * A collection of graph-related constants and utilities.
 */
public class GraphUtils {

  /**
   * The recommended key for "label" attributes.
   */
  public static final String LABEL = "label";

  /**
   * The recommended key for an attribute that places a vertex into a
   * partition of a k-partite graph (e.g., a bipartite graph).
   */
  public static final String PARTITION = "partition";

  /**
   * The recommended key for "weight" attributes.
   */
  public static final String WEIGHT = "weight";

  /**
   * This class is not intended to be instantiated.
   */
  private GraphUtils() { }

}
