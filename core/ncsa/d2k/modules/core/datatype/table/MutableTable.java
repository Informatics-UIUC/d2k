package ncsa.d2k.modules.core.datatype.table;

import java.util.*;

/**
 * MutableTable defines methods used to mutate the contents of a Table.
 */
public interface MutableTable extends Table {

static final long serialVersionUID = 1505481703513638163L;

	/**
	 * Add a row to the end of this Table, initialized with integer data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(int[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with float data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(float[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with double data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(double[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with long data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(long[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with short data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(short[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with boolean data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(boolean[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with String data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(String[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with char[] data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(char[][] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with byte[] data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(byte[][] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with Object[] data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(Object[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with byte data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(byte[] newEntry);

	/**
	 * Add a row to the end of this Table, initialized with char data.
	 * @param newEntry the data to put into the new row.
	 */
	public void addRow(char[] newEntry);

	/**
	 * Insert a new row into this Table, initialized with integer data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(int[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with float data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(float[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with double data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(double[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with long data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(long[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with short data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(short[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with boolean data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(boolean[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with String data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(String[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with char[] data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(char[][] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with byte[] data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(byte[][] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with Object data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(Object[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with byte data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(byte[] newEntry, int position);

	/**
	 * Insert a new row into this Table, initialized with char data.
	 * @param newEntry the data to put into the inserted row.
	 * @param position the position to insert the new row
	 */
	public void insertRow(char[] newEntry, int position);

	/**
	 * Add a new column to the end of this table, initialized with integer data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(int[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with float data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(float[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with double data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(double[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with long data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(long[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with short data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(short[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with boolean data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(boolean[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with String data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(String[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with char[] data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(char[][] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with byte[] data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(byte[][] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with Object data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(Object[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with byte data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(byte[] newEntry);

	/**
	 * Add a new column to the end of this table, initialized with char data.
	 * @param newEntry the data to initialize the column with.
	 */
	public void addColumn(char[] newEntry);

	/**
	 * Insert a new column into this Table, initialized with integer data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(int[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with float data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(float[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with double data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(double[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with long data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(long[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with short data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(short[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with boolean data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(boolean[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with String data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(String[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with char[] data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(char[][] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with byte[] data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(byte[][] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with Object data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(Object[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with byte data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(byte[] newEntry, int position);

	/**
	 * Insert a new column into this Table, initialized with char data.
	 * @param newEntry the initial values of the new column.
	 * @param position the position to insert the new row
	 */
	public void insertColumn(char[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of int data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(int[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of float data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(float[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of double data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(double[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of long data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(long[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of short data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(short[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of boolean data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(boolean[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of String data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(String[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of char[] data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(char[][] newEntry, int position);

	/**
	 * Set the row at the given position to an array of byte[] data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(byte[][] newEntry, int position);

	/**
	 * Set the row at the given position to an array of Object data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(Object[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of byte data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(byte[] newEntry, int position);

	/**
	 * Set the row at the given position to an array of char data.
	 *	@param newEntry the new values of the row.
	 *	@param position the position to set
	 */
	public void setRow(char[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of int data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(int[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of float data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(float[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of double data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(double[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of long data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(long[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of short data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(short[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of boolean data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(boolean[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of String data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(String[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of char data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(char[][] newEntry, int position);

	/**
	 * Set the column at the given position to an array of byte[] data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(byte[][] newEntry, int position);

	/**
	 * Set the column at the given position to an array of Object data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(Object[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of byte data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(byte[] newEntry, int position);

	/**
	 * Set the column at the given position to an array of char data.
	 *	@param newEntry the new values of the column.
	 *	@param position the position to set
	 */
	public void setColumn(char[] newEntry, int position);

	/**
		Remove a column from the table.
		@param position the position of the Column to remove
	*/
	public void removeColumn(int position);

	/**
		Remove a range of columns from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
	public void removeColumns(int start, int len);

	/**
	 * Remove a row from this Table.
	 * @param row the row to remove
	 */
	public void removeRow(int row);

	/**
		Remove a range of rows from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
	public void removeRows(int start, int len);

	/**
	 * Remove rows from this Table using a boolean map.
	 * @param flags an array of booleans to map to this Table's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeRowsByFlag(boolean[] flags);

	/**
	 * Remove rows from this Table using a boolean map.
	 * @param flags an array of booleans to map to this Table's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeColumnsByFlag(boolean[] flags);

	/**
	 * Remove rows from this Table by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeRowsByIndex(int[] indices);

	/**
	 * Remove rows from this Table by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeColumnsByIndex(int[] indices);

	/**
		Get a copy of this Table reordered based on the input array of indexes.
		Does not overwrite this Table.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows reordered
	*/
	public Table reorderRows(int[] newOrder);

	/**
		Get a copy of this Table reordered based on the input array of indexes.
		Does not overwrite this Table.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows reordered
	*/
	public Table reorderColumns(int[] newOrder);

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
	public void swapRows(int pos1, int pos2);

	/**
		Swap the positions of two columns.
		@param pos1 the first column to swap
		@param pos2 the second column to swap
	*/
	public void swapColumns(int pos1, int pos2);

	/**
		Set a specified element in the table.  If an element exists at this
		position already, it will be replaced.  If the position is beyond the capacity
		of this table then an ArrayIndexOutOfBounds will be thrown.
		@param element the new element to be set in the table
		@param row the row to be changed in the table
		@param column the Column to be set in the given row
	*/
	public void setObject(Object element, int row, int column);

    /**
	 * Set an int value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setInt(int data, int row, int column);

    /**
	 * Set a short value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setShort(short data, int row, int column);

    /**
	 * Set a float value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setFloat(float data, int row, int column);

    /**
	 * Set an double value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setDouble(double data, int row, int column);

    /**
	 * Set a long value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setLong(long data, int row, int column);

    /**
	 * Set a String value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setString(String data, int row, int column);

    /**
	 * Set a byte[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBytes(byte[] data, int row, int column);

    /**
	 * Set a boolean value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBoolean(boolean data, int row, int column);

    /**
	 * Set a char[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setChars(char[] data, int row, int column);

    /**
	 * Set a byte value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setByte(byte data, int row, int column);

    /**
	 * Set a char value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setChar(char data, int row, int column);

	/**
		Set the name associated with a column.
		@param label the new column label
		@param position the index of the column to set
	*/
	public void setColumnLabel(String label, int position);

	/**
		Set the comment associated with a column.
		@param comment the new column comment
		@param position the index of the column to set
	*/
	public void setColumnComment(String comment, int position);

    /**
    	Set the number of columns this Table can hold.
    	@param numColumns the number of columns this Table can hold
     */
	public void setNumColumns(int numColumns);

	/**
		Sort the specified column and rearrange the rows of the table to
		correspond to the sorted column.
		@param col the column to sort by
	*/
	public void sortByColumn(int col);

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
	   @param col the index of the column to sort
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sortByColumn(int col, int begin, int end);

	/**
		Sets a new capacity for this Table.  The capacity is its potential
		maximum number of entries.  If numEntries is greater than newCapacity,
		then the Table may be truncated.
		@param newCapacity a new capacity
	*/
	public void setNumRows(int newCapacity);

    /////////// Collect the transformations that were performed. /////////
    /**
     Add the transformation to the list.
     @param tm the Transformation that performed the reversable transform.
     */
    public void addTransformation (Transformation tm);

    /**
     Returns the list of all reversable transformations there were performed
     on the original dataset.
     @returns an ArrayList containing the Transformation which transformed the data.
     */
    public ArrayList getTransformations ();
}
