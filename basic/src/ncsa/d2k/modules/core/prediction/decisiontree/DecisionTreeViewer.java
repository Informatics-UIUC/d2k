/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.VisModule;
import ncsa.d2k.modules.core.io.sql.GenericMatrixModel;
import ncsa.d2k.modules.core.prediction.decisiontree.rainforest
          .DecisionForestModel;
   import ncsa.d2k.modules.core.prediction.decisiontree.rainforest
             .DecisionForestNode;
   import ncsa.gui.Constrain;
   import ncsa.gui.JOutlinePanel;

   import javax.swing.*;
   import javax.swing.table.TableColumnModel;

   import java.awt.*;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.io.File;
   import java.io.FileWriter;
   import java.io.IOException;
   import java.text.NumberFormat;
   import java.util.ArrayList;
   import ncsa.d2k.modules.core.util.*;


/**
 * <p>Title:</p>
 *
 * <p>Description: Display the decision tree in the text format</p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company:</p>
 *
 * @author  Dora Cai
 * @version 1.0
 */
public class DecisionTreeViewer extends VisModule {

   //~ Static fields/initializers **********************************************

   /** constant for empty string. */
   static String NOTHING = "";

   /** number format. */
   static private NumberFormat nf;

   static {
      nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(3);
   }

   //~ Instance fields *********************************************************

   /** column headings. */
   protected String[] columnHeading;

   /** file. */
   protected File file;

   /** file writer. */
   protected FileWriter fw;

   /** input column labels. */
   protected String[] inputColumnLabels;

   /** output column labels. */
   protected String[] outputColumnLabels;

   /** unique output values. */
   protected String[] outputs;

   /** list of rules. */
   protected ArrayList rules;

   /** total. */
   protected int total;

   /** treeList. */
   protected JTable treeList;

   /** table model. */
   protected GenericMatrixModel treeTableModel;

   //~ Constructors ************************************************************

   /**
    * Creates a new DecisionTreeViewer object.
    */
   public DecisionTreeViewer() { }

   //~ Methods *****************************************************************

   /**
    * take out aChar from aString.
    *
    * @param  aString string
    * @param  aChar   character to remove
    *
    * @return aString with aChar removed
    */
   private String cutString(String aString, String aChar) {

      // string does not contain the subString
      if (aString.indexOf(aChar) < 0) {
         return aString;
      } else {
         int idx = aString.indexOf(aChar);
         String tmpString =
            aString.substring(0, idx - 1) +
            aString.substring(idx + 1, aString.length() - 1);

         return tmpString;
      }
   }

   /**
    * Get accuracy for a node.
    *
    * @param  aNode      node
    * @param  classLabel class label
    *
    * @return the accuracy
    */
   private double getAccuracy(DecisionForestNode aNode, String classLabel) {
      int correct = aNode.getOutputTally(classLabel);
      int subTotal = aNode.getTotal();

      return (100 * (double) correct / (double) subTotal);
   }

   /**
    * compute the coverage.
    *
    * @param  aNode      node
    * @param  classLabel class label
    *
    * @return the coverage
    */
   private double getCoverage(DecisionForestNode aNode, String classLabel) {
      return (100 * (double) aNode.getTotal() / (double) total);
   }

   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   /**
    * print contents of array list.
    *
    * @param al array list
    */
   private void printArrayList(ArrayList al) {

      if (al.size() == 0) {
    	  myLogger.debug("No value in ArrayList");
      } else {
    	  myLogger.debug("ArrayList is: ");

         for (int i = 0; i < al.size(); i++) {
        	 myLogger.debug(al.get(i));
         }
      }

      System.out.println();
   }

   /**
    * print a rule.
    *
    * @param al array list containing rules
    */
   private void printRule(ArrayList al) {

      if (al.size() == 0) {
    	  myLogger.debug("No value in ArrayList");
      } else {
    	  myLogger.debug("ArrayList is: ");

         for (int i = 0; i < al.size(); i++) {
            rule aRule = (rule) al.get(i);
            myLogger.debug("Left rule : " + aRule.leftRule);
            myLogger.debug("Right rule : " + aRule.rightRule);
            myLogger.debug("Coverage : " + aRule.coverage);
            myLogger.debug("Accuracy : " + aRule.accuracy);
         }
      }

      myLogger.debug("");
   }

