package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;


/**
NewNsgaPopulation.java
 */

/**
This class is used to create a NSGA population by combining rank 1 solutions
 of two NSGA populations. It has a function filtering that will remove all 
the dominated member of the combined rank1 solutions of the two populations.  

 */

public class NewNsgaPopulation extends ConstrainedNsgaPopulation implements Serializable {

    public NewNsgaPopulation(NsgaPopulation pop1, NsgaPopulation pop2){
	//pass the information to the ConstrainedNsgaPopulation constructor
	// both the populations, pop1 and pop2, encodes the same 
	// multiobjective problem and hence the same objective function, traits
	// the population size is the sum of the rank1 solutions of pop1 
	// and pop2
	super(pop1.getTraits(), pop1.getObjectiveConstraints(),(((pop1.getParetoFronts()).getFrontSize(0)) +((pop2.getParetoFronts()).getFrontSize(0)))   , pop1.getTargetFitness());
	// number of memebers of this population
	int numMembers;
	// population size of the pop passed as the first 
	//argument to the constructor
	int popsize1 = pop1.size();
	// population size of the pop passed as the second 
	//argument to the constructor
	int popsize2 = pop2.size();
	//some variables
	int i,jj;
	//pareto fronts of pop1
	ParetoFront frontofpop1;
	//pareto fronts of pop2
	ParetoFront frontofpop2;
	frontofpop1 = pop1.getParetoFronts();
	frontofpop2 = pop2.getParetoFronts();
	//number of members in this population is equal to the sum of
	//the number of rank 1 solutions in pop1, and the number of 
	//solutions of rank 1 in pop2
	numMembers = frontofpop1.getFrontSize(0) + frontofpop2.getFrontSize(0);
	// memebers having rank 1 in pop1
	int [] addmem = frontofpop1.getFront(0);
	// copy the rank 1 memebers of pop1 to this population
	for(i=0;i<(frontofpop1.getFrontSize(0));i++){
	    // a check must be made to see if the member of pop1 
	    // is not null
	    if(pop1.combinedPopulation[addmem[i]] !=null){
		(this.members[i]).copy((Individual)(pop1.combinedPopulation[addmem[i]]));
	    }
	    else {
		i--;
	    }
	}
	// memebers having rank 1 in pop2
	addmem = frontofpop2.getFront(0);
	// copy the rank 2 memebers of pop1 to this population
	for(jj=0;jj<(frontofpop2.getFrontSize(0));jj++,i++){
	    // a check must be made to see if the member of pop2 
	    // is not null
	    if(pop2.combinedPopulation[addmem[jj]] != null){
		(this.members[i]).copy((Individual)(pop2.combinedPopulation[addmem[jj]]));
	    }
	    else{
		i--;
	    }
	}

	//   Create an array that will contain the pointers to the combined population.
	i = 0;
	combinedPopulation = new NsgaSolution [numMembers*2];
	for (; i < numMembers ; i++)
	    combinedPopulation [i] = (NsgaSolution) members [i];
	for (int j = 0 ; j < numMembers ; i++, j++)
	    combinedPopulation [i] = (NsgaSolution) nextMembers [j];

    }

    // this function performs a nondominated sort on the 
    //members of this population. It keeps only the nondominated 
    //members of the population and filters out the dominated members

    public void filtering(){
	// just to ensure that the current generation is not set to 0
	this.currentGeneration = 10;
	// do nondominated sort on the members of this population
	this.doNonDominatedSort();
	// get the nondominated members of this population
	int [] currentFront = fronts.getFront(0);
	// this is the new size of the population
	int newsize2 = fronts.getFrontSize(0);
	// calculate the crowding distance of the members of the population
	computeCrowdingDistance (currentFront, newsize2);
	int newsize =0;
	int zz;
	for(zz=0;zz<newsize2;zz++){
	    if(((combinedPopulation[currentFront[zz]].getCrowdingDistance ())!=0) &&( currentFront[zz] < members.length))
		newsize++; 
	}


	//create a temporary array of memebers, membertmp
	//assign membertemp all the non-dominated individuals
	// then assign membertemp as the current population
	Individual [] membertmp = new Individual[newsize];
	int i;
	int count =0;

	if (traits [0] instanceof BinaryRange) {

	    // Set up the membertemp
	    membertmp = new MOBinaryIndividual [newsize];
	    for ( i = 0 ; i < newsize ; i++) {
		membertmp[i] = new MOBinaryIndividual ((BinaryRange []) traits,
						       objectiveConstraints);
	    }

	} else if (traits [0] instanceof DoubleRange) {
	    // Set up the membertemp
	    membertmp = new MONumericIndividual [newsize];
	    for ( i = 0 ; i < newsize ; i++) {
		membertmp[i] = new MONumericIndividual ((DoubleRange []) traits, objectiveConstraints);
	    }
	} else if (traits [0] instanceof IntRange) {
	    // Set up the membertemp
	  
	} else {
	    System.out.println ("What kind of range is this?");
	}

	zz=0;
	//assign the nondominated individuals to the meber temp
	for(i=0;i<newsize2;i++){
	    if(((combinedPopulation[currentFront[i]].getCrowdingDistance ())!=0) &&( currentFront[i] < members.length))
		membertmp[zz++]=(Individual)combinedPopulation[currentFront[i]];
	}
	// assign membertemp to the current population
	members = membertmp;

    }
}













