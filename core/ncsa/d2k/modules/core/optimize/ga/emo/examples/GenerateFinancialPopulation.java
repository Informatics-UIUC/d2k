package ncsa.d2k.modules.core.optimize.ga.emo.examples;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
		Generate financial data

*/
public class GenerateFinancialPopulation extends PopulationPrep implements Serializable {
	public GenerateFinancialPopulation () {
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "This module sets up the initial population, and will set all the fields of the population that are used to steer the genetic algorithm.";
		return text;
	}

	/**
		Create the initial population. In this case we have chosen to override the doit method,
		though it was probably not necessary

		@param outV the array to contain output object.
	*/
	public void doit () throws Exception {

		// First define the binary range, in this case 8 bits.
		DoubleRange [] xyz = new DoubleRange [5];
		xyz [0] = new DoubleRange ("x1",0.3, 0.0);
		xyz [1] = new DoubleRange ("x2",0.3, 0.0);
		xyz [2] = new DoubleRange ("x3",0.3, 0.0);
		xyz [3] = new DoubleRange ("x4",0.3, 0.0);
		xyz [4] = new DoubleRange ("x5",0.3, 0.0);

		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values
		ObjectiveConstraints oc [] = new ObjectiveConstraints [2];
		oc[0] = ObjectiveConstraintsFactory.getObjectiveConstraints ("Return",
				.270, .190);
		oc[1] = ObjectiveConstraintsFactory.getObjectiveConstraints ("Risk",
				.0010, .0024);
		NsgaPopulation pop = new ConstrainedNsgaPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}
}