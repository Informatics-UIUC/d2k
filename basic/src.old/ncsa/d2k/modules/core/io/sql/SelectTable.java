package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import java.awt.event.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.userviews.swing.*;

/**
	SelectTable.java

*/
public class SelectTable extends ncsa.d2k.core.modules.UIModule {
  JOptionPane msgBoard = new JOptionPane();

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return " A list of tables in the database.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"java.util.Vector"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return " The name of the table that the user selected.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
          String s = "<p> Overview: ";
          s += "This module allows users to select a database table. </p>";
          s += "<p> Detailed Description: ";
          s += "Given a list of available tables, this module provides a user-interface ";
          s += "to allow users to choose a table from it. ";
          s += "The selected table will be used to construct SQL queries. For security ";
          s += "purposes, you may only view the tables you have been granted access to. ";
          s += "If you cannot see the tables you are looking for, please report the ";
          s += "problems to your database administrator. </p>";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle databases.";

          return s;
	}

	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new SelectTablesView();
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		String[] fieldMap = {"availTablesList"};
		return fieldMap;
	}

		/**
		 * Return the human readable name of the module.
		 * @return the human readable name of the module.
		 */
		public String getModuleName() {
			return "SelectTable";
		}

		/**
		 * Return the human readable name of the indexed input.
		 * @param index the index of the input.
		 * @return the human readable name of the indexed input.
		 */
		public String getInputName(int index) {
			switch(index) {
				case 0:
					return "Tables List";
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
					return "Selected Table";
				default: return "NO SUCH OUTPUT!";
			}
		}

	/**
		SelectTablesView
		This is the UserView class.
	*/
	private class SelectTablesView extends JUserPane
            implements ActionListener {
            JList availTablesList = new JList ();
            JButton abort = new JButton ("Abort");
            JButton done = new JButton ("Done");
            DefaultListModel availModel = new DefaultListModel();

		/**
			This method adds the components to a Panel and then adds the Panel
			to the view.
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
			This method is called whenever an input arrives, and is responsible
			for modifying the contents of any gui components that should reflect
			the value of the input.

			@param input this is the object that has been input.
			@param index the index of the input that has been received.
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

                public void actionPerformed(ActionEvent e) {
                  Object src = e.getSource();
                  if (src == done) {
                     if (availTablesList.getSelectedIndex()<0) {
                       JOptionPane.showMessageDialog(msgBoard,
                          "You must select a table. ", "Error", JOptionPane.ERROR_MESSAGE);
                       System.out.println("No table is selected. ");
                     }
                     else {
                       pushOutput(availTablesList.getSelectedValue().toString(), 0);
                       viewDone("Done");
                     }
                  }
                  else if (src == abort) {
                    viewCancel();
                  }
              }
	}

	// QA Comments
// 2/18/03 - Handed off to QA by Dora Cai
// 2/19/03 - Anca started QA process.  Minor changes to module info.
// 2/19/03 - Very clean and well documented. checked into basic.
// 2/28/03 - Dora added code to handle no selection for table name
// 03/03/03 - QA and checked into basic - Anca.
// END QA Comments

}