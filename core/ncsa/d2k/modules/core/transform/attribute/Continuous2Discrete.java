package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**************************************************
**  Continuous2Discrete.java
**
**	Takes a NumericColumn, puts the data in bins
**	(will assume/force the numbers to be doubles)
**  Outputs a VT with one column being bin midpoints
**  and another the probability for that bin
**	@author: Peter Groves
**************************************************/

public class Continuous2Discrete extends ncsa.d2k.core.modules.ComputeModule {


	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The column of continously distributed data  ";
			default: return "No such input";
		}
	}


	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.NumericColumn"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      A VerticalTable, the first column is the midpoints of the bins, the second column is probability of that bin    ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Takes a column (any NumericColumn) of data and bins it according to how     the Properties are set. The output is a 2 column VT with the first column     being the midpoints of the bins and the second the probabilty (number of     points in the bin over total points) of that bin. PROPS: binInterval-the     range of numbers each bin should hold, numBins-the number of bins to make,     useNumBins- to have the number of bins determined by the numBins property,     set to true, to use binInterval, set to false  </body></html>";
	}


	/*the number of bins to make, evenly sized over the range of the input data*/
	int numBins=1;

	/*sets the size of each individual bin, the number of bins will be determined by how many
	bins are needed to span all the data*/
	double binInterval=1.0;

	/*whether to use numBins(1) or binInterval(0) in determining bin size*/
	boolean useNumBins=true;


	//accessors for the properties
	public void setNumBins(int i){
		numBins=i;
	}

	public int getNumBins(){
		return numBins;
	}

	public void setBinInterval(double d){
		binInterval=d;
	}

	public double getBinInterval(){
		return binInterval;
	}

	public void setUseNumBins(boolean b){
		useNumBins=b;
	}

	public boolean getUseNumBins(){
		return useNumBins;
	}





  ////////////////////
  //THE ALMIGHTY DOIT
  ///////////////////
	public void doit () throws Exception {

	NumericColumn continuousData=(NumericColumn)pullInput(0);
	TableImpl discreteTable= (TableImpl)DefaultTableFactory.getInstance().createTable();


	//find what size the bins should be and what the number of bins will be

	if(useNumBins){
		binInterval=(continuousData.getMax()-continuousData.getMin())/numBins;
	}else{
		numBins=(int)Math.ceil((continuousData.getMax()-continuousData.getMin())/binInterval);
	}

	//arrays of output data
	double[] midpoints=new double[numBins];
	double[] probs=new double[numBins];

	//put the data in order, but do it to a copy so we don't screw anybody up
	continuousData=(NumericColumn)continuousData.copy();
	continuousData.sort();

	//set the initial midpoint
	midpoints[0]=continuousData.getMin()+.5*binInterval;

	//set the rest of the midpoints
	for(int j=1; j<numBins; j++){
		midpoints[j]=midpoints[j-1]+binInterval;
	}


	//tally will be reset after each bin and be used to calculate the probabilities, counter will iterate over the entire set
	double tally=0;
	int counter=0;
	double currentMax;

	for(int i=0; i<numBins-1; i++){
		currentMax=midpoints[i]+.5*binInterval;

		while(continuousData.getDouble(counter)<=currentMax){
			tally++;
			counter++;
		}
	//System.out.println(tally+" / "+continuousData.getNumRows());
		probs[i]=tally/continuousData.getNumRows();
	//	System.out.println(probs[i]);
		tally=0;

	}
	//had to do the last one separately or risk an array out of bounds
	probs[numBins-1]=((double)continuousData.getNumRows()-counter)/continuousData.getNumRows();

	DoubleColumn midpointsColumn=new DoubleColumn(midpoints);
	midpointsColumn.setLabel("Midpoints");
	DoubleColumn probsColumn=new DoubleColumn(probs);
	probsColumn.setLabel("Probabilities");

	discreteTable.addColumn(midpointsColumn);
	discreteTable.addColumn(probsColumn);


	pushOutput(discreteTable, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Continous to Discrete data converter";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Continous Data Column";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "PDF/PMF Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}





