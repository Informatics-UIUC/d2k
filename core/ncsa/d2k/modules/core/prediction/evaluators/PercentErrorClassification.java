package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	PercentErrorClassification.java

	Takes the predictions of a model and computes how many of the <i>single</i>
	output feature predictions were incorrect.  The predictions should be in the form produced by
	the module ScalarizeNominals, with a single output feature being converted into a series
	of 'k' boolean columns with only the class for that row set to true

	@author pgroves
	7/30/01
*/
public class PercentErrorClassification extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{



	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Results\">    <Text>A VT with one column indicating what percent were incorrectly classified </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}


	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"%ErrClass\">    <Text>Takes the predictions of a model and computes how many of the <i>single</i> output feature predictions were incorrect.  The predictions should be in the form produced by the module ScalarizeNominals, with a single output feature being converted into a series of 'k' boolean columns with only the class for that row set to true </Text>"+props+"  </Info></D2K>";

	}

	protected void setupMetrics(){
			metrics=(TableImpl)DefaultTableFactory.getInstance().createTable(1);
			metrics.setColumn(new DoubleColumn(n), 0);
			metrics.setColumnLabel("%wrong",  0);
	}

	protected void computeError(TestTable tt, int m){
		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();

		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		//the error tally
		double pse=0.0;

		for (int i = 0 ; i < rows ; i++){

			boolean correct=false;

			//look through the targets and find the correct answer (targ)
			for(int targ=0; targ<ttOuts.length; targ++){
				if(tt.getBoolean(i, ttOuts[targ])){
					//now see if the prediction is the same
					for(int pred=0; pred<ttPreds.length; pred++){
						if(tt.getBoolean(i, ttPreds[pred])){
							//if their the same, give this row a 'correct'
							if(targ==pred){
								correct=true;
							}else{
								correct=false;
							}
						}
					}
				}
			}
			//if wrong, increment the error
			if(!correct){
				pse++;
			}
		}
				//System.out.println("PSE:"+pse+" rows:"+rows);
				//tt.print();
				//make the error tally into a percent error
				pse=pse/rows*100;
				//put it in the output table
				metrics.setDouble(pse, m, 0);

	}


	//doit is in the superclass
}

