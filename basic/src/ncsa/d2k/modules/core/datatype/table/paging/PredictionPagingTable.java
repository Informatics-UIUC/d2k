/*
 * Created on Jun 7, 2004
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.IOException;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

/**
 * The paging prediction table will include and example table, and it will itself
 * be another paging table which only contains the prediction columns.
 * @author redman
 */
public class PredictionPagingTable extends ExamplePagingTable implements PredictionTable {
	SubsetPagingTable predictionTable;

	/**
	 * Construct a paging prediction table from it's example table.
	 * @param et the example table.
	 */
	PredictionPagingTable(ExamplePagingTable et) throws IOException {
		super(et.cache);
		this.outputIndices = et.outputIndices;
		this.outputColumns = et.outputColumns;
		this.inputIndices = et.inputIndices;
		this.inputColumns = et.inputColumns;
		this.trainSet = et.trainSet;
		this.testSet = et.testSet;
		this.subset = et.subset;

		// Construct the prediction table.
		int numPredictions = et.getNumOutputFeatures();
		int numRows = et.getNumRows();
		int pagesPerRow = et.cache.defaultPageSize;
		int numTables = numRows / pagesPerRow;
		if (numTables != (numRows * pagesPerRow))
			numTables++;

		// These pages comprise the prediction table.
		Page[] pages = new Page[numTables];
		int[] offset = new int[numTables];
		for (int whichTable = 0; whichTable < numTables; whichTable++) {
			Column [] cols = new Column[numPredictions];
			for (int i = 0; i < numPredictions; i++) {
				cols[i] = ColumnUtilities.createColumn(et.outputColumns[i].getType(),
					 et.getNumRows());
			}
			MutableTableImpl mti = new MutableTableImpl(cols);
			pages[whichTable] = new Page(mti, false);
			offset[whichTable] = whichTable * pagesPerRow;
		}

		// Create a paging table, check it's performance.
		predictionTable = new SubsetPagingTable(new PageCache(pages, offset, pagesPerRow));
	}


	public int[] getPredictionSet() {
		return null;
	}

	/**
	 * You can not set the prediction set in a paging prediction table,
	 * we will throw a runtime exception to prove it.
	 */
	public void setPredictionSet(int[] p) {
		throw new RuntimeException (
			"You can not set the prediction set in a paging prediction table.");
	}

	/**
	 * Set the int prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setIntPrediction(int prediction, int row, int predictionColIdx) {
		predictionTable.setInt(prediction, row, predictionColIdx);
	}

	/**
	 * Set the float prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
		predictionTable.setFloat(prediction, row, predictionColIdx);
	}

	/**
	 * Set the double prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
		predictionTable.setDouble(prediction, row, predictionColIdx);
	}

	/**
	 * Set the long prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setLongPrediction(long prediction, int row, int predictionColIdx) {
		predictionTable.setLong(prediction, row, predictionColIdx);
	}

	/**
	 * Set the short prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setShortPrediction(short prediction, int row, int predictionColIdx) {
		predictionTable.setShort(prediction, row, predictionColIdx);
	}

	/**
	 * Set the boolean prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
		predictionTable.setBoolean(prediction, row, predictionColIdx);
	}

	/**
	 * Set the string prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setStringPrediction(String prediction, int row, int predictionColIdx) {
		predictionTable.setString(prediction, row, predictionColIdx);
	}

	/**
	 * Set the chars prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
		predictionTable.setChars(prediction, row, predictionColIdx);
	}

	/**
	 * Set the bytes prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
		predictionTable.setBytes(prediction, row, predictionColIdx);
	}

	/**
	 * Set the Object prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
		predictionTable.setObject(prediction, row, predictionColIdx);
	}

	/**
	 * Set the byte prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
		predictionTable.setByte(prediction, row, predictionColIdx);
	}

	/**
	 * Set the char prediction.
	 * @param prediction the value.
	 * @param row the row to set.
	 * @param predictionColIdx the index of the prediction column.
	 */
	public void setCharPrediction(char prediction, int row, int predictionColIdx) {
		predictionTable.setChar(prediction, row, predictionColIdx);
	}

	/**
	 * Get the int prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public int getIntPrediction(int row, int predictionColIdx) {
		return predictionTable.getInt(row, predictionColIdx);
	}

	/**
	 * Get the float prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public float getFloatPrediction(int row, int predictionColIdx) {
		return predictionTable.getFloat(row, predictionColIdx);
	}

	/**
	 * Get the double prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public double getDoublePrediction(int row, int predictionColIdx) {
		return predictionTable.getDouble(row, predictionColIdx);
	}

	/**
	 * Get the long prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public long getLongPrediction(int row, int predictionColIdx) {
		return predictionTable.getLong(row, predictionColIdx);
	}

	/**
	 * Get the short prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public short getShortPrediction(int row, int predictionColIdx) {
		return predictionTable.getShort(row, predictionColIdx);
	}

	/**
	 * Get the boolean prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public boolean getBooleanPrediction(int row, int predictionColIdx) {
		return predictionTable.getBoolean(row, predictionColIdx);
	}

	/**
	 * Get the string prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public String getStringPrediction(int row, int predictionColIdx) {
		return predictionTable.getString(row, predictionColIdx);
	}

	/**
	 * Get the char array prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public char[] getCharsPrediction(int row, int predictionColIdx) {
		return predictionTable.getChars(row, predictionColIdx);
	}

	/**
	 * Get the byte array prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public byte[] getBytesPrediction(int row, int predictionColIdx) {
		return predictionTable.getBytes(row, predictionColIdx);
	}

	/**
	 * Get the object prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public Object getObjectPrediction(int row, int predictionColIdx) {
		return predictionTable.getObject(row, predictionColIdx);
	}

	/**
	 * Get the byte prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public byte getBytePrediction(int row, int predictionColIdx) {
		return predictionTable.getByte(row, predictionColIdx);
	}

	/**
	 * Get the char prepresentation of the value at the given row and column.
	 * @param row the index of the row where the prediction can be found.
	 * @param predictionColIdx the index of the column.
	 * @returns the value at row and predictionColIdx.
	 */
	public char getCharPrediction(int row, int predictionColIdx) {
		return predictionTable.getChar(row, predictionColIdx);
	}

}
