//package ncsa.d2k.modules.projects.clutter.weighted;
package ncsa.d2k.modules.core.datatype.table.weighted;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <p>Title: WeightedPredictionTable </p>
 * <p>Description: WeightedPredictionTable</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Sameer Mathur
 * @version 1.0
 */

public class WeightedTestTable extends WeightedPredictionTable implements TestTable {

    private ExampleTable originalExampleTable;

    /**
     Given an WeightedPredictionTable, create a WeightedTestTable
    @param ttt the prediction table that this test table is derived from
     /
    WeightedTestTable (WeightedExampleTable ttt) {
        super(ttt);
    }*/

    /**
     Given an example table, copy it's input columns, and create new
     columns to hold the predicted values.
    @param ttt the prediction table that this test table is derived from
     */
    WeightedTestTable (WeightedExampleTable ttt) {
        super(ttt);
    }

    /*public PredictionTable toPredictionTable() {
        return this;
    }*/

    /**
     * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
     */
    public ExampleTable getExampleTable (){
        //return new WeightedExampleTable(this);
        return originalExampleTable;
    }

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt (int row, int column){
        //int ro = rowIndices[testSet[row]];
/*        int ro = testSet[row];
        return super.getInt(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getInt(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getInt(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort (int row, int column){
        //int ro = rowIndices[testSet[row]];
/*        int ro = testSet[row];
        return super.getShort(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getShort(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getShort(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
    public long getLong (int row, int column){
/*        int ro = rowIndices[testSet[row]];
        return super.getLong(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getLong(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getLong(row,
                                      column - original.getNumColumns());
        }
    }

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat (int row, int column){
/*        int ro = rowIndices[testSet[row]];
        return super.getFloat(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getFloat(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getFloat(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble (int row, int column){
        //int ro = rowIndices[testSet[row]];
        /*int ro = testSet[row];
        return super.getDouble(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getDouble(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getDouble(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Strings from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String value at (row, column)
     */
    public String getString(int row, int column) {
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getString(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getString(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get an array of bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes (int row, int column){
        /*int ro = rowIndices[testSet[row]];
        return super.getBytes(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getBytes(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getBytes(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get an array of chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars (int row, int column){
        /*int ro = rowIndices[testSet[row]];
        return super.getChars(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getChars(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getChars(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean (int row, int column){
        /*int ro = rowIndices[testSet[row]];
        return super.getBoolean(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getBoolean(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getBoolean(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
    public byte getByte (int row, int column){
/*        int ro = rowIndices[testSet[row]];
        return super.getByte(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getByte(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getByte(row,
                                             column - original.getNumColumns());
        }
    }

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
    public char getChar (int row, int column){
/*        int ro = rowIndices[testSet[row]];
        return super.getChar(ro, column);
        */
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getChar(testSet[row], column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getChar(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     Get the number of entries in the train set.
     @return the size of the train set
     */
    public int getNumRows (){
        return this.testSet.length;
    }

}//WeightedTestTable