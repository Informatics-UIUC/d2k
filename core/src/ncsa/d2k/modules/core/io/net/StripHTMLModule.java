package ncsa.d2k.modules.core.io.net;


import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.*;

/**
		This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.
*/
public class StripHTMLModule extends ncsa.d2k.core.modules.DataPrepModule {

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This output will contain the plain text with the HTML tags stripped out.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This input contains the HTML text.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will strip all the html tags out of the given data. A byte     array output stream is used to generate the resulting byte array.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"[B"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"[B"};
		return types;
	}

	public void doit () {
		byte [] html = (byte []) this.pullInput (0);
		if (html == null)
			this.pushOutput (null, 0);
		else {
			// How many bytes do we care about.
			int howMany = html.length;

			// The location of the last point copied up to.
			int lastLoc = 0;

			// put bytes here.
			ByteArrayOutputStream bos = new ByteArrayOutputStream ();

			// Find the next '<'
			int i = 0;
			for (; i < howMany; i++) {
				if (html[i] == (byte) '<') {
					// If there is anything, copy everything up to the tag into the output.
					if (lastLoc != i) {
						bos.write (html, lastLoc, i - lastLoc - 1);
						if (bos.size () > 300) {
							lastLoc = i + 1;
							break;
						}
					}

					// Find the end of the anchor.
					for (; i < howMany; i++)
						if (html[i] == (byte) '>')
							break;
					lastLoc = i+1;
				}
			}

			if (lastLoc < i)
				bos.write (html, lastLoc, i - lastLoc - 1);
			this.pushOutput (bos.toByteArray(), 0);
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "StripHTMLModule";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
