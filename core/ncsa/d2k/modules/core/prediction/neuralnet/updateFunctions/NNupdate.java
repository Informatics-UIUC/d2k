package ncsa.d2k.modules.core.prediction.neuralnet.updateFunctions;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions.*;
import ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions.*;
import java.io.Serializable;

/*
	NNupdate-

	This is the abstract class for updating or creation functions
	for a NN
*/

abstract public class NNupdate implements Serializable{

	public final Table weights;
	public final Table sums;
	public final Table activations;
	public final Table deltas;
	public final ExampleTable data;
	public final NNactivation act;
	public final NNlearn learnFn;
	public ncsa.d2k.modules.core.prediction.neuralnet.NNModelGenerator.NNModel model;
	/* to put calculated outputs in as the computeFn determines them\
	*/
	public Table computedResults;


	public NNupdate(ncsa.d2k.modules.core.prediction.neuralnet.NNModelGenerator.NNModel mod){
		model=mod;
		weights=model.getWeights();
		sums=model.getSums();
		activations=model.getActivations();
		deltas=model.getDeltas();
		data=model.getData();
		act=model.getActivationFunction();
		learnFn=model.getLearnFunction();

		/*
			setup results table, just needs to be right size, everything will be
			written over
		*/

		computedResults=TableFactory.createTable(data.getNumOutputFeatures());
		for(int i=0; i<data.getNumOutputFeatures(); i++){
			computedResults.setColumn(data.getColumn(data.getInputFeatures()[0]).copy(), i);
		}

	}

	abstract public void create();

	abstract public String getName();
}


