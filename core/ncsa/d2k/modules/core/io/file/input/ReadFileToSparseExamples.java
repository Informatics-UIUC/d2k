package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

public class ReadFileToSparseExamples extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser",
            "java.lang.Object"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.sparse.examples.SparseExample"};
        return out;
    }

    public String getInputInfo(int i) {
        if(i == 0)
            return "File Parser";
        else
            return "Trigger object.";
    }

    public String getOutputInfo(int i) {
        return "The ith Example";
    }

    public String getModuleInfo() {
        return "Read i examples from a file.  The examples are read in only "+
                "when the trigger object has arrived.";
    }

    public boolean isReady() {
        if(firstTime) {
            return getInputPipeSize(0) > 0;
        }
        else {
            return getInputPipeSize(1) > 0 && curExample <= totalNumExamples;
        }
    }


    public void beginExecution() {
        firstTime = true;
        rdr = null;
        totalNumExamples = 0;
        curExample = 0;
        numCol = 0;
        types = null;
        labels = null;
    }

    private boolean firstTime;
    private FlatFileParser rdr;
    private int totalNumExamples;
    private int curExample;
    private int numCol;
    private int[] types;
    private String[] labels;

    public void doit() {
        if(firstTime) {
            firstTime = false;
            // pull in the file reader
            rdr = (FlatFileParser)pullInput(0);

            // get the total number of examples
            totalNumExamples = rdr.getNumRows();
            // get the number of columns
            numCol = rdr.getNumColumns();
            // the current example
            curExample = 0;

            // the data types
            types = new int[numCol];
            // the labels
            labels = new String[numCol];
            // get the data types and labels
            for(int i = 0; i < numCol; i++) {
                types[i] = rdr.getColumnType(i);
                labels[i] = rdr.getColumnLabel(i);
            }
            // read the 0th example
            Example e = readExample(curExample);
            curExample++;
            // push it out
            pushOutput(e, 0);
        }
        else {
            // read the ith example
            Example e = readExample(curExample);
            curExample++;
            // push it out
            pushOutput(e, 0);
        }
    }

    private Example readExample(int row) {
        SparseMutableTable smt = new SparseMutableTable();

        Column[] columns = new Column[numCol];
        for(int i = 0; i < columns.length; i++) {
            int type = rdr.getColumnType(i);

            // create the column
            if(type == ColumnTypes.STRING)
                columns[i] = new SparseStringColumn(1);
            else if(type == ColumnTypes.DOUBLE)
                columns[i] = new SparseDoubleColumn(1);
            else if(type == ColumnTypes.FLOAT)
                columns[i] = new SparseFloatColumn(1);
            else if(type == ColumnTypes.INTEGER)
                columns[i] = new SparseIntColumn(1);
            else if(type == ColumnTypes.SHORT)
                columns[i] = new SparseShortColumn(1);
            else if(type == ColumnTypes.LONG)
                columns[i] = new SparseLongColumn(1);
            else if(type == ColumnTypes.CHAR_ARRAY)
                columns[i] = new SparseCharArrayColumn(1);
            else if(type == ColumnTypes.BYTE_ARRAY)
                columns[i] = new SparseByteArrayColumn(1);
            else if(type == ColumnTypes.CHAR)
                columns[i] = new SparseCharColumn(1);
            else if(type == ColumnTypes.BYTE)
                columns[i] = new SparseByteColumn(1);
            else if(type == ColumnTypes.BOOLEAN)
                columns[i] = new SparseBooleanColumn(1);
            else
                columns[i] = new SparseStringColumn(1);

            // set the label
            String label = rdr.getColumnLabel(i);
            //System.out.println(i+" "+label);
            if(label != null)
                columns[i].setLabel(label);
        }

        //MutableTableImpl ti = new MutableTableImpl(columns);
        SparseMutableTable ti = new SparseMutableTable(1, 0);
        for(int i = 0; i < columns.length; i++)
            ti.addColumn((AbstractSparseColumn)columns[i]);
        char[][] data = rdr.getRowElements(row);
        if(data != null) {
            for(int j = 0; j < columns.length; j++) {
                char[] elem = data[j];//(char[])row.get(j);
                //System.out.println(new String(elem));
                if(elem.length > 0) {
                    try {
                        ti.setChars(elem, 0, j);
                    }
                    catch(NumberFormatException e) {
                        ti.setChars(Integer.toString(0).toCharArray(), 0, j);
                    }
                }
            }
        }

        ExampleTable et = ti.toExampleTable();
        et.setInputFeatures(new int[0]);
        et.setOutputFeatures(new int[0]);
        et.setTestingSet(new int[0]);
        et.setTrainingSet(new int[0]);
        Example e = et.getExample(0);
        return e;
    }
}
