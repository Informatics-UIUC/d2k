package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class LWRParamSpaceGenerator
    extends AbstractParamSpaceGenerator {

  //public static final String BIN_METHOD = "Binning Method";
  //public static final String ITEMS_PER_BIN = "Number of items per bin";
  //public static final String NUMBER_OF_BINS = "Number of bins";

  public static final String KERNEL_SELECTOR = "Kernel Selector";
  //public static final String DISTANCE_SELECTOR = "Distance Selector";
  public static final String NEAREST_NEIGHBORS = "Nearest Neighbors";
  public static final String NUMBER_OF_POINTS = "Number of Points";
  public static final String USE_NEAREST = "Use Nearest Neighbors";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {

    ParameterSpace psi = new ParameterSpaceImpl();
    String[] names = {
        KERNEL_SELECTOR, NEAREST_NEIGHBORS, NUMBER_OF_POINTS, USE_NEAREST};
    double[] min = { 0, 1, 1, 0};
    double[] max = { 4, Integer.MAX_VALUE, 100, 1};
    double[] def = { 0, 1, 1, 0};
    int[] res = { 1, 1, 1, 1};
    int[] types = {
        ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER,
        ColumnTypes.BOOLEAN};
    psi.createFromData(names, min, max, def, res, types);
    return psi;
  }

  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "LWR Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[4];
    pds[0] = new PropertyDescription(KERNEL_SELECTOR, "", "");
    pds[1] = new PropertyDescription(NEAREST_NEIGHBORS, "", "");
    pds[2] = new PropertyDescription(NUMBER_OF_POINTS, "", "");
    pds[3] = new PropertyDescription(USE_NEAREST, "", "");
    return pds;
  }
}