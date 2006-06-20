package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNContinuous extends ComputeModule {
  
  public String getModuleName() {
    return "NNContinuous";
  }
  
  public String getModuleInfo() {
    return "This is based off of NNCondensedLessRoundingInts and now adapted for a single " +
    "continuous output. The layertable should thus have a \"1\" in the final entry for outputs " +
    "even though this would look like 2 output. we'll just deal... we will always correct for rounding errors."
    + "This, in hopes of speeding it up... <p>" 
    + "This module actually calculates the activations (so-called "
    + "forward propagation through the network) for each neuron based "
    + "on some supplied explanatory variables and the designated "
    + "parameters. Each row corresponds to an example and each column " + "to a neuron." + "<p>"
    + "Further, we go beyond that in order to"
    + "try to speed things up and condense the gradient into here as well."
    + "<p>Additionally, we are trying to avoid rounding errors employing a version"
    + "of the trick supplied b tony plate. Lecture Notes in Computer Science 1524, chapter 11";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ExplanatoryVariables";
    case 1:
      return "Targets";
    case 2:
      return "LayerTable";
    case 3:
      return "SerializedWeights";
    case 4:
      return "Biases";
    case 5:
      return "LayerStartFinishNeuronTable";
    case 6:
      return "NeuronToLayerTable";
    case 7:
      return "WeightNumberFromToNeuronTable";
    case 8:
      return "CalculateGradientFlag";
    case 9:
      return "CalculateErrorFlag";
    case 10:
      return "ReportAllActivationsFlag";
    case 11:
      return "NumberOfElementsThreshold";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ExplanatoryVariables";
    case 1:
      return "Targets";
    case 2:
      return "LayerTable";
    case 3:
      return "SerializedWeights";
    case 4:
      return "Biases";
    case 5:
      return "LayerStartFinishNeuronTable";
    case 6:
      return "NeuronToLayerTable";
    case 7:
      return "WeightNumberFromToNeuronTable";
    case 8:
      return "CalculateGradientFlag: a TRUE means to calcuate the gradient calculation "
      + "(so as to be able to use this module to just calculate the errors if desired)";
    case 9:
      return "CalculateErrorFlag: a TRUE means to calculate value of the cross-entropy "
      + "error function (-loglikelihood) (so as to be able to use this just to calculate the gradient "
      + "and/or activations if desired)";
    case 10:
      return "ReportAllActivationsFlag: a TRUE means to output a huge table with all the "
      + "neuron activations for each example (memory intensive). A FALSE will "
      + "not report everything and thus save lots of memory.";
    case 11:
      return "NumberOfElementsThreshold";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "[[I",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "[[I",
        "[[I",
        "[[I",
        "java.lang.Boolean", "java.lang.Boolean", "java.lang.Boolean", "java.lang.Long", };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "NeuronActivations";
    case 1:
      return "UnscaledSumSquaredError";
    case 2:
      return "UnscaledSumAbsoluteDeviation";
    case 3:
      return "BiasesGradient";
    case 4:
      return "SerializedWeightsGradient";
    case 5:
      return "StackedWeightsBiasesGradient";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "NeuronActivations";
    case 1:
      return "UnscaledSumSquaredError";
    case 2:
      return "UnscaledSumAbsoluteDeviation";
    case 3:
      return "BiasesGradient";
    case 4:
      return "SerializedWeightsGradient";
    case 5:
      return "StackedWeightsBiasesGradient";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Double", "java.lang.Double",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
    return types;
  }
  
  public void doit() throws Exception {
    
    
    // pull in the supplied data...
    MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix) this.pullInput(0);
    MultiFormatMatrix Targets = (MultiFormatMatrix) this.pullInput(1);
    int[][] LayerTable = (int[][])((int[][]) this.pullInput(2)).clone();
    MultiFormatMatrix RawSerializedWeights = (MultiFormatMatrix) this.pullInput(3);
    MultiFormatMatrix RawBiases = (MultiFormatMatrix) this.pullInput(4);
    int[][] LayerStartFinishNeuronTable = (int[][])((int[][]) this.pullInput(5)).clone();
    int[][] NeuronToLayerTable = (int[][])((int[][]) this.pullInput(6)).clone();
    int[][] WeightNumberFromToNeuronTable = (int[][])((int[][]) this.pullInput(7)).clone();
