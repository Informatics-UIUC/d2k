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
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.modules.core.vis.widgets.ExpressionGUI;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import ncsa.d2k.modules.core.util.*;


/**
 * <code>FilterConstruction</code> is a simple user interface that facilitates
 * the creation of an expression for filtering rows from a <code>
 * MutableTable</code>. (See the documentation for <code>FilterExpression</code>
 * for details on the underlying expression string and its format.)
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class FilterConstruction extends HeadlessUIModule {

   //~ Static fields/initializers **********************************************

   /******************************************************************************/
/* GUI                                                                        */
   /******************************************************************************/

   static private String scalar = "FilterConstructionINTERNALscalar";

   //~ Instance fields *********************************************************

   /** If set, rows with missing values will be included in the result table. */
   private boolean _includeMissingValues = true;

   /******************************************************************************/
/* properties                                                                 */
   /******************************************************************************/
   /** last expression. */
   private String _lastExpression = "";

   /** Set this expression to filter out rows in the table, when
    * "Supress User Interface Display" is set to true. Validation of the
    * expression is done during run time. */
   private String expression;
   private D2KModuleLogger myLogger;
   

   //~ Methods *****************************************************************
   public void beginExecution() {
		  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   }

   /**
    * Get the help text.
    *
    * @return help text
    */
   private String getHelpString() {

      StringBuffer sb = new StringBuffer();

      sb.append("<html><body><h2>FilterConstruction</h2>");
      sb.append("This module allows a user to filter rows from a MutableTable ");
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
      sb.append("<br><br>");
      sb.append("To add parentheses around a sub expression, mark the sub expression ");
      sb.append("using the mouse, and click \"Add Paren\" button.");
      sb.append("</body></html>");

      return sb.toString();

   } // end method getHelpString

   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new FilterConstructionGUI(); }


   /**
    * Push out the filters from the last successful run when run in headless
    * mode.
    *
    * @throws Exception when something goes wrong
    */
   public void doit() throws Exception {

      // pulling input
      MutableTable table = (MutableTable) pullInput(0);

      // creating a column hash map
      HashMap availableColumns = StaticMethods.getAvailableAttributes(table);
      /*
       *        new HashMap();  for (int i=0; i<table.getNumColumns(); i++)
       * availableColumns.put(table.getColumnLabel(i), new Integer(i));
       */

      if (expression == null) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      if (availableColumns.size() == 0) {
    	  myLogger.warn(getAlias() + ": Warning - Table " +
                  table.getLabel() + " has no columns.");
       
         /*System.out.println("The transformation will be an empty one");
          * boolean[] val = new boolean[0]; pushOutput(new
          * FilterTransformation(val, false), 0);return;*/
      }

      //     String goodCondition = ExpressionParser.parseExpression(expression,
      // availableColumns, false); now good condition holds only relevant
      // filters. building a filter expression

      // assuming that filter expression is parsing and validating the
      // expression.
      FilterExpression fEx =
         new FilterExpression(table, getIncludeMissingValues());
      fEx.setExpression(expression);

      // getting the array of booleans - which row to filter.
      boolean[] eval = (boolean[]) fEx.evaluate();

      // pushing out the transformation
      pushOutput(new FilterTransformation(eval, false), 0);

   } // doit

   /**
    * Get expression property
    *
    * @return expression property
    */
   public String getExpression() { return expression; }

   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   public String[] getFieldNameMapping() { return null; }

   /**
    * Get include missing values property
    *
    * @return missing values property
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

      switch (index) {

         case 0:
            return "This is the mutable table for which a filter will be constructed.";

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
            return "Mutable Table";

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
      String[] types = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return types;
   }

   /**
    * Get last expression property
    *
    * @return last expression property
    */
   public String getLastExpression() { return _lastExpression; }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        return "<p>" +
                "      Overview: This module will provide a gui to be employed by the user to " +
                "      create a filter to eliminate the rows of the input table which do not " +
                "      meet the criterion specified." +
                "    </p>" +
                "    <p>" +
                "      Detailed Description: This module provides a gui used to specify " +
                "      expressions. These expressions are used to eliminate rows of the table " +
                "      that do not meet the criterion specified in those expressions. Complex " +
                "      expressions are supported. When the gui is dismissed, the filters are " +
                "      all collected into a <i>Transformation</i> object that is then passed as " +
                "      an output. The expressions are not applied to the data by this module, " +
                "      that can be done downstream using the transformation object passed." +
                "    </p>" +
                "    <p>" +
                "      The expressions which can be applied to columns support the following " +
                "      operators: &quot;&gt;&quot;, &quot;&gt;=&quot;, &quot;&lt;&quot;, &quot;&lt;=&quot;, &quot;!=&quot; and &quot;==&quot;. These operators are " +
                "      greater than, greater than or equal to, less than, less than or equal " +
                "      to, not equal, and equal. The result of these expressions, which we will " +
                "      call attribute expressions, are either true or false. We can construct " +
                "      more complex expressions using the boolean operators &quot;and&quot; (&amp;&amp;) or " +
                "      &quot;or&quot;(||) on the results of the attribute expressions. For example, if we " +
                "      had an attribute named <i>sepal_length</i> which is numeric, a filter " +
                "      resulting from the following expression (sepal_length &gt;= 6.0) &amp;&amp; " +
                "      (sepal_length &lt;= 7.0) will result in the removal of all rows where the " +
                "      sepal_length is not in the range from 6.0 to 7.0, inclusive. Please note " +
                "      that names of columns are case sensitive." +
                "    </p>" +
                "    <p>" +
                "      Data Type Restrictions: Filter operations are supported for numeric data " +
                "      only at this point." +
                "    </p>" +
                "    <p>" +
                "      Data Handling: This module <i>may</i> modify its input data: columns " +
                "      with blank labels will be assigned default ones. Other than that, this " +
                "      module does not modify its input. Rather, its output is a <i>" +
                "      Transformation</i> that can later be applied to filter the table. By " +
                "      default, if an expression operates on any missing value, the row " +
                "      containing the missing value is included in the result. There is a " +
                "      property that can be changed to reverse this behavior." +
                "    </p>";
    } // end method getModuleInfo


    /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Filter Construction"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "This is the transformation the user constructed using the gui associated     with this module.";

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
            return "Transformation";

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
      String[] types =
      { "ncsa.d2k.modules.core.datatype.table.Transformation" };

      return types;
   }


   /**
    * Get the descriptions of the properties of this module.
    *
    * @return the PropertyDescriptor for each property of this module.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] = super.supressDescription;
      pds[1] =
         new PropertyDescription("expression", "Filter Expression",
                                 "Set this expression to filter out rows in the table, when \"Supress User Interface Display\" is set to true. Validation of the expression is done during run time.");
      pds[2] =
         new PropertyDescription("includeMissingValues",
                                 "Include Missing Values",
                                 "If set, rows with missing values will be included in the result table.");

      return pds;

   }

   /**
    * Set expression property
    *
    * @param ex expression property
    */
   public void setExpression(String ex) { expression = ex; }

   /**
    * Set include missing values property
    *
    * @param value true if missing values should be included
    */
   public void setIncludeMissingValues(boolean value) {
      _includeMissingValues = value;
   }

   /**
    * Set last expression property
    *
    * @param value new last expression
    */
   public void setLastExpression(String value) { _lastExpression = value; }

   //~ Inner Classes ***********************************************************

   /******************************************************************************/
