package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.TransformationModule;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ContinuousExampleSet extends AbstractExampleSet implements ExampleTable, TestTable, java.io.Serializable {

  public String [] inputNames;
  public String [] outputNames;
  int numFeatures;
  int numExamples;
  int numInputs;
  int numOutputs;
  float data[];
  int [] exampleIndices;
  int [] testingSet;

  public ContinuousExampleSet () {
  }

  public ContinuousExampleSet (float [] data, int numExamples, int numInputs, int numOutputs, String [] inputNames, String [] outputNames) {

    this.data = data;
    int [] exampleIndices = new int[numExamples];
    for (int i = 0; i < numExamples; i++)
      exampleIndices[i] = i;
    this.exampleIndices = exampleIndices;
    this.numExamples = numExamples;
    this.numFeatures = numInputs + numOutputs;
    this.numInputs   = numInputs;
    this.numOutputs  = numOutputs;
    this.inputNames   = inputNames;
    this.outputNames  = outputNames;
  }

/*
  public void allocateExamplePointers(int numExamples) {
    int [] exampleIndices = new int[numExamples];
    this.exampleIndices = exampleIndices;
    this.numExamples = numExamples;
    }

  public void allocateSpace(int numExamples, int numInputs, int numOutputs) {
    this.data = new float[numExamples * (numInputs + numOutputs)];
    int [] exampleIndices = new int[numExamples];
    int [] testingSet = new int[numExamples];
    for (int i = 0; i < numExamples; i++)
      exampleIndices[i] = i;
    this.exampleIndices = exampleIndices;
    this.testingSet    = testingSet;
    this.numExamples = numExamples;
    this.numFeatures = numInputs + numOutputs;
    this.numInputs   = numInputs;
    this.numOutputs  = numOutputs;
    }

  public ExampleTable shallowCopy() {
    ContinuousExampleSet copy = new ContinuousExampleSet();
    copy.data             = this.data;
    copy.exampleIndices   = this.exampleIndices;
    copy.numExamples      = this.numExamples;
    copy.numFeatures      = this.numFeatures;
    copy.numInputs        = this.numInputs;
    copy.numOutputs       = this.numOutputs;
    copy.inputNames       = this.inputNames;
    copy.outputNames      = this.outputNames;
    return (ContinuousExampleSet) copy;
  }
*/

  public Table copy() {
    ContinuousExampleSet copy   = new ContinuousExampleSet();
    copy.inputNames         = this.inputNames;
    copy.outputNames        = this.outputNames;
    copy.numFeatures        = this.numFeatures;
    copy.numExamples        = this.numExamples;
    copy.numInputs          = this.numInputs;
    copy.numOutputs         = this.numOutputs;
    copy.data               = (float []) this.data.clone();
    copy.exampleIndices     = (int []) this.exampleIndices.clone();
    if (testingSet != null)
      copy.testingSet         = (int []) this.testingSet.clone();
    return (Table) copy;
  }

/*
  public ContinuousExampleSet(AbstractExample [] examples)
    {
    this.examples    = examples;
    this.numExamples = examples.length;
    this.numInputs   = examples[0].getNumInputs();
    this.numOutputs  = examples[0].getNumOutputs();
    }

  public void allocateExamplePointers(int numExamples)
    {
    this.examples = new AbstractExample[numExamples];
    this.numExamples = numExamples;
    }
*/



  public double getInputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + i];
  }

  public double getOutputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public String getInputString(int e, int i) {
    return Float.toString(data[exampleIndices[e] * numFeatures + i]);
  }

  public String getOutputString(int e, int i) {
    return Float.toString(data[exampleIndices[e] * numFeatures + numInputs + i]);
  }

  public int getInputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures + i];
  }

  public int getOutputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public float getInputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + i];
  }

  public float getOutputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public short getInputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures  + i];
  }

  public short getOutputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public long getInputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures  + i];
  }

  public long getOutputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public byte getInputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures  + i];
  }

  public byte getOutputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public Object getInputObject(int e, int i) {
    return (Object) new Double(data[exampleIndices[e] * numFeatures  + i]);
  }

  public Object getOutputObject(int e, int i)
  {
    return (Object) new Double(data[exampleIndices[e] * numFeatures + numInputs + i]);
  }

  public char getInputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures  + i];
  }

  public char getOutputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures + numInputs + i];
  }

  public byte[] getInputBytes(int e, int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures  + i];
    return bytes;
  }

  public byte[] getOutputBytes(int e, int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures + numInputs + i];
    return bytes;
  }

  public char[] getInputChars(int e, int i) {
    char [] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures  + i];
    return chars;
  }

  public char[] getOutputChars(int e, int i) {
    char [] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures + numInputs + i];
    return chars;
  }

  public boolean getInputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures  + i] < 0.5)
      return false;
    else
      return true;
  }

  public boolean getOutputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures + numInputs + i] < 0.5)
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
    return (Example) new FloatExample(this, e);
  }


  public String getInputName(int i) {
    return this.inputNames[i];
  }

  public String [] getOutputNames() {
    return this.outputNames;
  }

  public String [] getInputNames() {
    return this.inputNames;
  }

  public String getOutputName(int i) {
    return this.outputNames[i];
  }

  public int getInputType(int i) {
    System.out.println("Must override this method!");
    return ColumnTypes.DOUBLE;
  }

  public int getOutputType(int i) {
    System.out.println("Must override this method!");
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
    data[exampleIndices[e] * numFeatures + i] = (float) value;
  }

  public void setOutput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + numInputs + i] = (float) value;
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
    return Float.toString(data[exampleIndices[row] * numFeatures + column]);
  }

  public byte[] getBytes(int row, int column) {
    return null;
  }

  public boolean getBoolean(int row, int column) {
    float value = data[exampleIndices[row] * numFeatures + column];
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
      label = inputNames[position];
    else
      label = outputNames[position - numInputs];
    return label;
  }
  public String getColumnComment(int position)
  {
    return null;
  }
  public String getLabel()
  {
    return null;
  }
  public void setLabel(String labl)
  {
  }
  public String getComment()
  {
    return null;
  }
  public void setComment(String comment)
  {
  }
  public int getNumRows()
  {
    return numExamples;
  }
  public int getNumEntries()
  {
    return numExamples;
  }
  public int getNumColumns()
  {
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

  public ArrayList getTransformations () {
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


    String [] newInputNames = new String[newNumInputs];
    newIndex = 0;
    for (int i = 0; i < numInputs; i++) {
      if (!deleteFeatures[i])
        newInputNames[newIndex++] = inputNames[i];
    }
    this.inputNames = newInputNames;

    float [] newData = new float[numExamples * (newNumInputs + numOutputs)];

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

}