package ncsa.d2k.modules.core.optimize.util.reconstruct;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.util.MOSolution;
/*
	Assigns Multiple objective values to a solution

	@author pgroves
	*/

public class AssignMO extends DataPrepModule
	implements HasNames{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

	double[] objs=(double[])pullInput(0);
	MOSolution sol=(MOSolution)pullInput(1);

	for(int i=0; i<objs.length; i++){
		sol.setObjective(i, objs[i]);
	}

	pushOutput(sol, 0);

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Assigns an array of objective values (the double) to a solution object";
	}

   	public String getModuleName() {
		return "Assign Single Objective";
	}
	public String[] getInputTypes(){
		String[] s= {"[D",
					"ncsa.d2k.modules.core.optimize.util.MOSolution"
					};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The objective values";
			}
			case(1): {
				return "The solution the assign the objectives to";
			}

			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Objectives";
			}
			case(1): {
				return "Solution";
			}

			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.optimize.util.MOSolution"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "The solution with the objectives now assigned";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Evaluated Solution";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////

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







