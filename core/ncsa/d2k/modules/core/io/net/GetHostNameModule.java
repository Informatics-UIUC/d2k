package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.infrastructure.modules.*;
import java.io.*;
import java.net.*;

/**
		GetHostNameModule.java
		
		Given a string that must be a URL, this module will identify the host machine, and pass that along as an output string along with the original URL.
*/
public class GetHostNameModule extends ncsa.d2k.infrastructure.modules.DataPrepModule {

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"This string will contain the name of the host.", 
				"The original URL is passed along as an output."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
				"This is the URL containing the host name."
		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "Given a string that must be a URL, this module will identify the host machine, and pass that along as an output string along with the original URL.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {"java.lang.String"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"java.net.URL",  "java.lang.String"};
		return temp;
	}

	/**
		Get the URL string and from that get the hostname and return both as outputs.
	*/
	protected void doit () throws Exception {
		String urlString = (String) this.pullInput (0);
		URL url = null;
		try {
			url = new URL (urlString);
		} catch (MalformedURLException e) {
			System.out.println ("The url was wrong -> "+(String) urlString);
			return;
		}
		
		this.pushOutput (url, 0);
		this.pushOutput (url.getHost (), 1);
	}
}
