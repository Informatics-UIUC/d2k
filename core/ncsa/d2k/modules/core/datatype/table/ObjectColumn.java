//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;
import ncsa.d2k.util.*;

/**
 ObjectColumn is an implementation of Column which holds an Object array as its internal
 representation.  The accessor methods will attempt to convert the Object to the appropriate
 data type.
 <br>
 It it optimized for: retrieval of Objects by index, compact representation
 of Objects,  swappings , settings, reOrder-ing by index,
 It is very inefficient for: removals, insertions, additions
 */
final public class ObjectColumn extends AbstractColumn {

    private static final Object emptyValue = null;

    /** holds ObjectColumn's enternal data rep */
    private Object[] internal = null;

    /**
     Create a new, emtpy ObjectColumn
     */
    public ObjectColumn () {
        this(0);
    }

    /**
     Create an ObjectColumn with the specified capacity
     @param capacity the initial capacity
     */
    public ObjectColumn (int capacity) {
        internal = new Object[capacity];
        setType(new Object());
    }

    /**
     Create an ObjectColumn with the specified values.
     @param vals the initial values
     */
    public ObjectColumn (Object[] vals) {
        this.setInternal(vals);
        setType(new Object());
    }

    /**
     Return an exact copy of this column.  A deep copy is attempted, but if
	 it fails a new column will be created, initialized with the same data
	 as this column.
     @return A new Column with a copy of the contents of this column.
     */
    public Column copy () {
        ObjectColumn newCol;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            newCol = (ObjectColumn)ois.readObject();
            ois.close();
            return  newCol;
        } catch (Exception e) {
            newCol = new ObjectColumn(getCapacity());
            for (int i = 0; i < getCapacity(); i++)
                newCol.setObject(internal[i], i);
            newCol.setLabel(getLabel());
            newCol.setComment(getComment());
            newCol.setType(getType());
            return  newCol;
        }
    }

    /**
     Sort the elements in this column. Not supported for ObjectColumn.
     @exception NotSupportedException when sorting is not supported
     */
    public void sort () throws NotSupportedException {
        throw  new NotSupportedException();
    }

    /**
     Sort the elements in this column, and swap the rows in the table
     we are a part of.  Not supported for ObjectColumn.
     @param t the Table to swap rows for
     @exception NotSupportedException when sorting is not supported
     */
    public void sort (Table t) throws NotSupportedException {
        throw  new NotSupportedException();
    }

    //////////////////////////////////////
    //// Accessing Metadata
	/**
		Get the number of entries this Column holds.  This is the number of
		non-null entries in the Column.
		@return this Column's number of entries
	*/
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < internal.length; i++)
            if (internal[i] != emptyValue)
                numEntries++;
        return  numEntries;
    }

	/**
	 * Get the number of rows that this column can hold.  Same as getCapacity().
	 * @return the number of rows this column can hold
	 */
	public int getNumRows() {
		return getCapacity();
	}

    /**
     Get the capacity of this Column, its potential maximum number of entries.
     @return the max number of entries this Column can hold
     */
    public int getCapacity () {
        return  this.internal.length;
    }

    /**
     Set a new capacity for this ObjectColumn.  The capacity is its potential
     max number of entries.  If numEntries is greater than newCapacity the
	 Column will be truncated.
     @param newCapacity the new capacity
     */
    public void setCapacity (int newCapacity) {
        if (internal != null) {
            Object[] newInternal = new Object[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new Object[newCapacity];
    }

    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS
    /**
     Gets a reference to the internal representation of this Column
     (Object[]).  Changes made to this object will be reflected in the Column.
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
        Object[] subset = new Object[len];
        System.arraycopy(internal, pos, subset, 0, len);
        ObjectColumn oc = new ObjectColumn(subset);
        oc.setLabel(getLabel());
        oc.setComment(getComment());
        oc.setType(getType());
        return  oc;
    }

    /**
     Get a String from this Column at pos
     @param pos the position from which to get a String
     @return a String representation of the entry at that position
     */
    public String getString (int pos) {
        return  internal[pos].toString();
    }

    /**
     Set the value at pos to be newEntry.
     @param newEntry the new item
     @param pos the position
     */
    public void setString (String newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     If the item at pos is a Number, return its int value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its int value by calling Integer.parseInt()
     @param pos
     @return the int value of the item at pos
     */
    public int getInt (int pos) {
        //return new Integer(internal[pos].toString()).intValue();
        if (internal[pos] instanceof Number)
            return  ((Number)internal[pos]).intValue();
        else if (internal[pos] instanceof byte[])
            return  Integer.parseInt(new String((byte[])internal[pos]));
			//ByteUtils.toInt((byte[])internal[pos]);
        else if (internal[pos] instanceof char[])
            return  Integer.parseInt(new String((char[])internal[pos]));
        else
            return  Integer.parseInt(internal[pos].toString());
    }

    /**
     Set the value at pos to be newEntry (an Integer)
     @param newEntry the new item
     @param pos the position
     */
    public void setInt (int newEntry, int pos) {
        internal[pos] = new Integer(newEntry);
    }

    /**
     If the item at pos is a Number, return its short value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its short value by calling Short.parseShort()
     @param pos
     @return the int value of the item at pos
     */
    public short getShort (int pos) {
        if (internal[pos] instanceof Number)
            return  ((Number)internal[pos]).shortValue();
        else if (internal[pos] instanceof byte[])
			return Short.parseShort(new String((byte[])internal[pos]));
            //return  ByteUtils.toShort((byte[])internal[pos]);
        else if (internal[pos] instanceof char[])
            return  Short.parseShort(new String((char[])internal[pos]));
        else
            return  Short.parseShort(internal[pos].toString());
    }

    /**
     Set the value at pos to be newEntry (a Short)
     @param newEntry the new item
     @param pos the position
     */
    public void setShort (short newEntry, int pos) {
        internal[pos] = new Short(newEntry);
    }

    /**
     If the item at pos is a Number, return its long value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its long value by calling Long.parseLong()
     @param pos
     @return the int value of the item at pos
     */
    public long getLong (int pos) {
        if (internal[pos] instanceof Number)
            return  ((Number)internal[pos]).longValue();
        else if (internal[pos] instanceof byte[])
            //return  ByteUtils.toLong((byte[])internal[pos]);
			return Long.parseLong(new String((byte[])internal[pos]));
        else if (internal[pos] instanceof char[])
            return  Long.parseLong(new String((char[])internal[pos]));
        else
            return  Long.parseLong(internal[pos].toString());
    }

    /**
     Set the value at pos to be newEntry (a Long)
     @param newEntry the new item
     @param pos the position
     */
    public void setLong (long newEntry, int pos) {
        internal[pos] = new Long(newEntry);
    }

    /**
     If the item at pos is a Number, return its double value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its double value by calling Double.parseDouble()
     @param pos
     @return the int value of the item at pos
     */
    public double getDouble (int pos) {
        if (internal[pos] instanceof Number)
            return  ((Number)internal[pos]).doubleValue();
        else if (internal[pos] instanceof byte[])
            //return  ByteUtils.toDouble((byte[])internal[pos]);
			return Double.parseDouble(new String((byte[])internal[pos]));
        else if (internal[pos] instanceof char[])
            return  Double.parseDouble(new String((char[])internal[pos]));
        else
            return  Double.parseDouble(internal[pos].toString());
    }

    /**
     Set the value at pos to be newEntry (a Double)
     @param newEntry the new item
     @param pos the position
     */
    public void setDouble (double newEntry, int pos) {
        internal[pos] = new Double(newEntry);
    }

    /**
     If the item at pos is a Number, return its float value.  Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its float value by calling Float.parseFloat()
     @param pos
     @return the int value of the item at pos
     */
    public float getFloat (int pos) {
        if (internal[pos] instanceof Number)
            return  ((Number)internal[pos]).floatValue();
        else if (internal[pos] instanceof byte[])
            //return  ByteUtils.toFloat((byte[])internal[pos]);
			return Float.parseFloat(new String((byte[])internal[pos]));
        else if (internal[pos] instanceof char[])
            return  Float.parseFloat(new String((char[])internal[pos]));
        else
            return  Float.parseFloat(internal[pos].toString());
    }

    /**
     Set the value at pos to be newEntry (a Float)
     @param newEntry the new item
     @param pos the position
     */
    public void setFloat (float newEntry, int pos) {
        internal[pos] = new Float(newEntry);
    }

    /**
     If the entry at pos is a byte[], return the byte[], otherwise
     convert the Object to a byte[] by calling ByteUtils.writeObject()
     @param pos the position
     @return the entry at pos as a byte[]
     */
    public byte[] getBytes (int pos) {
        //return internal[pos].toString().getBytes();
        if (internal[pos] instanceof byte[])
            return  (byte[])internal[pos];
        else
            return  ByteUtils.writeObject(internal[pos]);
    }

    /**
     Set the value at pos to be newEntry.
     @param newEntry the new item
     @param pos the position
     */
    public void setBytes (byte[] newEntry, int pos) {
        //internal[pos] = newEntry;
        internal[pos] = newEntry;//ByteUtils.toObject(newEntry);
    }

    /**
     Get the item at pos.
     @param pos the position
     @return the Object at pos
     */
    public Object getObject (int pos) {
        return  internal[pos];
    }

    /**
     Set the value at pos to be newEntry.
     @param newEntry the new item
     @param pos the position
     */
    public void setObject (Object newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     If the item at pos is a char[], return it.  Otherwise
     call the toString() method on the Object and return it
     as a char[].
     @param pos the position
     @return the item at pos as a char[]
     */
    public char[] getChars (int pos) {
        if (internal[pos] instanceof char[])
            return  (char[])internal[pos];
        return  internal[pos].toString().toCharArray();
    }

    /**
     Set the item at pos to be newEntry.
     @param newEntry the new item
     @param pos the position
     */
    public void setChars (char[] newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     Get the item at pos as a Boolean.  If the item is a Boolean,
     return its boolean value, otherwise construct a new Boolean
     by calling the toString() method on the item and return its
     boolean value.
     @param pos the position
     @return the item as pos as a boolean value
     */
    public boolean getBoolean (int pos) {
        if (internal[pos] instanceof Boolean)
            return  ((Boolean)internal[pos]).booleanValue();
        return  Boolean.valueOf(internal[pos].toString()).booleanValue();
    }

    /**
     Set the item at pos to be newEntry (a Boolean).
     @param newEntry the new item
     @param pos the position
     */
    public void setBoolean (boolean newEntry, int pos) {
        internal[pos] = new Boolean(newEntry);
    }

    //////////////////////////////////////
    //// SUPPORT FOR Column INTERFACE
    /**
     Sets the reference to the internal representation of this Column.
     @param newInternal a new internal representation for this Column
     */
    public void setInternal (Object newInternal) {
        if (newInternal instanceof Object[])
            this.internal = (Object[])newInternal;
    }

    /**
     Gets an object representation of the entry at the indicated position in Column.
     @param pos the position
     @return the entry at pos
     */
    public Object getRow (int pos) {
        return  internal[pos];
    }

    /**
     Sets the entry at the given position to newEntry.
     The newEntry should be a subclass of Number, preferable Object.
     @param newEntry a new entry, a subclass of Number
     @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     Adds the new entry to the Column after the last non-empty position
     in the Column.
     @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
         int last = internal.length;
        Object[] newInternal = new Object[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = newEntry;
        internal = newInternal;
    }

    /**
     Removes an entry from the Column, at pos.
     All entries from pos+1 will be moved back 1 position.
     @param pos the position to remove
     @return a Object representation of the removed int
     */
    public Object removeRow (int pos) {
        Object removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        Object newInternal[] = new Object[internal.length - 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return  removed;
    }

    /**
     Inserts a new entry in the Column at position pos.
     All elements from pos to capacity will be moved up one.
     @param newEntry an Object wrapped int as the newEntry to insert
     @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        /*int last = 0;
         System.arraycopy(internal,pos,internal,pos+1,internal.length-(pos+1));
         internal[pos] = newEntry;
         */
        Object[] newInternal = new Object[internal.length + 1];
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
        newInternal[pos] = newEntry;
        internal = newInternal;
    }

    /**
     Swaps two entries in the Column.
     @param pos1 the position of the 1st entry to swap
     @param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
        Object d1 = internal[pos1];
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
        Object[] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new Object[internal.length];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        ObjectColumn oc = new ObjectColumn(newInternal);
        oc.setType(getType());
        oc.setComment(getComment());
        oc.setLabel(getLabel());
        return  oc;
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
        Object d1 = element;
        Object d2 = internal[pos];
        if (d1 == emptyValue) {
            if (d2 == emptyValue)
                return  0;
            else
                return  -1;
        }
        else if (d2 == emptyValue)
            return  1;
        if (d1 == d2)
            return  0;
        else
            return  2;
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
        Object d1 = internal[pos1];
        Object d2 = internal[pos2];
        if (d1 == emptyValue) {
            if (d2 == emptyValue)
                return  0;
            else
                return  -1;
        }
        else if (d2 == emptyValue)
            return  1;
        if (d1 == d2)
            return  0;
        else
            return  2;
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
     Given an array of ints, will remove the positions in the Table
     which are indicated by the ints in the array.
     @param indices the int array of remove indices
     */
    public void removeByIndex (int[] indices) {
        HashMap toRemove = new HashMap(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.put(id, id);
        }
        Object newInternal[] = new Object[internal.length - indices.length];
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
}
/*ObjectColumn*/
