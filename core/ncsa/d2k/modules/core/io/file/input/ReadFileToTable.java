//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.io.*;
import java.util.*;

/**
 * Given a FlatFileReader, create a TableImpl initialized with its contents.
 */
public class ReadFileToTable extends InputModule {

    private boolean useBlanks = true;
    public void setUseBlanks(boolean b) {
        useBlanks = b;
    }
    public boolean getUseBlanks() {
        return useBlanks;
    }

    public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[1];
        retVal[0] = new PropertyDescription("useBlanks", "Set Blanks to be Missing Values",
            "When true, any blank entries in the file will be set as missing values in the table.");
        return retVal;
    }

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    public String getInputInfo(int i) {
        return "A FlatFileParser to read data from.";
    }

    public String getOutputInfo(int i) {
        switch(i) {
            case 0:
                return "A Table with the data from the file reader.";
            default:
                return "";
        }
    }

    public String getInputName(int i) {
        return "filereader";
    }

    public String getOutputName(int i) {
        switch(i) {
            case 0:
                return "table";
            default:
                return "";
        }
    }

    public String getModuleInfo() {
        return "Given a FlatFileReader, create a Table initialized with its contents.";
    }

    public String getModuleName() {
        return "ReadFileToTable";
    }

    public void doit() throws Exception {
        FlatFileParser fle = (FlatFileParser)pullInput(0);
        Table t = createTable(fle);
        //Table bt = createBlanks(fle);

        if(useBlanks) {
            // if a value was blank, make it a 'missing value' in the table
            MutableTable mt = (MutableTable)t;
            boolean[][] blanks = fle.getBlanks();
            int nr = t.getNumRows();
            int nc = t.getNumColumns();
            for(int i = 0; i < nr; i++) {
                for(int j = 0; j < nc; j++) {
                    if(blanks[i][j])
                        mt.setValueToMissing(i, j);
                }
            }
        }

        pushOutput(t, 0);
        //pushOutput(bt, 1);
    }

    private Table createTable(FlatFileParser df) {
        int numRows = df.getNumRows();
        int numColumns = df.getNumColumns();

        boolean hasTypes = false;

        Column[] columns = new Column[numColumns];
        for(int i = 0; i < columns.length; i++) {
            int type = df.getColumnType(i);

/*            // create the column
            if(type == ColumnTypes.STRING)
                columns[i] = new StringColumn(numRows);
            else if(type == ColumnTypes.DOUBLE)
                columns[i] = new DoubleColumn(numRows);
            else if(type == ColumnTypes.FLOAT)
                columns[i] = new FloatColumn(numRows);
            else if(type == ColumnTypes.INTEGER)
                columns[i] = new IntColumn(numRows);
            else if(type == ColumnTypes.SHORT)
                columns[i] = new ShortColumn(numRows);
            else if(type == ColumnTypes.LONG)
                columns[i] = new LongColumn(numRows);
            else if(type == ColumnTypes.CHAR_ARRAY)
                columns[i] = new CharArrayColumn(numRows);
            else if(type == ColumnTypes.BYTE_ARRAY)
                columns[i] = new ByteArrayColumn(numRows);
            else if(type == ColumnTypes.CHAR)
                columns[i] = new CharColumn(numRows);
            else if(type == ColumnTypes.BYTE)
                columns[i] = new ByteColumn(numRows);
            else if(type == ColumnTypes.BOOLEAN)
                columns[i] = new BooleanColumn(numRows);
            else
                columns[i] = new StringColumn(numRows);
            */
            columns[i] = ColumnUtilities.createColumn(type, numRows);

            if(type != -1)
                hasTypes = true;

            // set the label
            String label = df.getColumnLabel(i);
            if(label != null)
                columns[i].setLabel(label);
        }

        TableImpl ti = new TableImpl(columns);
        for(int i = 0; i < numRows; i++) {
            char[][] row = df.getRowElements(i);
            if(row != null) {
            for(int j = 0; j < columns.length; j++) {
                char[] elem = row[j];//(char[])row.get(j);
                //System.out.println(new String(elem));
                try {
                    ti.setChars(elem, i, j);
                }
                catch(NumberFormatException e) {
                    ti.setChars(Integer.toString(0).toCharArray(), i, j);
                }
            }
            }
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
                    String s = ti.getString(j, i);
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
                    DoubleColumn dc = new DoubleColumn(newCol);
                    dc.setLabel(ti.getColumnLabel(i));
                    ti.setColumn(dc, i);
                }
            }
        }

        // set the feature (nom-scalar) type
        /*for(int i = 0; i < numColumns; i++) {
            int featureType = df.getFeatureType(i);
            if(featureType != -1) {
                if(featureType == df.SCALAR)
                    columns[i].setIsScalar(true);
                else
                    columns[i].setIsNominal(true);
            }
        }*/

        // set the in-out types if specified
/*        boolean hasInOut = false;
        // set the data (in-out) type
        for(int i = 0; i < numColumns; i++) {
            if(df.getDataType(i) != -1) {
                hasInOut = true;
                break;
            }
        }

        if(hasInOut) {
            int[] ins = new int[0];
            int[] outs = new int[0];
            for(int i = 0; i < numColumns; i++) {
                int ty = df.getDataType(i);
                if(ty == df.IN) {
                    ins = expandArray(ins);
                    ins[ins.length-1] = ty;
                }
                else if(ty == df.OUT) {
                    outs = expandArray(outs);
                    outs[outs.length-1] = ty;
                }
            }

            ExampleTable et = ti.toExampleTable();
            et.setInputFeatures(ins);
            et.setOutputFeatures(outs);
            return et;
        }
        */

        return ti;
    }

    /**
     * Return an int array of size orig.length+1 and copy the items of
     * orig into the new array.
     /
    private int[] expandArray(int[] orig) {
        int[] retVal = new int[orig.length+1];
        for(int i = 0; i < orig.length; i++)
            retVal[i] = orig[i];
        return retVal;
    }*/

	/**
		returns a VT that has 2 columns, corresponding
		to the row and column indices of the fields
		that were blank in the file that was read in
		/
	private Table createBlanks(FlatFileReader ffr){
        int[][] blanks = ffr.getBlanks();

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