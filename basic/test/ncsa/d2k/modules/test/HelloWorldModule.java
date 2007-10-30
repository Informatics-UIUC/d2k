/**
 * 
 */
package ncsa.d2k.modules.test;

import ncsa.d2k.core.modules.InputModule;

/**
 * This module is for the sake of demonstrating how unsatisfied inputs in an
 * itinerary are being satisfied by a D2K Itinerary Annotator. Therefore, this
 * module takes in a String, prints it out and also outputs that same String.
 * 
 * @author vered
 * 
 */
public class HelloWorldModule extends InputModule {


	/**
	 * Pulls in the input String, outputs it to stdout and pushes it out the
	 * output pipe.
	 * 
	 * @see ncsa.d2k.core.modules.ExecModule#doit()
	 */
	@Override
	protected void doit() throws Exception {

		String str = (String) pullInput(0);
		System.out.println(this.getAlias() + ": printing input string\n" + str);
		pushOutput(str, 0);
	}
	/**
	 * Returns the information regarding input number <codE>arg0</codE>
	 * 
	 * @see ncsa.d2k.core.modules.Module#getInputInfo(int)
	 * @param arg0
	 *            the index of the input its information is returned.
	 */
	public String getInputInfo(int arg0) {
		switch (arg0) {
		case 0:
			return "A Hello World string, to be printed out to stdout";
		default:
			return "No such input";
		}

	}

	/**
	 * Returns the common name of input index <code>arg0</code>
	 * 
	 * @param arg0
	 *            the index of the input its name is returned.
	 */
	public String getInputName(int arg0) {
		switch (arg0) {
		case 0:
			return "Hello World String";
		default:
			return "No such input";
		}

	}
	/**
	 * Returns an array containing the class names (types) of the inputs this
	 * module expects. The array is as big as the number of the inputs, and iten
	 * i in the array corresponds to input i of this module.
	 * 
	 * @see ncsa.d2k.core.modules.Module#getInputTypes()
	 */
	public String[] getInputTypes() {

		return new String[] { "java.lang.String" };
	}

	/**
	 * Returns a detailed description of this module.
	 * 
	 * @see ncsa.d2k.core.modules.Module#getModuleInfo()
	 */
	public String getModuleInfo() {

		return "This module prints out to stdout the input string, "
				+ "and then outputs that very same string.";
	}
	/**
	 * Returns the common name of this module.
	 */
	public String getModuleName() {

		return "Hello Worl Module";
	}

	/**
	 * Returns an information about outpu index <code>arg0</code>
	 * 
	 * @param arg0
	 *            the index of the output its information is returned.
	 * @see ncsa.d2k.core.modules.Module#getOutputInfo(int)
	 */
	public String getOutputInfo(int arg0) {
		switch (arg0) {
		case 0:
			return "The input string";
		default:
			return "No such input";
		}
	}

	/**
	 * Returns the common name of output index <codE>arg0</code>
	 * 
	 * @param arg0
	 *            the index of the output its common name is returned.
	 */
	public String getOutputName(int arg0) {
		switch (arg0) {
		case 0:
			return "String";
		default:
			return "No such input";
		}
	}

	/**
	 * Returns an array with class names (types) of the outputs of this module.
	 * The array is as long as the number of outputs this module has. Item i in
	 * the array corresponds to output i of this module.
	 * 
	 * @see ncsa.d2k.core.modules.Module#getOutputTypes()
	 */
	public String[] getOutputTypes() {
		return new String[] { "java.lang.String" };
	}



  

}
