package ncsa.d2k.modules.core.datatype.table.basic;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import java.util.*;

/**
 * A default implementation of ExampleTable.
 */
public class ExampleTableImpl extends SubsetTableImpl implements ExampleTable {

	static final long serialVersionUID = -3828377409585479094L;

	/** the indicies of the records in the various training sets. */
	private int trainSet[];

	/** the indicies of the records in the various test sets. */
	private int testSet[];

	/**the indicies of the attributes that are inputs (to the model). */
	protected int inputColumns[];

	/**the indicies of the attributes that are inputs (to the model). */
	protected int outputColumns[];

	/** the input columns. */
	private Column [] ins;

	/** the output columns. */
	private Column [] outs;

	private String[] inputNames;
	private String[] outputNames;

	/**
	* Create a new ExampleTableImpl
	*/
	public ExampleTableImpl() {
		super();
	}

	/**
	 * Create a new ExampleTableImpl given the number of columns
	 * @param numColumns the number of columns
	 */
	public ExampleTableImpl(int numColumns) {
		super(numColumns);
		inputColumns = new int[0];
		outputColumns = new int[0];
		inputNames = new String[0];
		outputNames = new String[0];
		testSet = new int[0];
		trainSet = new int[0];
	}

	/**
	 * Given a TableImpl to represent, replicate its
	 * contents in an ExampleTable.
	 * @param table the table to replicate.
	 */
	public ExampleTableImpl(TableImpl table) {
		super(table);

		if (table instanceof ExampleTableImpl) {

		  // Make sure we get the input / output definitions.
		  ExampleTableImpl tt = (ExampleTableImpl) table;

		  // make a copy of the input features
		  int[] origInputFeatures = tt.getInputFeatures();
		  if (origInputFeatures != null) {
			int inLen = origInputFeatures.length;
			int [] ics = new int[inLen];
			System.arraycopy(origInputFeatures, 0, ics, 0, inLen);
			setInputFeatures(ics);
		  }
		  else
			setInputFeatures(new int[0]);

		  // make a copy of the output features
		  int[] origOutputFeatures = tt.getOutputFeatures();
		  if (origOutputFeatures != null) {
			int outLen = origOutputFeatures.length;
			int [] ocs = new int[outLen];
			System.arraycopy(origOutputFeatures, 0, ocs, 0, outLen);
			setOutputFeatures(ocs);
		  } else
			setOutputFeatures(new int[0]);

		  // make a copy of the test set
		  int[] origTestSet = tt.getTestingSet();
		  if (origTestSet != null) {
			int testLen = origTestSet.length;
			this.testSet = new int[testLen];
			System.arraycopy(origTestSet, 0, testSet, 0, testLen);
		  } else
			setTestingSet(new int[0]);

		  // make a copy of the train set
		  int[] origTrainSet = tt.getTrainingSet();
		  if (origTrainSet != null) {
			int trainLen = origTrainSet.length;
			this.trainSet = new int[trainLen];
			System.arraycopy(origTrainSet, 0, trainSet, 0, trainLen);
		  } else
			setTestingSet(new int[0]);
		} else {
		  setInputFeatures(new int[0]);
		  setOutputFeatures(new int[0]);
		  setTestingSet(new int[0]);
		  setTrainingSet(new int[0]);
		}

		//copy the transformations
		try {
		  if(table instanceof MutableTable)
			transformations = (ArrayList) ( (ArrayList) ( (MutableTable) table).
										 getTransformations()).clone();
		}
		catch (Exception e) {
		  e.printStackTrace();
		  transformations = null;
		}
	}

