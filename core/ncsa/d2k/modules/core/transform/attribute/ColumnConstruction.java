package ncsa.d2k.modules.core.transform.attribute;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * <code>ColumnConstruction</code> is a simple user interface that facilitates
 * the construction of new columns in a <code>MutableTable</code> from operations
 * on existing columns in the table.
 *
 * <code>ColumnConstruction</code> works by constructing
 * <code>ColumnExpression</code>s; subsequently, its behavior and order of
 * operations are defined in the documentation for that class.
 *
 * @author gpape
 */
public class ColumnConstruction extends UIModule {

/******************************************************************************/
/* Module methods                                                             */
/******************************************************************************/

   public String getModuleName() {
      return "Construct New Columns";
   }

   public String getModuleInfo() {
      // return "Allows the user to construct new columns from operations on existing columns.";
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module provides a GUI that enables the user to construct ");
      sb.append("new columns in a table from operations on existing columns.");
      sb.append("</p>");
      sb.append("<p>Detailed Description: ");
      sb.append("This module provides a GUI used to specify expression strings. ");
      sb.append("These expressions are interpreted as operations on existing ");
      sb.append("columns in the table and are used to construct new columns. ");
      sb.append("When the GUI is dismissed, the information needed to construct ");
      sb.append("these new columns is encapsulated in a <i>Transformation</i> ");
      sb.append("object that can be applied downstream in order to actually ");
      sb.append("add the new columns to the table.");
      sb.append("</p><p>");
      sb.append("The available operations on numeric columns are addition, ");
      sb.append("subtraction, multiplication, division, and modulus. The ");
      sb.append("operations available on boolean columns are AND and OR.");
      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("Column construction operations can only be performed on the ");
      sb.append("numeric and boolean columns of a table. Other columns will ");
      sb.append("be ignored, but they will not be modified.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not immediately modify its input data. ");
      sb.append("Rather, its output is a <i>Transformation</i> that can then ");
      sb.append("be used to modify the table.");
      sb.append("</p>");
      return sb.toString();
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
      return i;
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Mutable Table";
      return null;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A MutableTable containing columns to be used in construction.";
      return "ColumnConstruction has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
      return o;
   }

   public String getOutputName(int index) {
      if (index == 0)
         return "Transformation";
      return null;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "The transformation to construct the new columns.";
      return "ColumnConstruction has no such output.";
   }

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
      return new ColumnConstructionGUI();
   }

