package ncsa.d2k.modules.core.transform.table;
import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/*
	NFoldTTables

	Takes in a single table, makes N ExampleTables that have different,
	exhaustive subsets set to the trainSet and testSet, but pushes out
	a TrainTable and TestTable made from the ExampleTable

	@author: Peter Groves w/ much cut and paste from Tom Redman's code
	*/

public class NFoldTTables extends NFoldExTable{

	public String getOutputInfo(int i){
		switch (i){
			case 0 :{
				return "The Test Table";
			}
			case 1: {
				return "The Train Table";
			}
			case 2:{
				return "The N that was set in the properties";
			}
			default: {
				return "No such output";
			}
		}
	}

	public String[] getOutputTypes(){
		String[] types={
			"ncsa.d2k.modules.core.datatype.table.TestTable",
			"ncsa.d2k.modules.core.datatype.table.TrainTable",
			"java.lang.Integer"
		};
		return types;
	}

	public String getModuleInfo(){
		String str= "Will produce and push N TestTable/TrainTable pairs";
		str+=".  The test sets are of size (1/N)*numExamples and the train sets the rest.";
		str+=" The sets are are randomly created based on the seed. PROPS: N - the num";
		str+="ber of exampleTables to make, Seed - the basis of the random subsampling";
		str+=", allows the user to create the same subsets or insure it changes";
		return str;
	}
	int totalFires=0;
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

		this.pushOutput (testT, 0);
		this.pushOutput (trainT, 1);
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
}
