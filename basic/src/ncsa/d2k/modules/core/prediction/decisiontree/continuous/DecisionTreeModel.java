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
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

import java.text.DecimalFormat;


/**
 * Description of class DecisionTreeModel.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeModel extends Model implements java.io.Serializable,
                                                        ViewableDTModel {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -1588218736593303198L;

   //~ Instance fields *********************************************************

   /** root of the decision tree */
   DecisionTreeNode decisionTree;

   /**  */
   DecimalFormat Format = new DecimalFormat();

   /** PrintOptions. */
   ModelPrintOptions PrintOptions = null;

   //~ Constructors ************************************************************

   /**
    * Creates a new DecisionTreeModel object.
    *
    * @param examples     table of examples
    * @param decisionTree tree root
    */
   public DecisionTreeModel(ExampleTable examples,
                            DecisionTreeNode decisionTree) {
      super(examples);
      this.decisionTree = decisionTree;
   }

   //~ Methods *****************************************************************

   /**
    * public void instantiate(int numInputs, int numOutputs, String []
    * inputNames, String [] outputNames, DecisionTreeNode decisionTree) {
    * this.numInputs = numInputs; this.numOutputs = numOutputs; this.inputNames
    * = inputNames; this.outputNames = outputNames; this.decisionTree =
    * decisionTree; }.


    /*
    * print level number of spaces to System.out
    * @param level number of spaces
    */
   void indent(int level) {

      for (int i = 0; i < level * 2; i++) {
         System.out.print(" ");
      }
   }

   /**
    * evaluate
    *
    * @param  testExampleSet table of examples
    * @param  e              Description of parameter e.
    *
    * @return Description of return value.
    *
    * @throws Exception when something goes wrong
    */
   public double[] evaluate(ExampleTable testExampleSet, int e)
      throws Exception {

      DecisionTreeNode node = decisionTree;

      while (node.decomposition != null) {

         if (node.decomposition.evaluate(testExampleSet, e)) {
            node = node.childNode1;
         } else {
            node = node.childNode2;
         }
      }

      double[] outputs = null;

      try {
         outputs = node.model.evaluate(testExampleSet, e);
      } catch (Exception e2) {
         throw e2;
      }

      return outputs;
   }

   /**
    * Get the root of the tree
    *
    * @return the root of the tree
    */
   public DecisionTreeNode getDecisionTreeRoot() { return decisionTree; }

   /**
    * Get the names of teh inputs
    *
    * @return the names of the inputs
    */
   public String[] getInputs() { return this.getInputFeatureNames(); }

   /**
    * Get the unique input values for the ith attribute
    *
    * @param  i the attribute of interest
    *
    * @return the unique input values for the ith attribute
    */
   public String[] getUniqueInputValues(int i) { return new String[] {
                                                           "0", "1"
                                                        }; }

   /**
    * get the unique output values
    *
    * @return the unique output values
    */
   public String[] getUniqueOutputValues() { return new String[] {
                                                       "0", "1"
                                                    }; }

   /**
    * Get the tree root as a ViewableDTNode
    *
    * @return root of tree that implements ViewableDTNode
    */
   public ViewableDTNode getViewableRoot() { return decisionTree; }

   /**
    * Print the tree
    *
    * @param  printOptions The options for printing
    *
    * @throws Exception when something goes wrong
    */
   public void print(ModelPrintOptions printOptions) throws Exception {
      PrintOptions = printOptions;

      Format.setMaximumFractionDigits(PrintOptions.MaximumFractionDigits);

      System.out.println("Decision Tree:");
      printTree(this.decisionTree, 0);
   }

   /**
    * Print a node
    *
    * @param  node       the node
    * @param  level      Description of parameter level.
    * @param  leafNode   Description of parameter leafNode.
    * @param  splitIndex Description of parameter splitIndex.
    *
    * @throws Exception when something goes wrong
    */
   public void printNodeModel(DecisionTreeNode node, int level,
                              boolean leafNode,
                              int splitIndex) throws Exception {

      indent(level);

      if (leafNode) {
         System.out.print("*");
      }

      node.model.print(PrintOptions);

      // !!!
      if (node.examples != null) {

         ExampleTable examples = node.examples;
         int numExamples = examples.getNumRows();
         double outputValueSum = 0.0;
         double outputValueMin = Double.MAX_VALUE;
         double outputValueMax = Double.MIN_VALUE;

         for (int e = 0; e < numExamples; e++) {
            double outputValue = examples.getOutputDouble(e, 0);

            if (outputValue < outputValueMin) {
               outputValueMin = outputValue;
            }

            if (outputValue > outputValueMax) {
               outputValueMax = outputValue;
            }

            outputValueSum += outputValue;
         }

         double outputValueMean = outputValueSum / numExamples;
         double outputVarianceSum = 0.0;

         for (int e = 0; e < numExamples; e++) {
            double outputValue = examples.getOutputDouble(e, 0);
            double difference = outputValue - outputValueMean;
            double differenceSquared = difference * difference;

            outputVarianceSum += differenceSquared;
         }

         double outputSTD = Math.sqrt(outputVarianceSum / numExamples);

         System.out.print(" (min=" + Format.format(outputValueMin) +
                          " max=" + Format.format(outputValueMax) +
                          " std=" + Format.format(outputSTD) +
                          " d=" + node.depth +

                          // " index=" + node.index +
                          ")" +
                          " [P:" + numExamples +
                          " ERR:" + Format.format(node.error / numExamples));

         if (!leafNode) {
            System.out.print(" ERR_REDUCT:" +
                             Format.format(node.bestErrorReduction /
                                              numExamples) +
                             " FEATURE:" +
                             this.getInputFeatureName(splitIndex));
         }

         System.out.println("]");
      } else {
         System.out.println("  [Pop = " + node.numExamples + "]");
      }
   } // end method printNodeModel

   /**
    * Print the tree
    *
    * @param  node  root of tree
    * @param  level Description of parameter level.
    *
    * @throws Exception when something goes wrong
    */
   public void printTree(DecisionTreeNode node, int level) throws Exception {

      if (node.decomposition != null) {
         int splitIndex = node.decomposition.inputIndex;
         double splitValue = node.decomposition.value;

         if (PrintOptions.PrintInnerNodeModels) {
            printNodeModel(node, level, false, splitIndex);
         }

         String testString = null;

         if (PrintOptions.AsciiInputs) {

            if (PrintOptions.EnumerateSplitValues) {
               int[] byteCounts = new int[256];
               ExampleTable examples = node.examples;
               int numExamples = examples.getNumRows();

               for (int e = 0; e < numExamples; e++) {
                  int byteValue = (int) examples.getInputDouble(e, splitIndex);

                  if (byteValue < splitValue) {
                     byteCounts[byteValue]++;
                  }
               }

               testString = "(" + this.getInputFeatureName(splitIndex) + " = {";

               boolean firstTime = true;

               for (int i = 0; i < 256; i++) {

                  if (byteCounts[i] > 0) {

                     if (!firstTime) {
                        testString += "|";
                     }

                     testString += (char) i;

                     firstTime = false;
                  }
               }

               testString += "})";
            } else {
               testString =
                  "(" + this.getInputFeatureName(splitIndex) + " > " +
                  ((char) splitValue) + ")";
            }
         } else {
            testString =
               "(" + this.getInputFeatureName(splitIndex) + " > " +
               Format.format(splitValue) + ")";
         }

         indent(level);
         System.out.println("If " + testString);

         indent(level + 1);
         System.out.println("Then");
         printTree(node.childNode1, level + 2);
         indent(level + 1);
         System.out.println("Else" + "  // not" + testString);
         printTree(node.childNode2, level + 2);
      } else {
         printNodeModel(node, level, true, -1);
      }

   } // end method printTree

   /**
    * Is the ith input scalar?
    *
    * @param  i input of interest
    *
    * @return true if the ith input is scalar, false otherwise
    */
   public boolean scalarInput(int i) { return true; }

} // end class DecisionTreeModel
