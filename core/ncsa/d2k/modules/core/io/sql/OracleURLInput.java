package ncsa.d2k.modules.core.io.sql;


/**
*/
public class OracleURLInput extends URLInput {
	public OracleURLInput() {
		setUsername("disted_mgr");
		setPassword("testmining");
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
		String[] outputDescriptions = {
			"A Connection Wrapper containing: \n"+
			"1. A JDBC-style URL identifying a data source in the JDBC API. "+
			"(jdbc:oracle:thin@<machine>:<port>:<dbinstance>), where "+
			"machine, port and dbinstance are what the user needs to input.\n "+
			"2. The fully qualified classname of the Oracle JDBC driver. "+
			"This is oracle.jdbc.driver.OracleDriver\n"+
			"3. The username to use; may be null\n"+
			"4. The password; may be null.\n"
		};
		return outputDescriptions[index];
	}

}

