package ncsa.d2k.modules.core.datatype.array;


import ncsa.d2k.core.modules.*;

/**
   Compute the standard deviation.
   @author David Clutter
*/
public class Variance extends ComputeModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module computes the variance and standard deviation of an array of     numbers.  </body></html>";
	}
    
    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "Variance";
	}

    /**
       Return a String array containing the datatypes the inputs to this 
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"java.lang.Object"};
		return types;
	}
 
    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"java.lang.Double","java.lang.Double"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "An array of numbers.  This can be an array of ints, floats, or doubles.";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Numbers";
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The variance of the input array.";
			case 1: return "The standard deviation of the input array.";
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Variance";
			case 1:
				return "StandardDeviation";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Perform the calculation.
    */
    public void doit() {       
	Object o = this.pullInput(0);
	Double variance = null;

	if(o instanceof int[])
	    variance = new Double(calcVariance((int[])o));
	else if(o instanceof float[])
	    variance = new Double(calcVariance((float[])o));
	else if(o instanceof double[])
	    variance = new Double(calcVariance((double[])o));
	
	pushOutput(variance, 0);
	Double stdDev = new Double(Math.pow(variance.doubleValue(), 0.5));
	pushOutput(stdDev, 1);
    }

    /**
       Calculate the variance.

       The formula used:
       variance = Sum(Xi^2) - (((Sum(Xi))^2)/n)
                  -----------------------------
		              n-1

       @param A an array of ints
       @return the variance
    */
    protected double calcVariance(int[] A) {
	double sumOfXiSquared = 0.0;
	double sumOfXi = 0.0;
	
	for(int i = 0; i < A.length; i++) {
	    sumOfXiSquared = sumOfXiSquared + Math.pow(A[i], 2);
	    sumOfXi = sumOfXi + A[i];
	}

	return ( (sumOfXiSquared - (Math.pow(sumOfXi, 2)/A.length)) 
		 / (A.length-1) );
    }

    /**
       Calculate the variance
       @param A an array of floats
       @param mean the mean of the array of floats
       @return the variance
    */
    protected double calcVariance(float[] A) {
	double sumOfXiSquared = 0.0;
	double sumOfXi = 0.0;
	
	for(int i = 0; i < A.length; i++) {
	    sumOfXiSquared = sumOfXiSquared + Math.pow(A[i], 2);
	    sumOfXi = sumOfXi + A[i];
	}

	return ( (sumOfXiSquared - (Math.pow(sumOfXi, 2)/A.length)) 
		 / (A.length-1) );
    }
    
    /**
       Calculate the variance
       @param A an array of doubles
       @param mean the mean of the array of doubles
       @return the variance
    */
    protected double calcVariance(double[] A) {
	double sumOfXiSquared = 0.0;
	double sumOfXi = 0.0;
	
	for(int i = 0; i < A.length; i++) {
	    sumOfXiSquared = sumOfXiSquared + Math.pow(A[i], 2);
	    sumOfXi = sumOfXi + A[i];
	}

	return ( (sumOfXiSquared - (Math.pow(sumOfXi, 2)/A.length)) 
		 / (A.length-1) );
    }
}
