package ncsa.d2k.modules.core.io.file.output;

import ncsa.d2k.infrastructure.modules.*;
import java.util.*;
import java.io.*;

/**
	StripHTMLModule.java
		
	This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.
*/
public class WriteBytesModule extends ncsa.d2k.infrastructure.modules.OutputModule implements Serializable {
	private String filename = null;
	
	/**
		Set the file name to the string passed in.
		
		@param filename the new file name.
	*/
	public void setFilename (String fileName) {
		this.filename = fileName;
	}
	
	/**
		Set the file name to the string passed in.
		
		@param filename the new file name.
	*/
	public String getFilename () {
		return this.filename;
	}
	
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"This is just a trigger to start the ngram procedure.",
				"The name of the generated file is output."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
			"This input contains the byte data that we will next run through the NGram code."
		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "The module takes the input byte array and writes it to a file, passing the filename and a boolean trigger.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {"[B"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"java.lang.Boolean", "java.lang.String"};
		return temp;
	}

	public void doit () {
		byte [] html = (byte []) this.pullInput (0);
		try {
			File file = new File (filename);
			if (file.exists ())
				file.delete ();
			FileOutputStream fos = new FileOutputStream (new File (filename));
			fos.write (html);
			fos.close ();
		} catch (IOException e) {
			System.out.println ("WE GOT AN IO EXCEPTION.");
		}
		this.pushOutput (new Boolean (true), 0);
		this.pushOutput (filename, 1);
	}
}
