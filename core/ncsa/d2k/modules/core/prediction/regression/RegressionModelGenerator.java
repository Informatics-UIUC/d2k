package ncsa.d2k.modules.core.prediction.regression;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.optimize.util.*;

import Jama.*;

/*
	Makes a multivariate linear regression
	model. Will predict multiple outputs but
	really makes a separate model for each, but this
	fact is transparent to the user.
	Based on the simple case of solving
	a Full-Rank Linear Least-Squares Problem
	with the Normal Equations as described in:

	<br>
	Gill, Murray, and Wright. <b>Numerical Linear Algebra and Optimization
	Volume 1</b>, Addison-Wesley Publishing Company, 1991. pg 223.

	<br><br>
	The Jama jar file of Linear Algebra methods is required. It is
	located at:<br>
	<a href="http://math.nist.gov/javanumerics/jama/">
	http://math.nist.gov/javanumerics/jama/</a>

	@author pgroves
	*/

public class RegressionModelGenerator extends ModelGeneratorModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=true;

	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	boolean makeModelAvailable=true;

	/////////////////////////
	/// other fields
	////////////////////////

	RegressionModel model;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		ExampleTable et=(ExampleTable)pullInput(0);
		model=new RegressionModel(et);
		pushOutput(model, 0);
		if(!makeModelAvailable)
			model=null;
	}

	public ModelModule getModel() {
    	return model;
  	}

	//////////////////////////////////////////////////////////////
	//// The model
	//////////////////////////////////////////////////////////////

	public class RegressionModel extends PredictionModelModule
					implements java.io.Serializable, HasNames
	{

		////////////////////
		//model's fields
		////////////////////
		double[][] coefficients;
		String[] outputNames;



		/////////////////////////
		//model's methods
		////////////////////////

		/*
			Constructor

		*/
		public RegressionModel(ExampleTable et){

			setName("Regression");
			coefficients=new double[et.getNumOutputFeatures()][];
			outputNames=new String[et.getNumOutputFeatures()];
			for(int i=0; i<et.getNumOutputFeatures(); i++){
				outputNames[i]=et.getColumnLabel(et.getOutputFeatures()[i]);
			}

			//get the main matrix (input features)
			//make sure to make room for a constant
			try{
			Matrix matA=new Matrix(et.getNumTrainExamples(),
									et.getNumInputFeatures()+1);
			for(int i=0;i<et.getNumTrainExamples(); i++){//row
				matA.set(i, 0, 1.0);//the constant
				for(int j=0;j<et.getNumInputFeatures(); j++){//column
					matA.set(i, j+1, et.getTrainInputDouble(i, j));
				}
			}

			Matrix matATA=(matA.transpose()).times(matA);
			Matrix matR=matATA.chol().getL();


			//assign the output being considered to matrix b,
			//compile the results from solving the regression
			//for each particular b
			for(int oi=0; oi<et.getNumOutputFeatures(); oi++){

				//get the output/predictions matrix
				Matrix matb=new Matrix(et.getNumTrainExamples(), 1);
				for(int i=0;i<et.getNumTrainExamples();i++){
					matb.set(i, 0, et.getTrainOutputDouble(i,oi));
				}
				coefficients[oi]=regression(matA, matb, matATA, matR);
			}
			}
			catch (Exception exc){
				//let's just leave everything the way it is (all coefficients 0)
				//and return
				for(int i=0;i<coefficients.length;i++){
					coefficients[i]=new double[et.getNumInputFeatures()+1];
				}
			}


		}

		/*
			finds the coefficients for a single output
		*/
		private double[] regression(Matrix matA, Matrix matb, Matrix ATA, Matrix matR){
			Matrix matATb=(matA.transpose()).times(matb);
			Matrix matY=matR.solve(matATb);
			Matrix matX=(matR.transpose()).solve(matY);
			if(debug){
				System.out.println("Mat X:");
				matX.print(8, 5);
			}
			double[][] arr=matX.getArray();
			double[] coef=new double[matA.getColumnDimension()];
			for(int i=0;i<coef.length;i++){
				coef[i]=arr[i][0];
			}
			return coef;
		}


	/**********************************
		PREDICT
		*******************************/


	public PredictionTable predict(ExampleTable et){


		PredictionTable predTable;
		if(et instanceof PredictionTable){
			predTable=(PredictionTable)et;
		}else{
			predTable=new PredictionTable(et);
		}

		//if there are no spots for pred columns
		if(predTable.getNumOutputFeatures()==0){
			for(int i=0; i<outputNames.length; i++){
				DoubleColumn dc=new DoubleColumn(et.getNumRows());
				dc.setLabel(outputNames[i]);
				predTable.addPredictionColumn(dc);
			}
		}

		for(int e=0; e<predTable.getNumRows(); e++){
			for(int oi=0; oi<outputNames.length; oi++){
				double sum=0.0;
				sum+=coefficients[oi][0];
				for(int i=0; i<et.getNumInputFeatures();i++){
					sum+=et.getDouble(e, et.getInputFeatures()[i])*
								coefficients[oi][i+1];
				}
				//System.out.println("Reg:"+outputNames[oi]);
				predTable.setDouble(sum, e, predTable.getPredictionSet()[oi]);
			}
		}
		return predTable;
	}


	/* just calls predict on the pulled in table*/
  	 public void doit() {
		 ExampleTable et=(ExampleTable)pullInput(0);
		 pushOutput(predict(et), 0);

  	 }
		////////////////////////////
		///model's d2k info methods
		///////////////////////////
		public String getModuleInfo(){
			return "Multi-variable in, Multi-variable out prediction"+
					" model based on linear regression";
		}


	   	public String getModuleName() {
			return "RegressionModel";
		}
		public String[] getInputTypes(){
			String[] s= {"ncsa.d2k.util.datatype.ExampleTable"};
			return s;
		}

		public String getInputInfo(int index){
			switch (index){
				case(0): {
					return "The input data set to make the predictions on";
				}
				default:{
					return "No such input.";
				}
			}
		}


		public String getInputName(int index) {
			switch (index){
				case(0): {
					return "Prediction Input Data";
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
					return "The input ExampleTable with the prediction"
					+" columns filled in";
				}
				default:{
					return "No such output.";
				}
			}
		}
		public String getOutputName(int index) {
			switch (index){
				case(0): {
					return "Table w/ Predictions";
				}
				default:{
					return "No such output.";
				}
			}
		}
	}






	////////////////////////////////
	/// ModelGen's D2K Info Methods
	////////////////////////////////


	public String getModuleInfo(){
		return "Creates multiple, multivariate linear regression models"+
				", one for each output, and encapsulates them in a single"+
				" model module. Predicting multiple outputs is therefore"+
				"like using any multi-variable prediction model";
	}

   	public String getModuleName() {
		return "Multivariate Regression Model Generator";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.util.datatype.ExampleTable"/*,
			"ncsa.d2k.modules.compute.learning.optimize.util.Solution"*/};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The training data set. Only row indices ind"+
						"icated by the trainingSet will be considered"+
						" during model generation";
			}
			/*case(1): {
				return "The Solution object which contains the parame"
						"ters or biases for generating the model";
			}*/

			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Training Data";
			}
			/*case(1): {
				return "Parameters";
			}*/

			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.prediction.regression.RegressionModelGenerator$RegressionModel"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "The model that was produced";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "The Model";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
		public boolean getMakeModelAvailable(){
		return makeModelAvailable;
	}
	public void setMakeModelAvailable(boolean b){
		makeModelAvailable=b;
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
	*/
}







