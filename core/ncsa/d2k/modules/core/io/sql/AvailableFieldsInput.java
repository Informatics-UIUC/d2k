package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;

public class AvailableFieldsInput extends InputModule
{
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Pass this on to the next module that needs a connection to this data source.";
			case 1: return "This is a list of the fields available in the specified table.";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the data source queried.";
			case 1: return "A list of the fields in this table is returned.";
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Retrieves a list of available fields in a table.  </body></html>";
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
				return "input0";
			case 1:
				return "input1";
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
				return "output0";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
