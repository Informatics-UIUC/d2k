package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import javax.swing.tree.*;

//===============
// Other Imports
//===============

//D2K
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.db.DBDataSource;
import ncsa.d2k.modules.core.datatype.table.db.DBTable;
import ncsa.d2k.modules.core.datatype.table.db.dstp.DSTPDataSource;

//DSTP Client
import backend.*;

//JDOM
import org.jdom.*;
import org.jdom.adapters.*;
import org.jdom.input.*;
import org.jdom.output.*;


public class DSTPView extends JUserPane implements  ActionListener, ComponentListener, KeyListener {

  //==============
  // Data Members
  //==============
  static private final int s_MIN_WIDTH = 700;
  static private final int s_MIN_HEIGHT = 500;
  private boolean _debug = true;
  private DSTPSelect _select = null;

  private MetaNode _chosenNode = null;

  private Hashtable _cats = new Hashtable();

  //Components
  private JButton _reset = null;
  private JButton _abort = null;
  private JButton _done = null;
  //private JMenuBar _menu = null;
  private JMenu _file = null;
  private JMenuItem _close = null;
  private DSTPTreePanel _treepan = null;
  static private DSTPView s_instance = null;
  private JLabel _serverlbl = null;
  private JTextField _servertext = null;
  private JList _list = null;
  private DefaultListModel _listmodel = new DefaultListModel();


  //================
  // Constructor(s)
  //================

  public DSTPView () {
      s_instance = this;
      //setupMenu();
  }


  public DSTPView (DSTPSelect select) {
      _select = select;
      s_instance = this;
      //setupMenu();
  }

  //================
  // Static Methods
  //================
  static public DSTPView getInstance () {
      return  s_instance;
  }

  static public void main(String[] args){
    DSTPView view = new DSTPView();
    JFrame frame = new JFrame("TEST");
    frame.getContentPane().add(view);
    view.init();
    view.addServerInfoToTree("ncdm121.lac.uic.edu");
    frame.pack();
    frame.show();
  }

  //================
  // Public Methods
  //================

  //callback method
  public void pushOut(DSTPDataSource ds){
    getMain().push(new DBTable(ds), 0);
    enableAll();
    getMain().viewDone("Done");
  }

  public void disableAll(){
    _list.disable();
    _treepan.disable();
    _abort.disable();
    _done.disable();
    _reset.disable();
  }

  public void enableAll(){
    _list.enable();
    _treepan.enable();
    _done.enable();
    _reset.enable();
    _abort.enable();
  }

  public void reset () {
    _listmodel.removeAllElements();
    _select.setServerName(_servertext.getText());
    addServerInfoToTree(_servertext.getText());
  }

  public void reset (String srvr) {
    _listmodel.removeAllElements();
    _servertext.setText(srvr);
    addServerInfoToTree(srvr);
  }

  /**
   * put your documentation comment here
   * @return
   */
  public DSTPSelect getMain () {
      return  _select;
  }

  /**
   * put your documentation comment here
   * @return
   */
  public Dimension getPreferredSize () {
      return  new Dimension(700, 500);
  }

  public void setChosenNode(MetaNode node){
    _chosenNode = node;
    _listmodel.removeAllElements();
    Iterator atts = node.getAttributes();
    while(atts.hasNext()){
      attribute att = (attribute)atts.next();
      _listmodel.addElement(att.getAttName() + " " + att.getAttType() + " " + att.getAttNote());
    }
    _list.setSelectionInterval(0, _listmodel.getSize()-1);
  }

  //=================
  // Private Methods
  //=================
  /*
  private void setupMenu () {
      _menu = new JMenuBar();
      _menu.setBackground(new Color(83, 110, 117));
      _file = new JMenu("File");
      _file.setBackground(new Color(83, 110, 117));
      _file.setForeground(Color.white);
      _file.setFont(new Font("Arial", Font.BOLD, 14));
      _menu.add(_file);
      _close = new JMenuItem("Close");
      _close.setFont(new Font("Arial", Font.BOLD, 14));
      _close.addActionListener(this);
      _file.add(_close);
  }
 */

