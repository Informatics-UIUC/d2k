/**
 * Title:        <p> WEKA_TreeUserView
 * Description:  <p> A UserView for displaying weka.core.Drawable instances (which
 * for now are only representations of trees).
 * @author D. Searsmith
 * @version 1.0
 */
package ncsa.d2k.modules.weka.vis;

//==============
// Java Imports
//==============

import java.util.Hashtable;
import java.awt.Component;
import java.awt.Dimension;

//===============
// Other Imports
//===============

import weka.gui.treevisualizer.*;
import ncsa.d2k.infrastructure.modules.ViewModule;

public class WEKA_TreeUserView extends TreeVisualizer implements ncsa.d2k.infrastructure.views.UserView {

    //==============
    // Data Members
    //==============

    //================
    // Constructor(s)
    //================

    public WEKA_TreeUserView(String dot) {
        super(null, dot, new PlaceNode2());
    }

    //====================================
    // Interface Implementation: UserView
    //====================================

  /**
   * This method is called whenever an input arrives, and is responsible
   * for modifying the contents of any gui components that should reflect
   * the value of the input.
   *
   * @param input this is the object that has been input.
   * @param index the index of the input that has been received.
   */
    public void setInput( Object input, int index ){}

  /**
   * This method is called to allow the view to initialize itself, and to set
   * any fields for which a reference to the module may be required.
   *
   * @param module the module this view is associated with.
   */
    public void initView( ViewModule module ){}

  /**
   * This method will traverse the containers component hierarchy, in search of
   * components which implement the addFields interface, and for each component where
   * this is the case, calling that components addFields method to get the results.
   *
   * @param message the hashtable that will contain the named results.
   * @param subComponents the components to examine.
   */
    public void compileResults( Hashtable message, Component [] subComponents ){}

  /**
   * This method is called to get a menu bar.  This menu is then added to the frame that contains
   * this view.
   * @return a java.awt.MenuBar or a javax.swing.JMenuBar
   */
    public Object getMenu() {
        return null;
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

}