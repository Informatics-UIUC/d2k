package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import java.io.Serializable;

import ncsa.d2k.modules.core.datatype.table.*;
/**
       VTLinearScaler.java

		This modules takes in a VerticalTable and outputs it with
		all the columns of doubles scaled. PROPS: upper/lowerBound - define
		the range that all DoubleColumns will be scaled to.

		@author Peter Groves
*/
public class VTLinearScaler extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable {


public double lowerBound=0;
public double upperBound=1;
public double newRange=1;

public boolean inplace=false;

public void setInplace(boolean b){
	inplace=b;
}
public boolean getInplace(){
	return inplace;
}

public void setLowerBound(double d){
	lowerBound=d;
	updateBounds();
}

public double getLowerBound(){
		return lowerBound;
}
public void setUpperBound(double d){
	upperBound=d;
	updateBounds();
}
public double getUpperBound(){
	return upperBound;
}

private void updateBounds(){
	newRange= upperBound-lowerBound;
}




	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataIn\">    <Text>The data table that will be normalized in every column </Text>    <Text>of doubles </Text>    <Text> </Text>  </Info></D2K>";
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"dataOut\">    <Text>The normalized data table </Text>  </Info></D2K>";
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

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Data Table Normalizer\">    <Text>This modules takes in a VerticalTable and outputs it with all the columns of doubles scaled. PROPS: upper/lowerBound - define the range that all DoubleColumns will be scaled to. </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {

	Table raw=(Table)pullInput(0);
	Column[] newColumns=new Column[raw.getNumColumns()];

	  for(int k=0; k<raw.getNumColumns(); k++){
	    Column current_column=raw.getColumn(k);


	    if (current_column instanceof DoubleColumn /* ||
			current_column.getType()==FloatColumn ||
			current_column.getType()==ShortColumn */){
	      double range=((DoubleColumn)current_column).getMax()-((DoubleColumn)current_column).getMin();
	      double min = ((DoubleColumn)current_column).getMin();
	      double[] current_data=new double[current_column.getNumRows()];
	   	  double[] old_data=(double[])((DoubleColumn)current_column).getInternal();

		for(int j=0; j<current_data.length; j++){
			current_data[j]=((old_data[j]-min)/range);
			current_data[j]=(current_data[j]*newRange + lowerBound);
		}

	      newColumns[k]=new DoubleColumn(current_data);
		  newColumns[k].setLabel(current_column.getLabel());

	    }
	    else{
	      newColumns[k]=raw.getColumn(k).copy();
	    }
	  }
	  Table newVT;
	  if(!inplace){
		  newVT=TableFactory.createTable();
		  newVT.setColumns(newColumns);
	  }else{
	  	newVT=raw;
		newVT.setColumns(newColumns);
	  }



	  pushOutput(newVT, 0);
	}
}


