package ncsa.d2k.modules.demos.performance;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.UserView;

public class InitIntegersModule extends DataPrepModule  {

	public String [] getInputTypes () {
		String[] types = {		};
		return types;
	}

	public String [] getOutputTypes () {
		String[] types = {"[I"};
		return types;
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
		return "<html>  <head>      </head>  <body>    Create an array of integers.  </body></html>";
	}

	/**
		This method will return a text description of the the input indexed by
		the value passed in.

		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
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
			case 0: return "This output is the array of integers after being generated.";
			default: return "No such output";
		}
	}

	public int getDataSize() { return SIZE; }
	public void setDataSize(int ns) { SIZE = ns; }

	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.

		@param inV the list of required inputs.
		@param outV list of results.
	*/
	int SIZE = 2000;
	public void doit () {

		// First decrement the counter
		int[ ] arry = new int[ SIZE ];
		for( int i = 0 ; i < SIZE ; i++)
			arry[i] = i;
		this.pushOutput( arry, 0 );
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "InitIntegersModule";
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