//  double RoundingThreshold = ((Double) this.pullInput(8)).doubleValue();
    boolean CalculateGradientFlag = ((Boolean) this.pullInput(8)).booleanValue();
    boolean CalculateErrorFlag = ((Boolean) this.pullInput(9)).booleanValue();
    boolean ReportAllActivationsFlag = ((Boolean) this.pullInput(10)).booleanValue();
    boolean DoNotReportAllActivationsFlag = !ReportAllActivationsFlag;
    long NumberOfElementsThreshold = ((Long) this.pullInput(11)).longValue();
    
    
    
    // create the local copies...
    MultiFormatMatrix SerializedWeights = new MultiFormatMatrix(1, new long[] {RawSerializedWeights.getDimensions()[0], 1});
    MultiFormatMatrix Biases = new MultiFormatMatrix(1, new long[] {RawBiases.getDimensions()[0],
        RawBiases.getDimensions()[1]});
    
    // initialize the local copies...
    for (long rowIndex = 0; rowIndex < RawSerializedWeights.getDimensions()[0]; rowIndex++){
//    SerializedWeights.setValue(rowIndex,0,RawSerializedWeights.getValue(rowIndex,0));
      SerializedWeights.setValue(new long[]{rowIndex,0},RawSerializedWeights.getValue(new long[] {rowIndex,0}));
    }
    for (long rowIndex = 0; rowIndex < RawBiases.getDimensions()[0]; rowIndex++){
//    Biases.setValue(rowIndex,0,RawBiases.getValue(rowIndex,0));
      Biases.setValue(new long[] {rowIndex,0},RawBiases.getValue(new long[] {rowIndex,0}));
    }
    
    
    
    // determine the proper format
    // NumElements = (Number of Neurons) * (Number of Explanatory Variables)
    // the Number of Neurons is the number of biases present + 1 because of
    // the extra standardized-to-zero output neuron.
    long numElements = (Biases.getDimensions()[0] + 1) * (ExplanatoryVariables.getDimensions()[0]);
    int formatIndex = -1; // initialize
    if (numElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      formatIndex = 1; // Beware the MAGIC NUMBER!!!
    } else { // not small means big, so go out of core; serialized blocks on
      // disk are best
      formatIndex = 3; // Beware the MAGIC NUMBER!!!
    }
    
    ////// pull out some relevant constants
    int nLayers = LayerTable.length;
//  long nLayers = LayerTable.getDimensions()[0];
    int nWeights = WeightNumberFromToNeuronTable.length;
//  long nWeights = (long) WeightNumberFromToNeuronTable.getDimensions()[0];
    long nExamples = ExplanatoryVariables.getDimensions()[0];
    int nInputs = (int)ExplanatoryVariables.getDimensions()[1];
    int nOutputs = LayerTable[nLayers - 1][0];
    if (nOutputs != 1) {
      System.err.println("nOutputs [" + nOutputs + " != 1");
      throw new Exception();
    }
//  long nOutputs = (long) LayerTable.getValue(nLayers - 1, 0);
    int outputFirst = LayerStartFinishNeuronTable[nLayers - 1][0];
//  long OutputFirst = (long) LayerStartFinishNeuronTable.getValue(nLayers - 1, 0);
    int layerIndexFinal = nLayers - 1;
    if (nInputs != LayerTable[0][0]) {
      System.out.println("(nInputs {" + nInputs + "} != LayerTable[0][0]) {"
          + LayerTable[0][0] + "} -> "
          + "number of inputs/explanatory variables does not match " + "number of input neurons.");
      throw new Exception();
    }
    // number of neurons
    int nNeurons = 0;
