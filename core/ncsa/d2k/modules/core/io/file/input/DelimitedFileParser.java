package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Reads data from a delimited file.  The delimiter is found automatically, or
 * can be set.
 */
public class DelimitedFileParser implements FlatFileParser {

    /** Possible delimiters */
    private static final char TAB = '\t';
    private static final char SPACE = ' ';
    protected static final char COMMA = ',';
    private static final char EQUALS = '=';

    /** the file to read from */
    protected File file;
    /** the index, from 0, of the types row */
    private int typesRow;
    /** the index, from 0, of the labels row */
    private int labelsRow;
    /** the index, from 0, of the in out row */
    private int inOutRow;
    /** the index, from 0, of the nominal/scalar row */
    private int nomScalarRow;

    /** the number of data rows in the file (does not include meta rows) */
    protected int numRows;
    /** the number of columns in the file */
    protected int numColumns;

    /** the types of the columns */
    protected int[] columnTypes;
    /** the labels of the columns */
    protected String[] columnLabels;
    /** the data (in/out) types of the columns */
    private int[] dataTypes;
    /** the feature (nom/scalar) types of the columns */
    private int[] featureTypes;

    /** the file reader */
    protected LineNumberReader lineReader;

    /** the delimter for this file */
    protected char delimiter;

    //protected ArrayList _blankRows;
    //protected ArrayList _blankColumns;
    protected boolean[][] blanks;

    protected DelimitedFileParser() {}