  private void init () {
    this.setBackground(new Color(83, 110, 117));
    this.setLayout(new BorderLayout());
    _treepan = new DSTPTreePanel(this);

    _list = new JList(_listmodel);
    JScrollPane sp1 = new JScrollPane(_list);

    JSplitPane split = new JSplitPane();
    split.add(_treepan, JSplitPane.LEFT);
    split.add(sp1, JSplitPane.RIGHT);

    add(split, BorderLayout.CENTER);

    JPanel tpan = new JPanel();
    _serverlbl = new JLabel("Server: " );
    _servertext = new JTextField();
    _servertext.setColumns(35);
    _servertext.addKeyListener(this);
    tpan.add(_serverlbl);
    tpan.add(_servertext);
    add(tpan, BorderLayout.NORTH);

    JPanel buttpan = new JPanel();
    _done = new JButton("Done");
    _done.addActionListener(this);
    buttpan.add(_done);
    _reset = new JButton("Reset");
    _reset.addActionListener(this);
    buttpan.add(_reset);
    _abort = new JButton("Abort");
    _abort.addActionListener(this);
    buttpan.add(_abort);

    add(buttpan, BorderLayout.SOUTH);

    if ((_select != null) && (_select.getServerName() != null)){
      _servertext.setText(_select.getServerName());
      addServerInfoToTree(_select.getServerName());
    }
  }

  private void parseMetaData(DSTPConnection conn){
    try {
      Vector data = null;
      data = conn.getServerDataVector("METADATA EXPAND",0);
      String concat = "<DSML>";
      for (int j = 0, k = data.size(); j < k; j++){
        String datum = (String)data.get(j);
        if (j > 0){
          concat += datum;
        }
      }
      concat += "</DSML>";

      SAXBuilder sb = new SAXBuilder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeBytes(concat);
      dos.flush();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      Document doc = sb.build(bais);

      System.out.println("Metadata parsed and document built ...");

      Iterator cats = doc.getRootElement().getChildren("CATEGORY").iterator();

      while (cats.hasNext()){
        Element cat = (Element)cats.next();
        ArrayList nodes = new ArrayList();
        _cats.put(cat, nodes);
        buildMetaNodes(nodes, cat);
      }
    } catch (Exception e){
    }
  }

  private void buildMetaNodes(ArrayList nodes, Element cat){

    Hashtable ht = new Hashtable();

    Iterator ucks = cat.getChildren("UCK").iterator();
    while (ucks.hasNext()){
      Element uck = (Element)ucks.next();
      String uckname = uck.getAttributeValue("NAME");
      String uckid = uck.getAttributeValue("ID");
      Iterator servers = uck.getChildren("SERVER").iterator();
      while (servers.hasNext()){
        Element server = (Element)servers.next();
        String servername = server.getAttributeValue("NAME");
        String serverlocation = server.getAttributeValue("Location");
        Iterator datafiles = server.getChildren("DATAFILE").iterator();
        while (datafiles.hasNext()){
          Element datafile = (Element)datafiles.next();
          String datafilename = datafile.getAttributeValue("NAME");
          String datafiledate = datafile.getAttributeValue("DATE");
          String datafiledescription = datafile.getAttributeValue("DESCRIPTION");
          String datafilenumrecords = datafile.getAttributeValue("NUMRECORDS");
          String datafilesource = datafile.getAttributeValue("SOURCE");
          //type
          //delimiter
          //dsfilename
          //address
          String key = datafilename + "::" + servername;
          MetaNode node = (MetaNode)ht.get(key);
          if (node == null){
            //put a new metanode
            node = new MetaNode(cat.getAttributeValue("NAME"),
                                servername,
                                serverlocation,
                                datafilename,
                                datafiledate,
                                datafiledescription,
                                datafilenumrecords,
                                datafilesource);
            node.addUCK(new uck(uckname, uckid));
            ht.put(key, node);
            nodes.add(node);
            Iterator atts = datafile.getChildren("ATTRIBUTE-DESCRIPTOR").iterator();
            while (atts.hasNext()){
              Element att = (Element)atts.next();
              //add att to metanode
              node.addAttribute(new attribute(att));
            }
          } else {
            //update existing metanode with a new uck
            node.addUCK(new uck(uckname, uckid));
          }
        }
      }
    }
  }

