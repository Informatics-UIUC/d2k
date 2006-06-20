package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNActivationsCondensed extends ComputeModule {

    public String getModuleName() {
        return "NNActivationsCondensed";
    }

    public String getModuleInfo() {
        return "This module actually calculates the activations (so-called "
                + "forward propagation through the network) for each neuron based "
                + "on some supplied explanatory variables and the designated "
                + "parameters. Each row corresponds to an example and each column " + "to a neuron." + "<p>"
                + "Further, we go beyond that in order to"
                + "try to speed things up and condense the gradient into here as well.";
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
            return "NumberOfElementsThreshold";
        default:
            return "Error!  No such input.  ";
        }
    }

    public String[] getInputTypes() {
        String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Long", };
        return types;
    }

    public String getOutputName(int i) {
        switch (i) {
        case 0:
            return "NeuronActivations";
        case 1:
            return "NNDeltas";
        case 2:
            return "BiasesGradient";
        case 3:
            return "SerializedWeightsGradient";
        case 4:
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
            return "NNDeltas";
        case 2:
            return "BiasesGradient";
        case 3:
            return "SerializedWeightsGradient";
        case 4:
            return "StackedWeightsBiasesGradient: This is a single column "
                    + "which has the weights gradient first followed by the "
                    + "<i>relevant</i> biases. aka: skipping the input neurons "
                    + "since they don't really have a bias. For use in H<sup>-1</sup>g";
        default:
            return "Error!  No such output.  ";
        }
    }

    public String[] getOutputTypes() {
        String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
        return types;
    }

    public void doit() throws Exception {

        // pull in the supplied data...
        MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix) this.pullInput(0);
        MultiFormatMatrix Targets = (MultiFormatMatrix) this.pullInput(1);
        MultiFormatMatrix LayerTable = (MultiFormatMatrix) this.pullInput(2);
        MultiFormatMatrix SerializedWeights = (MultiFormatMatrix) this.pullInput(3);
        MultiFormatMatrix Biases = (MultiFormatMatrix) this.pullInput(4);
        MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix) this.pullInput(5);
        MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix) this.pullInput(6);
        MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix)this.pullInput(7);
        long NumberOfElementsThreshold = ((Long) this.pullInput(8)).longValue();

        // determine the proper format
        // NumElements = (Number of Neurons) * (Number of Explanatory Variables)
        // the Number of Neurons is the number of biases present + 1 because of
        // the extra standardized-to-zero output neuron.
        long NumElements = (Biases.getDimensions()[0] + 1) * (ExplanatoryVariables.getDimensions()[0]);
        int FormatIndex = -101; // initialize
        if (NumElements < NumberOfElementsThreshold) {
            // small means keep it in core; single dimensional in memory is best
            FormatIndex = 1; // Beware the MAGIC NUMBER!!!
        } else { // not small means big, so go out of core; serialized blocks on
            // disk are best
            FormatIndex = 3; // Beware the MAGIC NUMBER!!!
        }

        ////// pull out some relevant constants
        long nLayers = LayerTable.getDimensions()[0];
        long nWeights = WeightNumberFromToNeuronTable.getDimensions()[0];
        long nExamples = ExplanatoryVariables.getDimensions()[0];
        long nInputs = ExplanatoryVariables.getDimensions()[1];
        long nOutputs = (long) LayerTable.getValue(nLayers - 1, 0);
        long OutputFirst = (long) LayerStartFinishNeuronTable.getValue(nLayers - 1, 0);
        long LayerIndexFinal = nLayers - 1;
        if (nInputs != (long)LayerTable.getValue(0,0)) {
            System.out.println("(nInputs {" + nInputs + "} != (long)LayerTable.getValue(0,0)) {"
                    + (long)LayerTable.getValue(0,0) + "} -> "
                    + "number of inputs/explanatory variables does not match " + "number of input neurons.");
            throw new Exception();
        }
        // number of neurons
        long nNeurons = 0;
        Double nNeuronsDoubleTemp = new Double(0.0);
        for (long RowIndex = 0; RowIndex < nLayers; RowIndex++) {
            nNeuronsDoubleTemp = new Double(LayerTable.getValue(RowIndex, 0));
            nNeurons += nNeuronsDoubleTemp.intValue();
        }
        if (nNeurons != Biases.getDimensions()[0]) {
            System.out.println("nNeurons {" + nNeurons + "} != Biases.getDimensions()[0] {"
                    + Biases.getDimensions()[0] + "}");
            throw new Exception();
        }

        // i need to build a table that relates the layer to its first incoming weight number...
        // Beware the MAGIC NUMBER!!! i am using format #1 (SDIM) and a single dimensional array
        MultiFormatMatrix FirstWeightToLayer = new MultiFormatMatrix(1, new long[] { nLayers });

        double FirstWeightNumberTemp = -18.3;
        for (long LayerIndex = 1; LayerIndex < nLayers; LayerIndex++) {
            FirstWeightNumberTemp = 0.0;
            for (long LayerFunnyIndex = 0; LayerFunnyIndex < LayerIndex - 1; LayerFunnyIndex++) {
                FirstWeightNumberTemp += LayerTable.getValue(LayerFunnyIndex, 0)
                        * LayerTable.getValue(LayerFunnyIndex + 1, 0);
            }
            FirstWeightToLayer.setValue(LayerIndex, FirstWeightNumberTemp);
        }

        /*
         * number of weights int nWeights = 0; Double nWeightsDoubleTemp =
         * new Double(0.0); Beware the MAGIC NUMBER!!! the "nLayers - 1" gets
         * us to the next to the last element... for (int RowIndex = 0;
         * RowIndex < nLayers - 1; RowIndex++) { nWeightsDoubleTemp = new
         * Double(LayerTable.getValue(RowIndex, 0) *
         * LayerTable.getValue(RowIndex + 1, 0) ); nWeights +=
         * nWeightsDoubleTemp.intValue(); }
         */

        // ************************begin NEW STUFF....
        // The idea here is to go through one example at a time and do
        // everything...
        /*
         * in defining the NeuronActivations table, we follow a new convention.
         * we want to avoid writing down the inputs every time, so we drop them.
         * that means instead of having as many columns as neurons and using the
         * neuron index as the column index we do it this way. the first column
         * corresponds to the first non-input neuron. thus, the relationship
         * between the "true" neuron index and its column is: NeuronTableIndex =
         * NeuronIndex - nInputs we will need to remember to do this whenever we
         * are reading or writing the NeuronActivations or Deltas...
         */

        // do a pile of initializations
        MultiFormatMatrix NeuronActivations = new MultiFormatMatrix(FormatIndex, new long[] { nExamples,
                nNeurons - nInputs + 1 });
        //		 nNeurons + 1 because of the extra un-notated output neuron
        MultiFormatMatrix NNDeltas = new MultiFormatMatrix(FormatIndex, new long[] { nNeurons - nInputs });
        // we will want to eventually change this to a SDIM one-dimensional
        // array that gets used over and over...
        //		 again, nNeurons + 1 because of the extra un-notated output neuron
        MultiFormatMatrix OutputExpedSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable.getValue(
                nLayers - 1, 0) + 1 });
        // Beware the MAGIC NUMBER!!! this thing is in format #1 (SDIM) and also is one dimensional...
        MultiFormatMatrix BiasesGradient = new MultiFormatMatrix(FormatIndex,
                new long[] {nNeurons, 1}); // ignoring the extra un-notated output neuron
        MultiFormatMatrix SerializedWeightsGradient = new MultiFormatMatrix(FormatIndex,
                new long[] {nWeights, 1}); // ignoring the extra un-notated output neuron
        MultiFormatMatrix StackedWeightsBiasesGradient = new MultiFormatMatrix(FormatIndex,
                new long[] {nWeights + nNeurons - nInputs, 1}); // ignoring the extra un-notated output neuron

        long ThisLayerFirst = -1;
