package ncsa.d2k.modules.demos.modeldemo;

import ncsa.d2k.infrastructure.modules.*;

/**
	This module is used to signal SeedParameterInput to begin execution.  When
	either of its two inputs are satisfied, it signals SeedParameterInput to begin.
*/
public class SeedLoopModule extends ComputeModule {

	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo(int i) {
		switch(i) {
			case 0:
				return "The beginning trigger.";
			case 1:
				return "The looping trigger.";
			default:
				return "No such input!";
		}		
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
		
		@returns the input types
	*/		
	public String []getInputTypes() {
		String []in = {"java.lang.Boolean", "java.lang.Boolean"};
		return in;
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
		return "This module triggers the next module when either of its two inputs"+
		" are available.";
	}
	
	/**
		This method has been overriden so that this module will be marked as 
		ready when either of its two inputs are available.
	*/
	public boolean isReady() {
		return inputFlags[0] > 0 || inputFlags[1] > 0;
	}
	
	/**
		Perform these actions when this module is executed.
	*/	
	public void doit() {
		Boolean res;
	
		// pull either input
		if(inputFlags[0] > 0)
			res = (Boolean) this.pullInput(0);
		else 
			res = (Boolean) this.pullInput(1);
			
		// signal the next module to begin executing.	
		this.pushOutput(res, 0);	
	}
}