//  int nNeuronsTemp = 0;
//  Double nNeuronsDoubleTemp = new Double(0.0);
    for (int rowIndex = 0; rowIndex < nLayers; rowIndex++) {
      nNeurons += LayerTable[rowIndex][0];
      /*            nNeuronsDoubleTemp = new Double(LayerTable.getValue(RowIndex, 0));
       nNeurons += nNeuronsDoubleTemp.intValue();
       */
    }
    if (nNeurons != (int)Biases.getDimensions()[0]) {
      System.out.println("nNeurons {" + nNeurons + "} != (int)Biases.getDimensions()[0] {"
          + (int)Biases.getDimensions()[0] + "}");
      throw new Exception();
    }
    
    // i need to build a table that relates the layer to its first incoming weight number...
    // Beware the MAGIC NUMBER!!! i am using format #1 (SDIM) and a single dimensional array
    int[] firstWeightToLayer = new int [nLayers]; // {nLayers};
//  MultiFormatMatrix FirstWeightToLayer = new MultiFormatMatrix(1, new long[] { nLayers });
    
    int FirstWeightNumberTemp = -18;
//  double FirstWeightNumberTemp = -18.3;
    for (int LayerIndex = 1; LayerIndex < nLayers; LayerIndex++) {
      FirstWeightNumberTemp = 0;
      for (int LayerFunnyIndex = 0; LayerFunnyIndex < LayerIndex - 1; LayerFunnyIndex++) {
        FirstWeightNumberTemp += LayerTable[LayerFunnyIndex][0]
                                                             * LayerTable[LayerFunnyIndex + 1][0];
      }
      firstWeightToLayer[LayerIndex] = FirstWeightNumberTemp;
    }
    
    /*
     * number of weights int nWeights = 0; Double nWeightsDoubleTemp = new Double(0.0); Beware the MAGIC NUMBER!!! the "nLayers - 1"
     * gets us to the next to the last element... for (int RowIndex = 0; RowIndex < nLayers - 1; RowIndex++) { nWeightsDoubleTemp = new
     * Double(LayerTable.getValue(RowIndex, 0) * LayerTable.getValue(RowIndex + 1, 0) ); nWeights += nWeightsDoubleTemp.intValue(); }
     */
    
    // ************************begin NEW STUFF....
    // The idea here is to go through one example at a time and do
    // everything...
    /*
     * in defining the NeuronActivations table, we follow a new convention. we want to avoid writing down the inputs every time, so we
     * drop them. that means instead of having as many columns as neurons and using the neuron index as the column index we do it this
     * way. the first column corresponds to the first non-input neuron. thus, the relationship between the "true" neuron index and its
     * column is: NeuronTableIndex = NeuronIndex - nInputs we will need to remember to do this whenever we are reading or writing the
     * NeuronActivations or Deltas...
     */
    
    // DO A PILE OF INITIALIZATIONS...
    // the table of activations...; nNeurons + 1 because of the extra un-notated output neuron
    MultiFormatMatrix neuronActivations = new MultiFormatMatrix(1, new long[] { 0 });
    if (ReportAllActivationsFlag) {
      numElements = nExamples * (nNeurons - nInputs + 1);
      if (numElements < NumberOfElementsThreshold) {
        formatIndex = 1;
      } else {
        formatIndex = 3;
      }
      neuronActivations = new MultiFormatMatrix(formatIndex,
          new long[] { nExamples, nNeurons - nInputs });
    } else {
      neuronActivations = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs });
      // if not reporting all of them, we never use the activation of the standardized output...
    }
    
//  boolean UseComplements[] = new boolean[(int) nNeurons - (int) nInputs + 1];
    // this is a set of flags that says whether we need to use the complements form for the derivative to avoid rounding errors...
    MultiFormatMatrix activationComplements = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs + 1 });
    /*
     * Beware the MAGIC NUMBER!!! and assumptions... i am making this a one dimensional array that is only used and re-used when
     * computing the deltas and derivatives... thus, i am also specifying the format to be #1 SDIM
     */
    //		 again, nNeurons + 1 because of the extra un-notated output neuron
//  MultiFormatMatrix outputSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable[nLayers - 1][0] + 1 });
    /*        MultiFormatMatrix OutputSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable.getValue(
     nLayers - 1, 0) + 1 });
     */        
