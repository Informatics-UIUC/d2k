package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.InputModule;

import java.sql.*;
import java.util.Vector;

public class AvailableFieldsInput extends InputModule
{
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
			"Pass this on to the next module that needs a connection to this data source.",
			"This is a list of the fields available in the specified table."
		};
		return outputDescriptions[index];
	}

	public String getInputInfo (int index) {
		String[] inputDescriptions = {
			"This is the data source queried.",
			"A list of the fields in this table is returned."
		};
		return inputDescriptions[index];
	}

	public String getModuleInfo () {
		String text = "Retrieves a list of available fields in a table.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {
			"ncsa.d2k.modules.io.input.sql.ConnectionWrapper",
			"java.lang.String"
		};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {
			"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			"java.util.Vector"};
		return temp;
	}

	public void doit () throws Exception
	{
		String tableName = (String) this.pullInput (1);
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		Vector v = new Vector();
		Vector c = new Vector();
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet tableSet = dbmd.getColumns (null,null,tableName,"%");
		while (tableSet.next())
			v.addElement(tableSet.getString("COLUMN_NAME"));

		this.pushOutput (cw, 0);
		this.pushOutput (v, 1);
	}
}
