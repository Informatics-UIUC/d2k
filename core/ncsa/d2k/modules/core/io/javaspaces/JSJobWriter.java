package ncsa.d2k.modules.core.io.javaspaces;

import net.jini.core.entry.*;
import net.jini.lookup.entry.Name;

import ncsa.d2k.infrastructure.modules.*;



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
    if (parm1 == 0){
      return "net.jini.core.entry.Entry";
    } else {
      return "";
    }
  }

  public String[] getOutputTypes() {
    String[] in = {"net.jini.core.entry.Entry"};
    return in;
  }

  public String getModuleInfo() {
    return "This module will write an entry out.";
  }

  public String getInputInfo(int parm1) {
    return "";
  }

  public String[] getInputTypes() {
    String[] out = null;
    return out;
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

}