package ncsa.d2k.modules.core.prediction.neuralnet;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;

/*
	Set the biases for the NNModelGenerator
	w/ properties so that a gui doesn't pop up
	each time

	@author pgroves
	*/

public class NNBiasByProps extends ComputeModule
	implements java.io.Serializable, HasNames{

	//////////////////////
	//d2k Props
	////////////////////

	/////////////////////////
	/// other fields
	////////////////////////
	private double[] propArray=new double[13];


	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		SOMixedSolution sol=
			new SOMixedSolution(NNModelGenerator.getRanges());
		sol.setParameters(propArray);
		pushOutput(sol, 0);

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "	Set the biases for the NNModelGenerator"+
				" w/ properties so that a gui doesn't pop up each time";
	}

   	public String getModuleName() {
		return "NN Biases";
	}
	public String[] getInputTypes(){
		String[] s= {};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
		/*	case(0): {
				return "";
			}*/
			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
		/*	case(0): {
				return "";
			}*/
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.optimize.util.SOMixedSolution"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "A solution object defined by properties";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Biases";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setActFunction(double d){
		propArray[NNModelGenerator.ACTIVATION_FUNCTION]=d;
	}
	public double getActFunction(){
		return propArray[NNModelGenerator.ACTIVATION_FUNCTION];
	}
	public void setLearningMethod(double d){
		propArray[NNModelGenerator.UPDATE_FUNCTION]=d;
	}
	public double getLearningMethod(){
		return propArray[NNModelGenerator.UPDATE_FUNCTION];
	}
	public void setEpochs(double d){
		propArray[NNModelGenerator.EPOCHS]=d;
	}
	public double getEpochs(){
		return propArray[NNModelGenerator.EPOCHS];
	}
	public void setSeed(double d){
		propArray[NNModelGenerator.SEED]=d;
	}
	public double getSeed(){
		return propArray[NNModelGenerator.SEED];
	}
	public void setWeightInitRange(double d){
		propArray[NNModelGenerator.WEIGHT_INIT_RANGE]=d;
	}
	public double getWeightInitRange(){
		return propArray[NNModelGenerator.WEIGHT_INIT_RANGE];
	}
	public void setLearnRateFunction(double d){
		propArray[NNModelGenerator.LEARNING_RATE_FUNCTION]=d;
	}
	public double getLearnRateFunction(){
		return propArray[NNModelGenerator.LEARNING_RATE_FUNCTION];
	}
	public void setInitLearnRate(double d){
		propArray[NNModelGenerator.INITIAL_LEARNING_RATE]=d;
	}
	public double getInitLearnRate(){
		return propArray[NNModelGenerator.INITIAL_LEARNING_RATE];
	}
	public void setFinalLearnRate(double d){
		propArray[NNModelGenerator.FINAL_LEARNING_RATE]=d;
	}
	public double getFinalLearnRate(){
		return propArray[NNModelGenerator.FINAL_LEARNING_RATE];
	}
	public void setHiddenLayerCount(double d){
		propArray[NNModelGenerator.HIDDEN_LAYERS]=d;
	}
	public double getHiddenLayerCount(){
		return propArray[NNModelGenerator.HIDDEN_LAYERS];
	}
	public void setNodesInLayer1(double d){
		propArray[NNModelGenerator.NODES_IN_LAYER_01]=d;
	}
	public double getNodesInLayer1(){
		return propArray[NNModelGenerator.NODES_IN_LAYER_01];
	}

	public void setNodesInLayer2(double d){
		propArray[NNModelGenerator.NODES_IN_LAYER_02]=d;
	}
	public double getNodesInLayer2(){
		return propArray[NNModelGenerator.NODES_IN_LAYER_02];
	}

	public void setNodesInLayer3(double d){
		propArray[NNModelGenerator.NODES_IN_LAYER_03]=d;
	}
	public double getNodesInLayer3(){
		return propArray[NNModelGenerator.NODES_IN_LAYER_03];
	}

	public void setNodesInLayer4(double d){
		propArray[NNModelGenerator.NODES_IN_LAYER_04]=d;
	}
	public double getNodesInLayer4(){
		return propArray[NNModelGenerator.NODES_IN_LAYER_04];
	}


	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}
	*/
}







