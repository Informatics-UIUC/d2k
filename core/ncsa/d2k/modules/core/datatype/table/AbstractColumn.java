package ncsa.d2k.modules.core.datatype.table;

/**
 * AbstractColumn implements several metadata methods that are common
 * to all Columns.
 */
abstract public class AbstractColumn implements Column {
	private String label;
	private String comment;
	private Object type;

	/**
		Get the label associated with this Column.
		@return the label which describes this Column
	*/
	final public String getLabel( ) {
		return label;
	}

	/**
		Set the label associated with this Column.
		@param labl the label which describes this Column
	*/
	final public void setLabel( String labl ) {
		label = labl;
	}

	/**
		Get the comment associated with this Column.
		@return the comment which describes this Column
	*/
	final public String getComment( ) {
		return comment;
	}

	/**
		Set the comment associated with this Column.
		@param cmt the comment which describes this Column
	*/
	final public void setComment( String cmt ) {
		comment = cmt;
	}

	/**
		Get the type associated with this Column.
		@return the type of data this Column holds
	*/
	final public Object getType( ) {
		return type;
	}

	/**
		Set the type associated with this Column.
		@param tp the type of data this Column holds
	*/
	final public void setType( Object tp ) {
		type = tp;
	}
}
