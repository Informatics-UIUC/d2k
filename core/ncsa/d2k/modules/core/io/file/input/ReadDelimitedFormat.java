package ncsa.d2k.modules.core.io.file.input;

import java.util.*;
import java.io.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
	A subclass of ReadVT that is optimized for files using a single delimiter.
	StringTokenizer is not used when reading the file.
	@author David Clutter
	@author Tom Redman
*/
public class ReadDelimitedFormat extends InputModule implements Serializable {

	static final String STRING_TYPE = "String";
	static final String FLOAT_TYPE = "float";
	static final String DOUBLE_TYPE = "double";
	static final String INT_TYPE = "int";
	static final String BOOLEAN_TYPE = "boolean";
	static final String CHAR_TYPE = "char[]";
	static final String BYTE_TYPE = "byte[]";
	static final String LONG_TYPE = "long";
	static final String SHORT_TYPE = "short";

	/** the delimiter identified. */
	private byte delimiterOne;

	/** the datatype for each column. */
	transient protected ArrayList typesList;

	/** the labels for each column. */
	transient protected ArrayList labelsList;

	/** set if the types are specified in the file. */
	transient protected boolean hasTypes = false;

	/** set if the labels are specified in the file. */
	transient protected boolean hasLabels = false;

	/** create DoubleColumns for numeric values, or StringColumns otherwise */
	public boolean useStringAndDouble;

	/** the row containing the labels. */
	public int labelsRow = -1;

	/** the row containing the column types. */
	public int typesRow = -1;

	/** the value to replace missing numerics values with. */
	double emptyValue = 0;

	public double getEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(double d) {
		emptyValue = d;
	}
	/**
		Get the index of the types row.
		@return the index of the types row.
	*/
	public int getTypesRow() {
		return typesRow;
	}

	/**
		Set the index of the types row.
		@param i the new index
	*/
	public void setTypesRow(int i) {
		typesRow = i;
	}

	/**
		Get the index of the labels row.
		@return the index of the labels row
	*/
	public int getLabelsRow() {
		return labelsRow;
	}

	/**
		Set the index of the labels row
		@param i the new index
	*/
	public void setLabelsRow(int i) {
		labelsRow = i;
	}

	/**
		Get the value of useStringAndDouble
		@return true if String and DoubleColumns should be used,
		false otherwise
	*/
	public boolean getUseStringAndDouble() {
		return useStringAndDouble;
	}

	/**
		Set the useStringAndDouble property
		@param b true if String and DoubleColumns should be used,
		false otherwise
	*/
	public void setUseStringAndDouble(boolean b) {
		useStringAndDouble = b;
	}

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		return "Optimized for a file with a single delimiter";
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes() {
		String []in = {"java.lang.String"};
		return in;
	}

   /**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.util.datatype.VerticalTable"};
		return out;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i) {
		if(i == 0)
			return "The name of the file to read.";
		else
			return "No such input";
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		if(i == 0)
			return "The VerticalTable";
		else
			return "No such output";
	}

	/**
		Called when the itinerary begins execution.  Initialize
		variables here.
	*/
	public void beginExecution() {
		hasTypes = false;
		hasLabels = false;
		typesList = null;
		labelsList = null;
	}

	/**
		Create the table.
	*/
	public void doit() throws Exception {
		// get our delimiter set
		String fileName = (String)pullInput(0);
		File file = new File (fileName);
		delimiterOne = this.findDelimiter (file);
		if (delimiterOne == (byte) '=') {
			throw new Exception ("No single character delimiter could be identified.");
		}

		if(typesRow >= 0)
			hasTypes = true;
		if(labelsRow >= 0)
			hasLabels = true;

		if(file.exists())
			pushOutput(readSDFile(file), 0);
		else
			System.out.println("File did not exist.");
	}


