package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.util.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Read in a file with a single delimiter.
*/
public class ReadDelimitedFormat extends InputModule
    implements Serializable, HasNames, HasProperties {

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

	/** the delimiter identified. */
	protected char delimiterOne;

	/** the datatype for each column. */
	transient protected ArrayList typesList;

	/** the labels for each column. */
	transient protected ArrayList labelsList;

	/** the list of variables */
	transient protected ArrayList variablesList;

	/** set if the types are specified in the file. */
	transient protected boolean hasTypes = false;

	/** set if the labels are specified in the file. */
	transient protected boolean hasLabels = false;

	/** set if the in/out variables are specified */
	transient protected boolean hasVariables = false;

	private String comment = "%";

	/** create DoubleColumns for numeric values, or StringColumns otherwise */
	protected boolean useStringAndDouble = false;

	/** the row containing the labels. */
	protected int labelsRow = 0;

	/** the row containing the column types. */
	protected int typesRow = 1;

	/** the row containing the in/out/omit declarations */
	protected int inOutRow = -1;

	/** the value to replace missing numerics values with. */
	protected double missingValue = 0;

	public double getMissingValue() {
		return missingValue;
	}

	/*public void setComment(String s) {
		comment = s;
	}

	public String getComment() {
		return comment;
	}*/

	public void setMissingValue(double d) {
		missingValue = d;
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
		Get the index of the variable row.
		@return the index of the types row.
	*/
	public int getInOutRow() {
		return inOutRow;
	}

	/**
		Set the index of the variable row.
		@param i the new index
	*/
	public void setInOutRow(int i) {
		inOutRow = i;
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
		String str = "Loads data that is in a delimited format ";
        str += "from the input filename into a Table and outputs this Table. ";
        str += "This is optimized for a file with a single character delimiter. ";
        str += "This module has several properties. labelsRow indicates which ";
        str += "row specifies attribute labels, if none specify '-l'. ";
        str += "typesRow indicates which row specifies data types, if none ";
        str += "specify '-1'.  inoutRow indicates which row specifies the ";
        str += "attributes to use as input and output, if none specify '-1'. ";
        str += "useStringAndDouble indicates whether or not the system needs ";
        str += "to determine data types. missingValue is the value to use for ";
        str += "missing numeric values.";
        return str;
	}

    public String getModuleName() {
        return "ReadDelimitedFormat";
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
		String []out = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
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

    public String getInputName(int i) {
        return "FileName";
    }

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		if(i == 0)
			return "The Table";
		else
			return "No such output";
	}

    public String getOutputName(int i) {
        return "Table";
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
		variablesList = null;
		hasVariables = false;
	}

	/**
		Create the table.
	*/
	public void doit() throws Exception {
		// get our delimiter set
		String fileName = (String)pullInput(0);
		File file = new File (fileName);
		delimiterOne = this.findDelimiter (file);
		if (delimiterOne == EQUALS) {
			throw new Exception ("No single character delimiter could be identified.");
		}

		if(typesRow >= 0)
			hasTypes = true;
		if(labelsRow >= 0)
			hasLabels = true;
		if(inOutRow >= 0)
			hasVariables = true;

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

	private static final char TAB = '\t';
	private static final char SPACE = ' ';
	private static final char COMMA = ',';
	private static final char EQUALS = '=';

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
	private char findDelimiter (File f) {
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
		Read a file and create a Table from the file.  Returns null
		if any errors occur.
		@param f the File to read
		@return a Table containing the data from the file, or null
		if any errors occur
	*/
	protected Table readSDFile(File f) {
		int numLines = 0;
		int numCols = 0;
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(f));

			// get the number of columns from the first line
			String line = reader.readLine();
			numCols = countSDRow(line);

			typesList = null;
			labelsList = null;
			variablesList = null;

			reader = new BufferedReader(new FileReader(f));
			while( (line = reader.readLine()) != null) {
				if(numLines == typesRow)
					typesList = createSDRow(line);
				else if(numLines == labelsRow)
					labelsList = createSDRow(line);
				else if(numLines == inOutRow)
					variablesList = createSDRow(line);
				numLines++;
			}
			int numRows = numLines;

			if(hasTypes)
				numRows--;
			if(hasLabels)
				numRows--;
			if(hasVariables)
				numRows--;

			// now create the table.
			Column[] cols = new Column[numCols];
			for(int i = 0; i < cols.length; i++) {
				if(typesList != null) {
					String type = new String((char[])typesList.get(i));
					cols[i] = createColumn(type, numRows);
				}
				else
					cols[i] = new StringColumn(numRows);

				if(labelsList != null)
					cols[i].setLabel(new String((char[])labelsList.get(i)));
				else
					cols[i].setLabel(Integer.toString(i));
			}
			if(typesList != null)
				typesList.clear();
			if(labelsList != null)
				labelsList.clear();

			TableImpl table = (TableImpl)DefaultTableFactory.getInstance().createTable(cols);

			// the number of the row in the table
			int rowNum = 0;
			// the number of the line in the actual file
			int lineNum = 0;
			reader = new BufferedReader(new FileReader(f));
			while( (line = reader.readLine()) != null) {
				if(lineNum != typesRow && lineNum != labelsRow && lineNum != inOutRow) {
					createSDRow(line, table, rowNum);
					rowNum++;
				}
				lineNum++;
			}

			// change the columns to String and DoubleColumns if necessary
			if(useStringAndDouble && !hasTypes) {
				for(int col = 0; col < table.getNumColumns(); col++) {
					if(isNumericColumn( (StringColumn)table.getColumn(col)) ) {
						table.setColumn(toDoubleColumn(
							(StringColumn)table.getColumn(col)), col);
					}
				}
			}

			// trim all textual columns down
			for(int i = 0; i < table.getNumColumns(); i++) {
				if(table.getColumn(i) instanceof TextualColumn)
					((TextualColumn)table.getColumn(i)).trim();
			}

			// create an example table if it has a variablesRow
			if(hasVariables)
				table = toExampleTable(table);

			return table;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the input and output features of an example table.
	 * @param t
	 * @return
	 */
	protected ExampleTableImpl toExampleTable(TableImpl t) {
		ExampleTableImpl retVal = (ExampleTableImpl)t.toExampleTable();
		int [] ins;
		int [] outs;

		int numIn = 0;
		int numOut = 0;

		// count the number of ins and outs
		Iterator i = variablesList.iterator();
		while(i.hasNext()) {
			char[] c = (char[])i.next();
			String s = new String(c);
			if(s.compareToIgnoreCase(IN) == 0)
				numIn++;
			else if(s.compareToIgnoreCase(OUT) == 0)
				numOut++;
		}


		ins = new int[numIn];
		outs = new int[numOut];

		int inIdx = 0;
		int outIdx = 0;

		int colIdx = 0;

		i = variablesList.iterator();
		while(i.hasNext()) {
			char[] c = (char[])i.next();
			String s = new String(c);
			if(s.compareToIgnoreCase(IN) == 0) {
				ins[inIdx] = colIdx;
				inIdx++;
			}
			else if(s.compareToIgnoreCase(OUT) == 0) {
				outs[outIdx] = colIdx;
				outIdx++;
			}
			colIdx++;
		}
		retVal.setInputFeatures(ins);
		retVal.setOutputFeatures(outs);

		return retVal;
	}

	/**
		Break a line from the file up into a list of tokens by searching
		for the single byte value that delimits the fields.
		@param row the line from the file
		@return an ArrayList containing the tokens from the line.
	*/
	protected void createSDRow (String row, TableImpl vt, int curRow) {
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

	public static void main(String []args) {
		new ReadDelimitedFormat(args[0]);
	}

	public ReadDelimitedFormat() {
	}

	public ReadDelimitedFormat(String fn) {
		File f = new File(fn);
		delimiterOne = this.findDelimiter (f);
		hasTypes = true;
		typesRow = 1;
		hasLabels = true;
		labelsRow = 0;
		if(f.exists()) {
			Table vtable = readSDFile(f);
			//vtable.print();
		}
	}
}
