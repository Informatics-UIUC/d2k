package ncsa.d2k.modules.core.io.file.input;


import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Read in a file with a single delimiter and use a random sample of the data.
 */
public class ReadDelimitedFormatWithSampling extends ReadDelimitedFormat {

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Loads data that is in a delimited format from the input filename into a     Table and outputs this Table. This is optimized for a file with a single     character delimiter. This module has several properties. labelsRow     indicates which row specifies attribute labels, if none specify '-l'.     typesRow indicates which row specifies data types, if none specify '-1'.     inOutRow indicates which row specifies the attributes to use as input and     output, if none specify '-1'. useStringAndDouble indicates whether or not     the system needs to determine data types. missingNumericFillerValue is the     value put into the table when a missing value is encountered in a numeric     column. missingTextualFillerValue is the value put into the table when a     missing value is encountered in a textual column. useCompactStrings will     use the most compact representation of strings in memory when true, and     will use a less efficient memory scheme when false. Allows a sample of the     file to be read in. The value of N determines the number of rows in the     table. If the useFirst property is set, only the first N rows will be in     the table. Otherwise N random rows will be put into the table.  </body></html>";
	}

	/** the number of rows to sample */
	private int N;
	/** true if the first N rows should be the sample, false if the sample
		should be random rows from the table */
	private boolean useFirst;
	/** the seed for the random number generator */
	private int seed;

	public void setUseFirst(boolean b) {
		useFirst = b;
	}

	public boolean getUseFirst() {
		return useFirst;
	}

	public void setN(int i) {
		N = i;
	}

	public int getN() {
		return N;
	}

	public void setSeed(int i) {
		seed = i;
	}

	public int getSeed() {
		return seed;
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

			boolean [] rowMap = new boolean[numLines];

			if(useFirst) {
				if(N > numRows)
					N = numRows;
				int i = 0;
				int row = 0;
				while (row <= N) {
					if(row != typesRow && row != labelsRow && row != inOutRow) {
						rowMap[row] = true;
					}
					row++;
					i++;
				}
			}
			else {
				Random r = new Random(seed);
				int i = 0;
				while(i < N) {
					int row = r.nextInt() % numRows;
					if(row != typesRow && row != labelsRow && row != inOutRow){
						rowMap[Math.abs(row)] = true;
						i++;
					}
				}
			}

			// now create the table.
			Column[] cols = new Column[numCols];
			for(int i = 0; i < cols.length; i++) {
				if(typesList != null) {
					String type = new String((char[])typesList.get(i));
					cols[i] = createColumn(type, N);
				}
				else
					cols[i] = new StringColumn(N);

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

			int numRowsRead = 0;

			// the number of the row in the table
			int rowNum = 0;
			// the number of the line in the actual file
			int lineNum = 0;
			reader = new BufferedReader(new FileReader(f));
			while( (line = reader.readLine()) != null) {
				if(lineNum != typesRow && lineNum != labelsRow && lineNum != inOutRow && rowMap[lineNum]) {
					createSDRow(line, table, rowNum);
					rowNum++;
					numRowsRead++;
				}
				lineNum++;
				if(numRowsRead == N)
					break;
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
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ReadDelimitedFormatWithSampling";
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
