package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;


public class SQLSelectTab extends InputModule
{
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Pass this on to the next module that needs a connection to this data source.";
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
		String[] types = {"java.util.Vector"};
		return types;
	}

    protected void doit ()  throws Exception
    {
	ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
	Connection con = cw.getConnection();
	Vector v = new Vector();
	Statement stmt = con.createStatement();
	ResultSet tableSet = stmt.executeQuery("SELECT * FROM TAB");
	while (tableSet.next())
	    v.addElement(tableSet.getString(1));

	//this.pusOutput (cw, 0);
	this.pushOutput (v, 0);
    }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SQLSelectTab";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
