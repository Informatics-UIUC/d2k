/* 
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 * 
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 * 
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: SQLChooseAttributes</p>
 *
 * <p>Description: Choose input and output fields from a database table, then
 * create an ExampleTableImpl to keep the meta data</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: NCSA ALG</p>
 *
 * @author Dora Cai
 *
 * @todo behavior of this module is different than ChooseAttributes. which puts
 *       no restriction on the selection that the user makes. shouldn't these
 *       modules be aligned?
 */


import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLChooseAttributes extends HeadlessUIModule {

	/** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4648503544541416221L;

   //~ Instance fields *********************************************************

   /** Description of field abort. */
   private JButton abort;

   /** Description of field done. */
   private JButton done;

   /** Description of field et. */
   private ExampleTable et;

   /** Description of field inputFeatures. */
   private int[] inputFeatures;

   /** Description of field inputLabel. */
   private JLabel inputLabel;

   /** Description of field inputList. */
   private JList inputList;

   /** Description of field inputToIndexMap. */
   private HashMap inputToIndexMap;

   /** Description of field outputFeatures. */
   private int[] outputFeatures;

   /** Description of field outputLabel. */
   private JLabel outputLabel;

   /** Description of field outputList. */
   private JList outputList;

   /** Description of field outputToIndexMap. */
   private HashMap outputToIndexMap;

   /** Description of field selectedInput. */
   private int[] selectedInput;

   /**
    * headless conversion selected input and output names are the names of
    * columns selected by the user through the gui, as input and output, and
    * were saved as properties of this module.
    */
   private String[] selectedInputNames;

   /** Description of field selectedOutput. */
   private int[] selectedOutput;

   /** Description of field selectedOutputNames. */
   private String[] selectedOutputNames;

   /** Description of field tableBrowseBtn. */
   private JButton tableBrowseBtn;

   /** Description of field tableName. */
   private JTextField tableName;

   /** ArrayList for column names. */
   ArrayList colNames;

   /** ArrayList for column types. */
   ArrayList colTypes;

   /** Description of field con. */
   Connection con;

   /** Description of field cw. */
   ConnectionWrapper cw;

   /** Description of field msgBoard. */
   JOptionPane msgBoard = new JOptionPane();

   /** Description of field tblName. */
   String tblName;

   //~ Constructors ************************************************************

   /**
    * Creates a new SQLChooseAttributes object.
    */
   public SQLChooseAttributes() { }

   //~ Methods *****************************************************************

   /**
    * create an ExampleTable object to hold the meta information.
    *
    * @return an object of Example table
    */
   private ExampleTable createMetaTable() {
      int selectedColumn = 0;
      int numRows = 0;

      // only include the selected columns in the example table
      Column[] cols = new Column[inputFeatures.length + outputFeatures.length];
      selectedColumn = 0;

      int inputIndex = 0;
      int outputIndex = 0;
/*
      boolean[] missingValue =
         new boolean[inputFeatures.length + outputFeatures.length];
*/
      for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {

         if (
             isInList(colIdx, inputFeatures) &&
                isInList(colIdx, outputFeatures)) {
            JOptionPane.showMessageDialog(msgBoard,
                                          "A column can be either an input or an output, but not both.",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
         }

         if (
             isInList(colIdx, inputFeatures) ||
                isInList(colIdx, outputFeatures)) {
            cols[selectedColumn] = new ObjectColumn();
            cols[selectedColumn].setLabel(colNames.get(colIdx).toString());

            // data type may be in uppercase or lowercase
            if (ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString())) {
               cols[selectedColumn].setIsScalar(true);
            } else {
               cols[selectedColumn].setIsScalar(false);
            }

            if (isInList(colIdx, inputFeatures)) {
               selectedInput[inputIndex] = selectedColumn;
               inputIndex++;
            } else if (isInList(colIdx, outputFeatures)) {
               selectedOutput[outputIndex] = selectedColumn;
               outputIndex++;
            }

            selectedColumn++;
         } 
      } 

      // create an Table to hold the meta data
      MutableTableImpl table = new MutableTableImpl(cols);

      // get the count of the rows that have class labels
      try {
         con = cw.getConnection();

         String countQry =
            new String("select count(*) from " + tableName.getText() +
                       " where " + outputList.getSelectedValue().toString() +
                       " is not null");
         Statement countStmt = con.createStatement();
         ResultSet countSet = countStmt.executeQuery(countQry);
         countSet.next();
         numRows = countSet.getInt(1);
      } catch (Exception e) {
         JOptionPane.showMessageDialog(msgBoard,
                                       e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error occurred in createExampleTable. " + e);
      }

      table.addRows(numRows);

      for (int colIdx = 0; colIdx < selectedColumn; colIdx++) {

         if (cols[colIdx].getIsScalar()) {
            table.setColumnIsScalar(true, colIdx);
            table.setColumnIsNominal(false, colIdx);
         } else {
            table.setColumnIsScalar(false, colIdx);
            table.setColumnIsNominal(true, colIdx);
         }
      }

      ExampleTable et = table.toExampleTable();
      et.setInputFeatures(selectedInput);
      et.setOutputFeatures(selectedOutput);

      return et;
   }

   /**
    * Method isInList().
    * return true when aColumn is in the columns.
    *
    * @param  aColumn the index of the column
    * @param  columns the list of indexes of the columns
    *
    * @return true or false
    */
   private boolean isInList(int aColumn, int[] columns) {

      for (int colIdx = 0; colIdx < columns.length; colIdx++) {

         if (aColumn == columns[colIdx]) {
            return true;
         }
      }

      return false;
   }


/*
**
    //
    // print ExampleTableImpl for debug.
    //
   private void printETInfo() {
      int[] input;
      int[] output;
      input = et.getInputFeatures();
      System.out.println("input feature list: ");

      for (int i = 0; i < input.length; i++) {
         System.out.print(input[i] + ", ");
      }

      System.out.println(" ");
      output = et.getOutputFeatures();
      System.out.println("output feature list: ");
      System.out.println(output[0]);
      System.out.println(" ");
      // int numRows = et.getNumEntries();
      int numRows = et.getNumRows();
      System.out.println("numRows is " + numRows);

      System.out.println("data type list: ");

      for (int j = 0; j < et.getNumColumns(); j++) {
         System.out.println("col " + j + " type: " + et.isColumnScalar(j));
      }
   }
**
*/
   
   /**
    * Method closeIt() closes inputList, outputList, and calls 
    * 		executionManager.moduleDone() for this module.
    */
   protected void closeIt() {
      inputList.removeAll();
      outputList.removeAll();
      executionManager.moduleDone(this);
   }

   /**
    * Create the UserView object for this module-view combination.
    *
    * @return The UserView associated with this module.
    */
   protected UserView createUserView() { 
	   return new GetFieldsView(); 
   }

   /**
    * Method getFieldNameMapping().
    *
    * @return <code>java.lang.String[]</code> FieldNameMappings.
    */
   protected String[] getFieldNameMapping() { 
	   return null; 
   }


   /**
    * Displays GUIS to allow selection of Attributes interactively.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      cw = (ConnectionWrapper) pullInput(0);

      String _table = (String) pullInput(1);

      // validating table's name
      if (_table == null || _table.length() == 0) {
         throw new Exception(getAlias() +
                             ": the input Data Table Name is invalid.");
      }

      // validating that inputs and outputs were selected on previous run
      if (
          selectedInputNames == null ||
             selectedOutputNames == null ||
             selectedOutputNames.length == 0 ||
             selectedInputNames.length == 0) {
         throw new Exception(this.getAlias() +
                             " has not been configured correctly. Before running headless, run with the gui and configure the parameters.");
      }

      con = cw.getConnection();
      DatabaseMetaData metadata = con.getMetaData();
      ResultSet columns = metadata.getColumns(null, "%", _table, "%");

      // will hold column name <-> column index
      HashMap availableColumnMap = new HashMap();

      // will hold column index <-> column name
      HashMap Id2ColumnNameMap = new HashMap();

      // will hold column index <-> column type.
      HashMap columTypes = new HashMap();


      // populating the hash maps.
      int counter = 0; // counter is the current column id.

      while (columns.next()) {
         String columnName = columns.getString("COLUMN_NAME");
         // String columnType = columns.getString("TYPE_NAME");
         availableColumnMap.put(columnName, new Integer(counter));
         Id2ColumnNameMap.put(new Integer(counter), columnName);
         columTypes.put(new Integer(counter), columnName);
         counter++;
      } 

      if (counter == 0) {
         throw new Exception(getAlias() + ": Table " + _table +
                             " is either not in the database or has no columns in it");
      }

      inputFeatures =
         StaticMethods.getIntersectIds(selectedInputNames, availableColumnMap);

      if (inputFeatures.length < selectedInputNames.length) {
         throw new Exception(getAlias() +
                             ": Some of the configured input names " +
                             "are not in the selected table. Cannot proceed without any valid input names." +
                             " Please reconfigure the module so it can run headless.");
      }

      /* selectedInput holds the input columns ids, of the output example
       * table.*/
      selectedInput = new int[inputFeatures.length];


      // since there can be only one output feature...      outputFeatures =
      // getTargetColumns(availableColumnMap, selectedOutputNames);
      /* outputfeatures holds the target output column id, as it is in the
       * table in the database.*/
      outputFeatures = new int[1];

      /* selectedOutput holds the output columns ids, of the output example
       * table.*/
      selectedOutput = new int[outputFeatures.length];

      String finalSelectedOutput = null;

      if (availableColumnMap.containsKey(selectedOutputNames[0])) {
         outputFeatures[0] =
            ((Integer) availableColumnMap.get(selectedOutputNames[0]))
               .intValue();
         finalSelectedOutput = selectedOutputNames[0];
      } else {
         throw new Exception(getAlias() + ": The selected output " +
                             "feature is not available in the data base table. cannot proceed.\n" +
                             " Please reconfigure the module so it can run headless.");
      }

      int selectedColumn = 0; // current column in the output example table.
      int numRows = 0; // number of rows in the output example table

      // only include the selected columns in the example table
      // will hold all the columns in the output example table.
      Column[] cols = new Column[inputFeatures.length + outputFeatures.length];

      // Iterator it = availableColumnMap.keySet().iterator();
      String currentColName; // name of currently created object column of the
                             // output exmaple table

      // creating the input columns.
      for (int i = 0; i < inputFeatures.length; i++) {

         currentColName =
            (String) Id2ColumnNameMap.get(new Integer(inputFeatures[i]));
         cols[selectedColumn] = new ObjectColumn();
         cols[selectedColumn].setLabel(currentColName);

         String type = (String) columTypes.get(new Integer(inputFeatures[i]));

         if (ColumnTypes.isEqualNumeric(type)) {
            cols[selectedColumn].setIsScalar(true);
         } else {
            cols[selectedColumn].setIsScalar(false);
         }

         selectedInput[i] = selectedColumn;
         selectedColumn++;

      } 

      // creating the output columns.
      for (int i = 0; i < outputFeatures.length; i++) {

         currentColName =
            (String) Id2ColumnNameMap.get(new Integer(outputFeatures[i]));
         cols[selectedColumn] = new ObjectColumn();
         cols[selectedColumn].setLabel(currentColName);

         String type = (String) columTypes.get(new Integer(outputFeatures[i]));

         if (ColumnTypes.isEqualNumeric(type)) {
            cols[selectedColumn].setIsScalar(true);
         } else {
            cols[selectedColumn].setIsScalar(false);
         }

         selectedOutput[i] = selectedColumn;
         selectedColumn++;
      } 


      // create a Table to hold the meta data
      MutableTableImpl table = new MutableTableImpl(cols);

      // get the count of the rows that have class labels
      try {
         // con = cw.getConnection();

         /*this handles only single output column. not multy labeling.*/
         String countQry =
            new String("select count(*) from " + _table +
                       " where " + finalSelectedOutput + " is not null");
         Statement countStmt = con.createStatement();
         ResultSet countSet = countStmt.executeQuery(countQry);
         countSet.next();
         numRows = countSet.getInt(1);
      } catch (Exception e) {

         System.out.println(getAlias() +
                            ": Error occurred in createExampleTable.");
         throw e;
      }
      // adding rows.
      table.addRows(numRows);

      for (int colIdx = 0; colIdx < selectedColumn; colIdx++) {

         if (cols[colIdx].getIsScalar()) {
            table.setColumnIsScalar(true, colIdx);
            table.setColumnIsNominal(false, colIdx);
         } else {
            table.setColumnIsScalar(false, colIdx);
            table.setColumnIsNominal(true, colIdx);
         }

         int missing = 0;

         try {

            // con = cw.getConnection();
            String countQry =
               new String("select count(*) from " + _table +
                          " where " + table.getColumn(colIdx).getLabel() +
                          " is null");
            Statement countStmt = con.createStatement();
            ResultSet countSet = countStmt.executeQuery(countQry);
            countSet.next();
            missing = countSet.getInt(1);
         } catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occurred in createExampleTable. " + e);
         }

         if (missing > 0) { // there are missing values in this column, set the
                            // first element to missing
            table.getColumn(colIdx).setValueToMissing(true, 0);
         }

      }

      // converting to example table and setting the input and output columns.
      ExampleTable et = table.toExampleTable();
      et.setInputFeatures(selectedInput);
      et.setOutputFeatures(selectedOutput);

      pushOutput(_table, 0);
      pushOutput(et, 1);

   } // doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {
      switch (i) {
         case 0:
            return "JDBC data source to make database connection.";
         case 1:
            return "The name of the cube table.";
         default:
            return "No such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {
      switch (i) {
         case 0:
            return "Database Connection";
         case 1:
            return "Data Table Name";
         default:
            return "NoInput";
      }
   }

   /**
    * Method getInputTypes().
    *
    * @return <code>java.lang.String[]</code> describes InputTypes.
    */
   public String[] getInputTypes() {
      String[] types =
      { "ncsa.d2k.modules.core.io.sql.ConnectionWrapper", "java.lang.String" };
      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p> Overview: ";
      s += "This module allows a user to choose input and output attributes ";
      s += "from a database table. </p>";
      s += "<p> Detailed Description: ";
      s += "This module first connects to a database, retrieves the list of attributes ";
      s += "for a selected table, and then allows a user to choose the input and output ";
      s += "attributes from the list. The user must choose at least one input and one ";
      s += "attribute and <u>at most</u> one output attribute. ";
      s += "Based on user's selection, this module creates a meta ";
      s += "table to store the meta information (such as attribute name, data type, ";
      s += "and number of rows in the data set), and pass on the information to the ";
      s += "next module. The meta table created by this module  does not contain the ";
      s += "real data. The real data can be retrieved from the database table ";
      s += "by other modules. </p>";
      s += "<p> Restrictions: ";
      s += "We currently only support Oracle, SQLServer, DB2 and MySql databases. </p> ";

      return s;
   } 

   /**
    * Method getModuleName().
    *
    * @return <code>String</code> describes module name.
    */
   public String getModuleName() { 
	   return "SQL Choose Attributes"; 
   }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
      switch (i) {
         case 0:
            return "The name of the database table.";
         case 1:
            return "The meta table built from the data table.";
         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {
      switch (i) {
         case 0:
            return "Selected Table";
         case 1:
            return "Meta Data Example Table";
         default:
            return "NoOutput";
      }
   }

   /**
    * Method getOutputTypes().
    *
    * @return <code>java.lang.String[]</code> of OutputTypes.
    */
   public String[] getOutputTypes() {
      String[] types =
      {
         "java.lang.String", "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }

   /**
    * Method getSelectedInputNames().
    *
    * @return <code>java.lang.Object[]</code> of SelectedOutputNames.
    */
   public Object[] getSelectedInputNames() { 
	   return selectedInputNames; 
   }

   /**
    * Method getSelectedOutputNames().
    *
    * @return <code>java.lang.Object[]</code> of SelectedOutputNames.
    */
   public Object[] getSelectedOutputNames() { 
	   return selectedOutputNames; 
   }

   /**
    * Method setSelectedInputNames().
    *
    * @param ins <code>Object[]</code> Array of Object.
    */
   public void setSelectedInputNames(Object[] ins) {
      selectedInputNames = new String[ins.length];

      for (int i = 0; i < ins.length; i++) {
         selectedInputNames[i] = (String) ins[i];
      }
   }

   /**
    * Method setSelectedOutputNames().
    *
    * @param outs <code>Object[]</code> Array of Object.
    */
   public void setSelectedOutputNames(Object[] outs) {
      selectedOutputNames = new String[outs.length];

      for (int i = 0; i < outs.length; i++) {
         selectedOutputNames[i] = (String) outs[i];
      }
   }

   //~ Inner Classes ***********************************************************

   public class GetFieldsView extends JUserPane implements ActionListener {
      /**
       * add all the components.
       */
      private void addComponents() {
         JPanel tableList = new JPanel();
         JPanel back = new JPanel();
         colNames = new ArrayList();
         colTypes = new ArrayList();

         DatabaseMetaData metadata = null;

         tableList.setLayout(new GridBagLayout());
         Constrain.setConstraints(tableList, new JLabel("Table Name"),
                                  0, 0, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);
         Constrain.setConstraints(tableList, tableName = new JTextField(5),
                                  1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 2, 1);
         tableName.setText(tblName);
         tableName.addActionListener(this);
         tableName.setEditable(false);
         Constrain.setConstraints(tableList, new JLabel(" "),
                                  3, 0, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);

         inputList = new JList();
         outputList = new JList();

         inputLabel = new JLabel("Input Columns");
         inputLabel.setHorizontalAlignment(SwingConstants.CENTER);

         outputLabel = new JLabel("Output Columns");
         outputLabel.setHorizontalAlignment(SwingConstants.CENTER);

         try {
            con = cw.getConnection();
            metadata = con.getMetaData();

            String[] names = { "TABLE" };
            ResultSet tableNames =
               metadata.getTables(null, "%", tableName.getText(), names);

            while (tableNames.next()) {
               ResultSet columns =
                  metadata.getColumns(null, "%",
                                      tableNames.getString("TABLE_NAME"), "%");

               while (columns.next()) {
                  String columnName = columns.getString("COLUMN_NAME");
                  String columnType = columns.getString("TYPE_NAME");

                  colNames.add(columnName);
                  colTypes.add(columnType);
               }
            }
         } catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occurred in addComponents.");
         }

         DefaultListModel dlm = new DefaultListModel();

         for (int i = 0; i < colNames.size(); i++) {
            dlm.addElement(colNames.get(i).toString());
         }

         inputList.setModel(dlm);
         dlm = new DefaultListModel();

         for (int i = 0; i < colNames.size(); i++) {
            dlm.addElement(colNames.get(i).toString());
         }

         outputList.setModel(dlm);

         JScrollPane leftScrollPane = new JScrollPane(inputList);
         JScrollPane rightScrollPane = new JScrollPane(outputList);
         orderedLabels();

         back.setLayout(new GridBagLayout());

         Constrain.setConstraints(back, inputLabel, 0, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(back, outputLabel, 1, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(back, leftScrollPane, 0, 1, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(back, rightScrollPane, 1, 1, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);

         JPanel buttons = new JPanel();
         abort = new JButton("Abort");
         done = new JButton("Done");
         abort.addActionListener(this);
         done.addActionListener(this);
         buttons.add(abort);
         buttons.add(done);

         this.add(tableList, BorderLayout.NORTH);
         this.add(back, BorderLayout.CENTER);
         this.add(buttons, BorderLayout.SOUTH);

      }


      private void orderedLabels() {
         inputToIndexMap = new HashMap(colNames.size());
         outputToIndexMap = new HashMap(colNames.size());

         for (int i = 0; i < colNames.size(); i++) {
            inputToIndexMap.put(colNames.get(i).toString(), new Integer(i));
            outputToIndexMap.put(colNames.get(i).toString(), new Integer(i));
         }
      }

      private void setFieldsInTable() {
         Object[] selected = inputList.getSelectedValues();
         inputFeatures = new int[selected.length]; // Store the column index of
                                                   // GUI list for input features
         selectedInput = new int[selected.length]; // Store the column index of
                                                   // Example Table for input
                                                   // features

         for (int i = 0; i < selected.length; i++) {
            String s = (String) selected[i];
            Integer ii = (Integer) inputToIndexMap.get(s);
            inputFeatures[i] = ii.intValue();
         }

         setSelectedInputNames(selected); // healdess conversion support

         selected = outputList.getSelectedValues();
         outputFeatures = new int[selected.length]; // Store the column index
                                                    // of GUI list for output
                                                    // features
         selectedOutput = new int[selected.length]; // Store the column index
                                                    // of Example Table for
                                                    // output features

         for (int i = 0; i < selected.length; i++) {
            String s = (String) selected[i];
            Integer ii = (Integer) outputToIndexMap.get(s);
            outputFeatures[i] = ii.intValue();
         }

         setSelectedOutputNames(selected); // healdess conversion support

      }

      /**
       * Make sure all choices are valid.
       *
       * @return <code>boolean</code>.
       */
      protected boolean checkChoices() {

         if (outputList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this,
                                          "You must select at least one output",
                                          "Error", JOptionPane.ERROR_MESSAGE);

            return false;
         }

         if (outputList.getSelectedIndices().length > 1) {
            JOptionPane.showMessageDialog(this,
                                          "You can only select one output column.",
                                          "Error", JOptionPane.ERROR_MESSAGE);

            return false;
         }

         if (inputList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this,
                                          "You must select at least one input",
                                          "Error", JOptionPane.ERROR_MESSAGE);

            return false;
         }

         return true;
      } // end method checkChoices

      /**
       * listen for ActionEvents.
       *
       * @param e <code>ActionEvent</code>.
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == abort) {
            closeIt();
         } else if (src == done) {

            if (checkChoices()) {
               setFieldsInTable();
               et = createMetaTable();

               if (et != null) {

                  // printETInfo();
                  pushOutput(tableName.getText(), 0);
                  pushOutput(et, 1);
                  closeIt();
               }
            }
         }
      }

      public Dimension getPreferredSize() { return new Dimension(400, 300); }

      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) { }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param input The object that has been input.
       * @param index The index of the module input that been received.
       */
      public void setInput(Object input, int index) {

         if (index == 0) {
            removeAll();
            cw = (ConnectionWrapper) input;
         } else if (index == 1) {
            tblName = (String) input;
            addComponents();
         }
      }
   } 

} 


/**
*
* 12-11-03 Anca added missing value info to the metadata example table
*                               The missing value is always in the first row of the column,
*                               and is there just to mark the column as having missing values
* 01-14-04 Anca - missing value info is present in the SQL table, all null values are
*                               considered missing
*                               
**/
