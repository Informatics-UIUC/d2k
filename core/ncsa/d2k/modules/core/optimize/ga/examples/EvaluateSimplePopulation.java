package ncsa.d2k.modules.core.optimize.ga.examples;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateSimplePopulation extends EvaluateModule {

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Evaluate Population\">    <Text>Evalute this simple population. </Text>  </Info></D2K>";
	}

	/**
		Evaluate the individuals in this class. Here we are simply maxing
		the objective function.
	*/
	public void evaluateIndividual (Individual memb) {

		BinaryIndividual member = (BinaryIndividual) memb;
		boolean [] genes = (boolean []) member.getGenes ();
		int numBits = genes.length;
		int x = 0;

		// Scale the X value and from to booleans.
		int j;
		for (j = 0; j < numBits; j++) {
			x <<= 1;
			if (genes[j])
				x++; // set the bit.
		}
		member.setObjective ((double)x/(double)255.0);
	}
}
