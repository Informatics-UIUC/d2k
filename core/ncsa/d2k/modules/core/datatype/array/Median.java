package ncsa.d2k.modules.core.datatype.array;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.Quicksort;

/**
   Compute the median of an array of numbers.
   @author David Clutter
*/
public class Median extends ComputeModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
	return "This module computes the median of an array of numbers.  The"+
	    " array can be an array of ints, an array of floats, or an array"+
	    " of doubles.";
    }
    
    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "Median";
    }

    /**
       Return a String array containing the datatypes the inputs to this 
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String[] in = {"java.lang.Object"};
	return in;
    }
 
    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
	String[] out = {"java.lang.Object"};
	return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
	if(i == 0)
	    return "An array of numbers.  This can be an array of ints, "+
		"floats, or doubles.";
	else
	    return "No such input!";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
	if(i == 0)
	    return "Numbers";
	else
	    return "No such input!";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
	if(i == 0)
	    return "The median of the input array.  This will be a "+
		"java.lang.Integer, a java.lang.Float, or a "+
		"java.lang.Double, depending on the type of input array.";
	else
	    return "No such output!";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	if(i == 0)
	    return "Median";
	else
	    return "No such output!";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
	double mean = 0;
	Object o = this.pullInput(0);
	Object med = null;
	
	if(o instanceof int[])
	    med = median(Quicksort.sort((int[])o));
	else if(o instanceof float[]) 
	    med = median(Quicksort.sort((float[])o));
	else if(o instanceof double[])
	    med = median(Quicksort.sort((double[])o));
	
	pushOutput(med, 0);
    }
    
    /** 
	Find the median of the array.  If the array has an even number of 
	elements, return the average of the middle two elements.  The array
	is assumed to be sorted.
	@param A A sorted array of ints
	@return The median.  This will be either a java.lang.Integer (when
	the array has an odd number of elements) or a java.lang.Double (when
	the array has an even number of elements)
    */
    protected Object median(int[] A) {
	int len = A.length;
	int idx = 0;
	if( (len % 2) == 1) {
	    idx = (int) len/2;
	    return new Integer(A[idx]);
	}
	else {
	    idx = len/2;
	    int tot = A[idx-1] + A[idx];
	    double m = (double)tot/2;
	    return new Double(m);
	}
    }

    /** 
	Find the median of the array.  If the array has an even number of 
	elements, return the average of the middle two elements.  The array
	is assumed to be sorted.
	@param A A sorted array of floats
	@return The median.
    */
    protected Object median(float[] A) {
	int len = A.length;
	int idx = 0;
	if( (len % 2) == 1) {
	    idx = (int) len/2;
	    return new Float(A[idx]);
	}
	else {
	    idx = len/2;
	    float tot = A[idx-1] + A[idx];
	    double m = (double)tot/2;
	    return new Float(m);
	}
    }

    /** 
	Find the median of the array.  If the array has an even number of 
	elements, return the average of the middle two elements.  The array
	is assumed to be sorted.
	@param A A sorted array of doubles
	@return The median.
    */
    protected Double median(double[] A) {
	int len = A.length;
	int idx = 0;
	if( (len % 2) == 1) {
	    idx = (int) len/2;
	    return new Double(A[idx]);
	}
	else {
	    idx = len/2;
	    double tot = A[idx-1] + A[idx];
	    double m = (double)tot/2;
	    return new Double(m);
	}
    }
}
