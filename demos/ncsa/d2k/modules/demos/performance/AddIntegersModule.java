package ncsa.d2k.modules.demos.performance;

import ncsa.d2k.infrastructure.modules.ComputeModule;
import ncsa.d2k.infrastructure.views.UserView;

public class AddIntegersModule extends ComputeModule  {
		
	public String [] getInputTypes () {
		String [] ins = {"[I", "[I"};
		return ins;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"[I"};
		return outputs;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public AddIntegersModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "Increments each integer in the array of integers passed in.";
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
				return "This is the first of the arrays of integers to add together.";
			case 1:
				return "This is the second of the arrays of integers to add together.";
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
				return "This output is the array of integers after being incremented.";
			default:
				return "There is no such output.";
		}
	}
			
	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.
		
		@param inV the list of required inputs.
		@param outV list of results.
	*/
	public void doit () {

		// First decrement the counter
		int [] arry = (int []) this.pullInput( 0 );
		int [] arry2 = (int []) this.pullInput( 1 );
		for( int i = 0 ; i < arry.length ; i++)
			arry [i] += arry2 [i];
			
		this.pushOutput (arry, 0);
	}
}
