/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	SelectFields.java

*/
public class SelectFields extends ncsa.d2k.core.modules.UIModule {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is a list of the fields that available.   ";
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
			case 0: return "      This is the list of selected fields.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"[Ljava.lang.String;"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Given a list of the fields available, this guy will present a gui that     will allow the user to select from among them, the fields to be extracted     from the database.  </body></html>";
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
		return new SelectFieldsView();
/*#end^9 Continue editing. ^#&*/
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {

/*&%^10 Do not modify this section. */
		String[] fieldMap = {};
/*#end^10 Continue editing. ^#&*/
		String[] fieldMap2 = {"fields"};
		return fieldMap2;
	}
/*&%^11 Do not modify this section. */
/*#end^11 Continue editing. ^#&*/
}

/*&%^12 Do not modify this section. */
/**
	SelectFieldsView
	This is the UserView class.
*/
class SelectFieldsView extends ncsa.d2k.userviews.UserInputPane {
/*#end^12 Continue editing. ^#&*/

	/**
		In the case of this selection list, everything in the list
		is to be passed along, rather than just the selected items.
	*/
	DSMultiSelectList selectedFields = new DSMultiSelectList (15) {
		/**
			This method returns an integer array.
			Or a String array.
		*/
		public Object getValue () {
			String [] stringArray = this.getItems ();
			System.out.println ("In here");
			return stringArray;
		}
	};

	java.awt.List possibleFields = new java.awt.List (15);
	Button add = new Button ("Add");
	Button remove = new Button ("Remove");

	class ActionListenerAdapter implements ActionListener {
		public void actionPerformed (ActionEvent e) {}
	}

	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {

		super.initView(mod);
/*&%^13 Do not modify this section. */
		Panel canvasArea = new Panel();
		canvasArea.setLayout(null);
		add(canvasArea);
/*#end^13 Continue editing. ^#&*/
		canvasArea.setLayout (new BorderLayout ());
		Panel buttons = new Panel ();
		buttons.setLayout (new BorderLayout ());
		buttons.add ("North", add);
		buttons.add ("South", remove);

		// The listener for the add button moves stuff from the possible list
		// to the selected list.
		add.addActionListener (new ActionListenerAdapter () {
			public void actionPerformed (ActionEvent e) {
				String selection = possibleFields.getSelectedItem ();
				possibleFields.remove (selection);
				selectedFields.add (selection);
			}
		});

		// The listener for the add button moves stuff from the possible list
		// to the selected list.
		remove.addActionListener (new ActionListenerAdapter () {
			public void actionPerformed (ActionEvent e) {
				String selection = selectedFields.getSelectedItem ();
				selectedFields.remove (selection);
				possibleFields.add (selection);
			}
		});

		canvasArea.add ("Center", buttons);
		canvasArea.add ("West", selectedFields);
		canvasArea.add ("East", possibleFields);
		selectedFields.setReturnType(DSMultiSelectList.DS_STRING_ARRAY);
		selectedFields.setKey ("fields");
	}

	/**
		This method is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
		Vector fields = (Vector) o;
		for (int i = 0 ; i < fields.size (); i++)
			possibleFields.add ((String) fields.elementAt (i));
	}

/*&%^14 Do not modify this section. */
/*#end^14 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Select Fields";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Fields";
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
				return "Selected Fields";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

