package ncsa.d2k.modules.core.prediction.neuralnet;

import ncsa.d2k.modules.PredictionModelModule;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.ScalingTransformation;

import ncsa.d2k.modules.core.datatype.parameter.*;

import java.io.Serializable;
import java.util.Random;
import ncsa.d2k.modules.core.util.*;

/**
	<html>
	This is a rudimentary backprop neural net. Most of the concepts used in
	this implementation can be found in the following two introductions to
	neural networks:
	<ul>
	<li>NN FAQ, Maintained by Warren Sarle of the SAS institute:
			ftp://ftp.sas.com/pub/neural/FAQ.html
	<li>Russel, S., Norvig, P. (1995) Artificial Intelligence: A Modern
			Approach.  Upper Saddle River, NJ: Prentice-Hall
	</ul>


	The following are the parameters of the BackProp Neural Network. Parameter
	names are in bold, options are underlined. Numbers following the option
	indicate the index that refers to that option in the parameter object.
	<ul>
	<li><b>Activation function</b>:
		The weighted sum of the input weights and inputs to every perceptron
		are fed through the activation function to ensure that the output
		is between 0 and 1 (or -1 and 1, depending on which function). These
		functions also must have a few other properties to ensure the back
		propagation function is able to work
		<ul>
			<li><u>Elliot</u> - 0 -
			From a paper by D.L. Elliot. ElliotAct(x)=|x/(1-x)|. This can be
			computed much faster than Sigmoid or Tanh and usually gives good
			results.
			<li><u>FastSigmoid</u> - 1 - A linear approximation of the Sigmoid
			function, which makes it faster but less accurate.
			<li><u>FastTanh</u> - 2 - A linear approximation of Tanh, similar
			issues as FastSigmoid
			<li><u>Sigmoid</u> - 3 - The function multi-layered perceptron
			neural
			 networks were first developed with. Normally neural nets that use
			 them are fairly accurate, but the function evaluation requires
			 calculating an exponential, which is computationally expensive
			 (read "slow").
			<li><u>Tanh</u> - 4 - Another expensive, accurate function.
		</ul>
	<li><b>Training Method</b>:This function defines the order the examples
	are trained on and when the update of the activation weights occurs.
		<ul>
			<li><u>Incremental BackProp</u> - 0 - The activation weights are
			updated after every training example is passesed through, and the
			examples are passed through in the order given in the training
			data. This is slower than Batch BackProp.
			<li><u>Batch BackProp</u> - 1 - Weights are updated after every
			epoch
			(complete pass through all examples), examples iterated over in
			order. This is less expensive
			than Incremental BackProp. In practice, it usually provides better
			results, as well.
		</ul>
	<li><b>Epochs</b>: The number of passes through the training data set
	(iterations) that the training function will do.
	<li><b>Seed</b>: A seed to the random weight initialization. This can't
	really be optimized but trying different values for any parameter setting
	is a good idea as back propagation is capable of finding only the local
	minimum.
	<li><b>Weight Initialization Range</b>: The activation weights will be
	randomly initialized to values between zero and this value. This is
	particularly useful if the inputs in the data set (independent variables)
	are not scaled to a standard range.
	<li><b>Learning Accelerator</b>: Learning acceleration refers to changing
	the learning rate as the training process proceeds. This can be based on
	the epoch or the time, and can be any kind of monotonically decreasing
	function. The purpose of altering the learning rate is to make large
	adjustments initially when the weights are still near-random and then
	smaller as the network approaches an optimal solution (think of it as a
	hill climbing algorithm that takes big steps when it's far from the
	optimum and takes smaller steps at it approaches the optimum for better
	accuracy). Currently only Linear by Epoch is implemented, but the
	infrastructure is such that other methods can easily be added.
	<ul>
		<li><u>Linear by Epoch</u> - 0 - Starts at the Initial Learning Rate
		and
		decreases it the same amount every epoch, such that the final epoch
		uses a learning rate of Final Learning Rate.
	</ul>
	<li><b>Initial Learning Rate</b>: The learning rate of the first epoch.
	<li><b>Final Learning Rate</b>: The learning rate of the last epoch.
	<li><b>Number of Hidden Layers</b>:This is the number of layers of
	perceptrons between the input nodes and the output nodes. Currently
	restricted to be between one and four. This restriction is only in place
	because a more sophisticated parameter selection interface is not in place.
	The actual algorithm can handle any number of hidden layers.
	<li><b>Nodes in Layer[1-4]</b>: The number of nodes in each layer, set
	individually. This can be any positive integer, although in practice values
	greater than 20 usually cause the model building process to run longer than
	is practical.
	</html>
*/
public class BackPropModel extends PredictionModelModule
	implements Serializable{


    /** true if debugging info should be generated */
	boolean debug=false;

	/////////////////////////
	/// other fields
	////////////////////////

	/* the total number of parameters this NN takes*/
	public final static int NUM_PARAMS=13;

	/**
     * The weighted sum of the input weights and inputs to every perceptron
     * are fed through the activation function to ensure that the output
     * is between 0 and 1 (or -1 and 1, depending on which function). These
     * functions also must have a few other properties to ensure the back
     * propagation function is able to work.
     * <ul>
     * <li><u>Elliot</u> - 0 -
     * From a paper by D.L. Elliot. ElliotAct(x)=|x/(1-x)|. This can be
     * computed much faster than Sigmoid or Tanh and usually gives good results.
     * <li><u>FastSigmoid</u> - 1 - A linear approximation of the Sigmoid
     * function, which makes it faster but less accurate.
     * <li><u>FastTanh</u> - 2 - A linear approximation of Tanh, similar
     * issues as FastSigmoid.
     * <li><u>Sigmoid</u> - 3 - The function multi-layered perceptron neural
     * networks were first developed with. Normally neural nets that use them
     * are fairly accurate, but the function evaluation requires calculating
     * an exponential, which is computationally expensive (read \"slow\").
     * <li><u>Tanh</u> - 4 - Another expensive, accurate function. </ul>
	*/
	public final static int ACTIVATION_FUNCTION=0;

    /**
     * This function defines the order the examples are trained on and when
     * the update of the activation weights occurs.
     * <ul>
     * <li><u>Incremental BackProp</u> - 0 - The activation weights are
     * updated after every training example is passed through, and the
     * examples are passed through in the order given in the training data. \
     * This is slower than Batch BackProp.
     * <li><u>Batch BackProp</u> - 1 - Weights are updated after every epoch
     * (complete pass through all examples), examples are iterated over in
     * order. This is less expensive than Incremental BackProp. In practice, it
     * usually provides better results, as well.
     * </ul>
     */
    public final static int UPDATE_FUNCTION=1;

    /**
     * 	The number of passes through the training data set (iterations)
     * that the training function will do.
     */
    public final static int EPOCHS=2;

    /**
     * 	A seed to the random weight initialization. This can't really be
     * optimized but trying different values for any parameter setting is a
     * good idea as back propagation is capable of finding only the locally
     * optimum set of weights.
     */
    public final static int SEED=3;

    /**
     * The activation weights will be randomly initiallized to values between
     * zero and this value. This is particularly useful if the inputs in the
     * data set (independent variables) are not scaled to a standard range.
     */
    public final static int WEIGHT_INIT_RANGE=4;

    /**
     *  The learning rate indicates how much of an adjustment to the weights
     * will be done during every update.
     * Learning acceleration refers to changing the learning rate as the
     * training process proceeds. This can be based on the epoch or the time,
     * and can be any kind of monotonically decreasing function. The purpose of
     * altering the learning rate is to make large adjustments initially when
     * the weights are still near-random and then smaller as the network
     * approaches an optimal solution (think of it as a hill climbing algorithm
     * that takes big steps when it's far from the optimum and takes smaller
     * steps at it approaches the optimum for better accuracy). Currently only
     * Linear by Epoch is implemented, but the infrastructure is such that
     * other methods can easily be added.
     * <ul>
     * <li><u>Linear by Epoch</u> - 0 - Starts at the Initial Learning Rate and
     * decreases it the same amount every epoch, such that the final epoch
     * uses a learning rate of Final Learning Rate.
     * </ul>
     */
    public final static int LEARNING_RATE_FUNCTION=5;

    /**
     *  The learning rate of the first epoch.
     */
    public final static int INITIAL_LEARNING_RATE=6;

    /**
     * The learning rate of the last epoch.
     */
    public final static int FINAL_LEARNING_RATE=7;

    /**
     * 	This is the number of layers of perceptrons between the input nodes and
     * the output nodes. Currently restricted to be between one and four. This
     * restriction is only in place because a more sophisticated parameter
     * selection interface is not in place. The actual algorithm can handle any
     * number of hidden layers.
     */
    public final static int HIDDEN_LAYERS=8;

    /**
     * The number of nodes in first layer. This can be any positive integer,
     * although in practice values greater than 20 usually cause the model
     * building process to run longer than is practical.
     */
    public final static int NODES_IN_LAYER_01=9;

    /**
     * The number of nodes in second layer. This can be any positive integer,
     * although in practice values greater than 20 usually cause the model
     * building process to run longer than is practical.
     */
    public final static int NODES_IN_LAYER_02=10;

    /**
     * The number of nodes in third layer. This can be any positive integer,
     * although in practice values greater than 20 usually cause the model
     * building process to run longer than is practical.
     */
    public final static int NODES_IN_LAYER_03=11;

    /**
     * The number of nodes in fourth layer. This can be any positive integer,
     * although in practice values greater than 20 usually cause the model
     * building process to run longer than is practical.
     */
    public final static int NODES_IN_LAYER_04=12;

	/** weights [i][j][k] for connections between nodes.
		where 	i=the layer of the 'to' node
				j=the 'to' node (including outputs)
				k=the 'from' node

		when j=0, all values are NaN as they correspond to the weights into
		the threshold nodes. These are never used and are only
	   present so that indexing is constant between weights, sums, activations,
	   etc.  NaN is used so no one accidentally uses them later on without
	   there being something obviously wrong
	**/
	public double[][][] weights;

	/**sums[i][j]
		the weighted sums for each node (before activation).
		the column (i) corresponds to layer (outputs
		are the last layer), row (j) is index within the layer,
		row zero is the threshold node, therefore set to NaN
		because it should never be used, just there for that
		indexing thing again
	*/
	private double[][] sums;

	/**the activation function values for all nodes, including outputs.
	  same mapping as 'sums'. row zero is set to the threshold/bias value
	  (normally -1)
	*/
	private double[][] activations;

	/**The 'deltas', or error distribution values, for each node.
	  same mapping as 'sums'
	*/
	private double[][] deltas;

	/** The parameters for the neural net architecture.
	*/
	public final ParameterPoint params;

	/** The data set being trained or tested.
	*/
	public ExampleTable data;


	/** The activation the compute class will use.
	*/
	protected NNactivation act;


	/**The function that controls the learning rate and the learning time.
	*/
	protected NNlearn learnFn;

	/**controls the scaling/unscaling of the inputs and outputs*/
	ScalingTransformation scaler;

    /** bias */
    public final double bias=-1.0;

    /** true if this model was succesfully trained.  to be successful, all
     * inputs and outputs must be scalar
     */
    public final boolean trainingSuccess;

	/** lower bound to scale the outputs to in <code>transform</code>
	*/
	final double lowerTanh=-.9;

	/** upper bound to scale the outputs to in <code>transform</code>
	*/
    final double upperTanh=.9;

	/** lower bound to scale the outputs to in <code>transform</code>
	*/
    final double lowerSig=.1;

	/** upper bound to scale the outputs to in <code>transform</code>
	*/
    final double upperSig=.9;

	/**
		These may be different than the input features in the training
		table, if the inputs in the training table are invalid
		*/
	protected int[] inputFeatures;

    /** the number of inputs */
    protected int numInputs;

    /**
		These may be different than the output features in the training
		table, if the outputs in the training table are invalid
		*/
	protected int[] outputFeatures;
    /** the number of outputs */
    protected int numOutputs;
    
    private D2KModuleLogger myLogger;
    
    public void beginExecution() {
 	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
    }
    /**
     * Pull in the ExampleTable, pass it to the predict() method,
     * and push out the PredictionTable returned by predict();
     * @throws Exception when something goes wrong
     */
    public void doit() throws Exception {
        if (debug) {
      	  myLogger.setDebugLoggingLevel();//temp set to debug
      	  myLogger.debug(getAlias() + ":Firing");
          myLogger.resetLoggingLevel();//re-set level to original level
        }
        super.doit();
	}


	/**
     * Builds a BackPropModel with the given parameters.
     * @param et the data set to train on
     * @param prms a table with the parameters to use
	 */
	public BackPropModel(ExampleTable et, ParameterPoint prms){
		super(et);
		int i,j,k;
		//data=et.getTrainTable(); !!!
                data=et;

		params=prms;
		/* make sure all of the columns we have to work with are
		scalar*/
		if(!verifyData()){
			 trainingSuccess=false;
			 return;
		}


		//transformTrainTable();  !!!

		//set up arrays, fill w/ random values
		initArrays();


		//find the activation function
		switch ((int)params.getValue(ACTIVATION_FUNCTION)){
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


		//pull out the epochs count
		//also get initial and final learning rate

		final double epochs=params.getValue(EPOCHS);
		final double initAlpha=params.getValue(INITIAL_LEARNING_RATE);
		final double finalAlpha=params.getValue(FINAL_LEARNING_RATE);

		//find the learning rate accelerator function
		switch ((int)params.getValue(LEARNING_RATE_FUNCTION)){
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
		switch ((int)params.getValue(UPDATE_FUNCTION)){
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
		creator.create();
		if(debug){
			printWeights();
		}

	   setName("BackProp NN");

	   //we're done with these
	   data=null;

		trainingSuccess=true;
	}



	/***
     * the main function to calculate the output(s) given an input vector.
     * @param e the row index in the table 'data' to use as input
     * @param results [e][out] the output 'out' will be put in row 'e' of this table
	*/
	public void compute(int e, double[][] results){
		int i,j,k;
		double tempSum;

		int numNodes;
		int numNodesPrevLayer;

		int numLayers=sums.length;

		//finding first hidden layer activations
		numNodes=sums[0].length;
		numNodesPrevLayer=data.getNumInputFeatures()+1;
		for(i=1; i<numNodes; i++){
			tempSum=0;
			/*
				b/c this is from the input layer, there is
				no 'activation' that is constantly -1,
				which is normally at row 0 in activations
				but there is no analogue in this layer because we only
				have the data inputs we're given
				*/
			tempSum+=weights[0][i][0]*bias;


			for(j=1; j<numNodesPrevLayer; j++){
				tempSum+=weights[0][i][j]*data.getDouble(
								e, inputFeatures[j-1]);
			}
			sums[0][i]=tempSum;
			activations[0][i]=act.activationOf(tempSum);
		}


		//finding the rest of the activations

		for(k=1; k<numLayers; k++){
			numNodesPrevLayer=numNodes;
			numNodes=sums[k].length;
			for(i=1; i<numNodes;i++){
				tempSum=0;
				for (j=0; j<numNodesPrevLayer; j++){
					tempSum+=weights[k][i][j]*activations[k-1][j];
				}

				sums[k][i]=tempSum;
				activations[k][i]=act.activationOf(tempSum);
			}
			for(i=0; i<results[0].length; i++){
				//the activations of the last layer (the output layer)
				//are the predicted outputs
				results[e][i]=activations[numLayers-1][i+1];
			}
		}
	}


		/**
         * Scales the outputs of the training data to the range required
         * by the activation function chosen. No longer scales the inputs
         * to the range -2,2. It will now be up to the user to scale the
         * inputs appropriately (it may be helpful to optimize the range).
		*/
	private void transformTrainTable(){
		boolean sig=false;

		switch ((int)params.getValue(ACTIVATION_FUNCTION)){
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
		double lowerBound;
		double upperBound;
		if(sig){
			lowerBound=lowerSig;
			upperBound=upperSig;
		}else{
			lowerBound=lowerTanh;
			upperBound=upperTanh;
		}
		/*
		int numOutputs=et.getNumOutputFeatures();
		int numInputs=et.getNumInputFeatures();

		int colsToScale=numOutputs+numInputs;
		int[] colIndices=new int[colsToScale];

		double[] maxs=new double[colsToScale];
		double[] mins=new double[colsToScale];
		int i;
		for(i=0;i<numInputs;i++){
			maxs[i]=-2;
			mins[i]=2;
			colIndices[i]=et.getInputFeatures()[i];

		}

		for(;i<colsToScale;i++){
			maxs[i]=upperBound;
			mins[i]=lowerBound;
			colIndices[i]=et.getOutputFeatures()[i-numInputs];
		}
		*/
		int i;

		int colsToScale=numOutputs;
		int[] colIndices=new int[colsToScale];

		double[] maxs=new double[colsToScale];
		double[] mins=new double[colsToScale];

		for(i=0;i<colsToScale;i++){
			maxs[i]=upperBound;
			mins[i]=lowerBound;
			colIndices[i]=outputFeatures[i];
		}

		scaler=new ScalingTransformation(colIndices, mins, maxs, this.data);

		scaler.transform(this.data);
		this.getTransformations().add(scaler);


	}

	/**
     * Checks for non-numeric (or non-boolean) inputs and outputs.
     * will remove them and reset the data if found. if removing them leaves
     * either zero inputs or zero outputs, this will return false and the
     * model-building will have to fail
    */
	protected boolean verifyData(){
		int i,j;
		numInputs = data.getNumInputFeatures();
		numOutputs = data.getNumOutputFeatures();

		//int numericInputs=0;
		//int numericOutputs=0;

		int type;
		//start with the inputs
		int numNumeric;
		//these are true if the column is not scalar/useable
		boolean[] noms=new boolean[numInputs];
		int numCols=numInputs;
		int[] features=data.getInputFeatures();
		for(int gg=0;gg<2;gg++){//once for inputs, once for outputs
		numNumeric=0;
		for(i=0;i<numCols;i++){
			type=data.getColumnType(features[i]);
			switch(type){

			case (ColumnTypes.DOUBLE) :
			case (ColumnTypes.INTEGER) :
			case (ColumnTypes.FLOAT) :
			case (ColumnTypes.SHORT) :
			case (ColumnTypes.LONG) : {
				 noms[i]=false;
				 numNumeric++;
				 break;
			}
			case (ColumnTypes.BOOLEAN) : {
				 //a boolean is exceptable as input, but not as output
				 if(gg==0){
					  noms[i]=false;
					  numNumeric++;
				 }else{
					  noms[i]=true;
					  alertRemoving(features[i],gg);
				 }
				break;
			}
			case (ColumnTypes.STRING) :
			case (ColumnTypes.CHAR_ARRAY) :
			case (ColumnTypes.BYTE_ARRAY) :
			case (ColumnTypes.OBJECT) :
			case (ColumnTypes.BYTE) :
			case (ColumnTypes.CHAR) :
			default : {
				 //isColumnNumeric returns true iff
				 //all values in the column can be parsed into
				 //a double, so its worth a shot
				 if(data.isColumnNumeric(features[i])){
					  noms[i]=false;
					  numNumeric++;
				 }else{
					  noms[i]=true;
					  alertRemoving(features[i], gg);
				 }
			}//default
			}//switch type
		}//for numCols
		if(numNumeric==0){
			alertFailure(gg);
			return false;
		}
		int [] useableCols=new int[numNumeric];
		j=0;
		for(i=0;i<numCols;i++){
			if(!noms[i]){
				  useableCols[j]=features[i];
				  j++;
			}
		}
		if(gg==0){//we were using inputs
			 this.inputFeatures = useableCols;
			 this.numInputs = this.inputFeatures.length;

			 //switch to outputs
			 numCols=numOutputs;

			 features=data.getOutputFeatures();

			 noms=new boolean[numOutputs];
		}else{//it was the outputs
			this.outputFeatures = useableCols;
			this.numOutputs = this.outputFeatures.length;
		}
		}//for gg

		//reset the superclass's info about training data
		//this is kind of a hack to get the local input features
		//to be known by the superclass (PMM), but ultimately
		//we don't want to change the input features of the
		//data table (TrainTable)
		int[] it = data.getInputFeatures();
		int[] ot = data.getOutputFeatures();

		data.setInputFeatures(this.inputFeatures);
		data.setOutputFeatures(this.outputFeatures);

		this.setTrainingTable(data);

		data.setInputFeatures(it);
		data.setOutputFeatures(ot);


		return true;

	}//verifyData()

	/**
     * tells the user that a column was of the wrong type and will not be
     * part of training.
     * @param inout  0 for inputs, 1 for outputs
     * @param colIdx column in table 'data'
	*/
	private void alertRemoving(int colIdx, int inout){
		String io;
		if(inout==0)
			 io="Input";
		else
			 io="Output";
		myLogger.debug("** BackPropModel Construction: "+io+" Feature Column"+
				colIdx+", *"+data.getColumnLabel(colIdx)+"* was not of a supported type"+
				" and was removed from the training data. See ModuleInfo for valid"+
				" types.");
	}

	/**
     *  Called if the data verification process removed either all input or
     * all output columns.  Will print warning message
     * @param inout should be 0 if all input columns removed, any other value to
     * signify that all output columns were removed
	*/
	private void alertFailure(int inout){
		String io;
		if(inout==0)
			 io="Input";
		else
			 io="Output";
		myLogger.debug("** BackPropModel Construction: There are no valid"+
				io+ " Features. The model building process is being aborted.");
	}


	/**
     * sets up the weights, sums, activations, and deltas arrays, puts in
     * NaN in the appropriate places, and fills the weights w/ random
     * initial values in the range defined by the params
	*/
	protected void initArrays(){
		int i,j,k;
		/////////////////////
		//initialize sums,deltas,activations
		//////////////////////

		int numLayers=(int)params.getValue(HIDDEN_LAYERS);
		sums= new double[numLayers+1][];
		deltas=new double[numLayers+1][];
		activations=new double[numLayers+1][];



		/*
		starts at NODES_IN_LAYER_01 and goes through every NODES_IN_LAYER_XX
		that HIDDEN_LAYERS indicates should be used, adding a column with the
		number of rows indicated by the params table includes space for a
		threshold node at index [0], whose activation is always the
		bias/threshold (sums/deltas threshold index aren't used, but we want
		sums, acts, and deltas to be the same dimensions
		*/

		for(i=0; i<numLayers; i++){
			j=((int)params.getValue(NODES_IN_LAYER_01+i))+1;
			sums[i]=new double[j];
			deltas[i]=new double[j];
			activations[i]=new double[j];
		}
		//also add a column for the outputs
		sums[numLayers]=new double[numOutputs+1];
		deltas[numLayers]=new double[numOutputs+1];
		activations[numLayers]=new double[numOutputs+1];

		//////////////////////
		//initialize weights array
		//////////////////////

		weights= new double[((int)params.getValue(HIDDEN_LAYERS)+1)][][];
		int weightColIdx=0;
		int numNodes;
		int numNodesPrevLayer;


		//when there are hidden layers
		if(numLayers>0){

			//inputs to first layer
			numNodes=((int)params.getValue(NODES_IN_LAYER_01))+1;
			weights[weightColIdx]=new double [numNodes][];
			for(k=0;k<numNodes;k++){
				weights[weightColIdx][k]=
					new double[numInputs+1];
			}
			weightColIdx++;


			//hidden layer to hidden layer
			//don't look at the last column (representing outputs) yet
			numNodesPrevLayer=numNodes;

			for (i=1; i<numLayers; i++){
				numNodes=((int)params.getValue(NODES_IN_LAYER_01+i))+1;
				weights[weightColIdx]=new double[numNodes][];
				for(k=0;k<numNodes;k++){
					weights[weightColIdx][k]=new double[numNodesPrevLayer];
				}
				numNodesPrevLayer=numNodes;
				weightColIdx++;
			}

			//last hidden layer to outputs
			numNodes=data.getNumOutputFeatures()+1;
			weights[weightColIdx]=new double[numNodes][];
			for(k=0;k<numNodes;k++){
				weights[weightColIdx][k]=new double[numNodesPrevLayer];
			}

		}
		//if no hidden layers, go straight from inputs to outputs
		else{
			numNodes=data.getNumOutputFeatures()+1;
			numNodesPrevLayer=data.getNumInputFeatures()+1;

			weights[weightColIdx]=new double[numNodes][];
			for(k=0;k<numNodes;k++){
				weights[weightColIdx][k]=new double[numNodesPrevLayer];
			}
			//won't use this anymore, just to keep from getting confused

			weightColIdx++;
		}
		randomizeWeights();
		setFiller();
	}

  /**
   * randomly initializes the weights arrays within the range
   * [0,Weight_init_range]
  */
	private void randomizeWeights(){
		double d;
		Random rand=new Random((long)params.getValue(SEED));
		double initRange=params.getValue(WEIGHT_INIT_RANGE);

		for(int i=0; i<weights.length; i++){
			for(int j=0; j<weights[i].length; j++){
				for(int k=0; k<weights[i][j].length; k++){
					d=((rand.nextDouble()*2*initRange)-initRange);
					weights[i][j][k]=d;
				}
			}
		}
	}

	/**
     * put the bias in the activations at index [0] to use as a threshold
     * put NaN in the sums and deltas at index [0]
     * also fills the appropriate weight spots with  NaN
	*/
	private void setFiller(){
		double nan=Double.NaN;

		for(int i=0; i<activations.length;i++){
			activations[i][0]=bias;
			sums[i][0]=nan;
			deltas[i][0]=nan;

		}
		for(int i=0; i<weights.length; i++){
			for(int j=0; j<weights[i][0].length; j++){
				weights[i][0][j]=nan;
			}
		}
	}


    /**
     * Make predictions on a prediction table
     *
     * @param pt table to store predictions
     */
    public void makePredictions(PredictionTable pt) {
        //make predictions for the test examples
        data = pt;

        try {
            //must scale the inputs before making predicitons
            //!!!scaler.transform(pt);
        } catch (Exception e) {
            System.out.println("Error scaling the prediction table. A possible" +
                    " reason for this is that the prediction table *must* have its" +
                    " output columns set for the neural net to work (even though this" +
                    " is not normally a constraint.");
            e.printStackTrace();
        }

        //make a Table to put the predictions in, a column for every output
        //feature
        int numRows = pt.getNumRows();
        int numOutputs = this.getOutputColumnLabels().length;
        double[][] predictedResults = new double[numRows][numOutputs];

        //make predictions, put them in predictedResults
        for (int i = 0; i < numRows; i++) {
            compute(i, predictedResults);
            //transfer the predictions to the prediction table
            for (int j = 0; j < numOutputs; j++) {
                pt.setDoublePrediction(predictedResults[i][j], i, j);
            }
        }
        //now must untransform for the sake of the predictions
        //!!!scaler.untransform(pt);
	}

	/**
     * prints the arrays, only for debugging
	*/
	private void printVTs(){
		System.out.println("Sums:");
		printVT(sums);
		System.out.println("Deltas:");
		printVT(deltas);
		System.out.println("Acts:");
		printVT(activations);
	}

    /**
     * prints the arrays, only for debugging
	*/
    private void printVT(double[][] vt){
		for(int i=0; i<vt.length; i++){
			System.out.print("col "+i+":");
			for(int j=0; j<vt[i].length; j++){
				System.out.print(vt[i][j]+", ");
			}
			System.out.println();
		}
	}

	/**
     * prints the arrays, only for debugging
	*/
    private void printWeights(){
		for(int i=0;i<weights.length;i++){
			for(int j=0;j<weights[i].length;j++){
				for(int k=0;k<weights[i][j].length;k++){
					System.out.println("From Node IDX:"+k+", To Node IDX:"+
						j+", Layer of To Node:"+i+", Weight:"+
						weights[i][j][k]);
				}
			}
		}
	}

    /**
     * Get the weigths
     * @return weights
     */
    public double[][][] getWeights(){
		return weights;
	}

    /**
     * Get the sums
     * @return sums
     */
    public double[][] getSums(){
		return sums;
	}

    /**
     * Get the activations
     * @return activations
     */
    public double[][] getActivations(){
		return activations;
	}

    /**
     * Get the deltas
     * @return deltas
     */
    public double[][] getDeltas(){
		return deltas;
	}

    /**
     * Get the training data
     * @return training data
     */
    public ExampleTable getData(){
		return data;
	}

    /**
     * Get the activation function
     * @return activation function
     */
    public NNactivation getActivationFunction(){
		return act;
	}

    /**
     * Get the learning function
     * @return learning function
     */
    public NNlearn getLearnFunction(){
		return learnFn;
	}


    /**
     * Describe the function of this module.
     *
     * @return the description of this module.
     */
    public String getModuleInfo() {
        return
                "<p><b>Overview</b>: This is an instance of a Back Propagation " +
                        " Neural Network produced by the module BackPropModelGenerator.  " +
                        "</p><p><b>Detailed Description</b>: This model was built using a data " +
                        " set with the following properties:</p><br> " +

                        super.getTrainingInfoHtml() +

                        "<p><b>Parameters</b>: The parameters that defined this neural " +
                        "network are " +
                        "listed below. See the documentation of the model " +
                        "producer (BackPropModelGenerator) for descriptions " +
                        "of these attributes.</p> " +

                        getModelInfoHtml() +

                        "<p><b>Data Type Restrictions</b>: This module requires the input " +
                        "ExampleTable " +
                        "to have the same number of input features as the data trained " +
                        "on, and for all input features to be scalar or boolean. " +
                        "</p><p><b>Data Handling</b>:If the input table is a PredictionTable " +
                        "with prediction " +
                        "columns already present, those prediction columns will simply " +
                        "be filled " +
                        "with predictions and the same table will be returned. If not, a  " +
                        "PredictionTable will be generated from the input ExampleTable " +
                        "and the " +
                        "necessary prediction columns will be added and filled in. " +
                        "</p><p><b>Scalability</b>: Prediction using a backprop neural net is " +
                        "much " +
                        "faster " +
                        "than training. As a result, predictions for any data set of size " +
                        "roughly the same order of magnitude as the training set will be " +
                        "computed in a reasonable amount of time as compared to the " +
                        "training process.</p> ";
    }

    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Back Propagation Neural Net";
    }

	/**
     * formats the parameters used to build this model into html for use by the
     * module doc
     * @return nicely formatted html
     **/
	private String getModelInfoHtml(){
		StringBuffer sb=new StringBuffer();
		sb.append("<ul>");
		sb.append("<li>Activation Function: <i>"+act.getName()+"</i>");
		sb.append("<li>Training Method: ");
		switch ((int)params.getValue(UPDATE_FUNCTION)){
			case 0:{
				sb.append("<i>Incremental BackProp</i>");
				break;
			}
			case 1:{
				sb.append("<i>Batch BackProp</i>");
				break;
			}
			default:{
				sb.append("<i>Incremental BackProp</i>");
			}
		}
		sb.append("<li>Learning Accelerator: <i>"+learnFn.getName()+"</i></li>");
		sb.append("<li>Initial Learning Rate: <i>");
		sb.append(params.getValue(INITIAL_LEARNING_RATE)+"</i></li>");
		sb.append("<li>Final Learning Rate: <i>");
		sb.append(params.getValue(FINAL_LEARNING_RATE)+"</i></li>");
		sb.append("<li>Epochs: <i>"+(int)params.getValue(EPOCHS)+"</i></li>");
		sb.append("<li>Hidden Layers: <i>");
		sb.append((int)params.getValue(HIDDEN_LAYERS)+"</i></li>");
		for(int i=0;i<params.getValue(HIDDEN_LAYERS);i++){
			sb.append("<li>Nodes in Layer "+i+1+": <i>");
			sb.append((int)params.getValue(NODES_IN_LAYER_01+i)+"</i></li>");
		}
		sb.append("<li>Weight Initialization Range: <i>");
		sb.append(params.getValue(WEIGHT_INIT_RANGE)+"</i></li>");
		sb.append("<li>Seed for Random Gen: <i>"+(long)params.getValue(SEED)+
						"</i></li></ul>");

		return sb.toString();

	}


    /**
     * Set debug
     * @param b new debug
     */
	public void setDebug(boolean b){
		debug=b;
	}

    /**
     * Get debug
     * @return debug
     */
    public boolean getDebug(){
		return debug;
	}

	//////////////////////////////////////////////////////////////////////
	///////////////////Activation Functions/////////////////////////////
	////////////////////////////////////////////////////////////////////

/**
 * This is the base class for classes that compute activation functions and
 * their derivatives when given the weighted sum (or other combination
 * function).  The class system is used for the benefit of being able to
 * easily interchange them in the Genetic Algorithm approach (or any other
 * optimizer) to parameter optimization of a neural net
*/
abstract public class NNactivation implements Serializable{

  /**
   * Constructor - never does anything
   **/
  public NNactivation(){
  }

  /**
   * computes the activation of the number its given
   * @param x number
    */
  abstract public double activationOf(double x);

  /**
   * computes the derivative function value of the number
   * @param y number
  */
  abstract public double derivativeOf(double y);

  /**
   * gives the name of the activation function
   */
  abstract public String getName();

}

/**
 * This activation is a function proposed by some guy named D.L. Elliot, the
 * paper is at ftp://ftp.isr.umd.edu/pub/TechReports/1993/TR_93-8.pdf
*/
public class ElliotAct extends NNactivation implements Serializable{
    /**
     * computes the activation of the number its given
     *
     * @param x number
     */
    public double activationOf(double x) {
        double z;
        if (x > 0) {
            z = (x / (1 + x));
        } else {
            z = (x / (1 - x));
        }
        return z;

    }

    /**
     * computes the derivative function value of the number
     *
     * @param y number
     */
    public double derivativeOf(double y) {

        double x = activationOf(y);
        double m;
        if (x > 0)
            m = 1 - x;
        else
            m = 1 + x;
        return m * m;
    }

    /**
     * gives the name of the activation function
     */
    public String getName() {
        return "Elliot's Proposed Activation";
    }
}

/**
 * Sigmoid approximation using linear functions
*/
public class FastSigmoidAct extends NNactivation implements Serializable{

    /**
     * computes the activation of the number its given
     *
     * @param y number
     */
    public double activationOf(double y) {
        double x = y / 4.1;
        if (x > 1)
            return 1;
        if (x < -1)
            return 0;
        if (x < 0)
            return (.5 + x * (1 + x / 2));
        return (.5 + x * (1 - x / 2));
    }

    /**
     * gives the name of the activation function
     */
    public String getName() {
        return "Fast Sigmoid";
    }

    /**
     * computes the derivative function value of the number
     *
     * @param y number
     */
    public double derivativeOf(double y) {
        double x = activationOf(y);
        return x * (1 - x);
    }
}

/**
 * Activation class that uses tanh(x) approximation for faster computation
*/
public class FastTanhAct extends NNactivation implements Serializable{
    /**
     * computes the activation of the number its given
     *
     * @param x number
     */
    public double activationOf(double x) {
        if (x > 1.92033)
            return .96016;
        if (x <= -1.92033)
            return -.96016;
        if (x > 0)
            return (.96016 - .26037 * (x - 1.92033) * (x - 1.92033));
        return (-.96016 + .26037 * (x + 1.92033) * (x + 1.92033));
    }

    /**
     * gives the name of the activation function
     */
    public String getName() {
        return "Fast Tanh";
    }

    /**
     * computes the derivative function value of the number
     *
     * @param y number
     */
    public double derivativeOf(double y) {

        double x = activationOf(y);
        double z = (1 - x * x);
        return z;
    }
}

/**
 * This is an activation that uses the standard sigmoid of f(x)=1/(1+e^-x)
*/
public class SigmoidAct extends NNactivation{

    /**
     * computes the activation of the number its given
     *
     * @param x number
     */
    public double activationOf(double x) {
        return (1 / (1 + Math.exp(-x)));
    }

    /**
     * computes the derivative function value of the number
     *
     * @param y number
     */
    public double derivativeOf(double y) {
        double x = activationOf(y);
        return (x * (1 - x));
    }

    /**
     * gives the name of the activation function
     */
    public String getName() {
        return "Standard Sigmoid";
    }

}

/**
 * Activation class that uses tanh(x)
*/
public class TanhAct extends NNactivation{

    /**
     * computes the activation of the number its given
     *
     * @param x number
     */
    public double activationOf(double x) {
        double z = (2 / (1 + Math.exp(-2 * x))) - 1;
        return z;
    }

    /**
     * gives the name of the activation function
     */
    public String getName() {
        return "Tanh";
    }

    /**
     * computes the derivative function value of the number
     *
     * @param y number
     */
    public double derivativeOf(double y) {

        double x = activationOf(y);
        double z = (1 - x * x);
        return z;
    }
}

/**
 * This is the abstract class for learning rate control functions (for learning
 * acceleration).  The two typical implementations will likely be those based
 * on iterations and time.
*/
abstract public class NNlearn implements Serializable{
/**
	The initial learning rate
*/
	protected final double initAlpha;

/**
	The final learning rate (at last learning iteration)
*/
	protected final double finalAlpha;

/**
	The number of epochs for learning
*/
	public final int totalEpochs;

/**
	the maximum time allowed for learning
*/
	protected double maxTime;

/**
	the range of values the learning rate can take on
*/
	protected final double rangeAlpha;

/***
	Constructor for using epochs as limit.

	@param iA initial learning rate
	@param fA final learning rate
	@param e the number of epochs
*/
	public NNlearn(double iA, double fA, int e){

		initAlpha=iA;
		finalAlpha=fA;
		totalEpochs=e;
		rangeAlpha=(fA-iA);
	}

/**
	Constructor for using time as limit.

	@param iA initial learning rate
	@param fA final learning rate
	@param timeLimit the maximum time allowed for the entire learning process
*/
	public NNlearn(double iA, double fA, double timeLimit){

		initAlpha=iA;
		finalAlpha=fA;
		maxTime=timeLimit;
		rangeAlpha=fA-iA;
		totalEpochs=0;
	}
/**
	tells the learning algorithm if it should stop iterating based on
	whether the number of epochs is reached or time is up
    @return true if learning should continue
*/
	abstract public boolean continueLearning();


/**
	tells this learning accelerator that the current epoch is finished
	and a new learning rate should be computed, returns the new learning rate
 @return new learning rate
*/
	abstract public double newLearningRate();

/**
	returns a string of info about the function
 @return name of function
*/
	abstract public String getName();
}

/******************************************************************
 * Linear
 * the learning rate changes over time in a linear fashion
 * from initAlpha at epoch one to initAlpha at the final epoch.
*/
public class Linear extends NNlearn implements Serializable{

    /** current epoch */
    private int currentEpoch=0;

    /**
     * Constructor
     * @param iA init alpha
     * @param fA final alpha
     * @param e  total epochs
     */
    public Linear(double iA, double fA, int e){
		super(iA, fA, e);
	}


    /**
     * tells the learning algorithm if it should stop iterating based on
     * whether the number of epochs is reached or time is up
     *
     * @return true if learning should continue
     */
    public boolean continueLearning() {
        if (currentEpoch == totalEpochs) {
            return false;
        }
        return true;
    }


    /**
     * tells this learning accelerator that the current epoch is finished
     * and a new learning rate should be computed, returns the new learning rate
     *
     * @return new learning rate
     */
    public double newLearningRate() {
        currentEpoch++;
        return (initAlpha + (currentEpoch / totalEpochs) * rangeAlpha);
    }


    /**
     * returns a string of info about the function
     *
     * @return name of function
     */
    public String getName() {
        return ("Linear based on epoch");
    }
}


/**
 * This is the abstract class for updating or creation functions for a NN
*/
abstract public class NNupdate implements Serializable{

    /** model */
	protected final BackPropModel model;

	/* to put calculated outputs in as the computeFn determines them
	*/
	protected double[][] computedResults;


    /**
     * Constructor
     * @param mod model
     */
    public NNupdate(BackPropModel mod){

		model=mod;
		/*
		weights=model.getWeights();
		sums=model.getSums();
		activations=model.getActivations();
		deltas=model.getDeltas();
		data=(TrainTable)model.getData();
		act=model.getActivationFunction();
		learnFn=model.getLearnFunction();
		*/

		/*
			setup results table, just needs to be right size, everything will be
			written over
		*/

		computedResults=new double[data.getNumRows()]
									[model.numOutputs];

	}

    /**
     * Create
     */
    abstract public void create();

    /**
     * returns a string of info about the function
     *
     * @return name of function
     */
    abstract public String getName();
}


/**
 * Creates a neural net. Updates the weights after every example is
 * presented to it using a regular backprop gradient descent method
 */
public class StandardIncrementalBP extends NNupdate implements Serializable{

	protected double alpha;

	public  StandardIncrementalBP(BackPropModel mod){
		super(mod);
	}


    /**
     * Create
     */
    public void create() {
        while (learnFn.continueLearning()) {
            alpha = learnFn.newLearningRate();

            for (int d = 0; d < data.getNumRows(); d++) {
                runExample(d);
            }
        }
    }

	/**
		after runExample calculates the weight change, this will add it onto
		the weight here. The algorithm is split up like this so that a batch
		method can override this and store the weight change instead of
		immediately making the change

		@param dw the weight change calculated
		@param i the 'from' node of the weight
		@param j the 'to' node
		@param l the layer of the 'to' node
	*/
	public void useWeightUpdate(double dw, int i, int j, int l){
		weights[l][j][i]+=dw;
	}

	/**
		feeds an example (input vector) through the NN and updates the
		weights based on the error
		@param g the example index in the table 'data'
		*/
	public void runExample(int g){
		int i,j,k,n,t;

		model.compute(g, computedResults);
		double d;
		double c;
		double thisDelta;

		int numInputs=model.numInputs;//data.getNumInputFeatures();
		int numLayers=weights.length;

		//compute output deltas
		final int span=model.numOutputs+1;//data.getNumOutputFeatures()+1;
		final int lastLayer=deltas.length-1;
		for (t=1; t<span; t++){//don't forget there is that -1 in index 0


			d=data.getDouble(g, model.outputFeatures[t-1]) -
				computedResults[g][t-1];

			d*=act.derivativeOf(sums[lastLayer][t]);
			deltas[lastLayer][t]=d;

			//update weights to outputs

			//if 1 or more hidden layers
			if(lastLayer>0){
				int lastHiddenLayerIndex=lastLayer-1;
										//activations.getNumColumns()-2;

				int lastLayerNodes=activations[lastHiddenLayerIndex].length;
				for(n=0; n<lastLayerNodes; n++){
					c=activations[lastHiddenLayerIndex][n];
					c*=alpha*d;
					useWeightUpdate(c, n, t, lastLayer);
				}
			}
			//if no hidden layers
			else{
				c=model.bias;
				c*=alpha*d;
				useWeightUpdate(c, 0, t, 0);

				for(n=0; n<numInputs; n++){
					c=data.getDouble(g, inputFeatures[n]);
					c*=alpha*d;
					useWeightUpdate(c, n+1, t, 0);
				}
			}
		}

		//update all other weights except those from inputs
		int nodesInLayer=0;
		int nodesInNextLayer=0;
		int nodesInPrevLayer=0;

		for (int layer=numLayers-2; layer>0; layer--){
			//n in this node
			nodesInLayer=weights[layer].length;;
			for(n=1; n<nodesInLayer; n++){
				thisDelta=0;
				//i is next node
				nodesInNextLayer=weights[layer+1].length;
				for(i=1; i<nodesInNextLayer; i++){
					thisDelta+= weights[layer+1][i][n]*deltas[layer+1][i];

				}
				thisDelta *= act.derivativeOf(sums[layer][n]);
				deltas[layer][n]=thisDelta;

				//k is previous node
				nodesInPrevLayer=weights[layer-1].length;
				for(k=0; k<nodesInPrevLayer;k++){
					d= alpha*activations[layer-1][k]*thisDelta;
					useWeightUpdate(d, k, n, layer);

				}
			}
		}

		//update weights inputs->firstlayer (if not already done in
		//outputs section)

		if(numLayers>1){
			//j is the 'to' node (this node)
			nodesInLayer=weights[0].length;
			nodesInNextLayer=weights[1].length;
			for (j=1; j<nodesInLayer; j++){
				thisDelta=0;
				//k is the next node
				for(k=1; k<nodesInNextLayer; k++){
					thisDelta+=weights[1][k][j]*deltas[1][k];
				}
				thisDelta*=act.derivativeOf(sums[0][j]);
				//??may not need to bother executing this line
				deltas[0][j]=thisDelta;

				d=alpha*model.bias*thisDelta;
				useWeightUpdate(d, 0, j, 0);

				//i is the input data
				for(i=0; i<numInputs; i++){
					d=alpha*
						data.getDouble(g,
								inputFeatures[i])*thisDelta;

					//there is no 'filler' bias node among the inputs(above)
					//so the indices are off by one for getting the data
					//and the corresponding weight, as seen in the 'i+1' below
					useWeightUpdate(d, i+1, j, 0);
				}
			}
		}
	}

    /**
     * returns a string of info about the function
     *
     * @return name of function
     */
    public String getName() {
        return ("Standard Incremental Back Prop");
    }
}
/**
 * Creates a neural net. calculates all weight updates for an iteration,
 * then updates the weights
 **/
public class StandardBatchBP extends StandardIncrementalBP{

    /**
     * running updates
     */
    private double[][][] runningUpdates;

    /**
     * Constructor
     * @param mod model
     */
    public  StandardBatchBP(BackPropModel mod){
		super(mod);
		runningUpdates=new double[weights.length][][];
		for(int k=0;k<weights.length;k++){
			runningUpdates[k]=new double[weights[k].length][];
			for(int h=0; h<weights[k].length;h++){
				runningUpdates[k][h]=new double[weights[k][h].length];
			}
		}
		wipeUpdates();
	}

    /**
     * Create
     */
    public void create(){
		while(learnFn.continueLearning()){
			alpha=learnFn.newLearningRate();

			for(int d=0; d<data.getNumTrainExamples(); d++){
				runExample(d);
			}
			batchUpdate();
			wipeUpdates();
		}
	}
	/**
		after runExample calculates the weight change, this will add it onto
		the runningTally here. The algorithm is split up like this so that a
		batch method can override the superclasses' and store the weight change
		instead of immediately making the change


		@param dw the weight change calculated
		@param i the 'from' node of the weight
		@param j the 'to' node
		@param l the layer of the 'to' node

		*/
	public void UseWeightUpdate(double dw, int i, int j, int l){
		runningUpdates[l][j][i]+=dw;
	}

	/**
		set all the running weight tallies to zero
	*/
	public void wipeUpdates(){
		int i,j,k;
		int l1,l2,l3;
		l1=runningUpdates.length;
		for(i=0;i<l1;i++){
			l2=runningUpdates[i].length;
			for(j=0;j<l2;j++){
				l3=runningUpdates[i][j].length;
				for(k=0;k<l3;k++){
					runningUpdates[i][j][k]=0;
				}
			}
		}
	}

	/**
		updates the weights in the model with the weight changes in the
		running tally.
	*/
	public void batchUpdate(){
		int i,j,k,l1,l2,l3;
		l1=runningUpdates.length;
		for(i=0;i<l1;i++){
			l2=runningUpdates[i].length;
			for(j=0;j<l2;j++){
				l3=runningUpdates[i][j].length;
				for(k=0;k<l3;k++){
					weights[i][j][k]+=runningUpdates[i][j][k];
				}
			}
		}
	}

    /**
     * returns a string of info about the function
     *
     * @return name of function
     */
    public String getName() {
        return ("Standard Batch Back Prop");
    }
}
}

