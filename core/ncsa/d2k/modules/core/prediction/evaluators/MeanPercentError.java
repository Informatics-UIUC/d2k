package ncsa.d2k.modules.core.prediction.evaluators;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
/**
	MeanPercentError.java

	For every output feature, sums the percent errors, divides by numTestExamples, and 
	outputs a VT with the output feature names and corresponding errors

	@author Peter Groves 
	6/26/01
	
 	
*/
public class MeanPercentError extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"error metric\">    <Text>A Table with A column for each outputfeature and a row for every iteration of crossvalidation  </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Average Over All CrossVal\">    <Text>The average error  </Text>  </Info></D2K>";

			default: return "No such output";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"M % Evaluator\">    <Text>Takes a TestTable with the prediction columns filled in returns a table with mean percent errors."+props+" </Text>  </Info></D2K>";
	}

	
	protected void computeError(TestTable tt, int m){

		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();
		
		//store an mabs error for each output feature, make sure to initialize to zero
		double[] mpe = new double[columns];
		for (int i=0; i<mpe.length; i++){
			mpe[i]=0;
		}
		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();
		
		for (int j = 0 ; j < columns ; j++){
			for (int i = 0 ; i < rows ; i++){
				double row_error;
				double prediction=tt.getDouble (i, ttPreds[j]);
				double target=tt.getDouble (i, ttOuts[j]);
				if(printResults){
				System.out.println("==="+i+" t,p: "+target+","+prediction);
				}
				row_error = Math.abs ((target-prediction)/target);
				mpe[j] += row_error;
			}
		}
		
		for (int j=0; j<mpe.length; j++){
			mpe[j] = (mpe[j] / rows)*100;
			metrics.setDouble(mpe[j], m, j);

		}


	}
}

