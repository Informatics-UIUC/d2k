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
package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.decisiontree.ScalarViewableDTNode;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;


/**
 * A node of a decision tree.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeNode implements java.io.Serializable,
                                         ScalarViewableDTNode {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 3986627339577665068L;

   //~ Instance fields *********************************************************

   /** bestErrorReduction. */
   protected double bestErrorReduction;

   /** child 1 */
   protected DecisionTreeNode childNode1 = null;

   /** child 2 */
   protected DecisionTreeNode childNode2 = null;

   /** decomposition. */
   protected Decomposition decomposition;

   /** depth of node. */
   protected int depth;

   /** error of node. */
   protected double error;

   /** examples. */
   protected ExampleTable examples;

   /** index. */
   protected int index;

   /** model */
   protected Model model;

   /** the number of examples. */
   protected int numExamples;

   /** outputMaxs. */
   protected double[] outputMaxs;

   /** outputMeans. */
   protected double[] outputMeans;

   /** outputMins. */
   protected double[] outputMins;

   /** parent. */
   protected DecisionTreeNode parent = null;

   /** root. */
   protected DecisionTreeNode root = null;

   //~ Constructors ************************************************************

   /**
    * Creates a new DecisionTreeNode object.
    */
   DecisionTreeNode() { }

   /**
    * Creates a new DecisionTreeNode object.
    *
    * @param index index
    * @param depth depth of node
    */
   DecisionTreeNode(int index, int depth) {
      this.index = index;
      this.depth = depth;
   }

   //~ Methods *****************************************************************

   /**
    * Get the label of a branch.
    *
    * @param  i the branch to get the label of
    *
    * @return the label of branch i
    */
   public String getBranchLabel(int i) {
      int inputIndex = decomposition.inputIndex;
      String inputName = model.getInputFeatureName(inputIndex);

      double value = decomposition.value;
      int intValue = (int) (value * 100);
      value = intValue / 100.0;

      if (i == 0) {
         return inputName + " < " + value;
      } else if (i == 1) {
         return inputName + " >= " + value;
      } else {
         return "";
      }
   }

   /**
    * Get a child of this node.
    *
    * @param  i The index of the child.
    *
    * @return The ith child of this node, where i=0 returns first child.
    */
   public DecisionTreeNode getChildNode(int i) {

      if (i == 0) {
         return this.childNode1;
      } else if (i == 1) {
         return this.childNode2;
      } else {
         return null;
      }
   }

   /**
    * Get the Decomposition object associated with the node.
    *
    * @return The Decomposition object associated with this node, or null if
    *         this is a leaf node.
    */
   public Decomposition getDecomposition() { return decomposition; }

   /**
    * Get the depth of this node. (Root is 0)
    *
    * @return the depth of this node.
    */
   public int getDepth() { return depth; }

   /**
    * get the error
    *
    * @return the error
    */
   public double getError() { return error; }

   /**
    * Get the error reduction
    *
    * @return error reduction
    */
   public double getErrorReduction() { return bestErrorReduction; }

   /**
    * Get the label of this node.
    *
    * @return the label of this node
    */
   public String getLabel() { return "Node " + index; }

   /**
    * Get the maximum output values.
    *
    * @return maximum output values
    */
   public double[] getMaximumValues() { return root.outputMaxs; }

   /**
    * Get the minimum output values
    *
    * @return minimum output values
    */
   public double[] getMinimumValues() { return root.outputMins; }

   /**
    * Get the Model object associated with the node.
    *
    * @return The Model object associated with this node.
    */
   public Model getModel() { return model; }

   /**
    * Get the number of children of this node.
    *
    * @return the number of children of this node
    */
   public int getNumChildren() {

      if (decomposition == null) {
         return 0;
      } else {
         return 2;
      }
   }

   /**
    * Get the total number of examples that passed through this node.
    *
    * @return the total number of examples that passes through this node
    */
   public int getTotal() { return this.numExamples; }

   /**
    * Get a child of this node.
    *
    * @param  i the index of the child to get
    *
    * @return the ith child of this node
    */
   public ViewableDTNode getViewableChild(int i) {

      if (i == 0) {
         return this.childNode2;
      } else if (i == 1) {
         return this.childNode1;
      } else {
         return null;
      }

   }

   /**
    * Get the parent of this node.
    *
    * @return get the parent of this node.
    */
   public ViewableDTNode getViewableParent() { return this.parent; }
} // end class DecisionTreeNode
