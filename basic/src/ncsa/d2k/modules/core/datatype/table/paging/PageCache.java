
/*
 * Created on Apr 27, 1976
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

/**
 * This is where pages for a paging table are cached. It contains an array list with
 * one entry for each page. A page is kind of a serialized table, but data only.
 * For each page, a count of the number of threads referencing the thread is stored.
 * When a count goes to zero, the table can be discarded, paged out. When the table
 * implementations get a page fault, which manifests itself in the form of an array
 * index out of bounds exception, the table implementation will request the appropriate
 * page from the page manager. The page is read in if neceessary, the referer count is
 * incremented, and the offset to the start of the page is returned to the table.
 * <p>
 * This object also stores metadata about the columns. This include the column labels,
 * comments, if they are nominal or scalar.\
 * <p>
 * There are also methods provided that manipulate the form of the tables. There are
 * methods to add column, insert and remove columns. Rows can also be added and removed.
 * Copy and subset methods are also provided here.
 * @author redman
 */
public class PageCache {

	/** this is the array of pages. */
	private Page [] pages;

	/** for each page a count of the number of threads referencing the page. */
	private int [] references;

	/** this is the offset of each table. */
	private int [] offsets;

	/** total number of rows. */
	private int numRows;

	/** the column labels. */
	private String [] columnLabels;

	/** the column comments. */
	private String [] columnComments;

	/** flag per column, set if column is scalar.*/
	private boolean [] columnScalar;

	/** set if column is nominal. */
	private boolean [] columnNominal;

	int defaultPageSize = 50000;

	private Page NON_PAGE = null;

