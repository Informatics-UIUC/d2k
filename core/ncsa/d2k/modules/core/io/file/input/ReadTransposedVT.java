package ncsa.d2k.modules.core.io.file.input;


import java.util.*;
import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	Works the same as ReadVT, but for an input file
	where each row represents a field and each column
	represents an example. It therefore transposes
	the table as it makes it into a Table.

	@author Peter Groves
*/
public class ReadTransposedVT extends ReadDelimitedFormat
			 {

	/**the column that contains the types of the VT columns*/
	int typesColumn= 1;

	/**the column that contains the labels of the VT columns*/
	int labelsColumn= 0;

    /** the column that contains the variable types */
	int variableColumn = -1;

	/**
		Read a file and create a Table from the file.  Returns null
		if any errors occur.
		@param f the File to read
		@return a Table containing the data from the file, or null
		if any errors occur
	*/
	protected Table readSDFile(File f) {
		int numLines = 0;
		int numRows = 0;
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(f));

			// get the number of rows from the first line
			String line = reader.readLine();
			numRows = countSDRow(line);

			typesList = new ArrayList();
			labelsList = new ArrayList();
			variablesList = new ArrayList();

            // now get the types, labels, and variables and count
            // the number of lines in the file
			reader = new BufferedReader(new FileReader(f));
			while( (line = reader.readLine()) != null) {
				if(hasTypes) {
					// grab the type from the line
					typesList.add(getIthElement(line, typesColumn));
				}
				if(hasLabels) {
					// grab the label from the line
					labelsList.add(getIthElement(line, labelsColumn));
				}
				if(hasVariables) {
					// grab the variable from the line
					variablesList.add(getIthElement(line, variableColumn));
				}
				numLines++;
			}
			int numCols = numLines;

			if(hasTypes)
				numRows--;
			if(hasLabels)
				numRows--;
			if(hasVariables)
				numRows--;

			// now create the table.
			Column[] cols = new Column[numCols];
			for(int i = 0; i < cols.length; i++) {
				if(hasTypes)
					cols[i] = createColumn(new String((char[])typesList.get(i)), numRows);
				else
					cols[i] = new StringColumn(numRows);

				if(hasLabels)
					cols[i].setLabel(new String((char[])labelsList.get(i)));
				else
					cols[i].setLabel(Integer.toString(i));
			}
			if(typesList != null)
				typesList.clear();
			if(labelsList != null)
				labelsList.clear();

			MutableTableImpl table = (MutableTableImpl)DefaultTableFactory.getInstance().createTable(cols);

			// the number of the row in the table
			int rowNum = 0;
			reader = new BufferedReader(new FileReader(f));
			while( (line = reader.readLine()) != null) {
					createSDRow(line, table, rowNum);
                    rowNum++;
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
				return toExampleTable(table);

			return table;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
		Break a line from the file up into a list of tokens by searching
		for the single byte value that delimits the fields.
		@param row the line from the file
	*/
	protected void createSDRow (String row, MutableTable vt, int curRow) {
        // the current column of the table to insert into
		int currentCol = curRow;

		int current = 0;
		char [] bytes = row.toCharArray ();
		char del = delimiterOne;
		int len = bytes.length;

        // the current row of the table to insert to
		int currentRow = 0;
        // the actual spot in the file we are in, this takes
        // types, labels, and variables into account.
		int curLoc = 0;

        // loop through the line
		for (int i = 0 ; i < len ; i++) {
			if (bytes[i] == del) {
				if ((i-current) > 0) {
					char [] newBytes = new char [i-current];
					System.arraycopy (bytes, current, newBytes, 0, i-current);
                    // if it is not a type, variable, or label column, insert
					if(curLoc != typesColumn && curLoc != labelsColumn && curLoc != variableColumn) {
						vt.setChars(newBytes, currentRow, currentCol);
						currentRow++;
					}
					curLoc++;
				} else {
                    // if it is not a type, variable, or label column, insert
					if(curLoc != typesColumn && curLoc != labelsColumn && curLoc != variableColumn) {
						vt.setChars(new char[0], currentRow, currentCol);
						currentRow++;
					}
					curLoc++;
				}
				current = i+1;
			}
		}

		if ((len-current) > 0) {
			char [] newBytes = new char [len-current];
			System.arraycopy (bytes, current, newBytes, 0, len-current);
            // if it is not a type, variable, or label column, insert
			if(curLoc != typesColumn && curLoc != labelsColumn && curLoc != variableColumn)
				vt.setChars(newBytes, currentRow, currentCol);
			currentRow++;
		}
	}

	///////////////////////
	///d2k Props accesors
	///////////////////////

	/**
		Get the index of the types column.
		@return the index of the types column.
	*/
	public int getTypesColumn() {
		return typesColumn;
	}

	/**
		Set the index of the types column.
		@param i the new index
	*/
	public void setTypesColumn(int i) {
		typesColumn = i;
		//we need to fool the superclasses doit so
		//that it knows we have types
		typesRow=i;
	}

	/**
		Get the index of the labels column.
		@return the index of the labels column
	*/
	public int getLabelsColumn() {
		return labelsColumn;
	}

	/**
		Set the index of the labels column
		@param i the new index
	*/
	public void setLabelsColumn(int i) {
		labelsColumn = i;
		//we need to fool the superclasses doit so
		//that it knows we have labels
		labelsRow=i;
	}

	/**
		Get the index of the variables column.
		@return the index of the labels column
	*/
	public int getVariableColumn() {
		return variableColumn;
	}

	/**
		Set the index of the variables column
		@param i the new index
	*/
	public void setVariableColumn(int i) {
		variableColumn = i;
		//we need to fool the superclasses doit so
		//that it knows we have labels
		inOutRow=i;
	}

	/**
		Set the index of the types row.
		@param i the new index
	*/
	public void setTypesRow(int i) {
		//typesRow = -1;
	}

	/**
		Set the index of the labels row
		@param i the new index
	*/
	public void setLabelsRow(int i) {
		//labelsRow = -1;
	}

	/**
		Set the index of the variable row.
		@param i the new index
	*/
	public void setVariableRow(int i) {
		//variableRow = -1;
	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "ReadTransposedVT";
	}

    /**
     * Get the ith item in a row.
     * @param row the line to parse
     * @param num the index of the element we want to get
     * @return the ith element of row
     */
	protected char[] getIthElement(String row, int num) {
		int current = 0;
		int last = 0;

		char [] bytes = row.toCharArray ();
		char del = delimiterOne;
		int len = bytes.length;

		int numToks = 0;

		for (int i = 0 ; i < len ; i++) {
			if (bytes[i] == del) {
				last = current;
				current = i+1;

				if(numToks == num) {
					char[] retVal = new char[i-last];
					System.arraycopy(bytes, last, retVal, 0, i-last);
					return retVal;
				}
				numToks++;
			}
		}

		if ((len-current) > 0) {
			numToks++;
		}
		return null;
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
