package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
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
		switch (i) {
			case 0: return "The Test Table";
			case 1: return "The Train Table";
			case 2: return "The N that was set in the properties";
			default: return "No such output";
		}
/*		switch (i) {
			case 0: return "The Example Table";
			case 1: return "The Test Table";
			case 2: return "The Train Table";
			case 3: return "The N that was set in the properties";
			default: return "No such output";
		}
        */
	}

	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.TestTable",
                                  "ncsa.d2k.modules.core.datatype.table.TrainTable",
                                  "java.lang.Integer"};
/*		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
                                  "ncsa.d2k.modules.core.datatype.table.TestTable",
                                  "ncsa.d2k.modules.core.datatype.table.TrainTable",
                                  "java.lang.Integer"};
        */
		return types;
	}

	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Will produce and push N TestTable/TrainTable pairs. The test sets are of     size (1/N)*numExamples and the train sets the rest. The sets are are     randomly created based on the seed. PROPS: N - the number of exampleTables     to make, Seed - the basis of the random subsampling, allows the user to     create the same subsets or insure it changes  </body></html>";
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

/*        this.pushOutput(examples, 0);
        this.pushOutput(testT, 1);
        this.pushOutput(trainT, 2);
        */

        if(numFires==0){
			this.pushOutput (new Integer(breaks.length+1), 2);
//            this.pushOutput (new Integer(breaks.length+1), 3);
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
            return "NFoldTTables";
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
            case 2:
                return "output2";
            case 3:
                return "outpur3";
            default: return "NO SUCH OUTPUT!";
        }
    }
}
