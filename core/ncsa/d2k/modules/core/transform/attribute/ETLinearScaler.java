package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import java.io.Serializable;

import ncsa.d2k.modules.core.datatype.table.*;
/**
       ETLinearScaler.java

	   This modules takes in an ExampleTable and
	   outputs it with all the features scaled (only
	   input and output features that are NumericColumns),
	   also has the
	   ability to untransform the last example table
	   that was past through it and any Test/TrainTables
	   made from it

	   @author Peter Groves

*/
public class ETLinearScaler extends ncsa.d2k.modules.TransformationModule implements Serializable {


public double lowerBound=0;
public double upperBound=1;

protected boolean transformInputs=false;
protected boolean clearPreviousTransforms=true;

private double newRange=1;
private double invNewRange=1;

/* for these two,
	first index is the feature index, second is 0-orig min,
	1-orig range
*/
private double[][] inputInfo;
private double[][] outputInfo;


public void setLowerBound(double d){
	lowerBound=d;
	updateBounds();
}

public double getLowerBound(){
		return lowerBound;
}
public void setUpperBound(double d){
	upperBound=d;
	updateBounds();
}
public double getUpperBound(){
	return upperBound;
}

private void updateBounds(){
	newRange= upperBound-lowerBound;
	invNewRange=1/newRange;
}

public boolean getTransformInputs(){
	return transformInputs;
}
public void setTransformInputs(boolean b){
	transformInputs=b;
}

public boolean getClearPreviousTransforms(){
	return clearPreviousTransforms;
}
public void setClearPreviousTransforms(boolean b){
	clearPreviousTransforms=b;
}


	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataIn\">    <Text>The ExampleTable whose input and output columns are to be scaled</Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;

	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataOut\">    <Text>The scaled ExampleTable </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;

	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Data Table Linearizer\">    <Text>This modules takes in an ExampleTable and outputs it with all the features scaled (only input and output features that are NumericColumns). Also has the ability to untransform the last example table that was past through it and any Test/TrainTables made from it. PROPS: upperBound/lowerBound - define the range that all NumericColumns will be scaled to. </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {

		ExampleTable et=(ExampleTable)pullInput(0);
		et=(ExampleTable)transform(et);
		if(clearPreviousTransforms){
				et.getTransformations().clear();
		}

