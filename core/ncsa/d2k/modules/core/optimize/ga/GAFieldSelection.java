/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	GAFieldSelection.java

*/
public class GAFieldSelection extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>This is the GA population, from the genes present here we will determine of the possible inputs have been selected. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Original Examples\">    <Text>This table contains the original examples passed in, and the original inputs selected by the user. </Text>  </Info></D2K>";
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
			"ncsa.d2k.modules.core.optimize.ga.Population",
			"ncsa.d2k.util.datatype.ExampleTable"};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Test Inputs\">    <Text>This example table contains the inputs to be tested. </Text>  </Info></D2K>";
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
			"ncsa.d2k.util.datatype.ExampleTable"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	ExampleTable originalExamples = null;
	int memberIndex = 0;
	Population pop = null;
	public void beginExecution () {
		originalExamples = null;
		pop = null;
		memberIndex = 0;
	}
	public void endExecution () {
		super.endExecution ();
		originalExamples = null;
		pop = null;
		memberIndex = 0;
	}

	private boolean debug = false;
	/**
		set the rankFlag.
		@param score is a boolean indicating if rankFlag is on or off.
	*/
	public void setDebug (boolean score) {
		this.debug = score;
	}

	/**
		get the rankFlag.
		@returns true if rankFlag is on.
	*/
	public boolean getDebug () {
		return this.debug;
	}

	/**
		This method is called at then end of each execution cycle
		for the module, by default, tripping any output triggers.
		Modules which need to set triggers at other times will override
		this method to apply the appropriate logic.
	 */
	protected void activateTriggers()
	{
		// hit all triggers
		if (oTriggers != null && pop == null)
		{
			for(int i = 0; i < oTriggers.length; i++)
				oTriggers [i].trigger ();
		}
	}

	/**
		Ready if we have the example table and a population
	*/
	public boolean isReady () {
		if (originalExamples != null || this.inputFlags[1] > 0)
			if (pop != null) {

				// this means there are more members from a previous population
				return true;
			} else
				if (this.inputFlags[0] > 0) {
					// We have another population of individuals to start on.
					return true;
				}
		return false;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		boolean first_time = false;
		if (originalExamples == null) {

			// We will get this only once.
			originalExamples = (ExampleTable) this.pullInput (1);
			first_time = true;
			if (debug) System.out.println ("GAFieldSelection: Got a new example table");
		}

		if (pop == null) {
			if (debug) System.out.println ("GAFieldSelection: Got a new Population");
			pop = (Population) this.pullInput (0);
			memberIndex = 0;
		}

		// Let's figure out what from among the original inputs we have
		// selected.
		int [] originalInputs = originalExamples.getInputFeatures ();
		int [] newinputs = new int [originalInputs.length];

		// For each gene set, one of the original inputs has been selected.
		BinaryIndividual member = (BinaryIndividual) pop.getMember (memberIndex++);
		boolean [] genes = (boolean []) member.getGenes ();
		int numBits = genes.length;
		int numberNewInputs = 0;
		for (int j = 0 ; j < numBits ; j++)
			if (genes [j]) {
				newinputs [numberNewInputs++] = originalInputs [j];
			}

		// Now copy the new input set to an appropriately sized int array.
		int [] tmp = new int [numberNewInputs];
		System.arraycopy (newinputs, 0, tmp, 0, numberNewInputs);

		// create a new example table.
		ExampleTable newExamples = new ExampleTable (originalExamples);
		newExamples.setInputFeatures (tmp);
		newExamples.setOutputFeatures (originalExamples.getOutputFeatures ());

		if (memberIndex == pop.size ()) {

			// We are done with this population.
			pop = null;
			memberIndex = 0;
		}
		if (debug)
			if (((memberIndex+1) % 100) == 0)
				System.out.println (".");
			else
				System.out.print (".");
		this.pushOutput (newExamples, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

