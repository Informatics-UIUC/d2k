package ncsa.d2k.modules.core.optimize.ga.emo.examples;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
		CrossoverModule.java

*/
public class GenerateEC6Population extends PopulationPrep implements Serializable {
	public GenerateEC6Population () {
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
		DoubleRange [] xyz = new DoubleRange [10];
		xyz [0] = new DoubleRange ("x1",1.0, 0.0);
		xyz [1] = new DoubleRange ("x2",1.0, 0.0);
		xyz [2] = new DoubleRange ("x3",1.0, 0.0);
		xyz [3] = new DoubleRange ("x4",1.0, 0.0);
		xyz [4] = new DoubleRange ("x5",1.0, 0.0);
		xyz [5] = new DoubleRange ("x6",1.0, 0.0);
		xyz [6] = new DoubleRange ("x7",1.0, 0.0);
		xyz [7] = new DoubleRange ("x8",1.0, 0.0);
		xyz [8] = new DoubleRange ("x9",1.0, 0.0);
		xyz [9] = new DoubleRange ("x10",1.0, 0.0);


		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values
		ObjectiveConstraints oc [] = new ObjectiveConstraints [2];
		oc[0] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f1",
				this.getBestFitness (), this.getWorstFitness ());
		oc[1] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f2",
				this.getBestFitness (), this.getWorstFitness ());
		NsgaPopulation pop = new UnconstrainedNsgaPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}
}