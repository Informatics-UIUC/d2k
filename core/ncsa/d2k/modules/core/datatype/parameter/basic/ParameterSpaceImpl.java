package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ParameterSpaceImpl extends FloatExampleSet implements ParameterSpace, ExampleTable, java.io.Serializable {

  Table [] subspaceTables;
  int numSubspaces;
  int [] subspaceSizes;
  int [] subspaceNumParameters;
  int numParameters;
  String [] parameterNames;
  double [] parameterMinValues;
  double [] parameterMaxValues;
  double [] parameterDefaultValues;
  int    [] resolutions;
  int    [] parameterSubspaceIndices;
  int    [] parameterSubspaceParameterIndices;

  public ParameterSpaceImpl () {
  }

  public ParameterSpace createFromTable (Table table) {

    this.numSubspaces = 1;
    this.subspaceTables = new Table[this.numSubspaces];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[this.numSubspaces];
    this.subspaceSizes[0] = table.getNumColumns();

    int numParameters = this.subspaceSizes[0];



    return (ParameterSpace) this;

  }

  /**
   * Get the number of parameters that define the space.
   * @return an int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters() {
    return numParameters;
  }

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a string value representing the name of the parameter.
   */
  public String getName(int parameterIndex) {
    return parameterNames[parameterIndex];
  }

  /**
   * Get the parameter index of that corresponds to the given name.
   * @return an integer representing the index of the parameters.
   */
  public int getParameterIndex(String name) {
    return -1;
  }

  /**
   * Get the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMinValue(int parameterIndex) {
    return parameterMinValues[parameterIndex];
  }

  /**
   * Get the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMaxValue(int parameterIndex) {
    return parameterMaxValues[parameterIndex];
  }

  /**
   * Get the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getDefaultValue(int parameterIndex) {
    return parameterDefaultValues[parameterIndex];
  }

  /**
   * Set the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   */
  public void setMinValue(int parameterIndex, double value) {
  }

  /**
   * Set the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setMaxValue(int parameterIndex, double value){
  }

  /**
   * Set the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setDefaultValue(int parameterIndex, double value){
  }

  /**
   * Get the minimum values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the minimum possible values of all parameters.
   */
  public ParameterPoint getMinParameterPoint() {
    return null;
  }

  /**
   * Get the maximum values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the maximum possible values of all parameters.
   */
  public ParameterPoint getMaxParameterPoint(){
    return null;
  }

  /**
   * Get the default values of all parameters returned as a ParamterPoint.
   * @param parameterIndex the index of the parameter of interest.
   * @return A ParamterPoint representing the default values of all parameters.
   */
  public ParameterPoint getDefaultParameterPoint(){
    return null;
  }

  /**
   * Get the type of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getType(int parameterIndex) {
    return resolutions[parameterIndex];
  }

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex) {
    return resolutions[parameterIndex];
  }

  /**
   * Get the subspace index of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the subpace index number of parameter.
   */
  public int getSubspaceIndex(int parameterIndex) {
    return parameterSubspaceIndices[parameterIndex];
  }

  /**
   * Get the subspace parameter index of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the subpace index number of parameter.
   */
  public int getSubspaceParameterIndex(int parameterIndex) {
    return parameterSubspaceParameterIndices[parameterIndex];
  }

  /**
   * Get the number of subspaces that defines the space.
   * @return a int value representing the number subspaces that define the space.
   */
  public int getNumSubspaces() {
    return numSubspaces;
  }

  /**
   * Get the number of parameters in each subspace.
   * @return a int array of values the number of parameters defining each subspace.
   */
  public int [] getSubspaceNumParameters() {
    return subspaceNumParameters;
  }

  /**
   * Get a subspace from the space.
   * @param subspaceIndex the index of the subspace of interest.
   * @return a ParameterSpace which defines the indicated subspace.
   */
  public ParameterSpace getSubspace(int subspaceIndex) {
    //!!!
    return null;
  }

  /**
   * Join two ParameterSpaces to produce a single parameter space.
   * @param firstSpace the first of the two ParameterSpaces to join.
   * @param secondSpace the second of the two ParameterSpaces to join.
   * @return a ParameterSpace which defines the indicated subspace.
   */
  public ParameterSpace joinSubspaces(ParameterSpace firstSpace, ParameterSpace secondSpace) {
    //!!!
    return null;
  }

  /**
   * Split a ParameterSpace into two parameter spaces.
   * @return an array of two ParameterSpaces which define the two subspaces, the first being the head and the second being the tail.
   */
  public ParameterSpace [] splitSubspaces(ParameterSpace space) {
    //!!!
    return null;
  }

}