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
   * Get the number of parameters that define the space.
   * @return An int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters();

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return A string value representing the name of the parameter.
   */
  public String getParameterName(int parameterIndex);

  /**
   * Get the names of all the parameters.
   * @return An array of string values representing the names of the all the parameters.
   */
  public String [] getParameterNames();

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return An int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex);

  /**
   * Get the subspace index of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return An int value representing the subpace index number of parameter.
   */
  public int getSubspaceIndex(int parameterIndex);

  /**
   * Get the number of subspaces that defines the space.
   * @return An int value representing the number subspaces that define the space.
   */
  public int getNumSubspaces();

  /**
   * Get the number of parameters in each subspace.
   * @return An int array of values the number of parameters defining each subspace.
   */
  public int [] getSubspaceNumParameters();

  /**
   * Get a subspace from the space.
   * @param subspaceIndex the index of the subspace of interest.
   * @return A ParameterPoint which defines the indicated subspace.
   */
  public ParameterPoint getSubspace(int subspaceIndex);

  /**
   * Join two ParameterPoints to produce a single parameter space.
   * @param firstSpace the first of the two ParameterPoints to join.
   * @param secondSpace the second of the two ParameterPoints to join.
   * @return A ParameterPoint which defines the indicated subspace.
   */
  public ParameterPoint joinSubspaces(ParameterPoint firstSpace, ParameterPoint secondSpace);

  /**
   * Split a ParameterPoint into two parameter spaces.
   * @return An array of two ParameterPoints which define the two subspaces, the first being the head and the second being the tail.
   */
  public ParameterPoint [] splitSubspaces(ParameterPoint space);



} /* ParameterPoint */