  private void addServerInfoToTree (String server) {
    try {
      _cats = new Hashtable();
      _treepan.getTree().removeAll();
      DSTPConnection conn = new DSTPConnection(server);
      parseMetaData(conn);
      DSTPTreeModel model = _treepan.getModel();
      DefaultMutableTreeNode root = _treepan.getNewRoot(server);
      model.setRoot(root);
      //rebuild the tree
      Enumeration cats = _cats.keys();
      while (cats.hasMoreElements()){
        Element cat = (Element)cats.nextElement();
        DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(cat.getAttributeValue("NAME"));
        model.insertNodeInto(newnode, root, 0);
        Iterator mnodes = ((ArrayList)_cats.get(cat)).iterator();
        while (mnodes.hasNext()){
          MetaNode mnode = (MetaNode)mnodes.next();
          mnode.buildSubTree(model, newnode);
        }
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage());
      System.out.println("EXCEPTION: " + e.getMessage());
      //e.printStackTrace();
      return;
    }
  }

  //====================================
  // Interface Implementation: UserView
  //====================================
  public void setInput (Object o, int i) {}

  public void initView (ViewModule vm) {
      init();
  }

  public Object getMenu () {
      return null;
  }
  //This must be the King of all hacks!!! UGLY
  boolean first = true;

  public void paint (Graphics g) {
      super.paint(g);
      if (first) {
          ((JFrame)this.getParent().getParent().getParent().getParent()).addComponentListener(this);
          ((JFrame)this.getParent().getParent().getParent().getParent()).addWindowListener(
                  new WindowAdapter() {

              /**
               * put your documentation comment here
               * @param e
               */
              public void windowClosing (WindowEvent e) {
                  getMain().viewAbort();
              }
          });
          first = false;
      }
  }

  //==========================================
  // Interface Implementation: KeyListener
  //==========================================

  public void keyPressed(KeyEvent e) {
    //Object src = e.getSource();
    Object src = e.getSource();
    if (src == _servertext){
      if (e.getKeyCode() == KeyEvent.VK_ENTER){
        reset();
      }
    }
   }

  public void keyReleased(KeyEvent e) {
    //Object src = e.getSource();
  }

  public void keyTyped(KeyEvent e) {
  }

  //==========================================
  // Interface Implementation: ActionListener
  //==========================================
  public void actionPerformed (ActionEvent e) {
      Object src = e.getSource();
      if (src == _reset) {
        reset();
      } else if (src == _abort) {
        _select.viewAbort();
      } else if (src == _done) {
        Object[] vals = _list.getSelectedValues();
        if (vals.length == 0){
          JOptionPane.showMessageDialog(this, "No attributes were selected.");
          return;
        }
        ArrayList alist = new ArrayList();
        for (int i = 0, n = vals.length; i < n; i++){
          StringTokenizer toker = new StringTokenizer((String)vals[i]);
          alist.add(toker.nextToken());
        }
        Iterator atts = _chosenNode.getAttributes();
        ArrayList sels = new ArrayList();
        while(atts.hasNext()){
          attribute att = (attribute)atts.next();
          if (alist.contains(att.getAttName())){
            sels.add(att);
          }
        }
        _chosenNode.setSelectedAttributes(sels);
        disableAll();
        new DSTPDataSource(this, _chosenNode);
      }
  }

  //=============================================
  // Interface Implementation: ComponentListener
  //=============================================
  public void componentResized (ComponentEvent e) {
      int width = ((JFrame)this.getParent().getParent().getParent().getParent()).getWidth();
      int height = ((JFrame)this.getParent().getParent().getParent().getParent()).getHeight();
      //we check if either the width
      //or the height are below minimum
      boolean resize = false;
      if (width < s_MIN_WIDTH) {
          resize = true;
          width = s_MIN_WIDTH;
      }
      if (height < s_MIN_HEIGHT) {
          resize = true;
          height = s_MIN_HEIGHT;
      }
      if (resize) {
          ((JFrame)this.getParent().getParent().getParent().getParent()).setSize(width,
                  height);
      }
  }

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentMoved (ComponentEvent e) {}

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentShown (ComponentEvent e) {}

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentHidden (ComponentEvent e) {}

  //=============
  // Inner Class
  //=============

