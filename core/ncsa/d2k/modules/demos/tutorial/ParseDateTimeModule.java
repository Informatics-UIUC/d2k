package ncsa.d2k.modules.demos.tutorial;
import ncsa.d2k.infrastructure.modules.*;
import java.util.*;

abstract public class ParseDateTimeModule extends OutputModule {
	String currentDate = null;
	String currentTime = null;
	
	public String [] getInputTypes () {
		String [] myInTypes  = {"java.util.Date"};
		return myInTypes;
	}

	public String [] getOutputTypes () {
		return null;
	}
	
	protected void parseDateTime (Date date) {
		Calendar calendar = Calendar.getInstance ();
		calendar.setTime (date);
		
		StringBuffer time = new StringBuffer ();
		time.append (calendar.get (Calendar.HOUR));
		time.append (":");
		time.append (calendar.get (Calendar.MINUTE));
		time.append (":");
		time.append (calendar.get (Calendar.SECOND));
		currentTime = time.toString();
		
		time = new StringBuffer ();
		time.append (calendar.get (Calendar.MONTH)+1);
		time.append ("/");
		time.append (calendar.get (Calendar.DATE));
		time.append ("/");
		time.append (calendar.get (Calendar.YEAR));
		currentDate = time.toString();
	}
    
	public synchronized void doit () {
		this.parseDateTime ((Date) this.pullInput (0));
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
				return "The input to this module is a date record..";
			default:
				return "There is no such input to this module.";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default:
				return "There are no outputs.";
		}
	}
}
