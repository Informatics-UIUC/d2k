package ncsa.d2k.modules.core.io.file.input;
//package ncsa.d2k.modules.projects.clutter.rdr;

import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Read a fixed format file.
 */
public class FixedFileParser implements FlatFileParser {

    private int[] _columnType;
    private String[] _columnLabels;

    private int[] _columnBegin;
    private int[] _columnEnd;

    private LineNumberReader _reader;
    //private long _fileLength;
    private int _tableLength;

    private int _noOfColumns;

    private File _file;

	//private char[] lineChars;
    private int lineLength;

    private boolean[][] bTable;

    public FixedFileParser(File file, Table header) throws Exception {
        int lbl = -1;
        int typ = -1;
        int strt = -1;
        int stp = -1;
        int len = -1;

        if(header.getNumColumns() == 0)
            throw new Exception("Could not read header.");

        for ( int i =0; i < header.getNumColumns() ; i++) {
		    if (header.getColumnLabel(i).equalsIgnoreCase(LABEL))
		        //setColumnLabels(header,i);
                lbl = i;
		    if (header.getColumnLabel(i).equalsIgnoreCase(TYPE))
		        //setColumnTypes(header,i);
                typ = i;
		    if (header.getColumnLabel(i).equalsIgnoreCase(START))
		        //setColumnBeginings(header,i);
                strt = i;
		    if (header.getColumnLabel(i).equalsIgnoreCase(STOP))
		        //setColumnEnds(header,i);
                stp = i;
		    if (header.getColumnLabel(i).equalsIgnoreCase(LENGTH))
		        //setColumnBounds(header,i);
                len = i;
	    }

        if(lbl != -1)
		    setColumnLabels(header, lbl);
        if(typ != -1)
		    setColumnTypes(header, typ);
        if(strt == -1)
            throw new Exception("Could not determine column start.");
        else
		    setColumnBeginings(header, strt);
        if(len == -1 && stp != -1)
		    setColumnEnds(header, stp);
        else if(len != -1)
		    setColumnBounds(header, len);
        else
            throw new Exception("Could not determine column sizes.");

        // the existence of the file should have been checked beforehand.
        _reader = new LineNumberReader(new FileReader(file));

        //_fileLength = file.length();
        _file = file;

		int lineLength = _columnEnd[_noOfColumns-1];
        //the number of lines in the file
		//int lines=(int)_fileLength/(lineLength+1);

        // count the number of lines in the file
        BufferedReader rdr = new BufferedReader(new FileReader(file));
        String line = null;
        while( (line = rdr.readLine()) != null) {
            _tableLength++;
            lineLength = line.length();
        }

        /*System.out.println("LL: "+lineLength);
        System.out.println("NR: "+_tableLength);
        System.out.println("NC: "+this._noOfColumns);
        for(int i = 0; i < this._columnBegin.length; i++) {
            System.out.println("BEGIN: "+_columnBegin[i]);
            System.out.println("END: "+_columnEnd[i]);
        }*/

		//_tableLength = lines-headerLines;
        //_tableLength = lines;
        //lineLength= _columnEnd[_noOfColumns-1];
	    //lineChars=new char[lineLength];
        _reader.setLineNumber(0);
        //currentLineNum = 0;

        bTable = new boolean[this._tableLength][this._noOfColumns];
        for(int i = 0; i < _tableLength; i++) {
            for(int j = 0; j < _noOfColumns; j++)
                bTable[i][j] = false;
        }
    }

    private void setColumnLabels(Table vt, int col) {
	    int nr=vt.getNumRows();
        _columnLabels = new String[nr];
	    for ( int i = 0 ; i <  nr ; i++)
	        _columnLabels[i] = vt.getString(i,col);
	    _noOfColumns = nr;
    }

    private void setColumnTypes(Table vt, int col) {
        int nr = vt.getNumRows();
        _columnType = new int[nr];
	    for ( int i = 0 ; i < nr ; i++) {
            String type = vt.getString(i, col);
            if(type.equalsIgnoreCase(STRING_TYPE) )
                _columnType[i] = ColumnTypes.STRING;
            else if(type.equalsIgnoreCase(DOUBLE_TYPE))
                _columnType[i] = ColumnTypes.DOUBLE;
            else if(type.equalsIgnoreCase(INT_TYPE))
                _columnType[i] = ColumnTypes.INTEGER;
            else if(type.equalsIgnoreCase(FLOAT_TYPE))
                _columnType[i] = ColumnTypes.FLOAT;
            else if(type.equalsIgnoreCase(SHORT_TYPE))
                _columnType[i] = ColumnTypes.SHORT;
            else if(type.equalsIgnoreCase(LONG_TYPE))
                _columnType[i] = ColumnTypes.LONG;
            else if(type.equalsIgnoreCase(BYTE_TYPE))
                _columnType[i] = ColumnTypes.BYTE;
            else if(type.equalsIgnoreCase(CHAR_TYPE))
                _columnType[i] = ColumnTypes.CHAR;
            else if(type.equalsIgnoreCase(BYTE_ARRAY_TYPE))
                _columnType[i] = ColumnTypes.BYTE_ARRAY;
            else if(type.equalsIgnoreCase(CHAR_ARRAY_TYPE))
                _columnType[i] = ColumnTypes.CHAR_ARRAY;
            else if(type.equalsIgnoreCase(BOOLEAN_TYPE))
                _columnType[i] = ColumnTypes.BOOLEAN;
            else
                _columnType[i] = ColumnTypes.STRING;

	    }
    }

