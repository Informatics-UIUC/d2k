package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;

public class InstanceBasedModel_1 extends Model implements java.io.Serializable {
  
  ExampleTable trainExampleSet;
  
  double[] inputRanges;
  double[] inputMins;
  double[] inputMaxs;
  int      NeighborhoodSize;
  double   DistanceWeightingPower;
  double   ZeroDistanceWeight;
  boolean  UseStepwiseRegression;
  int      Direction;
  int      NumRounds;
  boolean  ExpandNeighborhoodForTies;
  double   MaxExpansionFactor;
  
  
  
  double[] bestDistances = null;
  
  int[]    bestExampleIndices = null;
  
  double[] outputs = null;
  
  ErrorFunction ErrorFunction;
  
  public InstanceBasedModel_1(
   ExampleTable examples,
   double[] inputRanges,
   double[] inputMins,
   double[] inputMaxs,
   int NeighborhoodSize,
   double DistanceWeightingPower,
   double ZeroDistanceWeight,
   boolean UseStepwiseRegression,
   int Direction,
   int NumRounds,
   boolean ExpandNeighborhoodForTies,
   double MaxExpansionFactor,
   ErrorFunction ErrorFunction) {
    super(examples);
    
    this.inputRanges               = inputRanges;
    this.inputMins                 = inputMins;
    this.inputMaxs                 = inputMaxs;
    this.NeighborhoodSize          = NeighborhoodSize;
    this.DistanceWeightingPower    = DistanceWeightingPower;
    this.ZeroDistanceWeight        = ZeroDistanceWeight;
    this.UseStepwiseRegression     = UseStepwiseRegression;
    this.Direction                 = Direction;
    this.NumRounds                 = NumRounds;
    this.ErrorFunction             = ErrorFunction;
    this.ExpandNeighborhoodForTies = ExpandNeighborhoodForTies;
    this.MaxExpansionFactor        = MaxExpansionFactor;
    this.trainExampleSet           = examples;
    //this.trainExampleSet           = (ExampleTable) exampleSet.copy();  !!! ???
  }
  
  
  
  public void evaluate(double[] inputs, double[] outputs) throws Exception {
    
    int numExamples = trainExampleSet.getNumRows();
    int numInputs   = inputs.length;
    int numOutputs  = outputs.length;
    
    int actualNeighborhoodSize = -1;
    
    if (ExpandNeighborhoodForTies) {
      actualNeighborhoodSize = (int) (NeighborhoodSize * MaxExpansionFactor);
    } else {
      actualNeighborhoodSize = NeighborhoodSize;
    }
    
    if (actualNeighborhoodSize > numExamples) {
      actualNeighborhoodSize = numExamples;
    }
    
    // initialize distance and index vectors
    double bestDistance     = Double.POSITIVE_INFINITY;
    int    bestExampleIndex = Integer.MIN_VALUE;
    
    if (bestDistances == null) {
      bestDistances      = new double[actualNeighborhoodSize];
      bestExampleIndices = new int   [actualNeighborhoodSize];
    }
    for (int i = 0; i < actualNeighborhoodSize; i++) {
      bestDistances     [i] = Double.POSITIVE_INFINITY;
      bestExampleIndices[i] = Integer.MIN_VALUE;
    }
    
    
    
    // compare input vector with every stored example to fine neighborhood
    for (int e = 0; e < numExamples; e++) {
      double sumOfSquares = 0.0;
      double difference  = Double.NaN;
      for (int f = 0; f < numInputs; f++) {
        if (inputRanges[f] != 0.0) {
          difference = (trainExampleSet.getInputDouble(e, f) - inputs[f]) / inputRanges[f];
          sumOfSquares += difference * difference;
        }
      }
      double distanceSquared = sumOfSquares;
      
      // update neighborhood
      if (distanceSquared <= bestDistances[0]) {
        
        // make space if necessary
        for (int i = 0; i < actualNeighborhoodSize; i++) {
          if (distanceSquared <= bestDistances[i]) {
            if (i < actualNeighborhoodSize - 1) {
              // move existing pairs to make room
              bestDistances     [i] = bestDistances     [i + 1];
              bestExampleIndices[i] = bestExampleIndices[i + 1];
            } else {
              bestDistances     [i] = sumOfSquares;
              bestExampleIndices[i] = e;
              break;
            }
          } else {
            bestDistances     [i - 1] = sumOfSquares;
            bestExampleIndices[i - 1] = e;
            break;
          }
        }
        
      }
    }
    
    
    
    
    // find true neighborhood size
    int startIndex = -1;
    
    if (ExpandNeighborhoodForTies) {
      
      //System.out.println("before actualNeighborhoodSize = " + actualNeighborhoodSize);
      
      if (NeighborhoodSize < numExamples) {
        startIndex = actualNeighborhoodSize - NeighborhoodSize;
        double startDistance = bestDistances[startIndex];
        
        boolean terminated = false;
        for (int i = startIndex + 1; i >= 0; i--) {
          if (bestDistances[i] != startDistance) {
            break;
          }
          startIndex = i;
        }
        
        //System.out.println("############## startIndex = " + startIndex);
        
      }
    } else {
      startIndex = 0;
    }
    
    
    if (outputs == null)
      outputs = new double[numOutputs];
    else {
      for (int i = 0; i < numOutputs; i++)
        outputs[i] = 0;
    }
    
    if (UseStepwiseRegression) {
      
      // create example table for stepwise regression
      ExampleTable NeighborhoodExamples = (ExampleTable) ((ContinuousDoubleExampleTable) trainExampleSet).getSubset(bestExampleIndices);
      
      double[] bias      = new double[3];
      String[] biasNames = new String[3];
      
      int biasIndex = 0;
      
      biasNames[biasIndex] = "UseStepwise";
      bias[biasIndex]      =  1;
      biasIndex++;
      biasNames[biasIndex] = "Direction";
      bias[biasIndex]      =  Direction;
      biasIndex++;
      biasNames[biasIndex] = "NumRounds";
      bias[biasIndex]      =  NumRounds;
      biasIndex++;
      
      ParameterPoint parameterPoint = ParameterPointImpl.getParameterPoint(biasNames, bias);
      
      StepwiseLinearInducerOpt inducer = new StepwiseLinearInducerOpt();
      
      inducer.setControlParameters(parameterPoint);
      
      Model model = inducer.generateModel(NeighborhoodExamples, ErrorFunction);
      
      ModelPrintOptions options = new ModelPrintOptions();
      
      //model.print(options);
      
      model.evaluate(inputs, outputs);
      
    } else {
      
      
      //System.out.println(" bestDistance       = " + bestDistances[actualNeighborhoodSize - 1]);
      //System.out.println(" bestExampleIndices = " + bestExampleIndices[actualNeighborhoodSize - 1]);
      
      // use distance weighted output vectors
      
      double weightSum = 0.0;
      double weight;
      for (int i = startIndex; i < actualNeighborhoodSize; i++) {
        
        double distanceSumOfSquares = bestDistances[i];
        
        if (distanceSumOfSquares == 0) {
          weight = ZeroDistanceWeight;
        } else {
          double distance = Math.sqrt(distanceSumOfSquares / numInputs);
          weight = 1.0 / Math.pow(distance, DistanceWeightingPower);
          
          //System.out.println(" Math.pow(" + distance + ", " + DistanceWeightingPower + ")");
          
        }
        
        weightSum += weight;
        
        for (int f = 0; f < numOutputs; f++) {
          outputs[f] += trainExampleSet.getOutputDouble(bestExampleIndices[i], f) * weight;
        }
      }
      
      for (int f = 0; f < numOutputs; f++) {
        outputs[f] /= weightSum;
      }
      
    }
  }
  
  
  public double[] evaluate(ExampleTable testExampleSet, int e) throws Exception {
    
    int numExamples = trainExampleSet.getNumRows();
    int numInputs   = trainExampleSet.getNumInputFeatures();
    int numOutputs  = trainExampleSet.getNumOutputFeatures();
    
    
    double[] inputs  = new double[numInputs];
    double[] outputs = new double[numOutputs];
    
    
    for (int f = 0; f < numInputs; f++) {
      inputs[f] = testExampleSet.getInputDouble(e, f);
    }
    
    evaluate(inputs, outputs);
    
    return outputs;
  }
  
