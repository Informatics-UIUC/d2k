package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class ParameterSpaceImpl extends ContinuousExampleSet implements ParameterSpace, ExampleTable, java.io.Serializable {


  final static int minValueRowIndex     = 0;
  final static int maxValueRowIndex     = 1;
  final static int defaultValueRowIndex = 2;
  final static int resolutionRowIndex   = 3;
  final static int typeRowIndex         = 4;
  int    numSubspaces = 0;
  int    [] subspaceNumParameters;

  //public ParameterSpaceImpl () {
  //}

  public ParameterSpace createFromTable(MutableTable table) {


    int       numParameters = table.getNumColumns();
    String [] names         = new String[numParameters];
    double [] minValues     = new double[numParameters];
    double [] maxValues     = new double[numParameters];
    double [] defaultValues = new double[numParameters];
    int    [] resolutions   = new int   [numParameters];
    int    [] types         = new int   [numParameters];

    for (int i = 0; i < numParameters; i++) {
      names[i] = table.getColumnLabel(i);
      minValues    [i] = table.getDouble(minValueRowIndex,     i);
      maxValues    [i] = table.getDouble(maxValueRowIndex,     i);
      defaultValues[i] = table.getDouble(defaultValueRowIndex, i);
      resolutions  [i] = table.getInt   (resolutionRowIndex,   i);
      types        [i] = table.getInt   (typeRowIndex,         i);
    }

    return createFromData(names, minValues, maxValues, defaultValues, resolutions, types);

/*
      String [] names,
      double [] minValues,
    double [] maxValues,
    double [] defaultValues,
    int    [] resolutions,
    int    [] types) {


    this.numParameters = table.getNumColumns();
    this.numSubspaces = 1;
    this.subspaceTables = new MutableTable[this.numSubspaces];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[this.numSubspaces];
    this.subspaceSizes[0] = table.getNumColumns();


    return (ParameterSpace) this;
*/

  }


  public ParameterSpace createFromData (String [] names, double [] minValues, double [] maxValues, double [] defaultValues, int [] resolutions, int [] types) {

    int numParameters = names.length;
    int numRows      = 5;

    int numValues = numRows * numParameters;

    double [] data = new double[numValues];

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
        switch (rowIndex) {
        case 0:
          data[rowIndex * numParameters + parameterIndex] = minValues[parameterIndex];
          break;
        case 1:
          data[rowIndex * numParameters + parameterIndex] = maxValues[parameterIndex];
          break;
        case 2:
          data[rowIndex * numParameters + parameterIndex] = defaultValues[parameterIndex];
          break;
        case 3:
          data[rowIndex * numParameters + parameterIndex] = resolutions[parameterIndex];
          break;
        case 4:
          data[rowIndex * numParameters + parameterIndex] = types[parameterIndex];
          break;
      }
      }
    }

    super.initialize(data, numRows, numParameters, 0, names, null);

    this.numSubspaces = 1;
    this.subspaceNumParameters = new int[1];
    this.subspaceNumParameters[0] = numParameters;

    return this;
  }

  public int getNumParameters() {
    return this.getNumColumns();
  }

  public String getName(int parameterIndex) {
    return  getColumnLabel(parameterIndex);
  }

  public int getParameterIndex(String name) throws Exception {

    for (int i = 0; i < getNumParameters(); i++) {
      if (getName(i).equals(name))
        return i;
    }
    Exception e = new Exception();
    System.out.println("Error!  Can not find name (" + name + ").  ");
    throw e;
  }


  public double getMinValue(int parameterIndex) {
    return getDouble(minValueRowIndex, parameterIndex);
  }

  public double getMaxValue(int parameterIndex) {
    return getDouble(maxValueRowIndex, parameterIndex);
  }

  public double getDefaultValue(int parameterIndex) {
    return getDouble(defaultValueRowIndex, parameterIndex);
  }


  public int getResolution(int parameterIndex) {
    return getInt(resolutionRowIndex, parameterIndex);
  }

  public int getType(int parameterIndex) {
    return getInt(typeRowIndex, parameterIndex);
  }

  public ParameterPoint getMinParameterPoint() {
    return null;
  }

  public ParameterPoint getMaxParameterPoint(){
    return null;
  }

  public ParameterPoint getDefaultParameterPoint(){
    return null;
  }

  public int getSubspaceIndex(int parameterIndex) throws Exception {
    int count = parameterIndex;
    for (int i = 0; i < numSubspaces; i++) {
      parameterIndex -= subspaceNumParameters[i];
      if (parameterIndex < 0)
        return i;
    }
    System.out.println("Error!  parameterIndex (" + parameterIndex + ") invalid.  ");
    throw new Exception();
  }

  public int getSubspaceParameterIndex(int parameterIndex) throws Exception {
    int lastParameterIndex = parameterIndex;
    for (int i = 0; i < numSubspaces; i++) {
      parameterIndex -= subspaceNumParameters[i];
      if (parameterIndex < 0)
        return lastParameterIndex;
      lastParameterIndex = parameterIndex;
    }
    System.out.println("Error!  parameterIndex (" + parameterIndex + ") invalid.  ");
    throw new Exception();
  }

  public int getNumSubspaces() {
    return numSubspaces;
  }

  public int [] getSubspaceNumParameters() {
    return subspaceNumParameters;
  }

  public ParameterSpace getSubspace(int subspaceIndex) {
    //!!!
    return null;
  }

  public void setMinValue(int parameterIndex, double value) {
    this.setDouble(value, minValueRowIndex, parameterIndex);
  }

  public void setMaxValue(int parameterIndex, double value){
    this.setDouble(value, maxValueRowIndex, parameterIndex);
  }

  public void setDefaultValue(int parameterIndex, double value) {
    this.setDouble(value, defaultValueRowIndex, parameterIndex);
  }

  public void setResolution(int parameterIndex, int value) {
    this.setInt(value, resolutionRowIndex, parameterIndex);
  }

  public void setType(int parameterIndex, int value) {
    this.setInt(value, typeRowIndex, parameterIndex);
  }

  public ParameterSpace joinSubspaces(ParameterSpace firstSpace, ParameterSpace secondSpace) {
    //!!!
    return null;
  }

  public ParameterSpace [] segmentSpace(ParameterSpace point, int splitIndex) {

    int numParameters = point.getNumParameters();
    int [] headCols = new int[splitIndex];
    int [] tailCols = new int[numParameters - splitIndex];


    ParameterSpace headSpace = (ParameterSpace) getSubsetByColumnsReference(headCols);
    ParameterSpace tailSpace = (ParameterSpace) getSubsetByColumnsReference(tailCols);

    ParameterSpace [] spaces = new ParameterSpace[2];

    spaces[0] = headSpace;
    spaces[1] = tailSpace;

    return spaces;
  }

}