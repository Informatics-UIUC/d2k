package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.*;

/**
A parameter space is defines the search space for an optimizer.
The space is simply defined as a hyper-rectangle defined by min and max values for each dimension.
Each dimension has a resolution which an optimizer may used to limit its operation.
A parameter space contains both user modifiable and factory default settings for each parameter minimum and maximum.
A parameter space contains both user modifiable and factory default ParameterPoint.
An optimizer may elect to use the default ParameterPoint to select the first point in parameter space.
A parameter space is composed of one or more subspaces.
Each D2K module in the itinerary which the user disires to optimize has its separate ParameterSpace.
When these separate spaces are combined to form a single unified ParameterSpace,
the original substructure is maintain so that the ParameterSpace and ParmeterPoints can be later decomposed.
The column order for this table should match the order of the parameters as described in the PropertyDescriptors
defined in the space generator(s).  The learning algorithm will need to rely on this to be able to recognize
which parameter is which.  Alternatively, we could rely on the column names.
*/
public interface ParameterSpace extends ExampleTable, java.io.Serializable {

  //static final long serialVersionUID = 2508941379956505568L;

  //static final long serialVersionUID = -8081185634463160785L;


  /**
   * Instantiate a ParameterSpace from the information in the given table.
   * Each column in the table represents a paramter.
   * Row 1 is the minimum parameter value.
   * Row 2 is the maximum parameter value.
   * Row 3 is the parameter resolution.
   * Row 4 is the default parameter setting.
   * Row 5 is the default minimum parameter value.
   * Row 6 is the default maximum parameter value.
   * Row 7 is the factory default parameter setting.
   * Row 8 is the factory default minimum parameter value.
   * Row 9 is the factory default maximum parameter value.
   * @return an int value representing the minimum possible value of the parameter.
   */
  public int createFromTable(Table table);

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
  public String getParameterName(int parameterIndex);

  /**
   * Get the names of all the parameters.
   * @return an array of string values representing the names of the all the parameters.
   */
  public String [] getParameterNames();

  /**
   * Get the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMinParameterValue(int parameterIndex);

  /**
   * Get the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMaxParameterValue(int parameterIndex);

  /**
   * Get the minimum default value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory minimum parameter setting.
   */
  public double getFactoryDefaultMinParameterValue(int parameterIndex);

  /**
   * Get the maximum default value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory maximum parameter setting.
   */
  public double getFactoryDefaultMaxParameterValue(int parameterIndex);

  /**
   * Get the minimum value of a parameter based on default settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the default minimum parameter setting.
   */
  public double getDefaultMinParameterValue(int parameterIndex);

  /**
   * Get the maximum value of a parameter based on default settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the default maximum parameter setting.
   */
  public double getDefaultMaxParameterValue(int parameterIndex);

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex);

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