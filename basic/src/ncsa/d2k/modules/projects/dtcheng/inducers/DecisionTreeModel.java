package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.table.*;
import java.text.*;

import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;


public class DecisionTreeModel
    extends Model
    implements java.io.Serializable, ViewableDTModel {

  DecisionTreeNode decisionTree;
  public int maxDepth = 0;
  public boolean OutputModelIndices = false;
  public boolean ReportOnlyLeafModelIndices = true;

  public DecisionTreeModel(ExampleTable examples, DecisionTreeNode decisionTree) {
    super(examples);
    this.decisionTree = decisionTree;
  }

  public ViewableDTNode getViewableRoot() {
    //!!!return decisionTree;
    return null;
  }

  public boolean scalarInput(int i) {
    return true;
  }

  public String[] getInputs() {
    return this.getInputFeatureNames();
  }

  public String[] getUniqueInputValues(int i) {
    return new String[] {
        "0", "1"};
  }

  public String[] getUniqueOutputValues() {
    return new String[] {
        "0", "1"};
  }

  public DecisionTreeNode getDecisionTreeRoot() {
    return decisionTree;
  }

  public void evaluate(double [] inputs, double [] outputs) throws Exception {

    int level = 0;
    
    DecisionTreeNode node = decisionTree;

    while (node.decomposition != null) {
      
      if (OutputModelIndices && !ReportOnlyLeafModelIndices) {
        outputs[level] = node.subModelIndex;
      }
      level++;
      
      if (node.decomposition.evaluate(inputs)) {
        node = node.childNode1;
      }
      else {
        node = node.childNode2;
      }
    }

    try {
      if (OutputModelIndices) {
        if (ReportOnlyLeafModelIndices) {
          outputs[0] = node.subModelIndex;
        }
      }
      else {
      node.model.evaluate(inputs, outputs);
      }
    }
      
    catch (Exception e2) {
      throw e2;
    }
  }


  public double[] evaluate(ExampleTable testExampleSet, int e) throws Exception {

    int level = 0;
    double [] path = null;
    
    if (ReportOnlyLeafModelIndices)
      path = new double[1];
    else
      path = new double[maxDepth]; 
    
    DecisionTreeNode node = decisionTree;

    while (node.decomposition != null) {
      
      if (OutputModelIndices && !ReportOnlyLeafModelIndices) {
        path[level] = node.subModelIndex;
      }
      level++;
      
      
      if (node.decomposition.evaluate(testExampleSet, e)) {
        node = node.childNode1;
      }
      else {
        node = node.childNode2;
      }
    }

    

    if (OutputModelIndices) {
      if (ReportOnlyLeafModelIndices) {
        path[0] = node.subModelIndex;
      }
      return path;
    }
    else {
      return node.model.evaluate(testExampleSet, e);
    }
    
  }


  public double evaluateLogLikelihood(ExampleTable testExampleSet, int e) throws Exception {
    int level = 0;
    double [] path = null;
    
    if (ReportOnlyLeafModelIndices)
      path = new double[1];
    else
      path = new double[maxDepth]; 
    
    DecisionTreeNode node = decisionTree;

    while (node.decomposition != null) {
      
      if (OutputModelIndices && !ReportOnlyLeafModelIndices) {
        path[level] = node.subModelIndex;
      }
      level++;
      
      
      if (node.decomposition.evaluate(testExampleSet, e)) {
        node = node.childNode1;
      }
      else {
        node = node.childNode2;
      }
    }

    

//    if (OutputModelIndices) {
//      if (ReportOnlyLeafModelIndices) {
//        path[0] = node.subModelIndex;
//      }
//      return path;
//    }
//    else {
//      return node.model.evaluate(testExampleSet, e);
//    }
    
    return node.model.evaluateLogLikelihood(testExampleSet, e);
    
  }
  
  
  
  public void printNodeModel(DecisionTreeNode node, int indent, boolean leafNode,
                             int splitIndex) throws Exception {

    

    if (leafNode) {
      indent(indent);
      System.out.println("*** Leaf Node ***");
    }

    // calculate and print statistics
    
    // !!!
    if (false /*node.examples != null*/) {

      ExampleTable examples = node.examples;
      int numExamples = examples.getNumRows();
      double outputValueSum = 0.0;
      double outputValueMin = Double.MAX_VALUE;
      double outputValueMax = Double.MIN_VALUE;
      for (int e = 0; e < numExamples; e++) {
        double outputValue = examples.getOutputDouble(e, 0);

        if (outputValue < outputValueMin)
          outputValueMin = outputValue;
        if (outputValue > outputValueMax)
          outputValueMax = outputValue;
        
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
      
      
      indent(indent);
      System.out.println("Mean Value         = " + Format.format(outputValueMean));
      indent(indent);
      System.out.println("Minimum Value      = " + Format.format(outputValueMin));
      indent(indent);
      System.out.println("Maximum Value      = " + Format.format(outputValueMax));
      indent(indent);
      System.out.println("Standard Deviation = " + Format.format(outputSTD));
      indent(indent);
      System.out.println("Node Depth         = " + node.depth);
      indent(indent);
      System.out.println("Node Index         = " + node.index);
      indent(indent);
      System.out.println("Node Population    = " + numExamples);
      indent(indent);
      System.out.println("Error Reduction    = " + Format.format(node.error / numExamples));
      if (!leafNode) {
      indent(indent);
      System.out.println("Error Reduction    = " + Format.format(node.error / numExamples));
      indent(indent);
      System.out.println("Split Feature      = " + this.getInputFeatureName(splitIndex));
      }
    }
    
      indent(indent);
      System.out.println("[Pop = " + node.numExamples + "]");
    
    
    if (OutputModelIndices) {
      if (!ReportOnlyLeafModelIndices || leafNode)
        System.out.print("subModelIndex = " + node.subModelIndex);
    } else {
      indent(indent);
      node.model.print(PrintOptions, indent);
    }
  }

  public void printTree(DecisionTreeNode node, int indent) throws Exception {

    if (node.decomposition != null) {
      int splitIndex = node.decomposition.inputIndex;
      double splitValue = node.decomposition.value;

      if (PrintOptions.PrintInnerNodeModels) {
        printNodeModel(node, indent, false, splitIndex);
        System.out.println("");
      }

      String testString = null;
      if (PrintOptions.AsciiInputs) {
        if (PrintOptions.EnumerateSplitValues) {
          int[] byteCounts = new int[256];
          ExampleTable examples = node.examples;
          int numExamples = examples.getNumRows();
          for (int e = 0; e < numExamples; e++) {
            int byteValue = (int) examples.getInputDouble(e, splitIndex);
            if (byteValue < splitValue)
              byteCounts[byteValue]++;
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
        }
        else {
          testString = "(" + this.getInputFeatureName(splitIndex) + " > " +
              ( (char) splitValue) + ")";
        }
      }
      else {
        testString = "(" + this.getInputFeatureName(splitIndex) + " > " +
            Format.format(splitValue) + ")";
      }

      indent(indent);
      System.out.println("If " + testString);

      indent(indent + 1);
      System.out.println("Then");
      printTree(node.childNode1, indent + 2);
      indent(indent + 1);
      System.out.println("Else" + "  // not" + testString);
      printTree(node.childNode2, indent + 2);
    }
    else {
      printNodeModel(node, indent, true, -1);
      System.out.println("");
    }

  }

  ModelPrintOptions PrintOptions = null;
  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions printOptions, int indent) throws Exception {
    PrintOptions = printOptions;

    Format.setMaximumFractionDigits(PrintOptions.MaximumFractionDigits);

    System.out.println("Decision Tree:");
    printTree(this.decisionTree, indent);
  }


  public int countNumSubModels(DecisionTreeNode node, boolean reportOnlyLeafModelIndices, int modelIndex) throws Exception {

    if (node.decomposition != null) {
      
      if (!reportOnlyLeafModelIndices) 
        modelIndex++;

      modelIndex = countNumSubModels(node.childNode1, reportOnlyLeafModelIndices, modelIndex);
      modelIndex = countNumSubModels(node.childNode2, reportOnlyLeafModelIndices, modelIndex);
      return modelIndex;
    }
    else {
      modelIndex++;
      return modelIndex;
    }

  }
  public int calculateMaxDepth(DecisionTreeNode node, int depth, int maxDepth) throws Exception {

    if (depth > maxDepth) {
      maxDepth = depth;
    }
    
    if (node.decomposition != null) {
      
      maxDepth = calculateMaxDepth(node.childNode1, depth + 1, maxDepth);
      maxDepth = calculateMaxDepth(node.childNode2, depth + 1, maxDepth);
    }
    
    return maxDepth;
  }


  public int setSubModels(DecisionTreeNode node, boolean reportOnlyLeafModelIndices, int modelIndex) throws Exception {

    if (node.decomposition != null) {
      
      if (!reportOnlyLeafModelIndices)  {
        this.subModels[modelIndex] = node.model;
        node.subModelIndex = modelIndex;
        modelIndex++;
      }

      modelIndex = setSubModels(node.childNode1, reportOnlyLeafModelIndices, modelIndex);
      modelIndex = setSubModels(node.childNode2, reportOnlyLeafModelIndices, modelIndex);
      return modelIndex;
    }
    else {
      this.subModels[modelIndex] = node.model;
      node.subModelIndex = modelIndex;
      modelIndex++;
      return modelIndex;
    }

  }
}
