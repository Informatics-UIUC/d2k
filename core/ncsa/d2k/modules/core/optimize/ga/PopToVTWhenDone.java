
package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
	Converts the population to a vertical table where each column is an attribute, objective value,
	or fitness.
*/
public class PopToVTWhenDone extends ncsa.d2k.infrastructure.modules.DataPrepModule {

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Population\">    <Text>This is the population to plot. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"output table\">    <Text>This table just contains the objective values for each of the individuals in the first non-dominated front. </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.Table"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Objectives To VT\">    <Text>This module converts a GA populations objective values into a vertical table so that it can be plotted. </Text>  </Info></D2K>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		Population pop = (Population) this.pullInput (0);
		if (pop.isDone ()) {
			Table vt = pop.getTable ();
			this.pushOutput (vt, 0);
		}
	}

}

