package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.util.splaytree.*;
import Jama.*;
import java.util.*;
import java.lang.Number;
import java.io.Serializable;
import ncsa.d2k.modules.*;

/**
	LWRModel.java
		This model module implements a type of locally weighted
		learning algorithm called locally weighted regression.  The"\[\e]
		module will predict outputs for the testing data
		Table based on the training data Table.Shell

	@author talumbau
*/
public class LWRModel extends PredictionModelModule implements Serializable {

	//properties
	int kernelSelector;

	int distanceSelector;
	int nearestNeighbors;
	boolean useNearestNeighbors;

	int count;	//counter for the rows of the Testtable
	int num_pts;	//number of points in the regression plots
	int countPredCol;
	int N;
	double minDist = -1;
	int minDistIndex = 0;
	double[] weights;
	double[] kernels;
	double[] distances;
	double[] criterion;
	ExampleTable Traintable;
	TableImpl TraintableSubset;
	public PredictionTableImpl finalTable;
	Matrix X;
	Matrix Z;
	NumericColumn y;  //actual query values for given data
	double[] prediction;

	/**
		Constructor
		Builds the model from a Table of test data, one of training data,
		and the various properties of the model
		@param Table table1 - the training data
		@param Table table2 - the testing data
	*/
	public LWRModel(ExampleTable table1, int ker, int distance, int nearest, boolean useNearest, int numpts) {
		Traintable = table1;

		kernelSelector = ker;
		distanceSelector = distance;
		nearestNeighbors = nearest;
		useNearestNeighbors = useNearest;
		num_pts = numpts;

	}

	/**
		doit
		Predicts an output for any number of input queries based on the training
		data using a locally weighted regression model
	*/
	public void doit() {
		//get the testing data
		ExampleTable tble = (ExampleTable) pullInput(0);
		PredictionTable fTable = predict(tble);

		pushOutput(fTable, 0);
		pushOutput(this, 1);
	}

	/**
		predict
		The overwritten method for a PredictionModelModule that produces a
		NumericColumn of prediction given a Table object of queries
		@param Object m - the queries
		@return Object predCol - the column of predictions
	*/

