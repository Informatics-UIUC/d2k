
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


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
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
		switch (parm1) {
			case 0: return "Object implementing WEKA Drawable interface";
			default: return "No such input";
		}
	}

  public String[] getInputTypes() {
		String[] types = {"weka.core.Drawable"};
		return types;
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Produces a graphical visualization of a tree model. Requires the input     model to implement the weka.core.Drawable interface.  </body></html>";
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


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_TreeVisualizer";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Drawable";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
