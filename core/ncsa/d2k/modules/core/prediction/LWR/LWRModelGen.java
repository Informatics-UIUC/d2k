
package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import Jama.*;

/**
	LWRModelGen.java
		This model generating module creates an LWRModel module
		using the Tables that were passed in.
	@talumbau

*/
public class LWRModelGen extends ModelGeneratorModule implements HasNames
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Traintable\">    <Text> </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Testtable\">    <Text> </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table", "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	public String getInputName(int i) {
		switch(i) {
			case 0: return "Traintree";
			case 1: return "Testtree";
			default: return "so such input";
		}
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"theModel\">    <Text> </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.LWR.LWRModel"};
		return types;

	}

	public String getOutputName(int i) {
		switch(i) {
			case 0: return "model";
			default: return "no such output";
		}
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Generates an LWRModel ");
		sb = sb.append("from the Tables.  The LWRModel ");
		sb = sb.append("performs all the necessary calculations.");
		return sb.toString();
	}

	public String getModuleName() {
		return "modelgen";
	}

	int kernelSelector;
	int distanceSelector;
	int nearestNeighbors;
	boolean useNearestNeighbors;

	public void setKernelSelector(int x){
		kernelSelector = x;
	}

	public void setDistanceSelector(int y){
		distanceSelector = y;
	}

	public void setNearestNeighbors(int x){
		nearestNeighbors = x;
	}

	public void setUseNearestNeighbors(boolean a){
		useNearestNeighbors = a;
	}

	public int getKernelSelector(){
		return kernelSelector;
	}

	public int getDistanceSelector(){
		return distanceSelector;
	}

	public int getNearestNeighbors(){
		return nearestNeighbors;
	}

	public boolean getUseNearestNeighbors(){
		return useNearestNeighbors;
	}

	LWRModel model;
/**
		PUT YOUR CODE HERE.
	*/

	public void doit() throws Exception {
		Table Traintable = (Table) pullInput(0);
		Table Testtable = (Table) pullInput(1);
		model = new LWRModel(Traintable, Testtable, kernelSelector, distanceSelector, nearestNeighbors, useNearestNeighbors);
		pushOutput(model, 0);
	}

	public ModelModule getModel() {
		return model;
	}
}

