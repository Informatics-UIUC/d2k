package ncsa.d2k.modules.core.io.sql;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

public class ConnectToDB extends UIModule {
    /**
     * Returns an array of description objects for each property of the
     * Module.
     * @return an array of description objects for each property of the
     * Module.
     */
/*
    public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[7];
        retVal[0] = new PropertyDescription("url", "url", "hello");
        retVal[1] = new PropertyDescription("username", "un", "hello1");
        retVal[2] = new PropertyDescription("password", "pw", "hello2");
        retVal[3] = new PropertyDescription("machine", "mm", "hello3");
        retVal[4] = new PropertyDescription("port", "pt", "hello4");
        retVal[5] = new PropertyDescription("dbInstance", "db", "hello5");
        retVal[6] = new PropertyDescription("driver", "drv", "hello6");
        return retVal;
	}
*/
    /**
     *** Variables ***
     */
    private String url;
    /** username */
    private String username;

    /** password */
    private String password;

    /** machine */
    private String machine;

    /** port */
    private String port;

    /** dbInstance */
    private String dbInstance;

    /** driver */
    private String driver;

    /**
      Provide a description of this module.
      @return A description of this module.
      */
    public String getModuleInfo() {
	return "";
    }

    public String getModuleName() {
        return "ConnectToDB";
    }

    /**
       Return an array containing the input types to this module.
  @return The input types.
  */
    public String[] getInputTypes() {
        return null;
    }

