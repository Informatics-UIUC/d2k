//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

/**
 * An exception thrown by classes in this package.
 */
public class NotSupportedException extends Exception {

    /**
	 * Create a new NotSupportedException.
     */
    public NotSupportedException () {
        super();
    }

    /**
	 * Create a new NotSupportedException with the specified message.
     * @param msg the message to display
     */
    public NotSupportedException (String msg) {
        super(msg);
    }
}



