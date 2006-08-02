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
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.ExpressionException;
import ncsa.d2k.modules.core.datatype.ExpressionListener;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.transformations.AttributeTransform;
import ncsa.d2k.modules.core.datatype.table.transformations.Construction;
import ncsa.d2k.modules.core.vis.widgets.ExpressionGUI;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.gui.Constrain;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


/**
 * <code>AttributeConstruction</code> is a simple user interface that
 * facilitates the construction of new attributes in a <code>MutableTable</code>
 * from operations on existing attributes in the table. <code>
 * AttributeConstruction</code> works by constructing <code>
 * ColumnExpression</code>s; subsequently, its behavior and order of operations
 * are defined in the documentation for that class.
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class AttributeConstruction extends HeadlessUIModule {

   //~ Instance fields *********************************************************

   /** Construction. */
   protected Object[] lastCons = null;

   /** labels of constructed attributes. */
   protected String[] newLabel = null;

   /** types of constructed attributes. */
   protected int[] newTyp = null;

   //~ Methods *****************************************************************

   /**
    * Get some text to put into the help window
    *
    * @return text to put in help window
    */
   private String getHelpString() {

      StringBuffer sb = new StringBuffer();
      sb.append("<html><body><h2>AttributeConstruction</h2>");
      sb.append("This module allows a user to construct new attributes in a ");
      sb.append("table from operations on existing attributes.");
      sb.append("<br><br>");
      sb.append("The available operations on numeric attributes are addition, ");
      sb.append("subtraction, multiplication, division, and modulus.<BR> ");
      sb.append("<BR>In addition there are also the following scientific operations: ");
      sb.append("<BR>^ = by the power of...<BR>neg(x) = -1*x<BR>log(x) = base 10 logarithm");
      sb.append("<BR>ln() = base e logarithm<BR>abs() = absulute value<BR>exp(x) = e^x");
      sb.append("<BR>sin() = sine<BR>asin() = arc sine<BR>cosin() = cosine<BR>acosin() = arc cosine");
      sb.append("<BR>tan() = tangent<BR>atan() = arc tangent");
      sb.append("<P>The operations available on boolean attributes are AND and OR. ");
      sb.append("The user can construct an expression for the new attribute ");
      sb.append("using the lists of attributes and/or the scalar textfield and the operators on the left. ");
      sb.append("It is important that this expression be well-formed: that ");
      sb.append("parentheses match, that attribute labels surround operators, ");
      sb.append("and so forth.");
      sb.append("<br><br>");
      sb.append("In the absence of parentheses, the order of operations is ");
      sb.append("as follows: AND, OR, division and multiplication, modulus, ");
      sb.append("addition and subtraction (all from left to right). Note that ");
      sb.append("AND and OR cannot be applied to numeric attributes, and that ");
      sb.append("the numeric operators cannot be applied to boolean attributes. ");
      sb.append("Thus a given expression may involve numeric attributes or boolean ");
      sb.append("attributes, but not both.");
      sb.append("<br><br>");
      sb.append("The user must also specify the name for the new attribute in ");
      sb.append("the field at the top left. <b>Note carefully</b> that any characters in ");
      sb.append("this new name that could be interpreted as operators (such ");
      sb.append("as parentheses, or a hyphen, which would be interpreted as ");
      sb.append("a subtraction operator) <b>must</b> be preceded by a ");
      sb.append("backslash character in order to distinguish them from those ");
      sb.append("operators.  <b>Note:</b> Any numeric values used as the name of an attribute ");
      sb.append("will be treated as scalar values rather than column names");
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
   protected UserView createUserView() { return new ColumnConstructionGUI(); }

   /**
    * headless conversion support.  If the table is in the same format as the
    * last successful execution, push out the same constructions.
    *
    * @throws Exception when something goes wrong.
    */
   public void doit() throws Exception {
      Table t = (Table) pullInput(0);


      if (lastCons == null) {
         throw new Exception(this.getAlias() +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }


      String[] exps = new String[lastCons.length];


      for (int i = 0; i < lastCons.length; i++) {
         exps[i] = ((Construction) lastCons[i]).expression;
      }

      ConstructionValidator validator = new ConstructionValidator(exps, t);

      if (validator.validate()) {
         pushOutput(new AttributeTransform(lastCons), 0);
      } else { // should never get here
         throw new Exception("One or more of the configured constructions is either " +
                             "malformed or use a label that is not a valid label for " +
                             "constructions in the input table.\n" +
                             "Please reconfigure the module via a GUI run.");
      }
   } // doit

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
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      if (index == 0) {
         return "A MutableTable containing attributes to be used in construction.";
      }

      return "Attribute Construction has no such input.";
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
         return "Mutable Table";
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
      String[] i = { "ncsa.d2k.modules.core.datatype.table.MutableTable" };

      return i;
   }

   /**
    * Get lastCons.
    *
    * @return lastCons
    */
   public Object[] getLastCons() { return lastCons; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module provides a GUI that enables the user to construct ");
      sb.append("new attributes in a table from operations on existing attributes.");
      sb.append("</p>");
      sb.append("<p>Detailed Description: ");
      sb.append("This module provides a GUI used to specify expression strings. ");
      sb.append("These expressions are interpreted as operations on existing ");
      sb.append("attributes in the table and are used to construct new attributes. ");
      sb.append("When the GUI is dismissed, the information needed to construct ");
      sb.append("these new attributes is encapsulated in a <i>Transformation</i> ");
      sb.append("object that can be applied downstream in order to actually ");
      sb.append("add the new attributes to the table.");
      sb.append("</p><p>");
      sb.append("The available operations on numeric attributes are addition, ");
      sb.append("subtraction, multiplication, division, and modulus.<BR>");
      sb.append("<BR>In addition there are also the following scientific operations: ");
      sb.append("<BR>^ = by the power of...<BR>neg(x) = -1*x<BR>log(x) = base 10 logarithm");
      sb.append("<BR>ln() = base e logarithm<BR>abs() = absulute value<BR>exp(x) = e^x");
      sb.append("<BR>sin() = sine<BR>asin() = arc sine<BR>cosin() = cosine<BR>acosin() = arc cosine");
      sb.append("<BR>tan() = tangent<BR>atan() = arc tangent");
      sb.append("<P>The operations available on boolean attributes are AND and OR.");
      sb.append("</p><p>Missing Values Handling: Missing values are preserved by " +
                "the output Transformation of this module. An attribute that is " +
                "constructed from a missing values will be a missing value in the " +
                "result Table.");
      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("Attribute construction operations can only be performed on the ");
      sb.append("numeric and boolean attributes of a table. Other attributes will ");
      sb.append("be ignored, but they will not be modified.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module <i>might</i> immediately modify the table: ");
      sb.append("attributes with blank labels will be assigned default ones. ");
      sb.append("Other than that, this module does not modify its input data. ");
      sb.append("Rather, its output is a <i>Transformation</i> that can then ");
      sb.append("be used to modify the table. In this module, and computation ");
      sb.append("performed with a missing value will result in a missing value ");
      sb.append("in the constructed attribute column.");
      sb.append("</p>");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Attribute Construction"; }

   /**
    * Get newLabel.
    *
    * @return newLabel
    */
   public Object[] getNewLabel() { return newLabel; }

   /**
    * Get newTyp.
    *
    * @return newTyp
    */
   public Object getNewTyp() { return newTyp; }

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
         return "The transformation to construct the new attributes.";
      }

      return "Attribute Construction has no such output.";
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
         return "Transformation";
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
      String[] o = { "ncsa.d2k.modules.core.datatype.table.Transformation" };

      return o;
   }

   /**
    * Set lastCons.
    *
    * @param value new lastCons
    */
   public void setLastCons(Object[] value) { lastCons = (Object[]) value; }

   /**
    * Set newLabel.
    *
    * @param value new newLabel
    */
   public void setNewLabel(Object[] value) { newLabel = (String[]) value; }

   /**
    * Set newTyp.
    *
    * @param value new newTyp
    */
   public void setNewTyp(Object value) { newTyp = (int[]) value; }

   //~ Inner Classes ***********************************************************

   /******************************************************************************/
   /* help facilities
    */
   /******************************************************************************/

   /**
    * A window that shows the help text
    */
   private class HelpWindow extends JD2KFrame {
      public HelpWindow() {
         super("AttributeConstruction Help");

         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }


   /******************************************************************************/
   /* GUI
    */
   /******************************************************************************/

   /**
    * The view class
    */
   protected class ColumnConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      /** abort button. */
      protected JButton abortButton;

      /** add boolean button. */
      protected JButton addBooleanButton;

      /** add column button. */
      protected JButton addColumnButton;

      /** add operation button. */
      protected JButton addOperationButton;

      /** add scalar button. */
      protected JButton addScalarButton;

      /** boolean combo box. */
      protected JComboBox booleanBox;

      /** panel. */
      protected JPanel bottomPanel;

      /** holds column names. */
      protected JComboBox columnBox;

      /** panel. */
      protected JPanel columnPanel;

      /** label. */
      protected JLabel constructionLabel;

      /** construction objects. */
      protected Object[] constructions;

      /** delete button. */
      protected JButton deleteButton;

      /** done button. */
      protected JButton doneButton;

      /** expression. */
      protected ColumnExpression expression;

      /** gui. */
      protected ExpressionGUI gui;

      /** panel. */
      protected JPanel guiPanel;

      /** help window. */
      protected HelpWindow help = new HelpWindow();

      /** help button. */
      protected JButton helpButton;

      /* the view module */
      protected ViewModule mod;

      /** label. */
      protected JLabel newAttributeLabel;

      /** list of the new columns created. */
      protected JList newColumnList;

      /** list module for newColumnList. */
      protected DefaultListModel newColumnModel;

      /** labels for use in constructing. */
      protected String[] newLabels;

      /** new name field. */
      protected JTextField newNameField;

      /** panel. */
      protected JPanel newNamePanel;

      /** the transformation. */
      protected int[] newTypes;

      /** holds operations. */
      protected JComboBox operationBox;

      /** panel. */
      protected JPanel operationPanel;

      /** Panel. */
      protected JPanel rightPanel;

      /** text field to enter scalar value. */
      protected JTextField scalarField;

      /** selected index. */
      protected int selectedIndex;

      /** selected item. */
      protected Construction selectedItem;

      /** map of strings to column box entries. */
      protected HashMap stringsToColumnBoxEntries;

      /** the table. */
      protected MutableTable table;

      /** scroller. */
      protected JScrollPane thatPane;

      /**
       * constructor.
       */
      public ColumnConstructionGUI() {
         this.newLabels = (String[]) AttributeConstruction.this.getNewLabel();
         this.newTypes = (int[]) AttributeConstruction.this.getNewTyp();
         this.constructions =
            (Object[]) AttributeConstruction.this.getLastCons();
      }

      /**
       * handle action events on the buttons.
       *
       * @param e ActionEvent
       */
      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == addColumnButton) {

            gui.getTextArea().insert((String) columnBox.getSelectedItem(),
                                     gui.getTextArea().getCaretPosition());
         } else if (src == addOperationButton) {
            gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                                     gui.getTextArea().getCaretPosition());
         } else if (src == addBooleanButton) {
            gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                                     gui.getTextArea().getCaretPosition());
         } else if (src == addScalarButton) {
            gui.getTextArea().insert(" " + scalarField.getText() + " ",
                                     gui.getTextArea().getCaretPosition());
         } else if (src == abortButton) {
            help.setVisible(false);
            viewCancel();
         } else if (src == doneButton) {
            help.setVisible(false);

            // headless conversion support
            // LAM-tlr this is a fix, we are setting a value to itself here,
            // and that of course has no effect, and then we are passing
            // something else.
            /* if (AttributeConstruction.this.lastCons == null) //meaning the
             * user chose no constructions
             *lastCons = new Object[0];*/
            setLastCons(newColumnModel.toArray());
            // headless conversion support

            pushOutput(new AttributeTransform(newColumnModel.toArray()), 0);

            viewDone("Done");
         } else if (src == deleteButton) {

            int selected = newColumnList.getSelectedIndex();

            if (selected != -1) {

               String label =
                  ((Construction) (newColumnModel.elementAt(selected))).label;

               columnBox.removeItem(label);
               newColumnModel.removeElementAt(selected);

               if (newLabels != null) {

                  String[] newNewLabels = new String[newLabels.length - 1];
                  int[] newNewTypes = new int[newTypes.length - 1];

                  int index = 0;

                  for (index = 0; index < newLabels.length; index++) {

                     if (newLabels[index].equals(label)) {
                        break;
                     } else {
                        newNewLabels[index] = newLabels[index];
                        newNewTypes[index] = newTypes[index];
                     }
                  }

                  for (; index < newNewLabels.length; index++) {
                     newNewLabels[index] = newLabels[index + 1];
                     newNewTypes[index] = newTypes[index + 1];
                  }

                  newLabels = newNewLabels;
                  newTypes = newNewTypes;

               }

               newLabel = newLabels;
               newTyp = newTypes;
               lastCons = newColumnModel.toArray();

               newColumnList.getSelectionModel().clearSelection();
               newNameField.setText("");
               gui.getTextArea().setText("");
            } // end if
         } // end if-else


      } // end method actionPerformed

       /**
        * Called when the expression in the ExpressionGUI changes
        * @param evaluation not used
        */
      public void expressionChanged(Object evaluation) {

         if (newNameField.getText().length() == 0) {
            JOptionPane.showMessageDialog(this,
                                          "You must specify a new attribute name.",
                                          "AttributeConstruction",
                                          JOptionPane.ERROR_MESSAGE);
         } else {
            columnBox.addItem(newNameField.getText());

            StringBuffer newLabelBuffer =
               new StringBuffer(newNameField.getText());

            for (int i = 0; i < newLabelBuffer.length(); i++) {

               if (newLabelBuffer.charAt(i) == '\\') {
                  newLabelBuffer.deleteCharAt(i);
                  i--;
               }
            }

            // determine new expression, label, and type

            ColumnExpression newExp = new ColumnExpression(table);

            try {

               // System.out.println("new " + newLabels + " " + newTypes);
               newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                        newTypes);
            } catch (ExpressionException e) {
               JOptionPane.showMessageDialog(this,
                                             "Error in AttributeConstruction. Check your expression.",
                                             "AttributeConstruction",
                                             JOptionPane.ERROR_MESSAGE);

               return;
            }

            String newLab = newLabelBuffer.toString();
            String newStr = gui.getTextArea().getText();
            int newType = newExp.evaluateType();

            // add to arrays of new expressions, labels, types

            if (newLabels == null) {
               newLabels = new String[1];
               newLabels[0] = newLab;
            } else {
               String[] newNewLab = new String[newLabels.length + 1];

               for (int i = 0; i < newLabels.length; i++) {
                  newNewLab[i] = newLabels[i];
               }

               newNewLab[newLabels.length] = newLab;
               newLabels = newNewLab;
            }

            if (newTypes == null) {
               newTypes = new int[1];
               newTypes[0] = newType;
            } else {
               int[] newNewTyp = new int[newTypes.length + 1];

               for (int i = 0; i < newTypes.length; i++) {
                  newNewTyp[i] = newTypes[i];
               }

               newNewTyp[newTypes.length] = newType;
               newTypes = newNewTyp;
            }

            newExp = new ColumnExpression(table);

            try {
               newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                        newTypes);
            } catch (ExpressionException e) {
               JOptionPane.showMessageDialog(this,
                                             "Error in AttributeConstruction. Check your expression.",
                                             "AttributeConstruction",
                                             JOptionPane.ERROR_MESSAGE);

               return;
            }

            gui.setExpression(newExp);


