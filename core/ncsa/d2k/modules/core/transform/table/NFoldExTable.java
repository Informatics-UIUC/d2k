package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.io.Serializable;
/*
	NFoldExTable

	Takes in a single table, makes N ExampleTables that have different,
	exhaustive subsets set to the trainSet and testSet

	@author: Peter Groves w/ much cut and paste from Tom Redman's code
	*/

public class NFoldExTable extends ncsa.d2k.core.modules.DataPrepModule 
{
	public String getInputInfo(int i){
		switch (i) {
			case 0: return "The table that contains the data";
			default: return "No such input";
		}
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getOutputInfo(int i){
		switch (i) {
			case 0: return "The ExampleTables with their train and test sets set";
			case 1: return "The N that was set in the properties";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","java.lang.Integer"};
		return types;
	}

	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Will produce and push N ExampleTables with the test and train sets     created. The test sets are of size (1/N)*numExamples and the train sets     the rest. The sets are are randomly created based on the seed. PROPS: N -     the number of exampleTables to make, Seed - the basis of the random     subsampling, allows the user to create the same subsets or insure it     changes  </body></html>";
	}

	/** number of times we have fired. */
	int numFires = 0;

	/** the break points. */
	int [] breaks = null;

	/** the data table. */
	Table table = null;

	/** This is an array of all the indices so we can do an arraycopy. */
	int [] indices = null;

	/** the size of the training set. */
	int trainSize = 0;

	/** the size of the test set. */
	int testSize = 0;

	/** number of folds. */
	int N = 4;


	int numRows;

	long seed = (long)0.00;

	int totalFires=0;

	boolean debug=false;
	/**
		Reset the number of fires when we begin execution.
	*/
	public void beginExecution () {
		numFires = 0;
		breaks = null;
		table = null;
		indices = null;
		totalFires=0;
		numRows=0;
	}

	/**
		Fires N times where n is the number of folds.
	*/
	public boolean isReady () {
		/*if (numFires < N)
			return true;
		if(inputFlags[0]>0){
			return true;
		}*/
		if(numFires==0){
			return super.isReady();
		}else{
			return true;
		}
	}

	/**
		Setup the indexing array and shuffle it randomly.
	*/
	protected void setup () {

		// First time through, init the table field, compute the
		// breaks and indices
		table = (Table) this.pullInput (0);
		//System.out.println("XVal: pullingInput:"+numFires);
		numRows = table.getNumRows ();
		breaks = this.getTableBreaks (numRows);
		indices = new int [numRows];
		for (int i = 0 ; i < numRows ; i++)
			indices [i] = i;

		Random rand=new Random(seed);

		// Let's shuffle them
		for (int i = 0 ; i < numRows ; i++) {
			int swap = (int) (rand.nextDouble () * numRows);
			if (swap != 0)
				swap--;
			int old = indices[swap];
			indices [swap] = indices [i];
			indices [i] = old;
		}

		// Randomly
		if(N>numRows){
			testSize=1;
		}else{
			testSize = breaks[0];//(int) (((double)1.0/(double)(breaks.length+1)) * (double)numRows);
		}
		trainSize = numRows - testSize;
	}

	/**
		Returns a list of vertices which are the indices of the first index
		of the next set.
		@param orig the number of attributes.
	*/
	int [] getTableBreaks (int orig) {
		int [] tableBreaks;
		double numCols = (double)orig;
		double n = (double)N;

		if(N>orig){
			//if the crossvalidation 'fold' is greater
			//than the number of examples, simply make
			//each example its own set
			tableBreaks = new int[orig-1];
			for(int i=0; i<orig-1; i++){
				tableBreaks[i]=(i+1);
			}

		}else{
			tableBreaks = new int[N-1];
			//int tS = (int) (((double)1.0/(double)(tableBreaks.length+1)) * (double)orig);
			for(int i = 0; i < N-1; i++){
				tableBreaks[i] = /*(i+1)*tS;*/(int) (((double)(i+1)/n)*numCols);
			}
		}
				return tableBreaks;
	}

	/**
		Does things, especially 'it'
	*/
	public void doit () throws Exception {
		if (breaks == null) {
			setup ();
		}

		// Set up the train and test sets indices
		int testing [] = new int [testSize];
		int training [] = new int [trainSize];

		makeSets(testing, training);

		// now create a new vertical table.
		ExampleTable examples = table.toExampleTable();
		examples.setTrainingSet (training);
		examples.setTestingSet (testing);

		this.pushOutput (examples, 0);
		if(numFires==0){
			this.pushOutput (new Integer(breaks.length+1), 1);
		}

		numFires++;
		System.out.println(numFires);
		if(numFires==(breaks.length+1)){
			numFires=0;
			breaks=null;
			table=null;
			indices=null;
		}

	}

	/*
		does the actual assigning of the indices to test and train sets
	*/

	protected void makeSets(int[] testing, int[] training){
		if (numFires == 0) {
			System.arraycopy (indices, 0, testing, 0, breaks [0]);
			System.arraycopy (indices, breaks[0], training, 0, indices.length-breaks [0]);
			testSize=breaks[1]-breaks[0];
			trainSize=numRows-testSize;
		} else {
			int startTestingSet = breaks [numFires-1];
			System.arraycopy (indices, 0, training, 0, startTestingSet);
			System.arraycopy (indices, startTestingSet, testing, 0,
					testing.length);
			System.arraycopy (indices, startTestingSet+testing.length, training, startTestingSet,
					indices.length - breaks [numFires-1]-testing.length);
			if(breaks.length>numFires){
				if(breaks.length>(numFires+1)){
					testSize=breaks[numFires+1]-breaks[numFires];
				}else{
					testSize=indices.length-breaks[numFires];
				}
				trainSize=numRows-testSize;

			}
			//trainSize=numRows-testSize;
		}
	}

	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////


	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public void setSeed(long d){
		seed=d;
	}

	public long getSeed(){
		return seed;
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "NFoldExTable";
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
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
