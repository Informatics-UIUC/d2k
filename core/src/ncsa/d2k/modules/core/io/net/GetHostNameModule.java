package ncsa.d2k.modules.core.io.net;


import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

/**
		Given a string that must be a URL, this module will identify the host machine, and pass that along as an output string along with the original URL.
*/
public class GetHostNameModule extends ncsa.d2k.core.modules.DataPrepModule {

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This string will contain the name of the host.";
			case 1: return "The original URL is passed along as an output.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the URL containing the host name.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Given a string that must be a URL, this module will identify the host     machine, and pass that along as an output string along with the original     URL.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"java.net.URL","java.lang.String"};
		return types;
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GetHostNameModule";
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