	/**
	 * Construct the new example table given a new subset, and an existing table.
	 * @param table
	 * @param ss
	 */
	public ExampleTableImpl(TableImpl table, int [] ss) {
		super(table, ss);
		if (table instanceof ExampleTableImpl) {

		  // Make sure we get the input / output definitions.
		  ExampleTableImpl tt = (ExampleTableImpl) table;

		  // make a copy of the input features
		  int[] origInputFeatures = tt.getInputFeatures();
		  if (origInputFeatures != null) {
			int inLen = origInputFeatures.length;
			int [] ics = new int[inLen];
			System.arraycopy(origInputFeatures, 0, ics, 0, inLen);
			setInputFeatures(ics);
		  }
		  else
			setInputFeatures(new int[0]);

		  // make a copy of the output features
		  int[] origOutputFeatures = tt.getOutputFeatures();
		  if (origOutputFeatures != null) {
			int outLen = origOutputFeatures.length;
			int [] ocs = new int[outLen];
			System.arraycopy(origOutputFeatures, 0, ocs, 0, outLen);
			setOutputFeatures(ocs);
		  } else
			setOutputFeatures(new int[0]);

		  // make a copy of the test set
		  int[] origTestSet = tt.getTestingSet();
		  if (origTestSet != null) {
			int testLen = origTestSet.length;
			this.testSet = new int[testLen];
			System.arraycopy(origTestSet, 0, testSet, 0, testLen);
		  } else
			setTestingSet(new int[0]);

		  // make a copy of the train set
		  int[] origTrainSet = tt.getTrainingSet();
		  if (origTrainSet != null) {
			int trainLen = origTrainSet.length;
			this.trainSet = new int[trainLen];
			System.arraycopy(origTrainSet, 0, trainSet, 0, trainLen);
		  } else
			setTestingSet(new int[0]);
		} else {
		  setInputFeatures(new int[0]);
		  setOutputFeatures(new int[0]);
		  setTestingSet(new int[0]);
		  setTrainingSet(new int[0]);
		}

		//copy the transformations
		try {
		  if(table instanceof MutableTable)
			transformations = (ArrayList) ( (ArrayList) ( (MutableTable) table).
										 getTransformations()).clone();
		}
		catch (Exception e) {
		  e.printStackTrace();
		  transformations = null;
		}
	}

	/**
	 * Create a prediction table and return it.
	 * @return a prediction table.
	 */
	public PredictionTable toPredictionTable() {
		return new PredictionTableImpl(this);
	}

	//////////////  Input, output, test and train. ///////////////

	/**
	Returns an array of ints, the indices of the input columns.
	@return an array of ints, the indices of the input columns.
	*/
	public int[] getInputFeatures() {
		return inputColumns;
	}

	/**
	Returns the number of input features.
	@returns the number of input features.
	*/
	public int getNumInputFeatures() {
		return inputColumns.length;
	}

	/**
	Return the number of examples in the training set.
	@returns the number of examples in the training set.
	*/
	public int getNumTrainExamples() {
		return this.trainSet.length;
	}

	/**
	Return the number of examples in the testing set.
	@returns the number of examples in the testing set.
	*/
	public int getNumTestExamples() {
		return this.testSet.length;
	}

	/**
	Get the number of output features.
	@returns the number of output features.
	*/
	public int[] getOutputFeatures() {
		return outputColumns;
	}

	/**
	Get the number of output features.
	@returns the number of output features.
	*/
	public int getNumOutputFeatures() {
		return outputColumns.length;
	}

	/**
	Set the input features.
	@param inputs the indexes of the columns to be used as input features.
	*/
	public void setInputFeatures(int[] inputs) {
		inputColumns = inputs;
		ins = new Column[inputs.length];
		inputNames = new String[inputs.length];
		for (int i = 0; i < inputNames.length; i++) {
		  inputNames[i] = this.getColumnLabel(inputs[i]);
		  ins[i] = this.getColumn(i);
		}
	}

	/**
	Set the output features.
	@param outs the indexes of the columns to be used as output features.
	*/
	public void setOutputFeatures(int[] outCols) {
		outputColumns = outCols;
		outs = new Column[outCols.length];
		outputNames = new String[outs.length];
		for (int i = 0; i < outputNames.length; i++) {
			outputNames[i] = getColumnLabel(outCols[i]);
			outs[i] = this.getColumn(i);
		}
	}

	/**
	Set the indexes of the rows in the training set.
	@param trainingSet the indexes of the items to be used to train the model.
	*/
	public void setTrainingSet(int[] trainingSet) {
		trainSet = trainingSet;
	}

	/**
	Get the training set.
	@return the indices of the rows of the training set.
	*/
	public int[] getTrainingSet() {
		return trainSet;
	}

	/**
	Set the indexes of the rows in the testing set.
	@param testingSet the indexes of the items to be used to test the model.
	*/
	public void setTestingSet(int[] testingSet) {
		testSet = testingSet;
	}

	/**
	Get the testing set.
	@return the indices of the rows of the testing set.
	*/
	public int[] getTestingSet() {
		return testSet;
	}

	//////////////// Access the test train sets data ///////////////////
	/**
	 * This class provides transparent access to the test data only. The testSets
	 * field of the TrainTest table is used to reference only the test data, yet
	 * the getter methods look exactly the same as they do for any other table.
	 * @return a reference to a table referencing only the testing data
	 */
	public Table getTestTable() {
		if (testSet == null) {
			return null;
		}
		return new ExampleTableImpl(this, testSet);
	}

