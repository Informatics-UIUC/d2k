/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import java.sql.*;
import java.io.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	CountResults.java

*/
public class CountResults extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the database connection   ";
			case 1: return "      This is the name of the database table to read from.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This is a count of the number of hits against the database.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"java.lang.Integer"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will count the number of entries in a database table.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();

		String tableList = (String) this.pullInput (1);
		System.out.println ("--- "+tableList);
		String query = "SELECT FROM "+tableList;

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int count = 0;
		while (rs.next ())
			count++;
		System.out.println ("Count was "+count);
		this.pushOutput (new Integer (count), 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Count Hits";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Connection";
			case 1:
				return "Table";
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
				return "Hits";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

