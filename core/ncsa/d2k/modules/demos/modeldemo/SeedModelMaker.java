package ncsa.d2k.modules.demos.modeldemo;

import ncsa.d2k.infrastructure.modules.*;

/**
	This class creates a ModelModule.
*/
public class SeedModelMaker extends ModelGeneratorModule {

	/**
		These will be the inputs to this module.  We must save these values
		as instance variables so that we will have access to them beyond the
		scope of the doit() method.
		
		These values will be the threshhold values for the germination of a plant. If
		the experimental values passed into the model are greater than these threshhold
		values, the plant will grow.
	*/
	Double light = new Double(0.0);
	Double moisture = new Double(0.0);
	Double soil = new Double(0.0);
	
	/**
		Reset the values for light, moisture, and soil when execution is finished.
	*/
	public void endExecution() {
		super.endExecution();
		light = new Double(0.0);
		moisture = new Double(0.0);
		soil = new Double(0.0);
	}
	
	
	/**
		Perform these actions when this module is executed.
	*/		
	public void doit() {
		// pull the inputs and save them for use later.
		light = (Double)this.pullInput(0);
		moisture = (Double)this.pullInput(1);
		soil = (Double)this.pullInput(2);
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/	
	public String getModuleInfo() {
		return "Generates a SeedModel based on the four inputs.";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param index the index of the input we want the description of.
		@returns a text description of the indexed input
	*/		
	public String getInputInfo(int index) {
		switch(index) {
			case 0:
				return "A function of the amount of light the seed received.";
			case 1:
				return "A function of the amout of moisture the seed received.";
			case 2:
				return "A function of the soil the seed is planted in.";
			default:
				return "There is no such output.";
		}	
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/		
	public String getOutputInfo(int index) {
		return "There is no such output.";
	}

	/**
		Returns the input types.
		
		@returns the input types
	*/	
	public String []getInputTypes() {
		String []inputs = {"java.lang.Double", "java.lang.Double", "java.lang.Double"};
		return inputs;	
	}
	
	/**
		Returns the output types.
		
		@returns null since there are not outputs.
	*/			
	public String []getOutputTypes() {
		return null;
	}
	
	
	/**
		Returns an instance of a ModelModule.  NOTE: This method is called after the execution of the 
		itinerary is complete.
		
		@returns the ModelModule
	*/
	public ModelModule getModel() {
		return new SeedModel();	
	}
	
	/*********************************************************************************************
	 	This class will be the model.  It has access to all the fields of SeedModelMaker, so it is 
	 	easy to create the model.  The main work is done in the doit() method.
	*/
	class SeedModel extends ModelModule {
	
		/**
			Here we call setName() to set the name that this model will be known by in the
			Modules tree of D2K.
		*/
		public SeedModel() {
			setName("SeedModel");
		}
		
		/**
			Perform these actions when this module is executed.
		*/
		public void doit() {
			// pull our inputs.
			Double l = (Double)this.pullInput(0);
			Double m = (Double)this.pullInput(1);
			Double s = (Double)this.pullInput(2);
													
			//	if the inputs are greater than the threshhold values, the plant will germinate.
			//	otherwise it will fail.
			//	
			//	NOTE: light, moisture, and soil are instance variables from SeedModelMaker!										
			if( (l.doubleValue() >= light.doubleValue()) && (m.doubleValue() >= moisture.doubleValue()) && (s.doubleValue() >= soil.doubleValue()) ) 
				this.pushOutput(new Boolean(true), 0);
			// otherwise the seed will not germinate.
			else 
				this.pushOutput(new Boolean(false), 0);
		}
		
		/**
			Return a description of this module.
		
			@returns a text description of the modules function
		*/	
		public String getModuleInfo() {
			return "This model determines if a seed will germinate based on three parameters.";
		}
		
		/**
			Returns the input types.
			
			@returns the input types
		*/	
		public String []getInputTypes() {
			String []inputs = {"java.lang.Double", "java.lang.Double", "java.lang.Double"};
			return inputs;
		}
		
		/**
			Returns the output types.
		
			@returns the output types
		*/			
		public String []getOutputTypes() {
			String []out = {"java.lang.Boolean"};
			return out;
		}	
		
		/**
			This method will return a text description of the the input indexed by
			the value passed in.
		
			@param index the index of the input we want the description of.
			@returns a text description of the indexed input
		*/			
		public String getInputInfo(int i) {
			switch(i) {
				case 0: 
					return "The light index.";
				case 1:
					return "The moisture index.";
				case 2:
					return "The soil index.";
				default:
					return "There is no such input.";
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
				case(0):
					return "The result based on the three input parameters.";
				default:
					return "There is no such output!";
			}
		}	
	}
}
