package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: </p>
 * <p>Description: Retrieve a model from a database
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.infrastructure.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.classloading.*;
import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.io.*;
import ncsa.d2k.controller.editor.tree.*;

import ncsa.d2k.controller.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.prediction.naivebayes.*;

import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import oracle.sql.*;
import oracle.jdbc.driver.*;

public class GetModelFromDB extends UIModule
       implements java.io.Serializable {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  File file = new File("dbModel.mdl");
  BrowseTables bt;
  BrowseTablesView btw;
  JTextField modelName;
  GenericTableModel trainSet;
  GenericTableModel classLabel;
  JTextField dataSize;
  JTable trainSetDef;
  JTable classLabelDef;
  JTextArea notes;
  static String  NOTHING = "";

  public GetModelFromDB() {
  }

  public String getOutputInfo (int i) {
    switch(i) {
      case 0: return "A NaiveBayes model to visualize";
      default: return "No such output";
    }
  }

  public String getInputInfo (int i) {
    switch(i) {
      case 0: return "JDBC data source to make database connection.";
      default: return "No such input.";
    }
  }

  public String getModuleInfo () {
    String text = "Retrieve the NaiveBayes model from a database table.";
    return text;
  }

  public String[] getInputTypes () {
    String [] in = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
    return in;
  }

  public String[] getOutputTypes () {
    String [] out = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return out;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }
/**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new GetModelView();
  }

  public class GetModelView extends JUserInputPane
    implements ActionListener {
    /* The module that creates this view.  We need a
       reference to it so we can get and set its properties. */
    JButton browseBtn;
    JButton cancelBtn;
    JButton getModelBtn;
    NaiveBayesModel model;

    public void setInput(Object input, int index) {
      if (index == 0) {
        cw = (ConnectionWrapper)input;
        modelName.setText(NOTHING);
        dataSize.setText(NOTHING);
        notes.setText(NOTHING);
        trainSet.initTableModel(100,4);
        classLabel.initTableModel(100,1);
     }
    }

    public Dimension getPreferredSize() {
      return new Dimension (500, 400);
    }

    public void initView(ViewModule mod) {
      removeAll();
      cw = (ConnectionWrapper)pullInput (0);

      // Panel to hold outline panels
      JPanel getModelPanel = new JPanel();
      getModelPanel.setLayout (new GridBagLayout());

      // Outline panel for model information
      JOutlinePanel modelInfo = new JOutlinePanel("Model Information");
      modelInfo.setLayout (new GridBagLayout());
      Constrain.setConstraints(modelInfo, new JLabel("Model Name"),
        0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(modelInfo, modelName = new JTextField(10),
        1,0,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      modelName.setText(NOTHING);
      modelName.addActionListener(this);
      Constrain.setConstraints(modelInfo, browseBtn = new JButton ("Browse"),
        3,0,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      browseBtn.addActionListener(this);
      Constrain.setConstraints(modelInfo, new JLabel("Training Data Size (Records)"),
        0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(modelInfo, dataSize = new JTextField(10),
        1,3,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);

      // Outline panel for info of training set contents
      String [] columnHeading = {"Attribute Name","Attribute Type","Number of Bins","Input/Output"};
      JOutlinePanel trainSetInfo = new JOutlinePanel("Training Set Information");
      trainSetInfo.setLayout (new GridBagLayout());
      trainSet = new GenericTableModel(100,4,false,columnHeading);
      trainSetDef = new JTable(trainSet);
      JScrollPane tablePane = new JScrollPane(trainSetDef);
      trainSetDef.setPreferredScrollableViewportSize (new Dimension(200,80));
      Constrain.setConstraints(trainSetInfo, tablePane,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      // Outline panel for info of classes
      String [] columnHeading2 = {"Class Name"};
      JOutlinePanel classInfo = new JOutlinePanel("Class Information");
      classInfo.setLayout (new GridBagLayout());
      classLabel = new GenericTableModel(100,1,false,columnHeading2);
      classLabelDef = new JTable(classLabel);
      JScrollPane tablePane2 = new JScrollPane(classLabelDef);
      classLabelDef.setPreferredScrollableViewportSize (new Dimension(100,80));
      Constrain.setConstraints(classInfo, tablePane2,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      // Outline panel for notes
      JOutlinePanel notesInfo = new JOutlinePanel("Notes");
      notesInfo.setLayout (new GridBagLayout());
      notes = new JTextArea(5,5);
      notes.setText(NOTHING);
      notes.setBackground(Color.white);
      JScrollPane textPane = new JScrollPane();
      textPane.getViewport().add(notes);
      textPane.setBounds(0,0,5,5);
      Constrain.setConstraints(notesInfo, textPane,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      // panel for buttons
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridBagLayout());

      /* Add 3 outline panels to saveModelPanel */
      Constrain.setConstraints(getModelPanel, modelInfo,
        0,0,4,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getModelPanel, trainSetInfo,
        0,3,4,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getModelPanel, classInfo,
        0,6,2,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getModelPanel, notesInfo,
        2,6,2,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getModelPanel, cancelBtn = new JButton ("   Cancel   "),
        2,9,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      cancelBtn.addActionListener(this);
      Constrain.setConstraints(getModelPanel, getModelBtn = new JButton ("Get Model"),
        3,9,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      getModelBtn.addActionListener(this);

      setLayout (new BorderLayout());
      add(getModelPanel, BorderLayout.SOUTH);
    } // initView

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == cancelBtn) {
        closeIt();
      }
      else if (src == browseBtn) {
        doBrowse();
      }
      else if (src == getModelBtn) {
        try {
          // user may manually enter the model name and click this button right way (without hit enter key)
          if (getTrainSet()) {
            if (getClassLabel()) {
              getMasterInfo();
              doit();
            }
          }
        }
        catch (Exception ex){
          JOptionPane.showMessageDialog(msgBoard,
            ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in doBrowse.");
        }
      }
      else if (src == modelName) {
        if (modelName.getText()!= null && modelName.getText()!=NOTHING)
        {
          /* wipe out previously loaded model definition */
          trainSet.initTableModel(100,4);
          trainSet.fireTableDataChanged();
          classLabel.initTableModel(100,1);
          classLabel.fireTableDataChanged();
          if (getTrainSet()) {
            if (getClassLabel()) {
              getMasterInfo();
            }
          }
        }
      }
    }
  }
  /** connect to a database and retrieve the list of available tables */
  protected void doBrowse() {
    String query = "select model_name from model_master";
    try {
      bt = new BrowseTables(cw, query);
      btw = new BrowseTablesView(bt, query);
      btw.setSize(250,200);
      btw.setTitle("Available Models");
      btw.setLocation(200,250);
      btw.setVisible(true);
      btw.addWindowListener(new WindowAdapter() {
        public void windowClosed(WindowEvent e)
        {
          modelName.setText(btw.getChoosedRow());
          /* wipe out previously loaded information */
          trainSet.initTableModel(100,4);
          trainSet.fireTableDataChanged();
          classLabel.initTableModel(100,1);
          classLabel.fireTableDataChanged();
          if (getTrainSet()) {
            if (getClassLabel()) {
              getMasterInfo();
            }
          }
         }
      });
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doBrowse.");
    }
  }

  protected void getMasterInfo() {
    try {
      Connection con = cw.getConnection();
      Statement stmt;
      String sb = new String("select train_set_count, notes " +
            "from model_master where model_name = '" +
            modelName.getText()) + "'";
      stmt = con.createStatement ();
      ResultSet tableSet = stmt.executeQuery(sb);
      tableSet.next();
      dataSize.setText(tableSet.getString(1));
      notes.setText(tableSet.getString(2));
      stmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getMasterInfo.");
    }
  }

  protected boolean getTrainSet() {
    try {
      Connection con = cw.getConnection();
      Statement stmt;
      String sb = new String("select attribute_name, data_type, bin_count, io_flag " +
            "from model_attribute where model_name = '" +
            modelName.getText() + "' order by attribute_seq");
      stmt = con.createStatement ();
      ResultSet tableSet = stmt.executeQuery(sb);
      int rIdx = 0;
      while (tableSet.next()) {
        trainSetDef.setValueAt(tableSet.getString(1),rIdx,0);
        // output column does not have the data type
        if (tableSet.getString(4).equals("Input")) {
          trainSetDef.setValueAt(tableSet.getString(2),rIdx,1);
        }
        trainSetDef.setValueAt(Integer.toString(tableSet.getInt(3)),rIdx,2);
        trainSetDef.setValueAt(tableSet.getString(4),rIdx,3);
        rIdx++;
      }
      /* if rIdx == 0, that indicate the tableSet is empty, the model does not exist */
      if (rIdx == 0) {
        JOptionPane.showMessageDialog(msgBoard,
                "Cannot find the model " + modelName.getText(), "Error",
                JOptionPane.ERROR_MESSAGE);
        System.out.println("Can not find the model " + modelName.getText());
        return (false);
      }
      stmt.close();
      return (true);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getTrainSet.");
      return (false);
    }
  }

  protected boolean getClassLabel() {
    try {
      Connection con = cw.getConnection();
      Statement stmt;
      String sb = new String("select class " +
            "from model_class where model_name = '" +
            modelName.getText() + "'");
      stmt = con.createStatement ();
      ResultSet tableSet = stmt.executeQuery(sb);
      int rIdx = 0;
      while (tableSet.next()) {
        classLabelDef.setValueAt(tableSet.getString(1),rIdx,0);
        rIdx++;
      }
      stmt.close();
      return (true);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getClassLabel.");
      return (false);
    }
  }

  protected void doit () throws Exception {
    BLOB blob;
    OutputStream outstream;
    InputStream instream;
    Connection con = cw.getConnection();
    // first retrieve the model and write to a file
    try {
      con.setAutoCommit(false);
      // get the blob locator into a result set
      Statement stmt = con.createStatement();
      ResultSet rset = stmt.executeQuery(
        "select model_object from model_master where model_name = '"
        + modelName.getText() + "'");
      rset.next();
      // Get the blob data - cast to OracleResult set
      blob = ((OracleResultSet)rset).getBLOB(1);
      // Get the length of the blob
      long length = blob.getLength();
      System.out.println("blob length" + length);

      // Get input stream from BLOB
      instream = blob.getBinaryStream();
      // Get the binary media file
      outstream = new FileOutputStream(file);
      // Read the input stream and write the output stream by chunks.
      System.out.println("the chunk size is " + blob.getChunkSize());
      byte[] chunk = new byte[blob.getChunkSize()];
      int i=-1;
      System.out.println("Loading");
      while((i = instream.read(chunk)) != -1) {
        outstream.write(chunk,0,i);
        System.out.print('.');
      }
      // Close the input and output stream.
      instream.close();
      outstream.close();

      // Close the statement.
      stmt.close();
      con.commit();
      con.close();
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in retrieve.");
    }

    // Load the model from a file
    FileInputStream istream = new FileInputStream(file);
    ObjectInputStream modin = new ObjectInputStream(istream);
    Object mdl = modin.readObject();
    istream.close();
    this.pushOutput(mdl,0);
    executionManager.moduleDone(this);
  } // doit

  protected void closeIt() {
    executionManager.moduleDone(this);
  }
}