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
import ncsa.d2k.modules.core.prediction.decisiontree.NominalViewableDTNode;

import java.io.Serializable;


/**
 * A DecisionForestNode for numerical data. These are binary nodes that split on
 * a value of an attribute.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class NumericDecisionForestNode extends DecisionForestNode
   implements Serializable, NominalViewableDTNode {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -5794968185284562372L;

   /** everything less than the split value goes left. */
   static private final int LEFT = 0;

   /** everything greater than the split value goes right. */
   static private final int RIGHT = 1;

   //~ Instance fields *********************************************************

   /** the value used to compare whether to go left or right. */
   private double splitValue;

   //~ Constructors ************************************************************

   /**
    * Create a new NumericDecisionForestNode.
    *
    * @param lbl        the label
    * @param outputSize output size
    */
   public NumericDecisionForestNode(String lbl, int outputSize) {
      super(lbl, outputSize);
   }

   //~ Methods *****************************************************************


    /**
     * Add a branch to this node, given the label of the branch and the child
     * node. For a CategoricalDecisionTreeNode, the label of the branch is the
     * same as the value used to determine the split at this node.
     *
     * @param val   the label of the branch
     * @param child the child node
     */
    public void addBranch(String val, DecisionForestNode child) {
    }

   /**
    * If the node has only the left or right branch, use this method.
    *
    * @param split       Description of parameter split.
    * @param branchLabel Description of parameter branchLabel.
    * @param child       Description of parameter child.
    */
   public void addBranch(double split, String branchLabel,
                               DecisionForestNode child) {
      splitValue = split;
      branchLabels.add(branchLabel);
      children.add(child);
      child.setParent(this);
   }

   /**
    * Add left and right children to this node.
    *
    * @param split      the split value for this node
    * @param leftLabel  the label for the left branch
    * @param left       the left child
    * @param rightLabel the label for the right branch
    * @param right      the right child
    */
   public void addBranches(double split, String leftLabel,
                                 DecisionForestNode left, String rightLabel,
                                 DecisionForestNode right) {

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
   public Object evaluate(Table vt, int row) { return null; }

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
   public void setBranch(int branchNum, String val, DecisionForestNode child) {
      //DecisionForestNode oldChild = getChild(branchNum);

      children.set(branchNum, child);
      branchLabels.set(branchNum, val);
      child.setParent(this);
   }
} // end class NumericDecisionForestNode
