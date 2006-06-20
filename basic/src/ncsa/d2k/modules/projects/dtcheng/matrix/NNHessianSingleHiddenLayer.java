package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class NNHessianSingleHiddenLayer extends ComputeModule {

  public String getModuleName() {
    return "NNHessianSingleHiddenLayer";
  }


  public String getModuleInfo() {
    return "This module calculates the weight and bias Hessian " +
        "for a feedforward network (from the so-called <i>deltas</i>) " +
        "with softmax final activations and " +
        "logistic intermediate activations using a " +
        "cross-entropy error criterion. At the moment, there is very limited, " +
        "that is, non-existent idiot-proofing. <p> This is based on B.D. Ripley " +
        "Pattern Recognition and Neural Networks pp. 151-152. But I do the weights " +
        "and biases separately rather than considering the biases to be a +1 neuron. " +
        "Hence, I calculate the Hessian in three blocks: WW, WB, and BB.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "NNDeltas";
      case 2:
        return "LayerTable";
      case 3:
        return "NeuronToLayerTable";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
        return "WeightNumberFromToNeuronTable";
      case 6:
        return "SerializedWeights";
      case 7:
        return "Biases";
      case 8:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "NNDeltas";
      case 2:
        return "LayerTable";
      case 3:
        return "NeuronToLayerTable";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
        return "WeightNumberFromToNeuronTable";
      case 6:
        return "SerializedWeights";
      case 7:
        return "Biases";
      case 8:
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
        return "Hessian";
      case 1:
        return "aych";
      case 2:
        return "AYCH";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Hessian";
      case 1:
        return "aych";
      case 2:
        return "AYCH";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {
    /*
              case 0:
                return "NeuronActivations";
              case 1:
                return "NNDeltas";
              case 2:
                return "LayerTable";
              case 3:
                return "NeuronToLayerTable";
              case 4:
                return "LayerStartFinishNeuronTable";
              case 5:
                return "WeightNumberFromToNeuronTable";
              case 6:
                return "SerializedWeights";
              case 7:
                return "Biases";
              case 8:
                return "NumberOfElementsThreshold";
     */

    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix NNDeltas = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(2);
    MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix)this.pullInput(3);
    MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix)this.pullInput(4);
    MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix)this.pullInput(5);
    MultiFormatMatrix SerializedWeights = (MultiFormatMatrix)this.pullInput(6);
    MultiFormatMatrix Biases = (MultiFormatMatrix)this.pullInput(7);
    long NumberOfElementsThreshold = ((Long)this.pullInput(8)).longValue();

// some preparations
    long nInputs = (long) LayerTable.getValue(0, 0);
    long nRelevantOutputs = (long) LayerTable.getValue(LayerTable.getDimensions()[0] - 1, 0);
    long nWeights = (long) WeightNumberFromToNeuronTable.getDimensions()[0];
    long nExamples = NeuronActivations.getDimensions()[0];
    long nNeurons = NeuronActivations.getDimensions()[1] - 1;
    long nLayers = (long) LayerTable.getDimensions()[0];
    long nLayersMinusOne = nLayers - 1;
    long FirstOutput = (long) LayerStartFinishNeuronTable.getValue(nLayers - 1, 0);
    long nParameters = nWeights + nNeurons - nInputs; // weights and meaningful biases

    long FirstWeightToOutputLayer = -1234;
    long FirstNeuronInLayerBeforeOutput = -5;

    // Beware the MAGIC ASSUMPTION!!! we are assuming that we are dealing with either
    // zero hidden layers or a single hidden layer. hence, the layer
    if (nLayers == 2) {
      // we are dealing with zero hidden layers so "just prior to output" neurons start immediately
      FirstNeuronInLayerBeforeOutput = 0;
      FirstWeightToOutputLayer = 0;
    }
    else if (nLayers == 3) {
      FirstNeuronInLayerBeforeOutput = (long) LayerTable.getValue(1, 0);
      FirstWeightToOutputLayer = (long) LayerTable.getValue(0, 0) * (long) LayerTable.getValue(1, 0);
    }
    else {
      System.out.println("More than a single hidden layer. This module " +
                         "can only handle zero or one hidden layers.");
      throw new Exception();
    }

