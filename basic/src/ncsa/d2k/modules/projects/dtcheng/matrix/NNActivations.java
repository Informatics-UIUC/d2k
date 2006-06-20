package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNActivations
    extends ComputeModule {

  public String getModuleName() {
    return "NNActivations";
  }

  public String getModuleInfo() {
    return "This module actually calculates the activations (so-called " +
        "forward propagation through the network) for each neuron based " +
        "on some supplied explanatory variables and the designated " +
        "parameters. Each row corresponds to an example and each column " +
        "to a neuron.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExplanatoryVariables";
      case 1:
        return "LayerTable";
      case 2:
        return "SerializedWeights";
      case 3:
        return "Biases";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
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
        return "LayerTable";
      case 2:
        return "SerializedWeights";
      case 3:
        return "Biases";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix SerializedWeights = (MultiFormatMatrix)this.pullInput(2);
    MultiFormatMatrix Biases = (MultiFormatMatrix)this.pullInput(3);
    MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix)this.pullInput(4);
//    MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix)this.pullInput(5);
    long NumberOfElementsThreshold = ((Long)this.pullInput(5)).longValue();

// determine the proper format
    // NumElements = (Number of Neurons) * (Number of Explanatory Variables)
    // the Number of Neurons is the number of biases present + 1 because of the
    // extra standardized-to-zero output neuron.
    long NumElements = (Biases.getDimensions()[0] + 1) *
        (ExplanatoryVariables.getDimensions()[0]);
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

// pull out some relevant constants
    long nExamples = ExplanatoryVariables.getDimensions()[0];
    long nXs = ExplanatoryVariables.getDimensions()[1];
//    System.out.println("(nXs {" + nXs + "} AND (int)LayerTable.getValue(0,0)) {" +
//                       (int)LayerTable.getValue(0,0) + "} -> ");
    if (nXs != (int)LayerTable.getValue(0,0)) {
      System.out.println("(nXs {" + nXs + "} != (int)LayerTable.getValue(0,0)) {" +
                         (int)LayerTable.getValue(0,0) + "} -> " +
                         "number of explanatory variables does not match " +
                         "number of input neurons.");
      throw new Exception();
    }
    long nLayers = LayerTable.getDimensions()[0];
    // number of neurons
    long nNeurons = 0;
    Double nNeuronsDoubleTemp = new Double(0.0);
    for (long RowIndex = 0; RowIndex < nLayers; RowIndex++) {
      nNeuronsDoubleTemp = new Double(LayerTable.getValue(RowIndex, 0));
      nNeurons += nNeuronsDoubleTemp.intValue();
    }
    if (nNeurons != Biases.getDimensions()[0]) {
      System.out.println("nNeurons {" + nNeurons +
                         "} != Biases.getDimensions()[0] {" +
                         Biases.getDimensions()[0] + "}");
      throw new Exception();
    }
/*
    // number of weights
    int nWeights = 0;
    Double nWeightsDoubleTemp = new Double(0.0);
    // Beware the MAGIC NUMBER!!! the "nLayers - 1" gets us to
    // the next to the last element...
    for (int RowIndex = 0; RowIndex < nLayers - 1; RowIndex++) {
      nWeightsDoubleTemp = new Double(LayerTable.getValue(RowIndex, 0) *
                                      LayerTable.getValue(RowIndex + 1, 0)
                                      );
      nWeights += nWeightsDoubleTemp.intValue();
    }
*/

// begin to do the deeds

    MultiFormatMatrix NeuronActivations = new MultiFormatMatrix(FormatIndex,
        new long[] {nExamples, nNeurons + 1}); // nNeurons + 1 because of the extra un-notated output neuron
    long ThisLayerFirst = 0;
    long ThisLayerLast = (long)LayerTable.getValue(0,0);

    // activate the input neurons
    for (long NeuronIndex = ThisLayerFirst; NeuronIndex < ThisLayerLast; NeuronIndex++) {
      for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
//        System.out.println("ExampleIndex = " + ExampleIndex + "; NeuronIndex = " + NeuronIndex);
        NeuronActivations.setValue(ExampleIndex, NeuronIndex,
                                   ExplanatoryVariables.getValue(ExampleIndex,NeuronIndex));
      }
    }

    // activate the subsequent hidden layers
    // nLayers - 1 because we'll do the final layer separately
    long PreviousLayerFirst = -1;
//    long PreviousLayerLast = -2;
    long nNeuronsInPreviousLayer = -3;
    long nNeuronsInThisLayer = -4;
    long FirstWeightNumber = -5;
    long NeuronUnderConsideration = -6;
    long WeightUnderConsideration = -7;
    double PreviousActivationUnderConsideration = -8.0;
    double SquashedSum = -9.0;

    for (long LayerIndex = 1; LayerIndex < nLayers - 1; LayerIndex ++) {
      nNeuronsInPreviousLayer = (long)LayerTable.getValue(LayerIndex - 1,0);
      nNeuronsInThisLayer = (long)LayerTable.getValue(LayerIndex,0);
      ThisLayerFirst = (long)LayerStartFinishNeuronTable.getValue(LayerIndex,0);
      ThisLayerLast = (long)LayerStartFinishNeuronTable.getValue(LayerIndex,1);
      PreviousLayerFirst = (long)LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 0);
//      PreviousLayerLast = (long)LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 1);
      // let us now determine the weight number for the first incoming connection. then
      // we can just do a regular spacing to figure out the others...
      FirstWeightNumber = 0; // this is the weight number for the first connection to this layer...
      for (long LayerFunnyIndex = 0; LayerFunnyIndex < LayerIndex - 1; LayerFunnyIndex++){
        FirstWeightNumber += (long)LayerTable.getValue(LayerFunnyIndex,0) *
            (long)LayerTable.getValue(LayerFunnyIndex+1,0);
      }
