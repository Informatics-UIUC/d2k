package ncsa.d2k.modules.core.discovery.cluster.kmeans;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import java.util.*;
import java.lang.reflect.*;

/**
	KMeans.java
	
	Angela Bottum
	7/01

	Fixed the algorithm up 4/02 - pgroves
*/
public class KMeans extends ncsa.d2k.infrastructure.modules.ComputeModule
implements Reentrant
{
	int maxIterations=-1;
	public void setMaxIterations(int i){
		maxIterations=i;
	}
	public int getMaxIterations(){
		return maxIterations;
	}
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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K><Info common=\"KMeans\"><Text>The verticaltable will be split into k clusters, using the k-means algorithm.  The output verticaltable is the original verticaltable with an additional final column with the cluster point closest to the data point.PROPS: maxIterations - the number of cluster recomputions to allow. A value less than zero will let the algorithm continue until no examples change clusters. (A good way to have it hang)</Text></Info></D2K>";
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
		NumericColumn doublecol;
		int q = 0;
		/*
		for(int c=0; c<numcols; c++) {
			for(v=0; v<vlen; v++,q=q+numcols) {
				Integer ii = (Integer)inputCols.get(c);
				col = vt.getColumn(ii.intValue());
				numpoints = 0;
				mean = 0;
				if(col instanceof NumericColumn) {
					doublecol = (NumericColumn) col/*.copy()/;
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
		*/
		equal = false;
		//comparecol = (IntColumn) clustercol.copy();
		int iters=0;
		boolean maxIterReached=false;
		
		while((equal == false) && (!maxIterReached)) {
			//(re)calc the means
			q=0;
			for(int c=0; c<numcols; c++) {
				Integer ii = (Integer)inputCols.get(c);
				doublecol = (NumericColumn)vt.getColumn(ii.intValue());
				
				for(v=0; v<vlen; v++,q=q+numcols) {
					//Integer ii = (Integer)inputCols.get(c);
					//col = vt.getColumn(ii.intValue());
					numpoints = 0;
					mean = 0;
					//if(col instanceof NumericColumn) {//this should already be a numeric column
					//	doublecol = (NumericColumn) col/*.copy()*/;
						//find all data points that match clusterarray[i]
					for(i=0; i<len; i++) {
						if (clustercol.getInt(i) == clusterarray[v]) {
							mean = mean + doublecol.getDouble(i);
							numpoints++;
						}
					}
				//}
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
			//comparecol = (IntColumn) clustercol.copy();
			// for each data point find the closest cluster point
			double dist;
			double square;
			double comparedist;
			double[] cluster = new double[numcols];
			int p;
			NumericColumn dcol;// = new DoubleColumn(len);
			double thisDist=0.0;

			for(i=0; i<len; i++) {
				//calc the dist to the first cluster point
				Integer ii = (Integer)inputCols.get(0);
				col = vt.getColumn(ii.intValue());
				double[] datapoint = new double[numcols];
				for(v=0; v<numcols; v++) {
					ii = (Integer)inputCols.get(v);
					dcol = (NumericColumn)vt.getColumn(ii.intValue());
					//if(col instanceof NumericColumn) {
					//	dcol = (NumericColumn) col/*.copy()*/;
						datapoint[v] = dcol.getDouble(i);
					//}
					///else if(col instanceof StringColumn) {
					//	datapoint[v] = 0;
					//}
					//else {
					//	datapoint[v] = 0;
					//}
				}
				for(p=0,square=0; p<numcols; p++) {
					thisDist=datapoint[p]-Means[p];
					square+=thisDist*thisDist;
					
					//PDG//square = square + Math.pow(Math.abs(datapoint[p]-Means[p]),2);
				}
				clustercol.setInt(1,i);
				dist = square; //PDG//Math.sqrt(square);
				//compare all cluster points to find closest
				for(p=1; p<vlen; p++) {
					for(v=p*numcols,q=0; v<p*numcols+numcols; v++,q++) {
						cluster[q] = Means[v];
					}
					for(v=0,square=0; v<numcols; v++) {
						thisDist=datapoint[v]-cluster[v];
						square+=thisDist*thisDist;
						//PDG//square = square + Math.pow(Math.abs(datapoint[v]-cluster[v]),2);
					}
					/*I took out the sqrt intentionally, if sqrt(x)<sqrt(y) then x<y*/
					comparedist = square; //PDG//Math.sqrt(square);
					if(comparedist<dist) {
						dist = comparedist;
						clustercol.setInt(p+1, i);
					}
				}
			}
			//compare to see if clusters have changed
			equal = true;
			//if(maxIterations<0){
			for(i=0; i<len; i++) {
				if(comparecol.getInt(i) != clustercol.getInt(i)) {
					equal = false;
					
				}
				//do this so we don't have to reallocate and copy clustercol
				//to comparecol every iteration
				comparecol.setInt(clustercol.getInt(i), i);
			}
			iters++;

			if(maxIterations>0){
			
				//see if maxIterations is reached
				if(iters==maxIterations){
					maxIterReached=true;
				}
			}
			//IntColumn tc=comparecol;
			//comparecol = clustercol;//.copy();
			//clustercol=tc;
			
		}
		vt.setColumn(clustercol, addclustercol);
		pushOutput(vt, 0);
	}
}
