package ncsa.d2k.modules.core.io.javaspaces;

/**
 * Title: SpaceWriter
 * Description: Writes entries/objects to a JavaSpace
 * Copyright:    Copyright (c) 2001
 * Company: NCSA
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


public class JSWriter extends OutputModule {


  //==============
  // Data Members
  //==============

  //properties
  private boolean m_verbose = false;
  private String m_jiniURL = "jini://hyperion.ncsa.uiuc.edu:4160";
  private String m_spaceName = "JavaSpace";
  protected boolean m_debug = false;

  protected JavaSpace m_space = null;
  private String m_policyFile = "/home/dsears/dump/jini/jini1_1/policy/policy.all";

  private boolean m_inited = false;

  private String m_jobName = "test";

  //================
  // Constructor(s)
  //================

  public JSWriter() {
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

  public String getModuleInfo() {
    return "This module will write an entry to a JavaSpace";
  }

  public String getOutputInfo(int parm1) {
      return "";
  }

  public String[] getOutputTypes() {
    String[] out = null;
    return out;
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
  }

  protected void doit() throws java.lang.Exception {
    try {

      if (m_space == null){
        m_space = waitForSpace();
        if (m_space == null){
          throw new Exception("JSWriter: unable to connect to java space");
        }
      }

      Entry entry = (Entry)this.pullInput(0);
      ((JSJob)entry).setName(m_jobName);
      try {
        m_space.write(entry, null, net.jini.core.lease.Lease.FOREVER);
      } catch (Exception e){
        m_space = waitForSpace();
        m_space.write(entry, null, net.jini.core.lease.Lease.FOREVER);
      }

      if (m_debug){
        System.out.println("Wrote an entry: " + entry);
      }

     } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("Aborting: JSWriter.doit()");
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
      System.out.println("JSWriter: waitForSpace: locator = " + locator);
    }


    ServiceRegistrar registrar = (ServiceRegistrar)locator.getRegistrar(60*5000);

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
        System.out.println("JSWriter: waitForSpace: " + "space = " + m_space);
      }
    }
    System.out.println("found " + m_spaceName + " = " + tmpobj);
    m_space = (JavaSpace)tmpobj;

    return m_space;
  }

}