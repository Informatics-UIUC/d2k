package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
import ncsa.d2k.modules.TransformationModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/******************

	VTRandomSubset


	This module takes in a VerticalTable and outputs two new VerticalTables
	that are exhaustive of the original.  The fraction that is in each set is
	determined by the property \"splitPercent\".  ExampleTables will have their input/outputFeatures
	and transforms transferred to new ExampleTables but not their test/trainExample
	sets(they'll be left null).  PROPS:  splitPercent should be an integer between 0 and
	100. seed is the seed of the random generator.  A constant seed with the same Table
	will generate identical subsets every time the alg is run. It should be noted
	that rows remain in the same order that they were in in the original table

	@author Peter Groves

*********************/
public class VTRandomSubset extends ncsa.d2k.core.modules.DataPrepModule {

	/*the 1-100 int that indicates the fraction to split into*/
	int splitPercent=75;

	/*the seed for the random function*/
	double seed=0.0;

	public void setSplitPercent(int i){
		splitPercent=i;
	}

	public int getSplitPercent(){
		return splitPercent;
	}

	public void setSeed(double d){
		seed=d;
	}
	public double getSeed(){
		return seed;
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The Vertical Table to split  ";
			default: return "No such input";
		}
	}


	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      The table containing the larger percentage of rows   ";
			case 1: return "      The table containing the smaller percentage of rows   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module takes in a VerticalTable and outputs two new VerticalTables     that are exhaustive of the original. The fraction that is in each set is     determined by the property &quot;splitPercent&quot;. ExampleTables will have their     input/outputFeatures and transforms transferred to new ExampleTables but     not their test/trainExample sets(they'll be left null). PROPS:     splitPercent should be an integer between 0 and 100. seed is the seed of     the random generator. A constant seed with the same Table will generate     identical subsets every time the alg is run. It should be noted that rows     remain in the same order that they were in in the original table  </body></html>";
	}

  /*///////////////////
  //THE ALMIGHTY DOIT
  //////////////////*/
	public void doit () throws Exception {

	TableImpl raw=(TableImpl)pullInput(0);

	int subsetCountSmall=(int)(splitPercent*raw.getNumRows()*.01);
	int subsetCountBig=raw.getNumRows()-subsetCountSmall;

	//make subsetCount1>subsetCount2
	if(subsetCountBig<subsetCountSmall){
		int t=subsetCountSmall;
		subsetCountSmall=subsetCountBig;
		subsetCountBig=t;
	}
	//make splitPercent the small percent
	if(splitPercent>50){
		splitPercent=100-splitPercent;
	}

	TableImpl subsetBig=(TableImpl)raw.getSubset(0, subsetCountBig);

	TableImpl subsetSmall=(TableImpl)raw.getSubset(0, subsetCountSmall);

	//VT.getSubset should do this automatically, but it doesn't
	for(int j=0; j<raw.getNumColumns(); j++){
		String s=raw.getColumn(j).getLabel();
		if(s!=null){
			subsetBig.getColumn(j).setLabel(s);
			subsetSmall.getColumn(j).setLabel(s);
		}
	}



	Random rand=new Random((long)seed);
	int smallCounter=0;

	for(int i=0; i<raw.getNumRows(); i++){
			double d=rand.nextInt(100);
			if((d<splitPercent)&&(smallCounter<subsetCountSmall)){
				Object[] rr = new Object[raw.getNumColumns()];
				raw.getRow(rr, i);
				subsetSmall.setRow(/*raw.getRow(i)*/rr, smallCounter);
				smallCounter++;
			}
			else if(subsetCountBig>i-smallCounter){
				Object[] rr = new Object[raw.getNumColumns()];
				raw.getRow(rr, i);
				subsetBig.setRow(/*raw.getRow(i)*/rr, i-smallCounter);
			}
			else{
				Object[] rr = new Object[raw.getNumColumns()];
				raw.getRow(rr, i);
				subsetSmall.setRow(/*raw.getRow(i)*/rr, smallCounter);
				smallCounter++;
			}

	}
	if(raw instanceof ExampleTable){
		ExampleTable rawet=(ExampleTable)raw;

		ExampleTable smallet= subsetSmall.toExampleTable();
		ExampleTable biget= subsetBig.toExampleTable();

		smallet.setInputFeatures(rawet.getInputFeatures());
		biget.setInputFeatures(rawet.getInputFeatures());

		smallet.setOutputFeatures(rawet.getOutputFeatures());
		biget.setOutputFeatures(rawet.getOutputFeatures());

		ArrayList trans=rawet.getTransformations();
		for(int i=0; i<trans.size(); i++){
			smallet.addTransformation((ncsa.d2k.modules.TransformationModule)trans.get(i));
			biget.addTransformation((ncsa.d2k.modules.TransformationModule)trans.get(i));
		}
		pushOutput(smallet, 1);
		pushOutput(biget, 0);
		return;

	}
	pushOutput(subsetSmall, 1);
	pushOutput(subsetBig, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Basic Vertical Table Stat Generator";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
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
				return "TableSetBig";
			case 1:
				return "TableSetSmall";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
