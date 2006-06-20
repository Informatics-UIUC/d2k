package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class SeedNNwithLogit extends ComputeModule {

  public String getModuleName() {
    return "SeedNNwithLogit";
  }


  public String getModuleInfo() {
    return "This module attempts to seed Neural Net weights for a single " +
			"hidden layer network with those obtained from a no-hidden-layers " +
			"network (aka MultiNomialLogit). This particular implementation requires " +
			"having at least as many hidden units as input neurons. It will require a " +
			"small number for scaling the random noise addition to the parameters, a " +
			"large number for crunching everything down to the linear regions of the " +
			"logit squashing function, and a random seed. THERE IS NO IDIOT-PROOFING IN HERE!";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
      	return "MNLSerializedWeights";
  	  case 2:
  	  	return "MNLBiases";
  	  case 3:
  	  	return "nWeights";
  	  case 4:
  	  	return "SmallNumber";
  	  case 5:
  	  	return "LargeNumber";
  	  case 6:
  	  	return "RandomSeed";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
    case 0:
        return "LayerTable";
      case 1:
      	return "MNLSerializedWeights";
  	  case 2:
  	  	return "MNLBiases";
  	  case 3:
  	  	return "nWeights";
  	  case 4:
  	  	return "SmallNumber";
  	  case 5:
  	  	return "LargeNumber";
  	  case 6:
  	  	return "RandomSeed";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
		"java.lang.Long",
		"java.lang.Double",
		"java.lang.Double",
		"java.lang.Long",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SerializedWeights";
      case 1:
        return "Biases";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
        return "SerializedWeights";
      case 1:
        return "Biases";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {
/*    return "LayerTable";
  case 1:
  	return "MNLSerializedWeights";
	  case 2:
	  	return "MNLBiases";
	  case 3:
	  	return "SmallNumber";
	  case 4:
	  	return "LargeNumber";
	  case 5:
	  	return "RandomSeed";
*/
  	
  	
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix MNLSerializedWeights = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix MNLBiases = (MultiFormatMatrix)this.pullInput(2);
    long nWeights = ((Long)this.pullInput(3)).longValue();
    double SmallNumber = ((Double)this.pullInput(4)).doubleValue();
    double LargeNumber = ((Double)this.pullInput(5)).doubleValue();
    long RandomSeed = ((Long)this.pullInput(6)).longValue();

    long nInputs = (long)LayerTable.getValue(0,0);
    long nHidden = (long)LayerTable.getValue(1,0);
    long nOutputs = (long)LayerTable.getValue(2,0);
    long nNeurons = nInputs + nHidden + nOutputs;
    long FirstOutput = nNeurons - nOutputs;
    long WeightNumber = -1;
    long OldWeightNumber = -2;
    
    long BiasToLookUp = -1;
    double OriginalBias = 0;
    double SillyWeight = 1.0/LargeNumber;
    double BiasShift = 0;
    
    if (nHidden < nInputs) {
    	System.out.println("There must be at least as many hidden units [" + nHidden + "] as inputs [" + nInputs + "]");
    	throw new Exception();
    }
    
    Random RandomNumberGenerator = new Random(RandomSeed);

    MultiFormatMatrix SerializedWeights = new MultiFormatMatrix(1,new long[] {nWeights,1});
    MultiFormatMatrix Biases = new MultiFormatMatrix(1,new long[] {nNeurons,1});

    
    // the weights to the hidden layer...
    for (long IntermediateIndex = 0; IntermediateIndex < nInputs; IntermediateIndex++) {
    	SerializedWeights.setValue(IntermediateIndex + IntermediateIndex*nInputs,0,SillyWeight);
    }
    
    // the weights to the output layer...
    for (long IntermediateIndex = 0; IntermediateIndex < nInputs; IntermediateIndex++) {
    for (long OutputIndex = 0; OutputIndex < nOutputs; OutputIndex++) {
    
//    	WeightNumber = nInputs*nHidden + OutputIndex + OutputIndex*nOutputs;
    	WeightNumber = nInputs*nHidden + OutputIndex + IntermediateIndex*nOutputs;
//    	System.out.println("nInputs = " + nInputs + "; nHidden = " + nHidden + "; IntermediateIndex = " + IntermediateIndex + "; nOutputs = " + nOutputs);
//    	OldWeightNumber = OutputIndex + nInputs*OutputIndex;
    	OldWeightNumber = OutputIndex + IntermediateIndex*nOutputs;
    	SerializedWeights.setValue(WeightNumber,0,
    			4*LargeNumber*MNLSerializedWeights.getValue(OldWeightNumber,0));
    }
    }
    
    // the output biases
    for (long OutputIndex = 0; OutputIndex < nOutputs; OutputIndex++) {
    	// calculate the shift...
    	BiasShift = 0;
    	for (long InputIndex = 0; InputIndex < nInputs; InputIndex++) {
    		WeightNumber = OutputIndex + InputIndex*nOutputs;
    		BiasShift += MNLSerializedWeights.getValue(WeightNumber,0);
//    		System.out.println("WeightNumber = " + WeightNumber + "; OutputIndex = " + OutputIndex + "; InputIndex = " + InputIndex +
//    				"; MNLSerializedWeights[WeightNumber][0] = " + MNLSerializedWeights.getValue(WeightNumber,0) + "; BiasShift = " + BiasShift);
    	}
//    	Biases.setValue(5,1,3);
//    	System.out.println("MNLBiases[OutputIndex+nInputs][0] = " + MNLBiases.getValue(OutputIndex + nInputs,0) + "; OutputIndex = " + OutputIndex + "; nInputs = " + nInputs);
    	BiasToLookUp = OutputIndex + nInputs;
    	OriginalBias = MNLBiases.getValue(BiasToLookUp, 0);
//    	System.out.println("BiasToLookUp = " + BiasToLookUp + "; OriginalBias = " + OriginalBias);
    	Biases.setValue(FirstOutput + OutputIndex,0, (OriginalBias - 2*LargeNumber*BiasShift));
    }

    // tack on the random noise...
    for (long BiasIndex = nInputs; BiasIndex < nNeurons; BiasIndex++) {
    	Biases.setValue(BiasIndex,0,Biases.getValue(BiasIndex,0) + SmallNumber*RandomNumberGenerator.nextGaussian());
    }
    for (long WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
    	SerializedWeights.setValue(WeightIndex,0,SerializedWeights.getValue(WeightIndex,0)
    			+ SmallNumber*RandomNumberGenerator.nextGaussian());
    }
    
    

    this.pushOutput(SerializedWeights, 0);
    this.pushOutput(Biases, 1);
  }

}


