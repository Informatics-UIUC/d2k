package ncsa.d2k.modules.core.io.javaspaces;

/**
 * Title: SpaceReader
 * Description:  Reads an entry from a JavaSpace
 * Copyright:    Copyright (c) 2001
 * Company:      NCSA
 * @author D. Searsmith
 * @version 1.0
 */

 //===============
// Other Imports
//===============

import net.jini.space.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.*;
import net.jini.discovery.*;
import net.jini.core.entry.*;
import net.jini.lookup.entry.Name;
import net.jini.core.discovery.*;

import ncsa.d2k.infrastructure.modules.*;



public class JSTriggerReader extends InputModule {


  //==============
  // Data Members
  //==============

  //properties
  private boolean m_verbose = false;
  private boolean m_take = true;
  private String m_jiniURL = "jini://hyperion.ncsa.uiuc.edu:4160";
  private String m_spaceName = "JavaSpace";
  protected boolean m_debug = false;


  protected JavaSpace m_space = null;
  private String m_policyFile = "/home/dsears/dump/jini/jini1_1/policy/policy.all";

  private boolean m_inited = false;
  private boolean m_reading = false;

  private Entry m_readOnce = null;

  private String m_jobName = "test";

  //================
  // Constructor(s)
  //================

  public JSTriggerReader() {
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
    String[] out = {"net.jini.core.entry.Entry"};
    return out;
  }

  public String getModuleInfo() {
    return "This module will read an entry from a JavaSpace";
  }

  public String getInputInfo(int parm1) {
    if (parm1 == 0){
      return "net.jini.core.entry.Entry";
    } else if (parm1 == 1){
      return "java.lang.Object";
    } else {
      return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {"net.jini.core.entry.Entry","java.lang.Object"};
    return in;
  }

  public void beginExecution(){
    if (!m_inited){
      //some of the lookup codes use this property although we don't currently it
      //may still be prudent to set this
      System.getProperties().setProperty("com.sun.jini.lookup.locator", getJiniURL());

      System.getProperties().setProperty("java.security.policy", m_policyFile);
      if (System.getSecurityManager() == null){
        System.setSecurityManager(new java.rmi.RMISecurityManager());
      }
      m_inited = true;
    }
    m_readOnce = null;
    m_reading = false;
  }

  public void endExecution(){
    super.endExecution ();
    m_readOnce = null;
  }

  public boolean isReady(){
    /**
     * The semantics are as follows.  At first, input 0 determines ready status.
     * Once a single input 0 has been received, input 1 thereafter solely controls
     * the ready status.
     */
    if (m_readOnce == null) {
      if (inputFlags[0] > 0) {
        return true;
      } else {
        return false;
      }
    } else if ((inputFlags[1] > 0) || m_reading) {
      return true;
    } else {
      return false;
    }
  }


  protected void doit() throws java.lang.Exception {
    try {

      if (m_readOnce == null){
        m_readOnce = (Entry)this.pullInput(0);
        ((JSJob)m_readOnce).setName(m_jobName);
      } else {
        //clear the trigger
        if (!m_reading) {
          this.pullInput(1);
        }
      }

      if (m_space == null){
        m_space = waitForSpace();
        if (m_space == null){
          throw new Exception("JSTriggerReader: unable to connect to java space");
        }
      }

      //get a template
      Entry entry = null;


      try {
        if (m_take){
	  entry = (Entry)m_space.takeIfExists(m_readOnce, null, Long.MAX_VALUE);
	} else {
	  entry = (Entry)m_space.readIfExists(m_readOnce, null, Long.MAX_VALUE);
        }
      } catch (Exception e){
        m_space = waitForSpace();
        if (m_take){
	  entry = (Entry)m_space.takeIfExists(m_readOnce, null, Long.MAX_VALUE);
	} else {
	  entry = (Entry)m_space.readIfExists(m_readOnce, null, Long.MAX_VALUE);
        }
      }

      if (entry != null){
        if (m_debug){
          System.out.println("Read an entry: " + entry);
        }
        m_reading = false;
        this.pushOutput(entry, 0);
      } else {
        m_reading = true;
      }

     } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("Aborting: JSTriggerReader.doit()");
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

  public boolean getTake(){
    return m_take;
  }

  public void setTake(boolean b){
    m_take = b;
  }

  public String getJiniURL(){
    return m_jiniURL;
  }

  public void setJiniURL(String s){
    m_jiniURL = s;
  }

  public String getSpaceName(){
    return m_spaceName;
  }

  public void setSpaceName(String s){
    m_spaceName = s;
  }

  public boolean getDebug(){
    return m_debug;
  }

  public void setDebug(boolean b){
    m_debug = b;
  }

  public String getPolicyFile(){
    return m_policyFile;
  }

  public void setPolicyFile(String s){
    m_policyFile = s;
  }

  public void setJobName(String s){
    m_jobName = s;
  }

  public String getJobName(){
    return m_jobName;
  }

  //=================
  // Private Methods
  //=================

  /*
  com.sun.jini.lookup.locator</code> must be set to a "jini" URL (jini://host[:port]).
  */

  /**
   * Wait for the JavaSpace object to appear, and then return it.
   */
  public JavaSpace waitForSpace() throws Exception {

    Object tmpobj = null;

    LookupLocator locator = new LookupLocator(getJiniURL());

    if (m_debug) {
      System.out.println("JSTriggerReader: waitForSpace: locator = " + locator);
    }

    ServiceRegistrar registrar = (ServiceRegistrar)locator.getRegistrar();

    Entry[] attrs = new Entry[1];
    Name n = new Name();
    n.name = m_spaceName;
    attrs[0] = n;

    ServiceTemplate tmpl = new ServiceTemplate(null, null, attrs);

    if (m_debug) {
      System.out.println("LookupFinder: find: name = " + m_spaceName);
      System.out.println("LookupFinder: find: registrar = " +  registrar);
      System.out.println("LookupFinder: find: tmpl = " + tmpl);
    }

    do {
      tmpobj = registrar.lookup(tmpl);
      if (tmpobj == null) {
        try {
          System.out.println("waiting for " + m_spaceName);
          Thread.sleep(2000);
        } catch (Exception te) {}
      }
    } while (tmpobj == null);

    if (m_debug) {
      if (m_space != null){
        System.out.println("JSTriggerReader: waitForSpace: " + "space = " + m_space);
      }
    }
    System.out.println("found " + m_spaceName + " = " + tmpobj);
    m_space = (JavaSpace)tmpobj;

    return m_space;
  }


}