package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class NNSingleHiddenJacobian extends ComputeModule {

  public String getModuleName() {
    return "NNSingleHiddenJacobian";
  }


  public String getModuleInfo() {
    return "This module calculates the Jacobian of the transformation " +
        "represented by a <i><b>SINGLE HIDDEN LAYER</b></i> neural net." +
        "Specifically, " +
        "for a feedforward network with softmax final activations and " +
        "logistic intermediate activations. At the moment, there is very limited, " +
        "that is, non-existent idiot-proofing.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "LayerTable";
      case 2:
        return "SerializedWeights";
      case 3:
        return "LayerStartFinishNeuronTable";
      case 4:
        return "NeuronToLayerTable";
      case 5:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations: at this stage, it should be a single row; " +
            "perhaps later we will extend it to deal with multiple rows...";
      case 1:
        return "LayerTable";
      case 2:
        return "SerializedWeights";
      case 3:
        return "LayerStartFinishNeuronTable";
      case 4:
        return "NeuronToLayerTable";
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
        return "Jacobian";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Jacobian";
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
    /*
               case 0:
                return "NeuronActivations";
              case 1:
                return "LayerTable";
              case 2:
                return "SerializedWeights";
              case 3:
                return "LayerStartFinishNeuronTable";
              case 4:
                return "NeuronToLayerTable";
              case 5:
                return "NumberOfElementsThreshold";
     */

    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix SerializedWeights = (MultiFormatMatrix)this.pullInput(2);
    MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix)this.pullInput(3);
    MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix)this.pullInput(4);
    long NumberOfElementsThreshold = ((Long)this.pullInput(5)).longValue();

// determine the proper format
    long nInputs = (long) LayerTable.getValue(0, 0);
    long nOutputs = (long) LayerTable.getValue(2, 0); // Beware the MAGIC NUMBER!!! assuming a single hidden layer.
    // also, nOutputs is the number of RELEVANT OUTPUTS, there is an extra one due to standardization...
    long OutputFirst = (long) LayerStartFinishNeuronTable.getValue(2, 0);
    long NumElements = nInputs * (nOutputs + 1);
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

// pull out some relevant constants
    long nWeights = SerializedWeights.getDimensions()[0];

// begin to do the deeds
    // initializing constants...
    long MyLayer = -1;
    long MyLayerFirst = -2;
    long NextLayerFirst = -3;
    long FirstWeightNumber = -4;

    // find the index of the first output
    MultiFormatMatrix Jacobian = new MultiFormatMatrix(FormatIndex, new long[] {nOutputs, nInputs});

// make a dealy that will find the weight number between two arbitrary neurons
// this will assume a single hidden layer
// not idiot-proofed in the least

    long NeuronFrom; // = 0;
    long NeuronTo; // = 5;

    long LayerFrom; // = (long)NeuronToLayerTable.getValue(NeuronFrom,0);
    long LayerTo; // = (long)NeuronToLayerTable.getValue(NeuronTo,0);

    long FirstInLayerFrom; // = (long)LayerStartFinishNeuronTable.getValue(LayerFrom,0);
    long FirstInLayerTo; // = (long)LayerStartFinishNeuronTable.getValue(LayerTo,0);

    long WithinIndexFrom; // = NeuronFrom - FirstInLayerFrom;
    long WithinIndexTo; // = NeuronTo - FirstInLayerTo;

    long nWeightsBeforeAllPreviousLayers = -5;
    long nWeightsBeforeInFromLayer = -6;
    long WeightNumber = -7;

    /*
        // input to hidden case
        if (LayerFrom == 0 & LayerTo == 1) {
          nWeightsBeforeAllPreviousLayers = 0;
          nWeightsBeforeInFromLayer = WithinIndexFrom * (long)LayerTable.getValue(1,0);
//      nWeightsBeforeInFromLayer = WithinIndexFrom * (long)LayerTable.getValue(LayerTo,0);
          WeightNumber = nWeightsBeforeAllPreviousLayers + nWeightsBeforeInFromLayer + WithinIndexTo;
        }
        else if (LayerFrom == 1 & LayerTo == 2) {
          nWeightsBeforeAllPreviousLayers = (long)(LayerTable.getValue(0,0)*LayerTable.getValue(1,0));
//      nWeightsBeforeAllPreviousLayers = (long)(LayerTable.getValue(LayerFrom - 1,0)*LayerTable.getValue(LayerTo - 1,0));
//      nWeightsBeforeInFromLayer = WithinIndexFrom * (long)LayerTable.getValue(LayerTo,0);
          nWeightsBeforeInFromLayer = WithinIndexFrom * (long)LayerTable.getValue(2,0);
          WeightNumber = nWeightsBeforeAllPreviousLayers + nWeightsBeforeInFromLayer + WithinIndexTo;
        }

     */

    double BigDerivative = -1.0;
    double SumTemp = -2.0;
    long OutputNeuronIndex = -3;
    long WeightIndexInputToHidden = -4;
    long WeightIndexHiddenToOutput = -5;

