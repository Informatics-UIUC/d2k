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
  JOptionPane msgBoard = new JOptionPane();
  private String tableName = "";
  private ConnectionWrapper cw;
  // ArrayList for column types
  private ArrayList colTypes;
  // ArrayList for column names
  private ArrayList colNames;
  // ExampleTable to keep the meta data
  private ExampleTable table;


/******************************************************************************/
/* Module methods                                                             */
/******************************************************************************/

   public String getModuleName() {
      return "SQL Filter Construction";
   }

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

   public String getInputName(int index) {
      if (index == 0)
         return "Database Connection";
      else if (index == 1)
         return "Selected Table";

      return null;
   }

   public String[] getInputTypes() {
      String[] types = {"ncsa.d2k.modules.io.input.sql.ConnectionWrapper","java.lang.String"};
      return types;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The database connection.";
      else if (index == 1)
         return "The database table to be filtered.";
      return "SQLFilterConstruction has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"java.lang.String"};
      return o;
   }

   public String getOutputName(int index) {
      if (index == 0)
         return "Query Condition";
      return null;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "The query condition for the search. If the string is blank there will be no where clause, all records will be retrieved.";
      return "SQLFilterConstruction has no such output.";
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

   private boolean _includeMissingValues = true;
   public boolean getIncludeMissingValues() {
	  return _includeMissingValues;
   }
   public void setIncludeMissingValues(boolean value) {
	  _includeMissingValues = value;
   }


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


   //vered - headless conversion support
   //moved this method from FilterConstructionGUI into SQLFilterConstruction
   //so that the doit method can use it as well. (for validation of expression purposes)
   /** create an ExampleTable object to hold the meta information.
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
     JOptionPane.showMessageDialog(msgBoard,
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

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   private static String scalar = "FilterConstructionINTERNALscalar";

   protected class FilterConstructionGUI extends JUserPane
      implements ActionListener, ExpressionListener {

      private ExpressionGUI gui;
      private FilterExpression expression;

      private JButton addColumnButton, addScalarButton, addOperationButton,
                      addBooleanButton, abortButton, addButton, helpButton;
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
        if (i == 0) {
          cw = (ConnectionWrapper)o;
        }
        else if (i == 1) {
          tableName = (String)o;
          initialize();
        }
      }

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


      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == columnBox && initialized) {

            int index = columnBox.getSelectedIndex();
            if (table.isColumnNominal(index)) {

               nominalShowing = index;
               JComboBox nominalCombo = new JComboBox(getUniqueValues(index));
               if(!nominalComboBoxLookup.containsKey(new Integer(index)))
               {
                 comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(index));
                 nominalComboBoxLookup.put(new Integer(index), nominalCombo);
               }
               nominalOrScalarLayout.show(comboOrFieldPanel,
                  table.getColumnLabel(index));
            }
            else {
               nominalShowing = -1;
               nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
            }

         }

         else if (src == addColumnButton) {
	     // ANCA added condition related to missing values
         	if (getIncludeMissingValues()) {
         		gui.getTextArea().insert((String)columnBox.getSelectedItem() +
				      " is NULL || " +
				      (String)columnBox.getSelectedItem(),
				      gui.getTextArea().getCaretPosition());
			} else {
             gui.getTextArea().insert((String)columnBox.getSelectedItem(),
                gui.getTextArea().getCaretPosition());
         	}
	     // System.err.println("SQLFilterConstruction::actionPerformed::addColumnButton -- " ); //+ expression.toString);
	 }
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
          JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occurred in getUniqValue (db mode).");
          return null;
        }
      }

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

         pushOutput(queryCondition,0);
         viewDone("Done");
      }

//       public String replace(String oldString, String oldPattern, String newPattern) {
//          int index;
//          String newString;
//          index = oldString.indexOf(oldPattern);
//          // matched substring is located in the middle of the string
//          if (index > 0)
//            newString = oldString.substring(0,index) + newPattern +
//                      oldString.substring(index + oldPattern.length(), oldString.length());
//          // matched substring is located in the begining of the string
//          else if (index == 0)
//            newString = newPattern +
//                      oldString.substring(index + oldPattern.length(), oldString.length());
//          else
//            newString = oldString;
//          return newString;
//       }
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

   //headless conversion support
   private String queryCondition;
   public String getQueryCondition(){return queryCondition;}
   public void setQueryCondition(String str){queryCondition = str;}

   public void doit()throws Exception{
     //pulling input...
     /*ConnectionWrapper*/ cw = (ConnectionWrapper) pullInput(0);
     String tableName = (String) pullInput(1);

     if(queryCondition == null)
       throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

//     String goodCondition = ""; //this will be pushed out.

//validating
     //getting tables names in data base

     HashMap tables = StaticMethods.getAvailableTables(cw);
     //checking that tableName is in the hashmap
     if(!tables.containsKey(tableName))
       throw new Exception (getAlias() + ": Table " + tableName + " was not found in the data base!");

     //getting all attributes names.
     HashMap availableAttributes = StaticMethods.getAvailableAttributes(cw, tableName);


     if(availableAttributes.size() == 0){
       //this means either the table was not found in the data base, or that
       //it has no columns. the query condition will be empty anyway
       System.out.println(getAlias() + ": Warning - Table " +
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




     pushOutput(queryCondition,0);


   }//doit

   //supporting methods int he validation of the expression

   public static final String[] toReplace = {"or", "and", "=", "null"};
   public static final String[] replaceWith = {"||", "&&", "==", "NULL"};

   /**
    * looks for "or" "and" and single '=' int he String <codE>query</codE>
    * and replaces them with "||" "&&" and "=="
    * @param query - and SQL query expression
    * @return - the expression replaced
    */
/*   private String replaceSqlSigns(String query){
     String retVal = "";


//vered debug
  //   System.out.println("\n\noriginal query: " + query);
     //end debug
     retVal = replaceSigns(query);


     //vered debug
  //   System.out.println("replaced query: " + retVal);
     //end debug




     return retVal;
   }
*/

   /**
    * replaces single '=' with a double one in the String <code>str</code>
    * only if the '=' stands alone (could be part of "!=" or "<=")
    * @param str - an expression
    * @return - the expression with double '=' instead of single ones.
    */
  /* private String replaceEqualSigns(String str){

     //vered debug
   //  System.out.println("\n\nreplacing equal signs. original string: " + str);
     //end debug

     StringTokenizer tok = new StringTokenizer(str, "=", true);
     String retVal = "";

     while(tok.hasMoreTokens()){
       String currTok = tok.nextToken();


      //vered debug
  //    System.out.println("current token: " + currTok);
      //end debug


       if(currTok.equals("=")){
         char ch = retVal.charAt(retVal.length()-1);

         //vered debug
      //      System.out.println("last character in ret val is: " + ch );
            //end debug

         switch(ch){
           case '!':
           case '<':
           case '>':

            //vered debug
      //      System.out.println("doint nothign special");
            //end debug

             retVal += currTok;
             break;
          default:


            String next = tok.nextToken();
            if(next.equals("=")){
              retVal += "==";
//vered debug
        //         System.out.println("discovered a double equal sign");

                 //end debug


            }
            else {
              retVal += "==" + next;

                 //vered debug
        //         System.out.println("replacing single eaula with couble one");
        //         System.out.println("and adding the next token: " + next);
                 //end debug

            }
         }//switch
       }//if this is equal

       else{
         retVal += currTok;
         //vered debug
   //      System.out.println("addign current token");
                       //end debug

       }

     }//while

     //vered debug
 //    System.out.println("the returned string (by replace equal signs): " + retVal );

                 //end debug


     return retVal;
   }//replaceEqualSigns

   */

   /**
   * looks for <code>toReplace</code> strings in <code>query</code> and replaces
   * them with <codE>replaceWith</code>. the replacement will take place only if
   * <codE>toReplace</codE> is not already a part of a word.
   * @param query - an sql expression
   * @return - the expression with the substrings replaced.
   */
/*  private String replaceSqlSigns(String query, String toReplace, String replaceWith){

    //vered debug
 //   System.out.println("\n\nreplacing sql signs. original string: " + query);
//    System.out.println("looking for string: " + toReplace);
 //   System.out.println("the replacing string: " + replaceWith);
    //end debug


    String retVal = "";

    int index  = query.indexOf(toReplace);
    String str = query;

    while(index != -1){
      int currIdx = 0;

      //vered - debug
   //   System.out.println("looking in substring: " + str);
  //    System.out.println("index of " + toReplace + ": " + index);
      //end debug

      if(standAlone(str, index, toReplace)){


        retVal += str.substring(currIdx, index - 1) + replaceWith;

        //vered - debug
 //   System.out.println("it stands alone");
 //   System.out.println("retVal is now: " + retVal);
    //end debug

      }


      else{
        retVal += str.substring(currIdx, (index - 1 + toReplace.length()));

        //vered - debug
//  System.out.println("it does not stand alone");
//  System.out.println("retVal is now: " + retVal);
  //end debug

      }

        currIdx += index-currIdx + toReplace.length();
         str = str.substring(currIdx);
        index = str.indexOf(toReplace);

    }
retVal += str;
    //vered - debug

// System.out.println("returned string by replaceSqlSigns: " + retVal);
 //end debug

    return retVal;
  }*/



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
   * returns true if <codE>ch</codE> is a white space.
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





//end of supporting methods int he validation of the expression


   //headless conversion support

}
 /**
  * qa comments:
  * 01-21-04: vered
  * bug 201 - ignores include missing values property.
  *
  * 01-26-03: vered
  * bug 201 is fixed. updated module info.
  *
  * 02-11-04: vered
  * added a validation of the expression inside the doit method.
  * validating is done by FilterExpression, thus it is identical to the one done by
  * the GUI of this class.
  * for this reason I added methods that convert the sql condition back to the
  * regular one that can be parsed by FilterExpression.
  * for this reason, documentation was added to the module info,
  *  directing the user how to use the properties editor manually, it at all.
  */



 /**
  *
            private boolean[] getIsScalar(HashMap attMap) {
              // build an ArrayList to keep the column name.
              //colNames = new ArrayList();
              // build an ArrayList to keep the column type.
              colTypes = new ArrayList();
              DatabaseMetaData metadata = null;

              try {
                Connection con = cw.getConnection();
                metadata = con.getMetaData();
                ResultSet columns = metadata.getColumns(null,"%",tableName,"%");
                while (columns.next()) {
                 // String columnName = columns.getString("COLUMN_NAME");
                  String columnType = columns.getString("TYPE_NAME").toUpperCase();
                //  colNames.add(columnName);
                  colTypes.add(columnType);
                }
              }
              catch (Exception e) {

                System.out.println("Error occurred in getIsScalar.");
                e.printStackTrace();
              }

              //Column[] cols = new Column[colNames.size()];
              boolean[] retVal = new boolean [colTypes.size()];
              for (int colIdx = 0; colIdx < colTypes.size(); colIdx++) {
                //cols[colIdx] = new ObjectColumn(1);
                //cols[colIdx].setLabel(colNames.get(colIdx).toString());

                System.out.println("retrieving type of column index " + colIdx);


                if(ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString()))
                 retVal[colIdx] = true;
                else
                 retVal[colIdx] = false;
              }

              return retVal;
            }

 */