    /**
       Return an array containing the output types of this module.
       @return The output types.
       */
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.sql.DBConnection" };
        return out;
    }

    /**
       Return the info for a particular input.
       @param i The index of the input to get info about
       */
    public String getInputInfo(int i) {
        return "No such input!";
    }

    public String getInputName(int i) {
        return "No such input!";
    }

    /**
       Return the info for a particular output.
       @param i The index of the output to get info about
       */
    public String getOutputInfo(int i) {
        switch(i) {
            case(0):
                return "A connection to the database.";
            default:
                return "No such output!";
        }
    }

    public String getOutputName(int i) {
        switch(i) {
            case(0):
                return "DBConnection";
            default:
                return "No such output!";
        }
    }

    /**
       Get the field name map for this module-view combination.
       @return The field name map.
       */
    public String[] getFieldNameMapping() {
        return null;
    }


    /**
     *** Get and Set Functions ***
     */

    /**
     * Set the url
     * @param u - The url
     */
    public void setUrl(String u) {
        url = u;
    }
    /**
     * Get the url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the username
     * @param u - The username
     */
    public void setUsername(String u) {
        username = u;
    }
    /**
     * Get the username
     * @return
     */
    public String getUsername() {
        return username;
    }
    /**
     * Set the password
     * @param p - The password
     */
    public void setPassword(String p) {
        password = p;
    }
    /**
     * Get the password
     * @return
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set the port
     * @param p - The port
     */
    public void setPort(String p) {
        port = p;
    }
    /**
     * Get the port
     * @return
     */
    public String getPort() {
        return port;
    }
    /**
     * Set the Machine/Host
     * @param m - The machine
     */
    public void setMachine(String m) {
        machine = m;
    }
    /**
     * Get the Machine/Host
     * @return
     */
    public String getMachine() {
        return machine;
    }
    /**
     * Set the dbInstance
     * @param dbI - The dbInstance
     */
    public void setDbInstance(String dbI) {
        dbInstance = dbI;
    }
    /**
     * Get the dbInstance
     * @return
     */
    public String getDbInstance() {
        return dbInstance;
    }
    /**
     * Set the driver
     * @param d - The driver
     */
    public void setDriver(String d) {
        driver = d;
    }
    /**
     * Get the driver
     * @return
     */
    public String getDriver() {
        return driver;
    }


    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
       */
    protected UserView createUserView() {
        return new GetConnectionView();
    }

    /**
       Provides a simple user interface to get username, password, host and port.
       The text values used in the Labels and textfields are properties of the
       module class.
       If these properties are null, default values are used.
       */
    private class GetConnectionView extends JUserPane implements ActionListener {

        /**
         * Local Variables
         */
        private JComboBox cbV;
        private String[] Vendors = {"Oracle", "MySQL", "SQLServer"};
        private int dbFlag;

        /** A label for username */
	    private JLabel lU;
	    /** A text field to show the username */
	    private JTextField tfU = new JTextField();

        /** A label for password */
	    private JLabel lPa;
	    /** A password field to show (hidden) the password */
	    private JPasswordField pfPa = new JPasswordField(30);

	    /** A label for machine */
	    private JLabel lM;
	    /** A text field to show the machine */
	    private JTextField tfM = new JTextField();

        /** A label for port */
	    private JLabel lPo;
	    /** A text field to show the port */
	    private JTextField tfPo = new JTextField();

        /** A label for dbInstance */
	    private JLabel ldbI;
	    /** A text field to show the dbInstance */
	    private JTextField tfdbI = new JTextField();

        /** A label for driver */
	    private JLabel lD;
	    /** A text field to show the driver */
	    private JTextField tfD = new JTextField();

	    /** A button to Abort */
	    private JButton bAb = new JButton("Abort");

	    /** A button to Okay */
	    private JButton bDo = new JButton("Done");

	/** The module that creates this view.  We need a reference to it so
	    we can get and set its properties. */

	/**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/

        /** A text field to show the username */
  //      private JTextField tfU = new JTextField();

        /** A password field to show (hidden) the password */
 //       private JPasswordField pfPa = new JPasswordField(30);

        /** A text field to show the machine */
//        private JTextField tfM = new JTextField();

        /** A text field to show the port */
 //       private JTextField tfPo = new JTextField();

        /** A text field to show the dbInstance */
//        private JTextField tfdbI = new JTextField();

        /** A text field to show the driver */
//        private JTextField tfD = new JTextField();

        /** A button to Abort */
//        private JButton bAb = new JButton("Abort");

        /** A button to Okay */
//        private JButton bDo = new JButton("Done");

        /** The module that creates this view.  We need a reference to it so
     we can get and set its properties. */
    //ConnectToDB parentModule;

    /**
    Perform initializations here.
    @param mod The module that created this UserView
    */
        public void initView(ViewModule mod) {
            /**
             *** Initial Setup ***
             */
      	    JPanel placeholder = new JPanel();
	    JPanel p = new JPanel();
	    p.setLayout(new GridBagLayout());
            //          super.initView(mod); //DO NOT CALL SUPER
            //parentModule = (ConnectToDB)mod;

//            JPanel placeholder = new JPanel();
//            JPanel p = new JPanel();
//            p.setLayout(new GridBagLayout());

            cbV = new JComboBox( Vendors );
            cbV.setEditable(false);
            cbV.setAlignmentX(Component.LEFT_ALIGNMENT);

            cbV.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox cb = (JComboBox)e.getSource();
                    String newSelection = (String)cb.getSelectedItem();
                    if (newSelection == "Oracle") {
                        //tfU.setText("smathur");
                        tfPo.setText("1521");
                        //tfM.setText("bernoulli.ncsa.uiuc.edu");
                        //tfdbI.setText("d2k");
                        tfD.setText("oracle.jdbc.driver.OracleDriver");
                    }
                    else if (newSelection == "MySQL") {
                        //tfU.setText("d2ktest");
                        tfPo.setText("1520");
                        //tfM.setText("lorax.ncsa.uiuc.edu");
                        //tfdbI.setText("d2ktesting");
                        tfD.setText("org.gjt.mm.mysql.Driver");
                    }
                    else if (newSelection == "SQLServer") {
                        //tfU.setText("d2k-test");
                        tfPo.setText("1433");
                        //tfM.setText("zax.ncsa.uiuc.edu");
                        //tfdbI.setText("master");
                        tfD.setText("com.microsoft.jdbc.sqlserver.SQLServerDriver");
                    }
                }
            }
            );

            /**
             *** Process Each Label: username, password, machine, port***
             ***                     dbInstance, driver               ***
             */

	    // Add username
            lU = new JLabel("Username");
	    Constrain.setConstraints(p, lU, 0, 1, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last username chosen
	    String s = getUsername();
	    if(s != null) {
		tfU.setText(s);
            }
            boolean allPropsNull = true;

            // Add Database Vendors
            JLabel lV = new JLabel("Vendor");
            Constrain.setConstraints(p, lV, 0, 0, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            Constrain.setConstraints(p, cbV, 1, 0, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add username
            JLabel lU = new JLabel("Username");
            Constrain.setConstraints(p, lU, 0, 1, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last username chosen
            s = getUsername();
            if(s != null) {
                tfU.setText(s);
                allPropsNull = false;
            }

            Constrain.setConstraints(p, tfU, 1, 1, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add password
            JLabel lPa = new JLabel("Password");
            Constrain.setConstraints(p, lPa, 0, 2, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last password chosen
            s = getPassword();
            if(s != null) {
                pfPa.setText(s);
                allPropsNull = false;

            }

            Constrain.setConstraints(p, pfPa, 1, 2, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add machine
            JLabel lM = new JLabel("Machine");
            Constrain.setConstraints(p, lM, 0, 3, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last machine chosen
            s = getMachine();
            if(s != null) {
                tfM.setText(s);
                allPropsNull = false;
            }
            Constrain.setConstraints(p, tfM, 1, 3, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add port
            JLabel lPo = new JLabel("Port");
            Constrain.setConstraints(p, lPo, 0, 4, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last port chosen
            s = getPort();
            if(s != null) {
                tfPo.setText(s);
                allPropsNull = false;
            }
            Constrain.setConstraints(p, tfPo, 1, 4, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add dbInstance
            JLabel ldbI = new JLabel("Database Instance");
            Constrain.setConstraints(p, ldbI, 0, 5, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last dbInstance chosen
            s = getDbInstance();
            if(s != null) {
                tfdbI.setText(s);
                allPropsNull = false;
            }

            Constrain.setConstraints(p, tfdbI, 1, 5, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

            // Add driver
            JLabel lD = new JLabel("Driver");
            Constrain.setConstraints(p, lD, 0, 6, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 1, 1);

            // try to display the last driver chosen
            s = getDriver();

            if(s != null) {
                tfD.setText(s);
                allPropsNull = false;

            }

            Constrain.setConstraints(p, tfD, 1, 6, 2, 1,
                                     GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, tfU, 1, 1, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    // Add password
            lPa = new JLabel("Password");
	    Constrain.setConstraints(p, lPa, 0, 2, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last password chosen
	    s = /*parentModule.*/getPassword();
	    if(s != null)
		pfPa.setText(s);

	    Constrain.setConstraints(p, pfPa, 1, 2, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    // Add machine
            lM = new JLabel("Machine");
	    Constrain.setConstraints(p, lM, 0, 3, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last machine chosen
	    s = /*parentModule.*/getMachine();
	    if(s != null) {
		tfM.setText(s);
            }
	    Constrain.setConstraints(p, tfM, 1, 3, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    // Add port
            lPo = new JLabel("Port");
	    Constrain.setConstraints(p, lPo, 0, 4, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last port chosen
	    s = /*parentModule.*/getPort();
	    if(s != null) {
		tfPo.setText(s);
            }
	    Constrain.setConstraints(p, tfPo, 1, 4, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    // Add dbInstance
            ldbI = new JLabel("dbInstance");
	    Constrain.setConstraints(p, ldbI, 0, 5, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last dbInstance chosen
	    s = /*parentModule.*/getDbInstance();
	    if(s != null) {
		tfdbI.setText(s);
            }

	    Constrain.setConstraints(p, tfdbI, 1, 5, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    // Add driver
            lD = new JLabel("driver");
	    Constrain.setConstraints(p, lD, 0, 6, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the last driver chosen
	    s = /*parentModule.*/getDriver();

	    if(s != null)
		tfD.setText(s);

	    Constrain.setConstraints(p, tfD, 1, 6, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, placeholder, 0, 4, 4, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 4, 1);
            Constrain.setConstraints(p, placeholder, 0, 4, 4, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 4, 1);

            bAb.addActionListener(this);
            bDo.addActionListener(this);

            if(allPropsNull) {
                cbV.setSelectedItem("Oracle");
            }

            setLayout(new BorderLayout());
            add(p, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(bAb);
            buttonPanel.add(bDo);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
    This method is called when inputs arrive to the ViewModule.
    DBConnection does not receive any inputs, so this method is
    not used.
    @param input The input
    @param index The index of the input
    */
        public void setInput(Object input, int index) {
        }

        protected void done() {
            String _username = tfU.getText();
            /*parentModule.*/setUsername(_username);

             String _port = tfPo.getText();
             /*parentModule.*/setPort(_port);

              String _machine = tfM.getText();
              /*parentModule.*/setMachine(_machine);

               String _dbInstance = tfdbI.getText();
               /*parentModule.*/setDbInstance(_dbInstance);

                String _password = pfPa.getText();  //TODO
                /*parentModule.*/setPassword(_password);

                 String _driver = tfD.getText();
                 /*parentModule.*/setDriver(_driver);

                  /**
                   * Use the Driver to set up the corresponding URL
                   */

            if ( _driver.equals("oracle.jdbc.driver.OracleDriver")) {
                //OracleConnection dbi = new OracleConnection(out, getDbInstance().trim());
                OracleDBConnection oc = new OracleDBConnection(getUrl().trim(),
                                                           getDriver().trim(),
                                                           getUsername().trim(),
                                                           getPassword().trim());
                pushOutput (oc, 0);
            }
            else if ( _driver.equals("org.gjt.mm.mysql.Driver")) {
                mySQLDBConnection mc = new mySQLDBConnection(getUrl().trim(),
                                                         getDriver().trim(),
                                                         getUsername().trim(),
                                                         getPassword().trim(),
                                                         getDbInstance().trim());
                pushOutput (mc, 0);
            }
            else {//if (_driver == "com.microsoft.jdbc.sqlserver.SQLServerDriver") {
                SQLServerDBConnection sc = new SQLServerDBConnection(getUrl().trim(),
                                                                 getDriver().trim(),
                                                                 getUsername().trim(),
                                                                 getPassword().trim());
                pushOutput (sc, 0);
            }
            viewDone("Done");

                  if ( _driver.equals("oracle.jdbc.driver.OracleDriver")) {
                      setUrl("jdbc:oracle:thin:@"+getMachine()+":"+getPort()+":"+getDbInstance());
                      OracleDBConnection oc = new OracleDBConnection(getUrl().trim(),
                              getDriver().trim(),
                              getUsername().trim(),
                              getPassword().trim());
                      //dbi.setConnectionWrapper(out);
                      pushOutput (oc, 0);
                  }
                  else if ( _driver.equals("org.gjt.mm.mysql.Driver")) {
                      setUrl("jdbc:mysql://" + getMachine()+ "/" + getDbInstance());
                      mySQLDBConnection mc = new mySQLDBConnection(getUrl().trim(),
                              getDriver().trim(),
                              getUsername().trim(),
                              getPassword().trim(),
                              getDbInstance().trim());
                      pushOutput (mc, 0);
                  }
                  else if(_driver == "com.microsoft.jdbc.sqlserver.SQLServerDriver") {
                      setUrl("jdbc:microsoft:sqlserver://"+getMachine()+":"+getPort()+";"
                             +"DatabaseName="+getDbInstance());
                      SQLServerDBConnection sc = new SQLServerDBConnection(getUrl().trim(),
                              getDriver().trim(),
                              getUsername().trim(),
                              getPassword().trim());
                      pushOutput (sc, 0);
                  }
                  else
                      pushOutput(null, 0);
                  viewDone("Done");
        }

        protected void abort() {
            viewCancel();
        }

        /**
    When a button is pressed..
    @param e An ActionEvent
    */
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();

            if(src == bDo)
                done(); // User clicked 'Done'

            else if (src == bAb)
                abort(); // User clicked 'Abort'
        }
    }// end inner class
}