package ncsa.d2k.modules.demos.tutorial;
import ncsa.d2k.infrastructure.modules.*;

public class GetDateModule extends ParseDateTimeModule {

	public synchronized void doit () {
		super.doit ();
		System.out.println (currentDate);
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "This module parses a Date object to generate a date string and a time string, then it prints the date string.";
	}
}
