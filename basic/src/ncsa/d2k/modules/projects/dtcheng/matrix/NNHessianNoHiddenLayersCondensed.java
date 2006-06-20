package ncsa.d2k.modules.projects.dtcheng.matrix;

// import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNHessianNoHiddenLayersCondensed extends ComputeModule {

    public String getModuleName() {
        return "NNHessianNoHiddenLayersCondensed";
    }

    public String getModuleInfo() {
        return "This module calculates the weight and bias Hessian "
                + "for a feedforward network (from the so-called <i>deltas</i>) "
                + "with softmax final activations and " + "logistic intermediate activations using a "
                + "cross-entropy error criterion. <p> "
                + "This is for a zero-hidden-layers network (<i>i.e.</i>, equivalent "
                + "to a Multinomial Logit discrete choice model)<p>" + "At the moment, there is very limited, "
                + "that is, non-existent idiot-proofing. <p> This is based on B.D. Ripley "
                + "Pattern Recognition and Neural Networks pp. 151-152. But I do the weights "
                + "and biases separately rather than considering the biases to be a +1 neuron. "
                + "Hence, I calculate the Hessian in three blocks: WW, WB, and BB. <p> "
                + "This is to work with the condensed activations which do not have "
                + "the inputs/explanatory variables listed. Unfortunately, this will probably still "
                + "incur rounding errors.";
    }

    public String getInputName(int i) {
        switch (i) {
        case 0:
            return "NeuronActivations";
        case 1:
            return "ExplanatoryVariables";
        case 2:
            return "SerializedWeights";
        case 3:
            return "Biases";
        case 4:
            return "LayerTable";
        case 5:
            return "WeightNumberFromToNeuronTable";
        case 6:
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
            return "ExplanatoryVariables";
        case 2:
            return "SerializedWeights";
        case 3:
            return "Biases";
        case 4:
            return "LayerTable";
        case 5:
            return "WeightNumberFromToNeuronTable";
        case 6:
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
                "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Long", };
        return types;
    }

    public String getOutputName(int i) {
        switch (i) {
        case 0:
            return "Hessian";
        case 1:
            return "FormatOfHessian";
        default:
            return "Error!  No such output.  ";
        }
    }

    public String getOutputInfo(int i) {
        switch (i) {
        case 0:
            return "Hessian";
        case 1:
            return "FormatOfHessian";
        default:
            return "Error!  No such output.  ";
        }
    }

    public String[] getOutputTypes() {
        String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Integer" };
        return types;
    }

public void doit() throws Exception {
	
	    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
	    MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix)this.pullInput(1);
	    MultiFormatMatrix SerializedWeights = (MultiFormatMatrix)this.pullInput(2);
	    MultiFormatMatrix Biases = (MultiFormatMatrix)this.pullInput(3);
	    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(4);
	    MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix)this.pullInput(5);
	    long NumberOfElementsThreshold = ((Long)this.pullInput(6)).longValue();

	// some preparations
	    long nInputs = (long) LayerTable.getValue(0, 0);
//	    long nRelevantOutputs = (long) LayerTable.getValue(LayerTable.getDimensions()[0] - 1, 0);
	    long nWeights = WeightNumberFromToNeuronTable.getDimensions()[0];
	    long nExamples = NeuronActivations.getDimensions()[0];
	    long nNeurons = Biases.getDimensions()[0]; // the last output is irrelevant due to standardization...
	    long nParameters = nWeights + nNeurons - nInputs; // weights and meaningful biases


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
	    
//	    System.out.println("Got to here: before anything difficult... Hessian is defined...");
	
	    // from Ripley...
		long i = -1; // neuron number "from" for the WeightRow connection, has to be the input layer
		long j = -2; // neuron number "to" for the WeightRow connection, has to terminate in the output layer
		long k = -3; // neuron number "from" from the WeightCol connection, has to begin in the input layer
		long ell = -4; // neuron number "to" for the WeightCol connection, has to terminate in the output layer
		
		double HessTemp = -3.2;
	    // all of the connections terminate in the output layer and originate in
	    // the input layer (or biases which come later)

