package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;

/**
 *
 * <p>Title: ParseDSTPToDBTable</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class ParseDSTPToDBTable
    extends UIModule {
  //==============
  // Data Members
  //==============
  private DSTPView m_view = null;

  //============
  // Properties
  //============

  public String m_servername = "sol.ncsa.uiuc.edu";
  public String getServerName() {
    return m_servername;
  }

  public void setServerName(String name) {
    m_servername = name;
  }

  //================
  // Constructor(s)
  //================
  public ParseDSTPToDBTable() {
  }

  //================
  // Static Methods
  //================
  public static void main(String[] args) {
    ParseDSTPToDBTable DSTPSelect1 = new ParseDSTPToDBTable();
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Select DSTP Dataset";
  }

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[1];

    descriptions[0] = new PropertyDescription("serverName",
                                              "DSTP Server DNS or IP",
        "This is the address of the DSTP server.");

    return descriptions;
  }


  /**
     Code to execute before doit.
   */
  public void beginExecution() {
    if (m_view != null) {
      m_view.reset(this.getServerName());
    }
  }

  /**
     Code to execute at end of itinerary run.
   */
  public void endExecution() {
    super.endExecution();
  }

  public void quitModule() {
    executionManager.moduleDone(this);
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return
          "DBTable containing the data that was selected form the DSTP server.";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "DBTable";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module provides a GUI that is a metadata viewer for a DSTP ";
    s += "server.  The user can select a data source to be loaded into a DBTable ";
    s += "for use in D2K itineraries.";
    s += "</p>";

    s += "<p>";
    s += "See: http://www.dataspaceweb.net/index.htm.  DataSpaces is a research ";
    s += "product of The National Center for Data Mining (NCDM) at the University ";
    s += "of Illinois at Chicago (UIC).";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This GUI provides a tree view of metadata for DSTP data sources.  Any";
    s += "of these sources can be selected and loaded into a DBTable for use in ";
    s += "a D2K itinerary.  To select a source simply double click on the tree ";
    s += "node that bears the source name.  The attributes for the source will ";
    s += "be displayed in the ";
    s += "window on the right.  Initially, all attributes will appear selected. ";
    s += "The user can select and deselect attributes as desired.  When the ";
    s += "<i>Done</i> button is pressed the DBTable is built and output and ";
    s += "the GUI will be dismissed.  If the server on which the data source ";
    s += "resides (not necessarily the same as the server initially contacted) ";
    s += "is not reachable, then an error message is displayed and the GUI remains. ";
    s += "The <i>Reset</i> button causes the metadata to be rebuilt.  The ";
    s += "<i>Abort</i> button aborts the itinerary.  This version of the GUI ";
    s += "does not support sampling.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The DBTable that is created uses a primitive implementation of a DBDataSource ";
    s += "that lads the entire dataset into memory.  The DBTable is serializable ";
    s += "but the data is transient.  The data is reacquired from the DSTP server ";
    s += "when the object is deserialized. The DBTable is not mutable.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The DBTable created currently has the same memory limitations as an in ";
    s += "memory table.  This will eventually be corrected with an appropriate ";
    s += "caching scheme.";
    s += "</p>";
    return s;
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = null;
    return in;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "";
    } else {
      return "";
    }
  }

  public void abort(Exception ex) {
    ex.printStackTrace();
    System.out.println(ex.getMessage());
    System.out.println("Aborting: ParseDSTPToDBTable");
    viewAbort();
  }

  public void abort() {
    System.out.println("Aborting: ParseDSTPToDBTable");
    viewAbort();
  }

  public void push(Object o, int i) {
    pushOutput(o, i);
  }

  //===================
  // Protected Methods
  //===================
  public UserView createUserView() {
    m_view = new DSTPView(this);
    return m_view;
  }

  protected String[] getFieldNameMapping() {
    String[] out = null;
    return out;
  }

}