  /*
   * public void Instantiate(int numInputs, int numOutputs, String []
   * inputNames, String [] outputNames, double [] inputRanges, int
   * NeighborhoodSize, double DistanceWeightingPower, double ZeroDistanceWeight,
   * ExampleTable exampleSet) { this.numInputs = numInputs; this.numOutputs =
   * numOutputs; this.inputNames = inputNames; this.outputNames = outputNames;
   * this.inputRanges = inputRanges; this.NeighborhoodSize = NeighborhoodSize;
   * this.DistanceWeightingPower = DistanceWeightingPower;
   * this.ZeroDistanceWeight = ZeroDistanceWeight; this.trainExampleSet =
   * (ExampleTable) exampleSet.copy(); }
   */
  
  public void print(ModelPrintOptions printOptions, int indent) throws Exception {
    
    
    int numExamples = trainExampleSet.getNumRows();
    int numInputs   = trainExampleSet.getNumInputFeatures();
    
    
    System.out.println("Instance Based Control Parameters:");
    System.out.println("  NeighborhoodSize          = " + NeighborhoodSize);
    System.out.println("  DistanceWeightingPower    = " + DistanceWeightingPower);
    System.out.println("  ZeroDistanceWeight        = " + ZeroDistanceWeight);
    System.out.println("  UseStepwiseRegression     = " + UseStepwiseRegression);
    System.out.println("  Direction                 = " + Direction);
    System.out.println("  NumRounds                 = " + NumRounds);
    System.out.println("  ExpandNeighborhoodForTies = " + ExpandNeighborhoodForTies);
    System.out.println("  MaxExpansionFactor        = " + MaxExpansionFactor);
    
    
    System.out.println("  InputFeaturesUsed:");
    for (int f = 0; f < numInputs; f++) {
      if (inputRanges[f] != 0.0) {
        System.out.println("    " +
         trainExampleSet.getInputName(f) +
         "\t min = " + inputMins[f] +
         "\t max = " + inputMaxs[f] +
         "\t range = " + inputRanges[f]);
      }
    }
    
    System.out.println("  InputFeaturesEliminated:");
    for (int f = 0; f < numInputs; f++) {
      if (inputRanges[f] == 0.0) {
        System.out.println("    " + trainExampleSet.getInputName(f) + "\t range = " + inputRanges[f]);
      }
    }
    
    
    System.out.println("Example Set Attributes");
    System.out.println("  NumExamples               = " + trainExampleSet.getNumRows());
  }
  
}