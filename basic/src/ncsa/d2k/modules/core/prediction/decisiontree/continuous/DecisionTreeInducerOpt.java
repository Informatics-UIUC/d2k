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

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.model.ModelPrintOptions;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.sort.QuickSort;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;
import ncsa.d2k.modules.core.prediction.FunctionInducerOpt;
import ncsa.d2k.modules.core.prediction.mean.continuous.MeanOutputModel;
import ncsa.d2k.modules.core.prediction.regression.continuous
          .StepwiseLinearInducerOpt;


/**
 * <p>
 * Overview:
 * This module builds a decision tree from an example table. </p>
 * <p>Detailed Description:
 * The module implements a decision tree learning algorithm for continuous
 * attributes.  For inducing functions within a node, one of two different
 * learning algorithms can be used.
 * <ul>
 * <li><i>Use the mean averaging for models</i>:  Simple averaging of output
 * values.  </li>
 * <li><i>Use multiple regression for models</i>:  Using the best 1-d linear
 * function using a single input attribute.  </li>
 * </ul>
 * It allows for multiple decomposition strategies to be used simultaneously.
 * <ul>
 * <li><i>Generate splits only at 1/2</i>:  Generate splits only at 0.5.  Works
 * well when inputs are scaled from 0.0 to 1.0.</li>
 * <li><i>Generate mean splits</i>:  Generate splits at the mean input attribute
 * value within the node.  </li>
 * <li><i>Generate midpoint splits</i>:  Generate splits at the midpoint between
 * the min and max input attribute value within the node.  </li>
 * <li><i>Generate median splits</i>:  Generate splits at the median input
 * attribute value within the node which requires n log n sorting.  </li>
 * </ul>
 * Determining whether to split a node is controlled by the following
 * parameters:
 * <i>Minimum examples per leaf</i> and <i>Minimum split error reduction</i>.
 * A split is not considered if it results in a node with less than <i>Minimum
 * examples per leaf</i> examples in it.
 * A split is not considered if the resubstitution based error weighted by
 * population does improve by at least <i>Minimum split error reduction</i>.
 * <p>Restrictions:
 * This module will only classify examples with continuous outputs.  </p>
 * <p>Data Handling: This module does not modify the input data. </p>
 * <p>Scalability: This module can efficiently process a data set that can be
 * stored in memory.  The ultimate limit is how much virtual memory java can
 * access. </p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DecisionTreeInducerOpt extends FunctionInducerOpt {

   //~ Instance fields *********************************************************

   /** the errorFunction. */
   protected ErrorFunction errorFunction = null;

   /** the max tree depth. */
   protected int MaxTreeDepth = 20;

   /** Prevents the generation of splits that result in leaf nodes with
                                 less than the specified number of examples. */
   protected int MinDecompositionPopulation = 20;

    /** The units of this error reduction are relative to the error function
     *  passed to the decision tree inducer.  A split will not occur unless the
     * error is reduced by at least the amount specified
     */
   protected double MinErrorReduction = 0.0;

   /** ModelPrintOptions. */
   protected ModelPrintOptions ModelPrintOptions = new ModelPrintOptions();

   /** NodeIndex. */
   protected int NodeIndex;

   /** Print Evolving Models. */
   protected boolean PrintEvolvingModels = false;

   /** the root */
   protected DecisionTreeNode RootNode;

   /** In order to compute and print statistics of the node you must save the
    * examples at the node which increases the space and time complexity of the
    * algorithm by a linear factor.
    */
   protected boolean SaveNodeExamples = false;

   /** sort array */
   protected double[][] SortArray;

    /** This results in a decision tree with linear functions of the input
     * attributes at the leaves.  UseLinearNodeModels and UseMeanNodeModels are
     * mutually exclusive.
     */
   protected boolean UseLinearNodeModels = false;

    /** The mean of each attribute value is calculated in the given node and
     *  used to generate splits for that node.  One or more split methods
     * (mean, midpoint, and median) can be use simultaneously.
     */
   protected boolean UseMeanBasedSplit = true;

    /** This results in a simple decision tree with constant functions at the
     *  leaves.  UseMeanNodeModels and UseLinearNodeModels are mutually
     * exclusive.
     */
   protected boolean UseMeanNodeModels = true;

    /**The median of each attribute value is calculated in the given node and
     * used to generate splits for that node.  This requires sorting of all the
     * examples in a node and therefore scales at n log n in time complexity.
     * One or more split methods (mean, midpoint, and median) can be use
     * simultaneously.
     */
   protected boolean UseMidPointBasedSplit = false;

    /** This works fine for boolean and continuous values.  If used as the sole
     *  decomposition strategy, it forces the system to only split on a variable
     * once.
     */
   protected boolean UseOneHalfSplit = false;

    /** The median of each attribute value is calculated in the given node and
     *  used to generate splits for that node.  This requires sorting of all the
     *  examples in a node and therefore scales at n log n in time complexity.
     *  One or more split methods (mean, midpoint, and median) can be use
     *  simultaneously.
     */
   protected boolean UsePopulationBasedSplit = false;

   /**
    * This method partitions the given example table into two tables based on
    * the given decompostion. The example table assigned to node1 contains the
    * examples that the decomposition function evaluates true for and the
    * example table assigned to node2 contains the examples that the
    * decomposition function evaluates false for.
    */
   protected int[] workNode1ExampleIndices;

   /**workNode2ExampleIndices. */
   protected int[] workNode2ExampleIndices;

   //~ Methods *****************************************************************

   /**
    * calculate the mean, min, max of the outputs.
    *
    * @param  node node
    *
    * @throws Exception when something goes wrong
    */
   public void calculateOutputMeansMinsMaxs(DecisionTreeNode node)
      throws Exception {

      ExampleTable examples = node.examples;

      int numExamples = examples.getNumRows();

      double[] outputSums = new double[numOutputs];
      double[] outputMins = new double[numOutputs];
      double[] outputMaxs = new double[numOutputs];

      for (int f = 0; f < numOutputs; f++) {
         outputMins[f] = Double.MAX_VALUE;
         outputMaxs[f] = Double.MIN_VALUE;
      }

      for (int e = 0; e < numExamples; e++) {

         for (int f = 0; f < numOutputs; f++) {
            double value = examples.getOutputDouble(e, f);
            outputSums[f] += value;

            if (value < outputMins[f]) {
               outputMins[f] = value;
            }

            if (value > outputMaxs[f]) {
               outputMaxs[f] = value;
            }
         }
      }

      for (int f = 0; f < numOutputs; f++) {
         outputSums[f] /= numExamples;
      }

      node.outputMeans = outputSums;
      node.outputMins = outputMins;
      node.outputMaxs = outputMaxs;
   } // end method calculateOutputMeansMinsMaxs

   /**
    * Create the decision tree
    *
    * @param  examples table of examples
    *
    * @return root of decision tree
    *
    * @throws Exception when something goes wrong
    */
   public DecisionTreeNode createDecisionTree(ExampleTable examples)
      throws Exception {

      NodeIndex = 0;

      int depth = 0;
      RootNode = new DecisionTreeNode(NodeIndex, depth);
      NodeIndex++;
      RootNode.examples = examples;
      RootNode.numExamples = examples.getNumRows();
      RootNode.root = RootNode;

      calculateOutputMeansMinsMaxs(RootNode);

      if (_Trace) {
         System.out.println("calculating average outputs");
      }

      if (_Trace) {
         System.out.println("creating initial tree");
      }

      createModel(RootNode);

      if (_Trace) {
         System.out.println("evaluating initial tree");
      }

      evaluateModel(RootNode);

      if (PrintEvolvingModels) {
         DecisionTreeModel model = new DecisionTreeModel(examples, RootNode);

         // model.instantiate(numInputs, numOutputs, inputNames, outputNames,
         // RootNode);
         model.print(ModelPrintOptions);
         System.out.println();
      }

      if (_Trace) {
         System.out.println("recursive partitioning");
      }

      recursiveDecomposition(RootNode, depth);

      return RootNode;
   } // end method createDecisionTree

   /**
    * Description of method createDecompositions.
    *
    * @param  node decision tree node
    *
    * @return Description of return value.
    */
   public Decomposition[] createDecompositions(DecisionTreeNode node) {
      ExampleTable examples = node.examples;
      int numExamples = examples.getNumRows();

      int numSplitMethods = 0;

      if (UseOneHalfSplit) {
         numSplitMethods++;
      }

      if (UseMidPointBasedSplit) {
         numSplitMethods++;
      }

      if (UseMeanBasedSplit) {
         numSplitMethods++;
      }

      if (UsePopulationBasedSplit) {
         numSplitMethods++;
      }

      int numDecompositions = numInputs * numSplitMethods;
      int decompositionIndex = 0;

      Decomposition[] decompositions = new Decomposition[numDecompositions];

      for (int inputIndex = 0; inputIndex < numInputs; inputIndex++) {

         if (UseOneHalfSplit) {
            decompositions[decompositionIndex] = new Decomposition();
            decompositions[decompositionIndex].inputIndex = inputIndex;
            decompositions[decompositionIndex].value = 0.5;
            decompositionIndex++;
         }

         if (UseMidPointBasedSplit) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (int e = 0; e < numExamples; e++) {
               double value = examples.getInputDouble(e, inputIndex);

               if (value < min) {
                  min = value;
               }

               if (value > max) {
                  max = value;
               }
            }

            decompositions[decompositionIndex] = new Decomposition();
            decompositions[decompositionIndex].inputIndex = inputIndex;
            decompositions[decompositionIndex].value = (max - min) / 2.0;
            decompositionIndex++;
         }

         if (UseMeanBasedSplit) {
            double sum = 0.0;

            for (int e = 0; e < numExamples; e++) {
               double value = examples.getInputDouble(e, inputIndex);
               sum += value;
            }

            decompositions[decompositionIndex] = new Decomposition();
            decompositions[decompositionIndex].inputIndex = inputIndex;
            decompositions[decompositionIndex].value = sum / numExamples;
            decompositionIndex++;
         }

         if (UsePopulationBasedSplit) {

            if ((SortArray == null) || (SortArray.length != numExamples)) {
               SortArray = new double[numExamples][1];
            }

            for (int e = 0; e < numExamples; e++) {
               double value = examples.getInputDouble(e, inputIndex);

               SortArray[e][0] = value;
            }

            QuickSort.sort(SortArray);

            double splitValue = SortArray[(numExamples - 1) / 2][0];

            decompositions[decompositionIndex] = new Decomposition();
            decompositions[decompositionIndex].inputIndex = inputIndex;
            decompositions[decompositionIndex].value = splitValue;
            decompositionIndex++;
         }
      } // end for

      return decompositions;
   } // end method createDecompositions

   /**
    * Create the model
    *
    * @param  node Root of decision tree
    *
    * @throws Exception when something goes wrong
    */
   public void createModel(DecisionTreeNode node) throws Exception {

      ExampleTable examples = node.examples;

      if (UseMeanNodeModels || (!UseMeanNodeModels && !UseLinearNodeModels)) {
         int numExamples = examples.getNumRows();

         double[] outputSums = new double[numOutputs];

         for (int e = 0; e < numExamples; e++) {

            for (int f = 0; f < numOutputs; f++) {
               outputSums[f] += examples.getOutputDouble(e, f);
            }
         }

         for (int f = 0; f < numOutputs; f++) {
            outputSums[f] /= numExamples;
         }

         // instantiate model //

         MeanOutputModel model = null;

         if (RootNode.model == null) {
            model = new MeanOutputModel(examples, outputSums);
         } else {
            model =
               new MeanOutputModel(RootNode.model.getTrainingSetSize(),
                                   RootNode.model.getInputColumnLabels(),
                                   RootNode.model.getOutputColumnLabels(),
                                   RootNode.model.getInputFeatureTypes(),
                                   RootNode.model.getOutputFeatureTypes(),
                                   outputSums);
         }

         // model.Instantiate(numInputs, numOutputs, inputNames, outputNames,
         // outputSums);

         node.model = model;
      } // end if

      if (UseLinearNodeModels) {

         double[] bias = new double[3];
         String[] biasNames = new String[3];

         int biasIndex = 0;

         biasNames[biasIndex] = "UseStepwise";
         bias[biasIndex] = 1;
         biasIndex++;
         biasNames[biasIndex] = "Direction";
         bias[biasIndex] = 1;
         biasIndex++;
         biasNames[biasIndex] = "NumRounds";
         bias[biasIndex] = 1;
         biasIndex++;

         // ParameterPointImpl parameterPoint = new ParameterPointImpl();
         // parameterPoint.createFromData(biasNames, bias);

         ParameterPoint parameterPoint =
            ParameterPointImpl.getParameterPoint(biasNames, bias);

         StepwiseLinearInducerOpt inducer = new StepwiseLinearInducerOpt();

         inducer.setControlParameters(parameterPoint);

         Model model = inducer.generateModel(examples, errorFunction);

         node.model = model;
      } // end if

   } // end method createModel

   /**
    * evaluate the model
    *
    * @param  node root of decision tree
    *
    * @throws Exception when something goes wrong
    */
   public void evaluateModel(DecisionTreeNode node) throws Exception {

      ExampleTable examples = node.examples;

      int numExamples = examples.getNumRows();

      PredictionTable predictionTable = examples.toPredictionTable();
      node.model.predict(predictionTable);

      double error =
         errorFunction.evaluate(node.examples, predictionTable) * numExamples;

      node.error = error;

      if (_Trace) {
         System.out.println("node.error = " + node.error);
      }
   }

   /**
    * generate a model from a table of examples and an error function
    *
    * @param  examples      table of examples
    * @param  errorFunction error function
    *
    * @return model
    *
    * @throws Exception when something goes wrong
    */
   public Model generateModel(ExampleTable examples,
                              ErrorFunction errorFunction) throws Exception {

      // call superclass constructor using example table to initialize the
      // names;

      numInputs = examples.getNumInputFeatures();
      numOutputs = examples.getNumOutputFeatures();
      inputNames = new String[numInputs];
      outputNames = new String[numOutputs];

      for (int i = 0; i < numInputs; i++) {
         inputNames[i] = examples.getInputName(i);
      }

      for (int i = 0; i < numOutputs; i++) {
         outputNames[i] = examples.getOutputName(i);
      }

      this.errorFunction = errorFunction;

      DecisionTreeNode decisionTree = createDecisionTree(examples);
      decisionTree.examples = null;

      DecisionTreeModel model = new DecisionTreeModel(examples, decisionTree);
      // model.instantiate(decisionTree);

      RootNode = null;

      return (Model) model;
   } // end method generateModel

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {

      String s = "";
      s += "<p>";
      s += "Overview: ";
      s += "This module builds a decision tree from an example table. </p>";
      s += "<p>Detailed Description: ";
      s +=
         "The module implements a decision tree learning algorithm for continuous attributes. ";
      s +=
         "For inducing functions within a node, one of two different learning algorithms can be used. ";
      s += "<ul>";
      s +=
         "<li><i>Use the mean averaging for models</i>:  Simple averaging of output values.  </li>";
      s +=
         "<li><i>Use multiple regression for models</i>:  Using the best 1-d linear function using a single input attribute.  </li>";
      s += "</ul>";

      s +=
         "It allows for multiple decomposition strategies to be used simultaneously. ";

      s += "<ul>";
      s +=
         "<li><i>Generate splits only at 1/2</i>:  Generate splits only at 0.5.  Works well when inputs are scaled from 0.0 to 1.0.</li>";
      s +=
         "<li><i>Generate mean splits</i>:  Generate splits at the mean input attribute value within the node.  </li>";
      s +=
         "<li><i>Generate midpoint splits</i>:  Generate splits at the midpoint between the min and max input attribute value within the node.  </li>";
      s +=
         "<li><i>Generate median splits</i>:  Generate splits at the median input attribute value within the node which requires n log n sorting.  </li> ";
      s += "</ul>";

      s +=
         "Determining whether to split a node is controlled by the following parameters: ";
      s +=
         "<i>Minimum examples per leaf</i> and <i>Minimum split error reduction</i>.  ";
      s +=
         "A split is not considered if it results in a node with less than <i>Minimum examples per leaf</i> examples in it.  ";
      s +=
         "A split is not considered if the resubstitution based error weighted by population ";
      s += "does improve by at least <i>Minimum split error reduction</i>.  ";

      s += "<p>";
      s += "Restrictions: ";
      s += "This module will only classify examples with ";
      s += "continuous outputs.  </p>";

      s += "<p>Data Handling: This module does not modify the input data. </p>";

      s +=
         "<p>Scalability: This module can efficiently process a data set that can be stored in memory.  ";
      s +=
         "The ultimate limit is how much virtual memory java can access. </p> ";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Decision Tree Inducer Optimizable"; }

   /**
    * Description of method getPropertiesDescriptions.
    *
    * @return Description of return value.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      // so that "ordered and _trace" property are invisible
      return new PropertyDescription[0];
   }

   /**
    * partition the examples
    *
    * @param  examples      Description of parameter examples.
    * @param  parrentNode   Description of parameter parrentNode.
    * @param  node1         Description of parameter node1.
    * @param  node2         Description of parameter node2.
    * @param  decomposition Description of parameter decomposition.
    *
    * @throws Exception Description of exception Exception.
    */
   public void partitionExamples(ExampleTable examples,
                                 DecisionTreeNode parrentNode,
                                 DecisionTreeNode node1,
                                 DecisionTreeNode node2,
                                 Decomposition decomposition) throws Exception {

      if (
          workNode1ExampleIndices == null ||
             workNode1ExampleIndices.length < examples.getNumRows()) {
         workNode1ExampleIndices = new int[examples.getNumRows()];
         workNode2ExampleIndices = new int[examples.getNumRows()];
      }

      int numExamples = examples.getNumRows();
      int numNode1Examples = 0;
      int numNode2Examples = 0;

      for (int e = 0; e < numExamples; e++) {

         if (decomposition.evaluate(examples, e)) {
            workNode1ExampleIndices[numNode1Examples++] = e;
         } else {
            workNode2ExampleIndices[numNode2Examples++] = e;
         }
      }

      int[] node1ExampleIndicies = new int[numNode1Examples];
      int[] node2ExampleIndicies = new int[numNode2Examples];

      for (int i = 0; i < numNode1Examples; i++) {
         node1ExampleIndicies[i] = workNode1ExampleIndices[i];
      }

      for (int i = 0; i < numNode2Examples; i++) {
         node2ExampleIndicies[i] = workNode2ExampleIndices[i];
      }

// !!! should I just use the train set method for example set subseting or both?
// what about n-way subsetting? luckily, for now I can assume that at least the
// train test set are independent;

      // ExampleTable node1ExampleSet = (ExampleTable) examples.copy();
      // node1ExampleSet.setTestingSet(node1ExampleIndicies); node1.examples =
      // (ExampleTable) examples.getSubset(node1ExampleIndicies);
      node1.examples = (ExampleTable) examples.getSubset(node1ExampleIndicies);


      // ExampleTable node2ExampleSet = (ExampleTable) examples.copy();
      // node2ExampleSet.setTestingSet(node2ExampleIndicies); node2.examples =
      // (ExampleTable) examples.getSubset(node2ExampleIndicies);
      node2.examples = (ExampleTable) examples.getSubset(node2ExampleIndicies);

      // System.out.println("node1.examples.numExamples() = " +
      // node1.examples.getNumRows());
      // System.out.println("node2.examples.numExamples() = " +
      // node2.examples.getNumRows());

      node1.numExamples = node1.examples.getNumRows();
      node2.numExamples = node2.examples.getNumRows();

      node1.root = parrentNode.root;
      node2.root = parrentNode.root;

   } // end method partitionExamples

   /**
    * recursiveDecomposition.
    *
    * @param  node  Description of parameter node.
    * @param  depth Description of parameter depth.
    *
    * @return Description of return value.
    *
    * @throws Exception Description of exception Exception.
    */
   public DecisionTreeNode recursiveDecomposition(DecisionTreeNode node,
                                                  int depth) throws Exception {

      if (node.examples.getNumRows() < MinDecompositionPopulation) {
         return node;
      }

      if (depth >= MaxTreeDepth) {
         return node;
      }

      double parentError = node.error;

      if (_Trace) {
         System.out.println("creating decompositions");
      }

      Decomposition[] decompositions = createDecompositions(node);
      int numDecompositions = decompositions.length;

      // !!!
      // ExampleTable examples = (ExampleTable) node.examples.copy();
      ExampleTable examples = node.examples;
      int numExamples = examples.getNumRows();

      double bestErrorReduction = Double.NEGATIVE_INFINITY;
      Decomposition bestDecomposition = null;

      DecisionTreeNode childNode1 = new DecisionTreeNode();
      // childNode1.examples = examples.shallowCopy();
      // childNode1.examples.allocateExamplePointers(numExamples);

      DecisionTreeNode childNode2 = new DecisionTreeNode();
      // childNode2.examples = examples.shallowCopy();
      // childNode2.examples.allocateExamplePointers(numExamples);

      DecisionTreeNode bestChildNode1 = new DecisionTreeNode();
      // bestChildNode1.examples = examples.shallowCopy();
      // bestChildNode1.examples.allocateExamplePointers(numExamples);

      DecisionTreeNode bestChildNode2 = new DecisionTreeNode();
      // bestChildNode2.examples = examples.shallowCopy();
      // bestChildNode2.examples.allocateExamplePointers(numExamples);

      if (_Trace) {
         System.out.println("evaluating decompositions");
      }

      for (
           int decompositionIndex = 0;
              decompositionIndex < numDecompositions;
              decompositionIndex++) {

         Decomposition decomposition = decompositions[decompositionIndex];

         // count examples in each child
         int numNode1Examples = 0;
         int numNode2Examples = 0;

         for (int e = 0; e < numExamples; e++) {

            if (decomposition.evaluate(examples, e)) {
               numNode1Examples++;
            } else {
               numNode2Examples++;
            }
         }

         if (_Trace) {
            System.out.println("numNode1Examples = " + numNode1Examples);
            System.out.println("numNode2Examples = " + numNode2Examples);
         }

         if (
             (numNode1Examples < MinDecompositionPopulation) ||
                (numNode2Examples < MinDecompositionPopulation)) {
            continue;
         }

         // partition examples
         // childNode1.examples.allocateExamplePointers(numNode1Examples);
         // childNode2.examples.allocateExamplePointers(numNode2Examples);
         partitionExamples(examples, node, childNode1, childNode2,
                           decomposition);

         createModel(childNode1);
         createModel(childNode2);

         // evaluate error
         evaluateModel(childNode1);
         evaluateModel(childNode2);

         double error1 = childNode1.error;
         double error2 = childNode2.error;

         double errorReduction = parentError - error1 - error2;

         if (_Trace) {
            System.out.println("error1 = " + error1);
            System.out.println("error2 = " + error2);
            System.out.println("errorReduction = " + errorReduction);
         }

         if (
             (errorReduction > bestErrorReduction) &&
                ((numNode1Examples > 0) && (numNode2Examples > 0))) {

            bestErrorReduction = errorReduction;
            bestDecomposition = decomposition;

            DecisionTreeNode tempNode;

            tempNode = bestChildNode1;
            bestChildNode1 = childNode1;
            childNode1 = tempNode;
            tempNode = bestChildNode2;
            bestChildNode2 = childNode2;
            childNode2 = tempNode;
         }

      } // end for

      if (_Trace) {
         System.out.println("bestErrorReduction = " + bestErrorReduction);
      }

      if (!SaveNodeExamples) {
         node.examples = null;
      }

      // !!!
      childNode1 = null;
      childNode2 = null;

      if ((bestErrorReduction > MinErrorReduction * RootNode.numExamples)) {

         if (_Trace) {
            System.out.println("bestDecomposition.inputIndex = " +
                               bestDecomposition.inputIndex);
            System.out.println("bestDecomposition.value      = " +
                               bestDecomposition.value);
         }

         node.decomposition = bestDecomposition;
         node.bestErrorReduction = bestErrorReduction;

         node.childNode1 = bestChildNode1;
         node.childNode1.parent = node;
         node.childNode1.index = NodeIndex++;
         node.childNode1.depth = depth + 1;

         node.childNode2 = bestChildNode2;
         node.childNode2.parent = node;
         node.childNode2.index = NodeIndex++;
         node.childNode2.depth = depth + 1;

         if (PrintEvolvingModels) {
            DecisionTreeModel model = new DecisionTreeModel(examples, RootNode);

            // model.instantiate(numInputs, numOutputs, inputNames,
            // outputNames, RootNode);
            model.print(ModelPrintOptions);
            System.out.println();
         }

         recursiveDecomposition(bestChildNode1, depth + 1);
         bestChildNode1 = null;
         recursiveDecomposition(bestChildNode2, depth + 1);
         bestChildNode2 = null;
      } // end if

      return node;
   } // end method recursiveDecomposition

   /**
    * Set the control parameters that come from a paramter point
    *
    * @param parameterPoint parameter point
    */
   public void setControlParameters(ParameterPoint parameterPoint) {

      int i = 0;
      MinDecompositionPopulation = (int) parameterPoint.getValue(i++);
      MaxTreeDepth = (int) parameterPoint.getValue(i++);
      MinErrorReduction = parameterPoint.getValue(i++);

      UseOneHalfSplit = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UseOneHalfSplit = true;
      }

      UseMidPointBasedSplit = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UseMidPointBasedSplit = true;
      }

      UseMeanBasedSplit = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UseMeanBasedSplit = true;
      }

      UsePopulationBasedSplit = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UsePopulationBasedSplit = true;
      }

      SaveNodeExamples = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         SaveNodeExamples = true;
      }

      UseMeanNodeModels = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UseMeanNodeModels = true;
      }

      UseLinearNodeModels = false;

      if (parameterPoint.getValue(i++) > 0.5) {
         UseLinearNodeModels = true;
      }
   } // end method setControlParameters

} // end class DecisionTreeInducerOpt
