package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNCondensedLessRounding extends ComputeModule {

    public String getModuleName() {
        return "NNCondensedLessRounding";
    }

    public String getModuleInfo() {
        return "This module actually calculates the activations (so-called "
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
            return "RoundingThreshold";
        case 9:
            return "CalculateGradientFlag";
        case 10:
            return "CalculateErrorFlag";
        case 11:
            return "ReportAllActivationsFlag";
        case 12:
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
            return "RoundingThreshold: this is a positive number that "
                    + "sets the threshold for when it should use a rounding friendly "
                    + "method of calculating activations and derivatives. 26 is a good place to start "
                    + "with the rounding errors being about 8x10<sup>-4</sup>% of value of the derivative.";
        case 9:
            return "CalculateGradientFlag: a TRUE means to calcuate the gradient calculation "
                    + "(so as to be able to use this module to just calculate the errors if desired)";
        case 10:
            return "CalculateErrorFlag: a TRUE means to calculate value of the cross-entropy "
                    + "error function (-loglikelihood) (so as to be able to use this just to calculate the gradient "
                    + "and/or activations if desired)";
        case 11:
            return "ReportAllActivationsFlag: a TRUE means to output a huge table with all the "
                    + "neuron activations for each example (memory intensive). A FALSE will "
                    + "not report everything and thus save lots of memory.";
        case 12:
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
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Double",
                "java.lang.Boolean", "java.lang.Boolean", "java.lang.Boolean", "java.lang.Long", };
        return types;
    }

    public String getOutputName(int i) {
        switch (i) {
        case 0:
            return "NeuronActivations";
        case 1:
            return "ErrorFunction";
        case 2:
            return "FracCorrect";
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
            return "ErrorFunction: a Double whose value is the cross-entropy error";
        case 2:
            return "FracCorrect: a Double whose value is the fraction correctly predicted via winner-takes-all";
        case 3:
            return "BiasesGradient";
        case 4:
            return "SerializedWeightsGradient";
        case 5:
            return "StackedWeightsBiasesGradient: This is a single column "
                    + "which has the weights gradient first followed by the "
                    + "<i>relevant</i> biases. aka: skipping the input neurons "
                    + "since they don't really have a bias. For use in H<sup>-1</sup>g";
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
        /*
         * case 0: return "ExplanatoryVariables"; case 1: return "Targets"; case 2: return "LayerTable"; case 3: return "SerializedWeights";
         * case 4: return "Biases"; case 5: return "LayerStartFinishNeuronTable"; case 6: return "NeuronToLayerTable"; case 7: return
         * "WeightNumberFromToNeuronTable"; case 8: return "RoundingThreshold"; case 9: return "nBlocks"; case 10: return
         * "CalculateGradientFlag"; case 11: return "CalculateErrorFlag"; case 12: return "ReportAllActivationsFlag"; case 13: return
         * "NumberOfElementsThreshold";
         */

        // pull in the supplied data...
        MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix) this.pullInput(0);
        MultiFormatMatrix Targets = (MultiFormatMatrix) this.pullInput(1);
        MultiFormatMatrix LayerTable = (MultiFormatMatrix) this.pullInput(2);
        MultiFormatMatrix SerializedWeights = (MultiFormatMatrix) this.pullInput(3);
        MultiFormatMatrix Biases = (MultiFormatMatrix) this.pullInput(4);
        MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix) this.pullInput(5);
        MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix) this.pullInput(6);
        MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix) this.pullInput(7);
        double RoundingThreshold = ((Double) this.pullInput(8)).doubleValue();
        boolean CalculateGradientFlag = ((Boolean) this.pullInput(9)).booleanValue();
        boolean CalculateErrorFlag = ((Boolean) this.pullInput(10)).booleanValue();
        boolean ReportAllActivationsFlag = ((Boolean) this.pullInput(11)).booleanValue();
        boolean DoNotReportAllActivationsFlag = !ReportAllActivationsFlag;
        long NumberOfElementsThreshold = ((Long) this.pullInput(12)).longValue();

        // determine the proper format
        // NumElements = (Number of Neurons) * (Number of Explanatory Variables)
        // the Number of Neurons is the number of biases present + 1 because of
        // the extra standardized-to-zero output neuron.
        long NumElements = (Biases.getDimensions()[0] + 1) * (ExplanatoryVariables.getDimensions()[0]);
        int FormatIndex = -1; // initialize
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
        if (nInputs != (long) LayerTable.getValue(0, 0)) {
            System.out.println("(nInputs {" + nInputs + "} != (int)LayerTable.getValue(0,0)) {"
                    + (long) LayerTable.getValue(0, 0) + "} -> "
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
        MultiFormatMatrix NeuronActivations = new MultiFormatMatrix(1, new long[] { 0 });
        if (ReportAllActivationsFlag) {
            NumElements = nExamples * (nNeurons - nInputs + 1);
            if (NumElements < NumberOfElementsThreshold) {
                FormatIndex = 1;
            } else {
                FormatIndex = 3;
            }
            NeuronActivations = new MultiFormatMatrix(FormatIndex,
                    new long[] { nExamples, nNeurons - nInputs + 1 });
        } else {
            NeuronActivations = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs });
            // if not reporting all of them, we never use the activation of the standardized output...
        }

        boolean UseComplements[] = new boolean[(int) nNeurons - (int) nInputs + 1];
        // this is a set of flags that says whether we need to use the complements form for the derivative to avoid rounding errors...
        MultiFormatMatrix ActivationComplements = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs + 1 });
        /*
         * Beware the MAGIC NUMBER!!! and assumptions... i am making this a one dimensional array that is only used and re-used when
         * computing the deltas and derivatives... thus, i am also specifying the format to be #1 SDIM
         */
        //		 again, nNeurons + 1 because of the extra un-notated output neuron
        MultiFormatMatrix OutputSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable.getValue(
                nLayers - 1, 0) + 1 });
        MultiFormatMatrix OutputExpedSums = new MultiFormatMatrix(1, new long[] { (long) LayerTable.getValue(
                nLayers - 1, 0) + 1 });
        // Beware the MAGIC NUMBER!!! these things are in format #1 (SDIM) and also is one dimensional...
        MultiFormatMatrix NNDeltas = new MultiFormatMatrix(1, new long[] { 0 });
        MultiFormatMatrix BiasesGradient = new MultiFormatMatrix(1, new long[] { 0 });
        MultiFormatMatrix SerializedWeightsGradient = new MultiFormatMatrix(1, new long[] { 0 });
        MultiFormatMatrix StackedWeightsBiasesGradient = new MultiFormatMatrix(1, new long[] { 0 });
        if (CalculateGradientFlag) {
            NNDeltas = new MultiFormatMatrix(1, new long[] { nNeurons - nInputs });
            NumElements = (nNeurons);
            if (NumElements < NumberOfElementsThreshold) {
                FormatIndex = 1;
            } else {
                FormatIndex = 3;
            }
            BiasesGradient = new MultiFormatMatrix(FormatIndex, new long[] { nNeurons, 1 }); // ignoring the extra un-notated
            // output neuron
            NumElements = (nWeights);
            if (NumElements < NumberOfElementsThreshold) {
                FormatIndex = 1;
            } else {
                FormatIndex = 3;
            }
            SerializedWeightsGradient = new MultiFormatMatrix(FormatIndex, new long[] { nWeights, 1 }); // ignoring the extra
            // un-notated output neuron
            StackedWeightsBiasesGradient = new MultiFormatMatrix(1,
                    new long[] { nWeights + nNeurons - nInputs, 1 }); // Beware the MAGIC NUMBER!!! SDIM regardless
        }

        double FracCorrect = -59.1;
        boolean ErrorDoneForThisExample = false;
        double FinalStandardized = -54.1;
        double AlternateStandardized = -55.1;
        long TargetThisExample = -53;
        double ErrorFunction = 0;
        long ThisLayerFirst = -51;
