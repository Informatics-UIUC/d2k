/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.net;
import ncsa.d2k.infrastructure.modules.*;
import java.util.*;
import java.io.*;
import java.net.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	ReadURLText.java
	
*/
public class ReadURLText extends ncsa.d2k.infrastructure.modules.InputModule implements Serializable {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n<D2K>\r\n  <Info common=\"URL\">\r\n    <Text>This input can be either a string or a URL object that is to be read.\n</Text>\r\n  </Info>\r\n</D2K>\r\n\r\n";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"java.lang.Object"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n<D2K>\r\n  <Info common=\"Text\">\r\n    <Text>THis is the textual data read from the url.\n</Text>\r\n  </Info>\r\n</D2K>\r\n\r\n";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"[B"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n<D2K>\r\n  <Info common=\"URLReader\">\r\n    <Text>This guy reads text from a given url into a byte array which is the output. The URL can be either a string or a URL.\n</Text>\r\n  </Info>\r\n</D2K>\r\n\r\n";
/*#end^7 Continue editing. ^#&*/
	}

	/** 
		the debug property.
	*/
	boolean debug = false;
	public void setDebugging (boolean db) { debug = db; }
	public boolean getDebugging () { return debug; }
	/**
		PUT YOUR CODE HERE.
	*/
	final private int buffsize = 2048;
	public void doit () throws Exception {
		URL webLoc;
		Object tmp = pullInput(0);
		if (tmp == null) {
			// We are done
			if (debug)
				System.out.println ("WE are done");
			this.pushOutput (null, 0);
			return;
		}

		if (tmp instanceof URL)
			webLoc = (URL) tmp;
		else
			webLoc = new URL ((String) tmp);
			
		if (debug)
			System.out.println ("Second read :"+webLoc.toString ());
		
		InputStream in = null;
		int contentLength = -1;
		// try to open a connection.
		try {
			URLConnection urlCon = webLoc.openConnection ();
			in = webLoc.openConnection ().getInputStream ();
		} catch (IOException e) {
			System.out.println ("IO Exception!");
			return;
		}
		
		// Stick the bytes into the byte array output stream.
		ByteArrayOutputStream out = new ByteArrayOutputStream ();
		byte [] buffer = new byte [2048];
		long lastTime = System.currentTimeMillis ();
		int count = 0;
		try {
			while (true) {
				int available = in.read (buffer, 0, buffsize);
				if (available < 0) {
					break;
				} else if (available == 0) {
					count++;
					System.out.println ("Yielding");
					Thread.currentThread().yield ();
					if (count == 5)
						break;
					continue;
				}
				count = 0;
				out.write (buffer, 0, available);
			}
			in.close ();
		} catch (Exception e) { // done
			System.out.println ("Exception ending read is "+e.getClass().getName());
		}
		
		// get our bytes.
		this.pushOutput (out.toByteArray (), 0);
	}

/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

