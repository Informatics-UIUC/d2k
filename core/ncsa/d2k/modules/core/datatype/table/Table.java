//package ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

/**
	Table is a data structure of m rows where each row has n Columns.  Therefore,
	a Table is a 2D and rectangular datastructure.  Each Column of a table
	contains elements of a single type.  Hence, any two elements of a particular
	Column will be castable to the same type.  Each row of a given Table
	represents a single record.  Thus, the synchronization of Columns is
	important to maintaining the records.  For example, if the order of one Column's
	elements are manipulated, then all other Columns must be likewise updated.
	<br><br>
	A table can have a key column associated with it.  The key column is the Column
	which should contain unique keys that uniquely identify the rows (records)
	within the Table.  This can be accessed with the get/setKeyColumn methods.
	If the key Column is not set, it should default to Column 0.
	<br><br>

	Table is designed for the primary use of linking/grouping (possibly
	variously-typed) data together.  So that the linked data (synchronized Columns)
	may be accessed and manipulated as a group.
	<br>
	Table was designed for 2 types of access or manipulation:
	<ul><li>Specific access, where the accessor knows the underlying data types
		and content.  This is enabled by convienience methods such as getFloat(row, col).
		Programmers will find this to the easiest way to use Table for a specific solution.
	<li>Generalized access, where the accessor has no knowledge of the underlying data types or content.
	This is enabled by a generalized and flexible class hierarchy which allows
	Columns to be manipulated in an efficient manner without knowledge of their
	underlying types or content, while programming for this generalized result is
	more complex and generally yields less efficient code, it yields a more flexible and
	extensible result.
	</ul>
*/
public interface Table extends java.io.Serializable {

	/**
		Get a Column from the table.
		@param pos the position of the Column to get from table
		@return the Column at in the table at pos
	*/
	public Column getColumn( int pos );

	/**
		Set a specified Column in the table.  If a Column exists at this
		position already, it will be replaced.  If position is beyond the capacity
		of this table then an ArrayIndexOutOfBounds will be thrown.
		@param newColumn the Column to be set in the table
		@param pos the position of the Column to be set in the table
	*/
	public void setColumn( Column newColumn, int pos );

	/**
		Add a new Column after the last occupied position in this table.  If
		this table's number of columns equals its capacity, then the capacity will be
		increased.
		@param newColumn the Column to be added to the table
	*/
	public void addColumn( Column newColumn );

	/**
		Remove a Column from the table.
		@param pos the position of the Column to remove
	*/
	public void removeColumn( int pos );

	/**
		Remove a range of Columns from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
	public void removeColumns( int start, int len );

	/**
		Insert a new Column at the indicated position in this Table.  All subsequent
		Columns will be moved.
		@param newColumn the new Column
		@param position the position at which to insert
	*/
	public void insertColumn( Column newColumn, int position );

	/**
		Set a specified element in the table.  If an element exists at this
		position already, it will be replaced.  If position is beyond the capacity
		of this table then an ArrayIndexOutOfBounds will be thrown.
		@param element the new element to be set in the table
		@param row the row to be changed in the table
		@param column the Column to be set in the given row
	*/
	public void setObject( Object element, int row, int column );

    /**
	 * Get an Object from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object at (row, column)
     */
	public Object getObject( int row, int column );

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
	public int getInt( int row, int column ) ;

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
	public short getShort( int row, int column ) ;

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
	public float getFloat( int row, int column ) ;

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
	public double getDouble( int row, int column ) ;

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
	public long getLong( int row, int column );

    /**
	 * Get a String value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
	public String getString( int row, int column ) ;

    /**
	 * Get a value from the table as an array of bytes.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
	public byte[] getBytes( int row, int column ) ;

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
	public boolean getBoolean( int row, int column );

    /**
	 * Get a value from the table as an array of chars.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
	public char[] getChars( int row, int column );

    /**
	 * Set an int value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setInt( int data, int row, int column ) ;

    /**
	 * Set a short value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setShort( short data, int row, int column ) ;

    /**
	 * Set a float value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setFloat( float data, int row, int column ) ;

    /**
	 * Set an double value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setDouble( double data, int row, int column ) ;

    /**
	 * Set a long value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setLong( long data, int row, int column );

    /**
	 * Set a String value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setString( String data, int row, int column ) ;

    /**
	 * Set a byte[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBytes( byte[] data, int row, int column ) ;

    /**
	 * Set a boolean value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBoolean( boolean data, int row, int column );

    /**
	 * Set a char[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setChars( char[] data, int row, int column );

	//////////////////////////////////////
	//// Accessing Table Metadata

	/**
		Return the index which represents the key column of this table.
		@return the key column index
	*/
	public int getKeyColumn( );

