package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.DataPrepModule;

/**
 * User: redman
 * Date: Aug 28, 2003
 * Time: 11:14:33 AM
 * This module will push the first input it receives on input 0, but will not push subsequent inputs on 0
 * until it receives an input on one.
 */
public class WaitPush extends DataPrepModule{

	protected boolean debug;
	protected boolean firstTime = true;

	/**
	 * We are ready to run if we have received our first input on port zero or we have inputs on
	 * zero and one.
	 * @return true if we are ready to fire.
	 */
	public boolean isReady() {
		if (firstTime && inputFlags[0] > 0) {
			return true;
		} else {
			if (inputFlags[0] > 0 && inputFlags[1] > 0)
				return true;
		}
		return false;
	}

	/**
	 * set the first execution flag.
	 */
	public void beginExecution () {
		firstTime = true;
	}

	/**
	 * If first time, just pull input 0 and push it, subsequent runs, pull input 1, push it
	 * and pull an input off the second input port, discard it.
	 * @throws Exception
	 */
	public void doit() throws Exception {
		Object obj = this.pullInput (0);
		this.pushOutput(obj, 0);
		if (firstTime)
			firstTime = false;
		else
			this.pullInput(1);
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body> Pull the first input received on input zero, and push it, after that, pull inputs off zero and one. This requires two inputs to trigger after the first time.</body></html>";
	}

	public String getModuleName() {
		return "Wait Push";
	}

	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The object to push.";
			case 1: return "The triggering object";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object";
			case 1:
				return "Trigger";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The object pushed initially or after input on 1 is aquired.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Object";
			default: return "NO SUCH OUTPUT!";
		}
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
}