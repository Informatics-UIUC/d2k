package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.io.*;
import java.util.*;

/**
 * Contains methods and data members common to all delimted file readers.
 */
abstract public class AbstractDelimitedReader extends InputModule {

	/** the delimiter identified. */
	protected char delimiterOne;

	protected static final String NOMINAL = "Nominal";
	protected static final String SCALAR = "Scalar";
	protected static final String STRING_TYPE = "String";
	protected static final String FLOAT_TYPE = "float";
	protected static final String DOUBLE_TYPE = "double";
	protected static final String INT_TYPE = "int";
	protected static final String BOOLEAN_TYPE = "boolean";
	protected static final String CHAR_TYPE = "char[]";
	protected static final String BYTE_TYPE = "byte[]";
	protected static final String LONG_TYPE = "long";
	protected static final String SHORT_TYPE = "short";
	protected static final String IN = "in";
	protected static final String OUT = "out";
	protected static final String OMIT = "omit";

	/**
		Create a column given the type and size.
		@param type the type of column to create
		@param size the initial size of the column
		@return a new, empty column
	*/
	protected Column createColumn(String type, int size) {
		if(type.compareToIgnoreCase(STRING_TYPE) == 0)
			return new StringColumn(size);
		else if(type.compareToIgnoreCase(FLOAT_TYPE) == 0)
			return new FloatColumn(size);
		else if(type.compareToIgnoreCase(DOUBLE_TYPE) == 0)
			return new DoubleColumn(size);
		else if(type.compareToIgnoreCase(INT_TYPE) == 0)
			return new IntColumn(size);
		else if(type.compareToIgnoreCase(BOOLEAN_TYPE) == 0)
			return new BooleanColumn(size);
		else if(type.compareToIgnoreCase(CHAR_TYPE) == 0)
			return new ContinuousCharArrayColumn(size);
		else if(type.compareToIgnoreCase(BYTE_TYPE) == 0)
			return new ContinuousByteArrayColumn(size);
		else if(type.compareToIgnoreCase(LONG_TYPE) == 0)
			return new LongColumn(size);
		else if(type.compareToIgnoreCase(SHORT_TYPE) == 0)
			return new ShortColumn(size);
		else
			return new StringColumn(size);
	}

