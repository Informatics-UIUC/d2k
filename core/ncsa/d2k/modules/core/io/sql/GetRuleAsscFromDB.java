package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: GetRuleAsscFromDB </p>
 * <p>Description: Extract association rules from a cube table generated by ArrayCube algorithm </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */


import ncsa.d2k.core.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.text.*;
import javax.swing.table.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class GetRuleAsscFromDB extends UIModule
        {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  double minSupport = 0.2;
  double minConfidence = 0.7;
  int colCnt;
  int totalRow;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "NOAVL";
  String cubeTableName = NOTHING;
  String saveSupport = NOTHING;
  // ArrayList for column names
  ArrayList colNames;
  // ArrayList to keep rules represented by item-indexes
  // (the indexes map to the array index of "itemLabels").
  // The items except the last 3 are left handside rules
  // The last 3 items in the list are: right hadside rules, support and confident
  ArrayList resultList;
  // Array to keep all rules
  ArrayList allRules;
  ArrayList finalRules;
  TableImpl ruleTable;
  // ArrayList to keep all rule labels
  ArrayList itemLabels;

  JTextField tableName;
  JTextField condition;
  JTextField target;
  JTextField supportChosen;
  JTextField confidenceChosen;
  Checkbox sortS;
  Checkbox sortC;
  JButton tableBrowseBtn;
  JButton columnBrowseBtn;
  JButton condBrowseBtn;
  JButton cancelBtn;
  JButton ruleBtn;
  JButton sortBtn;
  int[][] itemRange;

  BrowseTables bt;
  BrowseTablesView btw;
  int targetIdx = -1;
  int conditionIdx = -1;

  public GetRuleAsscFromDB() {
  }

  public String getOutputInfo (int i) {
		switch (i) {
			case 0: return "Each rule consists of a list of distinct rule ids followed by a confidence and a support value. ";
			case 1: return "An array that contains all rule labels (column name => column value).";
			default: return "No such output";
		}
	}

  public String getInputInfo (int i) {
		switch (i) {
			case 0: return "JDBC data source to make database connection.";
			case 1: return "The name of the table which stores the data statistics.";
			default: return "No such input";
		}
	}

  public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Extract association rules from a cube table generated by ArrayCube     algorithm  </body></html>";
	}

  public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
		return types;
	}

  public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
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
      supportChosen.setText(Double.toString(minSupport));
      confidenceChosen.setText(Double.toString(minConfidence));
    }

    public Dimension getPreferredSize() {
      return new Dimension (500, 220);
    }

    public void initView(ViewModule mod) {
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
      //tableName.setText(NOTHING);
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
      Constrain.setConstraints(options, columnBrowseBtn = new JButton ("Browse"),
        15,2,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      columnBrowseBtn.addActionListener(this);

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

      /* Add the outline panel to getRulePanel */
      Constrain.setConstraints(getRulePanel, options,
        0,0,5,5,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(getRulePanel, cancelBtn = new JButton ("   Cancel   "),
        2,5,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      cancelBtn.addActionListener(this);
      Constrain.setConstraints(getRulePanel, ruleBtn = new JButton ("Get Rules"),
        3,5,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      ruleBtn.addActionListener(this);
      Constrain.setConstraints(getRulePanel, sortBtn = new JButton ("    Resort    "),
        4,5,0,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      sortBtn.addActionListener(this);

      setLayout (new BorderLayout());
      add(getRulePanel, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == tableBrowseBtn) {
        doTableBrowse();
      }
      else if (src == condBrowseBtn) {
        doColumnBrowse(0);
      }
      else if (src == columnBrowseBtn) {
        doColumnBrowse(1);
      }
      else if (src == ruleBtn) {
        if (tableName.getText().length()>0) {
          // only recalculate rules if table name or support are changed
          if (!cubeTableName.equals(tableName.getText().toString()) ||
              !saveSupport.equals(supportChosen.getText().toString())  ) {
            cubeTableName = tableName.getText().toString();
            saveSupport = supportChosen.getText().toString();
            allRules = new ArrayList();
            getItemLabels();
            //printItemLabels(); // for debugging
            //printItemRange(); // for debugging
            extractRules();
          }
          //dump allRules for debugging
          //printAllRules();
          finalRules = new ArrayList();
          filterRules();
          if (sortC.getState()) {
            sortList(1);
          }
          else if (sortS.getState()) {
            sortList(2);
          }
          convertToRuleTable();

          pushOutput (ruleTable,0);
          pushOutput (itemLabels, 1);
        }
        else { // The user has not chosen a table yet
          JOptionPane.showMessageDialog(msgBoard,
          "Click the button 'Browse' to choose a table first.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no table selected.");
        }
      }
      else if (src == cancelBtn) {
        closeIt();
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
          sortList(1);
        }
        else if (sortS.getState()) {
          sortList(2);
        }
        //ruleTable.initTableModel(100,5);
        if (finalRules.size() > 0) {
          pushOutput (ruleTable, 0);
          pushOutput (itemLabels, 1);
        }
      }
    }

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
    }
  }

  /** connect to a database and retrieve the list of available cube tables */
  protected void doTableBrowse() {
    String query = "select table_name from all_tables where table_name like '%_CUBE'";
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

  /** connect to a database and retrieve the column list of the cube table */
  protected void doColumnBrowse(int colType) {
    // get condition column use colType = 0 and
    // get target column use colType = 1
    String query = new String("select column_name from all_tab_columns where table_name = '");
    query = query + tableName.getText() + "' order by column_id";
    try {
      bt = new BrowseTables(cw, query);
      //bt = new BrowseTables(cw, query);
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
            conditionIdx = btw.selectedRow;
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
            targetIdx = btw.selectedRow;
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

  protected void getItemLabels() {
    itemLabels = new ArrayList();
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
        // add a column name to ArrayList colNames
        String valueQry = new String("select distinct " + colName + " from ");
        valueQry = valueQry + tableName.getText() + " where " + colName + " is not null";
        valueStmt = con.createStatement();
        valueSet = valueStmt.executeQuery(valueQry);
        min = itemLabels.size();
        while (valueSet.next()) {
          itemLabels.add(colName+"="+valueSet.getString(1));
        }
        max = itemLabels.size()-1;
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

  protected void getColNames() {
    Statement nameStmt;
    Statement cntStmt;
    ResultSet nameSet;
    ResultSet cntSet;
    int colIdx = 0;
    colNames = new ArrayList();
    colCnt = 0;
    try {
      con = cw.getConnection();
      String cntQry = new String("select count(column_name) from all_tab_columns where table_name = '");
      cntQry = cntQry + tableName.getText() + "'";
      cntStmt = con.createStatement ();
      cntSet = cntStmt.executeQuery(cntQry);
      cntSet.next();
      colCnt = cntSet.getInt(1);
      //cntStmt.close();

      String nameQry = new String("select column_name from all_tab_columns where table_name = '");
      nameQry = nameQry + tableName.getText() + "' order by column_id";
      nameStmt = con.createStatement ();
      nameSet = nameStmt.executeQuery(nameQry);
      // the last 2 columns are "set_size" and "cnt" which are added during building cube, not the original column.
      while (colIdx < (colCnt-2)) {
        nameSet.next();
        String colName = nameSet.getString(1);
        // add a column name to ArrayList colNames
        colNames.add(colName);
        colIdx ++;
      }
      // An integer array to keep min itemIdx and max itemIdx for each target attribute
      itemRange = new int[colNames.size()][2];
      //nameStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getColNames.");
    }

  }
  protected void extractRules() {
    Statement totalStmt;
    Statement cubeStmt;
    ResultSet totalSet;
    ResultSet cubeSet;
    String inColValue;
    String outColValue;
    String tempLabel;
    int cnt;
    int itemIdx;
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
      String totalQry = new String("select max(cnt) from " + tableName.getText());
      //System.out.println("the query string for total row count is " + totalQry);
      totalStmt = con.createStatement ();
      totalSet = totalStmt.executeQuery(totalQry);
      totalSet.next();
      totalRow = totalSet.getInt(1);
      //totalSet.close();
      System.out.println("totalRow is " + totalRow);

      String cubeQry = new String("select * from " + tableName.getText() + " order by set_size");
      //System.out.println("the query string for cube is " + cubeQry);
      cubeStmt = con.createStatement ();
      cubeSet = cubeStmt.executeQuery(cubeQry);
      while (cubeSet.next()) {
        cnt = cubeSet.getInt(colCnt); // in the cube record, the last column is the count
        // support(A=>B) = P(A U B)  -- count of A&B / count of total
        support = (double) cnt / (double) totalRow;
        // only process the record which passes the min support
        if (support < (Double.valueOf(supportChosen.getText()).doubleValue())) {
          continue;
        }
        // if it is an one-item set
        if (cubeSet.getInt(colCnt-1)==1) { // in a cube record, the second last column is set_size
          resultList = new ArrayList();
          for (int outputColIdx = 0; outputColIdx < (colCnt - 2); outputColIdx ++) {
            outColValue = cubeSet.getString(outputColIdx + 1);
            if (outColValue != null) {
              tempLabel = colNames.get(outputColIdx).toString() + "=" + outColValue;
              // find the item index and save it
              itemIdx = getItemIdx(tempLabel);
              resultList.add(Integer.toString(itemIdx));
              // add support
              resultList.add(Double.toString(support));
              // add confidence. Confidence for one_item is 1
              resultList.add("1");
              allRules.add(resultList);
            } // end of if outColValue != null
          } // end of for
        } // end of if it is an one_item set
        else  { // not an one_item set
          // one cube record will expand to multiple rule records,
          // and each of them is against to a different output column.
          // the last 2 columns are "set_size" and "cnt" which are added during building cube, not the original column.
          for (int outputColIdx = 0; outputColIdx < (colCnt - 2); outputColIdx ++) {
              outColValue = cubeSet.getString(outputColIdx + 1);
              if (outColValue != null) {
                resultList = new ArrayList();
                for (int inputColIdx = 0; inputColIdx < (colCnt - 2); inputColIdx ++) {
                  inColValue = cubeSet.getString(inputColIdx + 1);
                  if (inputColIdx != outputColIdx && inColValue!=null) {
                    tempLabel = colNames.get(inputColIdx).toString() + "=" + inColValue;
                    // find the item index and save it
                    itemIdx = getItemIdx(tempLabel);
                    resultList.add(Integer.toString(itemIdx));
                  }
                }
                /* now, the rule of lefthand side is completed, work on the rule of righthand side */
                tempLabel = colNames.get(outputColIdx).toString() + "=" + outColValue;
                itemIdx = getItemIdx(tempLabel);
                resultList.add(Integer.toString(itemIdx));
                /* add support to resultList */
                resultList.add(Double.toString(support));
                /* search allRules to find the rule that can be used to calculate confidence. */
                // confidence(A=>B) = P(B|A)   ----  count of A&B / count of A
                parentSupport = findParentSupport(resultList);
                confidence = support/parentSupport;
                //if (confidence > Double.valueOf(confidenceChosen.getText()).doubleValue()) {
                  resultList.add(Double.toString(confidence));
                  allRules.add(resultList);
                //}
              } // end of if outColValue is not null
          } // end of for loop of outputColIdx
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

  protected double findParentSupport(ArrayList aList) {
    int listSize;
    boolean match = true;
    String aValue;
    double aSupport = 1.0;
    /* search allRules to find the support of a rule that can be used to calculate confidence. */
    for (int ruleIdx = 0; ruleIdx < allRules.size(); ruleIdx ++) {
      listSize = ((ArrayList) allRules.get(ruleIdx)).size();
      // the parent rule set should have the same size as resultList,
      // because parent rule set should have one less item, but resultList has no confidence at this point
      if ((listSize)==aList.size()) {
        match = true;
        // not compare the last 2 items in the list, one is for output item, one is for support
        for (int itemIdx = 0; itemIdx < listSize-2; itemIdx ++) {
          if (!((ArrayList) allRules.get(ruleIdx)).get(itemIdx).equals(aList.get(itemIdx))) {
            match = false;
            break;
          }
        }
        if (match == true) {
          aValue = ((ArrayList) allRules.get(ruleIdx)).get(listSize-2).toString();
          aSupport = Double.valueOf(aValue).doubleValue();
          break;
        }
      }
    }
    if (match == true) {
      return (aSupport);
    }
    else {
      // Should not reach here
      System.out.println("cannot find the parent support for the list: " );
      for (int i=0; i<aList.size(); i++) {
        System.out.println(aList.get(i));
      }
      return (-100.0);
    }
  }

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

  protected void closeIt() {
    executionManager.moduleDone(this);
  }

  protected void filterRules() {
    if (target.getText().length() < 1) {
      targetIdx = -1;
    }
    if (condition.getText().length() < 1) {
      conditionIdx = -1;
    }
    // only the rules related to target class are added to finalRules
    for (int ruleIdx = 0; ruleIdx < allRules.size(); ruleIdx ++) {
      ArrayList tmpArray = (ArrayList) allRules.get(ruleIdx);
      // single item set has the rule size 3: item + support + confident. Don't display single item set
      if (tmpArray.size() > 3 ) {
        if (matchCondition(tmpArray)) {
          // the last item is the confidence
          double tmpConf = Double.valueOf(tmpArray.get(tmpArray.size()-1).toString()).doubleValue();
          if (targetIdx >= 0) {
            // the last third item is the target attribute
            int tmpIdx = Integer.parseInt(tmpArray.get(tmpArray.size()-3).toString());
            if (tmpIdx >= itemRange[targetIdx][0] &&
              tmpIdx <= itemRange[targetIdx][1] &&
              tmpConf >= Double.valueOf(confidenceChosen.getText()).doubleValue()) {
              finalRules.add(tmpArray);
            }
          }
          else { // The target attribute is not specified
            if (tmpConf >= Double.valueOf(confidenceChosen.getText()).doubleValue()) {
              finalRules.add(tmpArray);
            }
          }
        }
      }
    }
  }

  protected boolean matchCondition(ArrayList aList) {
    if (conditionIdx == -1) { // no condition attribute is specified
      return (true);
    }
    else {
      // condition attributes is from index 0 to aList.size()-4
      for (int itemIdx = 0; itemIdx < aList.size()-3; itemIdx ++) {
        int idx = Integer.parseInt(aList.get(itemIdx).toString());
        //System.out.println("idx is " + idx);
        if (idx >= itemRange[conditionIdx][0] &&
            idx <= itemRange[conditionIdx][1]) {
          return (true);
        }
      }
    }
    return (false);
  }
  // mode 1: sort by confidence, mode 2 sort by support
  protected void sortList(int mode) {
    for (int i=0; i<finalRules.size()-1; i++) {
      for (int j=finalRules.size()-1; j>i; j--) {
        if (! compare((ArrayList)finalRules.get(j-1), (ArrayList)finalRules.get(j), mode)) {
          Object tmp = finalRules.get(j-1);
          finalRules.set(j-1, finalRules.get(j));
          finalRules.set(j, tmp);;
        }
      }
    }
  }

  protected boolean compare(ArrayList arrayList1, ArrayList arrayList2, int sortMode) {
    double value1;
    double value2;
    value1 = Double.valueOf(arrayList1.get(arrayList1.size()-sortMode).toString()).doubleValue();
    value2 = Double.valueOf(arrayList2.get(arrayList2.size()-sortMode).toString()).doubleValue();
    if (value1 < value2)
      return (false);
    else
      return (true);
  }

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
      int ruleLength = ((ArrayList)finalRules.get(ruleIdx)).size();
      if (ruleLength <= 3){
        continue;
      }
      int[] head = new int[ruleLength - 3];
      int[] body = new int[1]; // we only implement single target class at this moment
      float supportVal = (float)0.0;
      float confidentVal = (float)0.0;
      for (int itemIdx = 0; itemIdx < ruleLength; itemIdx++) {
        tmpVal = ((ArrayList)finalRules.get(ruleIdx)).get(itemIdx).toString();
        // head components are up to the last 3 items
        if (itemIdx < (ruleLength - 3)) {
          head[itemIdx] = Integer.parseInt(tmpVal);
        }
        else if (itemIdx == (ruleLength - 3))
          body[0] = Integer.parseInt(tmpVal);
        else if (itemIdx == (ruleLength - 2))
          supportVal = Float.valueOf(tmpVal).floatValue();
        else if (itemIdx == (ruleLength - 1))
          confidentVal = Float.valueOf(tmpVal).floatValue();
      }
      ruleTable.setObject(head,ruleIdx,0);
      ruleTable.setObject(body,ruleIdx,1);
      ruleTable.setFloat(supportVal,ruleIdx,2);
      ruleTable.setFloat(confidentVal,ruleIdx,3);
    }
  }

  public void printFinalRules() {
    System.out.println("final rules are: ");
    String tmpVal;
    for (int m = 0; m < finalRules.size(); m++) {
      for (int n = 0; n < ((ArrayList)finalRules.get(m)).size(); n++) {
        tmpVal = ((ArrayList)finalRules.get(m)).get(n).toString();
        System.out.print(tmpVal + ", ");
      }
      System.out.println(" ----- ");
    }
  }

  public void printAllRules() {
    System.out.println("All rules are: ");
    String tmpVal;
    for (int i = 0; i < allRules.size(); i++) {
      for (int j = 0; j < ((ArrayList)allRules.get(i)).size(); j++) {
        tmpVal = ((ArrayList)allRules.get(i)).get(j).toString();
        System.out.print(tmpVal + ", ");
      }
    }
    System.out.println(" ----- ");
  }

  public void printRuleTable() {
    System.out.println("Rule table is: ");
    for (int i = 0; i < ruleTable.getNumRows(); i++) {
      for (int j = 0; j < ruleTable.getNumColumns(); j++) {
        System.out.print(ruleTable.getObject(i,j) + ", ");
      }
    }
    System.out.println(" ----- ");
  }

  public void printItemLabels() {
    System.out.println("item label list: ");
    for (int i = 0; i < itemLabels.size(); i++) {
      System.out.println("item" + i + " is " + itemLabels.get(i).toString() + ", ");
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
		return "GetRuleAsscFromDB";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
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
				return "output0";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
