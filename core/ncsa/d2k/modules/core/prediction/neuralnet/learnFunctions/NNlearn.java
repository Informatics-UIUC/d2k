package ncsa.d2k.modules.core.prediction.neuralnet.learnFunctions;

import java.util.*;
import java.io.Serializable;
/*

	NNlearn

	This is the abstract class for learning rate control functions (for learning
	acceleration).  The two typical implementations will likely be those based
	on iterations and time.
*/
abstract public class NNlearn implements Serializable{
/*
	The initial learning rate
*/

	protected final double initAlpha;
/*
	The final learning rate (at last learning iteration)
*/

	protected final double finalAlpha;

/*
	The number of epochs for learning
*/
	public final int totalEpochs;
/*
	the maximum time allowed for learning
*/
	protected double maxTime;
/*
	the range of values the learning rate can take on
*/
	protected final double rangeAlpha;
/*
	Constructor-

	@param: iA- initial learning rate
		fA- final learning rate
		e- the number of epochs
*/

	public NNlearn(double iA, double fA, int e){

		initAlpha=iA;
		finalAlpha=fA;
		totalEpochs=e;
		rangeAlpha=(fA-iA);

	}

/*
	Constructor-

	@param: iA- initial learning rate
		fA- final learning rate
		timeLimit- the maximum time allowed for the entire learning process
*/
	public NNlearn(double iA, double fA, double timeLimit){

		initAlpha=iA;
		finalAlpha=fA;
		maxTime=timeLimit;
		rangeAlpha=fA-iA;
		totalEpochs=0;
	}
/*
	tells the learning algorithm if it should stop iterating based on
	whether the number of epochs is reached or time is up
*/

	abstract public boolean continueLearning();


/*
	tells the learning accelerator that the current epoch is finished
	and a new learning rate should be computed, returns the new learning rate
*/
	abstract public double newLearningRate();

/*
	returns a string of info about the function
*/

	abstract public String getName();
}
