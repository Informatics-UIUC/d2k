package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.tree.*;


/**
 * put your documentation comment here
 */
public class DSTPTree extends JTree {
    //==============
    // Data Members
    //==============
    DSTPTreeModel m_ttm = null;

    //================
    // Constructor(s)
    //================
    public DSTPTree (DSTPTreeModel ttm) {
        super(ttm);
        m_ttm = ttm;
    }
    //================
    // Public Methods
    //================
}



