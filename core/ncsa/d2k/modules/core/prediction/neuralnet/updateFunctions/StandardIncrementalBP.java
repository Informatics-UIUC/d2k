package ncsa.d2k.modules.core.prediction.neuralnet.updateFunctions;

import ncsa.d2k.modules.core.prediction.neuralnet.*;
import ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions.*;
import java.io.Serializable;

import ncsa.d2k.modules.core.datatype.table.*;
/*
	StandardIncrementalBP

		Creates a neural net. Updates the weights after every example is presented to it
		using a regular backprop gradient descent method
*/


public class StandardIncrementalBP extends NNupdate implements Serializable{

	protected double alpha;
	private boolean bugg=true;

	public  StandardIncrementalBP(ncsa.d2k.modules.core.prediction.neuralnet.NNModelGenerator.NNModel mod){
		super(mod);
	}


	public void create(){

		while(learnFn.continueLearning()){
			alpha=learnFn.newLearningRate();

			for(int d=0;d<data.getNumTrainExamples(); d++){

				runExample(d);
			}
		}


	}

	/*
		after runExample calculates the weight change, this will add it onto
		the weight here. The algorithm is split up like this so that a batch
		method can override this and store the weight change instead of immediately
		making the change

		@param dw the weight change calculated
		@param i the 'from' node of the weight
		@param j the 'to' node
		@param l the layer of the 'to' node
	*/

	public void useWeightUpdate(double dw, int i, int j, int l){

		//double[] inWeights=(double[])((DoubleColumn)weights.getObject(j, l)).getInternal();
		//inWeights[i]+=dw;
		DoubleColumn dc=(DoubleColumn)weights.getObject(j, l);
		dc.setDouble(dc.getDouble(i)+dw, i);
	}

	public void runExample(int g){

		model.compute(g, computedResults/*, true*/);
		double d;
		double c;
		double thisDelta;

		//compute output deltas
		final double span=data.getNumOutputFeatures()+1;
		final int lastLayer=deltas.getNumColumns()-1;
		for (int t=1; t<span; t++){//don't forget there is that -1 in index 0
			d=(data.getDouble(g, data.getOutputFeatures()[t-1])-computedResults.getDouble(g, t-1));

			d*=act.derivativeOf(sums.getDouble(t, lastLayer));
			deltas.setDouble(d, t, lastLayer);

			/*if((Double.isNaN(d)||Double.isInfinite(d)) && bugg){
				System.out.println("UPDATE: err: delta output="+d+" t="+t+" g="+g);
				bugg = false;
			}*/
			//update weights to outputs

			//if 1 or more hidden layers
			if(lastLayer>0){
				int lastHiddenLayerIndex=lastLayer-1;//activations.getNumColumns()-2;
				int lastLayerNodes=activations.getColumn(lastHiddenLayerIndex).getCapacity();
				for(int n=0; n<lastLayerNodes; n++){
					c=activations.getDouble(n, lastHiddenLayerIndex);
					c*=alpha*d;
					useWeightUpdate(c, n, t, lastLayer);
					/*if((Double.isNaN(c)||Double.isInfinite(c)) && bugg){
						System.out.println("UPDATE: err hiddenlayer: c="+c+" t="+t+" g="+g+" n="+n);
						bugg = false;
					}*/
				}
			}
			//if no hidden layers
			else{
				c=model.bias;
				c*=alpha*d;
				useWeightUpdate(c, 0, t, 0);

				int weightsCount=weights.getColumn(0).getCapacity();
				for(int n=1; n<weightsCount; n++){
					c=data.getTrainInputDouble(g, n);
					c*=alpha*d;
					useWeightUpdate(c, n, t, 0);
				}
			}
		}

		//update all other weights except those from inputs
		int nodeInLayerCount=0;
		int nodesInNextLayer=0;
		int nodesInPrevLayer=0;

		for (int layer=weights.getNumColumns()-2; layer>0; layer--){
			//n is this node
			nodeInLayerCount=weights.getColumn(layer).getNumRows();
			for(int n=1; n<nodeInLayerCount; n++){
				thisDelta=0;
				//i is next node
				nodesInNextLayer=weights.getColumn(layer+1).getNumRows();
				for(int i=1; i<nodesInNextLayer; i++){
					thisDelta+= ((DoubleColumn)weights.getObject(i, layer+1)).getDouble(n)*deltas.getDouble(i, layer+1);
				}
				thisDelta *= act.derivativeOf(sums.getDouble(n, layer));
				deltas.setDouble(thisDelta, n, layer);

				//k is previous node
				nodesInPrevLayer=sums.getColumn(layer-1).getCapacity();
				for(int k=0; k<nodesInPrevLayer;k++){
					d= alpha*activations.getDouble(k, layer-1)*thisDelta;
					useWeightUpdate(d, k, n, layer);

				}
			}
		}

		//update weights inputs->firstlayer (if not already done in outputs section)

		if(/*lastLayer>0*/activations.getNumColumns()>1){
			//j is the 'to' node (this node)
			nodeInLayerCount=weights.getColumn(0).getCapacity();
			nodesInNextLayer=weights.getColumn(1).getCapacity();
			for (int j=1; j</*weights.getColumn(0).getCapacity()*/nodeInLayerCount; j++){
				thisDelta=0;
				//k is the next node
				for(int k=1; k<nodesInNextLayer/*weights.getColumn(1).getCapacity()*/; k++){
					thisDelta+=((DoubleColumn)weights.getObject(k, 1)).getDouble(j)*deltas.getDouble(k, 1);
				}
				thisDelta*=act.derivativeOf(sums.getDouble(j, 0));
				//??may not need to bother executing this line
				deltas.setDouble(thisDelta, j, 0);

				d=alpha*model.bias*thisDelta;
				useWeightUpdate(d, 0, j, 0);

				//i is the input data
				int inputCount=data.getNumInputFeatures();
				for(int i=0; i<inputCount; i++){
					d=alpha*data.getTrainInputDouble(g,i)*thisDelta;
					//there is no 'filler' bias node among the inputs(above)
					//so the indices are off by one for getting the data
					//and the corresponding weight, as seen in the 'i+1' below
					useWeightUpdate(d, i+1, j, 0);
				}
			}
		}
	}

	public String getName(){
		return("Standard Incremental Back Prop");
	}
}