		et.addTransformation(this);
		//System.out.println("ETLIN:"+et.getTransformations().size());
		firstUntransform=true;
		pushOutput(et, 0);
	}

	/* transform

		takes in a table and scales all the input and output
		features to the range defined by lowerBound/upperBound
		*/
	public Table transform(Table t){
		ExampleTable et=(ExampleTable)t;

		inputInfo=new double[et.getNumInputFeatures()][2];
		outputInfo=new double[et.getNumOutputFeatures()][2];
		if(transformInputs){
	  	for(int k=0; k<et.getNumInputFeatures(); k++){
			if(et.getColumn(et.getInputFeatures()[k]) instanceof NumericColumn){
		    	NumericColumn current_column=(NumericColumn)et.getColumn(et.getInputFeatures()[k]);

		   		inputInfo[k][1]=((DoubleColumn)current_column).getMax()-((DoubleColumn)current_column).getMin();
				inputInfo[k][0] = ((DoubleColumn)current_column).getMin();
	  			int numRows=current_column.getCapacity();
				for	(int j=0; j<numRows; j++){
					double d=current_column.getDouble(j);
					//transform to the interval [0,1]
					d=((d-inputInfo[k][0])/inputInfo[k][1]);
					//now to the interval [lowBound, hiBoung]
					d=(d*newRange + lowerBound);

					current_column.setDouble(d, j);
				}
			}
		}
		}
	  	for(int k=0; k<et.getNumOutputFeatures(); k++){
			if(et.getColumn(et.getOutputFeatures()[k]) instanceof NumericColumn){

		    	NumericColumn current_column=(NumericColumn)et.getColumn(et.getOutputFeatures()[k]);

		   		outputInfo[k][1]=((DoubleColumn)current_column).getMax()-((DoubleColumn)current_column).getMin();
				outputInfo[k][0] = ((DoubleColumn)current_column).getMin();
	  			int numRows=current_column.getCapacity();

				for	(int j=0; j<numRows; j++){
					double d=current_column.getDouble(j);
					//transform to the interval [0,1]
					d=((d-outputInfo[k][0])/outputInfo[k][1]);
					//now to the interval [lowBound, hiBoung]
					d=(d*newRange + lowerBound);

					current_column.setDouble(d, j);
				}
			}
		}


		return et;
	}


	/*so we don't untransform ins and outs more than once in cross validation*/
	private boolean firstUntransform=true;

	/*
		reverses the transform done by the function /transform
		using stored ranges and offsets of the original columns.
		also untransforms the prediction columns in a TestTable, if
		any.
		*/
	public Table untransform(Table t){
		ExampleTable et=(ExampleTable)t;
		if(firstUntransform){
		  	for(int k=0; k<et.getNumOutputFeatures(); k++){
				if(et.getColumn(et.getOutputFeatures()[k]) instanceof NumericColumn){

			    	NumericColumn current_column=(NumericColumn)et.getColumn(et.getOutputFeatures()[k]);
					untransformOutputColumn(current_column, k);
				}
			}
			if(transformInputs){
	  		for(int k=0; k<et.getNumInputFeatures(); k++){
				if(et.getColumn(et.getInputFeatures()[k]) instanceof NumericColumn){

		   	 		NumericColumn current_column=(NumericColumn)et.getColumn(et.getInputFeatures()[k]);
					untransformInputColumn(current_column, k);
				}
			}
			}
			firstUntransform=false;
		}
		//don't forget to untransform the predictions
		if(et instanceof TestTable){
			int[] testSet=et.getTestingSet();
		  	for(int k=0; k<et.getNumOutputFeatures(); k++){
				if(et.getColumn(et.getOutputFeatures()[k]) instanceof NumericColumn){

	    			NumericColumn current_column=(NumericColumn)et.getColumn(((TestTable)et).getPredictionSet()[k]);

					untransformPredictionColumn(current_column, k, testSet);
				}
			}
		}
		return et;
	}

	private void untransformOutputColumn(NumericColumn current_column, int k){
	//	System.out.println("uoc");
		final int numRows=current_column.getCapacity();
		double d;
		for	(int j=0; j<numRows; j++){
			d=current_column.getDouble(j);
			//double e=d;

			d-=lowerBound;
			d*=invNewRange;
			d*=outputInfo[k][1];
			d+=outputInfo[k][0];
			current_column.setDouble(d, j);
			//System.out.println("LIN index:"+j+" from:"+e+" to:"+d);
		}
	}
	private void untransformInputColumn(NumericColumn current_column, int k){
		final int numRows=current_column.getCapacity();

		double d;
		for	(int j=0; j<numRows; j++){
			d=current_column.getDouble(j);
			//double e=d;

			d=(d-lowerBound)*invNewRange;
			d=(d*inputInfo[k][1])+inputInfo[k][0];
			current_column.setDouble(d, j);
			//System.out.println("LIN index:"+j+" from:"+e+" to:"+d);
		}
	}
	private void untransformPredictionColumn(NumericColumn current_column, int k, int[] testSet){
		final int numRows=testSet.length;
		double d;
		for	(int j=0; j<numRows; j++){
			d=current_column.getDouble(testSet[j]);
			//double e=d;

			d-=lowerBound;
			d*=invNewRange;
			d*=outputInfo[k][1];
			d+=outputInfo[k][0];
			current_column.setDouble(d, testSet[j]);
			//System.out.println("LIN index:"+j+" from:"+e+" to:"+d);
		}
	}


}