//        long ThisLayerLast = -50;
        long PreviousLayerFirst = -1;
//        long PreviousLayerLast = -2;
        long nNeuronsInPreviousLayer = -3;
        long nNeuronsInThisLayer = -4;
        long FirstWeightNumber = -5;
        long NeuronUnderConsideration = -6;
        long WeightUnderConsideration = -7;
        double PreviousActivationUnderConsideration = -8.0;
        double ExpTemp = -3;
        double SquashedSum = -9.0;
        double SquashedSumComplement = -9.0;
//        double StretchedSum = -10.0;
//        double Denominator = -11.0;
        double SumTemp = -528.3;
        double FinalTemp = -32.52;
        double emm = -23.51;
        long emmIndex = -55;
        double CapitalCue = -23.52;
        double AlternateSumTemp = -53.21;
        long MyLayer = -1;
        long MyLayerFirst = -2;
        long NextLayerFirst = -3;
//        long FirstWeightNumberDeltas = -4;
        double DerivativeTemp = -5.0;
        double ThisActivation = -6.0;
        double PosteriorSum = -7.0;
        long LookupIndex = -58;
        double BiasGradientTemp = -1.0;
        double WeightsGradientTemp = -2.0;
        long NeuronFromLookup = -3;
        long NeuronToLookup = -4;

        long nCorrect = 0;

        // %%%%%%%%%%% the beginning of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%
        for (long ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {

            ////// pull out the target. will be used much later, but alas, let's do it here.
            if (CalculateGradientFlag || CalculateErrorFlag) {
                ErrorDoneForThisExample = false;
                TargetThisExample = (long) Targets.getValue(ExampleIndex, 0);
            }

            ////// consider the input neurons: i think i will try to skip them
            // this time and save all that writing...

            ////// consider the hidden neurons
            for (long LayerIndex = 1; LayerIndex < nLayers - 1; LayerIndex++) {
                nNeuronsInPreviousLayer = (long) LayerTable.getValue(LayerIndex - 1, 0);
                nNeuronsInThisLayer = (long) LayerTable.getValue(LayerIndex, 0);
                // remember to subtract off the number of inputs whenever
                // you're using a neuron index for storage...
                if (LayerIndex == 1) {
//                	System.out.println("I AM IN HERE!!!");
                    ThisLayerFirst = 0;
//                    ThisLayerLast = (long) LayerTable.getValue(LayerIndex, 1) - nInputs;
//                    ThisLayerLast = (long) LayerTable.getValue(LayerIndex, 0) - nInputs;
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
                            if (DoNotReportAllActivationsFlag) {
                                PreviousActivationUnderConsideration = NeuronActivations
                                        .getValue(PreviousLayerFirst + IncomingIndex);
                            } else {
                                PreviousActivationUnderConsideration = NeuronActivations.getValue(ExampleIndex,
                                        PreviousLayerFirst + IncomingIndex);
                            }
                        }
                        SumTemp += PreviousActivationUnderConsideration
                                * SerializedWeights.getValue(WeightUnderConsideration, 0);
                    }
                    ExpTemp = java.lang.Math.exp(-SumTemp);
                    SquashedSum = 1.0 / (1.0 + ExpTemp);
                    if (DoNotReportAllActivationsFlag) {
                        NeuronActivations.setValue(NeuronUnderConsideration, SquashedSum);
                    } else {
                        NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration, SquashedSum);
                    }
                    if (SumTemp > RoundingThreshold) {
                        SquashedSumComplement = ExpTemp / (1 + ExpTemp);
                        //                        System.out.println("^^^ Example " + ExampleIndex + ": ExpTemp = " + ExpTemp + "; SSC = " +
                        // SquashedSumComplement);
                        ActivationComplements.setValue(NeuronUnderConsideration, SquashedSumComplement);
                        if ((new Double(SquashedSumComplement)).isNaN()) {
                        }
                        UseComplements[(int) NeuronUnderConsideration] = true;
                    } else {
                        UseComplements[(int) NeuronUnderConsideration] = false;
                    }
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

//            Denominator = 1; // "one" because of the dead output which is always
            // zero: exp(0) = 1
            emm = 0; // initialized the thing to find the maximum internal activation among the outputs;
            emmIndex = nOutputs ; // nOutputs - 1 or nOutputs???
            // we start at zero because that would mean the final standardized output is the largest

            for (long WithinIndex = 0; WithinIndex < nNeuronsInThisLayer; WithinIndex++) {
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
                    if (PreviousLayerFirst < 0) {
                        PreviousActivationUnderConsideration = ExplanatoryVariables.getValue(ExampleIndex,
                                PreviousLayerFirst + IncomingIndex + nInputs);
                    } else {
                        if (DoNotReportAllActivationsFlag) {
                            PreviousActivationUnderConsideration = NeuronActivations.getValue(PreviousLayerFirst
                                    + IncomingIndex);
                        } else {
                            PreviousActivationUnderConsideration = NeuronActivations.getValue(ExampleIndex,
                                    PreviousLayerFirst + IncomingIndex);
                        }
                    }
                    //          System.out.println(" ExampleIndex = " + ExampleIndex + ";
                    // WithinIndex = " + WithinIndex + "; IncomingIndex = " +
                    // IncomingIndex + "; WeightUnderConsideration = " +
                    // WeightUnderConsideration + "; Value = " +
                    // SerializedWeights.getValue(WeightUnderConsideration, 0));
                    if (WeightUnderConsideration < 0) {
                    	System.out.println("Example = " + ExampleIndex + "; WeightUnderConsideration = " + WeightUnderConsideration + "; FirstWeightNumber " + FirstWeightNumber +
                    			"; WithinIndex = " + WithinIndex + "; IncomingIndex = " + IncomingIndex + "; nNeuronsInThisLayer = " + nNeuronsInThisLayer);
                    }
                    SumTemp += PreviousActivationUnderConsideration
                            * SerializedWeights.getValue(WeightUnderConsideration, 0);
                }
//                StretchedSum = java.lang.Math.exp(SumTemp);
                //        System.out.println("SumTemp = " + SumTemp + "; StretchedSum = " + StretchedSum);
                OutputSums.setValue(WithinIndex, SumTemp);
                if (SumTemp > emm) {
                    emm = SumTemp;
                    emmIndex = WithinIndex;
                }
//                                System.out.println("   -> emm = " + emm + "; emmIndex = " + emmIndex + "; for #"+ExampleIndex);
            }
            // go back through and find the centered intermediate sums and the denominator
            if (emmIndex == nOutputs) {
                CapitalCue = 1;
            } else {
                CapitalCue = Math.exp(-emm); // don't forget the standardized output which is always zero: exp(0) = 1
            }
            //System.out.println("*** emm = " + emm + "; emmIndex = " + emmIndex);
            for (long WithinIndex = 0; WithinIndex < nNeuronsInThisLayer; WithinIndex++) {
//                                System.out.println("--> before centering: OutputSums("+WithinIndex+")="+OutputSums.getValue(WithinIndex));
                OutputSums.setValue(WithinIndex, OutputSums.getValue(WithinIndex) - emm);
//                                System.out.println("---> after centering: OutputSums("+WithinIndex+")="+OutputSums.getValue(WithinIndex));
                if (WithinIndex == emmIndex) {
                    OutputExpedSums.setValue(WithinIndex, 1); // record the numerator
                } else {
                    OutputExpedSums.setValue(WithinIndex, Math.exp(OutputSums.getValue(WithinIndex))); // record the numerator
                }
                CapitalCue += OutputExpedSums.getValue(WithinIndex); // accumulate the denominator
//                System.out.println(" ---> after exp: OutputExpedSums("+WithinIndex+")="+OutputExpedSums.getValue(WithinIndex));
            }

            // bump up the counter for the number correct
            if (emmIndex == TargetThisExample) {
                nCorrect++;
            }
            // go through yet again to write down the final results...
            for (long WithinIndex = 0; WithinIndex < (long) LayerTable.getValue(LayerIndexFinal, 0); WithinIndex++) {
                NeuronUnderConsideration = ThisLayerFirst + WithinIndex;
                FinalTemp = OutputExpedSums.getValue(WithinIndex) / CapitalCue;
                if (DoNotReportAllActivationsFlag) {
                    NeuronActivations.setValue(NeuronUnderConsideration, FinalTemp);
                } else {
                    NeuronActivations.setValue(ExampleIndex, NeuronUnderConsideration, FinalTemp);
                }

                if (CalculateErrorFlag || CalculateGradientFlag) {
                    if (FinalTemp <= 0.5) {
                        if (TargetThisExample == WithinIndex) {
                            // This is the realized outcome...
                            if (CalculateGradientFlag) {
                                NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, FinalTemp - 1.0);
                            }
                            if (CalculateErrorFlag) {
                                ErrorFunction -= Math.log(FinalTemp);
                                ErrorDoneForThisExample = true;
                            }
                        } else { // This outcome was not realized...
                            if (CalculateGradientFlag) {
                                NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, FinalTemp);
                            }
                        }
                    } else {
                        if (emmIndex == nOutputs) {
                            AlternateSumTemp = 0;
                        }
                        else {
                            AlternateSumTemp = Math.exp(-emm);
                        }
                        for (long WithinIndexAlt = 0; WithinIndexAlt < (long) LayerTable.getValue(LayerIndexFinal,
                                0); WithinIndexAlt++) {
                            if (WithinIndexAlt != WithinIndex) {
                                AlternateSumTemp += OutputExpedSums.getValue(WithinIndexAlt);
                            }
                        }
                        AlternateSumTemp /= CapitalCue;

                        if (TargetThisExample == WithinIndex) {
                            // This is the realized outcome...
                            if (CalculateGradientFlag) {
                                NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, -AlternateSumTemp);
//                                System.out.println(" --> Option realized has P>1/2 AltTempSum = " + AlternateSumTemp + "; Option=" + WithinIndex +"; ExampleIndex = " + ExampleIndex );
                            }
                            if (CalculateErrorFlag) {
//                                ErrorFunction -= java.lang.Math.log1p(-AlternateSumTemp);
                                ErrorFunction -= java.lang.Math.log(1.0-AlternateSumTemp);
                                ErrorDoneForThisExample = true;
                            }
                        } else {
                            // This outcome was not realized...
                            if (CalculateGradientFlag) {
                                NNDeltas.setValue(OutputFirst + WithinIndex - nInputs, 1 - AlternateSumTemp);
                            }
                        }

                    }
                }
            }

            // do the final "permanently-zeroed" neuron and any error function associated with it.
            if (ReportAllActivationsFlag || (CalculateErrorFlag && !ErrorDoneForThisExample)) {
//                System.out.println("--We made it to calculate a FinalStandardized in Example [" + ExampleIndex +
//                        "] and the Error Flag is: " + ErrorDoneForThisExample);
                FinalStandardized = Math.exp(-emm) / CapitalCue;
                if (ReportAllActivationsFlag) {
                    NeuronActivations.setValue(ExampleIndex, nNeurons - nInputs, FinalStandardized);
                }
                if (CalculateErrorFlag && !ErrorDoneForThisExample) {
//                    if (TargetThisExample == (nNeurons - nInputs)) {
                        if (FinalStandardized <= 0.5) {
                            ErrorFunction -= Math.log(FinalStandardized);
                            ErrorDoneForThisExample = true;
                        } else {
                            AlternateStandardized = 0;
                            for (long WithinIndexAlt = 0; WithinIndexAlt < (long) LayerTable.getValue(
                                    LayerIndexFinal, 0); WithinIndexAlt++) {
                                AlternateStandardized += OutputExpedSums.getValue(WithinIndexAlt);
                            }
                            AlternateStandardized /= CapitalCue;
//                            ErrorFunction -= Math.log1p(-AlternateStandardized);
                            ErrorFunction -= Math.log(1.0-AlternateStandardized);
                            ErrorDoneForThisExample = true;
                        }
                    }
//                }
            }
