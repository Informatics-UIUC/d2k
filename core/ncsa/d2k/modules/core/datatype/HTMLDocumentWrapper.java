package ncsa.d2k.modules.core.datatype;
import java.net.*;

/**
	This class contains the html text and the url from which the text was 
	gotten.
*/
public class HTMLDocumentWrapper {
	
	/** the text. */
	public byte [] html;
	
	/** the url where we found the html. */
	public URL parent;
	
	/**
		give the constructor the url and the data.
		
		@param parentUrl the url where the data is.
		@param data the actual html text.
	*/
	public HTMLDocumentWrapper (URL parentUrl, byte [] data) {
		this.html = data;
		this.parent = parentUrl;
	}
}
