package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: SaveModelToDB
 * <p>Description: Save Model object and Model meta information to a database
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG
 * @author Dora Cai
 * @version 1.0
 */


import ncsa.d2k.core.modules.UIModule;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.naivebayes.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class SaveModelToDB extends UIModule {
  JOptionPane msgBoard = new JOptionPane();
  String modelType;
  static String  NOTHING = "";
  static String  NB = "NB"; // for NaiveBayes
  static String  DT = "DT"; // for Decision Tree

  public SaveModelToDB() {
  }

  public String getOutputInfo (int i) {
		switch (i) {
			default: return "No such output";
		}
	}

  public String getInputInfo (int i) {
		switch (i) {
			case 0: return "JDBC data source to make database connection.";
			case 1: return "A prediction model to save.";
			default: return "No such input";
		}
	}

  public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Save a data mining model to a database table.  </body></html>";
	}

  public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","ncsa.d2k.module.PredictionModelModule"};
		return types;
	}

  public String[] getOutputTypes () {
		String[] types = {		};
		return types;
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
    GenericMatrixModel trainSet;
    GenericMatrixModel classLabel;
    JTextField modelName;
    JTextField modelDesc;
    JTextField dataSize;
    JTable trainSetDef;
    JTable classLabelDef;
    JTextArea notes;
    JButton saveModelBtn;
    JButton cancelBtn;
    ConnectionWrapper cw;
    Connection con;
    PredictionModelModule model;
    NaiveBayesModel modelNB;
    DecisionTreeModel modelDT;
    File file = new File("aModel.mdl");

    public void setInput(Object input, int index) {
      if (index == 0) {
        cw = (ConnectionWrapper)input;
      }
      else if (index == 1) {
        model = (DecisionTreeModel)input;
        if (model.getClass().toString().equals(
          "class ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel")) {
          modelType = NB; // NB for NaiveBayes
        }
        else if (model.getClass().toString().equals(
          "class ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel")) {
          modelType = DT; // DT for decision tree
          modelDT = (DecisionTreeModel)model;
        }
        modelName.setText(NOTHING);
        dataSize.setText(NOTHING);
        notes.setText(NOTHING);
        trainSet.initMatrixModel(100,4);
        classLabel.initMatrixModel(100,1);
        // get the row count in the training set
        displayRowCount();
        // get the attribute information
        displayAttributes();
        // get the number of bins for Naive Bayes Model
        if (modelType.equals(NB)) {
          displayNumBins();
        }
        // get the class information
        displayClasses();
      }
    }

    public Dimension getPreferredSize() {
      return new Dimension (500, 450);
    }

    public void initView(ViewModule mod) {
      removeAll();
      cw = (ConnectionWrapper)pullInput (0);
      model = (PredictionModelModule)pullInput (1);
      if (model.getClass().toString().equals(
          "class ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel")) {
          modelType = NB; // NB for NaiveBayes
          modelNB = (NaiveBayesModel)model;
      }
      else if (model.getClass().toString().equals(
          "class ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel")) {
          modelType = DT; // DT for decision tree
          modelDT = (DecisionTreeModel)model;
      }

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
      Constrain.setConstraints(modelInfo, new JLabel("Model Type"),
        0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(modelInfo, modelDesc = new JTextField(10),
        1,1,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      if (modelType.equals(NB)) {
        modelDesc.setText("Naive Bayes");
      }
      else if (modelType.equals(DT)) {
        modelDesc.setText("Decision Tree");
      }
      Constrain.setConstraints(modelInfo, new JLabel("Training Data Size (Records)"),
        0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(modelInfo, dataSize = new JTextField(10),
        1,3,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);

      // Outline panel for info of training set contents
      String [] columnHeading = {"Attribute Name","Attribute Type","Number of Bins","Input/Output"};
      JOutlinePanel trainSetInfo = new JOutlinePanel("Training Set Information");
      trainSetInfo.setLayout (new GridBagLayout());
      trainSet = new GenericMatrixModel(100,4,false,columnHeading);
      trainSetDef = new JTable(trainSet);
      JScrollPane tablePane = new JScrollPane(trainSetDef);
      trainSetDef.setPreferredScrollableViewportSize (new Dimension(200,80));
      Constrain.setConstraints(trainSetInfo, tablePane,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      // Outline panel for info of classes
      String [] columnHeading2 = {"Class Name"};
      JOutlinePanel classInfo = new JOutlinePanel("Class Information");
      classInfo.setLayout (new GridBagLayout());
      classLabel = new GenericMatrixModel(100,1,false,columnHeading2);
      classLabelDef = new JTable(classLabel);
      JScrollPane tablePane2 = new JScrollPane(classLabelDef);
      classLabelDef.setPreferredScrollableViewportSize (new Dimension(100,80));
      Constrain.setConstraints(classInfo, tablePane2,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      // Outline panel for notes
      JOutlinePanel notesInfo = new JOutlinePanel("Notes");
      notesInfo.setLayout (new GridBagLayout());
      notes = new JTextArea(5,5);
      notes.setLineWrap(true);
      notes.setAutoscrolls(true);
      notes.setText(NOTHING);
      notes.setBackground(Color.white);
      JScrollPane textPane = new JScrollPane();
      textPane.setAutoscrolls(true);
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
      // get the number of bins for Naive Bayes Model
      if (modelType.equals(NB)) {
        displayNumBins();
      }
      // get the class information
      displayClasses();
    } // initView

    /**
     * display the row count
     */
    public void displayRowCount() {
        int totalRow = model.getTrainingSetSize();
        dataSize.setText(Integer.toString(totalRow));
    }

    /**
     * display the selected attributes
     */
    public void displayAttributes() {
      String [] inAttributeNames = model.getInputColumnLabels();
      String [] outAttributeNames = model.getOutputColumnLabels();
      String [] inAttributeTypes = model.getInputFeatureTypes();
      String [] outAttributeTypes = model.getOutputFeatureTypes();
      int i, j;
      for (i = 0; i < inAttributeNames.length; i++) {
        trainSetDef.setValueAt(inAttributeNames[i],i,0);
        trainSetDef.setValueAt(inAttributeTypes[i],i,1);
        trainSetDef.setValueAt("Input",i,3);
      }
      for (j = i; (j-i) < outAttributeNames.length; j++) {
        trainSetDef.setValueAt(outAttributeNames[j-i],j,0);
        trainSetDef.setValueAt(outAttributeTypes[j-i],j,1);
        trainSetDef.setValueAt("Output",j,3);
      }
    }

    /**
     * display the number of bins
     */
    public void displayNumBins() {
      String [] inAttributeNames = model.getInputColumnLabels();
      int numBin;
      int i;
      for (i = 0; i < inAttributeNames.length; i++) {
        numBin = modelNB.getBinNames(inAttributeNames[i].toString()).length;
        trainSetDef.setValueAt(Integer.toString(numBin),i,2);
      }
    }

    /**
     * display the class labels
     */
    public void displayClasses() {
      int i;
      String [] classNames = null;
      String className;
      // method getClassNames is not in the super class PredictionModelModule
      // only in the NaiveBayesModel and DecisionTreeModel
      if (modelType.equals(NB)) {
        classNames = modelNB.getClassNames();
      }
      else if (modelType.equals(DT)) {
        classNames = modelDT.getClassNames();
      }
      for (i = 0; i < classNames.length; i++) {
        classLabelDef.setValueAt(classNames[i].toString(),i,0);
      }
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == cancelBtn) {
        viewAbort();
      }
      else if (src == saveModelBtn) {
        if (verifyData()) {
          doAction();
        }
      }
    }

  /** verify all input information is properly entered
   *  @return true if verification is passed, false otherwise
   */
  protected boolean verifyData () {
    String qryStr;
    if (modelName.getText().length() == 0 ||
        modelName.getText().equals(NOTHING)) {
      JOptionPane.showMessageDialog(msgBoard,
        "You must enter a name for the model.", "Information",
        JOptionPane.INFORMATION_MESSAGE);
      return (false);
    }
    else {// verify the name is not already used
      try {
        Connection con = cw.getConnection();
        // create a statement
        Statement stmt = con.createStatement();
        qryStr = "select count(*) from model_master where model_name = '" +
                 modelName.getText() + "'";
        ResultSet rset = stmt.executeQuery(qryStr);
        rset.next();
        if (rset.getInt(1) > 0) {
          JOptionPane.showMessageDialog(msgBoard,
          "The model name " + modelName.getText() +
          " has been used. Please enter a different name.", "Information",
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

  /** insert the model into database tables: model_master, model_attribute and
   *  model_class.
   */
  protected void doAction () {
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
      qryStr = "insert into model_master values ('" + modelName.getText() +
               "', '" + modelDesc.getText() +
               "', " + Integer.parseInt(dataSize.getText()) + ", '" +
               notes.getText() + "', empty_blob(), '" + userName +
               "', to_date('" + dateStr + "', 'yyyy/mm/dd hh24:mi:ss'))";

      stmt.execute(qryStr);
      // Execute the query and lock the BLOB row
      qryStr = "select model_object from model_master where model_name = '" +
               modelName.getText() + "' for update";
      ResultSet rset = stmt.executeQuery(qryStr);
      rset.next();
      // Get the BLOB locator
      blob = ((OracleResultSet)rset).getBLOB(1);
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
      return;
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
          // get bin count.
          if (trainSet.getValueAt(i,2).toString().length() == 0 ||
            trainSet.getValueAt(i,0).toString().equals(NOTHING)) {
            sb = sb + "0, ";
          }
          else {
            sb = sb + Integer.parseInt((trainSet.getValueAt(i,2)).toString()) + ", ";
          }
          // get IO flag
          sb = sb + "'" + trainSet.getValueAt(i,3) + "')";
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
      return;
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
      return;
    }
  }
  }

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SaveModelToDB";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "DBConnection";
			case 1:
				return "PredictionModel";
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
