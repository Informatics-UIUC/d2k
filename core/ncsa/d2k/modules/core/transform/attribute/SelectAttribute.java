/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.transform.attribute;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import java.awt.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	SelectAttribute.java

*/
public class SelectAttribute extends ncsa.d2k.infrastructure.modules.UIModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"input table\">    <Text>This table contains the names of the attributes which we will select from. </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.util.datatype.Table"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"SelectedAttributes\">    <Text>The list of attributes that were selected. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>This is the table just as it was input.</Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"[Ljava.lang.String;",
			"ncsa.d2k.util.datatype.Table"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Select Attributes\">    <Text>Construct a list containing each of the attributes in the given dataset, by name. Output the result as a list of strings the user selected from the list. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
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
class SelectAttributeView extends ncsa.d2k.controller.userviews.UserInputPane {
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
}


