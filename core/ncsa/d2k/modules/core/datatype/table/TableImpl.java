package ncsa.d2k.modules.core.datatype.table;

import java.io.*;

/**
 TableImpl is an implementation of Table where each Column is represented by a
 subclass of the Column class so the Table is represented internally
 as vertical arrays of data (often primitives).
 <br>
 This is the first, most obvious, and probably most commonly used implementation
 of Table.
 <br>
 */
public class TableImpl extends AbstractTable {

	static final long serialVersionUID = -2649715462563902739L;

    protected Column[] columns = null;
    protected int keyColumn = 0;

    /**
    	Create a new, empty TableImpl.
     */
    public TableImpl () {
        columns = new Column[0];
    }

    /**
    	Create a TableImpl with the specified number of columns.
    	@param numColumns the number of columns
     */
    public TableImpl (int numColumns) {
        keyColumn = 0;
        columns = new Column[numColumns];
    }

    /**
    	Create a TableImpl with the specified columns
    	@param c the columns that make up the table
     */
    public TableImpl (Column[] c) {
        keyColumn = 0;
        columns = c;
    }

    /**
    	Create a TableImpl from a StaticDocument
    	@param sd the StaticDocument
     */
    /*public TableImpl (StaticDocument sd) {
        keyColumn = 0;
        int cols = sd.getNumColumns(0);
        columns = new Column[cols];
        this.fillTable(sd);
    }*/

