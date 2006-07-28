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
package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeNode;
import ncsa.d2k.modules.core.prediction.decisiontree.NominalViewableDTNode;

import java.io.Serializable;


/**
 * A DecisionTreeNode for numerical data. These are binary nodes that split on a
 * value of an attribute.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public final class NumericDecisionTreeNode extends DecisionTreeNode
   implements Serializable, NominalViewableDTNode {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -236238202219947848L;

   /** everything less than the split value goes left. */
   static private final int LEFT = 0;

   /** everything greater than the split value goes right. */
   static private final int RIGHT = 1;

   //~ Instance fields *********************************************************

   /** the value used to compare whether to go left or right. */
   private double splitValue;

   //~ Constructors ************************************************************

   /**
    * Create a new NumericDecisionTreeNode.
    *
    * @param lbl the label
    */
   public NumericDecisionTreeNode(String lbl) { super(lbl); }

   /**
    * Create a new NumericDecisionTreeNode.
    *
    * @param lbl  the label
    * @param prnt the parent node
    */
   public NumericDecisionTreeNode(String lbl, DecisionTreeNode prnt) {
      super(lbl, prnt);
   }

   //~ Methods *****************************************************************

   /**
    * Should never be called, because NumericDecisionTreeNodes use a split
    * value.
    *
    * @param val   value
    * @param child new child
    */
   public final void addBranch(String val, DecisionTreeNode child) { }

   /**
    * Add left and right children to this node.
    *
    * @param split      the split value for this node
    * @param leftLabel  the label for the left branch
    * @param left       the left child
    * @param rightLabel the label for the right branch
    * @param right      the right child
    */
   public final void addBranches(double split, String leftLabel,
                                 DecisionTreeNode left, String rightLabel,
                                 DecisionTreeNode right) {

      splitValue = split;
      branchLabels.add(leftLabel);
      children.add(left);
      left.setParent(this);
      branchLabels.add(rightLabel);
      children.add(right);
      right.setParent(this);
   }

   /**
    * Evaluate a record from the data set. If this is a leaf, return the label
    * of this node. Otherwise find the column of the table that represents the
    * attribute that this node evaluates. Get the value of the attribute for the
    * row to test and call evaluate() on the left child if the value is less
    * than our split value, or call evaluate() on the right child if the split
    * value is greater than or equal to the split value.
    *
    * @param  vt  the Table with the data
    * @param  row the row of the table to evaluate
    *
    * @return the result of evaluating the record
    */
   public final Object evaluate(Table vt, int row) {

      if (isLeaf()) {

         if (training) {
            String actualVal =
               vt.getString(row, ((ExampleTable) vt).getOutputFeatures()[0]);

            if (actualVal.equals(label)) {
               incrementOutputTally(label, true);
            } else {
               incrementOutputTally(label, false);
            }
         } else {
            incrementOutputTally(label, false);
         }

         return label;
      }

      // otherwise find our column.  this will be the column
      // whose label is equal to this node's label.
      int colNum = -1;

      for (int i = 0; i < vt.getNumColumns(); i++) {
         String s = vt.getColumnLabel(i);

         if (s.equals(label)) {
            colNum = i;

            break;
         }
      }

      if (colNum < 0) {
         incrementOutputTally(UNKNOWN, false);

         return UNKNOWN;
      }

      // now get the value from the row.
      double d = vt.getDouble(row, colNum);

      // go left if d is less than split value
      if (d < splitValue) {
         DecisionTreeNode dtn = (DecisionTreeNode) children.get(LEFT);

         return dtn.evaluate(vt, row);
      }
      // otherwise go right
      else {
         DecisionTreeNode dtn = (DecisionTreeNode) children.get(RIGHT);

         return dtn.evaluate(vt, row);
      }
   } // end method evaluate

   /**
    * Get the split attribute.
    *
    * @return the split attribute.
    */
   public String getSplitAttribute() { return getLabel(); }

   /**
    * Get the values for each branch of the node.
    *
    * @return the values for each branch of the node
    */
   public double getSplitValue() { return splitValue; }

   /**
    * Get the split values.  Should not be called, this is only for categorical
    *
    * @return split values
    */
   public String[] getSplitValues() { return null; }

   /**
    * Set a specific branch to be child.
    *
    * @param branchNum branch number
    * @param val       branch label
    * @param child     new child
    */
   public void setBranch(int branchNum, String val, DecisionTreeNode child) {
      DecisionTreeNode oldChild = getChild(branchNum);

      children.set(branchNum, child);
      branchLabels.set(branchNum, val);
      child.setParent(this);
   }
} // end class NumericDecisionTreeNode
