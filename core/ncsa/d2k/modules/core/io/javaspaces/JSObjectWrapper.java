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



public class JSObjectWrapper  extends DataPrepModule {


  //==============
  // Data Members
  //==============

  //properties
  private boolean m_verbose = false;
  private boolean m_debug = false;

  //================
  // Constructor(s)
  //================

  public JSObjectWrapper() {
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

  public String getInputInfo(int parm1) {
    if (parm1 == 0){
      return "java.lang.Object";
    } else {
      return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {"java.lang.Object"};
    return in;
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

      Object obj = this.pullInput(0);
      MarshalledObject mobj = new MarshalledObject(obj);
      JSJob job = new JSJob();
      job.setPayload(mobj);
      if (m_debug){
        System.out.println("JSObjectWrapper: JSJob written: " + job);
      }
       this.pushOutput(job, 0);

     } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("Aborting: JSObjectWrapper.doit()");
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