package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;

/**
 * AbstractColumn implements several methods that are common
 * to all Columns.
 */
abstract public class AbstractColumn implements Column {
	private String label;
	private String comment;
	private boolean isNominal;
	private boolean isScalar;
	protected int type;

	/**
		Get the label associated with this Column.
		@return the label which describes this Column
	*/
	public String getLabel( ) {
		return label;
	}

	/**
		Set the label associated with this Column.
		@param labl the label which describes this Column
	*/
	public void setLabel( String labl ) {
		label = labl;
	}

	/**
		Get the comment associated with this Column.
		@return the comment which describes this Column
	*/
	public String getComment( ) {
		return comment;
	}

	/**
		Set the comment associated with this Column.
		@param cmt the comment which describes this Column
	*/
	public void setComment( String cmt ) {
		comment = cmt;
	}

	public void setIsNominal(boolean value) {
		isNominal = value;
		isScalar = !value;
	}
	public void setIsScalar(boolean value) {
		isScalar = value;
		isNominal = !value;
	}
	public boolean getIsNominal() {
		return isNominal;
	}
	public boolean getIsScalar() {
		return isScalar;
	}

	public int getType() {
		return type;
	}

	public void removeRows(int start, int length) {
		int [] toRemove = new int[length];
		int idx = start;
		for(int i = 0; i < length; i++) {
			toRemove[i] = idx;
			idx++;
		}
		removeRowsByIndex(toRemove);
	}

    /**
    	Given an array of booleans, will remove the positions in the Column
    	which coorespond to the positions in the boolean array which are
    	marked true.  If the boolean array and Column do not have the same
    	number of elements, the remaining elements will be discarded.
    	@param flags the boolean array of remove flags
     */
    public void removeRowsByFlag (boolean[] flags) {
        // keep a list of the row indices to remove
        LinkedList ll = new LinkedList();
        int i = 0;
        for (; i < flags.length; i++) {
            if (flags[i])
                ll.add(new Integer(i));
        }
        for (; i < getNumRows(); i++) {
            ll.add(new Integer(i));
        }
        int[] toRemove = new int[ll.size()];
        int j = 0;
        Iterator iter = ll.iterator();
        while (iter.hasNext()) {
            Integer in = (Integer)iter.next();
            toRemove[j] = in.intValue();
            j++;
        }
        // now call remove by index to remove the rows
        removeRowsByIndex(toRemove);
    }
}
