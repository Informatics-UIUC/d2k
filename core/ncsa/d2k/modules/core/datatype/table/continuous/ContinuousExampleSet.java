package ncsa.d2k.modules.core.datatype.table.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.TransformationModule;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ContinuousExampleSet implements ExampleTable, java.io.Serializable {

  int numExamples;
  int numFeatures;
  String [] names;
  int       numInputs;
  int       numOutputs;
  //String [] inputNames;
  //String [] outputNames;
  int    [] inputIndices;
  int    [] outputIndices;
  int    [] exampleIndices;
  int    [] testingSet;

  double data[];

  public ContinuousExampleSet () {
  }

  void initialize(double [] data, int numExamples, int numInputs, int numOutputs, String [] inputNames, String [] outputNames) {

    this.numExamples = numExamples;
    this.numFeatures = numInputs + numOutputs;

    this.names = new String[this.numFeatures];
    for (int i = 0; i < numInputs; i++) {
      this.names[i] = inputNames[i];
    }
    for (int i = 0; i < numOutputs; i++) {
      this.names[numInputs + i] = outputNames[i];
    }
    this.numInputs   = numInputs;
    this.numOutputs  = numOutputs;

    //this.inputNames   = inputNames;
    //this.outputNames  = outputNames;

    this.inputIndices = new int[numInputs];
    for (int i = 0; i < numInputs; i++) {
      this.inputIndices[i] = i;
    }
    this.outputIndices = new int[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      this.outputIndices[i] = numInputs + i;
    }

    this.exampleIndices = new int[numExamples];
    for (int i = 0; i < numExamples; i++)
      this.exampleIndices[i] = i;

    this.data = data;
  }



  public ContinuousExampleSet (double [] data, int numExamples, int numInputs, int numOutputs, String [] inputNames, String [] outputNames) {

    initialize(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }


  public ContinuousExampleSet (int numExamples, int numInputs, int numOutputs, String [] inputNames, String [] outputNames) {
    double [] data = new double[numExamples * (numInputs + numOutputs)];
    initialize(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }



public ContinuousExampleSet (double [][][] data, String [] inputNames, String [] outputNames) {

    int numExamples = data.length;
    int numInputs   = inputNames.length;
    int numOutputs  = outputNames.length;
    int numFeatures = numInputs + numOutputs;

    double [] values = new double[numExamples * numFeatures];

    for (int e = 0; e < numExamples; e++) {
      for (int i = 0; i < numInputs; i++) {
        values[e * numFeatures             + i] = data[e][0][i];
      }
      for (int i = 0; i < numOutputs; i++) {
        values[e * numFeatures + numInputs + i] = data[e][1][i];
      }
    }

    initialize(values, numExamples, numInputs, numOutputs, inputNames, outputNames);


  }

  public Table copy() {
    ContinuousExampleSet copy   = new ContinuousExampleSet();

    copy.numExamples        = this.numExamples;
    copy.numFeatures        = this.numFeatures;
    copy.names              = (String []) this.names.clone();
    copy.numInputs          = this.numInputs;
    copy.numOutputs         = this.numOutputs;
    //copy.inputNames         = (String []) this.inputNames.clone();
    //copy.outputNames        = (String []) this.outputNames.clone();
    copy.inputIndices       = (int    []) this.inputIndices.clone();
    copy.outputIndices      = (int    []) this.outputIndices.clone();
    copy.exampleIndices     = (int    []) this.exampleIndices.clone();
    if (testingSet != null)
      copy.testingSet       = (int    []) this.testingSet.clone();
    copy.data               = (double []) this.data.clone();
    return (Table) copy;
  }


  public double getInputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public double getOutputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + outputIndices[i]];
  }


  public String getInputString(int e, int i) {
    return Double.toString(data[exampleIndices[e] * numFeatures + inputIndices[i]]);
  }

  public String getOutputString(int e, int i) {
    return Double.toString(data[exampleIndices[e] * numFeatures + outputIndices[i]]);
  }

  public int getInputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public int getOutputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures  + outputIndices[i]];
  }

  public float getInputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public float getOutputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + outputIndices[i]];
  }

  public short getInputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public short getOutputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures  + outputIndices[i]];
  }

  public long getInputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public long getOutputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures  + outputIndices[i]];
  }

  public byte getInputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public byte getOutputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures + outputIndices[i]];
  }

  public Object getInputObject(int e, int i) {
    return (Object) new Double(data[exampleIndices[e] * numFeatures + inputIndices[i]]);
  }

  public Object getOutputObject(int e, int i)
  {
    return (Object) new Double(data[exampleIndices[e] * numFeatures + outputIndices[i]]);
  }

  public char getInputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures + inputIndices[i]];
  }

  public char getOutputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures + outputIndices[i]];
  }

  public byte[] getInputBytes(int e, int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures + inputIndices[i]];
    return bytes;
  }

  public byte[] getOutputBytes(int e, int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures + outputIndices[i]];
    return bytes;
  }

  public char[] getInputChars(int e, int i) {
    char [] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures + inputIndices[i]];
    return chars;
  }

  public char[] getOutputChars(int e, int i) {
    char [] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures + outputIndices[i]];
    return chars;
  }

  public boolean getInputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures + inputIndices[i]] < 0.5)
      return false;
    else
      return true;
  }

  public boolean getOutputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures + outputIndices[i]] < 0.5)
      return false;
    else
      return true;
  }

  public int getNumInputs(int e) {
    return this.numInputs;
  }

  public int getNumOutputs(int e) {
    return this.numOutputs;
  }

  public Example getExample(int e) {
    return (Example) new ContinuousExample(this, e);
  }


  public String getInputName(int i) {
    return this.names[inputIndices[i]];
  }
  public String [] getInputNames() {
    String [] names = new String[this.numInputs];
    for (int i = 0; i < this.numInputs; i++) {
      names[i] = getInputName(i);
    }
    return names;
  }

  public String getOutputName(int i) {
    return this.names[outputIndices[i]];
  }
  public String [] getOutputNames() {
    String [] names = new String[this.numOutputs];
    for (int i = 0; i < this.numOutputs; i++) {
      names[i] = getOutputName(i);
    }
    return names;
  }


  public int getInputType(int i) {
    return ColumnTypes.DOUBLE;
  }

  public int getOutputType(int i) {
    return ColumnTypes.DOUBLE;
  }

  public boolean isInputNominal(int i) {
    return false;
  }

  public boolean isOutputNominal(int i) {
    return false;
  }

  public boolean isInputScalar(int i) {
    return true;
  }

  public boolean isOutputScalar(int i) {
    return true;
  }




  public void setExample(int e1, ContinuousExampleSet exampleSet, int e2) {
    for (int i = 0; i < numFeatures; i++) {
      data[exampleIndices[e1] * numFeatures + i] = data[exampleIndices[e2] * numFeatures + i];
    }
  }

  public void setInput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + inputIndices[i]] = (float) value;
  }

  public void setOutput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + outputIndices[i]] = (float) value;
  }