// ok, here will be the real loop...
    // Beware the MAGIC NUMBER!!! we are *definately* assuming a single hidden layer, so the bounds are set up that way...
    long ExampleIndex = 0; // this is the row of the NeuronActivations to use. potentially, the could be used in the future to handle more than one row
    for (long OutputIndex = 0; OutputIndex < nOutputs; OutputIndex++) {
      OutputNeuronIndex = OutputIndex + OutputFirst;
      BigDerivative = (NeuronActivations.getValue(ExampleIndex, OutputNeuronIndex) *
                       (1.0 - NeuronActivations.getValue(ExampleIndex, OutputNeuronIndex))
                       );
      SumTemp = 0.0;
      for (long InputIndex = 0; InputIndex < nInputs; InputIndex++) {
        for (long HiddenNeuron = (long) LayerStartFinishNeuronTable.getValue(1, 0);
             HiddenNeuron < (long) LayerStartFinishNeuronTable.getValue(2, 0);
             HiddenNeuron++) {
          // find weight number Input -> Hidden
          {
            NeuronFrom = InputIndex;
            NeuronTo = HiddenNeuron;
            LayerFrom = (long) NeuronToLayerTable.getValue(NeuronFrom, 0);
            LayerTo = (long) NeuronToLayerTable.getValue(NeuronTo, 0);
            FirstInLayerFrom = (long) LayerStartFinishNeuronTable.getValue(LayerFrom, 0);
            FirstInLayerTo = (long) LayerStartFinishNeuronTable.getValue(LayerTo, 0);
            WithinIndexFrom = NeuronFrom - FirstInLayerFrom;
            WithinIndexTo = NeuronTo - FirstInLayerTo;
            // input to hidden case
            nWeightsBeforeAllPreviousLayers = 0;
            nWeightsBeforeInFromLayer = WithinIndexFrom * (long) LayerTable.getValue(1, 0);
            //      nWeightsBeforeInFromLayer = WithinIndexFrom * (long)LayerTable.getValue(LayerTo,0);
          }
          WeightIndexInputToHidden = nWeightsBeforeAllPreviousLayers + nWeightsBeforeInFromLayer + WithinIndexTo;

          // find weight number Hidden -> Output
          {
            NeuronFrom = HiddenNeuron;
            NeuronTo = OutputNeuronIndex;
            LayerFrom = (long) NeuronToLayerTable.getValue(NeuronFrom, 0);
            LayerTo = (long) NeuronToLayerTable.getValue(NeuronTo, 0);
            FirstInLayerFrom = (long) LayerStartFinishNeuronTable.getValue(LayerFrom, 0);
            FirstInLayerTo = (long) LayerStartFinishNeuronTable.getValue(LayerTo, 0);
            WithinIndexFrom = NeuronFrom - FirstInLayerFrom;
            WithinIndexTo = NeuronTo - FirstInLayerTo;
            nWeightsBeforeAllPreviousLayers = (long) (LayerTable.getValue(0, 0) * LayerTable.getValue(1, 0));
            nWeightsBeforeInFromLayer = WithinIndexFrom * (long) LayerTable.getValue(2, 0);
          }
          WeightIndexHiddenToOutput = nWeightsBeforeAllPreviousLayers + nWeightsBeforeInFromLayer + WithinIndexTo;

          SumTemp += (SerializedWeights.getValue(WeightIndexInputToHidden, 0) *
                      SerializedWeights.getValue(WeightIndexHiddenToOutput, 0) *
                      NeuronActivations.getValue(ExampleIndex, HiddenNeuron) *
                      (1.0 - NeuronActivations.getValue(ExampleIndex, HiddenNeuron))
                      );
        }
        Jacobian.setValue(OutputIndex,InputIndex,BigDerivative*SumTemp);
        SumTemp = 0;
      }
    }

    this.pushOutput(Jacobian, 0);
  }
}


