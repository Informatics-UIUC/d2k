package ncsa.d2k.modules.core.datatype.array;

import ncsa.d2k.infrastructure.modules.*;

/**
   Compute the mean of an array of numbers.
   @author David Clutter
*/
public class Mean extends ComputeModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
	return "This module computes the mean of an array of numbers.  The"+
	    " array can be an array of ints, an array of floats, or an array"+
	    " of doubles.";
    }
    
    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "Mean";
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
	String[] out = {"java.lang.Double"};
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
	    return "The mean of the input array.";
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
	    return "Mean";
	else
	    return "No such output!";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
	double mean = 0;
	Object o = this.pullInput(0);

	if(o instanceof int[]) {
	    int t = total((int[])o);
	    mean = (double) t / ((int[])o).length;
	}
	else if(o instanceof float[]) { 
	    float t = total((float[])o);
	    mean = (double) t / ((float[])o).length; 
	}
	else if(o instanceof double[]) {
	    double t = total((double[])o);
	    mean = (double) t / ((double[])o).length;  
	}
	
	pushOutput(new Double(mean), 0);
    }

    /**
       Return the total of an array of ints.
       @param A The array of ints
       @return The total
    */
    protected int total(int[] A) {
	int tot = 0;
	for(int i = 0; i < A.length; i++)
	    tot = tot + A[i];
	return tot;
    }
    
    /**
       Return the total of an array of floats.
       @param A The array of floats
       @return The total
    */
    protected float total(float[] A) {
	float tot = 0;
	for(int i = 0; i < A.length; i++)
	    tot = tot + A[i];
	return tot;
    }
    
    /**
       Return the total of an array of doubles.
       @param A The array of doubles
       @return The total
    */
    protected double total(double[] A) {
	double tot = 0;
	for(int i = 0; i < A.length; i++)
	    tot = tot + A[i];
	return tot;
    }
}
