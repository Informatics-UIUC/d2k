/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import java.awt.*;
import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	SelectTable.java
	
*/
public class SelectTable extends ncsa.d2k.infrastructure.modules.UIModule {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Tables List\">    <Text>This vector contains a list of all the tables in the database. </Text>  </Info></D2K>";
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
			"java.util.Vector"};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Selected Table\">    <Text>The name of the table that the user selected. </Text>  </Info></D2K>";
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
			"java.lang.String"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Select Table\">    <Text>This module will provide a user interface from which the user can select one from among the database tables available in the targeted database. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
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
class SelectTablesView extends ncsa.d2k.controller.userviews.UserInputPane {
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
}