package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;

public class AvailableFieldsInput extends InputModule
{
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Pass this database connection to the next module.";
			case 1: return "This is a list of the fields available in the specified table.";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "The database connection.";
			case 1: return "The selected table name.";
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
          String s = "<p> Overview: ";
          s += "This module displays the list of available fields in the selected table. </p>";
          s += "<p> Detailed Description: ";
          s += "This module makes a connection to a database and retrieves the ";
          s += "list of available fields in the selected table. </p>";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle databases.";

          return s;
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.io.input.sql.ConnectionWrapper","java.lang.String"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.util.Vector"};
		return types;
	}

	public void doit () throws Exception
	{
		String tableName = (String) this.pullInput (1);
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		Vector v = new Vector();
		Vector c = new Vector();
		DatabaseMetaData dbmd = con.getMetaData();
		System.out.println("AvailableFieldsInput - tableName :" + tableName);
		ResultSet tableSet = dbmd.getColumns (null,null,tableName,"%");
		while (tableSet.next())
			v.addElement(tableSet.getString("COLUMN_NAME"));

		this.pushOutput (cw, 0);
		this.pushOutput (v, 1);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "AvailableFieldsInput";
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
				return "Selected Table";
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
				return "Database Connection";
			case 1:
				return "Fields List";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

// QA Comments
// 2/18/03 - Handed off to QA by Dora Cai
// 2/19/03 - Anca started QA process.  Corrected inputInfo for Selected Table.
//           Changed output name form Fields to Fields List to maintain consistent
//           naming scheme with Tables List.
// 2/19/03 - Very clean and well documented. checked into basic.
// END QA Comments
