package ncsa.d2k.modules.demos.performance;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import ncsa.d2k.infrastructure.views.UserView;
import java.io.*;

public class ReentrantPreface extends DataPrepModule implements Serializable  {
		
	public String [] getInputTypes () {
		String [] ins = {"[I"};
		return ins;
	}

	public String [] getOutputTypes () {
		String [] outputs = {"[I"};
		return outputs;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public ReentrantPreface() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "This module will continue to run until is reaches a certain number of iterations.";
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
				return "This is the array of integers to operate on.";
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
		After any itinerary completes execution, this method is call. This module will
		reset its' sole input argument here, since this is continuous, and it will reuse
		the input for several iterations.
	*/
	private int testCounter = 0;		
	public void beginExecution () {
		testCounter = 0;
		store = null;
	}
	public boolean isReady ()
	{
		if (testCounter == 0)
			return super.isReady ();
		else
			return true;
	}
	int inputArray [] = null;
	
	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.
		@param inV the list of required inputs.
		@param outV list of results.
	*/
	int [][] store = null;
	public void doit () 
	{
		// First decrement the counter
		if (testCounter == 0)
		{
			inputArray = (int []) this.pullInput (0);
			testCounter = inputArray.length;
			if (store == null)
				store = new int [inputArray.length][1];
		}
		
		int [] ary = store [inputArray.length-testCounter];
		ary[0] = inputArray [inputArray.length-testCounter];
		--testCounter;
		this.pushOutput (ary, 0);
	}
}
