package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.InputModule;

import java.sql.*;
import java.util.Vector;


public class ExecuteSelect extends InputModule
{
	public String getOutputInfo (int index) {
		String[] outputDescriptions = { "The reslut of the query." };
		return outputDescriptions[index];
	}

	public String getInputInfo (int index) {
		String[] inputDescriptions = {
			"This is the data source.",
			"A String array containing field names to be selected.",
			"The where clause if appropriate, or null",
			"The name of the table to select from."
		};
		return inputDescriptions[index];
	}

	public String getModuleInfo () {
		String text = "Executes the Query String.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {
			"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			"[Ljava.lang.String;", // field(s) to select
			"java.lang.String", // table(s) to select from
			"java.lang.String" // table(s) to select from
			//"java.lang.String" // the WHERE clause
		};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"java.sql.ResultSet"};
		return temp;
	}

	protected void doit () throws Exception
	{
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		String[] fieldArray = (String[]) this.pullInput (1);

		StringBuffer fieldList = new StringBuffer(fieldArray[0]);
		for (int i=1; i<fieldArray.length; i++)
			fieldList.append(", "+fieldArray[i]);

		String tableList = (String) this.pullInput (2);
		String query = "SELECT "+fieldList.toString()+" FROM "+tableList;
		String whereClause = (String) this.pullInput (3);
		if (whereClause != null)
			query += " WHERE "+whereClause;

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		this.pushOutput (rs, 0);
	}
}
