package ncsa.d2k.modules.core.io.net;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;

public class SelectURLModule extends UIModule  {
		
	private String [] fieldMap = {"result string"};
	protected String[] getFieldNameMapping () {
		return fieldMap;
	}

	public String [] getInputTypes () {
		String[] types = {		};
		return types;
	}

	public String [] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}
	
	/**
		Return a reference to a SelectURLView object.
		
		@returns a SelectURLView object.
	*/
	protected UserView createUserView( ) {
		return new SelectURLView();
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public SelectURLModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module allows the user to enter a URL and it will pass the resulting     string as it's output.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
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
			case 0: return "This output is a string containing the URL the user entered.";
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SelectURLModule";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