	/**
		Sets the key column index of this table.
		@param position the Column which is key for identifying unique rows
	*/
	public void setKeyColumn(int position);

	/**
		Returns the name associated with the Column.
		@param which the index of the Column name to get.
		@returns the name associated with the Column.
	*/
	public String getColumnLabel( int which );

	/**
		Set the name associated with a Column.
		@param label the new column label
		@param which the index of the Column to set
	*/
	public void setColumnLabel(String label, int which);

	/**
		Return the number of Columns this table holds.
		@return the capacity of the number of Columns in table
	*/
	public int getNumColumns();

    /**
    	Set the number of Columns this Table can hold
    	@param numCols the maximum number of Columns this Table can hold
     */
	public void setNumColumns(int numColumns);

	/**
		Sort the specified column and rearrange the rows of the table to
		correspond to the sorted column.
		@param col the column to sort by
		@throws NotSupportedException thrown when the column does not support
		sorting
	*/
	public void sortByColumn(int col) throws NotSupportedException;

	/**
		Swap the positions of two Columns.
		@param pos1 the first column to swap
		@param pos2 the second column to swap
	*/
	public void swapColumns(int pos1, int pos2);

	/**
		Get the label associated with this Table.
		@return the label which describes this Table
	*/
	public String getLabel( );

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	public void setLabel( String labl );

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	public String getComment( );

	/**
		Set the comment associated with this Table.
		@param comment the comment which describes this Table
	*/
	public void setComment( String comment );

	/**
	  	Get the number of rows in this Table.  Same as getCapacity().
		@return the number of rows in this Table.
	*/
	public int getNumRows( );

	/**
		Get the number of entries this Table holds.
		@return this Table's number of entries
	*/
	public int getNumEntries( );

	/**
		Get the capacity of this Table, its potential maximum number of entries.
		@return the maximum number of entries this Table can hold
	*/
	public int getCapacity( );

	/**
		Sets a new capacity for this Tagble.  The capacity is its potential
		maximum number of entries.  If numEntries > newCapacity then the Table
		may be truncated.
		@param newCapacity a new capacity
	*/
	public void setCapacity(int newCapacity);
	//////////////////////////////////////


	//////////////////////////////////////
	//// ACCESSING Column Elements
	/**
	 * Set the internal representation for this Table.
	 * @param newInternal the new internal representation for this Table.
	 */
	//public void setInternal( Object newInternal );

	/**
		Get an entry from the Table at the indicated position.
		@param pos the position
		@return the row at pos
	*/
	public Object[] getRow( int pos );

	/**
		Get a subset of this Table, given a start position and length.  The
		subset will be a new Table.
		@param pos the start position for the subset
		@param len the length of the subset
		@return a subset of this Table
	*/
	public Table getSubset( int pos, int len );

	/**
		Set the row at the given position to newEntry.
		@param newEntry a new entry
		@param pos the position to set
	*/
	public void setRow( Object[] newEntry, int pos );

	/**
		Get a copy of this Table reordered based on the input array of indexes.
		Does not overwrite this Table.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows re-ordered
	*/
	public Table reOrderRows( int[] newOrder );

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
	public void swapRows(int pos1, int pos2);

	/**
		Create a copy of this Table.
		@return a copy of this Table
	*/
	public Table copy();

	/**
	 * Remove a row from this Table.
	 * @param row the row to remove
	 * @return the Object held in the removed row
	 */
	public Object removeRow( int row );

	/**
	 * Add a row to the end of this Table.
	 * @param newEntry the Object to put into the new row
	 */
	public void addRow( Object[] newEntry );

	/**
	 * Insert a new row into this Table
	 * @param newEntry the Object to insert
	 * @param pos the position to insert the new row
	 */
	public void insertRow( Object[] newEntry, int pos );

	/**
	 * Remove rows from this Table using a boolean map.
	 * @param flags an array of booleans to map to this Table's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeByFlag( boolean[] flags );

	/**
	 * Remove rows from this Table by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeByIndex( int[] indices );

}/*Table*/