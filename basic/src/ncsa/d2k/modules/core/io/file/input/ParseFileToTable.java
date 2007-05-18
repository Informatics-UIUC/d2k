package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.BasicTableFactory;
import ncsa.d2k.modules.core.util.*;

/**
 * Given a FlatFileReader, create a TableImpl initialized with its contents.
 */
public class ParseFileToTable extends InputModule {

    protected static final char QUESTION = '?';
    protected static final char SPACE = ' ';

    public final static int IN_FACTORY = 1;
    public final static int IN_PARSER = 0;


    private boolean useBlanks = true;
    public void setUseBlanks(boolean b) {
        useBlanks = b;
    }
    public boolean getUseBlanks() {
        return useBlanks;
    }
    
    private D2KModuleLogger myLogger =
        D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
    public void beginExecution() {
    	myLogger.setLoggingLevel(moduleLoggingLevel);
    }

    private int moduleLoggingLevel=
    	D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
    	.getLoggingLevel();

    public int getmoduleLoggingLevel(){
    	return moduleLoggingLevel;
    }

    public void setmoduleLoggingLevel(int level){
    	moduleLoggingLevel = level;
    }
    
    /**
 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
 *
 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
 */
public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[2];
        retVal[0] = new PropertyDescription("useBlanks", "Set Blanks to be Missing Values",
            "When true, any blank entries in the file will be set as missing values in the table.");
        
        retVal[1] = 
            new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
            "The logging level of this modules"+"\n 0=DEBUG; 1=INFO; 2=WARN; 3=ERROR; 4=FATAL; 5=OFF");
        return retVal;
    }

    public String[] getInputTypes() {
      String[] in = new String[2];
      in[IN_FACTORY] = "ncsa.d2k.modules.core.datatype.table.TableFactory";
      in[IN_PARSER] = "ncsa.d2k.modules.core.io.file.input.FlatFileParser";

      return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int i) {
  switch (i) {
    case IN_FACTORY:
      return "An optional TableFactory to control the type of table created.  If not used, a basic table will be created.";
    case IN_PARSER:
      return "A FlatFileParser to read data from.";
    default:
      return "no such input";
  }
    }

    /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int i) {
        switch(i) {
            case 0:
                return "A Table with the data from the file reader.";
            default:
                return "";
        }
    }

    /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int i) {
  switch (i) {
    case IN_FACTORY:
      return "Table Factory";
    case IN_PARSER:
      return "File Parser";
    default:
      return "no such input";
  }

    }

    /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int i) {
        switch(i) {
            case 0:
                return "Table";
            default:
                return "";
        }
    }

    /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
        // return "Given a FlatFileReader, create a Table initialized with its contents.";
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("Given a FlatFileParser, this module creates a Table ");
        sb.append("initialized with the contents of a flat file from disk.");
        sb.append("<p>");
        sb.append("The type of Table created can be customized by passing a TableFactory to ");
        sb.append("this module's second input.  If the input is not connected, a BasicTableFactory will be used.");
        sb.append("</p>");
        return sb.toString();
    }

    /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
        return "Parse File To Table";
    }

    /**
 * Called by the D2K Infrastructure to determine if the module is ready to run.
 *
 * @return Whether or not the module is ready to run.
 */
public boolean isReady() {
        if(isInputPipeConnected(1))
                return super.isReady();
        else
            return getInputPipeSize(0) > 0;
    }

    /**
 * Performs the main work of the module.
 */
public void doit() throws Exception {
        FlatFileParser fle = (FlatFileParser)pullInput(0);
        
        myLogger.setLoggingLevel(moduleLoggingLevel);
        
        TableFactory tf = null;
        if(isInputPipeConnected(1))
            tf = (TableFactory)pullInput(1);
        else
            tf = new BasicTableFactory();

        Table t = createTable(fle, tf);
        pushOutput(t, 0);
    }

    public Table createTable(FlatFileParser df, TableFactory tf) {
        int numRows = df.getNumRows();
        int numColumns = df.getNumColumns();

        boolean hasTypes = false;

        MutableTable ti = (MutableTable)tf.createTable();

        //Column[] columns = new Column[numColumns];
        for(int i = 0; i < numColumns; i++) {
            int type = df.getColumnType(i);
            //columns[i] = ColumnUtilities.createColumn(type, numRows);
            Column c = tf.createColumn(type);
            c.setNumRows(numRows);

            if(type != -1)
                hasTypes = true;

            // set the label
            String label = df.getColumnLabel(i);
            if(label != null)
                c.setLabel(label);

            ti.addColumn(c);
        }

        //MutableTableImpl ti = new MutableTableImpl(columns);


         for(int i = 0; i < numRows; i++) {
            ParsedLine pl = df.getRowElements(i);
            char[][] row = pl.elements;
            boolean[] blanks = pl.blanks;
            if(row != null) {
                for(int j = 0; j < numColumns; j++) {
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
                    if(!isMissing && !blanks[j]) {
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
						ti.setValueToMissing(true, i, j);
                        switch (df.getColumnType(j)) {
                        	case ColumnTypes.INTEGER:
                        	case ColumnTypes.SHORT:
                        	case ColumnTypes.LONG:
                        		ti.setInt(ti.getMissingInt(), i, j);
                        		break;
							case ColumnTypes.DOUBLE:
							case ColumnTypes.FLOAT:
								ti.setDouble(ti.getMissingDouble(), i, j);
								break;
							case ColumnTypes.CHAR_ARRAY:
								ti.setChars(ti.getMissingChars(), i, j);
								break;
							case ColumnTypes.BYTE_ARRAY:
								ti.setBytes(ti.getMissingBytes(), i, j);
								break;
							case ColumnTypes.BYTE:
								ti.setByte(ti.getMissingByte(), i, j);
								break;
							case ColumnTypes.CHAR:
								ti.setChar(ti.getMissingChar(), i, j);
								break;
							case ColumnTypes.STRING:
								ti.setString(ti.getMissingString(), i, j);
								break;
							case ColumnTypes.BOOLEAN:
								ti.setBoolean(ti.getMissingBoolean(), i, j);
								break;
	                     }
                    }
               }
            }
        }

        // if types were not specified, we should try to convert to double columns
        // where appropriate
        if(!hasTypes) {

           // System.out.println("no types");

            // for each column
            for(int i = 0; i < numColumns; i++) {
                boolean isNumeric = true;
                double[] newCol = new double[numRows];

                // for each row
                for(int j = 0; j < numRows; j++) {
                    String s = ti.getString(j, i);

                    if (ti.isValueMissing(j, i) || ti.isValueEmpty(j, i))
                       continue;

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
                    Column dc = tf.createColumn(ColumnTypes.DOUBLE);
                    dc.setNumRows(ti.getNumRows());
                    dc.setLabel(ti.getColumnLabel(i));

                    for (int k = 0; k < ti.getNumRows(); k++) {
                       dc.setDouble(newCol[k], k);
                       if (ti.isValueMissing(k, i))
                          dc.setValueToMissing(true, k);
                       if (ti.isValueEmpty(k, i))
                          dc.setValueToEmpty(true, k);
                    }

                    ti.setColumn(dc, i);
                }
            }
        }
        return ti;
    }
}
