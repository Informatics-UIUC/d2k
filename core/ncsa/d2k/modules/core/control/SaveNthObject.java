package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;
import java.util.Vector;

/*
	@author pgroves
	*/

public class SaveNthObject extends ComputeModule 
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=false;	
	
	/////////////////////////
	/// other fields
	////////////////////////

	private Vector savedObjs;
	protected int numfires;
	protected int maxfires;

	boolean lastPushed;
	
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(inputFlags[0]>0){
			return true;
		}
		if(lastPushed&&(inputFlags[1]>0)){
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
		maxfires=Integer.MAX_VALUE;
		lastPushed=true;
		savedObjs=new Vector();
		//numfires=0;
		//savedObj=null;
	}
		
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(inputFlags[0]>0){
			savedObjs.add(pullInput(0));
				numfires++;
			if(debug)
				System.out.println(getAlias()+" numfires:"+numfires);
	
		}
		if(lastPushed&&(inputFlags[1]>0)){
			maxfires=((Integer)pullInput(1)).intValue();
			lastPushed=false;
			if(debug)
				System.out.println(getAlias()+" maxfires set to "+maxfires);
		}
		
		if(numfires>=maxfires){
			if(debug)
				System.out.println(getAlias()+" numfires>=maxfires");
			numfires-=maxfires;
			if(savedObjs.size()!=0){
				if(debug)
					System.out.println(getAlias()+" Pushing");
				pushOutput(savedObjs.get(maxfires-1), 0);
				Vector t=savedObjs;
				
				reset();//this will re-init savedObjs
				
				//put the leftovers in the new vector
				for(int i=maxfires-1; i<t.size();i++){
					savedObjs.add(t.get(i));
				}
			}else
				return;
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Will save objects that come in into a buffer. Will then push"+
			" out the Nth one where N is the other input. Because of the buffer"
			+", N can be pulled in after N+K (K>0) objects have come in";

	}
	
   	public String getModuleName() {
		return "Buffered Sequencer";
	}
	public String[] getInputTypes(){
		String[] s= {"java.lang.Object","java.lang.Integer"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The objects, of which the Nth one is wanted to be "+
				"pushed.";
			}case(1): {
				return "The Nth object will be pushed, all others will be "+
				"ignored";
			}


			default:{
				return "No such input.";
			}
		}
	}
	
	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Objects";
			}
			case(1):{return "Object Count (N)";}
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
				return "The Nth object pulled in";
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
			
					

			

								
	
