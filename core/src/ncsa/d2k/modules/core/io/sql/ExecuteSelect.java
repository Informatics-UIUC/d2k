package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;


public class ExecuteSelect extends InputModule
{
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "The reslut of the query.";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the data source.";
			case 1: return "A String array containing field names to be selected.";
			case 2: return "The where clause if appropriate, or null";
			case 3: return "The name of the table to select from.";
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Executes the Query String.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","[Ljava.lang.String;","java.lang.String","java.lang.String"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"java.sql.ResultSet"};
		return types;
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ExecuteSelect";
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
			case 2:
				return "input2";
			case 3:
				return "input3";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
