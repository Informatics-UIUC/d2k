package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;


/**
 * TableImpl is an implementation of Table where each Column is represented by a
 * subclass of the Column class.  The Table is represented internally as
 * vertical arrays of data (often primitives).
 * <br>
 * This is the first, most obvious, and probably most commonly used implementation
 * of Table.
 * <br>
 */
public class TableImpl extends AbstractTable implements MutableTable {

	private static TableFactory tableFactory = DefaultTableFactory.getInstance();

    protected Column[] columns = null;

	/**
	 * Create a new Table with zero columns.
	 */
    public TableImpl () {
        columns = new Column[0];
    }

    /**
	 * Create a new Table with the specified number of columns.  Space for the
	 * columns is created, but the columns themselves will be null.
	 * @param numColumns the initial number of columns
     */
    public TableImpl (int numColumns) {
		setKeyColumn(0);
        columns = new Column[numColumns];
    }

    /**
	 * Create a new Table with the specified columns.
	 * @param c the initial columns
     */
    public TableImpl (Column[] c) {
		setKeyColumn(0);
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
     * Return an exact copy of this Table.  A deep copy
     * is attempted, but if it fails a new Table will be created,
     * initialized with the same data as this Table.
   	 * @return A new Table with a copy of the contents of this column.
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
	 * Return the number of Columns this table holds.
	 * @return the capacity of the number of Columns in table
	*/
    public int getNumColumns () {
        return  columns.length;
    }

    /**
     * Set the number of Columns this Table can hold
     * @param numCols the maximum number of Columns this table can hold
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
	 * Get a Column from the table.
	 * @param pos the position of the Column to get from table
	 * @return the Column at in the table at pos
	 */
    public Column getColumn (int pos) {
        return  columns[pos];
    }

    /**
     * Set a specified Column in the table.  If a Column exists at this
     * position already, it will be replaced.  If position is beyond
     * the capacity of this table then an ArrayIndexOutOfBoundsException
     * will be thrown.
     * @param newColumn the Column to be set in the table
     * @param pos the postion of the Column to be set in the table
     */
    public void setColumn (Column newColumn, int pos) {
        columns[pos] = newColumn;
    }

    /**
     * Returns the label associated with the Column at the indicated position.
     * @param pos the index of the Column name to get.
     * @returns the name associated with the Column.
     */
    public String getColumnLabel (int pos) {
        String colLabel = columns[pos].getLabel();
        if (colLabel == null)
            return  "column_" + pos;
        else
            return  colLabel;
    }

    /**
     * Set the name associated with a Column.
     * @param label the new column label
     * @param pos the index of the Column to set
     */
    public void setColumnLabel (String label, int pos) {
        columns[pos].setLabel(label);
    }

    /**
     * Returns the comment associated with the Column at the indicated position.
     * @param pos the index of the Column name to get.
     * @returns the comment associated with the Column.
     */
    public String getColumnComment (int pos) {
		return columns[pos].getComment();
    }

    /**
     * Set the comment associated with a column.
     * @param label the new column comment
     * @param pos the index of the column to set
     */
    public void setColumnComment (String label, int pos) {
        columns[pos].setComment(label);
    }

	/**
	 * Add a new Column after the last occupied position in this Table.
	 * @param newColumn the Column to be added to the table
	*/
    public void addColumn (Column newColumn) {
        Column[] newColumns = new Column[columns.length + 1];
        System.arraycopy(columns, 0, newColumns, 0, columns.length);
        newColumns[columns.length] = newColumn;
        columns = newColumns;
    }

    /**
     * Remove a Column from the Table.
     * @param pos the position of the Column to remove
     */
    public void removeColumn (int pos) {
        Column[] newColumns = new Column[columns.length - 1];
        System.arraycopy(columns, 0, newColumns, 0, pos);
        System.arraycopy(columns, pos + 1, newColumns, pos, newColumns.length
                - pos);
        columns = newColumns;
    }

	/**
	 * Remove a range of columns from the Table.
	 * @param start the start position of the range to remove
	 * @param len the number to remove-the length of the range
	 */
    public void removeColumns (int start, int len) {
        Column[] newColumns = new Column[columns.length - len];
        System.arraycopy(columns, 0, newColumns, 0, start);
        System.arraycopy(columns, start + len, newColumns, start,
			newColumns.length - start);
        columns = newColumns;
    }

	/**
	 * Remove a row from this Table.
	 * @param pos the row to remove
	 */
    public void removeRow (int pos) {
        for (int i = 0; i < getNumColumns(); i++)
        	getColumn(i).removeRow(pos);
    }

	/**
	 * Remove a range of rows from the Table.
	 * @param start the start position of the range to remove
	 * @param len the number to remove-the length of the range
	 */
	public void removeRows(int start, int len) {
		for (int i = 0; i < getNumColumns(); i++)
			getColumn(i).removeRows(start, len);
	}

	/**
	 * Insert a new Column at the indicated position in this Table.  All
	 * subsequent Columns will be shifted.
	 * @param newColumn the new Column
	 * @param position the position at which to insert
	 */
    public void insertColumn (Column newColumn, int position) {
        Column[] newColumns = new Column[columns.length + 1];
        System.arraycopy(columns, 0, newColumns, 0, position);
        newColumns[position] = newColumn;
        System.arraycopy(columns, position, newColumns, position + 1,
			columns.length - position);
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
     * Get an Object from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the Object in the Table at (row, column)
     */
    public Object getObject (int row, int column) {
        return  columns[column].getRow(row);
    }

    /**
     * Get an int from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the int in the Table at (row, column)
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
     * Get a short from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the short in the Table at (row, column)
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
     * Get a long from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the long in the Table at (row, column)
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
     * Get a float from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the float in the Table at (row, column)
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
     * Get a double from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the float in the Table at (row, column)
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
     * Get a String from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the String in the Table at (row, column)
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
     * Get the bytes from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the bytes in the Table at (row, column)
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
     * Get a byte from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the byte in the Table at (row, column)
     */
    public byte getByte (int row, int column) {
        return  columns[column].getByte(row);
    }

    /**
	 * Set a byte value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setByte (byte data, int row, int column) {
        columns[column].setByte(data, row);
    }

    /**
     * Get a char[] from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the chars in the Table at (row, column)
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
     * Get a char from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the chars in the Table at (row, column)
     */
    public char getChar (int row, int column) {
        return  columns[column].getChar(row);
    }

    /**
	 * Set a char value in the Table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setChar (char data, int row, int column) {
        columns[column].setChar(data, row);
    }

    /**
     * Get a boolean from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the boolean in the Table at (row, column)
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

    //////////////////////////////////////

    /**
     * Get the number of rows this Table holds.
     * @return this Table's number of rows
     */
    public int getNumRows () {
        if (columns.length < 1)
            return  0;
        return  columns[0].getNumRows();
    }

	/**
	 * Get the number of non-null entries this Table holds.  This simply
	 * delegates the call to getNumEntries() on the first Column in this Table.
	 * @return this Table's number of entries
	 */
	public int getNumEntries() {
		if (columns.length < 1)
			return 0;
		return columns[0].getNumEntries();
	}

	/**
	 * Sets a new capacity for this Table.  The capacity is its potential
	 * maximum number of Columns.  If numEntries is greater than newCapacity,
	 * the Table will be truncated.
	 * @param newCapacity a new capacity
	 */
    public void setNumRows (int newCapacity) {
        if (columns != null)
            for (int c = 0; c < columns.length; c++)
                if (columns[c] != null)
                    columns[c].setNumRows(newCapacity);
    }

	/**
	 * Get all the entries from the specified row.  The caller must pass in
	 * a buffer for the data to be copied into.  This buffer should be one of
	 * following data types: int[], float[], double[], long[], short[], boolean[],
	 * String[], char[][], byte[][], Object[], byte[], or char[].  The data from
	 * the specified row will then be copied into the buffer.  If the length of
	 * the buffer is greater than the number of columns in the table, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 * @param buffer the array to copy data into
	 * @param pos the index of the row to copy
	 */
    public void getRow (Object buffer, int pos) {
		if(buffer instanceof int[]) {
			int[] b1 = (int[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getInt(pos, i);
		}
		else if(buffer instanceof float[]) {
			float[] b1 = (float[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getFloat(pos, i);
		}
		else if(buffer instanceof double[]) {
			double[] b1 = (double[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getDouble(pos, i);
		}
		else if(buffer instanceof long[]) {
			long[] b1 = (long[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getLong(pos, i);
		}
		else if(buffer instanceof short[]) {
			short[] b1 = (short[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getShort(pos, i);
		}
		else if(buffer instanceof boolean[]) {
			boolean[] b1 = (boolean[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getBoolean(pos, i);
		}
		else if(buffer instanceof String[]) {
			String[] b1 = (String[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getString(pos, i);
		}
		else if(buffer instanceof char[][]) {
			char[][] b1 = (char[][])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getChars(pos, i);
		}
		else if(buffer instanceof byte[][]) {
			byte[][] b1 = (byte[][])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getBytes(pos, i);
		}
		else if(buffer instanceof Object[]) {
			Object[] b1 = (Object[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getObject(pos, i);
		}
		else if(buffer instanceof byte[]) {
			byte[] b1 = (byte[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getByte(pos, i);
		}
		else if(buffer instanceof char[]) {
			char[] b1 = (char[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getChar(pos, i);
		}
    }

	/**
	 * Get all the entries from the specified column.  The caller must pass in
	 * a buffer for the data to be copied into.  This buffer should be one of
	 * following data types: int[], float[], double[], long[], short[], boolean[],
	 * String[], char[][], byte[][], Object[], byte[], or char[].  The data from
	 * the specified row will then be copied into the buffer.  If the length of
	 * the buffer is greater than the number of rows in the table, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 * @param buffer the array to copy data into
	 * @param pos the index of the column to copy
	 */
	public void getColumn (Object buffer, int pos) {
		if(buffer instanceof int[]) {
			int[] b1 = (int[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getInt(i, pos);
		}
		else if(buffer instanceof float[]) {
			float[] b1 = (float[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getFloat(i, pos);
		}
		else if(buffer instanceof double[]) {
			double[] b1 = (double[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getDouble(i, pos);
		}
		else if(buffer instanceof long[]) {
			long[] b1 = (long[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getLong(i, pos);
		}
		else if(buffer instanceof short[]) {
			short[] b1 = (short[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getShort(i, pos);
		}
		else if(buffer instanceof boolean[]) {
			boolean[] b1 = (boolean[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getBoolean(i, pos);
		}
		else if(buffer instanceof String[]) {
			String[] b1 = (String[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getString(i, pos);
		}
		else if(buffer instanceof char[][]) {
			char[][] b1 = (char[][])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getChars(i, pos);
		}
		else if(buffer instanceof byte[][]) {
			byte[][] b1 = (byte[][])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getBytes(i, pos);
		}
		else if(buffer instanceof Object[]) {
			Object[] b1 = (Object[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getObject(i, pos);
		}
		else if(buffer instanceof byte[]) {
			byte[] b1 = (byte[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getByte(i, pos);
		}
		else if(buffer instanceof char[]) {
			char[] b1 = (char[])buffer;
			for(int i = 0; i < b1.length; i++)
				b1[i] = getChar(i, pos);
		}
	}

    /**
     * Gets a subset of this Table's rows, a cropped Table, given a start
	 * position and length.  The returned Table is a copy of the original.
     * @param pos the start position for the subset
     * @param len the length of the subset
     * @return a subset of this Table's rows
     */
    public Table getSubset (int pos, int len) {
        TableImpl subset = new TableImpl(columns.length);
        for (int i = 0; i < columns.length; i++)
            subset.setColumn((Column)columns[i].getSubset(pos, len), i);
        return  subset;
    }

    /**
     * Sets the reference to the internal representation of this Table.
     * @param newColumns a new internal representation for this Table
     */
    public void setColumns (Column[] newColumns) {
       columns = newColumns;
    }

	/**
		Set the entry at the given position to newRow.
		@param newRow a new entry
		@param pos the position to set
	*/
    /*public void setRow (Object[] newRow, int pos) {
        Object[] ne = (Object[])newRow;
        for (int i = 0; i < columns.length; i++)
            columns[i].setRow(ne[i], pos);
    }*/

	/**
	 * Add a row to the end of this Table.  An Object[] should be passed to
	 * this method, with an entry for each Column in the Table.
	 * @param newRow the Object to put into the new row
	 */
    /*public void addRow (Object[] newRow) {
        if (newRow instanceof Object[]) {
            Object[] toAdd = (Object[])newRow;
            for (int i = 0; i < toAdd.length; i++) {
                if (i <= getNumColumns())
                    getColumn(i).addRow(toAdd[i]);
            }
        }
        else
            System.out.println("An Object[] must be passed to " + "addRow().  The row was not added.");
    }*/



	/**
	 * Insert a new row into this Table.  An Object[] should be passed to
	 * this method, with an entry for each Column in the Table.
	 * @param newRow the Object to insert
	 * @param pos the position to insert the new row
	 */
    /*public void insertRow (Object[] newRow, int pos) {
        if (newRow instanceof Object[]) {
            Object[] toAdd = (Object[])newRow;
            for (int i = 0; i < toAdd.length; i++) {
                if (i <= getNumColumns())
                    getColumn(i).insertRow(toAdd[i], pos);
            }
        }
        else
            System.out.println("An Object[] must be passed to " + "insertRow().  The row was not inserted.");
    }*/

	/**
	 * Swap the positions of two rows.
	 * @param pos1 the first row to swap
	 * @param pos2 the second row to swap
	 */
    public void swapRows (int pos1, int pos2) {
        for (int i = 0; i < columns.length; i++) {
            Object Obj1 = columns[i].getRow(pos1);
            columns[i].setRow(columns[i].getRow(pos2), pos1);
            columns[i].setRow(Obj1, pos2);
        }
    }

	/**
	 * Swap the positions of two Columns.
	 * @param pos1 the first column to swap
	 * @param pos2 the second column to swap
	 */
    public void swapColumns (int pos1, int pos2) {
        Column temp = getColumn(pos1);
        setColumn(getColumn(pos2), pos1);
        setColumn(temp, pos2);
    }

    /**
     * Get a copy of this Table, reordered, based on the input array of indexes,
     * does not overwrite this Table.
     * @param newOrder an array of indices indicating a new order
	 * @return a copy of this table that has been reordered.
     */
    public Table reorderRows (int[] newOrder) {
        TableImpl newTable = new TableImpl(columns.length);
        for (int i = 0; i < columns.length; i++)
            newTable.setColumn(columns[i].reorderRows(newOrder), i);
        newTable.setLabel(getLabel());
        newTable.setComment(getComment());
        return  newTable;
    }

	/**
	 * MUST GET COPIES!!
	 * @param newOrder
	 * @return
	 */
	public Table reorderColumns(int[] newOrder) {
		TableImpl newTable = new TableImpl(columns.length);
		for(int i = 0; i < newOrder.length; i++)
			newTable.setColumn(getColumn(newOrder[i]).copy(), i);
		newTable.setLabel(getLabel());
		newTable.setComment(getComment());
		return newTable;
	}

    /**
     * Given an array of booleans, will remove the positions in the Table
     * which coorespond to the positions in the boolean array which are
     * marked true.  If the boolean array and Table do not have the same
     * number of rows, the remaining elements will be discarded.
     * @param flags the boolean array of remove flags
     */
    public void removeRowsByFlag (boolean[] flags) {
        for (int i = 0; i < columns.length; i++)
            columns[i].removeRowsByFlag(flags);
    }

    /**
     * Given an array of booleans, will remove the positions in the Table
     * which coorespond to the positions in the boolean array which are
     * marked true.  If the boolean array and Table do not have the same
     * number of rows, the remaining elements will be discarded.
     * @param flags the boolean array of remove flags
     */
	public void removeColumnsByFlag(boolean[] flags) {
        // keep a list of the row indices to remove
        LinkedList ll = new LinkedList();
        int i = 0;
        for (; i < flags.length; i++) {
            if (flags[i])
                ll.add(new Integer(i));
        }
        for (; i < getNumColumns(); i++) {
            ll.add(new Integer(i));
        }
        int[] toRemove = new int[ll.size()];
        int j = 0;
        Iterator iter = ll.iterator();
        while (iter.hasNext()) {
            Integer in = (Integer)iter.next();
            toRemove[j] = in.intValue();
            j++;
        }
        // now call remove by index to remove the rows
        removeColumnsByIndex(toRemove);
	}

    /**
     * Given an array of ints, will remove the rows in the Table,
     * which are indicated by the ints in the array.
     * @param indices the indicies of the rows to remove
     */
    public void removeRowsByIndex (int[] indices) {
        for (int i = 0; i < columns.length; i++)
            columns[i].removeRowsByIndex(indices);
    }

    /**
     * Given an array of ints, will remove the columns in the Table,
     * which are indicated by the ints in the array.
     * @param indices the indicies of the columns to remove
     */
	public void removeColumnsByIndex(int[] indices) {
        HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
		Column newColumns[] = new Column[getNumColumns()-indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            // if this row is not in the list, copy it into the new internal
			if(!toRemove.contains(new Integer(i))) {
                newColumns[newIntIdx] = columns[i];
                newIntIdx++;
            }
        }
		columns = newColumns;
	}

    //////////////////////////////////////

    /**
     * Will print the contents of this Table to standard out.
     * Each row of the Table will be printed to a separate line
     * of standard out.
     * <br>
     * This method assumes there is a proper implementation of
     * getString for every underlying column.  (note: this should never
     * be a problem, as any implmentation of Column should be able to
     * support a String rep)
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
     * Sort a column and rearrange the rows of the Table accordingly.
     * The column must support sorting.
     * @param col the index of the column to sort
     * @throws NotSupportedException when sorting is not supported on
     * the specified column.
     */
    public void sortByColumn (int col) {
        /*Column c = getColumn(col);
        if (c instanceof AbstractColumn) {
            if ((c instanceof NumericColumn) || (c instanceof TextualColumn))
                ((AbstractColumn)c).sort(this);
            else
                throw  new NotSupportedException();
        }
        else
            throw  new NotSupportedException();
		*/
		getColumn(col).sort(this);
    }

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
	   @param col the index of the column to sort
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sortByColumn(int col, int begin, int end) {
		getColumn(col).sort(this, begin, end);
	}

	public TableFactory getTableFactory() {
		return tableFactory;
	}

	public boolean isColumnNominal(int index) {
		return getColumn(index).getIsNominal();
	}

	public boolean isColumnScalar(int index) {
		return getColumn(index).getIsScalar();
	}

	public void setColumnIsNominal(boolean value, int index) {
		getColumn(index).setIsNominal(value);
	}

	public void setColumnIsScalar(boolean value, int index) {
		getColumn(index).setIsScalar(value);
	}

	public void setColumn(char[] data, int pos) {
		// create the column
		// set it
		// copy old column label and comment??
	}
	public void setColumn(byte[] data, int pos) {
		ByteColumn bc = new ByteColumn(data);
		setColumn(bc, pos);
	}

	public void setColumn(int[] data, int pos) {
		IntColumn ic = new IntColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(float[] data, int pos) {
		FloatColumn ic = new FloatColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(double[] data, int pos) {
		DoubleColumn ic = new DoubleColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(long[] data, int pos) {
		LongColumn ic = new LongColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(short[] data, int pos) {
		ShortColumn ic = new ShortColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(String[] data, int pos) {
		StringColumn ic = new StringColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(byte[][] data, int pos) {
		ContinuousByteArrayColumn bc = new ContinuousByteArrayColumn(data);
		setColumn(bc, pos);
	}
	public void setColumn(char[][] data, int pos) {
		ContinuousCharArrayColumn cc = new ContinuousCharArrayColumn(data);
		setColumn(cc, pos);
	}
	public void setColumn(Object[] data, int pos) {
		ObjectColumn ic = new ObjectColumn(data);
		setColumn(ic, pos);
	}
	public void setColumn(boolean[] data, int pos) {
		BooleanColumn ic = new BooleanColumn(data);
		ic.setLabel(getColumnLabel(pos));
		ic.setComment(getColumnComment(pos));
		setColumn(ic, pos);
	}

	public void setRow(char[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setChar(data[i], pos);
	}
	public void setRow(byte[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setByte(data[i], pos);
	}
	public void setRow(int[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setInt(data[i], pos);
	}
	public void setRow(float[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setFloat(data[i], pos);
	}
	public void setRow(double[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setDouble(data[i], pos);
	}
	public void setRow(long[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setLong(data[i], pos);
	}
	public void setRow(short[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setShort(data[i], pos);
	}
	public void setRow(String[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setString(data[i], pos);
	}
	public void setRow(byte[][] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setBytes(data[i], pos);
	}
	public void setRow(char[][] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setChars(data[i], pos);
	}
	public void setRow(Object[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setObject(data[i], pos);
	}
	public void setRow(boolean[] data, int pos) {
		for(int i = 0; i < getNumColumns(); i++)
			getColumn(i).setBoolean(data[i], pos);
	}

	public void insertColumn(char[] data, int pos) {
		CharColumn cc = new CharColumn(data);
		insertColumn(cc, pos);
	}
	public void insertColumn(byte[] data, int pos) {
		ByteColumn bc = new ByteColumn(data);
		insertColumn(bc, pos);
	}
	public void insertColumn(int[] data, int pos) {
		IntColumn ic = new IntColumn(data);
		insertColumn(ic, pos);
	}
	public void insertColumn(float[] data, int pos) {
		FloatColumn fc = new FloatColumn(data);
		insertColumn(fc, pos);
	}
	public void insertColumn(double[] data, int pos) {
		DoubleColumn dc = new DoubleColumn(data);
		insertColumn(dc, pos);
	}
	public void insertColumn(long[] data, int pos) {
		LongColumn lc = new LongColumn(data);
		insertColumn(lc, pos);
	}
	public void insertColumn(short[] data, int pos) {
		ShortColumn sc = new ShortColumn(data);
		insertColumn(sc, pos);
	}
	public void insertColumn(String[] data, int pos) {
		StringColumn sc = new StringColumn(data);
		insertColumn(sc, pos);
	}
	public void insertColumn(byte[][] data, int pos) {
      ContinuousByteArrayColumn bc = new ContinuousByteArrayColumn(data);
      insertColumn(bc, pos);
	}
	public void insertColumn(char[][] data, int pos) {
      ContinuousCharArrayColumn ac = new ContinuousCharArrayColumn(data);
      insertColumn(ac, pos);
	}
	public void insertColumn(Object[] data, int pos) {
		ObjectColumn oc = new ObjectColumn(data);
		insertColumn(oc, pos);
	}
	public void insertColumn(boolean[] data, int pos) {
		BooleanColumn bc = new BooleanColumn(data);
		insertColumn(bc, pos);
	}

	public void insertRow(char[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Character(data[i]), pos);
	}
	public void insertRow(byte[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Byte(data[i]), pos);
    }
	public void insertRow(int[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Integer(data[i]), pos);
    }
	public void insertRow(float[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Float(data[i]), pos);
    }
	public void insertRow(double[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Double(data[i]), pos);
    }
	public void insertRow(long[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Long(data[i]), pos);
    }
	public void insertRow(short[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Short(data[i]), pos);
    }
	public void insertRow(String[] data, int pos) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).insertRow(data[i], pos);
    }
	public void insertRow(byte[][] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(data[i], pos);
    }
	public void insertRow(char[][] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(data[i], pos);
    }
	public void insertRow(Object[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(data[i], pos);
    }
	public void insertRow(boolean[] data, int pos) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).insertRow(new Boolean(data[i]), pos);
    }

	public void addColumn(char[] data) {
		CharColumn cc = new CharColumn(data);
		addColumn(cc);
	}
	public void addColumn(byte[] data) {
		ByteColumn bc = new ByteColumn(data);
		addColumn(bc);
	}

	public void addColumn(int[] data) {
		IntColumn ic = new IntColumn(data);
		addColumn(ic);
	}

	public void addColumn(float[] data) {
		FloatColumn fc = new FloatColumn(data);
		addColumn(fc);
	}

	public void addColumn(double[] data) {
		DoubleColumn lc = new DoubleColumn(data);
		addColumn(lc);
	}

	public void addColumn(long[] data) {
		LongColumn lc = new LongColumn(data);
		addColumn(lc);
	}

	public void addColumn(short[] data) {
		ShortColumn sc = new ShortColumn(data);
		addColumn(sc);
	}

	public void addColumn(String[] data) {
		StringColumn sc = new StringColumn(data);
		addColumn(sc);
	}

	public void addColumn(byte[][] data) {
      ContinuousByteArrayColumn bc = new ContinuousByteArrayColumn(data);
      addColumn(bc);
	}

	public void addColumn(char[][] data) {
      ContinuousCharArrayColumn ac = new ContinuousCharArrayColumn(data);
      addColumn(ac);
	}

	public void addColumn(Object[] data) {
		ObjectColumn oc = new ObjectColumn(data);
		addColumn(oc);
	}

	public void addColumn(boolean[] data) {
		BooleanColumn bc = new BooleanColumn(data);
		addColumn(bc);
	}

	public void addRow(char[] data) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).addRow(new Character(data[i]));
    }
	public void addRow(byte[] data) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).addRow(new Byte(data[i]));
    }
	public void addRow(int[] data) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).addRow(new Integer(data[i]));
    }
	public void addRow(float[] data) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).addRow(new Float(data[i]));
    }
	public void addRow(double[] data) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).addRow(new Double(data[i]));
    }
	public void addRow(long[] data) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).addRow(new Long(data[i]));
    }
	public void addRow(short[] data) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).addRow(new Short(data[i]));
    }
	public void addRow(String[] data) {
      for(int i= 0; i< data.length; i++)
        getColumn(i).addRow(data[i]);
    }
	public void addRow(byte[][] data) {
      for(int i= 0; i< data.length; i++)
        getColumn(i).addRow(data[i]);
    }
	public void addRow(char[][] data) {
      for(int i = 0; i< data.length; i++)
        getColumn(i).addRow(data[i]);
    }
	public void addRow(Object[] data) {
      for(int i = 0; i < data.length; i++)
        getColumn(i).addRow(data[i]);
    }
	public void addRow(boolean[] data) {
      for(int i= 0; i< data.length; i++)
        getColumn(i).addRow(new Boolean(data[i]));
    }

	public boolean isNumericColumn(int position) {
		if(getColumn(position) instanceof NumericColumn)
			return true;

		Column col = getColumn(position);
		int numRows = col.getNumRows();
        for(int row = 0; row < numRows; row++) {
            try {
                Double d = Double.valueOf(col.getString(row));
            }
            catch(Exception e) {
                return false;
            }
        }
        return true;
	}

	public int getColumnType(int position) {
		return getColumn(position).getType();
	}

	public ExampleTable toExampleTable() {
		return new ExampleTableImpl(this);
	}
}
