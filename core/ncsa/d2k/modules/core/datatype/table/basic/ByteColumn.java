package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;
//import ncsa.d2k.util.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;

/**
 * <code>ByteColumn</code> is an implementation of <code>TextualColumn</code>
 * which stores textual data in a byte form. The internal representation is a
 * <code>byte</code> array.
 * <br><br>
 * It it optimized for: retrieval of words by index, compact representation
 * of words, swapping of words, setting of words by index, reordering by index,
 * comparing of words
 * <br><br>
 * It is inefficient for: removals, insertions, searching (on contents of word)
 */
final public class ByteColumn extends AbstractColumn implements TextualColumn {

	static final long serialVersionUID = 8471652021364392992L;

    /** the internal representation of this Column */
    private byte[] internal = null;

    /**
     * Creates a new, empty <code>ByteColumn</code>.
     */
    public ByteColumn () {
        this(0);
    }

    /**
     * Creates a new <code>ByteColumn</code> with the specified initial
     * capacity.
     *
     * @param capacity       the initial capacity
     */
    public ByteColumn (int capacity) {
        internal = new byte[capacity];
        //byte[] ty = new byte[0];
        //setType(ty);
        setIsNominal(true);
        type = ColumnTypes.BYTE;
    }

    /**
     * Creates a new <code>ByteColumn</code> with the specified data.
     *
     * @param newInternal    the default data this column holds
     */
    public ByteColumn (byte[] newInternal) {
        this.setInternal(newInternal);
        //byte[] ty = new byte[0];
        //setType(ty);
        setIsNominal(true);
        type = ColumnTypes.BYTE;
    }

