package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.transform.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.prediction.regression.continuous.*;
import ncsa.d2k.modules.core.prediction.mean.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
public class DecisionTreeInducerOpt extends FunctionInducerOpt {

   boolean UseMeanNodeModels          = true;
   boolean UseLinearNodeModels        = false;
   boolean UseSimpleBooleanSplit      = false;
   boolean UseMidPointBasedSplit      = false;
   boolean UseMeanBasedSplit          = true;
   boolean UsePopulationBasedSplit    = false;
   boolean SaveNodeExamples           = false;
   int     MinDecompositionPopulation = 20;
   double  MinErrorReduction          = 0.0;

   boolean PrintEvolvingModels = false;

  public String getModuleName() {
    return "DecisionTreeInducerOpt";
  }
  public String getModuleInfo() {
    return "DecisionTreeInducerOpt";
  }


  public void setControlParameters(ParameterPoint parameterPoint) {

    MinDecompositionPopulation  = (int) parameterPoint.getValue(0);
    MinErrorReduction           =       parameterPoint.getValue(1);

    UseSimpleBooleanSplit       = false;
    if (parameterPoint.getValue(2) > 0.5) UseSimpleBooleanSplit   = true;
    UseMidPointBasedSplit       = false;
    if (parameterPoint.getValue(3) > 0.5) UseMidPointBasedSplit   = true;
    UseMeanBasedSplit           = false;
    if (parameterPoint.getValue(4) > 0.5) UseMeanBasedSplit       = true;
    UsePopulationBasedSplit     = false;
    if (parameterPoint.getValue(5) > 0.5) UsePopulationBasedSplit = true;
    SaveNodeExamples            = false;
    if (parameterPoint.getValue(6) > 0.5) SaveNodeExamples        = true;
    UseMeanNodeModels           = false;
    if (parameterPoint.getValue(7) > 0.5) UseMeanNodeModels       = true;
    UseLinearNodeModels         = false;
    if (parameterPoint.getValue(8) > 0.5) UseLinearNodeModels     = true;
  }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {

    //call superclass constructor using example table to initialize the names;

    numInputs   = examples.getNumInputFeatures();
    numOutputs  = examples.getNumOutputFeatures();
    inputNames  = new String[numInputs];
    outputNames = new String[numOutputs];
    for (int i = 0; i < numInputs; i++)
      inputNames[i] = examples.getInputName(i);
    for (int i = 0; i < numOutputs; i++)
      outputNames[i] = examples.getOutputName(i);


    this.errorFunction = errorFunction;
    DecisionTreeNode decisionTree = createDecisionTree(examples);
    decisionTree.examples = null;

    DecisionTreeModel model = new DecisionTreeModel(examples, decisionTree);
    //model.instantiate(decisionTree);

    RootNode = null;
    return (Model) model;
  }


  ModelPrintOptions ModelPrintOptions = new ModelPrintOptions();

  public void calculateOutputMeansMinsMaxs(DecisionTreeNode node) throws Exception {

    ExampleTable examples = node.examples;

    int numExamples = examples.getNumExamples();

    double [] outputSums = new double[numOutputs];
    double [] outputMins = new double[numOutputs];
    double [] outputMaxs = new double[numOutputs];

    for (int f = 0; f < numOutputs; f++) {
      outputMins[f] = Double.MAX_VALUE;
      outputMaxs[f] = Double.MIN_VALUE;
    }

    for (int e = 0; e < numExamples; e++) {
      for (int f = 0; f < numOutputs; f++) {
        double value = examples.getOutputDouble(e, f);
        outputSums[f] += value;
        if (value < outputMins[f])
          outputMins[f] = value;
        if (value > outputMaxs[f])
          outputMaxs[f] = value;
      }
    }

    for (int f = 0; f < numOutputs; f++){
      outputSums[f] /= numExamples;
    }

    node.outputMeans = outputSums;
    node.outputMins  = outputMins;
    node.outputMaxs  = outputMaxs;
  }

  public void createModel(DecisionTreeNode node) throws Exception {

    ExampleTable examples = node.examples;

    if (UseMeanNodeModels) {
      int numExamples = examples.getNumExamples();

      double [] outputSums = new double[numOutputs];

      for (int e = 0; e < numExamples; e++) {
        for (int f = 0; f < numOutputs; f++) {
          outputSums[f] += examples.getOutputDouble(e, f);
        }
      }

      for (int f = 0; f < numOutputs; f++){
        outputSums[f] /= numExamples;
      }

      // instantiate model //

      MeanOutputModel model = null;
      if (RootNode.model == null)
        model = new MeanOutputModel(examples, outputSums);
      else
        model = new MeanOutputModel(RootNode.model.getTrainingSetSize(),
                              RootNode.model.getInputColumnLabels(),
                              RootNode.model.getOutputColumnLabels(),
                              RootNode.model.getInputFeatureTypes(),
                              RootNode.model.getOutputFeatureTypes(),
                              outputSums);

      //model.Instantiate(numInputs, numOutputs, inputNames, outputNames, outputSums);

      node.model = model;
    }
    if (UseLinearNodeModels) {

      StepwiseLinearInducer inducer = new StepwiseLinearInducer();

      inducer.setNumRounds(0);
      inducer.setDirection(0);

      Model model = inducer.generateModel(examples, errorFunction);

      node.model = model;
    }

  }

