package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class ParameterSpaceImpl extends ContinuousExampleSet implements ParameterSpace, ExampleTable, java.io.Serializable {

  int       numParameters;
  int       numSubspaces;
  MutableTable  [] subspaceTables;
  int    [] subspaceSizes;
  int    [] subspaceNumParameters;
  String [] parameterNames;
  int    [] parameterSubspaceIndices;
  int    [] parameterSubspaceParameterIndices;

  public ParameterSpaceImpl () {
  }

  /**
   * Instantiate a ParameterSpace from the information in the given table.
   * Each column in the table represents a paramter.
   * Row 1 is the minimum parameter value.
   * Row 2 is the maximum parameter value.
   * Row 3 is the default parameter setting.
   * Row 4 is the parameter resolution in terms of number of intervals.
   * Row 5 is the type as an integer as defined in ColumnTypes.
   * @param table the table representing the parameter space.
   * @return a ParameterSpace.
   */
  public ParameterSpace createFromTable(MutableTable table) {

    this.numParameters = table.getNumColumns();
    this.numSubspaces = 1;
    this.subspaceTables = new MutableTable[this.numSubspaces];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[this.numSubspaces];
    this.subspaceSizes[0] = table.getNumColumns();


    return (ParameterSpace) this;

  }


  /**
   * Instantiate a ParameterSpace from primative data types.
   * @param names the names of the paramters.
   * @param minValues the minimum parameter values.
   * @param maxValues the maximum parameter values.
   * @param defaultValues the default parameter settings.
   * @param resolutions the parameter resolutions in terms of number of intervals.
   * @param types the type as an integer as defined in ColumnTypes.
   * @return a ParameterSpace.
   */
  public ParameterSpace createFromData (
      String [] names,
      double [] minValues,
    double [] maxValues,
    double [] defaultValues,
    int    [] resolutions,
    int    [] types) {

    int numParamters = names.length;
    int numRows      = 5;

    int numValues = numRows * numParamters;

    double [] data = new double[numValues];

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
        switch (rowIndex) {
        case 0:
          data[rowIndex * numParamters + parameterIndex] = minValues[parameterIndex];
          break;
        case 1:
          data[rowIndex * numParamters + parameterIndex] = maxValues[parameterIndex];
          break;
        case 2:
          data[rowIndex * numParamters + parameterIndex] = defaultValues[parameterIndex];
          break;
        case 3:
          data[rowIndex * numParamters + parameterIndex] = resolutions[parameterIndex];
          break;
        case 4:
          data[rowIndex * numParamters + parameterIndex] = types[parameterIndex];
          break;
      }
      }
    }

    super.initialize(data, numRows, numParameters, 0, names, null);

    return this;
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
  public int getParameterIndex(String name) throws Exception {
    return -1;
  }

  /**
   * Get the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMinValue(int parameterIndex) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 0;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    return subspaceTables[tableIndex].getDouble(rowIndex, colIndex);
  }

  /**
   * Get the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getMaxValue(int parameterIndex) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 1;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    return subspaceTables[tableIndex].getDouble(rowIndex, colIndex);
  }

  /**
   * Get the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getDefaultValue(int parameterIndex) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 2;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    return subspaceTables[tableIndex].getDouble(rowIndex, colIndex);
  }

  /**
   * Get the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getResolution(int parameterIndex) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 3;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    return subspaceTables[tableIndex].getInt(rowIndex, colIndex);
  }

  /**
   * Get the type of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a int value representing the number of intervals between the min and max parameter values.
   */
  public int getType(int parameterIndex) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 4;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    return subspaceTables[tableIndex].getInt(rowIndex, colIndex);
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
   * Set the minimum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   */
  public void setMinValue(int parameterIndex, double value) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 0;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    subspaceTables[tableIndex].setDouble(value, rowIndex, colIndex);
  }

  /**
   * Set the maximum value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setMaxValue(int parameterIndex, double value){
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 1;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    subspaceTables[tableIndex].setDouble(value, rowIndex, colIndex);
  }

  /**
   * Set the default value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the value of the parameter of interest.
   */
  public void setDefaultValue(int parameterIndex, double value) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 2;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    subspaceTables[tableIndex].setDouble(value, rowIndex, colIndex);
  }

  /**
   * Set the resolution of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the resolution.
   */
  public void setResolution(int parameterIndex, int resolution) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 3;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    subspaceTables[tableIndex].setInt(resolution, rowIndex, colIndex);
  }

  /**
   * Set the type of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @param value the type as defined in ColumnTypes().
   */
  public void setType(int parameterIndex, int type) {
    int tableIndex = parameterSubspaceIndices[parameterIndex];
    int rowIndex   = 4;
    int colIndex   = parameterSubspaceParameterIndices[parameterIndex];
    subspaceTables[tableIndex].setInt(type, rowIndex, colIndex);
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