package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
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
public class VTRandomSubset extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable{

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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The Vertical Table to split</Text>  </Info></D2K>";
			default: return "No such input";
		}
	}


	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {

		String [] types =  {
			"ncsa.d2k.util.datatype.VerticalTable"
			
			};
		return types;

	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TableSetBig\">    <Text>The table containing the larger percentage of rows </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TableSetSmall\">    <Text>The table containing the smaller percentage of rows </Text>  </Info></D2K>";
			
			default: return "No such output";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {

		String [] types =  {
			"ncsa.d2k.util.datatype.VerticalTable",
			"ncsa.d2k.util.datatype.VerticalTable"};
		return types;

	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {

		 return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Basic Vertical Table Stat Generator\">    <Text>This module takes in a VerticalTable and outputs two new VerticalTables that are exhaustive of the original.  The fraction that is in each set is determined by the property \"splitPercent\".  ExampleTables will have their input/outputFeatures and transforms transferred to new ExampleTables but not their test/trainExample sets(they'll be left null).  PROPS:  splitPercent should be an integer between 0 and 100. seed is the seed of the random generator.  A constant seed with the same Table will generate identical subsets every time the alg is run. It should be noted that rows remain in the same order that they were in in the original table</Text>  </Info></D2K>";

	}

  /*///////////////////
  //THE ALMIGHTY DOIT
  //////////////////*/
	public void doit () throws Exception {

	VerticalTable raw=(VerticalTable)pullInput(0);

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
	
	VerticalTable subsetBig=(VerticalTable)raw.getSubset(0, subsetCountBig);
	
	VerticalTable subsetSmall=(VerticalTable)raw.getSubset(0, subsetCountSmall);		

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
				subsetSmall.setRow(raw.getRow(i), smallCounter);
				smallCounter++;
			}
			else if(subsetCountBig>i-smallCounter){
				subsetBig.setRow(raw.getRow(i), i-smallCounter);
			}
			else{
				subsetSmall.setRow(raw.getRow(i), smallCounter);
				smallCounter++;
			}

	}
	if(raw instanceof ExampleTable){
		ExampleTable rawet=(ExampleTable)raw;
		
		ExampleTable smallet=new ExampleTable(subsetSmall);
		ExampleTable biget=new ExampleTable(subsetBig);

		smallet.setInputFeatures(rawet.getInputFeatures());
		biget.setInputFeatures(rawet.getInputFeatures());

		smallet.setOutputFeatures(rawet.getOutputFeatures());
		biget.setOutputFeatures(rawet.getOutputFeatures());

		ArrayList trans=rawet.getTransformations();
		for(int i=0; i<trans.size(); i++){
			smallet.addTransformation((TransformationModule)trans.get(i));
			biget.addTransformation((TransformationModule)trans.get(i));
		}
		pushOutput(smallet, 1);
		pushOutput(biget, 0);
		return;
		
	}
	pushOutput(subsetSmall, 1);
	pushOutput(subsetBig, 0);
	}
}
