package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;

public class SelectURLModule extends UIModule  {
		
	private String [] fieldMap = {"result string"};
	protected String[] getFieldNameMapping () {
		return fieldMap;
	}

	public String [] getInputTypes () {
		return null;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"java.lang.String"};
		return outputs;
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
		return "This module allows the user to enter a URL and it will pass the resulting string as it's output.";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		return "There is no such input.";
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0:
				return "This output is a string containing the URL the user entered.";
			default:
				return "There is no such output.";
		}
	}
}