   /**
    * test whether the predicate should be pruned.
    *
    * @param  aString one predicate
    * @param  aList   the list of predicates
    *
    * @return ture if the predicate should be pruned
    */
   private boolean pruneIt(String aString, ArrayList aList) {
      int lessAt = -1;
      int equalAt = -1;
      double minVal = Double.NEGATIVE_INFINITY;
      double maxVal = Double.POSITIVE_INFINITY;
      lessAt = aString.indexOf("<");
      equalAt = aString.indexOf("=");

      String labelStr = NOTHING;

      if (lessAt >= 0) {

         // take out the comma from the string
         String tmpString =
            cutString(aString.substring(lessAt + 2, aString.length()), ",");
         minVal = Double.parseDouble(tmpString);
         labelStr = aString.substring(0, lessAt + 1);
      } else if (equalAt >= 0) {

         // take out the comma from the string
         String tmpString =
            cutString(aString.substring(equalAt + 2, aString.length()), ",");
         maxVal = Double.parseDouble(tmpString);
         labelStr = aString.substring(0, equalAt + 1);
      }

      for (int idx = 0; idx < aList.size(); idx++) {

         // verify the predicate containing "<"
         if (lessAt >= 0 && aList.get(idx).toString().indexOf(labelStr) >= 0) {
            String valStr =
               aList.get(idx).toString().substring(lessAt + 2,
                                                   aList.get(idx).toString()
                                                        .length());
            double newVal = Double.parseDouble(cutString(valStr, ","));

            if (newVal < minVal) {
               return true;
            }
         }
         // verify the predicate containing ">="
         else if (
                  equalAt >= 0 &&
                     aList.get(idx).toString().indexOf(labelStr) >= 0) {
            String valStr =
               aList.get(idx).toString().substring(equalAt + 2,
                                                   aList.get(idx).toString()
                                                        .length());
            double newVal = Double.parseDouble(cutString(valStr, ","));

            if (newVal > maxVal) {
               return true;
            }
         }
      } // end for

      return false;
   } // end method pruneIt

   /**
    * remove the redundant predicate. For Example: (age <= 50 and age <= 35) is
    * redundant, should be pruned as age <= 35
    *
    * @param  leftRule a list of predicates for the left hand side rule
    *
    * @return a concatenated string including all predicates for the left hand
    *         side rule
    */
   private String prunePredicate(ArrayList leftRule) {

      // printArrayList(leftRule);
      String prunedLeftRule = NOTHING;

      for (int idx = 0; idx < leftRule.size(); idx++) {

         // It is a numeric predicate, check redundancy and prune it if
         // necessary
         if (
             leftRule.get(idx).toString().indexOf("<") >= 0 ||
                leftRule.get(idx).toString().indexOf(">=") >= 0) {

            if (pruneIt(leftRule.get(idx).toString(), leftRule)) {
               continue;
            } else {
               prunedLeftRule =
                  concatString(prunedLeftRule, leftRule.get(idx).toString());
            }
         } else {
            prunedLeftRule =
               concatString(prunedLeftRule, leftRule.get(idx).toString());
         }
      }

      return prunedLeftRule;
   } // end method prunePredicate

   /**
    * Concatenate two strings.
    *
    * @param  str1 first string
    * @param  str2 second string
    *
    * @return concatenated string
    */
   protected String concatString(String str1, String str2) {
      String newStr = NOTHING;

      if (!str1.equals(NOTHING)) {
         newStr = str1 + ", ";
      }

      newStr = newStr + str2;

      return (newStr);
   }

   /**
    * generate a new ArrayList which is the exact copy of the original one.
    *
    * @param  origArray original ArrayList
    *
    * @return a new ArrayList
    */
   protected ArrayList copyArrayList(ArrayList origArray) {
      ArrayList newArray = new ArrayList();

      for (int i = 0; i < origArray.size(); i++) {
         newArray.add(origArray.get(i));
      }

      return (newArray);
   }

