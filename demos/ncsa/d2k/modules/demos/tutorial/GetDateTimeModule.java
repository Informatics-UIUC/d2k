package ncsa.d2k.modules.demos.tutorial;

import ncsa.d2k.core.modules.*;
import java.util.Date;

public class GetDateTimeModule extends ParseDateTimeModule {

	public synchronized void doit () {
		System.out.println (((Date)this.pullInput( 0 )).toString ());
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module prints the String equivalent of the Date object passed in.  </body></html>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GetDateTimeModule";
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