/* the output Transformation                                                  */
   /******************************************************************************/

   /**
    * Transformation to perform filtering.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class FilterTransformation implements Transformation {

      private boolean[] filter;
      private boolean filterThese;

      FilterTransformation(boolean[] filter, boolean filterThese) {
         this.filter = filter;
         this.filterThese = filterThese;
      }

      public boolean transform(MutableTable table) {

         // if there are no filters, do nothing.
         if (filter == null) {
            return true;
         }

         try {

            // 9-17-03 vered: chnaged order of removing rows. if removing from
            // beginning to end an array index out of bound exception is likely.
            // in addition to a likelyhood of the exception, since a mutable
            // table is a subset table - removing first from the beginning
            // chnages the indexing of the end rows. which might result in
            // filtering the wrong rows.
            if (!filterThese) {

               // commented by vered.
               // for (int i = 0; i<filter.length; i++)
               for (int i = filter.length - 1; i >= 0; i--) {

                  if (!filter[i]) {
                     table.removeRow(i);
                  }
               }
            }


            // 4/7/02 commented out by Loretta...
            // this add gets done by applyTransformation
            // table.addTransformation(this);
         } catch (Exception e) {

            e.printStackTrace();

            return false;
         }

         return true;

      } // end method transform

   } // end class FilterTransformation

   /******************************************************************************/
