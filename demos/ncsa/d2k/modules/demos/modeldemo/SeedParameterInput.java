package ncsa.d2k.modules.demos.modeldemo;


import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.UserView;

/**
	This module will present a UserView to the user.  The UserView is defined in 
	SeedParameterView.java
*/
public class SeedParameterInput extends ViewModule {

	/** 
		This is the fieldMap.  Each of the items in this array represent DSComponents in
	   	SeedParameterView.  The String passed to the constructor for each DSComponent is the
	   	String used to identify it in the field map. The values of each of these DSComponents 
	   	will be used as the outputs of this module, in the order they appear in the fieldMap 
	   	array.
		
		ie output0 = "light" ( a DSTextField )
		   output1 = "moisture" ( a DSTextField )
		   output2 = "soil" ( a DSTextField )
		   output3 = "result" ( a DSStringChoice )
		   	   
	*/
	private String [] fieldMap = {"light", "moisture", "soil", "result"};
		
	public boolean isReady() {
		return (inputFlags[0] > 0);
	}	
	
	/**
		Return the field name mapping
	
		NOTE : see the comments for fieldMap
	
		@returns fieldMap
	*/
	protected String[] getFieldNameMapping() {
		return fieldMap;
	}
	
	/**
		Create the associated UserView
		
		@returns the UserView
	*/
	protected UserView createUserView() {
		return new SeedParameterView();
	}
	
	/**
		Returns the input types, of which there are none.
		
		@returns the input types
	*/			
	public String[] getInputTypes() {
		String[] types = {"java.lang.Boolean"};
		return types;
	}

	/**
		Returns the output types all of which are strings, the names
		of the possible operations.
		
		@returns the output types
	*/	
	public String[] getOutputTypes() {
		String[] types = {"java.lang.String","java.lang.String","java.lang.String","java.lang.Boolean"};
		return types;
	}

	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/	
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module presents a UserView for the user to input the results of each     experiment. The input to this module is just used as a trigger for it to     begin execution.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/	
	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The trigger for this module to begin executing.";
			default: return "No such input";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		NOTE: See the comments for fieldMap for an explanation of the outputs
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/		
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A function of the light used in the experiment.";
			case 1: return "A function of the moisture used in the experiment.";
			case 2: return "A function of the soil used in the experiment.";
			case 3: return "The result of the experiment.";
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SeedParameterInput";
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
			case 1:
				return "output1";
			case 2:
				return "output2";
			case 3:
				return "output3";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
