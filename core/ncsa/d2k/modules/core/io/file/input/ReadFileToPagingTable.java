//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.paging.*;

/**
 * Read a file to a MutablePagingTable.
 */
public class ReadFileToPagingTable extends ReadFileToTable {

    private int numRowsPerPage = 5000;
    public int getNumRowsPerPage() {
        return numRowsPerPage;
    }
    public void setNumRowsPerPage(int nr) {
        numRowsPerPage = nr;
    }

    /*private boolean useBlanks = true;
    public void setUseBlanks(boolean b) {
        useBlanks = b;
    }
    public boolean getUseBlanks() {
        return useBlanks;
    }*/

    public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[2];
        retVal[0] = new PropertyDescription("useBlanks", "Set Blanks to be Missing Values",
            "When true, any blank entries in the file will be set as missing values in the table.");
        retVal[1] = new PropertyDescription("numRowsPerPage", "The Number of Rows Per Page",
            "The maximum number of rows in a page of the PagingTable.");
        return retVal;
    }

    /*public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }

    public String[] getOutputTypes() {
        //String[] out = {"ncsa.d2k.modules.core.datatype.table.Table"};
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }*/

    /*public String getInputInfo(int i) {
        return "A FlatFileReader to read data from.";
    }*/

    public String getOutputInfo(int i) {
        return "The data read from the FlatFileParser in a PagingTable.";
    }

    /*public String getInputName(int i) {
        return "filereader";
    }*/

    public String getOutputName(int i) {
        return "table";
    }

    public String getModuleInfo() {
        return "Read the data from a FlatFileReader to a PagingTable.";
    }

    public String getModuleName() {
        return "ReadFileToPagingTable";
    }

    /*public void doit() throws Exception {
        FlatFileParser fle = (FlatFileParser)pullInput(0);
        Table t = createTable(fle);
        //Table bt = createBlanks(fle);

        if(useBlanks) {
            // if there were blanks in the file,
            // set them as 'missing values' in the table
            MutableTable mt = (MutableTable)t;
            boolean[][] blanks = fle.getBlanks();
            int nr = t.getNumRows();
            int nc = t.getNumColumns();
            for(int i = 0; i < nr; i++) {
                for(int j = 0; j < nc; j++) {
                    if(blanks[i][j])
                        mt.setValueToMissing(true, i, j);
                }
            }
        }

        pushOutput(t, 0);
        //pushOutput(bt, 1);
    }*/

    protected Table createTable(FlatFileParser df) {
        int numRows = df.getNumRows();
        int numColumns = df.getNumColumns();

        MutablePagingTable pt = new MutablePagingTable();

        // now figure out how many sub-tables we will make.
        int numTables = (int)Math.ceil(((double)numRows)/((double)numRowsPerPage));
        // the number of rows per page
        int [] pageRowNums = new int[numTables];
        int curNum = 0;
        for(int i = 0; i < numTables; i++) {
           if( (curNum+numRowsPerPage) <= numRows) {
               pageRowNums[i] = numRowsPerPage;
               curNum += pageRowNums[i];
           }
           else
               pageRowNums[i] = numRows-curNum;
        }

        curNum = 0;

        boolean hasTypes = false;

        // for each table
        for(int nt = 0; nt < numTables; nt++) {

            // create the columns
            Column[] columns = new Column[numColumns];
            for(int col = 0; col < columns.length; col++) {
                int type = df.getColumnType(col);

                /*if(type == ColumnTypes.STRING)
                    columns[col] = new StringColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.DOUBLE)
                    columns[col] = new DoubleColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.FLOAT)
                    columns[col] = new FloatColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.INTEGER)
                    columns[col] = new IntColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.SHORT)
                    columns[col] = new ShortColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.LONG)
                    columns[col] = new LongColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.CHAR_ARRAY)
                    columns[col] = new CharArrayColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.BYTE_ARRAY)
                    columns[col] = new ByteArrayColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.CHAR)
                    columns[col] = new CharColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.BYTE)
                    columns[col] = new ByteColumn(pageRowNums[nt]);
                else if(type == ColumnTypes.BOOLEAN)
                    columns[col] = new BooleanColumn(pageRowNums[nt]);
                else
                    columns[col] = new StringColumn(pageRowNums[nt]);
                */
                columns[col] = ColumnUtilities.createColumn(type, pageRowNums[nt]);

                if(type != -1)
                    hasTypes = true;

                // set the label
                String label = df.getColumnLabel(col);
                if(label != null)
                    columns[col].setLabel(label);
            }

            MutableTableImpl ti = new MutableTableImpl(columns);

            for(int i = 0; i < pageRowNums[nt]; i++) {
                char[][] row = df.getRowElements(i);
                curNum++;
                if(row != null)
                    for(int j = 0; j < columns.length; j++) {
/*                        char[] elem = row[colidx];
                        try {
                            ti.setChars(elem, ctr, colidx);
                        }
                        catch(NumberFormatException e) {
                            ti.setChars(Integer.toString(0).toCharArray(), ctr, colidx);
                        }
                */
                    boolean isMissing = true;
                    char[] elem = row[j];//(char[])row.get(j);

                    // test to see if this is '?'
                    // if it is, this value is missing.
                    for(int k = 0; k < elem.length; k++) {
                        if(elem[k] != QUESTION && elem[k] != SPACE) {
                            isMissing = false;
                            break;
                        }
                    }

                    // if the value was not missing, just put it in the table
                    if(!isMissing) {
                        try {
                            ti.setChars(elem, i, j);
                        }
                        // if there was a number format exception, set the value
                        // to 0 and mark it as missing
                        catch(NumberFormatException e) {
                            ti.setChars(Integer.toString(0).toCharArray(), i, j);
                            ti.setValueToMissing(true, i, j);
                        }
                    }
                    // if the value was missing..
                    else {
                        // put 0 in a numeric column and set the value to missing
                        if(ti.isColumnNumeric(j)) {
                            ti.setChars(Integer.toString(0).toCharArray(), i, j);
                            ti.setValueToMissing(true, i, j);
                        }
                        // otherwise put the '?' in the table and set the value to missing
                        else {
                            ti.setChars(elem, i, j);
                            ti.setValueToMissing(true, i, j);
                        }
                    }
                }
            }
            pt.addTable(ti, nt == 0);
        }

        // if types were not specified, we should try to convert to double columns
        // where appropriate
        if(!hasTypes) {
            // for each column
            for(int i = 0; i < numColumns; i++) {
                boolean isNumeric = true;
                double[] newCol = new double[numRows];

                // for each row
                for(int j = 0; j < numRows; j++) {
                    String s = pt.getString(j, i);
                    try {
                        double d = Double.parseDouble(s);
                        newCol[j] = d;
                    }
                    catch(NumberFormatException e) {
                        isNumeric = false;
                    }
                    if(!isNumeric)
                        break;
                }

                if(isNumeric) {
                    //DoubleColumn dc = new DoubleColumn(newCol);
                    String oldLabel = pt.getColumnLabel(i);
                    //dc.setLabel(pt.getColumnLabel(i));
                    pt.setColumn(newCol, i);
                    pt.setColumnLabel(oldLabel, i);
                }
            }
        }
        return pt;
    }

	/**
		returns a VT that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
		/
	private Table createBlanks(FlatFileReader ffr){
        /*int[][] blanks = ffr.getBlanks();

		IntColumn rowsColumn=new IntColumn(blanks[0]);
		rowsColumn.setLabel("Rows");

		IntColumn colsColumn=new IntColumn(blanks[1]);
		colsColumn.setLabel("Column");

		Column[] internal=new Column[2];
		internal[0]=rowsColumn;
		internal[1]=colsColumn;

		TableImpl table= (TableImpl)DefaultTableFactory.getInstance().createTable(internal);
		return table;
        return null;
	}*/
}