//        long ThisLayerLast = -2;
        long PreviousLayerFirst = -3;
//        long PreviousLayerLast = -4;
        long nNeuronsInPreviousLayer = -5;
        long nNeuronsInThisLayer = -6;
        long FirstWeightNumber = -7;
        long NeuronUnderConsideration = -8;
        long WeightUnderConsideration = -9;
        double PreviousActivationUnderConsideration = -8.0;
        double SquashedSum = -9.0;
        double StretchedSum = -10.0;
        double Denominator = -11.0;
        double SumTemp = -528.3;
        double FinalTemp = -32.52;
        long MyLayer = -10;
        long MyLayerFirst = -11;
        long NextLayerFirst = -12;
//        long FirstWeightNumberDeltas = -13;
        double DerivativeTemp = -5.0;
        double ThisActivation = -6.0;
        double PosteriorSum = -7.0;
        long LookupIndex = -14;
        double BiasGradientTemp = -1.0;
        double WeightsGradientTemp = -2.0;
        long NeuronFromLookup = -15;
        long NeuronToLookup = -16;

        // %%%%%%%%%%% the beginning of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%
        for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
            ////// consider the input neurons: i think i will try to skip them
            // this time and save all that writing...
            
            ////// consider the hidden neurons
            for (long LayerIndex = 1; LayerIndex < nLayers - 1; LayerIndex++) {
                nNeuronsInPreviousLayer = (long) LayerTable.getValue(LayerIndex - 1, 0);
                nNeuronsInThisLayer = (long) LayerTable.getValue(LayerIndex, 0);
                // remember to subtract off the number of inputs whenever
                // you're using a neuron index for storage...
                if (LayerIndex == 1) {
                    ThisLayerFirst = 0;
//                    ThisLayerLast = (long) LayerTable.getValue(LayerIndex, 1) - nInputs;
                    PreviousLayerFirst = 0;
//                    PreviousLayerLast = nInputs;
                } else {
                    ThisLayerFirst = (long) LayerStartFinishNeuronTable.getValue(LayerIndex, 0) - nInputs;
//                    ThisLayerLast = (long) LayerStartFinishNeuronTable.getValue(LayerIndex, 1) - nInputs;
                    PreviousLayerFirst = (long) LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 0) - nInputs;
//                    PreviousLayerLast = (long) LayerStartFinishNeuronTable.getValue(LayerIndex - 1, 1) - nInputs;
                }
                // let us now determine the weight number for the first incoming
                // connection. then we can just do a regular spacing to figure out the others...
                // gonna figure this as a table outright before this big loop...

                FirstWeightNumber = (long) FirstWeightToLayer.getValue(LayerIndex);
                // this is the weight number for the first connection to this layer...
                //      System.out.println("This layer's first incoming weight number is: " + FirstWeightNumber);
                // do the actual multiplication/summation/activation brute force style...
                for (long WithinIndex = 0; WithinIndex < (long) LayerTable.getValue(LayerIndex, 0); WithinIndex++) {
                    // ThisLayerLast + 1 due to ThisLayerLast being the index...
                    NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
                    // ok, here is where the shifting of the indices bites back:
                    // the biases are still coded as neuron indices, so we have
                    // to add the number of inputs back in...
                    SumTemp = Biases.getValue(NeuronUnderConsideration + nInputs, 0);
                    for (long IncomingIndex = 0; IncomingIndex < nNeuronsInPreviousLayer; IncomingIndex++) {
                        WeightUnderConsideration = FirstWeightNumber + WithinIndex + IncomingIndex
                                * nNeuronsInThisLayer;
                        if (LayerIndex == 1) {
                            PreviousActivationUnderConsideration = ExplanatoryVariables.getValue(ExampleIndex,
                                    IncomingIndex);
                        } else {
                            PreviousActivationUnderConsideration = NeuronActivations.getValue(ExampleIndex,
                                    PreviousLayerFirst + IncomingIndex);
                        }
                        SumTemp += PreviousActivationUnderConsideration
                                * SerializedWeights.getValue(WeightUnderConsideration, 0);
                    }
                    SquashedSum = 1.0 / (1.0 + java.lang.Math.exp(-SumTemp));
                    NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration, SquashedSum);
                }

            }
            ////// consider the output neurons: SoftMax activations...
            nNeuronsInPreviousLayer = (long) LayerTable.getValue(LayerIndexFinal - 1, 0);
            nNeuronsInThisLayer = (long) LayerTable.getValue(LayerIndexFinal, 0);
            // remember to subtract off the number of inputs whenever
            // you're using a neuron index for storage...
            ThisLayerFirst = (long) LayerStartFinishNeuronTable.getValue(LayerIndexFinal, 0) - nInputs;
