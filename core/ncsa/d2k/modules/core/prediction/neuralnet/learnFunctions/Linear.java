package ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions;
import java.io.Serializable;
/*
	Linear
		the learning rate changes over time in a linear fashion
		from initAlpha at epoch one to initAlpha at the final epoch
*/

public class Linear extends NNlearn implements Serializable{

	private int currentEpoch=0;

	public Linear(double iA, double fA, int e){
		super(iA, fA, e);
	}

	public boolean continueLearning(){
		if (currentEpoch==totalEpochs){
			//System.out.println("Linear Learn Function: current (final) epoch:"+currentEpoch);
			return false;
		}
		return true;
	}

	public double newLearningRate (){

		currentEpoch++;

		return (initAlpha+(currentEpoch/totalEpochs)*rangeAlpha);
	}
	public String getName(){
		return ("Linear based on epoch: initAlpha="+initAlpha+" finalAlpha="
		+finalAlpha+" epochs="+totalEpochs);
	}
}

