package ncsa.d2k.modules.core.discovery.cluster.kmeans;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
import java.lang.reflect.*;

/**
	KMeans.java
	
	Angela Bottum
	7/01
*/
public class KMeans extends ncsa.d2k.infrastructure.modules.ComputeModule
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"VerticalTable\"><Text>This is the verticaltable that already has a cluster column from the KRandom module and is to be re-clustered.</Text></Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.ExampleTable"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"table\"><Text>This verticaltable contains a column at the end with the cluster point closest to the data point.</Text></Info></D2K>";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.util.datatype.ExampleTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"KMeans\"><Text>The verticaltable will be split into k clusters, using the k-means algorithm.  The output verticaltable is the original verticaltable with an additional final column with the cluster point closest to the data point</Text></Info></D2K>";
	}

	public void doit() throws Exception {
		//VerticalTable vt = (VerticalTable)pullInput(0);
		ExampleTable vt = (ExampleTable)pullInput(0);

		Column col;
		int[] tempCols = vt.getInputFeatures();
		ArrayList inputCols = new ArrayList();
		for(int i = 0; i < tempCols.length; i++) {
			col = vt.getColumn(tempCols[i]);
			if(col instanceof NumericColumn)
				inputCols.add(new Integer(tempCols[i]));
		}

		//int numcols = vt.getNumColumns()-1;
		int numcols = inputCols.size();
		int addclustercol = vt.getNumColumns()-1;//numcols;
		int len = vt.getNumRows();
		//Column col = vt.getColumn(numcols);//clusters
		//IntColumn clustercol;// = new IntColumn(len);
		IntColumn clustercol = (IntColumn) vt.getColumn(addclustercol);
		IntColumn comparecol;
		comparecol = (IntColumn) clustercol.copy();
		double[] clusterarray = new double[len];
		int vlen = 0;
		int i;
		int v;
		boolean equal;

		//fill array with unique cluster points
		for(i=0; i<len; i++) {
			equal = false;
			for(v=0; v<=vlen; v++){
				if (clustercol.getInt(i) == clusterarray[v]) {
					equal = true;
				}
			}
			if(equal == false) {
				clusterarray[vlen] = clustercol.getInt(i);
				vlen++;
			}
		}

		double[] Means = new double[vlen*numcols];
		// calculate means of clusters
		int numpoints = 0;
		double mean = 0;
		DoubleColumn doublecol;
		int q = 0;
		for(int c=0; c<numcols; c++) {
			for(v=0; v<vlen; v++,q=q+numcols) {
				Integer ii = (Integer)inputCols.get(c);
				col = vt.getColumn(ii.intValue());
				numpoints = 0;
				mean = 0;
				if(col instanceof NumericColumn) {
					doublecol = (DoubleColumn) col/*.copy()*/;
					//find all data points that match clusterarray[i]
					for(i=0; i<len; i++) {
						if (clustercol.getInt(i) == clusterarray[v]) {
							mean = mean + doublecol.getDouble(i);
							numpoints++;
						}
					}
				}
				//else if(col instanceof StringColumn)
				//	numpoints = 1;

				//else
				//	numpoints = 1;

				if(q>=numcols*vlen) {
					q=q-((numcols*vlen)-1);
				}
				mean = mean / numpoints;
				Means[q] = mean;
			}
		}

		equal = false;
		comparecol = (IntColumn) clustercol.copy();

		while(equal == false) {
			// for each data point find the closest cluster point
			double dist;
			double square;
			double comparedist;
			double[] cluster = new double[numcols];
			int p;
			DoubleColumn dcol = new DoubleColumn(len);
			for(i=0; i<len; i++) {
				//calc the dist to the first cluster point
				Integer ii = (Integer)inputCols.get(0);
				col = vt.getColumn(ii.intValue());
				double[] datapoint = new double[numcols];
				for(v=0; v<numcols; v++) {
					ii = (Integer)inputCols.get(v);
					col = vt.getColumn(ii.intValue());
					if(col instanceof NumericColumn) {
						dcol = (DoubleColumn) col/*.copy()*/;
						datapoint[v] = dcol.getDouble(i);
					}
					//else if(col instanceof StringColumn) {
					//	datapoint[v] = 0;
					//}
					//else {
					//	datapoint[v] = 0;
					//}
				}
				for(p=0,square=0; p<numcols; p++) {
					square = square + Math.pow(Math.abs(datapoint[p]-Means[p]),2);
				}
				clustercol.setInt(1,i);
				dist = Math.sqrt(square);

				//compare all cluster points to find closest
				for(p=1; p<vlen; p++) {
					for(v=p*numcols,q=0; v<p*numcols+numcols; v++,q++) {
						cluster[q] = Means[v];
					}
					for(v=0,square=0; v<numcols; v++) {
						square = square + Math.pow(Math.abs(datapoint[v]-cluster[v]),2);
					}
					comparedist = Math.sqrt(square);
					if(comparedist<dist) {
						dist = comparedist;
						clustercol.setInt(p+1, i);
					}
				}
			}
			//compare to see if clusters have changed
			equal = true;
			for(i=0; i<len; i++) {
				if(comparecol.getInt(i) != clustercol.getInt(i)) {
					equal = false;
				}
			}
			comparecol = (IntColumn) clustercol.copy();
		}
		vt.setColumn(clustercol, addclustercol);
		pushOutput(vt, 0);
	}
}
