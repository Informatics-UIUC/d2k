/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.io.net;
import ncsa.d2k.infrastructure.modules.*;
import java.util.*;
import java.io.*;
import java.net.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	URLListModule.java
	
*/
public class URLListModule extends ncsa.d2k.infrastructure.modules.InputModule implements Serializable {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
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
};
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
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"URL\">    <Text>This is the url of the next link to visit. </Text>    <Text> </Text>  </Info></D2K>";
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
			"java.net.URL"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Read URLs\">    <Text>This module will read one line at a time from a file, and generate a url from that. If the input is the complete url, this module will do it, however, if some change to the line is require to create the url, it will have to be overridden. </Text></Info></D2K>";
/*#end^7 Continue editing. ^#&*/
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
}