   /**
    * Create the UserView object for this module-view combination.
    *
    * @return The UserView associated with this module.
    */
   protected UserView createUserView() { return new DisplayTreeView(); }

   /**
    * Add the rules to treeList.
    */
   protected void displayRules() {

      // layout of ruleList is: column 1: left handside rule (if rule),
      // column 2: symbol "-->",
      // column 3: right handside rule (then rule),
      // column 4: correct,
      // column 5: incorrect.
      // printRule(rules);
      for (int ruleIdx = 0; ruleIdx < rules.size(); ruleIdx++) {
         rule aRule = (rule) rules.get(ruleIdx);
         treeList.setValueAt(aRule.leftRule, ruleIdx, 0);
         treeList.setValueAt(" -->", ruleIdx, 1);
         treeList.setValueAt(aRule.rightRule, ruleIdx, 2);
         treeList.setValueAt(nf.format(aRule.coverage), ruleIdx, 3);
         treeList.setValueAt(nf.format(aRule.accuracy), ruleIdx, 4);
      }
   }

   /**
    * extract rules from the decision tree model.
    *
    * @param currLeftRule one path of the tree we have searched so far
    * @param aNode        next node we are going to exam
    */
   protected void extractRules(ArrayList currLeftRule,
                               DecisionForestNode aNode) {
      ArrayList newLeftRule;

      // it is not a leaf node
      if (aNode.getNumChildren() > 0) {

         for (
              int branchIdx = 0;
                 branchIdx < aNode.getNumChildren();
                 branchIdx++) {
            newLeftRule = new ArrayList();

            if (currLeftRule.size() > 0) {
               newLeftRule = copyArrayList(currLeftRule);
               newLeftRule.add(aNode.getBranchLabel(branchIdx));
            } else {
               newLeftRule.add(aNode.getBranchLabel(branchIdx));
            }

            DecisionForestNode newNode = aNode.getChild(branchIdx);
            extractRules(newLeftRule, newNode);
         }
      } else { // it is a leaf node

         String rightRule = outputColumnLabels[0] + " = " + aNode.getLabel();
         double coverage = getCoverage(aNode, aNode.getLabel());
         double accuracy = getAccuracy(aNode, aNode.getLabel());

         // remove the redundant predicate. For Example: (age <= 50 and age <=
         // 35) is redundant, should be pruned as age <= 35.
         String prunedLeftRule = prunePredicate(currLeftRule);

         rule aRule = new rule();
         aRule.leftRule = prunedLeftRule;
         aRule.rightRule = rightRule;
         aRule.coverage = coverage;
         aRule.accuracy = accuracy;
         rules.add(aRule);
         currLeftRule = null;
      }
   } // end method extractRules


   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   protected String[] getFieldNameMapping() { return null; }

