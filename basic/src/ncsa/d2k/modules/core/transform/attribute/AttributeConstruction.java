package ncsa.d2k.modules.core.transform.attribute;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * <code>AttributeConstruction</code> is a simple user interface that facilitates
 * the construction of new attributes in a <code>MutableTable</code> from operations
 * on existing attributes in the table.
 *
 * <code>AttributeConstruction</code> works by constructing
 * <code>ColumnExpression</code>s; subsequently, its behavior and order of
 * operations are defined in the documentation for that class.
 *
 * @author gpape
 */
public class AttributeConstruction extends UIModule {

/******************************************************************************/
/* Module methods                                                             */
/******************************************************************************/

   public String getModuleName() {
      return "Attribute Construction";
   }

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
      sb.append("subtraction, multiplication, division, and modulus. The ");
      sb.append("operations available on boolean attributes are AND and OR.");
      sb.append("</p><p>Data Type Restrictions: ");
      sb.append("Attribute construction operations can only be performed on the ");
      sb.append("numeric and boolean attributes of a table. Other attributes will ");
      sb.append("be ignored, but they will not be modified.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module <i>might</i> immediately modify the table: ");
      sb.append("attributes with blank labels will be assigned default ones. ");
      sb.append("Other than that, this module does not modify its input data. ");
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
         return "A MutableTable containing attributes to be used in construction.";
      return "Attribute Construction has no such input.";
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
         return "The transformation to construct the new attributes.";
      return "Attribute Construction has no such output.";
   }

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
      return new ColumnConstructionGUI();
   }

