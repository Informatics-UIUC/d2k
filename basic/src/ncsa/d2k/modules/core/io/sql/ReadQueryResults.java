/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.sql.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ReadQueryResults.java
*/
//  Modified by Dora Cai on 02/13/03: correct SQL syntax error, add codes to
//  handle null value in Varchar fields.


public class ReadQueryResults extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This manages the sql database connection object.   ";
			case 1: return "      The names of the fields needed from within the table.   ";
			case 2: return "      The name of the table containing the fields.   ";
			case 3: return "      Contains the where clause for the sq1 query.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","[Ljava.lang.String;","java.lang.String","java.lang.String"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This table object contains the results when we are completely done reading.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module constructs a SQL statement, and retrieves data from a database. </p>";
          s += "<p>Detailed Description: ";
          s += "This module constructs a SQL query based on 4 inputs: the database ";
          s += "connection object, the selected table, the selected fields, and ";
          s += "the query condition. This module then executes the query and retrieve ";
          s += "the data from the specified database. This module can be used to display ";
          s += "database data, or to prepare the database data set for feeding into ";
          s += "mining processes. </p>";
          s += "<p>Restrictions: ";
          s += "We currently only support Oracle, SqlServer, DB2 and MySql databases. </p>";
          return s;
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
                // get the query condition.
                String whereClause = (String) this.pullInput (3);



		////////////////////////////
		// Get the number of entries in the table.
		String query = "SELECT COUNT(*) FROM "+tableList;
                if (whereClause.length() > 0)
                   query += " WHERE " + whereClause;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int count = 0;
		while (rs.next ())
			count = rs.getInt (1);
System.out.println ("---- Entries - "+count);

		///////////////////////////
		// Get the column types, and create the appropriate column
		// objects

		// construct the query to get clumn information.
		query = "SELECT "+fieldList.toString()+" FROM "+tableList;

                if (whereClause.length() > 0)
                   query += " WHERE " + whereClause;

		// Get the number of columns selected.
		stmt = con.createStatement();
		rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numColumns = rsmd.getColumnCount ();
		MutableTableImpl vt =  new MutableTableImpl(numColumns);

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
					StringColumn byteC = new StringColumn (count);
                                        byteC.setLabel (fieldArray[i]);
					vt.setColumn (byteC, i);
					break;
				default:
                                        byteC = new StringColumn (count);
                                        byteC.setLabel (fieldArray[i]);
                                        vt.setColumn (byteC, i);
                                        break;
			}
		}

		//////////////////////////////////////
		// Now fill in the data
		// construct the query to get the column metadata.
		query = "SELECT "+fieldList.toString()+" FROM "+tableList;
                if (whereClause.length() > 0)
			query += " WHERE "+whereClause;

		// Now populate the table.
		for (int where = 0; rs.next (); where++)
			for (int i = 0 ; i < numColumns ; i++) {
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
                                                if (rs.getString(i+1) == null) {
                                                  vt.setString(" ", where, i);
                                                }
                                                else
						  vt.setString (rs.getString (i+1), where, i);
						break;
                                        default:
                                                vt.setString(" ", where, i);
                                                break;
				}
                              }
		this.pushOutput (vt, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Read Query Results";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Database Connection";
			case 1:
				return "Selected Fields";
			case 2:
				return "Selected Table";
			case 3:
				return "Query Condition";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Resulting Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