  public class MetaNode implements java.io.Serializable{

    //==============
    // Data Members
    //==============
    private ArrayList _ucks = new ArrayList();
    private ArrayList _atts = new ArrayList();
    private ArrayList _selatts = null;

    //============
    // Properties
    //============
    private String _category = null;
    public String getCategory(){return _category;}
    public void setCategory(String cat){_category = cat;}

    private String _servername = null;
    public String getServerName(){return _servername;}
    public void setServerName(String servername){_servername = servername;}

    private String _serverlocation = null;
    public String getServerLocation(){return _serverlocation;}
    public void setServerLocation(String serverlocation){_serverlocation = serverlocation;}

    private String _datafilename = null;
    public String getDatafileName(){return _datafilename;}
    public void setDatafileName(String datafilename){_datafilename = datafilename;}

    private String _datafiledate = null;
    public String getDatafileDate(){return _datafiledate;}
    public void setDatafileDate(String datafiledate){_datafiledate = datafiledate;}

    private String _datafiledescription = null;
    public String getDatafileDescription(){return _servername;}
    public void setDatafileDescription(String datafiledescription){_datafiledescription = datafiledescription;}

    private String _datafilenumrecords = null;
    public String getDatafileNumRecords(){return _datafilenumrecords;}
    public void setDatafileNumRecords(String datafilenumrecords){_datafilenumrecords = datafilenumrecords;}

    private String _datafilesource = null;
    public String getDatafileSource(){return _datafilesource;}
    public void setDatafileSource(String datafilesource){_datafilesource = datafilesource;}

    //===============
    // Constructor(s)
    //===============
    public MetaNode (){
    }

    public MetaNode (String cat,
                    String servername,
                    String serverlocation,
                    String datafilename,
                    String datafiledate,
                    String datafiledescription,
                    String datafilenumrecords,
                    String datafilesource){
      _category = cat;
      _servername = servername;
      _serverlocation = serverlocation;
      _datafilename = datafilename;
      _datafiledate = datafiledate;
      _datafiledescription = datafiledescription;
      _datafilenumrecords = datafilenumrecords;
      _datafilesource = datafilesource;
    }

    //================
    // Static Methods
    //================


    //================
    // Public Methods
    //================

    public void buildSubTree(DSTPTreeModel model, DefaultMutableTreeNode root){
      DefaultMutableTreeNode datafilenode = new DefaultMutableTreeNode(new DSTPTreeNodeData(this, getDatafileName()));
      model.insertNodeInto(datafilenode, root, 0);

      DefaultMutableTreeNode servernamenode = new DefaultMutableTreeNode("Server: " + this.getServerName(), false);
      model.insertNodeInto(servernamenode, datafilenode, 0);

      DefaultMutableTreeNode serverlocnode = new DefaultMutableTreeNode("Server Location: " + this.getServerLocation(), false);
      model.insertNodeInto(serverlocnode, datafilenode, 0);

      DefaultMutableTreeNode datenode = new DefaultMutableTreeNode("Date: " + this.getDatafileDate(), false);
      model.insertNodeInto(datenode, datafilenode, 0);

      DefaultMutableTreeNode descnode = new DefaultMutableTreeNode("Description: " + this.getDatafileDescription(), false);
      model.insertNodeInto(descnode, datafilenode, 0);

      DefaultMutableTreeNode numnode = new DefaultMutableTreeNode("Number of Records: " + this.getDatafileNumRecords(), false);
      model.insertNodeInto(numnode, datafilenode, 0);

      DefaultMutableTreeNode srcnode = new DefaultMutableTreeNode("Source: " + this.getDatafileSource(), false);
      model.insertNodeInto(srcnode, datafilenode, 0);

      Iterator ucks = getUCK();
      String ucklbl = "UCK's ";
      while (ucks.hasNext()){
        ucklbl += ((uck)ucks.next()).getUCKName();
        if (ucks.hasNext()){
          ucklbl += ", ";
        }
      }
      DefaultMutableTreeNode ucknode = new DefaultMutableTreeNode(ucklbl);
      model.insertNodeInto(ucknode, datafilenode, 0);
      ucks = getUCK();
      while (ucks.hasNext()){
        uck ukey = (uck)ucks.next();
        DefaultMutableTreeNode ucknodedetail = new DefaultMutableTreeNode(ukey.getUCKName() + " " + ukey.getUCKID(), false);
        model.insertNodeInto(ucknodedetail, ucknode, model.getChildCount(ucknode));
      }

      Iterator atts = this.getAttributes();
      DefaultMutableTreeNode attsnode = new DefaultMutableTreeNode("Attributes");
      model.insertNodeInto(attsnode, datafilenode, 0);
      while (atts.hasNext()){
        attribute att = (attribute)atts.next();
        DefaultMutableTreeNode attnamenode = new DefaultMutableTreeNode(att.getAttName());
        model.insertNodeInto(attnamenode, attsnode, model.getChildCount(attsnode));

        DefaultMutableTreeNode atttypenode = new DefaultMutableTreeNode("Type: " + att.getAttType(), false);
        model.insertNodeInto(atttypenode, attnamenode, model.getChildCount(attnamenode));
        DefaultMutableTreeNode attunitnode = new DefaultMutableTreeNode("Unit: " + att.getAttUnit(), false);
        model.insertNodeInto(attunitnode, attnamenode, model.getChildCount(attnamenode));
        DefaultMutableTreeNode attnumnode = new DefaultMutableTreeNode("Number: " + att.getAttNumber(), false);
        model.insertNodeInto(attnumnode, attnamenode, model.getChildCount(attnamenode));
        DefaultMutableTreeNode attnotenode = new DefaultMutableTreeNode("Note: " + att.getAttNote(), false);
        model.insertNodeInto(attnotenode, attnamenode, model.getChildCount(attnamenode));
      }
    }

