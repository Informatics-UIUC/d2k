package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.infrastructure.modules.*;
import java.util.*;
import java.io.*;

/**
		This module will strip all the html tags out of the given data. A byte array output stream is used to generate the resulting byte array.
*/
public class MakeNGramDataModule extends ncsa.d2k.infrastructure.modules.DataPrepModule {
	ByteArrayOutputStream byteOutput = null;

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"The output is the input data for use by the ngram procedure."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
				"The input is the text we want to run through ngram.",
				"This is just an input indicating the process is done."

		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "This module will take the given textual data and format it for use by the ngram program.";
		return text;
	}

	public String[] getInputTypes () {
		String[] temp = {"[B","java.lang.Boolean"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"[B"};
		return temp;
	}

	/**
		This module fires whenever it receives *either* of it's inputs, it
		does not want two inputs to execute.

		@param parentIndex the index of the parent providing the input.
		@param newInput the new input.
	*/
	public boolean isReady () {
		if (inputFlags[0] > 0 || inputFlags[1] > 0)
			return true;
		else
			return false;
	}

	final byte [] commentTag = {
			(byte) 'c',(byte) 'o',(byte) 'm',(byte) 'm',
			(byte) 'e',(byte) 'n',(byte) 't',(byte) ':', (byte)' '};
	final byte [] not = {
			(byte) 'N',(byte) 'O',(byte) 'T'};
	final byte [] newline = {(byte) 0x0A};
	int counter = 1;

	/**
		Write the newly formated ngram data to the byte array
		stream, and when done, send the whole byte array out.
	*/
	public void doit () {
		Boolean bool = (Boolean) this.pullInput (1);
		if (byteOutput == null)
			byteOutput = new ByteArrayOutputStream ();
		if (bool == null) {
			byte [] html = (byte []) this.pullInput (0);
			try {
				byteOutput.write (commentTag);
				String strng = new String (Integer.toString (counter)+" ");
				counter++;
				byteOutput.write (strng.getBytes ());
				int initialSize = byteOutput.size ();
				boolean noWhites = false;
				int linelen = 0;
				for (int i = 0; i < html.length ; i++) {
					switch ((int) html[i]) {
						case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
						case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
						case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
						case 's': case 't': case 'u': case 'v': case 'w': case 'x':
						case 'y': case 'z':
							html[i] -= 0x20;
						case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
						case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
						case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
						case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
						case 'Y': case 'Z':
							byteOutput.write ((byte) html[i]);
							noWhites = true;
							linelen++;
							break;
						default:
							// Strip all unprintable caracters, but ensure that is a space
							// between words. Only one space.
							if (noWhites) {
								if (linelen > 80) {
									byteOutput.write (newline);
									linelen = 0;
								} else {
									byteOutput.write ((byte)' ');
									linelen++;
								}
								noWhites = false;
							}
							break;
					}
				}
				if (byteOutput.size () == initialSize)
					byteOutput.write (not);
				byteOutput.write (newline);
			} catch (IOException e) {
				System.out.println ("How do ya get an IO exception writing a byte array????.");
			}
		} else {
			this.pushOutput (byteOutput.toByteArray (), 0);
			System.out.println ("~~~~ have completed prep.....");
			byteOutput = null;
		}
	}
}
