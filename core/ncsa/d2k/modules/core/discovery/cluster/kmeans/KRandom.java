package ncsa.d2k.modules.core.discovery.cluster.kmeans;

import ncsa.d2k.infrastructure.modules.*;
//import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import java.lang.*;

/**
	KRandom.java

	Angela Bottum
	7/01
*/
public class KRandom extends ncsa.d2k.infrastructure.modules.ComputeModule
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"VerticalTable\"><Text>This is the verticaltable to be clustered. </Text></Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTableImpl"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"table\"><Text>This verticaltable contains a column at the end with the random point closest to the data point.</Text></Info></D2K>";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.ExampleTableImpl"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"KRandom\"><Text>The verticaltable will be split into k clusters, using the k-means algorithm.  K is a property of this module, the default value is 3.  You must also specify if you would like to use a specific random seed, and if so, which one to use.  The default is to use none.  The output verticaltable is the original verticaltable with a final column with cluster point closest to the data point.</Text></Info></D2K>";
	}

	int kValue = 3;
	public void setKValue(int k) {
		kValue = k;
	}
	public int getKValue() {
		return kValue;
	}

	boolean useSeed = false;
	public void setUseSeed(boolean u) {
		useSeed = u;
	}
	public boolean getUseSeed() {
		return useSeed;
	}

	int seed = 6;
	public void setSeed(int s) {
			seed = s;
	}
	public int getSeed() {
		return seed;
	}

	public void doit() throws Exception {
		//VerticalTable vt = (VerticalTable)pullInput(0);
		ExampleTableImpl vt = (ExampleTableImpl)pullInput(0);
		int numrows = vt.getNumRows();
		int[] tempCols = vt.getInputFeatures();
		Column col;
		ArrayList inputCols = new ArrayList();
		for(int i = 0; i < tempCols.length; i++) {
			col = vt.getColumn(tempCols[i]);
			if(col instanceof NumericColumn)
				inputCols.add(new Integer(tempCols[i]));
		}

		NumericColumn ncol;
		IntColumn clustercol = new IntColumn(numrows);
		int numcols = inputCols.size();//vt.getNumColumns();
		double[] min = new double[numcols];
		double[] max = new double[numcols];
		int i;

		// find range of data points: min and max data values
		for(i=0; i<numcols; i++) {
			Integer ii = (Integer)inputCols.get(i);
			ncol = (NumericColumn)vt.getColumn(ii.intValue());
			//if(col instanceof NumericColumn) {
			//ncol = (NumericColumn) col;
			max[i] = ncol.getMax();
			min[i] = ncol.getMin();
			//}
			//else if(col instanceof StringColumn) {
			//	numcols = numcols - 1;
			//}
		}

		// find k random cluster points
		double[] rand = new double[kValue*numcols];
		double[] smallrand = new double[numcols];
		int v;
		int p;
		Random r;
		if(useSeed) {
			r = new Random(seed);
		}
		else {
			r = new Random();
		}
		for(i=0; i<kValue; i++) {
			for(p=i*numcols,v=0; p<i*numcols+numcols; p++,v++) {
				//*************************
				// changed 8.16
				if(max[v] > 0)
					rand[p] = r.nextInt((int)max[v]) + r.nextDouble();
				else
					rand[p] = r.nextDouble();
				//*************************
				if(rand[p]<min[v]) {
					rand[p] = rand[p] + min[v];
				}
			}
		}

		// for each data point find the closest cluster point
		double dist;
		double square;
		double comparedist;
		//DoubleColumn dcol = new DoubleColumn(numrows);
		for(i=0; i<numrows; i++) {
			//calc the dist to the first cluster point
			Integer ii = (Integer)inputCols.get(0);
			col = vt.getColumn(ii.intValue());//first column
			double[] datapoint = new double[numcols];
			for(v=0; v<numcols; v++) {
				ii = (Integer)inputCols.get(v);
				col = vt.getColumn(ii.intValue());
				//if(col instanceof NumericColumn) {
					//dcol = (DoubleColumn) col.copy();
				datapoint[v] = ((NumericColumn)col).getDouble(i);
				/*}
				else if(col instanceof StringColumn) {
					datapoint[v] = 0;
				}
				else {
					datapoint[v] = 0;
				}*/
			}
			for(p=0,square=0; p<numcols; p++) {
				square = square + Math.pow(Math.abs(datapoint[p]-rand[p]),2);
			}
			clustercol.setInt(1,i);
			dist = Math.sqrt(square);

			//compare all cluster points to find closest
			for(p=1; p<kValue; p++) {
				int q;
				for(v=p*numcols,q=0; v<p*numcols+numcols; v++,q++) {
					smallrand[q] = rand[v];
				}
				for(v=0,square=0; v<numcols; v++) {
					square = square + Math.pow(Math.abs(datapoint[v]-smallrand[v]),2);
				}
				comparedist = Math.sqrt(square);
				if(comparedist < dist) {
					dist = comparedist;
					clustercol.setInt(p+1, i);
				}
			}
		}
		clustercol.setLabel("cluster no.");
		vt.addColumn(clustercol);
		pushOutput(vt, 0);
	}
}
