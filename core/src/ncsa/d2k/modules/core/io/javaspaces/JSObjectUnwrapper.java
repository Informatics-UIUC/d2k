package ncsa.d2k.modules.core.io.javaspaces;

//==============
// Java Imports
//==============


import java.rmi.MarshalledObject;
import net.jini.core.entry.*;
import ncsa.d2k.core.modules.*;




public class JSObjectUnwrapper  extends DataPrepModule  {


  //==============
  // Data Members
  //==============

  //properties
  private boolean m_verbose = false;
  private boolean m_debug = false;

  //================
  // Constructor(s)
  //================

  public JSObjectUnwrapper() {
  }

  //================
  // Public Methods
  //================


  //========================
  // D2K Abstract Overrides

  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "net.jini.core.entry.Entry";
			default: return "No such input";
		}
	}

  public String[] getInputTypes() {
		String[] types = {"net.jini.core.entry.Entry"};
		return types;
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "java.lang.Object";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module will write an entry out.  </body></html>";
	}


  public void beginExecution(){
  }

  public void endExecution(){
    super.endExecution ();
  }

  public boolean isReady(){
    return super.isReady();
  }


  protected void doit() throws java.lang.Exception {
    try {

      JSJob job = (JSJob)this.pullInput(0);
      MarshalledObject mobj = job.getPayload();
      Object obj = mobj.get();
      this.pushOutput(obj, 0);

     } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("Aborting: JSObjectUnwrapper.doit()");
      throw ex;
    }
  }

  //==================
  // Option Accessors

  public boolean getVerbose(){
    return m_verbose;
  }

  public void setVerbose(boolean b){
    m_verbose = b;
  }

  public boolean getDebug(){
    return m_debug;
  }

  public void setDebug(boolean b){
    m_debug = b;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "JSObjectUnwrapper";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
