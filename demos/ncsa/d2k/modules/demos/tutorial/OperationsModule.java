package ncsa.d2k.modules.demos.tutorial;

import ncsa.d2k.core.modules.*;

public class OperationsModule extends InputModule {
	public static final String OP1 = "Date";
	public static final String OP2 = "Time";
	public static final String OP3 = "Date and time";
	
	/**
		Returns the input types, of which there are none.
		
		@returns null since there are not inputs.
	*/
	public String [] getInputTypes () {
		String[] types = {		};
		return types;
	}

	/**
		Returns the output types all of which are strings, the names
		of the possible operations.
		
		@returns null since there are not inputs.
	*/
	public String [] getOutputTypes () {
		String[] types = {"java.lang.String","java.lang.String","java.lang.String"};
		return types;
	}
	
	/**
		Returns the input types, of which there are none.
		
		@returns null since there are not inputs.
	*/
	public void doit () {
System.out.println("Ops mod has been fired!");
		this.pushOutput (OP1, 0);
		this.pushOutput (OP2, 1);
		this.pushOutput (OP3, 2);
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module takes no inputs and produces three outputs; These outputs are     simply names that identify the three possible operations we may perform.     The operations are Date, Time, and Date and time.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
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
			case 0: return "Contains a reference to the String name of the first operation, in this case Date.";
			case 1: return "Contains a reference to the String name of the second operation, in this case Time.";
			case 2: return "Contains a reference to the String name of the third operation, in this case Date and time.";
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "OperationsModule";
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
			case 1:
				return "output1";
			case 2:
				return "output2";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
