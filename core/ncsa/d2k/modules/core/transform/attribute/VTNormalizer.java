package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	VTNormalizer.java

	This modules takes in a VerticalTable and outputs
	 it with all the columns of doubles normalized

	 @author Peter Groves
*/
public class VTNormalizer extends ncsa.d2k.infrastructure.modules.DataPrepModule {

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataIn\">    <Text>The vertical table that will be normalized in every column </Text>    <Text>of doubles </Text>    <Text> </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataOut\">    <Text>The normalized vertical table </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Data Table Normalizer\">    <Text>This modules takes in a VerticalTable and outputs it with all the columns of doubles normalized. PROPS: printInfo- if true will print mean and st dev info for each column to standard out </Text>  </Info></D2K>";

	}
	public boolean printInfo=false;

	public boolean getPrintInfo(){
		return false;
	}
	public void setPrintInfo(boolean b){
		printInfo=b;
	}

	public void doit () throws Exception {

	Table raw=(Table)pullInput(0);
	Column[] newColumns=new Column[raw.getNumColumns()];

	  for(int k=0; k<raw.getNumColumns(); k++){
	    Column current_column=raw.getColumn(k);

	    if (current_column instanceof DoubleColumn /* ||
			current_column instanceof FloatColumn ||
			current_column instanceof ShortColumn */){

	      double mean = 0.0;
	      double[] diff_from_mean = new double[current_column.getNumRows()];
	      double var_sum=0.0;

		double[] old_data=(double[])((DoubleColumn)current_column).getInternal();

	      //calculate the average
	      for(int i=0; i<old_data.length; i++){
		mean+=old_data[i];
	      }
	      mean = mean/old_data.length;

	      //find differences from mean and variance

	      double diff=0;
	      for(int i=0; i<old_data.length; i++){
		  diff=old_data[i]-mean;
		  diff_from_mean[i]=diff;
		  var_sum+=Math.pow(diff, 2);
	      }

	      double deviation=(Math.pow(var_sum, .5)/(old_data.length-1));
		  if(printInfo){
  	      	System.out.println("column no " +k+ ": st dev = " +deviation + " , mean = "+mean);
		  }

	      //find the normalized values and put them in a column

	      double[] current_transformed=new double[old_data.length];
	      for(int i=0; i<old_data.length; i++){
		current_transformed[i]=diff_from_mean[i]/deviation;
	      }

	      newColumns[k]=new DoubleColumn(current_transformed);
		  newColumns[k].setLabel(current_column.getLabel());

	    }
	    else{
	      newColumns[k]=raw.getColumn(k).copy();
	    }

	  }
	  Table newVT=TableFactory.createTable(newColumns);

	  pushOutput(newVT, 0);


	}
}


