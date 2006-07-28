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

import ncsa.d2k.core.modules.OrderedReentrantModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

import java.util.HashSet;


/**
 * Implements pruning of C4.5 decision trees. The error is estimated at each
 * node using the training data. A confidence level of 25% is used.<br>
 * <br>
 * Each non-leaf node in the tree is examined for pruning. One of two types of
 * pruning can be attempted: subtree replacment or subtree raising. Subtree
 * replacement can occur when all the children of a node are leaves. If the
 * error of the leaves is less than the error at the node, the node will be
 * replaced with a leaf. Subtree raising can occur when the children of a node
 * are not all leaves. The branch with the most training examples is temporarily
 * raised, and if the error induced after the raising is less than the error of
 * the original node, the replacement is left intact.<br>
 * <br>
 * The pruning process can be time-intensive. Each time a possible pruning is
 * tested, the new, pruned tree must be applied to the training dataset to find
 * an estimate of the new error. When a possible pruning is not taken, the tree
 * is reverted to its original form, but the tree must again be applied to the
 * training dataset.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class C45TreePruner
   extends /*ReentrantComputeModule*/ OrderedReentrantModule {

   //~ Instance fields *********************************************************

   /** model that uses the tree. */
   private DecisionTreeModel dtm;

   /** training table. */
   private ExampleTable et;

   /** visited nodes, used in DFS */
   private HashSet gray;

   /** root node of the tree. */
   private DecisionTreeNode rootNode;

   /** unvisited nodes, used in DFS */
   private HashSet white;

   /** Z */
   private double Z = 0.69;

   //~ Methods *****************************************************************

   /**
    * f = E/N N = number of instances z = 0.69.
    *
    * @param  N Description of parameter N.
    * @param  E Description of parameter E.
    * @param  z Description of parameter z.
    *
    * @return Description of return value.
    */
   static private double errorEstimate(double N, double E, double z) {
      double f = E / N;

      double usq = (f / N);
      usq -= (Math.pow(f, 2) / N);
      usq += (Math.pow(z, 2) / (4 * Math.pow(N, 2)));

      double numerator =
         f + (Math.pow(z, 2) / (2 * N)) + (z * Math.pow(usq, .5));
      double denominator = 1 + (Math.pow(z, 2) / N);

      return numerator / denominator;
   }

   /**
    * Return true if the children of rt are all leaves.
    *
    * @param  rt decision tree node
    *
    * @return true if all the children of rt are leaves
    */
   private boolean areAllChildrenLeaves(DecisionTreeNode rt) {

      for (int i = 0; i < rt.getNumChildren(); i++) {

         if (!rt.getChild(i).isLeaf()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Perform a depth-first search, starting at the root.
    *
    * @param  rt the root
    *
    * @throws Exception when something goes wrong
    */
   private void DFS(DecisionTreeNode rt) throws Exception {

      for (int i = 0; i < rt.getNumChildren(); i++) {
         DecisionTreeNode dtn = rt.getChild(i);

         if (white.contains(dtn)) {
            DFSvisit(dtn);
         }
      }

      visit(rt);
   }

   /**
    * Visit a decision tree node.  Part of DFS.
    *
    * @param  rt decision tree node
    *
    * @throws Exception when something goes wrong
    */
   private void DFSvisit(DecisionTreeNode rt) throws Exception {
      white.remove(rt);
      gray.add(rt);

      for (int i = 0; i < rt.getNumChildren(); i++) {
         DecisionTreeNode dtn = rt.getChild(i);

         if (white.contains(dtn)) {
            DFSvisit(dtn);
         }
      }

      visit(rt);
   }

   /**
    * Put node and all its children in white set.  Part of DFS
    *
    * @param rt decision tree node
    */
   private void putAllInWhite(DecisionTreeNode rt) {
      white.add(rt);

      for (int i = 0; i < rt.getNumChildren(); i++) {
         putAllInWhite(rt.getChild(i));
      }
   }

   /**
    * Visit a node. The pruning operations are done here.
    *
    * @param  node the root
    *
    * @throws Exception when something goes wrong
    */
   private void visit(DecisionTreeNode node) throws Exception {
      gray.remove(node);

      // we cannot prune a leaf, only a node
      if (node.isLeaf()) {
         return;
      } else {

         // we will calculate the error estimation for the current node
         double originalNodeErrorEstimate =
            errorEstimate((double) (node.getTotal()),
                          (double) (node.getNumIncorrect()), Z);

         // now we must replace this node by the branch with the most
         // training examples and recalculate the error

         // find the child with the most training examples
         DecisionTreeNode cd = node.getChildWithMostTrainingExamples();

         // now find which branch of our parent that node represents
         DecisionTreeNode parent = node.getParent();

         if (parent != null) {

            // find the index of node
            int idx = -1;
            String lbl = null;

            for (int i = 0; i < parent.getNumChildren(); i++) {
               DecisionTreeNode cld = (DecisionTreeNode) parent.getChild(i);

               if (cld == node) {
                  idx = i;

                  if (parent instanceof CategoricalDecisionTreeNode) {
                     lbl =
                        ((CategoricalDecisionTreeNode) parent).getSplitValues()[i];
                  } else {
                     lbl = parent.getBranchLabel(i);
                  }

                  break;
               }
            }

            // find the index of the child to node
            int cdIdx = -1;
            String cdLabel = null;

            DecisionTreeNode prt = cd.getParent();

            for (int i = 0; i < prt.getNumChildren(); i++) {
               DecisionTreeNode c = prt.getChild(i);

               if (c == cd) {
                  cdIdx = i;

                  if (prt instanceof CategoricalDecisionTreeNode) {
                     cdLabel =
                        ((CategoricalDecisionTreeNode) prt).getSplitValues()[i];
                  } else {
                     cdLabel = prt.getBranchLabel(i);
                  }

                  break;
               }
            }

            // do the replacement, clear, re-do predict.
            parent.setBranch(idx, lbl, cd);

            rootNode.clear();
            dtm = new DecisionTreeModel(rootNode, et);
            dtm.predict(et);

            double replacementNodeErrorEstimate =
               errorEstimate((double) cd.getTotal(),
                             (double) cd.getNumIncorrect(), Z);

            // if the error of the replacement is less than the error of the
            // original, leave the replacement intact.
            if (replacementNodeErrorEstimate <= originalNodeErrorEstimate) {
               ;
            } else {
               parent.setBranch(idx, lbl, node);
               node.setBranch(cdIdx, cdLabel, cd);
               // otherwise revert to the original, unreplaced tree.

               // clear, re-do predict
               rootNode.clear();
               dtm = new DecisionTreeModel(rootNode, et);
               dtm.predict(et);
            }
         }
         // otherwise we are attempting to replace the root node.
         // node == rootNode
         else {
            // keep a temporary reference to the root node

            // perform prediction as if cd is the root
            rootNode.clear();
            dtm = new DecisionTreeModel(cd, et);
            dtm.predict(et);

            double replacementNodeErrorEstimate =
               errorEstimate((double) cd.getTotal(),
                             (double) cd.getNumIncorrect(), Z);

            // if the replacement is less than or equal to the error of the
            // original, leave the replacement intact
            if (replacementNodeErrorEstimate <= originalNodeErrorEstimate) {
               rootNode = cd;
            }

            rootNode.clear();
            dtm = new DecisionTreeModel(rootNode, et);
            dtm.predict(et);
         }
      } // end if
   } // end method visit

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      rootNode = (DecisionTreeNode) pullInput(0);
      et = (ExampleTable) pullInput(1);

      // clear
      rootNode.clear();

      // we need a decsion tree model so we can call the predict() method
      dtm = new DecisionTreeModel(rootNode, et);

      // now call the predict method.  we must predict so that we can
      // get the tallies of the correct and incorrect predictions on the
      // training data
      dtm.predict(et);

      white = new HashSet();
      gray = new HashSet();

      // now put all the nodes in the white category. they haven't been
      // seen yet
      putAllInWhite(rootNode);

      // perform a depth-first search
      DFS(rootNode);

      // clear the tallies of the tree
      rootNode.clear();

      // set training to false.
      rootNode.setTraining(false);

      // push the pruned tree out
      pushOutput(rootNode, 0);

      // pushOutput(et, 1);
   } // end method doit

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    */
   public void endExecution() {
      super.endExecution();
      rootNode = null;
      et = null;
      dtm = null;
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case (0):
            return "The root node of the unpruned decision tree.";

         case (1):
            return "The training data that was used to build the decision tree.";

         default:
            return "";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      switch (i) {

         case (0):
            return "Decision Tree Root";

         case (1):
            return "Example Table";

         default:
            return "";
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
      String[] in =
      {
         "ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeNode",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s =
         "<p>Overview: This module prunes a decision tree built by the C4.5 Tree Builder. " +
         "<p>Detailed Description: This module prunes a decision tree using a reduced-error " +
         "pruning technique.  Error estimates for the leaves and subtrees are " +
         "computed by classifying all the examples of the Example Table. " +
         "Both subtree replacement and subtree raising are used.  Subtree " +
         "replacement will replace a node by one of its leaves if the " +
         "induced error of the replacement is less than the sum of the errors " +
         "for the leaves of the node.  Subtree raising will lift a subtree if " +
         "the error for the raised subtree is less than the original.  The " +
         "complexity of pruning the tree is O(n (log n)<sup>2</sup>)." +
         "<p>References: C4.5: Programs for Machine Learning by J. Ross Quinlan" +
         "<p>Data Type Restrictions: The Unpruned Root must be a DecisionTreeNode " +
         "built by the C4.5 Tree Builder." +
         "<p>Data Handling: This module will attempt to classify the examples " +
         "in the Example Table N times, where N is the number of nodes in the tree." +
         "<p>Scalability: This module will classify the examples in the Example Table " +
         "at least once for each node of the tree.  This module will need " +
         "enough memory to hold those predictions.";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "C4.5 Tree Pruner"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case (0):
            return "A Decision Tree Node which is the root of the pruned tree.";

         default:
            return "";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case (0):
            return "Pruned Decision Tree Root";

         case (1):
            return "Example Table";

         default:
            return "";
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
      String[] out =
      { "ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeNode" };

      return out;
   }

   /**
    * Description of method getPropertiesDescriptions.
    *
    * @return Description of return value.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] retVal = new PropertyDescription[0];

      return retVal;
   }

} // end class C45TreePruner
