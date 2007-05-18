package ncsa.d2k.modules.core.transform.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import java.sql.*;

import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.modules.core.util.*;

/**
 * <code>SQLFilterConstruction</code> is a simple user interface that facilitates
 * the creation of an expression for filtering rows from a
 * <code>database table</code>. (See the documentation for
 * <code>FilterExpression</code> for details on the underlying expression
 * string and its format.)
 *
 * @author Dora Cai based on gpape's non-database version
 *
 * @todo: the user interface has a problem:
 * at the beginning it shows the first attribute and its unique values.
 * when changing to another attribute, the drop down list of the attributes does not
 * change, but when you add one of this irrelevant values to the expression -
 * it inserts a relevant value.
 * sometimes it shows the relevant vlues of an attributes but when you add one
 * attribute it, for somewhat reason, adds its sibling attribute. strange.
 */
public class SQLFilterConstruction extends HeadlessUIModule {

    /** name of the table */
  private String tableName = "";
    /** connection wrapper */
  private ConnectionWrapper cw;
  /** ArrayList for column types */
  private ArrayList colTypes;
  /** ArrayList for column names */
  private ArrayList colNames;
  /** ExampleTable to keep the meta data */
  private ExampleTable table;


    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "SQL Filter Construction";
    }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        String s = "<p> Overview: ";
        s += "This module helps the user to construct a filtering expression. </p>";
        s += "<p> Detailed Description: ";
        s += "This module allows the user to specify the query condition ";
        s += "that filters rows from a database table. Details can be found ";
        s += "in the module's online help. </p>";

        s += "<P><U>NOTE</U>:<br>When running the module in Headless mode (when 'Supress User Interface Display' is set to true):<br>";
        s += "It is highly recommneded to set the filter using the first GUI of this module, ";
        s += "and only then run it in a Headless mode.<BR>Should you choose to set the filter manually, ";
        s += "the query must be in an sql format according to the foloowing:";
        s += "<UL> <li>'!=' should be replaced with '&gt;&lt;'</li>";
        s += "<li>'==' should be replaced with '='</li>";
        s += "<li>'&&' should be replaced with 'and'</li>";
        s += "<li>'||' should be replaced with 'or'</li>";
        s += "<li>'null' should be uppercased - 'NULL'</li></ul>";

        s += "<P>Missing Values Handling: When the filter expression is edited via " +
                "the properties editor and 'Supress User Interface Display' is set to true, " +
                "if the user whishes to include missing values " +
                "in the result dataset, each simple condition should be paired with the " +
                "condition 'ATT_NAME is NULL' using the 'or' operand. For Example: " +
                "If the basic condition is 'Att1 = value' then the pair should be " +
                "((Att1 = value) or (Att1 is NULL))'.</P>";
        s += "<p> Restrictions: ";
        s += "We currently only support Oracle, SQLServer, DB2 and MySql database.";

        return s;
    }

    /**
     * Returns the name of the input at the specified index.
     *
     * @param index Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int index) {
        if (index == 0)
            return "Database Connection";
        else if (index == 1)
            return "Selected Table";

        return null;
    }

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        String[] types = {"ncsa.d2k.modules.io.input.sql.ConnectionWrapper", "java.lang.String"};
        return types;
    }

    /**
     * Returns a description of the input at the specified index.
     *
     * @param index Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int index) {
        if (index == 0)
            return "The database connection.";
        else if (index == 1)
            return "The database table to be filtered.";
        return "SQLFilterConstruction has no such input.";
    }

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        String[] o = {"java.lang.String"};
        return o;
    }

    /**
     * Returns the name of the output at the specified index.
     *
     * @param index Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int index) {
        if (index == 0)
            return "Query Condition";
        return null;
    }

    /**
     * Returns a description of the output at the specified index.
     *
     * @param index Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int index) {
        if (index == 0)
            return "The query condition for the search. If the string is blank there will be no where clause, all records will be retrieved.";
        return "SQLFilterConstruction has no such output.";
    }

    /**
     * The list of strings returned by this method allows the module to map the
     * results returned from the pier to the position dependent outputs. The
     * order in which the names appear in the string array is the order in which
     * to assign them to the outputs.
     *
     * @return a string array containing the names associated with the outputs
     *         in the order the results should appear in the outputs.
     */
    public String[] getFieldNameMapping() {
        return null;
    }

    /**
     * Create a new instance of a UserView object that provides the user
     * interface for this user interaction module.
     *
     * @return a new instance of a UserView providing the interface for this
     *         module.
     */
    protected UserView createUserView() {
        return new FilterConstructionGUI();
    }

    /** last expression. */
   private String _lastExpression = "";

   /**
    * Get last expression property
    *
    * @return last expression property
    */
   public String getLastExpression() {
      return _lastExpression;
   }

   /**
    * Set last expression property
    *
    * @param value new last expression
    */
   public void setLastExpression(String value) {
      _lastExpression = value;
   }

  /** If set, rows with missing values will be included in the result table. */
   private boolean _includeMissingValues = true;

   /**
    * Get include missing values property
    *
    * @return missing values property
    */
   public boolean getIncludeMissingValues() {
	  return _includeMissingValues;
   }

   /**
    * Set include missing values property
    *
    * @param value true if missing values should be included
    */
   public void setIncludeMissingValues(boolean value) {
	  _includeMissingValues = value;
   }

    /**
     * Get the descriptions of the properties of this module.
     *
     * @return the PropertyDescriptor for each property of this module.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[3];
        pds[0] = super.getPropertiesDescriptions()[0];
        pds[1] = new PropertyDescription("queryCondition", "Query Condition",
                "SQL query condition");
        pds[2] = new PropertyDescription("includeMissingValues", "Include Missing Values",
                "If set, rows with missing values will be included in the result table. " +
                        "This property is used when the module runs with GUI.");
        return pds;
    }

   /**
    * create an ExampleTable object to hold the meta information.
  *  @return an object of Example table
  */
 private ExampleTable createMetaTable(String tableName) {
   // build an ArrayList to keep the column name.
   colNames = new ArrayList();
   // build an ArrayList to keep the column type.
   colTypes = new ArrayList();
   DatabaseMetaData metadata = null;

   try {
     Connection con = cw.getConnection();
     metadata = con.getMetaData();
     ResultSet columns = metadata.getColumns(null,"%",tableName,"%");
     while (columns.next()) {
       String columnName = columns.getString("COLUMN_NAME");
       String columnType = columns.getString("TYPE_NAME");
       colNames.add(columnName);
       colTypes.add(columnType);
     }
   }
   catch (Exception e) {
     JOptionPane.showMessageDialog(null,
     "Could not create metadata table.", "Error",
     JOptionPane.ERROR_MESSAGE);
     System.out.println("Error occurred in createMetaTable.");
   }

   Column[] cols = new Column[colNames.size()];
   for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
     cols[colIdx] = new ObjectColumn(1);
     cols[colIdx].setLabel(colNames.get(colIdx).toString());
     if(ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString()))
      cols[colIdx].setIsScalar(true);
     else
      cols[colIdx].setIsScalar(false);
   }
   // create an Table to hold the meta data
   MutableTableImpl aTable = new MutableTableImpl(cols);
   for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
     if (cols[colIdx].getIsScalar()) {
       aTable.setColumnIsScalar(true,colIdx);
       aTable.setColumnIsNominal(false,colIdx);
     }
     else {
       aTable.setColumnIsScalar(false,colIdx);
       aTable.setColumnIsNominal(true,colIdx);
     }
   }

   ExampleTable et = aTable.toExampleTable();
   return et;
 }

   //end headless conversion support


    /** constant for layout manager */
   private static transient String scalar = "FilterConstructionINTERNALscalar";

    /**
     * The View
     */
   protected class FilterConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {
       /** gui for building expressions */
      private ExpressionGUI gui;
        /** expression */
      private FilterExpression expression;

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
        /** add button */
      private JButton addButton;
        /** help button */
      private JButton helpButton;
        /** holds names of columns */
      private JComboBox columnBox;
        /** holds operations */
      private JComboBox operationBox;
        /** holds booleans */
      private JComboBox booleanBox;
      /** text field to enter scalar value */
      private JTextField scalarField;
        /** panel */
      private JPanel comboOrFieldPanel;
        /** layout manager */
      private CardLayout nominalOrScalarLayout;
      /** look up JComboBoxes for nominal columns; key = column #*/
      private HashMap nominalComboBoxLookup;
      /** which nominal combobox is showing?  -1 if scalar textfield is showing
        */
      private int nominalShowing = -1;
        /** the view module that spawned this view */
      private ViewModule mod;
      /** true if it has been initialized */
      private boolean initialized = false;

        /**
         * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
         *
         * @param m The module this view is associated with.
         */
        public void initView(ViewModule m) {
            mod = m;
        }

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

         // scalar columns will require textfield input; nominal columns will
         // require a combobox of nominal values:

         scalarField = new JTextField(10);

         addScalarButton = new JButton(
               new ImageIcon(mod.getImage("/images/addbutton.gif")));
         addScalarButton.addActionListener(this);

         nominalComboBoxLookup = new HashMap();
         comboOrFieldPanel = new JPanel();
         nominalOrScalarLayout = new CardLayout();
         comboOrFieldPanel.setLayout(nominalOrScalarLayout);
         comboOrFieldPanel.add(scalarField, scalar);

         // find the first scalar column
         nominalShowing = 0;
         for (int i = 0; i < table.getNumColumns(); i++) {
           if (table.isColumnScalar(i)){
             nominalShowing = -1;
             columnBox.setSelectedIndex(i);
             nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
             break;
           }
         }

         // no scalar columns in the table, get the comboBox for the first column
         if (nominalShowing == 0) {
           JComboBox nominalCombo = new JComboBox(getUniqueValues(0));
           comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(0));
           nominalComboBoxLookup.put(new Integer(0), nominalCombo);
           nominalOrScalarLayout.show(comboOrFieldPanel,
              table.getColumnLabel(0));
         }

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


        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {

            Object src = e.getSource();

            if (src == columnBox && initialized) {

                int index = columnBox.getSelectedIndex();
                if (table.isColumnNominal(index)) {

                    nominalShowing = index;
                    JComboBox nominalCombo = new JComboBox(getUniqueValues(index));
                    if (!nominalComboBoxLookup.containsKey(new Integer(index)))
                    {
                        comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(index));
                        nominalComboBoxLookup.put(new Integer(index), nominalCombo);
                    }
                    nominalOrScalarLayout.show(comboOrFieldPanel,
                            table.getColumnLabel(index));
                } else {
                    nominalShowing = -1;
                    nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
                }

            } else if (src == addColumnButton) {
                // ANCA added condition related to missing values
                if (getIncludeMissingValues()) {
                    gui.getTextArea().insert((String) columnBox.getSelectedItem() +
                            " is NULL || " +
                            (String) columnBox.getSelectedItem(),
                            gui.getTextArea().getCaretPosition());
                } else {
                    gui.getTextArea().insert((String) columnBox.getSelectedItem(),
                            gui.getTextArea().getCaretPosition());
                }
                // System.err.println("SQLFilterConstruction::actionPerformed::addColumnButton -- " ); //+ expression.toString);
            } else if (src == addScalarButton) {

                if (nominalShowing < 0) {

                    gui.getTextArea().insert(scalarField.getText(),
                            gui.getTextArea().getCaretPosition());

                } else {

                    JComboBox combo = (JComboBox) nominalComboBoxLookup.get(
                            new Integer(nominalShowing));

                    gui.getTextArea().insert(
                            "'" + combo.getSelectedItem() + "'",
                            gui.getTextArea().getCaretPosition());

                }

            } else if (src == addOperationButton)
                gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                        gui.getTextArea().getCaretPosition());
            else if (src == addBooleanButton)
                gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                        gui.getTextArea().getCaretPosition());
            else if (src == abortButton)
                viewCancel();
        }

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
          String valueQry = new String("select distinct " + colNames.get(columnIndex) + " from ");
          valueQry = valueQry + tableName + " where " + colNames.get(columnIndex) + " is not null";
          valueQry = valueQry + " order by " + colNames.get(columnIndex);
          Statement valueStmt = con.createStatement();
          ResultSet valueSet = valueStmt.executeQuery(valueQry);
          while (valueSet.next()) {
            columnValues.add(valueSet.getString(1));
          }
          return columnValues;
        }
        catch (Exception e){
          JOptionPane.showMessageDialog(null,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occurred in getUniqValue (db mode).");
          return null;
        }
      }

        /**
         * Signal the expression has changed.
         *
         * @param evaluation new value of the expression.
         */
        public void expressionChanged(Object evaluation) {
            // ANCA replaced this code with toSQLString method in FilterExpression
//          String queryCondition = gui.getTextArea().getText();
//          _lastExpression = new String(queryCondition);
//          // SQL does not support "=="
//          while (queryCondition.indexOf("==") >= 0)
//            queryCondition = replace(queryCondition, "==", "=");
//          // SQL does not support "&&"
//          while (queryCondition.indexOf("&&") >= 0)
//            queryCondition = replace(queryCondition, "&&", " and ");
//          // SQL does not support "||"
//          while (queryCondition.indexOf("||") >= 0)
//            queryCondition = replace(queryCondition, "||", " or ");

//          //_lastExpression = new String(queryCondition);
//          //if(getIncludeMissingValues())


            String queryCondition = expression.toSQLString();
            // System.out.println("query condition " + queryCondition);

            //headless conversion support
            setQueryCondition(queryCondition);
            //headless conversion support

            pushOutput(queryCondition, 0);
            viewDone("Done");
        }
   }

   private class HelpWindow extends JD2KFrame {
    /**
     * Constructor
     */
      public HelpWindow() {
         super ("FilterConstruction Help");
         JEditorPane ep = new JEditorPane("text/html", getHelpString());
         ep.setCaretPosition(0);
         getContentPane().add(new JScrollPane(ep));
         setSize(400, 400);
      }
   }

   /**
    * Get the help text.
    *
    * @return help text
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

   /** the query condition */
   private String queryCondition;

    /**
     * Get the query condition
     * @return query condtion
     */
   public String getQueryCondition(){return queryCondition;}

    /**
     * Set the query condition
     * @param str new query condtion
     */
   public void setQueryCondition(String str){queryCondition = str;}
   
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

    /**
     * Execute this module using the same filters that were constructed in
     * the last successful execution.
     *
     * @throws Exception when something goes wrong
     */
    public void doit() throws Exception {
        //pulling input...
        /*ConnectionWrapper*/ cw = (ConnectionWrapper) pullInput(0);
        String tableName = (String) pullInput(1);

        if (queryCondition == null)
            throw new Exception(this.getAlias() + " has not been configured. Before running headless, run with the gui and configure the parameters.");

//     String goodCondition = ""; //this will be pushed out.

//validating
        //getting tables names in data base

        HashMap tables = StaticMethods.getAvailableTables(cw);
        //checking that tableName is in the hashmap
        if (!tables.containsKey(tableName))
            throw new Exception(getAlias() + ": Table " + tableName + " was not found in the data base!");

        //getting all attributes names.
        HashMap availableAttributes = StaticMethods.getAvailableAttributes(cw, tableName);


        if (availableAttributes.size() == 0) {
            //this means either the table was not found in the data base, or that
            //it has no columns. the query condition will be empty anyway
        	myLogger.warn(getAlias() + ": Warning - Table " +
                    tableName +
                    " has no columns.");
            //pushOutput(goodCondition, 0);
            //  pushOutput("", 0);
            //   return;
        }

//validting the expression:
        //creating a metadata table
        ExampleTable et = this.createMetaTable(tableName);
        //creating a validator
        FilterExpression expression = new FilterExpression(et, getIncludeMissingValues());
        //replacing the special signs of sql (or, and, = with the regular)
        String cond = replaceSigns(queryCondition);

//validating the expression....
        expression.setExpression(cond);


        pushOutput(queryCondition, 0);


    }//doit

   /** readable operators to replace */
   public static final String[] toReplace = {"or", "and", "=", "null"};
    /** operates to replace the readable operators with */
   public static final String[] replaceWith = {"||", "&&", "==", "NULL"};

  /**
   * returns true if <doe>lookFor</code> is not a part of a word in <code>str</code>
   * @param str - a String containing <codE>lookFor</code> at inex <codE>index</codE>
   * @param index - first index of <codE>lookFor</codE> in <codE>str</codE>
   * @param lookFor - substring of <code>str</codE>
   * @return - true if <codE>lookFor</code> is not a part of a word, false otherwise.
   */
  private boolean standAlone(String str, int index, String lookFor){
    //special cases it is in the beginning of the string or at the end of it.

    //debug
 //   System.out.print("\n\n\tinside standAlone: index = " + index);
 //   System.out.println("\tlooking for string = " + lookFor);
 //   System.out.println("\tinside string = " + str);
    //end debug

    if(index == 0) return false;
    if( (index  + lookFor.length() ) >= str.length() ) return false;

    char before = str.charAt(index-1);
    char after = str.charAt(index + lookFor.length());

    //debug
 //  System.out.println("\tthe character before is = " + before);

 //  System.out.println("\tthe character after is = " + after);
   //end debug


    if(( before == ')' || isWhiteSpace(before) )&&
        (after == '(' || isWhiteSpace(after)) )
       return true;

    return false;

  }

  /**
   * returns true if <code>ch</code> is a white space.
   * @param ch - a character to be observed
   * @return - true if ch is a white space. false otherwise.
   */
   private boolean isWhiteSpace(char ch){
     switch(ch){
       case ' ':
       case '\t':
       case '\n':
       case '\r':
       case '\f':
         return true;
      default: return false;
     }
   }//iswhiteSpace


    /**
     * Replace or, and, null, =, < with ||, &&, NULL, ==, !=
     * @param str string to format
     * @return string with signs replaced
     */
   private String replaceSigns(String str){
     String retVal = "";
     StringTokenizer tok = new StringTokenizer(str, "=orORandANDullULL<>", true);

     int lastIndex = 0;
     int newIndex = 0;

     while(tok.hasMoreTokens()){
       String currTok = tok.nextToken();
       newIndex += currTok.length();

       //vered - debug
 //      System.out.println("current token: " + currTok);
       //end debug


       switch(currTok.charAt(0)){
         case 'o':
         case 'O':

           //expecting to find R
           String are = "";
           if (tok.hasMoreTokens())
             are = tok.nextToken();


           newIndex += are.length();

  //vered - debug
 //  System.out.println("parsed another token: " + are);
  //end debug

           if (are.equalsIgnoreCase("r")) {
             //if found R
             if (this.standAlone(str, lastIndex, currTok + are)) {

               //vered - debug
     //          System.out.println("it stands alone replacing it with ||");
               //end debug

               //if it really an or operator replacing it.
               retVal += " || ";
             } //if stands alone

             //else adding the tokens
             else{
               //vered - debug
     //          System.out.println("it does not stand alone appending tokens");
               //end debug

               retVal += currTok + are;
             }//not stand alone
           } //if are

           //we did not find R - adding tokens.
           else{
             //vered - debug
      //       System.out.println("it is not an R, appending tokens");
             //end debug

             retVal += currTok + are;
           }//not are

           lastIndex = newIndex;

          //debug
     //     System.out.println("retVal is now: " + retVal);
           break;

         case 'a':
         case 'A':
           //expecting to find N
           String an = "";

           if (tok.hasMoreTokens())
             an = tok.nextToken();

           newIndex += an.length();

           //vered - debug
    //       System.out.println("parsed another token: " + an);
           //end debug


           if (an.equalsIgnoreCase("n")) {

             //expecting to find D

             String di = "";
             if (tok.hasMoreTokens())
               di = tok.nextToken();

             newIndex += di.length();

             //vered - debug
       //      System.out.println("parsed another token: " + di);
             //end debug

             if (di.equalsIgnoreCase("d")) {
               //found AND
               if (this.standAlone(str, lastIndex, currTok + an + di)) {

                 //vered - debug
        //         System.out.println("it stands alone replacing it with &&");
                 //end debug


                 //if it is really AND operator replacing it
                 retVal += " && ";
               } //if stands alone

               //this is part of an attribute name - appending the tokens
               else{
                 //vered - debug
        //         System.out.println("it does not stand alone , appending tokens");
                 //end debug

                 retVal += currTok + an + di;
               }//not stand alone

             } //if di

             //we did not find D - appending tokens
             else{
              //vered - debug
    //          System.out.println("it was not D , appending tokens");
              //end debug

              retVal += currTok + an + di;
             }//not D



           } //if equals N

           //we did not find N - appending tokens
           else{
             //vered - debug
     //         System.out.println("it was not N , appending tokens");
             //end debug

             retVal += currTok + an;
           }//not N


           lastIndex = newIndex;
           //debug
    //        System.out.println("retVal is now: " + retVal);
           break;

         case 'n':
         case 'N':

           //expecting to find NULL

           String you = "";
           if (tok.hasMoreTokens())
             you = tok.nextToken();

           newIndex += you.length();

           //vered - debug
     //      System.out.println("parsed another token: " + you);
           //end debug

           //expecting U
            if(you.equalsIgnoreCase("u")){

              String el1 = "";
              if (tok.hasMoreTokens())
                el1 = tok.nextToken();

              newIndex += el1.length();

             //vered - debug
     //        System.out.println("parsed another token: " + el1);
             //end debug

              //expecting L
              if (el1.equalsIgnoreCase("l")) {

                String el2 = "";
                if (tok.hasMoreTokens())
                  el2 = tok.nextToken();

                newIndex += el2.length();

                //vered - debug
           //     System.out.println("parsed another token: " + el2);
                //end debug


                //expecting another L
                if (el2.equalsIgnoreCase("l")) {

                  //found the word NULL
                  if (standAlone(str, lastIndex, currTok + you + el1 + el2)) {

                    //vered - debug
             //       System.out.println("it stands alone replacing it with NULL");
                    //end debug

                    //if it stands alone replacing it with upeercase.
                    retVal += " NULL ";
                  } //if stands alone

                  //it is not the token NULL - appending all tokens
                  else{
                    //vered - debug
         //           System.out.println("it does not stand alone , appending tokens");
                    //end debug

                    retVal += currTok + you + el1 + el2;
                  }//not stand alone
                } //if el2

                //l2 was not an L, appending all tokens
                else{
                  //vered - debug
          //        System.out.println("it was not L, appending tokens");
                  //end debug

                  retVal += currTok + you + el1 + el2;
                }//not L2

              } //if el1

              //el1 was not an L - appending 3 first tokens
              else{
                //vered - debug
     //           System.out.println("it was not L , appending tokens");
                //end debug

                retVal += currTok + you + el1;
              }//not L1
            }//if you

            //you was not U - appending first 2 tokens.
            else {
             //vered - debug
        //     System.out.println("it was not U , appending tokens");
             //end debug

              retVal += currTok + you;
            }//not you

            lastIndex = newIndex;

            //debug
       //     System.out.println("retVal is now: " + retVal);
              break;

            case '=':
                char ch = retVal.charAt(retVal.length()-1);

                //vered - debug
             //    System.out.println("last char in retVal is: " + ch);
                //end debug


                if (ch == '<' || ch == '>') {

                  //vered - debug
        //          System.out.println("appending the equal sign");
                  //end debug


                  retVal += currTok;
                }//this is not a single equal sign

                else{
                  String equ = "";
                  if(tok.hasMoreTokens())
                   equ = tok.nextToken();

                  newIndex += equ.length();

                  //vered - debug
            //      System.out.println("parsed another token: " + equ);
                  //end debug

                  if (equ.equals("=")) {

                    //vered - debug
              //      System.out.println("no need to replace with double equal. just appending");
                    //end debug

                    retVal += currTok + equ;
                  }//this is a legal double equal sign
                  else {
                    //vered - debug
          //          System.out.println("replacing the single equal with a double and appending the other token");
                    //end debug

                    retVal += "==" + equ;
                  }//a single equal sign

                }//the char before is not < or >

                  lastIndex = newIndex;
                //debug
        //        System.out.println("retVal is now: " + retVal);
                break;



          case '<':

            //looking for >
               String greater = "";
               if(tok.hasMoreTokens()){
                    greater = tok.nextToken();
               }

               //debug
             //  System.out.println("parsed another token: " + greater);


               newIndex += greater.length();

               if(greater.equals(">")){


                  retVal += " != ";
                  }
                else{


                  retVal += currTok + greater;
                  }//not >

                  lastIndex = newIndex;



                  break;

            default:
              retVal += currTok;
              lastIndex += currTok.length();

            break;


       }//switch
     }//while
     return retVal;
   }
}