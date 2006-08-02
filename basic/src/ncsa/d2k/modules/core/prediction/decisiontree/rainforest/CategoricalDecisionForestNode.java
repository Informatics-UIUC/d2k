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
import java.util.HashMap;


/**
 * A DecisionForestNode for categorical data. These have as many children as
 * there are values of the attribute that this node tests on.
 *
 * @author  David Clutter
 * @version $Revision$, $Date$
 */
public class CategoricalDecisionForestNode extends DecisionForestNode
   implements Serializable, NominalViewableDTNode {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -3601266827987916742L;

   /** constant for = */
   static private final String EQUALS = " = ";

   //~ Instance fields *********************************************************

   /** Maps an output to a specific child. Used in evaluate() */
   private HashMap outputToChildMap;

   //~ Constructors ************************************************************

   /**
    * Create a new node.
    *
    * @param lbl        the label of this node.
    * @param outputSize Description of parameter outputSize.
    */
   public CategoricalDecisionForestNode(String lbl, int outputSize) {
      super(lbl, outputSize);
      outputToChildMap = new HashMap();
   }

   //~ Methods *****************************************************************

   /**
    * Add a branch to this node, given the label of the branch and the child
    * node. For a CategoricalDecisionForestNode, the label of the branch is the
    * same as the value used to determine the split at this node.
    *
    * @param val   the label of the branch
    * @param child the child node
    */
   public void addBranch(String val, DecisionForestNode child) {
      outputToChildMap.put(val, child);
      children.add(child);
      branchLabels.add(val);
      child.setParent(this);
   }

   /**
    * This should never be called, it is only for adding single numeric branch.
    *
    * @param split       Description of parameter split.
    * @param branchLabel Description of parameter branchLabel.
    * @param child       Description of parameter child.
    */
   public void addBranch(double split, String branchLabel,
                               DecisionForestNode child) { }

   /**
    * This should never be called, because CategoricalDecisionForestNodes do not
    * use a split value.
    *
    * @param split      Description of parameter split.
    * @param leftlabel  Description of parameter leftlabel.
    * @param left       Description of parameter left.
    * @param rightlabel Description of parameter rightlabel.
    * @param right      Description of parameter right.
    */
   public void addBranches(double split, String leftlabel,
                                 DecisionForestNode left, String rightlabel,
                                 DecisionForestNode right) { }

   /**
    * Description of method clear.
    */
   public void clear() { super.clear();
   // for(int i = 0; i < childNumTrainingExamples.length; i++)
   // childNumTrainingExamples[i] = 0;
    }

   /**
    * Evaluate a record from the data set. If this is a leaf, return the label
    * of this node. Otherwise find the column of the table that represents the
    * attribute that this node evaluates. Get the value of the attribute for the
    * row to test and call evaluate() on the appropriate child node.
    *
    * @param  vt  the Table with the data
    * @param  row the row of the table to evaluate
    *
    * @return the result of evaluating the record
    */
   public Object evaluate(Table vt, int row) { return null; }

   /**
    * Get the label of a branch.
    *
    * @param  i the branch to get the label of
    *
    * @return the label of branch i
    */
   public String getBranchLabel(int i) {
      StringBuffer sb = new StringBuffer(getLabel());
      sb.append(EQUALS);
      sb.append((String) branchLabels.get(i));

      return sb.toString();
   }

   /**
    * Get the split attribute.
    *
    * @return the split attribute.
    */
   public String getSplitAttribute() { return getLabel(); }

   /**
    * Get the split value
    *
    * @return split value
    */
   public double getSplitValue() { return 0; }

   /**
    * Get the values for each branch of the node.
    *
    * @return the values for each branch of the node
    */
   public String[] getSplitValues() {
      String[] retVal = new String[0];
      retVal = (String[]) branchLabels.toArray(retVal);

      return retVal;
   }

   /**
    * Set the branch to be newChild
    *
    * @param branchNum branch num
    * @param val       branch label
    * @param newChild  child node
    */
   public void setBranch(int branchNum, String val,
                         DecisionForestNode newChild) {
      //DecisionForestNode oldChild = getChild(branchNum);

      outputToChildMap.put(val, newChild);
      children.set(branchNum, newChild);
      branchLabels.set(branchNum, val);
      newChild.setParent(this);
   }
} // end class CategoricalDecisionForestNode
