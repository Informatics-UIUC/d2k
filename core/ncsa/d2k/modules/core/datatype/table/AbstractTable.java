package ncsa.d2k.modules.core.datatype.table;

/**
 * AbstractTable implements several meta methods common to all Tables.
 */
public abstract class AbstractTable implements Table {
	private String label;
	private String comment;
	private int keyColumn;

	/**
		Get the label associated with this Table.
		@return the label which describes this Table
	*/
	public String getLabel( ) {
		return label;
	}

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	public void setLabel( String labl ) {
		label = labl;
	}

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	public String getComment( ) {
		return comment;
	}

	/**
		Set the comment associated with this Table.
		@param cmt the comment which describes this Table
	*/
	public void setComment( String cmt ) {
		comment = cmt;
	}

	public void setKeyColumn(int idx) {
		keyColumn = idx;
	}

	public int getKeyColumn() {
		return keyColumn;
	}
}
