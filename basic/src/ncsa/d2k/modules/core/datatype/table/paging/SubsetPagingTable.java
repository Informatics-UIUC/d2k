/*
 * Created on Apr 27, 1976
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * A paging table simply contains a reference to a table. Page faults 
 * are caught and the appropriate table is paged in.
 * @author redman
 */
public class SubsetPagingTable extends AbstractTable implements MutableTable {
	
	/** this is the page we are looking at. */
	Table table;
	
	/** these are the columns included here only for efficiency. */
	protected Column [] columns;
	
	/** the offset of this page. */
	int offset = -1;
	
	/** this is the cache. */
	PageCache cache;
	
	/** this is the current page. */
	Page currentPage;
	
	/** this is the subset. */
	int [] subset;
	
	/** list of transformations performed. */
	ArrayList transformations = new ArrayList();

	final static int DEFAULT_PAGESIZE = 50000;
	
	/**
	 * This is the initial test for paging tables, but
	 * it can also server an example of how to create and use
	 * paging tables.
	 * @param args the first arg is the table size, second is the number
	 * of pages, and the last, if present is the location of the pages.
	 */
	static public void main(String [] args) {
		String filepath;
		int tableSize = Integer.parseInt(args[0]);
		int numTables = Integer.parseInt(args[1]);
		if (args.length == 3)
			filepath = args[2];
		else
			filepath = "/tmp/D2Kpages/page-";
			
		// Now let's do a regular table to compare time.
		Column[] cols = new Column[4];
		cols[0] = new IntColumn(tableSize);
		cols[1] = new IntColumn(tableSize);
		cols[2] = new IntColumn(tableSize);
		cols[3] = new IntColumn(tableSize);
		MutableTableImpl mti = new MutableTableImpl(cols);
		for (int i = 0; i < tableSize; i++) {
			mti.setInt(i, i, 0);
			mti.setInt(i, i, 1);
			mti.setInt(i, i, 2);
			mti.setInt(i, i, 3);
		}
		
		System.out.println();
		System.out.println("--------------- Doing a mutable table ----------------");
		long start = System.currentTimeMillis();
		for (int whichTable = 0; whichTable < numTables; whichTable++) {
			for (int i = 0; i < tableSize; i++) {
				int value = mti.getInt(i, 0);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 0 value = "+value);
				value = mti.getInt(i, 1);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 1 value = "+value);
				value = mti.getInt(i, 2);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 2 value = "+value);
				value = mti.getInt(i, 3);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 3 value = "+value);
			}
		}
		System.out.println("Total access for mutable table took "+(System.currentTimeMillis()-start));
		mti = null;
		
		// Now let's do a subset table to compare time.
		cols = new Column[4];
		cols[0] = new IntColumn(tableSize);
		cols[1] = new IntColumn(tableSize);
		cols[2] = new IntColumn(tableSize);
		cols[3] = new IntColumn(tableSize);
		SubsetTableImpl sti = new SubsetTableImpl(cols);
		for (int i = 0; i < tableSize; i++) {
			sti.setInt(i, i, 0);
			sti.setInt(i, i, 1);
			sti.setInt(i, i, 2);
			sti.setInt(i, i, 3);
		}

		System.out.println();
		System.out.println("--------------- Doing a subset table ----------------");
		start = System.currentTimeMillis();
		for (int whichTable = 0; whichTable < numTables; whichTable++) {
			for (int i = 0; i < tableSize; i++) {
				int value = sti.getInt(i, 0);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 0 value = "+value);
				value = sti.getInt(i, 1);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 1 value = "+value);
				value = sti.getInt(i, 2);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 2 value = "+value);
				value = sti.getInt(i, 3);
				if (value != i) System.out.println("Data wrong, row = "+i+" column = 3 value = "+value);
			}
		}
		System.out.println("Total access for subset table took "+(System.currentTimeMillis()-start));
		sti = null;
		
		System.out.println("Constructing pages...");
		Page[] pages = new Page[numTables];
		int [] offset = new int [numTables];
		for (int whichTable = 0; whichTable < numTables; whichTable++) {
			cols = new Column[4];
			cols[0] = new IntColumn(tableSize);
			cols[1] = new IntColumn(tableSize);
			cols[2] = new IntColumn(tableSize);
			cols[3] = new IntColumn(tableSize);
			mti = new MutableTableImpl(cols);
			for (int i = 0; i < tableSize; i++) {
				int val = i + (tableSize*whichTable);
				mti.setInt(val, i, 0);
				mti.setInt(val, i, 1);
				mti.setInt(val, i, 2);
				mti.setInt(val, i, 3);
			}
			try {
				pages[whichTable] = new Page(mti, false);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			offset[whichTable] = whichTable*tableSize;
		}
		
		// Create a paging table, check it's performance.
		ExamplePagingTable spt = new ExamplePagingTable(new PageCache(pages, offset, tableSize));
		start = System.currentTimeMillis();
		System.out.println();
		System.out.println("TABLE SIZE = "+spt.getNumRows());
		int size = spt.getNumRows();
		for (int i = 0; i < size; i++) {
			int value = spt.getInt(i, 0);
			if (value != i) {
				System.out.println("Data wrong, row = "+i+" column = 0 value = "+value);
				System.exit(0);
			} 
			value = spt.getInt(i, 1);
			if (value != i) {
				System.out.println("Data wrong, row = "+i+" column = 1 value = "+value);
				System.exit(0);
			} 
			value = spt.getInt(i, 2);
			if (value != i) {
				System.out.println("Data wrong, row = "+i+" column = 2 value = "+value);
				System.exit(0);
				} 
			value = spt.getInt(i, 3);
			if (value != i) {
				System.out.println("Data wrong, row = "+i+" column = 3 value = "+value);
				System.exit(0);
			} 
		}
		long total = System.currentTimeMillis()-start;
		
		System.out.println("Total access for paging took "+total);
		long sum = 0;
		for (int i = 0; i < pages.length; i++) {
			sum += pages[i].fileReadTime;
		}
		System.out.println("Time spent on Data access = "+(total-sum));
	}
	
	/**
	 * default empty paging table.
	 *
	 */
	public SubsetPagingTable() {
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
	public SubsetPagingTable(PageCache pager) {
		this.cache = pager;
		this.offset = -1;
		this.getPage(0);
	}
		
	/////////////////////////////////
	// Getter methods.
	/////////////////////////////////
	
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
	}
	
	/**
	 * Get the object at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the object representation of the data at the given row and column.
	 */
	public Object getObject(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getObject(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getObject(where);
		}
	}
	
	/**
	 * Get the int at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the int representation of the data at the given row and column.
	 */
	public int getInt(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getInt(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getInt(where);
		}
	}
	
	/**
	 * Get the short at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the short representation of the data at the given row and column.
	 */
	public short getShort(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getShort(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getShort(where);
		}
	}
	
	/**
	 * Get the float at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the float representation of the data at the given row and column.
	 */
	public float getFloat(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getFloat(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getFloat(where);
		}
	}
	
	/**
	 * Get the double at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the double representation of the data at the given row and column.
	 */
	public double getDouble(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getDouble(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getDouble(where);
		}
	}
	
	/**
	 * Get the long at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the long representation of the data at the given row and column.
	 */
	public long getLong(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getLong(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getLong(where);
		}
	}
	
	/**
	 * Get the string at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the string representation of the data at the given row and column.
	 */
	public String getString(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getString(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getString(where);
		}
	}

	/**
	 * Get the byte at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the byte representation of the data at the given row and column.
	 */
	public byte[] getBytes(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getBytes(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getBytes(where);
		}
	}

	/**
	 * Get the boolean at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the boolean representation of the data at the given row and column.
	 */
	public boolean getBoolean(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getBoolean(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getBoolean(where);
		}
	}

	/**
	 * Get the character at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the character representation of the data at the given row and column.
	 */
	public char[] getChars(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getChars(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getChars(where);
		}
	}

	/**
	 * Get the byte at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the byte representation of the data at the given row and column.
	 */
	public byte getByte(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getByte(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getByte(where);
		}
	}

	/**
	 * Get the object at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the object representation of the data at the given row and column.
	 */
	public char getChar(int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			return columns[column].getChar(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[column].getChar(where);
		}
	}
	
	/////////////////////////////////
	// Setter methods.
	/////////////////////////////////
	
	/**
	 * Set the value at the given row and column to the value provided.
	 * @param element the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setObject(Object data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setObject(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setObject(data, where);
		}
		currentPage.mark(true);
	}
	
	/**
	 * Set the int value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setInt(int data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setInt(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setInt(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the short value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setShort(short data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setShort(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setShort(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the float value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setFloat(float data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setFloat(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setFloat(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the double value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setDouble(double data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setDouble(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setDouble(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setLong(long data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setLong(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setLong(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the string at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setString(String data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setString(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setString(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the bytes at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setBytes(byte[] data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setBytes(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setBytes(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the boolean value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setBoolean(boolean data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setBoolean(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setBoolean(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the character array value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setChars(char[] data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setChars(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setChars(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the byte value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setByte(byte data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setByte(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setByte(data, where);
		}
		currentPage.mark(true);
	}

	/**
	 * Set the char value at the given row and column to the value provided.
	 * @param data the value to set the entry to.\
	 * @param row the row
	 * @param column the column
	 */
	public void setChar(char data, int row, int column) {
		int where;
		try {
			where = subset[row-offset];
			columns[column].setChar(data, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[column].setChar(data, where);
		}
		currentPage.mark(true);
	}

	////////////////////////////////////////
	// Labels and comments, these are stored in the cache.
	////////////////////////////////////////

	/**
	 * Get the column label.
	 * @param position the column index
	 * @return the the column label.
	 */
	public String getColumnLabel(int position) {
		return cache.getColumnLabel(position);
	}

	/**
	 * Return the column comment.
	 * @param position the column index
	 * @return the column comment.
	 */
	public String getColumnComment(int position) {
		return cache.getColumnComment(position);
	}
	
	/**
	 * Set the column label for the given column.
	 * @param label the new column label
	 * @param position the column position.
	 */
	public void setColumnLabel(String label, int position) {
		cache.setColumnLabel(label, position);
	}
	
	/**
	 * Set the column comment.
	 * @param comment the new column comment for the column.
	 * @param position the column.
	 */
	public void setColumnComment(String comment, int position) {
		cache.setColumnComment(comment, position);
	}

	///////////////////////////////////////////////////////////////
	// Column modifier methods.
	///////////////////////////////////////////////////////////////
	
	/**
	 * Set the column. The column passed in is expected to be only
	 * a template. It should define the type and the label and comment,
	 * however, it is not expected to actually contain the data, since
	 * the data would likely be much to large to fit in memory. The column
	 * should be populated using the writer methods of the table.
	 * @param col the new column.
	 * @param where the index of the column to replace.
	 */
	public void setColumn(Column col, int where) {
		this.cache.setColumn(col, where);
		this.refresh();
	}
	
	/**
	 * Add a column. The column passed in is expected to be only
	 * a template. It should define the type and the label and comment,
	 * however, it is not expected to actually contain the data, since
	 * the data would likely be much to large to fit in memory. The column
	 * should be populated using the writer methods of the table.
	 * @param col the new column.
	 * @param where the index of the column to replace.
	 */
	public void addColumn(Column datatype) {
		cache.addColumn(datatype);
		this.refresh();
	}
	
	/**
	 * Add columns. The columns passed in is expected to be only
	 * a template. It should define the type and the label and comment,
	 * however, it is not expected to actually contain the data, since
	 * the data would likely be much to large to fit in memory. The columns
	 * should be populated using the writer methods of the table.
	 * @param col the new column.
	 * @param where the index of the column to replace.
	 */
	public void addColumns(Column[] datatype) {
		cache.addColumns(datatype);
		this.refresh();
	}
	
	/**
	 * Insert the column. The column passed in is expected to be only
	 * a template. It should define the type and the label and comment,
	 * however, it is not expected to actually contain the data, since
	 * the data would likely be much to large to fit in memory. The column
	 * should be populated using the writer methods of the table.
	 * @param col the new column.
	 * @param where the index of the column to replace.
	 */
	public void insertColumn(Column col, int where) {
		cache.insertColumn(col, where);
		this.refresh();
	}
	
	/**
	 * insert columns. The columns passed in is expected to be only
	 * a template. It should define the type and the label and comment,
	 * however, it is not expected to actually contain the data, since
	 * the data would likely be much to large to fit in memory. The columns
	 * should be populated using the writer methods of the table.
	 * @param col the new column.
	 * @param where the index of the column to replace.
	 */
	public void insertColumns(Column[] datatype, int where) {
		cache.insertColumns(datatype, where);
		this.refresh();
	}
	
	/**
	 * Remove the column from each table.
	 */
	public void removeColumn(int position) {
		cache.removeColumn(position);
		this.refresh();
	}
	
	/**
	 * Remove len columns starting at start.
	 * @param start the first column to delete.
	 * @param len the number of columns to delete.
	 */
	public void removeColumns(int start, int len) {
		cache.removeColumns(start, len);
		this.refresh();
	}
	
	/**
	 * This is easy, just add the rows to the last page.
	 * @param howMany how many rows to add.
	 */
	public void addRows(int howMany) {
		cache.addRows(howMany);
		this.refresh();
	}
	
	/**
	 * Remove a row. Take the row out of the table, and then decrement
	 * each of the offsets for the subsequent tables offsets.
	 * @param row remove the row at the given index.
	 */
	public void removeRow(int row) {
		cache.removeRow(row);
		this.refresh();
	}

	/**
	 * Remove the specified number of rows.
	 * @param start
	 * @param len
	 */
	public void removeRows(int start, int len) {
		cache.removeRows(start, len);
		this.refresh();
	}

	/**
	 * Make a copy of the table with the columns in a new order. This requires
	 * us to make a copy of the PageCache object.
	 * @param newOrder
	 * @return
	 */
	public Table reorderColumns(int[] newOrder) {
		PageCache pc = cache.reorderColumns(newOrder);
		this.refresh();
		return new SubsetPagingTable(pc);
	}
	
	/**
	 * Swap two rows. They can potentially be in different pages, but it doesn't matter, 
	 * we treat them the same either way.
	 * @param pos1 the first position.
	 * @param pos2 the second postion.
	 */
	public void swapRows(int pos1, int pos2) {
		cache.swapRows(pos1, pos2);
	}
	
	/**
	 * Swap the position of two columns.
	 * @param pos1
	 * @param pos2
	 */
	public void swapColumns(int pos1, int pos2) {
		cache.swapColumns(pos1, pos2);
		this.refresh();
	}
	
	/////////////////////////////////////////////
	// transformation support.
	/////////////////////////////////////////////
	
	/**
	 * add a reversible transformation.
	 * @param tm the transformation.
	 */
	public void addTransformation(Transformation tm) {
		transformations.add(tm);
	}

	/**
	 * Return the list of transformations.
	 * @return the list of transformations.
	 */
	public List getTransformations() {
		return transformations;
	}

	///////////////////////////////////////////
	// Missing and empty value support.
	///////////////////////////////////////////
	
	/**
	 * Set the missing value flag for the data.
	 * @param b true or fals if missing or not.
	 * @param row the row of the data.
	 * @param col the column of the data.
	 */
	public void setValueToMissing(boolean b, int row, int col) {
		int where;
		try {
			where = subset[row-offset];
			columns[col].setValueToMissing(b, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[col].setValueToMissing(b, where);
		}
	}
	
	/**
	 * Set the empty flag
	 * @param b the flag, true if the cell is empty.
	 * @param row the row of the table.
	 * @param col the column of the table.
	 */
	public void setValueToEmpty(boolean b, int row, int col) {
		int where;
		try {
			where = subset[row-offset];
			columns[col].setValueToEmpty(b, where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			columns[col].setValueToEmpty(b, where);
		}
	}

	/**
	 * return true if the value is missing.
	 * @param row the row to test.
	 * @param col the column to test.
	 * @return true if the value is missing.
	 */
	public boolean isValueMissing(int row, int col) {
		int where;
		try {
			where = subset[row-offset];
			return columns[col].isValueMissing(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[col].isValueMissing(where);
		}
	}

	/**
	 * return true if the value is empty.
	 * @param row the row to test.
	 * @param col the column to test.
	 * @return true if the value is empty.
	 */
	public boolean isValueEmpty(int row, int col) {
		int where;
		try {
			where = subset[row-offset];
			return columns[col].isValueEmpty(where);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
					
			// Get the next page.
			this.getPage(row);
			where = subset[row-offset];
			return columns[col].isValueEmpty(where);
		}
	}
	
	/**
	 * If there are any missing values in the table, return true.
	 * @return true if there are any missing values in the table.
	 */
	public boolean hasMissingValues() {	
		for (int row = 0; row < this.getNumRows(); row++) {
			for (int col = 0; col < this.getNumColumns(); col++) {
				if (this.isValueMissing(row, col))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * If there are any missing values in the specified column of the
	 * table, return true.
	 * @return true if there are any missing values in the table column.
	 */
	public boolean hasMissingValues(int columnIndex) {
		for (int row = 0; row < this.getNumRows(); row++) {
			if (this.isValueMissing(row, columnIndex))
				return true;
		}
		return false;
	}
	
	/**
	 * Return the total number of rows.
	 * @return the number of rows.
	 */
	public int getNumRows() {
		return cache.getNumRows();
	}

	/**
	 * Get the object at the given row and column indices, reading a new page if
	 * necessary.
	 * @param row the row index.
	 * @param column the column index
	 * @return the object representation of the data at the given row and column.
	 */
	public int getNumColumns() {
		return table.getNumColumns();
	}

	/**
	 * Return a subset of the data. The paging table returned will actually replicate the data,
	 * because the subset array is stored with the data in an out-of-memory page, so we are required
	 * to use a different page.
	 * @param row the row index.
	 * @param column the column index
	 * @return the object representation of the data at the given row and column.
	 */
	public Table getSubset(int start, int len) {
		try {
			return new SubsetPagingTable(cache.subset(start, len));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get a subset of the indexed rows.
	 * @param rows the rows to include.
	 */
	public Table getSubset(int[] rows) {
		PageCache fudge;
		try {
			fudge = cache.subset(rows);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new SubsetPagingTable(fudge);
	}
	
	/**
	 * Make a deep copy of this table.
	 * @returns the copy of this table.
	 */
	public Table copy() {
		return new SubsetPagingTable(cache.copy(null));
	}
	
	/**
	 * Make a copy of the table data from start for len.
	 * @param start the first entry of the new table.
	 * @param len the number of entries tto copy.
	 */
	public Table copy(int start, int len) {
		int [] newsubset = new int [len];
		for (int i = 0; i < len; i++) {
			newsubset[i] = start+i;
		}
		return new SubsetPagingTable(cache.copy(newsubset));
	}
	
	/**
	 * Copy only the specified rows.
	 */
	public Table copy(int[] rows) {
		int [] newsubset = new int [rows.length];
		for (int i = 0; i < rows.length; i++) {
			newsubset[i] = rows[i];
		}
		return new SubsetPagingTable(cache.copy(newsubset));
	}
	
	/**
	 * Copy the table, but not it's contents. The new table will
	 * share the same data.
	 */
	public Table shallowCopy() {
		return new SubsetPagingTable(cache);
	}
	
	/**
	 * Create a new table.
	 */
	public MutableTable createTable() {
		Page [] pg = new Page[0];
		int [] os = new int[0];
		return new SubsetPagingTable(new PageCache(pg, os, this.cache.defaultPageSize));
		
	}
	
	/**
	 * if the column contains nominal data, return true, otherwise false.
	 * @param position the column index.
	 * @return true if the column is nominal
	 */
	public boolean isColumnNominal(int position) {
		return cache.isColumnNominal(position);
	}

	/**
	 * if the column contains scalar data, return true, otherwise false.
	 * @param position the column index.
	 * @return true if the column is scalar
	 */
	public boolean isColumnScalar(int position) {
		return cache.isColumnScalar(position);
	}
	
	/**
	 * set the a flag that indicates if the column is treated as nominal or not.
	 * @param value the new nominal value flag.
	 * @param position the column position.
	 */
	public void setColumnIsNominal(boolean value, int position) {
		cache.setColumnIsNominal(value, position);
	}
	
	/**
	 * set the a flag that indicates if the column is treated as scalar or not.
	 * @param value the new scalar value flag.
	 * @param position the column position.
	 */
	public void setColumnIsScalar(boolean value, int position) {
		cache.setColumnIsScalar(value, position);
	}
	
	/**
	 * return true if the column is numeric.
	 * @return true if the column is numeric.
	 */
	public boolean isColumnNumeric(int position) {
		if(columns[position] instanceof NumericColumn)
		   return true;
		return false;
	}
	
	/**
	 * Returns the datatype of the column.
	 * @param postion the column index.
	 */
	public int getColumnType(int position) {
		return columns[position].getType();
	}

	/**
	 * This returns the current column at the given location, but it will
	 * only represent a portion of the data int he table. This table pages
	 * because the data is big, so it makes not sense to return all the data
	 * for the row, it likely would not fit in memory.
	 * @param n the index of the column
	 */
	public Column getColumn(int n) {
		return columns[n];
	}
	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.Table#getRow()
	 */
	public Row getRow() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * The example table is a shallow copy of this one, shares the same
	 * page manager and all.
	 */
	public ExampleTable toExampleTable() {
		return new ExamplePagingTable(cache);
	}
	
	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.MutableTable#sortByColumn(int)
	 */
	public void sortByColumn(int col) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.MutableTable#sortByColumn(int, int, int)
	 */
	public void sortByColumn(int col, int begin, int end) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean equals(Object mt) {
	  Table mti = (Table) mt;
	  int numColumns = mti.getNumColumns();
	  int numRows = mti.getNumRows();
	  if (getNumColumns() != numColumns)
		return false;
	  if (getNumRows() != numRows)
		return false;
	  for (int i = 0; i < numRows; i++)
		for (int j = 0; j < numColumns; j++)
		  if (!getString(i, j).equals(mti.getString(i, j)))
			return false;
	  return true;
	}

}
