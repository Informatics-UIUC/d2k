package ncsa.d2k.modules.core.io.sql;


/**
*/
public class OracleURLInput extends URLInput {
	public OracleURLInput() {
		setUsername("smathur");
		setPassword("d2kd2k");
		setDriver("oracle.jdbc.driver.OracleDriver");
		setMachine("houdin.ncsa.uiuc.edu");
		setDbInstance("oracle2");
		setPort("1521");
	}

	public String getUrl() {
                System.out.print("jdbc:oracle:thin:@"+getMachine()+":"+getPort()+":"+getDbInstance());
		return "jdbc:oracle:thin:@"+getMachine()+":"+getPort()+":"+getDbInstance();
	}

	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "A Connection Wrapper containing: 1. A JDBC-style URL identifying a data source in the JDBC API. (jdbc:oracle:thin@<machine>:<port>:<dbinstance>), where machine, port and dbinstance are what the user needs to input. 2. The fully qualified classname of the Oracle JDBC driver. This is oracle.jdbc.driver.OracleDriver3. The username to use; may be null4. The password; may be null.";
			default: return "No such output";
		}
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "OracleURLInput";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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

