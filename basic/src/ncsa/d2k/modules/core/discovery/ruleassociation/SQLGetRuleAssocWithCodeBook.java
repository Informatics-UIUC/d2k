package ncsa.d2k.modules.core.discovery.ruleassociation;

/**
 * <p>Title: SQLGetRuleAssocWithCodeBook </p>
 * <p>Description: Extract rule association from a cube table </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */



import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gnu.trove.*;

public class SQLGetRuleAssocWithCodeBook extends UIModule
        {
  private static final int SUPPORT = 1;
  private static final int CONFIDENCE = 2;
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  double minSupport = 20;
  double minConfidence = 70;
  double threshold = 10;
  int colCnt;
  int totalRow;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "NOAVL";
  String cubeTableName = NOTHING;
  String saveBookName = NOTHING;
  String saveSupport = NOTHING;
  boolean changeCodeBook = true;
  // ArrayList for column names
  ArrayList colNames;
  // ArrayList to keep all rule labels in short code
  ArrayList itemLabels;
  // ArrayList to keep all rule labels in expand text
  ArrayList itemText;
  // ArrayList to keep the objects of the class FreqItemSet
  ArrayList freqItemSets;
  // Array to keep the objects of the class Rule
  ArrayList allRules;
  public class Rule {
    int headIdx; // the index in freqItemSets for the left-hand side rule
    int bodyIdx; // the index in freqItemSets for the right-hand side rule
    double support; // support for the rule
    double confidence; // coufidence for the rule
    int headSize; // the size of the left-hand side rule
  }
  ArrayList finalRules;
  TableImpl ruleTable;
  TableImpl codeTable;
  SQLCodeBook aBook;

  JPanel codeBookPanel;
  CardLayout codeBookLayout;
  JTextField tableName;
  JTextField condition;
  JTextField target;
  JTextField supportChosen;
  JTextField confidenceChosen;
  JTextField thresholdChosen;
  JTextField bookName;
  JLabel bookLabel;
  Checkbox sortS;
  Checkbox sortC;
  Checkbox useCodeBook;
  JButton tableBrowseBtn;
  JButton targetBrowseBtn;
  JButton condBrowseBtn;
  JButton bookBrowseBtn;
  JButton cancelBtn;
  JButton ruleBtn;
  JButton sortBtn;
  int[][] itemRange;

  BrowseTables bt;
  BrowseTablesView btw;
  int targetIdx = -1;
  int conditionIdx = -1;

  private static String BLANK = "NoCodeBook";
  private static String FILLED = "WithCodeBook";


  public SQLGetRuleAssocWithCodeBook() {
  }

  public String getOutputInfo (int i) {
                switch (i) {
                        case 0: return "An object of RuleTable.";
                        default: return "No such output";
                }
        }

  public String getInputInfo (int i) {
                switch (i) {
                        case 0: return "JDBC data source to make database connection.";
                        case 1: return "The name of the cube table which stores the data statistics.";
                        default: return "No such input";
                }
        }

  public String getModuleInfo () {
        String s = "<p> Overview: ";
          s += "This module extracts association rules from a cube table. </p>";
          s += "<p> Detailed Description: ";
          s += "This module first makes a connection to a database and retrieves the ";
          s += "data from a specified cube table, then compute the association ";
          s += "rules and present them to users. Each association rule has 4 components: 'IF' ";
          s += "part, 'THEN' part, 'SUPPORT' part, and 'CONFIDENCE' part. ";
          s += "The 'IF' part is the condition of the rule, or called left-hand side ";
          s += "of the rule. The 'THEN' part is the target of the rule, or called ";
          s += "right-hand side of the rule. 'SUPPORT' and 'CONFIDENCE' are ";
          s += "two measuresre of rule interestingness. They respectively reflect ";
          s += "the usefulness and certainty of discovered rules. A support of 2% ";
          s += "for a rule means that 2% of data under analysis support this rule. ";
          s += "A confidence of 60% means that 60% of data that match 'IF' condition ";
          s += "support this rule. You can control the rule generation by three ";
          s += "parameters: 'minimum support', 'minimum confidence', and 'pruning ";
          s += "threshold'. By setting 'minimum support' and 'minimum confidence' lower ";
          s += "you would get more trivial rules. However, by setting 'minimum support' ";
          s += "and 'minimum confidence' higher, you might loose some important rules. ";
          s += "The parameter 'pruning threshold' is used to prune rules after they ";
          s += "are generated. A 'pruning threshold' 10% means if the confidence ";
          s += "of two similar rules is less than 10%, the more specialized rule ";
          s += "will be pruned. This module also provides an user-interface to filter out rules. ";
          s += "You can specify the 'IF' part by choosing a condition, and specify ";
          s += "the 'THEN' part by choosing a target. This module will only generate ";
          s += "association rules that match your selection.";
          s += "<p> Restrictions: ";
          s += "We currently only support Oracle databases.";
          return s;
  }

  public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
                return types;
        }

  public String[] getOutputTypes () {
                String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
                return types;
        }

  /** this property is the min acceptable support score.
   *  @param i the minimum support to set
   */
  public void setMinSupport (double i) {
    minSupport = i;
  }
  public double getMinSupport () {
    return minSupport;
  }

  /** this property is the min acceptable confidence score.
   *  @param i the minimum confidence to set
   */
  public void setMinConfidence (double i) {
    minConfidence = i;
  }
  public double getMinConfidence () {
    return minConfidence;
  }

  /** this property is the min acceptable pruning threshold.
   *  @param i the pruning threshold. If the confidence difference of two
   *           similar rules is less than this threshold, the more specific
   *           rule is pruned. E.g. A=>C (confidence 0.95) and (A,B)=>C
   *           (confidence 0.94), with pruningThreshold 0.1, (A,B)=>C is pruned
   */
  public void setThreshold (double i) {
    threshold = i;
  }
  public double getThreshold () {
    return threshold;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
    PropertyDescription [] pds = new PropertyDescription [3];
    pds[0] = new PropertyDescription ("minSupport", "Minimum Support %", "If the occurrence ratio of a rule is below Minimum Support, it is pruned.");
    pds[1] = new PropertyDescription ("minConfidence", "Minimum Confidence %", "If the accuracy confidence is below Minimum Confidence, it is pruned.");
    pds[2] = new PropertyDescription ("threshold", "Pruning Threshold %", "If the confidence difference between two similar rules is below Pruning Threshold, one rule is pruned.");
    return pds;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  /**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new GetRuleView();
  }

  public class GetRuleView extends JUserPane
    implements ActionListener, ItemListener {

    /** a reference to our parent module */
    protected SQLGetRuleAssocWithCodeBook parent;

    public void setInput(Object input, int index) {
      if (index == 0) {
        cw = (ConnectionWrapper)input;
      }
      else if (index == 1) {
        tableName.setText((String)input);
        getColNames();
      }
      //tableName.setText(NOTHING);
      condition.setText(NOTHING);
      target.setText(NOTHING);
      cubeTableName = NOTHING;
      saveSupport = NOTHING;
      supportChosen.setText(Double.toString(minSupport));
      confidenceChosen.setText(Double.toString(minConfidence));
      thresholdChosen.setText(Double.toString(threshold));
      bookName.setText(NOTHING);
    }

    /*
    public Dimension getPreferredSize() {
      return new Dimension (500, 280);
    } */

    public void initView(ViewModule mod) {
      parent = (SQLGetRuleAssocWithCodeBook)mod;
      removeAll();
      cw = (ConnectionWrapper)pullInput (0);

      // Panel to hold outline panels
      JPanel getRulePanel = new JPanel();
      getRulePanel.setLayout (new GridBagLayout());

      // Outline panel for options
      JOutlinePanel options = new JOutlinePanel("Options");
      options.setLayout (new GridBagLayout());
      Constrain.setConstraints(options, new JLabel("Table Name"),
        0,0,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, tableName = new JTextField(10),
        5,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      tableName.setText((String)pullInput(1));
      tableName.addActionListener(this);
      getColNames();
      Constrain.setConstraints(options, tableBrowseBtn = new JButton ("Browse"),
        15,0,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      tableBrowseBtn.addActionListener(this);

      Constrain.setConstraints(options, new JLabel("Condition Attribute"),
        0,1,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, condition = new JTextField(10),
        5,1,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      condition.setText(NOTHING);
      condition.addActionListener(this);
      Constrain.setConstraints(options, condBrowseBtn = new JButton ("Browse"),
        15,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      condBrowseBtn.addActionListener(this);

      Constrain.setConstraints(options, new JLabel("Target Attribute"),
        0,2,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, target = new JTextField(10),
        5,2,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      target.setText(NOTHING);
      target.addActionListener(this);
      Constrain.setConstraints(options, targetBrowseBtn = new JButton ("Browse"),
        15,2,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      targetBrowseBtn.addActionListener(this);

      Constrain.setConstraints(options, new JLabel("Minimum Support"),
        0,3,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, supportChosen = new JTextField(10),
        5,3,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      supportChosen.setText(Double.toString(minSupport));
      supportChosen.addActionListener(this);
      sortS = new Checkbox( "Sort by Support", null, true );
      sortS.addItemListener( this );
      Constrain.setConstraints(options, sortS,
        15,3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1,1);

      Constrain.setConstraints(options, new JLabel("Minimum Confidence"),
        0,4,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, confidenceChosen = new JTextField(10),
        5,4,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      confidenceChosen.setText(Double.toString(minConfidence));
      confidenceChosen.addActionListener(this);
      sortC = new Checkbox ( "Sort by Confidence", null, false);
      sortC.addItemListener( this );
      Constrain.setConstraints(options, sortC,
        15,4,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1,1);

      Constrain.setConstraints(options, new JLabel("Pruning Threshold"),
        0,5,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(options, thresholdChosen = new JTextField(10),
        5,5,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      thresholdChosen.setText(Double.toString(threshold));
      thresholdChosen.addActionListener(this);

      useCodeBook = new Checkbox ( "Use Code Book", null, true);
      useCodeBook.addItemListener( this );
      Constrain.setConstraints(options, useCodeBook,
        0,6,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1,1);

      codeBookPanel = new JPanel();
      codeBookLayout = new CardLayout();
      codeBookPanel.setLayout(codeBookLayout);

      JPanel filledPanel = new JPanel();
      filledPanel.setLayout (new GridBagLayout());
      Constrain.setConstraints(filledPanel, bookLabel = new JLabel("Code Book Name"),
        0,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(filledPanel, bookName = new JTextField(10),
        5,0,5,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      bookName.setText(NOTHING);
      bookName.addActionListener(this);
      Constrain.setConstraints(filledPanel, bookBrowseBtn = new JButton ("Browse"),
        15,0,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      bookBrowseBtn.addActionListener(this);
      codeBookPanel.add(filledPanel, FILLED);

      JPanel blankPanel = new JPanel();
      blankPanel.setLayout (new GridBagLayout());
      Constrain.setConstraints(blankPanel, new JPanel(),
        0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      codeBookPanel.add(blankPanel, BLANK);
      codeBookLayout.show(codeBookPanel, FILLED);

      Constrain.setConstraints(options, codeBookPanel,
        5,6,15,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1,1);

      /* Add the outline panel to getRulePanel */
      Constrain.setConstraints(getRulePanel, options,
        0,0,5,5,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0);
      Constrain.setConstraints(getRulePanel, new JPanel(),
        0,6,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,0.5,0);
      Constrain.setConstraints(getRulePanel, cancelBtn = new JButton ("   Abort   "),
        1,6,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,0,0);
      cancelBtn.addActionListener(this);
      Constrain.setConstraints(getRulePanel, ruleBtn = new JButton ("Get Rules"),
        2,6,1,1,GridBagConstraints.NONE, GridBagConstraints.CENTER,0,0);
      ruleBtn.addActionListener(this);
      Constrain.setConstraints(getRulePanel, sortBtn = new JButton ("    Resort    "),
        3,6,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,0,0);
      sortBtn.addActionListener(this);
      Constrain.setConstraints(getRulePanel, new JPanel(),
        4,6,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,0.5,0);

      setLayout (new BorderLayout());
      add(getRulePanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == tableBrowseBtn) {
        doTableBrowse();
        condition.setText(NOTHING);
        target.setText(NOTHING);
      }
      else if (src == condBrowseBtn) {
        doColumnBrowse(0);
      }
      else if (src == targetBrowseBtn) {
        doColumnBrowse(1);
      }
      else if (src == bookBrowseBtn) {
        doBookBrowse();
        changeCodeBook = true;
      }
      else if (src == ruleBtn) {
        if (useCodeBook.getState() && bookName.getText().length()<=0) {
          // The user has not chosen a code book yet
          JOptionPane.showMessageDialog(msgBoard,
            "You must choose a code book.", "Error",
            JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no code book selected.");
        }
        else if (tableName.getText().length()>0) {
          // if code book is required and the code book is not retrieved yet, then get it
          if (!saveBookName.equals(bookName.getText().toString()) &&
              useCodeBook.getState()) {
            //codeTable = getCodeBook(bookName.getText().toString());
            aBook = new SQLCodeBook(cw, bookName.getText().toString());
            codeTable = aBook.codeBook;
            saveBookName = bookName.getText().toString();
          }
          // only recalculate rules if table name or support are changed
          if (!cubeTableName.equals(tableName.getText().toString()) ||
              !saveSupport.equals(supportChosen.getText().toString()) ||
              changeCodeBook ) {
            cubeTableName = tableName.getText().toString();
            saveSupport = supportChosen.getText().toString();
            freqItemSets = new ArrayList();
            allRules = new ArrayList();
            getItemLabels();
            //printArrayList(itemLabels);
            //printArrayList(itemText);
            extractRules();
          }
          finalRules = new ArrayList();
          filterRules();
          if (finalRules.size() > 0) {
            convertToRuleTable();
            if (sortC.getState()) {
              sortRuleTable(CONFIDENCE);
            }
            else if (sortS.getState()) {
              sortRuleTable(SUPPORT);
            }

            changeCodeBook = false;
            RuleTable rt;
            if (useCodeBook.getState()) {
              rt = new RuleTable(ruleTable,  minConfidence, minSupport, totalRow,
                    itemText, freqItemSets);
            }
            else {
              rt = new RuleTable(ruleTable,  minConfidence, minSupport, totalRow,
                    itemLabels, freqItemSets);
            }

            pushOutput(rt, 0);
          }
          // if no rules discovered, inform user.
          else {
            JOptionPane.showMessageDialog(msgBoard,
            "There is no rule discovered. You may like to adjust " +
            "Minimum Support, Minimum Confidence and Pruning Threshold, and try again.",
            "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("There is no rule discovered.");
          }
        }
        else if (tableName.getText().length()<=0) { // The user has not chosen a table yet
          JOptionPane.showMessageDialog(msgBoard,
          "You must choose a table first.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no table selected.");
        }
      }
      else if (src == cancelBtn) {
        cubeTableName = NOTHING;
        saveSupport = NOTHING;
        parent.viewCancel();
        //closeIt();
      }
      else if (src == tableName) {
        getColNames();
      }
      else if (src == target) {
        if (target.getText().length() < 1) {
          targetIdx = -1;
        }
      }
      else if (src == condition) {
        if (condition.getText().length() < 1) {
          conditionIdx = -1;
        }
      }
      else if (src == sortBtn) {
        if (allRules.size()==0) {
          JOptionPane.showMessageDialog(msgBoard,
          "Please click button 'Get Rules' first.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("Please click button 'Get Rules' first.");
        }
        else if (sortC.getState()) {
          sortRuleTable(CONFIDENCE);
        }
        else if (sortS.getState()) {
          sortRuleTable(SUPPORT);
        }
        if (finalRules.size() > 0) {
          changeCodeBook = false;
          RuleTable rt;
          if (useCodeBook.getState()) {
            rt = new RuleTable(ruleTable,  minConfidence, minSupport, totalRow,
                  itemText, freqItemSets);
          }
          else {
            rt = new RuleTable(ruleTable,  minConfidence, minSupport, totalRow,
                  itemLabels, freqItemSets);
          }
          pushOutput(rt, 0);
        }
      }
    }

    /** toggle the sorting methods. Disable confidence-sorting if support-sorting
     *  is choosed, and disable support-sorting if confidence-sorting is choosed.
     *  @param e the event user has triggered
     */
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == sortS) {
        if (sortS.getState()) {
          sortC.setState(false);
        }
        else {
          sortC.setState(true);
        }
      }
      else if (e.getSource() == sortC) {
        if (sortC.getState()) {
          sortS.setState(false);
        }
        else {
          sortS.setState(true);
        }
      }
      else if (e.getSource() == useCodeBook) {
        if (useCodeBook.getState()) {
          changeCodeBook = true;
          codeBookLayout.show(codeBookPanel, FILLED);
          bookName.setText(NOTHING);
        }
        else {
          changeCodeBook = true;
          codeBookLayout.show(codeBookPanel, BLANK);
          bookName.setText(NOTHING);
        }
      }
    }
  }

  /** connect to a database and retrieve the list of available cube tables
   */
  protected void doTableBrowse() {
    String query = "select table_name from user_tables where table_name like '%_CUBE'";
    try {
      bt = new BrowseTables(cw, query);
      btw = new BrowseTablesView(bt, query);
      btw.setSize(250,200);
      btw.setTitle("Available Cube Tables");
      btw.setLocation(200,250);
      btw.setVisible(true);
      btw.addWindowListener(new WindowAdapter() {
        public void windowClosed(WindowEvent e)
        {
          tableName.setText(btw.getChosenRow());
          getColNames();
         }
      });
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doTableBrowse.");
    }
  }

  /** connect to a database and retrieve the column list of the cube table
   *  @param colType the column type: 0 for condition column and 1 for target column
   */
  protected void doColumnBrowse(int colType) {
    String query = new String("select column_name from all_tab_columns where table_name = '");
    query = query + tableName.getText() + "' and column_name != 'SET_SIZE' and " +
            "column_name != 'CNT' order by column_id";
    try {
      bt = new BrowseTables(cw, query);
      btw = new BrowseTablesView(bt, query);
      btw.setSize(250,200);
      btw.setLocation(200,250);
      btw.setVisible(true);
      if (colType == 0) {
        btw.setTitle("Available Condition Attributes");
        btw.addWindowListener(new WindowAdapter() {
          public void windowClosed(WindowEvent e)
          {
            condition.setText(btw.getChosenRow());
            conditionIdx = btw.getSelectedRow();
            //conditionIdx = btw.selectedRow;
            System.out.println("conditionIdx is " + conditionIdx);
          }
        });
      }
      else if (colType == 1) {
        btw.setTitle("Available Target Attributes");
        btw.addWindowListener(new WindowAdapter() {
          public void windowClosed(WindowEvent e)
          {
            target.setText(btw.getChosenRow());
            targetIdx = btw.getSelectedRow();
            //targetIdx = btw.selectedRow;
            System.out.println("targetIdx is " + targetIdx);
          }
        });
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doColumnBrowse.");
    }
  }

  /** connect to a database and retrieve the list of available book code tables
   */
  protected void doBookBrowse() {
    String query = "select table_name from all_tables where table_name like '%_CODEBOOK'";
    try {
      bt = new BrowseTables(cw, query);
      btw = new BrowseTablesView(bt, query);
      btw.setSize(250,200);
      btw.setTitle("Available Code Book Tables");
      btw.setLocation(200,250);
      btw.setVisible(true);
      btw.addWindowListener(new WindowAdapter() {
        public void windowClosed(WindowEvent e)
        {
          bookName.setText(btw.getChosenRow());
         }
      });
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in doBookBrowse.");
    }
  }

  /** build an ArrayList itemLables to keep strings "colName=colValue".
   *  to speed up search and filtering, build an int[2] to keep the min index
   *  and max index for each column.
   */
  protected void getItemLabels() {
    itemLabels = new ArrayList();
    itemText = new ArrayList();
    Statement valueStmt;
    ResultSet valueSet;
    int colIdx = 0;
    int min=0;
    int max=0;
    if (target.getText().length() < 1) {
      targetIdx = -1;
    }
    if (condition.getText().length() < 1) {
      conditionIdx = -1;
    }
    try {
      con = cw.getConnection();
      colIdx = 0;
      // the last 2 columns are "set_size" and "cnt" which are added during building cube, not the original column.
      while (colIdx < colNames.size()) {
        String colName = colNames.get(colIdx).toString();
        // add a column name and values to ArrayList itemLabels.
        String valueQry = new String("select distinct " + colName + " from ");
        valueQry = valueQry + tableName.getText() + " where " + colName + " is not null";
        valueStmt = con.createStatement();
        valueSet = valueStmt.executeQuery(valueQry);
        min = itemLabels.size();
        while (valueSet.next()) {
          String codeVal = valueSet.getString(1);
          itemLabels.add(colName+"="+codeVal);
          if (useCodeBook.getState()) {
            String textVal = aBook.getDescription(colName+"="+codeVal);
            if (textVal==null) { // if there is no mapping description, then use the code
              itemText.add(colName+"="+codeVal);
            }
            else { // if there is a description, then use the description
              itemText.add(colName+"="+textVal);
            }
          }
        }
        max = itemLabels.size()-1;
        // use itemRange to trace the min index and max index for each column.
        itemRange[colIdx][0] = min;
        itemRange[colIdx][1] = max;
        //valueStmt.close();
        colIdx++;
      }
      //valueStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getItemLabels.");
    }
  }

  /** get column names
   */
  protected void getColNames() {
    Statement nameStmt;
    Statement cntStmt;
    ResultSet nameSet;
    ResultSet cntSet;
    int colIdx = 0;
    colNames = new ArrayList();
    colCnt = 0;
    DatabaseMetaData metadata = null;
    try {
      con = cw.getConnection();
      metadata = con.getMetaData();
      ResultSet columns = metadata.getColumns(null,"%",tableName.getText(),"%");
      while (columns.next()) {
        String colName = columns.getString("COLUMN_NAME");
        if (!colName.equals("SET_SIZE") && !colName.equals("CNT")) {
          colNames.add(colName);
        }
        colCnt = colCnt + 1;
      }
      // An integer array to keep min itemIdx and max itemIdx for each target attribute
      itemRange = new int[colNames.size()][2];
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getColNames.");
    }
  }

  /** extract association rules from a database cube table
   */
  protected void extractRules() {
    Statement totalStmt;
    Statement cubeStmt;
    ResultSet totalSet;
    ResultSet cubeSet;
    String headValue;
    String bodyValue;
    String tempLabel;
    FreqItemSet aItemSet;
    TIntArrayList items;
    TIntArrayList headItems;
    TIntArrayList bodyItems;
    Rule aRule;
    int cnt;
    int itemIdx;
    int headItemIdx;
    int bodyItemIdx;
    double support;
    double parentSupport;
    double confidence;
    if (condition.getText().equals(target.getText()) && target.getText().length()>0) {
      JOptionPane.showMessageDialog(msgBoard,
                "The condition attribute cannot be same as the target attribute", "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("The condition attribute cannot be same as the target attribute");
    }
    else {
    try {
      con = cw.getConnection();
      // the row with set_size=null keep the total count of the data table
      String totalQry = new String("select cnt from " + tableName.getText() + " where set_size is null");
      totalStmt = con.createStatement ();
      totalSet = totalStmt.executeQuery(totalQry);
      totalSet.next();
      totalRow = totalSet.getInt(1);
      System.out.println("totalRow is " + totalRow);

      String cubeQry = new String("select * from " + tableName.getText() + " order by set_size");
      cubeStmt = con.createStatement ();
      cubeSet = cubeStmt.executeQuery(cubeQry);
      while (cubeSet.next()) {
        cnt = cubeSet.getInt(colCnt); // in the cube record, the last column is the count
        // support(A=>B) = P(A U B)  -- count of A&B / count of total
        support = (double) cnt / (double) totalRow;
        // only process the record which passes the min support
        if (support < (Double.valueOf(supportChosen.getText()).doubleValue())/100) {
          continue;
        }
        aItemSet = new FreqItemSet();
        items = new TIntArrayList();
        // if it is an one-item set
        if (cubeSet.getInt(colCnt-1)==1) { // in a cube record, the second last column is set_size
          for (int bodyColIdx = 0; bodyColIdx < (colCnt - 2); bodyColIdx ++) {
            bodyValue = cubeSet.getString(bodyColIdx + 1);
            if (bodyValue != null) {
              tempLabel = colNames.get(bodyColIdx).toString() + "=" + bodyValue;
              // find the item index and save it
              itemIdx = getItemIdx(tempLabel);
              items.add(itemIdx);
            }
          }
          aItemSet.items = items;
          aItemSet.support = support;
          aItemSet.numberOfItems = 1;
          freqItemSets.add(aItemSet);
        } // end of if it is an one_item set

        else  { // If the cube record is not an one_item set,
          // one cube record will be a frequent item set. One cube record
          // will expand to multiple rule records, each of them split the cube
          // record to different head (antecedent) and body (consequent).
          // the last 2 columns are "set_size" and "cnt"
          // which are added during building cube, not the original column.
          for (int bodyColIdx = 0; bodyColIdx < (colCnt - 2); bodyColIdx ++) {
            bodyValue = cubeSet.getString(bodyColIdx + 1);
            if (bodyValue != null) {
              aRule = new Rule();
              // build frequent item set object
              tempLabel = colNames.get(bodyColIdx).toString() + "=" + bodyValue;
              itemIdx = getItemIdx(tempLabel);
              items.add(itemIdx);
              // build body part of the rule object
              bodyItems = new TIntArrayList();
              bodyItems.add(itemIdx);
              // build head part of the rule object
              headItems = new TIntArrayList();
              for (int headColIdx = 0; headColIdx < (colCnt - 2); headColIdx ++) {
                headValue = cubeSet.getString(headColIdx + 1);
                if (headColIdx != bodyColIdx && headValue!=null) {
                  tempLabel = colNames.get(headColIdx).toString() + "=" + headValue;
                  // find the item index and save it
                  headItemIdx = getItemIdx(tempLabel);
                  headItems.add(headItemIdx);
                }
              }
              // update the rule object.
              aRule.headIdx = getSetIdx(headItems);
              aRule.headSize = headItems.size();
              aRule.bodyIdx = getSetIdx(bodyItems);
              aRule.support = support;
              // confidence(A=>B) = P(B|A)   ----  support of A&B / support of A
              aRule.confidence = support/getSupport(aRule.headIdx);
              allRules.add(aRule);
            } // end of if bodyValue is not null
          } // end of for loop of bodyColIdx
          aItemSet.items = items;
          aItemSet.support = support;
          aItemSet.numberOfItems = items.size();
          freqItemSets.add(aItemSet);
          //printFreqItemSets();
        } // end of else (not an one item set)
      } // end of while loop
    } // end of try
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in extractRules.");
    }
    }
  }


  /** get the index of ArrayList itemLabels for the given item
   *  @param aItemLabel the item to check
   *  @return the index of the item in ArrayList itemLabels
   */
  protected int getItemIdx(String aItemLabel) {
    // find the index for the input item label
    for (int idx = 0; idx < itemLabels.size(); idx++) {
      if (aItemLabel.equals(itemLabels.get(idx))) {
        return (idx);
      }
    }
    // Should never reach here. All possible labels should be in itemLabels
    JOptionPane.showMessageDialog(msgBoard,
                "Can find the matched item label", "Error",
                JOptionPane.ERROR_MESSAGE);
    System.out.println("Cannot find the matched item label.");
    return (-1);
  }

  /** find the index of freqItemSets for the if-part or the then-part
   *  @param indexes the frequent item set to search. Frequent item set is
   *         represented as a list of indexes.
   *  @return the index of freqItemSets that matches the pass-in item set.
   */
  protected int getSetIdx(TIntArrayList indexes) {
    boolean found = true;
    for (int setIdx = 0; setIdx < freqItemSets.size(); setIdx++) {
      found = true;
      FreqItemSet aSet = (FreqItemSet)freqItemSets.get(setIdx);
      // only compare the lists that have the same size
      if (((TIntArrayList)aSet.items).size() == indexes.size()) {
        for (int itemIdx = 0; itemIdx < ((TIntArrayList)aSet.items).size(); itemIdx++) {
          int val1 = indexes.get(itemIdx);
          int val2 = ((TIntArrayList)aSet.items).get(itemIdx);
          if (val1 != val2) {
            found = false;
            continue;
          }
        }
        if (found == true) {
          return (setIdx);
        }
      }
    }
    // should never get to this point. Something is wrong!
    System.out.println("Cannot find the index for the frequent item set: ");
    for (int arrayIdx = 0; arrayIdx < indexes.size(); arrayIdx++) {
      System.out.print(indexes.get(arrayIdx) + ", ");
    }
    System.out.println(" ");
    return (-1);
  }

  /** get the value of support
   *  @param setIdx the item set to check
   *  @return the support for the given item set
   */
  protected double getSupport(int setIdx) {
    double aSupport = ((FreqItemSet)freqItemSets.get(setIdx)).support;
    return(aSupport);
  }

  protected void closeIt() {
    executionManager.moduleDone(this);
  }

  /** filter rules based on user specified support, confidence, condition columns
   *  and target columns.
   */
  protected void filterRules() {
    if (target.getText().length() < 1) {
      targetIdx = -1;
    }
    if (condition.getText().length() < 1) {
      conditionIdx = -1;
    }

    // only the rules related to target class are added to finalRules
    for (int ruleIdx = 0; ruleIdx < allRules.size(); ruleIdx ++) {
      Rule aRule = (Rule)allRules.get(ruleIdx);
      // filter out rules they have the low confidence
      if (aRule.confidence >= Double.valueOf(confidenceChosen.getText()).doubleValue()/100) {
        // only the rules that match specified the condition and target columns will be displayed
        if (matchCondition(aRule,conditionIdx) && matchTarget(aRule,targetIdx)) {
          // if a more general rule can be found, do not display the specific rule
          if (isARule(aRule)) {
            finalRules.add(aRule);
          }
        }
      }
    }
  }

  /** check whether the rule match user specified condition column
   *  @param rule1 the rule to check
   *  @param conditionIdx the condition column user has chosen
   *  @return true if the rule matches user chosen column, false otherwise
   */
  protected boolean matchCondition(Rule rule1, int conditionIdx) {
    if (conditionIdx == -1) { // no condition attribute is specified
      return (true);
    }
    else {
      FreqItemSet aSet = (FreqItemSet)freqItemSets.get(rule1.headIdx);
      TIntArrayList aList = (TIntArrayList)aSet.items;
      for (int itemIdx = 0; itemIdx < aList.size(); itemIdx ++) {
        int idx = aList.get(itemIdx);
        if (idx >= itemRange[conditionIdx][0] &&
            idx <= itemRange[conditionIdx][1]) {
          return (true);
        }
      }
    }
    return (false);
  }

  /** check whether the rule match user specified target column
   *  @param rule1 the rule to check
   *  @param targetIdx the target column user has chosen
   *  @return true if the rule matches user chosen column, false otherwise
   */
  protected boolean matchTarget(Rule rule1, int targetIdx) {
    if (targetIdx == -1) { // no target attribute is specified
      return (true);
    }
    else {
      FreqItemSet aSet = (FreqItemSet)freqItemSets.get(rule1.bodyIdx);
      TIntArrayList aList = (TIntArrayList)aSet.items;
      for (int itemIdx = 0; itemIdx < aList.size(); itemIdx ++) {
        int idx = aList.get(itemIdx);
        if (idx >= itemRange[targetIdx][0] &&
            idx <= itemRange[targetIdx][1]) {
          return (true);
        }
      }
    }
    return (false);
  }

  /** search allRules to find whether this rule is significant:
  *   such as: Immediate parent rule has much lower confidence.
  *   @param rule1 the rule to verify
  *   @return true if the rule has significant higher confidence than parents,
  *           false otherwise.
  */
  protected boolean isARule(Rule rule1) {
    // head is if-part (left-hand-side), and body is then-part (right-hand-side)
    FreqItemSet head1 = (FreqItemSet)freqItemSets.get(rule1.headIdx);
    FreqItemSet body1 = (FreqItemSet)freqItemSets.get(rule1.bodyIdx);
    // no parent rule for 2-item (1 for head and 1 for body) set
    if (rule1.headSize <= 1) {
      return(true);
    }
    // we first compare the immediate parent, then compare ancestor parents
    for (int i=(finalRules.size()-1); i>=0; i--) {
      Rule rule2 = (Rule)finalRules.get(i);
      if (rule1.headSize <= rule2.headSize) {
        continue;
      }
      else if (rule1.headSize > rule2.headSize) {
        FreqItemSet head2 = (FreqItemSet)freqItemSets.get(rule2.headIdx);
        FreqItemSet body2 = (FreqItemSet)freqItemSets.get(rule2.bodyIdx);
        // if the body parts of two rules are different, don't compare the head parts of the rules
        if (body1 != body2) {
          continue;
        }
        int matchItem = 0;
        TIntArrayList list2 = (TIntArrayList)head2.items;
        for (int r2 = 0; r2 < rule2.headSize; r2++) {
          int item2 = list2.get(r2);
          TIntArrayList list1 = (TIntArrayList)head1.items;
          for (int r1 = 0; r1 < rule1.headSize; r1++) {
            int item1 = list1.get(r1);
            if (item1 == item2) {
              matchItem = matchItem + 1;
              continue;
            }
          }
        }
        if (matchItem == rule2.headSize) { // rule2 is the parent rule of rule1
          if ((rule1.confidence - rule2.confidence) < Double.valueOf(thresholdChosen.getText()).doubleValue()) {
            return(false);
          }
          else {// rule1's confidence is significant higher than rule2
            // This rule may have several parent rule, return true only when
            // its confidence higher than any ancestors
            continue;
          }
        }
      }
    }
    // if we cannot find any ancestor rules or all ancestor rules' confidence
    // are way below the prunning threshold, this rule should be kept
    return(true);
  }

  /** sort the rule table based on the user specified mode
   *  @param mode either sort by confidence or by support.
   */
  protected void sortRuleTable(int mode) {
    for (int i=0; i<ruleTable.getNumRows()-1; i++) {
      for (int j=ruleTable.getNumRows()-1; j>i; j--) {
        if (mode == SUPPORT) {
          if (ruleTable.getDouble(j-1, 2) < ruleTable.getDouble(j,2)) {
            ruleTable.swapRows(j-1, j);
          }
        }
        else if (mode == CONFIDENCE) {
          if (ruleTable.getDouble(j-1, 3) < ruleTable.getDouble(j,3)) {
            ruleTable.swapRows(j-1, j);
          }
        }
      }
    }
  }

  /** convert ArrayList to table
   */
  protected void convertToRuleTable() {
    Column[] cols = new Column[4];
    cols[0] = new ObjectColumn(finalRules.size());
    cols[1] = new ObjectColumn(finalRules.size());
    cols[2] = new DoubleColumn(finalRules.size());
    cols[3] = new DoubleColumn(finalRules.size());
    cols[0].setLabel("Head");
    cols[1].setLabel("Body");
    cols[2].setLabel("Support");
    cols[3].setLabel("Confidence");
    ruleTable = (TableImpl)DefaultTableFactory.getInstance().createTable(cols);
    String tmpVal;
    for (int ruleIdx = 0; ruleIdx < finalRules.size(); ruleIdx++) {
      Rule aRule = (Rule)finalRules.get(ruleIdx);
      ruleTable.setInt(aRule.headIdx,ruleIdx,0);
      ruleTable.setInt(aRule.bodyIdx,ruleIdx,1);
      ruleTable.setDouble(aRule.support,ruleIdx,2);
      ruleTable.setDouble(aRule.confidence,ruleIdx,3);
    }
  }

  public void printFinalRules() {
    System.out.println("final rules are: ");
    for (int m = 0; m < finalRules.size(); m++) {
      Rule aRule = (Rule)finalRules.get(m);
      System.out.print(aRule.headIdx + ", ");
      System.out.print(aRule.bodyIdx + ", ");
      System.out.print(aRule.support + ", ");
      System.out.print(aRule.confidence + ", ");
      System.out.println(" ----- ");
    }
  }

  public void printAllRules() {
    System.out.println("All rules are: ");
    for (int m = 0; m < allRules.size(); m++) {
      Rule aRule = (Rule)allRules.get(m);
      System.out.print(aRule.headIdx + ", ");
      System.out.print(aRule.bodyIdx + ", ");
      System.out.print(aRule.support + ", ");
      System.out.print(aRule.confidence + ", ");
      System.out.println(" ----- ");
    }
  }

  public void printFreqItemSets() {
    System.out.println("Frequent Item Sets: ");
    for (int m = 0; m < freqItemSets.size(); m++) {
      FreqItemSet aSet = (FreqItemSet)freqItemSets.get(m);
      for (int n = 0; n < aSet.items.size(); n++) {
        System.out.print(aSet.items.get(n) + ", ");
      }
      System.out.println(" ");
      System.out.println("number of items is " + aSet.numberOfItems);
      System.out.println("support is " + aSet.support);
    }
  }

  public void printRuleTable() {
    System.out.println("Rule table is: ");
    for (int m = 0; m < ruleTable.getNumRows(); m++) {
      System.out.print(ruleTable.getInt(m,0) + ", ");
      System.out.print(ruleTable.getInt(m,1) + ", ");
      System.out.print(ruleTable.getDouble(m,2) + ", ");
      System.out.println(ruleTable.getDouble(m,3));
    }
  }

  public void printArrayList(ArrayList al) {
    System.out.println("Array list: ");
    for (int i = 0; i < al.size(); i++) {
      System.out.println("item" + i + " is " + al.get(i).toString() + ", ");
    }
  }

  public void printItemRange() {
    System.out.println("target item range list: ");
    for (int i = 0; i < itemRange.length; i++) {
      System.out.println("target " + i + " is " + itemRange[i][0] + " and " + itemRange[i][1]);
    }
  }


        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "SQL Get Rule Assoc With Code Book";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "Database Connection";
                        case 1:
                                return "Cube Table";
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
                                return "Rule Table";
                        default: return "NO SUCH OUTPUT!";
                }
        }

}