/* help facilities                                                            */
   /******************************************************************************/

   /**
    * Window to hold help text.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   private class HelpWindow extends JD2KFrame {
      public HelpWindow() {
         super("FilterConstruction Help");

         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

    /**
     * The view
     */
   protected class FilterConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

        /** add column button */
      private JButton addColumnButton;
        /** add scalar button */
      private JButton addScalarButton;
        /** add operation button */
      private JButton addOperationButton;
        /** add boolean button */
      private JButton addBooleanButton;
        /** abort button */
      private JButton abortButton;
        /** help button */
      private JButton helpButton;
        /** holds names of columns */
      private JComboBox columnBox;
        /** holds operations */
      private JComboBox operationBox;
        /** holds booleans */
      private JComboBox booleanBox;
       /** panel */
      private JPanel comboOrFieldPanel;
        /** expression */
      private FilterExpression expression;
        /** gui for building expressions */
      private ExpressionGUI gui;
        /** true if it has been initialized */
      private boolean initialized = false;
        /** the view module that spawned this view */
      private ViewModule mod;
        /** look up JComboBoxes for nominal */
      private HashMap nominalComboBoxLookup;

      /** layout manager */
      private CardLayout nominalOrScalarLayout;
      /** which nominal combobox is showing? */
      private int nominalShowing = -1;

      /** text field to enter scalar value */
      private JTextField scalarField;
        /** table */
      private MutableTable table;

      /**
       * Get the unique values from a column.
       *
       * @param  columnIndex column index
       *
       * @return Vector containing the unique values
       */
      private Vector getUniqueValues(int columnIndex) {

         if (table == null) {
            return null;
         }

         int numRows = table.getNumRows();

         HashMap columnValues = new HashMap();

         int index = 0;

         for (int i = 0; i < numRows; i++) {

            String str = table.getString(i, columnIndex);

            if (!columnValues.containsKey(str)) {
               columnValues.put(str, new Integer(index++));
            }

         }

         return new Vector(columnValues.keySet());

      }

      /**
       * Set up the UI.
       */
      private void initialize() {

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

         ///////////////////////////////////////////////////////////////////////
         // scalar columns will require textfield input; nominal columns will
         // require a combobox of nominal values:
         // {

         scalarField = new JTextField();

         addScalarButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addScalarButton.addActionListener(this);

         nominalComboBoxLookup = new HashMap();
         comboOrFieldPanel = new JPanel();
         nominalOrScalarLayout = new CardLayout();
         comboOrFieldPanel.setLayout(nominalOrScalarLayout);
         comboOrFieldPanel.add(scalarField, scalar);

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (!table.isColumnNominal(i)) {
               continue;
            }

            JComboBox nominalCombo = new JComboBox(getUniqueValues(i));

            comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(i));
            nominalComboBoxLookup.put(new Integer(i), nominalCombo);

         }

         if (table.isColumnNominal(0)) {
            nominalOrScalarLayout.show(comboOrFieldPanel,
                                       table.getColumnLabel(0));
            nominalShowing = 0;
         }

         /*
          * JPanel scalarPanel = new JPanel(); scalarPanel.setLayout(new
          * GridBagLayout()); Constrain.setConstraints(scalarPanel, new
          * JLabel(), 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
          * GridBagConstraints.CENTER, 1, 0);
          * Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
          * GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
          * Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
          * GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
          */

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

         // }
         ///////////////////////////////////////////////////////////////////////

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

         // doneButton = new JButton("Done");
         // doneButton.addActionListener(this);
         helpButton = new JButton("Help");
         helpButton.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  HelpWindow help = new HelpWindow();
                  help.setVisible(true);
               }
            });

         JButton addButton = gui.getAddButton();
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
       * @param e action event
       */
      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == columnBox && initialized) {

            int index = columnBox.getSelectedIndex();

            if (table.isColumnNominal(index)) {

               nominalShowing = index;

               nominalOrScalarLayout.show(comboOrFieldPanel,
                                          table.getColumnLabel(index));

            } else {

               nominalShowing = -1;

               nominalOrScalarLayout.show(comboOrFieldPanel, scalar);

            }

         } else if (src == addColumnButton) {
            gui.getTextArea().insert((String) columnBox.getSelectedItem(),
                                     gui.getTextArea().getCaretPosition());
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
       * Called when the expression changes.
       *
       * @param evaluation argument
       */
      public void expressionChanged(Object evaluation) {
         _lastExpression = gui.getTextArea().getText();
         pushOutput(new FilterTransformation((boolean[]) evaluation, false), 0);

         // headless conversion support
         setExpression(_lastExpression);

         // headless conversion support
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
       * @param obj The object that has been input.
       * @param ind The index of the module input that been received.
       */
      public void setInput(Object obj, int ind) {

         if (ind != 0) {
            return;
         }

         table = (MutableTable) obj;

         for (int i = 0; i < table.getNumColumns(); i++) {
            String label = table.getColumnLabel(i);

            if (label == null || label.length() == 0) {
               table.setColumnLabel("column_" + i, i);
            }
         }

         initialize();
      }

   } // end class FilterConstructionGUI

   // headless conversion support
} // end class FilterConstruction