	public PredictionTable predict( ExampleTable exTable) {

		TableImpl xTable = generatePlotValues();
		//xTable.print();


		N = xTable.getNumColumns();


		//System.out.println("exTable has "+exTable.getNumColumns()+" columns");
		//place to put the final predictions (will become a PredictionTable)
		//finalTable = ((ExampleTable) exTable.copy()).toPredictionTable();
		finalTable = new PredictionTableImpl((ExampleTableImpl) (new ExampleTableImpl(xTable).copy()));

		//System.out.println("finalTable has "+finalTable.getNumColumns()+" columns");
		//finalTable.removeColumn(finalTable.getNumColumns()-1);

	for (int i=0; i<N; i++){


		TableImpl TestTable = testTableLWRGen(xTable, i);

		//determine the subset of training data which should be used for
		//the regression
		determineSubset(TestTable);
		/*System.out.println("***************TrainTable Subset *****************");
		TraintableSubset.print();
		System.out.println("***************end TrainTable    *****************");
		System.out.println("   ");*/

		/*System.out.println("***************TestTable looks like *****************");
		TestTable.print();
		System.out.println("***************end TestTable    *****************");
		System.out.println("   ");*/
		//Each of these arrays stores the corresponding value for each query.
		//Thus, their length is the number of queries

		
		weights = new double[TraintableSubset.getNumRows()];
		kernels = new double[TraintableSubset.getNumRows()];
		distances = new double[TraintableSubset.getNumRows()];
		criterion = new double[TraintableSubset.getNumRows()];

		
		//if the number of nearest neighbors given is too big,
		//default to not using k-nearest neighbors bandwith selection
		if (useNearestNeighbors){
			if (nearestNeighbors >= TraintableSubset.getNumRows())
				useNearestNeighbors = false;
		}

		//remove column of outputs from the training data, leaving only
		//the inputs
		y = removeYs(TraintableSubset);
		//remove column of outputs from the test data, leaving only
		//the inputs
		NumericColumn g = removeYs(TestTable);

		//the regression requires a constant 1 as an additional dimension to each
		//vector for training data and each query
		addConstant1s(TestTable);
		addConstant1s(TraintableSubset);
		int numRows = TestTable.getNumRows();
		int numColumns = TestTable.getNumColumns();
		
		//This array will hold the predicted values for the queries
		prediction = new double[TestTable.getNumRows()];

		X = new Matrix(constructX());


		for (int c=0; c<numRows; c++){

			//getting a query from the test data
			double[] q = getQuery(c, TestTable);

			//System.out.println("here's X");
			//X.print(4,3);
			//System.out.println("****end X****");

			//construct a weight vector based on the query
			constructWeights(X,q);

			//construct a new matrix Z by element-wise multiplication
			//of the weights vector with matrix X
			Z = new Matrix(constructZ(X));

			//Perform QR decomposition of Z
			QRDecomposition qrOfZ = new QRDecomposition(Z);


			//construct the 'weighted' output vector by multiplying y
			//(element-wise) by the weights vector
			Matrix v = constructV(y);

			//System.out.println("*********v********");
			//v.print(4,3);
			//System.out.println("**** end v********");

			try{
				Matrix Beta = qrOfZ.solve(v);
				//The Matrix Beta is really just a column vector so this makes a
				//double array of the column vector
				double[] BetaAr = Beta.getRowPackedCopy();

				//a standard vector multiplication of the query and the Beta vector
				//making the scalar predicted value for this query
				double ans = arrayMult(q, BetaAr);
				prediction[c] = ans;

				//calculate criterion
				//(this value is not used yet.  Supposedly, it can be used to
				//estimate the error for the predicted value)
				double sum = 0;
				for (int b=0; b<TraintableSubset.getNumRows(); b++){
					Matrix B = X.getMatrix(b, b, 0, X.getColumnDimension()-1);
					double[] p = B.getColumnPackedCopy();
					double xB = (arrayMult(p, BetaAr)) - ((Number) y.getRow(b)).doubleValue();
					sum = xB*xB*kernels[b]+sum;
				}
				criterion[c] = sum;
			}
			catch(Exception e){
				//default to pulling nearest neighbor
				System.out.println("problem in solving Z...defaulting to nearest neighbor selection.");
				//System.out.println("Vector v was:");
				//v.print(4,3);
				//System.out.println(" ");
				double[] doubAr = new double[Traintable.getNumRows()];
				Traintable.getColumn(doubAr, Traintable.getNumColumns()-1);
				double ans = doubAr[minDistIndex];

				//double ans = ((Double) Traintable.getColumn(Traintable.getNumColumns()-1).getRow(minDistIndex)).doubleValue();
				prediction[c] = ans;


			}
			//Beta is a column vector, B, that is the solution to the matrix
			//equation:
			// Z*B = v
			//This is where the program will throw a run-time exception
			//if the R matrix is rank deficient.  This has happened often when
			//the weights vectors has very small values and thus makes Z contain
			//small values which get approximated to 0.  This can happen if the
			//k-th nearest neighbor is 'far away' relative to many of the closer
			//data points, or if nearest neighbor selection is not used and the
			//computed distances are quite large.

		}



		/*for (int j=0; j< TestTable.getNumColumns()-1; j++){
			finalTable.addColumn(TestTable.getColumn(j));
		}*/
		finalTable.addPredictionColumn(prediction);
		finalTable.addPredictionColumn(criterion);


	}	//end big if statement; should collect predCols


		return finalTable;
	}

