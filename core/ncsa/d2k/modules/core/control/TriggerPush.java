package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;

/*
	takes in an object once, saves it, and then
	pushes it out every time an object comes into
	the other input

	@author pgroves
	*/

public class TriggerPush extends DataPrepModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug;

	/////////////////////////
	/// other fields
	////////////////////////
	protected Object theObject;

	protected boolean waitingForObject=true;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(waitingForObject)
			return super.isReady();
		else
			return(inputFlags[1]>0);

	}
	public void endExecution(){
		super.endExecution ();
		waitingForObject=true;
		return;
	}
	public void beginExecution(){
		waitingForObject=true;
		totalFires=0;
		return;
	}
	int totalFires=0;
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(waitingForObject){
			theObject=pullInput(0);
			waitingForObject=false;

		}
		totalFires++;
		pullInput(1);
		if (debug)
			System.out.println("TriggerPush:"+totalFires);
		pushOutput(theObject, 0);


	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Takes in an object and saves it. Every time an object"+
				"enters the <i>other</i> input pipe, the saved object"+
				"will be pushed out the single output";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] s= {"java.lang.Object","java.lang.Object"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The object to be saved and passed";
			}
			case(1): {
				return "The triggering object";
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
			case(1): {
				return "Trigger";
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
				return "The saved object pushed out every time there"+
						"is a trigger object";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "The Saved Object";
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







