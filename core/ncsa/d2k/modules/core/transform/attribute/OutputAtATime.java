package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/*
	For use with CompoundModels.
	Takes an ExampleTable with several
	output features and creates N Example
	Tables with N=num of output features.
	in each new table the outputFeatures
	index array is set to one of the
	original output features

	@author pgroves
	*/

public class OutputAtATime extends DataPrepModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////

	ExampleTable origTable;

	int[] origOutputFeatures;

	int numFires=0;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(numFires>0){
			return true;
		}else{
			return super.isReady();
		}
	}
	public void beginExecution(){
		wipeFields();
		return;
	}
	private void wipeFields(){
		numFires=0;
		origOutputFeatures=null;
		origTable=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(numFires==0){
			origTable=(ExampleTable)pullInput(0);
			origOutputFeatures=origTable.getOutputFeatures();
			pushOutput(new Integer(origOutputFeatures.length), 1);
		}
		ExampleTable et= (ExampleTable)origTable.toExampleTable();
		int[] newOutputs=new int[1];
		newOutputs[0]=origOutputFeatures[numFires];
		et.setOutputFeatures(newOutputs);
		numFires++;
		pushOutput(et, 0);
	//	pushOutput(origTable, 2);

		if(numFires==origOutputFeatures.length){
			wipeFields();
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
		String[] s= {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "";
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
		String[] s={"ncsa.d2k.modules.core.datatype.table.ExampleTable",
					"java.lang.Integer",
					/*"ncsa.d2k.util.datatype.ExampleTable"*/};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "";
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







