package ncsa.d2k.modules.demos.modeldemo;

import ncsa.d2k.infrastructure.modules.InputModule;

/**
	This module is used as a simple triggering mechanism.  Its only
	function is to signal the next module to begin executing.
*/
public class SeedTrigger extends InputModule {

	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo(int i) {
		return "No such input";
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/	
	public String getOutputInfo(int i) {
		if(i == 0)
			return "The trigger for the next module.";
		else	
			return "No such output.";
	}
	
	/**
		Returns the input types, of which there are none.
		
		@returns null since there are not inputs.
	*/	
	public String []getInputTypes() {
		return null;
	}
	
	/**
		Returns the output types all of which are strings, the names
		of the possible operations.
		
		@returns the output types
	*/	
	public String []getOutputTypes() {
		String []out = {"java.lang.Boolean"};
		return out;
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/	
	public String getModuleInfo() {
		return "This module simply triggers the execution of the itinerary.";
	}
	
	/**
		Perform these actions when this module is executed.
	*/
	public void doit() {	
		this.pushOutput(new Boolean(true), 0);
	}
}
