package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.paging.*;
import java.io.*;

/**
 * Read a file to a SubsetPagingTable.
 */
public class ParseFileToPagingTable extends ParseFileToTable {

	private int numRowsPerPage = 50000;
	public int getNumRowsPerPage() {
		return numRowsPerPage;
	}
	public void setNumRowsPerPage(int nr) {
		numRowsPerPage = nr;
	}

	private boolean pageSubsets = true;
	public boolean getPageSubsets() {
		return pageSubsets;
	}
	public void setPageSubsets(boolean nr) {
		pageSubsets = nr;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] retVal = new PropertyDescription[3];
		retVal[0] =
			new PropertyDescription(
				"useBlanks",
				"Set Blanks to be Missing Values",
				"When true, any blank entries in the file will be set as missing values in the table.");
		retVal[1] =
			new PropertyDescription(
				"numRowsPerPage",
				"The Number of Rows Per Page",
				"The maximum number of rows in a page of the PagingTable.");
		retVal[2] =
			new PropertyDescription(
				"pageSubsets",
				"Page Subset Arrays",
				"Set this to page subset arrays to disk rather than keeping them in memory.");
		return retVal;
	}

	public String getOutputInfo(int i) {
		return "The data read from the FlatFileParser in a PagingTable.";
	}

	public String getOutputName(int i) {
		return "Table";
	}

	public String getModuleInfo() {
		// return "Read the data from a FlatFileParser to a PagingTable.";
		StringBuffer sb = new StringBuffer("<p>Overview: ");
		sb.append("Given a FlatFileParser, this module reads the data ");
		sb.append("from the flat file on disk into a PagingTable, a type ");
		sb.append("of Table capable of keeping only part of itself in ");
		sb.append("memory at any given time.");
		sb.append("</p>");
		return sb.toString();
	}

	public String getModuleName() {
		return "Parse File to Paging Table";
	}

	public void doit() throws Exception {
		try {
			super.doit();
		} catch (OutOfMemoryError e) {
			throw new Exception(
				getAlias()
					+ ": The file could not be read in.  Try reducing "
					+ "the number of rows per page.");
		}
	}

	public Table createTable(FlatFileParser df) {
		int numRows = df.getNumRows();
		int numColumns = df.getNumColumns();

		// now figure out how many sub-tables we will make.
		int numTables = (int) Math.ceil(((double) numRows) / ((double) numRowsPerPage));
		
		// create the pages.
		Page [] pages = new Page[numTables];
		
		// the number of rows per page
		int[] pageRowNums = new int[numTables];
		int[] offsets = new int[numTables];
		int curNum = 0;
		for (int i = 0; i < numTables; i++) {
			offsets[i] = curNum;
			if ((curNum + numRowsPerPage) <= numRows) {
				pageRowNums[i] = numRowsPerPage;
				curNum += pageRowNums[i];
			} else
				pageRowNums[i] = numRows - curNum;
				
		}

		curNum = 0;
		boolean hasTypes = false;

		// for each table
		for (int nt = 0; nt < numTables; nt++) {

			// create the columns
			Column[] columns = new Column[numColumns];
			for (int col = 0; col < columns.length; col++) {
				int type = df.getColumnType(col);
				columns[col] =
					ColumnUtilities.createColumn(type, pageRowNums[nt]);

				if (type != -1)
					hasTypes = true;

				// set the label
				String label = df.getColumnLabel(col);
				if (label != null)
					columns[col].setLabel(label);
			}

			MutableTableImpl ti = new MutableTableImpl(columns);
			int offset = nt * numRowsPerPage; //
			for (int i = 0; i < pageRowNums[nt]; i++) {
				ParsedLine pl = df.getRowElements(i + offset);
				char[][] row = pl.elements;
				boolean[] bl = pl.blanks;
				curNum++;
				if (row != null)
					for (int j = 0; j < columns.length; j++) {
						boolean isMissing = true;
						char[] elem = row[j]; //(char[])row.get(j);

						// test to see if this is '?'
						// if it is, this value is missing.
						for (int k = 0; k < elem.length; k++) {
							if (elem[k] != QUESTION && elem[k] != SPACE) {
								isMissing = false;
								break;
							}
						}

						// if the value was not missing, just put it in the table
						if (!isMissing && !bl[j]) {
							try {
								ti.setChars(elem, i, j);
							}
							// if there was a number format exception, set the value
							// to 0 and mark it as missing
							catch (NumberFormatException e) {
								ti.setChars(
									Integer.toString(0).toCharArray(),
									i,
									j);
								ti.setValueToMissing(true, i, j);
							}
						}
						// if the value was missing..
						else {
							// put 0 in a numeric column and set the value to missing
							if (df.getColumnType(j) == ColumnTypes.INTEGER
								|| df.getColumnType(j) == ColumnTypes.DOUBLE
								|| df.getColumnType(j) == ColumnTypes.FLOAT
								|| df.getColumnType(j) == ColumnTypes.LONG
								|| df.getColumnType(j) == ColumnTypes.SHORT
								|| df.getColumnType(j) == ColumnTypes.BYTE) {

								ti.setChars(Integer.toString(0).toCharArray(), i, j);
								ti.setValueToMissing(true, i, j);
							}
							// otherwise put the '?' in the table and set the value to missing
							else {
								ti.setChars(elem, i, j);
								ti.setValueToMissing(true, i, j);
							}
						}
					}
			}
			try {
				if (pageSubsets) { 
					pages[nt] = new SubsetsPage(ti, false);
				} else {
					pages[nt] = new Page(ti, false);
				}
				pages[nt] = new Page(ti, false);
			} catch (IOException e) {
				throw new RuntimeException ("We can't create the temporary files!");
			}
		}
		PageCache pc = new PageCache(pages, offsets, numRowsPerPage);
		SubsetPagingTable spt = new SubsetPagingTable(pc);
		return spt;
	}

}