package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Class FixedFormatParser
 *
 * Implements a line oriented parser with a fixed format. <p>
 *
 *
 * The method <code>ignoreSyntaxErrors</code> can be used to toggle syntax
 * checking for data in columns. If set to true, it will put an null object
 * in the column. If set to false, it triggers an exception if the data in
 * the column can not be read as specified in <code>_format</code>.<p>
 *
 */
class FixedFormatParser extends FileInputStream
{

    static String STRING_TYPE = "String";
    static String FLOAT_TYPE = "float";
    static String DOUBLE_TYPE = "double";
    static String INT_TYPE = "int";
    static String BOOLEAN_TYPE = "boolean";
    static String CHAR_TYPE = "char[]";
    static String BYTE_TYPE = "byte[]";
    static String LONG_TYPE = "long";
    static String SHORT_TYPE = "short";


    protected LineNumberReader _reader = null;
    protected   String           _format = null;
    protected   String           _comment = "#;";
    protected   long             _fileLength = 0;
    protected   boolean          _ignoreSyntaxErrors = false;
    protected   Vector           _columnBegin = null;
    protected   Vector           _columnEnd = null;
    protected   Vector           _columnType;
    protected   Vector           _columnLabels;
    protected   int              _tableLength;
    protected   int              _noOfColumns;
    protected   int              _labelsRow;
    protected   int              _typesRow;

    protected	double	_emptyValue = 0;

    //these are used by the addBlank and getBlanks methods
    ArrayList blankRows;
    ArrayList blankCols;

    /**
     * Creates a parser for file, using format for columns from typesRow.
     */

    public FixedFormatParser(File file, int labelsRow, int typesRow)
        throws FileNotFoundException
    {
        super(file);
	_labelsRow = labelsRow;
	_typesRow = typesRow;
        _reader = new LineNumberReader(new InputStreamReader(this));
        _fileLength = file.length();

    }

   public FixedFormatParser(File file, int labelsRow, int typesRow, double emptyValue)
        throws FileNotFoundException
    {
	this(file, labelsRow, typesRow);
	_emptyValue = emptyValue;
    }

    public FixedFormatParser(File file, Table header)
        throws FileNotFoundException
    {
        super(file);
	_labelsRow = -1;
	_typesRow = -1;

        for ( int i =0; i < header.getNumColumns() ; i++)
	    {
			//System.out.println(header.getColumnLabel(i).toUpperCase());
		if (header.getColumnLabel(i).toUpperCase().equals("LABEL"))
		    setColumnLabels(header,i);
		if (header.getColumnLabel(i).toUpperCase().equals("TYPE"))
		    setColumnTypes(header,i);
		if (header.getColumnLabel(i).toUpperCase().equals("START"))
		    setColumnBeginings(header,i);
		if (header.getColumnLabel(i).toUpperCase().equals("STOP"))
		    setColumnEnds(header,i);
		if (header.getColumnLabel(i).toUpperCase().equals("LENGTH"))
		    setColumnBounds(header,i);
	    }
        _reader = new LineNumberReader(new InputStreamReader(this));
        _fileLength = file.length();

    }

    public FixedFormatParser(File file, Table header, double emptyVal)
        throws FileNotFoundException
    {
	this(file, header);
	_emptyValue = emptyVal;
	}

    /**
     * @return whether the line is a comment or an empty line
     */
    protected boolean isComment(String line)
    {
        return (line.length() == 0 ||
                _comment.indexOf(line.charAt(0), 0) != -1);
    }

    public void ignoreSyntaxErrors(boolean flag)
    {
        _ignoreSyntaxErrors = flag;
    }


    public void setColumnTypes(Table vt, int col) {
	_columnType = new Vector();
	int nr=vt.getNumRows();
	for ( int i = 0 ; i < nr ; i++)
	    _columnType.add(vt.getString(i,col));
	_noOfColumns = nr;
	//System.out.println("nr:"+nr);
    }


    public void setColumnBeginings(Table vt, int col) {
	// _columnBegin has not been initialized by setColumnBounds
	if (_columnBegin == null)
	    {
		_columnBegin = new Vector();
		int nr=vt.getNumRows();
		for ( int i = 0 ; i < nr ; i++)
		    _columnBegin.add(new Integer(vt.getInt(i,col)));
	    }
    }


