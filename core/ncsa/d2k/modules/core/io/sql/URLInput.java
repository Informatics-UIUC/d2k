package ncsa.d2k.modules.core.io.sql;

import java.io.Serializable;
import ncsa.d2k.infrastructure.modules.InputModule;

/**
	This module contains properties that are used to
	specify information that identifies a database instance,
	the user name and password, and the port where the
	database listens.
	<P>
	Such a URL is of the form
	<PRE>jdbc:<driverName>:<driver-specific-string><PRE>
	<P>
	Subclasses should be made for specific drivers
	that sets the driver classname for the user,
	and asks them nicely for the info required to
	generate the third portion of the URL.
*/
abstract public class URLInput extends InputModule implements Serializable {

	/** A JDBC URL and associated username and password, and
	  a JDBC driver are neccecary to initiate a JDBC connection */
	private String dbInstance;
	private String machine;
	private String port;
	protected String driver;
	protected String username;
	protected String password;

	/**
		This method returns the url appropriately formated.
	*/
	abstract public String getUrl();

	/**
		Database instance accessors.
	*/
	public String getDbInstance() { return dbInstance; }
	public void setDbInstance(String newdbi) { dbInstance = newdbi;}
	/**
		machine name accessors.
	*/
	public String getMachine() { return machine; }
	public void setMachine(String newmach) { machine = newmach; }
	/**
		port number accessors.
	*/
	public String getPort() { return port; }
	public void setPort(String newport) { port = newport; }
	/**
		driver name accessors.
	*/
	public String getDriver() { return driver; }
	public void setDriver(String newDriver) { driver = newDriver; }
	/**
		user name accessors.
	*/
	public String getUsername() { return username; }
	public void setUsername(String newUsername) { username = newUsername; }
	/**
		password accessors.
	*/
	public String getPassword() { return password; }
	public void setPassword(String newPassword) { password = newPassword; }

	public URLInput() {
	}

	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
			"A ConnectionWrapper wrapper that contains:\n "+
			"1. A JDBC-style URL that identifies a data source in the JDBC API.\n "+
			"2. The fully qualified classname of the JDBC driver. \n"+
			"3. The username to use.  May be null \n"+
			"4. The password, may be null. "
		};
		return outputDescriptions[index];
	}

	public String getInputInfo (int index) {
		return "There are no inputs to this module.";
	}

	public String getModuleInfo () {
		String text =
			"This base user input module just lets the user\n"+
			"enter the JDBC-style URL directly, gets\n"+
			"a username and password (can be left null),\n"+
			"and gets the fully qualified classname of the\n"+
			"JDBC driver that will handle the URL.\n"+
			"Such a URL is of the form\n"+
			"jdbc:<driverName>:<driver-specific-string>\n"+
			"Subclasses should be made for specific drivers\n"+
			"that sets the driver classname for the user,\n"+
			"and asks them nicely for the info required to \n"+
			"generate the third portion of the URL.\n";
		return text;
	}

	public String[] getInputTypes () { return null; }
	public String[] getOutputTypes () {
		String[] temp = {
			"ncsa.d2k.modules.core.io.sql.ConnectionWrapper" };
		return temp;
	}

	/**
		The doit method simply creates the connection wrapper and pushes it.
	*/
	protected void doit() throws Exception
	{
		ConnectionWrapper out = new ConnectionWrapper(
			getUrl(),getDriver(),getUsername(),getPassword() );
		this.pushOutput (out, 0);
	}
}
