package ncsa.d2k.modules.core.prediction.neuralnet.updateFunctions;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.neuralnet.*;
import ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions.*;
import ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions.*;


/*  Standard Batch BP

		Creates a neural net. calculates all weight updates for
		an iteration, then updates the weights
*/

public class StandardBatchBP extends StandardIncrementalBP{

	private Table runningUpdates;

	public  StandardBatchBP(ncsa.d2k.modules.core.prediction.neuralnet.NNModelGenerator.NNModel mod){
		super(mod);
		runningUpdates=weights.copy();
		//make it a deep copy
		for (int k=0; k<runningUpdates.getNumColumns();k++){
			for(int h=0; h<runningUpdates.getColumn(k).getNumRows(); h++){
				runningUpdates.setObject(((DoubleColumn)(weights.getObject(h,k))).copy(), h, k);
			}
		}
		wipeUpdates();
	}

	public void create(){
		while(learnFn.continueLearning()){
			alpha=learnFn.newLearningRate();

			for(int d=0; d<data.getNumTrainExamples(); d++){
				runExample(d);
			}
			batchUpdate();
		}
	}
	/*
		after runExample calculates the weight change, this will add it onto
		the runningTally here. The algorithm is split up like this so that a batch
		method can override the superclasses' and store the weight change instead of immediately
		making the change


		@param dw the weight change calculated
		@param i the 'from' node of the weight
		@param j the 'to' node
		@param l the layer of the 'to' node

		*/
	public void UseWeightUpdate(double dw, int i, int j, int l){
		double[] inWeights=(double[])((DoubleColumn)runningUpdates.getObject(j, l)).getInternal();
		inWeights[i]+=dw;
	}

	/*
		set all the running weight tallies to zero
	*/

	public void wipeUpdates(){
		for(int i=0; i<runningUpdates.getNumColumns(); i++){
			for(int j=0; j<runningUpdates.getColumn(i).getNumRows(); j++){
				double[] a=(double[])((DoubleColumn)runningUpdates.getObject(j, i)).getInternal();
				for(int k=0; k<a.length; k++){
						a[k]=0;
				}
			}
		}
	}
	/*
		updates the weights in the model with the weight changes in the running tally
	*/

	public void batchUpdate(){
		for(int i=0; i<runningUpdates.getNumColumns(); i++){
			for(int j=0; j<runningUpdates.getColumn(i).getCapacity(); j++){
				double[] a=(double[])((DoubleColumn)runningUpdates.getObject(j, i)).getInternal();
				double[] b=(double[])((DoubleColumn)weights.getObject(j, i)).getInternal();

				for(int k=0; k<a.length; k++){
					b[k]+=a[k];
				}
			}
		}
	}

	public String getName(){
		return ("Standard Batch Back Prop");
	}

}


