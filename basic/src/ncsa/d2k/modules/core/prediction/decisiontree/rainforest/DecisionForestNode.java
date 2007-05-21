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
package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import ncsa.d2k.modules.core.util.*;


/**
 * A DecisionTree is made up of DecisionForestNodes. This module is created
 * based on DecisionTreeNode written by David Clutter.
 *
 * @author  Dora Cai
 * @version $Revision$, $Date$
 */
public abstract class DecisionForestNode implements ViewableDTNode,
                                                    Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -509618705768569851L;

   /** constant for unknown */
   static protected final String UNKNOWN = "Unknown";

   //~ Instance fields *********************************************************

   /** The labels for the branches for the children. */
   protected ArrayList branchLabels;

   /** The list of children of this node. */
   protected ArrayList children;

   /**
    * The label of this node. If this is a leaf, this is the value of the class
    * that this leaf represents. Otherwise this is the name of the attribute
    * that this node splits on
    */
   protected String label;

   /** number of correct predictions */
   protected int numCorrect;

   /** number of incorrect predictions */
   protected int numIncorrect;

   /** number of training examples */
   protected int numTrainingExamples;

   /** output tallies */
   protected int[] outputTallies;

   /** the output classes */
   protected String[] outputValues;

   /** pointer to parent */
   protected DecisionForestNode parent = null;

   /** true if in training phase */
   protected boolean training;

   //~ Constructors ************************************************************

   /**
    * Create a new DecisionForestNode.
    *
    * @param lbl        label of node
    * @param outputSize number of outputs
    */
   DecisionForestNode(String lbl, int outputSize) {
      children = new ArrayList();
      branchLabels = new ArrayList();
      outputValues = new String[outputSize];
      outputTallies = new int[outputSize];
      training = true;
      numCorrect = 0;
      numIncorrect = 0;
      label = lbl;
   }

   //~ Methods *****************************************************************

   /**
    * Get the number of training examples
    *
    * @return number of training examples
    */
   int getNumTrainingExamples() { return numTrainingExamples; }

   /**
    * Add a branch to this node, given the label of the branch and the child
    * node. For a CategoricalDecisionTreeNode, the label of the branch is the
    * same as the value used to determine the split at this node.
    *
    * @param val   the label of the branch
    * @param child the child node
    */
   public abstract void addBranch(String val, DecisionForestNode child);

   /**
    * Add a branch to this node, given the split value, branch label, and child
    * node.
    *
    * @param split       split value
    * @param branchLabel label of branch
    * @param child       the child node
    */
   public abstract void addBranch(double split, String branchLabel,
                                  DecisionForestNode child);

   /**
    * Add left and right children to this node.
    *
    * @param split      the split value for this node
    * @param leftlabel  the label for the left branch
    * @param left       the left child
    * @param rightlabel the label for the right branch
    * @param right      the right child
    */
   public abstract void addBranches(double split, String leftlabel,
                                    DecisionForestNode left, String rightlabel,
                                    DecisionForestNode right);

   /**
    * set a branch to be child.
    *
    * @param branchNum branch number
    * @param val      branch label
    * @param child     child node
    */
   public abstract void setBranch(int branchNum, String val,
                                  DecisionForestNode child);

   /**
    * Clear the values from this node and its children.
    */
   public void clear() {
      numCorrect = 0;
      numIncorrect = 0;
      numTrainingExamples = 0;

      for (int i = 0; i < children.size(); i++) {
         ((DecisionForestNode) children.get(i)).clear();
      }
   }
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   /**
    * Evaluate a record from the data set. If this is a leaf, return the label
    * of this node. Otherwise find the column of the table that represents the
    * attribute that this node evaluates. Call evaluate() on the appropriate
    * child.
    *
    * @param  node Description of parameter node.
    * @param  vt   the Table with the data
    * @param  row  the row of the table to evaluate
    *
    * @return the class label of the row
    */
   public Object evaluate(DecisionForestNode node, Table vt, int row) {

      if (node.isLeaf()) {
         return node.getLabel();
      } else {

         for (
              int branchIdx = 0;
                 branchIdx < node.getNumChildren();
                 branchIdx++) {
            DecisionForestNode childNode =
               (DecisionForestNode) node.getChild(branchIdx);
            String branchLabel = node.getBranchLabel(branchIdx);
            branchLabel = branchLabel.replace('-', '_');
            branchLabel = branchLabel.toUpperCase();
            branchLabel = squeezeSpace(branchLabel);

            for (int colIdx = 0; colIdx < vt.getNumColumns(); colIdx++) {
               String colLabel = vt.getColumnLabel(colIdx);
               colLabel = colLabel.replace('-', '_');
               colLabel = colLabel.toUpperCase();
               colLabel = squeezeSpace(colLabel);

               if (branchLabel.indexOf(colLabel) >= 0) { // column label match
                                                         // the branch label

                  if (vt.isColumnNumeric(colIdx)) { // numeric column

                     double numVal = vt.getDouble(row, colIdx);

                     if (inRange(branchLabel, numVal)) { // value match the
                                                         // range
                        return evaluate(childNode, vt, row);
                     }
                  } else { // non-numeric column

                     String strVal = vt.getString(row, colIdx);
                     strVal = strVal.toUpperCase();

                     if (branchLabel.indexOf(strVal) >= 0) { // values match
                        return evaluate(childNode, vt, row);
                     }
                  }

                  break;
               }
            } // end for
         } // end for

         // should never get here
         myLogger.fatal("something is wrong in evaluate");

         return node.label;
      } // end if
   } // end method evaluate

   /**
    * Get the label of a branch.
    *
    * @param  i the branch to get the label of
    *
    * @return the label of branch i
    */
   public String getBranchLabel(int i) {
      String branchLabel = (String) branchLabels.get(i);
      int idx = branchLabel.indexOf(".");

      // label may contain table name prefix if more than one tables are used
      if (idx >= 0) {
         return branchLabel.substring(idx + 1, branchLabel.length());
      } else {
         return branchLabel;
      }
   }

   /**
    * Get a child of this node.
    *
    * @param  i the index of the child to get
    *
    * @return the ith child of this node
    */
   public DecisionForestNode getChild(int i) {
      return (DecisionForestNode) children.get(i);
   }

   /**
    * Get the child of this node with the most training examples
    *
    * @return child node with most training examples
    */
   public DecisionForestNode getChildWithMostTrainingExamples() {
      int numTE = Integer.MIN_VALUE;
      DecisionForestNode node = null;

      for (int i = 0; i < getNumChildren(); i++) {

         if (getChild(i).getNumTrainingExamples() >= numTE) {
            node = getChild(i);
            numTE = node.getNumTrainingExamples();
         }
      }

      return node;
   }

   /**
    * Get the depth of this node.
    *
    * @return the depth of this node.
    */
   public int getDepth() {

      if (parent == null) {
         return 0;
      }

      return parent.getDepth() + 1;
   }

   /**
    * Get the label of this node.
    *
    * @return the label of this node
    */
   public String getLabel() {
      int idx = label.indexOf(".");

      if (idx >= 0) { // when using > 1 tables, the column name has a table
                      // name as the prefix
         return label.substring(idx + 1, label.length());
      } else {
         return label;
      }
   }

   /**
    * Get the number of children of this node.
    *
    * @return the number of children of this node
    */
   public int getNumChildren() { return children.size(); }

   /**
    * Number of correct predictions
    *
    * @return number of correct predictions
    */
   public int getNumCorrect() { return numCorrect; }

   /**
    * Number incorrect predictions
    *
    * @return number incorrect predictions
    */
   public int getNumIncorrect() { return numIncorrect; }

   /**
    * Get the count of the number of records with the given output value that
    * passed through this node.
    *
    * @param  outputVal the unique output value to get the tally of
    *
    * @return the count of the number of records with the given output value
    *         that passed through this node
    */
   // public int getOutputTally(String outputVal) throws Exception{
   public int getOutputTally(String outputVal) {
      int index = -1;

      for (int i = 0; i < outputValues.length; i++) {

         if (outputValues[i].equals(outputVal)) {
            index = i;

            break;
         }
      }

      if (index == -1) { // no match for the class label
         return index;
      } else {
         return outputTallies[index];
      }
   }

   /**
    * Get the parent of this node.
    *
    * @return get the parent of this node.
    */
   public DecisionForestNode getParent() { return parent; }


   /**
    * Get the total number of examples that passed through this node.
    *
    * @return the total number of examples that passes through this node
    */
   public int getTotal() {
      int tot = 0;

      for (int i = 0; i < outputTallies.length; i++) {
         tot += outputTallies[i];
      }

      return tot;
   }

   /**
    * Return true if in training phase and building tree
    *
    * @return true if in training phase
    */
   public boolean getTraining() { return training; }

   /**
    * Get a child of this node as a ViewableDTNode.
    *
    * @param  i the index of the child to get
    *
    * @return the ith child of this node
    */
   public ViewableDTNode getViewableChild(int i) {
      return (ViewableDTNode) children.get(i);
   }


    /**
     * Get the parent of this node.
     */
    public ViewableDTNode getViewableParent() {
        return (ViewableDTNode) parent;
    }

   /**
    * check whether numVal is belong to the branch.
    *
    * @param  branchLabel the branch label which contains the attribute name and
    *                     value
    * @param  numVal      the value to test
    *
    * @return true if numVal is belong to the branch, false otherwise
    */
   public boolean inRange(String branchLabel, double numVal) {
      int idx;
      idx = branchLabel.indexOf(">=");

      if (idx >= 0) {
         double tmpValue =
            Double.parseDouble(branchLabel.substring(idx + 2,
                                                     branchLabel.length()));

         if (numVal >= tmpValue) {
            return true;
         } else {
            return false;
         }
      }

      idx = branchLabel.indexOf("<");

      if (idx >= 0) {
         double tmpValue =
            Double.parseDouble(branchLabel.substring(idx + 1,
                                                     branchLabel.length()));

         if (numVal < tmpValue) {
            return true;
         } else {
            return false;
         }
      }

      return false;
   } // end method inRange

   /**
    * Return true if this is a leaf, false otherwise.
    *
    * @return true if this is a leaf, false otherwise
    */
   public boolean isLeaf() { return (children.size() == 0); }

   /**
    * print contents
    */
   public void print() {
      System.out.println("Depth: " + getDepth());
      System.out.print("\tLabel: " + getLabel());

      if (parent != null) {
         System.out.println("\t\tParent: " + parent.getLabel());
      } else {
         System.out.println("");
      }

      for (int i = 0; i < getNumChildren(); i++) {
         System.out.print("\t\tBranch: " + branchLabels.get(i));
         System.out.println("\t\t\tNode: " + getChild(i).getLabel());
      }

      for (int i = 0; i < getNumChildren(); i++) {
         getChild(i).print();
      }
   }

   /**
    * print contents
    *
    * @param  out Writer to print to
    *
    * @throws Exception when something goes wrong
    */
   public void print(Writer out) throws Exception {
      out.write("Depth: " + getDepth() + "\n");
      out.write("\tLabel: " + getLabel() + "\n");

      if (parent != null) {
         out.write("\t\tParent: " + parent.getLabel() + "\n");
      } else {
         out.write("");
      }

      for (int i = 0; i < getNumChildren(); i++) {
         out.write("\t\tBranch: " + branchLabels.get(i) + "\n");
         out.write("\t\t\tNode: " + getChild(i).getLabel() + "\n");
      }

      for (int i = 0; i < getNumChildren(); i++) {
         getChild(i).print(out);
      }
   }

   /**
    * Set the label of this node.
    *
    * @param s the new label
    */
   public void setLabel(String s) {
      int idx = s.indexOf(".");

      if (idx >= 0) { // when using > 1 tables, the column name has a table
                      // name as the prefix
         label = s.substring(idx + 1, s.length());
      } else {
         label = s;
      }
   }

   /**
    * Set the parent of this node.
    *
    * @param p Description of parameter p.
    */
   public void setParent(DecisionForestNode p) { parent = p; }

   /**
    * Set to true if in training phase
    *
    * @param b boolean
    */
   public void setTraining(boolean b) {
      training = b;

      for (int i = 0; i < getNumChildren(); i++) {
         getChild(i).setTraining(b);
      }
   }

   /**
    * Squeeze out spaces from the string value.
    *
    * @param  value The string to edit
    *
    * @return The string after spaces are squeezed out
    */
   public String squeezeSpace(String value) {
      int j;
      String newStr = "";

      for (j = 0; j < value.length(); j++) {

         if (value.charAt(j) != ' ') {
            newStr = newStr + value.charAt(j);
         }
      }

      return (newStr);
   }
} // end class DecisionForestNode