//if (!ErrorDoneForThisExample) {
//    System.out.println("The Error has not been added in for this example [" + ExampleIndex + "]");
//}
            ////// calculate the deltas

            // the deltas for the output layer have already been done...
            // now for the hidden layers...
            if (CalculateGradientFlag) {
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

                    if (DoNotReportAllActivationsFlag) {
                        ThisActivation = NeuronActivations.getValue(LookupIndex);
                    } else {
                        ThisActivation = NeuronActivations.getValue(ExampleIndex, LookupIndex);
                    }
                    //                System.out.println("#---> MyLayer="+MyLayer+";
                    // MyLayerFirst="+MyLayerFirst+";
                    // NextLayerFirst="+NextLayerFirst);
                    if (UseComplements[(int) LookupIndex]) {
                        DerivativeTemp = ThisActivation * ActivationComplements.getValue(LookupIndex);
                    } else {
                        DerivativeTemp = ThisActivation * (1.0 - ThisActivation);
                    }
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
                        //System.out.println("** -> PosteriorIndex="+PosteriorIndex+"; NextLayerFirst="+NextLayerFirst+";
                        // FirstWeightNumber="+FirstWeightNumber);
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
                    BiasGradientTemp = BiasesGradient.getValue(NeuronIndex, 0);
                    LookupIndex = NeuronIndex - nInputs;
                	if (LookupIndex < 0) {
                    	System.out.println("Example = " + ExampleIndex + "; NeuronIndex = " + NeuronIndex + "; LookupIndex = " + LookupIndex + "; nInputs = " + nInputs +
                    			"; nNeurons = " + nNeurons + "; LayerTable.getValue(0,0) = " + LayerTable.getValue(0,0));
                	}
                    BiasGradientTemp += NNDeltas.getValue(LookupIndex);

                    BiasesGradient.setValue(NeuronIndex, 0, BiasGradientTemp);
                    //                System.out.println("NeuronIndex = " + NeuronIndex + "; nInputs = " + nInputs +
                    //                                   "; nWeights = " + nWeights);
                }
                // figure the gradient for the weights
                for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
                    WeightsGradientTemp = SerializedWeightsGradient.getValue(WeightIndex, 0);

                    //                System.out.println("WeightIndex = " + WeightIndex);
                    NeuronFromLookup = (long) WeightNumberFromToNeuronTable.getValue(WeightIndex, 0) - nInputs;
                    NeuronToLookup = (long) WeightNumberFromToNeuronTable.getValue(WeightIndex, 1) - nInputs;
                    if (NeuronFromLookup < 0) { // we have a weight originating in an input...
//                    	System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
                    	if (ExampleIndex < 0) {
                        	System.out.println("Example = " + ExampleIndex + "; WeightIndex = " + WeightIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
                    	}
                    	if (NeuronFromLookup + nInputs < 0) {
                        	System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
                    	}
                    	if (NeuronToLookup < 0) {
                        	System.out.println("Example = " + ExampleIndex + "; NeuronFromLookup + nInputs = " + (NeuronFromLookup + nInputs) + "; NeuronToLookup = " + NeuronToLookup);
                    	}
                        WeightsGradientTemp += (ExplanatoryVariables.getValue(ExampleIndex, NeuronFromLookup
                                + nInputs) * NNDeltas.getValue(NeuronToLookup));
                    } else { // the weight is *not* originating in an input
                        if (DoNotReportAllActivationsFlag) {
                            WeightsGradientTemp += (NeuronActivations.getValue(NeuronFromLookup) * NNDeltas
                                    .getValue(NeuronToLookup));
                        } else {
                            WeightsGradientTemp += (NeuronActivations.getValue(ExampleIndex, NeuronFromLookup) * NNDeltas
                                    .getValue(NeuronToLookup));
                        }
                    }
                    SerializedWeightsGradient.setValue(WeightIndex, 0, WeightsGradientTemp);
                }
            } // end of if(CalculateGradientFlag)
            
                for (long NeuronDisplay = 0; NeuronDisplay < NNDeltas.getDimensions()[0]; NeuronDisplay++) {
                    /*
                    if (NNDeltas.getValue(NeuronDisplay) == 0.0) {
                    System.out.println("for example ("+ExampleIndex+"), NNDeltas["+NeuronDisplay+"] = " + NNDeltas.getValue(NeuronDisplay));
                    }
                    */
                }
            
        } // end of examples loop
        //      %%%%%%%%%%% the end of the big EXAMPLES loop %%%%%%%%%%%%%%%%%%

        if (CalculateErrorFlag) {
            FracCorrect = (double)nCorrect/(double)nExamples;
        }
        if (CalculateGradientFlag) {
            // now put together the stacked gradient...
            for (long NeuronIndex = (long) LayerTable.getValue(0, 0); NeuronIndex < nNeurons; NeuronIndex++) {
                StackedWeightsBiasesGradient.setValue(NeuronIndex - nInputs + nWeights, 0, BiasesGradient
                        .getValue(NeuronIndex, 0));
            }
            for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
                StackedWeightsBiasesGradient.setValue(WeightIndex, 0, SerializedWeightsGradient.getValue(
                        WeightIndex, 0));
            }
        }

        // ********************** end NEW STUFF...

        this.pushOutput(NeuronActivations, 0);
        this.pushOutput(new Double(ErrorFunction), 1);
        this.pushOutput(new Double(FracCorrect), 2);
        this.pushOutput(BiasesGradient, 3);
        this.pushOutput(SerializedWeightsGradient, 4);
        this.pushOutput(StackedWeightsBiasesGradient, 5);

    }
}