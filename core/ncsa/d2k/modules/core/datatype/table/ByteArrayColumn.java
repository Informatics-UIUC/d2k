package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;
import ncsa.d2k.util.*;

/**
	ByteArrayColumn is an implementation of TextualColumn which stores
	textual data in a byte form.  The internal representation is an array of
	byte arrays.
	<br><br>
	It it optimized for: retrieval of words by index, compact representation
	of words,  swapping of words, setting of words by index, reOrdering by index,
	comparing of words
	<br><br>
	It is inefficient for: removals, insertions, searching(on contents of word),
	@deprecated Use ContinuousByteArrayColumn.
 */
final public class ByteArrayColumn extends TextualColumn {

    /** the internal representation of this Column */
    private byte[][] internal = null;

    /**
    	Create a new, empty ByteArrayColumn.
     */
    public ByteArrayColumn () {
        this(0);
    }

    /**
    	Create a new ByteArrayColumn with the specified initial capacity.
		@param capacity the initial capacity
     */
    public ByteArrayColumn (int capacity) {
        internal = new byte[capacity][];
        //byte[] ty = new byte[0];
        //setType(ty);
    }

    /**
    	Create a new ByteArrayColumn with the specified data.
		@param newInternal the default data this column holds
     */
    public ByteArrayColumn (byte[][] newInternal) {
        this.setInternal(newInternal);
        //byte[] ty = new byte[0];
        //setType(ty);
    }

