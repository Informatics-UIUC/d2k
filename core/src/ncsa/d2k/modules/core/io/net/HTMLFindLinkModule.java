package ncsa.d2k.modules.core.io.net;

import java.net.*;
import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
/**
	Given an array of bytes, this module will search it the array for all anchors,
	generating a list of anchors and also including the original bytes as an output.
*/
public class HTMLFindLinkModule extends DataPrepModule {

	/** enumerates all links we have visited, non visited twice. */
	Hashtable visited = new Hashtable ();

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "The byte array containing the HTML text.";
			case 1: return "A list of more unique anchors.";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the url of the parent document.";
			case 1: return "This is an array of bytes containing the HTML text.";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Given an array of bytes and the URL of the original document, this module     will search the HTML text for all anchors, generating a list of anchors     and also including the original bytes as an output. When the text input is     null, we are done.  </body></html>";
	}

	public String[] getInputTypes() {
		String[] types = {"java.net.URL","[B"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {"java.util.Vector","[B"};
		return types;
	}

	/**
		When we are done, clear the host and the list.
	*/
	public void endExecution () {
		super.endExecution ();
		visited = new Hashtable ();
	}

	/**
		Parse this guy looking for anchors, adding the href to a
		Vector each time we identify one.

		@param outV the outputs.
	*/
	public void doit() throws Exception {

		// First instantiate a ParserDelegater to ensure that the default DTD is
		// created and initiailized.
		URL parent = (URL) this.pullInput(0);
		if (parent == null) {
			this.pushOutput (null, 1);
		} else {
			byte [] html = (byte[]) this.pullInput (1);
			ParserDelegator pd = new ParserDelegator ();
			Vector newLinks = new Vector ();
			ByteParser parser = new ByteParser (DTD.getDTD ("html32"), newLinks,
					parent, parent.getHost (), visited);
			parser.parse (new ByteReader (html));
			this.pushOutput (newLinks, 0);
			this.pushOutput (html, 1);
		}
	}
}

/**
	This is a reader that provides data from a simple array of bytes.
*/
class ByteReader extends Reader {

	/** the data we are reading. */
	byte [] data = null;

	/** the current position in the byte array. */
	int position = 0;

	/**
		Constructor must be given the bytes to read from.

		@param bytes the bytes containing the html.
	*/
	public ByteReader (byte [] bytes) {
		this.data = bytes;
	}
	/**
		There is really nothing to close, I will null the reference to the
		byte array, so that it may be garbage collected.
	*/
	public void close () {
		this.data = null;
	}

	/**
		Read characters into a portion of an array.  This method will block
		until some input is available, an I/O error occurs, or the end of the
		stream is reached.

		@param      cbuf  Destination buffer
		@param      off   Offset at which to start storing characters
		@param      len   Maximum number of characters to read

		@return     The number of characters read, or -1 if the end of the
					stream has been reached

		@exception  IOException  If an I/O error occurs
	*/
	public int read(char cbuf[], int off, int len) throws IOException {

		// Are we done?
		if (position == this.data.length)
			return -1;

		// Determine how many characters we can read.
		int stopAt = (off + len) > (this.data.length - position) ? off + (this.data.length - position) :
			(off + len);
		int total = stopAt - off;
		// Fill the buffer.
		for (; off < stopAt ; off++) {
			cbuf[off] = (char) this.data [position];
			position++;
		}
		return total;
	}

}

class ByteParser extends javax.swing.text.html.parser.Parser {

	/** this is the attribute for an href. */
	static HTML.Attribute attkey = HTML.getAttributeKey ("href");

	/** The url of the parent document. */
	URL parent=null;

	/** number of additions to the linksQ from this page. */
	public int numberAdded = 0;

	/** The name of the host machine we are traversing. */
	String host;

	/** This hashtable enumerates all the places we have already visited. */
	Hashtable visited;

	/** This vector contains the list of links found. */
	Vector linksQ = null;

	/**
		Take the DTD, and a queue stream which we write he urls into.

		@param dtd the HTML dtd.
		@param qs the queue stream.
	*/
	public ByteParser (DTD dtd, Vector qs, URL parent, String host, Hashtable visited) {
		super (dtd);
		this.linksQ = qs;
		this.parent = parent;
		this.host = host;
		this.visited = visited;
	}

	/**
		An error has occurred.
	*/
	protected void handleError(int ln, String msg) {
	}

	/**
		This method is called when a start tag is encountered in the stream. We are
		only concerned with the anchor tag, and will use it to get the associated
		link.

		@param tag the tag element.
	*/
    protected void handleStartTag (TagElement tag) {
 		Element elem = tag.getElement();
		if (elem == dtd.getElement("a"))  {
			SimpleAttributeSet attrs = this.getAttributes ();
			String value = (String) attrs.getAttribute (attkey);
			try {
				URL resultURL = new URL (parent, value);

				// If it is not an http link, go on.
				if (!resultURL.getProtocol ().equals ("http")) {
					return;
				}

				String name = resultURL.toString ();
				if ((resultURL.getHost ().equals (host)) && (visited.size () < URLDispatchModule.search_size))
					if (name.endsWith (".htm") || name.endsWith (".html"))
						if (visited.get (name) == null) {

							// Just so we don't have to take up any more space, we will make the entry
							// associated with the key the hashtable itself.
							visited.put (resultURL.toString (), visited);
							numberAdded++;
							linksQ.addElement (resultURL);
						}
			} catch (MalformedURLException e) {}
		}
    }

	/**
		The only empty tag we use is the base tag, and we use that to resolve relative urls.

		@param tag the tag element.
	*/
	protected void handleEmptyTag (TagElement tag) throws ChangedCharSetException {
		Element elem = tag.getElement();
		if (elem == dtd.base) {
			SimpleAttributeSet attrs = this.getAttributes ();
			String value = (String) attrs.getAttribute (attkey);
			try {
				parent = new URL (parent, value);
			} catch (MalformedURLException e) {
			}
		}
    }

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "HTMLFindLinkModule";
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
