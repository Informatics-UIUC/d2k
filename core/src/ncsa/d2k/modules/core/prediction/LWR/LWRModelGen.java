
package ncsa.d2k.modules.core.prediction.LWR;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import Jama.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

/**
	LWRModelGen.java
		This model generating module creates an LWRModel module
		using the Tables that were passed in.
	@talumbau

*/
public class LWRModelGen extends ModelGeneratorModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "         ";
			case 1: return "         ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
                  "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Traintable";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "         ";
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
			case 0:
				return "theModel";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Generates an LWRModel from the Tables. The LWRModel performs all the     necessary calculations.  </body></html>";
	}

	public String getModuleName() {
		return "modelgen";
	}

/*	int kernelSelector;
	int distanceSelector;
	int nearestNeighbors;
	int numberOfPoints = 60;
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

	public void setNumberOfPoints(int z){
		numberOfPoints = z;
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

	public int getNumberOfPoints(){
		return numberOfPoints;
	}

	public boolean getUseNearestNeighbors(){
		return useNearestNeighbors;
	}
        */

	LWRModel model;
/**
		PUT YOUR CODE HERE.
	*/

	public void doit() throws Exception {
		ExampleTable Traintable = (ExampleTable) pullInput(0);
                ParameterPoint pp = (ParameterPoint)pullInput(1);

                int kernelSelector = (int)pp.getValue(LWRParamSpaceGenerator.KERNEL_SELECTOR);
                int nearestNeighbors = (int)pp.getValue(LWRParamSpaceGenerator.NEAREST_NEIGHBORS);
                int numberOfPoints = (int)pp.getValue(LWRParamSpaceGenerator.NUMBER_OF_POINTS);
                int useNearest = (int)pp.getValue(LWRParamSpaceGenerator.USE_NEAREST);

                boolean useNearestNeighbors;
                if(useNearest == 0)
                  useNearestNeighbors = false;
                else
                  useNearestNeighbors = true;

                int distanceSelector = 0;

		//ExampleTable Testtable = (ExampleTable) pullInput(1);
		model = new LWRModel(Traintable, kernelSelector, distanceSelector, nearestNeighbors, useNearestNeighbors, numberOfPoints);
		pushOutput(model, 0);
	}

	public ModelModule getModel() {
		return model;
	}
}

