package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.io.Serializable;

/* SensitivityAnalysis

	Creates a Table that for every InputFeature has a column of predictedOutputs
	that were generated as follows:
		for(all TestExamples)
			vary every column by (highVal-lowVal)/TrialsPerExample
			a total of TrialsPerExample times
	All of the outputs (even though they come from different examples being varied)
	are put in a single column (corresponding to an InputFeature).  Finding the
	min, max and stdev of this column will then yield some info on the sensitivity
	of the model to that input

	@author Peter Groves
	*/

public class SensitivityAnalysis extends ncsa.d2k.core.modules.ComputeModule {
	//////////////
	//PROPS
	/////////////


	/*the number of synthetic examples that will be produced
	for every row of every inputFeature column*/
	int trialsPerExample=10;

	/*if true, will find the min and max of the column
	and vary the trials over that interval, if false,
	will use the user defined values lowVal and highVal*/
	boolean autoRange=false;

	/*the lowest synthetic value that will be considered
	for all the inputFeatures*/
	double lowVal= -1.0;

	/*the highest synthetic value that...*/
	double highVal=1.0;


	public int getTrialsPerExample(){
		return trialsPerExample;
	}
	public void setTrialsPerExample(int i){
		trialsPerExample=i;
	}
	public boolean getAutoRange(){
		return autoRange;
	}
	public void setAutoRange(boolean b){
		autoRange=b;
	}
	public double getLowVal(){
		return lowVal;
	}
	public void setLowVal(double i){
		lowVal=i;
	}
	public double getHighVal(){
		return highVal;
	}
	public void setHighVal(double i){
		highVal=i;
	}

	///////////////////
	///other global fields
	////////////////////

	/*The table to hold 'synthetic' data
	we will swap these with real columns, test, then swap them
	back, then repeat with different columns*/
	TableImpl syntheticTable;

	/*The ExampleTable that is pulledIn on the first run,
	this holds the examples that will be varied*/
	ExampleTableImpl mainData;

	/*will be sent to the PredictionModel everytime
	with a different column swapped out with a synthetic column*/
	TestTableImpl testTable;

	/*holds the results, one column for every inputFeature
	plus one with an example-trial index*/
	TableImpl[] resultsTables;


	private boolean firstRun;

	/*which inputFeature being considered*/
	private int currentFeature;

	/*which of the trialsPerExample being considered*/
	private int currentTrial;


	///////////////////////////////
	//the divide & conquer functions
	//////////////////////////////

	private void setup(){
		mainData=(ExampleTableImpl)pullInput(0);
		testTable=(TestTableImpl)mainData.getTestTable();

		int mainDataRowCount=mainData.getNumRows();
		int testTableRowCount=testTable.getNumRows();

		//set up the synth table
		syntheticTable= (TableImpl)DefaultTableFactory.getInstance().createTable(mainData.getNumInputFeatures());
		for(int i=0; i<syntheticTable.getNumColumns(); i++){
			DoubleColumn dc=new DoubleColumn(mainDataRowCount);
			syntheticTable.setColumn(dc, i);
		}
		fillSyntheticTable();

		//set up the results tables
		resultsTables=new TableImpl[mainData.getNumOutputFeatures()];
		for(int k=0; k<resultsTables.length; k++){
			resultsTables[k]= (TableImpl)DefaultTableFactory.getInstance().createTable(mainData.getNumInputFeatures()+1);
			StringColumn sc=new StringColumn(testTableRowCount*trialsPerExample);
			for(int i=0; i<testTableRowCount; i++){
				for(int j=0; j<trialsPerExample; j++){
					sc.setString((i+"-"+j), i*trialsPerExample+j);
				}
			}
			resultsTables[k].setColumn(sc, 0);
			for(int i=1; i<mainData.getNumInputFeatures()+1; i++){
				DoubleColumn dc=new DoubleColumn(testTableRowCount*trialsPerExample);
				resultsTables[k].setColumn(dc, i);
			}
			//now put the original inputs' labels in
			resultsTables[k].setLabel(mainData.getColumnLabel(mainData.getOutputFeatures()[k]));

			resultsTables[k].setColumnLabel("Example-Trial", 0);
			for(int i=1; i<mainData.getNumInputFeatures()+1; i++){
				String s=mainData.getColumnLabel(mainData.getInputFeatures()[i-1]);
				resultsTables[k].setColumnLabel(s, i);
			}

		}

	}

