package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ParameterSpaceImpl extends FloatExampleSet implements ExampleTable, java.io.Serializable {

  Table [] subspaceTables;
  int numSubspaces;
  int [] subspaceSizes;
  int [] subspaceNumParameters;
  int numParameters;
  String [] parameterNames;
  double [] parameterMinValues;
  double [] parameterMaxValues;
  double [] parameterDefaultMinValues;
  double [] parameterDefaultMaxValues;
  double [] parameterDefaultValues;
  double [] parameterFactoryDefaultMinValues;
  double [] parameterFactoryDefaultMaxValues;
  double [] parameterFactoryDefaultValues;
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
  public String getParameterName(int parameterIndex) {
    return parameterNames[parameterIndex];
  }

  /**
   * Get the names of all the parameters.
   * @return an array of string values representing the names of the all the parameters.
   */
  public String [] getParameterNames() {
    return parameterNames;
  }

  /**
   * Get the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMinParameterValue(int parameterIndex) {
    return parameterMinValues[parameterIndex];
  }

  /**
   * Get the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMaxParameterValue(int parameterIndex) {
    return parameterMaxValues[parameterIndex];
  }

  /**
   * Get the minimum default value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory minimum parameter setting.
   */
  public double getFactoryDefaultMinParameterValue(int parameterIndex) {
    return parameterFactoryDefaultMinValues[parameterIndex];
  }

  /**
   * Get the maximum default value of a parameter based on factory settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the factory maximum parameter setting.
   */
  public double getFactoryDefaultMaxParameterValue(int parameterIndex) {
    return parameterFactoryDefaultMaxValues[parameterIndex];
  }

  /**
   * Get the minimum value of a parameter based on default settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the default minimum parameter setting.
   */
  public double getDefaultMinParameterValue(int parameterIndex) {
    return parameterDefaultMinValues[parameterIndex];
  }

  /**
   * Get the maximum value of a parameter based on default settings.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the default maximum parameter setting.
   */
  public double getDefaultMaxParameterValue(int parameterIndex) {
    return parameterDefaultMaxValues[parameterIndex];
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