/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.IOException;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * @author redman
 */
public class ExamplePagingTable extends SubsetPagingTable implements ExampleTable {

	/** this is the indices of the output columns. */
	int [] outputIndices = new int[0];
	
	/** the output columns themselves. */
	Column [] outputColumns = new Column[0];
	
	/** this is the indices of the input columns. */
	int [] inputIndices = new int[0];
	
	/** the input columns. */
	Column [] inputColumns = new Column[0];
	
	/** the training set. */
	int [] trainSet = new int[0];
	
	/** the testing set. */
	int [] testSet = new int[0];
	
	/**
	 * default empty paging table.
	 *
	 */
	public ExamplePagingTable() {
		Page [] pages = new Page[1];
		int [] offsets = new int [1];
		offsets[0] = 0;
		try {
			pages[0] = new Page(new MutableTableImpl(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cache = new PageCache(pages, offsets, DEFAULT_PAGESIZE);
		this.offset = -1;
	}
	
	/**
	 * Given only the page cache. the first data access will cause a page fault.
	 * @param pager
	 */
	public ExamplePagingTable(PageCache pager) {
		this.cache = pager;
		this.offset = -1;
		this.getPage(0);
	}
		
	///////////////////////////////////////
	// Methods for dealing with the subseting and
	// input output identification.
	//////////////////////////////////////
	
	/**
	 * @return the array of the indices of the inputs features.
	 */
	public int[] getInputFeatures() {
		return inputIndices;
	}
	
	/**
	 * @return the number of input features.
	 */
	public int getNumInputFeatures() {
		return inputIndices.length;
	}
	
	/**
	 * @return the number of train examples.
	 */
	public int getNumTrainExamples() {
		return trainSet.length;
	}
	
	/**
	 * @return the number of test examples.
	 */
	public int getNumTestExamples() {
		return testSet.length;
	}

	/**
	 * @return the array of indices of output features.
	 */
	public int[] getOutputFeatures() {
		return outputIndices;
	}
	
	/**
	 * @return the number of output features.
	 */
	public int getNumOutputFeatures() {
		return outputIndices.length;
	}
	
	/**
	 * When we set the array of input features, we will also need to
	 * set the input column array up.
	 * @param inputs the new inputs.
	 */
	public void setInputFeatures(int[] inputs) {
		inputIndices = inputs;
		inputColumns = new Column[inputs.length];
		if (currentPage != null) {
			// we have a page, update the input columns.
			for (int i = 0; i < inputs.length; i++) {
				inputColumns[i] = table.getColumn(inputs[i]);
			}
		}
	}
	
	/**
	 * When we set the array of output features, we will also need to
	 * set the output column array up.
	 * @param output the new inputs.
	 */
	public void setOutputFeatures(int[] outs) {
		outputIndices = outs;
		this.updateOutputFeatures();
	}
	
	/**
	 * set the indices of the training set.
	 */
	public void setTrainingSet(int[] trainingSet) {
		this.trainSet = trainingSet;
	}
	
	/**
	 * @return the indices of the training set.
	 */
	public int[] getTrainingSet() {
		return trainSet;
	}
	
	/**
	 * set the indices of the training set.
	 */
	public void setTestingSet(int[] trainingSet) {
		this.testSet = trainingSet;
	}
	
	/**
	 * @return the indices of the training set.
	 */
	public int[] getTestingSet() {
		return testSet;
	}

	//////////////////////////////////////////////////////////
	// Methods overriden from SubsetPagingTable to update
	// the input and output columns when we page.
	//////////////////////////////////////////////////////////
	
	/**
	 * Update the output columns array when we page in or change
	 * which features are outputs.
	 */
	private void updateOutputFeatures() {
		if (outputColumns.length != outputIndices.length)
			outputColumns = new Column[outputIndices.length];
			
		if (currentPage != null) {
			// we have a page, update the input columns.
			for (int i = 0; i < outputIndices.length; i++) {
				outputColumns[i] = table.getColumn(outputIndices[i]);
			}
		}

	}
	/**
	 * Update the input columns array when we page in or change
	 * which features are outputs.
	 */
	private void updateInputFeatures() {
		
		// reallocate the array if needed.
		if (inputColumns.length != inputIndices.length)
			inputColumns = new Column[inputIndices.length];
		
		// if we have a page, update the column array.
		if (currentPage != null) {
			
			// we have a page, update the input columns.
			for (int i = 0; i < inputIndices.length; i++) {
				inputColumns[i] = table.getColumn(inputIndices[i]);
			}
		}

	}
	/**
	 * Grab the next page and init the columns.
	 * @param where the row we are to access next.
	 */
	protected void getPage(int where) {
		currentPage = cache.getPageAt(where, offset);
		this.table = currentPage.getTable();
		this.subset = currentPage.getSubset();
		this.offset = cache.getOffsetAt(where);
		columns = ((TableImpl)table).getRawColumns();
		this.updateInputFeatures();
		this.updateOutputFeatures();
	}
	
	/**
	 * Refresh the page we are on now. This is done when the table has changed,
	 * and we need to update our fields.
	 */
	protected void refresh() {
		if (currentPage == null) {
			this.getPage(0);
		}
		this.table = currentPage.getTable();
		this.subset = currentPage.getSubset();
		this.offset = cache.getOffsetAt(offset);
		columns = ((TableImpl)table).getRawColumns();
		this.updateInputFeatures();
		this.updateOutputFeatures();
	}
	
	/////////////////////////////////////////////////
	// Getter methods.
	/////////////////////////////////////////////////
	
	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public double getInputDouble(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getDouble(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getDouble(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public double getOutputDouble(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getDouble(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getDouble(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public String getInputString(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getString(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getString(where);
		}
	}
	
	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public String getOutputString(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getString(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getString(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public int getInputInt(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getInt(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getInt(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public int getOutputInt(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getInt(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getInt(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public float getInputFloat(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getFloat(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getFloat(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public float getOutputFloat(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getFloat(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getFloat(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public short getInputShort(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getShort(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getShort(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public short getOutputShort(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getShort(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getShort(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public long getInputLong(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getLong(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getLong(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public long getOutputLong(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getLong(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getLong(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public byte getInputByte(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getByte(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getByte(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public byte getOutputByte(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getByte(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getByte(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public Object getInputObject(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getObject(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getObject(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public Object getOutputObject(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getObject(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getObject(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public char getInputChar(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getChar(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getChar(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public char getOutputChar(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getChar(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getChar(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public byte[] getInputBytes(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getBytes(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getBytes(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public byte[] getOutputBytes(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getBytes(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getBytes(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public char[] getInputChars(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getChars(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getChars(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public char[] getOutputChars(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getChars(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getChars(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public boolean getInputBoolean(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return inputColumns[column].getBoolean(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return inputColumns[column].getBoolean(where);
		}
	}

	/**
	 * Get the input double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data.
	 */
	public boolean getOutputBoolean(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return outputColumns[column].getBoolean(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return outputColumns[column].getBoolean(where);
		}
	}


	public String getInputName(int i) {
		return null;
	}

	//////////////////////////////////////////
	// Column info accessor methods.
	//////////////////////////////////////////
	
	/**
	 * The labels are gotten from the cache.
	 */
	public String getOutputName(int o) {
		return cache.getColumnLabel(outputIndices[0]);
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getInputType(int)
	 */
	public int getInputType(int i) {
		return inputColumns[i].getType();
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getOutputType(int)
	 */
	public int getOutputType(int o) {
		return outputColumns[0].getType();
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#hasMissingInputsOutputs()
	 */
	public boolean hasMissingInputsOutputs() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#isInputNominal(int)
	 */
	public boolean isInputNominal(int i) {
		return cache.isColumnNominal(inputIndices[i]);
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#isOutputNominal(int)
	 */
	public boolean isOutputNominal(int o) {
		return cache.isColumnNominal(outputIndices[o]);
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#isInputScalar(int)
	 */
	public boolean isInputScalar(int i) {
		return cache.isColumnScalar(inputIndices[i]);
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#isOutputScalar(int)
	 */
	public boolean isOutputScalar(int o) {
		return cache.isColumnScalar(outputIndices[o]);
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getInputNames()
	 */
	public String[] getInputNames() {
		String [] inputNames = new String [inputIndices.length];
		for (int i = 0; i < inputNames.length; i++) {
			inputNames[i] = cache.getColumnLabel(inputIndices[i]);
		}
		return inputNames;
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getOutputNames()
	 */
	public String[] getOutputNames() {
		String [] outputNames = new String [outputIndices.length];
		for (int i = 0; i < outputNames.length; i++) {
			outputNames[i] = cache.getColumnLabel(outputIndices[i]);
		}
		return outputNames;
	}
	
	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getTestTable()
	 */
	public Table getTestTable() {
		try {
			return new ExamplePagingTable(cache.subset(this.testSet));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#getTrainTable()
	 */
	public Table getTrainTable() {
		try {
			return new ExamplePagingTable(cache.subset(this.trainSet));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.ExampleTable#toPredictionTable()
	 */
	public PredictionTable toPredictionTable() {
		try {
			return new PredictionPagingTable(this);
		} catch (Exception e) {
			throw new RuntimeException("There was a problem with the disk IO system, and "+
				"the paging prediction table could not be created. Check disk space.");
		}
	}
	
	/**
	 * make a copy of the tables internal data structures but not the data, or in
	 * this case the pages. Return a reference to the new table structure.
	 */
	public Table shallowCopy() {
		ExamplePagingTable ept = new ExamplePagingTable(cache);
		ept.outputIndices = this.outputIndices;
		ept.outputColumns = this.outputColumns;
		ept.inputIndices = this.inputIndices;
		ept.inputColumns = this.inputColumns;
		ept.trainSet = this.trainSet;
		ept.testSet = this.testSet;
		return ept;
	}
}
