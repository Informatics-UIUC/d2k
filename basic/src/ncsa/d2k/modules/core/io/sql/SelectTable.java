package ncsa.d2k.modules.core.io.sql;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.userviews.swing.JUserPane;

/**
 * <p>Title: SelectTable</p>
 *
 * <p>Description: 
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: NCSA ALG</p>
 *
 * @author 
 *
 */
public class SelectTable extends ncsa.d2k.core.modules.HeadlessUIModule {
	JOptionPane msgBoard = new JOptionPane();

	/**
	 * Method getModuleName().
	 * 
	 * Describes the purpose of the module.
	 *
	 * @return <code>String</code> describing the purpose of the module.
	 */
	public String getModuleName() {
			return "Select Table";
	}

	/**
	 * Method getModuleInfo ().
	 * 
	 * Describes the purpose of the module.
	 *
	 * @return <code>String</code> description of the module.
	 */
	public String getModuleInfo () {
          String s = "<p> Overview: ";
          s += "This module allows the user to select a database table. </p>";
          s += "<p> Detailed Description: ";
          s += "This module provides a user-interface that allows one table to be chosen ";
          s += "from a list of available database tables. The list of available tables is ";
          s += "retrieved from the <i>Tables List</i> input port. ";
          s += "The selected table will be used to construct SQL queries.  </p>";
          s += "<p>";
          s += "For security reasons, a user may only view the tables they have been granted access to. ";
          s += "If you cannot see the tables you are looking for, please report the ";
          s += "problem to your database administrator. </p>";
          s += "<p> Restrictions: ";
          s += "Currently only Oracle, SQLServer, DB2 and MySql databases are supported.";

          return s;
	}

	/**
	 * Method getInputName().
	 * 
	 * Returns the name of the input at the specified index.
	 *
	 * @param inputIndex Index of the input for which a name should be returned.
	 *
	 * @return <code>String</code> containing the name of the input at the specified index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Tables List";
			default:
				return "No such input.";
		}
	}

	/**
	 * Method getInputInfo().
	 * 
	 * Returns a description of the input at the specified index.
	 *
	 * @param inputIndex Index of the input for which a description should be returned.
	 *
	 * @return <code>String</code> describing the input at the specified index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return " A list of tables in the database.   ";
			default: return "No such input";
		}
	}
	
	/**
	 * Method getInputTypes().
	 * 
	 *	This method returns an array of strings that contains the data types for the inputs.
	 *
	 *	@return the data types of all inputs.
	 */
	public String[] getInputTypes () {
		String[] types = {"java.util.Vector"};
		return types;
	}

