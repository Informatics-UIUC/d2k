/* This is the working version before adding verification routine */
package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: SaveModelToDB
 * <p>Description: Save Model object and Model meta information to a database
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG
 * @author Dora Cai
 * @version 1.0
 */

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.prediction.naivebayes.*;

import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import oracle.sql.*;
import oracle.jdbc.driver.*;

public class SaveModelToDB extends UIModule {
  JOptionPane msgBoard = new JOptionPane();
  static String  NOTHING = "";

  public SaveModelToDB() {
  }

  public String getOutputInfo (int i) {
    return null;
  }

  public String getInputInfo (int i) {
    switch(i) {
      case 0: return "JDBC data source to make database connection.";
      case 1: return "A NaiveBayes model to save.";
      default: return "No such input.";
    }
  }

  public String getModuleInfo () {
    String text = "Save the NaiveBayes model to a database table.";
    return text;
  }

  public String[] getInputTypes () {
    String [] in = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                    "ncsa.d2k.core.prediction.naivebayes.NaiveBayesModel" };
    return in;
  }

  public String[] getOutputTypes () {
    return null;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }
  /**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new SaveModelView();
  }

  public class SaveModelView extends JUserInputPane
    implements ActionListener {
    /* The module that creates this view.  We need a
       reference to it so we can get and set its properties. */
    GenericTableModel trainSet;
    GenericTableModel classLabel;
    JTextField modelName;
    JTextField dataSize;
    JTable trainSetDef;
    JTable classLabelDef;
    JTextArea notes;
    JButton saveModelBtn;
    JButton cancelBtn;
    ConnectionWrapper cw;
    Connection con;
    NaiveBayesModel model;
    File file = new File("aModel.mdl");

    public void setInput(Object input, int index) {
      if (index == 0) {
        cw = (ConnectionWrapper)input;
        System.out.println("the input 1 in setInput is " + cw.toString());
      }
      else if (index == 1) {
        model = (NaiveBayesModel)input;
        modelName.setText(NOTHING);
        dataSize.setText(NOTHING);
        notes.setText(NOTHING);
        trainSet.initTableModel(100,4);
        classLabel.initTableModel(100,1);
        // get the row count in the training set
        displayRowCount();
        // get the attribute information
        displayAttributes();
        // get the class information
        displayClasses();
      }
    }

    public Dimension getPreferredSize() {
      return new Dimension (500, 400);
    }

    public void initView(ViewModule mod) {
      removeAll();
      System.out.println("enter initView");
      cw = (ConnectionWrapper)pullInput (0);
      model = (NaiveBayesModel)pullInput (1);

      // Panel to hold outline panels
      JPanel saveModelPanel = new JPanel();
      saveModelPanel.setLayout (new GridBagLayout());

      // Outline panel for model information
      JOutlinePanel modelInfo = new JOutlinePanel("New Model Information");
      modelInfo.setLayout (new GridBagLayout());
      Constrain.setConstraints(modelInfo, new JLabel("Model Name"),
        0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(modelInfo, modelName = new JTextField(10),
        1,0,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      modelName.setText(NOTHING);
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
      Constrain.setConstraints(saveModelPanel, modelInfo,
        0,0,4,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(saveModelPanel, trainSetInfo,
        0,3,4,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(saveModelPanel, classInfo,
        0,6,2,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(saveModelPanel, notesInfo,
        2,6,2,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(saveModelPanel, cancelBtn = new JButton ("   Cancel   "),
        2,9,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      cancelBtn.addActionListener(this);
      Constrain.setConstraints(saveModelPanel, saveModelBtn = new JButton ("Save Model"),
        3,9,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      saveModelBtn.addActionListener(this);

      setLayout (new BorderLayout());
      add(saveModelPanel, BorderLayout.CENTER);

      // get the row count in the training set
      displayRowCount();
      // get the attribute information
      displayAttributes();
      // get the class information
      displayClasses();
    } // initView

    public void displayRowCount() {
        int totalRow = model.table.getNumRows();
        dataSize.setText(Integer.toString(totalRow));
        System.out.println("totalRow is " + dataSize.getText());
    }

    public void displayAttributes() {
      String [] attributeNames = model.getAttributeNames();
      String attributeName;
      int numBin;
      int i;
      double total;
      for (i = 0; i < attributeNames.length; i++) {
        trainSetDef.setValueAt(attributeNames[i].toString(),i,0);
        // the column order in attributeNames is different the one in model.table
        // we need to loop through model.table's column to match it
        for (int j = 0; j<model.table.getNumColumns(); j++) {
          if (attributeNames[i].toString().equals(model.table.getColumnLabel(j).toString())) {
            if (model.table.getColumn(j) instanceof StringColumn)
              trainSetDef.setValueAt("Text",i,1);
            else if(model.table.getColumn(j) instanceof ByteArrayColumn)
              trainSetDef.setValueAt("Text",i,1);
            else if(model.table.getColumn(j) instanceof CharArrayColumn)
              trainSetDef.setValueAt("Text",i,1);
            else
              trainSetDef.setValueAt("Numeric",i,1);
          }
        }
        numBin = model.getBinNames(attributeNames[i].toString()).length;
        trainSetDef.setValueAt(Integer.toString(numBin),i,2);
        trainSetDef.setValueAt("Input",i,3);
      }
      // class column is the only output column
      trainSetDef.setValueAt(model.getClassColumn().toString(),i,0);
      trainSetDef.setValueAt("Output",i,3);
    }

    public void displayClasses() {
      String [] classNames = model.getClassNames();
      String className;
      int i;

      for (i = 0; i < classNames.length; i++) {
        classLabelDef.setValueAt(classNames[i].toString(),i,0);
      }
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == cancelBtn) {
        System.out.println("Cancel button is pressed");
        viewAbort();
      }
      else if (src == saveModelBtn) {
        System.out.println("Save model button is pressed");
        if (verifyData()) {
          doit();
        }
      }
    }

  protected boolean verifyData () {
    String qryStr;
    if (modelName.getText().length() == 0 ||
        modelName.getText().equals(NOTHING)) {
      JOptionPane.showMessageDialog(msgBoard,
        "You must enter a name for the model.", "Information",
        JOptionPane.INFORMATION_MESSAGE);
      return (false);
    }
    else {// verify the name is not used
      try {
        Connection con = cw.getConnection();
        // create a statement
        Statement stmt = con.createStatement();
        qryStr = "select count(*) from model_master where model_name = '" +
                 modelName.getText() + "'";
        ResultSet rset = stmt.executeQuery(qryStr);
        rset.next();
        System.out.println("count is " + rset.getInt(1));
        if (rset.getInt(1) > 0) {
          JOptionPane.showMessageDialog(msgBoard,
          "The model name " + modelName.getText() +
          " has be used. Please enter a different name.", "Information",
          JOptionPane.INFORMATION_MESSAGE);
          return (false);
        }
        else
          return (true);
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(msgBoard,
          e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured when validating the model name.");
      }
    }
    return (true);
  }

  protected void doit () {
    BLOB blob;
    OutputStream outstream;
    FileInputStream instream;
    File file = new File("aModel.mdl");
    String qryStr;
    // first save the model to a file
    try {
      // set the name of the temporary file
      model.setName(file.getName().toString());
      model.setAlias(file.getName().toString());
      FileOutputStream ostream = new FileOutputStream(file);
      ObjectOutputStream modout = new ObjectOutputStream(ostream);
      modout.writeObject(model);
      modout.flush();
      modout.close();
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error when saving model to file");
    }
    System.out.println("model has been saved into a file");

    String userName = System.getProperty("user.name");
    System.out.println("user name is " + userName);
    java.util.Date now = new java.util.Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String dateStr = df.format(now);

    // save the master information and model object to model_master
    try {
      // Set AutoCommit to OFF - required by BLOB locking mechanism
      Connection con = cw.getConnection();
      con.setAutoCommit(false);
      // create a statement
      Statement stmt = con.createStatement();
      // insert an empty BLOB locator
      System.out.println("modelName is " + modelName.getText());
      System.out.println("dataSize is " + Integer.parseInt(dataSize.getText()));
      System.out.println("notes is " + notes.getText());

      qryStr = "insert into model_master values ('" + modelName.getText() +
               "', " + Integer.parseInt(dataSize.getText()) + ", '" +
               notes.getText() + "', empty_blob(), '" + userName +
               "', to_date('" + dateStr + "', 'yyyy/mm/dd hh24:mi:ss'))";
      //System.out.println("query string is " + qryStr);

      stmt.execute(qryStr);
      System.out.println("after insert");
      // Execute the query and lock the BLOB row
      qryStr = "select model_object from model_master where model_name = '" +
               modelName.getText() + "' for update";
      ResultSet rset = stmt.executeQuery(qryStr);
      System.out.println("after select 1 ");
      rset.next();
      // Get the BLOB locator
      blob = ((OracleResultSet)rset).getBLOB(1);
      // Insert to the BLOB from an output stream.
      System.out.println("after select 2 ");

      // Get the binary media file.
      instream = new FileInputStream(file);

      // Insert to the BLOB from an output stream.
      outstream = blob.getBinaryOutputStream();

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
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured when insert to model_master.");
    }

    // save attribute info into model_attribute table
    try {
      Connection con = cw.getConnection ();
      Statement stmt;
      int attribute_seq = 1;
      for(int i = 0; i < trainSet.getRowCount(); i++, attribute_seq++) {
        if (trainSet.getValueAt(i,0).toString().length() == 0 ||
        trainSet.getValueAt(i,0).toString().equals(NOTHING)) {
          break;
        }
        else {
          String sb = new String("insert into model_attribute " +
                    " values (");
          // get model name
          sb = sb + "'" + modelName.getText() + "', ";
          // get attribute sequence
          sb = sb + attribute_seq + ", ";
          // get attribute name
          sb = sb + "'" + trainSet.getValueAt(i,0) + "', ";
          // get attribute data type
          sb = sb + "'" + trainSet.getValueAt(i,1) + "', ";
          // get bin count. Output attribute does not have bin count
          if (trainSet.getValueAt(i,3).toString().equals("Output")) {
            sb = sb +  "0, ";
          }
          else {
            sb = sb + Integer.parseInt((trainSet.getValueAt(i,2)).toString()) + ", ";
          }
          // get IO flag
          sb = sb + "'" + trainSet.getValueAt(i,3) + "')";
          //System.out.println("sb for attribute is " + sb);
          stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          stmt.close();
        }
      } /* end of for i */
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured when insert into model_attribute.");
    }

    // Save class info to model_class
    try {
      Connection con = cw.getConnection ();
      Statement stmt;
      for(int i = 0; i < classLabel.getRowCount(); i++) {
        if (classLabel.getValueAt(i,0).toString().length() == 0 ||
            classLabel.getValueAt(i,0).toString().equals(NOTHING)) {
          break;
        }
        else {
          String sb = new String("insert into model_class " +
                    " values (");
          // get model name
          sb = sb + "'" + modelName.getText() + "', ";
          // get class name
          sb = sb + "'" + classLabel.getValueAt(i,0) + "')";
          //System.out.println("sb for class is " + sb);
          stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          stmt.close();
        }
      } /* end of for i */
      JOptionPane.showMessageDialog(msgBoard,
        "The model " + modelName.getText() + " has been saved into the database",
        "Information", JOptionPane.INFORMATION_MESSAGE);
      con.commit();
      con.close();
      viewAbort();
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured when insert into model_class.");
    }
  }
  }
}