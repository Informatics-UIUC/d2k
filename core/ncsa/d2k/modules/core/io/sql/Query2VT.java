package ncsa.d2k.modules.core.io.sql;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;


/**
	Query2VT.java

*/
public class Query2VT extends ncsa.d2k.infrastructure.modules.DataPrepModule

{

    /**
       This method returns the description of the various inputs.
       @return the description of the indexed input.
    */
    public String getInputInfo(int index) {

	switch (index) {
	case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Connection Wrapper\">    <Text>This manages the sql database connection object. </Text>  </Info></D2K>";
	case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table Name\">    <Text>The name of the table containing the fields. </Text>  </Info></D2K>";
	case 2: return "example table contaning metadata";
	default: return "No such input";
	}
    }

    /**
       This method returns an array of strings that contains the data types for the inputs.
       @return the data types of all inputs.
    */
    public String[] getInputTypes () {
	String [] types =  {
	    "ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
	    "java.lang.String",
	    "ncsa.d2k.util.datatype.Table"};
	return types;
    }

    /**
       This method returns the description of the outputs.
       @return the description of the indexed output.
    */
    public String getOutputInfo (int index) {
	switch (index) {
	case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Resulting Table\">    <Text>This table object contains the results when we are completely done reading. </Text>  </Info></D2K>";
	default: return "No such output";
	}

    }

    /**
       This method returns an array of strings that contains the data types for the outputs.
       @return the data types of all outputs.
    */
    public String[] getOutputTypes () {
	String [] types =  {
	    "ncsa.d2k.util.datatype.Table"};
	return types;

    }

    /**
       This method returns the description of the module.
       @return the description of the module.
    */
    public String getModuleInfo () {
	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Read Table\">    <Text>This method given the array of fields name, a jdbc connection wrapper, the table name and the where clause (or null) for the query, will populate the table with the results. </Text>  </Info></D2K>";

	}

    /**
       PUT YOUR CODE HERE.
    */
    public void doit () throws Exception {

	// We need a connection wrapper
	ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
	Connection con = cw.getConnection();

	// get the name of the table.
	String tableName = (String) this.pullInput (1);

	// get the example table containing metadata
	ExampleTable vt = (ExampleTable) this.pullInput(2);

	////////////////////////////
	// Get the number of entries in the table.
	String query = "SELECT COUNT(*) FROM "+tableName;
	Statement stmt = con.createStatement();
	ResultSet rs = stmt.executeQuery(query);
	int count = 0;
	while (rs.next ())
	    count = rs.getInt (1);
	System.out.println ("---- Entries - "+count);

	///////////////////////////
	// Get the column types, and create the appropriate column
	// objects

	// construct the query to get the column metadata.
	query = "SELECT * FROM " + tableName;

	// Get the number of columns selected.
	stmt = con.createStatement();
	rs = stmt.executeQuery(query);
	ResultSetMetaData rsmd = rs.getMetaData ();
	//int numColumns = rsmd.getColumnCount ();
	//VerticalTable vt = new VerticalTable (numColumns);

	int numColumns = vt.getNumColumns();

	// Now compile a list of the datatypes.
	int [] types = new int [numColumns];
	String [] columnNames = new String [numColumns];
	for (int i = 0 ; i < numColumns ; i++) {
	    types[i] = rsmd.getColumnType (i+1);
	    columnNames[i] = rsmd.getColumnLabel(i+1);
	    switch (types[i]) {
	    case Types.TINYINT:
	    case Types.SMALLINT:
		ShortColumn intSC = new ShortColumn (count);
		intSC.setLabel (columnNames[i]);
		vt.setColumn (intSC, i);
		break;
	    case Types.INTEGER:
	    case Types.BIGINT:
		IntColumn intC = new IntColumn (count);
		intC.setLabel (columnNames[i]);
		vt.setColumn (intC, i);
		break;
	    case Types.DOUBLE:
		DoubleColumn doubleC = new DoubleColumn (count);
		doubleC.setLabel (columnNames[i]);
		vt.setColumn (doubleC, i);
		break;
	    case Types.NUMERIC:
	    case Types.DECIMAL:
	    case Types.FLOAT:
	    case Types.REAL:
		FloatColumn floatC = new FloatColumn (count);
		floatC.setLabel (columnNames[i]);
		vt.setColumn (floatC, i);
		break;
	    case Types.CHAR:
	    case Types.VARCHAR:
	    case Types.LONGVARCHAR:
		ByteArrayColumn byteC = new ByteArrayColumn (count);
		byteC.setLabel (columnNames[i]);
		vt.setColumn (byteC, i);
		break;
	    default:
		System.out.println ("Data type not known:"+types[i]);
	    }
	}



	// Now populate the table.
	for (int where = 0; rs.next (); where++)
	    for (int i = 0 ; i < numColumns ; i++)
		switch (types[i]) {
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		    vt.setInt (rs.getInt (i+1), where, i);
		    break;
		case Types.DOUBLE:
		    vt.setDouble (rs.getDouble (i+1), where, i);
		    break;
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
		    vt.setFloat (rs.getFloat (i+1), where, i);
		    break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		    vt.setBytes (rs.getBytes (i+1), where, i);
		    break;
		}
	for (int i = 0 ; i < numColumns; i++)
	    System.out.println (vt.getColumnLabel (i));
	this.pushOutput (vt, 0);
	System.out.println("vt pushed ");
    }
}













