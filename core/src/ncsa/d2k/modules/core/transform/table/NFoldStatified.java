package ncsa.d2k.modules.core.transform.table;





import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.table.NFoldExTable;
import java.util.*;
import gnu.trove.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class NFoldStatified extends NFoldCrossValidation {
	Hashtable uniqueOutputToRows;
	TIntArrayList testIndices, trainIndices;

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "NFoldStatified";
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
	 * get the input information for each input
	 * @param i the index of the input
	 * @return the input information
	 */
	public String getInputInfo(int i){
		switch (i) {
			case 0: return "<p>      This is the original table from which we will construct the test and       train tables.    </p>";
			default: return "No such input";
		}
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Test Table";
			case 1:
				return "Train Table";
			case 2:
				return "Number Folds";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getOutputInfo(int i){
		switch (i) {
			case 0: return "This is the test table.";
			case 1: return "This is the table containing the training data.";
			case 2: return "This is the number of folds.";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.TestTable","ncsa.d2k.modules.core.datatype.table.TrainTable","java.lang.Integer"};
		return types;
	}

	public String getModuleInfo(){
		return "<p>      Overview: This module N-fold cross validation with a twist(see module       info for"+
			" &quot;NFoldTTables&quot;). It will identify examples having the same       output features,"+
			" and attempt to distribute those examples evenly between       the test and train sets.    </p>"+
			"    <p>      Detailed Description: For each table input, this modules will execute n       times"+
			" where n is the number of folds specified in the <i>Number Folds</i>       property. At each"+
			" execution, it will produce one test table and one       train table. The data is initially"+
			" divided into n equally sized chunks       of data, that is each chunk contains nearly the same"+
			" number of examples.       Each time the module executes, it will hold out a different chunk"+
			" of the       data for testing. This hold out data should be about 1/n of the data,       and"+
			" this will constitute the test data. The training data will be the       other chunks of data"+
			" combined into a single test table. This should       represent n-1/n of the entire dataset."+
			" The number of folds is also       output, but this value is only output once, the first time"+
			" the module       executes.    </p>    <p>      Additionally, this module will identify examples"+
			" with the same output       features, and distribute those examples evenly among the test and"+
			" train       sets.    </p>    <p>      Data Type Restrictions: This module has no explicit data"+
			" type       restrictions, however the majority of the supervised learning algorithms       are"+
			" only prepared to deal with floating point numbers, so data       conversion may be need to"+
			" be done, and if so, it should be done upstream       from this module. Otherwise, it will be"+
			" done repeated, that is, on each       fold.     </p>    <p>      Scalability: This algorithm"+
			" expects a limited set of distinct output       features. Typically, the output features will"+
			" be nominals of some sort.       If the number of distinct output sets is large, so too will"+
			" be the       memory requirements for this module.     </p>    <p>      Trigger Criteria: When"+
			" this module receives an input, it will execute <i>       Number Folds</i> times, where <i>Number"+
			" Folds</i> is the property the       user sets.    </p>";
	}
	String once;
	public void beginExecution() {
		super.beginExecution();
		once = null;
	}

	protected void setup(){
		once = new String("first");
		table = (Table) this.pullInput (0);
	}

	/**
			Does things, especially 'it'
	*/
	public void doit () throws Exception {
		if (once == null) {
			setup ();
		}

		createUniqueOutputToRowsHash();
		createTestTrainSets();

		// Set up the train and test sets indices
		//convert a Vector of Integer objects to an array of ints
		int testing [] = new int [testIndices.size()];
		for (int i=0; i<testing.length; i++) {
			testing[i] = testIndices.get(i);
		}

		int training [] = new int [trainIndices.size()];
		for (int i=0; i<training.length; i++) {
			training[i] = trainIndices.get(i);
		}

		// now create a new table.
		ExampleTable examples = table.toExampleTable();
		examples.setTrainingSet (training);
		examples.setTestingSet (testing);

		TestTable testT = examples.getTestTable();
		TrainTable trainT = examples.getTrainTable();

		this.pushOutput (testT, 0);
		this.pushOutput (trainT, 1);
		if(numFires==0){
			this.pushOutput (new Integer(N), 2);
		}

		numFires++;

		totalFires++;
		  if (numFires == N){
				numFires=0;
		}
	}

	/**
	 * Hashtable : key - a Vector of output columns. value - Vector of row no Integers where this occurs
	 * For a row of data, create a vector of output columns.
	 *    If this vector is a key of the Hashtable
	 *        Get the vector of row numbers where this output occurs;
	 *        Add the current row number to this vector and put it back into the hash table
	 *    Else if this vector is not present in the hashtable
	 *        Create a new entry in the table - key: this vector; value: a new Vector
	 *        containig the current row number.
	 * Repeat this check for all rows.
	 */
	private void createUniqueOutputToRowsHash() {
		uniqueOutputToRows = new Hashtable();
		Vector    output; //a vector that holds the output column values for a particular row
		TIntArrayList rowIndices;

		int[] outputCols = table.toExampleTable().getOutputFeatures();

		for (int r=0; r< this.table.getNumRows(); r++) {
			output = new Vector(outputCols.length); // build a vector v of the outputCols
			for (int c=0; c< outputCols.length; c++) // of every row
				output.add(table.getString(r,outputCols[c]));

			// try to add output to the HashSet uniqueOutput,
			if (uniqueOutputToRows.containsKey(output)){ // success: lookup output in Hashtable
				rowIndices = (TIntArrayList)uniqueOutputToRows.get(output);
				rowIndices.add(r);
				uniqueOutputToRows.put(output, rowIndices);
			}
			else {    // failure: create a new entry in the Hashtable
				rowIndices = new TIntArrayList(1);
				rowIndices.add(r);
				uniqueOutputToRows.put(output, rowIndices);
			}
		}
	}

	protected void createTestTrainSets() {
		testIndices = new TIntArrayList();
		trainIndices = new TIntArrayList();
		Random rdm0 = new Random(this.seed);
		Random rdm = new Random(this.seed);

		Enumeration keyEnum = uniqueOutputToRows.keys();
		while ( keyEnum.hasMoreElements() ) {
			TIntArrayList rowIndices = (TIntArrayList) uniqueOutputToRows.get(keyEnum.nextElement());
			if (rowIndices.size() < N) {         //if the number of row indices retrived 'n' is < N
				for (int i=0; i<rowIndices.size(); i++) {
					int coin = rdm0.nextInt(2);//add them randomly to either the test or the train set
					if (coin == 0)
						testIndices.add(rowIndices.get(i));
					else if (coin == 1)
						trainIndices.add(rowIndices.get(i));
				}//for
			}//if
			else {
				TIntHashSet testIndicesSet = new TIntHashSet();//randomly pick (n/N) of them
				while (testIndicesSet.size() < (int)(rowIndices.size()/N)){
					int index = rdm.nextInt(rowIndices.size());
					testIndicesSet.add(rowIndices.get(index));
				}//while

				testIndices.add(testIndicesSet.toArray());
				for (int i=0; i<rowIndices.size(); i++) {
					if (! testIndicesSet.contains(rowIndices.get(i)))
						trainIndices.add(rowIndices.get(i));
				}//for
			}//else
		}//while
	}//createTestTrainSets
}