// Changed 3.25.3004 by DC
            if (selectedItem != null) {
               String newName = newNameField.getText();
               selectedItem.label = newNameField.getText();
               selectedItem.expression = gui.getTextArea().getText();
               newColumnModel.set(selectedIndex, selectedItem);
               newColumnList.getSelectionModel().clearSelection();
            } else {
               Construction added =
                  new Construction(newNameField.getText(),
                                   gui.getTextArea().getText());
               newColumnModel.addElement(added);
            }
// end change

            /*Construction added =
             * new Construction(   newNameField.getText(),
             * gui.getTextArea().getText());newColumnModel.addElement(added);*/
            newColumnList.setMinimumSize(new Dimension(200, 200));

            newLabel = newLabels;
            newTyp = newTypes;
            lastCons = newColumnModel.toArray();

            newNameField.setText("");
            gui.getTextArea().setText("");

         } // end if

      } // end method expressionChanged

      /**
       * lay out the UI here.
       */
      public void initialize() {

         this.removeAll();
         this.newLabels = null;
         this.newTypes = null;
         this.constructions = null;
         expression = new ColumnExpression(table);

         gui = new ExpressionGUI(expression, true);
         gui.addExpressionListener(this);

         newNameField = new JTextField(16);
         newNameField.setMinimumSize(new Dimension(180, 20));
         newNameField.setPreferredSize(new Dimension(180, 20));
         stringsToColumnBoxEntries = new HashMap();

         newNamePanel = new JPanel();
         newNamePanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(newNamePanel, newNameField, 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

         columnBox = new JComboBox();

         for (int i = 0; i < table.getNumColumns(); i++) {

            int columnType = table.getColumnType(i);

            switch (columnType) {

               case ColumnTypes.BOOLEAN:
               case ColumnTypes.BYTE:
               case ColumnTypes.DOUBLE:
               case ColumnTypes.FLOAT:
               case ColumnTypes.INTEGER:
               case ColumnTypes.LONG:
               case ColumnTypes.SHORT:

                  StringBuffer buffer =
                     new StringBuffer(table.getColumnLabel(i));

                  for (int j = 0; j < buffer.length(); j++) {
                     // vered - merging with basic3 updates - shouldn't we
                     // support the scientific

                     switch (buffer.charAt(j)) {

                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '%':
                        case '(':
                        case ')':
                           buffer.insert(j, '\\');
                           j++;

                           break;

                        case '&':
                        case '|':
                           buffer.insert(j, '\\');
                           j += 2;

                           break;

                     }

                  } // end for

                  columnBox.addItem(buffer.toString());

            }

         } // end for

         if (newLabels != null) {

            for (int i = 0; i < newLabels.length; i++) {
               columnBox.addItem(newLabels[i]);

            }
         }

         addColumnButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addColumnButton.addActionListener(this);

         columnPanel = new JPanel();
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

         operationBox = new JComboBox();
         operationBox.addItem("+");
         operationBox.addItem("-");
         operationBox.addItem("*");
         operationBox.addItem("/");
         operationBox.addItem("%");

         // vered - merging with absic 3
         operationBox.addItem("^");
         operationBox.addItem("neg()");
         operationBox.addItem("log()");
         operationBox.addItem("ln()");
         operationBox.addItem("sqrt()");
         operationBox.addItem("abs()");
         operationBox.addItem("exp()");
         operationBox.addItem("sin()");
         operationBox.addItem("asin()");
         operationBox.addItem("cos()");
         operationBox.addItem("acos()");
         operationBox.addItem("tan()");
         operationBox.addItem("atan()");
// vered end merge

         addOperationButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addOperationButton.addActionListener(this);

         operationPanel = new JPanel();
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

         JPanel scalarPanel = new JPanel();
         JPanel scalarNamePanel = new JPanel();


         addScalarButton =
            new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addScalarButton.addActionListener(this);
         scalarField = new JTextField(10);

         scalarField.setMinimumSize(new Dimension(150, 20));
         scalarField.setPreferredSize(new Dimension(150, 20));
         scalarPanel.setLayout(new GridBagLayout());
         scalarNamePanel.setLayout(new GridBagLayout());

         Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.EAST, 0, 0);


         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new GridBagLayout());

         Constrain.setConstraints(leftPanel, new JLabel("              "), 0, 0,
                                  1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 0, 0);
         Constrain.setConstraints(leftPanel,
                                  newAttributeLabel =
                                  new JLabel("New attribute label:"), 0, 0, 1,
                                  1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.NORTH, 0, 0);
         Constrain.setConstraints(leftPanel, new JLabel("   "), 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JLabel("   "), 0, 2, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);

         Constrain.setConstraints(leftPanel, columnPanel, 0, 3, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, operationPanel, 0, 4, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, booleanPanel, 0, 5, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTH, 1, 0);


         Constrain.setConstraints(leftPanel, scalarPanel, 0, 6, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JLabel(), 0, 7, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);

         newColumnList = new JList();
         newColumnModel = new DefaultListModel();
         newColumnList.setModel(newColumnModel);
         newColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
