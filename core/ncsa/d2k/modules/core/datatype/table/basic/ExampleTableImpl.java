package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;
//import ncsa.d2k.modules.*;

/**
 * A default implementation of ExampleTable.
 */
public class ExampleTableImpl extends TableImpl implements ExampleTable {

	static final long serialVersionUID = -3828377409585479094L;

    /** the indicies of the records in the various training sets. */
    protected int trainSet[];

    /** the indicies of the records in the various test sets. */
    protected int testSet[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int inputColumns[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int outputColumns[];


    /**
	 * Create a new ExampleTableImpl
     */
    public ExampleTableImpl () {
        super();
    }

    /**
	 * Create a new ExampleTableImpl given the number of columns
	 * @param numColumns the number of columns
	 */
    public ExampleTableImpl (int numColumns) {
        super(numColumns);
    }

    /**
	 * Create a new ExampleTableImpl given a StaticDocument representation of the data.
	 * @param sd the StaticDocument to fill the table with
	 */
    /*public ExampleTableImpl (StaticDocument sd) {
        super(sd);
    }*/

    /**
    	Given a TableImpl to represent, replicate its
    	contents in an ExampleTable.
    	@param table the table to replicate.
     */
    public ExampleTableImpl (TableImpl table) {
        int numColumns = table.getNumColumns();
        columns = new Column[numColumns];
        for (int i = 0; i < numColumns; i++)
            columns[i] = table.getColumn(i);
        setLabel(table.getLabel());
        setComment(table.getComment());
		//setType(table.getType());
        if (table instanceof ExampleTableImpl) {
            // Make sure we get the input / output definitions.
            ExampleTableImpl tt = (ExampleTableImpl)table;
            // make a copy of the input features
            int[] origInputFeatures = tt.getInputFeatures();
            if (origInputFeatures != null) {
                int inLen = origInputFeatures.length;
                this.inputColumns = new int[inLen];
                System.arraycopy(origInputFeatures, 0, inputColumns, 0, inLen);
            }
            // make a copy of the output features
            int[] origOutputFeatures = tt.getOutputFeatures();
            if (origOutputFeatures != null) {
                int outLen = origOutputFeatures.length;
                this.outputColumns = new int[outLen];
                System.arraycopy(origOutputFeatures, 0, outputColumns, 0, outLen);
            }
            // make a copy of the test set
            int[] origTestSet = tt.getTestingSet();
            if (origTestSet != null) {
                int testLen = origTestSet.length;
                this.testSet = new int[testLen];
                System.arraycopy(origTestSet, 0, testSet, 0, testLen);
            }
            // make a copy of the train set
            int[] origTrainSet = tt.getTrainingSet();
            if (origTrainSet != null) {
                int trainLen = origTrainSet.length;
                this.trainSet = new int[trainLen];
                System.arraycopy(origTrainSet, 0, trainSet, 0, trainLen);
            }
            //copy the transformations
			try {
            transformations = (ArrayList)((ArrayList)((MutableTable)tt).getTransformations()).clone();
			}
			catch(Exception e) {
				transformations = null;
			}
        }
    }

    /**
    	Return an exact copy of this Table.  A deep copy is attempted, but if
		it fails a new Table will be created, initialized with the same data as
		this column.
    	@return A new Table with a copy of the contents of this Table.
     */
    public Table copy () {
        ExampleTableImpl vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (ExampleTableImpl)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new ExampleTableImpl(getNumColumns());
            vt.setKeyColumn(getKeyColumn());
            for (int i = 0; i < getNumColumns(); i++)
                vt.setColumn(getColumn(i).copy(), i);
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            //vt.setType(getType());
			vt.setInputFeatures(getInputFeatures());
			vt.setOutputFeatures(getOutputFeatures());
			vt.transformations = (ArrayList)transformations.clone();
			vt.setTestingSet(getTestingSet());
			vt.setTrainingSet(getTrainingSet());
            return  vt;
        }
    }



    //////////////  Input, output, test and train. ///////////////
    /**
    	Returns an array of ints, the indices of the input columns.
    	@return an array of ints, the indices of the input columns.
     */
    public int[] getInputFeatures () {
        return  inputColumns;
    }

    /**
    	Returns the number of input features.
    	@returns the number of input features.
     */
    public int getNumInputFeatures () {
        return  inputColumns.length;
    }

    /**
    	Returns the number of example rows.
    	@returns the number of example rows.
     */
    public int getNumExamples () {
        return  this.getNumRows();
    }

    /**
    	Return the number of examples in the training set.
    	@returns the number of examples in the training set.
     */
    public int getNumTrainExamples () {
        return  this.trainSet.length;
    }

    /**
    	Return the number of examples in the testing set.
    	@returns the number of examples in the testing set.
     */
    public int getNumTestExamples () {
        return  this.testSet.length;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int[] getOutputFeatures () {
        return  outputColumns;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int getNumOutputFeatures () {
        return  outputColumns.length;
    }

    /**
    	Set the input features.
    	@param inputs the indexes of the columns to be used as input features.
     */
    public void setInputFeatures (int[] inputs) {
        inputColumns = inputs;
        /*if (outputColumns == null) {
            // So these are the inputs, we can surmise the outputs.
            int numOutputs = this.getNumColumns() - inputs.length;
            outputColumns = new int[numOutputs];
            boolean[] isInput = new boolean[this.getNumColumns()];
            // Set the bit for each column that is an input.
            for (int j = 0; j < inputs.length; j++)
                isInput[inputs[j]] = true;
            // So, each column that is not an input must be an output.
            for (int i = 0, outputIndex = 0; i < this.getNumColumns(); i++)
                if (!isInput[i])
                    outputColumns[outputIndex++] = i;
        }*/
    }

    /**
    	Set the output features.
    	@param outs the indexes of the columns to be used as output features.
     */
    public void setOutputFeatures (int[] outs) {
        outputColumns = outs;
    }

    /**
    	Set the indexes of the rows in the training set.
    	@param trainingSet the indexes of the items to be used to train the model.
     */
    public void setTrainingSet (int[] trainingSet) {
        trainSet = trainingSet;
    }

    /**
    	Get the training set.
    	@return the indices of the rows of the training set.
     */
    public int[] getTrainingSet () {
        return  trainSet;
    }

    /**
    	Set the indexes of the rows in the testing set.
    	@param testingSet the indexes of the items to be used to test the model.
     */
    public void setTestingSet (int[] testingSet) {
        testSet = testingSet;
    }

    /**
    	Get the testing set.
    	@return the indices of the rows of the testing set.
     */
    public int[] getTestingSet () {
        return  testSet;
    }

    ///////////////////// ACCESSOR METHODS ///////////////////
    /**
	 * Get an int from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the int in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public int getTestOutputInt (int row, int column) {
        return  columns[outputColumns[column]].getInt(testSet[row]);
    }

    /**
	 * Get a short from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the short in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public short getTestOutputShort (int row, int column) {
        return  columns[outputColumns[column]].getShort(testSet[row]);
    }

    /**
	 * Get a long from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the long in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public long getTestOutputLong (int row, int column) {
        return  columns[outputColumns[column]].getLong(testSet[row]);
    }

    /**
	 * Get a float from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the float in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public float getTestOutputFloat (int row, int column) {
        return  columns[outputColumns[column]].getFloat(testSet[row]);
    }

    /**
	 * Get a double from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the double in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public double getTestOutputDouble (int row, int column) {
        return  columns[outputColumns[column]].getDouble(testSet[row]);
    }

    /**
	 * Get a String from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the String in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public String getTestOutputString (int row, int column) {
        return  columns[outputColumns[column]].getString(testSet[row]);
    }

    /**
	 * Get a value from the table at (testSet[row], outputColumns[column]) as a byte[]
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the value in the table at (testSet[row], outputColumns[column]) as a byte[]
	 * @deprecated Use TestTable
     */
    public byte[] getTestOutputBytes (int row, int column) {
        return  columns[outputColumns[column]].getBytes(testSet[row]);
    }

    /**
	 * Get a value from the table at (testSet[row], outputColumns[column]) as a char[]
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the value in the table at (testSet[row], outputColumns[column]) as a char[]
	 * @deprecated Use TestTable
     */
    public char[] getTestOutputChars (int row, int column) {
        return  columns[outputColumns[column]].getChars(testSet[row]);
    }

    /**
	 * Get a boolean from the table at (testSet[row], outputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into outputColumns
	 * @return the boolean in the table at (testSet[row], outputColumns[column])
	 * @deprecated Use TestTable
     */
    public boolean getTestOutputBoolean (int row, int column) {
        return  columns[outputColumns[column]].getBoolean(testSet[row]);
    }

    /**
	 * Get an int from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the int in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public int getTestInputInt (int row, int column) {
        return  columns[inputColumns[column]].getInt(testSet[row]);
    }

    /**
	 * Get a short from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the short in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public short getTestInputShort (int row, int column) {
        return  columns[inputColumns[column]].getShort(testSet[row]);
    }

    /**
	 * Get a long from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the long in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public long getTestInputLong (int row, int column) {
        return  columns[inputColumns[column]].getLong(testSet[row]);
    }

    /**
	 * Get a flaot from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the float in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public float getTestInputFloat (int row, int column) {
        return  columns[inputColumns[column]].getFloat(testSet[row]);
    }

    /**
	 * Get a double from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the double in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public double getTestInputDouble (int row, int column) {
        return  columns[inputColumns[column]].getDouble(testSet[row]);
    }

    /**
	 * Get a String from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the String in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public String getTestInputString (int row, int column) {
        return  columns[inputColumns[column]].getString(testSet[row]);
    }

    /**
	 * Get a value from the table at (testSet[row], inputColumns[column]) as a byte[]
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the value in the table at (testSet[row], inputColumns[column]) as a byte[]
	 * @deprecated Use TestTable
     */
    public byte[] getTestInputBytes (int row, int column) {
        return  columns[inputColumns[column]].getBytes(testSet[row]);
    }

    /**
	 * Get a value from the table at (testSet[row], inputColumns[column]) as a char[]
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the value in the table at (testSet[row], inputColumns[column]) as a char[]
	 * @deprecated Use TestTable
     */
    public char[] getTestInputChars (int row, int column) {
        return  columns[inputColumns[column]].getChars(testSet[row]);
    }

    /**
	 * Get a boolean from the table at (testSet[row], inputColumns[column])
	 * @param row the row of the test set
	 * @param column the index into inputColumns
	 * @return the boolean in the table at (testSet[row], inputColumns[column])
	 * @deprecated Use TestTable
     */
    public boolean getTestInputBoolean (int row, int column) {
        return  columns[inputColumns[column]].getBoolean(testSet[row]);
    }

    /**
	 * Get an int from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the int in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public int getTrainOutputInt (int row, int column) {
        return  columns[outputColumns[column]].getInt(trainSet[row]);
    }

    /**
	 * Get a short from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the short in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public short getTrainOutputShort (int row, int column) {
        return columns[outputColumns[column]].getShort(trainSet[row]);
    }

    /**
	 * Get a long from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the long in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public long getTrainOutputLong (int row, int column) {
        return  columns[outputColumns[column]].getLong(trainSet[row]);
    }

    /**
	 * Get a float from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the float in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public float getTrainOutputFloat (int row, int column) {
        return  columns[outputColumns[column]].getFloat(trainSet[row]);
    }

    /**
	 * Get a double from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the double in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public double getTrainOutputDouble (int row, int column) {
        return  columns[outputColumns[column]].getDouble(trainSet[row]);
    }

    /**
	 * Get a String from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the String in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public String getTrainOutputString (int row, int column) {
        return  columns[outputColumns[column]].getString(trainSet[row]);
    }

    /**
	 * Get a value from the table at (trainSet[row], outputColumns[column]) as a byte[]
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the value in the table at (trainSet[row], outputColumns[column]) as a byte[]
	 * @deprecated Use TrainTable
     */
    public byte[] getTrainOutputBytes (int row, int column) {
        return  columns[outputColumns[column]].getBytes(trainSet[row]);
    }

    /**
	 * Get a value from the table at (trainSet[row], outputColumns[column]) as a char[]
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the value in the table at (trainSet[row], outputColumns[column]) as a char[]
	 * @deprecated Use TrainTable
     */
    public char[] getTrainOutputChars (int row, int column) {
        return  columns[outputColumns[column]].getChars(trainSet[row]);
    }

    /**
	 * Get a boolean from the table at (trainSet[row], outputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into outputColumns
	 * @return the boolean in the table at (trainSet[row], outputColumns[column])
	 * @deprecated Use TrainTable
     */
    public boolean getTrainOutputBoolean (int row, int column) {
        return  columns[outputColumns[column]].getBoolean(trainSet[row]);
    }

    /**
	 * Get an int from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the int in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public int getTrainInputInt (int row, int column) {
        return  columns[inputColumns[column]].getInt(trainSet[row]);
    }

    /**
	 * Get a short from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the short in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public short getTrainInputShort (int row, int column) {
        return  columns[inputColumns[column]].getShort(trainSet[row]);
    }

    /**
	 * Get a long from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the long in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public long getTrainInputLong (int row, int column) {
        return  columns[inputColumns[column]].getLong(trainSet[row]);
    }

    /**
	 * Get a float from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the float in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public float getTrainInputFloat (int row, int column) {
        return  columns[inputColumns[column]].getFloat(trainSet[row]);
    }

    /**
	 * Get a double from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the double in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public double getTrainInputDouble (int row, int column) {
        return  columns[inputColumns[column]].getDouble(trainSet[row]);
    }

    /**
	 * Get a String from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the String in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public String getTrainInputString (int row, int column) {
        return  columns[inputColumns[column]].getString(trainSet[row]);
    }

    /**
	 * Get a value from the table at (trainSet[row], inputColumns[column]) as a byte[]
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the value in the table at (trainSet[row], inputColumns[column]) as a byte[]
	 * @deprecated Use TrainTable
     */
    public byte[] getTrainInputBytes (int row, int column) {
        return  columns[inputColumns[column]].getBytes(trainSet[row]);
    }

    /**
	 * Get a value from the table at (trainSet[row], inputColumns[column]) as a char[]
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the value in the table at (trainSet[row], inputColumns[column]) as a char[]
	 * @deprecated Use TrainTable
     */
    public char[] getTrainInputChars (int row, int column) {
        return  columns[inputColumns[column]].getChars(trainSet[row]);
    }

    /**
	 * Get a boolean from the table at (trainSet[row], inputColumns[column])
	 * @param row the row of the train set
	 * @param column the index into intputColumns
	 * @return the boolean in the table at (trainSet[row], inputColumns[column])
	 * @deprecated Use TrainTable
     */
    public boolean getTrainInputBoolean (int row, int column) {
        return  columns[inputColumns[column]].getBoolean(trainSet[row]);
    }

    //////////////// Access the test data ///////////////////
    /**
    	This class provides transparent access to the test data only. The testSets
    	field of the TrainTest table is used to reference only the test data, yet
    	the getter methods look exactly the same as they do for any other table.
		@return a reference to a table referencing only the testing data
     */
    public TestTable getTestTable () {
        if (testSet == null)
            return  null;
        return  new TestTableImpl(this);
    }

    /**
    	Return a reference to a Table referencing only the training data.
    	@return a reference to a Table referencing only the training data.
     */
    public TrainTable getTrainTable () {
        if (trainSet == null)
            return  null;
        return  new TrainTableImpl(this);
    }

	public PredictionTable toPredictionTable() {
		return new PredictionTableImpl(this);
	}

    //*******************

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
    public int getNumInputs(int e) {
        return inputColumns.length;
    }
    public int getNumOutputs(int e) {
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
