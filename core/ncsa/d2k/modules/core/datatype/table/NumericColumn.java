//package ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

/**
	NumericColumn is an extension of Column which is implemented by all
	classes which are optimized to represent and access numeric data.
	<br>
*/
public abstract class NumericColumn extends AbstractColumn {

	static final long serialVersionUID = 1970428654845248318L;

    //////////////////////////////////////
    //// Accessing Metadata
    /**
       Get the minimum value contained in this list
       @return the minimum value of this list
    */
    abstract public double getMin();

    /**
       Get the maximum value contained in this list
       @return the maximum value of this list
    */
    abstract public double getMax();

    /**
       Sets the value which indicates an empty entry.
       @param emptyVal the value to which an empty entry is set
    */
    abstract public void setEmptyValue( Number emptyVal );

    /**
       Gets the value which indicates an empty entry.
       @return the value of an empty entry as a subclass of Number
    */
    abstract public Number getEmptyValue( );
    //////////////////////////////////////
}/*NumericColumn*/
