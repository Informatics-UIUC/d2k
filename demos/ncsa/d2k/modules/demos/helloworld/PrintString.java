
package ncsa.d2k.modules.demos.helloworld;


import ncsa.d2k.core.modules.*;

/**
	PrintString.java
*/
public class PrintString extends ncsa.d2k.core.modules.OutputModule {

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      theString is a string that will be printed out with the string that is set as a property.   ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.lang.String"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module takes as input a string and allows the user to set the value     for another string as a property. The input string and the property string     are concatenated and printed to the console.  </body></html>";
	}

	String name = "D2K to the rescue!!!";

	/**
		Adds a string to the end of string passed to this module.
	*/
	public String getName()
	{
		return(name);
	}

	public void setName(String str)
	{
		name = str;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
         String str = (String)pullInput(0)+name;
         System.out.println(str);
         System.out.println("length of input string="+str.length());
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "theString";
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