// determine the proper format for the Hessian itself
    long NumElements = nParameters * nParameters;
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }
    MultiFormatMatrix Hessian = new MultiFormatMatrix(FormatIndex,
        new long[] {nParameters, nParameters});

// now for the little h: aych
    NumElements = nRelevantOutputs * nRelevantOutputs * nExamples;
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix aych = new MultiFormatMatrix(FormatIndex,
        new long[] {nRelevantOutputs, nRelevantOutputs, nExamples});
    double aychTemp = -1.0;
//    int NeuronFromA = -2; // i
    long NeuronToA = -3; // j
//    int NeuronFromB = -4; // k
    long NeuronToB = -5; // l

    // build the little h: aych
    for (long WeightIndexA = FirstWeightToOutputLayer; WeightIndexA < nWeights; WeightIndexA++) {
      for (long WeightIndexB = WeightIndexA; WeightIndexB < nWeights; WeightIndexB++) {
//        NeuronFromA = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexA, 0);
        NeuronToA = (long) WeightNumberFromToNeuronTable.getValue(WeightIndexA, 1);
//        NeuronFromB = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexB, 0);
        NeuronToB = (long) WeightNumberFromToNeuronTable.getValue(WeightIndexB, 1);

        aychTemp = 0.0;
        for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
          if (WeightIndexA != WeightIndexB) {
            aychTemp = -(NeuronActivations.getValue(ExampleIndex, NeuronToA) *
                         NeuronActivations.getValue(ExampleIndex, NeuronToB));
            aych.setValue(NeuronToA - FirstOutput, NeuronToB - FirstOutput, ExampleIndex, aychTemp);
//            aych.setValue(NeuronToB - FirstOutput, NeuronToA - FirstOutput, ExampleIndex, aychTemp);
          }
          else {
            aychTemp += (NeuronActivations.getValue(ExampleIndex, NeuronToA) -
                         (NeuronActivations.getValue(ExampleIndex, NeuronToA) *
                          NeuronActivations.getValue(ExampleIndex, NeuronToB)));
            aych.setValue(NeuronToA - FirstOutput, NeuronToB - FirstOutput, ExampleIndex, aychTemp);
//            aych.setValue(NeuronToB - FirstOutput, NeuronToA - FirstOutput, ExampleIndex, aychTemp);
          }
        }
      }
    }

    // build the Big H: AYCH; only needed if there is, in fact, a hidden layer
    MultiFormatMatrix AYCH = null;
    long FirstHidden = (long) LayerTable.getValue(0, 0); // Beware the MAGIC NUMBER!!!
    // remember, only a single hidden layer, so it must be the first neuron after the inputs
    long LastHidden = FirstHidden + (long) LayerTable.getValue(1, 0);
    if (nLayers == 3) {

      NumElements = (long) LayerTable.getValue(1, 0) * nExamples; // Beware the MAGIC NUMBER!!! the number of neurons in the hidden layer
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }
      // the idea here is that a row is a hidden unit (shifted by the number of inputs) and a column is an example
      // Beware the MAGIC ASSUMPTION!!! this is the first time we have put "examples" as columns
      AYCH = new MultiFormatMatrix(FormatIndex, new long[] {(long) LayerTable.getValue(1, 0), nExamples}); // Beware the MAGIC NUMBER!!! the number of neurons in the hidden layer
      long WeightIndexFromHereToOutputUnderConsideration = -12;
      long FirstOutgoingWeightNumberForThisHiddenNeuron = -11;
      long WithinHiddenIndex = -13;
      long WithinOutputIndex = -14;

      long FirstOutgoingWeightNumberInHiddenLayer = (long) LayerTable.getValue(0, 0) * (long) LayerTable.getValue(1, 0);

      for (long NeuronInHiddenLayer = FirstHidden; NeuronInHiddenLayer < LastHidden; NeuronInHiddenLayer++) {
        WithinHiddenIndex = NeuronInHiddenLayer - FirstHidden;
        FirstOutgoingWeightNumberForThisHiddenNeuron = (FirstOutgoingWeightNumberInHiddenLayer +
            WithinHiddenIndex * nRelevantOutputs);
        for (long NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
          WithinOutputIndex = NeuronInOutputLayer - FirstOutput;
          WeightIndexFromHereToOutputUnderConsideration = FirstOutgoingWeightNumberForThisHiddenNeuron + WithinOutputIndex;
          for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
            AYCH.setValue(WithinHiddenIndex, ExampleIndex,
                          (SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsideration, 0) *
                           NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer)));
          }
        }
