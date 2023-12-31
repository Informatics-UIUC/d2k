package ncsa.d2k.modules.core.datatype.table;
import ncsa.d2k.modules.core.datatype.table.Column;


/**
 TextualColumn is an extension of Column which is implemented by all
 classes which are optimized to represent and access textual data.
 <br>
 */
public interface TextualColumn extends Column {
	static final long serialVersionUID = 1L;

	/**
	 * Trim any excess storage from the internal buffer for this TextualColumn.
	 */
	//public void trim();
}
/*TextualColumn*/
