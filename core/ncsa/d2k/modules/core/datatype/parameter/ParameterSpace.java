package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.*;

/**
A parameter space is defines the search space for an optimizer.
The space is simply defined as a hyper-rectangle defined by min and max values for each dimension.
Each parameter has a minimum and maximum value which an optimizer should used to limit its operation.
Each parameter has a default value which an optimizer can use as the intial starting point.
Each parameter has a resolution which an optimizer should used to limit its operation.
Each parameter has type that represented as an integer define in ColumnTypes.
A parameter space is composed of one or more subspaces.
Each D2K module in the itinerary which the user disires to optimize has its separate ParameterSpace.
When these separate spaces are combined to form a single unified ParameterSpace,
the original substructure is maintain so that the ParameterSpace can be use to decompose points in the
unified space.
The column order for this table should match the order in which the module designer
of the parameters as described in the PropertyDescriptors
defined in the space generator(s).
Paramters can be accessed by name using getParameterIndex().
*/
public interface ParameterSpace extends ExampleTable, java.io.Serializable {

  //static final long serialVersionUID = 2508941379956505568L;

  /**
   * Instantiate a ParameterSpace from the information in the given table.
   * Each column in the table represents a paramter.
   * Row 1 is the minimum parameter value.
   * Row 2 is the maximum parameter value.
   * Row 3 is the parameter resolution.
   * Row 4 is the default parameter setting.
   * Row 5 is the type as an integer as defined in ColumnTypes
   * @return an int value representing the minimum possible value of the parameter.
   */
  public ParameterSpace createFromTable(Table table);

  /**
   * Get the number of parameters that define the space.
   * @return an int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters();

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a string value representing the name of the parameter.
   */
  public String getName(int parameterIndex);

  /**
   * Get the parameter index of that corresponds to the given name.
   * @return an integer representing the index of the parameters.
   */
  public int getParameterIndex(String name);

  /**
   * Get the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMinValue(int parameterIndex);

  /**
   * Get the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMaxValue(int parameterIndex);

  /**
   * Get the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getDefaultValue(int parameterIndex);

  /**
   * Set the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   */
  public void setMinValue(int parameterIndex, double value);

  /**
   * Set the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setMaxValue(int parameterIndex, double value);

  /**
   * Set the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setDefaultValue(int parameterIndex, double value);

  /**
   * Get the minimum values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the minimum possible values of all parameters.
   */
  public ParameterPoint getMinParameterPoint();

  /**
   * Get the maximum values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the maximum possible values of all parameters.
   */
  public ParameterPoint getMaxParameterPoint();

  /**
   * Get the default values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the default values of all parameters.
   */
  public ParameterPoint getDefaultParameterPoint();

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex);

  /**
   * Get the type of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the type of the parameter value as defined in ColumnTypes.
   */
  public int getType(int parameterIndex);

  /**
   * Get the number of subspaces that defines the space.
   * @return a int value representing the number subspaces that define the space.
   */
  public int getNumSubspaces();

  /**
   * Get the number of parameters in each subspace.
   * @return a int array of values the number of parameters defining each subspace.
   */
  public int [] getSubspaceNumParameters();

  /**
   * Get the subspace index of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the subpace index number of parameter.
   */
  public int getSubspaceIndex(int parameterIndex);

  /**
   * Get the subspace parameter index of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the subpace index number of parameter.
   */
  public int getSubspaceParameterIndex(int parameterIndex);

  /**
   * Get a subspace from the space.
   * @param subspaceIndex the index of the subspace of interest.
   * @return a ParameterSpace which defines the indicated subspace.
   */
  public ParameterSpace getSubspace(int subspaceIndex);

  /**
   * Join two ParameterSpaces to produce a single parameter space.
   * @param firstSpace the first of the two ParameterSpaces to join.
   * @param secondSpace the second of the two ParameterSpaces to join.
   * @return a ParameterSpace which defines the indicated subspace.
   */
  public ParameterSpace joinSubspaces(ParameterSpace firstSpace, ParameterSpace secondSpace);

  /**
   * Split a ParameterSpace into two parameter spaces.
   * @return an array of two ParameterSpaces which define the two subspaces, the first being the head and the second being the tail.
   */
  public ParameterSpace [] splitSubspaces(ParameterSpace space);

} /* ParameterSpace */