    public void addAttribute(attribute att){
      this._atts.add(att);
    }

    public void removeAttribute(attribute att){
      _atts.remove(att);
    }

    public Iterator getAttributes(){
      return _atts.iterator();
    }

    public void setSelectedAttributes(ArrayList alist){
      _selatts = alist;
    }

    public Iterator getSelectedAttributes(){
      if (_selatts != null){
        return _selatts.iterator();
      }
      return null;
    }

    public void addUCK(uck u){
      this._ucks.add(u);
    }

    public Iterator getUCK(){
      return _ucks.iterator();
    }

    public String toString(){
      return _servername;
    }

    public String getKey(){
      return getDatafileName() + "::" + getServerName();
    }
  }

  public class uck implements java.io.Serializable {
    uck(){}
    uck(String name, String id){
      _uckname = name;
      _uckid = id;
    }
    private String _uckname = null;
    public String getUCKName(){return _uckname;}
    public void setUCKName(String name){_uckname = name;}
    private String _uckid = null;
    public String getUCKID(){return _uckid;}
    public void setUCKID(String id){_uckid = id;}
  }

  public class attribute implements java.io.Serializable {
    attribute(){}
    attribute(Element att){
      _attname = att.getAttributeValue("NAME");
      _attnumber = att.getAttributeValue("NUMBER");
      _atttype = att.getAttributeValue("DATA-TYPE");
      _attunit = att.getAttributeValue("UNIT");
      _attnote = att.getAttributeValue("NOTE");
      _attuckname = att.getAttributeValue("UCKNAME");
      _attuckid = att.getAttributeValue("UCKID");
    }
    private String _attname = null;
    public String getAttName(){return _attname;}
    public void setAttName(String name){_attname = name;}
    private String _attnumber = null;
    public String getAttNumber(){return _attnumber;}
    public void setAttNumber(String number){_attnumber = number;}
    private String _atttype = null;
    public String getAttType(){return _atttype;}
    public void setAttType(String type){_atttype = type;}
    private String _attunit = null;
    public String getAttUnit(){return _attunit;}
    public void setAttUnit(String unit){_attunit = unit;}
    private String _attnote = null;
    public String getAttNote(){return _attnote;}
    public void setAttNote(String note){_attnote = note;}
    private String _attuckname = null;
    public String getAttUCKName(){return _attuckname;}
    public void setAttUCKName(String uckname){_attuckname = uckname;}
    private String _attuckid = null;
    public String getAttUCKID(){return _attuckid;}
    public void setAttUCKID(String uckid){_attuckid = uckid;}
  }
}



