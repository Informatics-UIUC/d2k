package ncsa.d2k.modules.core.discovery.pca;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;


/**
	Performs PCA on an ExampleTable's
	input columns.  This module simply finds the
	weights. Push to 'ApplyWeightedSummation' module
	to do the transformation for them. The method was found in:
	
	<p>Johnson, R.A. and D.W. Wichern, <u>Applied
	Multivariate Statistical Analysis, Prentice Hall,
	Upper Saddle River, NJ, 1998.

	Specific Functions come from the following sources:

	Press, etc. <b>Numerical Recipes in C<b>, Second Edition, 1992

	Wilkinson, J.H., Reinsch, C. <b> Handbook for Automatic Computation
	: Volume II, Linear Algebra</b>, 1971
	
	@author pgroves
	*/

public class PrincipalComponents extends ComputeModule 
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////

	
	boolean verbose=false;	
	
	boolean doScalingHere=false;	

	int numComponents=5;

	boolean useCorrelationMatrix=true;
	/////////////////////////
	/// other fields
	////////////////////////
	transient double[][] tridiag;
	transient double[][] covMat;

	transient java.util.Random rand;

	/**the loadings are the weights, or eigenvectors*/
	MutableTableImpl loadings;
	
	/**this holds the eigenvalues*/
	MutableTableImpl eigTable;

	/**Returns the loadings, or eigenvectors*/
	public MutableTableImpl getLoadings(){return loadings;}
	
	/**Returns the eigenvalues (which are measures of the information
	content of each component*/
	public MutableTableImpl getEigenValues(){return eigTable;}

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////

	/*	do it*/
	public void doit() throws Exception{
		if(verbose){
			  System.out.println(this.getAlias()+": Firing");
		}
		ExampleTable et=(ExampleTable)pullInput(0);
		
		computeLoadings(et);
		
		pushOutput(loadings, 0);
		pushOutput(et, 1);
		pushOutput(eigTable, 2);

	}

	/** computeLoadings.
	 
	this does all the work. after setting all the necessary parameters,
	call this, and then the loadings and eigenvalues can be accessed from
	the getter methods
	*/
	public void computeLoadings(ExampleTable et){
		rand=new java.util.Random();
		
		int numColumns=et.getNumInputFeatures();
		int[] inputs =et.getInputFeatures();	
		if(doScalingHere){
			 scaleTable(et);
			if(verbose){
				System.out.println("Scaling Done");		
			}
		}			
		//find covariance matrix
		covMat=calcCovarianceMatrix(et);
		if(verbose){
			System.out.println("Covariance Matrix Calculated");		
		}
			
		//householder transform
		tridiag=tred1(covMat);
		
		if(verbose){
			System.out.println("TriDiag Done");		
		}
		//find the eigenvalues

		//first we need copies of some of the tridiag columns
		double[] triCol0=new double[tridiag[0].length];
		double[] triCol2=new double[tridiag[2].length];
		
		System.arraycopy(tridiag[0],0,triCol0,0,triCol0.length);
		System.arraycopy(tridiag[2],0,triCol2,0,triCol2.length);

		if(verbose){
			System.out.println("EigenValues");	
		}
		double[] eigVals=ratqr(	triCol0, triCol2, numComponents);
		
		//find the eigenvectors

		//set up the table to hold the loadings
		int numRows=numComponents;

		//check for the '-1' flag, and that it's in range
		if((numRows<1)||(numRows>numColumns)){
			 numRows=numColumns;
		}
		DoubleColumn[] cols=new DoubleColumn[numColumns];
		for(int i=0; i<numColumns; i++){
			cols[i]=new DoubleColumn(numRows);
			cols[i].setLabel("load-"+et.getColumnLabel(inputs[i]));
		}
	 	loadings=new MutableTableImpl(cols);

		//calculate each eigenvector
		double[] vect;
		for(int i=0; i<numRows; i++){
			if(verbose){
				System.out.println("Calc EigenVector:"+i);
			}
			vect=calcEigenVector(tridiag, eigVals[i]);
			//copy them in
			for(int j=0; j<numColumns; j++){
				loadings.setDouble(vect[j], i, j);
			}
			vect=null;
		}
		//convert them back to eigvectors of the original matrix
		if (verbose){
			System.out.println("Converting Eigenvectors back");
		}
		loadings=trbak1(covMat, tridiag[1], loadings);
		if (verbose){
			System.out.println("PCA done");
		}
			
		//put the eigenvalues in a table and push it out
		Column[] eigCols=new Column[1];
		eigCols[0]=new DoubleColumn(eigVals);
		eigTable=new MutableTableImpl(eigCols);
	}
	