/*
  public void deleteInputs(boolean [] deleteFeatures)
  {
  }
*/

  public Object getObject(int row, int column) {
    return null;
  }

  public int getInt(int row, int column) {
    return (int) data[exampleIndices[row] * numFeatures + column];
  }

  public short getShort(int row, int column) {
    return (short) data[exampleIndices[row] * numFeatures + column];
  }

  public float getFloat(int row, int column) {
    return (float) data[exampleIndices[row] * numFeatures + column];
  }

  public double getDouble(int row, int column) {
    return (double) data[exampleIndices[row] * numFeatures + column];
  }


  public long getLong(int row, int column) {
    return (long) data[exampleIndices[row] * numFeatures + column];
  }

  public String getString(int row, int column) {
    return Double.toString(data[exampleIndices[row] * numFeatures + column]);
  }

  public byte[] getBytes(int row, int column) {
    return null;
  }

  public boolean getBoolean(int row, int column) {
    double value = data[exampleIndices[row] * numFeatures + column];
    if (value < 0.5)
      return false;
    else
      return true;
  }

  public char[] getChars(int row, int column) {
    return null;
  }

  public byte getByte(int row, int column) {
    return (byte) data[exampleIndices[row] * numFeatures + column];
  }

  public char getChar(int row, int column) {
    return (char) data[exampleIndices[row] * numFeatures + column];
  }

  public int getKeyColumn() {
    return 0;
  }

  public void setKeyColumn(int position) {
  }

  public String getColumnLabel(int position)
  {
    String label = null;
    if (position < numInputs)
      label = getInputName(position);
    else
      label = getOutputName(position - numInputs);
    return label;
  }
  public String getColumnComment(int position) {
    return null;
  }
  public String getLabel() {
    return null;
  }
  public void setLabel(String labl) {
  }
  public String getComment() {
    return null;
  }
  public void setComment(String comment) {
  }
  public int getNumRows() {
    return numExamples;
  }
  public int getNumEntries() {
    return numExamples;
  }
  public int getNumColumns() {
    return numFeatures;
  }
  public void getRow (Object buffer, int pos) {
    if(buffer instanceof int[]) {
      int[] b1 = (int[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getInt(pos, i);
    }
    else if(buffer instanceof float[]) {
      float[] b1 = (float[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getFloat(pos, i);
    }
    else if(buffer instanceof double[]) {
      double[] b1 = (double[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getDouble(pos, i);
    }
    else if(buffer instanceof long[]) {
      long[] b1 = (long[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getLong(pos, i);
    }
    else if(buffer instanceof short[]) {
      short[] b1 = (short[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getShort(pos, i);
    }
    else if(buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getBoolean(pos, i);
    }
    else if(buffer instanceof String[]) {
      String[] b1 = (String[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getString(pos, i);
    }
    else if(buffer instanceof char[][]) {
      char[][] b1 = (char[][])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getChars(pos, i);
    }
    else if(buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getBytes(pos, i);
    }
    else if(buffer instanceof Object[]) {
      Object[] b1 = (Object[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getObject(pos, i);
    }
    else if(buffer instanceof byte[]) {
      byte[] b1 = (byte[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getByte(pos, i);
    }
    else if(buffer instanceof char[]) {
      char[] b1 = (char[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getChar(pos, i);
    }
  }

  public void getColumn (Object buffer, int pos) {
    if(buffer instanceof int[]) {
      int[] b1 = (int[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getInt(i, pos);
    }
    else if(buffer instanceof float[]) {
      float[] b1 = (float[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getFloat(i, pos);
    }
    else if(buffer instanceof double[]) {
      double[] b1 = (double[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getDouble(i, pos);
    }
    else if(buffer instanceof long[]) {
      long[] b1 = (long[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getLong(i, pos);
    }
    else if(buffer instanceof short[]) {
      short[] b1 = (short[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getShort(i, pos);
    }
    else if(buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getBoolean(i, pos);
    }
    else if(buffer instanceof String[]) {
      String[] b1 = (String[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getString(i, pos);
    }
    else if(buffer instanceof char[][]) {
      char[][] b1 = (char[][])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getChars(i, pos);
    }
    else if(buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getBytes(i, pos);
    }
    else if(buffer instanceof Object[]) {
      Object[] b1 = (Object[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getObject(i, pos);
    }
    else if(buffer instanceof byte[]) {
      byte[] b1 = (byte[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getByte(i, pos);
    }
    else if(buffer instanceof char[]) {
      char[] b1 = (char[])buffer;
      for(int i = 0; i < b1.length; i++)
        b1[i] = getChar(i, pos);
    }
  }

  public Table getSubset(int start, int len) {
    return null;
  }
  public Table getSubsetByReference(int start, int len) {
    return null;
  }
  public Table getSubsetByReference(int rows[]){
    return null;
  }

  public TableFactory getTableFactory() {
    return null;
  }

  public boolean isColumnNominal(int position) {
    return false;
  }

  public boolean isColumnScalar(int position) {
    return true;
  }

  public void setColumnIsNominal(boolean value, int position) {
  }

  public void setColumnIsScalar(boolean value, int position) {
  }
  public boolean isColumnNumeric(int position) {
    return true;
  }

  public int getColumnType(int position) {
    return 0;
  }

  public ExampleTable toExampleTable() {
    return (ExampleTable) this;
  }

  public void addTransformation (TransformationModule tm) {
  }

  public List getTransformations () {
    return null;
  }

  public int[] getInputFeatures () {
    int [] inputFeatures = new int[numInputs];
    for (int i = 0; i < numInputs; i++) {
      inputFeatures[i] = i;
    }
    return inputFeatures;
  }

  public int getNumInputFeatures () {
    return numInputs;
  }

  public int getNumExamples () {
    return numExamples;
  }

  public int getNumTrainExamples () {
    return 0;
  }

  public int getNumTestExamples () {
    return 0;
  }

  public int[] getOutputFeatures () {
    int [] outputFeatures = new int[numOutputs];
    for (int i = 0; i < numOutputs; i++)
    {
      outputFeatures[i] = i + numInputs;
    }
    return outputFeatures;
  }

  public int getNumOutputFeatures () {
    return numOutputs;
  }

  public void setInputFeatures (int[] inputs) {
  }

  public void setOutputFeatures (int[] outs) {
  }

  public void setTrainingSet (int[] trainingSet) {
  }

  public int[] getTrainingSet () {
    return null;
  }

  public void setTestingSet (int[] testingSet) {
    this.testingSet = testingSet;
  }

  public int[] getTestingSet () {
    return testingSet;
  }


  public ExampleTable getExampleTable() {
    return (ExampleTable) null;
  }

  public TestTable getTestTable () {

    ContinuousExampleSet table = (ContinuousExampleSet) this.copy();

    int [] newExampleIndices = new int[testingSet.length];

    for (int i = 0; i < testingSet.length; i++) {
      newExampleIndices[i] = exampleIndices[testingSet[i]];
    }

    table.exampleIndices = newExampleIndices;
    table.numExamples    = testingSet.length;

    return (TestTable) table;
  }

  public TrainTable getTrainTable () {
    return null;
  }

  public PredictionTable toPredictionTable() {
    return null;
  }


  public int[] getPredictionSet () {
    return null;
  }

  public void setPredictionSet (int[] p) {
  }

  public void setIntPrediction(int prediction, int row, int predictionColIdx) {
  }


  public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
  }


  public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
  }

  public void setLongPrediction(long prediction, int row, int predictionColIdx) {
  }

  public void setShortPrediction(short prediction, int row, int predictionColIdx) {
  }


  public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
  }

  public void setStringPrediction(String prediction, int row, int predictionColIdx) {
  }

  public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
  }

  public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
  }

  public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
  }

  public void setBytePrediction(byte prediction, int row, int predictionColIdx)
  {
  }
  public void setCharPrediction(char prediction, int row, int predictionColIdx)
  {
  }
  public int getIntPrediction(int row, int predictionColIdx) {
    return 0;
  }
  public float getFloatPrediction(int row, int predictionColIdx) {
    return 0;
  }

  public double getDoublePrediction(int row, int predictionColIdx) {
    return 0;
  }

  public long getLongPrediction(int row, int predictionColIdx) {
    return 0;
  }

  public short getShortPrediction(int row, int predictionColIdx) {
    return 0;
  }

  public boolean getBooleanPrediction(int row, int predictionColIdx) {
    return false;
  }

  public String getStringPrediction(int row, int predictionColIdx) {
    return null;
  }

  public char[] getCharsPrediction(int row, int predictionColIdx) {
    return null;
  }

  public byte[] getBytesPrediction(int row, int predictionColIdx) {
    return null;
  }

  public Object getObjectPrediction(int row, int predictionColIdx) {
    return null;
  }

  public byte getBytePrediction(int row, int predictionColIdx) {
    return 0;
  }

  public char getCharPrediction(int row, int predictionColIdx) {
    return 0;
  }

  public int addPredictionColumn(int[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(float[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(double[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(long[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(short[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(boolean[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(String[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(char[][] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(byte[][] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(Object[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(byte[] predictions, String label) {
    return 0;
  }

  public int addPredictionColumn(char[] predictions, String label) {
    return 0;
  }

  public boolean isValueMissing(int row, int col) {
    return false;
  }

  public boolean isValueEmpty(int row, int col) {
    return false;
  }

  public Number getScalarMissingValue(int col) {
    return null;
  }

  public String getNominalMissingValue(int col) {
    return null;
  }

  public Number getScalarEmptyValue(int col) {
    return null;
  }

  public String getNominalEmptyValue(int col) {
    return null;
  }

  public void deleteInputs(boolean [] deleteFeatures) {
    int numFeaturesToDelete = 0;
    for (int i = 0; i < numInputs; i++) {
      if (deleteFeatures[i])
        numFeaturesToDelete++;
    }

    int newNumInputs = numInputs - numFeaturesToDelete;
    int newNumFeatures = newNumInputs + numOutputs;
    int newIndex;


    double [] newData = new double[numExamples * (newNumInputs + numOutputs)];

    for (int e = 0; e < numExamples; e++) {
      newIndex = 0;
      for (int i = 0; i < numInputs; i++) {
        if (!deleteFeatures[i])
          newData[e * newNumFeatures + (newIndex++)] = data[e * numFeatures + (newIndex++)];
      }
      for (int i = 0; i < numOutputs; i++) {
        newData[e * newNumFeatures + newNumInputs + i] = data[e * numFeatures + numInputs + i];
      }

    }
    this.data = newData;
    this.numInputs = newNumInputs;
    this.numFeatures = newNumFeatures;
  }

  public void setExample(int e1, ExampleTable exampleSet, int e2) {
    exampleIndices[e1] = ((ContinuousExampleSet) exampleSet).exampleIndices[e2];
      /*
      for (int i = 0; i < numInputs; i++)
      {
        this.setInput(e1, i, exampleSet.getInputFloat(e2, i));
      }
      for (int i = 0; i < numOutputs; i++)
      {
        this.setOutput(e1, i, exampleSet.getOutputFloat(e2, i));
      }
      */
  }

  public boolean scalarInput(int index) {
    return true;
  }






















  /**
   * Add a row to the end of this Table, initialized with integer data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(int[] newEntry) {
  }

  /**
   * Add a row to the end of this Table, initialized with float data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(float[] newEntry){
  }
  /**
   * Add a row to the end of this Table, initialized with double data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(double[] newEntry){

    int newNumExamples = numExamples + 1;
    int [] newExampleIndices = new int[newNumExamples];
    System.arraycopy(this.exampleIndices, 0, newExampleIndices, 0, this.numExamples);
    newExampleIndices[this.exampleIndices.length] = this.numExamples;

    double [] newData = new double[newNumExamples * this.numFeatures];
    System.arraycopy(this.data, 0, newData, 0, this.numExamples * this.numFeatures);
    System.arraycopy(newEntry, 0, newData, this.numExamples * this.numFeatures, this.numFeatures);

    this.numExamples++;



  }

  /**
   * Add a row to the end of this Table, initialized with long data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(long[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with short data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(short[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with boolean data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(boolean[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with String data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(String[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with char[] data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(char[][] newEntry) {
  }

  /**
   * Add a row to the end of this Table, initialized with byte[] data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(byte[][] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with Object[] data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(Object[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with byte data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(byte[] newEntry){
  }

  /**
   * Add a row to the end of this Table, initialized with char data.
   * @param newEntry the data to put into the new row.
   */
  public void addRow(char[] newEntry){
  }

  /**
   * Insert a new row into this Table, initialized with integer data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(int[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with float data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(float[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with double data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(double[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with long data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(long[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with short data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(short[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with boolean data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(boolean[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with String data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(String[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with char[] data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(char[][] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with byte[] data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(byte[][] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with Object data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(Object[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with byte data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(byte[] newEntry, int position){
  }

  /**
   * Insert a new row into this Table, initialized with char data.
   * @param newEntry the data to put into the inserted row.
   * @param position the position to insert the new row
   */
  public void insertRow(char[] newEntry, int position){
  }

  /**
   * Add a new column to the end of this table, initialized with integer data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(int[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with float data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(float[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with double data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(double[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with long data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(long[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with short data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(short[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with boolean data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(boolean[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with String data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(String[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with char[] data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(char[][] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with byte[] data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(byte[][] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with Object data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(Object[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with byte data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(byte[] newEntry) {
  }

  /**
   * Add a new column to the end of this table, initialized with char data.
   * @param newEntry the data to initialize the column with.
   */
  public void addColumn(char[] newEntry) {
  }

  /**
   * Insert a new column into this Table, initialized with integer data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(int[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with float data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(float[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with double data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(double[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with long data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(long[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with short data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(short[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with boolean data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(boolean[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with String data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(String[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with char[] data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(char[][] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with byte[] data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(byte[][] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with Object data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(Object[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with byte data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(byte[] newEntry, int position) {
  }

  /**
   * Insert a new column into this Table, initialized with char data.
   * @param newEntry the initial values of the new column.
   * @param position the position to insert the new row
   */
  public void insertColumn(char[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of int data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(int[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of float data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(float[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of double data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(double[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of long data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(long[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of short data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(short[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of boolean data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(boolean[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of String data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(String[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of char[] data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(char[][] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of byte[] data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(byte[][] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of Object data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(Object[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of byte data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(byte[] newEntry, int position) {
  }

  /**
   * Set the row at the given position to an array of char data.
   *	@param newEntry the new values of the row.
   *	@param position the position to set
   */
  public void setRow(char[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of int data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(int[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of float data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(float[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of double data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(double[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of long data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(long[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of short data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(short[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of boolean data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(boolean[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of String data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(String[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of char data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(char[][] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of byte[] data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(byte[][] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of Object data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(Object[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of byte data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(byte[] newEntry, int position) {
  }

  /**
   * Set the column at the given position to an array of char data.
   *	@param newEntry the new values of the column.
   *	@param position the position to set
   */
  public void setColumn(char[] newEntry, int position) {
  }

  /**
  Remove a column from the table.
  @param position the position of the Column to remove
  */
  public void removeColumn(int position) {
  }

  /**
  Remove a range of columns from the table.
  @param start the start position of the range to remove
  @param len the number to remove-the length of the range
  */
  public void removeColumns(int start, int len) {
  }

  /**
   * Remove a row from this Table.
   * @param row the row to remove
   */
  public void removeRow(int row) {
  }

  /**
  Remove a range of rows from the table.
  @param start the start position of the range to remove
  @param len the number to remove-the length of the range
  */
  public void removeRows(int start, int len) {
  }

  /**
   * Remove rows from this Table using a boolean map.
   * @param flags an array of booleans to map to this Table's rows.  Those
   * with a true will be removed, all others will not be removed
   */
  public void removeRowsByFlag(boolean[] flags) {
  }

  /**
   * Remove rows from this Table using a boolean map.
   * @param flags an array of booleans to map to this Table's rows.  Those
   * with a true will be removed, all others will not be removed
   */
  public void removeColumnsByFlag(boolean[] flags) {
  }

  /**
   * Remove rows from this Table by index.
   * @param indices a list of the rows to remove
   */
  public void removeRowsByIndex(int[] indices) {
  }

  /**
   * Remove rows from this Table by index.
   * @param indices a list of the rows to remove
   */
  public void removeColumnsByIndex(int[] indices) {
  }

  /**
  Get a copy of this Table reordered based on the input array of indexes.
  Does not overwrite this Table.
  @param newOrder an array of indices indicating a new order
  @return a copy of this column with the rows reordered
  */
  public Table reorderRows(int[] newOrder) {
    return null;
  }

  /**
  Get a copy of this Table reordered based on the input array of indexes.
  Does not overwrite this Table.
  @param newOrder an array of indices indicating a new order
  @return a copy of this column with the rows reordered
  */
  public Table reorderColumns(int[] newOrder) {
    return null;
  }

  /**
  Swap the positions of two rows.
  @param pos1 the first row to swap
  @param pos2 the second row to swap
  */
  public void swapRows(int pos1, int pos2) {
  }

  /**
  Swap the positions of two columns.
  @param pos1 the first column to swap
  @param pos2 the second column to swap
  */
  public void swapColumns(int pos1, int pos2) {
  }

  /**
  Set a specified element in the table.  If an element exists at this
  position already, it will be replaced.  If the position is beyond the capacity
  of this table then an ArrayIndexOutOfBounds will be thrown.
  @param element the new element to be set in the table
  @param row the row to be changed in the table
  @param column the Column to be set in the given row
  */
  public void setObject(Object element, int row, int column) {
  }

  /**
   * Set an int value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setInt(int data, int row, int column) {
  }

  /**
   * Set a short value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setShort(short data, int row, int column) {
  }

  /**
   * Set a float value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setFloat(float data, int row, int column) {
  }

  /**
   * Set an double value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setDouble(double data, int row, int column) {
  }

  /**
   * Set a long value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setLong(long data, int row, int column) {
  }

  /**
   * Set a String value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setString(String data, int row, int column) {
  }

  /**
   * Set a byte[] value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setBytes(byte[] data, int row, int column) {
  }

  /**
   * Set a boolean value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setBoolean(boolean data, int row, int column) {
  }

  /**
   * Set a char[] value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setChars(char[] data, int row, int column) {
  }

  /**
   * Set a byte value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setByte(byte data, int row, int column) {
  }

  /**
   * Set a char value in the table.
   * @param data the value to set
   * @param row the row of the table
   * @param column the column of the table
   */
  public void setChar(char data, int row, int column) {
  }

  /**
  Set the name associated with a column.
  @param label the new column label
  @param position the index of the column to set
  */
  public void setColumnLabel(String label, int position) {
  }

  /**
  Set the comment associated with a column.
  @param comment the new column comment
  @param position the index of the column to set
  */
  public void setColumnComment(String comment, int position) {
  }

  /**
     Set the number of columns this Table can hold.
     @param numColumns the number of columns this Table can hold
     */
  public void setNumColumns(int numColumns) {
  }

  /**
  Sort the specified column and rearrange the rows of the table to
  correspond to the sorted column.
  @param col the column to sort by
  */
  public void sortByColumn(int col) {
  }

  /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
    @param col the index of the column to sort
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
       */
  public void sortByColumn(int col, int begin, int end) {
  }

  /**
  Sets a new capacity for this Table.  The capacity is its potential
  maximum number of entries.  If numEntries is greater than newCapacity,
  then the Table may be truncated.
  @param newCapacity a new capacity
  */
  public void setNumRows(int newCapacity) {
  }

  /////////// Collect the transformations that were performed. /////////
  /**
     Add the transformation to the list.
     @param tm the Transformation that performed the reversable transform.
     */
  public void addTransformation (Transformation tm) {
  }

  /**
   * Set the value at (row, col) to be missing or not missing.
   * @param b true if the value should be set as missing, false otherwise
   * @param row the row index
   * @param col the column index
   */
  public void setValueToMissing(boolean b, int row, int col) {
  }

  /**
   * Set the value at (row, col) to be empty or not empty.
   * @param b true if the value should be set as empty, false otherwise
   * @param row the row index
   * @param col the column index
   */
  public void setValueToEmpty(boolean b, int row, int col) {
  }

  /**
   * Set the value used to signify a nominal empty value for col.
   * @param val the new value
   * @param col the column index
   */
  //public void setNominalEmptyValue(String val, int col);

  /**
   * Set the value used to signify a scalar missing value for col.
   * @param val the new value
   * @param col the column index
   */
  //public void setScalarMissingValue(Number val, int col);

  /**
   * Set the value used to signify a nominal missing value for col.
   * @param val the new value
   * @param col the column index
   */
  //public void setNominalMissingValue(String val, int col);

  /**
   * Set the value used to signify a scalar empty value for col.
   * @param val the new value
   * @param col the column index
   */
  //public void setScalarEmptyValue(Number val, int col);























}