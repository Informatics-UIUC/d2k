package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.infrastructure.modules.*;
import java.util.*;
import java.io.*;

/**
		StripHTMLModule.java
		
		This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.
*/
public class StripHTMLModule extends ncsa.d2k.infrastructure.modules.DataPrepModule {

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"This output will contain the plain text with the HTML tags stripped out."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
				"This input contains the HTML text."
		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {"[B"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {			"[B"};
		return temp;
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
}
