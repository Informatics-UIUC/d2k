package ncsa.d2k.modules.demos.performance;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.UserView;

public class PulseInitModule extends ComputeModule  {

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
	public PulseInitModule() {
		super ();
	}

	/**
		Return a description of this module.

		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body> Just push a bunch of copies. this is for testing.</body></html>";
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
			case 0: return "This output is the array of integers after being incremented.";
			default: return "No such output";
		}
	}
	private int counter, MAX = 1000;
	public void setTimes (int tms) {
		MAX = tms;
	}
	public int getTimes () { return MAX; }
	long time;
	public void beginExecution() { counter = 0;time = System.currentTimeMillis(); }
	public void endExecution() {
		System.out.println("It took "+(System.currentTimeMillis()-time)+" ticks.");
	}
	public boolean isReady() {
		if (counter == 0)
			return super.isReady();
		else
			return counter < MAX;
	}
	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.

		@param inV the list of required inputs.
		@param outV list of results.
	*/
	int [] arry;
	public void doit () {

		// First decrement the counter
		if (counter == 0)
		    arry = (int []) this.pullInput( 0 );
		this.pushOutput (arry, 0);

		counter++;
		if ((counter % 10) == 0)
			System.out.println("-------- Pulse : "+counter+" ---------");

		if (counter == MAX) {
			System.out.println ();
			System.out.println ();
			System.out.println ("---------- PulseInitModule Done -----------");
			System.out.println ();
			System.out.println ();
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IncIntegersModule";
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
