package ncsa.d2k.modules.core.datatype.array;


import ncsa.d2k.core.modules.*;
import ncsa.util.Quicksort;
import ncsa.util.Queue;
import java.util.Vector;

/**
   Compute the mode of an array of numbers.
   @author David Clutter
*/
public class Mode extends ComputeModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module finds the mode of an array of numbers. This array can be an     array of ints, an array of floats, or an array of doubles.  </body></html>";
	}
    
    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "Mode";
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
		String[] types = {"java.util.Vector","java.lang.Integer"};
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
			case 0: return "A Vector containing the mode(s) if the input array. There is one entry in the Vector for each mode.  The objects in the Vector will be Integers when the input is an array of ints, Floats when the input is an array of floats, and Doubles when the input is an array of doubles.";
			case 1: return "The frequency of the mode(s) of the input array.";
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
				return "ModeVector";
			case 1:
				return "Frequency";
			default: return "NO SUCH OUTPUT!";
		}
	}

    int frequency;

    /**
       Perform the calculation.
    */
    public void doit() {  
	Object o = this.pullInput(0);
	Vector v = null;

	if(o instanceof int[])
	    v = findMode(Quicksort.sort((int[])o));
	else if(o instanceof float[]) 
	    v = findMode(Quicksort.sort((float[])o));
	else if(o instanceof double[])
	    v = findMode(Quicksort.sort((double[])o));
	
	pushOutput(v, 0);
	pushOutput(new Integer(frequency), 1);
    }

    /**
       Find the mode of an array of integers.  The array is assumed to
       be sorted.
       @param A a sorted array of ints
       @return a vector containing the mode(s) of the sorted array
    */
    protected Vector findMode(int[] A) {
    	int current = A[0];
	int currentTally = 1;

	int tallyToBeat = 1;

	Queue modes = new Queue();

	for(int i = 1; i < A.length; i++) {      
	    // if A[i] is equal to the current, increment the tally
	    if(current == A[i]) {
		currentTally++;
	    }
	    // otherwise we have reached the end of a run
	    else {
		// we have found a new winner.  Clear the old ones and
		// insert this number into the Hashtable.
		if(currentTally > tallyToBeat) {
		    modes.clear();	    
		    modes.push(new Integer(current));
		    tallyToBeat = currentTally;		    
		}
		// we have a tie.  Add this one to the Hashtable.
		else if(currentTally == tallyToBeat)	    
		    modes.push(new Integer(current));
	       	       
		// reset the variables.
		current = A[i];
		currentTally = 1;
	    }		 
	}

	// handle the last element
	// we have found a new winner.  Clear the old ones and
	// insert this number into the queue.
	if(currentTally > tallyToBeat) {
	    modes.clear();
	    modes.push(new Integer(current));
	    tallyToBeat = currentTally;	
	}
	// we have a tie.  Add this one to the queue
       	else if(currentTally == tallyToBeat)
	    modes.push(new Integer(current));
	
	Vector v = new Vector();
	Integer i;
	// copy the items from the Queue into a vector
	while( (i = (Integer)modes.pop()) != null )
	    v.addElement(i);

	frequency = tallyToBeat;
	return v;
    }

   /**
       Find the mode of an array of floats.  The array is assumed to
       be sorted.
       @param A a sorted array of floats
       @return a vector containing the mode(s) of the sorted array
    */
    protected Vector findMode(float[] A) {
       	float current = A[0];
	int currentTally = 1;

	int tallyToBeat = 1;

	Queue modes = new Queue();

	for(int i = 1; i < A.length; i++) {      
	    // if A[i] is equal to the current, increment the tally
	    if(current == A[i]) {
		currentTally++;
	    }
	    // otherwise we have reached the end of a run
	    else {
		// we have found a new winner.  Clear the old ones and
		// insert this number into the queue
		if(currentTally > tallyToBeat) {
		    modes.clear();	    
		    modes.push(new Float(current));
		    tallyToBeat = currentTally;		    
		}
		// we have a tie.  Add this one to the queue
		else if(currentTally == tallyToBeat)	    
		    modes.push(new Float(current));
	       	       
		// reset the variables.
		current = A[i];
		currentTally = 1;
	    }		 
	}

	// handle the last element
	if(currentTally > tallyToBeat) {
	    modes.clear();
	    modes.push(new Float(current));
	    tallyToBeat = currentTally;	
	}
       	else if(currentTally == tallyToBeat)
	    modes.push(new Float(current));
	    
	Vector v = new Vector();
	Float f;
	// copy the items from the Queue into a vector
	while( (f = (Float)modes.pop()) != null )
	    v.addElement(f);

	frequency = tallyToBeat;
	return v;
    }

   /**
       Find the mode of an array of doubles.  The array is assumed to
       be sorted.
       @param A a sorted array of doubles
       @return a vector containing the mode(s) of the sorted array
    */
    protected Vector findMode(double[] A) {
       	double current = A[0];
	int currentTally = 1;

	int tallyToBeat = 1;

	Queue modes = new Queue();

	for(int i = 1; i < A.length; i++) {      
	    // if A[i] is equal to the current, increment the tally
	    if(current == A[i]) {
		currentTally++;
	    }
	    // otherwise we have reached the end of a run
	    else {
		// we have found a new winner.  Clear the old ones and
		// insert this number into the queue
		if(currentTally > tallyToBeat) {
		    modes.clear();	    
		    modes.push(new Double(current));
		    tallyToBeat = currentTally;		    
		}
		// we have a tie.  Add this one to the queue
		else if(currentTally == tallyToBeat)	    
		    modes.push(new Double(current));
	       	       
		// reset the variables.
		current = A[i];
		currentTally = 1;
	    }		 
	}

	// handle the last element
	if(currentTally > tallyToBeat) {
	    modes.clear();
	    modes.push(new Double(current));
	    tallyToBeat = currentTally;	
	}
       	else if(currentTally == tallyToBeat)
	    modes.push(new Double(current));
	
	Vector v = new Vector();
	Double d;
     	// copy the items from the Queue into a vector
	while( (d = (Double)modes.pop()) != null )
	    v.addElement(d);

	frequency = tallyToBeat;
	return v;
    }
}
