package ncsa.d2k.modules.demos.tutorial;

import ncsa.d2k.core.modules.*;

public class GetTimeModule extends ParseDateTimeModule {
	
	public void doit () {
		super.doit ();
		System.out.println (currentTime);
	}
	
	/**
		Return a description of this module.
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module parses a Date object to generate a date string and a time     string, then it prints the time string.  </body></html>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GetTimeModule";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
