package ncsa.d2k.modules.core.io.net;


import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
		This module takes two inputs; One is a vector containing a list of URLs, the other is a trigger that will always cause it to fire. When it fires, will simply take the next URL from the list and provide it as output.
*/
public class URLDispatchModule extends ncsa.d2k.core.modules.DataPrepModule {

	/** This is the list of links we will process. */
	Vector links = new Vector ();

	/** keep track of all the urls that we have traversed so we don't visit them twice. */
	Hashtable hashedURLs = new Hashtable ();

	/** the host we are interested in. */
	String host = null;

	/** This counter contains the number of urls that are in the pipe, that is URLs
	that have been dispatched for process, but for which we have not yet gotten
	the processed links. */
	private int processedCount = 1;

	final static public int search_size = 10000;

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This output is the URL to be loaded next.";
			case 1: return "This is a list of URLs that were traversed.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This string contains the name of the host we want to search.";
			case 1: return "This vector will contain a list of URLs.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module takes two inputs; One is a vector containing a list of strings     containing urls.  </body></html>";
	}

	public String[] getInputTypes() {
		String[] types = {"java.lang.String","java.util.Vector"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"java.net.URL","java.util.Hashtable"};
		return types;
	}

	/**
		When we are done, clear the host and the list.
	*/
	public void endExecution () {
		super.endExecution ();
		host = null;
		links = new Vector ();
		hashedURLs = new Hashtable ();
		processedCount = 1;
	}

	/**
		the module should ONLY check if input from pipes is ready through this method - jjm-uniq
		return true if ready to fire , false otherwise
	*/
	public boolean isReady () {
		if (host != null || inputFlags [0] > 0)
			if (links.size () > 0 || inputFlags[1] > 0){
				return true;
			}
		return false;
	}

	/**
		This method will simply put the next element in our list of urls into the output
		until that list is empty, then it will return only a boolean to indicate completion.
	*/
	public void doit () {
		// Get the host name of there is one.
		if (inputFlags [0] > 0)

			// We have gotten the host name.
			host = (String) this.pullInput (0);

		// Check to see if we have more links to add to our list.
		if (inputFlags[1] > 0) {

			// We got a response to one of the URLs in the pipe
			processedCount--;

			// copy the contents of the vector passed in into our vector.
			Enumeration enum = ((Vector)this.pullInput (1)).elements();

			// Check each of the urls, and it it appears to be html and it is
			// in our target domain, add it to the list.
			while (enum.hasMoreElements () && hashedURLs.size () < search_size) {
				URL obj = (URL) enum.nextElement ();
				String urlForm = obj.toExternalForm ();
				if (urlForm.endsWith (".html") || urlForm.endsWith (".htm"))
					if (obj.getHost ().equals (host))
						if (!hashedURLs.containsKey (urlForm)) {
							links.addElement (obj);
							hashedURLs.put (urlForm, urlForm);
						}
			}
		}

		if (links.size () == 0) {
			if (processedCount == 0) {
				this.pushOutput (hashedURLs, 1);
				this.pushOutput (null, 0);
			}
		} else {
			int index = links.size () - 1;
			processedCount++;
			this.pushOutput (links.elementAt (index), 0);
			links.removeElementAt (index);
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "URLDispatchModule";
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
