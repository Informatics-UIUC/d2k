/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	AssignFitness.java

*/
public class AssignFitness extends ncsa.d2k.infrastructure.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{
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
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>This is the input population, the members of this population will have fitness values when we are done here. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Fitness\">    <Text>The table contains in each column a different measure of fitness, we are only interested in the first, which is assumed to be a number between 0 and 1, higher is better. </Text>  </Info></D2K>";
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
			"ncsa.d2k.util.datatype.Table"};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>This population will have fitness values for each member. </Text>  </Info></D2K>";
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
			"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Assign Fitness\">    <Text>This module will receive a population, and a fitness for each of the individuals in the population. The fitness field of the population member is filled in, one for each fitness table input, until each member of the population has a fitness. This module allows the evaluation of the members to be done by a seperate model builder algorithm. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	int memberCounter = 0;
	Population population = null;

	/**
		This module is ready whenever we get a new population, or when we have already gotten
		a population, and we have not yet filled in all the fitness values for all the members.
		@returns true if we have something to do.
	*/
	public boolean isReady () {
		if (population != null || this.inputFlags [0] > 0)
			if (this.inputFlags [1] > 0)
				return true;

		return false;
	}

	public void endExecution () {
		super.endExecution ();
		population = null;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		if (population == null) {
			memberCounter = 0;
			if (debug) System.out.println ("AssignFitness: Pulling population");
			population = (Population) this.pullInput (0);
		}

		// Assign the fitness to the next member.
		Table fitness = (Table) this.pullInput (1);
		SOSolution member = (SOSolution) population.members [memberCounter++];
		member.setObjective (fitness.getDouble (0, 0));

		// Are we done with this population?
		if (memberCounter == population.members.length) {
			if (debug) System.out.println ("Assign fitness: done, pushing result");
			this.pushOutput (population, 0);
			population = null;
			memberCounter = 0;
		} else
			if (debug) System.out.println ("Assign fitness: "+memberCounter);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

