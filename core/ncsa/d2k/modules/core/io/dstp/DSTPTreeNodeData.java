package ncsa.d2k.modules.core.io.dstp;

import javax.swing.tree.*;
import backend.*;

public class DSTPTreeNodeData {

  //==============
  // Data Members
  //==============

  private String _print = null;
  private DSTPView.MetaNode _node = null;

  //================
  // Constructor(s)
  //================

  public DSTPTreeNodeData(DSTPView.MetaNode node, String msg){
    _print = msg;
    _node = node;
  }

  public String toString(){
    return _print;
  }

  public DSTPView.MetaNode getNode(){
    return _node;
  }
}