///////////////////////////////////////////////////
//*	Covariance Matrix
//////////////////////////////////////////////////


	/**
		calculates the covariance matrix of the columns
		over all examples

		@param et the input ExampleTableImpl
		
		@return cov the covariance matrix in a TableImpl
	*/
	public double[][] calcCovarianceMatrix(ExampleTable et){
		int i,j,k;
		
		int numCols=et.getNumInputFeatures();
		int[] inputs=et.getInputFeatures();

		double[][] covs=new double[numCols][numCols];
		
		//calculate the means
		double[] means=new double[numCols];
		double[] stdevs=new double[numCols];

		for(i=0; i<numCols; i++){
			means[i]=mean(et,inputs[i]);
			if(useCorrelationMatrix){
				stdevs[i]=stdev(et,inputs[i], means[i]);
			}
		}
		for(i=0; i<numCols;i++){
			for(j=i; j<numCols; j++){
				double d=covariance(et,inputs[i],means[i],inputs[j],means[j]);
				//the matrix is symmetric
				if(useCorrelationMatrix){
					d/=(stdevs[i]*stdevs[j]);
				}
				covs[i][j]=d;
				covs[j][i]=d;
			}
		}

		return covs;
	}
	
	/**
		calculates the covariance between two columns or the
		variance of one column if both the columns are the same
		*/
	public double covariance(ExampleTable et,
							int colI,
							double meanI,
							int colJ,
							double meanJ){
		
		double cov=0.0;
		double p;
		int rows=et.getNumRows();
		for(int r=0; r<rows; r++){
			p=(et.getDouble(r,colI)-meanI)*(et.getDouble(r,colJ)-meanJ);
			cov+=p;
		}
		cov/=(rows-1);
		return cov;
	}

	/**
		calculates the mean of a column
		*/
	private double mean(ExampleTable et, int colIndex){
		double d=0.0;
		int rows=et.getNumRows();
		for(int i=0; i<rows; i++){
			d+=et.getDouble(i, colIndex);
		}
		d/=rows;
		return d;
	}
	/**
		calculates the stdev of a column
		*/
	private double stdev(ExampleTable et, int colIndex, double mean){
		double d=0.0;
		double d2=0.0;
		int rows=et.getNumRows();
		for(int i=0; i<rows; i++){
			d2=et.getDouble(i, colIndex)-mean;
			d2*=d2;
			d+=d2;
		}
		d/=rows;
		d=Math.sqrt(d);
		return d;
	}


	/**linearly scale the input columns of an exampe table, in place,
	to the range [0,1] */
	private void scaleTable(ExampleTable et){

		double oldMin, oldMax, oldRange, d;
		int j;

		int numInputs=et.getNumInputFeatures();
		int numRows=et.getNumRows();

		int[] inputs =et.getInputFeatures();	

		double[] toMins=new double[numInputs];
		double[] toMaxs=new double[numInputs];
		for(j=0;j<numInputs;j++){
			 toMins[j]=0.0;
			 toMaxs[j]=1.0;
		}
		ScalingTransformation st=
			new ScalingTransformation(inputs,toMins,toMaxs,et);

		st.transform(et);
		et.addTransformation(st);

	}
	

