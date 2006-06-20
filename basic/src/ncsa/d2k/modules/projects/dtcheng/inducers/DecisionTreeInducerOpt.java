package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.examples.*;

import java.util.Arrays;

public class DecisionTreeInducerOpt
    extends FunctionInducerOpt {
  
  boolean trace = false;

  boolean UseMeanNodeModels = true;
  boolean UseOneHalfSplit = false;
  boolean UseMidPointBasedSplit = false;
  boolean UseMeanBasedSplit = true;
  boolean UseAllBetweenValueSplits = false;
  boolean UsePopulationBasedSplit = false;
  boolean SaveNodeExamples = false;
  int MinDecompositionPopulation = 20;
  int MaxTreeDepth = 20;
  double MinErrorReduction = 0.0;
  boolean UseLinearNodeModels = false;
  int Direction = 1;
  int NumRounds = 1;
  int HistNumRegions = 20;
  double HistMinValue = 0.0;
  double HistMaxValue = 1.0;

  boolean PrintEvolvingModels = false;

  boolean OutputModelIndices = false;
  boolean ReportOnlyLeafModelIndices = false;
  

  public String getModuleName() {
    return "Decision Tree Inducer Optimizable";
  }

  public String getModuleInfo() {

    String s = "";
    s += "<p>";
    s += "Overview: ";
    s += "This module builds a decision tree from an example table. </p>";
    s += "<p>Detailed Description: ";
    s += "The module implements a decision tree learning algorithm for continuous attributes. ";
    s += "For inducing functions within a node, one of two different learning algorithms can be used. ";
    s += "<ul>";
    s += "<li><i>Use the mean averaging for models</i>:  Simple averaging of output values.  </li>";
    s += "<li><i>Use multiple regression for models</i>:  Using the best 1-d linear function using a single input attribute.  </li>";
    s += "</ul>";

    s += "It allows for multiple decomposition strategies to be used simultaneously. ";

    s += "<ul>";
    s += "<li><i>Generate splits only at 1/2</i>:  Generate splits only at 0.5.  Works well when inputs are scaled from 0.0 to 1.0.</li>";
    s += "<li><i>Generate mean splits</i>:  Generate splits at the mean input attribute value within the node.  </li>";
    s +=
        "<li><i>Generate midpoint splits</i>:  Generate splits at the midpoint between the min and max input attribute value within the node.  </li>";
    s +=
        "<li><i>Generate median splits</i>:  Generate splits at the median input attribute value within the node which requires n log n sorting.  </li> ";
    s += "</ul>";

    s += "Determining whether to split a node is controlled by the following parameters: ";
    s += "<i>Minimum examples per leaf</i> and <i>Minimum split error reduction</i>.  ";
    s += "A split is not considered if it results in a node with less than <i>Minimum examples per leaf</i> examples in it.  ";
    s += "A split is not considered if the resubstitution based error weighted by population ";
    s += "does improve by at least <i>Minimum split error reduction</i>.  ";

    s += "<p>";
    s += "Restrictions: ";
    s += "This module will only classify examples with ";
    s += "continuous outputs.  </p>";

    s += "<p>Data Handling: This module does not modify the input data. </p>";

    s += "<p>Scalability: This module can efficiently process a data set that can be stored in memory.  ";
    s += "The ultimate limit is how much virtual memory java can access. </p> ";

    return s;
  }

  public void setControlParameters(ParameterPoint parameterPoint) {

    int i = 0;
    MinDecompositionPopulation = (int) parameterPoint.getValue(i++);
    MaxTreeDepth = (int) parameterPoint.getValue(i++);
    MinErrorReduction = parameterPoint.getValue(i++);

    UseOneHalfSplit = false;
    if (parameterPoint.getValue(i++) > 0.5) UseOneHalfSplit = true;
    
    UseMidPointBasedSplit = false;
    if (parameterPoint.getValue(i++) > 0.5) UseMidPointBasedSplit = true;
    
    UseAllBetweenValueSplits = false;
    if (parameterPoint.getValue(i++) > 0.5) UseAllBetweenValueSplits = true;
    
    UseMeanBasedSplit = false;
    if (parameterPoint.getValue(i++) > 0.5) UseMeanBasedSplit = true;
    
    UsePopulationBasedSplit = false;
    if (parameterPoint.getValue(i++) > 0.5) UsePopulationBasedSplit = true;
    
    SaveNodeExamples = false;
    if (parameterPoint.getValue(i++) > 0.5) SaveNodeExamples = true;
    
    UseMeanNodeModels = false;
    if (parameterPoint.getValue(i++) > 0.5) UseMeanNodeModels = true;
    
    UseLinearNodeModels = false;
    if (parameterPoint.getValue(i++) > 0.5) UseLinearNodeModels = true;
    
    Direction = (int) parameterPoint.getValue(i++);
    
    NumRounds = (int) parameterPoint.getValue(i++);
    
    HistNumRegions = (int) parameterPoint.getValue(i++);
    
    HistMinValue = (double) parameterPoint.getValue(i++);
    
    HistMaxValue = (double) parameterPoint.getValue(i++);
    
    OutputModelIndices = false;
    if (parameterPoint.getValue(i++) > 0.5) OutputModelIndices = true;
    
    ReportOnlyLeafModelIndices = false;
    if (parameterPoint.getValue(i++) > 0.5) ReportOnlyLeafModelIndices = true;
    
  }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {

    //call superclass constructor using example table to initialize the names;

    numInputs = examples.getNumInputFeatures();
    numOutputs = examples.getNumOutputFeatures();
    inputNames = new String[numInputs];
    outputNames = new String[numOutputs];
    for (int i = 0; i < numInputs; i++)
      inputNames[i] = examples.getInputName(i);
    for (int i = 0; i < numOutputs; i++)
      outputNames[i] = examples.getOutputName(i);

    this.errorFunction = errorFunction;
    DecisionTreeNode decisionTree = createDecisionTree(examples);
    
    if (!SaveNodeExamples) {
      decisionTree.examples = null;
    }


    DecisionTreeModel model = new DecisionTreeModel(examples, decisionTree);
    //model.instantiate(decisionTree);

    
    if (OutputModelIndices) {
      if (trace) {
        System.out.println("creating sub model array");
      }

      int numSubModels = model.countNumSubModels(RootNode, ReportOnlyLeafModelIndices, 0);
      System.out.println("numSubModels = " + numSubModels);
      
      model.maxDepth = model.calculateMaxDepth(RootNode, 0, 0);
      System.out.println("model.maxDepth = " + model.maxDepth);
      
      Model [] subModels = new Model[numSubModels];
      model.subModels = subModels;
      
      model.setSubModels(RootNode, ReportOnlyLeafModelIndices, 0);
      
      model.OutputModelIndices = OutputModelIndices;
      model.ReportOnlyLeafModelIndices = ReportOnlyLeafModelIndices;
      
    }
    
    
    
    RootNode = null;
    return (Model) model;
  }

  ModelPrintOptions ModelPrintOptions = new ModelPrintOptions();

  public void calculateOutputMeansMinsMaxs(DecisionTreeNode node) throws Exception {

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
        if (value < outputMins[f])
          outputMins[f] = value;
        if (value > outputMaxs[f])
          outputMaxs[f] = value;
      }
    }

    for (int f = 0; f < numOutputs; f++) {
      outputSums[f] /= numExamples;
    }

    node.outputMeans = outputSums;
    node.outputMins = outputMins;
    node.outputMaxs = outputMaxs;
  }

  HistogramPDFInducer histogramPDFInducer = new HistogramPDFInducer();
  public void createDecisionTreeModel(DecisionTreeNode node) throws Exception {
    
    ExampleTable examples = node.examples;
    
    if (UseMeanNodeModels || (!UseMeanNodeModels && !UseLinearNodeModels)) {
      
      
      if (this.errorFunction.index != ErrorFunction.likelihoodErrorFunctionIndex) {
        
        Model model = null;
      
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
      } else {
        Model model = null;

        histogramPDFInducer.setNumRegions(HistNumRegions);
        histogramPDFInducer.setMinValue(HistMinValue);
        histogramPDFInducer.setMaxValue(HistMaxValue);
        
        model = histogramPDFInducer.generateModel(examples, errorFunction);

      node.model = model;
      }
    }
    if (UseLinearNodeModels) {

      double[] bias = new double[3];
      String[] biasNames = new String[3];

      int biasIndex = 0;

      biasNames[biasIndex] = "UseStepwise";
      bias[biasIndex] = 1;
      biasIndex++;
      biasNames[biasIndex] = "Direction";
      bias[biasIndex] = Direction;
      biasIndex++;
      biasNames[biasIndex] = "NumRounds";
      bias[biasIndex] = NumRounds;
      biasIndex++;

      //ParameterPointImpl parameterPoint = new ParameterPointImpl();
      //parameterPoint.createFromData(biasNames, bias);

      ParameterPoint parameterPoint = ParameterPointImpl.getParameterPoint(biasNames, bias);

      StepwiseLinearInducerOpt inducer = new StepwiseLinearInducerOpt();

      inducer.setControlParameters(parameterPoint);

      Model model = inducer.generateModel(examples, errorFunction);

      node.model = model;
    }

  }

  ErrorFunction errorFunction = null;
  public void evaluateModel(DecisionTreeNode node) throws Exception {

    ExampleTable examples = node.examples;
    int numExamples = examples.getNumRows();
//
//    PredictionTable predictionTable = examples.toPredictionTable();
//    node.model.predict(predictionTable);
//
//    double error = errorFunction.evaluate(node.examples, predictionTable) * numExamples;
    
    
    
    //PredictionTable predictionTable = examples.toPredictionTable();
    //model.predict(predictionTable);

    double error = node.model.error(examples, errorFunction) * numExamples;
    
    

    node.error = error;

    if (trace) {
      System.out.println("node.error = " + node.error);
    }
  }

  DecisionTreeNode RootNode;
  int NodeIndex;
  public DecisionTreeNode createDecisionTree(ExampleTable examples) throws Exception {

    NodeIndex = 0;
    int depth = 0;
    RootNode = new DecisionTreeNode(NodeIndex, depth);
    NodeIndex++;
    RootNode.examples = examples;
    RootNode.numExamples = examples.getNumRows();
    RootNode.root = RootNode;

    calculateOutputMeansMinsMaxs(RootNode);

    if (trace) {
      System.out.println("calculating average outputs");
    }

    if (trace) {
      System.out.println("creating initial tree");
    }

    createDecisionTreeModel(RootNode);

    if (trace) {
      System.out.println("evaluating initial tree");
    }

   evaluateModel(RootNode);
    
    if (PrintEvolvingModels) {
      DecisionTreeModel model = new DecisionTreeModel(examples, RootNode);
      //model.instantiate(numInputs, numOutputs, inputNames, outputNames, RootNode);
      model.print(ModelPrintOptions, 0);
      System.out.println();
    }

    if (trace) {
      System.out.println("recursive partitioning");
    }

    recursiveDecomposition(RootNode, depth);



    return RootNode;
  }

  
  double[] SortArray;

  public Decomposition[] createDecompositions(DecisionTreeNode node) {
    ExampleTable examples = node.examples;
    int numExamples = examples.getNumRows();

    int numSplitMethods = 0;
    
    if (UseOneHalfSplit)         numSplitMethods++;
    if (UseMidPointBasedSplit)   numSplitMethods++;
    if (UseMeanBasedSplit)       numSplitMethods++;
    if (UsePopulationBasedSplit) numSplitMethods++;

    int numDecompositions = numInputs * numSplitMethods;
    
    
    
    // count number of decompositions caused by UseAllBetweenValueSplits
    if (UseAllBetweenValueSplits) {
      for (int inputIndex = 0; inputIndex < numInputs; inputIndex++) {
        
        if ((SortArray == null) || (SortArray.length != numExamples)) {
          SortArray = new double[numExamples];
        }
        
        for (int e = 0; e < numExamples; e++) {
          double value = examples.getInputDouble(e, inputIndex);     
          SortArray[e] = value;
        }
        
        Arrays.sort(SortArray);
        
        double currentValue = SortArray[0];
        double lastValue    = SortArray[numExamples - 1];
        
        for (int e = 1; e < numExamples; e++) {
          
          double value = SortArray[e];
          
          if (value != currentValue && value != lastValue) {
            numDecompositions++;
            currentValue = value;
          }
          
        }
        
      }
    }


    
    
    
    
    
    
    
    
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
          if (value < min)
            min = value;
          if (value > max)
            max = value;
        }

        decompositions[decompositionIndex] = new Decomposition();
        decompositions[decompositionIndex].inputIndex = inputIndex;
        decompositions[decompositionIndex].value = (min + max) / 2.0;
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

      
      
      
      if (UseAllBetweenValueSplits) {
        if ((SortArray == null) || (SortArray.length != numExamples)) {
          SortArray = new double[numExamples];
        }
        for (int e = 0; e < numExamples; e++) {
          double value = examples.getInputDouble(e, inputIndex);
          
          SortArray[e] = value;
        }
        
        Arrays.sort(SortArray);
                
        double currentValue = SortArray[0];
        double lastValue    = SortArray[numExamples - 1];
        
        for (int e = 1; e < numExamples; e++) {
          
          double value = SortArray[e];
          
          if (value != currentValue && value != lastValue) {
            
            double splitValue = (currentValue + value) / 2;
            
            decompositions[decompositionIndex] = new Decomposition();
            decompositions[decompositionIndex].inputIndex = inputIndex;
            decompositions[decompositionIndex].value = splitValue;
            decompositionIndex++;

            numDecompositions++;
            currentValue = value;
          }
          
        }
        
        
      }
      
      
      
      
      
      if (UsePopulationBasedSplit) {
        if ((SortArray == null) || (SortArray.length != numExamples)) {
          SortArray = new double[numExamples];
        }
        for (int e = 0; e < numExamples; e++) {
          double value = examples.getInputDouble(e, inputIndex);

          SortArray[e] = value;
        }

        Arrays.sort(SortArray);

        double splitValue = SortArray[(numExamples - 1) / 2];

        decompositions[decompositionIndex] = new Decomposition();
        decompositions[decompositionIndex].inputIndex = inputIndex;
        decompositions[decompositionIndex].value = splitValue;
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

  int[] workNode1ExampleIndices;
  int[] workNode2ExampleIndices;

  
  Class continuousDoubleExampleTableClass = (new ContinuousDoubleExampleTable()).getClass();
  Class continuousByteExampleTableClass = (new OldContinuousByteExampleTable()).getClass();
  
  public void partitionExamples(ExampleTable examples,
                                DecisionTreeNode parrentNode,
                                DecisionTreeNode node1,
                                DecisionTreeNode node2,
                                Decomposition decomposition) throws Exception {

    if (workNode1ExampleIndices == null || workNode1ExampleIndices.length < examples.getNumRows()) {
      workNode1ExampleIndices = new int[examples.getNumRows()];
      workNode2ExampleIndices = new int[examples.getNumRows()];
    }

    int numExamples = examples.getNumRows();
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

    int[] node1ExampleIndicies = new int[numNode1Examples];
    int[] node2ExampleIndicies = new int[numNode2Examples];

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
    //node1.examples = (ExampleTable) examples.getSubset(node1ExampleIndicies);
    
        node1.examples = (ExampleTable) ((ContinuousExampleTable) examples).getSubset(node1ExampleIndicies);

//    if (continuousDoubleExampleTableClass.isInstance(examples)) {
//        node1.examples = (CExampleTable) ((ContinuousDoubleExampleTable) examples).getSubset(node1ExampleIndicies);
//    } else
//    if (continuousByteExampleTableClass.isInstance(examples)) {
//        node1.examples = (ExampleTable) ((OldContinuousByteExampleTable) examples).getSubset(node1ExampleIndicies);
//    } else {    
//    }

    //ExampleTable node2ExampleSet = (ExampleTable) examples.copy();
    //node2ExampleSet.setTestingSet(node2ExampleIndicies);
    //node2.examples = (ExampleTable) examples.getSubset(node2ExampleIndicies);

        
    node2.examples = (ExampleTable) ((ContinuousExampleTable) examples).getSubset(node2ExampleIndicies);

//    if (continuousDoubleExampleTableClass.isInstance(examples)) {
//        node2.examples = (ExampleTable) ((ContinuousDoubleExampleTable) examples).getSubset(node2ExampleIndicies);
//    } else
//    if (continuousByteExampleTableClass.isInstance(examples)) {
//        node2.examples = (ExampleTable) ((OldContinuousByteExampleTable) examples).getSubset(node2ExampleIndicies);
//    } else {
//      
//    }
    
    //System.out.println("node1.examples.numExamples() = " + node1.examples.getNumRows());
    //System.out.println("node2.examples.numExamples() = " + node2.examples.getNumRows());

    node1.numExamples = node1.examples.getNumRows();
    node2.numExamples = node2.examples.getNumRows();

    node1.root = parrentNode.root;
    node2.root = parrentNode.root;

  }

  public DecisionTreeNode recursiveDecomposition(DecisionTreeNode node, int depth) throws Exception {

    if (node.examples.getNumRows() < MinDecompositionPopulation)
      return node;

    if (depth >= MaxTreeDepth)
      return node;

    double parentError = node.error;

    if (trace) {
      System.out.println("creating decompositions");
    }

    Decomposition[] decompositions = createDecompositions(node);
    int numDecompositions = decompositions.length;

    //!!!
    //ExampleTable examples = (ExampleTable) node.examples.copy();
    ExampleTable examples = node.examples;
    int numExamples = examples.getNumRows();

    double bestErrorReduction = Double.NEGATIVE_INFINITY;
    Decomposition bestDecomposition = null;

    DecisionTreeNode childNode1 = new DecisionTreeNode();
    //childNode1.examples = examples.shallowCopy();
    //childNode1.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode childNode2 = new DecisionTreeNode();
    //childNode2.examples = examples.shallowCopy();
    //childNode2.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode bestChildNode1 = new DecisionTreeNode();
    //bestChildNode1.examples = examples.shallowCopy();
    //bestChildNode1.examples.allocateExamplePointers(numExamples);

    DecisionTreeNode bestChildNode2 = new DecisionTreeNode();
    //bestChildNode2.examples = examples.shallowCopy();
    //bestChildNode2.examples.allocateExamplePointers(numExamples);

    if (trace) {
      System.out.println("evaluating decompositions");
    }

    for (int decompositionIndex = 0; decompositionIndex < numDecompositions; decompositionIndex++) {

      Decomposition decomposition = decompositions[decompositionIndex];
      
      // count examples in each child
      int numNode1Examples  = 0;
      int numNode2Examples  = 0;
      int numNode1Positives = 0;
      int numNode2Positives = 0;
      
      for (int e = 0; e < numExamples; e++) {
        if (decomposition.evaluate(examples, e)) {
          numNode1Examples++;
          numNode1Positives += examples.getOutputDouble(e, 0);
        } else {
          numNode2Examples++;
          numNode2Positives += examples.getOutputDouble(e, 0);
        }
      }
      
      

      if (trace) {
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

      createDecisionTreeModel(childNode1);
      createDecisionTreeModel(childNode2);

      // evaluate error
      evaluateModel(childNode1);
      evaluateModel(childNode2);

      double error1 = childNode1.error;
      double error2 = childNode2.error;

      double errorReduction = parentError - error1 - error2;

      if (trace) {
        System.out.println("error1 = " + error1);
        System.out.println("error2 = " + error2);
        System.out.println("errorReduction = " + errorReduction);
      }

      if ((errorReduction > bestErrorReduction) &&
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

    }

    if (trace) {
      System.out.println("bestErrorReduction = " + bestErrorReduction);
    }

    if (!SaveNodeExamples) {
      node.examples = null;
    }

    // !!!
    childNode1 = null;
    childNode2 = null;

    if ((bestErrorReduction > MinErrorReduction * RootNode.numExamples)) {
      if (trace) {
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
        model.print(ModelPrintOptions, 0);
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
