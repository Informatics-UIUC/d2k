package ncsa.d2k.modules.core.discovery.ruleassociation;

/**
 * <p>Title: RuleAssocReport </p>
 * <p>Description: Display rule association </p>
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
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;



public class RuleAssocReport extends UIModule
                {
  JOptionPane msgBoard = new JOptionPane();
  File file;
  FileWriter fw;
  ConnectionWrapper cw;
  Connection con;
  String cubeTableName;
  //double minSupport = 0.1;
  //double minConfidence = 0.5;
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
  GenericMatrixModel ruleModel;
  JTable ruleList;
  JButton doneBtn;
  JButton printBtn;

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
        s += "confidence, respectively. The rules displayed in the table can be filtered by different ";
        s += "threshold values for support and confidence, and can be sorted by support or ";
        s += "confidence by the user interface module 'SQLGetRuleAsscFromCube'. </p>";
        s += "<p> Restrictions: ";
        s += "We currently only support Oracle databases.";
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
        return "RuleAssocReport";
  }

  protected String[] getFieldNameMapping () {
        return null;
  }

  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
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
          removeAll();
          ruleTable = (TableImpl)input;
          itemLabels = (ArrayList)((RuleTable)ruleTable).getNamesList();
          freqItemSets = (ArrayList)((RuleTable)ruleTable).getItemSetsList();
          doGUI();
          displayRules();
          this.validate();
          this.repaint();
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
          String[] columnHeading = {"IF","-->","THEN","S %","C %"};
          JOutlinePanel ruleInfo = new JOutlinePanel("Association Rules");
          ruleInfo.setLayout (new GridBagLayout());
          ruleModel = new GenericMatrixModel(ruleTable.getNumRows(),5,false,columnHeading);
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
                0,0,1,1,GridBagConstraints.BOTH, GridBagConstraints.WEST,1,1);

          /* Add the outline panel to displayRulePanel */
          Constrain.setConstraints(displayRulePanel, ruleInfo,
                0,0,4,1,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0);
          Constrain.setConstraints(displayRulePanel, new JPanel(),
                0,1,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,.5,0);
          Constrain.setConstraints(displayRulePanel, printBtn = new JButton ("Print"),
                1,1,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,0,0);
          printBtn.addActionListener(this);
          Constrain.setConstraints(displayRulePanel, doneBtn = new JButton ("Done"),
                2,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,0,0);
          Constrain.setConstraints(displayRulePanel, new JPanel(),
                3,1,1,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,.5,0);
          doneBtn.addActionListener(this);

          setLayout (new BorderLayout());
          //Constrain.setConstraints(this, displayRulePanel, 0, 0, 1, 1, GridBagConstraints.BOTH,
                //					   GridBagConstraints.CENTER, 1.0, 1.0);
          add(displayRulePanel, BorderLayout.CENTER);
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
        //double minConfidence;
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
          ruleList.setValueAt(Float.toString(ruleTable.getFloat(ruleIdx,2)*100),ruleIdx,4);
          ruleList.setValueAt(Float.toString(ruleTable.getFloat(ruleIdx,3)*100),ruleIdx,3);
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

}
