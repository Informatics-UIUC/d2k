package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.*;

/**
ParameterSpace:

The AbstractParamSpaceGen (or subclasses of it) will output a ParameterSpace.

·	ParameterSpace can subclass Table.
o	Row 1 = min values
o	Row 2 = max values
o	Row 3 = default point
o	Row 4 = resolutions
o	Row 5 = subspace group
·	Additionally, we will maintain an array of ints called subspaces that describes a partition of the space into subspaces.
o	When a space is initially created it will contain a single entry in this array which is the count of columns.
o	When two spaces are joined (see space joining and splitting below) this array will be appended with the column count of the appended space.  This process may occur repeatedly.
o	Similarly, when a space is split, to new subspaces will be formed.  If N is that last entry in the subspaces array before splitting then one subspace will contain the last N columns and will have one subspaces entry of value N.  The second space will contain the remaining columns and will have the original subspaces array but with the value N removed.
·	There should be methods as follows (list is not exhaustive)
o	Accessors to get the min/max values (get and set parameters)
o	Accessors to get resolution for a parameter
o	Accessors to get names for parameters
o	Get dimension of bias space
o	Get the default value as a ParameterPoint.  ParameterPoint can subclass ExampleImpl
o	Join and Spilt a subspace
·	The column order for this table should match the order of the parameters as described in the PropertyDescriptors defined in the space generator.  The learning algorithm will need to rely on this to be able to recognize which parameter is which.  Alternatively, we could rely on the column names.

*/
public interface ParameterSpace extends ExampleTable, java.io.Serializable {

  //static final long serialVersionUID = 2508941379956505568L;

  //static final long serialVersionUID = -8081185634463160785L;


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
   * Get the minimum value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory minimum parameter setting.
   */
  public double getFactoryMinParameterValue(int parameterIndex);

  /**
   * Get the maximum value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory maximum parameter setting.
   */
  public double getFactoryMaxParameterValue(int parameterIndex);

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