package ncsa.d2k.modules.demos.tutorial;

import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.UserView;

public class Sub1Module extends ViewModule  {
		
	private String [] fieldMap = {"result string"};
	protected String[] getFieldNameMapping () {
		return fieldMap;
	}

	public String [] getInputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}

	public String [] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public Sub1Module() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module allows the user to select one from the three strings passed     in. A List user interface component is used to display the inputs, the     user selects one of the inputs from the list.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the first of three string to chose from.";
			default: return "No such input";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This output is the string that the user selected from those passed in.";
			default: return "No such output";
		}
	}
	
	/**
		Create a new instance of a UserView object that provides the 
		user interface for this user interaction module.
		
		@returns a new instance of a UserView providing the interface
			 for this module.
	*/
	protected UserView createUserView () {
		return new SelectStringView ();
	}
	
	/**
		Get the window name.
	*/
	public String getWindowName( )
		{
		return "Kaakaa";
		}
	/**
		Get the window name.
	*/
	public String getPaneName( )
		{
		return "center";
		}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Sub1Module";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
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
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
