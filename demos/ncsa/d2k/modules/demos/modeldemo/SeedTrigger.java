package ncsa.d2k.modules.demos.modeldemo;


import ncsa.d2k.core.modules.InputModule;

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
		switch (i) {
			default: return "No such input";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/	
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The trigger for the next module.";
			default: return "No such output";
		}
	}
	
	/**
		Returns the input types, of which there are none.
		
		@returns null since there are not inputs.
	*/	
	public String []getInputTypes() {
		String[] types = {		};
		return types;
	}
	
	/**
		Returns the output types all of which are strings, the names
		of the possible operations.
		
		@returns the output types
	*/	
	public String []getOutputTypes() {
		String[] types = {"java.lang.Boolean"};
		return types;
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/	
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module simply triggers the execution of the itinerary.  </body></html>";
	}
	
	/**
		Perform these actions when this module is executed.
	*/
	public void doit() {	
		this.pushOutput(new Boolean(true), 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SeedTrigger";
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