    private void setColumnBeginings(Table vt, int col) {
	    // _columnBegin has not been initialized by setColumnBounds
	    //if (_columnBegin == null) {
		    int nr = vt.getNumRows();
		    _columnBegin = new int[nr];
		    for ( int i = 0 ; i < nr ; i++)
		        _columnBegin[i] = vt.getInt(i,col);
	    //}
    }

    private void setColumnEnds(Table vt, int col) {
	    // _columnEnd has not been initialized by setColumnBounds
	    //if (_columnEnd == null) {
		    int nr=vt.getNumRows();
		    _columnEnd = new int[nr];
		    for ( int i = 0 ; i <  nr; i++)
		        _columnEnd[i] = vt.getInt(i,col);
	    //}
    }

    private void setColumnBounds(Table vt, int col) {
	    int nr=vt.getNumRows();
	    //_columnEnd = new int[nr];
	    //_columnBegin = new int[nr];
	    int start = 0;
	    int end;
        _columnEnd = new int[vt.getNumRows()];
	    for ( int i = 0 ; i <  nr ; i++) {
	        start = _columnBegin[i];
	        end=start+vt.getInt(i,col)-1;
	        start = end+1;
	        _columnEnd[i] = end;
	    }
    }

    public char[][] getRowElements(int i) {
        try {
            return parseLine(i);
        }
        catch(Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the number of columns.
     * @return the number of columns in the file
     */
    public int getNumColumns() {
        return _noOfColumns;
    }

    /**
     * Get the number of rows.
     * @return the number of rows in the file.
     */
    public int getNumRows() {
        return (int)_tableLength;
    }

    public int getColumnType(int i) {
        if(_columnType == null)
            return -1;
        return _columnType[i];
    }

    public String getColumnLabel(int i) {
        if(_columnLabels == null)
            return null;
        return _columnLabels[i];
    }

    /*public int getDataType(int i) {
        return -1;
    }

    public int getFeatureType(int i) {
        return -1;
    }*/

    private char[][] parseLine(int row) throws IOException {
		//System.out.println("noOfColumns:"+_noOfColumns);
        //ArrayList retVal = new ArrayList();
        char[][] retVal = new char[_noOfColumns][];

        //System.out.println("parse line: "+row+" "+currentLineNum);

		//these will be used for eliminating trailing and
		//leading whitespace
		int trueBegin;
		int trueEnd;

		//this is the current
		//read each line, put the data in the columns
		//for(int r=0; r<_tableLength; r++){

        // MUST SKIP TO THE APPROPRIATE LINE HERE
        skipToLine(row);

        //lineChars = _reader.readLine().toCharArray();

		//_reader.read(lineChars, 0, lineLength);
		//_reader.skip(1);//the line delimiter
        //_reader.setLineNumber(_reader.getLineNumber()+1);

        String ln = _reader.readLine();
        char[] lineChars = ln.toCharArray();

		for(int col=0; col<_noOfColumns; col++){
		    //getting rid of whitespace
			trueBegin= _columnBegin[col]-1;
			trueEnd= _columnEnd[col]-1;
			//trueBegin= _columnBegin[col];
			//trueEnd= _columnEnd[col];
			while(((lineChars[trueBegin]==' ')/*||(lineChars[trueBegin]=='0')*/)
			    &&(trueBegin<trueEnd))
					trueBegin++;
			//if(r==100)
			//System.out.println("b:"+trueBegin+" e:"+trueEnd);
			while((lineChars[trueEnd]==' ')&&(trueEnd>trueBegin))
			   trueEnd--;
			//if(r==100)
			//		System.out.println("b:"+trueBegin+" e:"+trueEnd);

			if(trueBegin!=trueEnd){
            //if(true) {
			    /*try{
			        tableColumns[col].setString(
						new String( lineChars,
										trueBegin,
										trueEnd-trueBegin+1),
															r);
					}catch(NumberFormatException e){
						System.err.println("NumberFormatException at");
						System.err.println("Row:"+r+" Col:"+col);
						System.err.println("  "+new String(lineChars,
										trueBegin,
										trueEnd-trueBegin+1));

					}
                    */
                char[] element = new String(lineChars, trueBegin, trueEnd-trueBegin+1).toCharArray();
                //char[] element = new String(lineChars, _columnBegin[col], _columnEnd[col]-_columnBegin[col]+1).toCharArray();
                //System.out.println(new String(element)+"/");
                //retVal.add(element);
                retVal[col] = element;
			}
            else {
                //retVal.add(new char[0]);
                retVal[col] = new char[0];
			    addBlank(row, col);
            }
		}
        return retVal;
    }

    private void skipToLine(int lineNum) {
        try {
            if(lineNum < _reader.getLineNumber()) {
                _reader = new LineNumberReader(new FileReader(_file));
                int ctr = 0;
                while(ctr < lineNum) {
                    _reader.readLine();
                    ctr++;
                }
            }
        }
        catch(Exception e) {
        }
    }

	/*
		keeps track of which fields that were read were actually
		blank
	*/
	private void addBlank(int r, int c){
/*		_blankRows.add(new Integer(r));
		_blankColumns.add(new Integer(c));
        _blankRows.add(r);
        _blankColumns.add(c);
        */
        bTable[r][c] = true;
	}

    public boolean[][] getBlanks() {
        return bTable;
    }

	/**
		returns a Table that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
	/
	public int[][] getBlanks(){
		int numBlanks = _blankRows.size();

        int[] rws = _blankRows.toNativeArray();
        int[] cls = _blankColumns.toNativeArray();

        int[][] blanks = {rws,cls};
        for(int i = 0; i < numBlanks; i++) {
            //blanks[0][i] = ((Integer)_blankRows.get(i)).intValue();
            //blanks[1][i] = ((Integer)_blankRows.get(i)).intValue();
        }
        return blanks;
	}*/
}