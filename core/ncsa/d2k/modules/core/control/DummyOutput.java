package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;

/*
	Has an output pipe that never is filled. 
	Used to clean up unused inputs in nested itins.
	@author pgroves
	*/

public class DummyOutput extends ComputeModule 
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	
	
	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return false;
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "	Has an output pipe that never is filled. "+
		"Used to clean up unused inputs in nested itins.";
	}
	
   	public String getModuleName() {
		return "Nested Input Cleanup";
	}
	public String[] getInputTypes(){
		String[] s= {};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such input.";
			}
		}
	}
	
	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"java.lang.Object"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "This pipe will never be filled";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Dummy Pipe";
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
			
					

			

								
	
