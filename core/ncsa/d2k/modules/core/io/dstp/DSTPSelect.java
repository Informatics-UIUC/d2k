package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.util.*;

//===============
// Other Imports
//===============

import ncsa.d2k.core.modules.*;

public class DSTPSelect extends UIModule {
    //==============
    // Data Members
    //==============
    private DSTPView m_view = null;

    //============
    // Properties
    //============

    public String m_servername = "sol.ncsa.uiuc.edu";
    public String getServerName(){return m_servername;}
    public void setServerName(String name){m_servername = name;}

    //================
    // Constructor(s)
    //================
    public DSTPSelect () {
    }

    //================
    // Static Methods
    //================
    public static void main (String[] args) {
        DSTPSelect DSTPSelect1 = new DSTPSelect();
    }

    //================
    // Public Methods
    //================

    /**
     * put your documentation comment here
     */
    public void beginExecution () {
      if (m_view != null){
        m_view.reset(this.getServerName());
      }
    }

    /**
     * put your documentation comment here
     */
    public void endExecution () {
        super.endExecution();
    }

    /**
     * put your documentation comment here
     */
    public void quitModule () {
        executionManager.moduleDone(this);
    }

    /**
     * put your documentation comment here
     * @param parm1
     * @return
     */
    public String getOutputInfo (int parm1) {
        if (parm1 == 0) {
            return  "ncsa.d2k.modules.core.datatype.table.Table";
        } else {
            return  "";
        }
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
      String[] out =  {"ncsa.d2k.modules.core.datatype.table.Table"};
      return  out;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleInfo () {
        return  "This module provides a gui for finding and selecting a data source using dstp.";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getInputTypes () {
        String[] in =  null;
        return  in;
    }

    /**
     * put your documentation comment here
     * @param parm1
     * @return
     */
    public String getInputInfo (int parm1) {
        if (parm1 == 0) {
            return  "";
        }
        else {
            return  "";
        }
    }

    /**
     * put your documentation comment here
     * @param ex
     */
    public void abort (Exception ex) {
        ex.printStackTrace();
        System.out.println(ex.getMessage());
        System.out.println("Aborting: DSTPSelect");
        viewAbort();
    }

    /**
     * put your documentation comment here
     * @param ex
     */
    public void abort () {
        System.out.println("Aborting: DSTPSelect");
        viewAbort();
    }

    /**
     * put your documentation comment here
     * @param o
     * @param i
     */
    public void push (Object o, int i) {
        pushOutput(o, i);
    }

/*
    public boolean cacheUserView() {
	return false;
    }
*/
    //===================
    // Protected Methods
    //===================
    public UserView createUserView () {
        m_view = new DSTPView(this);
        return  m_view;
    }

    /**
     * put your documentation comment here
     * @return
     */
    protected String[] getFieldNameMapping () {
        String[] out =  null;
        return  out;
    }

}



