package ncsa.d2k.modules.demos.performance;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import ncsa.d2k.infrastructure.views.UserView;

public class InitIntegersModule extends DataPrepModule  {

	public String [] getInputTypes () {
		return null;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"[I"};
		return outputs;
	}

	/**
		Default constructor simply sets up the input and output types.
	*/
	public InitIntegersModule() {
		super ();
	}

	/**
		Return a description of this module.

		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "Create an array of integers.";
	}

	/**
		This method will return a text description of the the input indexed by
		the value passed in.

		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
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
				return "This output is the array of integers after being generated.";
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
	static final int SIZE = 2;
	public void doit () {

		// First decrement the counter
		int[ ] arry = new int[ SIZE ];
		for( int i = 0 ; i < SIZE ; i++)
			arry[i] = i;
		this.pushOutput( arry, 0 );
	}
}
