package ncsa.d2k.modules.demos.performance;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.UserView;

public class IncIntegersModule extends ComputeModule  {

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
	public IncIntegersModule() {
		super ();
	}

	/**
		Return a description of this module.

		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Increments each integer in the array of integers passed in.  </body></html>";
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

	int loopSize = 200;
	public int getLoopSize () { return loopSize; }
	public void setLoopSize (int nls) { loopSize = nls; }
	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.

		@param inV the list of required inputs.
		@param outV list of results.
	*/
	public void doit () {

		// First decrement the counter
		int [] arry = (int []) this.pullInput( 0 );
		for (int t = 0 ; t < loopSize ; t++)
		for( int i = 0 ; i < arry.length ; i++)
			arry [i] ++;

		this.pushOutput (arry, 0);
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
