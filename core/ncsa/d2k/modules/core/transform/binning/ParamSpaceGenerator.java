package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class ParamSpaceGenerator extends AbstractParamSpaceGenerator {

  public static final String BIN_METHOD = "Binning Method";
  public static final String ITEMS_PER_BIN = "Number of items per bin";
  public static final String NUMBER_OF_BINS = "Number of bins";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {

    ParameterSpace psi = new ParameterSpaceImpl();
    String[] names = { BIN_METHOD, ITEMS_PER_BIN, NUMBER_OF_BINS};
    double[] min = { 0, 1, 1};
    double[] max = { 1, Integer.MAX_VALUE, Integer.MAX_VALUE};
    double[] def = { 0, 3, 3};
    int[] res = { 1, 1, 1};
    int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
    psi.createFromData(names, min, max, def, res, types);
    return psi;
  }

  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Naive Bayes Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[3];
    pds[0] = new PropertyDescription(BIN_METHOD, "Discretization Method",
        "The method to use for discretization.  Select 0 to create bins" +
        " by weight.  This will create bins with an equal number of items in "+
        "each slot.  Select 1 to discretize by specifying the number of bins. "+
        "This will give equally spaced bins between the minimum and maximum for "+
        "each scalar column.");
    pds[1] = new PropertyDescription(ITEMS_PER_BIN, "Number of Items per Bin",
        "When binning by weight, this is the number of items" +
        " that will go in each bin.  The last bin may have less than "+
        "<i>weight</i> values, however.");
    pds[2] = new PropertyDescription(NUMBER_OF_BINS, "Number of Bins",
                                     "Define the number of bins absolutely. "+
                                     "This will give equally spaced bins between "+
                                     "the minimum and maximum for each scalar "+
                                     "column.");
    return pds;
  }
}