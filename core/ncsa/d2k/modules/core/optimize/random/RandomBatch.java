package ncsa.d2k.modules.core.optimize.random;

import ncsa.d2k.modules.core.optimize.util.*;
import java.util.Random;
import java.io.Serializable;

/*
	Random Batch Optimizer

	makes a bunch of random parameter sets, sends them
	all out at once in a solution space

	@author pgroves
	*/

public class RandomBatch extends ncsa.d2k.infrastructure.modules.ComputeModule implements Serializable{

	//////////////////////
	//d2k Props
	////////////////////

	/*
		The seed for the random number generator
	*/
	protected long seed=(long)234;

/*	/* if 1, single objective solutions will be
		created. if >1, multi objectives with
	protected int numObjectivesPerSolution=1;
*/

	/* the number of solutions that will be generated*/
	protected int numSolutions=1000;



	/////////////////////////
	/// other fields
	////////////////////////

	SolutionSpace ss;

	Solution[] computedSolutions;
	Random rand;

	public void doit() throws Exception{
		ss=(SolutionSpace)pullInput(0);
		rand=new Random(seed);
		//computedSolutions=new SOSolution[numSolutions];
		initSolutions();

		ss.setSolutions(computedSolutions);
		pushOutput(ss, 0);

	}

		/*
			calculates random parameters for all the solutions
			at once. done like this so we only have to go through
			the 'instanceof' if statements once and not every
			pass through the loop
			*/
	private void initSolutions(){
		Range[] ranges=ss.getRanges();

		ss.createSolutions(numSolutions);
		computedSolutions=ss.getSolutions();
		double d;
		for(int s=0; s<numSolutions; s++){
			for(int p=0; p<ranges.length; p++){

				d = rand.nextDouble ();
				d = ranges [p].getMin () + d *
					(ranges [p].getMax () - ranges [p].getMin ());
				computedSolutions[s].setDoubleParameter(d, p);
			}
		}

	}



	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Makes parameter sets at random, sends them out as a solution space"+
				"<br>Properties:<ul><li>Seed-The seed used by the random"+
				"number generator<li>NumSolutions-the number of random parameter sets to make"+
				"and evaluate</ul>";
	}

	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return s;
	}

	public String getInputInfo(int index){
		if(index==0){
			return "The solution space object containing definitions for the ranges to bind the parameters to";
		}

		else
			return "No such input";
	}

	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return s;
	}

	public String getOutputInfo(int i){
		if(i==0){
			return "The random solutions that will need to be evaluated ";
		}
		else
			return "no such output";
	}

	////////////////////
	//D2K Property get/set
	///////////////////
	public int getNumSolutions(){
		return numSolutions;
	}
	public void setNumSolutions(int i){
		numSolutions=i;
	}
	public long getSeed(){
		return seed;
	}
	public void setSeed(long l){
		seed=l;
	}

}







