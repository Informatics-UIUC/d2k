
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	LWRCollect.java
		This module collects the predictions from an LWR model
		and passes them along to the LWRPlotVis.  It will fire
		as many times as there are attributes for the queries
		and will finally output a table containing inputs, outputs,
		and criterion calculations.
	@author talumbau
*/
public class LWRCollect extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"collectTable\">    <Text>This table collects all of the predicted values together with the 'x' values.  It grows each time the modules fires. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"predictionTable\">    <Text>This table contains predictions and criterion calculations from an LWRModel.  The x values and predictions are collected and the rest of the data is discarded. </Text>  </Info></D2K>";
			case 2: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"originalTable\">    <Text>This table is the original set of numeric data.  It is used to determine the number of times this module should fire (once for each attribute, with the last column counted as the output) </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"collectTable\">    <Text>The collectTable that was input has columns added to it, and then output again to be fed into its own input. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"finaltable\">    <Text>This is the final table to be used by an LWR vis module to graph the lines of best fit. </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	TableImpl collectTable;
	TableImpl inputTable;
	//StringColumn colLabels;
	int nFired = -1;
	int N = 0;

	public void doit() throws Exception {

		TableImpl predTable = (TableImpl) pullInput(1);
		inputTable = (TableImpl) pullInput(2);

		if (nFired == -1)
			nFired = 0;

		if (nFired < 1){ //first time through
			collectTable = (TableImpl)DefaultTableFactory.getInstance().createTable();
			//colLabels = new StringColumn();
			N = inputTable.getNumColumns()-1;
		}
		else
			collectTable = (TableImpl) pullInput(0);

		nFired++;	//at this point, nFired = # of times module entered here

		for (int i=0; i<predTable.getNumColumns();i++){
			collectTable.addColumn(predTable.getColumn(i).copy());
		}

		if (nFired == N){
			//colLabels.setLabel("prediction_labels");
			//collectTable.addColumn(colLabels);
			pushOutput(collectTable,1);
		}
		else
			pushOutput(collectTable,0);
	}

	public boolean isReady(){
		//System.out.println("!!!!!!!!!!!!!!isReady collect called!!!!!!!!!!!");
		if ((inputFlags[1]>0) && (nFired == -1) && inputFlags[2]>0)
			return true;
		else if ((inputFlags[1]>0) && (inputFlags[0]>0))
				return true;
			else
				return false;


	}

	public void beginExecution(){
		nFired = -1;
		N = 0;
		collectTable = null;
	}

}

