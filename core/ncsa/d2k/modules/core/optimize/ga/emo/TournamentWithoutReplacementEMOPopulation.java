package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	In tournament selection, s individuals are selected to compete in a tournament,
	the winner is awarded a space in the next generation. This process is repeated
	until teh new population is full. In tournament selection without replacement,
	individuals who have already be selected to compete in previous tournaments may
	not compete in sucessive tournaments.<p>

	The only property is the tournament size, denoted as S above.
*/
public class TournamentWithoutReplacementEMOPopulation extends SelectionModule  {

	/** selective pressure determins how rapidly the population converges. */
	protected int tsize = 4;

	//////////////////////////////////
	// Accessors for the properties.
	//////////////////////////////////

	/**
		Set the selection pressure. This value must be between 1 and 2.
		@param score new min rank.
	*/
/*	public void setTournamentSize (int score) {
		tsize = score;
	}

	/**
		get the selection pressure.
		@returns the minimum rank
	*/
/*	public int getTournamentSize () {
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
		switch (index) {
			case 0: return "This population is the resulting progeny.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the next generation population to be selected on.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will take the given population of Individuals and select based     on fitness, or random draw. The result will be a population that was     selected. RankIndividuals is set if the individuals are to be ranked on     the basis of some fitness function. Gap is set if there is a generation     gap. 1.0 is no generation gap, and the smaller the number (&gt; 0) the     greater the gap.  </body></html>";
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
		Do tournament selection with replacement, filling in the indexes of the selected
		individuals in the sample array.
		@param population is the population of individuals.
	*/
	protected void compute (Population population) {
		Individual [] individual = population.getMembers ();
		int s = tsize;
		int popSize = sample.length;
		int [] shuffle = new int [popSize];
		for (int i = 0 ; i < popSize ; i++) shuffle [i] = i;

		// Shuffle the individuals, then for each group of s individuals, pick
		// the best one and put it into the new population. Continue to do this
		// until the new population is complete.
		for (int nextSample = 0 ; nextSample < popSize ;) {
			population.shuffleIndices (shuffle);

			// Select one individual from each group of s the population
			// contains
			for (int i = 0 ; i < popSize &&  nextSample < popSize;) {
				int best = shuffle [i];

				// From among the next s members, find the best one.
				for (int j = 0 ; j < s && i < popSize; j++, i++) {
					if (population.compareMembers (individual [shuffle [i]],
							individual [best]) > 0)
						best = shuffle [i];
				}
				sample [nextSample++] = best;
			}
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "TournamentWithoutReplacement";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}

        public void doit () {

                // Our input argument is the population.
                Population population = (Population) this.pullInput (0);
                NsgaPopulation nsga = (NsgaPopulation)population;
                this.tsize = nsga.getPopulationInfo().tournamentSize;
                if (debug) {
                        System.out.println ("==================================================");
                        System.out.println ("-------------- BEFORE SELECTION ------------------");
                        for (int i = 0 ; i < population.size () ; i++)
                                System.out.println (i+"::"+population.getMember (i));

                }

                // This is an array of indices which when done will identify
                // the members of the new population.
                int popSize = population.size ();
                sample = new int [popSize];

                // Compute the members to survive and reproduce
                compute (population);
                if (debug) {
                        System.out.println ("");
                        System.out.println ("-------------- AFTER SELECTION ------------------");
                        for (int i = 0 ; i < sample.length ; i++)
                                System.out.println (i+":"+sample [i]+":"+population.getMember (sample[i]));

                }
                population.shuffleIndices (sample);

                // The population does the work of creating the new population,
                // we tell it which members are to survive.
                population.makeNextGeneration (sample);
                if (debug) {
                        System.out.println ("");
                        System.out.println ("-------------- RANDOM ORDERED ------------------");
                        for (int i = 0 ; i < sample.length ; i++)
                                System.out.println (i+":"+i+":"+population.getMember (i));

                }
                this.pushOutput (population, 0);
        }


}
