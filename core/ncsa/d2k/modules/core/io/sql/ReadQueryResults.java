/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ReadQueryResults.java

*/
public class ReadQueryResults extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Connection Wrapper\">    <Text>This manages the sql database connection object. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Field Names\">    <Text>The names of the fields needed from within the table. </Text>  </Info></D2K>";
			case 2: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table Name\">    <Text>The name of the table containing the fields. </Text>  </Info></D2K>";
			case 3: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Where Clause\">    <Text>Contains the where clause for the sq1 query. </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			"[Ljava.lang.String;",
			"java.lang.String",
			"java.lang.String"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Resulting Table\">    <Text>This table object contains the results when we are completely done reading. </Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.util.datatype.Table"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Read Table\">    <Text>This method given the array of fields name, a jdbc connection wrapper, the table name and the where clause (or null) for the query, will fully populate the table. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {

		// We need a connection wrapper
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		String[] fieldArray = (String[]) this.pullInput (1);

		// get the list of fields to read.
		StringBuffer fieldList = new StringBuffer(fieldArray[0]);
		for (int i=1; i<fieldArray.length; i++)
			fieldList.append(", "+fieldArray[i]);

		// get the name of the table.
		String tableList = (String) this.pullInput (2);

		////////////////////////////
		// Get the number of entries in the table.
		String query = "SELECT COUNT("+fieldArray[0]+") FROM "+tableList;
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
		query = "SELECT "+fieldList.toString()+" FROM "+tableList;
		String whereClause = (String) this.pullInput (3);
		if (whereClause.length() == 0)
			whereClause = null;
		if (whereClause != null)
			query += " WHERE "+whereClause+" DESC";

		// Get the number of columns selected.
		stmt = con.createStatement();
		rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numColumns = rsmd.getColumnCount ();
		VerticalTable vt = new VerticalTable (numColumns);

		// Now compile a list of the datatypes.
		int [] types = new int [numColumns];
		for (int i = 0 ; i < numColumns ; i++) {
			types[i] = rsmd.getColumnType (i+1);
			switch (types[i]) {
				case Types.TINYINT:
				case Types.SMALLINT:
					ShortColumn intSC = new ShortColumn (count);
					intSC.setLabel (fieldArray[i]);
					vt.setColumn (intSC, i);
					break;
				case Types.INTEGER:
				case Types.BIGINT:
					IntColumn intC = new IntColumn (count);
					intC.setLabel (fieldArray[i]);
					vt.setColumn (intC, i);
					break;
				case Types.DOUBLE:
					DoubleColumn doubleC = new DoubleColumn (count);
					doubleC.setLabel (fieldArray[i]);
					vt.setColumn (doubleC, i);
					break;
				case Types.NUMERIC:
				case Types.DECIMAL:
				case Types.FLOAT:
				case Types.REAL:
					FloatColumn floatC = new FloatColumn (count);
					floatC.setLabel (fieldArray[i]);
					vt.setColumn (floatC, i);
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					ByteArrayColumn byteC = new ByteArrayColumn (count);
					byteC.setLabel (fieldArray[i]);
					vt.setColumn (byteC, i);
					break;
				default:
					System.out.println ("Data type not known:"+types[i]);
			}
		}

		//////////////////////////////////////
		// Now fill in the data
		// construct the query to get the column metadata.
		query = "SELECT "+fieldList.toString()+" FROM "+tableList;
		if (whereClause != null)
			query += " WHERE "+whereClause;

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
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