    /**
    	Return an exact copy of this Table.  A deep copy
    	is attempted, but if it fails a new Table will be created,
    	initialized with the same data as this Table.
    	@return A new Table with a copy of the contents of this column.
     */
    public Table copy () {
        TableImpl vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (TableImpl)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new TableImpl(getNumColumns());
            vt.setKeyColumn(getKeyColumn());
            for (int i = 0; i < getNumColumns(); i++)
                vt.setColumn(getColumn(i).copy(), i);
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            //vt.setType(getType());
            return  vt;
        }
    }

    /**
    	Fill the initialized table with data from staticDoc.
    	If this table is not initialized to match staticDoc's structure and types... there will be problems...
		@param sd a static document
     */
    //public void fillTable (StaticDocument sd) {
        /*if(!staticDoc.isSquare())
         throw new NotSupportedException("StaticDocument is not square so it cannot be used to construct a new VerticalTable");
         else if( this.columns = null)
         else if( .... if this table initialized???
         //jjm*/
     //   int cols = sd.getNumColumns(0);
     //   int rows = sd.getNumRows();
     //   for (int c = 0; c < cols; c++) {
            //determine type->for now int, double, byteword
            /*jjm assuming byteword for now
             if( its a double )
             columns[c] = new DoubleColumn();
             else if( its an int )
             columns[c] = new IntColumn();
             else //its a byteword
             */
      //      {
       //         byte[][] byteCol = new byte[rows][];
       //         columns[c] = new ByteArrayColumn(rows);
        //        for (int r = 0; r < rows; r++)
       //             byteCol[r] = sd.getBytes(r, c);
       //         columns[c].setInternal(byteCol);
       //     }
     //   }
    //}

    //////////////////////////////////////
    //// ACCESSING Table Fields

	/**
		Return the number of Columns this table holds.
		@return the capacity of the number of Columns in table
	*/
    public int getNumColumns () {
        return  columns.length;
    }

    /**
    	Set the number of Columns this Table can hold
    	@param numCols the maximum number of Columns this table can hold
     */
    public void setNumColumns (int numCols) {
        if (columns == null)
            columns = new Column[numCols];
        else if (columns.length != numCols) {
            int numcopy = 0;
            if (columns.length < numCols)
                numcopy = columns.length;
            else
                numcopy = numCols;
            Column[] newColumns = new Column[numCols];
            System.arraycopy(columns, 0, newColumns, 0, numcopy);
            columns = newColumns;
        }
    }

	/**
		Get a Column from the table.
		@param pos the position of the Column to get from table
		@return the Column at in the table at pos
	*/
    public Column getColumn (int pos) {
        return  columns[pos];
    }

    /**
    	Set a specified Column in the table, if a Column exists at this
    	position already, it will be replaced.  If position is beyond
    	the capacity of this table then an ArrayIndexOutOfBounds
    	will be thrown.
    	@param newColumn the Column to be set in the table
    	@param pos the postion of the Column to be set in the table
     */
    public void setColumn (Column newColumn, int pos) {
        columns[pos] = newColumn;
    }

    /**
    	Returns the label associated with the Column at the indicated position
    	@param pos the index of the Column name to get.
    	@returns the name associated with the Column.
     */
    public String getColumnLabel (int pos) {
        String colLabel = columns[pos].getLabel();
        if (colLabel == null)
            return  "attribute_" + pos;
        else
            return  colLabel;
    }

    /**
    	Set the name associated with a Column.
    	@param label the new column label
    	@param pos the index of the Column to set
     */
    public void setColumnLabel (String label, int pos) {
        columns[pos].setLabel(label);
    }

	/**
		Add a new Column after the last occupied position in this table.  If
		this table's number of columns equals its capacity, then the capacity
		will be increased.
		@param newColumn the Column to be added to the table
	*/
    public void addColumn (Column newColumn) {
        //JJM add error checking for size
        Column[] newColumns = new Column[columns.length + 1];
        System.arraycopy(columns, 0, newColumns, 0, columns.length);
        newColumns[columns.length] = newColumn;
        columns = newColumns;
    }

    /**
    	Remove a Column from the Table
    	@param pos the position of the Column to remove
     */
    public void removeColumn (int pos) {
        Column[] newColumns = new Column[columns.length - 1];
        System.arraycopy(columns, 0, newColumns, 0, pos);
        System.arraycopy(columns, pos + 1, newColumns, pos, newColumns.length
                - pos);
        columns = newColumns;
    }

	/**
		Remove a range of Columns from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
    public void removeColumns (int start, int len) {
        Column[] newColumns = new Column[columns.length - len];
        System.arraycopy(columns, 0, newColumns, 0, start);
        System.arraycopy(columns, start + len, newColumns, start, newColumns.length
                - start);
        columns = newColumns;
    }

	/**
		Insert a new Column at the indicated position in this Table.  All subsequent
		Columns will be moved.
		@param newColumn the new Column
		@param position the position at which to insert
	*/
    public void insertColumn (Column newColumn, int position) {
        //JJM add error checking for size
        Column[] newColumns = new Column[columns.length + 1];
        System.arraycopy(columns, 0, newColumns, 0, position);
        newColumns[position] = newColumn;
        System.arraycopy(columns, position, newColumns, position + 1, columns.length
                - position);
        columns = newColumns;
    }

    //////////////////////////////////////
    /**
	 * Set an Object in the Table.
	 * @param element the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setObject (Object element, int row, int column) {
        columns[column].setRow(element, row);
    }

    /**
     Get an Object from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the Object in the Table at (row, column)
     */
    public Object getObject (int row, int column) {
        return  columns[column].getRow(row);
    }

    /**
     Get an int from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the int in the Table at (row, column)
     */
    public int getInt (int row, int column) {
        return  columns[column].getInt(row);
    }

    /**
	 * Set an int value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setInt (int data, int row, int column) {
        columns[column].setInt(data, row);
    }

    /**
     Get a short from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the short in the Table at (row, column)
     */
    public short getShort (int row, int column) {
        return  columns[column].getShort(row);
    }

    /**
	 * Set a short value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setShort (short data, int row, int column) {
        columns[column].setShort(data, row);
    }

    /**
     Get a long from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the long in the Table at (row, column)
     */
    public long getLong (int row, int column) {
        return  columns[column].getLong(row);
    }

    /**
	 * Set a long value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setLong (long data, int row, int column) {
        columns[column].setLong(data, row);
    }

    /**
     Get a float from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the float in the Table at (row, column)
     */
    public float getFloat (int row, int column) {
        return  columns[column].getFloat(row);
    }

    /**
	 * Set a float value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setFloat (float data, int row, int column) {
        columns[column].setFloat(data, row);
    }

    /**
     Get a double from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the float in the Table at (row, column)
     */
    public double getDouble (int row, int column) {
        return  columns[column].getDouble(row);
    }

    /**
	 * Set a double value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setDouble (double data, int row, int column) {
        columns[column].setDouble(data, row);
    }

    /**
     Get a String from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the String in the Table at (row, column)
     */
    public String getString (int row, int column) {
        return  columns[column].getString(row);
    }

    /**
	 * Set a String value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setString (String data, int row, int column) {
        columns[column].setString(data, row);
    }

    /**
     Get the bytes from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the bytes in the Table at (row, column)
     */
    public byte[] getBytes (int row, int column) {
        return  columns[column].getBytes(row);
    }

    /**
	 * Set a byte[] value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setBytes (byte[] data, int row, int column) {
        columns[column].setBytes(data, row);
    }

    /**
     Get a char[] from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the chars in the Table at (row, column)
     */
    public char[] getChars (int row, int column) {
        return  columns[column].getChars(row);
    }

    /**
	 * Set a char[] value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setChars (char[] data, int row, int column) {
        columns[column].setChars(data, row);
    }

    /**
     Get a boolean from the Table.
     @param row the position of the row to find the element
     @param column the column of row to be returned
     @return the boolean in the Table at (row, column)
     */
    public boolean getBoolean (int row, int column) {
        return  columns[column].getBoolean(row);
    }

    /**
	 * Set a boolean value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setBoolean (boolean data, int row, int column) {
        columns[column].setBoolean(data, row);
    }

    //////////////////////////////////////
    //// Accessing Table Metadata

	/**
		Return the index which represents the key column of this Table.
		@return the key column index
	*/
    public int getKeyColumn () {
        return  keyColumn;
    }

	/**
		Sets the key column index of this Table.
		@param position the Column which is key for identifying unique rows
	*/
    public void setKeyColumn (int position) {
        if (position <= columns.length && position >= 0)
            keyColumn = position;
        else
            throw  new ArrayIndexOutOfBoundsException();
    }

    //////////////////////////////////////

    /**
    	Get the number of rows this Table holds.
    	@return this Table's number of rows
     */
    public int getNumRows () {
        if (columns.length < 1)
            return  0;
        return  columns[0].getNumRows();
    }

	/**
		Get the number of entries this Table holds.  This simply delegates the
		call to getNumEntries() on the first Column in this Table.
		@return this Column's number of entries
	*/
	public int getNumEntries() {
		if (columns.length < 1)
			return 0;
		return columns[0].getNumEntries();
	}

    /**
		Get the capacity of this Table, its potential maximum number of Columns.
		@return the max number of Columns this Table can hold
     */
    public int getCapacity () {
        if (columns.length < 1)
            return  0;
        return  columns[0].getCapacity();
    }

	/**
		Sets a new capacity for this Table.  The capacity is its potential
		maximum number of Columns.  If numEntries is greater than newCapacity,
		the Table will be truncated.
		@param newCapacity a new capacity
	*/
    public void setCapacity (int newCapacity) {
        if (columns != null)
            for (int c = 0; c < columns.length; c++)
                if (columns[c] != null)
                    columns[c].setCapacity(newCapacity);
    }

	/**
		Get an entry from the Table at the indicated position.  This method will
		always return an Object[] containing the Objects that are in the table
		at the specified row.
		@param pos the position
		@return an Object[] containing the Objects that are in the table at the
		specified row
	*/
    public Object[] getRow (int pos) {
        Object[] rec = new Object[columns.length];
        for (int i = 0; i < columns.length; i++)
            rec[i] = columns[i].getRow(pos);
        return  rec;
    }

    /**
    	Gets a subset of this Table's rows, a cropped Table, given a start position and length.
    	@param pos the start position for the subset
    	@param len the length of the subset
    	@return a subset of this Table's rows
     */
    public Table getSubset (int pos, int len) {
        TableImpl subset = new TableImpl(columns.length);
        for (int i = 0; i < columns.length; i++)
            subset.setColumn((Column)columns[i].getSubset(pos, len), i);
        return  subset;
    }

    /**
    	Gets a reference to the internal representation of this Table.
    	Any changes made to this object will be reflected in the Table.
    	@return the internal representation of this Table.
     */
    //public Object getInternal () {
        //JJM is this unnecesary in interface?
     //   return  columns;
    //}

    /**
     Sets the reference to the internal representation of this Table.
     @param newInternal a new internal representation for this Table
     */
    public void setColumns (Column[] newColumns) {
        //JJM support this? should setInternal be Objectified?? or specific?
       columns = newColumns;
    }

	/**
		Set the entry at the given position to newRow.
		@param newRow a new entry
		@param pos the position to set
	*/
    public void setRow (Object[] newRow, int pos) {
        Object[] ne = (Object[])newRow;
        for (int i = 0; i < columns.length; i++)
            columns[i].setRow(ne[i], pos);
    }

	/**
	 * Add a row to the end of this Table.  An Object[] should be passed to
	 * this method, with an entry for each Column in the Table.
	 * @param newRow the Object to put into the new row
	 */
    public void addRow (Object[] newRow) {
        if (newRow instanceof Object[]) {
            Object[] toAdd = (Object[])newRow;
            for (int i = 0; i < toAdd.length; i++) {
                if (i <= getNumColumns())
                    getColumn(i).addRow(toAdd[i]);
            }
        }
        else
            System.out.println("An Object[] must be passed to " + "addRow().  The row was not added.");
    }

	/**
	 * Remove a row from this Table.
	 * @param pos the row to remove
	 * @return the Object held in the removed row
	 */
    public Object removeRow (int pos) {
        Object[] retVal = new Object[getNumColumns()];
        for (int i = 0; i < getNumColumns(); i++)
            retVal[i] = getColumn(i).removeRow(pos);
        return  retVal;
    }

	/**
	 * Insert a new row into this Table.  An Object[] should be passed to
	 * this method, with an entry for each Column in the Table.
	 * @param newRow the Object to insert
	 * @param pos the position to insert the new row
	 */
    public void insertRow (Object[] newRow, int pos) {
        if (newRow instanceof Object[]) {
            Object[] toAdd = (Object[])newRow;
            for (int i = 0; i < toAdd.length; i++) {
                if (i <= getNumColumns())
                    getColumn(i).insertRow(toAdd[i], pos);
            }
        }
        else
            System.out.println("An Object[] must be passed to " + "insertRow().  The row was not inserted.");
    }

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
    public void swapRows (int pos1, int pos2) {
        for (int i = 0; i < columns.length; i++) {
            Object Obj1 = columns[i].getRow(pos1);
            columns[i].setRow(columns[i].getRow(pos2), pos1);
            columns[i].setRow(Obj1, pos2);
        }
    }

	/**
		Swap the positions of two Columns.
		@param pos1 the first column to swap
		@param pos2 the second column to swap
	*/
    public void swapColumns (int pos1, int pos2) {
        Column temp = getColumn(pos1);
        setColumn(getColumn(pos2), pos1);
        setColumn(temp, pos2);
    }

    /**
    	Get a copy of this Table, reordered, based on the input array of indexes,
    	does not overwrite this Table.
    	@param newOrder an array of indices indicating a new order
		@return a copy of this table that has been reordered.
     */
    public Table reOrderRows (int[] newOrder) {
        TableImpl newTable = new TableImpl(columns.length);
        for (int i = 0; i < columns.length; i++)
            newTable.setColumn(columns[i].reOrderRows(newOrder), i);
        newTable.setLabel(getLabel());
        newTable.setComment(getComment());
        //newTable.setType(getType());
        return  newTable;
    }

    /**
    	Given an array of booleans, will remove the positions in the Table
    	which coorespond to the positions in the boolean array which are
    	marked true.  If the boolean array and Table do not have the same
    	number of rows, the remaining elements will be discarded.
    	@param flags the boolean array of remove flags
     */
    public void removeByFlag (boolean[] flags) {
        for (int i = 0; i < columns.length; i++)
            columns[i].removeByFlag(flags);
    }

    /**
    	Given an array of ints, will remove the positions in the Table
    	which are indicated by the ints in the array.
    	@param indices the int array of remove indices
     */
    public void removeByIndex (int[] indices) {
        for (int i = 0; i < columns.length; i++)
            columns[i].removeByIndex(indices);
    }

    //////////////////////////////////////
    /**
     Will print the contents of this Table to standard out.
     Each row of the Table will be printed to a separate line
     of standard out.
     <br>
     This method assumes there is a proper implementation of
     getString for every underlying column.  (note: this should never
     be a problem, as any implmentation of Column should be able to
     support a String rep)
     */
    public void print () {
        int rows = this.getNumRows();
        int cols = this.getNumColumns();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++)
                System.out.print(this.getString(r, c) + ", ");
            System.out.println(" ");
        }
    }

    /**
    	Sort a column and rearrange the rows of the Table accordingly.
    	The column must support sorting.
    	@param col the index of the column to sort
    	@throws NotSupportedException when sorting is not supported on
    	the specified column.
     */
    public void sortByColumn (int col) throws NotSupportedException {
        Column c = getColumn(col);
        if (c instanceof AbstractColumn) {
            if ((c instanceof NumericColumn) || (c instanceof TextualColumn))
                ((AbstractColumn)c).sort(this);
            else
                throw  new NotSupportedException();
        }
        else
            throw  new NotSupportedException();
    }
}