//            ThisLayerLast = (long) LayerStartFinishNeuronTable.getValue(LayerIndexFinal, 1) - nInputs;
            PreviousLayerFirst = (long) LayerStartFinishNeuronTable.getValue(LayerIndexFinal - 1, 0) - nInputs;
//            PreviousLayerLast = (long) LayerStartFinishNeuronTable.getValue(LayerIndexFinal - 1, 1) - nInputs;
            FirstWeightNumber = (long) FirstWeightToLayer.getValue(LayerIndexFinal);

            Denominator = 1; // "one" because of the dead output which is always
            // zero: exp(0) = 1
            for (long WithinIndex = 0; WithinIndex < (long) LayerTable.getValue(LayerIndexFinal, 0); WithinIndex++) {
                // ThisLayerLast + 1 due to ThisLayerLast being the index...
                NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
                //        System.out.println(" ---> NeuronUnderConsideration = " +
                // NeuronUnderConsideration + "; nNeurons = " + nNeurons);
                // ok, here is where the shifting of the indices bites back: the
                // biases are still coded as neuron indices, so we have to add
                // the number of inputs back in...
                SumTemp = Biases.getValue(NeuronUnderConsideration + nInputs, 0);
                for (long IncomingIndex = 0; IncomingIndex < nNeuronsInPreviousLayer; IncomingIndex++) {
                    WeightUnderConsideration = FirstWeightNumber + WithinIndex + IncomingIndex
                            * nNeuronsInThisLayer;
                    /*
                    System.out.println(" ExampleIndex = " + ExampleIndex + 
                            "; WithinIndex = " + WithinIndex + "; IncomingIndex = " +
                            IncomingIndex + "; WeightUnderConsideration = " +
                            WeightUnderConsideration + "; Value = " +
                            SerializedWeights.getValue(WeightUnderConsideration, 0) +
                            "; PreviousLayerFirst = " + PreviousLayerFirst + "; IncomingIndex = " +
                            IncomingIndex);
                    */
                    if (PreviousLayerFirst < 0) {
                        PreviousActivationUnderConsideration = ExplanatoryVariables.getValue(ExampleIndex,
                                PreviousLayerFirst + IncomingIndex + nInputs);
                    }
                    else {
                    PreviousActivationUnderConsideration = NeuronActivations.getValue(ExampleIndex,
                            PreviousLayerFirst + IncomingIndex);
                    }
                    SumTemp += PreviousActivationUnderConsideration
                            * SerializedWeights.getValue(WeightUnderConsideration, 0);
                }
                StretchedSum = java.lang.Math.exp(SumTemp);
                //        System.out.println("SumTemp = " + SumTemp + "; StretchedSum = " + StretchedSum);
                OutputExpedSums.setValue(WithinIndex, StretchedSum); // record the  numerator
                Denominator += StretchedSum; // accumulate the denominator
            }
            // go back through and do the division
            for (long WithinIndex = 0; WithinIndex < (long) LayerTable.getValue(LayerIndexFinal, 0); WithinIndex++) {
                NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
                FinalTemp = OutputExpedSums.getValue(WithinIndex) / Denominator;

                NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration, FinalTemp);
                // and some output delta work...
                if (WithinIndex != nOutputs) {
                    if ((long) Targets.getValue(ExampleIndex, 0) == WithinIndex) {
                        // This is the realized outcome...
                        NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, FinalTemp - 1.0);
                    } else {
                        // This outcome was not realized...
                        NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, FinalTemp);
                    }
                }
            }
            // do the final "permanently-zeroed" neuron
            NeuronActivations.setValue(ExampleIndex, nNeurons - nInputs, 1.0 / Denominator);

            ////// calculate the deltas

            // the deltas for the output layer have already been done...
            // now for the hidden layers...
            for (long NeuronIndex = (OutputFirst - 1); NeuronIndex >= nInputs; NeuronIndex--) {
                // NOTICE: skipping the inputs....
                //              System.out.println("NeuronIndex = " + NeuronIndex);
                // going backwards through. hence my non-standard break criterion...
                // find the list of posterior nodes for a particular neuron.
                // that is, what neurons does this one feed into...
                // under the assumption of full connectivity, it will feed into
                // all of the neurons in the next layer...
                // so, i need to know what layer i'm in, and what layer comes after...
                LookupIndex = NeuronIndex - nInputs;
                //                System.out.println("---> NeuronIndex="+NeuronIndex+";
                // nInputs="+nInputs+"; LookupIndex="+LookupIndex);
                MyLayer = (long) NeuronToLayerTable.getValue(NeuronIndex, 0);
                MyLayerFirst = (long) LayerStartFinishNeuronTable.getValue(MyLayer, 0) - nInputs;
                NextLayerFirst = (long) LayerStartFinishNeuronTable.getValue(MyLayer + 1, 0) - nInputs;

                ThisActivation = NeuronActivations.getValue(ExampleIndex, LookupIndex);
                //                System.out.println("#---> MyLayer="+MyLayer+";
                // MyLayerFirst="+MyLayerFirst+";
                // NextLayerFirst="+NextLayerFirst);
                DerivativeTemp = ThisActivation * (1.0 - ThisActivation);
                // run through the posterior nodes and their weights. trying to
                // calculate sum [over posterior neurons]
                // delta_posterior*w_j_to_posterior

                // find the weight number for the first connection under
                // consideration...
                FirstWeightNumber = (long) FirstWeightToLayer.getValue(MyLayer + 1)
                        + ((LookupIndex - MyLayerFirst) * (long) LayerTable.getValue(MyLayer + 1, 0));
                //                FirstWeightNumber = (long)
                // FirstWeightToLayer.getValue(MyLayer)
                //                        + ((NeuronIndex - MyLayerFirst) * (long)
                // LayerTable.getValue(MyLayer + 1, 0));
                //                FirstWeightNumber = (long)
                // FirstWeightToLayer.getValue(MyLayer)
                //                + ((LookupIndex - MyLayerFirst) * (long)
                // LayerTable.getValue(MyLayer + 1, 0));
                PosteriorSum = 0;
                //                System.out.println("
                // ->FirstWeightNumber="+FirstWeightNumber);
                for (long PosteriorIndex = 0; PosteriorIndex < (long) LayerTable.getValue(MyLayer + 1, 0); PosteriorIndex++) {
                    // that is, from 0 to the # of neurons in the next layer (minus one for index's sake)
//                    System.out.println("** -> PosteriorIndex="+PosteriorIndex+"; NextLayerFirst="+NextLayerFirst+"; FirstWeightNumber="+FirstWeightNumber);
                    PosteriorSum += (NNDeltas.getValue(PosteriorIndex + NextLayerFirst) * SerializedWeights
                            .getValue(PosteriorIndex + FirstWeightNumber, 0));
                    // i can do this because of the way the weights are encoded. the
                    // outgoing weights from a particular neuron are sequential..
                }
                //              System.out.println("ExampleIndex = " + ExampleIndex + ";
                // NeuronIndex = " +
                //                                 NeuronIndex + "; FirstWeightNumber = " + FirstWeightNumber +
                //                                 "; PosteriorSum = " + PosteriorSum + "; DerivativeTemp = " +
                // DerivativeTemp);
                NNDeltas.setValue(LookupIndex, DerivativeTemp * PosteriorSum);
            }

            // calculate the gradient
            // figure the gradient for the biases
            for (long NeuronIndex = (long) LayerTable.getValue(0, 0); NeuronIndex < nNeurons; NeuronIndex++) {
                BiasGradientTemp = BiasesGradient.getValue(NeuronIndex,0);
                LookupIndex = NeuronIndex - nInputs;
                BiasGradientTemp += NNDeltas.getValue(LookupIndex);
            
                BiasesGradient.setValue(NeuronIndex, 0, BiasGradientTemp);
                //                System.out.println("NeuronIndex = " + NeuronIndex + "; nInputs = " + nInputs +
                //                                   "; nWeights = " + nWeights);
            }            
            // figure the gradient for the weights
            for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
              WeightsGradientTemp = SerializedWeightsGradient.getValue(WeightIndex,0);