	/**
	Return a reference to a Table referencing only the training data.
	@return a reference to a Table referencing only the training data.
	*/
	public Table getTrainTable() {
		if (trainSet == null) {
			return null;
		}
		return new ExampleTableImpl(this, trainSet);
	}

	/////////////////////////////////////////
	// Input and output column accessor methods.
	//

	/**
	 * return a row object used to access each row, this instance is also an
	 * Example object providing access to input and output columns within a row
	 * specifically.
	 * @return a row accessor object.
	 */
	public Row getRow() {
		return new ExampleImpl(this);
	}

	/**
	 * Get the input double.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public double getInputDouble(int e, int i) {
		return ins[i].getDouble(subset[e]);
	}

	/**
	 * Get the output double.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public double getOutputDouble(int e, int i) {
		return outs[i].getDouble(subset[e]);
	}

	/**
	 * Get the input string.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public String getInputString(int e, int i) {
		return ins[i].getString(subset[e]);
	}

	/**
	 * Get the output string.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public String getOutputString(int e, int i) {
		return outs[i].getString(subset[e]);
	}
	/**
	 * Get the input int.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public int getInputInt(int e, int i) {
		return ins[i].getInt(subset[e]);
	}

	/**
	 * Get the output int.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public int getOutputInt(int e, int i) {
		return outs[i].getInt(subset[e]);
	}

	/**
	 * Get the input float.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public float getInputFloat(int e, int i) {
		return ins[i].getFloat(subset[e]);
	}

	/**
	 * Get the output float.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public float getOutputFloat(int e, int i) {
		return outs[i].getFloat(subset[e]);
	}

	/**
	 * Get the input short.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public short getInputShort(int e, int i) {
		return ins[i].getShort(subset[e]);
	}

	/**
	 * Get the output short.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public short getOutputShort(int e, int i) {
		return outs[i].getShort(subset[e]);
	}

	/**
	 * Get the input long.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public long getInputLong(int e, int i) {
		return ins[i].getLong(subset[e]);
	}

	/**
	 * Get the output long.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public long getOutputLong(int e, int i) {
		return outs[i].getLong(subset[e]);
	}

	/**
	 * Get the input byte.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public byte getInputByte(int e, int i) {
		return ins[i].getByte(subset[e]);
	}

	/**
	 * Get the output byte.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public byte getOutputByte(int e, int i) {
		return outs[i].getByte(subset[e]);
	}
	/**
	 * Get the input object.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public Object getInputObject(int e, int i) {
		return ins[i].getObject(subset[e]);
	}

	/**
	 * Get the output object.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public Object getOutputObject(int e, int i) {
		return outs[i].getObject(subset[e]);
	}
	/**
	 * Get the input char.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public char getInputChar(int e, int i) {
		return ins[i].getChar(subset[e]);
	}

	/**
	 * Get the output char.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public char getOutputChar(int e, int i) {
		return outs[i].getChar(subset[e]);
	}

	/**
	 * Get the input char array.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public char[] getInputChars(int e, int i) {
		return ins[i].getChars(subset[e]);
	}

	/**
	 * Get the output char array.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public char [] getOutputChars(int e, int i) {
		return outs[i].getChars(subset[e]);
	}

	/**
	 * Get the input byte array.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public byte[] getInputBytes(int e, int i) {
		return ins[i].getBytes(subset[e]);
	}

	/**
	 * Get the output byte array.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public byte[] getOutputBytes(int e, int i) {
		return outs[i].getBytes(subset[e]);
	}

	/**
	 * Get the input boolean.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public boolean getInputBoolean(int e, int i) {
		return ins[i].getBoolean(subset[e]);
	}

	/**
	 * Get the input boolean.
	 * @param e the row of the table containing the value
	 * @param i the column of the table containing the value
	 * @return the value at the row and column
	 */
	final public boolean getOutputBoolean(int e, int i) {
		return outs[i].getBoolean(subset[e]);
	}

	////////////////////////////////
	// Metadata methods.
	//
	public int getNumInputs(int e) {
		return inputColumns.length;
	}

	public int getNumOutputs(int e) {
		return outputColumns.length;
	}

	/*public Row getRow() {
	return null;
	}*/

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

	public String[] getInputNames() {
		return inputNames;
	}

	public String[] getOutputNames() {
		return outputNames;
	}
}