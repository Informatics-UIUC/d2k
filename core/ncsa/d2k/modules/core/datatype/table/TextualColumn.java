//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

/**
 TextualColumn is an extension of Column which is implemented by all
 classes which are optimized to represent and access textual data.
 <br>
 */
public abstract class TextualColumn extends AbstractColumn {

	/**
	 * Trim any excess storage from the internal buffer for this TextualColumn.
	 */
	abstract public void trim();
}

/*TextualColumn*/

