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

/**
 * <code>FilterConstruction</code> is a simple user interface that facilitates
 * the creation of an expression for filtering rows from a
 * <code>MutableTable</code>. (See the documentation for
 * <code>FilterExpression</code> for details on the underlying expression
 * string and its format.)
 *
 * @author gpape
 */
public class FilterConstruction extends UIModule {

/******************************************************************************/
/* Module methods                                                             */
/******************************************************************************/

   public String getModuleInfo() {
      return "Allows the user to filter rows of a MutableTable.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The MutableTable to be filtered.";
      return "FilterConstruction has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "The transformation to filter the MutableTable.";
      return "FilterConstruction has no such output.";
   }

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
      return new FilterConstructionGUI();
   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   protected class FilterConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      private ExpressionGUI gui;
      private FilterExpression expression;
      private MutableTable table;

      private JButton addColumnButton, addScalarButton, addOperationButton,
                      addBooleanButton, abortButton, doneButton, helpButton;
      private JComboBox columnBox, operationBox, booleanBox;
      private JTextField scalarField;

      public void initView(ViewModule m) { }

      public void setInput(Object o, int i) {
         if (i != 0) return;
         table = (MutableTable)o;
         initialize();
      }

      private void initialize() {

         this.removeAll();

         expression = new FilterExpression(table);

         gui = new ExpressionGUI(expression, false);
         gui.addExpressionListener(this);

         columnBox = new JComboBox();
         for (int i = 0; i < table.getNumColumns(); i++)
            columnBox.addItem(table.getColumnLabel(i));

         addColumnButton = new JButton(">");
         addColumnButton.addActionListener(this);

         JPanel columnPanel = new JPanel();
         columnPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(columnPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(columnPanel, columnBox, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(columnPanel, addColumnButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         scalarField = new JTextField(6);

         addScalarButton = new JButton(">");
         addScalarButton.addActionListener(this);

         JPanel scalarPanel = new JPanel();
         scalarPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(scalarPanel, new JLabel(), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

         operationBox = new JComboBox();
         operationBox.addItem("==");
         operationBox.addItem("!=");
         operationBox.addItem("<");
         operationBox.addItem("<=");
         operationBox.addItem(">");
         operationBox.addItem(">=");

         addOperationButton = new JButton(">");
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

         addBooleanButton = new JButton(">");
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
         Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, scalarPanel, 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, operationPanel, 0, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, booleanPanel, 0, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(leftPanel, new JLabel(), 0, 4, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         JPanel topPanel = new JPanel();
         topPanel.setLayout(new GridBagLayout());
         Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.WEST, 0, 1);
         Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

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
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
         Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(bottomPanel, abortButton, 2, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
         Constrain.setConstraints(bottomPanel, addButton, 3, 0, 1, 1,
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

         else if (src == addScalarButton)
            gui.getTextArea().insert(scalarField.getText(),
               gui.getTextArea().getCaretPosition());

         else if (src == addOperationButton)
            gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
               gui.getTextArea().getCaretPosition());

         else if (src == addBooleanButton)
            gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
               gui.getTextArea().getCaretPosition());

         else if (src == abortButton)
            viewCancel();

      }

      public void expressionChanged(Object evaluation) {
         pushOutput(new FilterTransformation((boolean[])evaluation, false), 0);
         viewDone("Done");
      }

   }

/******************************************************************************/
/* help facilities                                                            */
/******************************************************************************/

   private class HelpWindow extends JD2KFrame {
      public HelpWindow() {
         super ("FilterConstruction Help");
         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

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
      sb.append("characters: =, !, <, >, |, &.");
      sb.append("</body></html>");

      return sb.toString();

   }

/******************************************************************************/
/* the output Transformation                                                  */
/******************************************************************************/

   private class FilterTransformation implements Transformation {

      private boolean[] filter;
      private boolean filterThese;

      FilterTransformation(boolean[] filter, boolean filterThese) {
         this.filter = filter;
         this.filterThese = filterThese;
      }

      public boolean transform(MutableTable table) {

         if (!filterThese)
            for (int i = 0; i < filter.length; i++)
               filter[i] = !filter[i];

         try {
            table.removeRowsByFlag(filter);
            table.addTransformation(this);
         }
         catch(Exception e) { return false; }

         return true;

      }

   }

}
