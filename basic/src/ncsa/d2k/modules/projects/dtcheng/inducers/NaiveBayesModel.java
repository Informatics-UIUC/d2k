package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.table.*;
public class NaiveBayesModel extends Model implements java.io.Serializable {
  
  int     numSelectedInputs;
  int     [] selectedIndices;
  int     numExamples;
  int     numInputFeatures;
  int     [][][][] counts;
  
  double [] inputs;
  
  double [] inputMeans;
  int    [] newInputs;
  double [] outputs;
  
  public NaiveBayesModel(ExampleTable examples, boolean [] selectedInputs, int [][][][] counts, int numExamples, double [] inputMeans) {
    
    
  
    super(examples);  
  
    int wrongNumInputs = getNumInputs();
    
    int numInputs  = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();
    
    int    numSelectedInputs = 0;
    int [] selectedInputIndices = new int[numInputs];
    
    for (int i = 0; i < numInputs; i++) {
      if (selectedInputs[i] == true) {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
      }
    }
    
    
    this.numExamples          = numExamples;
    this.numSelectedInputs    = numSelectedInputs;
    this.selectedIndices      = selectedInputIndices;
    this.counts               = (int [][][][]) counts.clone();
    this.inputs               = new double[numInputs];
    this.newInputs            = new int[numInputs];
    this.outputs              = new double[numOutputs];
    this.numInputFeatures     = numInputs;
    this.inputMeans           = (double []) inputMeans.clone();
    
  }
  

  public void evaluate(double [] inputs, double [] outputs) throws Exception {
    
    for (int i = 0; i < inputs.length; i++) {
      if (inputs[i] > inputMeans[i]) {
         newInputs[i] = 1;
      }
      else {
         newInputs[i] = 0;
      }
    }
    
    
    for (int o = 0; o < counts.length; o++) {
    
      double posLogProbSum   = 0.0;
      double negLogProbSum   = 0.0;
      double logOddsRatioSum = 0.0;
      double probabilitySum  = 0.0;
      
      for (int i = 0; i < numSelectedInputs; i++) {
      
        int input = newInputs[selectedIndices[i]];
        
        double negativeInputCount = counts[o][selectedIndices[i]][input][0];
        double positiveInputCount = counts[o][selectedIndices[i]][input][1];
        
        // assign default probabilities to unseed events
        if (negativeInputCount == 0.0)
          negativeInputCount = 1.0 / numExamples;
        
        if (positiveInputCount == 0.0)
          positiveInputCount = 1.0 / numExamples;
        
        double logOddsRatio = Math.log(positiveInputCount / negativeInputCount);
        
        double positiveProbability = positiveInputCount / (positiveInputCount + negativeInputCount);
        
        probabilitySum += positiveProbability;
        
        logOddsRatioSum += logOddsRatio;
        
        //negLogProbSum += Math.log(negativeInputCount / numExamples);
        //posLogProbSum += Math.log(positiveInputCount / numExamples);
      }
      
      //outputs[o] = Math.exp(posLogProbSum) / (Math.exp(posLogProbSum) + Math.exp(negLogProbSum));
      //outputs[o] = posLogProbSum - negLogProbSum;
      
      //outputs[o] =  Math.exp(logOddsRatioSum / numSelectedInputs) / (1.0 + Math.exp(logOddsRatioSum / numSelectedInputs));
      
      
      //outputs[o] =  logOddsRatioSum / numSelectedInputs;
      outputs[o] = probabilitySum / numSelectedInputs;
    }
    
    
  }
  
  public double [] evaluate(ExampleTable examples, int e) throws Exception {
    
    for (int i = 0; i < numInputFeatures; i++) {
      inputs[i] = examples.getInputDouble(e, i);
    }
    evaluate(inputs, outputs);
    return outputs;
  }

  public void print(ModelPrintOptions printOptions, int indent) throws Exception {
    System.out.println("NaiveBayes Model:");
    for (int o = 0; o < counts.length; o++) {
      System.out.println(this.getOutputName(o) + " = " );
      for (int i = 0; i < numSelectedInputs; i++) {
        System.out.println(counts[o][selectedIndices[i]][1][1] + " * (" + 
         getInputFeatureName(selectedIndices[i]) + ">" + inputMeans[selectedIndices[i]] + ") + ");
      }
    }
  }
  
  
}