	/**
		constructX
		makes 2-D array used to construct the matrix X, the matrix of all of the
		input vectors
		@return double[][] A - the 2d array of input vectors
	*/
	public double[][] constructX() {

		int numR = TraintableSubset.getNumRows();
		int numC = TraintableSubset.getNumColumns();
		double[][] A = new double[numR][numC];
		for (int i=0; i<numC; i++){
			Column col = TraintableSubset.getColumn(i);
			for (int j=0; j<numR; j++){
				Number dub = (Number) col.getRow(j);
				double d = dub.doubleValue();
				A[j][i] = d;
			}
		}
		return A;
	}

	/**
		constructZ
		makes a 2-D array used to construct the matrix Z, which is X multiplied
		element-wise by the weights vector
		@param Matrix B - the matrix to multiply by the weights vector
		@return double[][] zArray - the 2-D array passed to a Matrix constructor
	*/
	public double[][] constructZ(Matrix B) {
		int numR = B.getRowDimension();
		int numC = B.getColumnDimension();

		double[][] zArray = new double[B.getRowDimension()][B.getColumnDimension()];
		for (int i=0; i<numR; i++){
			Matrix xRow = B.getMatrix(i,i, 0, numC-1);
			double[] xArray = xRow.getColumnPackedCopy();
			double[] zRow = new double[numC];
			for (int j=0; j<numC; j++){
				double w = weights[i];
				zRow[j] = w*xArray[j];
			}
			zArray[i] = zRow;
		}
		return zArray;
	}

	/**
		constructV
		makes a Matrix object of the vector resulting from the multiplication
		(element-wise) of the y vector and the weights vector
		@param NumericColumn col - the y vector
		@return Matrix v - the vector v as a Matrix object
	*/
	public Matrix constructV( NumericColumn col){
		double[][] matrix = new double[col.getNumRows()][1];
		for (int i=0; i< col.getNumRows(); i++){
			matrix[i][0] = weights[i]*(((Number) col.getRow(i)).doubleValue());
		}
		Matrix v = new Matrix(matrix);
		return v;
	}

	/**
		constructWeights
		Builds the weight vector for a particular query by first computing
		the distance from the query to each training data point, and then
		using that as the input to a kernel function.  If useNearestNeighbors
		is true, then each input to the kernel function is divided by the
		distance to the kth nearest point (k = numNearestNeighbors).
		@param Matrix D - the matrix of input values
		@param double[] q - the query
	*/
	public void constructWeights(Matrix D, double[] q){
		int m = weights.length;
		if (useNearestNeighbors) {
			//structure to hold nearest neighbors
			SplayTree tree = new SplayTree();
			int numInserted = 0;
			for (int k=0; k<m; k++){
				Matrix B = D.getMatrix(k, k, 0, D.getColumnDimension()-1);
				double[] x = B.getColumnPackedCopy();
				double distance = getDistance(x,q);
				if (minDist > 0) {
					minDist = distance;
					minDistIndex = k;
				}else if (distance < minDist) {
					minDist = distance;
					minDistIndex = k;
					}
				distances[k] = distance;
				DoubleNode dNode = new DoubleNode(distance);
				if (numInserted < nearestNeighbors) {//'fill up' the tree first
					tree.insert(dNode);
					numInserted++;
				}
				else {
					//now replace the largest value if there is a smaller one
					BNode bigNode = tree.findMax();
					int test = bigNode.compareTo(dNode);
					if (test < 0) {//checkNode.value < dNode.value
						tree.remove(bigNode);
						tree.insert(dNode);
					}
				}
			}
			double h = ((DoubleNode)(tree.findMax())).value;
			//h is the distance of the kth nearest neighbor
			for (int y=0; y<m; y++){
				double a = kernelOf(distances[y]/h);
				kernels[y] = a;
				weights[y] = Math.sqrt(a);
			}
		}
		else {
			//in this case, just simply compute the weight for each query
			for (int j=0; j<m; j++){
				Matrix B = D.getMatrix(j, j, 0, D.getColumnDimension()-1);
				double[] x = B.getColumnPackedCopy();
				double distance = getDistance(x,q);
				if (minDist > 0) {
					minDist = distance;
					minDistIndex = j;
				}else if (distance < minDist) {
					minDist = distance;
					minDistIndex = j;
					}
				distances[j] = distance;
				double y = kernelOf(getDistance(x,q));
				kernels[j] = y;
				weights[j] = Math.sqrt(y);
			}

		}

	}

