
package ncsa.d2k.modules.demos.helloworld;

import ncsa.d2k.infrastructure.modules.*;

/**
	PrintHelloStringModule.java
*/
public class PrintHelloStringModule extends ncsa.d2k.infrastructure.modules.OutputModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"theString\">    <Text>the GUI input string </Text>  </Info></D2K>";
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Printer\">    <Text>This is a GUI input. </Text>  </Info></D2K>";

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

}