	private void gotoNextInputFeature(){
		currentFeature++;
		currentTrial=0;
		fillSyntheticTable();
	}

	private void fillSyntheticTable(){
		if (autoRange||firstRun){
			NumericColumn nc=(NumericColumn)mainData.getColumn(mainData.getInputFeatures()[currentFeature]);
			double min=0, max=0;

			if(autoRange){
				min=(double)nc.getMin();
				max=(double)nc.getMax();
			}else if (firstRun){
				min=lowVal;
				max=highVal;
			}

			double incr=(max-min)/(trialsPerExample-1);
			//syntheticTable should already be the right size, just need to fill it
			for(int c=0; c<syntheticTable.getNumColumns(); c++){
				double synth=(min+incr*c);
				for(int r=0; r<syntheticTable.getNumRows(); r++){
					syntheticTable.setDouble(synth, r, c);
				}
			}
		}
	}


	private void makeAndPushTT(){
		//swap the right synth data with a column in testTable

		Column swp=syntheticTable.getColumn(currentTrial);
		syntheticTable.setColumn(
								testTable.getColumn(testTable.getInputFeatures()[currentFeature]),
								currentTrial);

		testTable.setColumn(swp, testTable.getInputFeatures()[currentFeature]);
		pushOutput(testTable, 0);
	}

	private void pullAndSavePredictions(){
		//this is actually the same object we pushed out earlier, but we'll
		//pull it in so we can use the pipes to keep the sequencing right
		testTable=(TestTableImpl)pullInput(1);

		for(int k=0; k<resultsTables.length; k++){//for every output Feature
			DoubleColumn allPredictions=(DoubleColumn)resultsTables[k].getColumn(currentFeature+1);
																	//+1 cos o that string index column

			for(int i=0; i<testTable.getNumRows(); i++){//for every test example

				//in the end, all the predictions generated from the n-th example will be together,
				//while every time we get a set of predictions they will be one trial for all examples
				double pr=testTable.getDouble(i, testTable.getPredictionSet()[k]);
				allPredictions.setDouble(pr, i*trialsPerExample+currentTrial);
			}
		}
		//put the 'real' data back in the testTable, put the synth data back into syntheticTAble
		Column swp = syntheticTable.getColumn(currentTrial);
		syntheticTable.setColumn(testTable.getColumn(testTable.getInputFeatures()[currentFeature]), currentTrial);
		testTable.setColumn(swp, testTable.getInputFeatures()[currentFeature]);
	}

	private void finish(){
		for(int i=0; i<resultsTables.length; i++){
			pushOutput(resultsTables[i], 1);
		}
	}


	//////////////////////
	//d2k functions
	////////////////////
	public void beginExecution(){
		currentFeature=0;
		currentTrial=0;
		firstRun=true;
	}

	public boolean isReady(){
		if(inputFlags[0]>0 && firstRun){
			return true;
		}
		else
			return (inputFlags[1]>0);
	}


	public void doit() throws Exception{

		if(!firstRun){
			pullAndSavePredictions();
			currentTrial++;
		}

		if(firstRun){
			setup();
			firstRun=false;
		}


		if(currentTrial==trialsPerExample){
			if(currentFeature==mainData.getNumInputFeatures()-1){
				//we're done
				finish();
				return;
			}
			else{
				gotoNextInputFeature();
			}
		}

		makeAndPushTT();
	}

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "An exampleTable with the TestSet set.  The test examples willbe the synthetically varied examples";
			case 1: return "The testTable that come back from Model, all filled in";
			default: return "No such input";
		}
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl","ncsa.d2k.modules.core.datatype.table.basic.TestTableImpl"};
		return types;
	}
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "The generated TestTable for the model to fill in";
			case 1: return "The final results, with all the predicted values organized by inputFeature";
			default: return "No such output";
		}
	}
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TestTableImpl","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Performs the basis of a sensitivity analysis. Will vary every testinput     double over some range and organize the resulting predictions by     inputFeature. analyzing a feature'scolumn will show the extent to which a     feature can influence the output (a larger range meansmore influence).     PROPS: trialsPerExample- every test example will be duplicated this many     times with the input feature under consideration incremented equally every     time. autoRange-if true, will find the min and max of each input feature     and vary it over that interval. lowVal, highVal- when autorange is false,     these are the bounds that the inputs will be variedover. Regardless of     autoRange, there will always be 'trialsPerExample' false examples madefor     every input in every example  </body></html>";
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SensitivityAnalysis";
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
			case 1:
				return "input1";
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








