package ncsa.d2k.modules.core.transform.table;
import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/*
	works just like NFoldTTables except that the test tables
	are all the same tables w/ different test sets.  this
	way, when your done cross-validating you have a single table
	w/ a prediction for every record.  unfortunately, you
	now will have to use a sequencing module so that this module
	doesn't override the testset indices field before it gets used

	*/

public class NFoldTTables_inplace extends NFoldTTables{


	protected TestTable testT;
	protected TrainTable trainT;

	boolean firstRun;

	public void beginExecution(){
		firstRun=true;
		super.beginExecution();
	}


	/**
		the first time, just say if we have the data,
		every other time, if we have the 'all clear'
		trigger.  If it's reset, it needs to see if
		there is both a trigger and a new data set
		*/
	public boolean isReady(){
		if(firstRun){
			return(inputFlags[0]>0);
		}
		if(numFires==0){
			return super.isReady();
		}else{
			return (inputFlags[1]>0);
		}
	}

	public void doit () throws Exception {
		if(firstRun)
			firstRun=false;
		else
			pullInput(1);

		if(debug){
			System.out.println(getAlias()+"InputFlags[0]"+inputFlags[0]);
			System.out.println(getAlias()+"InputFlags[1]"+inputFlags[1]);
		}

		if (breaks == null) {
			setup ();

			// Set up the train and test sets indices
			int testing [] = new int [testSize];
			int training [] = new int [trainSize];
			if(debug){
				System.out.println("Xval: testSize="+testSize+" trainSize="+trainSize);
			System.out.println(breaks.length);
			}

			makeSets(testing, training);

			// now create a new vertical table.
			ExampleTableImpl examples = (ExampleTableImpl)DefaultTableFactory.getInstance().createExampleTable (table);
			examples.setTrainingSet (training);
			examples.setTestingSet (testing);

			testT=(TestTable)examples.getTestTable();
			trainT=(TrainTable)examples.getTrainTable();

			/*System.out.println("Indices");
			for(int i=0; i<indices.length; i++){
				System.out.print(indices[i]+" ,");
			}

			System.out.println("breaks");
			for(int i=0; i<breaks.length; i++){
				System.out.println(breaks[i]+", "+indices[breaks[i]-1]+", "+
						indices[breaks[i]]+", "+indices[breaks[i]+1]);
			}
			*/
		}else{

			// Set up the train and test sets indices
			int testing [] = new int [testSize];
			int training [] = new int [trainSize];

			makeSets(testing, training);
			if(debug){
				System.out.println("TestSEt");
				for(int i=0; i<testing.length; i++){
					System.out.print(testing[i]+", ");
				}				System.out.println();
				System.out.println("TrainSEt");
				for(int i=0; i<training.length; i++){
					System.out.print(training[i]+", ");
				}
				System.out.println();
			}

			// now create a new vertical table.
			//ExampleTable examples = new ExampleTable (table);
			trainT.setTrainingSet (training);
			testT.setTestingSet (testing);
			//pullInput(1);
		}

		this.pushOutput (testT, 0);
		this.pushOutput (trainT, 1);
		if(numFires==0){
			this.pushOutput (new Integer(breaks.length+1), 2);
		}
		numFires++;
		if(debug)
			System.out.println("Xval: numfires="+numFires);

		if(numFires==(breaks.length+1)){
			if(debug)
				System.out.println("Xval: reset");
			numFires=0;
			breaks=null;
			testT=null;
			trainT=null;
		}
	}

	public String getModuleInfo(){
		String str=super.getModuleInfo();
		str+=". **This version returns the same tables every time"+
			" with different test/train sets.  the trigger input"+
			" is used to prevent referencing problems";
		return str;
	}

	public String getInputInfo(int i){
		if(i==0){
			return "The table that contains the data";
		}
		if(i==1){
			return "The object that triggers the next set of"+
				" tables to be pushed";
		}
		else{
			return "No such input";
		}
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.util.datatype.Table",
			"java.lang.Object"};
		return types;
	}

	public String getInputName(int i){
		if(i==0){
			return "Data Table";
		}
		if(i==1){
			return "Trigger Object";
		}
		else{
			return "No such input";
		}
	}

}
