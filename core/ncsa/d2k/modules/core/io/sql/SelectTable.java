package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.userviews.widgets.swing.*;

/**
	SelectTable.java

*/
public class SelectTable extends ncsa.d2k.core.modules.UIModule {

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
          s += "Given a list of available tables, this module provides an user-interface ";
          s += "to allows users to choose a table from it. ";
          s += "The selected table will be used to construct SQL queries. For the security purpose, ";
          s += "you may only view the tables you have been granted to. If you ";
          s += "cannot see the tables you are looking for, please report the ";
          s += "problems to your database administrator. </p>";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle database.";

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
	private class SelectTablesView extends ncsa.d2k.userviews.swing.JUserInputPane {
		JLabel availTablesLabel = new JLabel("Available Tables");
		DSJList availTablesList = new DSJList();

		/**
			This method adds the components to a Panel and then adds the Panel
			to the view.
		*/
		public void initView(ViewModule mod) {
			super.initView(mod);
			availTablesList.setKey("availTablesList");
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane(availTablesList);
			jsp.setColumnHeaderView(availTablesLabel);
			p.add(jsp, BorderLayout.CENTER);
			add(p, BorderLayout.CENTER);
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
			DefaultListModel dlm = new DefaultListModel();
			for (int i = 0 ; i < inputs.size (); i++)
				dlm.addElement ((String) inputs.elementAt (i));
			availTablesList.setModel(dlm);
		}
	}
}