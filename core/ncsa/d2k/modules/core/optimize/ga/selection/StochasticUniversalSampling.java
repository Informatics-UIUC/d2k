package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	This will select individuals on the basis of an unranked evaluation function, namely
	the stochastic universal selection without replacement also known as expected value model,
	aka Stochastic Sampling without Replacement.<p>

	In effect it works like this. Imagine that each of the individuals of the current population
	is assigned a start and end point on a line where the length betwee the start
	and end point is perportional to their fitness relative to the total fitness of the
	population. Then assign N equidistant points along the line such that the points are evenly
	distributed all the way along the line. The only variable is where the first point is placed,
	this is determined by a role of the dice. For each of the N points, it is determined which
	individual resides there on the line, and that individual is selected.
*/
public class StochasticUniversalSampling extends SelectionModule
	implements Serializable {

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>the population after selection </Text>  </Info></D2K>";
			default: return "No such output";
		}
}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>the input population </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"SUS\">    <Text>This will select individuals on the basis of an unranked evaluation function, namely </Text>    <Text>the stochastic universal Sampling (J. E. Baker, \"Reducing bias and inefficiency in the selection algorithm\", 1987). </Text>    <Text> </Text>    <Text>Imagine that each of the individuals of the current population is assigned a start and end point on a line where the length betwee the start </Text>    <Text>and end point is perportional to their fitness relative to the total fitness of the population. Then assign N equidistant points along the line such that the points are evenly </Text>    <Text>distributed all the way along the line. The only variable is where the first point is placed, </Text>    <Text>this is determined by a role of the dice. For each of the N points, it is determined which </Text>    <Text>individual resides there on the line, and that individual is selected. </Text>    <Text> </Text>    <Text>The advantage of this mechanism is that it will reduce the chance fluctuations between the expected number of individuals to be selected and the actual number of copies actually allocated to the new population when fliping a coin. </Text>  </Info></D2K>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
}

	/**
		This will select individuals on the basis of an unranked evaluation function, namely
		the stochastic universal selection without replacement also known as expected value model.
		@param population is the population of individuals.
	*/
	protected void compute (Population population) {
		int popSize = population.size();
		double worst = population.getWorstFitness ();

		// normalizer for proportional selection probabilities
		double avg = ((SOPopulation)population).getAverageFitness ();
		boolean mf = worst < avg;
		double factor =  mf ?
					1.0 / (avg - worst) :
					1.0 / (worst - avg);

		//
		// Now the stochastic universal sampling algorithm by James E. Baker!
		//
		int k = 0; 						// index of the next selected sample.
		double ptr = Math.random ();	// role the dice.
		double sum = 0;					// control for selection loop.
		double expected;
		int i = 0;

		for (; i < popSize; i++) {

			// Get the member to test.
			SOSolution member = (SOSolution) population.getMember (i);
			if (mf)
				expected = (member.getObjective () - worst) * factor;
			else
				expected = (worst - member.getObjective ()) * factor;

			// the magnitude of expected will determine the number of
			// progeny of the individual to survive.
			for (sum += expected; sum > ptr; ptr++) {
				this.sample[k++] = i;
			}
		}
	}
}
