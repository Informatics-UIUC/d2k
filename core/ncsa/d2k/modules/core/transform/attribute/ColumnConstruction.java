//package ncsa.d2k.modules.projects.gpape;
package ncsa.d2k.modules.core.transform.attribute;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.JD2KFrame;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.Constrain;

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

   public String getModuleInfo() {
      return "Allows the user to construct new columns from operations on existing columns.";
   }

   public String getModuleName() {
  		return "ColumnConstruction";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.MutableTable"}; return i;
   }

   public String getInputInfo(int index) {
      if (index == 0) return "A MutableTable containing columns to be used in construction.";
      else return "ColumnConstruction has no such input.";
   }

   public String getInputName(int index) {
      if (index == 0) return "MutableTable";
      else return "No such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.table.MutableTable"}; return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0) return "The input table plus the new columns constructed by the user.";
      else return "ColumnConstruction has no such output.";
   }

   public String getOutputName(int index) {
      if (index == 0) return "MutableTable";
      else return "No such output.";
   }

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
      return new ColumnConstructionGUI();
   }

   private class ColumnConstructionGUI extends JUserPane implements ActionListener {
      private MutableTable table;
      private ColumnExpression expression;
      // private HashMap columnLookup;

      private JTextField columnNameField;

      private JList columnList, operationList;
      private DefaultListModel columnModel, operationModel;

      private JTextArea expressionArea;

      private JButton addColumnButton, addOperationButton,
         checkExpressionButton, addExpressionButton,
         abortButton, doneButton, helpButton;

      public void initView(ViewModule m) { }

      public void setInput(Object o, int i) {
         if (i != 0) return;
         if (!(o instanceof MutableTable))
            throw new RuntimeException("ColumnConstruction requires a MutableTable.");

         table = (MutableTable)o;
         initialize();
      }

      private void initialize() {
         this.removeAll();

         // columnLookup = new HashMap();

         columnNameField = new JTextField(20);

         JPanel columnNamePanel = new JPanel();
         columnNamePanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(columnNamePanel,
            new JLabel("New column label:"), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTH, 0, 0);
         Constrain.setConstraints(columnNamePanel, columnNameField, 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);

         columnList = new JList();
         columnModel = new DefaultListModel();
         columnList.setModel(columnModel);
         columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         int columnType;
         StringBuffer buffer;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            switch (columnType) {

               case ColumnTypes.BOOLEAN:
               case ColumnTypes.BYTE:
               case ColumnTypes.DOUBLE:
               case ColumnTypes.FLOAT:
               case ColumnTypes.INTEGER:
               case ColumnTypes.LONG:
               case ColumnTypes.SHORT:

                  buffer = new StringBuffer(table.getColumnLabel(i));

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

                  columnModel.addElement(buffer.toString());
                  // columnLookup.put(buffer.toString(), new Integer(i));

            }

         }

         addColumnButton = new JButton("Add");
         addColumnButton.addActionListener(this);

         JPanel columnListPanel = new JPanel();
         columnListPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(columnListPanel,
            new JLabel("Add column:"), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTH, 0, 0);
         Constrain.setConstraints(columnListPanel,
            new JScrollPane(columnList), 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(columnListPanel, addColumnButton, 1, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         operationList = new JList();
         operationModel = new DefaultListModel();
         operationList.setModel(operationModel);
         operationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         operationModel.addElement("(  )");
         operationModel.addElement("+");
         operationModel.addElement("-");
         operationModel.addElement("*");
         operationModel.addElement("/");
         operationModel.addElement("%");
         operationModel.addElement("&&");
         operationModel.addElement("||");

         addOperationButton = new JButton("Add");
         addOperationButton.addActionListener(this);

         JPanel operationListPanel = new JPanel();
         operationListPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(operationListPanel,
            new JLabel("Add operation:"), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTH, 0, 0);
         Constrain.setConstraints(operationListPanel,
            new JScrollPane(operationList), 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(operationListPanel, addOperationButton, 1, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(leftPanel, columnNamePanel, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, .2);
         Constrain.setConstraints(leftPanel, columnListPanel, 0, 1, 1, 2,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, .4);
         Constrain.setConstraints(leftPanel, operationListPanel, 0, 3, 1, 2,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, .4);

         expressionArea = new JTextArea(30, 40);
         expressionArea.getCaret().setVisible(true);
         expressionArea.setCaretColor(Color.red);
         checkExpressionButton = new JButton("Check expression for validity");
         checkExpressionButton.addActionListener(this);
         addExpressionButton = new JButton("Add this column to the table");
         addExpressionButton.addActionListener(this);

         JPanel rightPanel = new JPanel();
         rightPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(rightPanel, new JScrollPane(expressionArea), 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(rightPanel, checkExpressionButton, 0, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.SOUTH, 0, 0);
         Constrain.setConstraints(rightPanel, addExpressionButton, 0, 2, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.SOUTH, 0, 0);

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

         Box bottomBox = new Box(BoxLayout.X_AXIS);
         bottomBox.add(abortButton);
         bottomBox.add(doneButton);
         bottomBox.add(helpButton);

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, leftPanel, 0, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.WEST, 0, 1);
         Constrain.setConstraints(this, rightPanel, 1, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(this, bottomBox, 1, 1, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

      }

      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == addColumnButton)
            expressionArea.insert((String)columnList.getSelectedValue(),
               expressionArea.getCaretPosition());

         else if (src == addOperationButton)
            expressionArea.insert(" " + (String)operationList.getSelectedValue() + " ",
               expressionArea.getCaretPosition());

         else if (src == checkExpressionButton) {

            // we check expressions for validity by attempting to construct a
            // ColumnExpression from them. if this operation succeeds, the
            // expression makes sense. otherwise, an exception is thrown, and
            // we know that the expression is invalid somehow.

            try {
               expression = new ColumnExpression(table, expressionArea.getText());
            }
            catch (Exception ex) {
               JOptionPane.showMessageDialog(this,
                  "This expression does not appear to be valid.",
                  "ColumnConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }

            int t = expression.evaluateType();
            String s = "";
            switch (t) {
               case ColumnExpression.TYPE_BOOLEAN: s = "BOOLEAN"; break;
               case ColumnExpression.TYPE_BYTE: s = "BYTE"; break;
               case ColumnExpression.TYPE_DOUBLE: s = "DOUBLE"; break;
               case ColumnExpression.TYPE_FLOAT: s = "FLOAT"; break;
               case ColumnExpression.TYPE_INTEGER: s = "INTEGER"; break;
               case ColumnExpression.TYPE_LONG: s = "LONG"; break;
               case ColumnExpression.TYPE_SHORT: s = "SHORT"; break;
            }

            JOptionPane.showMessageDialog(this,
               "This expression is valid and returns type " + s,
               "ColumnConstruction", JOptionPane.INFORMATION_MESSAGE);

         }

         else if (src == addExpressionButton) {

            try {
               expression = new ColumnExpression(table, expressionArea.getText());
               expression.evaluateType();
            }
            catch (Exception x) {
               JOptionPane.showMessageDialog(this,
                  "This expression does not appear to be valid.",
                  "ColumnConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }

            if (columnNameField.getText().length() == 0)
               JOptionPane.showMessageDialog(this,
                  "You must specify a new column name.",
                  "ColumnConstruction", JOptionPane.ERROR_MESSAGE);

            else {

               switch (expression.evaluateType()) {

                  case ColumnExpression.TYPE_BOOLEAN:
                     boolean[] b = (boolean[])expression.evaluate();
                     table.addColumn(b);
                     break;
                  case ColumnExpression.TYPE_BYTE:
                     byte[] bb = (byte[])expression.evaluate();
                     table.addColumn(bb);
                     break;
                  case ColumnExpression.TYPE_DOUBLE:
                     double[] d = (double[])expression.evaluate();
                     table.addColumn(d);
                     break;
                  case ColumnExpression.TYPE_FLOAT:
                     float[] f = (float[])expression.evaluate();
                     table.addColumn(f);
                     break;
                  case ColumnExpression.TYPE_INTEGER:
                     int[] I = (int[])expression.evaluate();
                     table.addColumn(I);
                     break;
                  case ColumnExpression.TYPE_LONG:
                     long[] l = (long[])expression.evaluate();
                     table.addColumn(l);
                     break;
                  case ColumnExpression.TYPE_SHORT:
                     short[] s = (short[])expression.evaluate();
                     table.addColumn(s);
                     break;
                  default:
                     throw new RuntimeException
                     ("Error in ColumnConstruction. Check your expression.");

               }

               table.setColumnLabel(columnNameField.getText(), table.getNumColumns() - 1);
               columnModel.addElement(columnNameField.getText());
               columnNameField.setText("");
               expressionArea.setText("");

            }

         }

         else if (src == abortButton)
            viewCancel();

         else if (src == doneButton) {
            pushOutput(table, 0);
            viewDone("Done");
         }

      }

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

   }

}