  ErrorFunction errorFunction = null;
  public void evaluateModel(DecisionTreeNode node) throws Exception {

    ExampleTable examples = node.examples;

    int numExamples = examples.getNumExamples();
    int numOutputs  = examples.getNumOutputFeatures();

    double error = errorFunction.evaluate(node.examples, node.model) * numExamples;

    node.error = error;

    if (_Trace) {
      System.out.println("node.error = " + node.error);
    }
  }

  DecisionTreeNode RootNode;
  int              NodeIndex;
  public DecisionTreeNode createDecisionTree(ExampleTable examples) throws Exception {

    NodeIndex = 0;
    int depth = 0;
    RootNode = new DecisionTreeNode(NodeIndex, depth);
    NodeIndex++;
    RootNode.examples = examples;
    RootNode.numExamples = examples.getNumExamples();
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
      //model.instantiate(numInputs, numOutputs, inputNames, outputNames, RootNode);
      model.print(ModelPrintOptions);
      System.out.println();
    }

    if (_Trace) {
      System.out.println("recursive partitioning");
    }

    recursiveDecomposition(RootNode, depth);

    return RootNode;
  }


  double [][] SortArray;

  public Decomposition [] createDecompositions(DecisionTreeNode node) {
    ExampleTable examples = node.examples;
    int numExamples = examples.getNumExamples();

    int numSplitMethods = 0;

    if (UseSimpleBooleanSplit)   numSplitMethods++;
    if (UseMidPointBasedSplit)   numSplitMethods++;
    if (UseMeanBasedSplit)       numSplitMethods++;
    if (UsePopulationBasedSplit) numSplitMethods++;


    int numDecompositions = numInputs * numSplitMethods;
    int decompositionIndex = 0;

    Decomposition [] decompositions = new Decomposition[numDecompositions];


    for (int inputIndex = 0; inputIndex < numInputs; inputIndex++){

      if (UseSimpleBooleanSplit){
        decompositions[decompositionIndex] = new Decomposition();
        decompositions[decompositionIndex].inputIndex = inputIndex;
        decompositions[decompositionIndex].value      = 0.5;
        decompositionIndex++;
      }

      if (UseMidPointBasedSplit){
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int e = 0; e < numExamples; e++) {
          double value = examples.getInputDouble(e, inputIndex);
          if (value < min)
            min = value;
          if (value > max)
            max = value;
        }

        decompositions[decompositionIndex] = new Decomposition();
        decompositions[decompositionIndex].inputIndex = inputIndex;
        decompositions[decompositionIndex].value      = (max - min) / 2.0;
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
        decompositions[decompositionIndex].value      = sum / numExamples;
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
        decompositions[decompositionIndex].value      = splitValue;
        decompositionIndex++;
      }
    }

    return decompositions;
  }

