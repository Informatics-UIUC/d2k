package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * The <code>Page</code> class encapsulates a <code>Table</code> that can be
 * paged to and from disk.
 *
 * @author gpape
 */
public class SubsetsPage extends Page {
	static final long serialVersionUID = -3452910203130295662L;

	/** the file we save the table in. */
	File subsetFile;

	/** these are used in the naming of the temp files. */
	static final String OFFSET_PREFIX = "offsets-";

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

	public SubsetsPage(Table table, boolean keepInMemory) throws IOException {
		this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
		this.pageFile.deleteOnExit();
		this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
		this.subsetFile.deleteOnExit();
		this.table = table;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		subset = new int[this.numRows];
		for (int i = 0; i < this.numRows; i++)
			this.subset[i] = i;
		pageOut();
		if (!keepInMemory) {
			this.table = null;
			this.subset = null;
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
	SubsetsPage(Table table, int[] subset, boolean keepInMemory)
		throws IOException {
		this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
		this.pageFile.deleteOnExit();
		this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
		this.subsetFile.deleteOnExit();
		this.table = table;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		this.subset = subset;
		pageOut();
		if (!keepInMemory) {
			this.table = null;
			this.subset = null;
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
	SubsetsPage(File pp, Table table, int[] s) throws IOException {
		this.pageFile = pp;
		this.table = null;
		numRows = table.getNumRows();
		numColumns = table.getNumColumns();
		this.subset = null;

		// Write the subset to an object output stream.
		this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
		this.subsetFile.deleteOnExit();
		ObjectOutputStream O =
			new ObjectOutputStream(new FileOutputStream(subsetFile));
		O.writeObject(s);
		O.close();
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Return the table subset, if it is spooled off to disk read it in.
	 * @return the table.
	 */
	int[] getSubset() {
		if (table == null)
			pageIn();
		return subset;
	}

	/**
	 * Reads the table in from disk.
	 * <p>
	 * <b>WARNING:</b> This method must only be called while the resource is
	 * externally write-locked!
	 */
	void pageIn() {

		if (this.pageFile == null)
			return;
		try {
			super.pageIn();

			// Read the subset, it is in a seperate file so we can have subset tables which share the
			// same data.	
			long start = System.currentTimeMillis();
			ObjectInputStream I =
				new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(subsetFile)));
			this.subset = (int[]) I.readObject();
			I.close();
			long start2 = System.currentTimeMillis();
			fileReadTime += (start2 - start);
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
		if (pageFile == null)
			return;
		try {
			super.pageOut();
			
			// Write the subset to an object output stream.
			ObjectOutputStream O = new ObjectOutputStream(new FileOutputStream(subsetFile));
			O.writeObject(this.subset);
			O.close();
		} catch (Exception e) {
			System.err.println("Page " + pageFile + ": exception on pageOut:");
			e.printStackTrace(System.err);
		}
		dirty = false;
	}

	/**
	 * Writes the table out to disk, if it has been modified, and frees the table
	 * for garbage collection.
	 * <p>
	 * <b>WARNING:</b> This method must only be called while the resource is
	 * externally write-locked!
	 */
	void free() {
		super.free();
		subset = null;
	}
}