    public void setColumnEnds(Table vt, int col) {
	// _columnEnd has not been initialized by setColumnBounds
	if (_columnEnd == null)
	    {
		_columnEnd = new Vector();
		int nr=vt.getNumRows();
		for ( int i = 0 ; i <  nr; i++)
		    _columnEnd.add(new Integer(vt.getInt(i,col)));
	    }
    }


    public void setColumnLabels(Table vt, int col) {
	_columnLabels = new Vector();
	int nr=vt.getNumRows();
	for ( int i = 0 ; i <  nr ; i++)
	    _columnLabels.add(vt.getString(i,col));
    }


    public void setColumnBounds(Table vt, int col) {
	_columnEnd = new Vector();
	_columnBegin = new Vector();
	int start = 0;
	int end;
	int nr=vt.getNumRows();
	for ( int i = 0 ; i <  nr ; i++) {
	    _columnBegin.add(new Integer(start));
	    end=start+vt.getInt(i,col)-1;
	    start = end+1;
	    _columnEnd.add(new Integer(end));
	}
    }


    /**
     * reads in the header. File specific, needs to be overriden by subclasses.
     * @return the number of lines in the header
     * @exception ParseException
     * @exception IOException
     */
    protected int readHeader()
        throws ParseException, IOException
    {

	int rowsToParse = 0;
	if ( _labelsRow >= 0 )
	    rowsToParse++;
	if ( _typesRow >= 0 )
	    rowsToParse++;

	for ( int i =0; i < rowsToParse ; i ++) {
	    String line = _reader.readLine();

	    //  System.out.println(" header line " + line);
	    if(line == null)
		throw new ParseException("null line " + i   , 0);


	    if( i == _labelsRow )  // set the labels if given
		{
		    StringTokenizer st = new StringTokenizer(line, " ");
		    _columnLabels = new Vector();
		    while(st.hasMoreTokens())
			_columnLabels.add(st.nextToken());
		}

	    if ( i == _typesRow) {
		parseFormat(line);
	    }
	}
	//	System.out.println("header done rowsToParse " + rowsToParse);
	return _reader.getLineNumber();

	}

    /**
     * Parse the body of the file, which is formatted according to
     * <code>format</code>. <br>
     * Most of derived classes shouldn't need to change this method.
     *
     * @return a Table
     *
     * @exception ParseException
     * @exception IOException
     */
	public Table parse() throws ParseException, IOException{
		blankRows=new ArrayList();
		blankCols=new ArrayList();

        int headerLines = readHeader(); // read in the header

        /* count the number of lines in the rest of the file */
        _reader.mark((int)_fileLength);  // mark the current position in the stream
		/*char buffer[] = new char[(int)_fileLength];
		_reader.read(buffer, 0, (int)_fileLength);
		buffer=null;
		int lines = _reader.getLineNumber();
		_reader.reset();
		*/

	    // now populate the columns
		//*****************This is where this sublcass changes

		//get rid of the vectors
		int[] begins=new int[_noOfColumns];
		int[] ends=new int[_noOfColumns];

		for(int i=0;i<_noOfColumns; i++){
			begins[i]=((Integer)_columnBegin.get(i)).intValue();
			ends[i]=((Integer)_columnEnd.get(i)).intValue();
		}
		//System.out.println("noOfColumns:"+_noOfColumns);
		int lineLength=ends[_noOfColumns-1];

		//the number of lines in the file
		int lines=(int)_fileLength/(lineLength+1);

		_tableLength = lines-headerLines;

		/* allocate the table */
		//Table result = new VerticalTable(_noOfColumns);

		/* allocate the columns  and set the labels*/
		Column []tableColumns = new Column[_noOfColumns];
		for(int i = 0; i < _noOfColumns; i++) {

		    tableColumns[i] = createColumn((String)_columnType.get(i),_tableLength);
		    if ( _columnLabels != null )
				tableColumns[i].setLabel((String)_columnLabels.get(i));
	   		else
				tableColumns[i].setLabel(Integer.toString(i));
		}
		System.out.println("Reader: HeaderInfo In"+_tableLength);

		//done w/ these
		_columnBegin=null;
		_columnEnd=null;
		_columnType=null;
		_columnLabels=null;

		char[] lineChars=new char[lineLength];

		//make sure we're at the beginning
		//this.reset();
		//advance past the header
		/*for(int i=0; i<headerLines; i++){
			_reader.read(lineChars, 0, lineLength);
			_reader.skip(1);//the line delimiter
		}*/


		//these will be used for eliminating trailing and
		//leading whitespace
		int trueBegin;
		int trueEnd;

		//this is the current
		//read each line, put the data in the columns
		for(int r=0; r<_tableLength; r++){
			_reader.read(lineChars, 0, lineLength);
			_reader.skip(1);//the line delimiter

			for(int col=0; col<_noOfColumns; col++){
				//getting rid of whitespace
				trueBegin=begins[col]-1;
				trueEnd=ends[col]-1;
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
					try{
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
					//if(r==100){
					//	System.out.println(tableColumns[col].getString(r));
					//}
				}else{
					this.addBlank(r, col);
				}
			}
		}
		return TableFactory.createTable(tableColumns);
	}

