package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.awt.event.*;
import  java.awt.*;
import  javax.swing.*;
import  javax.swing.tree.*;
import  javax.swing.event.*;
import  java.util.*;

//===============
// Other Imports
//===============
import backend.*;

public class DSTPTreePanel extends JPanel implements MouseInputListener {
    //==============
    // Data Members
    //==============
    private DSTPView m_view = null;
    //components
    private DSTPTree m_tree = null;
    private DSTPTreeModel m_model = null;
    private JScrollPane m_scroll = null;
    private DefaultMutableTreeNode m_root = null;

    /*
    private ImageIcon m_folderIcon = new ImageIcon(ThemeViewController.makePath(ThemeViewController.getImagesDir(),
            "folder.gif"));
    private ImageIcon m_folderSelIcon = new ImageIcon(ThemeViewController.makePath(ThemeViewController.getImagesDir(),
            "folder_selected.gif"));
    private ImageIcon m_leafIcon = new ImageIcon(ThemeViewController.makePath(ThemeViewController.getImagesDir(),
            "file.gif"));
    */

    //================
    // Constructor(s)
    //================
    public DSTPTreePanel (DSTPView view) {
        m_view = view;
        init();
    }

    //================
    // Public Methods
    //================

    public DefaultMutableTreeNode getRoot(){
      return m_root;
    }

    public DefaultMutableTreeNode getNewRoot(String s){
      return new DefaultMutableTreeNode("DSTP " + s, true);
    }

    public DSTPTreeModel getModel(){
      return m_model;
    }

    public DSTPTree getTree(){
      return m_tree;
    }


    //=================
    // Package Methods
    //=================

    //=================
    // Private Methods
    //=================
    private void init () {
        setLayout(new BorderLayout());
        m_root = new DefaultMutableTreeNode("DSTP Sources", true);
        //m_root.setUserObject(new DSTPTreeNodeData(null, "DSTP Sources"));
        m_model = new DSTPTreeModel(m_root);
        m_model.setAsksAllowsChildren(true);
        m_tree = new DSTPTree(m_model);
        //m_tree.setFont(new Font("Arial", Font.BOLD, 14));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        //renderer.setClosedIcon(m_folderIcon);
        //renderer.setOpenIcon(m_folderSelIcon);
        //renderer.setLeafIcon(m_leafIcon);
        m_tree.setCellRenderer(renderer);
        m_tree.setShowsRootHandles(true);
        m_scroll = new JScrollPane(m_tree);
        m_tree.setEditable(false);
        add(m_scroll, BorderLayout.CENTER);
        //m_model.insertNodeInto(new DefaultMutableTreeNode("Test Child", false), m_root, 0);
        m_tree.addMouseListener(this);
        m_tree.addMouseMotionListener(this);
   }


  //=========================================
  // Interface Implementation: MouseListener
  //=========================================

  public void mouseClicked(MouseEvent evt){
    if (evt.getClickCount() > 1){
      Object ob = null;
      try {
        ob = ((DefaultMutableTreeNode)m_tree.getSelectionModel().getSelectionPath().getLastPathComponent()).getUserObject();
      } catch (Exception e){
        return;
      }
      System.out.println("saw a dbl click");
      if (ob != null){
        System.out.println("clicked on node returned non null user object");
        if (ob instanceof DSTPTreeNodeData){
          try {
            DSTPTreeNodeData ndata = (DSTPTreeNodeData)ob;
            System.out.println("it is a DSTPTreeNodeData");
            DSTPView.MetaNode node = ndata.getNode();
            m_view.setChosenNode(node);
          } catch (Exception e){
            System.out.println("EXCEPTION in building datasource: " + e);
            e.printStackTrace();
          }
        }
      }
    }
  }

  public void mouseEntered(MouseEvent evt){
  }

  public void mouseExited(MouseEvent evt){
  }

  public void mousePressed(MouseEvent evt){
  }

  public void mouseReleased(MouseEvent evt){
  }

  public void mouseMoved(MouseEvent evt){
  }

  public void mouseDragged(MouseEvent evt){
  }

}



