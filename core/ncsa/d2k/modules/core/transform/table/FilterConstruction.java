package ncsa.d2k.modules.core.transform.table;



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
import ncsa.d2k.modules.core.transform.attribute.*;

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
      return "<p>      Overview: This module will provide a gui to be employed by the user to       create"+
         " a filter to eliminate the rows of the input table which do not       meet the criterion specified."+
         "    </p>    <p>      Detailed Description: This module provides a gui used to specify      "+
         " expressions. These expressions are used to eliminate rows of the table       that do not meet"+
         " the criterion specified in those expressions. Complex       expressions are supported. When"+
         " the gui is dismissed, the filters are       all collected into a <i>Transformation</i> object"+
         " that is then passed as       an output. The expressions are not applied to the data by this"+
         " module,       that can be done downstream using the transformation object passed.    </p> "+
         "   <p>      The expressions which can be applied to columns support the following       operators:"+
         " &quot;&gt;&quot;, &quot;&gt;=&quot;, &quot;&lt;&quot;, &quot;&lt;=&quot;, &quot;!=&quot; and"+
         " &quot;==&quot;. These operators are       greater than, greater than or equal to, less than,"+
         " less than or equal       to, not equal, and equal. The result of these expressions, which we"+
         " will       call attribute expressions, are either true or false. We can construct       more"+
         " complex expressions using the boolean operators &quot;and&quot; (&amp;&amp;) or       &quot;or&quot;(||)"+
         " on the results of the attribute expressions. For example, if we       had an attribute named"+
         " <i>sepal_length</i> which is numeric, a filter       resulting from the following expression"+
         " (sepal_length &gt;= 6.0) &amp;&amp;       (sepal_length &lt;= 7.0) will result in the removal"+
         " of all rows where the       sepal_length is not in the range from 6.0 to 7.0, inclusive.  "+
         "  </p>    <p>      Data Type Restrictions: Filter operations are supported for numeric data"+
         "       only at this point.    </p>";
   }

   public String[] getInputTypes() {
      String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
      return types;
   }

   public String getInputInfo(int index) {
      switch (index) {
         case 0: return "This is the mutable table for which a filter will be constructed.";
         default: return "No such input";
      }
   }

   public String[] getOutputTypes() {
      String[] types = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
      return types;
   }

   public String getOutputInfo(int index) {
      switch (index) {
         case 0: return "This is the transformation the user constructed using the gui associated     with this module.";
         default: return "No such output";
      }
   }

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
     return new FilterConstructionGUI();
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

   private static String scalar = "FilterConstructionINTERNALscalar";

   protected class FilterConstructionGUI extends JUserPane
     implements ActionListener, ExpressionListener {

     private ExpressionGUI gui;
     private FilterExpression expression;
     private MutableTable table;

     private JButton addColumnButton, addScalarButton, addOperationButton,
                 addBooleanButton, abortButton, doneButton, helpButton;
     private JComboBox columnBox, operationBox, booleanBox;
     private JTextField scalarField;

     private JPanel comboOrFieldPanel;
     private CardLayout nominalOrScalarLayout;
     private HashMap nominalComboBoxLookup; // look up JComboBoxes for nominal
                                  // columns; key = column #
     private int nominalShowing = -1; // which nominal combobox is showing?
                              // -1 if scalar textfield is showing

     private ViewModule mod;

     private boolean initialized = false;

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

       expression = new FilterExpression(table);

       gui = new ExpressionGUI(expression, false);
       gui.addExpressionListener(this);

       gui.getTextArea().setText(_lastExpression);

       columnBox = new JComboBox();
       columnBox.addActionListener(this);
       for (int i = 0; i < table.getNumColumns(); i++)
         columnBox.addItem(table.getColumnLabel(i));

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

       ///////////////////////////////////////////////////////////////////////
       // scalar columns will require textfield input; nominal columns will
       // require a combobox of nominal values:
       // {

       scalarField = new JTextField();

       addScalarButton = new JButton(
          new ImageIcon(mod.getImage("/images/addbutton.gif")));
       addScalarButton.addActionListener(this);

       nominalComboBoxLookup = new HashMap();
       comboOrFieldPanel = new JPanel();
       nominalOrScalarLayout = new CardLayout();
       comboOrFieldPanel.setLayout(nominalOrScalarLayout);
       comboOrFieldPanel.add(scalarField, scalar);

       for (int i = 0; i < table.getNumColumns(); i++) {

         if (!table.isColumnNominal(i))
            continue;

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
       JPanel scalarPanel = new JPanel();
       scalarPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(scalarPanel, new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       */

       JPanel nominalOrScalarPanel = new JPanel();
       nominalOrScalarPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(nominalOrScalarPanel,
         new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(nominalOrScalarPanel,
         comboOrFieldPanel, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(nominalOrScalarPanel,
         addScalarButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       // }
       ///////////////////////////////////////////////////////////////////////

       operationBox = new JComboBox();
       operationBox.addItem("==");
       operationBox.addItem("!=");
       operationBox.addItem("<");
       operationBox.addItem("<=");
       operationBox.addItem(">");
       operationBox.addItem(">=");

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
       Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
       Constrain.setConstraints(leftPanel, nominalOrScalarPanel, 0, 1, 1, 1,
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

       initialized = true;

     }

     public void actionPerformed(ActionEvent e) {

       Object src = e.getSource();

       if (src == columnBox && initialized) {

         int index = columnBox.getSelectedIndex();

         if (table.isColumnNominal(index)) {

            nominalShowing = index;

            nominalOrScalarLayout.show(comboOrFieldPanel,
              table.getColumnLabel(index));

         }
         else {

            nominalShowing = -1;

            nominalOrScalarLayout.show(comboOrFieldPanel, scalar);

         }

       }

       else if (src == addColumnButton)
         gui.getTextArea().insert((String)columnBox.getSelectedItem(),
            gui.getTextArea().getCaretPosition());

       else if (src == addScalarButton) {

         if (nominalShowing < 0) {

            gui.getTextArea().insert(scalarField.getText(),
                              gui.getTextArea().getCaretPosition());

         }
         else {

            JComboBox combo = (JComboBox)nominalComboBoxLookup.get(
              new Integer(nominalShowing));

            gui.getTextArea().insert(
              "'" + combo.getSelectedItem() + "'",
              gui.getTextArea().getCaretPosition());

         }

       }

       else if (src == addOperationButton)
         gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
            gui.getTextArea().getCaretPosition());

       else if (src == addBooleanButton)
         gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
            gui.getTextArea().getCaretPosition());

       else if (src == abortButton)
         viewCancel();

     }

     private Vector getUniqueValues(int columnIndex) {

       if (table == null)
         return null;

       int numRows = table.getNumRows();

       HashMap columnValues = new HashMap();

       int index = 0;
       for (int i = 0; i < numRows; i++) {

         String str = table.getString(i, columnIndex);

         if (!columnValues.containsKey(str))
            columnValues.put(str, new Integer(index++));

       }

       return new Vector(columnValues.keySet());

     }

     public void expressionChanged(Object evaluation) {
        _lastExpression = gui.getTextArea().getText();
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
     sb.append("characters: =, !, <, >, |, &. You may specify nominal ");
     sb.append("values from columns; i.e., ColumnA != 'value', but those ");
     sb.append("values must be enclosed in tick marks (single quotes) so ");
     sb.append("as to distinguish them from column labels.");
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
       // if there are no filters, do nothing.
       if (filter == null)
          return true;

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


   /**
    * Return the human readable name of the module.
    * @return the human readable name of the module.
    */
   public String getModuleName() {
      return "Filter Construction";
   }

   /**
    * Return the human readable name of the indexed input.
    * @param index the index of the input.
    * @return the human readable name of the indexed input.
    */
   public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Mutable Table";
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
         case 0:
            return "Transformation";
         default: return "NO SUCH OUTPUT!";
      }
   }
}
