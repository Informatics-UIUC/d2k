package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.*;

/**

The ParameterPoint object can extend ExampleImpl.
It will be input to the learning algorithm.
It should use the same column names and column order as the ParameterSpace implementation.
Note: It is important that this be an Example so that additional layers
(optimizing the optimizer etc..) can more easily be implemented.

*/

public interface ParameterPoint extends Example, java.io.Serializable {

  //static final long serialVersionUID = 2508941379956505568L;


  /**
   * Create a ParameterPoint from the information in the given table.
   * Each column in the table represents a paramter.
   * Row 1 is the values for all the parameter settings.
   * @param table the table representing the parameter space.
   * @return a ParameterPoint.
   */
  public ParameterPoint createFromTable(MutableTable table);

  /**
   * Create a ParameterPoint from primative data types.
   * @param names the names of the paramters.
   * @param values the values parameter settings.
   * @param types the type as an integer as defined in ColumnTypes.
   * @return a ParameterPoint.
   */

  public ParameterPoint createFromData(String [] names, double [] values);


  /**
   * Get the number of parameters that define the space.
   * @return An int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters();

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return A string value representing the name of the parameter.
   */
  public String getName(int parameterIndex);

  /**
   * Get the value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getValue(int parameterIndex);


} /* ParameterPoint */
