package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Read a file into an ADTree.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author clutter
 * @version 1.0
 */
public class ParseFileToADTree extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }

    public String getInputInfo(int i) {
        return "The FlatFileParser to read data from.";
    }

    public String getInputName(int i) {
        return "File Parser";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.ADTree",
            "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
        return out;
    }

    public String getOutputName(int i) {
        if(i == 0)
            return "ADTree";
        else
            return "Example Table";
    }

    public String getOutputInfo(int i) {
        if(i == 0)
            return "An ADTree.";
        else
            return "ExampleTable containing meta data.";
    }

    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("Reads a file into an ADTree index structure ");
        sb.append("<p>Detailed Description: ");
        sb.append("Given a FlatFileParser, reads the data and stores all counts ");
        sb.append("into an ADTree. ");
        sb.append("An ExampleTable that contains the meta data for the ADTree ");
        sb.append("is also created.");
        return sb.toString();
    }

    public String getModuleName() {
        return "Create an ADTree";
    }

    public void doit() throws Exception {
        FlatFileParser ffr = (FlatFileParser)pullInput(0);

        // make the meta table
        int numRows = ffr.getNumRows();
        Column[] cols = new Column[ffr.getNumColumns()];
        for(int i = 0; i < cols.length; i++) {
            int typ = ffr.getColumnType(i);

            // create the column
            if(typ == ColumnTypes.STRING)
                cols[i] = new StringColumn(numRows);
            else if(typ == ColumnTypes.DOUBLE)
                //cols[i] = new DoubleColumn(numRows);
                          throw new Exception ( "Cannot build ADTree for continuous data in column " + i);
            else if(typ == ColumnTypes.FLOAT)
                //cols[i] = new FloatColumn(numRows);
			throw new Exception ( "Cannot build ADTree for continuous data in column " + i);
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


				// create the ADTree
        ADTree adt = new ADTree(ffr.getNumColumns());
            //there are two levels of debug for an ADTree; set the first one only
            adt.setDebug1(debug);
          for(int i = 0; i < ffr.getNumColumns(); i++)
            adt.setLabel(i+1, ffr.getColumnLabel(i));
        for(int i = 0; i < ffr.getNumRows(); i++) {
            String[] vals = new String[ffr.getNumColumns()];
            //char[][] row = ffr.getRowElements(i);
            ParsedLine pl = ffr.getRowElements(i);
            char[][] row = pl.elements;
            for(int j = 0; j < row.length; j++)
                vals[j] = new String(row[j]);
             //System.out.println("countingLine for " + i + ": " +  vals.toString());
            adt.countLine(adt, vals);
        }


        pushOutput(adt, 0);
        pushOutput(meta, 1);
    }

      boolean debug = false;


      /**
       Set the value for the debug variable
       @param b boolean value for the debug variable
    */
      public void setDebug(boolean b){
        debug=b;
     }

      /**
       Get the debug setting
       @return The boolean value of debug
    */

      public boolean getDebug(){
        return debug;
     }

     public PropertyDescription[] getPropertiesDescriptions(){
      PropertyDescription[] pd = new PropertyDescription[1] ;
      pd[0] = new PropertyDescription("debug", "Debug Mode",
            "This property controls the first debug level of the ADTree. setting "  +
            "it to true will generate more output to stdout while building the ADTree.");
      return pd;
     }

}
// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/17/03 - Anca started QA process.  Updated module info and added
//           exception handling for numeric/continuous data.
//           FEATURE REQUEST: a module that will create an ADTree
//           from selected columns only, either from a table or from a file..
// 2/18/03 - checked into basic.
// 2/21/03 - vered started QA second test.
//           added getPropertiesDescriptions method.
// END QA Comments

