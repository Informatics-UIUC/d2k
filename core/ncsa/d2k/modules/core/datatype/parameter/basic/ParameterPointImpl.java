package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class ParameterPointImpl extends ContinuousExample implements ParameterPoint, Example, java.io.Serializable {
  Table     table;

  final static int valueRowIndex = 0;

  public ParameterPointImpl () {
  }

  /**
   * Create a ParameterPoint from the information in the given table.
   * Each column in the table represents a paramter.
   * Row 1 is the values for all the parameter settings.
   * Row 2 is the types for all the parameter settings.
   * @param table the table representing the parameter space.
   * @return a ParameterPoint.
   */
  public ParameterPoint createFromTable(MutableTable table) {
    this.table = table;
    return (ParameterPoint) this;
  }

  /**
   * Instantiate a ParameterPoint from primative data types.
   * @param names the names of all the paramters.
   * @param values the values for all the parameters.
   * @param types the types for all of the parameter.
   * @return a ParameterPoint.
   */
  public ParameterPoint createFromData (String [] names, double [] values) {

    int numParameters = names.length;
    int numRows       = 1;

    int numValues = numRows * numParameters;

    double [] data = new double[numValues];

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
        switch (rowIndex) {
        case valueRowIndex:
          data[rowIndex * numParameters + parameterIndex] = values[parameterIndex];
          break;
      }
      }
    }

    ContinuousExampleSet table = new ContinuousExampleSet(data, numRows, numParameters, 0, names, null);

    ParameterPoint point = createFromTable(table);

    return (ParameterPoint) point;
  }

  /**
   * Get the number of parameters that define the space.
   * @return An int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters() {
    return table.getNumColumns();
  }

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return A string value representing the name of the parameter.
   */
  public String getName(int parameterIndex) {
    table.getColumnLabel(parameterIndex);
    return null;
  }

  /**
   * Get the value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getValue(int parameterIndex) {
    return table.getDouble(valueRowIndex, parameterIndex);
  }

}