//      System.out.println("This layer's first incoming weight number is: " + FirstWeightNumber);
      // do the actual multiplication/summation/activation brute force style...
      double SumTemp = 0;
      for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
        for (long WithinIndex = 0; WithinIndex < (long)LayerTable.getValue(LayerIndex,0); WithinIndex++) {
          // ThisLayerLast + 1 due to ThisLayerLast being the index...
          NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
          SumTemp = Biases.getValue(NeuronUnderConsideration, 0);
          for (long IncomingIndex = 0; IncomingIndex < nNeuronsInPreviousLayer; IncomingIndex++) {
//          WeightUnderConsideration = FirstWeightNumber + nNeuronsInThisLayer*IncomingIndex +
//              (nNeuronsInPreviousLayer - 1)*WithinIndex;
          WeightUnderConsideration = FirstWeightNumber + WithinIndex + IncomingIndex*nNeuronsInThisLayer;

            PreviousActivationUnderConsideration =
                NeuronActivations.getValue(ExampleIndex, PreviousLayerFirst + IncomingIndex);
            SumTemp += PreviousActivationUnderConsideration *
                SerializedWeights.getValue(WeightUnderConsideration, 0);
          }
          SquashedSum = 1.0/(1.0 + java.lang.Math.exp(-SumTemp));
          NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration, SquashedSum);
        }
      }
    }


    // activate the output layer (SoftMax activations...)
    long LayerIndex = nLayers - 1; // just define it straight up and copy/paste

    double StretchedSum = -10.0;
    double Denominator = -11.0;
    MultiFormatMatrix OutputExpedSums = new MultiFormatMatrix(FormatIndex,
        new long[] {1, (long)LayerTable.getValue(LayerIndex,0) + 1});

    nNeuronsInPreviousLayer = (long)LayerTable.getValue(LayerIndex - 1,0);
    nNeuronsInThisLayer = (long)LayerTable.getValue(LayerIndex,0);
    ThisLayerFirst = (long)LayerStartFinishNeuronTable.getValue(LayerIndex,0);
    ThisLayerLast = (long)LayerStartFinishNeuronTable.getValue(LayerIndex,1);
    PreviousLayerFirst = (long)LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 0);
//    PreviousLayerLast = (long)LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 1);
    // let us now determine the weight number for the first incoming connection. then
    // we can just do a regular spacing to figure out the others...
    FirstWeightNumber = 0; // this is the weight number for the first connection to this layer...
    for (long LayerFunnyIndex = 0; LayerFunnyIndex < LayerIndex - 1; LayerFunnyIndex++){
      FirstWeightNumber += (long)LayerTable.getValue(LayerFunnyIndex,0) *
          (long)LayerTable.getValue(LayerFunnyIndex+1,0);
    }
//    System.out.println("This layer's first incoming weight number is: " + FirstWeightNumber);
    // do the actual multiplication/summation/activation brute force style...
    double SumTemp = 0;
    for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
      Denominator = 1; // "one" because of the dead output which is permanently set to "zero", hence exp(0) = 1...
      for (long WithinIndex = 0; WithinIndex < (long)LayerTable.getValue(LayerIndex,0); WithinIndex++) {
        // ThisLayerLast + 1 due to ThisLayerLast being the index...
        NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
//        System.out.println("  ---> NeuronUnderConsideration = " + NeuronUnderConsideration + "; nNeurons = " + nNeurons);
        SumTemp = Biases.getValue(NeuronUnderConsideration, 0);
        for (long IncomingIndex = 0; IncomingIndex < nNeuronsInPreviousLayer; IncomingIndex++) {
//          WeightUnderConsideration = FirstWeightNumber + nNeuronsInThisLayer*IncomingIndex +
//              (nNeuronsInPreviousLayer - 1)*WithinIndex;
          WeightUnderConsideration = FirstWeightNumber + WithinIndex + IncomingIndex*nNeuronsInThisLayer;
          PreviousActivationUnderConsideration =
              NeuronActivations.getValue(ExampleIndex, PreviousLayerFirst + IncomingIndex);
//          System.out.println("   ExampleIndex = " + ExampleIndex + "; WithinIndex = " + WithinIndex + "; IncomingIndex = " + IncomingIndex + "; WeightUnderConsideration = " + WeightUnderConsideration + "; Value = " + SerializedWeights.getValue(WeightUnderConsideration, 0));
          SumTemp += PreviousActivationUnderConsideration *
              SerializedWeights.getValue(WeightUnderConsideration, 0);
        }
        StretchedSum = java.lang.Math.exp(SumTemp);
//        System.out.println("SumTemp = " + SumTemp + "; StretchedSum = " + StretchedSum);
        OutputExpedSums.setValue(0, WithinIndex, StretchedSum); // record the numerator
        Denominator += StretchedSum; // accumulate the denominator
      }
      // go back through and do the division
      for (long WithinIndex = 0; WithinIndex < (long)LayerTable.getValue(LayerIndex,0); WithinIndex++) {
        NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
        NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration,
                                   OutputExpedSums.getValue(0,WithinIndex)/Denominator);
      }
      // do the final "permanently-zeroed" neuron
      NeuronActivations.setValue(ExampleIndex, nNeurons, 1.0/Denominator);
    }

    this.pushOutput(NeuronActivations, 0);
  }

}