   /**
    * write rules to a file.
    */
   protected void writeToFile() {
      JFileChooser chooser = new JFileChooser();
 //     String delimiter = "\t";
      String newLine = "\n";
      String fileName;
      int retVal = chooser.showSaveDialog(null);

      if (retVal == JFileChooser.APPROVE_OPTION) {
         fileName = chooser.getSelectedFile().getAbsolutePath();
      } else {
         return;
      }

      String s1 = NOTHING;
      String s2 = NOTHING;
      String s3 = NOTHING;

      try {
         fw = new FileWriter(fileName);

         String s = "DECISION TREE: ";
         fw.write(s, 0, s.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());

         s = "Data Set Size: " + total;
         fw.write(s, 0, s.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());

         s = NOTHING;

         for (int colIdx = 0; colIdx < inputColumnLabels.length; colIdx++) {

            if (colIdx == 0) {
               s = s + inputColumnLabels[colIdx];
            } else {
               s = s + ", " + inputColumnLabels[colIdx];
            }
         }

         s = "Input Features: " + s;
         fw.write(s, 0, s.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());

         s = outputColumnLabels[0];
         s = "Output Class: " + s;
         fw.write(s, 0, s.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());
         fw.write(newLine.toCharArray(), 0, newLine.length());

         // write the actual data
         for (int rowIdx = 0; rowIdx < treeList.getRowCount(); rowIdx++) {
            s1 = NOTHING;
            s2 = NOTHING;
            s3 = NOTHING;

            for (int colIdx = 0; colIdx < treeList.getColumnCount(); colIdx++) {

               if (colIdx == 0) {
                  s1 = "IF (";
                  s1 =
                     s1 + treeList.getValueAt(rowIdx, colIdx).toString() + ") ";
               } else if (colIdx == 2) {
                  s2 = "    THEN (";
                  s2 =
                     s2 + treeList.getValueAt(rowIdx, colIdx).toString() + ") ";
               } else if (colIdx == 3) {
                  s3 = "        with Coverage: ";
                  s3 = s3 + treeList.getValueAt(rowIdx, colIdx).toString();
                  s3 = s3 + "%";
               } else if (colIdx == 4) {
                  s3 = s3 + " and Accuracy: ";
                  s3 = s3 + treeList.getValueAt(rowIdx, colIdx).toString();
                  s3 = s3 + "%";
               }
            }

            fw.write(s1, 0, s1.length());
            fw.write(newLine.toCharArray(), 0, newLine.length());
            fw.write(s2, 0, s2.length());
            fw.write(newLine.toCharArray(), 0, newLine.length());
            fw.write(s3, 0, s3.length());
            fw.write(newLine.toCharArray(), 0, newLine.length());
         } // end for

         fw.flush();
         fw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   } // end method writeToFile


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "Decision Tree Model.";

         default:
            return "No such input";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Decision Tree Model";

         default:
            return "NO SUCH INPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types =
      { "ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p> Overview: ";
      s += "This module displays a decision tree in a tablet form. </p> ";
      s += "<p> Detailed Description: ";
      s +=
         "This module takes a decision tree model, and display the rules in text ";
      s +=
         "using a tablet form. Each rule represents a branch in a tree. The nodes ";
      s +=
         "in the branch form the IF part of the rule, and the leaf of the branch ";
      s += "forms the THEN part of the rule.";
      s +=
         "<p>The rules displayed can be saved into a file. The <i>File</i> pull-down ";
      s += "menu offers a <i>Save</i> option to do this. ";
      s += "A file browser window pops up, allowing the user to select ";
      s += "where the rules should be saved. ";

      return s;

   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Decision Tree Viewer"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         default:
            return "NO SUCH OUTPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = {};

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

   //~ Inner Classes ***********************************************************

   /**
    * class for each rule extracted from the tree.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   protected class rule {
      double accuracy;
      double coverage;
      String leftRule;
      String rightRule;
   }

   /**
    * This is the user view.
    *
    * @author  $Author$
    * @version $Revision$, $Date$
    */
   public class DisplayTreeView extends ncsa.d2k.userviews.swing.JUserPane
      implements ActionListener {

      /** menu bar. */
      JMenuBar menuBar;

      /** print menu item. */
      JMenuItem print;

      /**
       * Action listener.
       *
       * @param e Description of parameter $param.name$.
       */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if (src == print) {
            writeToFile();
         }
      }

      /**
       * layout the UI.
       */
      public void doGUI() {

         // Panel to hold outline panels
         JPanel displayTreePanel = new JPanel();
         displayTreePanel.setLayout(new GridBagLayout());

         // Outline panel for rules
         JOutlinePanel treeInfo = new JOutlinePanel("Decision Tree");
         treeInfo.setLayout(new GridBagLayout());
         Constrain.setConstraints(treeInfo, new JLabel("Data Set Size: "),
                                  0, 0, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(treeInfo,
                                  new JTextField(Integer.toString(total)),
                                  1, 0, 3, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 2, 1);

         String labelString = NOTHING;

         for (int colIdx = 0; colIdx < inputColumnLabels.length; colIdx++) {

            if (colIdx == 0) {
               labelString = labelString + inputColumnLabels[colIdx];
            } else {
               labelString = labelString + ", " + inputColumnLabels[colIdx];
            }
         }

         Constrain.setConstraints(treeInfo, new JLabel("Input Features: "),
                                  0, 1, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);

         JTextArea inputList = new JTextArea(5, 1);
         inputList.setLineWrap(true);
         inputList.setAutoscrolls(true);
         inputList.setText(labelString);
         inputList.setEditable(false);
         inputList.setBackground(Color.white);

         JScrollPane textPane = new JScrollPane();
         textPane.setAutoscrolls(true);
         textPane.getViewport().add(inputList);
         textPane.setBounds(0, 0, 5, 1);
         Constrain.setConstraints(treeInfo, textPane,
                                  1, 1, 3, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 2, 1);

         labelString = outputColumnLabels[0];
         Constrain.setConstraints(treeInfo, new JLabel("Output Class: "),
                                  0, 2, 1, 1, GridBagConstraints.NONE,
                                  GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(treeInfo, new JTextField(labelString),
                                  1, 2, 3, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 2, 1);

         String[] columnHeading =
         { "IF", "-->", "THEN", "COVERAGE%", "ACCURACY%" };
         treeTableModel =
            new GenericMatrixModel(rules.size(), 5, false, columnHeading);

         TableSorter sorter = new TableSorter(treeTableModel);
         treeList = new JTable(sorter);
         sorter.addMouseListenerToHeaderInTable(treeList);

         TableColumnModel colModel = treeList.getColumnModel();
         colModel.getColumn(0).setPreferredWidth(90);
         colModel.getColumn(1).setPreferredWidth(10);
         colModel.getColumn(2).setPreferredWidth(50);
         colModel.getColumn(3).setPreferredWidth(25);
         colModel.getColumn(4).setPreferredWidth(25);

         colModel.getColumn(0).setHeaderValue("IF");
         colModel.getColumn(1).setHeaderValue("-->");
         colModel.getColumn(2).setHeaderValue("THEN");
         colModel.getColumn(3).setHeaderValue("COVERAGE%");
         colModel.getColumn(4).setHeaderValue("ACCURACY%");


         JScrollPane tablePane = new JScrollPane(treeList);
         treeList.setPreferredScrollableViewportSize(new Dimension(500, 300));
         Constrain.setConstraints(treeInfo, tablePane,
                                  0, 4, 4, 15, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);

         /* Add the outline panel to displayTreePanel */
         Constrain.setConstraints(displayTreePanel, treeInfo,
                                  0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.WEST, 1, 1);

         // 0,0,3,8,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
         setLayout(new BorderLayout());
         add(displayTreePanel, BorderLayout.SOUTH);
      } // end method doGUI

      /**
       * Get the menu bar.
       *
       * @return menu bar
       */
      public Object getMenu() { return menuBar; }


      /**
       * Called by the D2K Infrastructure to allow the view to perform
       * initialization tasks.
       *
       * @param mod The module this view is associated with.
       */
      public void initView(ViewModule mod) {
         menuBar = new JMenuBar();

         JMenu fileMenu = new JMenu("File");
         print = new JMenuItem("Save...");
         print.addActionListener(this);
         fileMenu.add(print);
         menuBar.add(fileMenu);
      }

      /**
       * Called to pass the inputs received by the module to the view.
       *
       * @param input The object that has been input.
       * @param index The index of the module input that been received.
       */
      public void setInput(Object input, int index) {
         ViewableDTModel inputModel = (ViewableDTModel) input;

         DecisionForestModel model = (DecisionForestModel) inputModel;
         DecisionForestNode root = model.getRoot();
         total = root.getTotal();
         outputColumnLabels = model.getOutputColumnLabels();
         inputColumnLabels = model.getInputColumnLabels();
         outputs = model.getUniqueOutputValues();

         rules = new ArrayList();

         ArrayList leftRule = new ArrayList();
         extractRules(leftRule, root);

         // printRule(rules);
         doGUI();
         displayRules();
      }
      /*
       * public Dimension getPreferredSize() { return new Dimension (600, 500);}
       */
   } // end class DisplayTreeView

} // end class DecisionTreeViewer