// added 3.25.2004 by DC
         final ListSelectionModel rowSel = newColumnList.getSelectionModel();
         rowSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         rowSel.addListSelectionListener(new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {

                  if (e.getValueIsAdjusting()) {
                     return;
                  }

                  if (rowSel.isSelectionEmpty()) {
                     selectedItem = null;
                     newNameField.setText("");
                     gui.getTextArea().setText("");

                     return;
                  } else {
                     int selRow = rowSel.getMinSelectionIndex();

                     Construction ff =
                        (Construction) newColumnModel.get(selRow);
                     selectedItem = ff;
                     selectedIndex = selRow;
                     newNameField.setText(ff.label);
                     gui.getTextArea().setText(ff.expression);
                  }
               }
            });

         if (constructions != null) {

            for (int i = 0; i < constructions.length; i++) {

               Construction current = (Construction) constructions[i];

               try {
                  expression.setLazyExpression(current.expression, newLabels,
                                               newTypes);
               } catch (Exception e) { } // this can't really happen, since it
                                         // would have been caught on the last
                                         // run

               newColumnModel.addElement(current);

            }
         }

         deleteButton = new JButton("Delete");
         deleteButton.addActionListener(this);

         thatPane = new JScrollPane(newColumnList);
         thatPane.setPreferredSize(new Dimension(200, 200));

         rightPanel = new JPanel();
         rightPanel.setLayout(new GridBagLayout());
         rightPanel.setPreferredSize(new Dimension(thatPane.getPreferredSize()));

         Constrain.setConstraints(rightPanel,
                                  new JLabel("New attributes defined:  "), 0, 0,
                                  1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(rightPanel, thatPane, 0, 1, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(rightPanel, deleteButton, 0, 2, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.SOUTHEAST, 0, 0);


         guiPanel = new JPanel();
         guiPanel.setLayout(new GridBagLayout());
         constructionLabel = new JLabel("Construction String:");

         Constrain.setConstraints(guiPanel, newNamePanel, 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(guiPanel, new JSeparator(), 0, 1, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.NORTHWEST, 0, 0);

         Constrain.setConstraints(guiPanel, constructionLabel, 0, 2, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(guiPanel, gui, 0, 3, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);


         JPanel topPanel = new JPanel();
         topPanel.setLayout(new GridBagLayout());

         Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
                                  GridBagConstraints.VERTICAL,
                                  GridBagConstraints.WEST, 0, 1);
         Constrain.setConstraints(topPanel, guiPanel, 1, 0, 1, 1,
                                  GridBagConstraints.BOTH,
                                  GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(topPanel,
                                  new JSeparator(SwingConstants.VERTICAL), 2, 0,
                                  1, 1,
                                  GridBagConstraints.VERTICAL,
                                  GridBagConstraints.EAST, 0, 1);
         Constrain.setConstraints(topPanel, rightPanel, 3, 0, 1, 1,
                                  GridBagConstraints.VERTICAL,
                                  GridBagConstraints.EAST, 0, 1);

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);
         helpButton = new JButton("Help");
         helpButton.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {

                  help.setVisible(true);
               }
            });

         bottomPanel = new JPanel();

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
         Constrain.setConstraints(bottomPanel, doneButton, 3, 0, 1, 1,
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

      } // end method initialize


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

   } // end class ColumnConstructionGUI

   // headless conversion support


} // AttributeConstruction
