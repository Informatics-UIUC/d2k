package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;
import ncsa.d2k.core.modules.*;

public class AvailableTablesInput extends InputModule
{
	/** list data tables (not views and cube tables) only */
	protected boolean dataTableOnly = true;
        protected boolean dataCubeOnly = false;

	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "Pass the database connection to the next module.";
			case 1: return "A list of available tables.";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "The database connection to discover what tables are available.";
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
          String s = "<p> Overview: ";
          s += "This module displays the list of available database tables. </p>";
          s += "<p> Detailed Description: ";
          s += "This module makes a connection to a database and retrieves the ";
          s += "list of available database tables. There are two types of tables in a ";
          s += "database: raw data tables and aggregated cube tables. By using ";
          s += "the property parameters: 'List Data Tables' and 'List Cube Tables', you ";
          s += "can list one or both types of tables. For the security purpose, ";
          s += "you may only view the tables you have been granted to. If you ";
          s += "cannot see the tables you are looking for, please report the ";
          s += "problems to your database administrator. </p>";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle database.";

          return s;
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.util.Vector"};
		return types;
	}

	/**
		Get the value of dataTableOnly
		@return true if only data tables (not data cubes) should be listed.
		false otherwise
	*/
	public boolean getDataTableOnly() {
		return dataTableOnly;
	}

	/**
		Set the the value of dataTableOnly
		@param b true if only data tables should be listed.
		false otherwise
	*/
	public void setDataTableOnly(boolean b) {
		dataTableOnly = b;
	}

	/**
		Get the value of dataCubeOnly
		@return true if only data cubes (not data tables) should be listed.
		false otherwise
	*/
	public boolean getDataCubeOnly() {
		return dataCubeOnly;
	}

	/**
		Set the the value of dataTableOnly
		@param b true if only data tables should be listed.
		false otherwise
	*/
	public void setDataCubeOnly(boolean b) {
		dataCubeOnly = b;
	}

        public PropertyDescription [] getPropertiesDescriptions () {
          PropertyDescription [] pds = new PropertyDescription [2];
          pds[0] = new PropertyDescription ("dataTableOnly", "List Data Tables", "Choose True if you only want to list data tables, but not data cubes.");
          pds[1] = new PropertyDescription ("dataCubeOnly", "List Data Cubes", "Choose True if you only want to list data cubes, but not data tables.");
          return pds;
        }

	protected void doit ()  throws Exception
	{
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		Vector v = new Vector();

		Statement stmt = con.createStatement();
                String qryString = "select table_name from user_tables";
                if (dataTableOnly && !dataCubeOnly) {
                  qryString = qryString + " where table_name not like '%_CUBE%'";
                }
                else if (dataCubeOnly && !dataTableOnly) {
                  qryString = qryString + " where table_name like '%_CUBE%'";
                }
		ResultSet tableSet = stmt.executeQuery(qryString);
		while (tableSet.next())
		    v.addElement(tableSet.getString(1));

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
				return "Database Connection";
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
				return "Tables List";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
