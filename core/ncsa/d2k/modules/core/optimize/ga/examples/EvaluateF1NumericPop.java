package ncsa.d2k.modules.core.optimize.ga.examples;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateF1NumericPop extends EvaluateModule {

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
	public void evaluateIndividual (Individual memb) {
		NumericIndividual member = (NumericIndividual) memb;
		double [] genes = (double []) member.getGenes ();
		double x = genes[0];
		double y = genes[1];
		double z = genes[2];
		member.setObjective (Math.log ((x*x) + (y*y) + (z*z))/
			Math.log ((255*255) + (255*255) + (255*255)));
	}
}
