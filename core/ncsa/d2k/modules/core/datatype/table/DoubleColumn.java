package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;

/**
 DoubleColumn is an implementation of NumericColumn which holds a double array
 as its internal representation.
 <br><br>
 It it optimized for: retrieval of doubles by index, compact representation
 of doubles,  swapping of doubles, setting of doubles by index, reOrdering by index,
 compareing of doubles
 It is very inefficient for: removals, insertions, additions
 */
final public class DoubleColumn extends NumericColumn {

    private double min, max;
    private double emptyValue = Double.MIN_VALUE;

    /** holds DoubleColumn's internal data rep */
    private double[] internal = null;

    /**
     Create a new, emtpy DoubleColumn.
     */
    public DoubleColumn () {
        this(0);
    }

    /**
     Create a new DoubleColumn with the specified initial capacity.
	 @param capacity the initial capacity for this column
     */
    public DoubleColumn (int capacity) {
        internal = new double[capacity];
        //setType(new Double(0.0));
    }

    /**
     Create a new DoubleColumn with the specified values.
	 @param vals the initial values to store in this column
     */
    public DoubleColumn (double[] vals) {
        this.setInternal(vals);
        //setType(new Double(0.0));
    }

    /**
     Return an exact copy of this column.  A deep copy is attempted, but if it
	 fails a new column will be created, initialized with the same data as this
	 column.
     @return A new Column with a copy of the contents of this column.
     */
    public Column copy () {
        DoubleColumn dc;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            dc = (DoubleColumn)ois.readObject();
            ois.close();
            return  dc;
        } catch (Exception e) {
            dc = new DoubleColumn(getCapacity());
            for (int i = 0; i < getCapacity(); i++)
                dc.setDouble(internal[i], i);
            dc.min = getMin();
            dc.max = getMax();
            dc.setLabel(getLabel());
            dc.setComment(getComment());
            //dc.setType(getType());
            return  dc;
        }
    }

    //////////////////////////////////////
    //// Accessing Metadata
    /**
     Return the count for the number of non-null entries.
     This variable is recomputed each time...as keeping
     track of it could be very inefficient.
     @return this DoubleColumn's number of entries
     */
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < internal.length; i++)
            if (internal[i] != emptyValue)
                numEntries++;
        return  numEntries;
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
        return  this.internal.length;
    }

    /**
     Set a new capacity for this DoubleColumn.  The capacity is its potential
     max number of entries.  If numEntries is greater than newCapacity, the
	 Column will be truncated.
     @param newCapacity the new capacity
     */
    public void setCapacity (int newCapacity) {
        if (internal != null) {
            double[] newInternal = new double[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new double[newCapacity];
    }

    /**
     Get the minimum value contained in this Column
     @return the minimum value of this Column
     */
    public double getMin () {
        initRange();
        return  min;
    }

    /**
     Get the maximum value contained in this Column
     @return the maximum value of this Column
     */
    public double getMax () {
        initRange();
        return  max;
    }

    /**
     Sets the value which indicates an empty entry.
     This can by any subclass of Number
     @param emptyVal the value to which an empty entry is set
     */
    public void setEmptyValue (Number emptyVal) {
        emptyValue = ((Number)emptyVal).doubleValue();
    }

    /**
     Gets the value which indicates an empty entry.
     @return the value of an empty entry wrapped in a subclass of Number
     */
    public Number getEmptyValue () {
        return  new Double(emptyValue);
    }

    //////////////////////////////////////
    /**
     Initializes the min and max of this DoubleColumn.
     */
    protected void initRange () {
        max = min = internal[0];
        for (int i = 1; i < internal.length; i++) {
            if (internal[i] > max)
                max = internal[i];
            if (internal[i] < min)
                min = internal[i];
        }
    }

    /*initRange*/
    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS
    /**
     Gets a reference to the internal representation of this Column
     (double[]).  Changes made to this object will be reflected in the Column.
     @return the internal representation of this Column.
     */
    public Object getInternal () {
        return  this.internal;
    }

    /**
     Returns an array of doubles scaled to the range 0-1.
     @return the internal representation of this Column.
     */
    public double[] getScaledDoubles () {
        int size1 = this.internal.length;
        double[] tmp = new double[size1];
        double min = this.getMin();
        double scal = 1.0/(this.getMax() - this.getMin());
        for (int i = 0; i < size1; i++)
            tmp[i] = (this.internal[i] - min)*scal;
        return  tmp;
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
        double[] subset = new double[len];
        System.arraycopy(internal, pos, subset, 0, len);
        DoubleColumn dc = new DoubleColumn(subset);
        dc.setLabel(getLabel());
        dc.setComment(getComment());
        //dc.setType(getType());
        return  dc;
    }

    /**
     Get a String from this column at pos
     @param pos the position from which to get a String
     @return a String representation of the entry at that position
     */
    public String getString (int pos) {
        return  String.valueOf(this.internal[pos]);
    }

    /**
     Set the value at pos to be newEntry by calling Double.parseDouble()
     @param newEntry the new item
     @param pos the position
     */
    public void setString (String newEntry, int pos) {
        internal[pos] = Double.parseDouble(newEntry);
    }

    /**
     Get the value at pos, cast to an int
     @param pos the position
     @return the value at pos as an int
     */
    public int getInt (int pos) {
        return  (int)internal[pos];
    }

    /**
     Set the value at pos to be newEntry
     @param newEntry the new item
     @param pos the position
     */
    public void setInt (int newEntry, int pos) {
        this.internal[pos] = (double)newEntry;
    }

    /**
     Get the value at pos, cast to a short
     @param pos the position
     @return the value at pos as a short
     */
    public short getShort (int pos) {
        return  (short)internal[pos];
    }

    /**
     Set the value at pos to be newEntry
     @param newEntry the position
     @param pos the position
     */
    public void setShort (short newEntry, int pos) {
        this.internal[pos] = (double)newEntry;
    }

    /**
     Set the value at pos to be newEntry
     @param newEntry the position
     @param pos the position
     */
    public void setLong (long newEntry, int pos) {
        this.internal[pos] = (double)newEntry;
    }

    /**
     Get the value at pos, cast to a long
     @param pos the position
     @return the value at pos as a long
     */
    public long getLong (int pos) {
        return  (long)internal[pos];
    }

    /**
     Get the value at pos
     @param pos the position
     @return the value at pos
     */
    public double getDouble (int pos) {
        return  this.internal[pos];
    }

    /**
     Set the value at pos
     @param newEntry the new item
     @param pos the position
     */
    public void setDouble (double newEntry, int pos) {
        this.internal[pos] = newEntry;
    }

    /**
     Get the value at pos, cast to a float
     @param pos the position
     @return the value at pos as a float
     */
    public float getFloat (int pos) {
        return  (float)internal[pos];
    }

    /**
     Set the value at pos
     @param newEntry the new item
     @param pos the position
     */
    public void setFloat (float newEntry, int pos) {
        this.internal[pos] = (double)newEntry;
    }

    /**
     Returns the value at pos as an array of bytes.  The number
	 is converted to a String and then its byte[] representation is
	 returned.
     @param pos the position
     @return the value at pos as a byte[]
     */
    public byte[] getBytes (int pos) {
        return (String.valueOf(this.internal[pos])).getBytes();
        //return  ByteUtils.writeDouble(internal[pos]);
    }

    /**
     Convert newEntry to a double.  newEntry is converted to a
	 String and then to a double.
     @param newEntry the new item
     @param pos the position
     */
    public void setBytes (byte[] newEntry, int pos) {
        setString(new String(newEntry), pos);
        //internal[pos] = ByteUtils.toDouble(newEntry);
    }

    /**
     Get the value at pos as a Double object.
     @param pos the position
     @return the value as pos as a Double
     */
    public Object getObject (int pos) {
        return  new Double(internal[pos]);
    }

    /**
     Sets the value at pos to be newEntry.  If newEntry is a Number,
     it is converted to a double and stored accordingly.  Otherwise,
     setString() is called with newEntry.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof Number)
            internal[pos] = ((Number)newEntry).doubleValue();
        else
            setString(newEntry.toString(), pos);
    }

    /**
     Converts the entry at pos to a String and returns it as a char[]
     @param pos
     @return the entry at pos as a char[]
     */
    public char[] getChars (int pos) {
        return  Double.toString(internal[pos]).toCharArray();
    }

    /**
     Converts newEntry to a String and calls setString()
     @param newEntry the new item
     @param pos the position
     */
    public void setChars (char[] newEntry, int pos) {
        setString(new String(newEntry), pos);
    }

    /**
     Returns true if the entry at pos is greater than zero, false
     otherwise.
     @param pos the position
     @return true if the value at pos is greater than zero, false
     otherwise
     */
    public boolean getBoolean (int pos) {
        if (internal[pos] > 0)
            return  true;
        return  false;
    }

    /**
     Sets the value at pos to be 1.0 if newEntry is true, sets the value
     to 0.0 otherwise.
     @param newEntry the new item
     @param pos the position
     */
    public void setBoolean (boolean newEntry, int pos) {
        if (newEntry)
            internal[pos] = (double)1.0;
        else
            internal[pos] = 0;
    }

    //////////////////////////////////////
    //////////////////////////////////////
    //// SUPPORT FOR Column INTERFACE
    /**
     Sets the reference to the internal representation of this Column.
     @param newInternal a new internal representation for this Column
     */
    public void setInternal (Object newInternal) {
        if (newInternal instanceof double[])
            this.internal = (double[])newInternal;
    }

    /**
     Gets an object representation of the entry at the indicated position in Column
     @param pos the position
     @return the entry at pos
     */
    public Object getRow (int pos) {
        return  new Double(internal[pos]);
    }

    /**
     Sets the entry at the given position to newEntry.
     The newEntry should be a subclass of Number, preferable Double.
     @param newEntry a new entry, a subclass of Number
     @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        internal[pos] = ((Number)newEntry).doubleValue();
    }

    /**
     Adds the new entry to the Column after the last non-empty position
     in the Column.
     @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
        /*int last = internal.length;
         for(int i=internal.length-1;i>=0;i--)
         if( internal[i] == emptyValue )
         last = i;
         if (last != (internal.length) )
         internal[last] = ((Number)newEntry).doubleValue();
         else {
         double[] newInternal = new double[internal.length+1];
         System.arraycopy(newInternal,0,internal,0,internal.length);
         newInternal[last] = ((Number)newEntry).doubleValue();
         internal=newInternal;
         }
         */
        int last = internal.length;
        double[] newInternal = new double[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = ((Double)newEntry).doubleValue();
        internal = newInternal;
    }

    /**
     Removes an entry from the Column, at pos.
     All entries from pos+1 will be moved back 1 position
     @param pos the position to remove
     @return a Double representation of the removed double
     */
    public Object removeRow (int pos) {
        double removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        double newInternal[] = new double[internal.length - 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return  new Double(removed);
    }

    /**
     Inserts a new entry in the Column at position pos.
     All elements from pos to capacity will be moved up one.
     @param newEntry a Double wrapped double as the newEntry to insert
     @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        /*double[] newInternal = new double[internal.length+1];
         int last = 0;
         System.arraycopy(newInternal,0,internal,0,pos-1);
         System.arraycopy(newInternal,pos,internal,pos+1,internal.length-(pos+1));
         newInternal[pos] = ((Number)newEntry).doubleValue();
         internal = newInternal;
         */
        double[] newInternal = new double[internal.length + 1];
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
        newInternal[pos] = ((Double)newEntry).doubleValue();
        internal = newInternal;
    }

    /**
     Swaps two entries in the Column
     @param pos1 the position of the 1st entry to swap
     @param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
        double d1 = internal[pos1];
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
        double[] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new double[internal.length];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        DoubleColumn dc = new DoubleColumn(newInternal);
        dc.setLabel(getLabel());
        dc.setComment(getComment());
        //dc.setType(getType());
        return  dc;
    }

    /**
     Compare the values of the object passed in and pos. Return 0 if they
     are the same, greater than zero if element is greater,
	 and less than zero if element is less.
     @param element the object to be passed in should be a subclass of Number
     @param pos the position of the element in Column to be compared with
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int pos) {
        double d1 = ((Number)element).doubleValue();
        double d2 = internal[pos];
        if (d1 == emptyValue) {
            if (d2 == emptyValue)
                return  0;
            else
                return  -1;
        }
        else if (d2 == emptyValue)
            return  1;
        if (d1 > d2)
            return  1;
        else if (d1 < d2)
            return  -1;
        return  0;
    }

    /**
     Compare pos1 and pos2 positions in the Column. Return 0 if they
     are the same, greater than zero if pos1 is greater,
	 and less than zero if pos1 is less.

     @param pos1 the position of the first element to compare
     @param pos2 the position of the second element to compare
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
        double d1 = internal[pos1];
        double d2 = internal[pos2];
        if (d1 == emptyValue) {
            if (d2 == emptyValue)
                return  0;
            else
                return  -1;
        }
        else if (d2 == emptyValue)
            return  1;
        if (d1 > d2)
            return  1;
        else if (d1 < d2)
            return  -1;
        return  0;
    }

    //////////////////////////////////////
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
        double newInternal[] = new double[internal.length - indices.length];
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

    /**
     Sort the elements in this column.
     @exception NotSupportedException when sorting is not supported
     */
    public void sort () throws NotSupportedException {
        sort(null);
    }

    /**
     Sort the elements in this column, and swap the rows in the table
     we are a part of.
     @param t the Table to swap rows for
     @exception NotSupportedException when sorting is not supported
     */
    public void sort (Table t) throws NotSupportedException {
        internal = doSort(internal, 0, internal.length - 1, t);
    }

    /**
     Implement the quicksort algorithm.  Partition the array and
     recursively call doSort.
     @param A the array to sort
     @param p the beginning index
     @param r the ending index
     @param t the Table to swap rows for
	 @return a sorted array of doubles
     */
    final private static double[] doSort (double[] A, int p, int r, Table t) {
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
	 @return the partition point
     */
    final private static int partition (double[] A, int p, int r, Table t) {
        double x = A[p];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            do {
                j--;
            } while (A[j] > x);
            do {
                i++;
            } while (A[i] < x);
            if (i < j) {
                if (t == null) {
                    double temp = A[i];
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
/*DoubleColumn*/

