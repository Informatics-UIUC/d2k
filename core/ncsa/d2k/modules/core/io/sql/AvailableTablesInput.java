package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;

public class AvailableTablesInput extends InputModule
{
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Pass this on to the next module that needs a connection to this data source.";
			case 1: return "This is a list of the available tables, soon to be replaced by a superior data structure containing the table list and metadata about the tables (allowing for a much improved table selection dialog).";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This data source is queried to discover what tables are available.";
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Retrieves a list of available tables from a jdbc source.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.util.Vector"};
		return types;
	}

	protected void doit ()  throws Exception
	{
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		Vector v = new Vector();

		Statement stmt = con.createStatement();
		ResultSet tableSet = stmt.executeQuery("select table_name from all_tables where owner not like '%SYS%'");
		while (tableSet.next())
		    v.addElement(tableSet.getString(1));

	//	DatabaseMetaData dbmd = con.getMetaData();
	//ResultSet tableSet = dbmd.getTables(null,null,"%",null);
	//	while (tableSet.next())
	//		v.addElement(tableSet.getString("TABLE_NAME"));
		this.pushOutput (cw, 0);
		this.pushOutput (v, 1);
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "AvailableTablesInput";
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
