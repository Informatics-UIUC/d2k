package ncsa.d2k.modules.demos.modeldemo;

import ncsa.d2k.infrastructure.modules.*;
import java.io.Serializable;

/**
	This module accumulates the data passed in.  It also manages the looping in the
	itinerary.  It loops back to the beginning until curIteration == iterations.
	When this occurs, it pushes the average values for the three functions to SeedModelMaker.
	SeedModelMaker then creates a model based on these three average values.
*/
public class SeedAccumulatorModule extends ComputeModule implements Serializable {

	int curIteration = 1;
	
	
	public void endExecution() {
		super.endExecution();
		curIteration = 1;
	}
	
	// iterations is a property of this module.  Therefore it needs getter and setter methods.
	protected int iterations;
		
	/**
		Set the number of iterations.
		
		@param i the number of iterations
	*/
	public void setIterations(int i) {
		iterations = i;
	}
	
	/**
		Get the number of iterations.
		
		@return the number of iterations
	*/
	public int getIterations() {
		return iterations;
	}

	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo(int i) {
		switch(i) {
			case(0):
				return "The light function used in this experiment.";
			case(1):
				return "The moisture function used in this experiment.";
			case(2):
				return "The soil function used in this experiment.";
			case(3):
				return "The result of the experiment.";	
			default:
				return "No such input";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/		
	public String getOutputInfo(int i) {
		switch(i) {
			case (0):
				return "The average light function for successful experiments.";
			case (1):
				return "The average moisture function for successful experiments.";
			case (2):
				return "The average soil function for successful experiments.";
			case (3):
				return "The trigger used when looping back to the beginning of the itinerary.";	
			default:
				return "No such output.";
		}
	}
	
	/**
		Returns the input types, of which there are none.
		
		@returns the input types
	*/			
	public String []getInputTypes() {
		String []in = {"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.Boolean"};
		return in;
	}
	
	/**
		Returns the output types all of which are strings, the names
		of the possible operations.
		
		@returns the output types
	*/		
	public String []getOutputTypes() {
		String []out = {"java.lang.Double", "java.lang.Double", "java.lang.Double", "java.lang.Boolean"};
		return out;
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/		
	public String getModuleInfo() {
		return "This module accumulates the data.  This module is also used as a branching mechanism for"+
		" the itinerary.";
	}
	
	// these are the average values.
	double aveLight = 0.0;
	double aveMois = 0.0;
	double aveSoil = 0.0;
	
	// the number of successful experiments that have taken place
	int numSuccessful = 0;
	
	/**
		Perform these actions when this module is executed.
	*/		
	public void doit() {
System.out.println("NUM ITERATIONS: "+iterations);	
		// pull the inputs
		Double l = new Double( (String)this.pullInput(0) );
		Double m = new Double( (String)this.pullInput(1) );
		Double s = new Double( (String)this.pullInput(2) );
		String r = (String)this.pullInput(3);
		
		// if the experiment was successful, increment aveLight, aveMois, and aveSoil
		// otherwise just discard the results.
		if(r.equals("success")) {
			numSuccessful++;
			aveLight += l.doubleValue();
			aveMois += m.doubleValue();
			aveSoil += s.doubleValue();
		}	
		
		System.out.println("curIteration "+curIteration);
		
		// loop to the beginning until curIteration = iterations
		if(curIteration < iterations) {
			curIteration++;
			this.pushOutput(new Boolean(true), 3);
		}
		// when we have gone through all iterations, push the average values to
		// SeedModelMaker
		else {
			this.pushOutput(new Double( (aveLight/numSuccessful) ), 0);
			this.pushOutput(new Double( (aveMois/numSuccessful) ), 1);
			this.pushOutput(new Double( (aveSoil/numSuccessful) ), 2);
		}	
	}	
}
