package ncsa.d2k.modules.demos.tutorial;

/**
*/
import ncsa.d2k.infrastructure.modules.*;
import java.util.*;

public class DateBranchModule extends ComputeModule {

	// This string is used to store the operation between
	// invocations.
	String operation = null;
	/**
		Return a description of this module.

		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "This branch module will provide the first output is the sole input string equals \"OperationsModule.OP1\", it will provide the second output if the input equals \"OperationsModule.OP2\", or it will provide the third output if the input string equals \"OperationsModule.OP3\"";
	}

	/**
		This method will return a text description of the the input indexed by
		the value passed in.

		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0:
				return "This input string is the key against which we will select an output to provide.";
			default:
				return "There is no such input.";
		}
	}

	/**
		After any itinerary completes execution, this method is call. This module will
		reset its' field which contains the string identifying the operation to perform.
	*/
	public void endExecution () {
		super.endExecution ();
		input = null;
	}

	/**
		Set the number of iterations.
		@param iterations this is the desired number of iterations.
	*/
	public void setNumberIterations (int iterations) {
		numIterations = iterations;
	}

	/**
		After any itinerary completes execution, this method is call. This module will
		reset its' sole input argument here, since this is continuous, and it will reuse
		the input for several iterations.
	*/
	private int testCounter;
	private int numIterations = 1000;
	public void beginExecution () {
		testCounter = numIterations;
	}

	/**
		This method will return a text description of the the output indexed by
		the value passed in.

		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0:
				return "Contains a Date object containing the current date if the input string equals \"OperationsModule.OP1\".";
			case 1:
				return "Contains a Date object containing the current date if the input string equals \"OperationsModule.OP2\".";
			case 2:
				return "Contains a Date object containing the current date if the input string equals \"OperationsModule.OP3\".";
			default:
				return "There is no such output.";
		}
	}

	/**
		Our input type is just the string that identified the operations.

		@returns an array of one entry, a string.
	*/
	public String [] getInputTypes () {
		String [] ins = {"java.lang.String"};
		return ins;
	}

	/**
		Our input type is just the string that identified the operations.

		@returns an array of three entries, all strings.
	*/
	public String [] getOutputTypes () {
		String [] outs = {"java.util.Date", "java.util.Date", "java.util.Date"};
		return outs;
	}

	/**
		the module should ONLY check if input from pipes is ready through this method - jjm-uniq
		return true if ready to fire , false otherwise
	*/
	long startTime;
	public boolean isReady () {
		if (testCounter < numIterations)
			if (testCounter == 0)
				return false;
			else
				return true;
		else
			if (inputFlags[0] == 0)
					return false;

		return true;
	}

	/**
		Ascertain the correct output to generate on the basis of the string
		passed in.

		@param inV the list of required inputs.
		@param outV list of results.
	*/
	String input = null;
	public void doit () {

		// First decrement the counter
		if (testCounter == numIterations)
			input = (String) this.pullInput( 0 );
		testCounter--;
		if (input.equals (OperationsModule.OP1)) {
			pushOutput (new Date (System.currentTimeMillis()), 0);
		} else if (input.equals (OperationsModule.OP2)) {
			pushOutput (new Date (System.currentTimeMillis()), 1);
		} else if (input.equals (OperationsModule.OP3)) {
			pushOutput (new Date (System.currentTimeMillis()), 2);
		} else
			System.out.println ("Whoaaaa");
	}
}
