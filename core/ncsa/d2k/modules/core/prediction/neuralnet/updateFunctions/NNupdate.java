package ncsa.d2k.modules.core.prediction.neuralnet.updateFunctions;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions.*;
import ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions.*;
import java.io.Serializable;

/*
	NNupdate-

	This is the abstract class for updating or creation functions
	for a NN
*/

abstract public class NNupdate implements Serializable{

	public final TableImpl weights;
	public final TableImpl sums;
	public final TableImpl activations;
	public final TableImpl deltas;
	public final ExampleTableImpl data;
	public final NNactivation act;
	public final NNlearn learnFn;
	public ncsa.d2k.modules.core.prediction.neuralnet.NNModelGenerator.NNModel model;
	/* to put calculated outputs in as the computeFn determines them\
	*/
	public TableImpl computedResults;


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

		computedResults= (TableImpl)DefaultTableFactory.getInstance().createTable(data.getNumOutputFeatures());
		for(int i=0; i<data.getNumOutputFeatures(); i++){
			computedResults.setColumn(data.getColumn(data.getInputFeatures()[0]).copy(), i);
		}

	}

	abstract public void create();

	abstract public String getName();
}


