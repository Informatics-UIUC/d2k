package ncsa.d2k.modules.core.io.net;


import ncsa.d2k.core.modules.*;
import java.net.*;
import java.io.*;

/**
		Given a valid URL, this module will read in an HTML file, write it into a byte array, and provide the byte array as it's only output.
*/
public class HTMLReaderModule extends ncsa.d2k.core.modules.InputModule  {

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This byte array contains the result.";
			case 1: return "The reference to the document that contained the bytes.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the URL from which we load the initial document.";
			case 1: return "This is the URL which we are to load the current document.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Given a valid URL, this module will read in an HTML file, write it into a     byte array, and provide the byte array as it's only output.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"java.net.URL","java.net.URL"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"[B","java.net.URL"};
		return types;
	}

	/**
		the debug property.
	*/
	boolean debug = false;
	public void setDebugging (boolean db) { debug = db; }
	public boolean getDebugging () { return debug; }
	/**
		This module fires whenever it receives *either* of it's inputs, it
		does not want two inputs to execute.
	*/
	public synchronized boolean isReady () {
		if (this.inputFlags[0] > 0 || this.inputFlags[1] > 0)
			return true;
		else
			return false;
	}

	private final int buffsize = 2048;

	public void doit () {
		URL webLoc;
		if (this.inputFlags[1] > 0)
			webLoc = (URL) this.pullInput (1);
		else
			webLoc = (URL) this.pullInput (0);

		if (webLoc == null) {
			if (debug)
				System.out.println ("Done with first");
			this.pushOutput (null, 0);
			this.pushOutput (null, 1);
		} else {
			if (debug)
				System.out.println ("Reading URL : "+webLoc);
			InputStream in = null;
			int contentLength = -1;
			// try to open a connection.
			try {
				URLConnection urlCon = webLoc.openConnection ();
				in = webLoc.openConnection ().getInputStream ();
			} catch (IOException e) {
				System.out.println ("IO Exception!");
				this.pushOutput (new byte[0], 0);
				this.pushOutput (webLoc, 1);
				return;
			}

			// Stick the bytes into the byte array output stream.
			ByteArrayOutputStream out = new ByteArrayOutputStream ();
			byte [] buffer = new byte [buffsize];
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
			try {
				out.close ();
			} catch (IOException e) {}

			// get our bytes.
			this.pushOutput (out.toByteArray (), 0);
			this.pushOutput (webLoc, 1);

		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "HTMLReaderModule";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
