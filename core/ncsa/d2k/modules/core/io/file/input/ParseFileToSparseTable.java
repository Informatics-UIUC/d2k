package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

/**
 * Given a FlatFileReader, create a TableImpl initialized with its contents.
 */
public class ParseFileToSparseTable extends ParseFileToTable {

    /*private boolean useBlanks = true;
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
    }*/

    /*public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }*/

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    /*public String getInputInfo(int i) {
        return "A FlatFileParser to read data from.";
    }*/

    /*public String getOutputInfo(int i) {
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
    }*/

    public String getModuleName() {
        return "Parse File to Sparse Table";
    }

    /*public void doit() throws Exception {
        FlatFileParser fle = (FlatFileParser)pullInput(0);
        Table t = createTable(fle);
        //Table bt = createBlanks(fle);

        if(getUseBlanks()) {
            // if a value was blank, make it a 'missing value' in the table
            MutableTable mt = (MutableTable)t;
            boolean[][] blanks = fle.getBlanks();
            int nr = t.getNumRows();
            int nc = t.getNumColumns();
            for(int i = 0; i < nr; i++) {
                for(int j = 0; j < nc; j++) {
                    if(blanks[i][j]) {
                        mt.setValueToMissing(true, i, j);
                    }
                }
            }
        }

        pushOutput(t, 0);
        //pushOutput(bt, 1);
    }*/

    protected Table createTable(FlatFileParser df) {
        int numRows = df.getNumRows();
        int numColumns = df.getNumColumns();

        boolean hasTypes = false;

        Column[] columns = new Column[numColumns];
        for(int i = 0; i < columns.length; i++) {
            int type = df.getColumnType(i);

            // create the column
            if(type == ColumnTypes.STRING)
                columns[i] = new SparseStringColumn(numRows);
            else if(type == ColumnTypes.DOUBLE)
                columns[i] = new SparseDoubleColumn(numRows);
            else if(type == ColumnTypes.FLOAT)
                columns[i] = new SparseFloatColumn(numRows);
            else if(type == ColumnTypes.INTEGER)
                columns[i] = new SparseIntColumn(numRows);
            else if(type == ColumnTypes.SHORT)
                columns[i] = new SparseShortColumn(numRows);
            else if(type == ColumnTypes.LONG)
                columns[i] = new SparseLongColumn(numRows);
            else if(type == ColumnTypes.CHAR_ARRAY)
                columns[i] = new SparseCharArrayColumn(numRows);
            else if(type == ColumnTypes.BYTE_ARRAY)
                columns[i] = new SparseByteArrayColumn(numRows);
            else if(type == ColumnTypes.CHAR)
                columns[i] = new SparseCharColumn(numRows);
            else if(type == ColumnTypes.BYTE)
                columns[i] = new SparseByteColumn(numRows);
            else if(type == ColumnTypes.BOOLEAN)
                columns[i] = new SparseBooleanColumn(numRows);
            else
                columns[i] = new SparseStringColumn(numRows);

//            columns[i] = ColumnUtilities.createColumn(type, numRows);

            if(type != -1)
                hasTypes = true;

            // set the label
            String label = df.getColumnLabel(i);
            //System.out.println(i+" "+label);
            if(label != null)
                columns[i].setLabel(label);
        }

        //MutableTableImpl ti = new MutableTableImpl(columns);
        SparseMutableTable ti = new SparseMutableTable(numRows, 0);
        for(int i = 0; i < columns.length; i++)
            ti.addColumn((AbstractSparseColumn)columns[i]);
        for(int i = 0; i < numRows; i++) {
            ParsedLine pl = df.getRowElements(i);
//            char[][] row = df.getRowElements(i);
            char[][] row = pl.elements;
            boolean[] bl = pl.blanks;
            if(row != null) {
                for(int j = 0; j < columns.length; j++) {

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
                    if(!isMissing && !bl[j]) {
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

/*                char[] elem = row[j];//(char[])row.get(j);
                //System.out.println(new String(elem));
                if(elem.length > 0) {
                    try {
                        ti.setChars(elem, i, j);
                    }
                    catch(NumberFormatException e) {
                        ti.setChars(Integer.toString(0).toCharArray(), i, j);
                    }
                }*/
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
                    //SparseDoubleColumn dc = new SparseDoubleColumn(newCol);
                    //dc.setLabel(ti.getColumnLabel(i));
                    String oldlbl = ti.getColumnLabel(i);
                    ti.setColumn(newCol, i);
                    ti.setColumnLabel(/*ti.getColumnLabel(i)*/oldlbl, i);
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