package ncsa.d2k.modules.core.optimize.simplex;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
import java.util.Vector;


/**	Simplex Optimizer<br>

	takes a solution space and performs a simplex
	search, using the first entry in the solutions[]
	of the input space, if available. Otherwise, generates
	a new random one as the basis.  The initial simplex
	is generated using a tiled initialization and the algorithm
	is a variable size simplex, both described at:<br>
	<b>http://www.multisimplex.com/simplexbook/index.htm</b>

	@author pgroves

	*/

public class SimplexOptimizer extends ComputeModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////

	/*if true, will keep all solutions explored,
	and return them as part of the output solution
	space. if false, only the best solution will be
	returned in the solution space*/
	protected boolean keepAllSolutions=false;


	/*every iteration, the difference between
	the best and worst solution will be calculated,
	and if it is less than this value, the optimization
	will end*/
	protected double asymptoticTolerance=.01;


	/*what fraction of the space to have the initial
	simplex span*/
	protected double initWidthFraction=.1;

	/*the system can either create its own random
	seed for initialization or use the input solution
	in the second input. if false it won't wait
	for that input before firing*/
	protected boolean useInputSeedSolution=false;

	/*some println*/

	protected boolean debug=false;

	/////////////////////////
	/// other fields
	////////////////////////
	/*the input solution space, will also be the
	final output (with the optimum set)*/
	SOSolutionSpace space;

	SOSolution[] simplex;
	Vector allSolutions;

	/*used when testing if an expansion should occur*/
	SOSolution savedReflection;
	/*the solution to output to be evaluated, pulled back in*/
	SOSolution evaluateSolution;

	//indices of the best solution, the discard solution,
	//and the next solution to discard (worst)
	int best;
	int kill;
	int nextKill;

	//some module-state flags
	boolean haveSpace=false;
	boolean initEvaluations=true;
	boolean tryingE=false;
	boolean contraction=false;


	/*the index in the simplex that
	the pulled in solutions should be
	assigned to during initialization*/
	int counter;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(!haveSpace){
			return ((inputFlags[0]>0)&&//have the space
					((inputFlags[1]>0)||//and have the seed
					 (!useInputSeedSolution)));//or aren't using the seed
		}
		return(inputFlags[2]>0);
	}
	public void endExecution(){
		//space=null;
		//simplex=null;
		allSolutions=null;
		if(debug){
			printStuff();
			space.computeStatistics();
			System.out.println(space.statusString());

		}
		clearFields();
		return;
	}
	public void beginExecution(){
		clearFields();
		return;
	}

	public void clearFields(){
		haveSpace=false;
		initEvaluations=true;
		tryingE=false;
		contraction=false;
		allSolutions=null;
		simplex=null;
	}

	public void printStuff(){
		System.out.println();
		space.setSolutions(simplex);
		((ncsa.d2k.modules.core.datatype.table.TableImpl)space.getTable()).print();

		System.out.println();
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(!haveSpace){
			space=(SOSolutionSpace)pullInput(0);

			if(keepAllSolutions){
				allSolutions=new Vector();
			}


			simplex=getInitialSimplex(space);

			haveSpace=true;

			pushOutput(simplex[0], 1);

			counter=0;

			return;
		}

		evaluateSolution=(SOSolution)pullInput(2);

		/*if(debug){
			System.out.println(evaluateSolution);
		}*/

		if(initEvaluations){
			simplex[counter]=evaluateSolution;
			counter++;

			if(counter<simplex.length){//still don't have them all
				pushOutput(simplex[counter], 1);
				System.out.println("Simplex:"+(counter+1));
				return;
			}else{//have them all, get started
				initEvaluations=false;
				initRank();
				pushOutput(standardReflection(), 1);
				return;
			}
		}

		if(this.isDone()){
			this.finishUp();
			pushOutput(space, 0);
			clearFields();
			//pushOutput(simplex[best], 2);
		}else{
			simplexIteration();
		}
	}

	/*returns an initial simplex with k+1 points where k
	is the dimensionality of the solution space*/
	private SOSolution[] getInitialSimplex(SOSolutionSpace ss){

		int dimCount=space.getRanges().length;
		//find/make the origin
		SOSolution origin;
		if(useInputSeedSolution){
			origin=(SOSolution)pullInput(1);
		}else{
			ss.createSolutions(1);
			origin=(SOSolution)ss.getSolutions()[0];
		}

		//we'll have the space make them so we don't
		//have to worry about range types
		ss.createSolutions(dimCount);
		SOSolution[] simplex=new SOSolution[dimCount+1];
		simplex[0]=origin;
		for(int i=1; i<simplex.length; i++){
			simplex[i]=(SOSolution)ss.getSolutions()[i-1];
		}

		//we'll need these several times in the loop,
		//'could save some time
		double root2=Math.sqrt(2);
		double rootd=Math.sqrt(dimCount+1);

		double min, max, stepSize, bigStep, littleStep, param;

		for(int d=0; d<dimCount; d++){//for every parameter

			//need a sensible increment for each range
			min=space.getRanges()[d].getMin();
			max=space.getRanges()[d].getMax();
			//going to make it so the intial set
			//will cover 1/2 the length of every dimension
			stepSize=(max-min)*(initWidthFraction);

			//find the big and little increments
			bigStep=stepSize*(rootd+dimCount-1)/(dimCount*root2);
			littleStep=stepSize*(rootd-1)/(dimCount*root2);

			//make the new solutions: imagine a table with
			//every solution being a row.  every parameter
			//is it's origin plus the littleStep except those
			//on the center diagonal, which have bigStep added
			for(int i=0; i<dimCount; i++){//for each solution
				if(i==d){
					param=origin.getDoubleParameter(d)+
						bigStep;
						//if out of bounds, go other direction
						if((param<min)||(param>max)){
							param=origin.getDoubleParameter(d)-
								bigStep;
						}
				}else{
					param=origin.getDoubleParameter(d)+
						littleStep;
						if((param<min)||(param>max)){
							param=origin.getDoubleParameter(d)-
								littleStep;
						}
					simplex[i+1].setDoubleParameter(param, d);
				}

			}
		}
		return simplex;
	}

	/*finds the best, worst, next to worst, when initializing
	*/
	private void initRank(){
		best=0;
		kill=0;
		nextKill=0;

		int betterThan;
		for (int i=1; i<simplex.length; i++){
			if(0<comparePoints(i, best)){//i better than best
				best=i;
			}
			if(0<comparePoints(kill, i)){//kill better than i
				nextKill=kill;
				kill=i;
			}
		}
		/*System.out.println("Best"+best+" Kill:"+kill+" NextKill:"+nextKill);
		*/
	}

	/*
		kills the worst, finds the next worst and best, puts the
		new point it finds in the simplex and returns the index of
		the new point (which will be pushed out to evaluate)
	*/
	private void simplexIteration(){

		//we are seeing if an expansion is worthwhile
		if(tryingE){
			if(!isBetterThanESol(best)){
				//use the expansion, the usable slot
				//in the simplex is where the kill solution
				//was/is
				simplex[kill]=evaluateSolution;

			}else{
				simplex[kill]=savedReflection;
			}
			tryingE=false;
		}
		//a contraction was performed and sent to be
		//evaluated: it goes in, no questions asked
		else if(contraction){
			simplex[kill]=evaluateSolution;
			contraction=false;
		}
		//if the solution is mediocre, b/w nextKill and Best
		else if((!isBetterThanESol(nextKill))
			&&(isBetterThanESol(best))){
				simplex[kill]=evaluateSolution;
		}
		//if the reflection was the best yet
		else if(!isBetterThanESol(best)){
			savedReflection=evaluateSolution;
			tryingE=true;
			pushOutput(expandReflection(), 1);
			return;
		}
		//if reflection between kill and nextKill
		else if((isBetterThanESol(nextKill))&&
			(!isBetterThanESol(kill))){
			contraction=true;
			pushOutput(contractReflection(), 1);
			return;
		}
		//if reflection worse than kill
		else if(isBetterThanESol(kill)){
			contraction=true;
			pushOutput(contractKill(), 1);
			return;
		}

		//if we made it this far, its the 'normal' case
		//with no contractions or expansions
		findBestAndNextKill();
		pushOutput(standardReflection(), 1);

	}

			/////////////////////////
			//finding the next point
			////////////////////////

	/*
		reflects the worst solution over the centroid.
		if this new point is out of the bounds of the
		search space, it assigns the min or max of
		that parameter's range to the parameter as
		appropriate
	*/
	private SOSolution standardReflection(){
		/*if(debug){
			System.out.println("Standard Reflection");
		}*/
		//this is what we'll manipulate and push out
		//since it's the kill, we can write over it
		SOSolution ref=(SOSolution)simplex[kill].clone();

		double[] deltas=findParamDeltas(kill, kill);
		double newParam;
		for(int d=0; d<deltas.length; d++){//for each dimension
			//reflect the point over the centroid,
			//so add 2x the distance (delta) to itself
			newParam=2*deltas[d]+ref.getDoubleParameter(d);
			//System.out.println(newParam);
			//System.out.println("Min:"+space.getRanges()[d].getMin());
			//System.out.println("Max:"+space.getRanges()[d].getMax());

			if(newParam<space.getRanges()[d].getMin()){
				newParam=space.getRanges()[d].getMin()+
							initWidthFraction*Math.random()*(
							space.getRanges()[d].getMax()-
							space.getRanges()[d].getMin());

			}else if(newParam>space.getRanges()[d].getMax()){

				newParam=space.getRanges()[d].getMax()-
							initWidthFraction*Math.random()*(
							space.getRanges()[d].getMax()-
							space.getRanges()[d].getMin());
			}
			//System.out.println(newParam);
			//System.out.println();
			ref.setDoubleParameter(newParam, d);

		}
		return ref;

	}
	/*
		basically the same as standard reflection, but
		move in the same direction we've already gone
	*/
	private SOSolution expandReflection(){
		/*if(debug){
			System.out.println("Expansion");
		}*/

		//this is what we'll manipulate and push out
		//since it's the kill, we can write over it
		SOSolution ref=(SOSolution)simplex[kill].clone();

		double[] deltas=findParamDeltas(kill, kill);

		double newParam;

		for(int d=0; d<deltas.length; d++){//for each dimension
			//go the amount delta even further away from the centroid
			newParam=evaluateSolution.getDoubleParameter(d)+deltas[d];

			if(newParam<space.getRanges()[d].getMin()){
				newParam=space.getRanges()[d].getMin()+
							initWidthFraction*Math.random()*(
							space.getRanges()[d].getMax()-
							space.getRanges()[d].getMin());
			}else if(newParam>space.getRanges()[d].getMax()){
				newParam=space.getRanges()[d].getMax()-
							initWidthFraction*Math.random()*(
							space.getRanges()[d].getMax()-
							space.getRanges()[d].getMin());
			}

			ref.setDoubleParameter(newParam, d);
		}
		return ref;

	}
	private SOSolution contractReflection(){
		/*if(debug){
			System.out.println("Contraction - reflection");
		}*/

		//this is what we'll manipulate and push out
		//since it's the kill, we can write over it
		SOSolution cont=(SOSolution)simplex[kill].clone();

		//we need to find the distance from the evaluateSolution (the
		//reflection) to the centroid, so put it in the simplex and
		//don't use it in the centroid calc.
		simplex[kill]=evaluateSolution;

		double[] deltas=findParamDeltas(kill,kill);

		for(int d=0; d<deltas.length; d++){//for each dimension
			//find half the distance b/w the centroid and the reflection (evalSol)
			double db=evaluateSolution.getDoubleParameter(d)+(.5*deltas[d]);
			//don't need the bounds check b/c pulling in towards the centroid
			cont.setDoubleParameter(db, d);
		}
		return cont;
	}
	private SOSolution contractKill(){
		/*if(debug){
			System.out.println("Contraction - kill");
		}*/

		//this is what we'll manipulate and push out
		//since it's the kill, we can write over it
		SOSolution cont=(SOSolution)simplex[kill].clone();

		double[] deltas=findParamDeltas(kill,kill);
		for(int d=0; d<deltas.length; d++){//for each dimension
			//find half the distance b/w the centroid and the reflection (evalSol)
			double db=simplex[kill].getDoubleParameter(d)+(.5*deltas[d]);

			//don't need the bounds check b/c pulling in towards the centroid

			cont.setDoubleParameter(db, d);
		}
		return cont;

	}


	/* finds the distances between the centroid of all points
		 that aren't leaveOut and the distanceTo point
		given. (centroid-distanceTo)
	*/
	private double[] findParamDeltas(int leaveOut, int distanceTo){
		double[] centroid=new double[space.getRanges().length];
		for(int s=0;s<simplex.length;s++){
			if(s!=leaveOut){
				for(int dim=0; dim<centroid.length; dim++){
					centroid[dim]+=simplex[s].getDoubleParameter(dim);
				}
			}
		}
		double[] deltas=new double[centroid.length];
		for(int dim=0; dim<centroid.length; dim++){
			centroid[dim]/=(simplex.length-1);// -1 b/c of the leaveOut
			deltas[dim]=centroid[dim]-simplex[distanceTo].getDoubleParameter(dim);
		}
		return deltas;

	}
	/*
		kills the 'kill', puts nextKill to kill, finds a new
		nextKill and a new best
		*/
	private void findBestAndNextKill(){

		kill=nextKill;
		//the very first one to get killed will get missed, oh well
		if(keepAllSolutions){
			allSolutions.add(simplex[kill].clone());
		}

		//find the worst(nextKill) and best
		nextKill=0;

		if(kill==0){
			nextKill=1;
		}

		for(int i=0; i<simplex.length; i++){
			if((i!=kill)/*&&
				(!ignoreKill||(i!=oldKill)*/){

				if(0<comparePoints(i, best)){
					best=i;
				}
				if(0<comparePoints(nextKill, i)){
					nextKill=i;
				}
			}
		}
		/*if(debug)
		System.out.println("Best"+best+" Kill:"+kill+" NextKill:"+nextKill);*/
	}


	/*puts the last of the solutions in allSolutions and puts
	that in the output solution space, if needed
	*/
	private void finishUp(){
		simplex[kill]=evaluateSolution;
		if(keepAllSolutions){
			for(int i=0; i<simplex.length; i++){
				allSolutions.add(simplex[i]);
			}
			SOSolution[] sols=(SOSolution[])(allSolutions.toArray(simplex));
			space.setSolutions(sols);
		}else{
			space.setSolutions(simplex);
		}
		clearFields();
	}

	/* * @returns 1 if a is better than be, 0 if they are equal,
	 *    and -1 if b is better than a.  the points
	 	are the indices in the simplex
	 */
	private int comparePoints(int a, int b){
		return space.getObjectiveConstraints().compare(
						simplex[a].getObjective(),
						simplex[b].getObjective());
	}

	/*tells if the solution at the given index in the simplex
	is better than the solution in the field 'evaluateSolution
	*/
	private boolean isBetterThanESol(int index){
		int ii=space.getObjectiveConstraints().compare(
						simplex[index].getObjective(),
						evaluateSolution.getObjective());
		return(ii>0);
	}

	/* tells if the convergence criteria are met
	*/
	private boolean isDone(){
	/*	if(debug){
			if(counter==30){
				return true;
			}
			else{
				counter++;
			}
		}
		*/
		//if the convergence target defined in the
		//solution space is reached
		if(0<space.getObjectiveConstraints().compare(
			evaluateSolution.getObjective(),
			space.getConvergenceTarget())){
				return true;
		}

		//if the best and worst are within the tolerance distance
		//of each other
		double bestObj=simplex[best].getObjective();
		double worstObj=simplex[nextKill].getObjective();
		if(bestObj>worstObj){
			return (asymptoticTolerance>(bestObj-worstObj));
		}else{
			return(asymptoticTolerance>(worstObj-bestObj));
		}
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.optimize.util.SOSolutionSpace",
						"ncsa.d2k.modules.core.optimize.util.SOSolution",
						"ncsa.d2k.modules.core.optimize.util.SOSolution"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}
			case(1): {
				return "the initialization solution";
			}
			case(2): {
				return "the return/evaluated solution";
			}

			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.optimize.util.SOSolutionSpace",
					"ncsa.d2k.modules.core.optimize.util.SOSolution",
					/*"ncsa.d2k.modules.compute.learning.optimize.util.SOSolution"*/};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}
			case(1): {
				return "";
			}
			case(2): {
				return "The best solution";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public boolean getKeepAllSolutions(){
		return keepAllSolutions;
	}
	public void setKeepAllSolutions(boolean b){
		keepAllSolutions=b;
	}
	public double  getAsymptoticTolerance(){
		return asymptoticTolerance;
	}
	public void setAsymptoticTolerance(double d){
			asymptoticTolerance=d;
	}
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public double  getInitWidthFraction(){
		return initWidthFraction;
	}
	public void setInitWidthFraction(double d){
		initWidthFraction=d;
	}
	public boolean getUseInputSeedSolution(){
		return useInputSeedSolution;
	}
	public void setUseInputSeedSolution(boolean b){
		useInputSeedSolution=b;
	}

	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}
	*/
}







