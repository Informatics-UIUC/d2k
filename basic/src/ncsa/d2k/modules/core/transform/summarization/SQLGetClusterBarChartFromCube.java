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
package ncsa.d2k.modules.core.transform.summarization;


import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.d2k.modules.core.io.sql.BrowseTables;
import ncsa.d2k.modules.core.io.sql.BrowseTablesView;
import ncsa.d2k.modules.core.io.sql.ConnectionWrapper;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.modules.core.transform.attribute.SQLCodeBook;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;


/**
 * <p>Title: SQLGetClusterBarChartFromCube</p>
 *
 * <p>Description: Extract rule association from a cube table</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: NCSA ALG</p>
 *
 * @author  Dora Cai
 * @version 1.0
 */
public class SQLGetClusterBarChartFromCube extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** constant for empty string */
   static String NOTHING = "";

   /** constant for blank */
   static private String BLANK = "NoCodeBook";

   /** constant for filled */
   static private String FILLED = "WithCodeBook";

   //~ Instance fields *********************************************************

   /** Set this property to true if you wish to use a code book. */
   private boolean book;

   /** Supply a code book if 'Use Code Book' is set to true. */
   private String codeBook;

   /** selected attributes */
   private String[] selectedAttributes;

   /** SQLCodeBook */
   private SQLCodeBook aBook;

   /** button */
   private JButton bookBrowseBtn;

   /** label */
   private JLabel bookLabel;

   /** text field to enter book name */
   private JTextField bookName;

   /** browse table widget */
   private BrowseTables bt;

   /** browse tables view */
   private BrowseTablesView btw;

   /** button  */
   private JButton cancelBtn;

   /** layout manager */
   private CardLayout codeBookLayout;

   /** panel */
   private JPanel codeBookPanel;

   /** variables for codebook. */
   private TableImpl codeTable;

   /** column count */
   private int colCnt;

   /** connection */
   private Connection con;

   /** cube. */
   private String cube;

   /** field to enter cube table name. */
   private JTextField cubeTableName;

   /** connection wrapper */
   private ConnectionWrapper cw;

   /** data. */
   private MutableTableImpl data;

   /** just a button */
   private JButton displayBtn;

   /** feature index */
   private int featureIdx = -1;

   /** list of fields */
   private JList possibleFields;

   /** list model for possibleFields */
   private DefaultListModel possibleModel;

   /** list of selected fields */
   private JList selectedFields;

   /** list model for selectedFields */
   private DefaultListModel selectedModel;

   /** just a checkbox */
   private Checkbox useCodeBook;

   //~ Constructors ************************************************************

   /**
    * Creates a new SQLGetClusterBarChartFromCube object.
    */
   public SQLGetClusterBarChartFromCube() { }

   //~ Methods *****************************************************************

   /**
    * Close the view and end execution.
    */
   protected void closeIt() { executionManager.moduleDone(this); }


   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new GetStatView(); }

   /**
    * connect to a database and retrieve the list of available book code tables.
    */
   protected void doBookBrowse() {
      Vector v = new Vector();

      try {
         DatabaseMetaData metadata = null;
         con = cw.getConnection();
         metadata = con.getMetaData();

         String[] types = { "TABLE" };
         ResultSet tableNames =
            metadata.getTables(null, "%", "%_CODEBOOK%", types);

         while (tableNames.next()) {
            String aTable = tableNames.getString("TABLE_NAME");
            v.addElement(aTable);
         }

         if (v.size() <= 0) {
            JOptionPane.showMessageDialog(null,
                                          "There is no any code book in the database",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("There is no any code book in the database.");
         } else {
            bt = new BrowseTables(cw, v);
            btw = new BrowseTablesView(bt, v);
            btw.setSize(250, 200);
            btw.setTitle("Available Code Book Tables");
            btw.setLocation(200, 250);
            btw.setVisible(true);
            btw.addWindowListener(new WindowAdapter() {
                  public void windowClosed(WindowEvent e) {
                     bookName.setText(btw.getChosenRow());
                  }
               });
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null,
                                       e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error occurred in doBookBrowse.");
      }
   } // end method doBookBrowse

   /**
    * connect to a database and retrieve the column list of the cube table.
    */
   protected void doColumnBrowse() {
      possibleModel.removeAllElements();
      selectedModel.removeAllElements();

      DatabaseMetaData metadata = null;

      try {
         con = cw.getConnection();
         metadata = con.getMetaData();

         ResultSet columns =
            metadata.getColumns(null, "%", cubeTableName.getText(), "%");

         while (columns.next()) {
            String colName = columns.getString("COLUMN_NAME");

            if (!colName.equals("SET_SIZE") && !colName.equals("CNT")) {
               possibleModel.addElement(colName);
            }
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null,
                                       e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error occurred in doColumnBrowse.");
      }
   } // end method doColumnBrowse

   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   protected String[] getFieldNameMapping() { return null; }

   /**
    * Replace descriptions in origTbl with those in aBook
    *
    * @param origTbl original table
    */
   protected void replaceCode(TableImpl origTbl) {
      MutableTable newTbl = (MutableTable) origTbl;

      for (int rowIdx = 0; rowIdx < newTbl.getNumRows(); rowIdx++) {

         for (int colIdx = 0; colIdx < 2; colIdx++) {
            String colName = newTbl.getColumnLabel(colIdx);
            String codeVal = newTbl.getString(rowIdx, colIdx);
            String textVal = aBook.getDescription(colName + "=" + codeVal);

            if (textVal != null) {
               newTbl.setString(textVal, rowIdx, colIdx);
            }
         }
      }

      origTbl = (TableImpl) newTbl;
   }

   /**
    * creates a data table for the selected attributes in the input database
    * table.
    *
    * @param  tableName - name of table in data base
    * @param  selected  - selected attributes in the table
    *
    * @return - true if successfule false if fails.
    *
    * @throws Exception when something goes wrong
    */
   public boolean create1ItemDataTable(String tableName, String[] selected)
      throws Exception {
      int rowCnt = 0;

      try {
         con = cw.getConnection();
         // only pick up the cube records that have values in the selected
         // columns and is a 1-item set


         // String cntQry = new String("select count(*) from " +
         // cubeTableName.getText() +                         " where ");
         String cntQry =
            new String("select count(*) from " + tableName +
                       " where ");


         for (int idx = 0; idx < selected.length; /* selectedModel.size()*/ idx++) {

            if (idx == 0) {
               cntQry = cntQry + selected[idx] + " is not null ";
            } else {
               cntQry = cntQry + " and " + selected[idx] + " is not null ";
            }
         }

         cntQry = cntQry + " AND set_size = 2";

         Statement cntStmt = con.createStatement();
         ResultSet cntSet = cntStmt.executeQuery(cntQry);

         while (cntSet.next()) {
            rowCnt = cntSet.getInt(1);
         }

         cntStmt.close();
      } catch (Exception e) {

         if (!this.getSuppressGui()) {
            JOptionPane.showMessageDialog(null,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
         } else {
            throw e;
         }

         System.out.println("Error occurred in create1ItemDataTable.");

         return false;
      }

      // data table will contains one of the columns the user selected and one
      // extra column for count
      Column[] cols = new Column[3];
      cols[0] = new ObjectColumn(rowCnt);
      cols[0].setLabel(selected[0]);
      cols[1] = new ObjectColumn(rowCnt);
      cols[1].setLabel(selected[1]);
      cols[2] = new ObjectColumn(rowCnt);
      cols[2].setLabel("COUNT");
      data = new MutableTableImpl(cols);

      try {
         con = cw.getConnection();

         String dataQry = new String("select ");

         for (
              int idx = 0;
                 idx < selected.length /*selectedModel.size()*/;
                 idx++) {
            dataQry = dataQry + selected[idx] + ", ";
         }

         dataQry =
            dataQry + "CNT from " + tableName /*cubeTableName.getText()*/ +
            " where ";

         for (
              int idx = 0;
                 idx < selected.length /*selectedModel.size()*/;
                 idx++) {

            if (idx == 0) {
               dataQry = dataQry + selected[idx] + " is not null ";
            } else {
               dataQry = dataQry + " and " + selected[idx] + " is not null ";
            }
         }

         dataQry = dataQry + " AND set_size = 2";

         Statement dataStmt = con.createStatement();
         ResultSet dataSet = dataStmt.executeQuery(dataQry);
         int rowIdx = 0;

         while (dataSet.next()) {
            data.setString(dataSet.getString(1), rowIdx, 0);
            data.setString(dataSet.getString(2), rowIdx, 1);
            data.setInt(dataSet.getInt(3), rowIdx, 2);
            rowIdx++;
         }

         dataStmt.close();

         return true;
      } catch (Exception e) {

         if (!this.getSuppressGui()) {
            JOptionPane.showMessageDialog(null,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
         } else {
            throw e;
         }

         System.out.println("Error occurred in createDataTable.");

         return false;
      }
   } // end method create1ItemDataTable


   /**
    * This method is provided here for compatability only, it must be overriden
    * by HeadlessUIModules.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      cw = (ConnectionWrapper) pullInput(0);

      String tableName = (String) pullInput(1);

      if (book && (codeBook == null || codeBook.length() == 0)) {
         throw new Exception(getAlias() +
                             ": Code Book properties were not configured correctly. " +
                             "You must choose a code book or set 'Use Code Book' to false. " +
                             "You may configure these properties using the properties editor " +
                             "or via running this itinerary with GUI first.");
      }


      if (selectedAttributes == null) {
         throw new Exception(getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      if (selectedAttributes.length != 2) {
         throw new Exception(getAlias() +
                             " has been configured incorrectly. Before running headless, run with the gui and configure the parameters.");
      }


      if (tableName == null || tableName.length() == 0) {
         throw new Exception(getAlias() +
                             ": Illegal table name on input port 2.");
      }

      if (!(tableName.indexOf("_CUBE") >= 0)) {
         throw new Exception(getAlias() +
                             ": The input table must be a cubed table, and must have the string '_CUBE' in its name");
      }

      // verifying that tableName is in the data base
      if (!StaticMethods.getAvailableTables(cw).containsKey(tableName)) {
         throw new Exception(getAlias() + ": Table named " + tableName +
                             " was not found in the database.");
      }

      con = cw.getConnection();

      DatabaseMetaData metadata = con.getMetaData();

      ResultSet columns = metadata.getColumns(null, "%", tableName,
                                              "%");
      Vector columnsVector = new Vector();
      int counter = 0;

      while (columns.next()) {
         String colName = columns.getString("COLUMN_NAME");

         if (!colName.equals("SET_SIZE") && !colName.equals("CNT")) {
            columnsVector.add(counter, colName);
            counter++;
         } // if
      } // while


      String[] targetAttributes =
         StaticMethods.getIntersection(selectedAttributes, columnsVector);

      if (targetAttributes.length < selectedAttributes.length) {
         throw new Exception(getAlias() +
                             ": Some of the configured attributes were not found " +
                             "in the database table " + tableName +
                             ". Please reconfigure this module via a GUI run so it can run Headless.");
      }

      if (book) {
         aBook = new SQLCodeBook(cw, codeBook);
         codeTable = aBook.codeBook;
      }

      if (create1ItemDataTable(tableName, targetAttributes)) {

         if (book) {
            replaceCode(data);
         }

         pushOutput(data, 0);
      }


   } // doit

   /**
    * Get book.  Set this property to true if you wish to use a code book
    *
    * @return book
    */
   public boolean getBook() { return book; }

   /**
    * Get codeBook.  Supply a code book if 'Use Code Book' is set to true
    *
    * @return codeBook
    */
   public String getCodeBook() { return codeBook; }

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
            return "The name of the cube table which stores the data statistics.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Database Connection";

         case 1:
            return "Cube Table";

         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
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
      s +=
         "This module displays a cluster bar chart for data in a cube table. </p>";
      s += "<p> Detailed Description: ";
      s +=
         "This module first makes a connection to a database and retrieves the ";
      s +=
         "data from two user-selected columns in a cube table, then displays the counts ";
      s +=
         "using a cluster bar chart. The displayed data not only can be labeled in predefined ";
      s +=
         "codes, but also in detailed descriptions by choosing the 'Use Code Book' ";
      s += "option and specifying a code book for use. ";
      s += "<p> Restrictions: ";
      s +=
         "We currently only support Oracle, SQLServer, DB2 and MySql databases.";

      return s;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SQLGetClusterBarChartFromCube"; }

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
            return "A table to visualize.";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Table";

         default:
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }

   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // so that "WindowName" property is invisible
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] = super.supressDescription;
      pds[1] =
         new PropertyDescription("book", "Use Code Book",
                                 "Set this property to true if you wish to use a code book");
      pds[2] =
         new PropertyDescription("codeBook", "Code Book",
                                 "Supply a code book if 'Use Code Book' is set to true");

      return pds;

   }

   /**
    * Get the selected attributes
    *
    * @return selected attributes
    */
   public Object[] getSelectedAttributes() { return selectedAttributes; }

   /**
    * Set book.
    *
    * @param val new book
    */
   public void setBook(boolean val) { book = val; }

   /**
    * Set codeBook
    *
    * @param book new codeBook
    */
   public void setCodeBook(String book) { codeBook = book; }

   /**
    * Get the selected attributes
    *
    * @return selected attributes
    */
   public void setSelectedAttributes(Object[] atts) {
      selectedAttributes = new String[atts.length];

      for (int i = 0; i < atts.length; i++) {
         selectedAttributes[i] = (String) atts[i];
      }
   }

   //~ Inner Classes ***********************************************************

   /**
    * User view.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   public class GetStatView extends JUserPane implements ActionListener,
                                                         ItemListener {

      /**
       * Invoked when an action occurs.
       *
       * @param e action event
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == displayBtn) {
            Object[] values = selectedModel.toArray();

            // headless conversion support
            setBook(useCodeBook.getState());
            setCodeBook(bookName.getText().toString());
            setSelectedAttributes(values);
            // headless conversion support


            String[] retVal = new String[values.length];

            if (useCodeBook.getState() && bookName.getText().length() <= 0) {

               // The user has not chosen a code book yet
               JOptionPane.showMessageDialog(null,
                                             "You must choose a code book or deselect 'Use Code Book'.",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);
               System.out.println("There is no code book selected.");
            } else if (retVal.length != 2) {
               JOptionPane.showMessageDialog(null,
                                             "You must choose two features from the selected table.",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);
               System.out.println(retVal.length +
                                  " are selected. You must choose two features.");
            } else if (cubeTableName.getText().length() <= 0) { // The user has not chosen a table yet
               JOptionPane.showMessageDialog(null,
                                             "There is no cube table selected.",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);
               System.out.println("There is no table selected.");
            } else if (
                       cubeTableName.getText().toString().indexOf("_CUBE") <
                          0) {
               JOptionPane.showMessageDialog(null,
                                             "To display a chart, you must select a cube table rather than a data table.",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);
               System.out.println("A cube table is selected instead of a data table.");
            } else if (
                       cubeTableName.getText().length() > 0 &&
                          retVal.length > 0) {

               // if code book is required and the code book is not retrieved
               // yet, then get it
               if (useCodeBook.getState()) {
                  aBook = new SQLCodeBook(cw, bookName.getText().toString());
                  codeTable = aBook.codeBook;
               }

               // vered: added this try catch to bypass compilation error if
               // running this module with gui, exceptions are always handled
               // within createItemDataTable
               try {

                  if (
                      create1ItemDataTable(cubeTableName.getText(),
                                              (String[]) getSelectedAttributes())) {

                     if (useCodeBook.getState()) {
                        replaceCode(data);
                     }

                     pushOutput(data, 0);
                  }

               } // try
               catch (Exception ex) { }

               closeIt();
            } // end if-else
         } else if (src == cancelBtn) {
            cubeTableName.setText(NOTHING);
            closeIt();
         } else if (src == bookBrowseBtn) {
            doBookBrowse();
         }
      } // end method actionPerformed


      /**
       * lay out the GUI.
       */
      public void doGUI() {
         removeAll();
         // cw = (ConnectionWrapper)pullInput (0);

         selectedFields = new JList();
         possibleFields = new JList();

         JButton add = new JButton("Add");
         JButton remove = new JButton("Remove");
         possibleModel = new DefaultListModel();
         selectedModel = new DefaultListModel();

         // Panel to hold outline panels
         JPanel getStatPanel = new JPanel();
         getStatPanel.setLayout(new GridBagLayout());

         // panel for table name
         JPanel options = new JPanel();
         options.setLayout(new GridBagLayout());
         Constrain.setConstraints(options, new JLabel("Table Name"),
                                  0, 0, 5, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(options, cubeTableName = new JTextField(10),
                                  5, 0, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 2, 1);

         // cubeTableName.setText((String)pullInput(1));
         cubeTableName.setText(cube);
         cubeTableName.setEditable(false);
         doColumnBrowse();

         JPanel buttons = new JPanel();
         buttons.setLayout(new GridLayout(6, 1));
         buttons.add(add);
         buttons.add(remove);

         JPanel b1 = new JPanel();
         b1.add(buttons);

         // the add button moves stuff from the possible list
         // to the selected list.
         add.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = possibleFields.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {
                     possibleModel.removeElement(sel[i]);
                     selectedModel.addElement(sel[i]);
                  }
               }
            });

         // the remove button moves stuff from the selected list
         // to the possible list.
         remove.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  Object[] sel = selectedFields.getSelectedValues();

                  for (int i = 0; i < sel.length; i++) {
                     selectedModel.removeElement(sel[i]);
                     possibleModel.addElement(sel[i]);
                  }
               }
            });

         JPanel featureArea = new JPanel();
         featureArea.setLayout(new BorderLayout());

         possibleFields.setFixedCellWidth(150);

         // possibleFields.setFixedCellHeight(100);
         selectedFields.setFixedCellWidth(150);

         // selectedFields.setFixedCellHeight(100);
         selectedFields.setModel(selectedModel);
         possibleFields.setModel(possibleModel);

         JScrollPane jsp = new JScrollPane(possibleFields);
         jsp.setColumnHeaderView(new JLabel("Possible Fields"));

         JScrollPane jsp1 = new JScrollPane(selectedFields);
         jsp1.setColumnHeaderView(new JLabel("Selected Fields"));

         featureArea.add(b1, BorderLayout.CENTER);
         featureArea.add(jsp, BorderLayout.WEST);
         featureArea.add(jsp1, BorderLayout.EAST);

         JPanel options2 = new JPanel();
         options2.setLayout(new GridBagLayout());
         useCodeBook = new Checkbox("Use Code Book", null, false);
         useCodeBook.addItemListener(this);
         Constrain.setConstraints(options2, useCodeBook,
                                  0, 0, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.EAST, 1, 1);

         codeBookPanel = new JPanel();
         codeBookLayout = new CardLayout();
         codeBookPanel.setLayout(codeBookLayout);

         JPanel filledPanel = new JPanel();
         filledPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(filledPanel,
                                  bookLabel = new JLabel("Code Book Name"),
                                  0, 0, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(filledPanel, bookName = new JTextField(10),
                                  5, 0, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         bookName.setText(NOTHING);
         bookName.addActionListener(this);
         Constrain.setConstraints(filledPanel,
                                  bookBrowseBtn = new JButton("Browse"),
                                  15, 0, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);
         bookBrowseBtn.addActionListener(this);
         codeBookPanel.add(filledPanel, FILLED);

         JPanel blankPanel = new JPanel();
         blankPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(blankPanel, new JPanel(),
                                  0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         codeBookPanel.add(blankPanel, BLANK);
         codeBookLayout.show(codeBookPanel, BLANK);

         Constrain.setConstraints(options2, codeBookPanel,
                                  5, 0, 15, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);

         /* Add the outline panel to getRulePanel */
         Constrain.setConstraints(getStatPanel, options,
                                  0, 0, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(getStatPanel, featureArea,
                                  0, 1, 5, 5, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(getStatPanel, options2,
                                  0, 7, 5, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(getStatPanel,
                                  cancelBtn = new JButton(" Abort "),
                                  2, 8, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 1, 1);
         cancelBtn.addActionListener(this);
         Constrain.setConstraints(getStatPanel,
                                  displayBtn = new JButton("Display"),
                                  3, 8, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);
         displayBtn.addActionListener(this);
         setLayout(new BorderLayout());
         add(getStatPanel, BorderLayout.NORTH);
      } // end method doGUI

      /**
       * If the <code>preferredSize</code> has been set to a
       * non-<code>null</code> value just returns it. If the UI delegate's
       * <code>getPreferredSize</code> method returns a non <code>null</code>
       * value then return that; otherwise defer to the component's layout
       * manager.
       *
       * @return the value of the <code>preferredSize</code> property
       *
       * @see    #setPreferredSize
       * @see    javax.swing.plaf.ComponentUI
       */
      public Dimension getPreferredSize() { return new Dimension(450, 300); }

      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) { removeAll(); }

      /**
       * Invoked when an item has been selected or deselected by the user. The
       * code written for this method performs the operations that need to occur
       * when an item is selected (or deselected).
       *
       * @param e item event
       */
      public void itemStateChanged(ItemEvent e) {

         if (e.getSource() == useCodeBook) {

            if (useCodeBook.getState()) {
               codeBookLayout.show(codeBookPanel, FILLED);
               bookName.setText(NOTHING);
            } else {
               codeBookLayout.show(codeBookPanel, BLANK);
               bookName.setText(NOTHING);
            }
         }
      }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param input The object that has been input.
       * @param index The index of the module input that been received.
       */
      public void setInput(Object input, int index) {

         if (index == 0) {
            cw = (ConnectionWrapper) input;
         } else if (index == 1) {
            cube = (String) input;
            doGUI();
            cubeTableName.setText((String) input);
         }
      }
   } // end class GetStatView

} // end class SQLGetClusterBarChartFromCube