/******************************************************************************/
/* properties                                                                 */
/******************************************************************************/

   private String _lastExpression = "";

   public String getLastExpression() {
      return _lastExpression;
   }

   public void setLastExpression(String value) {
      _lastExpression = value;
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0]; // so that "last expression" property
                                         // is invisible
   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   protected class ColumnConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      private ExpressionGUI gui;
      private ColumnExpression expression;
      private MutableTable table;

      private ColumnExpression[] newExpressions;  // for use in constructing
      private String[]           newLabels,       // the transformation
                                 newExpressionStrings;
      private int[]              newTypes;

      private JButton addColumnButton, addOperationButton, addBooleanButton,
                      deleteButton, abortButton, doneButton, helpButton;
      private JComboBox columnBox, operationBox, booleanBox;
      private JTextField newNameField;

      private JList newColumnList;
      private DefaultListModel newColumnModel;

      private HashMap stringsToColumnBoxEntries;

      private ViewModule mod;

      public void initView(ViewModule m) {
         mod = m;
      }

      public void setInput(Object o, int i) {
         if (i != 0) return;
         table = (MutableTable)o;
         initialize();
      }

      private void initialize() {

         this.removeAll();

         expression = new ColumnExpression(table);

         gui = new ExpressionGUI(expression, true);
         gui.addExpressionListener(this);

         gui.getTextArea().setText(_lastExpression);

         newNameField = new JTextField(16);

         stringsToColumnBoxEntries = new HashMap();

         JPanel newNamePanel = new JPanel();
         newNamePanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(newNamePanel,
            new JLabel("New column label:"), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(newNamePanel, newNameField, 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);

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

                  StringBuffer buffer = new StringBuffer(table.getColumnLabel(i));

                  for (int j = 0; j < buffer.length(); j++) {

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

                  }

                  columnBox.addItem(buffer.toString());

            }

         }

         addColumnButton = new JButton(
               new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addColumnButton.addActionListener(this);

         JPanel columnPanel = new JPanel();
         columnPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(columnPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(columnPanel, columnBox, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(columnPanel, addColumnButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         operationBox = new JComboBox();
         operationBox.addItem("+");
         operationBox.addItem("-");
         operationBox.addItem("*");
         operationBox.addItem("/");
         operationBox.addItem("%");

         addOperationButton = new JButton(
               new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addOperationButton.addActionListener(this);

         JPanel operationPanel = new JPanel();
         operationPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         booleanBox = new JComboBox();
         booleanBox.addItem("&&");
         booleanBox.addItem("||");

         addBooleanButton = new JButton(
               new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addBooleanButton.addActionListener(this);

         JPanel booleanPanel = new JPanel();
         booleanPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(leftPanel, newNamePanel, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JSeparator(), 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, columnPanel, 0, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, operationPanel, 0, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, booleanPanel, 0, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JLabel(), 0, 5, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         newColumnList = new JList();
         newColumnModel = new DefaultListModel();
         newColumnList.setModel(newColumnModel);
         newColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         deleteButton = new JButton("Delete");
         deleteButton.addActionListener(this);

         JPanel rightPanel = new JPanel();
         rightPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(rightPanel, new JLabel("New columns defined:  "), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(rightPanel, new JScrollPane(newColumnList), 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(rightPanel, deleteButton, 0, 2, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST, 0, 0);

         JPanel guiPanel = new JPanel();
         guiPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(guiPanel, new JLabel("Construction string:"), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(guiPanel, gui, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         JPanel topPanel = new JPanel();
         topPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.WEST, 0, 1);
         Constrain.setConstraints(topPanel, guiPanel, 1, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(topPanel, new JSeparator(SwingConstants.VERTICAL), 2, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.EAST, 0, 1);
         Constrain.setConstraints(topPanel, rightPanel, 3, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.EAST, 0, 1);

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);
         helpButton = new JButton("Help");
         helpButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               HelpWindow help = new HelpWindow();
               help.setVisible(true);
            }
         });

         JPanel bottomPanel = new JPanel();
         bottomPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(bottomPanel, helpButton, 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(bottomPanel, abortButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(bottomPanel, doneButton, 3, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, topPanel, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(this, new JSeparator(), 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);
         Constrain.setConstraints(this, bottomPanel, 0, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);

      }

      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == addColumnButton)
            gui.getTextArea().insert((String)columnBox.getSelectedItem(),
               gui.getTextArea().getCaretPosition());

         else if (src == addOperationButton)
            gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
               gui.getTextArea().getCaretPosition());

         else if (src == addBooleanButton)
            gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
               gui.getTextArea().getCaretPosition());

         else if (src == abortButton)
            viewCancel();

         else if (src == doneButton) {
            pushOutput(new ColumnTransformation(newColumnModel.toArray()), 0);
            viewDone("Done");
         }

         else if (src == deleteButton) {
            int selected = newColumnList.getSelectedIndex();
            if (selected != -1) {
               columnBox.removeItem(((Construction)(newColumnModel.elementAt(selected))).label);
               newColumnModel.removeElementAt(selected);
            }
         }

      }

      public void expressionChanged(Object evaluation) {

         if (newNameField.getText().length() == 0)
            JOptionPane.showMessageDialog(this,
               "You must specify a new column name.",
               "ColumnConstruction", JOptionPane.ERROR_MESSAGE);

         else {

            /*
            switch (expression.evaluateType()) {

               case ColumnExpression.TYPE_BOOLEAN:
                  boolean[] b = (boolean[])evaluation;
                  table.addColumn(b);
                  break;
               case ColumnExpression.TYPE_BYTE:
                  byte[] bb = (byte[])evaluation;
                  table.addColumn(bb);
                  break;
               case ColumnExpression.TYPE_DOUBLE:
                  double[] d = (double[])evaluation;
                  table.addColumn(d);
                  break;
               case ColumnExpression.TYPE_FLOAT:
                  float[] f = (float[])evaluation;
                  table.addColumn(f);
                  break;
               case ColumnExpression.TYPE_INTEGER:
                  int[] I = (int[])evaluation;
                  table.addColumn(I);
                  break;
               case ColumnExpression.TYPE_LONG:
                  long[] l = (long[])evaluation;
                  table.addColumn(l);
                  break;
               case ColumnExpression.TYPE_SHORT:
                  short[] s = (short[])evaluation;
                  table.addColumn(s);
                  break;
               default:
                  JOptionPane.showMessageDialog(this,
                     "Error in ColumnConstruction. Check your expression.",
                     "ColumnConstruction", JOptionPane.ERROR_MESSAGE);
                  return;

            }
            */

            columnBox.addItem(newNameField.getText());

            StringBuffer newLabelBuffer = new StringBuffer(newNameField.getText());
            for (int i = 0; i < newLabelBuffer.length(); i++)
               if (newLabelBuffer.charAt(i) == '\\') {
                  newLabelBuffer.deleteCharAt(i);
                  i--;
               }

            // determine new expression, label, and type

            ColumnExpression newExp = new ColumnExpression(table);
            try {
               // System.out.println("new " + newLabels + " " + newTypes);
               newExp.setLazyExpression(gui.getTextArea().getText(), newLabels, newTypes);
            }
            catch(ExpressionException e) {
               JOptionPane.showMessageDialog(this,
                     "Error in ColumnConstruction. Check your expression.",
                     "ColumnConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }

            String newLab = newLabelBuffer.toString();
            String newStr = gui.getTextArea().getText();
            int newTyp = newExp.evaluateType();

            /*
            System.out.println("-----");
            System.out.println(newExp);
            System.out.println(newLab);
            System.out.println(newTyp);
            System.out.println("-----");
            */

            // add to arrays of new expressions, labels, types

            if (newExpressions == null) {
               newExpressions = new ColumnExpression[1];
               newExpressions[0] = newExp;
            }
            else {
               ColumnExpression[] newNewExp = new ColumnExpression[newExpressions.length + 1];
               for (int i = 0; i < newExpressions.length; i++)
                  newNewExp[i] = newExpressions[i];
               newNewExp[newExpressions.length] = newExp;
               newExpressions = newNewExp;
            }

            if (newLabels == null) {
               newLabels = new String[1];
               newLabels[0] = newLab;
            }
            else {
               String[] newNewLab = new String[newLabels.length + 1];
               for (int i = 0; i < newLabels.length; i++)
                  newNewLab[i] = newLabels[i];
               newNewLab[newLabels.length] = newLab;
               newLabels = newNewLab;
            }

            if (newExpressionStrings == null) {
               newExpressionStrings = new String[1];
               newExpressionStrings[0] = newStr;
            }
            else {
               String[] newNewStr = new String[newExpressionStrings.length + 1];
               for (int i = 0; i < newExpressionStrings.length; i++)
                  newNewStr[i] = newExpressionStrings[i];
               newNewStr[newExpressionStrings.length] = newStr;
               newExpressionStrings = newNewStr;
            }

            if (newTypes == null) {
               newTypes = new int[1];
               newTypes[0] = newTyp;
            }
            else {
               int[] newNewTyp = new int[newTypes.length + 1];
               for (int i = 0; i < newTypes.length; i++)
                  newNewTyp[i] = newTypes[i];
               newNewTyp[newTypes.length] = newTyp;
               newTypes = newNewTyp;
            }

            // table.setColumnLabel(newLabelBuffer.toString(), table.getNumColumns() - 1);
            // expression = new ColumnExpression(table);
            // gui.setExpression(expression);

            newExp = new ColumnExpression(table);
            try {
               newExp.setLazyExpression(gui.getTextArea().getText(), newLabels, newTypes);
            }
            catch(ExpressionException e) {
               JOptionPane.showMessageDialog(this,
                     "Error in ColumnConstruction. Check your expression.",
                     "ColumnConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }
            gui.setExpression(newExp);

            newColumnModel.addElement(new Construction(newNameField.getText(), gui.getTextArea().getText()));

            _lastExpression = gui.getTextArea().getText();

            newNameField.setText("");
            gui.getTextArea().setText("");

         }

      }

   }

/******************************************************************************/
/* help facilities                                                            */
/******************************************************************************/

   private class HelpWindow extends JD2KFrame {
      public HelpWindow() {
         super ("ColumnConstruction Help");
         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

   private String getHelpString() {

      StringBuffer sb = new StringBuffer();
      sb.append("<html><body><h2>ColumnConstruction</h2>");
      sb.append("This module allows a user to construct new columns of a ");
      sb.append("table from operations on existing columns.");
      sb.append("<br><br>");
      sb.append("The available operations on numeric columns are addition, ");
      sb.append("subtraction, multiplication, division, and modulus. The ");
      sb.append("operations available on boolean columns are AND and OR. ");
      sb.append("The user can construct an expression for the new column ");
      sb.append("using the lists of columns and operators on the left. ");
      sb.append("It is important that this expression be well-formed: that ");
      sb.append("parentheses match, that column labels surround operators, ");
      sb.append("and so forth.");
      sb.append("<br><br>");
      sb.append("In the absence of parentheses, the order of operations is ");
      sb.append("as follows: AND, OR, division and multiplication, modulus, ");
      sb.append("addition and subtraction (all from left to right). Note that ");
      sb.append("AND and OR cannot be applied to numeric columns, and that ");
      sb.append("the numeric operators cannot be applied to boolean columns. ");
      sb.append("Thus a given expression may involve numeric columns or boolean ");
      sb.append("columns, but not both.");
      sb.append("<br><br>");
      sb.append("The user must also specify the name for the new column in ");
      sb.append("the field at the top left. <b>Note carefully</b> that any characters in ");
      sb.append("this new name that could be interpreted as operators (such ");
      sb.append("as parentheses, or a hyphen, which would be interpreted as ");
      sb.append("a subtraction operator) <b>must</b> be preceded by a ");
      sb.append("backslash character in order to distinguish them from those ");
      sb.append("operators.");
      sb.append("</body></html>");
      return sb.toString();

   }

/******************************************************************************/
/* the type used to store column constructions                                */
/******************************************************************************/

   private class Construction {

      String label, expression;

      Construction(String label, String expression) {
         this.label = label;
         this.expression = expression;
      }

      public String toString() {
         return label + ": " + expression;
      }

   }

/******************************************************************************/
/* the output Transformation                                                  */
/******************************************************************************/

   private class ColumnTransformation implements Transformation {

      private Object[] constructions;

      ColumnTransformation(Object[] constructions) {
         this.constructions = constructions;
      }

      public boolean transform(MutableTable table) {

         if (constructions == null || constructions.length == 0)
            return true;

         for (int i = 0; i < constructions.length; i++) {

            ColumnExpression exp = new ColumnExpression(table);
            Construction current = (Construction)constructions[i];

            Object evaluation = null;
            try {
               exp.setExpression(current.expression);
               evaluation = exp.evaluate();
            }
            catch(Exception e) {
               e.printStackTrace();
               return false;
            }

            switch (exp.evaluateType()) {

               case ColumnExpression.TYPE_BOOLEAN:
                  boolean[] b = (boolean[])evaluation;
                  table.addColumn(b);
                  break;
               case ColumnExpression.TYPE_BYTE:
                  byte[] bb = (byte[])evaluation;
                  table.addColumn(bb);
                  break;
               case ColumnExpression.TYPE_DOUBLE:
                  double[] d = (double[])evaluation;
                  table.addColumn(d);
                  break;
               case ColumnExpression.TYPE_FLOAT:
                  float[] f = (float[])evaluation;
                  table.addColumn(f);
                  break;
               case ColumnExpression.TYPE_INTEGER:
                  int[] I = (int[])evaluation;
                  table.addColumn(I);
                  break;
               case ColumnExpression.TYPE_LONG:
                  long[] l = (long[])evaluation;
                  table.addColumn(l);
                  break;
               case ColumnExpression.TYPE_SHORT:
                  short[] s = (short[])evaluation;
                  table.addColumn(s);
                  break;
               default:
                  return false;

            }

            table.setColumnLabel(current.label, table.getNumColumns() - 1);

         }

         return true;

      }

   }

}
