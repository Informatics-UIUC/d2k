package ncsa.d2k.modules.core.optimize.random;

import ncsa.d2k.modules.core.optimize.util.*;
import java.util.Random;
import java.io.Serializable;

/*
	Random Optimizer

	makes a bunch of random parameter sets, sends them one
	by one to get evaluated, puts them all in an array and
	pushes it out at the end

	@author pgroves
	*/

public class RandomOptimizer extends ncsa.d2k.infrastructure.modules.ComputeModule implements Serializable{

	//////////////////////
	//d2k Props
	////////////////////

	/*
		The seed for the random number generator
	*/
	protected long seed=(long)234;

/*	 if 1, single objective solutions will be
		created. if >1, multi objectives with
	protected int numObjectivesPerSolution=1;
*/

	/* the number of solutions that will be generated*/
	protected int numSolutions=1000;




	/////////////////////////
	/// other fields
	////////////////////////
	/* we need to get the SolutionSpace only once,
	 then don't need to wait for the input
	 */

	protected boolean haveSpace=false;
	/*
		isReady, only take in the
		ranges once, fire 'numSolutions'
		times
	*/
	public boolean isReady(){
		if(!haveSpace){
			return (inputFlags[0]>0);
		}else{
			return (inputFlags[1]>0);
		}
	}


	SolutionSpace ss;
	Random rand;
	Solution[] computedSolutions;
	int counter;

	public void doit() throws Exception{
		if(haveSpace){
			computedSolutions[counter]=(Solution)pullInput(1);
			counter++;
			//when we're done
			if(counter>=numSolutions){
				this.finishUp();
				return;
			}
			pushOutput(ss.getSolutions()[counter],0);
		}
		if(!haveSpace){
			ss=(SolutionSpace)pullInput(0);
			rand=new Random(seed);
			if(ss instanceof SOSolutionSpace){
				computedSolutions=new SOSolution[numSolutions];
			}else{
				computedSolutions=new MOSolution[numSolutions];
			}
			counter=0;

			initSolutions();

			haveSpace=true;
			//push the first solution to get things going in the loop
			pushOutput(ss.getSolutions()[0], 0);
		}
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
		Solution[] sols=ss.getSolutions();
		double d;
		for(int s=0; s<numSolutions; s++){
			for(int p=0; p<ranges.length; p++){

				d = rand.nextDouble ();
				d = ranges [p].getMin () + d *
					(ranges [p].getMax () - ranges [p].getMin ());
				sols[s].setDoubleParameter(d, p);
			}
		}
		if(ss instanceof SOSolutionSpace){
			double targ=((SOSolutionSpace)ss).getConvergenceTarget();
			Range[] rn=ss.getRanges();
			ObjectiveConstraints oc=((SOSolutionSpace)ss).getObjectiveConstraints();
			ss=new SOSolutionSpace(rn, oc, targ);
			ss.setSolutions(sols);
		}

	}

	private void finishUp(){
		//System.out.println("RO:counter:"+counter);
		//System.out.println("RO:SolutionsLength:"+computedSolutions.length);
		//for(int i=0;i<counter; i++){
	//		System.out.println(((SOSolution)computedSolutions[i]).getObjective());
//		}
		counter=0;
		haveSpace=false;
		//System.out.print(((SOSolutionSpace)ss).getSpaceDefinitionString());
		//System.out.print(((SOSolutionSpace)ss).statusString());
		//((SOSolutionSpace)ss).getTable().print();
		ss.setSolutions(computedSolutions);
		//ss.computeStatistics();
		/*if(ss instanceof SOSolutionSpace){
			pushOutput(ss.getSolutions()
						[((SOSolutionSpace)ss).getBestSolution()], 2);
		}*/
		pushOutput(ss, 1);
		wipeFields();
	}

	private void wipeFields(){
		counter=0;
		haveSpace=false;
		ss=null;
		rand=null;
		computedSolutions=null;
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Makes parameter sets at random, sends solutions holding them to be evaluated,"+
				" compiles a list (array) containing all the evaluated solutions and then passes"+
				"that array at the end.<br>Properties:<ul><li>Seed-The seed used by the random"+
				"number generator<li>NumSolutions-the number of random parameter sets to make"+
				"and evaluate</ul>";
	}

	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.optimize.util.SolutionSpace",
					"ncsa.d2k.modules.core.optimize.util.Solution"};
		return s;
	}

	public String getInputInfo(int index){
		if(index==0){
			return "The solution space object containing definitions for the ranges to bind the parameters to";
		}
		if(index==1){
			return"The individual, evaluated solutions (sent from this module to an evaluator then back)";
		}
		else
			return "No such input";
	}

	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.optimize.util.Solution",
					"ncsa.d2k.modules.core.optimize.util.SolutionSpace",
					/*"ncsa.d2k.modules.compute.learning.optimize.util.Solution"*/};
		return s;
	}

	public String getOutputInfo(int i){
		if(i==0){
			return "The random solution that will need to be evaluated then sent back";
		}
		if(i==1){
			return "All of the evaluated solutions in a solution space object";
		}
		/*if(i==2){
			return "The best solution, available only if this is a single-objective optimization";
		}*/

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







