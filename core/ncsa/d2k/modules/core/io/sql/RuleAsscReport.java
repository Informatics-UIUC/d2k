package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: RuleAsscReport </p>
 * <p>Description: Display rule association </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */

import ncsa.d2k.core.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
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

public class RuleAsscReport extends UIModule
        {
  JOptionPane msgBoard = new JOptionPane();
  File file;
  FileWriter fw;
  ConnectionWrapper cw;
  Connection con;
  String cubeTableName;
  double minSupport = 0.1;
  double minConfidence = 0.5;
  int colCnt;
  int totalRow;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "NOAVL";
  // TableImpl object to keep rules. The table has 4 columns: head, body, support and confidence
  TableImpl ruleTable;
  // ArrayList object to keep all item labels in the format "column_name=column_value"
  ArrayList itemLabels;
  // ArrayList object to keep frequent item sets. Each set is an object of FreqItemSet.
  ArrayList freqItemSets;

  String [] columnHeading;
  GenericTableModel ruleModel;
  JTable ruleList;
  JButton doneBtn;
  JButton printBtn;

  public RuleAsscReport() {
  }

  public String getInputInfo (int i) {
		switch (i) {
			case 0: return "A rule table that has 4 columns: Head, Body, Support and Confidence.";
			case 1: return "An array list that contains all rule labels (column name = column value).";
                        case 3: return "An array list that contains all frequent item sets.";
			default: return "No such input";
		}
	}

  public String getOutputInfo (int i) {
		switch (i) {
			default: return "No such output";
		}
	}

  public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Display the association rules in text format.  </body></html>";
	}

  public String[] getOutputTypes () {
		String[] types = {		};
		return types;
	}

  public String[] getInputTypes () {
		//String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl","java.util.ArrayList","java.util.ArrayList"};
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
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
    return new DisplayRuleView();
  }

  public class DisplayRuleView extends JUserInputPane
    implements ActionListener {

    public void setInput(Object input, int index) {
      closeIt();
      removeAll();
      ruleTable = (TableImpl)input;
      itemLabels = (ArrayList)((RuleTable)ruleTable).getNamesList();
      freqItemSets = (ArrayList)((RuleTable)ruleTable).getItemSetsList();
      doGUI();
      displayRules();
    }

    public Dimension getPreferredSize() {
      return new Dimension (500, 450);
    }

    public void initView(ViewModule mod) {
    }

    public void doGUI() {
      // Panel to hold outline panels
      JPanel displayRulePanel = new JPanel();
      displayRulePanel.setLayout (new GridBagLayout());

      // Outline panel for rules
      String[] columnHeading = {"IF","-->","THEN","S","C"};
      JOutlinePanel ruleInfo = new JOutlinePanel("Association Rules");
      ruleInfo.setLayout (new GridBagLayout());
      ruleModel = new GenericTableModel(ruleTable.getNumRows(),5,false,columnHeading);
      ruleList = new JTable(ruleModel);
      TableColumnModel colModel = ruleList.getColumnModel();
      colModel.getColumn(0).setPreferredWidth(200);
      colModel.getColumn(1).setPreferredWidth(25);
      colModel.getColumn(2).setPreferredWidth(150);
      colModel.getColumn(3).setPreferredWidth(50);
      colModel.getColumn(4).setPreferredWidth(50);
      JScrollPane tablePane = new JScrollPane(ruleList);
      ruleList.setPreferredScrollableViewportSize (new Dimension(200,350));
      Constrain.setConstraints(ruleInfo, tablePane,
        0,0,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      /* Add the outline panel to displayRulePanel */
      Constrain.setConstraints(displayRulePanel, ruleInfo,
        0,0,3,10,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(displayRulePanel, printBtn = new JButton ("Print"),
        1,11,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
      printBtn.addActionListener(this);
      Constrain.setConstraints(displayRulePanel, doneBtn = new JButton ("Done"),
        2,11,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      doneBtn.addActionListener(this);

      setLayout (new BorderLayout());
      add(displayRulePanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == printBtn) {
        writeToFile();
      }
      else if (src == doneBtn) {
        closeIt();
      }
    }
  }
  protected void displayRules() {
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
    double minConfidence;
    for (int ruleIdx = 0; ruleIdx < ruleTable.getNumRows(); ruleIdx++) {
      leftRule = NOTHING;
      rightRule = NOTHING;
      // display the head part (left) of the rule
      headIdx = ruleTable.getInt(ruleIdx,0);
      aSet = (FreqItemSet)freqItemSets.get(headIdx);
      for (int itemIdx = 0; itemIdx < aSet.numberOfItems; itemIdx++) {
        labelIdx = aSet.items.get(itemIdx);
        aLabel = itemLabels.get(labelIdx).toString();
        if (itemIdx < (aSet.numberOfItems - 1))
          leftRule = leftRule + aLabel + ",";
        else {
          leftRule = leftRule + aLabel;
          ruleList.setValueAt(leftRule,ruleIdx,0);
        }
      }
      ruleList.setValueAt(" -->",ruleIdx,1);
      bodyIdx = ruleTable.getInt(ruleIdx,1);
      aSet = (FreqItemSet)freqItemSets.get(bodyIdx);
      for (int itemIdx = 0; itemIdx < aSet.numberOfItems; itemIdx++) {
        labelIdx = aSet.items.get(itemIdx);
        aLabel = itemLabels.get(labelIdx).toString();
        if (itemIdx < (aSet.numberOfItems - 1))
          rightRule = rightRule + aLabel + ",";
        else {
          rightRule = rightRule + aLabel;
          ruleList.setValueAt(rightRule,ruleIdx,2);
        }
      }
      ruleList.setValueAt(Float.toString(ruleTable.getFloat(ruleIdx,2)),ruleIdx,4);
      ruleList.setValueAt(Float.toString(ruleTable.getFloat(ruleIdx,3)),ruleIdx,3);
    }
  }

  protected void closeIt() {
    executionManager.moduleDone(this);
  }

  protected void printItemLabels() {
    System.out.println("item label list in RuleAsscReport: ");
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
    try {
      String fileName = "ruleAssocation.out";
      String delimiter = "          ";
      String newLine = "\n";

      fw = new FileWriter(fileName);

      String s = "RULE ASSOCIATION: ";
      fw.write(s, 0, s.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());

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
          fw.write(s, 0, s.length());
        }
        fw.write(newLine.toCharArray(), 0, newLine.length());
      }
      fw.flush();
      fw.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "RuleAsscReport";
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
                        case 2:
                                return "input2";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