    /**
    	Return an exact copy of this ByteArrayColumn.  A deep copy is attempted,
		but if it fails a new column will be created, initialized with the same
		data as this column.
    	@return A new Column with a copy of the contents of this column.
     */
    public Column copy () {
        ByteArrayColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ByteArrayColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            bac = new ByteArrayColumn(getCapacity());
            for (int i = 0; i < getCapacity(); i++) {
                byte orig[] = getBytes(i);
                byte res[] = new byte[orig.length];
                for (int j = 0; j < orig.length; j++)
                    res[j] = orig[j];
                bac.setBytes(res, i);
            }
            bac.setLabel(getLabel());
            bac.setComment(getComment());
            //bac.setType(getType());
            return  bac;
        }
    }

    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS
    /**
    	Get a String from this Column at pos
    	@param pos the position from which to get a String
    	@return a String representation of the entry at that position
     */
    final public String getString (int pos) {
        return  new String(this.internal[pos]);
    }

    /**
    	Set the entry at pos to be newEntry.  newEntry.getBytes() is called
    	to store newEntry as a byte array.
    	@param newEntry the new item to put in the column
    	@param pos the position to put newEntry
     */
    final public void setString (String newEntry, int pos) {
        this.internal[pos] = newEntry.getBytes();
    }

    /**
    	Get the value of the bytes as an int using ByteUtils.toInt()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as an int
     */
    final public int getInt (int pos) {
        return  ByteUtils.toInt(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeInt()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setInt (int newEntry, int pos) {
        //internal[pos] = Integer.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeInt(newEntry);
    }

    /**
    	Get the value of the bytes as a short using ByteUtils.toShort().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a short
     */
    final public short getShort (int pos) {
        //return Short.valueOf(new String(internal[pos])).shortValue();
        return  ByteUtils.toShort(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeShort()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setShort (short newEntry, int pos) {
        //internal[pos] = Short.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeShort(newEntry);
    }

    /**
    	Get the value of the bytes as a long using ByteUtils.toLong().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a long
     */
    final public long getLong (int pos) {
        //return Long.valueOf(new String(internal[pos])).longValue();
        return  ByteUtils.toLong(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeLong()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setLong (long newEntry, int pos) {
        //internal[pos] = Long.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeLong(newEntry);
    }

    /**
    	Get the value of the bytes as a double using ByteUtils.toDouble().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a double
     */
    final public double getDouble (int pos) {
        return  ByteUtils.toDouble(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeDouble()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setDouble (double newEntry, int pos) {
        //internal[pos] = Double.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeDouble(newEntry);
    }

    /**
    	Get the value of the bytes as a float using ByteUtils.toFloat()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a float
     */
    final public float getFloat (int pos) {
        //return Float.valueOf(new String(internal[pos])).floatValue();
        return  ByteUtils.toFloat(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeFloat()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setFloat (float newEntry, int pos) {
        //internal[pos] = Float.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeFloat(newEntry);
    }

    /**
    	Get the value of the bytes at this position.
    	@param pos the position to get the data from
    	@return the byte array at pos
     */
    final public byte[] getBytes (int pos) {
        return  this.internal[pos];
    }

    /**
    	Put newEntry into this column at pos
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setBytes (byte[] newEntry, int pos) {
        this.internal[pos] = newEntry;
    }

    /**
    	Get the value of the bytes as a char[] using ByteUtils.toChars()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a char[]
     */
    final public char[] getChars (int pos) {
        return  ByteUtils.toChars(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeChars()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setChars (char[] newEntry, int pos) {
        //this.internal[pos] = new String(newEntry).getBytes();
        internal[pos] = ByteUtils.writeChars(newEntry);
    }

    /**
    	Return the entry at pos as an Object (byte[])
    	@param pos the position of interest
		@return the Object at the specified row
     */
    final public Object getObject (int pos) {
        return  this.internal[pos];
    }

    /**
    	Set the entry at pos to be newEntry.  If newEntry is a
    	byte[] or char[], call the appropriate method.  Otherwise,
    	convert the Object to a byte[] by calling ByteUtils.writeObject()
    	@param newEntry the new item
    	@param pos the position
     */
    final public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof byte[])
            setBytes((byte[])newEntry, pos);
        else if (newEntry instanceof char[])
            setChars((char[])newEntry, pos);
        else
            internal[pos] = ByteUtils.writeObject(newEntry);
    }

    /**
    	Return the entry at pos as a boolean using ByteUtils.toBoolean()
    	@param pos the position of interest
		@return the boolean at the specified row
     */
    final public boolean getBoolean (int pos) {
        //return new Boolean(new String(internal[pos])).booleanValue();
        return  ByteUtils.toBoolean(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeBoolean()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    final public void setBoolean (boolean newEntry, int pos) {
        //this.internal[pos] = new Boolean(newEntry).toString().getBytes();
        internal[pos] = ByteUtils.writeBoolean(newEntry);
    }

	/**
	 * Trim any excess storage from the internal buffer for this TextualColumn.
	 */
	final public void trim() {}

    //////////////////////////////////////
    //// Accessing Metadata
    /**
    	Return the count for the number of non-null entries.  This variable is
		recomputed each time...as keeping track of it could be very time
		inefficient.
    	@return this ByteArrayColumn's number of entries
     */
    final public int getNumEntries () {
        //return internal.length;
        int numEntries = 0;
        for (int i = 0; i < internal.length; i++)
            if (internal[i] != null)
                numEntries++;
        return  numEntries;
    }

	/**
	 * Get the number of rows that this column can hold.  Same as getCapacity
	 * @return the number of rows this column can hold
	 */
	final public int getNumRows() {
		return getCapacity();
	}

    /**
    	Get the capacity of this Column, its potential maximum number of entries
    	@return the max number of entries this Column can hold
     */
    final public int getCapacity () {
        return  internal.length;
    }

    /**
    	Suggests a new capacity for this Column.  If this implementation of Column
    	supports capacity then the suggestion may be followed. The capacity is its
    	potential max number of entries.  If numEntries > newCapacity then Column
    	may be truncated.  If internal.length > newCapacity then Column will be
    	truncated.
    	@param newCapacity a new capacity
     */
    final public void setCapacity (int newCapacity) {
        if (internal != null) {
            byte[][] newInternal = new byte[newCapacity][];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new byte[newCapacity][];
    }

    //////////////////////////////////////
    //////////////////////////////////////
    //// ACCESSING ByteArrayColumn ELEMENTS
    /**
    	Gets an entry from the Column at the indicated position.
    	For ByteArrayColumn, this is the same as calling getBytes( int )
    	@param pos the position
    	@return the entry at pos
     */
    final public Object getRow (int pos) {
        return  this.internal[pos];
    }

    /**
    	Gets a subset of this Column, given a start position and length.
    	Only the byteword references are copied, so if you change their contents,
    	they change, but if you reassign the reference, the Column is not affected.
    	@param pos the start position for the subset
    	@param len the length of the subset
    	@return a subset of this Column
     */
    final public Column getSubset (int pos, int len) {
        byte[][] subset = new byte[len][];
        System.arraycopy(internal, pos, subset, 0, len);
        ByteArrayColumn bac = new ByteArrayColumn(subset);
        bac.setLabel(getLabel());
        bac.setComment(getComment());
        //bac.setType(getType());
        return  bac;
    }

    /**
    	Gets a reference to the internal representation of this Column.
    	Changes made to this object will be reflected in the Column.
    	@return the internal representation of this Column.
     */
    final public Object getInternal () {
        return  this.internal;
    }

    /**
    	Sets the reference to the internal representation of this Column.
    	If a miscompatable Object is passed in, the most common Exception
    	thrown is a ClassCastException.
    	@param newInternal a new internal representation for this Column
     */
    final public void setInternal (Object newInternal) {
        this.internal = (byte[][])newInternal;
    }

    /**
    	Sets the entry at the given position to newEntry
    	@param newEntry a new entry
    	@param pos the position to set
     */
    final public void setRow (Object newEntry, int pos) {
        this.internal[pos] = (byte[])newEntry;
    }

    /**
    	Appends the new entry to the end of the Column.
    	@param newEntry a new entry
     */
    final public void addRow (Object newEntry) {
        /*int last = 0;
         for(int i=internal.length-1;i>=0;i--)
         if( internal[i] != null )
         last = i;
         this.internal[last+1] = (byte[])newEntry;
         */
        int last = internal.length;
        byte[][] newInternal = new byte[internal.length + 1][];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = (byte[])newEntry;
        internal = newInternal;
    }

    /**
    	Removes an entry from the Column, at pos.
    	All entries from pos+1 will be moved back 1 position
    	@param pos the position to remove
    	@return the removed object
     */
    final public Object removeRow (int pos) {
        byte[] removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        byte newInternal[][] = new byte[internal.length - 1][];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return  removed;
    }

    /**
    	Inserts a new entry in the Column at position pos.
    	All elements from pos to capacity will be moved up one.
    	@param newEntry the newEntry to insert
    	@param pos the position to insert at
     */
    final public void insertRow (Object newEntry, int pos) {
        byte[][] newInternal = new byte[internal.length + 1][];
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
        newInternal[pos] = (byte[])newEntry;
        internal = newInternal;
    }

    /**
    	Swaps two entries in the Column
    	@param pos1 the position of the 1st entry to swap
    	@param pos2 the position of the 2nd entry to swap
     */
    final public void swapRows (int pos1, int pos2) {
        byte[] e1 = internal[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = e1;
    }

    /**
    	Return a copy of this Column, re-ordered based on the input array of indexes.
    	Does not overwrite this Column.
    	@param newOrder an array of indices indicating a new order
		@return a copy of this column, re-ordered
     */
    final public Column reOrderRows (int[] newOrder) {
        byte[][] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new byte[internal.length][];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        ByteArrayColumn bac = new ByteArrayColumn(newInternal);
        bac.setLabel(getLabel());
        //bac.setType(getType());
        bac.setComment(getComment());
        return  bac;
    }

    /**
    	Compare the values of the element passed in and pos. Return 0 if they
    	are the same, greater than 0 if element is greater, and less than 0 if
		element is less.
    	@param element the element to be passed in and compared
    	@param pos the position of the element in Column to be compare with
    	@return a value representing the relationship- >, <, or == 0
     */
    final public int compareRows (Object element, int pos) {
        byte[] b = internal[pos];
        return  compareBytes((byte[])element, b);
    }

    /**
    	Compare pos1 and pos2 positions in the Column. Return 0 if they
    	are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
    	@param pos1 the position of the first element to compare
    	@param pos2 the position of the second element to compare
    	@return a value representing the relationship- >, <, or == 0
     */
    final public int compareRows (int pos1, int pos2) {
        byte[] b1 = internal[pos1];
        byte[] b2 = internal[pos2];
        return  compareBytes(b1, b2);
    }

    /**
	 * Compare two byte arrays
     * @param b1 the first byte array to compare
     * @param b2 the second byte array to compare
     * @return -1, 0, 1
     */
    final private int compareBytes (byte[] b1, byte[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }

    /**
    	Given an array of booleans, will remove the positions in the Column
    	which coorespond to the positions in the boolean array which are
    	marked true.  If the boolean array and Column do not have the same
    	number of elements, the remaining elements will be discarded.
    	@param flags the boolean array of remove flags
     */
    final public void removeByFlag (boolean[] flags) {
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
    final public void removeByIndex (int[] indices) {
        HashMap toRemove = new HashMap(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.put(id, id);
        }
        byte newInternal[][] = new byte[internal.length - indices.length][];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            Integer x = (Integer)toRemove.get(new Integer(i));
            // if this row is not in the list, copy it into the new internal
            if (x == null) {
                newInternal[newIntIdx] = internal[i];
                newIntIdx++;
            }
            //else
            //   internal[i] = null;
        }
        internal = newInternal;
    }

    /**
    	Sort the items in this column.
    	@exception NotSupportedException when sorting is not supported
     */
    final public void sort () throws NotSupportedException {
        sort(null);
    }

    /**
    	Sort the elements in this column, and swap the rows in the table
    	we are a part of.
    	@param t the Table to swap rows for
    	@exception NotSupportedException when sorting is not supported
     */
    final public void sort (Table t) throws NotSupportedException {
        internal = doSort(internal, 0, internal.length - 1, t);
    }

    /**
    	Implement the quicksort algorithm.  Partition the array and
    	recursively call doSort.
    	@param A the array to sort
    	@param p the beginning index
    	@param r the ending index
    	@param t the Table to swap rows for
		@return a sorted array of byte arrays
     */
    final private byte[][] doSort (byte[][] A, int p, int r, Table t) {
        if (p < r) {
            int q = partition(A, p, r, t);
            doSort(A, p, q, t);
            doSort(A, q + 1, r, t);
        }
        return  A;
    }

    /**
    	Rearrange the subarray A[p..r] in place.
    	@param A the array to rearrange
    	@param p the beginning index
    	@param r the ending index
    	@param t the Table to swap rows for
		@return the new partition point
     */
    final private int partition (byte[][] A, int p, int r, Table t) {
        //String x = A[p];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            do {
                j--;
            } while (compareRows(A[j], p) > 0);
            do {
                i++;
            } while (compareRows(A[i], p) < 0);
            if (i < j) {
                if (t == null) {
                    byte[] temp = A[i];
                    A[i] = A[j];
                    A[j] = temp;
                }
                else
                    t.swapRows(i, j);
            }
            else
                return  j;
        }
    }
}
/*ByteArrayColumn*/