	/**
		Loop through the items in column, if they can all be represented
		numerically, return true.  Otherwise return false.
		@param column the column to test
		@return true if column contains only numeric data, false otherwise
	*/
	protected boolean isNumericColumn(SimpleColumn column) {
		for(int row = 0; row < column.getNumRows(); row++) {
			try {
				Double d = Double.valueOf(new String( column.getBytes(row) ));
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
		Create a column given the type and size.
		@param type the type of column to create
		@param size the initial size of the column
		@return a new, empty column
	*/
	protected SimpleColumn createColumn(String type, int size) {
		if(type.equals(STRING_TYPE))
			return new StringColumn(size);
		else if(type.equals(FLOAT_TYPE))
			return new FloatColumn(size);
		else if(type.equals(DOUBLE_TYPE))
			return new DoubleColumn(size);
		else if(type.equals(INT_TYPE))
			return new IntColumn(size);
		else if(type.equals(BOOLEAN_TYPE))
			return new BooleanColumn(size);
		else if(type.equals(CHAR_TYPE))
			return new CharArrayColumn(size);
		else if(type.equals(BYTE_TYPE))
			return new ByteArrayColumn(size);
		else if(type.equals(LONG_TYPE))
			return new LongColumn(size);
		else if(type.equals(SHORT_TYPE))
			return new ShortColumn(size);
		else
			return new ByteArrayColumn(size);
	}

	/**
		Create a DoubleColumn from a ByteArrayColumn
		@param sc the original column
		@return a DoubleColumn with the values from sc
	*/
	protected DoubleColumn toDoubleColumn(ByteArrayColumn sc) {
		DoubleColumn retVal = new DoubleColumn(sc.getNumRows());
		int numRows = sc.getNumRows ();
		for(int row = 0; row < numRows; row++)
			retVal.setDouble( Double.valueOf(
				sc.getString(row)).doubleValue(), row);
		return retVal;
	}

	/**
		Create a StringColumn from a ByteArrayColumn
		@param sc the original column
		@return a StringColumn with the values from sc
	*/
	protected StringColumn toStringColumn(ByteArrayColumn sc) {
		StringColumn retVal = new StringColumn(sc.getNumRows());
		int numRows = sc.getNumRows ();
		for(int row = 0; row < numRows; row++) {
			byte []val = sc.getBytes(row);
			if(val == null)
				val = new byte[0];
			retVal.setString(new String(val), row);
		}
		return retVal;
	}

	/////// Private methods //////////////

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
	private byte findDelimiter (File f) {
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
				byte [] bytes = line [currentRow].getBytes ();

				// In this row, count instances of each delimiter
				for (int i = 0 ; i < bytes.length ; i++) {
					switch (bytes [i]) {
						case '\t':
							counters [tabIndex] ++;
							break;
						case ' ':
							counters [spaceIndex] ++;
							break;
						case ',':
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
			return (byte) '=';
		}

		// Did one of the possible delimiters come up a winner?
		if (isComma && !isSpace && !isTab)
			return (byte) ',';
		if (!isComma && isSpace && !isTab)
			return (byte) ' ';
		if (!isComma && !isSpace && isTab)
			return (byte) '\t';

		// OK, that didn't work. Lets trim the strings and see if it will work the.
		// read the file in one row at a time
		isComma = true;
		isSpace = true;
		isTab = true;
		for (int currentRow = 0; currentRow < 10 ;currentRow++) {
			String tmp = line [currentRow].trim ();
			byte [] bytes = tmp.getBytes ();
			counters [tabIndex] = counters [spaceIndex] = counters [commaIndex] = 0;
			// In this row, count instances of each delimiter
			for (int i = 0 ; i < bytes.length ; i++) {
				switch (bytes [i]) {
					case '\t':
						counters [tabIndex] ++;
						break;
					case ' ':
						counters [spaceIndex] ++;
						break;
					case ',':
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
			return (byte) ',';
		if (!isComma && isSpace && !isTab)
			return (byte) ' ';
		if (!isComma && !isSpace && isTab)
			return (byte) '\t';

		return (byte) '=';
	}

	/** this method takes the data and creates from it a vertical table.
	 *  @param rowPtrs the list of byte arrays containing the original data.
	 *  @param maxRowLength the maximum number of entries found in any row.
	 *  @returns a vertical table representing the data.
	 */
	protected VerticalTable createVerticalTable (ArrayList rowPtrs, int maxRowLength) {
		// now create the columns
		SimpleColumn []tableColumns = new SimpleColumn[maxRowLength];
		for(int i = 0; i < maxRowLength; i++) {
			if(hasTypes) {
				tableColumns[i] = createColumn(
					new String((byte[])typesList.get(i)),
					rowPtrs.size());
			} else
				tableColumns[i] = new ByteArrayColumn(rowPtrs.size());
		}

		// now populate the columns
		for(int row = 0; row < rowPtrs.size(); row++) {
			ArrayList thisRow = (ArrayList)rowPtrs.get(row);
			for(int col = 0; col < thisRow.size(); col++) {
				/*if(tableColumns[col] instanceof NumericColumn) {
					String s = new String((byte[])thisRow.get(col));
					double d;
					try {
						d = Double.parseDouble(s);
					}
					catch(Exception e) {
						d = emptyValue;
					}
	    			tableColumns[col].setString(Double.toString(d),row);
				*/
				if(tableColumns[col] instanceof NumericColumn) {
					String s = new String((byte[])thisRow.get(col));
					if(s.trim().length() == 0) {
						if(tableColumns[col] instanceof IntColumn)
	    						tableColumns[col].setInt(
								(int)emptyValue,row);
						else if(tableColumns[col] instanceof ShortColumn)
	    						tableColumns[col].setShort(
								(short)emptyValue,row);
						else if(tableColumns[col] instanceof LongColumn)
							tableColumns[col].setLong(
								(long)emptyValue, row);
						else if(tableColumns[col] instanceof DoubleColumn)
							tableColumns[col].setDouble(
								emptyValue, row);
						else
							tableColumns[col].setFloat(
								(float)emptyValue, row);
					}
					else
						tableColumns[col].setString(
							new String((byte[])thisRow.get(col)), row);
				} else
					tableColumns[col].setBytes((byte[])thisRow.get(col), row);
			}
		}

		// change the columns to String and DoubleColumns if necessary
		if(useStringAndDouble && !hasTypes) {
			for(int col = 0; col < tableColumns.length; col++) {
				if(isNumericColumn( (ByteArrayColumn)tableColumns[col] ) ) {
					tableColumns[col] = toDoubleColumn(
						(ByteArrayColumn)tableColumns[col]);
				} else {
					tableColumns[col] = toStringColumn(
						(ByteArrayColumn)tableColumns[col]);
				}
			}
		}

		// set the labels if given
		if(hasLabels) {
			for(int i = 0; i < labelsList.size(); i++)
				tableColumns[i].setLabel(
					new String( (byte[])labelsList.get(i)) );
		}

		// otherwise make the labels be the index of the column
		else {
			for(int i = 0; i < tableColumns.length; i++)
				tableColumns[i].setLabel(Integer.toString(i));
		}

		return new VerticalTable(tableColumns);
	}

	/**
		Read a file and create a VerticalTable from the file.  Returns null
		if any errors occur.
		@param f the File to read
		@return a VerticalTable containing the data from the file, or null
		if any errors occur
	*/
	private VerticalTable readSDFile(File f) {
		int maxRowLength = 0;
		ArrayList rowPtrs = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;

			// read the file in one row at a time
			int currentRow = 0;
			while( (line = reader.readLine()) != null) {
				ArrayList thisRow = createSDRow(line);
				if(currentRow == typesRow) {
					typesList = thisRow;
				} else if(currentRow == labelsRow) {
					labelsList = thisRow;
				} else
					rowPtrs.add(thisRow);

				currentRow++;
				if(thisRow.size() > maxRowLength)
					maxRowLength = thisRow.size();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		return this.createVerticalTable (rowPtrs, maxRowLength);
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i) {
		if(i == 0)
			return "filename";
		else
			return "No such input";
	}

	/**
		Break a line from the file up into a list of tokens by searching
		for the single byte value that delimits the fields.
		@param row the line from the file
		@return an ArrayList containing the tokens from the line.
	*/
	private ArrayList createSDRow (String row) {
		int current = 0;
		ArrayList thisRow = new ArrayList();
		byte [] bytes = row.getBytes ();
		byte del = delimiterOne;
		int len = bytes.length;
		for (int i = 0 ; i < len ; i++)
		{
			if (bytes[i] == del)
			{
				if ((i-current) > 0) {
					byte [] newBytes = new byte [i-current];
					System.arraycopy (bytes, current, newBytes, 0, i-current);
					thisRow.add(newBytes);
				} else
					thisRow.add (new byte [0]);
				current = i+1;
			}
		}

		if ((len-current) > 0) {
			byte [] newBytes = new byte [len-current];
			System.arraycopy (bytes, current, newBytes, 0, len-current);
			thisRow.add(newBytes);
		}
		return thisRow;
	}
}