	/**
	 * Method getOutputName()
	 * 
	 * Returns the name of the output at the specified index.
	 *
	 * @param outputIndex Index of the output for which a name should be returned.
	 *
	 * @return <code>String</code> containing the name of the output at the specified index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Selected Table";
			default:
				return "No such output.";
		}
	}

	/**
	 * Method getOutputInfo().
	 * 
	 * Returns a description of the output at the specified index.
	 *
	 * @param outputIndex Index of the output for which a description should be returned.
	 *
	 * @return <code>String</code> describing the output at the specified index.
	 */
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return " The name of the table selected by the user.   ";
			default: return "No such output";
		}
	}

	/**
	 * Method getOutputTypes().
	 * 
	 *	This method returns an array of strings that contains the data types for the outputs.
	 *
	 *	@return the data types of all outputs.
	 */
	public String[] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}

	/**
	 * Method getPropertiesDescription()
	 * 
	 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
	 *
	 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
	 */
	public PropertyDescription[] getPropertiesDescriptions() {
        // so that "WindowName" property is invisible
     PropertyDescription[] pds = new PropertyDescription[2] ;
     pds[0] = super.supressDescription;
     pds[1] = new PropertyDescription("selectedTable", "Table Name", "When manually setting this property - make sure the name will be available in the input vector. The names are case sensitive.");
     return pds;
    }

	/**
	 * Method createUserView()
	 * 
	 *	This method is called by D2K to get the UserView for this module.
	 *
	 *	@return the UserView.
	 */
	protected UserView createUserView() {
		return new SelectTablesView();
	}

	/**
	 *  Method getFieldMapping()
	 *  
	 *	This method returns an array with the names of each DSComponent in the UserView
	 *	that has a value.  These DSComponents are then used as the outputs of this module.
	 *
	 * @retun java.lang.String[] 
	 */
	public String[] getFieldNameMapping() {
		String[] fieldMap = {"availTablesList"};
		return fieldMap;
	}


	/**
	 *	Method SelectTablesView()
	 *
	 *	This is the UserView class.
	 */
	private class SelectTablesView extends JUserPane
        implements ActionListener {
        JList availTablesList = new JList ();
        JButton abort = new JButton ("Abort");
        JButton done = new JButton ("Done");
        DefaultListModel availModel = new DefaultListModel();

		/**
		 * Method initView().
		 * 
		 * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
		 * This method adds the components to a Panel and then adds the Panel
		 * to the view.
		 *
		 * @param module The module this view is associated with.
		 */
		public void initView(ViewModule mod) {
	          JPanel canvasArea = new JPanel();
	          canvasArea.setLayout (new BorderLayout ());
	          JPanel buttons = new JPanel ();
	          buttons.setLayout (new GridLayout(1,2));
	          buttons.add (abort);
	          abort.addActionListener(this);
	          buttons.add (done);
	          done.addActionListener(this);
	
	          JPanel b1 = new JPanel();
	          b1.add(buttons);
	
	          availTablesList.setModel(availModel);
	          JScrollPane jsp = new JScrollPane(availTablesList);
	          jsp.setColumnHeaderView(new JLabel("Available Tables"));
	
	          canvasArea.add (jsp, BorderLayout.NORTH);
	          canvasArea.add (b1, BorderLayout.SOUTH);
	          add(canvasArea);
		}
	
		/**
		 * Method setInput().
		 * 
		 * Called to pass the inputs received by the module to the view.
		 * This method is called whenever an input arrives, and is responsible
		 * for modifying the contents of any gui components that should reflect
		 * the value of the input.
		 *
		 * @param input The object that has been input.
		 * @param index The index of the module input that been received.
		 */
		public void setInput(Object o, int index) {
			Vector inputs = (Vector) o;
	        availModel.removeAllElements();
	        String longest = null;
	        int lengthOfLongest = 0;
		        for (int i = 0 ; i < inputs.size (); i++) {
		                String elem = (String)inputs.elementAt(i);
		                availModel.addElement (elem);
		                if(elem.length() > lengthOfLongest) {
		                        longest = elem;
		                        lengthOfLongest = elem.length();
		                }
		        }
	        availTablesList.setPrototypeCellValue(longest);
		}
	
	
		/**
		 * Method actionPerformed().
		 * 
		 */
	    public void actionPerformed(ActionEvent e) {
	      Object src = e.getSource();
	          if (src == done) {
	             if (availTablesList.getSelectedIndex()<0) {
	               JOptionPane.showMessageDialog(msgBoard,
	                  "You must select a table. ", "Error", JOptionPane.ERROR_MESSAGE);
	               System.out.println("No table is selected. ");
	             }
	             else {
	               //headless conversion support
	               setSelectedTable(availTablesList.getSelectedValue().toString());
	               //headless conversion support
	               pushOutput(availTablesList.getSelectedValue().toString(), 0);
	               viewDone("Done");
	             }
	          }
	          else if (src == abort) {
	            viewCancel();
	          }
	      }
	}

    //headless conversion support
    private String selectedTable;
    public String getSelectedTable(){return selectedTable;}
    public void setSelectedTable(String name){selectedTable = name;}

	/**
	 * Method doit().
	 * 
	 */
	public void doit() throws Exception {
      Vector vec = (Vector) pullInput(0);
      HashMap available = new HashMap();
      for(int i=0; i<vec.size(); i++)
        available.put(((String)vec.elementAt(i)).toUpperCase(), new Integer (i));

      if (selectedTable == null || selectedTable.length() == 0)
           throw new Exception (this.getAlias()+" has not been configured. Before running headless,  " +
                                "either configure the parameters via the properties editor " +
                                "or run with the gui and configure the parameters.");
      if(!available.containsKey(selectedTable.toUpperCase()))
        throw new Exception(getAlias() + ": The selected table - " + selectedTable + " was not found in the input list of available tables."+
                             " Please reconfigure the module (using the properties editor or via a GUI run ) so it can run headless.");

       else
         pushOutput(selectedTable.toUpperCase(), 0);

    }

}