//        System.out.println("NeuronInHiddenLayer = " + NeuronInHiddenLayer + "; WithinHiddenIndex = " + WithinHiddenIndex + "; Hrows = " + AYCH.getDimensions()[0] + "; Hcols = " + AYCH.getDimensions()[1]);
      }
    }

// the big nasty loop
    // over all the weights
    long NeuronFromRow = -22;
    long NeuronToRow = -23;
    long NeuronFromCol = -24;
    long NeuronToCol = -25;
    long NeuronToRowLayer = -26;
    long NeuronToColLayer = -27;
    double HessianTemp = -28.0;
    double DeltaSum = -29.0;
    double WWPSum = -30.0;
    double AychJ = -31.0;
    double AychL = -32.0;
    double WeightJTemp = -32.5;
    long WeightIndexFromHereToOutputUnderConsideration = -12;
    long FirstOutgoingWeightNumberForThisHiddenNeuron = -11;
    long WithinHiddenIndex = -13;
    long WithinOutputIndex = -14;
    long FirstOutgoingWeightNumberInHiddenLayer = (long) LayerTable.getValue(0, 0) * (long) LayerTable.getValue(1, 0);
    long WithinHiddenIndexJ = -15;
    long WithinHiddenIndexL = -16;
    long FirstOutgoingWeightNumberForThisHiddenNeuronJ = -17;
    long FirstOutgoingWeightNumberForThisHiddenNeuronL = -18;
    long WeightIndexFromHereToOutputUnderConsiderationJ = -19;
    long WeightIndexFromHereToOutputUnderConsiderationL = -20;
    double effJ = -40.0;
    double OneMinuseffJ = -40.5;
    double effL = -41.0;
    double OneMinuseffL = -41.5;
    double aychSum = -42.0;

