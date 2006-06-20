package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.table.*;
import java.text.*;


public class HistogramPDFModel extends Model implements java.io.Serializable {
  int       numExamples;
  int       numInputs;
  int       numOutputs;
  int       numRegions;
  double    minValue;
  double    maxValue;
  double [] splitPoints;
  double [] logDensities;
  
  public HistogramPDFModel(ExampleTable examples, int numExamples, int numRegions, double minValue, double maxValue, double [] splitPoints,  double [] logProbabilities) {
    super(examples);
    this.numExamples = numExamples;
    this.numInputs = examples.getNumInputFeatures();
    this.numOutputs = examples.getNumOutputFeatures();
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.numRegions = numRegions;
    this.splitPoints = splitPoints;
    this.logDensities = logProbabilities;
  }
  
  public HistogramPDFModel(
   int trainingSetSize,
   String[] inputColumnLabels,
   String[] outputColumnLabels,
   int[] inputFeatureTypes,
   int[] outputFeatureTypes,
   int numExamples,
   int numRegions,
   double minValue,
   double maxValue,
   double [] regionSplitPoints,
   double [] logProbabilities) {
    super(trainingSetSize, inputColumnLabels, outputColumnLabels, inputFeatureTypes, outputFeatureTypes);
    this.numExamples = numExamples;
    this.numRegions = numRegions;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.splitPoints = regionSplitPoints;
    this.logDensities = logProbabilities;
  }
  
  public void evaluate(double [] inputs, double [] outputs) {
//    for (int f = 0; f < outputs.length; f++) {
//      outputs[f] = meanOutputValues[f];
//      }
  }
  
  public double [] evaluate(ExampleTable exampleSet, int e) {
    double [] outputs = new double[exampleSet.getNumOutputFeatures()];
//    for (int f = 0; f < meanOutputValues.length; f++) {
//      outputs[f] = meanOutputValues[f];
//      }
    return outputs;
  }
  
  public void evaluate(ExampleTable exampleSet, int e, double [] outputs) {
//    for (int f = 0; f < meanOutputValues.length; f++) {
//      outputs[f] = meanOutputValues[f];
//      }
  }
  
  
  public double evaluateLogLikelihood(ExampleTable exampleSet, int e) throws Exception {
    
    
    if (numOutputs != 1) {
      System.out.println("Error in evaluateLogLikelihood ! (numOutputs != 1) and algorithm not generalized yet");
    }
    
    double [] actualOutputs = new double[this.numOutputs];
    
    double actualOutput = exampleSet.getOutputDouble(e, 0);  //!!! generalize later
    double logLikelihood = 0;
    int matching_r = numRegions - 1;
    for (int r = 0; r < numRegions - 1; r++) {
      if (actualOutput < splitPoints[r]) {  //!!! generalize later
        matching_r = r;
        break;
      }
    }
    
    logLikelihood += logDensities[matching_r];
    
    return logLikelihood;
    
  }
  /*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames, double [] meanOutputValues) {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.inputNames = inputNames;
    this.outputNames = outputNames;
    this.meanOutputValues = (double []) meanOutputValues.clone();
    }
 */
  
  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions options, int indent) {
    
    Format.setMaximumFractionDigits(options.MaximumFractionDigits);
    Format.setMinimumFractionDigits(options.MaximumFractionDigits);
    
    indent(indent);
    System.out.println("Histogram PDF Model");
    indent(indent);
    System.out.println("numExamples        = " + numExamples);
    indent(indent);
    System.out.println("Number Of Regions  = " + numRegions);
    indent(indent);
    System.out.println("PDF Lower Bound    = " + minValue);
    indent(indent);
    System.out.println("PDF Upper Bound    = " + maxValue);
    
    
    int bestRegionIndex = -1;
    double bestLogDensity = Double.NEGATIVE_INFINITY;
    double bestPoint = Double.NaN;
    for (int i = 0; i < numRegions - 1; i++) {
      double binLowerBound = Double.NaN;
      if (i == 0)
        binLowerBound = minValue;
      else
        binLowerBound = splitPoints[i];
      
      double binUpperBound = Double.NaN;
      if (i == numRegions - 2)
        binUpperBound = maxValue;
      else
         binUpperBound = splitPoints[i + 1];
      
      if (logDensities[i] > bestLogDensity) {
        bestRegionIndex = i;
        bestLogDensity = logDensities[i];
        bestPoint = (binLowerBound + binUpperBound) / 2.0;
      }
    }
    
    
    
    indent(indent);
    System.out.println("PDF Best Density   = " + Math.pow(10.0, bestLogDensity));
    indent(indent);
    System.out.println("PDF Best Point     = " + bestPoint);
    
    
//    System.out.println("splitPoints: ");
//    for (int i = 0; i < numRegions - 1; i++) {
//      System.out.println(Format.format(splitPoints[i]));
//    }
    
    indent(indent);
    System.out.println("PDF:");
    indent(indent);
    System.out.println("BinLowerBound\tBinUpperBound\tProbabilityDensity");
    for (int i = 0; i < numRegions; i++) {
      
      indent(indent);
      if (i == 0)
        System.out.print(Format.format(minValue));
      else
        System.out.print(Format.format(splitPoints[i - 1]));
      System.out.print("\t");
      if (i == numRegions - 1)
        System.out.print(Format.format(maxValue));
      else
        System.out.print(Format.format(splitPoints[i]));
      System.out.print("\t");
      System.out.print(Format.format(Math.pow(10.0, logDensities[i])));
      System.out.println();
    }
    //System.out.println();
  }
  
}
