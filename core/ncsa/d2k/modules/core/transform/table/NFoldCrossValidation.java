package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.beans.PropertyVetoException;

/*
	NFoldCrossValidation (previously NFoldTTest)

	Takes in a single table, makes N ExampleTables that have different,
	exhaustive subsets set to the trainSet and testSet, but pushes out
	a TrainTable and TestTable made from the ExampleTable

	@author: Peter Groves w/ much cut and paste from Tom Redman's code
	*/

public class NFoldCrossValidation extends ncsa.d2k.core.modules.DataPrepModule {
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
		if(numFires==0){
			return super.isReady();
		}else{
			return true;
		}
	}

	/**
		Setup the indexing array and shuffle it randomly.
	*/
	protected void setup () throws Exception {

		// First time through, init the table field, compute the
		// breaks and indices
		table = (Table) this.pullInput (0);
		numRows = table.getNumRows ();
		if (numRows < (N * 2))
			throw new Exception (this.getAlias()+": There must be twice as many rows in a table as the number of folds to do N-Fold Cross Validation.");
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
			testSize = breaks[0];
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
			for(int i = 0; i < N-1; i++){
				tableBreaks[i] = (int) (((double)(i+1)/n)*numCols);
			}
		}
				return tableBreaks;
	}

	/**
	 * get the input information for each input
	 * @param i the index of the input
	 * @return the input information
	 */
	public String getInputInfo(int i){
		switch (i) {
			case 0: return "This is the input table from which we will extract the test table and the     train table.";
			default: return "No such input";
		}
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}


	public String getOutputInfo(int i){
		switch (i) {
			case 0: return "This is the output table containing the train data.";
			case 1: return "The table allows acces to the testing data.";
			case 2: return "This integer contains the number of folds. This is the same value as the     property, numberFolds, which can be set by the user.";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.TestTable","ncsa.d2k.modules.core.datatype.table.TrainTable","java.lang.Integer"};
		return types;
	}

	public String getModuleInfo(){
		return "<p>      Overview: This module provides the mechanism by which N-fold cross       validation"+
			" can be performed. It will produce the number of test and       training tables the user specifies"+
			" in the properties.    </p>    <p>      Detailed Description: For each table input, this modules"+
			" will execute n       times where n is the number of folds specified in the <i>Number Folds</i>"+
			"       property. At each execution, it will produce one test table and one       train table."+
			" The data is initially divided into n equally sized chunks       of data, that is each chunk"+
			" contains nearly the same number of examples.       Each time the module executes, it will hold"+
			" out a different chunk of the       data for testing. This hold out data should be about 1/n"+
			" of the data,       and this will constitute the test data. The training data will be the  "+
			"     other chunks of data combined into a single test table. This should       represent n-1/n"+
			" of the entire dataset. The number of folds is also       output, but this value is only output"+
			" once, the first time the module       executes.    </p>    <p>      Data Type Restrictions:"+
			" This module has no explicit data type       restrictions, however the majority of the supervised"+
			" learning algorithms       are only prepared to deal with floating point numbers, so data  "+
			"     conversion may be need to be done, and if so, it should be done upstream       from this"+
			" module. Otherwise, it will be done repeated, that is, on each       fold.    </p>    <p>  "+
			"    Scalability: The memory requirements of the original data set will       likely dwarf the"+
			" memory requirements of this module. This module will       require an array of integers with"+
			" one entry for each row of the original       table.    </p>    <p>      Trigger Criteria: When"+
			" this module receives an input, it will execute <i>       Number Folds</i> times, where <i>Number"+
			" Folds</i> is the property the       user sets.    </p>";
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

		TestTable testT= examples.getTestTable();
		TrainTable trainT= examples.getTrainTable();

		this.pushOutput (trainT, 0);
		this.pushOutput (testT, 1);

		if(numFires==0){
			this.pushOutput (new Integer(breaks.length+1), 2);
		}

		numFires++;

		totalFires++;
		if(debug)
		System.out.println("Xval:numfires:"+numFires+" totalFires:"+totalFires+" n:"+N);
		if(numFires==(breaks.length+1)){
			numFires=0;
			breaks=null;
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "N-Fold Cross Validation";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Table";
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
				return "Train Table";
			case 1:
				return "Test Table";
			case 2:
				return "Number Folds";
			default: return "NO SUCH OUTPUT!";
		}
	}

   /**
	* make the testing and training sets by just assigning indices.
	* @param testing t the array that will contain the testing set.
	* @param training the array that will contain the training set.
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

	/**
	 * returns the number of folds.
	 * @returns the number of folds.
	 */
	public int getNumberFolds(){
		return N;
	}
	public void setNumberFolds(int n) throws PropertyVetoException {
		if (n < 3)
			throw new PropertyVetoException ("There must be at least 3 folds", null);
		N = n;
	}

	/**
	 * set the random seed
	 * @returns the number of folds.
	 */
	public void setSeed(long d){
		seed=d;
	}
	public long getSeed(){
		return seed;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("numberFolds", "Number of Folds", "The number of folds for the cross validation. This number must be greater than 2.");
		pds[1] = new PropertyDescription ("seed", "Seed", "The seed for the random number generator. If the same seed is used across runs, you should get the same result sets.");
		pds[2] = new PropertyDescription ("debug", "Debug", "If this flag is set, the indices of the train and test sets will output to the console as the module runs.");
		return pds;
	}
}
// Start QA Comments
// 2/24/03 - Received by QA from Loretta & Tom
// 3/5/03  - Ruth starts QA;  Reordered Properties so what user will likely change is first;
//         - Reported ArrayOutOfBounds err to developers.
// End QA Comments
