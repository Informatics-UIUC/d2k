package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.InputModule;

import java.sql.*;
import java.util.Vector;

public class AvailableTablesInput extends InputModule
{
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
			"Pass this on to the next module that needs a connection to this data source.",
			"This is a list of the available tables, "+
			"soon to be replaced by a superior data structure containing "+
				"the table list and metadata about the tables (allowing for a "+
				"much improved table selection dialog)."
		};
		return outputDescriptions[index];
	}

	public String getInputInfo (int index) {
		String[] inputDescriptions = {
			"This data source is queried to discover what tables are available."
		};
		return inputDescriptions[index];
	}

	public String getModuleInfo () {
		String text = "Retrieves a list of available tables from a jdbc source.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {
			"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			"java.util.Vector"};
		return temp;
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

}