// pure weights stuff...
    for (long WeightRow = 0; WeightRow < nWeights; WeightRow++) {
      for (long WeightCol = WeightRow; WeightCol < nWeights; WeightCol++) {
        NeuronFromRow = (long) WeightNumberFromToNeuronTable.getValue(WeightRow, 0); // i
        NeuronToRow = (long) WeightNumberFromToNeuronTable.getValue(WeightRow, 1); // j
        NeuronFromCol = (long) WeightNumberFromToNeuronTable.getValue(WeightCol, 0); // k
        NeuronToCol = (long) WeightNumberFromToNeuronTable.getValue(WeightCol, 1); // l
        // now we determine which layers the "to" neurons are in...
        NeuronToRowLayer = (long) NeuronToLayerTable.getValue(NeuronFromRow, 0);
        NeuronToColLayer = (long) NeuronToLayerTable.getValue(NeuronFromCol, 0);
        // now for the different cases

        // both output neurons
        // the conditional is set up with "nLayers" so that it will also work with zero hidden layers
        // networks.
        if (NeuronToRowLayer == nLayers & NeuronToColLayer == nLayers) {
          HessianTemp = 0;
          for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
            HessianTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromRow) *
                            NeuronActivations.getValue(ExampleIndex, NeuronFromCol) *
                            aych.getValue(NeuronToRow, NeuronToCol, ExampleIndex));
          }
          Hessian.setValue(WeightRow, WeightCol, HessianTemp);
          Hessian.setValue(WeightCol, WeightRow, HessianTemp); // that whole symmetry bit...
          System.out.println("NeuronToRow = " + NeuronToRow + "; NeuronToCol = " +
                             NeuronToCol + "; HessianTemp = " + HessianTemp);
        } // end of both output neurons

        // j hidden, l output; that is NeuronFromRow is hidden and NeuronToCol is output
        else if (NeuronToRowLayer == nLayersMinusOne & NeuronToColLayer == nLayers) {
          HessianTemp = 0;
          for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
            if (NeuronToRow == NeuronFromCol) {
              aychSum = 0;
              WithinHiddenIndex = NeuronToRow - FirstHidden;
              FirstOutgoingWeightNumberForThisHiddenNeuron = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndex * nRelevantOutputs);
              for (long NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
                WithinOutputIndex = NeuronInOutputLayer - FirstOutput;
                WeightIndexFromHereToOutputUnderConsideration = FirstOutgoingWeightNumberForThisHiddenNeuron + WithinOutputIndex;
                aychSum += (SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsideration, 0) *
                            aych.getValue(NeuronToRow, NeuronInOutputLayer, ExampleIndex));
              }
              HessianTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromRow) *
                              (NeuronActivations.getValue(ExampleIndex, NeuronToRow) *
                               (1.0 - NeuronActivations.getValue(ExampleIndex, NeuronToRow)) *
                               (NNDeltas.getValue(ExampleIndex, NeuronToCol)
                                +
                                NeuronActivations.getValue(ExampleIndex, NeuronFromCol) * aychSum
                                )
                               )
                              );
            }
            else {
              aychSum = 0;
              WithinHiddenIndex = NeuronToRow - FirstHidden;
              FirstOutgoingWeightNumberForThisHiddenNeuron = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndex * nRelevantOutputs);
              for (long NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
                WithinOutputIndex = NeuronInOutputLayer - FirstOutput;
                WeightIndexFromHereToOutputUnderConsideration = FirstOutgoingWeightNumberForThisHiddenNeuron + WithinOutputIndex;
                System.out.println("NeuronToRow = " + NeuronToRow + "; NeuronInOutputLayer = " + NeuronInOutputLayer + "; ExampleIndex = " + ExampleIndex +
                                   "; WeightIndexFromHereToOutputUnderConsideration = " + WeightIndexFromHereToOutputUnderConsideration);
                aychSum += (SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsideration, 0) *
                            aych.getValue(NeuronToRow, NeuronInOutputLayer, ExampleIndex));
              }
              HessianTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromRow) *
                              (NeuronActivations.getValue(ExampleIndex, NeuronToRow) *
                               (1.0 - NeuronActivations.getValue(ExampleIndex, NeuronToRow)) *
                               NeuronActivations.getValue(ExampleIndex, NeuronFromCol) * aychSum
                               )
                              );
            }
          }
          Hessian.setValue(NeuronToRow, NeuronToCol, HessianTemp);
          Hessian.setValue(NeuronToCol, NeuronToRow, HessianTemp); // live for symmetry
          System.out.println("NeuronToRow = " + NeuronToRow + "; NeuronToCol = " +
                             NeuronToCol + "; HessianTemp = " + HessianTemp);
        } // end mixed hidden and output

        // both hidden
        else if (NeuronToRowLayer == nLayersMinusOne & NeuronToColLayer == nLayersMinusOne) {
          if (NeuronToRow == NeuronToCol) {
            HessianTemp = 0.0;
            for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
              DeltaSum = 0.0;
              WWPSum = 0.0;
              AychJ = 0.0;
              AychL = 0.0;

              WithinHiddenIndexJ = NeuronToRow - FirstHidden;
              WithinHiddenIndexL = NeuronToCol - FirstHidden;
              FirstOutgoingWeightNumberForThisHiddenNeuronJ = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndexJ * nRelevantOutputs);
              FirstOutgoingWeightNumberForThisHiddenNeuronL = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndexL * nRelevantOutputs);
              for (long NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
                WithinOutputIndex = NeuronInOutputLayer - FirstOutput;
                WeightIndexFromHereToOutputUnderConsiderationJ = FirstOutgoingWeightNumberForThisHiddenNeuronJ + WithinOutputIndex;
                WeightIndexFromHereToOutputUnderConsiderationL = FirstOutgoingWeightNumberForThisHiddenNeuronL + WithinOutputIndex;

                WeightJTemp = SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationJ, 0);

                DeltaSum += (WeightJTemp *
                             NNDeltas.getValue(ExampleIndex, NeuronInOutputLayer));
                WWPSum += (WeightJTemp *
                           SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationL, 0) *
                           NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer));
                AychJ += (WeightJTemp *
                          NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer));
                // don't have to calculate AychL because J=L... and that's why we are doing the DeltaSum
