package ncsa.d2k.modules.core.prediction.compound;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.PredictionModelModule;

/**
	This is used by a CompoundModelGenerator
	to assign a score (error value from cross validation)
	to a model. It takes one score and one model for
	every output and compiles them into a single object

	@author Peter Groves
	*/

public class AssignModelScore extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////

	/*The model type of the modelgenerator this
	module is assigned to*/
	String modelName="Model";

	protected boolean debug=false;

	/////////////////////////
	/// other fields
	////////////////////////

	/*the number of models to expect, equal to the
	number of output features the original data set had
	*/
	int modelCount;

	/*the number of models that have been pulled in*/
	int numRuns=0;

	//holds one error value (average from
	//cross validation) per output feature
	double[] errs;

	/*the models, one for each output feature*/
	PredictionModelModule[] models;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(numRuns==0){
			return super.isReady();
		}else{
			return ((inputFlags[1]>0)&&
					(inputFlags[0]>0));
		}

	}
	public void beginExecution(){
		wipeFields();
		return;
	}

	/** wipeFields

		clears the number of models in field,
		the models, and the errors
	*/

	private void wipeFields(){
		numRuns=0;
		errs=null;
		models=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(numRuns==0){
			modelCount=((Integer)pullInput(2)).intValue();
			models=new PredictionModelModule[modelCount];
			errs=new double[modelCount];
		}
		if(debug){
			System.out.println("AssignScore: PullingInput:"+numRuns);
		}

		errs[numRuns]=((Double)pullInput(0)).doubleValue();
		models[numRuns]=(PredictionModelModule)pullInput(1);
		numRuns++;
		if(numRuns==modelCount){
			if(debug){
				System.out.println("AssignScore: PushingOutput");
			}
			pushOutput(new ModelScore(models, errs, modelName), 0);
			wipeFields();
		}

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Used with CompoundModelGen and OutputAtATime. Takes the output count from     OutputAtATime and waits for that many models and error values from     crossvalidation. Puts them in a single object to be given to     CompoundModelGen so that it can find the best model for each output.<br><b>    Properties</b>    <ul>      <li>        <b>ModelName</b>: A inique name for the type of model being scored      </li>    </ul>  </body></html>";
	}

   	public String getModuleName() {
		return "Assign Models to Errors";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Double","ncsa.d2k.core.modules.PredictionModelModule","java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The error value or score to be associated with the incoming model";
			case 1: return "The model to be scored";
			case 2: return "The number of models to wait for. Output from OutputAtATime";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Error";
			case 1:
				return "Model";
			case 2:
				return "Model Count";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.prediction.compound.ModelScore"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The object to pass to CompoundModelGen. Contains a model for every output feature and a score for every model.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Model Score Object";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public String getModelName(){
		return modelName;
	}
	public void setModelName(String s){
		modelName=s;
	}
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
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







