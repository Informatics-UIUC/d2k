
/**
 * Title:        <p> VisModule for visualizing tree structures
 *
 * Description:  <p> Visualize tree structures using the WEKA "dotty" graph format and tree
 * visualization classes.
 * @author D. Searsmith
 * @version 1.0
 */
package ncsa.d2k.modules.weka.vis;

//==============
// Java Imports
//==============


//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;
import weka.core.Drawable;


public class WEKA_TreeVisualizer extends VisModule {

//==============
// Data Members
//==============


//================
// Constructor(s)
//================

  public WEKA_TreeVisualizer() {
  }

//================
// Public Methods
//================

  public String getInputInfo(int parm1) {
    return "Object implementing WEKA Drawable interface";
  }

  public String[] getInputTypes() {
    String[] in = {"weka.core.Drawable"};
    return in;
  }

  public String getOutputInfo(int parm1) {
    return "This module has no outputs.";
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("Produces a graphical visualization of a tree model.");
    sb = sb.append(" Requires the input model to implement the weka.core.Drawable interface.");
    return sb.toString();
  }

  protected UserView createUserView() {
    String graphstr = null;
    try {
      graphstr = ((Drawable)this.pullInput(0)).graph();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      //System.out.println("Aborting.");
      //getExecutionManager().abortExecution();
    }
    WEKA_TreeUserView wtuv = new WEKA_TreeUserView(graphstr);
    return (UserView) wtuv;
  }

  protected String[] getFieldNameMapping() {
    return null;
    //TODO: implement this ncsa.d2k.infrastructure.modules.ViewModule abstract method
  }

}