/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import java.awt.*;
import ncsa.d2k.modules.core.datatype.table.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	SelectAttribute.java

*/
public class SelectAttribute extends ncsa.d2k.core.modules.UIModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This table contains the names of the attributes which we will select from.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      The list of attributes that were selected.   ";
			case 1: return "      This is the table just as it was input.  ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"[Ljava.lang.String;","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Construct a list containing each of the attributes in the given dataset,     by name. Output the result as a list of strings the user selected from the     list.  </body></html>";
	}

	Table table = null;
	/**
		We will push the output table along as well as what the user entered.
	*/
	public void viewDone (String command) {
		this.pushOutput (table, 1);
		super.viewDone (command);
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
		return new SelectAttributeView();
/*#end^9 Continue editing. ^#&*/
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
/*&%^10 Do not modify this section. */
		String[] fieldMap = {"viewWidgit0"};
		return fieldMap;
/*#end^10 Continue editing. ^#&*/
	}
/*&%^11 Do not modify this section. */
/*#end^11 Continue editing. ^#&*/
}


/*&%^12 Do not modify this section. */
/**
	SelectAttributeView
	This is the UserView class.
*/
class SelectAttributeView extends ncsa.d2k.userviews.UserInputPane {
	DSMultiSelectList viewWidgit0 = new DSMultiSelectList("viewWidgit0");
/*#end^12 Continue editing. ^#&*/
	SelectAttribute module = null;
	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {
/*&%^13 Do not modify this section. */
		super.initView(mod);
		/*Panel canvasArea = new Panel();
		canvasArea.add("Center", viewWidgit0);*/
		add("Center", viewWidgit0);
/*#end^13 Continue editing. ^#&*/
		viewWidgit0.setReturnType (DSMultiSelectList.DS_STRING_ARRAY);
		module = (SelectAttribute) mod;
	}

	/**
		This method is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
		Table table = (Table) o;
		viewWidgit0.removeAll ();
		for (int i = 0 ; i < table.getNumColumns (); i++)
			viewWidgit0.add (table.getColumnLabel (i));
		module.table = table;
	}
/*&%^14 Do not modify this section. */
/*#end^14 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Select Attributes";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input table";
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
				return "SelectedAttributes";
			case 1:
				return "Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}


