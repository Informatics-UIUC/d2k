package ncsa.d2k.modules.core.io.file.output;


import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.*;

/**
	This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.
*/
public class WriteBytesModule extends ncsa.d2k.core.modules.OutputModule  {
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
		switch (index) {
			case 0: return "This is just a trigger to start the ngram procedure.";
			case 1: return "The name of the generated file is output.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This input contains the byte data that we will next run through the NGram code.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    The module takes the input byte array and writes it to a file, passing the     filename and a boolean trigger.  </body></html>";
	}

	public String[] getInputTypes () {
		String[] types = {"[B"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"java.lang.Boolean","java.lang.String"};
		return types;
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WriteBytesModule";
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
