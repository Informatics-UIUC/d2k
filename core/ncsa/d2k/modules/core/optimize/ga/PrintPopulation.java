
package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
	Prints the population out to the console.
*/
public class PrintPopulation extends ncsa.d2k.infrastructure.modules.OutputModule
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>When we are done we simply pass the population along. </Text>  </Info></D2K>";
			default: return "No such output";
		}
}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Population\">    <Text>This is the population that will be printed. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Print\">    <Text>Print all </Text>    <Text I=\"t\">the </Text>    <Text>individuals in the current population. </Text>  </Info></D2K>";
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
	public void beginExecution () {
		setTriggers = false;
	}

	private boolean setTriggers = false;
	/**
		Set the triggers when this module does converge.
	 */
	protected void activateTriggers()
	{
		// hit all triggers
		if (oTriggers != null && setTriggers)
		{
			setTriggers = false;
			for(int i = 0; i < oTriggers.length; i++)
				oTriggers [i].trigger ();
		}
	}
	boolean sorting = true;
	public void setSorting (boolean sort) { sorting = sort; }
	public boolean getSorting () { return sorting; }
	int numToPrint = 1000;
	public void setNumberToPrint (int sort) { numToPrint = sort; }
	public int getNumberToPrint () { return numToPrint; }

	/**
		Do a standard evaluation, the best the worst individuals are sought, and the average
		fitness is computed.

		@param outV the array to contain output object.
	*/
	public void doit () {
		System.out.println ();
		System.out.println ("-------------------------------------------------------");
		Population population = (Population) this.pullInput (0);
		int num = population.size();
		num = num < numToPrint ? num : numToPrint;
		if (sorting) {
			int [] order = population.sortIndividuals ();
			for (int i = 0 ; i < num ; i++)
				population.printIndividual (order [i]);
		} else
			for (int i = 0 ; i < num ; i++)
				population.printIndividual (i);

		this.pushOutput (population, 0);
	}
}
