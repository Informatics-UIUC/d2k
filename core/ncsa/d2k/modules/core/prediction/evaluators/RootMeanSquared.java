package ncsa.d2k.modules.core.prediction.evaluators;
import ncsa.d2k.infrastructure.modules.ComputeModule;
import ncsa.d2k.modules.TransformationModule;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.ArrayList;
import java.io.Serializable;
/**
	RootMeanSquaredjava

	 this module computes the rms for each predicted feature independently.
	every outputfeature gets a row with 'n' errors, one for each crossValidation
	generated model or just 1 if crossValidate is false

	@author Peter Groves
	7/30/01


*/
public class RootMeanSquared extends ncsa.d2k.infrastructure.modules.ComputeModule implements Serializable
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"example set\">    <Text>This is the example set, a TestTable with the prediction columns filled in. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"nl\">    <Text>How many time this module will need to fire </Text>  </Info></D2K>";

			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.TestTable",
			"java.lang.Integer"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"error metric\">    <Text>A Table with each column the rms errors for an output feature </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"average\">    <Text>The average of all the cross-validation trials </Text>  </Info></D2K>";

			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"java.lang.Double"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"RMSEvaluator\">    <Text>Given an example set and a model, check the models performance against the data, returning the room mean squared error for each output feature <i>separately</i> in the output table. "+props+"</Text>  </Info></D2K>";
	}
	/*this is here so subclasses can put it in their getModuleInfo*/
	protected String props="PROPS: untransformFirst - applies any untransforms to the data after prediction and "+
							"before calculating any errors. crossValidate - if true, will wait for 'n ' TestTables,"+
							" if false, will just wait for one and not wait for an Integer to be passed into input(1)"+
							". printResults - will print each target/prediction pair to System.out";



	public boolean untransformFirst=true;

	public void setUntransformFirst(boolean b){
		untransformFirst=b;
	}
	public boolean getUntransformFirst(){
		return untransformFirst;
	}

	public boolean crossValidate=true;
	public boolean getCrossValidate(){
		return crossValidate;
	}
	public void setCrossValidate(boolean b){
		crossValidate=b;
	}
	public boolean printResults=false;
	public boolean getPrintResults(){
		return printResults;
	}
	public void setPrintResults(boolean b){
		printResults=b;
		}

	public boolean isReady(){
		if((!crossValidate)&&(inputFlags[0]>0)){
			return true;
		}
		if(crossValidate&&(tts!=null)&&(inputFlags[0]>0)){
			return true;
		}
		return super.isReady();
	}
	public void beginExecution(){
		tts=null;

	}

	protected TestTable[] tts;
	protected Table metrics;

	int n=1;

	protected void setupTestTables(){
		if(crossValidate){
			n=((Integer)pullInput(1)).intValue();
			tts=new TestTable[n];
		}else{
			n=1;
			tts=new TestTable[1];
		}
	}
	protected void setupMetrics(){
		int lastIndex=n-1;
		metrics= TableFactory.createTable(tts[lastIndex].getNumOutputFeatures());
		for(int i=0; i<metrics.getNumColumns();i++){
			metrics.setColumn(new DoubleColumn(n), i);
			metrics.setColumnLabel(tts[lastIndex].getColumnLabel(tts[lastIndex].getOutputFeatures()[i]), i);
		}
	}

	protected void untransformTable(TestTable tt){
		ArrayList transforms=tt.getTransformations();
		//make sure to untransform in reverse order
		int origSize=transforms.size()-1;
		//System.out.println(origSize);
		for(int i=origSize; i>=0; i--){
			tt=(TestTable)((TransformationModule)(transforms.get(i))).untransform(tt);
		}
	}
	protected void computeError(TestTable tt, int m){
		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();

		//store an rms error for each output feature, make sure to initialize to zero
		double[] rmse = new double[columns];
		for (int i=0; i<rmse.length; i++){
			rmse[i]=0;
		}

		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		for (int j = 0 ; j < columns ; j++){
			for (int i = 0 ; i < rows ; i++){
				double row_error;
				double prediction=tt.getDouble (i, ttPreds[j]);
				double target=tt.getDouble (i, ttOuts[j]);
				if (printResults){
					System.out.println("T,P,: "+target+","+prediction);
				}
				row_error = Math.abs (target-prediction);
				row_error *= row_error;
				rmse[j] += row_error;
			}
		}

		for (int j=0; j<rmse.length; j++){
			rmse[j] = rmse[j] / rows;
			rmse[j]=Math.sqrt(rmse[j]);
			//put the error in the verticalTable
			metrics.setDouble(rmse[j], m, j);
		}
	}

	/*
		finds the average of the numbers in the first column
		of a tabl
	*/
	public Double average(Table vt){
		double avg=0;
		for(int i=0;i<vt.getNumRows(); i++){
			avg+=vt.getDouble(i, 0);
		}
		avg/=vt.getNumRows();
		return new Double(avg);
	}


	/*
		does amazing things, really
	*/
	public void doit () throws Exception{

		if(tts==null){
			setupTestTables();
		}
		Object table = this.pullInput (0);
		if (table instanceof TestTable) {
			tts [n-1] = (TestTable) table;
		}else {
			tts [n-1] = (TestTable) ((ExampleTable)table).getTestTable ();
		}

		//if this is the first run (we're putting the testTable in the last index of tts)
		//we need to have one TestTable in so that we can get outputFeature names
		if(tts.length==n){
			setupMetrics();
		}
		//decrement n
		n--;
		//to be done the last fire
		if(n==0){
			for(int m=0; m<tts.length; m++){
				TestTable tt=tts[m];

				if (untransformFirst){
					untransformTable(tt);
				}
				// Now we have the predicted values, compare them to the real values.

				computeError(tt, m);
			}
			pushOutput(metrics, 0);
			pushOutput(average(metrics), 1);
			tts=null;

		}

	}
}