	/**
	 * Given the pages and the page size.
	 * @param pgs the pages.
	 * @param offsets the offsets of each page.
	 * @param defaultPageSize the default size of a page.
	 */
	public PageCache (Page [] pgs, int [] offsets, int defaultPageSize) {
		this.pages = pgs;
		this.offsets = offsets;
		this.references = new int [pgs.length];
		this.defaultPageSize = defaultPageSize;
		try {
			NON_PAGE = new Page (new MutableTableImpl(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < references.length; i++) references[i] = 0;

		// Get the table from the first page, to get the column labels and comments.
		int nc;
		MutableTable t = null;
		if (pgs.length > 0) {
			t = (MutableTable) pgs[0].getTable();
			nc = t.getNumColumns();
		} else nc = 0;
		columnLabels = new String[nc];
		columnComments = new String[nc];
		columnNominal = new boolean[nc];
		columnScalar = new boolean[nc];
		for (int i = 0; i < nc; i++) {
			columnLabels[i] = t.getColumnLabel(i);
			columnComments[i] = t.getColumnComment(i);
			columnNominal[i] = t.isColumnNominal(i);
			columnScalar[i] = t.isColumnScalar(i);
		}
	}

	/**
	 * Return a reference to the column comment.
	 * @param columnIndex the column
	 * @return the column coment.
	 */
	String getColumnComment(int columnIndex) {
		return this.columnComments[columnIndex];
	}

	/**
	 * Return a reference to the column label.
	 * @param columnIndex the column
	 * @return the column label.
	 */
	String getColumnLabel(int columnIndex) {
		return this.columnLabels[columnIndex];
	}

	/**
	 * Set the columns label.
	 * @param label the new label.
	 * @param postion the position.
	 */
	public void setColumnLabel(String label, int position) {
		this.columnLabels[position] = label;
	}

	/**
	 * Set the columns comment.
	 * @param com the new commment.
	 * @param postion the position.
	 */
	public void setColumnComment(String com, int position) {
		this.columnComments[position] = com;
	}

	/**
	 * Returns true if the column is scalar.
	 * @param columnIndex the column index
	 * @return true if the column is scalar.
	 */
	boolean isColumnScalar(int columnIndex) {
		return this.columnScalar[columnIndex];
	}

	/**
	 * Set the column to scalar or not.
	 * @param value true if the column is to be scalar.
	 * @param columnIndex the index of the column to set up.
	 */
	void setColumnIsScalar(boolean value, int columnIndex) {
		this.columnScalar[columnIndex] = value;
	}

	/**
	 * Returns true if the column is scalar.
	 * @param columnIndex the column index
	 * @return true if the column is scalar.
	 */
	boolean isColumnNominal(int columnIndex) {
		return this.columnNominal[columnIndex];
	}

	/**
	 * Set the column to scalar or not.
	 * @param value true if the column is to be scalar.
	 * @param columnIndex the index of the column to set up.
	 */
	void setColumnIsNominal(boolean value, int columnIndex) {
		this.columnNominal[columnIndex] = value;
	}

	/**
	 * Get the number of rows in the entire cache.
	 */
	int getNumRows () {
		int last = offsets.length-1;
		Table t = pages[last].getTable();
		int len = pages[last].getSubset().length;
		return offsets[last]+len;
	}

	/**
	 * Get the index of the table at the given offset.
	 * @param row the row we search for.
	 * @param hint suggestion of where to start looking.
	 * @return the index of the page.
	 */
	final private int getPageIndexWithHint(int row, int hint) {
		if (offsets.length > (hint+1) &&
			row >= offsets[hint] && row < offsets[hint+1])
			return hint;
		else
			return getPageIndex(row);
	}

	/**
	 * Get the index of the table at the given offset.
	 * @param row
	 * @return
	 */
	final private int getPageIndex(int row) {

		int [] o = offsets;
		int low = 1;

		// We search for two entries where row is between them, so
		// if it is less than the offset for the second entry,
		// it must be the first entry.
		if (row > o[o.length-1])
			return o.length-1;
		if (o.length < 2 || row < o[1])
			return 0;

		int high = o.length-1;
		int middle;
		while (low <= high) {
			middle = (low+high)/2;
			if (row == o[middle])
				return middle;
			else if (row < o[middle]) {
				if (row >= o[middle-1]) {

					// We look for the first entry that is greater than or equal to.
					return middle - 1;
				} else
					high = middle-1;
			} else
				low = middle+1;
		}
		return 0;
	}

	/**
	 * Get the table at the given offset.
	 * @param row The row attempting to access.
	 * @param previousOffset the offset of the previously accessed table
	 * @return the table at the given offset, or null if there is not such row.
	 */
	Page getPageAt (int row, int previousOffset) {

		// If nobody is referencing the previous page, page it out.
		int previous = this.getPageIndex(previousOffset);
		int which;
		if (previous >= 0) {
			if ((--references[previous]) == 0)
				pages[previous].free();

			// If practical, use the previous offset as a hint where to start searching.
			if (offsets.length > 2) {
				previous = previous < offsets.length-2 ? previous+1 : offsets.length-2;
				which = this.getPageIndexWithHint(row, previous);
			} else
				which = this.getPageIndex(row);
		} else
			which = this.getPageIndex(row);

		// check to see if the row even exists. If it does not
		// return null.
		if (which == -1)
			return NON_PAGE;

		references[which]++;
		return pages[which];
	}

	/**
	 * Get the table at the given offset.
	 * @param row The row attempting to access.
	 * @param previousOffset the offset of the previously accessed table
	 * @return the table at the given offset, or null if there is not such row.
	 */
	Page getPageAtAndPrefetch (int row, int previousOffset) {

		// If nobody is referencing the previous page, page it out.
		int previous = this.getPageIndex(previousOffset);
		if (previous >= 0 && (--references[previous]) == 0)
			pages[previous].free();

		// return the new page table.
		final int which = this.getPageIndex(row);

		// check to see if the row even exists. If it does not
		// return null.
		if (which == -1)
			return NON_PAGE;

		references[which]++;

		// Do a prefetch, on a separate thread fetch the table.
		new Thread () {
			public void run() {
				pages[which+1].getTable();
			}
		}.start();

		return pages[which];
	}

	/**
	 * return the offset of the table at the given index.
	 * @param index the index of the entry
	 * @return the offset of the table at the given entry.
	 */
	int getOffsetAt (int offset) {
		if (this.offsets.length == 0)
			return 0;
		return this.offsets[this.getPageIndex(offset)];
	}


	/////////////////////////////////////////////////////////
	// Methods to change change the data.
	/////////////////////////////////////////////////////////

	/**
	 * column was inserted, update all the information associated with the columns.
	 * @param col the new column
	 * @param where where to insert the new column
	 */
	private void columnInserted (Column col, int where) {

		// add a label
		int oldcount = columnLabels.length;
		String[] newlabels = new String[oldcount + 1];
		System.arraycopy(columnLabels, 0, newlabels, 0, where);
		newlabels[where] = col.getLabel();
		System.arraycopy(columnLabels, where, newlabels, where+1,
						newlabels.length-(where+1));
		columnLabels = newlabels;

		// add a label
		newlabels = new String[oldcount + 1];
		System.arraycopy(columnComments, 0, newlabels, 0, where);
		newlabels[where] = col.getComment();
		System.arraycopy(columnComments, where, newlabels, where+1,
						newlabels.length-(where+1));
		columnComments = newlabels;

		// add a label
		boolean[] nb = new boolean[oldcount + 1];
		System.arraycopy(columnScalar, 0, nb, 0, where);
		nb[where] = col.getIsScalar();
		System.arraycopy(columnScalar, where, nb, where+1,
						newlabels.length-(where+1));
		columnScalar = nb;

		// add a label
		nb = new boolean[oldcount + 1];
		System.arraycopy(columnNominal, 0, nb, 0, where);
		nb[where] = col.getIsNominal();
		System.arraycopy(columnNominal, where, nb, where+1,
						newlabels.length-(where+1));
		columnNominal = nb;
	}

	/**
	 * column was added, update all the information associated with the columns.
	 * @param col the new column
	 */
	private void columnAdded (Column col) {

		// add a label
		int oldcount = columnLabels.length;
		String[] newlabels = new String[oldcount + 1];
		System.arraycopy(columnLabels, 0, newlabels, 0, oldcount);
		newlabels[oldcount] = col.getLabel();
		columnLabels = newlabels;

		// add a label
		newlabels = new String[oldcount + 1];
		System.arraycopy(columnComments, 0, newlabels, 0, oldcount);
		newlabels[oldcount] = col.getComment();
		columnComments = newlabels;

		// add a label
		boolean [] nb = new boolean[oldcount + 1];
		System.arraycopy(columnScalar, 0, nb, 0, oldcount);
		nb[oldcount] = col.getIsScalar();
		columnScalar = nb;

		// add a label
		nb = new boolean[oldcount + 1];
		System.arraycopy(columnNominal, 0, nb, 0, oldcount);
		nb[oldcount] = col.getIsNominal();
		columnNominal = nb;
	}

	/**
	 * column was removed, update all the information associated with the columns.
	 * @param index the index of the column trashed.
	 */
	private void columnRemoved (int index) {

		// add a label
		int oldcount = columnLabels.length;
		String[] newlabels = new String[oldcount - 1];
		System.arraycopy(columnLabels, 0, newlabels, 0, index);
		System.arraycopy(columnLabels, index+1, newlabels, index, newlabels.length-index);
		columnLabels = newlabels;

		// remove a commment
		newlabels = new String[oldcount - 1];
		System.arraycopy(columnComments, 0, newlabels, 0, index);
		System.arraycopy(columnComments, index+1, newlabels, index, newlabels.length-index);
		columnComments = newlabels;

		// remove a scalar flag
		boolean[] nb = new boolean[oldcount - 1];
		System.arraycopy(columnScalar, 0, nb, 0, index);
		System.arraycopy(columnScalar, index+1, nb, index, nb.length-index);
		columnScalar = nb;

		// remove a nominal flag
		nb = new boolean[oldcount - 1];
		System.arraycopy(columnNominal, 0, nb, 0, index);
		System.arraycopy(columnNominal, index+1, nb, index, nb.length-index);
		columnNominal = nb;
	}

	/**
	 * If nobody is referencing the page, delete it.
	 */
	private void freeIfUnreferenced(int whichPage) {
		if (references[whichPage] == 0)
			pages[whichPage].free();
	}

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

		// Add the column to each page, set all values to missing.
		Class colClass = col.getClass();
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			try {
				col = (Column) colClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
			col.setNumRows(t.getNumRows());
			t.setColumn(col, where);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}

		// update the column info
		this.setColumnLabel(col.getLabel(), where);
		this.setColumnComment(col.getComment(), where);
		this.setColumnIsScalar(col.getIsScalar(), where);
		this.setColumnIsNominal(col.getIsNominal(), where);
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
	public void addColumn(Column col) {

		// Add the column to each page, set all values to missing.
		Class colClass = col.getClass();
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			try {
				col = (Column) colClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
			col.setNumRows(t.getNumRows());
			t.addColumn(col);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}

		// Update the column info.
		this.columnAdded(col);
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
	public void addColumns(Column[] cols) {

		// Make an array of classes to add.
		Class [] colClasses = new Class[cols.length];
		for (int i = 0; i < colClasses.length; i++) {
			colClasses[i] = cols[i].getClass();
		}

		// For each page.
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			for (int j = 0; j < cols.length; j++){
				try {
					cols[j] = (Column) colClasses[i].newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
				cols[j].setNumRows(t.getNumRows());
			}
			t.addColumns(cols);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}

		// update the info
		for (int i = 0; i < cols.length; i++) {
			this.columnAdded(cols[i]);
		}
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

		// Add the column to each page, set all values to missing.
		Class colClass = col.getClass();
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			try {
				col = (Column) colClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
			col.setNumRows(t.getNumRows());
			t.insertColumn(col, where);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}
		this.columnInserted(col, where);
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
	public void insertColumns(Column[] cols, int where) {

		// Make an array of classes to add.
		Class [] colClasses = new Class[cols.length];
		for (int i = 0; i < colClasses.length; i++) {
			colClasses[i] = cols[i].getClass();
		}

		// For each page.
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			for (int j = 0; j < cols.length; j++){
				try {
					cols[j] = (Column) colClasses[i].newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
				cols[j].setNumRows(t.getNumRows());
			}
			t.insertColumns(cols, where);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}

		// update the info
		for (int i = 0; i < cols.length; i++) {
			this.columnInserted(cols[i], where+i);
		}
	}

	/**
	 * Remove the column from each table.
	 */
	public void removeColumn(int position) {

		// remove the column from each page
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			t.removeColumn(position);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}
		this.columnRemoved(position);
	}

	/**
	 * Remove len columns starting at start.
	 * @param start the first column to delete.
	 * @param len the number of columns to delete.
	 */
	public void removeColumns(int start, int len) {

		// remove the column from each page
		int numPages = pages.length;
		for (int i = 0; i < numPages; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			t.removeColumns(start, len);
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}

		// Remove the data for the columns removed.
		for (int i = 0; i < len; i++)
			this.columnRemoved(start);
	}

	/**
	 * This is easy, just add the rows to the last page.
	 * @param howMany how many rows to add.
	 */
	public void addRows(int howMany) {
		int last = pages.length-1;

		// add rows to the table.
		MutableTable t = (MutableTable) pages[last].getTable();
		int tablesize = t.getNumRows();
		t.addRows(howMany);

		// update the subset.
		int [] no = pages[last].getSubset();
		int [] newss = new int [no.length+howMany];
		System.arraycopy(no, 0, newss, 0, no.length);
		int old_len = no.length;
		for (int i = 0; i < howMany; i++) {
			newss[old_len+i] = tablesize+i;
		}
		pages[last].subset = newss;
		pages[last].mark(true);
		this.freeIfUnreferenced(last);
	}

	/**
	 * Remove a row. Take the row out of the table, and then decrement
	 * each of the offsets for the subsequent tables offsets.
	 * @param row remove the row at the given index.
	 */
	public void removeRow(int row) {
		int whichPage = this.getPageIndex(row);
		MutableTable mt = (MutableTable) pages[whichPage].getTable();
		mt.removeRow(row);
		pages[whichPage].mark(true);
		this.freeIfUnreferenced(whichPage);

		// Update the offsets.
		for (int i = whichPage+1; i < pages.length; i++) {
			offsets[i]--;
		}
	}

	int [] removeSubsetEntries(int [] ss, int where, int howmany) {
		int remainder = ss.length - howmany;
		int [] newss = new int [remainder];
		System.arraycopy (ss, 0, newss, 0, where);
		System.arraycopy (ss, where+howmany, newss, where, remainder-where);
		return newss;
	}

	/**
	 * Remove the specified number of rows.
	 * @param start
	 * @param len
	 */
	public void removeRows(int start, int len) {
		int currentPage = this.getPageIndex(start);

		// The first table, we remove starting from an offset, the
		// remaining rows we remove starting at zero.
		int offset = start - this.offsets[currentPage];
		MutableTable t = (MutableTable) pages[currentPage].getTable();
		if (t.getNumColumns() == 0)
			return; // there is nothing to do if there are no columns.

		int [] subset = pages[currentPage].getSubset();
		int total = subset.length - offset;
		int howmany = total < len ? total : len;
		subset = this.removeSubsetEntries(subset, offset, howmany);
		pages[currentPage].subset = subset;
		pages[currentPage].mark(true);
		this.freeIfUnreferenced(currentPage);

		// update the offset of the next page if we can.
		currentPage++;

		// Now compute how many rows remain to be removed.
		howmany = len - howmany;

		// remove rows from consecutive tables until we have
		// removed the right amount.
		while (currentPage < pages.length) {
			subset = pages[currentPage].getSubset();
			int numberRows = subset.length;
			if (howmany != 0) {
				if (numberRows > howmany) {
					subset = this.removeSubsetEntries(subset, 0, howmany);
					pages[currentPage].subset = subset;
					pages[currentPage].mark(true);
					this.freeIfUnreferenced(currentPage);
					offsets[currentPage] -= len - howmany;
					howmany = 0;
				} else {
					subset = this.removeSubsetEntries(subset, 0, numRows);
					pages[currentPage].subset = subset;
					pages[currentPage].mark(true);
					this.freeIfUnreferenced(currentPage);
					offsets[currentPage] -= len - howmany;
					howmany -= numberRows;
				}
			} else
				offsets[currentPage] -= len;
			currentPage++;
		}
	}

	/**
	 * Make a copy of the table with the columns in a new order. This requires
	 * us to make a copy of the PageCache object.
	 * @param newOrder
	 * @return
	 */
	public PageCache reorderColumns(int[] newOrder) {

		// remove the column from each page
		int numPages = pages.length;
		Page [] newpages = new Page [pages.length];
		for (int i = 0; i < numPages; i++) {

			// Make a deep copy of the table.
			MutableTable t = (MutableTable)pages[i].getTable().copy();
			this.freeIfUnreferenced(i);
			t.reorderColumns(newOrder);
			try {
				newpages[i] = new Page (t, false);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		int [] newoffsets = new int [offsets.length];
		System.arraycopy (offsets, 0, newoffsets, 0, offsets.length);
		return new PageCache(newpages, newoffsets, this.defaultPageSize);
	}

	/**
	 * Create a new table for a page of the given size, usually the default
	 * page size.
	 * @param size the number of rows in the resulting table.
	 * @return the new table.
	 */
	Table createNewTable(int size) {
		Table t = pages[0].getTable();
		this.freeIfUnreferenced(0);
		Column [] columns = new Column[t.getNumColumns()];
		for (int i = 0; i < t.getNumColumns(); i++) {
			try {
				columns[i] = (Column) t.getColumn(i).getClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			columns[i].setNumRows(size);
		}
		return new MutableTableImpl(columns);
	}

	/**
	 * create a temporary file.
	 * @returns a temporary file.
	 */
	static final private File createTempFile() throws IOException {
		return File.createTempFile("page-",".ser");
	}

	/**
	 * Make a copy of the page cache including only the given subset. If the subset is
	 * null, return everything.
	 * us to make a copy of the PageCache object.
	 * @param newOrder
	 * @return
	 */
	PageCache copy(int[] newOrder) {

		if (newOrder == null) {
			// remove the column from each page
			int numPages = pages.length;
			Page [] newpages = new Page [numPages];
			for (int i = 0; i < numPages; i++) {

				// Make a deep copy of the table.
				MutableTable t = (MutableTable)pages[i].getTable().copy();
				this.freeIfUnreferenced(i);
				try {
					newpages[i] = new Page (t, false);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			int [] newoffsets = new int [offsets.length];
			System.arraycopy (offsets, 0, newoffsets, 0, offsets.length);
			return new PageCache(newpages,newoffsets, this.defaultPageSize);
		} else {

			// make a subset of the table.
			Table t = pages[0].getTable();
			int numColumns = t.getNumColumns();
			int numPages = newOrder.length / this.defaultPageSize;
			if ((numPages * this.defaultPageSize) != newOrder.length) {

				// Add another page to accomodate the remainder
				numPages++;
			}

			// create the new pages.
			Page [] newpages = new Page[numPages];
			int [] newoffsets = new int [numPages];
			int currentOffset = 0;
			for (int i = 0; i < newpages.length; i++) {
				t = this.createNewTable(defaultPageSize);
				try {
					newpages[i] = new Page(t, true);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				newoffsets[i] = currentOffset;
				currentOffset += defaultPageSize;
			}

			// Create the new page cache.
			PageCache pc = new PageCache(newpages, newoffsets, defaultPageSize);

			// Get the tables we will start with.
			MutableTable newTable = (MutableTable)pc.getPageAt(0, -1).getTable();
			int newoffset = pc.getOffsetAt(0);
			MutableTable origTable = (MutableTable)this.getPageAt(0, -1).getTable();
			int myoffset= this.getOffsetAt(0);
			int [] coltypes = new int [numColumns];
			for (int ci = 0 ; ci < numColumns; ci++) {
				coltypes[ci] = origTable.getColumnType(ci);
			}

			// Now we have the data, transfer the data from one table to the other.
			// We really have to transfer the data in it's native format. We know these
			// tables are huge, and the alternative it to read the data as an object, and
			// then write it to the other table as an object. Man! that's a lot of objects
			// to allocate!!!
			for (int i = 0; i < newOrder.length; i++) {
				int newlocation = newOrder[i];
				for (int col = 0; col < numColumns; col++){

					switch (coltypes[col]) {
						case ColumnTypes.BOOLEAN: {

							// get the old data.
							boolean a;
							try {
								a = origTable.getBoolean(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getBoolean(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setBoolean(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setBoolean(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.BYTE: {

							// get the old data.
							byte a;
							try {
								a = origTable.getByte(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getByte(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setByte(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setByte(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.BYTE_ARRAY: {

							// get the old data.
							byte[] a;
							try {
								a = origTable.getBytes(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getBytes(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setBytes(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setBytes(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.CHAR: {

							// get the old data.
							char a;
							try {
								a = origTable.getChar(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getChar(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setChar(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setChar(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.CHAR_ARRAY: {

							// get the old data.
							char[] a;
							try {
								a = origTable.getChars(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getChars(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setChars(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setChars(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.DOUBLE: {

							// get the old data.
							double a;
							try {
								a = origTable.getDouble(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getDouble(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setDouble(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setDouble(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.FLOAT: {

							// get the old data.
							float a;
							try {
								a = origTable.getFloat(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getFloat(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setFloat(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setFloat(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.SHORT: {

							// get the old data.
							short a;
							try {
								a = origTable.getShort(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getShort(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setShort(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setShort(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.INTEGER: {

							// get the old data.
							int a;
							try {
								a = origTable.getInt(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getInt(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setInt(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setInt(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.LONG: {

							// get the old data.
							long a;
							try {
								a = origTable.getLong(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getLong(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setLong(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setLong(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.NOMINAL:
						case ColumnTypes.STRING: {

							// get the old data.
							String a;
							try {
								a = origTable.getString(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getString(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setString(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setString(a, i - newoffset, col);
							}
							break;
						}
						case ColumnTypes.OBJECT: {

							// get the old data.
							Object a;
							try {
								a = origTable.getObject(newOrder[i] - myoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								origTable = (MutableTable)this.getPageAt(newOrder[i], myoffset).getTable();
								myoffset = this.getOffsetAt(newOrder[i]);
								a = origTable.getObject(newOrder[i] - myoffset, col);
							}

							// set the new data.
							try {
								newTable.setObject(a, i - newoffset, col);
							} catch (ArrayIndexOutOfBoundsException aiob) {
								newTable = (MutableTable)pc.getPageAt(i, newoffset).getTable();
								newoffset = this.getOffsetAt(i);
								newTable.setObject(a, i - newoffset, col);
							}
							break;
						}
					}
				}
			}
			return new PageCache(newpages,newoffsets, this.defaultPageSize);
		}
	}

	/**
	 * Swap two rows. They can potentially be in different pages, but it doesn't matter,
	 * we treat them the same either way.
	 * @param pos1 the first position.
	 * @param pos2 the second postion.
	 */
	public void swapRows(int pos1, int pos2) {

		// Get the page indices, the tables, and the offsets of the positions in those tables.
		int firstIndex = this.getPageIndex(pos1);
		int secondIndex = this.getPageIndex(pos2);
		MutableTable firstTable = (MutableTable)pages[firstIndex].getTable();
		MutableTable secondTable = (MutableTable)pages[secondIndex].getTable();
		int firstOffset = pos1 - offsets[firstIndex];
		int secondOffset = pos2 = offsets[secondIndex];

		int nc = firstTable.getNumColumns();
		for (int i = 0; i < nc; i++) {
			Object obj = firstTable.getObject(firstOffset, i);
			firstTable.setObject(secondTable.getObject(secondOffset, i), firstOffset, i);
			secondTable.setObject(obj, secondOffset, i);

			// swap missing values.
			boolean missing1 = firstTable.isValueMissing(pos1, i);
			boolean missing2 = secondTable.isValueMissing(pos2, i);
			firstTable.setValueToMissing(missing2, pos1, i);
			secondTable.setValueToMissing(missing1, pos2, i);

		}

		// Dirty and free up.
		pages[firstIndex].mark(true);
		this.freeIfUnreferenced(firstIndex);
		pages[secondIndex].mark(true);
		this.freeIfUnreferenced(secondIndex);
	}

	/**
	 * Swap the position of two columns.
	 * @param pos1
	 * @param pos2
	 */
	public void swapColumns(int pos1, int pos2) {

		// remove the column from each page
		int len = pages.length;
		for (int i = 0; i < len; i++) {
			MutableTable t = (MutableTable)pages[i].getTable();
			t.swapColumns(pos1, pos2);

			// mark dirty and free them up
			pages[i].mark(true);
			this.freeIfUnreferenced(i);
		}
	}

	//////  These methods change the table subset.

	/**
	 * When we subset a paging table, the offsets arrays will reflect the table view of the
	 * data, not the physical arrangement of the data. In this way, the searches will be much
	 * faster.
	 */
	PageCache subset (int start, int len) throws IOException {
		Page [] newpages = new Page[this.pages.length];
		int [] newoffsets = new int[this.pages.length];
		int newPageCount = 0;

		// Get the page at start, and it's offsets.
		int which = this.getPageIndex(start);
		Page currentPage = this.pages[which];
		Table currentTable = currentPage.getTable();
		int [] currentSubset = currentPage.getSubset();
		int currentOffset = this.offsets[which];
		this.freeIfUnreferenced(which);

		// Copy the subset of the subset into a new subset array.
		int offset = start - currentOffset;
		int amount = currentSubset.length - offset;
		amount = amount > len ? len : amount;
		int [] newsubset = new int [amount];
		System.arraycopy (currentSubset, offset, newsubset, 0, amount);

		// We have the new subset, the old table and page, now
		// we create a new page sharing the same page data file but
		// a different offset file.
		newpages[newPageCount] = new Page(currentPage.pageFile, currentTable, newsubset);
		newoffsets[newPageCount++] = 0;
		int total = amount;
		while (total < len) {
			which++;
			// Get the page at start, and it's offsets.
			currentPage = this.pages[which];
			currentTable = currentPage.getTable();
			currentSubset = currentPage.getSubset();
			amount = currentSubset.length;
			amount = amount > len-total ? len-total : amount;
			newsubset = new int [amount];
			System.arraycopy (currentSubset, 0, newsubset, 0, amount);

			// We have the new subset, the old table and page, now
			// we create a new page sharing the same page data file but
			// a different offset file.
			newpages[newPageCount] = new Page(currentPage.pageFile, currentTable, newsubset);
			newoffsets[newPageCount++] = total;
			total += amount;
			this.freeIfUnreferenced(which);
		}

		// Create the accuratly sized pages and offsets array.
		Page [] tmpp = new Page[newPageCount];
		int [] tmpo = new int[newPageCount];
		System.arraycopy (newpages, 0, tmpp, 0, newPageCount);
		System.arraycopy (newoffsets, 0, tmpo, 0, newPageCount);
		return new PageCache(tmpp, tmpo, this.defaultPageSize);
	}

	/**
	 * When we subset a paging table, the offsets arrays will reflect the table view of the
	 * data, not the physical arrangement of the data. In this way, the searches will be much
	 * faster.
	 */
	PageCache subset (int [] rows) throws IOException {

		// First, sort the array.
		Arrays.sort(rows);
		Page [] newpages = new Page[this.pages.length];
		int [] newoffsets = new int[this.pages.length];
		int newPageCount = 0;

		// Get the page at start, and it's offsets.
		int which = this.getPageIndex(rows[0]);
		Page currentPage = this.pages[which];
		int [] currentSubset = currentPage.getSubset();
		this.freeIfUnreferenced(which);
		int currentOffset = this.offsets[which];

		// Copy the subset of the subset into a new subset array.
		int [] newsubset = this.populateSubset(rows, 0, currentSubset, currentOffset);

		// We have the new subset, the old table and page, now
		// we create a new page sharing the same page data file but
		// a different offset file.
		newpages[newPageCount] = new Page(currentPage.pageFile, currentPage.numRows,
					currentPage.numColumns, newsubset);
		newoffsets[newPageCount++] = 0;
		int total = newsubset.length;
		int len = rows.length;
		while (total < len) {
			which++;
			// Get the page at start, and it's offsets.
			currentPage = this.pages[which];
			currentSubset = currentPage.getSubset();
			this.freeIfUnreferenced(which);
			currentOffset = this.offsets[which];
			newsubset = this.populateSubset(rows, total, currentSubset, currentOffset);

			// We have the new subset, the old table and page, now
			// we create a new page sharing the same page data file but
			// a different offset file.
			newpages[newPageCount] = new Page(currentPage.pageFile, currentPage.numRows,
								currentPage.numColumns, newsubset);
			newoffsets[newPageCount++] = total;
			total += newsubset.length;
		}

		// Create the accuratly sized pages and offsets array.
		Page [] tmpp = new Page[newPageCount];
		int [] tmpo = new int[newPageCount];
		System.arraycopy (newpages, 0, tmpp, 0, newPageCount);
		System.arraycopy (newoffsets, 0, tmpo, 0, newPageCount);
		return new PageCache(tmpp, tmpo, this.defaultPageSize);
	}

	/**
	 * Returns the index of the first item in the array greater than last. If
	 * there are no entries greater than last, it returns the array size.
	 * @param last the index.
	 * @param values the values
	 * @return the value.
	 */
	private int getLastEntry (int last, int [] values) {
		int i = values.length;
		while (values[i-1] > last)
			i--;
		return i;
	}

	/**
	 * Given a subset array, this method will create a subset that represents the subset. Starting
	 * with the value entry in rows specified by firstIndexInRows, this method will determine how
	 * big the resulting subset array is, it will allocate it and initialize it's values. We will
	 * identify the first entry in the original subset array to be included, the index of the last
	 * entry to be included. We then allocate the array of the appropriate size, and copy the entries
	 * from the original subset to the new subset array. Also, the original subset array is offset
	 * by currentOffset, so appropriate adjustments are made.
	 * @param rows
	 * @param start
	 * @param current
	 * @param currentOffset
	 * @return
	 */
	int [] populateSubset(int [] rows, int firstIndexInRows, int [] current, int currentOffset) {
		int firstIndexInCurrent = rows[firstIndexInRows] - currentOffset;
		int lastValueInCurrent = (current.length-1) + currentOffset;
		int lastIndexInRows = this.getLastEntry(lastValueInCurrent, rows);
		int numEntriesInRows = lastIndexInRows-firstIndexInRows;
		int [] newindices = new int [numEntriesInRows];

		// Now populate the new subset.
		for (int i = 0, ri = firstIndexInRows; i < numEntriesInRows;) {
			newindices[i++] = current[rows[firstIndexInRows] - currentOffset];
			firstIndexInRows++;

		}
		return newindices;
	}
}
