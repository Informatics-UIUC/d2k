package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.util.datatype.*;

/*
	takes multiple PredictionTables, puts all the columns
	of prediction in the first one and returns it.
	does not update the "predictionSet" field in any way


	@author pgroves
	*/

public class CombinePredictionTables extends ComputeModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	int tablesToWaitFor=4;

	/////////////////////////
	/// other fields
	////////////////////////

	int tablesIn=0;

	PredictionTable[] tables;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		clearFields();
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}

	protected void clearFields(){
		tablesIn=0;
		tables=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(tablesIn==0){
			tables=new PredictionTable[tablesToWaitFor];
		}
		tables[tablesIn]=(PredictionTable)pullInput(0);
		tablesIn++;

		if(tablesIn==tablesToWaitFor){
			pushOutput(makeMasterTable(), 0);
			clearFields();

		}

	}

	protected PredictionTable makeMasterTable(){
		PredictionTable master=tables[0];
		int[] predictionSet=master.getPredictionSet();
		int addedPredCount=0;

		for(int i=1; i<tablesToWaitFor; i++){
			addedPredCount+=tables[i].getPredictionSet().length;
		}
		//int[] newPredSet=new int[addedPredCount+predictionSet.length];
		Column[] newInternal=new Column[master.getNumColumns()+addedPredCount];

		//put the old columns in the new columnarray
		for(int i=0; i<master.getNumColumns(); i++){
			newInternal[i]=master.getColumn(i);
		}
		//put in the new prediction columns
		int internalIndex=master.getNumColumns();
		for(int i=1; i<tablesToWaitFor;i++){
			for(int j=0; j<tables[i].getPredictionSet().length; j++){
				newInternal[internalIndex]=tables[i].getColumn(
											tables[i].getPredictionSet()[j]);
				internalIndex++;
			}
		}
		master.setInternal(newInternal);
		return master;
	}
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Takes in a number of Prediction Tables and puts all of"+
				"the prediction columns in one of them. Returns it."+
				"meant to be used when several models were assigned to"+
				"the same problem and one master table is wanted for"+
				"output that holds all of the models' predictions";
	}

   	public String getModuleName() {
		return "Predictions Compiler";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.util.datatype.PredictionTable"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The Prediction Tables that will be combined";
			}
			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Tables w/ Predictions";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.util.datatype.PredictionTable"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "One of the input tables, now holding all prediction"+
						"columns";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Compiled Predictions";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public int  getTablesToWaitFor(){
		return tablesToWaitFor;
	}
	public void setTablesToWaitFor(int d){
		tablesToWaitFor=d;
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

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}







