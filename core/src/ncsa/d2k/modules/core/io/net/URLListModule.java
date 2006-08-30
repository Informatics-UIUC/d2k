/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.*;
import java.net.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	URLListModule.java
	
*/
public class URLListModule extends ncsa.d2k.core.modules.InputModule  {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {		};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This is the url of the next link to visit.        ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"java.net.URL"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will read one line at a time from a file, and generate a url     from that. If the input is the complete url, this module will do it,     however, if some change to the line is require to create the url, it will     have to be overridden.  </body></html>";
	}

	private RandomAccessFile raf = null;
	public void beginExecution () {
		raf = null;
	}
	public void endExectution () {
		try {
			raf.close ();
		} catch (IOException e) {
			// there is nothing we can do here.
		}
		raf = null;
	}
	
	// the property here is the name of the file to read.
	private String filename = null;
	public String getFilename () {
		return this.filename;
	}
	public void setFilename (String newName) {
		this.filename = newName;
	}
	
	/**
		Take the input, which is a line from the file we are reading, and
		construct a URL from it.
	*/
	protected URL createURL (String inputName) {
		URL tmp;
		try {
			tmp = new URL ("http://www.drugstore.com/pharmacy/prices/drugprice.asp?back=%2Fpharmacy%2Fdrugindex%2Fdefault%2Easp&drug=\""+inputName+"\"");
		} catch (MalformedURLException e) {
			return null;
		}
		return tmp;
	}
	
	/**
		Run until the pipe closes.
	*/
	public boolean isReady () {
		if (this.triggersActivated ()) 
		{
		System.out.println ("Triggered");
			return true;
		}
		else
		{
			if (raf == null)
				System.out.println ("Done");
			return raf != null;
		}
	}
	
	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		if (raf == null) 
		
			// The file must exist
			raf = new RandomAccessFile (filename, "r");;		
		
		String nextLine = raf.readLine ();
		if (nextLine != null) {
			URL url = this.createURL (nextLine);
			if (url != null) {
				System.out.println (url);
				this.pushOutput (url, 0);
			}
		} else {
			raf.close ();
			raf = null;
		}
		
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Read URLs";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
				return "URL";
			default: return "NO SUCH OUTPUT!";
		}
	}
}



