package ncsa.d2k.modules.core.optimize.ga.examples;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
		CrossoverModule.java
*/
public class GenerateSimplePopulation extends PopulationPrep implements Serializable {
	public GenerateSimplePopulation () {
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
		int numGenes = 1;
		BinaryRange [] xyz = new BinaryRange [1];
		xyz [0] = new BinaryRange ("x",8);

		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values.
		ObjectiveConstraints oc = ObjectiveConstraintsFactory.getObjectiveConstraints ("y",
				this.getBestFitness (), this.getWorstFitness ());
		SOPopulation pop = new SOPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}
}