//  MultiFormatMatrix outputExpedSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable[nLayers - 1][0] + 1 });
    /*        MultiFormatMatrix OutputExpedSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable.getValue(
     nLayers - 1, 0) + 1 });
     */
    // Beware the MAGIC NUMBER!!! these things are in format #1 (SDIM) and also is one dimensional...
    MultiFormatMatrix NNDeltas = new MultiFormatMatrix(1, new long[] { 0 });
    MultiFormatMatrix biasesGradient = new MultiFormatMatrix(1, new long[] { 0 });
    MultiFormatMatrix serializedWeightsGradient = new MultiFormatMatrix(1, new long[] { 0 });
    MultiFormatMatrix stackedWeightsBiasesGradient = new MultiFormatMatrix(1, new long[] { 0 });
    if (CalculateGradientFlag) {
      NNDeltas = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs });
      numElements = (nNeurons);
      if (numElements < NumberOfElementsThreshold) {
        formatIndex = 1;
      } else {
        formatIndex = 3;
      }
      biasesGradient = new MultiFormatMatrix(formatIndex, new long[] { nNeurons, 1 }); // ignoring the extra un-notated
      // output neuron
      numElements = (nWeights);
      if (numElements < NumberOfElementsThreshold) {
        formatIndex = 1;
      } else {
        formatIndex = 3;
      }
      serializedWeightsGradient = new MultiFormatMatrix(formatIndex, new long[] { nWeights, 1 }); // ignoring the extra
      // un-notated output neuron
      stackedWeightsBiasesGradient = new MultiFormatMatrix(1,
          new long[] { nWeights + nNeurons - nInputs, 1 }); // Beware the MAGIC NUMBER!!! SDIM regardless
    }
    
    double unscaledSumSquaredError = 0; // This actually needs to be zero...
    double unscaledSumAbsoluteDeviation = 0; // This actually needs to be zero...
    double minSumAllowed = -Math.log(Double.MAX_VALUE); // This actually needs to be what it is...
//  double fracCorrect = -59.1;
//  boolean errorDoneForThisExample = false;
//  double finalStandardized = -54.1;
//  double alternateStandardized = -55.1;
    double targetThisExample = -5.3;
//  double errorFunction = 0;
    long thisLayerFirst = -51;
//  long ThisLayerLast = -50;
    long previousLayerFirst = -1;
//  long PreviousLayerLast = -2;
    long nNeuronsInPreviousLayer = -3;
    long nNeuronsInThisLayer = -4;
    int firstWeightNumber = -5;
    long neuronUnderConsideration = -6;
    long weightUnderConsideration = -7;
    double previousActivationUnderConsideration = -8.0;
    double expTemp = -3;
    double squashedSum = -9.0;
    double squashedSumComplement = -9.0;
//  double StretchedSum = -10.0;
//  double Denominator = -11.0;
    double sumTemp = -528.3;
//  double finalTemp = -32.52;
//  double emm = -23.51;
//  long emmIndex = -55;
//  double capitalCue = -23.52;
//  double alternateSumTemp = -53.21;
    int myLayer = -1;
    long myLayerFirst = -2;
    long nextLayerFirst = -3;
//  long FirstWeightNumberDeltas = -4;
    double derivativeTemp = -5.0;
    double thisActivation = -6.0;
    double posteriorSum = -7.0;
    long lookupIndex = -58;
    double biasGradientTemp = -1.0;
    double weightsGradientTemp = -2.0;
    long neuronFromLookup = -3;
    long neuronToLookup = -4;
    