/*
This method partitions the given example table into two tables based on the given decompostion.
The example table assigned to node1 contains the examples that the decomposition function evaluates true for
and the example table assigned to node2 contains the examples that the decomposition function evaluates false for.
*/


  int [] workNode1ExampleIndices;
  int [] workNode2ExampleIndices;

  public void partitionExamples (ExampleTable examples,
                                 DecisionTreeNode parrentNode,
                                 DecisionTreeNode node1,
                                 DecisionTreeNode node2,
                                 Decomposition decomposition) throws Exception {

    if (workNode1ExampleIndices == null || workNode1ExampleIndices.length < examples.getNumExamples()) {
      workNode1ExampleIndices = new int[examples.getNumExamples()];
      workNode2ExampleIndices = new int[examples.getNumExamples()];
    }

    int numExamples = examples.getNumExamples();
    int numNode1Examples = 0;
    int numNode2Examples = 0;
    for (int e = 0; e < numExamples; e++) {

      if (decomposition.evaluate(examples, e)) {
        workNode1ExampleIndices[numNode1Examples++] = e;
      }
      else {
        workNode2ExampleIndices[numNode2Examples++] = e;
      }
    }

    int [] node1ExampleIndicies = new int[numNode1Examples];
    int [] node2ExampleIndicies = new int[numNode2Examples];

    for (int i = 0; i < numNode1Examples; i++) {
      node1ExampleIndicies[i] = workNode1ExampleIndices[i];
    }
    for (int i = 0; i < numNode2Examples; i++) {
      node2ExampleIndicies[i] = workNode2ExampleIndices[i];
    }



//!!! should I just use the train set method for example set subseting or both? what about n-way subsetting?
//luckily, for now I can assume that at least the train test set are independent;

    //ExampleTable node1ExampleSet = (ExampleTable) examples.copy();
    //node1ExampleSet.setTestingSet(node1ExampleIndicies);
    node1.examples = (ExampleTable) examples.getSubsetByReference(node1ExampleIndicies);

    //ExampleTable node2ExampleSet = (ExampleTable) examples.copy();
    //node2ExampleSet.setTestingSet(node2ExampleIndicies);
    node2.examples = (ExampleTable) examples.getSubsetByReference(node2ExampleIndicies);

    //System.out.println("node1.examples.numExamples() = " + node1.examples.getNumExamples());
    //System.out.println("node2.examples.numExamples() = " + node2.examples.getNumExamples());

    node1.numExamples = node1.examples.getNumExamples();
    node2.numExamples = node2.examples.getNumExamples();

    node1.root = parrentNode.root;
    node2.root = parrentNode.root;

  }


  public DecisionTreeNode recursiveDecomposition(DecisionTreeNode node, int depth) throws Exception {

    if (node.examples.getNumExamples() < MinDecompositionPopulation)
      return node;

    double parentError = node.error;


    if (_Trace){
    System.out.println("creating decompositions");}

    Decomposition [] decompositions = createDecompositions(node);
    int numDecompositions = decompositions.length;

    //!!!
    //ExampleTable examples = (ExampleTable) node.examples.copy();
    ExampleTable examples = node.examples;
    int    numExamples = examples.getNumExamples();



    double bestErrorReduction = Double.NEGATIVE_INFINITY;
    Decomposition bestDecomposition = null;

    DecisionTreeNode     childNode1 = new DecisionTreeNode();
    //childNode1.examples = examples.shallowCopy();
    //childNode1.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode     childNode2 = new DecisionTreeNode();
    //childNode2.examples = examples.shallowCopy();
    //childNode2.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode bestChildNode1 = new DecisionTreeNode();
    //bestChildNode1.examples = examples.shallowCopy();
    //bestChildNode1.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode bestChildNode2 = new DecisionTreeNode();
    //bestChildNode2.examples = examples.shallowCopy();
    //bestChildNode2.examples.allocateExamplePointers(numExamples);

    if (_Trace) {
      System.out.println("evaluating decompositions");
    }

    for (int decompositionIndex = 0; decompositionIndex < numDecompositions; decompositionIndex++) {

      Decomposition decomposition = decompositions[decompositionIndex];


      // count examples in each child
      int numNode1Examples = 0;
      int numNode2Examples = 0;
      for (int e = 0; e < numExamples; e++) {
        if (decomposition.evaluate(examples, e)) {
          numNode1Examples++;
        }
        else {
          numNode2Examples++;
        }
      }

      if (_Trace) {
        System.out.println("numNode1Examples = " + numNode1Examples);
        System.out.println("numNode2Examples = " + numNode2Examples);
      }

      if ((numNode1Examples < MinDecompositionPopulation) ||
          (numNode2Examples < MinDecompositionPopulation))
        continue;


      // partition examples
      //childNode1.examples.allocateExamplePointers(numNode1Examples);
      //childNode2.examples.allocateExamplePointers(numNode2Examples);
      partitionExamples(examples, node, childNode1, childNode2, decomposition);

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

      if ((errorReduction > bestErrorReduction) &&
          ((numNode1Examples > 0) && (numNode2Examples > 0))) {

        bestErrorReduction = errorReduction;
        bestDecomposition  = decomposition;

        DecisionTreeNode tempNode;

        tempNode = bestChildNode1;  bestChildNode1 = childNode1;  childNode1 = tempNode;
        tempNode = bestChildNode2;  bestChildNode2 = childNode2;  childNode2 = tempNode;
      }

    }

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
        System.out.println("bestDecomposition.inputIndex = " + bestDecomposition.inputIndex);
        System.out.println("bestDecomposition.value      = " + bestDecomposition.value);
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
        //model.instantiate(numInputs, numOutputs, inputNames, outputNames, RootNode);
        model.print(ModelPrintOptions);
        System.out.println();
      }

      recursiveDecomposition(bestChildNode1, depth + 1);
      bestChildNode1 = null;
      recursiveDecomposition(bestChildNode2, depth + 1);
      bestChildNode2 = null;
    }

    return node;
  }


}