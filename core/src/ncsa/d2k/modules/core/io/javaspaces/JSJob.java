package ncsa.d2k.modules.core.io.javaspaces;

//==============
// Java Imports
//==============

import java.rmi.MarshalledObject;

//===============
// Other Imports
//===============

import net.jini.core.entry.*;

public class JSJob implements Entry {

  //==============
  // Data Members
  //==============

  public String m_name = null;
  public MarshalledObject m_mobj = null;

  //================
  // Constructor(s)
  //================

  public JSJob(){
  }

  //================
  // Public Methods
  //================

  public String toString(){
    return "JSJob: " + m_name;
  }

  public void setName(String name){
    m_name = name;
  }

  public String getName(){
    return m_name;
  }

  public void setPayload(MarshalledObject mobj){
    m_mobj = mobj;
  }

  public MarshalledObject getPayload(){
    return m_mobj;
  }

}