    /**
     * Create a new DelimitedFileReader with no types row, no labels row,
     * no in out row, no nominal scalar row.
     * @param f the file to read
     */
    public DelimitedFileParser(File f) throws Exception {
        this(f, -1, -1, -1, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels row.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     */
    public DelimitedFileParser(File f, int _labelsRow) throws Exception {
        this(f, _labelsRow, -1, -1, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels and types rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow) throws Exception {
        this(f, _labelsRow, _typesRow, -1, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels, types, and
     * in-out rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     * @param _inOutRow the index of the in-out row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow, int _inOutRow)
            throws Exception {
        this(f, _labelsRow, _typesRow, _inOutRow, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels, types, inout,
     * and nominal/scalar rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     * @param _inOutRow the index of the in-out row
     * @param _nomScalarRow the index of the nominal-scalar row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow, int _inOutRow,
                               int _nomScalarRow) throws Exception {
        file = f;
        typesRow = _typesRow;
        labelsRow = _labelsRow;
        inOutRow = _inOutRow;
        nomScalarRow = _nomScalarRow;
        //_blankRows = new ArrayList();
        //_blankColumns = new ArrayList();

        // read through the file to count the number of rows, columns, and find
        // the delimiter
        scanFile();

        lineReader = new LineNumberReader(new FileReader(file));

        // now read in the types, scalar, in out rows, labels
        if(typesRow > -1) {
            numRows--;

            // now parse the line and get the types
            ArrayList row = getLineElements(typesRow);
            createColumnTypes(row);
        }
        else
            columnTypes = null;
        if(labelsRow > -1) {
            numRows--;

            // now parse the line and the the labels
            ArrayList row = getLineElements(labelsRow);
            createColumnLabels(row);
        }
        else
            columnLabels = null;
        /*if(inOutRow > -1) {
            numRows--;

            // now parse the line and get the in-out features
            ArrayList row = getLineElements(inOutRow);
            createDataTypes(row);
        }
        else
            dataTypes = null;
        if(nomScalarRow > -1) {
            numRows--;

            // now parse the line and get the nom-scalar features
            ArrayList row = getLineElements(nomScalarRow);
        }
        else
            featureTypes = null;
        */
        lineReader.setLineNumber(0);
    }

    /**
     * Create the columns types.
     */
    private void createColumnTypes(ArrayList row) {
        columnTypes = new int[row.size()];

        for(int i = 0; i < row.size(); i++) {
            char[] ty = (char[])row.get(i);
            String type = new String(ty).trim();
            if(type.equalsIgnoreCase(STRING_TYPE))
                columnTypes[i] = ColumnTypes.STRING;
            else if(type.equalsIgnoreCase(DOUBLE_TYPE))
                columnTypes[i] = ColumnTypes.DOUBLE;
            else if(type.equalsIgnoreCase(INT_TYPE))
                columnTypes[i] = ColumnTypes.INTEGER;
            else if(type.equalsIgnoreCase(FLOAT_TYPE))
                columnTypes[i] = ColumnTypes.FLOAT;
            else if(type.equalsIgnoreCase(SHORT_TYPE))
                columnTypes[i] = ColumnTypes.SHORT;
            else if(type.equalsIgnoreCase(LONG_TYPE))
                columnTypes[i] = ColumnTypes.LONG;
            else if(type.equalsIgnoreCase(BYTE_TYPE))
                columnTypes[i] = ColumnTypes.BYTE;
            else if(type.equalsIgnoreCase(CHAR_TYPE))
                columnTypes[i] = ColumnTypes.CHAR;
            else if(type.equalsIgnoreCase(BYTE_ARRAY_TYPE))
                columnTypes[i] = ColumnTypes.BYTE_ARRAY;
            else if(type.equalsIgnoreCase(CHAR_ARRAY_TYPE))
                columnTypes[i] = ColumnTypes.CHAR_ARRAY;
            else if(type.equalsIgnoreCase(BOOLEAN_TYPE))
                columnTypes[i] = ColumnTypes.BOOLEAN;
            else
                columnTypes[i] = ColumnTypes.STRING;
        }
    }

    private void createColumnLabels(ArrayList row) {
        columnLabels = new String[row.size()];
        for(int i = 0; i < row.size(); i++)
            columnLabels[i] = new String((char[])row.get(i));
    }

    /*private void createDataTypes(ArrayList row) {
        dataTypes = new int[row.size()];
        for(int i = 0; i < row.size(); i++) {
            char[] ty = (char[])row.get(i);
            String type = new String(ty);
            if(type.equalsIgnoreCase(IN_TYPE) || type.equalsIgnoreCase(IN_TYPE2))
                dataTypes[i] = IN;
            else if(type.equalsIgnoreCase(OUT_TYPE) || type.equalsIgnoreCase(OUT_TYPE2))
                dataTypes[i] = OUT;
            else
                dataTypes[i] = OMIT;
        }
    }*/

    /*private void createFeatureTypes(ArrayList row) {
        featureTypes = new int[row.size()];
        for(int i = 0; i < featureTypes.length; i++) {
            char[] ty = (char[])row.get(i);
            String type = new String(ty);
            if(type.equalsIgnoreCase(NOMINAL_TYPE))
                featureTypes[i] = NOMINAL;
            else if(type.equalsIgnoreCase(SCALAR_TYPE))
                featureTypes[i] = SCALAR;
            else
                featureTypes[i] = NOMINAL;
        }
    }*/

    /**
     * Get the number of columns.
     * @return the number of columns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * Get the number of rows
     * @return the number of rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Get the column type at column i.
     * @return the column type at column i, or -1 if no column types were
     * specified
     */
    public int getColumnType(int i) {
        if(columnTypes == null)
            return -1;
        return columnTypes[i];
    }

    /**
     * Get the column label at column i.
     * @return the column label at column i, or null if no column labels were
     * specified
     */
    public String getColumnLabel(int i) {
        if(columnLabels == null)
            return null;
        return columnLabels[i];
    }

    /**
     * Get the data type (in-out) at column i.
     * @return the data type (in-out) at column i, or -1 if no in-out types were
     * specified
     */
    public int getDataType(int i) {
        if(dataTypes == null)
            return -1;
        return dataTypes[i];
    }

    /**
     * Get the feature type (nominal-scalar) at column i.
     * @return the feature type at column i, or -1 if no nominal-scalar types
     * were specified.
     */
    public int getFeatureType(int i) {
        if(featureTypes == null)
            return -1;
        return featureTypes[i];
    }

    /**
     * This method will search the document, counting the number of each
     * possible delimiter per line to identify the delimiter to use. If in
     * the first pass we can not find a single delimiter that that can be found
     * the same number of times in each line, we will strip all the whitespace
     * off the start and end of the lines, and try again. If then we still can
     * not find the delimiter, we will fail.
     *
     * This method also counts the number of rows and columns in the file.
     */
    private void scanFile() throws Exception {
        int counters [] = new int [3];
        final int tabIndex = 0, spaceIndex = 1, commaIndex = 2;

        // Now just count them.
        int commaCount = -1, spaceCount = -1, tabCount = -1;
        boolean isComma = true, isSpace = true, isTab = true;

        String line;
        final int NUM_ROWS_TO_COUNT = 10;
        ArrayList lines = new ArrayList();

        BufferedReader reader = new BufferedReader (new FileReader (file));

        // read the file in one row at a time
        int currentRow = 0;
        while ( ((line = reader.readLine ()) != null) && (currentRow < NUM_ROWS_TO_COUNT)) {
            lines.add(line);
            char[] bytes = line.toCharArray ();

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
            currentRow++;
        }

        char delim = '0';
        boolean delimiterFound = false;

        if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount >= 0) )
            isTab = true;
        else if( (commaCount <= 0) && (spaceCount >= 0) && (tabCount <= 0) )
            isSpace = true;
        else if( (commaCount >= 0) && (spaceCount <= 0) && (tabCount <= 0) )
            isComma = true;

        // Did one of the possible delimiters come up a winner?
        if (isComma && !isSpace && !isTab) {
            delimiter = COMMA;
            delimiterFound = true;
        }
        else if (!isComma && isSpace && !isTab) {
            delimiter = SPACE;
            delimiterFound = true;
        }
        else if (!isComma && !isSpace && isTab) {
            delimiter = TAB;
            delimiterFound = true;
        }

        if(!delimiterFound) {
            // OK, that didn't work. Lets trim the strings and see if it will work the.
            // read the file in one row at a time
            isComma = true;
            isSpace = true;
            isTab = true;

            for (currentRow = 0; currentRow < lines.size(); currentRow++) {
                String tmp = ((String)lines.get(currentRow)).trim();
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

            if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount > 0) )
                isTab = true;
            else if( (commaCount <= 0) && (spaceCount >= 0) && (tabCount <= 0) )
                isSpace = true;
            else if( (commaCount >= 0) && (spaceCount <= 0) && (tabCount <= 0) )
                isComma = true;

            // Did one of the possible delimiters come up a winner?
            if (isComma && !isSpace && !isTab) {
                delimiter = COMMA;
                delimiterFound = true;
            }
            else if (!isComma && isSpace && !isTab) {
                delimiter = SPACE;
                delimiterFound = true;
            }
            else if (!isComma && !isSpace && isTab) {
                delimiter = TAB;
                delimiterFound = true;
            }

            if(!delimiterFound)
                throw new IOException("No delimiter could be found.");
        }

        int nr = 0;
        int nc = 0;

        reader = new BufferedReader (new FileReader (file));

        // read the file in one row at a time
        while ((line = reader.readLine ()) != null) {
            nr++;
            int ct = countRowElements(line);
            if(ct > nc)
                nc = ct;
        }
        numRows = nr;
        numColumns = nc;
        blanks = new boolean[nr][nc];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numColumns; j++)
                blanks[i][j] = false;
        }
    }


