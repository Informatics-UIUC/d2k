package ncsa.d2k.modules.demos.performance;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.UserView;
import java.io.*;

public class TestBranchModule extends DataPrepModule   {
		
	public String [] getInputTypes () {
		String[] types = {"[I"};
		return types;
	}

	public String [] getOutputTypes () {
		String[] types = {"[I"};
		return types;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public TestBranchModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will continue to run until is reaches a certain number of     iterations.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the array of integers to operate on.";
			default: return "No such input";
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
			case 0: return "This output is the string that the user selected from those passed in.";
			default: return "No such output";
		}
	}
		
	/**
		After any itinerary completes execution, this method is call. This module will
		reset its' sole input argument here, since this is continuous, and it will reuse
		the input for several iterations.
	*/
	private int testCounter;
	public int iterations = 1500;
	public void setIterations (int iterations) {
		this.iterations = iterations;
	}
		
	public int getIterations( ) {
		return this.iterations;
	}
		
	public void beginExecution () {
		testCounter = iterations;
	}
	
	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.
		
		@param inV the list of required inputs.
		@param outV list of results.
	*/
	long startTime = 0;
	int prev = 1;
	public void doit () 
	{
		// First decrement the counter
		if (testCounter == iterations)
			startTime = System.currentTimeMillis ();
		testCounter--;
		int [] ary = (int []) this.pullInput (0);
		if (testCounter > 0)
		{
			this.pushOutput (ary, 0);
		}else
		{
			startTime = System.currentTimeMillis( )-startTime;
			System.out.println ("It took "+startTime);
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "TestBranchModule";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