//		System.out.println("nWeights = " + nWeights);
		
		// These are the weight/weight elements...
	    for (long WeightRow = 0; WeightRow < nWeights ; WeightRow++ ) {
	    for (long WeightCol = WeightRow; WeightCol < nWeights ; WeightCol++){
//	        System.out.println("For Loop, before i,j,k,l WNFTNT.getvalues");
//	        System.out.println("WeightNumberFromToNeuronTable dimensions are: " +
//	        		WeightNumberFromToNeuronTable.getDimensions()[0] + "x" +
//					WeightNumberFromToNeuronTable.getDimensions()[0]);
//	        System.out.println("Before: WeightRow=" + WeightRow + "; WeightCol=" + WeightCol);
	    	i = (long)WeightNumberFromToNeuronTable.getValue(WeightRow,0); // this will be an explanatory variable
	    	j = (long)WeightNumberFromToNeuronTable.getValue(WeightRow,1) - nInputs;
	    	k = (long)WeightNumberFromToNeuronTable.getValue(WeightCol,0); // this will be an explanatory variable
	    	ell = (long)WeightNumberFromToNeuronTable.getValue(WeightCol,1) - nInputs;
//    	    System.out.println("WW: row = " + WeightRow + "; col = " + WeightCol + "; i = " + i + "; j = " + j + "; k = " + k + "; ell = " + ell);
//	        System.out.println("For Loop, after i,j,k,l WNFTNT.getvalues");
//	        System.out.println("WeightRow=" + WeightRow + "; WeightCol=" + WeightCol);
//	    	System.out.println("i=" + i +"; j=" + j + "; k=" + k + "; ell=" + ell);
	    	HessTemp = 0;
	    	if (j == ell) { // then we do it with a one in that spot...
	        	for (long Example = 0; Example < nExamples; Example++){
//	                System.out.println("Inside summing Loop, before lookups");
	        		//HessTemp = sum(activation(:,i).*activation(:,k).*activation(:,j).*((j == ell) - activation(:,ell)));
	        		//HessTemp = sum(activation(:,i).*activation(:,k).*activation(:,j).*(1 - activation(:,ell)));
	        		HessTemp += ExplanatoryVariables.getValue(Example,i) * ExplanatoryVariables.getValue(Example,k) * 
								NeuronActivations.getValue(Example,j) * (1 - NeuronActivations.getValue(Example,ell));
//	                System.out.println("Inside summing Loop, after lookups");
	        	}
	        	Hessian.setValue(WeightRow,WeightCol,HessTemp);
	    	}
	    	else { // then we do it with a zero in that spot, that is, without it altogether...
	        	for (long Example = 0; Example < nExamples; Example++){
	        		//HessTemp = sum(activation(:,i).*activation(:,k).*activation(:,j).*( 0 - activation(:,ell)));
//	                System.out.println("Inside summing Loop, before lookups else");
//	        		HessTemp += NeuronActivations.getValue(Example,i) * NeuronActivations.getValue(Example,k) *
//					NeuronActivations.getValue(Example,j) * ( - NeuronActivations.getValue(Example,ell));
	        		// moving the minus sign out of multiplication to the front as a "-="
	        		HessTemp -= ExplanatoryVariables.getValue(Example,i) * ExplanatoryVariables.getValue(Example,k) * 
					NeuronActivations.getValue(Example,j) * ( NeuronActivations.getValue(Example,ell));
//	                System.out.println("Inside summing Loop, after lookups else");
	        	}
//	        	System.out.println("--> WeightRow = " + WeightRow + "; WeightCol = " + WeightCol + "Hessian Dimensions = " + Hessian.getDimensions()[0] + "x" + Hessian.getDimensions()[1]);
	        	Hessian.setValue(WeightRow,WeightCol,HessTemp);
//	        	Hessian.setValue(WeightCol,WeightRow,HessTemp); // live for symmetry...
	    	}
	    }
	    }
	    
	    // These are the weight/bias combinations...
	    long FirstBias = nInputs;
	    long nBiases = nNeurons; // this is the total number of biases listed; those for the inputs are irrelevant
	    long ColumnToStore = -3; // this is a thing to figure out what column of the Hessian we want to store this in...
	    for (long WeightRow = 0; WeightRow < nWeights; WeightRow++) {
	    for (long BiasIndex = FirstBias; BiasIndex < nBiases; BiasIndex++){
	    	i = (long)WeightNumberFromToNeuronTable.getValue(WeightRow,0); // this will be an input neuron
	    	j = (long)WeightNumberFromToNeuronTable.getValue(WeightRow,1) - nInputs;
	        // k is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
	    	ell = BiasIndex - nInputs; // here, the biases are "terminating" in the neuron with the same number...
	    	ColumnToStore = nWeights + BiasIndex - nInputs;
//    	    System.out.println("WB: row = " + WeightRow + "; col = " + ColumnToStore + "; i = " + i + "; j = " + j + "; k = " + k + "; ell = " + ell);
//	    	System.out.println("ColToStore=" + ColumnToStore + "; Row=Weight=" + WeightRow);
	    	HessTemp = 0;
	    	if (j == ell) { // then we do it with a one in that spot...
	        	for (long Example = 0; Example < nExamples; Example++){
	        		// same idea as above, just the activation of "k" is 1
//	        		HessTemp += NeuronActivations.getValue(Example,i) * 1 *
//								NeuronActivations.getValue(Example,j) * (1 - NeuronActivations.getValue(Example,ell));
	        		HessTemp += ExplanatoryVariables.getValue(Example,i) *  
								NeuronActivations.getValue(Example,j) * (1 - NeuronActivations.getValue(Example,ell));
	        	}
	        	Hessian.setValue(WeightRow,ColumnToStore,HessTemp);
	        	Hessian.setValue(ColumnToStore,WeightRow,HessTemp); // less apparent, but still living for symmetry...
	    	}
	    	else { // then we do it wiht a zero in that spot, that is, without it altogether...
	        	for (long Example = 0; Example < nExamples; Example++){
//	        		HessTemp += NeuronActivations.getValue(Example,i) *
//					NeuronActivations.getValue(Example,j) * ( - NeuronActivations.getValue(Example,ell));
	        		// moving the minus sign out of multiplication to the front as a "-="
	        		HessTemp -= ExplanatoryVariables.getValue(Example,i) *  
					NeuronActivations.getValue(Example,j) * ( NeuronActivations.getValue(Example,ell));
	        	}
	        	Hessian.setValue(WeightRow,ColumnToStore,HessTemp);
//	        	Hessian.setValue(ColumnToStore,WeightRow,HessTemp); // live for symmetry...
	    	}
	    }
	    }

	    // These are the bias/bias combinations...
	    long RowToStore = -4;
	    ColumnToStore = -3; // this is a thing to figure out what column of the Hessian we want to store this in...
	    for (long BiasIndexRow = FirstBias; BiasIndexRow < nBiases; BiasIndexRow++) {
	    for (long BiasIndexCol = BiasIndexRow; BiasIndexCol < nBiases; BiasIndexCol++){
	    	// i is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
	    	j = BiasIndexRow - nInputs;
	        // k is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
	    	ell = BiasIndexCol - nInputs; // here, the biases are "terminating" in the neuron with the same number...
//    	    System.out.println("BB: i = " + i + "; j = " + j + "; k = " + k + "; ell = " + ell);
	    	RowToStore = nWeights + BiasIndexRow - nInputs;
	    	ColumnToStore = nWeights + BiasIndexCol - nInputs;
//    	    System.out.println("WB: row = " + RowToStore + "; col = " + ColumnToStore + "; i = " + i + "; j = " + j + "; k = " + k + "; ell = " + ell);
	    	HessTemp = 0;
	    	if (j == ell) { // then we do it with a one in that spot...
	        	for (long Example = 0; Example < nExamples; Example++){
	        		// same idea as above, just the activation of "k" is 1
//	        		HessTemp += 1 * 1 *
//								NeuronActivations.getValue(Example,j) * (1 - NeuronActivations.getValue(Example,ell));
	        		HessTemp += NeuronActivations.getValue(Example,j) * (1 - NeuronActivations.getValue(Example,ell));
	        	}
	        	Hessian.setValue(RowToStore,ColumnToStore,HessTemp);
	    	}
	    	else { // then we do it wiht a zero in that spot, that is, without it altogether...
	        	for (long Example = 0; Example < nExamples; Example++){
//	        		HessTemp += 1 * NeuronActivations.getValue(Example,j) * ( - NeuronActivations.getValue(Example,ell));
	        		// moving the minus sign out of multiplication to the front as a "-="
	        		HessTemp -= NeuronActivations.getValue(Example,j) * ( NeuronActivations.getValue(Example,ell));
	        	}
	        	Hessian.setValue(RowToStore,ColumnToStore,HessTemp);
//	        	Hessian.setValue(ColumnToStore,RowToStore,HessTemp); // live for symmetry...
	    	}
	    }
	    }
	    
	    // now do the symmetry thing...
	    for (long HessianIndexA = 0; HessianIndexA < nParameters; HessianIndexA++) {
	    for (long HessianIndexB = HessianIndexA + 1; HessianIndexB < nParameters; HessianIndexB++) {
	        Hessian.setValue(HessianIndexB,HessianIndexA,
	                Hessian.getValue(HessianIndexA,HessianIndexB));
	    }
	    }
	    
	    /*
         * 
         * Hessian = zeros(n_weights + n_neurons - n_Xs) + NaN; % first, i'll do the weight/weight elements, then we'll do bias/bias, then
         * we'll do weight/bias for weight_row = 1:n_weights for weight_col = weight_row:n_weights % figure out which neurons are connected
         * by row and column weights under consideration row_from = Wn_N_ft(weight_row,1); % in Ripley: i row_to = Wn_N_ft(weight_row,2); %
         * in Ripley: j col_from = Wn_N_ft(weight_col,1); % in Ripley: k col_to = Wn_N_ft(weight_col,2); % in Ripley: l % what exactly do
         * they connect? we do this by cases... i = row_from; j = row_to; k = col_from; ell = col_to; % case I: both are output units.. that
         * is, both j & ell are output neurons if ((N_to_L(j,1) == n_layers) & (N_to_L(ell,1) == n_layers)) Hessian(weight_row,weight_col) =
         * sum( ... ... activation(:,i).*activation(:,k).* ... activation(:,j).*((j == ell) - activation(:,ell)) ... ); %
         * Hessian(weight_row,weight_col) = sum( ... % ... % activation(:,row_from).*activation(:,col_from).* ... %
         * activation(:,row_to).*((row_to == col_to) - activation(:,col_to)) ... % );
         * 
         *  % BIASES....
         * 
         * if ((N_to_L(j,1) == n_layers) & (N_to_L(ell,1) == n_layers)) Hessian(bias_row - n_Xs + n_weights,bias_col - n_Xs + n_weights) =
         * sum( ... ... 1.*1.* ... activation(:,j).*((j == ell) - activation(:,ell)) ... ); % Hessian(weight_row,weight_col) = sum( ... %
         * ... % activation(:,row_from).*activation(:,col_from).* ... % activation(:,row_to).*((row_to == col_to) - activation(:,col_to))
         * ... % );
         * 
         *  
         */
	  
	    
	    this.pushOutput(Hessian, 0);
	    this.pushOutput(new Integer(FormatIndex),1);
	  }}