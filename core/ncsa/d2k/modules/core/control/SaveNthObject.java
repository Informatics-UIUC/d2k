package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;

/*
	@author pgroves
	*/

public class SaveNthObject extends ComputeModule 
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	
	
	/////////////////////////
	/// other fields
	////////////////////////

	private Object savedObj;
	protected int numfires;
	protected int maxfires;
	
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(inputFlags[0]>0){
			return true;
		}
		if(inputFlags[1]>0){
			return true;
		}
		return false;
	}

	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		reset();
		super.beginExecution();
	}
	
	private void reset(){
		//just so it doesn't think it's done before it even starts
		maxfires=-2;

		numfires=0;
		savedObj=null;
	}
		
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(inputFlags[0]>0){
			savedObj=pullInput(0);
			numfires++;
			System.out.println("numfires:"+numfires);

		}
		if(inputFlags[1]>0){
			maxfires=((Integer)pullInput(1)).intValue();
			System.out.println("maxfires set to "+maxfires);
			return;
		}
		
		if(numfires==maxfires){
			if(savedObj!=null){
				pushOutput(savedObj, 0);
				reset();
			}else
				return;
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] s= {"java.lang.Object","java.lang.Integer"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}case(1): {
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
				return "Saved Object";
			}
			case(1):{return "Object Count";}
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
				return "The Nth object";
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
			
					

			

								
	
