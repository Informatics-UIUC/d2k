package ncsa.d2k.modules.core.prediction.neuralnet;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;

/*
	Defines a single objective search space
	for an optimizer trying to minimize the
	error metric, whatever it may be.

	@author pgroves
	*/

public class NNSpaceDefine extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////

	double convergenceTarget=.01;

	/////////////////////////
	/// other fields
	////////////////////////


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

		pushOutput(getSolutionSpace(), 0);

	}

	public static Range[] getRanges(){
		return NNModelGenerator.getRanges();
	}

	public SOSolutionSpace getSolutionSpace(){
		Range[] ranges=this.getRanges();
		ObjectiveConstraints ocs=this.getObjectiveConstraints();

		return new SOSolutionSpace(ranges, ocs, convergenceTarget);

	}

	public static ObjectiveConstraints getObjectiveConstraints(){

		return ObjectiveConstraintsFactory.
					getObjectiveConstraints( "Error", 0, 1);
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {		};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOSolutionSpace"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public double  getConvergenceTarget(){
		return convergenceTarget;
	}
	public void setConvergenceTarget(double d){
		convergenceTarget=d;
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







