
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ET1ExType.java

	Takes a VT and outputs it as an ET (actually a TrainTable or TestTable
	with all the examples either as test or train examples. PROPS: TrainVsTest:
	true- all rows set to train examples, false- all rows to test examples
*/
public class SetTrainOrTest extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The raw table";
						default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "Either a TestTable or TrainTable with all the examples either as test or all as train";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"ET\">    <Text>Takes a VT and outputs it as an ET (actually a TrainTable or TestTablewith all the examples either as test or train examples. PROPS: TrainVsTest: true- all rows set to train examples, false- all rows to test examples </Text>  </Info></D2K>";

	}
	/*true - everything train examples
	  false - everything test examples
	  */
	private boolean trainVsTest;

	public void setTrainVsTest(boolean b){
		trainVsTest=b ;
	}
	public boolean getTrainVsTest(){
		return trainVsTest;
	}

	/**
		does it
	*/
	public void doit() throws Exception {
		TableImpl tt=(TableImpl)pullInput(0);

		ExampleTableImpl et;
		if(tt instanceof ExampleTable){
			et=(ExampleTableImpl)tt;
		}else{
			et= (ExampleTableImpl)DefaultTableFactory.getInstance().createExampleTable(tt);
		}

		int[] exsAll=new int[et.getColumn(0).getNumRows()];
		int[] exsNone=new int[0];

		for(int i=0; i<exsAll.length; i++){
			exsAll[i]=i;
		}

		if(trainVsTest){
			et.setTrainingSet(exsAll);
			et.setTestingSet(exsNone);
			if(!(et instanceof TrainTable))
				et=(ExampleTableImpl)et.getTrainTable();
		}else{
			et.setTestingSet(exsAll);
			et.setTrainingSet(exsNone);
			if(!(et instanceof TestTable))
				et=(ExampleTableImpl)et.getTestTable();
		}

		pushOutput(et, 0);
	}

}

