
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
	GeneratePlotValues.java
		Given a VerticalTable of test data, this module finds the min
		and max in each column, and creates a new column with 'num_pts'
		number of values that are equally spaced ranging from the min
		to the max.  In outputs each of these columns in a VerticalTable

	@talumbau
*/
public class GeneratePlotValues extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>The table to generate plot values from. </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"originalTable\">    <Text>The original table. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"outputTable\">    <Text>The table containing the \"x\" values for each column to plot for the line of best fit. </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";

	}

	public void setNum_pts(int x){
		num_pts = x;
	}

	public int getNum_pts(){
		return num_pts;
	}

	Table table;
	int num_pts = 60;

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		table = (Table) pullInput(0);
		int numCol = table.getNumColumns();
		Table outputTable = TableFactory.createTable();

		for (int i=0; i< numCol-1; i++){
			double min, max;
			min = getMin((NumericColumn)table.getColumn(i));
			max = getMax((NumericColumn)table.getColumn(i));
			DoubleColumn doubCol = 	new DoubleColumn(fillXs(min,max,num_pts));
			doubCol.setLabel(table.getColumnLabel(i));
			outputTable.addColumn(doubCol);
		}
		outputTable.addColumn(table.getColumn(numCol-1).copy());
		pushOutput(table, 0);
		pushOutput(outputTable, 1);
	}

	public double getMin(NumericColumn col){
		double min = ((Number)col.getRow(0)).doubleValue();
		for (int k=1; k<col.getNumRows(); k++){
			double value = ((Number)col.getRow(k)).doubleValue();
			if (value < min)
				min = value;
		}
		return min;
	}

	public double getMax(NumericColumn col){
		double max = ((Number)col.getRow(0)).doubleValue();
		for (int m=0; m<col.getNumRows(); m++){
			double val = ((Number)col.getRow(m)).doubleValue();
			if (val > max)
				max = val;
		}
		return max;
	}

	public double[] fillXs(double min, double max, int numval){
		double[] xvalues = new double[numval];
		double increment = (max - min)/(numval-1);
		double fill = min;
		for (int w=0; w<numval; w++){
			xvalues[w] = fill;
			fill = fill + increment;
		}
		return xvalues;
	}

}

