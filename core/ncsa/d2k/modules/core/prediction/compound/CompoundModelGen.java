package ncsa.d2k.modules.core.prediction.compound;

import ncsa.d2k.infrastructure.modules.ModelGeneratorModule;
import ncsa.d2k.infrastructure.modules.HasNames;
import ncsa.d2k.infrastructure.modules.ModelModule;
import ncsa.d2k.infrastructure.modules.HasNames;
import ncsa.d2k.modules.PredictionModelModule;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**

	Takes in several models w/ scores (errors)
	and makes a model that uses the best
	model it sees for each output. the models
	are all expect to predict only one output

	@author pgroves
	*/

public class CompoundModelGen extends ModelGeneratorModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug=true;

	/* The number of models/scores to
	wait for before deciding which is best*/
	protected int inputModelCount=3;

	/*if true, a different type of model
	may be used to predict each output, and
	the selection of which model for a particular
	output will be based on the errors for that
	output only.  if the models must all be the
	same, the model types will be ranked for each
	output accuracy, and the best average rank
	will be the selection criteria
	*/
	protected boolean allowDifferentModelTypes=false;

	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	protected boolean makeModelAvailable=true;

	/////////////////////////
	/// other fields
	////////////////////////

	/*the overall model to be pushed/saved*/
	protected CompoundModel model;

	/*how man of the models/scores we've gotten so far*/
	protected int numModelsIn=0;

	/*the models/scores objects*/
	protected ModelScore[] scoreObjs;



	//////////////////////////
	///d2k control methods
	///////////////////////

	/** isReady

		if this is the first run, wait for both the
		exampetable and the first modelscore, otherwise
		just wait for the modelscore
	*/
	public boolean isReady(){
		//the first run, wait for
		//the et and the first modelscore
		/*if(debug){
			System.out.println("Compound: isReady()");
			System.out.println("     inputFlag[0]:"+inputFlags[0]);
			System.out.println("     inputFlag[1]:"+inputFlags[1]);

		}*/
		if(numModelsIn==0){

			return super.isReady();
		}
		//just waiting for the modelscores
		else{//if(numModelsIn>0){
			return (inputFlags[1]>0);
		}
	}
	public void endExecution(){
		wipeFields();
		return;
	}
	public void beginExecution(){
		wipeFields();
		return;
	}

	/** wipeFields

		clears the score objects and resets
		the counter.  Also clears the model
		if we aren't going to save it for
		d2k to pull out
	*/
	protected void wipeFields(){
		numModelsIn=0;
		scoreObjs=null;
		if(!makeModelAvailable)
			model=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(numModelsIn==0){
			scoreObjs=new ModelScore[inputModelCount];

		}
		scoreObjs[numModelsIn]=(ModelScore)pullInput(1);
		numModelsIn++;

		if(debug)
			System.out.println("Compound: modelsIn="+numModelsIn);

		if(numModelsIn==inputModelCount){
			//this should have been sitting in
			//the pipe since we started
			ExampleTable et=(ExampleTable)pullInput(0);
			model=new CompoundModel(et, scoreObjs,
									allowDifferentModelTypes);
			if(debug){
				System.out.println("Compound ModelGen Finished");
			}
			pushOutput(model, 0);
			wipeFields();
		}

	}

	public ModelModule getModel() {
    	return model;
  	}

	//////////////////////////////////////////////////////////////
	//// The model
	//////////////////////////////////////////////////////////////

	public class CompoundModel extends PredictionModelModule
					implements java.io.Serializable, HasNames
	{

		////////////////////
		//model's fields
		////////////////////
		String[] outputNames;

		PredictionModelModule[] models;

		String[] modelNames;



		/////////////////////////
		//model's methods
		////////////////////////

		/*
			Constructor

		*/
		public CompoundModel(ExampleTable et,
							ModelScore[] scores,
							boolean differentModels){


			outputNames=new String[et.getNumOutputFeatures()];
			for(int i=0; i<outputNames.length;i++){
				outputNames[i]=et.getColumnLabel(et.getOutputFeatures()[i]);
			}

			modelNames=new String[et.getNumOutputFeatures()];

			//this will hold the indices of which model
			//is best for each output. the index indicates
			//which output, the value is which model, w/
			//order determined by the order in the ModelScore array
			int[] bestModels;

			if(differentModels){
				bestModels=findBestDifferent(scores, et);
			}else{
				bestModels=findBestSame(scores, et);
			}

			models=new PredictionModelModule[et.getNumOutputFeatures()];

			for(int i=0; i<bestModels.length; i++){
				models[i]=scores[bestModels[i]].models[i];
				modelNames[i]=scores[bestModels[i]].modelName;
				if(debug){
					System.out.println(modelNames[i]+" ");
				}
			}

			setName("Compound");
		}


		/** find the indices of the best models, allows
		   different outputs to be predicted by different
		   types of models
		 */
		private int[] findBestDifferent(ModelScore[] scores,
									 ExampleTable et){

			int[] bests=new int[et.getNumOutputFeatures()];

			//for every output
			for(int oi=0; oi<bests.length; oi++){
				bests[oi]=0;
				//for every model/modelscore
				if(debug)
					System.out.println(outputNames[oi]);
				for(int m=0; m<scores.length; m++){
					if(debug){
						System.out.println("   "+scores[m].modelName+
											":"+scores[m].errors[oi]);
					}
					if(scores[m].errors[oi]<
						scores[bests[oi]].errors[oi]){

						bests[oi]=m;
					}
				}

			}
			return bests;
		}

		/**find the indices of the best models, based
			on best average
		*/
		private int[] findBestSame(ModelScore[] scores,
									 ExampleTable et)
			{
			/*int outputCount=et.getNumOutputFeatures();
			int modelCount=scores.length;

			int[] bests=new int[outputCount];

			//ranks[modelIndex][outputIndex]
			double[][] ranks=new int[modelCount][outputCount];

			double[] aveRanks=new int[modelCount];

			//for every output, rank the model types
			for(int oi=0; oi<outputCount; oi++){
				//assign random order
				for(int m=0; m<modelCount;m++){
					ranks[m][oi]=scores[m].errors[oi];
				}
				//bubbleSort based on error
				for(int m=0; m<modelCount; m++){
					for(int n=m+1; n<modelCount; n++){
						if(
			*/
			int outputCount=et.getNumOutputFeatures();
			int modelCount=scores.length;

			int[] bests=new int[outputCount];

			double[] averageErrors=new double[modelCount];
			if(debug)
				System.out.println("Average Errors:");

			for(int m=0; m<modelCount; m++){
				averageErrors[m]=0;
				for(int oi=0; oi<outputCount; oi++){
					averageErrors[m]+=scores[m].errors[oi];
				}
				averageErrors[m]/=outputCount;
				if(debug){
						System.out.println("   "+scores[m].modelName+
											":"+averageErrors[m]);
				}
			}
			int best=0;
			for(int m=0; m<modelCount; m++){
				if(averageErrors[m]<averageErrors[best]){
					best=m;
				}
			}
			for(int oi=0; oi<outputCount; oi++){
				bests[oi]=best;
			}
			return bests;
					}




			/**********************************
			PREDICT
			*******************************/


		public PredictionTable predict(ExampleTable et){

			PredictionTable predTable;
			if(et instanceof PredictionTable){
				predTable=(PredictionTable)et;
			}else{
				predTable= (PredictionTable)et.toPredictionTable();
			}

			//if there are no spots for pred columns
			if(predTable.getNumOutputFeatures()==0){
				for(int i=0; i<outputNames.length; i++){
					double[] dc = new double[et.getNumRows()];
					predTable.addPredictionColumn(dc ,outputNames[i]);
				}
			}

			//for every output, we get rid of the output
			//features (make length=0), its corresponding
			//model will predict the correct column
			int[] holdOutputFeatures=predTable.getOutputFeatures();
			int[] holdPredictionSet=predTable.getPredictionSet();

			predTable.setOutputFeatures(new int[1]);
			predTable.setPredictionSet(new int[1]);
			for(int i=0; i<models.length;i++){
				predTable.getPredictionSet()[0]=
					holdPredictionSet[i];
				predTable=(PredictionTable)models[i].predict(predTable);
			}
			predTable.setOutputFeatures(holdOutputFeatures);
			predTable.setPredictionSet(holdPredictionSet);
			return predTable;
		}


		/* just calls predict on the pulled in table*/
 	 	 public void doit() {
			 pushOutput(predict((ExampleTable)pullInput(0)),0);

	  	 }

		 public String getModelName(int i){
			 return modelNames[i];
		 }
		////////////////////////////
		///model's d2k info methods
		///////////////////////////
		public String getModuleInfo(){
			String s="";
			for(int i=0; i<modelNames.length; i++){
				s+="<li>"+outputNames[i]+" : "+modelNames[i];
			}
			return "This model uses different models to predict each"+
					" output.  They are:<ul>"+s+"</ul>" ;
		}


	   	public String getModuleName() {
			return "Compound Model";
		}
		public String[] getInputTypes(){
			String[] s= {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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
			String[] s={"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
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
		return
					"Makes a modelmodule that uses the best of the input"+
					"models to predict any particular feature.  For instance,"+
					" if regression predicts feature A better than a decision"+
					" tree, but the tree beats the regression for feature B,"+
					" a compound model will be made that will always predict"+
					"feature A w/ the regression model and B w/ the decision "+
					"tree. The resulting compound model will behave the same"+
					"as any other prediction model module, but it will not"+
					"be apparent what modeling algorithm is generating any"+
					"particular set of predictions.<br><b>Properties</b>:"+
					"<ul><li>AllowDifferentModelTypes: if false, all of the"+
					"models will be of the same type, with the winner being"+
					"determined by the lowest average error over all outputs."+
					"  Therefore, the error metrics should all be on the same"+
					" scale. Otherwise, the models will be compared for"+
					" every output, and the lowest error for that output will"+
					" win.<li>InputModelCount: Basically the number of Model"+
					"Score objects to expect for determining each compound mo"+
					"del -or- the number of different models being considered"+
					".<li>MakeModelAvailable: whether to let d2k save the last"+
					" model created in the execution of the itinerary</ul>";
	}
   	public String getModuleName() {
		return "Compound Model Generator";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
					"ncsa.d2k.modules.core.prediction.compound.ModelScore" };
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "This is only used to get Features' names";
			}
			case(1): {
				return "The ModelScore Objects";
			}

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
			case(1): {
				return "Score Object";
			}

			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.PredictionModelModule"

					/*"modules.compute.learning.modelgen."+
					"compound.CompoundModelGen$CompoundModel"*/};
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
	public int getInputModelCount(){
		return inputModelCount;
	}
	public void setInputModelCount(int i){
		inputModelCount=i;
	}
	public boolean getAllowDifferentModelTypes(){
		return allowDifferentModelTypes;
	}
	public void setAllowDifferentModelTypes(boolean b){
		allowDifferentModelTypes=b;
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







