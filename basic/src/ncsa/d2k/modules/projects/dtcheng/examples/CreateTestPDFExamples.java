package ncsa.d2k.modules.projects.dtcheng.examples;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;
import java.util.Random;

public class CreateTestPDFExamples extends ComputeModule {
  
  
  public String getModuleName() {
    return "CreateTestPDFExamples";
  }
  
  public String getModuleInfo() {
    return "The module creates an example set for testing PDF learning";
  }
  
  public String getInputName(int i) {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "ExampleTable";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.ContinuousDoubleExampleTable"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "ExampleTable";
      default: return "No such output";
    }
  }
  
  
  
  
  public void doit() {
    
    double StdRange = 0.01;
    int numExamples = 10000;
    int numInputs = 10;
    int numOutputs = 1;
    long seed = 123;
    
    Random generator = new Random(seed);
    
    int numCentroids = 3;
    double [][] CentroidInputSpacePoints = new double[numCentroids][numInputs];
    double [] CentroidMeans = new double[numCentroids];
    double [] CentroidStds  = new double[numCentroids];
    
    /////////////////////////////
    // creat centroid for pdfs //
    /////////////////////////////
    for (int c = 0; c < numCentroids; c++) {
      
      for (int v = 0; v < numInputs; v++) {
        CentroidInputSpacePoints[c][v] = generator.nextDouble();
      }
      
      CentroidMeans[c] = generator.nextDouble();
      CentroidStds [c] = generator.nextDouble() * StdRange;
    }
    
    
    ////////////////////////////
    // allocate example table //
    ////////////////////////////
    
    int numFeatures = numInputs + numOutputs;
    
    double []   examples = new double[numExamples * numFeatures];
    
    int index = 0;
    
    String [] outputNames = new String[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      outputNames[i] = "out" + (i+1);
    }
    
    String [] inputNames = new String[numInputs];
    for (int i = 0; i < numInputs; i++) {
      inputNames[i] = "in" + (i+1);
    }
    
    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(examples, numExamples, numInputs, numOutputs, inputNames, outputNames);
    examples = null;
    
    
    
    
    /////////////////////////
    // create pdf examples //
    /////////////////////////
    for (int e = 0; e < numExamples; e++) {
      
      for (int v = 0; v < numInputs; v++) {
        exampleSet.setInput(e, v, generator.nextDouble());
      }
      
      // find closest centroid //
      double minDistance = Double.POSITIVE_INFINITY;
      int bestCentroidIndex = -1;
      for (int c = 0; c < numCentroids; c++) {
        double distance = 0.0;
        for (int v = 0; v < numInputs; v++) {
          double difference = Math.abs(CentroidInputSpacePoints[c][v] - exampleSet.getInputDouble(e, v));
          distance += difference;
        }
        if (distance < minDistance) {
          minDistance = distance;
          bestCentroidIndex = c;
        }
        
      }
      
      // use centroid pdf to generate output values //
      
      double mean = CentroidMeans[bestCentroidIndex];
      double std  = CentroidStds[bestCentroidIndex];
      
      for (int v = 0; v < numOutputs; v++) {
        exampleSet.setOutput(e, v, mean + generator.nextGaussian() * std);
      }
      
    }
    
    
    this.pushOutput(exampleSet, 0);
    
    
  }
  
}