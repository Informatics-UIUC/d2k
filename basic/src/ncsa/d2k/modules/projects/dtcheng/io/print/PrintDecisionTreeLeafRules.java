package ncsa.d2k.modules.projects.dtcheng.io.print;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class PrintDecisionTreeLeafRules
 extends OutputModule {
  
  public String getModuleName() {
    return "PrintDecisionTreeLeafRules";
  }
  
  public String getModuleInfo() {
    return "This module report the rules assocaited with each decision tree leaf";
  }
  
  public String[] getInputTypes() {
    String[] in = {
      "ncsa.d2k.modules.projects.dtcheng.inducers.DecisionTreeModel"};
      return in;
  }
  
  public String[] getOutputTypes() {
    String[] out = {};
    return out;
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Model";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Model to be reported";
    }
    return "";
  }
  
  public String getOutputName(int i) {
    switch (i) {
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    }
    return "";
  }
  
  
  
  
  private boolean SortByPredictedOutput = false;
  public void setSortByPredictedOutput(boolean value) {
    this.SortByPredictedOutput = value;
  }

  public boolean getSortByPredictedOutput() {
    return this.SortByPredictedOutput;
  }

  
  
  private boolean SortByAverageInput = false;
  public void setSortByAverageInput(boolean value) {
    this.SortByAverageInput = value;
  }

  public boolean getSortByAverageInput() {
    return this.SortByAverageInput;
  }

  
  
  
  
  
  
  
  
  
  private final class PredictedOutputComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      
      DecisionTreeNode node1 = (DecisionTreeNode) ((Object[]) o1)[0];
      DecisionTreeNode node2 = (DecisionTreeNode) ((Object[]) o2)[0];
      
      MeanOutputModel model1 = (MeanOutputModel) node1.getModel();
      MeanOutputModel model2 = (MeanOutputModel) node2.getModel();
      
      return Double.compare(model2.meanOutputValues[0], model1.meanOutputValues[0]);
    }

    public boolean equals(Object o) {
      return super.equals(o);
    }
  }
  
  
  public void recurse(DecisionTreeNode node, double [] lowerBounds, double [] upperBounds, Vector results) throws Exception {
    
    if (node.getDecomposition() != null) {
      
//      System.out.println("node.decomposition.getInputIndex( = " + node.decomposition.getInputIndex());
//      System.out.println("node.decomposition.getValue() = " + node.decomposition.getValue());
      
      
      double [] newLowerBounds = (double []) lowerBounds.clone();
      double [] newUpperBounds = (double []) upperBounds.clone();
      
      newLowerBounds[node.getDecomposition().getInputIndex()] = node.getDecomposition().getValue();
      newUpperBounds[node.getDecomposition().getInputIndex()] = node.getDecomposition().getValue();
      
      recurse(node.childNode1, newLowerBounds, upperBounds, results);
      recurse(node.childNode2, lowerBounds, newUpperBounds, results);
    } else {
      
      // compute average input
      
      ExampleTable examples = node.examples;
      
      int numExamples = examples.getNumRows();
      double inputSum = 0.0;
      
      for (int e = 0; e < numExamples; e++) {
        inputSum += examples.getInputDouble(e, 0);
      }
      
      double inputAverage = inputSum / numExamples;
      
      
      results.add(new Object [] {node, new Double(inputAverage), lowerBounds.clone(), upperBounds.clone()});

      
    }
    
  }
  
  ModelPrintOptions printOptions = null;
  int numInputs;
  
  public void doit() throws Exception {
    
    
    printOptions = new ModelPrintOptions();
    
    printOptions.AsciiInputs = false;
    printOptions.EnumerateSplitValues = false;
    printOptions.PrintInnerNodeModels = false;
    printOptions.MaximumFractionDigits = 6;
    
    
    DecisionTreeModel model = (DecisionTreeModel)this.pullInput(0);
    
    if (model == null) {
      this.pushOutput(null, 0);
      return;
    }
    
    
    DecisionTreeNode rootNode = model.getDecisionTreeRoot();
    
    
    
    // find leaves //
    int numLeaves = model.countNumSubModels(rootNode, true, 0);
    System.out.println("numSubModels = " + numLeaves);
    
    int maxDepth = model.calculateMaxDepth(rootNode, 0, 0);
    System.out.println("maxDepth = " + maxDepth);
    
    
    // create sub model information for leaves //
    
    Model [] subModels = new Model[numLeaves];
    model.subModels = subModels;
    model.setSubModels(rootNode, true, 0);
    
    /* create array of sub model information for sorting */
    
    
    
    Decomposition [] decompositionHistory = new Decomposition[12];
    
    numInputs = model.getNumInputFeatures();
    
    double [] lowerBounds = new double[numInputs];
    double [] upperBounds = new double[numInputs];
    
    for (int i = 0; i < numInputs; i++) {
      lowerBounds[i] = Double.NEGATIVE_INFINITY;
      upperBounds[i] = Double.POSITIVE_INFINITY;
    }
    
    Vector results = new Vector();
    
    recurse(rootNode, lowerBounds, upperBounds, results);
    
    
    int numResults = results.size();
    
    Object [] resultArray = new Object[numResults];
    
    for (int leafIndex = 0; leafIndex < numResults; leafIndex++) {
      resultArray[leafIndex] =  results.get(leafIndex);
    }
    
    if (SortByPredictedOutput)
      Arrays.sort(resultArray, new PredictedOutputComparator());
  
  
    for (int leafIndex = 0; leafIndex < numResults; leafIndex++) {
      
      
      DecisionTreeNode node = (DecisionTreeNode) ((Object []) resultArray[leafIndex])[0];
      double averageInput = ((Double) ((Object []) resultArray[leafIndex])[1]).doubleValue();
      lowerBounds = (double []) ((Object []) resultArray[leafIndex])[2];
      upperBounds = (double []) ((Object []) resultArray[leafIndex])[3];
      
      if (SortByAverageInput) {
      System.out.print(averageInput);
      System.out.print("\t");
      }
      System.out.print(((MeanOutputModel)node.getModel()).meanOutputValues[0]);
      System.out.print("\t");
      System.out.print(node.numExamples);
      System.out.print("\t");
      for (int i = 0; i < numInputs; i++) {
        if ((lowerBounds [i] != Double.NEGATIVE_INFINITY) || (upperBounds [i] != Double.POSITIVE_INFINITY )) {
          System.out.print("(");
          if (lowerBounds [i] != Double.NEGATIVE_INFINITY) {
            System.out.print(lowerBounds[i] + " < ");
          }
          System.out.print(node.model.getInputFeatureName(i));
          if (upperBounds [i] != Double.POSITIVE_INFINITY ) {
            System.out.print(" < " + upperBounds[i]);
          }
          System.out.print(")");
        }
      }
      System.out.println();
    }
  }
  
}
