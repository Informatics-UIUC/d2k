package ncsa.d2k.modules.core.datatype.table;

/**
 * AbstractTable implements several meta methods common to all Tables.
 */
public abstract class AbstractTable extends DefaultMissingValuesTable {

 //static final long serialVersionUID = 7799682534587814277L;
	static final long serialVersionUID = 2147035826260285698L;

	/** this is the label for the column. */
	protected String label;
	
	/** this is the table comment. */
	protected String comment;
	
	
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
}