    /**
     * This method will search the document, counting the number of each
     * possible delimiter per line to identify the delimiter to use. If in
     * the first pass we can not find a single delimiter that that can be found
     * the same number of times in each line, we will strip all the whitespace
     * off the start and end of the lines, and try again. If then we still can
     * not find the delimiter, we will fail.
     * @param f the file to check for delimiters
     * @returns one from among the set of delimiters we look for (',', ' ', '\t'), or '=' if the search failed.
     /
    private char findDelimiter() {
        int counters [] = new int [3];
        final int tabIndex = 0, spaceIndex = 1, commaIndex = 2;

        // Now just count them.
        int commaCount = -1, spaceCount = -1, tabCount = -1;
        boolean isComma = true, isSpace = true, isTab = true;
        String line [];
        if(numRows < 10)
            line = new String [numRows];
        else
            line = new String[10];
        try {
            BufferedReader reader = new BufferedReader (new FileReader (file));

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
                if (++currentRow == line.length)
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
        for (int currentRow = 0; currentRow < line.length ;currentRow++) {
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
    }*/

    /**
     * Skip to a specific line in the file.
     * @param lineNum the line number to skip to
     */
    private void skipToLine(int lineNum) {
        try {
            if(lineNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int ctr = 0;
            while(ctr < lineNum) {
                lineReader.readLine();
                ctr++;
            }
        }
        catch(Exception e) {
        }
    }

    /**
     * Skip to a specific row in the file.  Rows are lines of data in the file,
     * not including the optional meta data rows.
     * @param rowNum the row number to skip to
     */
    protected void skipToRow(int rowNum) {
        if(labelsRow > -1)
            rowNum++;
        if(typesRow > -1)
            rowNum++;
        if(inOutRow > -1)
            rowNum++;
        if(nomScalarRow > -1)
            rowNum++;
        try {
            if(rowNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int current = lineReader.getLineNumber();
            while(current < rowNum-1) {
                lineReader.readLine();
                current++;
            }
        }
        catch(Exception e) {
        }
    }

    /**
     * Read in a row and put its elements into an ArrayList.
     * @param row the row to tokenize
     * @return an ArrayList containing each of the elements in the row
     */
    public char[][] getRowElements(int rowNum) {
        try {
            skipToRow(rowNum);
            String row = lineReader.readLine();
            int current = 0;
            char[][] thisRow = new char[numColumns][];
            int counter = 0;
            char [] bytes = row.toCharArray();
            char del = delimiter;
            int len = bytes.length;

            for (int i = 0 ; i < len ; i++) {
                if (bytes[i] == del) {
                    if ((i-current) > 0) {
                        char [] newBytes = new char [i-current];
                        System.arraycopy (bytes, current, newBytes, 0, i-current);
                        thisRow[counter] = newBytes;
                        counter++;
                    } else {
                        this.addBlank(rowNum, counter);
                        thisRow[counter] = new char[0];
                        counter++;
                    }
                    current = i+1;
                }
            }

            if ((len-current) > 0) {
                char [] newBytes = new char [len-current];
                System.arraycopy (bytes, current, newBytes, 0, len-current);
                thisRow[counter] = newBytes;
                counter++;
            }

            for(int i = counter; i < thisRow.length; i++) {
                thisRow[i] = new char[0];
                this.addBlank(rowNum, i);
            }

            return thisRow;
        }
        catch(Exception e) {
            return null;
        }
    }

    /**
     * Read in a row and put its elements into an ArrayList.
     * @param row the row to tokenize
     * @return an ArrayList containing each of the elements in the row
     */
    private ArrayList getLineElements(int rowNum) {
        try {
            //skipToRow(lineNum);
            skipToLine(rowNum);
            String row = lineReader.readLine();
            int current = 0;
            ArrayList thisRow = new ArrayList();
            char [] bytes = row.toCharArray();
            char del = delimiter;
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
        catch(Exception e) {
            return null;
        }
    }

    /**
        Count the number of tokens in a row.
        @param row the row to count
        @return the number of tokens in the row
    */
    private int countRowElements (String row) {
        int current = 0;

        char [] bytes = row.toCharArray ();
        int len = bytes.length;

        int numToks = 0;

        for (int i = 0 ; i < len ; i++) {
            if (bytes[i] == delimiter) {
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

	/**
		returns a Table that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
		*/
	/*public Table getBlanks(){
		Object[] rowsObjArray=_blankRows.toArray();
		Object[] colsObjArray=_blankColumns.toArray();
		int numBlanks=rowsObjArray.length;
		//use these to make the kind of rows that VT's like

		IntColumn rowsColumn=new IntColumn(numBlanks);
		rowsColumn.setLabel("Rows");

		IntColumn colsColumn=new IntColumn(numBlanks);
		colsColumn.setLabel("Column");

		Column[] internal=new Column[2];
		internal[0]=rowsColumn;
		internal[1]=colsColumn;

		TableImpl table= (TableImpl)DefaultTableFactory.getInstance().createTable(internal);
		Object[] tableRow=new Object[2];
		for(int i=0; i<numBlanks; i++){
			tableRow[0]=rowsObjArray[i];
			tableRow[1]=colsObjArray[i];
			table.setRow(tableRow, i);
		}
		return table;
	}*/

	/*
		keeps track of which fields that were read were actually
		blank
	*/
	protected void addBlank(int r, int c){
		//_blankRows.add(new Integer(r));
		//_blankColumns.add(new Integer(c));
        blanks[r][c] = true;
	}

	/**
		returns a Table that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
	/
	public int[][] getBlanks(){
		int numBlanks = _blankRows.size();

        int[][] blanks = new int[2][numBlanks];
        for(int i = 0; i < numBlanks; i++) {
            blanks[0][i] = ((Integer)_blankRows.get(i)).intValue();
            blanks[1][i] = ((Integer)_blankRows.get(i)).intValue();
        }
        return blanks;
	}*/

   public boolean[][] getBlanks() {
       return blanks;
   }
}