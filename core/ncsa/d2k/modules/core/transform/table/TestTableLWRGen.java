
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	TestTableLWRGen.java
		This module is only used (so far) in creating a
		VerticalTable of test data for the purpose of generating
		a model from an LWRModelGen module. It takes the table of
		x values, select a column at random, and uses that column
		of x values to create a new table of test data.  The other
		values for each row are pulled from the training data.
		The row from the training data that most closely matches
		the x value in the randomly selected column is chosen as
		the other values to fill the new VerticalTable.
	@author talumbau
*/
public class TestTableLWRGen extends ncsa.d2k.infrastructure.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"trainTable\">    <Text>This is the table of training data.  It is used to create a table of testing data for lines of best fit. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"xTable\">    <Text>This is the table of 'x' values.  Column 'i' contains equally spaced values for plotting the line of best fit for predicted value vs. attribute 'i'. </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"testTable\">    <Text> An output table of test data for an LWRModel. </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"TestTableLWRGen\">    <Text>The purpose of this module is to generate a table of test data for an LWRModel.   </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	TableImpl trainTable = null;
	TableImpl xTable = null;
	public void doit() throws Exception {

		trainTable = (TableImpl) pullInput(0);
		xTable = (TableImpl) pullInput(1);
		int N = xTable.getNumColumns();
		int randomIndex = getRandom(N);
		NumericColumn col = (NumericColumn) xTable.getColumn(randomIndex);
		Table t = makeTable(col, randomIndex);

		pushOutput(t,0);
	}

	public Table makeTable(NumericColumn col, int i) {
		TableImpl theTable = (TableImpl)DefaultTableFactory.getInstance().createTable(trainTable.getNumColumns());

		//initializing a new table
		for (int t=0; t<trainTable.getNumColumns(); t++){
			double[] work = new double[col.getNumRows()];
			for (int j=0; j<col.getNumRows(); j++){
				work[j] = 0;
			}
			DoubleColumn c1 = new DoubleColumn(work);
			c1.setLabel(trainTable.getColumnLabel(t));
			theTable.setColumn(c1, t);
		}

		Column[] c1 = {col.copy()};
		TableImpl xTble = (TableImpl)DefaultTableFactory.getInstance().createTable(c1);
		//NumericColumn col2 = (NumericColumn) trainTable.getColumn(i).copy();
		//Column[] c2 = {col2};
		//VerticalTable trainTble = new VerticalTable(c2);
		//trainTble.print();
		try {
			trainTable.sortByColumn(i);
			xTble.sortByColumn(0);
		}
		catch (Exception e){
			System.out.println("unable to sort");
		}
		/*System.out.println("************xTble**********");
		xTble.print();
		System.out.println("************end************");*/
		NumericColumn sortedColx = (NumericColumn) xTble.getColumn(0);
		NumericColumn sortedColt = (NumericColumn) trainTable.getColumn(i);
		int[] indices = new int[sortedColx.getNumRows()];
		theTable.setNumRows(indices.length);
		int index = 0;
		int last = 0;
		for (int j=0; j<(sortedColx.getNumRows()); j++){
			//xval > tval
			double xval = 0;
			double tval = 0;
			boolean stillLooking = true;
			while (stillLooking) {
				xval = ((Number) sortedColx.getRow(j)).doubleValue();
				tval = ((Number) sortedColt.getRow(last)).doubleValue();
				if (xval <= tval){
					indices[j] = last;
					stillLooking = false;
				}
				else {
					double diff = xval - tval;
					double nxtTval = tval;
					boolean keepGoing = true;
					int P = sortedColt.getNumRows();
					int jump = 0;
					while (keepGoing){
						//System.out.println("inside the while loop");
						if (last+jump == P-1){
							nxtTval = ((Number) sortedColt.getRow(last+jump)).doubleValue();
							keepGoing = false;
						}
						else {
							nxtTval = ((Number) sortedColt.getRow(last+jump)).doubleValue();
							if (nxtTval != tval)
								keepGoing = false;
							else
								jump++;
						}
					}
					double nxtDiff = Math.abs(nxtTval - xval);
					if (nxtDiff < diff) {
						if ((last +jump) <= P-1)
							last = last + jump;
					}
					else
						stillLooking = false;
					}
			}
			/*System.out.println("xval = "+xval);
			System.out.println("j = "+j);
			System.out.println("last = "+last);
			System.out.println("tval = "+tval);*/
			indices[j] = last;
		}

		/*for (int m=0; m<indices.length; m++){
			System.out.println(indices[m]);
		}*/
		//theTable.print();
		for (int k=0; k<indices.length; k++){
			int n = indices[k];
			//System.err.println("say what?");

			Object[] row = new Object[trainTable.getNumColumns()];
			//Object o = trainTable.getRow(n);
			trainTable.getRow(row, n);

			//System.err.println("snizzle");
			//Object q = o.clone();
			theTable.setRow((Object[])row,k);

			//System.out.println(" ");
			//theTable.print();
			//System.out.println(" ");
			//System.out.println("k = "+k);
		}
		//theTable.print();
		theTable.removeColumn(i);
		theTable.insertColumn(sortedColx, i);
		return theTable;
	}

	public int getRandom(int N){
		double d = N*Math.random();
		return (int) d;
	}

	public void beginExecution() {
		xTable = null;
		trainTable = null;
	}
}

