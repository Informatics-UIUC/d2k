package ncsa.d2k.modules.core.io.file.input;

import java.util.*;
import java.io.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;


import ncsa.d2k.modules.core.io.file.*;

/**
	Works the same as ReadVT, but for an input file
	where each row represents a field and each column
	represents an example. It therefore transposes
	the table as it makes it into a VerticalTable.

	@author Peter Groves
*/

public class ReadTransposedVT extends ReadDelimitedFormat
			implements HasNames, Serializable {

	/**the column that contains the types of the VT columns*/
	int typesColumn=-1;

	/**the column that contains the labels of the VT columns*/
	int labelsColumn=-1;

	/** the delimiters to use for the StringTokenizer */
	protected String delimiter;

	/**
		Read a file and create a VerticalTable from the file.  Returns null
		if any errors occur.
		@param f the File to read
		@return a VerticalTable containing the data from the file, or null
		if any errors occur
	*/
	protected VerticalTable readSDFile(File f) {
		int maxInputRowLength = 0;
		int maxInputColumnLength =0;
		byte[] delbyt=new byte[1];
		delbyt[0]=delimiterOne;
		delimiter=new String(delbyt);
		System.out.println("Delimiter:"+delimiter);
		ArrayList rowPtrs = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));

			String line;

			// read the file in one row at a time
			while( (line = reader.readLine()) != null) {
				ArrayList thisRow = createRow(line);
				rowPtrs.add(thisRow);
				maxInputColumnLength++;
				if(thisRow.size() > maxInputRowLength)
					maxInputRowLength = thisRow.size();
			}

		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		//make the labels and types lists
		if(hasTypes){
			typesList=new ArrayList(maxInputColumnLength);
			for(int i=0; i<rowPtrs.size(); i++){
				typesList.add(((ArrayList)rowPtrs.get(i)).get(typesColumn));
			}
		}
		if(hasLabels){
			labelsList=new ArrayList(maxInputColumnLength);
			for(int i=0; i<rowPtrs.size(); i++){
				labelsList.add(((ArrayList)rowPtrs.get(i)).get(labelsColumn));
			}
		}


		// now create the columns
		SimpleColumn []tableColumns = new SimpleColumn[maxInputColumnLength];

		int vtColumnLength=maxInputRowLength;
		if(hasTypes)
			vtColumnLength--;
		if(hasLabels)
			vtColumnLength--;

		for(int i = 0; i < maxInputColumnLength; i++) {
			if(hasTypes)
				tableColumns[i] = createColumn(
					new String((byte[])typesList.get(i)),
					vtColumnLength);
			else
				tableColumns[i] = new ByteArrayColumn(vtColumnLength);
		}

		// now populate the columns
		for(int row = 0; row < maxInputColumnLength; row++) {
			ArrayList thisRow = (ArrayList)rowPtrs.get(row);
			int vtRowIndex=0;
			for(int col = 0; col < thisRow.size(); col++){
				if((col!=labelsColumn) && (col!=typesColumn)){
					tableColumns[row].setString(
						(new String((byte[])thisRow.get(col))), vtRowIndex);
					vtRowIndex++;
				}
			}

		}

		// change the columns to String and DoubleColumns if necessary
		if(useStringAndDouble && !hasTypes) {
			for(int col = 0; col < tableColumns.length; col++) {
				if(isNumericColumn( (ByteArrayColumn)tableColumns[col] ) )
					tableColumns[col] = toDoubleColumn(
						(ByteArrayColumn)tableColumns[col]);
				else
					tableColumns[col] = toStringColumn(
						(ByteArrayColumn)tableColumns[col]);
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
			for(int i = 0; i < labelsList.size(); i++)
				tableColumns[i].setLabel(Integer.toString(i));
		}

		return new VerticalTable(tableColumns);
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



	//////////////////////////////////////////////////////
	///these properties accesors need to not do anything
	//and tell the user as much in this subclass
	/////////////////////////////////////////////////


	/**
		Get the index of the types row.
		@return the index of the types row.
	*/
	public int getTypesRow() {
		return -1;
	}

	/**
		Set the index of the types row.
		@param i the new index
	*/
	public void setTypesRow(int i) {
		System.out.println("ReadTransposedVT: Types Row is ignored in this subclass");
	}

	/**
		Get the index of the labels row.
		@return the index of the labels row
	*/
	public int getLabelsRow() {
		return -1;
	}

	/**
		Set the index of the labels row
		@param i the new index
	*/
	public void setLabelsRow(int i) {
		System.out.println("ReadTransposedVT: Lables Row is ignored in this subclass");
	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "ReadTransposedVT";
	}


	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i) {
		if(i == 0)
			return "table";
		else
			return "No such output";
	}
	/**
		Break a line from the file up into a list of tokens.
		@param row the line from the file
		@return an ArrayList containing the tokens from the line.
	*/
	protected ArrayList createRow(String row) {
		ArrayList thisRow = new ArrayList();
		StringTokenizer tokenizer = getNewTokenizer(row);
		boolean lastDelimWasToken = false;
		while(tokenizer.hasMoreTokens()) {
			String tok = tokenizer.nextToken();
			if(isDelimiter(tok)) {
				// two delimiters in a row means that the space was empty.
				if(lastDelimWasToken)
					thisRow.add(new byte[0]);
				lastDelimWasToken = true;
			}
			else {
				thisRow.add(tok.getBytes());
				lastDelimWasToken = false;
			}
		}
		return thisRow;
	}

	/**
		Return a new StringTokenizer for a specific line.  Subclass
		StringTokenizer and this method to substitute a different
		tokenizer.
		@param line the line to tokenize
		@return a new StringTokenizer, initialized with this line and
		the delimiter property.
	*/
	protected StringTokenizer getNewTokenizer(String line) {
		return new StringTokenizer(line, delimiter, true);
	}

	protected boolean isDelimiter(String s) {
		int retVal = delimiter.indexOf(s);
		if(retVal == -1)
			return false;
		else
			return true;
	}

}


