
package ncsa.d2k.modules.core.control;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;
/**
	MultiPusher.java

	pushes the input object out N times, where N is a property
	or input

	@author Peter Groves
	7/15/01
*/
public class MultiPusher extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable
{

	protected boolean debug=false;


	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"objIn\">    <Text>The object to be passed multiple times </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"N\">    <Text>The number of times to pass it</Text>  </Info></D2K>";

			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.lang.Object", "java.lang.Integer"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"objOut\">    <Text>One of multiple pushes of the object </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"java.lang.Object"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"ObjectPusher\">    <Text>Takes an Object, pushes it out N times.PROPS: N-the number of pushes, usePropNValue - false- will wait for and use the Integer in input 1; true - will use the N in properties </Text>  </Info></D2K>";

	}
	/* the number of times to push the object*/
	int N=4;
	public void setN(int i){
		N=i;
	}
	public int getN(){
		return N;
	}

	/*false- will wait for and use the Integer in input 1
		true - will use the N in properties*/
	private boolean usePropNValue=false;
	public void setUsePropNValue(boolean b){
		usePropNValue=b;
	}
	public boolean getUsePropNValue(){
		return usePropNValue;
	}

	int numFires=0;

	public void beginExecution(){
		numFires=0;
		totalFires=0;
		super.beginExecution();
	}

	/*
		checks to see if the object is in and  if it should wait for input (1)
		(the Integer N), otherwise returns the superclass's
		isReady
		*/
	public boolean isReady(){
		if(	numFires==0){//this is the 'first' run
			if( (inputFlags[0]>0)&&//we have the object to push
				((usePropNValue)||(inputFlags[1]>0))){//we have the number of times
				return true;
			}else{
				return false;
			}
		}
		if((0<numFires)&&(numFires<N)){
			return true;
		}

		return false;
	}

	Object obj;
	int totalFires=0;
	/**
		does it every time
	*/
	public void doit() throws Exception {
		if(numFires==0){
			obj= pullInput(0);
			if(!usePropNValue){
				Integer I=(Integer)pullInput(1);
				N=I.intValue();
			}
		}


		pushOutput(obj, 0);
		numFires++;

		totalFires++;
		if(debug)
		System.out.println("MultiPush: numFires:"+numFires+" total:"+totalFires);
		if(numFires==N){
			obj=null;
			numFires=0;
		}
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}

}