//                AychL += (SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationL,0) *
//                          NeuronActivations.getValue(ExampleIndex,NeuronInOutputLayer));
                AychL = AychJ;
              }
              //        System.out.println("NeuronInHiddenLayer = " + NeuronInHiddenLayer + "; WithinHiddenIndex = " + WithinHiddenIndex + "; Hrows = " + AYCH.getDimensions()[0] + "; Hcols = " + AYCH.getDimensions()[1]);
              effJ = NeuronActivations.getValue(ExampleIndex, NeuronToRow);
              OneMinuseffJ = 1.0 - effJ;
              // no need to look up effL since J = L
//              effL = NeuronActivations.getValue(ExampleIndex,NeuronToCol);
//              OneMinuseffL = 1.0 - effL;
              effL = effJ;
              OneMinuseffL = OneMinuseffJ;

              HessianTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromRow) *
                              NeuronActivations.getValue(ExampleIndex, NeuronFromCol) *
                              (OneMinuseffJ * (effJ * OneMinuseffJ - effJ * effJ) * DeltaSum +
                               (effJ * OneMinuseffJ) * (effL * OneMinuseffL) *
                               (WWPSum - AychJ * AychL
                                )
                               )
                              );
            }
            Hessian.setValue(NeuronToRow, NeuronToCol, HessianTemp);
            System.out.println("NeuronToRow = " + NeuronToRow + "; NeuronToCol = " +
                               NeuronToCol + "; HessianTemp = " + HessianTemp);
          }
          else { // J and L are not the same...
            HessianTemp = 0.0;
            for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
              WWPSum = 0.0;
              AychJ = 0.0;
              AychL = 0.0;

              WithinHiddenIndexJ = NeuronToRow - FirstHidden;
              WithinHiddenIndexL = NeuronToCol - FirstHidden;
              FirstOutgoingWeightNumberForThisHiddenNeuronJ = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndexJ * nRelevantOutputs);
              FirstOutgoingWeightNumberForThisHiddenNeuronL = (FirstOutgoingWeightNumberInHiddenLayer +
                  WithinHiddenIndexL * nRelevantOutputs);
              for (long NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
                WithinOutputIndex = NeuronInOutputLayer - FirstOutput;
                WeightIndexFromHereToOutputUnderConsiderationJ = FirstOutgoingWeightNumberForThisHiddenNeuronJ + WithinOutputIndex;
                WeightIndexFromHereToOutputUnderConsiderationL = FirstOutgoingWeightNumberForThisHiddenNeuronL + WithinOutputIndex;

                WeightJTemp = SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationJ, 0);

                WWPSum += (WeightJTemp *
                           SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationL, 0) *
                           NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer));
                AychJ += (WeightJTemp *
                          NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer));
                AychL += (SerializedWeights.getValue(WeightIndexFromHereToOutputUnderConsiderationL, 0) *
                          NeuronActivations.getValue(ExampleIndex, NeuronInOutputLayer));
              }
              //        System.out.println("NeuronInHiddenLayer = " + NeuronInHiddenLayer + "; WithinHiddenIndex = " + WithinHiddenIndex + "; Hrows = " + AYCH.getDimensions()[0] + "; Hcols = " + AYCH.getDimensions()[1]);
              effJ = NeuronActivations.getValue(ExampleIndex, NeuronToRow);
              OneMinuseffJ = 1.0 - effJ;
              effL = NeuronActivations.getValue(ExampleIndex, NeuronToCol);
              OneMinuseffL = 1.0 - effL;

              HessianTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromRow) *
                              NeuronActivations.getValue(ExampleIndex, NeuronFromCol) *
                              (effJ * OneMinuseffJ) * (effL * OneMinuseffL) *
                              (WWPSum - AychJ * AychL
                               )
                              );
            }
            Hessian.setValue(NeuronToRow, NeuronToCol, HessianTemp);
            System.out.println("NeuronToRow = " + NeuronToRow + "; NeuronToCol = " +
                               NeuronToCol + "; HessianTemp = " + HessianTemp);
          }
        } // end both hidden

      }
    }

    this.pushOutput(Hessian, 0);
    this.pushOutput(aych, 1);
    this.pushOutput(AYCH, 2);
  }

}
