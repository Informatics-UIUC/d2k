package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class ReadFileToADTree extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }

    public String getInputInfo(int i) {
        return "The FlatFileParser to read data from.";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.ADTree", "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
        return out;
    }

    public String getOutputInfo(int i) {
        if(i == 0)
            return "An ADTree.";
        else
            return "ExampleTable containing meta data.";
    }

    public String getModuleInfo() {
        return "Read a file into an ADTree.";
    }

    public void doit() {
        FlatFileParser ffr = (FlatFileParser)pullInput(0);
        ADTree adt = new ADTree(ffr.getNumColumns());
        for(int i = 0; i < ffr.getNumColumns(); i++)
            adt.setLabel(i+1, ffr.getColumnLabel(i));
        for(int i = 0; i < ffr.getNumRows(); i++) {
            String[] vals = new String[ffr.getNumColumns()];
            char[][] row = ffr.getRowElements(i);
            for(int j = 0; j < row.length; j++)
                vals[j] = new String(row[j]);
            adt.countLine(adt, vals);
        }

        // make the meta table
        int numRows = ffr.getNumRows();
        Column[] cols = new Column[ffr.getNumColumns()];
        for(int i = 0; i < cols.length; i++) {
            int typ = ffr.getColumnType(i);

            // create the column
            if(typ == ColumnTypes.STRING)
                cols[i] = new StringColumn(numRows);
            else if(typ == ColumnTypes.DOUBLE)
                cols[i] = new DoubleColumn(numRows);
            else if(typ == ColumnTypes.FLOAT)
                cols[i] = new FloatColumn(numRows);
            else if(typ == ColumnTypes.INTEGER)
                cols[i] = new IntColumn(numRows);
            else if(typ == ColumnTypes.SHORT)
                cols[i] = new ShortColumn(numRows);
            else if(typ == ColumnTypes.LONG)
                cols[i] = new LongColumn(numRows);
            else if(typ == ColumnTypes.CHAR_ARRAY)
                cols[i] = new CharArrayColumn(numRows);
            else if(typ == ColumnTypes.BYTE_ARRAY)
                cols[i] = new ByteArrayColumn(numRows);
            else if(typ == ColumnTypes.CHAR)
                cols[i] = new CharColumn(numRows);
            else if(typ == ColumnTypes.BYTE)
                cols[i] = new ByteColumn(numRows);
            else if(typ == ColumnTypes.BOOLEAN)
                cols[i] = new BooleanColumn(numRows);
            else
                cols[i] = new StringColumn(numRows);

            String lbl = ffr.getColumnLabel(i);
            if(lbl != null)
                cols[i].setLabel(lbl);
            else
                cols[i].setLabel(Integer.toString(i));
        }

        DefaultTableFactory  dtf = DefaultTableFactory.getInstance();
        Table table = dtf.createTable(cols);
  	    ExampleTable meta = dtf.createExampleTable(table);

        pushOutput(adt, 0);
        pushOutput(meta, 1);
    }
}