    /**
     * Returns an exact copy of this <code>ByteColumn</code>. A deep copy is
     * attempted, but if it fails, a new column will be created, initialized
     * with the same data as this column.
     *
     * @return               a new <code>Column</code> with a copy of the
     *                       contents of this column.
     */
    public Column copy () {
        ByteColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ByteColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            bac = new ByteColumn(getCapacity());
            for (int i = 0; i < getCapacity(); i++)
                bac.setByte(getByte(i), i);
            bac.setLabel(getLabel());
            bac.setComment(getComment());
            //bac.setType(getType());
            return  bac;
        }
    }

    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS

    /**
     * Gets the entry at <code>pos</code> as a <code>String</code>.
     *
     * @param pos            the position from which to get a
     *                       <code>String</code>
     * @return               a <code>String</code> representation of the entry
     *                       at that position
     */
    public String getString (int pos) {
        return new Byte(getByte(pos)).toString();
    }

    /**
     * Set the entry at <code>pos</code> to be <code>newEntry</code>.
     * <code>Byte.byteValue()</code> is called to store <code>newEntry</code>
     * as a <code>byte</code>.
     *
     * @param newEntry       the new item to put in the column
     * @param pos            the position in which to put <code>newEntry</code>
     */
    public void setString (String newEntry, int pos) {
        setByte(Byte.valueOf(newEntry).byteValue(), pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to an <code>int</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as an <code>int</code>
     */
    public int getInt (int pos) {
        return (int)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setInt (int newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>short</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>short</code>
     */
    public short getShort (int pos) {
        return (short)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setShort (short newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>long</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>long</code>
     */
    public long getLong (int pos) {
        return (long)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setLong (long newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>double</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>double</code>
     */
    public double getDouble (int pos) {
        return (double)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setDouble (double newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>float</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>float</code>
     */
    public float getFloat (int pos) {
        return (float)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setFloat (float newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets a <code>byte</code> array, the first element of which is the
     * <code>byte</code> at position <code>pos</code>.
     *
     * @param pos            the position to get the data from
     * @return               the appropriate <code>byte</code> array
     */
    public byte[] getBytes (int pos) {
        byte[] retVal = new byte[1];
        retVal[0] = getByte(pos);
        return retVal;
    }

    /**
     * Sets the <code>byte</code> at position <code>pos</code> to be the
     * first element of <code>newEntry</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position at which to place the first element
     *                       of <code>newEntry</code>
     */
    public void setBytes (byte[] newEntry, int pos) {
        setByte(newEntry[0], pos);
    }

    /**
     * Gets the <code>byte</code> at this position.
     *
     * @param pos            the position to get the data from
     * @return               the <code>byte</code> at position <code>pos</code>
     */
    public byte getByte(int pos) {
        return internal[pos];
    }

    /**
     * Sets the <code>byte</code> at this position to be <code>newEntry</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setByte (byte newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     * Gets a <code>char</code> array representing this <code>byte</code>
     * as text.
     *
     * @param pos            the position to get the data from
     * @return               the corresponding <code>char</code> array
     */
    public char[] getChars (int pos) {
        return Byte.toString(getByte(pos)).toCharArray();
    }

    /**
     * Attempts to parse <code>newEntry</code> as a textual representation
     * of a <code>byte</code> and store that value at <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setChars (char[] newEntry, int pos) {
        setString(String.copyValueOf(newEntry), pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>char</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>char</code>
     */
    public char getChar (int pos) {
        return (char)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setChar (char newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Returns a new <code>Byte</code> containing the value of the
     * <code>byte</code> at <code>pos</code>.
     *
     * @param pos            the position of interest
     * @return               a corresponding <code>Byte</code>.
     */
    public Object getObject (int pos) {
        return new Byte(getByte(pos));
    }

    /**
     * Attempts to set the entry at <code>pos</code> to correspond to
     * <code>newEntry</code>. If <code>newEntry</code> is a <code>byte[]</code>,
     * <code>char[]</code>, or <code>Byte</code>, the appropriate method is
     * called. Otherwise, <code>setString</code> is called.
     *
     * @param newEntry       the new item
     * @param pos            the position
     */
    public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof byte[])
            setBytes((byte[])newEntry, pos);
        else if (newEntry instanceof char[])
            setChars((char[])newEntry, pos);
        else if (newEntry instanceof Byte)
            setByte(((Byte)newEntry).byteValue(), pos);
        else
            setString(newEntry.toString(), pos);
    }

    /**
     * Returns <code>false</code> if the <code>byte</code> at <code>pos</code>
     * is equal to 0. Otherwise, returns 1.
     *
     * @param pos            the position of interest
     * @return               the corresponding <code>boolean</code>.
     */
    public boolean getBoolean (int pos) {
        if (getByte(pos) == 0)
            return false;
        else
            return true;
    }

    /**
     * If <code>newEntry</code> is <code>false</code>, stores 0 at position
     * <code>pos</code>. Otherwise, stores 1.
     *
     * @param newEntry       the new item
     * @param pos            the position to place newEntry
     */
    public void setBoolean (boolean newEntry, int pos) {
        if (newEntry)
            setByte((byte)1, pos);
        else
            setByte((byte)0, pos);
    }

    /**
     * Trim any excess storage from the internal buffer for this
     * <code>TextualColumn</code>. This has no effect on a
     * <code>ByteColumn</code>.
     */
    public void trim() {}

    //////////////////////////////////////
    //// Accessing Metadata

    /**
        Return the count for the number of non-null entries.  This variable is
        recomputed each time...as keeping track of it could be very time
        inefficient.
        @return this ByteArrayColumn's number of entries
     */
    public int getNumEntries () {
        return internal.length;
    }

    /**
     * Get the number of rows that this column can hold.  Same as getCapacity
     * @return the number of rows this column can hold
     */
    public int getNumRows() {
        return getCapacity();
    }

    /**
        Get the capacity of this Column, its potential maximum number of entries
        @return the max number of entries this Column can hold
     */
    public int getCapacity () {
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
    public void setNumRows (int newCapacity) {
        if (internal != null) {
            byte[] newInternal = new byte[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new byte[newCapacity];
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
    public Object getRow (int pos) {
        return  new Byte(internal[pos]);
    }

    /**
        Gets a subset of this Column, given a start position and length.
        Only the byteword references are copied, so if you change their contents,
        they change, but if you reassign the reference, the Column is not affected.
        @param pos the start position for the subset
        @param len the length of the subset
        @return a subset of this Column
     */
    public Column getSubset (int pos, int len) {
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
    public Object getInternal () {
        return  this.internal;
    }

    /**
        Sets the reference to the internal representation of this Column.
        If a miscompatable Object is passed in, the most common Exception
        thrown is a ClassCastException.
        @param newInternal a new internal representation for this Column
     */
    public void setInternal (Object newInternal) {
        this.internal = (byte[])newInternal;
    }

    /**
        Sets the entry at the given position to newEntry
        @param newEntry a new entry
        @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        setObject(newEntry, pos);
    }

    /**
        Appends the new entry to the end of the Column.
        @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
        /*int last = 0;
         for(int i=internal.length-1;i>=0;i--)
         if( internal[i] != null )
         last = i;
         this.internal[last+1] = (byte[])newEntry;
         */
        int last = internal.length;
        byte[] newInternal = new byte[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = ((Byte)newEntry).byteValue();
        internal = newInternal;
    }

    /**
        Removes an entry from the Column, at pos.
        All entries from pos+1 will be moved back 1 position
        @param pos the position to remove
        @return the removed object
     */
    public Object removeRow (int pos) {
        byte removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        byte newInternal[] = new byte[internal.length - 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return new Byte(removed);
    }

    /**
        Inserts a new entry in the Column at position pos.
        All elements from pos to capacity will be moved up one.
        @param newEntry the newEntry to insert
        @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        byte[] newInternal = new byte[internal.length + 1];
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
        newInternal[pos] = ((Byte)newEntry).byteValue();
        internal = newInternal;
    }

    /**
        Swaps two entries in the Column
        @param pos1 the position of the 1st entry to swap
        @param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
        byte e1 = internal[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = e1;
    }

    /**
        Return a copy of this Column, re-ordered based on the input array of indexes.
        Does not overwrite this Column.
        @param newOrder an array of indices indicating a new order
        @return a copy of this column, re-ordered
     */
    public Column reorderRows (int[] newOrder) {
        byte[] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new byte[internal.length];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        ByteColumn bac = new ByteColumn(newInternal);
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
    public int compareRows (Object element, int pos) {
        byte[] b1 = new byte[1];
        b1[0] = ((Byte)element).byteValue();
        byte[] b2 = new byte[1];
        b2[0] = getByte(pos);
        return compareBytes(b1, b2);
    }

    /**
        Compare pos1 and pos2 positions in the Column. Return 0 if they
        are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
        @param pos1 the position of the first element to compare
        @param pos2 the position of the second element to compare
        @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
        byte[] b1 = new byte[1];
        b1[0] = getByte(pos1);
        byte[] b2 = new byte[1];
        b2[0] = getByte(pos2);
        return compareBytes(b1, b2);
    }

    /**
     * Compare two byte arrays
     * @param b1 the first byte array to compare
     * @param b2 the second byte array to compare
     * @return -1, 0, 1
     */
    private int compareBytes (byte[] b1, byte[] b2) {
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
        Given an array of ints, will remove the positions in the Column
        which are indicated by the ints in the array.
        @param indices the int array of remove indices
     */
    public void removeRowsByIndex (int[] indices) {
        HashMap toRemove = new HashMap(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.put(id, id);
        }
        byte newInternal[] = new byte[internal.length - indices.length];
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
     */
    public void sort () {
        sort(null);
    }

    /**
        Sort the elements in this column, and swap the rows in the table
        we are a part of.
        @param t the Table to swap rows for
     */
    public void sort (MutableTable t) {
        internal = doSort(internal, 0, internal.length - 1, t);
    }

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
       and swap the rows in the table  we are a part of.
       @param t the VerticalTable to swap rows for
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sort(MutableTable t,int begin, int end)
    {
    if (end > internal.length -1) {
        System.err.println(" end index was out of bounds");
        end = internal.length -1;
    }
    internal = doSort(internal, begin, end, t);

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
    private byte[] doSort (byte[] A, int p, int r, MutableTable t) {
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
    private int partition (byte[] A, int p, int r, MutableTable t) {
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
                    byte temp = A[i];
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