//////////////////////////////////////////
////* Finding Eigenvalues*/
///////////////////////////////////////////

	/****************************************************
	*	tred1
	*
	*	reduces the lower triangle of a symmetric
	*	matrix to a symmetric tridiagonal form using Householder's reduction.  
	*	The upper half remains the original upper half of the 
	* 	input symmetric matrix.
	*	Adapted from Handbook for Automatic Computing,
	*	Vol ii, Linear Algebra. Wilkinson and Reinsch
	*	
	*	@param matrix the nXn matrix to be transformed in place
	*
	*	@return TableImpl 	Three columns, all length n,
	*						 	Col1- Diagonal elements of the TriD matrix
	*							Col2- n-1 off diagonal elements (idx 0=0)
	*							Col3- col2 values squared
	updated 05/30/03
	********************************/
	public double[][] tred1(double[][] mat){
		//the n in the nXn matrix
		int n=mat.length;
		
		double[] diag=new double[n];
		double[] offDiag=new double[n];
		double[] offDiag2=new double[n];

		double f, g, h, scale;
		
		for(int i=0; i<n; i++){
			diag[i]=mat[i][i];
		}
		for(int i=n-1; i>=0; i--){
			if(verbose&&((i % 50) ==0)){
				System.out.println("tridiag iteration"+i);
			}
			int l=i-1;
			h=0.0;
			scale=0.0;
			
			for(int k=0; k<=l; k++){
				scale+=Math.abs(mat[i][k]);
			}
			
			//if h is too small to guarantee orthogonality,
			//skip to the end of this iteration
			if(scale==0.0){
				offDiag[i]=0.0;
				offDiag2[i]=0.0;
				
			}else{
			
				for(int k=0; k<=l; k++){
					double d=mat[i][k];
					d/=scale;
					h+=d*d;
					mat[i][k]=d;
				}
			
				offDiag2[i]=h*scale*scale;
				f=mat[i][l];
			
				if(f>0){
					g=-1*Math.sqrt(h);
				}else{
					g=Math.sqrt(h);
				}
				offDiag[i]=g*scale;
				h-=(f*g);
				mat[l][i]=f-g;
				f=0.0;
				
				for(int j=0; j<=l; j++){
					g=0.0;
					for(int k=0; k<=j; k++){
						g+=(mat[j][k]*mat[i][k]);
					}
					for(int k=j+1; k<=l; k++){
						g+=(mat[k][j]*mat[i][k]);
					}
					offDiag[j]=g/h;
					f+=offDiag[j]*mat[i][j];
				}
				h=f/(h+h);
				for(int j=0; j<=l; j++){
					f=mat[i][j];
					offDiag[j]-=h*f;
					g=offDiag[j];
					for(int k=0; k<=j; k++){
						double d=mat[j][k];
						d-=(f*offDiag[k])+(g*mat[i][k]);
						mat[j][k]=d;

					}
				}
				
				for(int k=0; k<=l; k++){
					mat[i][k]*=scale;
				}
				
			}	
			h=diag[i];
			diag[i]=mat[i][i];
			mat[i][i]=h;
		}
	

		double[][] retCols=new double[3][];
		retCols[0]=diag;
		retCols[1]=offDiag;
		retCols[2]=offDiag2;
		return retCols;
	}

	/****************************************************
	*	trbak1
	*
	*	Converts the m eigenvectors of the tridiagonalized
	*	matrix into eigenvectors of the original matrix
	*	actually does it in place, but the table is returned
	*	anyway
	*
	*	Adapted from Handbook for Automatic Computing,
	*	Vol ii, Linear Algebra. Wilkinson and Reinsch
	*
	*	@param tred1Matrix the matrix that was <i>input</i> 
	*							into tred1
	*	@param offDiag	the double array of off-diagonals
	*					generated by tred1
	*	@param eigVectors	m eigvectors, with 1 vector being a row
	*						in the table 	
	*
	*	@return MutableTableImpl the modified (but same) eigenvectors table
	updated 05/30/03
	********************************/
	public static MutableTableImpl trbak1(double[][] tred1Matrix,
										double[] offDiag,
										MutableTableImpl eigVectors){
			
		int m=eigVectors.getNumRows();
		int n=tred1Matrix.length;
		
		double h,s,d;
		int l,j,k;
		for(int i=1; i<n; i++){
			if(offDiag[i]!=0){
				l=i-1;
				h=offDiag[i]*tred1Matrix[i][i-1];
				for(j=0; j<m; j++){
					s=0.0;
					for(k=0; k<=l; k++){
						s+=tred1Matrix[i][k]*eigVectors.getDouble(j, k);
					}
					s/=h;
					
					for(k=0; k<=l; k++){
						d=eigVectors.getDouble(j,k);
						d+=s*tred1Matrix[i][k];
						eigVectors.setDouble(d, j, k);
					}
				}
			}
		}
		return eigVectors;
	}
		
	/*******************************************************
	*	ratqr
	*
	*	Finds the k largest eigenvalues of the tridiagonal matrix
	*	Adapted from Handbook for Automatic Computing,
	*	Vol ii, Linear Algebra. Wilkinson and Reinsch
	*	
	*	@param diag	the diagonal column from tred1
	*	@param off2	the squared off-diagonals from tred1
	*	@param eigCount	the number of eigenvalues wanted
	*
	*	@returns double[] the eigenvalues	
	*
	*	updated 5/30/03 pgroves
	**************************************************/
	public double[] ratqr(	double[] diag, 
									double[] off2, 
									int eigCount){

		double[] d= diag;
		//modify to find largest eigenvalues
		for(int i=0; i<d.length; i++){
			d[i]*=-1;
		}
		
		double[] b2=off2;
		int n=d.length;
		int m=eigCount;
		boolean posdef=false;
		double dlam=0.0;
		double eps=Double.MIN_VALUE;

		double delta, e, ep, err, p, q, qp, r, s, tot;
		qp=0.0;
		int i;
		b2[0]=err=q=s=0;
		tot=d[0];

		for(i=n-1; i>=0; i--){
			p=q;
			q=Math.sqrt(b2[i]);
			e=d[i]-p-q;
			if(e<tot){
				tot=e;
			}
		}
		if((posdef)&&(tot<0)){
			tot=0;
		}else{
			for(i=0; i<n; i++){
				d[i]-=tot;
			}
		}
		for(int k=0; k<m; k++){
			boolean convergence=false;
			//QR Transform
			do{
				tot+=s;
				delta=d[n-1]-s;
				i=n-1;
				e=Math.abs(eps*tot);
				if(dlam<e){
					dlam=e;
				}
				if(delta>dlam){
					e=b2[n-1]/delta;
					qp=delta+e;
					p=1;
					for(i=n-2; i>=k ;i--){
						q=d[i]-s-e;
						r=q/qp;
						p=p*r+1;
						ep=e*r;
						d[i+1]=qp+ep;
						delta=q-ep;
						if(delta<=dlam){
							//break to convergence
							convergence=true;
							break;
						}
						e=b2[i]/q;
						qp=delta+e;
						b2[i+1]=qp*ep;
					}
					if(!convergence){
						d[k]=qp;
						s=qp/p;
					}
				}else{
					convergence=true;
					break;
					}
			}while((!convergence)&&(tot+s>tot));

			if(!convergence){
				s=0;
				i=k;
				delta=qp;
				for(int j=k+1; j<n; j++){
					if(d[j]<delta){
						i=j;
						delta=d[j];
					}
				}
			}
			//convergence
			if(i<(n-1)){
				b2[i+1]=b2[i]*e/qp;
			}
			for(int j=i-1; j>=k; j--){
				d[j+1]=d[j]-s;
				b2[j+1]=b2[j];
			}
			d[k]=tot;
			b2[k]=err=err+Math.abs(delta);
			if(verbose){
				System.out.println("Eigenvalue "+k+" = "+(d[k]*-1));
			}
		}//end k
		
		//undo intial negation
		for(i=0; i<d.length; i++){
			d[i]*=-1;
		}
		double[] ret=new double[m];
		for(i=0; i<m; i++){
			ret[i]=d[i];
		}
		return ret;

	}
	
	/***************************************************
	*	calcEigenVector
	*
	*	Takes an eigenvalue and a tridiagonal matrix 
	*	and solves for the associated eigenvector. Uses inverse iteration
	*	by finding the LU decomposition, then solving (A-lambdaI)y=b
	* 	and iteratively replacing b w/ y at each step. (b is randomly 
	*	initialized)
	*
	*	Mainly from ideas presented in NR in C, but
	*	also from Wilkinson and Reinsch.
	*
	*	@param matrix, the output matrix from tred1
	*	@param eig	the value of the eigenvalue
	*
	*	@return double[] the n dimensional eigenvector
	****************************************************/
	public double[] calcEigenVector(double[][] matrix, double eig){
		double[] diag=matrix[0];
		int n=diag.length;

		double[] diagBac=new double[n];
		System.arraycopy(diag,0,diagBac,0,n);

		double d;
		//make the matrix, A, into A-I*eig
		for(int i=0;i<n; i++){
			d=diag[i]-eig;
			diag[i]=d;
		}
		
		//this is what will actually be used to solve
		double[][] lu=LUDecomp(matrix);
		//put the copy back in
		double[] y;
		double[] b=new double[n];

		boolean converged=false;
		//boolean singular=false;
		double norm=0.0;
		int counter=0;
		int counter2=0;
		double dist;
		double pastDist=Double.MAX_VALUE;
		while(!converged){
			boolean improving=true;
			randomInit(b);
			normalize(b);
			counter2++;
			while(improving){
				if(counter>20){
					improving=false;
					break;
				}
				y=solveSystem(lu, b);
				normalize(y);
				dist=distance(y,b);
				
				if(dist<0.000005){
					converged=true;
					break;
				}
				if((dist<=2)&&(dist>1.97)){
					improving=false;
					double eigDelt=eig*.001*(((double)counter2)/2.0);
					if((counter2 % 2)==0)
						eigDelt*= -1;
					for(int i=0; i<n; i++){
						diag[i]+=eigDelt;
					}
					lu=LUDecomp(matrix);
				}
				b=y;
				counter++;
				pastDist=dist;
				
			}
			counter=0;
			if(counter2>40){
				converged=true;	
				//we'll return an eigenvector of all zeros, so that
				//this principal component will basically be null
				//(the component created from the eignevector will
				//be all zeroes)
				for(int i=0;i<n;i++){
					 b[i]=0;
				}
				if(verbose)
					System.out.println("******Did not converge*******");
			}
		}

		matrix[0]=diagBac;
		return b;		
	}
	/*calcEigenValue helper*/
		private void randomInit(double[] da){
		int n=da.length;
		for(int i=0; i<n; i++){
			da[i]=rand.nextDouble()*10;
		}
	}
	
	//calcEigenValue helper
	private void normalize(double[] da){
		double norm=0.0;
		int n=da.length;
		for(int i=0; i<n;i++){
			norm+=da[i]*da[i];
		}
		norm=Math.sqrt(norm);
		for(int i=0; i<n;i++){
			da[i]/=norm;
		}
	}
	//calcEigenValue helper
	private double distance(double[] a, double[] b){
		double d=0.0;
		int n=a.length;
		double t;
		for(int i=0; i<n; i++){
			t=a[i]-b[i];
			d+=t*t;
		}
		return Math.sqrt(d);
	}

	/*************************************************8
	**	solveSystem
	*
	*	solves for Ax=b, uses a premade LU Decomposition
	*	from NR in C, pg 43
	*
	*	@param A The LUDecomposition output
	*	@param b an nX1 matrix
	*
	*	@return double[] the solution, x
	*****************************************************/
	public static double[] solveSystem(double[][] A, double[]  b){

		int n=b.length;

		double[] lower=A[0];
		double[] diag=A[1];
		double[] upper=A[2];
		
		//solve for Ly=b
		double[] y=new double[n];

		y[0]=b[0];

		for(int i=1; i<n; i++){
			y[i]=b[i];
			y[i]-=y[i-1]*lower[i];
		}
		
		//solve for Ux=y
		double[] x=new double[n];

		x[n-1]=y[n-1]/diag[n-1];

		for(int i=n-2; i>-1; i--){
			x[i]=1/diag[i];
			x[i]*=y[i]-upper[i+1]*x[i+1];
		}
		return x;
	}

	
	/**************************************************
	*	LUDecomposition
	*
	*	calculates the LUDecomposition of a matrix
	*	optimized so that it expects a Symmetric Tridiagonal
	*	matrix.  It also DOES NOT DO PIVOTING, so it
	*	could fail if there are zeroes, but I can't
	*	think of an instance in this application where
	*	this would happen
	*	
	*	@param mat the matrix to perform the decomp on
	*				[0]is diag
	*				[1] is off diag		
	*
	*	@return double[][] [0] is the L off Diag
	*							[1] is the diag
	*							[2] is the U offDiag
	*							
	****************************************************/
	public static double[][] LUDecomp(double[][] mat){
		double[] diag=mat[0];
		double[] offDiag=mat[1];
		
		int n=diag.length;
		
		double[] lower=new double[n];
		double[] betaDiag=new double[n];
		double[] upper=new double[n];

		double ii=1.0;

		betaDiag[0]=diag[0];

		double bu=0.0;
		double bd=0.0;
		double al=0.0;
		
		//int i;
		for(int j=0; j<n; j++){
			//upper off diag
			
			bu=offDiag[j];
			upper[j]=bu;

			bd=diag[j];
			bd-=lower[j]*upper[j];
			betaDiag[j]=bd;
			if(j<n-1){
				al=1/betaDiag[j];
				al*=offDiag[j+1];
				lower[j+1]=al;
			}
		}
		
		double[][] retCols=new double[3][];
		retCols[0]=lower;
		retCols[1]=betaDiag;
		retCols[2]=upper;
		return retCols;
	}
			
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		 java.net.URL url = getClass().getResource("/images/pca-diag.jpg");
		return 
		"<p><b>Overview</b>: Principal Component Analysis (PCA) involves project"+
		"ing a data set in N dimensions into a new set of dimensions. These new"+
		" dimensions are orthogonal to each other, but are oriented in such a"+
		" way that they can be rank ordered in terms of information content."+
		" This module can output the top M dimensions (M &lt N), which will "+
		"contain"+
		" much of the information in the input features set using a smaller"+
		" number of features. This module outputs vectors representing the"+
		" new dimensions, a metric of each dimension's information content, and"+
		" the original data set, scaled and ready to be projected into the "+
		"new dimensions."+ 

		"</p><p><b>Detailed Description</b>:"+
		" The simple 2-D graph shows the results of a PCA. The data is plotted "+
		"in the original (x,y) coordinate system. The principal component analy"+
		"sis yields the vectors A and B (which are orthogonal to each other)."+
		" Vector A contains most of the information, or discrimination between"+
		" points, found in the data set. Vector B presents the rest. Note that"+
		" the magnitude of the vectors is determined by their information conte"+
		"nt, and A is therefore longer than B."+
		"<p><img src=\""+url.toString()+"\"></p>"+
		"<p> It possible that in some data sets, particularly those where the "+
		" features are highly correlated, that the computation of some (or most)"+
		" of the component computations will fail. In this case, the loadings "+
		"for that component will be all zeros. This will hopefully allow any "+
		" other computations dependent on the PCA to continue using the good "+
		" components along with the components"+
		" that contain no information, but will be valid in any computation."+	

		"</p><p><b>Data Type Restrictions</b>:"+
		"Only numeric input features are supported. If non-numeric input"+
		" features are present, the algorithm will fail."+
		
		"</p><p><b>Data Handling</b>:"+
		" The input features"+
		" will be scaled <i>in place</i> to the interval [0,1]. The table"+
		" will then be pushed out by output 2."+
		

		"</p><p><b>Scalability</b>:"+
		" PCA analysis does not scale well with the number of dimensions. The "+
		"bulk of the computation is done on the correlation matrix, which is "+
		" of size n by n, with n the number of dimensions. The correlation mat"+
		"rix is computed in linear time with respect to number of examples, "+
		"however, and therefore scales well with increasing number of examples."+
		 
		"</p><p><b>References</b>:</p>"+
		" <p>The main algorithm was taken from:</p>"+
		"<p>Johnson, R.A. and D.W. Wichern, <u>Applied	Multivariate Statistical"+
		" Analysis, Prentice Hall, Upper Saddle River, NJ, 1998.</p>"+

		" <p>Various linear algebra functions come from the following sources:"+
		"</p>"+
		"<p>Press, et al. <u>Numerical Recipes in C</u>, Second Edition, 1992."+

		"</p><p>Wilkinson, J.H., Reinsch, C. <u> Handbook for Automatic "+
		"Computation : Volume II, Linear Algebra</u>, 1971.</p>";
	}
	
   	public String getModuleName() {
		return "PCA";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "An example table with the InputColumns selected";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Data Table";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return 
				"The table of PCA weights (sometimes called loadings)"+
				".  The number of columns is the number"+
				" of InputFeatures from the input data set and the number of "+
				"rows is the number of Components";
			case 1: return 
				"The original data set, with the input features scaled in place."+
				" Feed this, along with the weights from the first output, to"+
				" the module ApplyWeightedSum to transform the data set into"+
				" the coordinate system defined by the PCA.";
			case(2): {
				return 
					"The Eigenvalues for the computed PC's (a table with one "+
					"column). These values correspond to the amount of information"+
					" in each component (newly defined dimension)";
			}
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Principal Component Weights";
			case 1:
				return "Scaled Data Set";
			case(2): {
				return "Eigenvalues";
			}
			default: return "NO SUCH OUTPUT!";
		}
	}	
	
    public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[4];
      pds[0] = new PropertyDescription("doScalingHere",
		 "Scale to a Common Range",
        "The input features of the data set should be scaled to a common inter"+
		  "val. If the data set already meets this criteria, set this to FALSE."+
		  " If not, set to TRUE and the data will be scaled to the interval [0,"+
		  "1] before processing begins.");
		pds[1] = new PropertyDescription("numComponents",
        "Number of Principle Components",
        "Specifies the number of most significant principal components to "+
		  "calculate. A value of -1 will produce the maximum number of compon"+
		  "ents (which"+
		  " is the number of input features of the input data set), regardless"+
		  " of what that maximum value is.");
		pds[2] = new PropertyDescription("useCorrelationMatrix",
        "Correlation or Covariance",
        "If true, the analysis will be based on a correlation matrix. If false"+
		  ", a covariance matrix. Both will produce a correct set of components,"+
		  " but they will be different from each other. For some data sets, one"+
		  " type of matrix will fail, producing a large amount of non-converging"+
		  " eigenvectors, while the other one will not. ");
      pds[3] = new PropertyDescription("verbose",
        "Generate Verbose Output",
        "If true, the module will print various statements"+
		  " about the progress of the module's execution to the console.");
      return pds;
    }		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setVerbose(boolean b){
		verbose=b;
	}
	public boolean getVerbose(){
		return verbose;
	}
	public boolean getDoScalingHere(){
		return doScalingHere;
	}
	public void setDoScalingHere(boolean b){
		doScalingHere=b;
	}
	public int getNumComponents(){
		return numComponents;
	}
	public void setNumComponents(int i){
		numComponents=i;
	}
	public boolean getUseCorrelationMatrix(){
		return useCorrelationMatrix ;
	}
	public void setUseCorrelationMatrix(boolean b){
		useCorrelationMatrix =b;
	}

}
	
