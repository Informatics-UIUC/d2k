package ncsa.d2k.modules.core.discovery.ruleassociation;

/**
 * <p>Title: RuleAssocReport </p>
 * <p>Description: Display rule association </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

public class RuleAssocReport extends UIModule {
  File file;
  FileWriter fw;
  String cubeTableName;
  int colCnt;
  int totalRow;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";

  // TableImpl object to keep rules. The table has 4 columns: head, body, support and confidence
  RuleTable ruleTable;

  // ArrayList object to keep all item labels in the format "column_name=column_value"
  ArrayList itemLabels;

  // ArrayList object to keep frequent item sets. Each set is an object of FreqItemSet.
  ArrayList freqItemSets;

  int numberOfTransactions;
  String [] columnHeading;
  RuleModel ruleModel;
  JTable ruleList;
  JButton doneBtn;
  JButton abortBtn;
  boolean displayCount = false;

  public RuleAssocReport() {

  }

  public String getInputInfo (int i) {
    switch (i) {
      case 0: return "A table that has 4 columns: Head, Body, Support and Confidence.";
      default: return "No such input";
    }
  }

  public String getOutputInfo (int i) {
    switch (i) {
      default: return "No such output";
    }
  }

  public String getModuleInfo () {
        String s = "<p> Overview: ";
        s += "This module displays the association rules in a tablet form. </p>";
        s += "<p> Detailed Description: ";
        s += "This module takes a rule table, that has 4 columns: Head, Body, ";
        s += "Support and Confidence, as an input and displays the association ";
        s += "rules in a tablet form. The first column in the table is the 'IF' ";
        s += "part of the rule, the third column is the 'THEN' part of the rule, ";
        s += "the second column is the logic symbol '==>' to connect IF and THEN, ";
        s += "and the forth and fifth columns represent the rule's support and ";
        s += "confidence, respectively. In the case that the frequent counts are ";
        s += "preferred, the property parameter <i>displayCount</i> can be set to ";
        s += "true. This will make the frequent counts to be displayed as the ";
        s += "sixth column. The rules displayed in the table can be filtered by different ";
        s += "threshold values for support and confidence. </p>";
        s += "<p>The rules can be sorted by clicking on a column header.";
        s += "<p>The <i>File</i> pull-down menu offers a <i>Save</i> option to ";
        s += "save the displayed rules to a file. ";
        s += "A file browser window pops up, allowing the user to select ";
        s += "where the rules should be saved. ";
        s += "<p>The <i>Done</i> button closes the display window. ";
        s += "The <i>Abort</i> button closes the display window and aborts itinerary execution. ";
        s += "<p> Restrictions: ";
        s += "We currently only support Oracle, SqlServer, DB2 and MySql databases.";

        return s;
  }

  public String[] getOutputTypes () {
    String[] types = {		};
    return types;
  }

  public String[] getInputTypes () {
    String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
    return types;
  }



  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "Rule Table";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputName(int index) {
    return "NO SUCH OUTPUT!";
  }

  public String getModuleName() {
    return "Rule Assoc Report";
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
    PropertyDescription [] pds = new PropertyDescription [1];
    pds[0] = new PropertyDescription ("displayCount",
        "Display Count", "Choose true if you want <i>Count</i> is displayed in " +
        "additional to <i>Support</i> and <i>Confidence</i>. ");
    return pds;
  }

  public boolean getDisplayCount() {
          return displayCount;
  }

  public void setDisplayCount(boolean b) {

          displayCount = b;

  }

  /**
        Create the UserView object for this module-view combination.
        @return The UserView associated with this module.
  */

  protected UserView createUserView() {
    return new DisplayRuleView();
  }

  public class DisplayRuleView extends JUserInputPane
      implements ActionListener {
    JMenuBar menuBar;
    JMenuItem print;

    /** a reference to our parent module */
    protected RuleAssocReport parent;

    public void setInput(Object input, int index) {
      removeAll();
      ruleTable = (RuleTable)input;
      itemLabels = (ArrayList)((RuleTable)ruleTable).getNamesList();
      freqItemSets = (ArrayList)((RuleTable)ruleTable).getItemSetsList();
      numberOfTransactions = ruleTable.getNumberOfTransactions();
      doGUI();
      this.validate();
      this.repaint();
    }

    public Dimension getPreferredSize() {
      return new Dimension (800, 350);
    }

    public void initView(ViewModule mod) {
      parent = (RuleAssocReport)mod;
      menuBar = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      print = new JMenuItem("Save...");
      print.addActionListener(this);
      fileMenu.add(print);
      menuBar.add(fileMenu);
    }

    public Object getMenu() {
      return menuBar;
    }

    public void doGUI() {
      // Panel to hold outline panels
      JPanel displayRulePanel = new JPanel();
      displayRulePanel.setLayout (new GridBagLayout());
      // Outline panel for rules
      final String[] columnHeading = new String[6];
      columnHeading[0] = "IF";
      columnHeading[1] = "-->";
      columnHeading[2] = "THEN";
      columnHeading[3] = "Support %";
      columnHeading[4] = "Confidence %";
      columnHeading[5] = "Count";

      JOutlinePanel ruleInfo = new JOutlinePanel("Association Rules");
      ruleInfo.setLayout (new GridBagLayout());

      ruleModel = new RuleModel(ruleTable.getNumRows(),6,columnHeading);
      ruleList = new JTable(ruleModel);
      ruleList.getTableHeader().setReorderingAllowed(false);
      ruleList.setColumnSelectionAllowed(false);
      MouseAdapter ruleMouseListener = new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
           TableColumnModel columnModel = ruleList.getColumnModel();
           int viewCol = columnModel.getColumnIndexAtX(e.getX());
           if(viewCol == 0) {
             // sort by antecedent
             ruleTable.sortByAntecedent();
           }
           else if(viewCol == 2) {
             ruleTable.sortByConsequent();
           }
           else if(viewCol == 3) {
             // sort by support
             ruleTable.sortBySupport();
           }
           else if(viewCol == 4) {
             // sort by confidence
             ruleTable.sortByConfidence();
           }
           else if (displayCount && (viewCol == 5)) {
             // sort by count (only if displayCount is turned on) is equivalent to sort by support
             ruleTable.sortBySupport();
           }
           ruleModel = new RuleModel(ruleTable.getNumRows(),6,columnHeading);
           ruleList.setModel(ruleModel);
           TableColumnModel colModel = ruleList.getColumnModel();
           colModel.getColumn(0).setPreferredWidth(400);
           colModel.getColumn(1).setPreferredWidth(25);
           colModel.getColumn(2).setPreferredWidth(250);
           colModel.getColumn(3).setPreferredWidth(50);
           colModel.getColumn(4).setPreferredWidth(50);
           if (displayCount)
             colModel.getColumn(5).setPreferredWidth(50);
           else
             colModel.removeColumn(colModel.getColumn(5));
        }
      };

      JTableHeader th = ruleList.getTableHeader();
      th.addMouseListener(ruleMouseListener);
      TableColumnModel colModel = ruleList.getColumnModel();
      colModel.getColumn(0).setPreferredWidth(400);
      colModel.getColumn(1).setPreferredWidth(25);
      colModel.getColumn(2).setPreferredWidth(250);
      colModel.getColumn(3).setPreferredWidth(50);
      colModel.getColumn(4).setPreferredWidth(50);

      if (displayCount)
        colModel.getColumn(5).setPreferredWidth(50);
      else
        colModel.removeColumn(colModel.getColumn(5));

      JScrollPane tablePane = new JScrollPane(ruleList);
      ruleList.setPreferredScrollableViewportSize (new Dimension(600,350));
      Constrain.setConstraints(ruleInfo, tablePane,
                0,0,1,1,GridBagConstraints.BOTH, GridBagConstraints.WEST,1,1);

      /* Add the outline panel to displayRulePanel */
      Constrain.setConstraints(displayRulePanel, ruleInfo,
                0,0,4,1,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0);
      Constrain.setConstraints(displayRulePanel, new JPanel(),
                0,1,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,.5,0);
      Constrain.setConstraints(displayRulePanel, abortBtn = new JButton ("Abort"),
                1,1,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,0,0);
      Constrain.setConstraints(displayRulePanel, doneBtn = new JButton ("Done"),
                2,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,0,0);
      Constrain.setConstraints(displayRulePanel, new JPanel(),
                3,1,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,.5,0);
      abortBtn.addActionListener(this);
      doneBtn.addActionListener(this);

      setLayout (new BorderLayout());
      add(displayRulePanel, BorderLayout.CENTER);
    }



    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == print) {
        writeToFile();
      }
      else if (src == doneBtn) {
        closeIt();
      }
      else if (src == abortBtn) {
        parent.viewCancel();
      }
    }
  }

  protected void closeIt() {
    executionManager.moduleDone(this);
  }

  protected void printItemLabels() {
    System.out.println("item label list in RuleAssocReport: ");
    for (int i = 0; i < itemLabels.size(); i++) {
      System.out.println("item" + i + " is " + itemLabels.get(i).toString() + ", ");
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

  protected void writeToFile() {
    JFileChooser chooser = new JFileChooser();
    String fileName;
    int retVal = chooser.showSaveDialog(null);
    if(retVal == JFileChooser.APPROVE_OPTION)
       fileName = chooser.getSelectedFile().getAbsolutePath();
    else
       return;
    try {
      fw = new FileWriter(fileName);
      String s = "RULE ASSOCIATION: ";
      fw.write(s, 0, s.length());
      fw.write(ELN.toCharArray(), 0, ELN.length());
      fw.write(ELN.toCharArray(), 0, ELN.length());

      // write the actual data
      for(int rowIdx = 0; rowIdx < ruleList.getRowCount(); rowIdx++) {
        for (int colIdx = 0; colIdx < ruleList.getColumnCount(); colIdx++) {
          s = NOTHING;
          if (colIdx == 0) {
            s = "IF (";
            s = s + ruleList.getValueAt(rowIdx, colIdx).toString() + ") ";
          }
          else if (colIdx == 2) {
            s = "THEN (";
            s = s + ruleList.getValueAt(rowIdx, colIdx).toString() + ") ";
          }
          else if (colIdx == 3) {
            s = "with SUPPORT ";
            s = s + ruleList.getValueAt(rowIdx, colIdx).toString();
          }
          else if (colIdx == 4) {
            s = " and CONFIDENCE ";
            s = s + ruleList.getValueAt(rowIdx, colIdx).toString();
          }
          else if (displayCount && colIdx == 5) {
            s = " and COUNT ";
            s = s + ruleList.getValueAt(rowIdx, colIdx).toString();
          }
          fw.write(s, 0, s.length());
        }
        fw.write(ELN.toCharArray(), 0, ELN.length());
      }
      fw.flush();
      fw.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  private class RuleModel extends AbstractTableModel {
    Object data[][];
    String columnNames [];
    boolean edit = false;
    String  NOTHING = "";

    /**
     * Table Model for displaying Bin information: attribute name, type, number of bins, input or output
     * @param maxRow Maximum number of rows in the table
     * @param maxCol Maximum number of columns in the table
     * @param editable Is this table editable?
     */
    public RuleModel(int maxRow, int maxCol, String [] columnHeading)
    {
      data = new Object[maxRow][maxCol];
      columnNames = columnHeading;

      // now initialize the data with the entries from the table
      String leftRule;
      String rightRule;
      int headIdx;
      int bodyIdx;
      int labelIdx;
      String aLabel;
      FreqItemSet aSet;

      // layout of ruleList is: column 1: left handside rule (if rule),
      //                        column 2: symbol "-->",
      //                        column 3: right handside rule (then rule),
      //                        column 4: support,
      //                        column 5: confidence.
      int listSize;
      double aConfidence;

      for (int ruleIdx = 0; ruleIdx < ruleTable.getNumRows(); ruleIdx++) {
        leftRule = NOTHING;
        rightRule = NOTHING;

        // display the head part (left) of the rule
        headIdx = ruleTable.getRuleAntecedentID(ruleIdx);
        aSet = (FreqItemSet)freqItemSets.get(headIdx);
        for (int itemIdx = 0; itemIdx < aSet.numberOfItems; itemIdx++) {
             labelIdx = aSet.items.get(itemIdx);
             aLabel = itemLabels.get(labelIdx).toString();
             if (itemIdx < (aSet.numberOfItems - 1))
               leftRule = leftRule + aLabel + ",";
             else {
               leftRule = leftRule + aLabel;
               data[ruleIdx][0] = leftRule;
             }
       }

       data[ruleIdx][1] = " -->";
       bodyIdx = ruleTable.getRuleConsequentID(ruleIdx);
       aSet = (FreqItemSet)freqItemSets.get(bodyIdx);

       for (int itemIdx = 0; itemIdx < aSet.numberOfItems; itemIdx++) {
             labelIdx = aSet.items.get(itemIdx);
             aLabel = itemLabels.get(labelIdx).toString();
             if (itemIdx < (aSet.numberOfItems - 1))
               rightRule = rightRule + aLabel + ",";
             else {
               rightRule = rightRule + aLabel;
               data[ruleIdx][2] = rightRule;
             }

       }
       data[ruleIdx][4] = Float.toString((float)ruleTable.getConfidence(ruleIdx)*100);
       data[ruleIdx][3] = Float.toString((float)ruleTable.getSupport(ruleIdx)*100);
       if (displayCount)
        data[ruleIdx][5] = Integer.toString((int)(ruleTable.getSupport(ruleIdx)*numberOfTransactions));
      }
    }

    /**
     * Get row count of the table
     * @return The number of rows in the table
     */

    public int getRowCount()
    {
      return data.length;
    }

    /**
     * Get column count of the table
     * @return The number of columns in the table
     */

    public int getColumnCount()
    {
      return columnNames.length;
    }

    /**
     * Get the name of the column
     * @param col The column index
     * @return The name of the column
     */

    public String getColumnName (int col)
    {
      return columnNames[col];
    }

    /**
     * Get the value of a cell
     * @param row The row index
     * @param col The column index
     * @return The object in a cell
     */

    public Object getValueAt(int row, int col)
    {
      return data[row][col];
    }

    /**
     * Is the cell editable?
     * @param row The row index
     * @param col The column index
     * @return true or false
     */

    public boolean isCellEditable (int row, int col)
    {
        return edit;
    }

  } /* end of AbstractTableModel */





}


/**
 * 01-26-04: vered started qa process
 * edited labels of the table this moduel displays (changed S% to be Support%
 * and C% to be Confidence%)
 *
 */