	/**
		determineSubset
		finds the subset of the Traintable that will serve as appropriate
		training data for the given Table.  This subset of the data
		will become TraintableSubset
		@param Table t - the test data
	*/
	public void determineSubset(TableImpl t){
		TraintableSubset = (TableImpl)DefaultTableFactory.getInstance().createTable();
		for (int j=0; j<t.getNumColumns(); j++){
			//System.out.println("j in determineSubset is "+j);
			int index = findIndexInTraintable(t.getColumnLabel(j));
			double[] dubAr = new double[Traintable.getNumRows()];
			Traintable.getColumn(dubAr, index);
			TraintableSubset.addColumn(dubAr);
			TraintableSubset.setColumnLabel(Traintable.getColumnLabel(index), TraintableSubset.getNumColumns()-1);
		}
	}

	public int findIndexInTraintable(String label){
		int num = -1;
		for (int h=0; h<Traintable.getNumColumns(); h++){
			//System.out.println(label);
			if (Traintable.getColumnLabel(h).equals(label)){
				num = h;
				break;
			}
		}
		if (num == -1){
			System.out.println("Column Label doesn't match training data");
		}
		return num;
	}

	/**
		getWeight
		finds the weight associated with a query and a training data point
		by finding the distance with the selected distance function,
		putting the result into the selected kernel function, then taking
		the square root of that.
		@param double[] x - the input vector
		@param double[] q - the query
		@return double - the scalar weight value
	*/
	public double getWeight( double[] x, double[] q){
		double dist = getDistance(x, q);
		double ker = kernelOf(dist);
		double w = Math.sqrt(ker);
		return w;
	}

	/**
		getDistance
		computes the distance between two vectors using the distance function
		selected by the distanceSelector
		@param double[] a - a vector
		@param double[] b - a vector
		@return double - the scalar distance
	*/

	public double getDistance( double[] a, double[] b){
		double d;
		switch ( distanceSelector) {
			case 0:
				d = distUnweightedEuclidean(a,b);
				break;

			default:
				d = distUnweightedEuclidean(a,b);
				break;
		}
		return d;
	}

	/**
		kernelOf
		computes the kernel of the given value using the kernel function
		selected
		@param double c
		@return double ker
	*/
	public double kernelOf(double c){
		double ker;
		switch (kernelSelector) {
			case 0:
				ker = keSquare(c);
				break;
			case 1:
				ker = kInversePlusOne(c);
				break;
			case 2:
				ker = kInverse(c);
				break;
			case 3:
				ker = kInverseSquare(c);
				break;

			default:
				ker = keSquare(c);
				break;
		}
		return ker;
	}

	/**
		removeYs
		removes the column of output values from the training data
		@param Table table - the table of input data
	*/
	public NumericColumn removeYs(TableImpl table) {
		//table.print();
		int numCol = table.getNumColumns();
		NumericColumn col = (NumericColumn) table.getColumn(numCol-1);
		table.removeColumn(numCol-1);
		return col;
	}

