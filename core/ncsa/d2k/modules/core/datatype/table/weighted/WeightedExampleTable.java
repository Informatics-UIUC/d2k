package ncsa.d2k.modules.core.datatype.table.weighted;
//package ncsa.d2k.modules.projects.clutter.weighted;
import ncsa.d2k.modules.core.datatype.table.*;
/**
 * <p>Title: WeightedExampleTable </p>
 * <p>Description: WeightedExampleTable</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Sameer Mathur
 * @version 1.0
 */

class WeightedExampleTable extends WeightedTable implements ExampleTable {

    /**indexes to rows that represent the testSet and trainSet */
    protected int[] testSet;
    protected int[] trainSet;

    /**the indicies of the attributes that are inputs (to the model). */
    protected int inputColumns[];
    /*the indicies of the attributes that are inputs (to the model). */
    protected int outputColumns[];

    /**
     * Constructor that takes in a WeightedTable
     * @param wt - Weighted Table
    */
    WeightedExampleTable(WeightedTable wt) {
        super(wt);
        if(wt instanceof ExampleTable) {
            ExampleTable et = (ExampleTable)wt;
            this.testSet = et.getTestingSet();
            this.trainSet = et.getTrainingSet();
            this.inputColumns = et.getInputFeatures();
            this.outputColumns = et.getOutputFeatures();
        }
        else {
            trainSet = new int[0];
            testSet = new int[0];
            inputColumns = new int[0];
            outputColumns = new int[0];
        }
   }

    public PredictionTable toPredictionTable() {
        return new WeightedPredictionTable(this);
    }

    /**
     * getTestTable
     * @return the TestTable
     */
    public TestTable getTestTable() {
        return new WeightedTestTable(this);
    }

    public TrainTable getTrainTable() {
        return new WeightedTrainTable(this);
    }

    public int[] getInputFeatures() {
        //return ((ExampleTable)original).getInputFeatures();
        return inputColumns;
    }

    public int[] getOutputFeatures() {
        //return ((ExampleTable)original).getOutputFeatures();
        return outputColumns;
    }

    public void setInputFeatures(int[] ins) {
        //((ExampleTable)original).setInputFeatures(ins);
        inputColumns = ins;
    }

    public void setOutputFeatures(int[] outs) {
        //((ExampleTable)original).setOutputFeatures(outs);
        outputColumns = outs;
    }

    public int[] getTestingSet() {
        return testSet;
    }

    public int[] getTrainingSet() {
        return trainSet;
    }

    public void setTestingSet(int[] ts) {
        testSet = ts;
    }

    public void setTrainingSet(int[] ts) {
        trainSet = ts;
    }

    public int getNumOutputFeatures() {
        return getOutputFeatures().length;
    }

    public int getNumInputFeatures() {
        return getInputFeatures().length;
    }

    public int getNumTestExamples() {
        return testSet.length;
    }

    public int getNumTrainExamples() {
        return trainSet.length;
    }

    public int getNumExamples() {
        return getNumRows();
    }

    public double getInputDouble(int e, int i) {
        return getDouble(e, inputColumns[i]);
    }

    public double getOutputDouble(int e, int o) {
        return getDouble(e, outputColumns[o]);
    }
    public String getInputString(int e, int i) {
        return getString(e, inputColumns[i]);
    }
    public String getOutputString(int e, int o) {
        return getString(e, outputColumns[o]);
    }
    public int getInputInt(int e, int i) {
        return getInt(e, inputColumns[i]);
    }
    public int getOutputInt(int e, int o) {
        return getInt(e, outputColumns[o]);
    }
    public float getInputFloat(int e, int i) {
        return getFloat(e, inputColumns[i]);
    }
    public float getOutputFloat(int e, int o) {
        return getFloat(e, outputColumns[o]);
    }
    public short getInputShort(int e, int i) {
        return getShort(e, inputColumns[i]);
    }
    public short getOutputShort(int e, int o) {
        return getShort(e, outputColumns[o]);
    }
    public long getInputLong(int e, int i) {
        return getLong(e, inputColumns[i]);
    }
    public long getOutputLong(int e, int o) {
        return getLong(e, outputColumns[o]);
    }
    public byte getInputByte(int e, int i) {
        return getByte(e, inputColumns[i]);
    }
    public byte getOutputByte(int e, int o) {
        return getByte(e, outputColumns[o]);
    }
    public Object getInputObject(int e, int i) {
        return getObject(e, inputColumns[i]);
    }
    public Object getOutputObject(int e, int o) {
        return getObject(e, outputColumns[o]);
    }
    public char getInputChar(int e, int i) {
        return getChar(e, inputColumns[i]);
    }
    public char getOutputChar(int e, int o) {
        return getChar(e, outputColumns[o]);
    }
    public char[] getInputChars(int e, int i) {
        return getChars(e, inputColumns[i]);
    }
    public char[] getOutputChars(int e, int o) {
        return getChars(e, inputColumns[o]);
    }
    public byte[] getInputBytes(int e, int i) {
        return getBytes(e, inputColumns[i]);
    }
    public byte[] getOutputBytes(int e, int o) {
        return getBytes(e, outputColumns[o]);
    }
    public boolean getInputBoolean(int e, int i) {
        return getBoolean(e, inputColumns[i]);
    }
    public boolean getOutputBoolean(int e, int o) {
        return getBoolean(e, outputColumns[o]);
    }
    public int getNumInputs() {
        return inputColumns.length;
    }
    public int getNumOutputs() {
        return outputColumns.length;
    }

    public Example getExample(int i) { return null; }

    public String getInputName(int i) {
        return getColumnLabel(inputColumns[i]);
    }
    public String getOutputName(int o) {
        return getColumnLabel(outputColumns[o]);
    }
    public int getInputType(int i) {
        return getColumnType(inputColumns[i]);
    }
    public int getOutputType(int o) {
        return getColumnType(outputColumns[o]);
    }
    public boolean isInputNominal(int i) {
        return isColumnNominal(inputColumns[i]);
    }
    public boolean isOutputNominal(int o) {
        return isColumnNominal(outputColumns[o]);
    }
    public boolean isInputScalar(int i) {
        return isColumnScalar(inputColumns[i]);
    }
    public boolean isOutputScalar(int o) {
        return isColumnScalar(outputColumns[o]);
    }
}