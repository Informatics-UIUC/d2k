package ncsa.d2k.modules.core.datatype.table;

/**
	Column is an ordered list, generally associated with a table.  There are
	many implementations of Column optimized for different sets of tasks.
	<br><br>
	Column defines methods that are common to all implementations.  These
	include the insertion and deletion of rows and row reordering methods.
	Several methods are defined to provide metadata about the contents of a
	Column.  These include a label, a comment, and a data type.  Accessor
	methods are also defined for each primitive data type and for several
	common Object types used.
*/
public interface Column extends java.io.Serializable {
	//////////////////////////////////////
	//// Accessing Metadata

	/**
		Get the label associated with this Column.
		@return the label which describes this Column
	*/
	public String getLabel( );

	/**
		Set the label associated with this Column.
		@param labl the label which describes this Column
	*/
	public void setLabel( String labl );

	/**
		Get the comment associated with this Column.
		@return the comment which describes this Column
	*/
	public String getComment( );

	/**
		Set the comment associated with this Column.
		@param comment the comment which describes this Column
	*/
	public void setComment( String comment );

	/**
		Get the type associated with this Column.
		@return the type of data this Column holds
	*/
	public Object getType( );

	/**
		Set the type associated with this Column.
		@param tp the type of data this Column holds
	*/
	public void setType( Object tp );

	/**
	  	Get the number of rows in this Column.  Same as getCapacity().
		@return the number of rows in this Column.
	*/
	public int getNumRows( );

	/**
		Get the number of entries this Column holds.  This is the number of
		non-null entries in the Column.
		@return this Column's number of entries
	*/
	public int getNumEntries( );

	/**
		Get the capacity of this Column, its potential maximum number of entries.
		@return the maximum number of entries this Column can hold
	*/
	public int getCapacity( );

    /**
    	Get a String from this column at pos
    	@param pos the position from which to get a String
    	@return a String representation of the entry at that position
     */
	public String getString( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setString( String newEntry, int pos );

    /**
    	Get the value at pos as an int
    	@param pos the position
    	@return the int value of the item at pos
     */
	public int getInt( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setInt( int newEntry, int pos );

    /**
    	Get the value at pos as a short
    	@param pos the position
    	@return the short at pos
     */
	public short getShort( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setShort( short newEntry, int pos );

    /**
    	Get the value at pos as a long
    	@param pos the position
    	@return the value at pos as a long
     */
	public long getLong(int pos);

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setLong(long newEntry, int pos);

    /**
    	Get the value at pos as a double
    	@param pos the position
    	@return the value at pos as a double
     */
	public double getDouble( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setDouble( double newEntry, int pos );

    /**
    	Get the value at pos as a float
    	@param pos the position
    	@return the value at pos as a float
     */
	public float getFloat( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setFloat( float newEntry, int pos );

    /**
    	Get the value at pos as a byte array
    	@param pos the position
    	@return the value at pos as a byte array
     */
	public byte[] getBytes( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setBytes( byte[] newEntry, int pos );

    /**
    	Get the value at pos as a boolean
    	@param pos the position
    	@return the value at pos as a byte array
     */
	public boolean getBoolean( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setBoolean( boolean newEntry, int pos );

    /**
    	Get the value at pos as a char array
    	@param pos the position
    	@return the value at pos as a char array
     */
	public char[] getChars( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setChars( char[] newEntry, int pos );

    /**
    	Get the value at pos as an Object
    	@param pos the position
    	@return the value at pos as an Object
     */
	public Object getObject( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setObject( Object newEntry, int pos );

    /**
    	Sort the elements in this column.
    	@throws NotSupportedException when sorting is not supported
     */
	public void sort() throws NotSupportedException;

    /**
    	Sort the elements in this column, and swap the rows in the table
		it is a member of.
    	@param t the Table to swap rows for
    	@exception NotSupportedException when sorting is not supported
     */
	public void sort(Table t) throws NotSupportedException;

    /**
    	Compare the values of the object passed in and pos. Return 0 if they
    	are the same, greater than zero if element is greater,
		and less than zero if element is less.
    	@param element the object to be passed in should be a subclass of Number
    	@param pos the position of the element in Column to be compared with
    	@return a value representing the relationship- >, <, or == 0
     */
 	public int compareRows(Object element, int pos);

    /**
    	Compare pos1 and pos2 positions in the Column. Return 0 if they
    	are the same, greater than zero if pos1 is greater,
		and less than zero if pos1 is less.
    	@param p1 the position of the first element to compare
    	@param p2 the position of the second element to compare
    	@return a value representing the relationship- >, <, or == 0
     */
	public int compareRows(int p1, int p2);

	/**
		Sets a new capacity for this Column.  The capacity is its potential
		maximum number of entries.  If numEntries is greater than newCapacity
		then the Column will be truncated.
		@param newCapacity a new capacity
	*/
	public void setCapacity(int newCapacity);

	//////////////////////////////////////
	//// ACCESSING Column Elements
	/**
	 * Set the internal representation for this column.
	 * @param newInternal the new internal representation for this column.
	 */
	//public void setInternal( Object newInternal );

	/**
		Get an entry from the Column at the indicated position.
		@param pos the position
		@return the entry at pos
	*/
	public Object getRow( int pos );

	/**
		Get a subset of this Column, given a start position and length.  The
		subset will be a new Column.
		@param pos the start position for the subset
		@param len the length of the subset
		@return a subset of this Column
	*/
	public Column getSubset( int pos, int len );

	/**
		Set the entry at the given position to newEntry.
		@param newEntry a new entry
		@param pos the position to set
	*/
	public void setRow( Object newEntry, int pos );

	/**
		Get a copy of this Column reordered based on the input array of indexes.
		Does not overwrite this Column.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows re-ordered
	*/
	public Column reOrderRows( int[] newOrder );

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
	public void swapRows(int pos1, int pos2);

	/**
		Create a copy of this Column.
		@return a copy of this column
	*/
	public Column copy();

	/**
	 * Remove a row from this Column.
	 * @param row the row to remove
	 * @return the Object held in the removed row
	 */
	public Object removeRow( int row );

	/**
	 * Add a row to the end of this column
	 * @param newEntry the Object to put into the new row
	 */
	public void addRow( Object newEntry );

	/**
	 * Insert a new row into this Column
	 * @param newEntry the Object to insert
	 * @param pos the position to insert the new row
	 */
	public void insertRow( Object newEntry, int pos );

	/**
	 * Remove rows from this column using a boolean map.
	 * @param flags an array of booleans to map to this column's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeByFlag( boolean[] flags );

	/**
	 * Remove rows from this column by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeByIndex( int[] indices );

}/*Column*/
