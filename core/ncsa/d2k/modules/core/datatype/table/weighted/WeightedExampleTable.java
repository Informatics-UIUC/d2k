package ncsa.d2k.modules.core.datatype.table.weighted;
//package ncsa.d2k.modules.projects.clutter.weighted;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

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

    public String[] getInputNames() {
      return ((ExampleTable)original).getInputNames();
    }

    public String[] getOutputNames() {
      return ((ExampleTable)original).getOutputNames();
    }
    // Mutable Table support
    /**
     * Add a row to the end of this Table, initialized with integer data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(int[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with float data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(float[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with double data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(double[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with long data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(long[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with short data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(short[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with boolean data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(boolean[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with String data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(String[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with char[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(char[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with byte[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(byte[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with Object[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(Object[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with byte data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(byte[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a row to the end of this Table, initialized with char data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(char[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with integer data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with float data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with double data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with long data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with short data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with boolean data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with String data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with char[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with byte[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with Object data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with byte data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new row into this Table, initialized with char data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with integer data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(int[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with float data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(float[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with double data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(double[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with long data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(long[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with short data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(short[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with boolean data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(boolean[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with String data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(String[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with char[] data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(char[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with byte[] data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(byte[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with Object data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(Object[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with byte data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(byte[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Add a new column to the end of this table, initialized with char data.
     * @param newEntry the data to initialize the column with.
     */
    public void addColumn(char[] newEntry) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with integer data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with float data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with double data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with long data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with short data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with boolean data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with String data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with char[] data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with byte[] data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with Object data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with byte data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Insert a new column into this Table, initialized with char data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
    public void insertColumn(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of int data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of float data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of double data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of long data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of short data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of boolean data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of String data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of char[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of byte[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of Object data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of byte data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the row at the given position to an array of char data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of int data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of float data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of double data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of long data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of short data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of boolean data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of String data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of char data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of byte[] data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of Object data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of byte data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the column at the given position to an array of char data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
    public void setColumn(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Remove a column from the table.
            @param position the position of the Column to remove
    */
    public void removeColumn(int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }
    /**
            Remove a range of columns from the table.
            @param start the start position of the range to remove
            @param len the number to remove-the length of the range
    */
    public void removeColumns(int start, int len) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Remove a row from this Table.
     * @param row the row to remove
     */
    public void removeRow(int row) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Remove a range of rows from the table.
            @param start the start position of the range to remove
            @param len the number to remove-the length of the range
    */
    public void removeRows(int start, int len) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
    public void removeRowsByFlag(boolean[] flags) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
    public void removeColumnsByFlag(boolean[] flags) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
    public void removeRowsByIndex(int[] indices) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
    public void removeColumnsByIndex(int[] indices) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Get a copy of this Table reordered based on the input array of indexes.
            Does not overwrite this Table.
            @param newOrder an array of indices indicating a new order
            @return a copy of this column with the rows reordered
    */
    public Table reorderRows(int[] newOrder) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Get a copy of this Table reordered based on the input array of indexes.
            Does not overwrite this Table.
            @param newOrder an array of indices indicating a new order
            @return a copy of this column with the rows reordered
    */
    public Table reorderColumns(int[] newOrder) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Swap the positions of two rows.
            @param pos1 the first row to swap
            @param pos2 the second row to swap
    */
    public void swapRows(int pos1, int pos2) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Swap the positions of two columns.
            @param pos1 the first column to swap
            @param pos2 the second column to swap
    */
    public void swapColumns(int pos1, int pos2) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
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
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set an int value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setInt(int data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a short value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setShort(short data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a float value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setFloat(float data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set an double value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setDouble(double data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a long value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setLong(long data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a String value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setString(String data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a byte[] value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setBytes(byte[] data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a boolean value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setBoolean(boolean data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a char[] value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setChars(char[] data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a byte value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setByte(byte data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
     * Set a char value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
    public void setChar(char data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Set the name associated with a column.
            @param label the new column label
            @param position the index of the column to set
    */
    public void setColumnLabel(String label, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Set the comment associated with a column.
            @param comment the new column comment
            @param position the index of the column to set
    */
    public void setColumnComment(String comment, int position) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
        Set the number of columns this Table can hold.
        @param numColumns the number of columns this Table can hold
 */
    public void setNumColumns(int numColumns) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Sort the specified column and rearrange the rows of the table to
            correspond to the sorted column.
            @param col the column to sort by
    */
    public void sortByColumn(int col) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
   Sort the elements in this column starting with row 'begin' up to row 'end',
       @param col the index of the column to sort
   @param begin the row no. which marks the beginnig of the  column segment to be sorted
   @param end the row no. which marks the end of the column segment to be sorted
*/
public void sortByColumn(int col, int begin, int end) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
            Sets a new capacity for this Table.  The capacity is its potential
            maximum number of entries.  If numEntries is greater than newCapacity,
            then the Table may be truncated.
            @param newCapacity a new capacity
    */
    public void setNumRows(int newCapacity) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/////////// Collect the transformations that were performed. /////////
/**
 Add the transformation to the list.
 @param tm the Transformation that performed the reversable transform.
 */
public void addTransformation (Transformation tm) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

/**
 Returns the list of all reversable transformations there were performed
 on the original dataset.
 @returns an ArrayList containing the Transformation which transformed the data.
 */
public List getTransformations () {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the value at (row, col) to be missing or not missing.
 * @param b true if the value should be set as missing, false otherwise
     * @param row the row index
     * @param col the column index
     */
    public void setValueToMissing(boolean b, int row, int col) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }

    /**
     * Set the value at (row, col) to be empty or not empty.
 * @param b true if the value should be set as empty, false otherwise
     * @param row the row index
     * @param col the column index
     */
    public void setValueToEmpty(boolean b, int row, int col) {
      throw new RuntimeException("Table mutation not supported in WeightedTable.");
    }
}