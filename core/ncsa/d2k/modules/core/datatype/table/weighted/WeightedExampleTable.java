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
    /*protected int[] testSet;
    protected int[] trainSet;

    /**the indicies of the attributes that are inputs (to the model). */
    //protected int inputColumns[];
    /*the indicies of the attributes that are inputs (to the model). */
    //protected int outputColumns[];

    /**
     * Constructor that takes in a WeightedTable
     * @param wt - Weighted Table
    */
    WeightedExampleTable(WeightedTable wt) {
    /*    super(wt);
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
        */
        super(wt);
        original = wt.original.toExampleTable();
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
        return ((ExampleTable)original).getInputFeatures();
        //return inputColumns;
    }

    public int[] getOutputFeatures() {
        return ((ExampleTable)original).getOutputFeatures();
        //return outputColumns;
    }

    public void setInputFeatures(int[] ins) {
        ((ExampleTable)original).setInputFeatures(ins);
        //inputColumns = ins;
    }

    public void setOutputFeatures(int[] outs) {
        ((ExampleTable)original).setOutputFeatures(outs);
        //outputColumns = outs;
    }

    public int[] getTestingSet() {
        //return testSet;
        return ((ExampleTable)original).getTestingSet();
    }

    public int[] getTrainingSet() {
        //return trainSet;
        return ((ExampleTable)original).getTrainingSet();
    }

    public void setTestingSet(int[] ts) {
        //testSet = ts;
        ((ExampleTable)original).setTestingSet(ts);
    }

    public void setTrainingSet(int[] ts) {
        //trainSet = ts;
        ((ExampleTable)original).setTrainingSet(ts);
    }

    public int getNumOutputFeatures() {
        //return getOutputFeatures().length;
        return ((ExampleTable)original).getNumOutputFeatures();
    }

    public int getNumInputFeatures() {
        //return getInputFeatures().length;
        return ((ExampleTable)original).getNumInputFeatures();
    }

    public int getNumTestExamples() {
        //return testSet.length;
        return ((ExampleTable)original).getNumTestExamples();
    }

    public int getNumTrainExamples() {
        //return trainSet.length;
        return ((ExampleTable)original).getNumTrainExamples();
    }

    public int getNumExamples() {
        //return getNumRows();
        return ((ExampleTable)original).getNumExamples();
    }

    public double getInputDouble(int e, int i) {
        //return getDouble(e, inputColumns[i]);
        return ((ExampleTable)original).getInputDouble(e, i);
    }

    public double getOutputDouble(int e, int o) {
        //return getDouble(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputDouble(e, o);
    }

    public String getInputString(int e, int i) {
        //return getString(e, inputColumns[i]);
        return ((ExampleTable)original).getInputString(e, i);
    }
    public String getOutputString(int e, int o) {
        //return getString(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputString(e, o);
    }
    public int getInputInt(int e, int i) {
        //return getInt(e, inputColumns[i]);
        return ((ExampleTable)original).getInputInt(e, i);
    }
    public int getOutputInt(int e, int o) {
        //return getInt(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputInt(e, o);
    }
    public float getInputFloat(int e, int i) {
        //return getFloat(e, inputColumns[i]);
        return ((ExampleTable)original).getInputFloat(e, i);
    }
    public float getOutputFloat(int e, int o) {
        //return getFloat(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputFloat(e, o);
    }
    public short getInputShort(int e, int i) {
//        return getShort(e, inputColumns[i]);
        return ((ExampleTable)original).getInputShort(e, i);
    }
    public short getOutputShort(int e, int o) {
        //return getShort(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputShort(e, o);
    }
    public long getInputLong(int e, int i) {
        //return getLong(e, inputColumns[i]);
        return ((ExampleTable)original).getInputLong(e, i);
    }
    public long getOutputLong(int e, int o) {
        //return getLong(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputLong(e, o);
    }
    public byte getInputByte(int e, int i) {
        //return getByte(e, inputColumns[i]);
        return ((ExampleTable)original).getInputByte(e, i);
    }
    public byte getOutputByte(int e, int o) {
        //return getByte(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputByte(e, o);
    }
    public Object getInputObject(int e, int i) {
        //return getObject(e, inputColumns[i]);
        return ((ExampleTable)original).getInputObject(e, i);
    }
    public Object getOutputObject(int e, int o) {
        //return getObject(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputObject(e, o);
    }
    public char getInputChar(int e, int i) {
        //return getChar(e, inputColumns[i]);
        return ((ExampleTable)original).getInputChar(e, i);
    }
    public char getOutputChar(int e, int o) {
        //return getChar(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputChar(e, o);
    }
    public char[] getInputChars(int e, int i) {
        //return getChars(e, inputColumns[i]);
        return ((ExampleTable)original).getInputChars(e, i);
    }
    public char[] getOutputChars(int e, int o) {
        //return getChars(e, inputColumns[o]);
        return ((ExampleTable)original).getOutputChars(e, o);
    }
    public byte[] getInputBytes(int e, int i) {
        //return getBytes(e, inputColumns[i]);
        return ((ExampleTable)original).getInputBytes(e, i);
    }
    public byte[] getOutputBytes(int e, int o) {
        //return getBytes(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputBytes(e, o);
    }
    public boolean getInputBoolean(int e, int i) {
        //return getBoolean(e, inputColumns[i]);
        return ((ExampleTable)original).getInputBoolean(e, i);
    }
    public boolean getOutputBoolean(int e, int o) {
        //return getBoolean(e, outputColumns[o]);
        return ((ExampleTable)original).getOutputBoolean(e, o);
    }
    public int getNumInputs(int e) {
        //return inputColumns.length;
        return ((ExampleTable)original).getNumInputs(e);
    }
    public int getNumOutputs(int e) {
        //return outputColumns.length;
        return ((ExampleTable)original).getNumOutputs(e);
    }

    public Example getExample(int i) { return null; }

    public String getInputName(int i) {
        //return getColumnLabel(inputColumns[i]);
        return ((ExampleTable)original).getInputName(i);
    }
    public String getOutputName(int o) {
        //return getColumnLabel(outputColumns[o]);
        return ((ExampleTable)original).getOutputName(o);
    }
    public int getInputType(int i) {
//        return getColumnType(inputColumns[i]);
        return ((ExampleTable)original).getInputType(i);
    }
    public int getOutputType(int o) {
//        return getColumnType(outputColumns[o]);
        return ((ExampleTable)original).getOutputType(o);
    }
    public boolean isInputNominal(int i) {
//        return isColumnNominal(inputColumns[i]);
        return ((ExampleTable)original).isColumnNominal(i);
    }
    public boolean isOutputNominal(int o) {
//        return isColumnNominal(outputColumns[o]);
        return ((ExampleTable)original).isOutputNominal(o);
    }
    public boolean isInputScalar(int i) {
//        return isColumnScalar(inputColumns[i]);
        return ((ExampleTable)original).isInputScalar(i);
    }
    public boolean isOutputScalar(int o) {
//        return isColumnScalar(outputColumns[o]);
        return ((ExampleTable)original).isOutputScalar(o);
    }
}