package ncsa.d2k.modules.core.prediction.evaluators;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
/**
	MeanAbsoluteError.java

	For every output feature, sums the absolute errors, divides by numTestExamples, and 
	outputs a VT with the output feature names and corresponding errors

	@author Peter Groves 
	7/30/01
	
	
*/
public class MeanAbsoluteError extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{


	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"error metric\">    <Text>A Table with the mean absolute  error for each feature in a separate column, rows are the different crossValidation tests. </Text>  </Info></D2K>";
			default: return "No such output";
		}
	}
	
	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"M ABS Evaluator\">    <Text>Given an example set and a model, check the models performance against the data, returning the Mean Absolute error for each output feature <i>separately</i> in the output table. </Text>"+props+"  </Info></D2K>";
	}

	/*the only function that's here, it does the actual error
	calculation, everything else it gets from the superclass
	*/
	protected void computeError(TestTable tt, int m){
		int rows = tt.getNumTestExamples ();
		int columns = tt.getNumOutputFeatures ();
		
		//store an mabs error for each output feature, make sure to initialize to zero
		double[] mabse = new double[columns];
		for (int i=0; i<mabse.length; i++){
			mabse[i]=0;
		}
		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();
		
		for (int j = 0 ; j < columns ; j++){
			for (int i = 0 ; i < rows ; i++){
				double row_error;
				double prediction=tt.getDouble (i, ttPreds[j]);
				double target=tt.getDouble (i, ttOuts[j]);
				if(printResults){
					System.out.println("=="+i+" t,p: "+target+","+prediction);
				}
				row_error = Math.abs (target-prediction);
				mabse[j] += row_error;
			}
		}
		
		for (int j=0; j<mabse.length; j++){
			mabse[j] = mabse[j] / rows;
			metrics.setDouble(mabse[j], m, j);
		}

	}
}

