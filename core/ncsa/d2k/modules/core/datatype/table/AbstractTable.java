package ncsa.d2k.modules.core.datatype.table;

/**
 * AbstractTable implements several metadata methods common to all Tables.
 */
public abstract class AbstractTable implements Table {
	private String label;
	private String comment;
	//private Object type;

	/**
		Get the label associated with this Table.
		@return the label which describes this Table
	*/
	final public String getLabel( ) {
		return label;
	}

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	final public void setLabel( String labl ) {
		label = labl;
	}

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	final public String getComment( ) {
		return comment;
	}

	/**
		Set the comment associated with this Table.
		@param cmt the comment which describes this Table
	*/
	final public void setComment( String cmt ) {
		comment = cmt;
	}

	/**
		Get the type associated with this Table.
		@return the type of data this Table holds
	*/
	/*final public Object getType( ) {
		return type;
	}*/

	/**
		Set the type associated with this Table.
		@param tp the type of data this Table holds
	*/
	/*final public void setType( Object tp ) {
		type = tp;
	}*/
}