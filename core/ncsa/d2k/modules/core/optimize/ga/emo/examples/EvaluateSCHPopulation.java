package ncsa.d2k.modules.core.optimize.ga.emo.examples;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateSCHPopulation extends EvaluateModule {

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Evaluate Population\">    <Text>Evalute this F1 population. </Text>  </Info></D2K>";
	}

	/**
		Evaluate the individuals in this class. Here we are simply maxing
		the objective function.
	*/
	public void evaluateIndividual (Individual member) {
		// Must be an Nsga member.
		MOSolution ni = (MOSolution) member;
		double [] genes = (double []) member.getGenes ();
		int numTraits = genes.length;
		double x = genes [0];

		// compute f1
		double f1 = x * x;

		// f2
		double f2 = (x - 2.0);
		f2 *= f2;
		ni.setObjective (0, f1);
		ni.setObjective (1, f2);
	}
}