//  long nCorrect = 0;
    
    // %%%%%%%%%%% the beginning of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%
    for (long exampleIndex = 0; exampleIndex < nExamples; exampleIndex++) {
      
      ////// pull out the target. will be used much later, but alas, let's do it here.
      if (CalculateGradientFlag || CalculateErrorFlag) {
//      errorDoneForThisExample = false;
        targetThisExample = Targets.getValue(new long[] {exampleIndex, 0});
      }
      
      ////// consider the input neurons: i think i will try to skip them
      // this time and save all that writing...
      
      ////// consider the hidden neurons
      for (int layerIndex = 1; layerIndex < nLayers - 1; layerIndex++) {
        nNeuronsInPreviousLayer = LayerTable[layerIndex - 1][0];
        nNeuronsInThisLayer = LayerTable[layerIndex][0];
        // remember to subtract off the number of inputs whenever
        // you're using a neuron index for storage...
        
        if (layerIndex == 1) {
          thisLayerFirst = 0;
//        System.out.println("LayerTable is [" + LayerTable.length + "][" + LayerTable[0].length + "], LayerIndex = " + LayerIndex);
//        ThisLayerLast = (long) (LayerTable[LayerIndex][1] - nInputs);
//        ThisLayerLast = (LayerTable[LayerIndex][0] - nInputs);
          previousLayerFirst = 0;
//        PreviousLayerLast = nInputs;
        } else {
          thisLayerFirst = (LayerStartFinishNeuronTable[layerIndex][0] - nInputs);
//        ThisLayerLast = (LayerStartFinishNeuronTable[LayerIndex][1] - nInputs);
          previousLayerFirst = (LayerStartFinishNeuronTable[layerIndex - 1][0] - nInputs);
//        PreviousLayerLast = (LayerStartFinishNeuronTable[LayerIndex - 1][1] - nInputs);
        }
        // let us now determine the weight number for the first incoming
        // connection. then we can just do a regular spacing to figure out the others...
        // gonna figure this as a table outright before this big loop...
        
        firstWeightNumber = firstWeightToLayer[layerIndex];
        // this is the weight number for the first connection to this layer...
        //      System.out.println("This layer's first incoming weight number is: " + FirstWeightNumber);
        // do the actual multiplication/summation/activation brute force style...
        for (long WithinIndex = 0; WithinIndex < LayerTable[layerIndex][0]; WithinIndex++) {
          // ThisLayerLast + 1 due to ThisLayerLast being the index...
          neuronUnderConsideration = thisLayerFirst + WithinIndex;
          // ok, here is where the shifting of the indices bites back:
          // the biases are still coded as neuron indices, so we have
          // to add the number of inputs back in...
          sumTemp = Biases.getValue(new long[] {neuronUnderConsideration + nInputs, 0});
          for (long incomingIndex = 0; incomingIndex < nNeuronsInPreviousLayer; incomingIndex++) {
            weightUnderConsideration = firstWeightNumber + WithinIndex + incomingIndex
            * nNeuronsInThisLayer;
            if (layerIndex == 1) {
              previousActivationUnderConsideration = ExplanatoryVariables.getValue(new long[] {exampleIndex,
                  incomingIndex});
            } else {
              if (DoNotReportAllActivationsFlag) {
                previousActivationUnderConsideration = neuronActivations
                .getValue(new long[] {previousLayerFirst + incomingIndex});
              } else {
                previousActivationUnderConsideration = neuronActivations.getValue(new long[] {exampleIndex,
                    previousLayerFirst + incomingIndex});
              }
            }
            sumTemp += previousActivationUnderConsideration
            * SerializedWeights.getValue(new long[] {weightUnderConsideration, 0});
//            System.out.println("example " + exampleIndex + ", neuronUnderConsideration " +
//                neuronUnderConsideration + ", incomingIndex = " + incomingIndex + ", sumTemp = " + sumTemp);
          }
          if (sumTemp < minSumAllowed) {
            sumTemp = minSumAllowed;
            expTemp = Double.MAX_VALUE;
          } else {
            expTemp = java.lang.Math.exp(-sumTemp);
            squashedSum = 1.0 / (1.0 + expTemp);
          }
//          System.out.println("sumTemp = " + sumTemp + " ---max double is: [" + Double.MAX_VALUE + "] its natural log is: " + Math.log(Double.MAX_VALUE));
//          System.out.println("expTemp = " + expTemp );
          if (DoNotReportAllActivationsFlag) {
            neuronActivations.setValue(neuronUnderConsideration, squashedSum);
          } else {
            neuronActivations.setValue(exampleIndex, neuronUnderConsideration, squashedSum);
          }
          squashedSumComplement = expTemp / (1 + expTemp);
          //                        System.out.println("^^^ Example " + ExampleIndex + ": ExpTemp = " + ExpTemp + "; SSC = " +
          // SquashedSumComplement);
          activationComplements.setValue(neuronUnderConsideration, squashedSumComplement);
        }
        
      }
      ////// consider the output neurons: SoftMax activations...
      nNeuronsInPreviousLayer = LayerTable[layerIndexFinal - 1][0];
      nNeuronsInThisLayer = LayerTable[layerIndexFinal][0];
      // remember to subtract off the number of inputs whenever
      // you're using a neuron index for storage...
      thisLayerFirst = (LayerStartFinishNeuronTable[layerIndexFinal][0] - nInputs);
//    ThisLayerLast = (LayerStartFinishNeuronTable[LayerIndexFinal][1] - nInputs);
      previousLayerFirst = (LayerStartFinishNeuronTable[layerIndexFinal - 1][0] - nInputs);
//    PreviousLayerLast = (LayerStartFinishNeuronTable[LayerIndexFinal - 1][1] - nInputs);
      firstWeightNumber = firstWeightToLayer[layerIndexFinal];
      
//    Denominator = 1; // "one" because of the dead output which is always
      // zero: exp(0) = 1
//    emm = 0; // initialized the thing to find the maximum internal activation among the outputs;
//    emmIndex = nOutputs ; // nOutputs - 1 or nOutputs???
      // we start at zero because that would mean the final standardized output is the largest
      
      for (long withinIndex = 0; withinIndex < nNeuronsInThisLayer; withinIndex++) {
        // ThisLayerLast + 1 due to ThisLayerLast being the index...
        
        neuronUnderConsideration = thisLayerFirst + withinIndex;
        //        System.out.println(" ---> NeuronUnderConsideration = " +
        // NeuronUnderConsideration + "; nNeurons = " + nNeurons);
        // ok, here is where the shifting of the indices bites back: the
        // biases are still coded as neuron indices, so we have to add
        // the number of inputs back in...
        sumTemp = Biases.getValue(new long[] {neuronUnderConsideration + nInputs, 0});
        for (long incomingIndex = 0; incomingIndex < nNeuronsInPreviousLayer; incomingIndex++) {
          weightUnderConsideration = firstWeightNumber + withinIndex + incomingIndex
          * nNeuronsInThisLayer;
          if (previousLayerFirst < 0) {
            previousActivationUnderConsideration = ExplanatoryVariables.getValue(new long[] {exampleIndex,
                previousLayerFirst + incomingIndex + nInputs});
          } else {
            if (DoNotReportAllActivationsFlag) {
              previousActivationUnderConsideration = neuronActivations.getValue(new long[] {previousLayerFirst
                  + incomingIndex});
            } else {
              previousActivationUnderConsideration = neuronActivations.getValue(new long[] {exampleIndex,
                  previousLayerFirst + incomingIndex});
            }
          }
          sumTemp += previousActivationUnderConsideration
          * SerializedWeights.getValue(new long[] {weightUnderConsideration, 0});
        }
        /////////////********************/////////////////////
        
        // record the output value in proper place...
        if (DoNotReportAllActivationsFlag) {
          neuronActivations.setValue(neuronUnderConsideration, sumTemp);
        } else {
          neuronActivations.setValue(exampleIndex, neuronUnderConsideration, sumTemp);
        }
        
        // calculate the error and/or delta if need be...
        if (CalculateErrorFlag || CalculateGradientFlag) {
          if (CalculateGradientFlag) {
            // since there is only one output, WithinIndex = 0;
//          NNDeltas.setValue(outputFirst + WithinIndex - nInputs, finalTemp - 1.0);
            NNDeltas.setValue(outputFirst - nInputs, sumTemp - targetThisExample);
            if (Double.isNaN(sumTemp)) {
              System.out.println("Example " + exampleIndex + " demonstrated sumTemp for output as NaN for withinIndex " + withinIndex);
            }
          }
          if (CalculateErrorFlag) {
            unscaledSumSquaredError += (sumTemp - targetThisExample)*(sumTemp - targetThisExample);
            unscaledSumAbsoluteDeviation += Math.abs(sumTemp - targetThisExample);
//          errorDoneForThisExample = true;
          }
          
        }
      }
      
      
      ////// calculate the deltas
      
      // the deltas for the output layer have already been done...
      // now for the hidden layers...
      if (CalculateGradientFlag) {
        for (long NeuronIndex = (outputFirst - 1); NeuronIndex >= nInputs; NeuronIndex--) {
          // NOTICE: skipping the inputs....
          //              System.out.println("NeuronIndex = " + NeuronIndex);
          // going backwards through. hence my non-standard break criterion...
          // find the list of posterior nodes for a particular neuron.
          // that is, what neurons does this one feed into...
          // under the assumption of full connectivity, it will feed into
          // all of the neurons in the next layer...
          // so, i need to know what layer i'm in, and what layer comes after...
          lookupIndex = NeuronIndex - nInputs;
          //                System.out.println("---> NeuronIndex="+NeuronIndex+";
          // nInputs="+nInputs+"; LookupIndex="+LookupIndex);
          myLayer = NeuronToLayerTable[(int)NeuronIndex][0];
          myLayerFirst = (LayerStartFinishNeuronTable[myLayer][0] - nInputs);
          nextLayerFirst = LayerStartFinishNeuronTable[myLayer + 1][0] - nInputs;
          
          if (DoNotReportAllActivationsFlag) {
            thisActivation = neuronActivations.getValue(new long[] {lookupIndex});
          } else {
            thisActivation = neuronActivations.getValue(new long[] {exampleIndex, lookupIndex});
          }
          //                System.out.println("#---> MyLayer="+MyLayer+";
          // MyLayerFirst="+MyLayerFirst+";
          // NextLayerFirst="+NextLayerFirst);
          derivativeTemp = thisActivation * activationComplements.getValue(new long[] {lookupIndex});
          // run through the posterior nodes and their weights. trying to
          // calculate sum [over posterior neurons]
          // delta_posterior*w_j_to_posterior
          
          // find the weight number for the first connection under
          // consideration...
          firstWeightNumber = firstWeightToLayer[myLayer + 1]
                                                 + (((int)lookupIndex - (int)myLayerFirst) * LayerTable[myLayer + 1][0]);
          //                FirstWeightNumber = (long)
          // FirstWeightToLayer.getValue(MyLayer)
          //                        + ((NeuronIndex - MyLayerFirst) * (long)
          // LayerTable.getValue(MyLayer + 1, 0));
          //                FirstWeightNumber = (long)
          // FirstWeightToLayer.getValue(MyLayer)
          //                + ((LookupIndex - MyLayerFirst) * (long)
          // LayerTable.getValue(MyLayer + 1, 0));
          posteriorSum = 0;
          //                System.out.println("
          // ->FirstWeightNumber="+FirstWeightNumber);
          for (long PosteriorIndex = 0; PosteriorIndex < LayerTable[myLayer + 1][0]; PosteriorIndex++) {
            // that is, from 0 to the # of neurons in the next layer (minus one for index's sake)
            //System.out.println("** -> PosteriorIndex="+PosteriorIndex+"; NextLayerFirst="+NextLayerFirst+";
            // FirstWeightNumber="+FirstWeightNumber);
            posteriorSum += (NNDeltas.getValue(new long[] {PosteriorIndex + nextLayerFirst}) * SerializedWeights
                .getValue(new long[] {PosteriorIndex + firstWeightNumber, 0}));
            // i can do this because of the way the weights are encoded. the
            // outgoing weights from a particular neuron are sequential..
          }
          //              System.out.println("ExampleIndex = " + ExampleIndex + ";
          // NeuronIndex = " +
          //                                 NeuronIndex + "; FirstWeightNumber = " + FirstWeightNumber +
          //                                 "; PosteriorSum = " + PosteriorSum + "; DerivativeTemp = " +
          // DerivativeTemp);
          NNDeltas.setValue(lookupIndex, derivativeTemp * posteriorSum);
        }
        
//        for (long deltaIndex = 0; deltaIndex < NNDeltas.getDimensions()[0]; deltaIndex++) {
//          System.out.println("for example " + exampleIndex + ", deltas[" + deltaIndex + "] = " + NNDeltas.getValue(deltaIndex));
//        }
        
        // calculate the gradient
        // figure the gradient for the biases
        for (long NeuronIndex = LayerTable[0][0]; NeuronIndex < nNeurons; NeuronIndex++) {
          biasGradientTemp = biasesGradient.getValue(NeuronIndex, 0);
          lookupIndex = NeuronIndex - nInputs;
          /*                	if (LookupIndex < 0) {
           System.out.println("Example = " + ExampleIndex + "; NeuronIndex = " + NeuronIndex + "; LookupIndex = " + LookupIndex + "; nInputs = " + nInputs +
           "; nNeurons = " + nNeurons + "; LayerTable.getValue(0,0) = " + LayerTable[0][0]);
           }
           */
          biasGradientTemp += NNDeltas.getValue(new long[] {lookupIndex});
          
          if (biasGradientTemp == Double.NaN) {
            System.err.println("Example " + exampleIndex + " demonstrated sumTemp for output as NaN");
          }

          biasesGradient.setValue(NeuronIndex, 0, biasGradientTemp);
          //                System.out.println("NeuronIndex = " + NeuronIndex + "; nInputs = " + nInputs +
          //                                   "; nWeights = " + nWeights);
        }
        // figure the gradient for the weights
        for (int weightIndex = 0; weightIndex < nWeights; weightIndex++) {
          weightsGradientTemp = serializedWeightsGradient.getValue(new long[]{weightIndex, 0});
          
          //                System.out.println("WeightIndex = " + WeightIndex);
          neuronFromLookup = (WeightNumberFromToNeuronTable[weightIndex][0] - nInputs);
          neuronToLookup = (WeightNumberFromToNeuronTable[weightIndex][1] - nInputs);
          if (neuronFromLookup < 0) { // we have a weight originating in an input...
//          System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
            /*                    	if (ExampleIndex < 0) {
             System.out.println("Example = " + ExampleIndex + "; WeightIndex = " + WeightIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
             }
             if (NeuronFromLookup + nInputs < 0) {
             System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
             }
             if (NeuronToLookup < 0) {
             System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
             }
             */
            weightsGradientTemp += (ExplanatoryVariables.getValue(new long[] {exampleIndex, neuronFromLookup
                + nInputs}) * NNDeltas.getValue(new long[] {neuronToLookup}));
          } else { // the weight is *not* originating in an input
            if (DoNotReportAllActivationsFlag) {
              weightsGradientTemp += (neuronActivations.getValue(new long[] {neuronFromLookup}) * NNDeltas
                  .getValue(new long[] {neuronToLookup}));
            } else {
              weightsGradientTemp += (neuronActivations.getValue(new long[] {exampleIndex, neuronFromLookup}) * NNDeltas
                  .getValue(new long[] {neuronToLookup}));
            }
          }
          if (Double.isNaN(weightsGradientTemp)) {
            System.out.println("Example " + exampleIndex + " demonstrated weightsGradientTemp for WeightIndex " + weightIndex);
          }
          serializedWeightsGradient.setValue(weightIndex, 0, weightsGradientTemp);
        }
      } // end of if(CalculateGradientFlag)
      
      /*                for (long NeuronDisplay = 0; NeuronDisplay < NNDeltas.getDimensions()[0]; NeuronDisplay++) {
       
       if (NNDeltas.getValue(NeuronDisplay) == 0.0) {
       System.out.println("for example ("+ExampleIndex+"), NNDeltas["+NeuronDisplay+"] = " + NNDeltas.getValue(NeuronDisplay));
       }
       
       }
       */            
    } // end of examples loop
    //      %%%%%%%%%%% the end of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%
    
//  if (CalculateErrorFlag) {
//  fracCorrect = (double)nCorrect/(double)nExamples;
//  }
    if (CalculateGradientFlag) {
      // now put together the stacked gradient...
      for (long NeuronIndex = LayerTable[0][0]; NeuronIndex < nNeurons; NeuronIndex++) {
        stackedWeightsBiasesGradient.setValue(NeuronIndex - nInputs + nWeights, 0, biasesGradient
            .getValue(NeuronIndex, 0));
      }
      for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
        stackedWeightsBiasesGradient.setValue(WeightIndex, 0, serializedWeightsGradient.getValue(
            WeightIndex, 0));
      }
    }
    
    // ********************** end NEW STUFF...
    
    this.pushOutput(neuronActivations, 0);
    this.pushOutput(new Double(unscaledSumSquaredError), 1);
    this.pushOutput(new Double(unscaledSumAbsoluteDeviation), 2);
    this.pushOutput(biasesGradient,               3);
    this.pushOutput(serializedWeightsGradient,    4);
    this.pushOutput(stackedWeightsBiasesGradient, 5);
    
  }
}