	/*
		keeps track of which fields that were read were actually
		blank
		*/
	protected void addBlank(int r, int c){
		blankRows.add(new Integer(r));
		blankCols.add(new Integer(c));
	}

	/**
		returns a VT that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
		*/
	public Table getBlanks(){
		Object[] rowsObjArray=blankRows.toArray();
		Object[] colsObjArray=blankCols.toArray();
		int numBlanks=rowsObjArray.length;
		//use these to make the kind of rows that VT's like

		IntColumn rowsColumn=new IntColumn(numBlanks);
		rowsColumn.setLabel("Rows");

		IntColumn colsColumn=new IntColumn(numBlanks);
		colsColumn.setLabel("Column");

		Column[] internal=new Column[2];
		internal[0]=rowsColumn;
		internal[1]=colsColumn;

		Table table= TableFactory.createTable(internal);
		Object[] tableRow=new Object[2];
		for(int i=0; i<numBlanks; i++){
			tableRow[0]=rowsObjArray[i];
			tableRow[1]=colsObjArray[i];
			table.setRow(tableRow, i);
		}
		return table;
	}
    /**
     * parses the format and creates the template for the columns
     * as well as initializes the _columnWidths
     * @return the template
     */
    protected void parseFormat(String format)
        throws UnsupportedEncodingException, ParseException
    {
	if(format == null) throw new ParseException("null format string", 0);

	StringTokenizer st = new StringTokenizer(format, "- ", true);


        int col = 0;
	int first = 0;
	int last = 0;
	int begining = -1;
	int ending  = -1;
	_columnBegin = new Vector();
	_columnEnd = new Vector();
	_columnType = new Vector();

        while(st.hasMoreTokens()) {

	    String firstNo =  st.nextToken();

	    String delim = null;
	    String  type = null;
	    boolean done = false;
	    if (st.hasMoreTokens())
		delim =  st.nextToken();
	    else
		System.out.println("missing type in format");



            // ----------------------------------------------------------------
            // get the beginning and ending column
            // ----------------------------------------------------------------
            first = Integer.decode(firstNo).intValue();
	    if(st.hasMoreTokens())
		if (delim.equals("-"))
		  {
		      String secondNo = st.nextToken();
		      last =Integer.decode(secondNo).intValue();

		  }
		else
		    {
			type = st.nextToken();
			//delim = st.nextToken();
			last = ending +1 + first;
			first = ending+1;
			done = true;
		    }
	    else
		System.out.println("missing type in format");

	    if ( !done && st.hasMoreTokens()) {
		delim  = st.nextToken();
		if ( st.hasMoreTokens()) {
		    type = st.nextToken() ;
		    //delim = st.nextToken();
		    done = true;

		}
		else
		    System.out.println("missing type in format");
	    }
	    //	    else
	    //	System.out.println("missing type in format");

	    if (st.hasMoreTokens())
		delim = st.nextToken();

	    System.out.println("first last " + first + " " + last);

	    _columnBegin.add(new Integer(first));
	    _columnEnd.add(new Integer(last));
	    _columnType.add(type);
	    begining = first;
	    ending = last;
	    col++;
	}

	    _noOfColumns = col;

    }



	/**
		Create a column given the type and size.
		@param type the type of column to create
		@param size the initial size of the column
		@return a new, empty column
	*/
	protected Column createColumn(String type, int size) {
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




    public String toString() {
	String result = "";
	for(int i = 0; i < _columnType.size(); i++)
	    result += (String)_columnType.get(i) + ":" +
		_columnBegin.get(i) + " - " + _columnEnd.get(i);
	return result;
    }
}











