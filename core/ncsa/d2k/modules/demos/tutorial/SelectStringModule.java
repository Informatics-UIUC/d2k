package ncsa.d2k.modules.demos.tutorial;
import ncsa.d2k.infrastructure.modules.ViewModule;
import ncsa.d2k.infrastructure.views.UserView;

public class SelectStringModule extends ViewModule  {
		
	private String [] fieldMap = {"result string"};
	protected String[] getFieldNameMapping () {
		return fieldMap;
	}

	public String [] getInputTypes () {
		String [] ins = {"java.lang.String","java.lang.String","java.lang.String"};
		return ins;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"java.lang.String"};
		return outputs;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public SelectStringModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "This module allows the user to select one from the three strings passed in. A List user interface component is used to display the inputs, the user selects one of the inputs from the list.";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0:
				return "This is the first of three string to chose from.";
			case 1:
				return "This is the second of three string to chose from.";
			case 2:
				return "This is the third of three string to chose from.";
			default:
				return "There is no such input.";
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
			case 0:
				return "This output is the string that the user selected from those passed in.";
			default:
				return "There is no such output.";
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
}
