package ncsa.d2k.modules.projects.dtcheng.matrix;

// import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class NNHessianNoHiddenLayersCondensedIntsReordered extends
		ComputeModule {

	public String getModuleName() {
		return "NNHessianNoHiddenLayersCondensedIntsReordered";
	}

	public String getModuleInfo() {
		return "This is the same as the original, just it uses integer arrays for the reference tables... and it runs through the activations only once...<p>"
				+ "This module calculates the weight and bias Hessian "
				+ "for a feedforward network (from the so-called <i>deltas</i>) "
				+ "with softmax final activations and "
				+ "logistic intermediate activations using a "
				+ "cross-entropy error criterion. <p> "
				+ "This is for a zero-hidden-layers network (<i>i.e.</i>, equivalent "
				+ "to a Multinomial Logit discrete choice model)<p>"
				+ "At the moment, there is very limited, "
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
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"[[I", "[[I", "java.lang.Long", };
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
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Integer" };
		return types;
	}

	public void doit() throws Exception {

		MultiFormatMatrix NeuronActivations = (MultiFormatMatrix) this
				.pullInput(0);
		MultiFormatMatrix ExplanatoryVariables = (MultiFormatMatrix) this
				.pullInput(1);
		MultiFormatMatrix SerializedWeights = (MultiFormatMatrix) this
				.pullInput(2);
		MultiFormatMatrix Biases = (MultiFormatMatrix) this.pullInput(3);
		int[][] LayerTable = (int[][]) ((int[][]) this.pullInput(4)).clone();
		int[][] WeightNumberFromToNeuronTable = (int[][]) ((int[][]) this
				.pullInput(5)).clone();
		long NumberOfElementsThreshold = ((Long) this.pullInput(6)).longValue();

		// some preparations
		int nInputs = LayerTable[0][0];
		int nWeights = WeightNumberFromToNeuronTable.length;
		long nExamples = NeuronActivations.getDimensions()[0];
		long nNeurons = Biases.getDimensions()[0]; // the last output is irrelevant due to standardization...
		long nParameters = nWeights + nNeurons - nInputs; // weights and meaningful biases

		// determine the proper format for the Hessian itself
		long NumElements = nParameters * nParameters;
		int FormatIndex = -1; // initialize
		if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
			FormatIndex = 1; // Beware the MAGIC NUMBER!!!
		} else { // not small means big, so go out of core; serialized blocks on disk are best
			FormatIndex = 3; // Beware the MAGIC NUMBER!!!
		}
		MultiFormatMatrix Hessian = new MultiFormatMatrix(FormatIndex,
				new long[] { nParameters, nParameters });

		// from Ripley...
		long i = -1; // neuron number "from" for the WeightRow connection, has to be the input layer
		long j = -2; // neuron number "to" for the WeightRow connection, has to terminate in the output layer
		long k = -3; // neuron number "from" from the WeightCol connection, has to begin in the input layer
		long ell = -4; // neuron number "to" for the WeightCol connection, has to terminate in the output layer

		double HessTemp = -3.2;
		// all of the connections terminate in the output layer and originate in
		// the input layer (or biases which come later)

		for (long ExampleOuterIndex = 0; ExampleOuterIndex < nExamples; ExampleOuterIndex++) {

			// These are the weight/weight elements...
			for (int WeightRow = 0; WeightRow < nWeights; WeightRow++) {
				for (int WeightCol = WeightRow; WeightCol < nWeights; WeightCol++) {
					i = WeightNumberFromToNeuronTable[WeightRow][0]; // this will be an explanatory variable
					j = (WeightNumberFromToNeuronTable[WeightRow][1] - nInputs);
					k = WeightNumberFromToNeuronTable[WeightCol][0]; // this will be an explanatory variable
					ell = (WeightNumberFromToNeuronTable[WeightCol][1] - nInputs);
					HessTemp = Hessian.getValue(WeightRow,WeightCol);
					if (j == ell) { // then we do it with a one in that spot...
//						for (long Example = 0; Example < nExamples; Example++)
							//{
							HessTemp += ExplanatoryVariables.getValue(ExampleOuterIndex,i)
									* ExplanatoryVariables.getValue(ExampleOuterIndex,k)
									* NeuronActivations.getValue(ExampleOuterIndex, j)
									* (1 - NeuronActivations.getValue(ExampleOuterIndex,ell));
//						}
						Hessian.setValue(WeightRow, WeightCol, HessTemp);
					} else { // then we do it with a zero in that spot, that is, without it altogether...
//						for (long Example = 0; Example < nExamples; Example++) {
							HessTemp -= ExplanatoryVariables.getValue(ExampleOuterIndex,
									i)
									* ExplanatoryVariables.getValue(ExampleOuterIndex, k)
									* NeuronActivations.getValue(ExampleOuterIndex, j)
									* (NeuronActivations.getValue(ExampleOuterIndex, ell));
//						}
						Hessian.setValue(WeightRow, WeightCol, HessTemp);
					}
				}
			}

			// These are the weight/bias combinations...
			long FirstBias = nInputs;
			long nBiases = nNeurons; // this is the total number of biases listed; those for the inputs are irrelevant
			long ColumnToStore = -3; // this is a thing to figure out what column of the Hessian we want to store this in...
			for (int WeightRow = 0; WeightRow < nWeights; WeightRow++) {
				for (long BiasIndex = FirstBias; BiasIndex < nBiases; BiasIndex++) {
					i = WeightNumberFromToNeuronTable[WeightRow][0]; // this will be an input neuron
					j = (WeightNumberFromToNeuronTable[WeightRow][1] - nInputs);
					// k is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
					ell = BiasIndex - nInputs; // here, the biases are "terminating" in the neuron with the same number...
					ColumnToStore = nWeights + BiasIndex - nInputs;
					HessTemp = Hessian.getValue(WeightRow,ColumnToStore);
					if (j == ell) { // then we do it with a one in that spot...
//						for (long Example = 0; Example < nExamples; Example++) {
							// same idea as above, just the activation of "k" is 1
							HessTemp += ExplanatoryVariables.getValue(ExampleOuterIndex,i)
									* NeuronActivations.getValue(ExampleOuterIndex,j)
									* (1 - NeuronActivations.getValue(ExampleOuterIndex,
											ell));
//						}
						Hessian.setValue(WeightRow, ColumnToStore, HessTemp);
						Hessian.setValue(ColumnToStore, WeightRow, HessTemp); // less apparent, but still living for symmetry...
					} else { // then we do it wiht a zero in that spot, that is, without it altogether...
//						for (long Example = 0; Example < nExamples; Example++) {
							//	        		HessTemp += NeuronActivations.getValue(Example,i) *
							//					NeuronActivations.getValue(Example,j) * ( - NeuronActivations.getValue(Example,ell));
							// moving the minus sign out of multiplication to the front as a "-="
							HessTemp -= ExplanatoryVariables.getValue(ExampleOuterIndex,
									i)
									* NeuronActivations.getValue(ExampleOuterIndex, j)
									* (NeuronActivations.getValue(ExampleOuterIndex, ell));
//						}
						Hessian.setValue(WeightRow, ColumnToStore, HessTemp);
					}
				}
			}

			// These are the bias/bias combinations...
			long RowToStore = -4;
			ColumnToStore = -3; // this is a thing to figure out what column of the Hessian we want to store this in...
			for (long BiasIndexRow = FirstBias; BiasIndexRow < nBiases; BiasIndexRow++) {
				for (long BiasIndexCol = BiasIndexRow; BiasIndexCol < nBiases; BiasIndexCol++) {
					// i is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
					j = BiasIndexRow - nInputs;
					// k is absent because it would be a "+1 permanently" neuron, but i don't list them, i hardcode them...
					ell = BiasIndexCol - nInputs; // here, the biases are "terminating" in the neuron with the same number...
					RowToStore = nWeights + BiasIndexRow - nInputs;
					ColumnToStore = nWeights + BiasIndexCol - nInputs;
					HessTemp = Hessian.getValue(RowToStore,ColumnToStore);
					if (j == ell) { // then we do it with a one in that spot...
//						for (long Example = 0; Example < nExamples; Example++) {
							// same idea as above, just the activation of "k" is 1
							HessTemp += NeuronActivations.getValue(ExampleOuterIndex, j)
									* (1 - NeuronActivations.getValue(ExampleOuterIndex,
											ell));
//						}
						Hessian.setValue(RowToStore, ColumnToStore, HessTemp);
					} else { // then we do it wiht a zero in that spot, that is, without it altogether...
						for (long Example = 0; Example < nExamples; Example++) {
							HessTemp -= NeuronActivations.getValue(Example, j)
									* (NeuronActivations.getValue(Example, ell));
						}
						Hessian.setValue(RowToStore, ColumnToStore, HessTemp);
					}
				}
			}

		}

		// now do the symmetry thing...
		for (long HessianIndexA = 0; HessianIndexA < nParameters; HessianIndexA++) {
			for (long HessianIndexB = HessianIndexA + 1; HessianIndexB < nParameters; HessianIndexB++) {
				Hessian.setValue(HessianIndexB, HessianIndexA, Hessian
						.getValue(HessianIndexA, HessianIndexB));
			}
		}

		this.pushOutput(Hessian, 0);
		this.pushOutput(new Integer(FormatIndex), 1);
	}
}