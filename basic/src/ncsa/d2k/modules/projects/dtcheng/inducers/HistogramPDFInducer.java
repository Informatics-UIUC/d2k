package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class HistogramPDFInducer extends FunctionInducer {
  //int NumBiasParameters = 0;
  
  
  
  
  int numBiasDimensions = 3;
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];
    
    int i = 0;
    
    pds[i++] = new PropertyDescription(
     "numRegions",
     "Num Regions",
     "The number of histogram bins.  " +
     "This must be set to 1 or greater.  ");
    
    pds[i++] = new PropertyDescription(
     "minValue",
     "Min Value",
     "The lower bound for output values");
    
    pds[i++] = new PropertyDescription(
     "maxValue",
     "Max Value",
     "The upper bound for output values");
    
    return pds;
  }
  
  private int NumRegions = 20;
  public void setNumRegions(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.NumRegions = value;
  }
  
  public int getNumRegions() {
    return this.NumRegions;
  }
  
  
  private double MinValue = 0.0;
  public void setMinValue(double value) throws PropertyVetoException {
    if (MinValue >= MaxValue) {
      throw new PropertyVetoException("MinValue >= MaxValue", null);
    }
    this.MinValue = value;
  }
  public double getMinValue() {
    return this.MinValue;
  }
  
  private double MaxValue = 1.0;
  public void setMaxValue(double value) throws PropertyVetoException {
    if (MaxValue < MinValue) {
      throw new PropertyVetoException("MaxValue < MinValue", null);
    }
    this.MaxValue = value;
  }
  public double getMaxValue() {
    return this.MaxValue;
  }
  
  
  
  
  
  
  
  public String getModuleInfo() {
    
    String s = "";
    s += "<p>";
    s += "Overview: ";
    s += "This module builds a simple histogram pdf model by counting the number of examples in each output bin.  </p>";
    
    return s;
  }
  
  
  public String getModuleName() {
    return "Histograph PDF Inducer";
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
       "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }
  
  public void instantiateBias(double [] bias) {
  }
  
  
  
  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {
    
    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();
    
    int       numRegions = NumRegions;
    double    minValue = MinValue;
    double    maxValue = MaxValue;
    double [] splitPoints      = new double[numRegions - 1];
    int    [] counts           = new int   [numRegions];
    double [] logDensities = new double[numRegions];
    
    double range = MaxValue - MinValue;
    
    
    
    

    double outputMin = Double.POSITIVE_INFINITY;
    double outputMax = Double.NEGATIVE_INFINITY;

    
    //////////////////////////////////////////
    // calculate feature min and max values //
    //////////////////////////////////////////
    

    for (int e = 0; e < numExamples; e++) {

        double value = examples.getOutputDouble(e, 0);
        if (value < outputMin)
          outputMin = value;
        if (value > outputMax)
          outputMax = value;
    }
    
    
    
    

    int numSplitPoints = numRegions - 1;

    double [] outputSplitLowerBounds     = new double[numSplitPoints];
    double [] outputSplitUpperBounds     = new double[numSplitPoints];
    double [] outputSplitCurrentValue    = new double[numSplitPoints];
    double [] outputTargetSplitCount     = new double[numSplitPoints];
    double [] outputActualSplitCount     = new double[numSplitPoints];
    double [] outputLastActualSplitCount = new double[numSplitPoints];

    for (int s = 0; s < numSplitPoints; s++) {

      for (int i = 0; i < numOutputs; i++) {
        outputSplitLowerBounds [s] =  outputMin;
        outputSplitUpperBounds [s] =  outputMax;
        outputSplitCurrentValue[s] = (outputMin + outputMax) / 2.0;
        outputTargetSplitCount [s] = (double) numExamples / (double) numRegions * (double) (s + 1);
      }
    }

    int NumRounds = 20;
    
    for (int r = 0; r < NumRounds; r++) {

//      System.out.println("Round = " + (r + 1));

      // clear counts
      for (int s = 0; s < numSplitPoints; s++) {
          outputActualSplitCount[s] = 0;
      }

      // calculate actual counts
      for (int e = 0; e < numExamples; e++) {
        for (int s = 0; s < numSplitPoints; s++) {
            double value = examples.getOutputDouble(e, 0);
            if (value < outputSplitCurrentValue[s])
              outputActualSplitCount[s]++;
        }
      }

      // report progress
//        for (int s = 0; s < numSplitPoints; s++) {
//          System.out.println("  s = " + (s+1));
//          System.out.println("    outputSplitCurrentValue = " + outputSplitCurrentValue[s]);
//          System.out.println("    outputTargetSplitCount  = " + outputTargetSplitCount [s]);
//          System.out.print  ("    outputActualSplitCount  = " + outputActualSplitCount [s]);
//          if (outputLastActualSplitCount[s] == outputActualSplitCount[s])
//            System.out.println();
//          else
//            System.out.println("*changed*");
//          outputLastActualSplitCount[s] = outputActualSplitCount[s];
//        }

      // adjust search ranges and midpoint

      for (int s = 0; s < numSplitPoints; s++) {
          if (outputActualSplitCount[s] < outputTargetSplitCount[s]) {
            outputSplitLowerBounds[s] = outputSplitCurrentValue[s];
            outputSplitCurrentValue[s] = (outputSplitLowerBounds[s] +  outputSplitUpperBounds[s]) / 2.0;
          }
          else {
            outputSplitUpperBounds[s] = outputSplitCurrentValue[s];
            outputSplitCurrentValue[s] = (outputSplitLowerBounds[s] +  outputSplitUpperBounds[s]) / 2.0;
          }
      }
    }

      // eliminate bins that are empty
      for (int e = 0; e < numExamples; e++) {
        for (int s = 0; s < numSplitPoints; s++) {
            double value = examples.getOutputDouble(e, 0);
            if (value < outputSplitCurrentValue[s])
              outputActualSplitCount[s]++;
        }
      }
    
    
//    double binWidth = range / (double) numRegions;
//    
//    for (int r = 0; r < NumRegions - 1; r++) {
//      
//      splitPoints[r] = ((maxValue - minValue) / NumRegions) * (r + 1) + MinValue;
//      
//    }
    
    double [] regionLowerBounds = new double[NumRegions];
    double [] regionUpperBounds = new double[NumRegions];
    for (int r = 0; r < NumRegions; r++) {
      
      if (r < NumRegions - 1) {
        splitPoints[r] = outputSplitCurrentValue[r];
      }
      
      if (r == 0) 
        regionLowerBounds[r] = minValue;
      else
        regionLowerBounds[r] = splitPoints[r - 1];
      
      if (r == NumRegions - 1) 
        regionUpperBounds[r] = maxValue;
      else
        regionUpperBounds[r] = splitPoints[r];
      
      
    }
    
    
    
    for (int e = 0; e < numExamples; e++) {
      int matching_r = numRegions - 1;
      double outputValue = examples.getOutputDouble(e, 0);  //!!! generalize later
      for (int r = 0; r < numRegions - 1; r++) {
        if (outputValue < splitPoints[r])  {
          matching_r = r;
          break;
        }
      }
      counts[matching_r]++;
    }
    
    
    int numEmptyBins = 0;
    double emptyBinCombinedWidth = 0.0;
    for (int r = 0; r < numRegions; r++) {
      if (counts[r] == 0) {
        numEmptyBins++;
        emptyBinCombinedWidth += (regionUpperBounds[r] - regionLowerBounds[r]);
      }
    }
    
    double emptyBinCountFraction = 0.5;
    double emptyBinCount = emptyBinCountFraction;
    
    double unnormalizedDensity;
    double unnormalizedDensitySum = 0.0;
    double massSum = 0.0;
    for (int r = 0; r < numRegions; r++) {
      if (counts[r] > 0) {
        unnormalizedDensity = (double) counts[r] /  (regionUpperBounds[r] - regionLowerBounds[r]);
        massSum += (double) counts[r];
      }
      else {
        unnormalizedDensity = emptyBinCount / emptyBinCombinedWidth;
        massSum += emptyBinCount;
      }
    }
    
    for (int r = 0; r < numRegions; r++) {
      if (counts[r] > 0)
        unnormalizedDensity = (double) counts[r] /  (regionUpperBounds[r] - regionLowerBounds[r]);
      else
        unnormalizedDensity = emptyBinCount /  (regionUpperBounds[r] - regionLowerBounds[r]);
      
      double trueDensity = unnormalizedDensity / massSum;
      logDensities[r] =  Math.log10(trueDensity);
    }
    
    HistogramPDFModel model = new HistogramPDFModel(
     examples,
     numExamples,
     numRegions,
     minValue,
     maxValue,
     splitPoints,
     logDensities);
    
    return (Model) model;
  }
  
}
