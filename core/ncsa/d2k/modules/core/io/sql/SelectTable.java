/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import java.awt.*;
import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	SelectTable.java

*/
public class SelectTable extends ncsa.d2k.core.modules.UIModule {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This vector contains a list of all the tables in the database.   ";
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
			case 0: return "      The name of the table that the user selected.   ";
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
		return "<html>  <head>      </head>  <body>    This module will provide a user interface from which the user can select     one from among the database tables available in the targeted database.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
	}


/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {


/*&%^9 Do not modify this section. */
		return new SelectTablesView();
/*#end^9 Continue editing. ^#&*/
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {


/*&%^10 Do not modify this section. */
		String[] fieldMap = {"availTablesList"};
		return fieldMap;
/*#end^10 Continue editing. ^#&*/
	}



/*&%^11 Do not modify this section. */
/*#end^11 Continue editing. ^#&*/
}


/*&%^12 Do not modify this section. */
/**
	SelectTablesView
	This is the UserView class.
*/
class SelectTablesView extends ncsa.d2k.userviews.UserInputPane {
	java.awt.Label availTablesLabel = new java.awt.Label("availTablesLabel");
	DSList availTablesList = new DSList("availTablesList");
/*#end^12 Continue editing. ^#&*/

	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {
		super.initView(mod);
/*&%^13 Do not modify this section. */
		Panel canvasArea = new Panel();
		canvasArea.setLayout(new BorderLayout());
		canvasArea.add(availTablesLabel, BorderLayout.NORTH);
		canvasArea.add(availTablesList, BorderLayout.CENTER);
		add(canvasArea);
/*#end^13 Continue editing. ^#&*/
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
		for (int i = 0 ; i < inputs.size (); i++)
			availTablesList.add ((String) inputs.elementAt (i));
	}


/*&%^14 Do not modify this section. */
/*#end^14 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Select Table";
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
}
