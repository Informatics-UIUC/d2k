package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.util.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/**
 * Read in a file with a single delimiter.
*/
public class ReadDelimitedFormatWithSampling extends ReadDelimitedFormat {

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		String s = "Optimized for a file with a single delimiter.";
		s += " Allows a sample of the file to be read in.  The ";
		s += "value of N determines the number of rows in the table. ";
		s += "If the useFirst property is set, only the first N rows ";
		s += "will be in the table.  Otherwise N random rows will be ";
		s += "put into the table.";
		return s;
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
		Read a file and create a VerticalTable from the file.  Returns null
		if any errors occur.
		@param f the File to read
		@return a VerticalTable containing the data from the file, or null
		if any errors occur
	*/
	protected VerticalTable readSDFile(File f) {
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
					cols[i] = createColumn(type, /*numRows*/N);
				}
				else
					cols[i] = new StringColumn(/*numRows*/N);

				if(labelsList != null)
					cols[i].setLabel(new String((char[])labelsList.get(i)));
				else
					cols[i].setLabel(Integer.toString(i));
			}
			if(typesList != null)
				typesList.clear();
			if(labelsList != null)
				labelsList.clear();

			VerticalTable table = new VerticalTable(cols);

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
}
