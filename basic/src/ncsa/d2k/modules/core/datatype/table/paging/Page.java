package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * The <code>Page</code> class encapsulates a <code>Table</code> that can be
 * paged to and from disk. Pages also handle subset arrays, but this implementation
 * does not page those out to disk, they remain resident in memory.
 *
 * @author gpape, tredman
 */
public class Page {
	static final long serialVersionUID = -3452910203130295662L;

	/** Sedt when we change the data. */
	protected volatile boolean dirty = false;
	
	/** the time of last reference. */
	protected long timestamp;

	/** the file we save the table in. */
	protected File pageFile;
	
	/** the table we are paging in and out. */
	protected Table table;

	/** this is the subset array for the subsetted table. */
	protected int [] subset;
	
	/** these are used in the naming of the temp files. */
	protected static final String PAGE_PREFIX = "page-";
	protected static final String SUFFIX = ".ser";

	// In practice, getNumRows() and getNumColumns() are called so frequently
	// that we really don't want to require the table paged in before we can
	// compute them. Thus, we keep them here, and ensure in the method calls
	// that they are kept consistent with the table.

	protected int numRows, numColumns;
	Page(){
	}
	
	/**
	 * Constructs a new <code>Page</code> that can read the given
	 * <code>Table</code> from (and write it to) the given <code>File</code>.
	 * The <code>Table</code> itself will be retained in memory if and only if
	 * the <code>keepInMemory</code> flag is <code>true</code>.
	 *
	 * @param file            a file on disk
	 * @param table           the <code>Table</code> to store
	 * @param keepInMemory    keep the <code>Table</code> in memory after
	 *                        construction?
	 */
	public Page(Table table, boolean keepInMemory) throws IOException{
		this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
		this.pageFile.deleteOnExit();
		this.table = table;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		subset = new int [this.numRows];
		for (int i = 0; i < this.numRows; i++) this.subset[i] = i;
		pageOut();
		if (!keepInMemory) {
			this.table = null;
		}
		timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Constructs a new <code>Page</code> that can read the given
	 * <code>Table</code> from (and write it to) the given <code>File</code>.
	 * The <code>Table</code> itself will be retained in memory if and only if
	 * the <code>keepInMemory</code> flag is <code>true</code>.
	 *
	 * @param file            a file on disk
	 * @param table           the <code>Table</code> to store
	 * @param keepInMemory    keep the <code>Table</code> in memory after
	 *                        construction?
	 */
	Page(Table table, int [] subset, boolean keepInMemory) throws IOException {
		this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
		this.pageFile.deleteOnExit();
		this.table = table;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		this.subset = subset;
		pageOut();
		if (!keepInMemory) {
			this.table = null;
		}
		timestamp = System.currentTimeMillis();
	}


	/**
	 * If we are creating a new subset of the table, the page for the data
	 * already exists, we just need to create a new subset. The table has already
	 * been written to memory since it is already part of another paging table.
	 * @param file a file on disk containing the data
	 * @param table the <code>Table</code> to store
	 * @param s the subset data.
	 * @param keepInMemory keep the <code>Table</code> in memory after
	 *                        construction?
	 */
	Page(File pp, Table table, int [] s) throws IOException {
		this.pageFile = pp;
		this.table = null;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		this.subset = s;
		timestamp = System.currentTimeMillis();
	}
	
	/**
	 * If we are creating a new subset of the table, the page for the data
	 * already exists, we just need to create a new subset. The table has already
	 * been written to memory since it is already part of another paging table.
	 * @param file a file on disk containing the data
	 * @param table the <code>Table</code> to store
	 * @param s the subset data.
	 * @param keepInMemory keep the <code>Table</code> in memory after
	 *                        construction?
	 */
	Page(File pp, int rows, int cols, int [] s) throws IOException {
		this.pageFile = pp;
		this.table = null;
		numRows = rows;
		numColumns = cols;
		this.subset = s;
		timestamp = System.currentTimeMillis();
	}
	/**
	 * The table must be paged in for this to work, it will update the count
	 * of the number of rows and columns in the table.
	 */
	private void updateCounts() {
		if (table == null) pageIn();
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
	}
	
	/**
	 * Return the table, if it is spooled off to disk read it in.
	 * @return the table.
	 */
	synchronized Table getTable() {
		if (table == null)
			pageIn();
		return table;
	}
	
	/**
	 * Return the table subset, if it is spooled off to disk read it in.
	 * @return the table.
	 */
	int [] getSubset() {
		return subset;
	}
	
	/**
	 * returns true if the table is loaded.
	 * @return true if the table is loaded.
	 */
	boolean hasTable() {
		return table != null;
	}
	
	/**
	 * Reads the table in from disk.
	 * <p>
	 * <b>WARNING:</b> This method must only be called while the resource is
	 * externally write-locked!
	 */
	long fileReadTime = 0;	// LAM-tlr remove these.
	long deserializeTime = 0;
	final static boolean standardRead = true;
	void pageIn() {
		
		if (this.pageFile == null) 
			return;
			
		try {
			long start, start2, end;
			if (standardRead) {
				start = System.currentTimeMillis();
				ObjectInputStream I = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pageFile)));
				Column [] cols = new Column[numColumns];
				for (int i = 0; i < numColumns; i++) {
					int type = I.readInt();
					switch (type) {
						case ColumnTypes.BOOLEAN:
							cols[i] = new BooleanColumn((boolean[])I.readObject());
							break;
							
						case ColumnTypes.BYTE:
							cols[i] = new ByteColumn((byte[])I.readObject());
							break;
							
						case ColumnTypes.CHAR:
							cols[i] = new CharColumn((char[])I.readObject());
							break;
							
						case ColumnTypes.CHAR_ARRAY:
							cols[i] = new CharArrayColumn((char[][])I.readObject());
							break;
							
						case ColumnTypes.BYTE_ARRAY:
							cols[i] = new ByteArrayColumn((byte[][])I.readObject());
							break;
							
						case ColumnTypes.STRING:
							cols[i] = new StringColumn((String[])I.readObject());
							break;
							
						case ColumnTypes.SHORT:
							cols[i] = new ShortColumn((short[])I.readObject());
							break;
							
						case ColumnTypes.INTEGER:
							cols[i] = new IntColumn((int[])I.readObject());
							break;
							
						case ColumnTypes.LONG:
							cols[i] = new LongColumn((long[])I.readObject());
							break;
							
						case ColumnTypes.FLOAT:
							cols[i] = new FloatColumn((float[])I.readObject());
							break;
							
						case ColumnTypes.DOUBLE:
							cols[i] = new DoubleColumn((double[])I.readObject());
							break;
							
						case ColumnTypes.OBJECT:
							cols[i] = new ObjectColumn((Object[])I.readObject());
							break;
						
						default:
							cols[i] = (Column) I.readObject();
							break;							
					}
					boolean [] mv = (boolean[]) I.readObject();
					((MissingValuesColumn)cols[i]).setMissingValues(mv);
				}
				this.table = new MutableTableImpl(cols);
				I.close();
				start2 = end = System.currentTimeMillis();
			}
			fileReadTime += (start2-start);
			deserializeTime += (end-start2);
		} catch (Exception e) {
			System.err.println("Page " + pageFile + ": exception on pageIn:");
			e.printStackTrace(System.err);
		} finally {
			dirty = false;
		}

	}

	/**
	 * Writes the table out to disk.
	 * <p>
	 * <b>WARNING:</b> This method must only be called while the resource is
	 * externally write-locked!
	 */
	protected void pageOut() {
		if (pageFile == null) return;
		try {
			
			// This will create a new file, if and only if the file does not already exist.
			if (!pageFile.getParentFile().exists())
				pageFile.getParentFile().mkdirs();
			
			// If we are writing, it is possible that the table
			// has changed, let's make sure we have the right number of
			// rows and columns. 
			this.updateCounts();
			
			// Write the data to an object output stream.
			ObjectOutputStream O =
				new ObjectOutputStream(new FileOutputStream(pageFile));
			Column [] cols = ((TableImpl)table).getRawColumns();
			for (int i = 0; i < numColumns; i++) {
				int type = cols[i].getType();
				O.writeInt(type);
				switch (type) {
					case ColumnTypes.BOOLEAN:
					case ColumnTypes.BYTE:
					case ColumnTypes.CHAR:
					case ColumnTypes.CHAR_ARRAY:
					case ColumnTypes.BYTE_ARRAY:
					case ColumnTypes.STRING:
					case ColumnTypes.SHORT:
					case ColumnTypes.INTEGER:
					case ColumnTypes.LONG:
					case ColumnTypes.FLOAT:
					case ColumnTypes.DOUBLE:
					case ColumnTypes.OBJECT:
						O.writeObject(cols[i].getInternal());
						break;
					default:
						O.writeObject(cols[i]);
						break;							
				}
				O.writeObject(((MissingValuesColumn)cols[i]).getMissingValues());
			}
			O.close();
		} catch (Exception e) {
			System.err.println("Page " + pageFile + ": exception on pageOut:");
			e.printStackTrace(System.err);
		} finally {
			dirty = false;
		}

	}

	/**
	 * Writes the table out to disk, if it has been modified, and frees the table
	 * for garbage collection.
	 * <p>
	 * <b>WARNING:</b> This method must only be called while the resource is
	 * externally write-locked!
	 */
	void free() {
		if (dirty)
			pageOut();
		table = null;
	}

	/**
	 * Time-stamps this page, and optionally marks it as dirty (modified).
	 * <p>
	 * <b>NOTE:</b> This method <i>should</i> only be called while the resource
	 * is externally locked (long assignment is not actually an atomic operation
	 * in Java). Failure to do so should not cause serious problems, however.
	 *
	 * @param modified        does this reference represent a modification of the
	 *                        underlying <code>Table</code>?
	 */
	void mark(boolean modified) {
		if (modified) // if no modification, dirty should not be changed
			dirty = true;
		this.updateCounts();
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Returns the <code>long</code> time stamp at which this page was last
	 * referenced (read from or written to).
	 *
	 * @return                this page's reference timestamp
	 */
	long time() {
		return timestamp;
	}

	/**
	 * Returns an exact copy of this <code>Page</code>, with its underlying
	 * <code>Table</code> optionally retained in memory.
	 * <p>
	 * @param keepInMemory    keep the <code>Table</code> in memory after
	 *                        construction?
	 *
	 * @return                the new <code>Page</code> copy
	 */
	Page copy(boolean keepInMemory) {
		File newFile = null;
		try {
			newFile = File.createTempFile("d2k-", null);
			newFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Page newPage;
		try {
			newPage = new Page(table.copy(), false);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		if (keepInMemory)
			newPage.pageIn();
		newPage.mark(false);
		newPage.numRows = numRows;
		newPage.numColumns = numColumns;
		return newPage;
	}
}
