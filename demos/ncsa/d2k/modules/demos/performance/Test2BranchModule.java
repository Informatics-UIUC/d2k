package ncsa.d2k.modules.demos.performance;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.UserView;
import java.io.*;

public class Test2BranchModule extends DataPrepModule   {

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
	public Test2BranchModule() {
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
	private int iterationCounter;
	public int iterations = 1500;
	public void setIterations (int iterations) {
		this.iterations = iterations;
	}

	public int getIterations () {
		return this.iterations;
	}

	public void beginExecution () {
		iterationCounter = iterations;
		counter = 0;
		prev = -1;
	}

	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.
		@param inV the list of required inputs.
		@param outV list of results.
	*/
	long startTime = 0;
	int counter = 0;
	int prev = -1;
	int tmp [] = null;

	int SIZE = 10;
	public void setSize(int size) { SIZE = size; }
	public int getSIze() { return SIZE; }

	public void doit ()
	{
		// If we are starting, start the timer.
		if (iterationCounter == iterations)
			startTime = System.currentTimeMillis ();

		int [] ary = (int []) this.pullInput (0);
		if (prev == -1)
			prev = ary[0];
		else
			prev++;

		if (ary[0] != prev)
			System.out.println ("## OUT OF SEQUENCE : expected "+prev+" got "+ary[0]);

		if (tmp == null)
			tmp = new int [SIZE];
		tmp [counter] = ary[0];
		counter++;
		if (counter == SIZE)
		{
			counter = 0;
			iterationCounter--;
System.out.println ("iterationCounter:"+iterationCounter);
			if (iterationCounter > 0)
			{
				for (int i = 0 ; i < tmp.length; i++)
					tmp [i] += tmp.length;
				this.pushOutput (tmp, 0);
			}
			else
			{
				startTime = System.currentTimeMillis( )-startTime;
				System.out.println ("It took "+startTime);
			}
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Test2BranchModule";
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