	/**
		getQuery
		gets a row from the Table of test data
		@param int c - the index of the row to get
		@param Table Testtable - the table to take a query from
	*/
	public double[] getQuery(int c, Table Testtable){
		int numCol = Testtable.getNumColumns();
		double[] query = new double[numCol];
		for (int h=0; h<numCol; h++){
			query[h] = ((Number) Testtable.getObject(c, h)).doubleValue();
		}
		return query;
	}

    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Makes predictions based on the");
		sb.append(" data it was created with.  The predictions are put into ");
		sb.append(" a new column of the table.");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "LWRmodel2";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs
       of this module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.Table",
			"ncsa.d2k.modules.core.prediction.LWR.LWRModel"};
   		return out;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if (i == 0)
			return "A testing data set used for making predictions";
		else
			return "No such input";
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		if (i == 0)
		    return "Testable";
		else
			return "";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0)
    		return "Predicted data set, with predictions in last column";
		else
			return "A reference to this model.";
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		if(i == 0)
   			return "predictions";
		else
			return "thisModel";
	}

	public double[][] subtractQuery(double[] q){
		Matrix A = (Matrix) X.clone();
		double[][] ar1 = A.getArrayCopy();
		for (int g=0; g< A.getRowDimension(); g++){
			for (int h=0; h< (A.getColumnDimension()-1); h++){
				ar1[g][h] = ar1[g][h] - q[h];
			}
		}
		return ar1;
	}

	/**
		kInverse
		a kernel function: k(x) = 1/x
		@param double x
	*/
	public double kInverse(double x) {
		double y = 1/x;
		if (y == Double.NaN){
			System.out.println("we have a problem");
			y = 100;
		}
		return y;
	}

	/**
		kInversePlusOne
		a kernel function: k(x) = 1/(1+x)
		@param double x
	*/
	public double kInversePlusOne(double x) {
		double y = 1/(x+1);
		return y;
	}

	/**
		kInverseSquare
		a kernel function: k(x) = 1/(x^2)
		@param double x
	*/
	public double kInverseSquare(double x) {
		double y = 1/(x*x);
		return y;
	}

	/**
		keSquare
		a kernel function: k(x) = 1/(e^(x^2))
		@param double x
	*/
	public double keSquare(double x) {
		double val = -(x*x);
		double y = Math.exp(val);
		return y;
	}

	/**
		kLinear
		a kernel function: k(x) = 1-x
		@param double x
	*/
	public double kLinear(double x) {
		double y = 1-x;
		return y;
	}

	/**
		distUnWeightedEuclidean
		the normal Euclidean distance for two n-dimensional vectors
		@param double[] x - a vector
		@param double[] q - a vector
		return double ans - the distance
	*/
	public double distUnweightedEuclidean( double[] x, double[] q) {
		int num1 = x.length;
		int num2 = q.length;

		if (num1 != num2)
			System.out.println("not same length in distUnweightedEuc.");
		double ans = 0;
		for (int i=0; i<num1; i++){
			double add = x[i] - q[i];
			add = add*add;
			ans = ans+add;
		}
		ans = Math.sqrt(ans);
		return ans;
	}

	/**
		addConstant1s
		adds a column of constant 1's
	*/
	protected void addConstant1s(TableImpl table){
		int numR = table.getNumRows();
		int[] ar1 = new int[numR];
		for (int i=0; i<numR; i++){
			ar1[i] = 1;
		}
		IntColumn oneCol = new IntColumn(ar1);
		table.addColumn(oneCol);
	}

	public double arrayMult(double[] a, double[] b){
		if (a.length != b.length){
			System.out.println("a != b");
			return 0;
		}
		else {
			double sum = 0;
			for (int n=0; n < a.length; n++){
				sum = sum + a[n]*b[n];
			}
			return sum;
		}
	}

	public void beginExecution(){
		countPredCol = 0;
		minDist = -1;
		minDistIndex = 0;
	}

	public TableImpl testTableLWRGen(Table xTble, int i) {
		//the output column of the Traintable is copied into the last 
		//column of the xTable.  Thus, we can grab column N-1 for the
		//predictions

		double[] predArray = new double[xTble.getNumRows()];
		xTble.getColumn(predArray, N-1);
		DoubleColumn predCol = new DoubleColumn(predArray);
		predCol.setLabel(xTble.getColumnLabel(N-1));

		double[] anArray = new double[xTble.getNumRows()];
		xTble.getColumn(anArray, i);
		DoubleColumn col = new DoubleColumn(anArray);
		col.setLabel(xTble.getColumnLabel(i));
		//System.out.println("the label at testTableLWRGen is "+xTble.getColumnLabel(i));

		DoubleColumn[] c = {col, predCol};
		TableImpl t = (TableImpl)DefaultTableFactory.getInstance().createTable(c);
		//System.out.println("the second label is "+t.getColumnLabel(1));
		return t;
	}

	/**
		generatePlotValues()
		Uses the Traintable ExampleTable to generate a TableImple
		with a column for each input Column in Traintable.  There are 
		num_pts values in each column, equally spaced from the min to 
		the max.
		@param double[] x - a vector
		@param double[] q - a vector
		return double ans - the distance
	*/

	public TableImpl generatePlotValues() {

		//int numCol = Traintable.getNumColumns();
		int numRow = Traintable.getNumRows();
		TableImpl returnTable = (TableImpl)DefaultTableFactory.getInstance().createTable();

		int inputCols[] = Traintable.getInputFeatures();
		int outputCols[] = Traintable.getOutputFeatures();

		for (int i=0; i< inputCols.length; i++){
			//System.out.println("inputCols["+i+"] = "+inputCols[i]);
			double min, max;
			double[] doubAr = new double[numRow];
			Traintable.getColumn(doubAr, inputCols[i]);
			min = getMin(doubAr);
			max = getMax(doubAr);
			DoubleColumn doubCol = new DoubleColumn(fillXs(min,max,num_pts));
			doubCol.setLabel(Traintable.getColumnLabel(inputCols[i]));
			returnTable.addColumn(doubCol);
		}
		
		for (int j=0; j< outputCols.length; j++){
			double min, max;
			//System.out.println("outputCols["+j+"] = "+outputCols[j]);
			double[] doubAr2 = new double[numRow];
			Traintable.getColumn(doubAr2, outputCols[j]);
			returnTable.addColumn(doubAr2);
			returnTable.setColumnLabel(Traintable.getColumnLabel(outputCols[j]), returnTable.getNumColumns()-1);
		}

	/*	for (int i=0; i< numCol-1; i++){
			double min, max;
			double[] doubAr = new double[numRow];
			Traintable.getColumn(doubAr, i);
			min = getMin(doubAr);
			max = getMax(doubAr);
			DoubleColumn doubCol = 	new DoubleColumn(fillXs(min,max,num_pts));
			doubCol.setLabel(Traintable.getColumnLabel(i));
			returnTable.addColumn(doubCol);
		}
		double[] dubAr2 = new double[numRow];
		Traintable.getColumn(dubAr2, numCol-1);
		returnTable.addColumn(dubAr2);
		returnTable.setColumnLabel(Traintable.getColumnLabel(numCol-1), returnTable.getNumColumns()-1);*/

		return returnTable;

	}


	public double getMin(double[] c){
		double min = c[0];
		for (int k=1; k<c.length; k++){
			double value = c[k];
			if (value < min)
				min = value;
		}
		return min;
	}

	public double getMax(double[] c){
		double max = c[0];
		for (int k=1; k<c.length; k++){
			double value = c[k];
			if (value > max)
				max = value;
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


	public void setKernelSelector(int x){
		kernelSelector = x;
	}

	public void setDistanceSelector(int y){
		distanceSelector = y;
	}

	public void setNearestNeighbors(int x){
		nearestNeighbors = x;
	}

	public void setUseNearestNeighbors(boolean a){
		useNearestNeighbors = a;
	}

	public int getKernelSelector(){
		return kernelSelector;
	}

	public int getDistanceSelector(){
		return distanceSelector;
	}

	public int getNearestNeighbors(){
		return nearestNeighbors;
	}

	public boolean getUseNearestNeighbors(){
		return useNearestNeighbors;
	}

}

