package ncsa.d2k.modules.core.prediction.neuralnet;


import ncsa.d2k.core.modules.ModelGeneratorModule;
import ncsa.d2k.core.modules.ModelModule;
import java.io.*;
import java.util.*;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions.*;
import ncsa.d2k.modules.core.prediction.neuralnet.updateFunctions.*;
import ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	NNFeedForwardModelGenerator

	Builds a single back propagation neural network according to the parameters passed in.
	It supports various activation functions and update methods and learning acceleration.

	@author pgroves

*/

public class NNModelGenerator
			extends ncsa.d2k.core.modules.ModelGeneratorModule
			/*, Reentrant*/{


	boolean debug=true;
	boolean makeModelAvailable=true;

	boolean doScalingHere=true;
	public void setDoScalingHere(boolean b){
		debug=doScalingHere;
	}
	public boolean getDoScalingHere(){
		return doScalingHere;
	}


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
	boolean bug2=true;

	/*Row Indices for the Tables that (1) paramRangeTable- output
	 the range of the variables for an optimizer (2) param-input the actual
	 values that this model should use
	 */
	public final static int ACTIVATION_FUNCTION=0;
	public final static int UPDATE_FUNCTION=1;
	public final static int EPOCHS=2;
	public final static int SEED=3;
	public final static int WEIGHT_INIT_RANGE=4;
	public final static int LEARNING_RATE_FUNCTION=5;
	public final static int INITIAL_LEARNING_RATE=6;
	public final static int FINAL_LEARNING_RATE=7;
	public final static int HIDDEN_LAYERS=8;
	public final static int NODES_IN_LAYER_01=9;
	public final static int NODES_IN_LAYER_02=10;
	public final static int NODES_IN_LAYER_03=11;
	public final static int NODES_IN_LAYER_04=12;




	/*the model that will be saved at the end of execution
	*/
	public PredictionModelModule model;


	public final static Range[] getRanges(){
		Range[]	ranges=new Range[13];

		ranges[NNModelGenerator.ACTIVATION_FUNCTION]=
			new IntRange("Activation Function", 4, 0);

		ranges[NNModelGenerator.UPDATE_FUNCTION]=
			new IntRange("Update Function", 1, 0);

		ranges[NNModelGenerator.EPOCHS]=
			new IntRange("Epochs", 15000, 500);

		ranges[NNModelGenerator.SEED]=
			new DoubleRange("Seed", 100, -100);

		ranges[NNModelGenerator.WEIGHT_INIT_RANGE]=
			new DoubleRange("Weight Init Range", 20, .1);

		ranges[NNModelGenerator.LEARNING_RATE_FUNCTION]=
			new IntRange("Learning Rate Function", 1, 0);

		ranges[NNModelGenerator.INITIAL_LEARNING_RATE]=
			new DoubleRange("Initial Learning Rate", .999, .001);

		ranges[NNModelGenerator.FINAL_LEARNING_RATE]=
			new DoubleRange("Final Learning Rate", .999, .001);

		ranges[NNModelGenerator.HIDDEN_LAYERS]=
			new IntRange("Number Hidden Layers", 4, 1);

		ranges[NNModelGenerator.NODES_IN_LAYER_01]=
			new IntRange("Nodes In Layer 1", 20, 1);

		ranges[NNModelGenerator.NODES_IN_LAYER_02]=
			new IntRange("Nodes In Layer 2", 20, 1);

		ranges[NNModelGenerator.NODES_IN_LAYER_03]=
			new IntRange("Nodes In Layer 3", 20, 1);

		ranges[NNModelGenerator.NODES_IN_LAYER_04]=
			new IntRange("Nodes In Layer 4", 20, 1);

		return ranges;
	}


	public static double[] getDefaultParameters(){
		double []params=new double[13];

		params[NNModelGenerator.ACTIVATION_FUNCTION]=0;
		params[NNModelGenerator.UPDATE_FUNCTION]=0;
		params[NNModelGenerator.EPOCHS]=3000;
		params[NNModelGenerator.SEED]=3.12;
		params[NNModelGenerator.WEIGHT_INIT_RANGE]=3;
		params[NNModelGenerator.LEARNING_RATE_FUNCTION]=0;
		params[NNModelGenerator.INITIAL_LEARNING_RATE]=.8;
		params[NNModelGenerator.FINAL_LEARNING_RATE]=.1;
		params[NNModelGenerator.HIDDEN_LAYERS]=1;
		params[NNModelGenerator.NODES_IN_LAYER_01]=4;
		params[NNModelGenerator.NODES_IN_LAYER_02]=4;
		params[NNModelGenerator.NODES_IN_LAYER_03]=4;
		params[NNModelGenerator.NODES_IN_LAYER_04]=4;

		return params;
	}




	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The data set to train on (will train only on trainExamples. The output columns must be scaled to [-1,1] if using activation functions Elliot, Tanh, or FastTanh and [0,1] for Sigmoid and FastSigmoid  ";
			case 1: return "      The architecture and learning method parameters. See NNSolutionMaker for details  ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TrainTableImpl","ncsa.d2k.modules.core.optimize.util.MixedSolution"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      The model created.";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    A Neural Network Model Generator that works for both manually setting     parameters and using an optimizer.  </body></html>";
	}



	/**
		does it
	*/
  public void doit () throws Exception {

	MixedSolution inputParams=(MixedSolution)pullInput(1);
	TrainTableImpl trainingData=(TrainTableImpl)pullInput(0);

	NNModel mod=new NNModel(trainingData, (double[])inputParams.getParameters(), doScalingHere);
	pushOutput(mod, 0);
	if(makeModelAvailable)
		model=mod;

  }


  public ModelModule getModel() {
    return model;
  }


	/**************The model itself**************************/



  public class NNModel extends PredictionModelModule implements Serializable{

	/* the weights for each link between nodes, rows correspond to the
	   'to' node (including outputs), column number corresponds to the layer of the 'to' node.
		Each position is an object which is a DoubleColumn, the 'row' in this
		column corresponds to the 'from' node (row zero is the weight from the threshold node).
		also, the first row of the main VT is filled with DoubleColumns that
		contain all NaN, which represent the weights into the threshold node.  These
		are never used and are only present so that indexing is constant between
		weights, sums, activations, etc VT's.  NaN is used so no one accidentally
		uses them later on without saying "what the.."


	*/
	public final TableImpl weights;

	/*the weighted sums for each node (before activation),
	  column # corresponds to layer (outputs
	  are the last layer), row is index within the layer,
	  row zero is the threshold node, therefore set to NaN
	  because it should never be used, just there for that
	  indexing thing again
	*/
	private final TableImpl sums;

	/*the activation function values for all nodes, including outputs
	  same mapping as 'sums'. row zero is set to the threshold/bias value
	  (normally -1)
	*/

	private final  TableImpl activations;

	/*The 'deltas', or error distribution values, for each node
	  same mapping as 'sums'
	*/
	private final TableImpl deltas;

	/* The parameters
	*/
	public final double[] params;

	/* The data set being trained or tested
	*/
	public ExampleTableImpl data;


	/* the activation the compute class will use
	*/
	protected final NNactivation act;


	/*the function that controls the learning rate and the learning time
	*/
	protected NNlearn learnFn;

	public final double bias=-1.0;

	/*the names of the outputs that this model predicts*/
	protected final String[] outputNames;


	/* for these two,
		first index is the feature index, second is 0-orig min,
		1-orig range
	*/
	private double[][] transformOutputInfo;
	private boolean doTransform;

	//stuff for scaling
	double lowerBound;
	double upperBound;
	double newRange;
	double invNewRange;

	final double lowerTanh=-.9;
	final double upperTanh=.9;
	final double lowerSig=.1;
	final double upperSig=.9;


    /**********************************************************************************
			CONSTRUCTOR


      All the stuff that would probably normally be done in the doit of the modelgen
	  is done here. This is so that one modelgen can be continually making new models without
	  having to worry about a model that was already being made being screwed up
	  because its weights' arrays are getting written over

	  @param d= the data set to train on
	  @param p= a table with the parameters to use
    ****************************************************************************************/

    public NNModel(TrainTableImpl d, double[] p, boolean transform) {
                super(d);
		data=d;
		doTransform=transform;

		params=p;
		//params.print();

		/////////////////////
		//initialize sums VT
		//////////////////////

		sums= (TableImpl)DefaultTableFactory.getInstance().createTable(/*(int)params.getDouble(HIDDEN_LAYERS, 1)+1*/);


		/*starts at NODES_IN_LAYER_01 and goes through every NODES_IN_LAYER_XX that HIDDEN_LAYERS indicates
		should be used, adding a column
		with the number of rows indicated by the params table
		includes space for a threshold node at index [0], whose activation
		is always the bias/threshold (sums/deltas threshold index aren't used, but we want sums, acts, and deltas to be
		the same dimensions*/

		for(int i=0; i<(int)params[HIDDEN_LAYERS]; i++){
			sums.addColumn(new DoubleColumn((int)params[NODES_IN_LAYER_01+i]+1));
		}
		//also add a column for the outputs
		sums.addColumn(new DoubleColumn(data.getNumOutputFeatures()+1));

		//////////////////////
		//initialize weights VT
		//////////////////////

		weights= (TableImpl)DefaultTableFactory.getInstance().createTable((int)params[HIDDEN_LAYERS]+1);
		int numWeightCols=0;

		DoubleColumn tempColumn;

		//when there are hidden layers
		if(params[HIDDEN_LAYERS]>0){

			//inputs to first layer
			weights.setColumn(new ObjectColumn((int)params[NODES_IN_LAYER_01]+1), numWeightCols);//see 'weights' comment
			numWeightCols++;																				  //for expl of the +1
			//System.out.println(	data.getNumInputFeatures());
			for(int k=0; k<weights.getColumn(numWeightCols-1).getNumRows(); k++){
				tempColumn=new DoubleColumn(((int)data.getNumInputFeatures())+1);
				//System.out.println("tc:"+tempColumn.getCapacity());
				weights.setObject(tempColumn, k, 0);

			}

			//hidden layer to hidden layer
			for (int i=1; i<weights.getNumColumns()-1; i++){//don't look at the last column (representing outputs) yet
				weights.setColumn(new ObjectColumn((int)params[NODES_IN_LAYER_01+i]+1), numWeightCols);
				numWeightCols++;

				for (int k=0; k<weights.getColumn(i).getNumRows(); k++){
					tempColumn=new DoubleColumn((int)params[NODES_IN_LAYER_01+i-1]+1);

					weights.setObject(tempColumn, k, i);
				}
			}
			//last hidden layer to outputs
			weights.setColumn(new ObjectColumn(data.getNumOutputFeatures()+1), numWeightCols);
			numWeightCols++;
			for (int j=0; j<weights.getColumn(numWeightCols-1).getNumRows(); j++){
				tempColumn=new DoubleColumn(weights.getColumn(numWeightCols-2).getNumRows());
				weights.setObject(tempColumn, j, numWeightCols-1);
			}


		}
		//if no hidden layers, go straight from inputs to outputs
		else{
			weights.setColumn(new ObjectColumn(data.getNumOutputFeatures()+1), numWeightCols);
			numWeightCols++;//won't use this anymore, just to keep from getting confused

			for(int k=0; k<weights.getColumn(numWeightCols-1).getNumRows(); k++){
				tempColumn=new DoubleColumn((int)data.getNumInputFeatures()+1);
				weights.setObject(tempColumn, k, 0);
			}

		}
		randomizeWeights();


		//these just need to be the right size, none of the values will be used
		deltas=(TableImpl)sums.copy();
		activations=(TableImpl)sums.copy();
		//printWeights();
 		setFiller();
		//printWeights();
		//printVTs();

		//find the activation function
		//System.out.println("act:"+params.getDouble(ACTIVATION_FUNCTION, 1));
		switch ((int)params[ACTIVATION_FUNCTION]){
			case 0:{
				act=new ElliotAct();
				break;
			}
			case 1:{
				act=new FastSigmoidAct();
				break;
			}
			case 2:{
				act=new FastTanhAct();
				break;
			}
			case 3:{
				act=new SigmoidAct();
				break;
			}
			case 4:{
				act=new TanhAct();
				break;
			}
			default: {
				act=new SigmoidAct();

			}
		}
		//now that we know what activation function, we can scale the outputs
		if(doTransform){
			data= (ExampleTableImpl)data.toExampleTable();
			transform(data);
		}

		//pull out the epochs count (which for now can double as the time limit if time is being used instead)
		//also get initial and final learning rate
		/*	System.out.println("epochs:"+params.getDouble(EPOCHS, 1));
		System.out.println("INITIAL_LEARNING_RATE:"+params.getDouble(INITIAL_LEARNING_RATE, 1));
		System.out.println("FINAL_LEARNING_RATE:"+params.getDouble(FINAL_LEARNING_RATE, 1));
		System.out.println("LEARNING_RATE_FUNCTION:"+params.getDouble(LEARNING_RATE_FUNCTION, 1));
		System.out.println("UPDATE_FUNCTION:"+params.getDouble(UPDATE_FUNCTION, 1));
		System.out.println(":"+params.getDouble(, 1));
		System.out.println(":"+params.getDouble(, 1));
		System.out.println(":"+params.getDouble(, 1));
		*/
		final double epochs=params[EPOCHS];
		final double initAlpha=params[INITIAL_LEARNING_RATE];
		final double finalAlpha=params[FINAL_LEARNING_RATE];

		//find the learning rate accelerator function
		switch ((int)params[LEARNING_RATE_FUNCTION]){
			case 0:{
				learnFn=new Linear(initAlpha, finalAlpha, (int)epochs);
				break;
			}
			default:{
				learnFn= new Linear(initAlpha, finalAlpha, (int)epochs);
			}
		}

			//find the NNupdate function to use (learning method)
			NNupdate creator;
		switch ((int)params[UPDATE_FUNCTION]){
			case 0:{
				creator=new StandardIncrementalBP(this);
				break;
			}
			case 1:{
				creator=new StandardBatchBP(this);
				break;
			}
			default:{
				creator=new StandardIncrementalBP(this);
			}
		}
		//System.out.println("****about to Create");
		//	printWeights();
		//printVTs();
		creator.create();
		//printVTs();
		if(debug){
			printWeights();
		}

   	   setName("MlpNNModel");

	   outputNames=new String[data.getNumOutputFeatures()];
	   for(int i=0; i<outputNames.length; i++){
		   outputNames[i]=data.getColumnLabel(data.getOutputFeatures()[i]);
	   }
	   //we're done with these
	   data=null;
	   learnFn=null;

	}

	/**********************************
		PREDICT
		*******************************/


        public PredictionTable predict(Table value) throws Exception {
          if(value instanceof ExampleTable)
            data = (ExampleTableImpl)value;
          return super.predict(value);

        }

//	public PredictionTable predict(ExampleTable value){
        protected void makePredictions(PredictionTable predTable) {




		//this now becomes the global data that the compute function uses
                // DC 3.5.03 this was put into the predict() method
		//data=(ExampleTableImpl)value;

		//make a VT to put the predictions in, a column for every output feature
/*		MutableTableImpl predictedResults= (MutableTableImpl)
                    DefaultTableFactory.getInstance().createTable(outputNames.length);
		for(int i=0; i<outputNames.length; i++){
			//make sure to get the real length, not just the number of test examples
			predictedResults.setColumn(new DoubleColumn(data.getNumRows()), i);
			predictedResults.setColumnLabel(outputNames[i], i);
		}

		//make predictions for the test examples
		for(int i=0; i<data.getNumRows(); i++){
			compute(i, predictedResults);
		}
		if(doTransform){
			unTransform(predictedResults);
		}
		//if the data is in a TestTable, transfer the predictedResutls VT
		//to in the output columns of the TestTable

		if(data instanceof TestTable){
			int numTests=data.getNumTestExamples();
			int numOutputs=data.getNumOutputFeatures();
			for(int i=0; i<numOutputs; i++){
				for(int j=0; j<numTests; j++){
					((TestTableImpl)data).setDouble(predictedResults.getDouble(j, i), j, ((TestTable)data).getPredictionSet()[i]);
				}
			}
			//if this was a TestTable, anything down the pipe will be expecting that table back
			//with the prediction features filled in
			return (TestTable)data;
		}

		PredictionTableImpl predTable= (PredictionTableImpl)data.toPredictionTable();

		//if there are no spots for pred columns
		if(predTable.getNumOutputFeatures()==0){
				for(int i=0; i<predictedResults.getNumColumns(); i++){
					predTable.addPredictionColumn(predictedResults.getColumn(i));
				}
		}else{ //the spots are already there
			for(int i=0; i<predictedResults.getNumColumns(); i++){
				predTable.setColumn(predictedResults.getColumn(i), predTable.getPredictionSet()[i]);
			}
		}
		data=null;
		return predTable;
*/
	}
        protected void makePrediction(ExampleTable example, int row, double [] predictedOutputs) {
        }

    /**
       Perform these actions when this module is executed.
    */
    public void doit() throws Exception {

		ExampleTable queryData=(ExampleTable)pullInput(0);

		PredictionTable results=predict(queryData);
		pushOutput(results, 0);

    }


	//these are here in case you want to use an old model as a starting point
	//also so the update/NNlearner can get to them
	public TableImpl getWeights(){
		return weights;
	}

	public TableImpl getSums(){
		return sums;
	}

	public TableImpl getActivations(){
		return activations;
	}

	public TableImpl getDeltas(){
		return deltas;
	}

	public ExampleTableImpl getData(){
		return data;
 	}

	public NNactivation getActivationFunction(){
		return act;
	}

	public NNlearn getLearnFunction(){
		return learnFn;
	}

    public String getModuleInfo() {
			return "Given some inputs, use back prop neural net to predict outputs.";
		}

	/* calculates outputs, puts them in 'results, e is the example (row of input data),
		setsIndices=normally the training or test set, but can be everything
					if that's what you want to be looked at
	*/

	public void compute(int e, TableImpl results){


		//finding first hidden layer activations

		for(int i=1; i<sums.getColumn(0).getNumRows(); i++){
			double tempSum=0;
			//DoubleColumn dc=(DoubleColumn)(weights.getObject(i,0));
			double[] inWeights=(double[])(((DoubleColumn)(weights.getObject(i, 0))).getInternal());

			/*
				b/c this is from the input layer, there is
				no 'activation' that is constantly -1, which is normally at row 0 in activations
				but there is no analogue in this layer because we only have the data inputs we're given
				*/
			tempSum+=inWeights[0]*bias;
			/*if((Double.isNaN(tempSum)||Double.isInfinite(tempSum)) && bug){
					System.out.println();
					for(int l=0; l<inWeights.length; l++){
						System.out.print(inWeights[l]+", ");
						System.out.println();
					}

					System.out.println("compute acts 0: err: tempsum="+tempSum+" j=0"+" i="+i);
					printWeights();
					printVTs();
					bug = false;
			}*/

			for(int j=1; j<inWeights.length; j++){
			/*if(trainOrTest){*/
				tempSum+=inWeights[j]*data.getDouble(e, data.getInputFeatures()[j-1]);

					/*if((Double.isNaN(tempSum)||Double.isInfinite(tempSum)) && bug){
					System.out.println("compute acts 0: err: tempsum="+tempSum+" j="+j+" i="+i);
					printWeights();
					printVTs();
					bug = false;
					}*/
				/*}
				else{
					tempSum+=inWeights[j]*data.getTestInputDouble(e, j-1);
				}*/
			}
			sums.setDouble(tempSum, i, 0);
			/*if((Double.isNaN(tempSum)||Double.isInfinite(tempSum)) && bug){
					System.out.println("COMPUTE acts 0: err: tempsum="+tempSum+" i="+i);
					printWeights();
					printVTs();
					bug = false;
				}
			*/
			activations.setDouble(act.activationOf(tempSum), i, 0);
		}


		//finding the rest of the activations

		for(int k=1; k<sums.getNumColumns(); k++){
			for(int i=1; i<sums.getColumn(k).getNumRows();i++){
				double[] inWeights=(double[])((DoubleColumn)weights.getObject(i, k)).getInternal();

				double tempSum=0;
				for (int j=0; j<inWeights.length; j++){

					tempSum+=inWeights[j]*activations.getDouble(j, k-1);
				/*	if((Double.isNaN(tempSum)||Double.isInfinite(tempSum)) && bug){
					System.out.println("COMPUTE acts "+k+" : err: tempsum="+tempSum+" i="+i);
					printWeights();
					printVTs();
					bug = false;
					}
					*/
				}

				sums.setDouble(tempSum, i, k);
				activations.setDouble(act.activationOf(tempSum), i, k);
			}
			for(int i=0; i<results.getNumColumns(); i++){
				results.setDouble(activations.getDouble(i+1, activations.getNumColumns()-1), e, i);
			}
		}
	}


  //randomly initializes the weights arrays
	private void randomizeWeights(){
		double d;
		Random rand=new Random((long)params[SEED]);
		double initRange=params[WEIGHT_INIT_RANGE];

		for(int i=0; i<weights.getNumColumns(); i++){
			for(int j=0; j<weights.getColumn(i).getNumRows(); j++){
				DoubleColumn dc=(DoubleColumn)weights.getObject(j, i);
				for(int k=0; k<dc.getNumRows(); k++){
					d=((rand.nextDouble()*2*initRange)-initRange);
					dc.setDouble(d, k);
				}
			}
		}
	}
	//prints the weights VT to the screen
	private void printWeights(){
		System.out.println("Final Weights");
		for(int c=0; c<weights.getNumColumns(); c++){
			System.out.println("c:"+weights.getColumn(c).getNumRows());
			for (int r=0; r<weights.getColumn(c).getNumRows(); r++){
				System.out.print(c+" "+r+":");
				DoubleColumn dc=(DoubleColumn)weights.getObject(r,c);
				for(int rr=0; rr<dc.getNumRows(); rr++){
					System.out.print(dc.getDouble(rr)+", ");
				}
				System.out.println();
			}
		}
	}

	//print the deltas, sums, activations,
	private void printVTs(){
		System.out.println("Sums:");
		printVT(sums);
		System.out.println("Deltas:");
		printVT(deltas);
		System.out.println("Acts:");
		printVT(activations);
	}
	private void printVT(TableImpl vt){
		for(int i=0; i<vt.getNumColumns(); i++){
			System.out.print("col "+i+":");
			for(int j=0; j<vt.getColumn(i).getNumRows(); j++){
				System.out.print(vt.getDouble(j, i)+", ");
			}
			System.out.println();
		}
	}
	//put the bias in the activations at index [0] to use as a threshold
	//put NaN in the sums and deltas at index [0]
	//also fills the appropriate weight spots with doublecolumns containing NaN at [0]

	private void setFiller(){
		double nan=Double.NaN;

		for(int i=0; i<activations.getNumColumns();i++){
			activations.setDouble(bias, 0, i);
			sums.setDouble(nan, 0, i);
			deltas.setDouble(nan, 0, i);

		}
		for(int i=0; i<weights.getNumColumns(); i++){
			DoubleColumn dc=(DoubleColumn)weights.getObject(0, i);
			for(int j=0; j<dc.getNumRows(); j++){
				dc.setDouble(nan, j);
			}
		}
	}

	//fix this so that it only looks at training examples when determining scaling
	private void transform(ExampleTableImpl et){
		boolean sig=false;
		switch ((int)params[ACTIVATION_FUNCTION]){
			case 0:{
				sig=false;
				break;
			}
			case 1:{
				sig=true;
				break;
			}
			case 2:{
				sig=false;
				break;
			}
			case 3:{
				sig=true;
				break;
			}
			case 4:{
				sig=false;
				break;
			}
			default: {
				sig=true;
			}
		}

		if(sig){
			lowerBound=lowerSig;
			upperBound=upperSig;
		}else{
			lowerBound=lowerTanh;
			upperBound=upperTanh;
		}
		newRange=upperBound-lowerBound;
		invNewRange=1/newRange;

		transformOutputInfo=new double[et.getNumOutputFeatures()][2];

		for(int k=0; k<et.getNumOutputFeatures(); k++){

			//make a copy in case something else is also trying to use this table
	    	NumericColumn current_column=(NumericColumn)(et.getColumn(et.getOutputFeatures()[k]).copy());

	   		transformOutputInfo[k][1]=((DoubleColumn)current_column).getMax()
					-((DoubleColumn)current_column).getMin();
			transformOutputInfo[k][0] = ((DoubleColumn)current_column).getMin();
	 		int numRows=current_column.getNumRows();

			for	(int j=0; j<numRows; j++){
				double d=current_column.getDouble(j);
				//transform to the interval [0,1]
				d=((d-transformOutputInfo[k][0])/transformOutputInfo[k][1]);
				//now to the interval [lowBound, hiBoung]
				d=(d*newRange + lowerBound);

				current_column.setDouble(d, j);
			}
			et.setColumn(current_column, et.getOutputFeatures()[k]);

		}
	return;

	}

	private void unTransform(TableImpl vt){
		final int outputCount=transformOutputInfo.length;
		final int rows=vt.getColumn(0).getNumRows();
		double d;
		for(int k=0;k<outputCount; k++){
			DoubleColumn current_column=(DoubleColumn)vt.getColumn(k);
			for(int r=0;r<rows; r++){
				d=current_column.getDouble(r);
				//double e=d;

				d-=lowerBound;
				d*=invNewRange;
				d*=transformOutputInfo[k][1];
				d+=transformOutputInfo[k][0];
				current_column.setDouble(d, r);
			}
		}
	}



		/**
			Returns the input types, of which there are none.

			@returns the input types
		*/
		public String []getInputTypes() {
			String []inputs = {
				"ncsa.d2k.modules.core.datatype.table.ExampleTable"
};

			return inputs;
		}

		/**
			Returns the output types all of which are strings, the names
			of the possible operations.

			@returns the output types
		*/
		public String []getOutputTypes() {
			String []out = {"ncsa.d2k.modules.core.datatype.table.Table"};
			return out;
		}

		/**
			This method will return a text description of the the input indexed by
			the value passed in.

			@param index the index of the input we want the description of.
			@returns a text description of the indexed input
		*/
		public String getInputInfo(int i) {
			switch(i) {
				case 0:
					return "The input ExampleTable to use in prediction. Only Test Examples will be used.";
				default:
					return "There is no such input.";
			}
		}

		/**
			This method will return a text description of the the output indexed by
			the value passed in.

			@param index the index of the output we want the description of.
			@returns a text description of the indexed output
		*/
		public String getOutputInfo(int i) {
			switch(i) {
				case(0):
					return "The result Table with each output being a column with an entry for each example in the input data's test set."
						+"IF the input table was a TestTable, this output will be the same TestTable with the PredictionSet columns filled in";

				default:
					return "There is no such output!";
			}
		}
	}







	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Neural Network Module Generator";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "data";
			case 1:
				return "Parameters";
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
				return "model";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
