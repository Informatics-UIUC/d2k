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
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.ExpressionListener;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.io.sql.ConnectionWrapper;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.modules.core.vis.widgets.ExpressionGUI;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import java.awt.CardLayout;
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
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * <code>SQLFilterConstruction</code> is a simple user interface that
 * facilitates the creation of an expression for filtering rows from a database
 * table. (See the documentation for <code>FilterExpression</code> for details
 * on the underlying expression string and its format.)
 *
 * @author  Dora Cai based on gpape's non-database version
 * @version $Revision$, $Date$
 *
 * @todo    The user interface has a problem: At the beginning it shows the
 *          first attribute and its unique values. When changing to another
 *          attribute, the drop down list of the attributes does not change, but
 *          when you add one of this irrelevant values to the expression - it
 *          inserts a relevant value. Sometimes it shows the relevant values of
 *          an attributes but when you add one attribute it, for somewhat
 *          reason, adds its sibling attribute. strange.
 */
public class SQLFilterConstruction extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /** Constant for layout manager. */
   static private transient String scalar = "FilterConstructionINTERNALscalar";

   /** Readable operators to replace. */
   static public final String[] toReplace = { "or", "and", "=", "null" };

   /** operates to replace the readable operators with. */
   static public final String[] replaceWith = { "||", "&&", "==", "NULL" };

   //~ Instance fields *********************************************************

   /** If set, rows with missing values will be included in the result table. */
   private boolean _includeMissingValues = true;

   /** Last expression. */
   private String _lastExpression = "";

   /** ArrayList for column names. */
   private ArrayList colNames;

   /** ArrayList for column types. */
   private ArrayList colTypes;

   /** Connection wrapper. */
   private ConnectionWrapper cw;

   /** The query condition. */
   private String queryCondition;

   /** ExampleTable to keep the meta data. */
   private ExampleTable table;

   /** Name of the table. */
   private String tableName = "";

   //~ Methods *****************************************************************

   /**
    * Creates an ExampleTable object to hold the meta information.
    *
    * @param  tableName Description of parameter tableName
    *
    * @return Example table instance
    */
   private ExampleTable createMetaTable(String tableName) {

      // Build an ArrayList to keep the column name
      colNames = new ArrayList();

      // Build an ArrayList to keep the column type
      colTypes = new ArrayList();

      DatabaseMetaData metadata = null;

      try {
         Connection con = cw.getConnection();
         metadata = con.getMetaData();

         ResultSet columns = metadata.getColumns(null, "%", tableName, "%");

         while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            colNames.add(columnName);
            colTypes.add(columnType);
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null,
                                       "Could not create metadata table.",
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         System.out.println("Error occurred in createMetaTable.");
      }

      Column[] cols = new Column[colNames.size()];

      for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
         cols[colIdx] = new ObjectColumn(1);
         cols[colIdx].setLabel(colNames.get(colIdx).toString());

         if (ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString())) {
            cols[colIdx].setIsScalar(true);
         } else {
            cols[colIdx].setIsScalar(false);
         }
      }

      // Create an Table to hold the meta data
      MutableTableImpl aTable = new MutableTableImpl(cols);

      for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {

         if (cols[colIdx].getIsScalar()) {
            aTable.setColumnIsScalar(true, colIdx);
            aTable.setColumnIsNominal(false, colIdx);
         } else {
            aTable.setColumnIsScalar(false, colIdx);
            aTable.setColumnIsNominal(true, colIdx);
         }
      }

      ExampleTable et = aTable.toExampleTable();

      return et;
   } // end method createMetaTable


   /**
    * Returns the help text.
    *
    * @return Help text as a String
    */
   private String getHelpString() {

      StringBuffer sb = new StringBuffer();

      sb.append("<html><body><h2>SQLFilterConstruction</h2>");
      sb.append("This module allows a user to filter rows from a database table ");
      sb.append("by specifying an appropriate filtering expression.");
      sb.append("<br><br>");
      sb.append("The user can construct an expression for filtering ");
      sb.append("using the lists of columns and operators on the left. ");
      sb.append("It is important that this expression be well-formed: that ");
      sb.append("parentheses match, that column labels or scalars surround ");
      sb.append("operators, and so forth.");
      sb.append("<br><br>");
      sb.append("Column names may not contain spaces or the following ");
      sb.append("characters: =, !, <, >, |, &. You may specify nominal ");
      sb.append("values from columns; i.e., ColumnA != 'value', but those ");
      sb.append("values must be enclosed in tick marks (single quotes) so ");
      sb.append("as to distinguish them from column labels.");
      sb.append("</body></html>");

      return sb.toString();

   }

   /**
    * Returns true if <code>ch</code> is white space.
    *
    * @param  ch Character to be observed
    *
    * @return True if <code>ch</code> is white space.
    */
   private boolean isWhiteSpace(char ch) {

      switch (ch) {

         case ' ':
         case '\t':
         case '\n':
         case '\r':
         case '\f':
            return true;

         default:
            return false;
      }
   }


   /**
    * Replace or, and, null, =, < with ||, &&, NULL, ==, !=.
    *
    * @param  str String to format
    *
    * @return String with signs replaced
    */
   private String replaceSigns(String str) {
      String retVal = "";
      StringTokenizer tok =
         new StringTokenizer(str, "=orORandANDullULL<>", true);

      int lastIndex = 0;
      int newIndex = 0;

      while (tok.hasMoreTokens()) {
         String currTok = tok.nextToken();
         newIndex += currTok.length();

         switch (currTok.charAt(0)) {

            case 'o':
            case 'O':

               // Expecting to find R
               String are = "";

               if (tok.hasMoreTokens()) {
                  are = tok.nextToken();
               }

               newIndex += are.length();

               if (are.equalsIgnoreCase("r")) {

                  if (this.standAlone(str, lastIndex, currTok + are)) {

                     // If it's really an OR operator replacing it
                     retVal += " || ";
                  }

                  // else adding the tokens
                  else {
                     retVal += currTok + are;
                  }
               }

               // Did not find R - adding tokens.
               else {
                  retVal += currTok + are;
               }

               lastIndex = newIndex;

               break;

            case 'a':
            case 'A':

               // Expecting to find N
               String an = "";

               if (tok.hasMoreTokens()) {
                  an = tok.nextToken();
               }

               newIndex += an.length();

               if (an.equalsIgnoreCase("n")) {

                  // Expecting to find D

                  String di = "";

                  if (tok.hasMoreTokens()) {
                     di = tok.nextToken();
                  }

                  newIndex += di.length();

                  if (di.equalsIgnoreCase("d")) {

                     // Found AND
                     if (this.standAlone(str, lastIndex, currTok + an + di)) {

                        // If it is really an AND operator replace it
                        retVal += " && ";
                     }

                     // Is part of an attribute name - append the tokens
                     else {
                        retVal += currTok + an + di;
                     }
                  }

                  // Did not find D - appending tokens
                  else {
                     retVal += currTok + an + di;
                  }
               }

               // Did not find N - appending tokens
               else {
                  retVal += currTok + an;
               }

               lastIndex = newIndex;

               break;

            case 'n':
            case 'N':

               // Expecting to find NULL

               String you = "";

               if (tok.hasMoreTokens()) {
                  you = tok.nextToken();
               }

               newIndex += you.length();

               // Expecting U
               if (you.equalsIgnoreCase("u")) {
                  String el1 = "";

                  if (tok.hasMoreTokens()) {
                     el1 = tok.nextToken();
                  }

                  newIndex += el1.length();

                  // Expecting L
                  if (el1.equalsIgnoreCase("l")) {

                     String el2 = "";

                     if (tok.hasMoreTokens()) {
                        el2 = tok.nextToken();
                     }

                     newIndex += el2.length();

                     // Expecting another L
                     if (el2.equalsIgnoreCase("l")) {

                        // Found the word NULL
                        if (
                            standAlone(str, lastIndex, currTok + you + el1 +
                                          el2)) {

                           // If it stands alone replace it with uppercase.
                           retVal += " NULL ";
                        }

                        // It is not the token NULL - appending all tokens
                        else {
                           retVal += currTok + you + el1 + el2;
                        }
                     }

                     // l2 was not an L, appending all tokens
                     else {

                        retVal += currTok + you + el1 + el2;
                     }

                  } // if el1

                  // el1 was not an L - appending 3 first tokens
                  else {

                     retVal += currTok + you + el1;
                  }
               }

               // You was not U - appending first 2 tokens.
               else {
                  retVal += currTok + you;
               }

               lastIndex = newIndex;

               break;

            case '=':

               char ch = retVal.charAt(retVal.length() - 1);

               if (ch == '<' || ch == '>') {

                  retVal += currTok;
               } // this is not a single equal sign
               else {
                  String equ = "";

                  if (tok.hasMoreTokens()) {
                     equ = tok.nextToken();
                  }

                  newIndex += equ.length();

                  if (equ.equals("=")) {

                     retVal += currTok + equ;
                  } else {
                     retVal += "==" + equ;
                  }

               }

               lastIndex = newIndex;

               break;


            case '<':

               // Looking for >
               String greater = "";

               if (tok.hasMoreTokens()) {
                  greater = tok.nextToken();
               }

               newIndex += greater.length();

               if (greater.equals(">")) {
                  retVal += " != ";
               } else {


                  retVal += currTok + greater;
               }

               lastIndex = newIndex;


               break;

            default:
               retVal += currTok;
               lastIndex += currTok.length();

               break;


         }
      } // end while

      return retVal;
   } // end method replaceSigns

   /**
    * Returns true if <code>lookFor</code> is not a part of a word in <code>
    * str</code>.
    *
    * @param  str     String containing <code>lookFor</code> at index <code>
    *                 index</code>
    * @param  index   First index of <code>lookFor</code> in <code>str</code>
    * @param  lookFor Substring of <code>str</code>
    *
    * @return True if <code>lookFor</code> is not a part of a word
    */
   private boolean standAlone(String str, int index, String lookFor) {

      if (index == 0) {
         return false;
      }

      if ((index + lookFor.length()) >= str.length()) {
         return false;
      }

      char before = str.charAt(index - 1);
      char after = str.charAt(index + lookFor.length());

      if (
          (before == ')' || isWhiteSpace(before)) &&
             (after == '(' || isWhiteSpace(after))) {
         return true;
      }

      return false;

   }

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new FilterConstructionGUI(); }

   /**
    * Execute this module using the same filters that were constructed in the
    * last successful execution.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      cw = (ConnectionWrapper) pullInput(0);

      String tableName = (String) pullInput(1);

      if (queryCondition == null) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      HashMap tables = StaticMethods.getAvailableTables(cw);

      // Checking that tableName is in the hashmap
      if (!tables.containsKey(tableName)) {
         throw new Exception(getAlias() + ": Table " + tableName +
                             " was not found in the data base!");
      }

      // Getting all attributes names
      HashMap availableAttributes =
         StaticMethods.getAvailableAttributes(cw, tableName);

      if (availableAttributes.size() == 0) {

         // This means either the table was not found in the database, or that
         // it has no columns. The query condition will be empty anyway
         System.out.println(getAlias() + ": Warning - Table " +
                            tableName +
                            " has no columns.");
      }

      // Creating a metadata table
      ExampleTable et = this.createMetaTable(tableName);

      // Creating a validator
      FilterExpression expression =
         new FilterExpression(et, getIncludeMissingValues());

      // Replacing the special signs of sql (or, and, = with the regular)
      String cond = replaceSigns(queryCondition);

      expression.setExpression(cond);
      pushOutput(queryCondition, 0);

   } // doit

   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return String array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   public String[] getFieldNameMapping() { return null; }

   /**
    * Get include missing values property.
    *
    * @return Missing values property
    */
   public boolean getIncludeMissingValues() { return _includeMissingValues; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      if (index == 0) {
         return "The database connection.";
      } else if (index == 1) {
         return "The database table to be filtered.";
      }

      return "SQLFilterConstruction has no such input.";
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

      if (index == 0) {
         return "Database Connection";
      } else if (index == 1) {
         return "Selected Table";
      }

      return null;
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
      { "ncsa.d2k.modules.io.input.sql.ConnectionWrapper", "java.lang.String" };

      return types;
   }

   /**
    * Get last expression property.
    *
    * @return Last expression property
    */
   public String getLastExpression() { return _lastExpression; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p> Overview: ";
      s +=
         "This module helps the user to construct a filtering expression. </p>";
      s += "<p> Detailed Description: ";
      s += "This module allows the user to specify the query condition ";
      s += "that filters rows from a database table. Details can be found ";
      s += "in the module's online help. </p>";

      s +=
         "<P><U>NOTE</U>:<br>When running the module in Headless mode (when 'Supress User Interface Display' is set to true):<br>";
      s +=
         "It is highly recommneded to set the filter using the first GUI of this module, ";
      s +=
         "and only then run it in a Headless mode.<BR>Should you choose to set the filter manually, ";
      s += "the query must be in an sql format according to the foloowing:";
      s += "<UL> <li>'!=' should be replaced with '&gt;&lt;'</li>";
      s += "<li>'==' should be replaced with '='</li>";
      s += "<li>'&&' should be replaced with 'and'</li>";
      s += "<li>'||' should be replaced with 'or'</li>";
      s += "<li>'null' should be uppercased - 'NULL'</li></ul>";

      s +=
         "<P>Missing Values Handling: When the filter expression is edited via " +
         "the properties editor and 'Supress User Interface Display' is set to true, " +
         "if the user whishes to include missing values " +
         "in the result dataset, each simple condition should be paired with the " +
         "condition 'ATT_NAME is NULL' using the 'or' operand. For Example: " +
         "If the basic condition is 'Att1 = value' then the pair should be " +
         "((Att1 = value) or (Att1 is NULL))'.</P>";
      s += "<p> Restrictions: ";
      s +=
         "We currently only support Oracle, SQLServer, DB2 and MySql database.";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "SQL Filter Construction"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      if (index == 0) {
         return "The query condition for the search. If the string is blank there will be no where clause, all records will be retrieved.";
      }

      return "SQLFilterConstruction has no such output.";
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

      if (index == 0) {
         return "Query Condition";
      }

      return null;
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] o = { "java.lang.String" };

      return o;
   }

   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] = super.getPropertiesDescriptions()[0];
      pds[1] =
         new PropertyDescription("queryCondition", "Query Condition",
                                 "SQL query condition");
      pds[2] =
         new PropertyDescription("includeMissingValues",
                                 "Include Missing Values",
                                 "If set, rows with missing values will be included in the result table. " +
                                 "This property is used when the module runs with GUI.");

      return pds;
   }

   /**
    * Get the query condition.
    *
    * @return query condtion
    */
   public String getQueryCondition() { return queryCondition; }

   /**
    * Set include missing values property.
    *
    * @param value true if missing values should be included
    */
   public void setIncludeMissingValues(boolean value) {
      _includeMissingValues = value;
   }

   /**
    * Set last expression property.
    *
    * @param value new last expression
    */
   public void setLastExpression(String value) { _lastExpression = value; }

   /**
    * Set the query condition.
    *
    * @param str new query condtion
    */
   public void setQueryCondition(String str) { queryCondition = str; }

   //~ Inner Classes ***********************************************************

   private class HelpWindow extends JD2KFrame {

      /**
       * Constructor.
       */
      public HelpWindow() {
         super("FilterConstruction Help");

         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

   /**
    * The View.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   protected class FilterConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      /** abort button. */
      private JButton abortButton;

      /** add boolean button. */
      private JButton addBooleanButton;

      /** add button. */
      private JButton addButton;

      /** add column button. */
      private JButton addColumnButton;

      /** add operation button. */
      private JButton addOperationButton;

      /** add scalar button. */
      private JButton addScalarButton;

      /** holds booleans. */
      private JComboBox booleanBox;

      /** holds names of columns. */
      private JComboBox columnBox;

      /** panel. */
      private JPanel comboOrFieldPanel;

      /** expression. */
      private FilterExpression expression;

      /** gui for building expressions. */
      private ExpressionGUI gui;

      /** help button. */
      private JButton helpButton;

      /** true if it has been initialized. */
      private boolean initialized = false;

      /** the view module that spawned this view. */
      private ViewModule mod;

      /** look up JComboBoxes for nominal columns; key = column #. */
      private HashMap nominalComboBoxLookup;

      /** layout manager. */
      private CardLayout nominalOrScalarLayout;

      /**
       * which nominal combobox is showing? -1 if scalar textfield is showing
       */
      private int nominalShowing = -1;

      /** holds operations. */
      private JComboBox operationBox;

      /** text field to enter scalar value. */
      private JTextField scalarField;

      /**
       * Get the unique values from a column.
       *
       * @param  columnIndex column index
       *
       * @return Vector containing the unique values
       */
      private Vector getUniqueValues(int columnIndex) {

         Vector columnValues = new Vector();
         int index = 0;

         try {
            Connection con = cw.getConnection();
            String valueQry =
               new String("select distinct " + colNames.get(columnIndex) +
                          " from ");
            valueQry =
               valueQry + tableName + " where " + colNames.get(columnIndex) +
               " is not null";
            valueQry = valueQry + " order by " + colNames.get(columnIndex);

            Statement valueStmt = con.createStatement();
            ResultSet valueSet = valueStmt.executeQuery(valueQry);

            while (valueSet.next()) {
               columnValues.add(valueSet.getString(1));
            }

            return columnValues;
         } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occurred in getUniqValue (db mode).");

            return null;
         }
      } // end method getUniqueValues

      /**
       * Set up the UI.
       */
      private void initialize() {
         table = createMetaTable(tableName);
         this.removeAll();

         expression = new FilterExpression(table, getIncludeMissingValues());

         gui = new ExpressionGUI(expression, false);
         gui.addExpressionListener(this);

         gui.getTextArea().setText(_lastExpression);

         columnBox = new JComboBox();
         columnBox.addActionListener(this);

         for (int i = 0; i < table.getNumColumns(); i++) {
            columnBox.addItem(table.getColumnLabel(i));
         }

         addColumnButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addColumnButton.addActionListener(this);

         JPanel columnPanel = new JPanel();
         columnPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(columnPanel, new JLabel(), 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(columnPanel, columnBox, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(columnPanel, addColumnButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);

         // scalar columns will require textfield input; nominal columns will
         // require a combobox of nominal values:

         scalarField = new JTextField(10);

         addScalarButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addScalarButton.addActionListener(this);

         nominalComboBoxLookup = new HashMap();
         comboOrFieldPanel = new JPanel();
         nominalOrScalarLayout = new CardLayout();
         comboOrFieldPanel.setLayout(nominalOrScalarLayout);
         comboOrFieldPanel.add(scalarField, scalar);

         // find the first scalar column
         nominalShowing = 0;

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (table.isColumnScalar(i)) {
               nominalShowing = -1;
               columnBox.setSelectedIndex(i);
               nominalOrScalarLayout.show(comboOrFieldPanel, scalar);

               break;
            }
         }

         // no scalar columns in the table, get the comboBox for the first
         // column
         if (nominalShowing == 0) {
            JComboBox nominalCombo = new JComboBox(getUniqueValues(0));
            comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(0));
            nominalComboBoxLookup.put(new Integer(0), nominalCombo);
            nominalOrScalarLayout.show(comboOrFieldPanel,
                                       table.getColumnLabel(0));
         }

         JPanel nominalOrScalarPanel = new JPanel();
         nominalOrScalarPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(nominalOrScalarPanel, new JLabel(), 0, 0, 1,
                                  1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(nominalOrScalarPanel,
                                  comboOrFieldPanel, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(nominalOrScalarPanel,
                                  addScalarButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);

         operationBox = new JComboBox();
         operationBox.addItem("==");
         operationBox.addItem("!=");
         operationBox.addItem("<");
         operationBox.addItem("<=");
         operationBox.addItem(">");
         operationBox.addItem(">=");

         addOperationButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addOperationButton.addActionListener(this);

         JPanel operationPanel = new JPanel();
         operationPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1,
                                  1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);

         booleanBox = new JComboBox();
         booleanBox.addItem("&&");
         booleanBox.addItem("||");

         addBooleanButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addBooleanButton.addActionListener(this);

         JPanel booleanPanel = new JPanel();
         booleanPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);

         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, nominalOrScalarPanel, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, operationPanel, 0, 2, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, booleanPanel, 0, 3, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JLabel(), 0, 4, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);

         JPanel topPanel = new JPanel();
         topPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
                                  GridBagConstraints.VERTICAL,
                                  GridBagConstraints.WEST, 0, 1);
         Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         helpButton = new JButton("Help");
         helpButton.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  HelpWindow help = new HelpWindow();
                  help.setVisible(true);
               }
            });

         addButton = gui.getAddButton();
         addButton.setText("Done");

         JPanel bottomPanel = new JPanel();
         bottomPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(bottomPanel, helpButton, 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(bottomPanel, abortButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(bottomPanel, addButton, 3, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, topPanel, 0, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(this, new JSeparator(), 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.SOUTH, 1, 0);
         Constrain.setConstraints(this, bottomPanel, 0, 2, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.SOUTH, 1, 0);

         initialized = true;

      } // end method initialize


      /**
       * Invoked when an action occurs.
       *
       * @param e Description of parameter e.
       */
      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == columnBox && initialized) {

            int index = columnBox.getSelectedIndex();

            if (table.isColumnNominal(index)) {

               nominalShowing = index;

               JComboBox nominalCombo = new JComboBox(getUniqueValues(index));

               if (!nominalComboBoxLookup.containsKey(new Integer(index))) {
                  comboOrFieldPanel.add(nominalCombo,
                                        table.getColumnLabel(index));
                  nominalComboBoxLookup.put(new Integer(index), nominalCombo);
               }

               nominalOrScalarLayout.show(comboOrFieldPanel,
                                          table.getColumnLabel(index));
            } else {
               nominalShowing = -1;
               nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
            }

         } else if (src == addColumnButton) {

            if (getIncludeMissingValues()) {
               gui.getTextArea().insert((String) columnBox.getSelectedItem() +
                                        " is NULL || " +
                                        (String) columnBox.getSelectedItem(),
                                        gui.getTextArea().getCaretPosition());
            } else {
               gui.getTextArea().insert((String) columnBox.getSelectedItem(),
                                        gui.getTextArea().getCaretPosition());
            }
         } else if (src == addScalarButton) {

            if (nominalShowing < 0) {

               gui.getTextArea().insert(scalarField.getText(),
                                        gui.getTextArea().getCaretPosition());

            } else {

               JComboBox combo =
                  (JComboBox) nominalComboBoxLookup.get(new Integer(nominalShowing));

               gui.getTextArea().insert("'" + combo.getSelectedItem() + "'",
                                        gui.getTextArea().getCaretPosition());

            }

         } else if (src == addOperationButton) {
            gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                                     gui.getTextArea().getCaretPosition());
         } else if (src == addBooleanButton) {
            gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                                     gui.getTextArea().getCaretPosition());
         } else if (src == abortButton) {
            viewCancel();
         }
      } // end method actionPerformed

      /**
       * Signal the expression has changed.
       *
       * @param evaluation new value of the expression.
       */
      public void expressionChanged(Object evaluation) {
         String queryCondition = expression.toSQLString();

         setQueryCondition(queryCondition);

         pushOutput(queryCondition, 0);
         viewDone("Done");
      }

      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param m The module this view is associated with.
       */
      public void initView(ViewModule m) { mod = m; }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param o The object that has been input.
       * @param i The index of the module input that been received.
       */
      public void setInput(Object o, int i) {

         if (i == 0) {
            cw = (ConnectionWrapper) o;
         } else if (i == 1) {
            tableName = (String) o;
            initialize();
         }
      }
   } // end class FilterConstructionGUI
} // end class SQLFilterConstruction
