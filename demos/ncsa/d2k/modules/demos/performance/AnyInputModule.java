package ncsa.d2k.modules.demos.performance;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import ncsa.d2k.infrastructure.views.UserView;

public class AnyInputModule extends DataPrepModule  {
		
	public String [] getInputTypes () {
		String [] ins = {"[I","[I"};
		return ins;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"[I"};
		return outputs;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public AnyInputModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "Pass either input to the output.";
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
				return "This is the first of two possible arrays of integers to operate on.";
			case 1:
				return "This is the second of two possible arrays of integers to operate on.";
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
		Ascertain the correct output to generate on the basis of the string
		passed in.
		
		@param inV the list of required inputs.
		@param outV list of results.
	*/
	public void doit () {
		if( inputFlags[ 0 ] > 0 )
			this.pushOutput (this.pullInput( 0 ), 0);
		else
			this.pushOutput (this.pullInput( 1 ), 0);

	}
	
	/** 
		the module should ONLY check if input from pipes is ready through this method - jjm-uniq
		return true if ready to fire , false otherwise
	*/
	public boolean isReady () {
		if( inputFlags[ 0 ] > 0 || inputFlags[ 1 ] > 0 )
			return true;
		else
			return false;
	}
}