//                System.out.println("WeightIndex = " + WeightIndex);
                NeuronFromLookup = (long)WeightNumberFromToNeuronTable.getValue(WeightIndex,0) - nInputs;
                NeuronToLookup = (long)WeightNumberFromToNeuronTable.getValue(WeightIndex,1) - nInputs;
                if (NeuronFromLookup < 0) { // we have a weight originating in an input...
                    WeightsGradientTemp += (ExplanatoryVariables.getValue(ExampleIndex,NeuronFromLookup+nInputs) *
                            NNDeltas.getValue(NeuronToLookup));
                }
                else { // the weight is *not* originating in an input
                    WeightsGradientTemp += (NeuronActivations.getValue(ExampleIndex,NeuronFromLookup) *
                                        NNDeltas.getValue(NeuronToLookup));
                }
              SerializedWeightsGradient.setValue(WeightIndex,0,WeightsGradientTemp);
            }
            
        }
        // now put together the stacked gradient...
        for (long NeuronIndex = (long) LayerTable.getValue(0,0); NeuronIndex < nNeurons; NeuronIndex++) {
            StackedWeightsBiasesGradient.setValue(NeuronIndex - nInputs + nWeights, 0, BiasesGradient.getValue(NeuronIndex,0));
        }
        for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
            StackedWeightsBiasesGradient.setValue(WeightIndex,0,SerializedWeightsGradient.getValue(WeightIndex,0));
        }
        // %%%%%%%%%%% the end of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%
        // ********************** end NEW STUFF...

        // &&&&&&&& begin old gradient stufff....
        

        // &&&&&&&& end old gradient stufff....

        this.pushOutput(NeuronActivations, 0);
        this.pushOutput(NNDeltas, 1);
        this.pushOutput(BiasesGradient,2);
        this.pushOutput(SerializedWeightsGradient,3);
        this.pushOutput(StackedWeightsBiasesGradient,4);

    }
}