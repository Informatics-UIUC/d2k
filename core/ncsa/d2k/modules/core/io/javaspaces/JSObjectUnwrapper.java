package ncsa.d2k.modules.core.io.javaspaces;

//==============
// Java Imports
//==============

import java.rmi.MarshalledObject;

//===============
// Other Imports
//===============

import net.jini.core.entry.*;
import ncsa.d2k.infrastructure.modules.*;




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
    if (parm1 == 0){
      return "net.jini.core.entry.Entry";
    } else {
      return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {"net.jini.core.entry.Entry"};
    return in;
  }

  public String getOutputInfo(int parm1) {
    if (parm1 == 0){
      return "java.lang.Object";
    } else {
      return "";
    }
  }

  public String[] getOutputTypes() {
    String[] out = {"java.lang.Object"};
    return out;
  }

  public String getModuleInfo() {
    return "This module will write an entry out.";
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

}