/******************************************************************************/
/* properties                                                                 */
/******************************************************************************/

   private String[] _newLab = null;
   public Object getNewLab() { return _newLab; }
   public void setNewLab(Object value) { _newLab = (String[])value; }

   private int[] _newTyp = null;
   public Object getNewTyp() { return _newTyp; }
   public void setNewTyp(Object value) { _newTyp = (int[])value; }

   private Object[] _lastCons = null;
   public Object getLastCons() { return _lastCons; }
   public void setLastCons(Object value) { _lastCons = (Object[])value; }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   protected class ColumnConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      private ExpressionGUI gui;
      private ColumnExpression expression;
      private MutableTable table;

      private String[]           newLabels; // for use in constructing
      private int[]              newTypes;  // the transformation

      private JButton addColumnButton, addOperationButton, addBooleanButton,
                      deleteButton, abortButton, doneButton, helpButton, addScalarButton;
      private JComboBox columnBox, operationBox, booleanBox;
      private JTextField newNameField;
      private JTextField scalarField;
      private JScrollPane thatPane;
      private HelpWindow help = new HelpWindow();

      private JList newColumnList;
      private DefaultListModel newColumnModel;
      private JPanel columnPanel;
      private HashMap stringsToColumnBoxEntries;

      private Object[] constructions;

      private ViewModule mod;

      public void initView(ViewModule m) {
         mod = m;
         mod.setWindowName( getAlias() );
      }

      public void setInput(Object obj, int ind) {
         if (ind != 0)
            return;

         table = (MutableTable)obj;

         for (int i = 0; i < table.getNumColumns(); i++) {
            String label = table.getColumnLabel(i);
            if (label == null || label.length() == 0)
               table.setColumnLabel("column_" + i, i);
         }

         initialize();
      }

      private void initialize() {


         this.removeAll();
         expression = new ColumnExpression(table);

         gui = new ExpressionGUI(expression, true);
         gui.addExpressionListener(this);

         newLabels = _newLab;
         newTypes = _newTyp;
         constructions = _lastCons;

         newNameField = new JTextField(16);

         stringsToColumnBoxEntries = new HashMap();

         JPanel newNamePanel = new JPanel();
         newNamePanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(newNamePanel,
            new JLabel("New attribute label:"), 0, 0, 1, 1,
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

         if (newLabels != null) {
            for (int i = 0; i < newLabels.length; i++) {
               columnBox.addItem(newLabels[i]);
            }
         }

         addColumnButton = new JButton(
               new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addColumnButton.addActionListener(this);

         columnPanel = new JPanel();
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

         JPanel scalarPanel = new JPanel();
         JPanel scalarNamePanel = new JPanel();


         addScalarButton = new JButton(new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addScalarButton.addActionListener(this);
         scalarField = new JTextField(16);

         scalarField.setMinimumSize(new Dimension(150, 20));
         scalarField.setPreferredSize(new Dimension(150, 20));
         scalarPanel.setLayout(new GridBagLayout());
         scalarNamePanel.setLayout(new GridBagLayout());

         Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

        Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
                 GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
        Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
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



      Constrain.setConstraints(leftPanel, scalarPanel ,0, 6, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.NORTH, 1, 0);
      Constrain.setConstraints(leftPanel, new JLabel(), 0, 7, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         newColumnList = new JList();
         newColumnModel = new DefaultListModel();
         newColumnList.setModel(newColumnModel);
         newColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         if (constructions != null) {
            for (int i = 0; i < constructions.length; i++) {

               AttributeTransform.Construction current =
                  (AttributeTransform.Construction)constructions[i];

               try {
                  expression.setLazyExpression(current.expression, newLabels, newTypes);
               }
               catch(Exception e) { } // this can't really happen, since it
                                      // would have been caught on the last run

               newColumnModel.addElement(current);

            }
         }

         deleteButton = new JButton("Delete");
         deleteButton.addActionListener(this);


         JPanel rightPanel = new JPanel();

         thatPane = new JScrollPane(newColumnList);
         thatPane.setPreferredSize(new Dimension(200,200));

         rightPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(rightPanel, new JLabel("New attributes defined:  "), 0, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(rightPanel, thatPane, 0, 1, 1, 1,
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

      else if (src == addScalarButton)
                 gui.getTextArea().insert(" " + scalarField.getText() + " ",
                    gui.getTextArea().getCaretPosition());

         else if (src == abortButton) {
           help.setVisible(false);
           viewCancel();
         }

         else if (src == doneButton) {
           help.setVisible(false);
            pushOutput(new AttributeTransform(newColumnModel.toArray()), 0);
            viewDone("Done");
         }

         else if (src == deleteButton) {

            int selected = newColumnList.getSelectedIndex();
            if (selected != -1) {

               String label = ((AttributeTransform.Construction)(newColumnModel.elementAt(selected))).label;

               columnBox.removeItem(label);
               newColumnModel.removeElementAt(selected);

               if (newLabels != null) {

                  String[] newNewLabels = new String[newLabels.length - 1];
                  int[] newNewTypes = new int[newTypes.length - 1];

                  int index = 0;
                  for (index = 0; index < newLabels.length; index++) {
                     if (newLabels[index].equals(label)) {
                        break;
                     }
                     else {
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

               _newLab = newLabels;
               _newTyp = newTypes;
               _lastCons = newColumnModel.toArray();

            }
         }


      }

      public void expressionChanged(Object evaluation) {

         if (newNameField.getText().length() == 0)
            JOptionPane.showMessageDialog(this,
               "You must specify a new attribute name.",
               "AttributeConstruction", JOptionPane.ERROR_MESSAGE);

         else {
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
                     "Error in AttributeConstruction. Check your expression.",
                     "AttributeConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }

            String newLab = newLabelBuffer.toString();
            String newStr = gui.getTextArea().getText();
            int newTyp = newExp.evaluateType();

            // add to arrays of new expressions, labels, types

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

            newExp = new ColumnExpression(table);
            try {
               newExp.setLazyExpression(gui.getTextArea().getText(), newLabels, newTypes);
            }
            catch(ExpressionException e) {
               JOptionPane.showMessageDialog(this,
                     "Error in AttributeConstruction. Check your expression.",
                     "AttributeConstruction", JOptionPane.ERROR_MESSAGE);
               return;
            }
            gui.setExpression(newExp);

            AttributeTransform.Construction added =
               new AttributeTransform(null).new Construction(
                  newNameField.getText(), gui.getTextArea().getText());
            newColumnModel.addElement(added);
            newColumnList.setMinimumSize(new Dimension(200, 200));

            _newLab = newLabels;
            _newTyp = newTypes;
            _lastCons = newColumnModel.toArray();

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
         super ("AttributeConstruction Help");
         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

   private String getHelpString() {

      StringBuffer sb = new StringBuffer();
      sb.append("<html><body><h2>AttributeConstruction</h2>");
      sb.append("This module allows a user to construct new attributes in a ");
      sb.append("table from operations on existing attributes.");
      sb.append("<br><br>");
      sb.append("The available operations on numeric attributes are addition, ");
      sb.append("subtraction, multiplication, division, and modulus. The ");
      sb.append("operations available on boolean attributes are AND and OR. ");
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

   }

}
//
// QA comments:
// 3-4-03 vered started qa:
// 3-6-03 sent back to greg to support default labels.
// 7-17-03 Ruth changed module name to be Attribute Construction.
//         Deleted commented out code - it's in CVS if needed. 
//         Explicitly call setWindowName so it matches module alias.
//

