package ncsa.d2k.modules.core.io.javaspaces;


import net.jini.core.entry.*;
import net.jini.lookup.entry.Name;
import ncsa.d2k.core.modules.*;



public class JSJobWriter extends DataPrepModule {


  //==============
  // Data Members
  //==============

  //properties
  private boolean m_verbose = false;
  private boolean m_debug = false;

  //================
  // Constructor(s)
  //================

  public JSJobWriter() {
  }

  //================
  // Public Methods
  //================


  //========================
  // D2K Abstract Overrides

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "net.jini.core.entry.Entry";
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {"net.jini.core.entry.Entry"};
		return types;
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module will write an entry out.  </body></html>";
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public void beginExecution(){
  }

  public void endExecution(){
    super.endExecution ();
  }


  protected void doit() throws java.lang.Exception {
    try {

      JSJob entry = new JSJob();
      this.pushOutput(entry, 0);

     } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("Aborting: JSJobWriter.doit()");
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
		return "JSJobWriter";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
