package ncsa.d2k.modules.demos.tutorial;
import ncsa.d2k.infrastructure.modules.*;
import java.util.Date;

public class GetDateTimeModule extends ParseDateTimeModule {

	public synchronized void doit () {
		System.out.println (((Date)this.pullInput( 0 )).toString ());
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "This module prints the String equivalent of the Date object passed in.";
	}
}
