/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.optimize.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	GAFieldSelectionPrep.java

*/
public class GAFieldSelectionPrep extends PopulationPrep
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Input table\">    <Text> </Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Population\">    <Text>The population object is the input and driver for the genetic algorithm, this instance will be configured to manipulate bits, one per attribute, a bit turned on is a feature selected. </Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Feature Selection Prep\">    <Text>This module will take the input example table, and set up a population object that will search the space of all input features. There will be another module that will evaluate each individual in the population evaluating it's fitness relative to some fitness function related to the quality of the associated inputs in terms of predicting the outputs. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	ExampleTable et = null;
	protected int getNumberBits () {
		return et.getInputFeatures ().length;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		et = (ExampleTable) this.pullInput (0);
		BinaryRange [] xyz = new BinaryRange [1];
		xyz [0] = new BinaryRange ("fields", this.getNumberBits ());

		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values.
		ObjectiveConstraints oc = ObjectiveConstraintsFactory.getObjectiveConstraints ("fitness",
				this.getBestFitness (), this.getWorstFitness ());
		SOPopulation pop = new SOPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

