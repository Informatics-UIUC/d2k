package ncsa.d2k.modules.core.optimize.ga.selection;
import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	Do a tounament selection with only one shuffle. Continue the tournament selection over and over
	again until the population is filled.
*/
public class Truncation extends SelectionModule implements Serializable {

	/** tournament size. */
	protected int tsize = 4;

	//////////////////////////////////
	// Accessors for the properties.
	//////////////////////////////////

	/**
		Set the Tournament size. Value msut be 2 or greater.
		@param score the size of the tounament.
	*/
	public void setTournamentSize (int score) {
		tsize = score;
	}

	/**
		get the tournament size.
		@returns the tournament size.
	*/
	public int getTournamentSize () {
		return tsize;
	}
	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"This population is the resulting progeny."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
				"This is the next generation population to be selected on."
		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "This module will take the given population of Individuals and select based on fitness, or random draw. The result will be a population that was selected.\n RankIndividuals is set if the individuals are to be ranked on the basis of some fitness function.\n Gap is set if there is a generation gap. 1.0 is no generation gap, and the smaller the number (> 0) the greater the gap.";
		return text;
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return temp;
	}

	/**
		Do tournament selection with only one shuffle, filling in the indexes of the selected
		individuals in the sample array.
		@param population is the population of individuals.
	*/
	protected void compute (Population population) {
		int s = tsize;
		int popSize = population.size ();
		int [] sorted = population.sortIndividuals ();

		// Shuffle the individuals, then for each group of s individuals, pick
		// the best one and put it into the new population. Continue to do this
		// until the new population is complete.
		int nextOriginal = 0;
		int nextSample = 0;
		for (; nextSample < popSize ;) {
			for (int i = 0; nextSample < popSize && i < s ;nextSample++, i++)
				sample [nextSample] = sorted [nextOriginal];
			nextOriginal++;
		}
	}
}