	/**
		Loop through the items in column, if they can all be represented
		numerically, return true.  Otherwise return false.
		@param column the column to test
		@return true if column contains only numeric data, false otherwise
	*/
	protected boolean isNumericColumn(Column column) {
        int numRows = column.getNumRows();
		for(int row = 0; row < numRows; row++) {
			try {
				Double d = Double.valueOf(column.getString(row));
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
		Create a DoubleColumn from a ByteArrayPointerColumn
		@param sc the original column
		@return a DoubleColumn with the values from sc
	*/
	protected DoubleColumn toDoubleColumn(ContinuousCharArrayColumn sc) {
		int numRows = sc.getNumRows ();
		DoubleColumn retVal = new DoubleColumn(numRows);
		for(int row = 0; row < numRows; row++)
			retVal.setDouble( Double.valueOf(
				sc.getString(row)).doubleValue(), row);
		retVal.setLabel(sc.getLabel());
		retVal.setComment(sc.getComment());
		return retVal;
	}

	/**
		Create a StringColumn from a ByteArrayPointerColumn
		@param sc the original column
		@return a StringColumn with the values from sc
	*/
	protected StringColumn toStringColumn(ContinuousCharArrayColumn sc) {
		int numRows = sc.getNumRows ();
		StringColumn retVal = new StringColumn(numRows);
		for(int row = 0; row < numRows; row++) {
			char[] val = sc.getChars(row);
			if(val == null)
				val = new char[0];
			retVal.setString(new String(val), row);
		}
		retVal.setLabel(sc.getLabel());
		retVal.setComment(sc.getComment());

		return retVal;
	}

	/////// Private methods //////////////

	protected static final char TAB = '\t';
	protected static final char SPACE = ' ';
	protected static final char COMMA = ',';
	protected static final char EQUALS = '=';

	/**
	 * This method will search the document, counting the number of each
	 * possible delimiter per line to identify the delimiter to use. If in
	 * the first pass we can not find a single delimiter that that can be found
	 * the same number of times in each line, we will strip all the whitespace
	 * off the start and end of the lines, and try again. If then we still can
	 * not find the delimiter, we will fail.
	 * @param f the file to check for delimiters
	 * @returns one from among the set of delimiters we look for (',', ' ', '\t'), or '=' if the search failed.
	 */
	protected char findDelimiter (File f) {
		int counters [] = new int [3];
		final int tabIndex = 0, spaceIndex = 1, commaIndex = 2;

		// Now just count them.
		int commaCount = -1, spaceCount = -1, tabCount = -1;
		boolean isComma = true, isSpace = true, isTab = true;
		String line [] = new String [10];
		try {
			BufferedReader reader = new BufferedReader (new FileReader (f));

			// read the file in one row at a time
			int currentRow = 0;
			while ((line[currentRow] = reader.readLine ()) != null) {
				char[] bytes = line [currentRow].toCharArray ();

				// In this row, count instances of each delimiter
				for (int i = 0 ; i < bytes.length ; i++) {
					switch (bytes [i]) {
						case TAB:
							counters [tabIndex] ++;
							break;
						case SPACE:
							counters [spaceIndex] ++;
							break;
						case COMMA:
							counters [commaIndex] ++;
							break;
					}
				}

				// If first row, just init the counts...
				if (currentRow == 0) {
					commaCount = counters [commaIndex] == 0 ? -1 : counters[commaIndex];
					spaceCount = counters [spaceIndex] == 0 ? -1 : counters[spaceIndex];
					tabCount = counters [tabIndex] == 0 ? -1 : counters[tabIndex];
				} else {

					// Check that the counts remain the same.
					if (counters [commaIndex] != commaCount)
						isComma = false;
					if (counters [spaceIndex] != spaceCount)
						isSpace = false;
					if (counters [tabIndex] != tabCount)
						isTab = false;
				}
				counters [tabIndex] = counters [spaceIndex] = counters [commaIndex] = 0;
				if (++currentRow == 10)
					break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return EQUALS;
		}

		// Did one of the possible delimiters come up a winner?
		if (isComma && !isSpace && !isTab)
			return COMMA;
		if (!isComma && isSpace && !isTab)
			return SPACE;
		if (!isComma && !isSpace && isTab)
			return TAB;

		// OK, that didn't work. Lets trim the strings and see if it will work the.
		// read the file in one row at a time
		isComma = true;
		isSpace = true;
		isTab = true;
		for (int currentRow = 0; currentRow < 10 ;currentRow++) {
			String tmp = line [currentRow].trim ();
			char [] bytes = tmp.toCharArray ();
			counters [tabIndex] = counters [spaceIndex] = counters [commaIndex] = 0;
			// In this row, count instances of each delimiter
			for (int i = 0 ; i < bytes.length ; i++) {
				switch (bytes [i]) {
					case TAB:
						counters [tabIndex] ++;
						break;
					case SPACE:
						counters [spaceIndex] ++;
						break;
					case COMMA:
						counters [commaIndex] ++;
						break;
				}
			}

			// If first row, just init the counts...
			if (currentRow == 0) {
				commaCount = counters [commaIndex] == 0 ? -1 : counters[commaIndex];
				spaceCount = counters [spaceIndex] == 0 ? -1 : counters[spaceIndex];
				tabCount = counters [tabIndex] == 0 ? -1 : counters[tabIndex];
			} else {

				// Check that the counts remain the same.
				if (counters [commaIndex] != commaCount)
					isComma = false;
				if (counters [spaceIndex] != spaceCount)
					isSpace = false;
				if (counters [tabIndex] != tabCount)
					isTab = false;
			}
		}

		// Did one of the possible delimiters come up a winner?
		if (isComma && !isSpace && !isTab)
			return COMMA;
		if (!isComma && isSpace && !isTab)
			return SPACE;
		if (!isComma && !isSpace && isTab)
			return TAB;

		return EQUALS;
	}

	/**
		Break a line from the file up into a list of tokens by searching
		for the single byte value that delimits the fields.
		@param row the line from the file
		@return an ArrayList containing the tokens from the line.
	*/
	protected void createSDRow (String row, MutableTable vt, int curRow) {
		int current = 0;
		char [] bytes = row.toCharArray ();
		char del = delimiterOne;
		int len = bytes.length;

		int curCol = 0;

		for (int i = 0 ; i < len ; i++) {
			if (bytes[i] == del) {
				if ((i-current) > 0) {
					char [] newBytes = new char [i-current];
					System.arraycopy (bytes, current, newBytes, 0, i-current);
					vt.setChars(newBytes, curRow, curCol);
					curCol++;
				} else {
					vt.setChars(new char[0], curRow, curCol);
					curCol++;
				}
				current = i+1;
			}
		}

		if ((len-current) > 0) {
			char [] newBytes = new char [len-current];
			System.arraycopy (bytes, current, newBytes, 0, len-current);
			vt.setChars(newBytes, curRow, curCol);
			curCol++;
		}

		// fill in blank entries at the end..
		for(int i = curCol; i <= vt.getNumColumns()-1; i++) {
			vt.setChars(new char[0], curRow, i);
		}
	}

	protected ArrayList createSDRow (String row) {
		int current = 0;
		ArrayList thisRow = new ArrayList();
		char [] bytes = row.toCharArray();
		char del = delimiterOne;
		int len = bytes.length;

		for (int i = 0 ; i < len ; i++) {
			if (bytes[i] == del) {
				if ((i-current) > 0) {
					char [] newBytes = new char [i-current];
					System.arraycopy (bytes, current, newBytes, 0, i-current);
					thisRow.add(newBytes);
				} else {
					thisRow.add (new char [0]);
				}
				current = i+1;
			}
		}

		if ((len-current) > 0) {
			char [] newBytes = new char [len-current];
			System.arraycopy (bytes, current, newBytes, 0, len-current);
			thisRow.add(newBytes);
		}
		return thisRow;
	}

	/**
		Count the number of tokens in a row.
	*/
	protected int countSDRow (String row) {
		int current = 0;

		char [] bytes = row.toCharArray ();
		char del = delimiterOne;
		int len = bytes.length;

		int numToks = 0;

		for (int i = 0 ; i < len ; i++) {
			if (bytes[i] == del) {
				current = i+1;
				numToks++;
			}
		}

		if ((len-current) > 0) {
			numToks++;
		}
		//return thisRow;
		return numToks;
	}
}