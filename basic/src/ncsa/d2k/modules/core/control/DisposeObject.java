/*
 * Created on Dec 14, 2004
 * Copyright 2004 University of Illinois Board of Trustees.
 */
package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.OutputModule;

/**
 * @author redman
 */
public class DisposeObject extends OutputModule {
	/**
		This method returns the description of the various inputs.
		@returns the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "The object to be disposed. ";
			default: return "No such input";
		}
	}
	
	/**
		This method returns an array of strings that contains the data types for the inputs.
		@returns the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"java.lang.Object"};
		return types;
	}
	
	/**
		This method returns the description of the outputs.
		@returns the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}
	
	/**
		This method returns an array of strings that contains the data types for the outputs.
		@returns the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {		};
		return types;
	}
	
	/**
		This method returns the description of the module.
		@returns the description of the module.
	*/
	public String getModuleInfo () {
		return "<p>Overview: Disposes of any input received.</p>";
	}
	
	/**
		@param outV the array to contain output object.
	*/
	public void doit () throws Exception {
		this.pullInput (0);
	}
	
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Dispose Object";
	}
	
	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object";
			default: return "No such input";
		}
	}
	
	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "No such output";
		}
	}

}
