package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;

/*
	Takes in an object, does nothing with it.
	Used to clean up unused outputs in nested
	itins
	
	@author pgroves
	*/

public class ObjectBlocker extends ComputeModule 
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
		return super.isReady();
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
		pullInput(0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Takes in an object, does nothing with it."+
				"Used to clean up unused outputs in nested itins ";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] s= {"java.lang.Object"};
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
				return "Unwanted Object";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "";
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
			
					

			

								
	
