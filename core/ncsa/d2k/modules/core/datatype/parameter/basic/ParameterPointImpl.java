package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ParameterPointImpl extends FloatExample implements ParameterPoint, Example, java.io.Serializable {

  Table [] subspaceTables;
  int numSubspaces;
  int [] subspaceSizes;

  public ParameterPointImpl () {
  }

  public ParameterPointImpl (Table table) {

    this.subspaceTables = new Table[1];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[1];
    this.subspaceSizes[0] = table.getNumColumns();

  }


  /**
   * Get the number of parameters that define the space.
   * @return An int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters() {
  return -1;
  }

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return A string value representing the name of the parameter.
   */
  public String getName(int parameterIndex) {
    return null;
  }

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return An int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex) {
    return -1;
  }

  /**
   * Get the value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getValue(int parameterIndex) {
    return Double.NaN;
  }

}