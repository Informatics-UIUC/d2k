package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
	This module measures the success of the population, computing the average fitnessd,
	and identifying the best and worst current individuals in the population. Also, the
	population is queried to determine if we are done or not.
*/
public class MeasureModule extends ncsa.d2k.infrastructure.modules.ComputeModule 	implements Serializable {

	/** the last element crossed. */
	private int last = 0;

	///////////////////////////////
	// Properties.
	///////////////////////////////

	private boolean debugging = false;

	/**
		set the rankFlag.

		@param score is a boolean indicating if rankFlag is on or off.
	*/
	public void setDebugging (boolean score) {
		this.debugging = score;
	}

	/**
		get the rankFlag.

		@returns true if rankFlag is on.
	*/
	public boolean getDebugging () {
		return this.debugging;
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>We will generate this output population only if we are not done. </Text>  </Info></D2K>";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>The input population will be examined for convergence or any other stopping criterion. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Measure Success\">    <Text>THis is the module that will determine when the population is done. The population object is responsible for making the call, it will stop when we have reached the maximum number of interations (property of the population generation module), or if we have reached the target fitness (also a property of the population generation module). </Text>  </Info></D2K>";
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
		System.out.println ("MeasureModule : done and triggering");
			setTriggers = false;
			for(int i = 0; i < oTriggers.length; i++)
				oTriggers [i].trigger ();
		}
	}

	/**
		Do a standard evaluation, the best the worst individuals are sought, and the average
		fitness is computed.

		@param outV the array to contain output object.
	*/
	public void doit () {
		Population population = (Population) this.pullInput (0);
		population.computeStatistics ();

		if (debugging) {
			System.out.println (population.statusString ());
		}

		// set if we have hit the target fitness.
		if (population.isDone ()) {
			setTriggers = true;
		}else
			this.pushOutput (population, 0);
	}
}
