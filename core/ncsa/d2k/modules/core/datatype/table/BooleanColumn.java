package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;
import ncsa.d2k.util.*;

/**
	BooleanColumn is an implementation of Column which holds boolean values.
	The values are kept in an array of booleans.
 */
final public class BooleanColumn extends AbstractColumn {

    /** holds BooleanColumn's internal data rep */
    private boolean[] internal = null;

    /**
    	Create a new, empty BooleanColumn.
     */
    public BooleanColumn () {
        this(0);
    }

    /**
    	Create a new BooleanColumn with the specified capacity.
    	@param capacity the initial capacity
     */
    public BooleanColumn (int capacity) {
        internal = new boolean[capacity];
        //setType(new Boolean(false));
    }

    /**
    	Create a new BooleanColumn with the specified values.
    	@param vals the initial values
     */
    public BooleanColumn (boolean[] vals) {
        this.setInternal(vals);
        //setType(new Boolean(false));
    }

    /**
    	Return an exact copy of this Column.  A deep copy is attempted,
		but if it fails a new BooleanColumn will be created, initialized with
		the same data as this BooleanColumn.
    	@return a new BooleanColumn with a copy of the contents of this column.
     */
    public Column copy () {
        BooleanColumn newCol;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            newCol = (BooleanColumn)ois.readObject();
            ois.close();
            return  newCol;
        } catch (Exception e) {
            newCol = new BooleanColumn(this.getCapacity());
            for (int i = 0; i < newCol.getCapacity(); i++)
                newCol.setBoolean(internal[i], i);
            newCol.setLabel(getLabel());
            newCol.setComment(getComment());
            //newCol.setType(getType());
            return  newCol;
        }
    }

    //////////////////////////////////////
    //// Accessing Metadata

	/**
	  	Get the number of rows in this Column.  Same as getCapacity().
		@return the number of rows in this Column
	*/
    public int getNumRows () {
        return  internal.length;
    }

	/**
		Get the number of entries this Column holds.  This is the number of
		non-null entries in the Column.
		@return this Column's number of entries
	*/
	public int getNumEntries() {
		return internal.length;
	}

    /**
    	Get the capacity of this Column, its potential maximum number of entries.
    	@return the maximum number of entries this Column can hold
     */
    public int getCapacity () {
        return  this.internal.length;
    }

    /**
    	Set a new capacity for this BooleanColumn.  The capacity is its
		potential maximum number of entries.  If numEntries is greater than
		newCapacity, the Column will be truncated.
     	@param newCapacity the new capacity
     */
    public void setCapacity (int newCapacity) {
        if (internal != null) {
            boolean[] newInternal = new boolean[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new boolean[newCapacity];
    }

    /**
    	Get the entry at pos as a String.
    	@param pos the position from which to get a String
    	@return a String representation of the entry at that position
     */
    public String getString (int pos) {
        return  new Boolean(internal[pos]).toString();
    }

    /**
   		Set the entry at pos to be newEntry.  Set to true only if newEntry is
     	equal to "true".  Otherwise set to false.
     	@param newEntry the new entry
     	@param pos the position in the column
     */
    public void setString (String newEntry, int pos) {
        internal[pos] = new Boolean(newEntry).booleanValue();
    }

    /**
     	Returns 1 if the value at pos is true, 0 otherwise.
     	@param pos the position in the column
     	@return 1 if the value at pos is true, 0 otherwise
     */
    public int getInt (int pos) {
        if (internal[pos])
            return  1;
        return  0;
    }

    /**
     	Set the entry at pos to true if newEntry is greater than 0.
     	Set to false otherwise.
     	@param newEntry the newEntry
     	@param pos the position in the column
     */
    public void setInt (int newEntry, int pos) {
        if (newEntry > 0)
            internal[pos] = true;
        else
            internal[pos] = false;
    }

    /**
     	Returns 1 if the value at pos is true, 0 otherwise.
     	@param pos the position in the column
     	@return 1 if the value at pos is true, 0 otherwise
     */
    public short getShort (int pos) {
        if (internal[pos])
            return  (short)1;
        return  (short)0;
    }

    /**
     	Set the entry at pos to true if newEntry is greater than 0.
     	Set to false otherwise.
     	@param newEntry the newEntry
     	@param pos the position in the column
     */
    public void setShort (short newEntry, int pos) {
        if (newEntry > 0)
            internal[pos] = true;
        else
            internal[pos] = false;
    }

    /**
     	Returns 1 if the value at pos is true, 0 otherwise.
     	@param pos the position in the column
     	@return 1 if the value at pos is true, 0 otherwise
     */
    public long getLong (int pos) {
        if (internal[pos])
            return  (long)1;
        return  (long)0;
    }

    /**
     	Set the entry at pos to true if newEntry is greater than 0.
     	Set to false otherwise.
     	@param newEntry the newEntry
     	@param pos the position in the column
     */
    public void setLong (long newEntry, int pos) {
        if (newEntry > 0)
            internal[pos] = true;
        else
            internal[pos] = false;
    }

    /**
     	Returns 1 if the value at pos is true, 0 otherwise.
     	@param pos the position in the column
     	@return 1 if the value at pos is true, 0 otherwise
     */
    public double getDouble (int pos) {
        if (internal[pos])
            return  (double)1;
        return  (double)0;
    }

    /**
     	Set the entry at pos to true if newEntry is greater than 0.
     	Set to false otherwise.
     	@param newEntry the newEntry
     	@param pos the position in the column
     */
    public void setDouble (double newEntry, int pos) {
        if (newEntry > 0)
            internal[pos] = true;
        else
            internal[pos] = false;
    }

    /**
     	Returns 1 if the value at pos is true, 0 otherwise.
     	@param pos the position in the column
     	@return 1 if the value at pos is true, 0 otherwise
     */
    public float getFloat (int pos) {
        if (internal[pos])
            return  (float)1;
        return  (float)0;
    }

    /**
     	Set the entry at pos to true if newEntry is greater than 0.
     	Set to false otherwise.
     	@param newEntry the newEntry
     	@param pos the position in the column
     */
    public void setFloat (float newEntry, int pos) {
        if (newEntry > 0)
            internal[pos] = true;
        else
            internal[pos] = false;
    }

    /**
     	Returns the byte representation of the entry at pos using
     	ByteUtils.writeBoolean().
     	@param pos the position of interest
     	@return the entry at pos as an array of bytes
     */
    public byte[] getBytes (int pos) {
        return  ByteUtils.writeBoolean(internal[pos]);
    }

    /**
    	Converts newEntry to a boolean using ByteUtils.toBoolean() and
    	sets the value at pos to this boolean.
    	@param newEntry the new item
    	@param pos the position
     */
    public void setBytes (byte[] newEntry, int pos) {
        internal[pos] = ByteUtils.toBoolean(newEntry);
    }

    /**
    	Converts the value at pos to a String and returns the String as a char
		array.
    	@param pos the position
    	@return the String representation of the item at pos as an array of chars
     */
    public char[] getChars (int pos) {
        return  getString(pos).toCharArray();
    }

    /**
    	Set the entry at pos to be newEntry.  Set to true only if newEntry is
    	equal to "true".  Otherwise set to false.
    	@param newEntry the new entry
    	@param pos the position in the column
     */
    public void setChars (char[] newEntry, int pos) {
        setString(new String(newEntry), pos);
    }

    /**
    	Return the boolean value at this position.
    	@param pos the position
    	@return the value at pos
     */
    public boolean getBoolean (int pos) {
        return  this.internal[pos];
    }

    /**
    	Set the value at this position.
    	@param newEntry the new entry
    	@param pos the position
     */
    public void setBoolean (boolean newEntry, int pos) {
        this.internal[pos] = newEntry;
    }

    /**
    	Return the value at pos as a Boolean.
    	@param pos the position
    	@return a Boolean object with the value contained at pos
     */
    public Object getObject (int pos) {
        return  new Boolean(internal[pos]);
    }

    /**
    	Sets the value at pos to be newEntry.  If newEntry is a Boolean,
    	then it sets the value to be the same as newEntry.  If newEntry
    	is not a boolean, the setString() method is called with newEntry.
    	@param newEntry the newEntry
    	@param pos the position
     */
    public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof Boolean)
            this.internal[pos] = ((Boolean)newEntry).booleanValue();
        else
            setString(newEntry.toString(), pos);
    }

    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS
    /**
    	Gets a reference to the internal representation of this Column.
    	(boolean[]).  Changes made to this object will be reflected in the Column.
    	@return the internal representation of this Column.
     */
    public Object getInternal () {
        return  this.internal;
    }

    /**
    	Gets a subset of this Column, given a start position and length.
    	The primitive values are copied, so they have no destructive abilities
    	as far as the Column is concerned.
    	@param pos the start position for the subset
    	@param len the length of the subset
    	@return a subset of this Column
     */
    public Column getSubset (int pos, int len) {
        if ((pos + len) > internal.length)
            throw  new ArrayIndexOutOfBoundsException();
        boolean[] subset = new boolean[len];
        System.arraycopy(internal, pos, subset, 0, len);
        BooleanColumn bc = new BooleanColumn(subset);
        bc.setLabel(getLabel());
        bc.setComment(getComment());
        //bc.setType(getType());
        return  bc;
    }

    //////////////////////////////////////
    /**
    	Sets the reference to the internal representation of this Column.
    	@param newInternal a new internal representation for this Column
     */
    public void setInternal (Object newInternal) {
        if (newInternal instanceof boolean[])
            this.internal = (boolean[])newInternal;
    }

    /**
    	Gets a Boolean representation of the entry at the indicated position.
    	@param pos the position
    	@return the entry at pos
     */
    public Object getRow (int pos) {
        return  new Boolean(internal[pos]);
    }

    /**
    	Sets the entry at the given position to newEntry.
    	The newEntry should be a Boolean.
    	@param newEntry a new entry, a Boolean
    	@param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        internal[pos] = ((Boolean)newEntry).booleanValue();
    }

    /**
    	Appends the new entry to the end of the Column.
    	@param newEntry a new entry
     */
    public void addRow (Object newEntry) {
        int last = internal.length;
        boolean[] newInternal = new boolean[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = ((Boolean)newEntry).booleanValue();
        internal = newInternal;
    }

    /**
    	Removes an entry from the Column, at pos.  All entries from pos+1 will
		be moved back 1 position.
    	@param pos the position to remove
    	@return a Boolean representation of the removed boolean
     */
    public Object removeRow (int pos) {
        boolean removed = internal[pos];
        // copy all the items after the item to be removed one position up
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        // copy the items into a new array
        boolean newInternal[] = new boolean[internal.length - 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return  new Boolean(removed);
    }

    /**
    	Inserts a new entry in the Column at position pos.  All elements from pos to
    	capacity will be moved up one.  If pos is greater than the capacity of
    	this Column, the new entry will be appended to the end of the Column
    	by calling addRow.
    	@param newEntry a Boolean wrapped boolean as the newEntry to insert
    	@param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        boolean[] newInternal = new boolean[internal.length + 1];
        if (pos > getCapacity()) {
            addRow(newEntry);
            return;
        }
        if (pos == 0)
            System.arraycopy(internal, 0, newInternal, 1, getCapacity());        /*else if(pos == 1) {
         newInternal[0] = internal[0];
         System.arraycopy(internal, 1, newInternal, 2, getCapacity()-2);
         }*/
        else {
            System.arraycopy(internal, 0, newInternal, 0, pos);
            System.arraycopy(internal, pos, newInternal, pos + 1, internal.length
                    - pos);
        }
        newInternal[pos] = ((Boolean)newEntry).booleanValue();
        internal = newInternal;
    }

    /**
    	Swaps two entries in the column.
    	@param pos1 the position of the 1st entry to swap
    	@param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
        boolean d1 = internal[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = d1;
    }

    /**
    	Get a copy of this Column, reordered, based on the input array of indices.
    	Does not overwrite this Column.
    	@param newOrder an array of indices indicating a new order
		@return a copy of this column, re-ordered
     */
    public Column reOrderRows (int[] newOrder) {
        boolean[] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new boolean[internal.length];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        BooleanColumn bc = new BooleanColumn(newInternal);
        bc.setLabel(getLabel());
        //bc.setType(getType());
        bc.setComment(getComment());
        return  bc;
    }

    /**
    	Given an array of booleans, will remove the positions in the Column
    	which coorespond to the positions in the boolean array which are
    	marked true.  If the boolean array and Column do not have the same
    	number of rows, the remaining elements will be discarded.
    	@param flags the boolean array of remove flags
     */
    public void removeByFlag (boolean[] flags) {
        // keep a list of the row indices to remove
        LinkedList ll = new LinkedList();
        int i = 0;
        for (; i < flags.length; i++) {
            if (flags[i])
                ll.add(new Integer(i));
        }
        for (; i < internal.length; i++) {
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
        removeByIndex(toRemove);
    }

    /**
    	Given an array of ints, will remove the positions in the Column
    	which are indicated by the ints in the array.
    	@param indices the int array of remove indices
     */
    public void removeByIndex (int[] indices) {
        HashMap toRemove = new HashMap(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.put(id, id);
        }
        boolean newInternal[] = new boolean[internal.length - indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            Integer x = (Integer)toRemove.get(new Integer(i));
            // if this row is not in the list, copy it into the new internal
            if (x == null) {
                newInternal[newIntIdx] = internal[i];
                newIntIdx++;
            }
        }
        internal = newInternal;
    }

    //////////////////////////////////////
    /**
    	Sort the items in this column.  Not supported for BooleanColumn.
    	@exception NotSupportedException when sorting is not supported
     */
    public void sort () throws NotSupportedException {
        throw  new NotSupportedException();
    }

    /**
    	Sort the items in this column.  Not supported for BooleanColumn.
    	@param t the Table to swap rows for
    	@exception NotSupportedException when sorting is not supported
     */
    public void sort (Table t) throws NotSupportedException {
        throw  new NotSupportedException();
    }

    /**
    	Compare the values of the object passed in and pos. Return 0 if they
    	are the same, greater than 0 if element is different.
    	@param element the object to be passed in should be a subclass of Boolean
    	@param row the position of the element in Column to be compared with
    	@return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int row) {
        boolean b = ((Boolean)element).booleanValue();
        if (b == getBoolean(row))
            return  0;
        else
            return  1;
    }

    /**
    	Compare the values of the at r1 and r2. Return 0 if they
    	are the same, greater than 0 if element is different.
    	@param r1 the first row to compare
    	@param r2 the second row to compare
    	@return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int r1, int r2) {
        boolean b = getBoolean(r1);
        if (b == getBoolean(r2))
            return  0;
        else
            return  1;
    }
}
/*BooleanColumn*/