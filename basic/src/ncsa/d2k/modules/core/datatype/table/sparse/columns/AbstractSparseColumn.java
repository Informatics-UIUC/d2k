/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  ncsa.d2k.modules.core.datatype.table.sparse.columns;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import  ncsa.d2k.modules.core.datatype.table.*;
import  ncsa.d2k.modules.core.datatype.table.basic.*;
import  ncsa.d2k.modules.core.datatype.table.sparse.*;
import  ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import  java.util.*;
import gnu.trove.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 *
 * @todo rethink removeRowMissing and removeRowEmpty methods. they are kind-a
 * redundant as one can call setValueToMissing or setValueToEmpty with a true
 * value.
 */
abstract public class AbstractSparseColumn extends AbstractColumn {
    //==============
    // Data Members
    //==============
    protected VIntHashSet missing;
    protected VIntHashSet empty;

    //================
    // Constructor(s)
    //================
    /**
     * put your documentation comment here
     */
    public AbstractSparseColumn () {
        super();
        missing = new VIntHashSet();
        empty = new VIntHashSet();
        super.setLabel("");
    }

    //================
    // Public Methods
    //================
    /**
     * FIX THIS -- DDS
     * @return
     */
    public int getNumMissingValues () {
        return  missing.size();
    }

    /**
     * put your documentation comment here
     * @param indices
     * @return
     */
    //public abstract Column getSubset (int[] indices);

    /**
     * copies only the label and the comment of <code>srcCol</code> into this
     * column.
     * @param srcCol - an object of type AbstractSparseColumn, from which the
     *                 attributes are being copied.
     *
     */
    public void copyAttributes (AbstractSparseColumn srcCol) {
        setLabel(srcCol.getLabel());
        setComment(srcCol.getComment());
    }

    /**
     * Copies the missing set and label and comment of <code>srcCol</code>
     * into this column
     *
     * @param srcCol    the column from which the data is copied.
     */
    public void copy (AbstractSparseColumn srcCol) {
        this.missing = srcCol.missing.copy();
        this.empty = srcCol.empty.copy();
        copyAttributes(srcCol);
    }

    /**
     * Returns the valid rows in this column, sorted.
     *
     * @return    a sorted int array with all the valid row numbers in this column.
     */
    public int[] getIndices () {
        return  VHashService.getIndices(getElements());
    }

    /**
     * Returns the valid rows in this column, UN SORTED
     *
     *
     * @return    an UN-SORTED int array with all the valid row numbers in this column.
     */
    public int[] keys () {
        return  getElements().keys();
    }

    /**
     * Adds  <code>newEntry</code> to the end of this column
     *
     * @param newEntry - the data to be inserted at the end of this column
     */
    public void addRow (Object newEntry) {
        setObject(newEntry, getNumRows());
    }

    /**
     * Returns the total number of data items that this column holds.
     *
     * VERED - added the doesValueExist test... (7-12-04)
     *
     * @return    the total number of data items that this column holds.
     */
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < getNumRows(); i++) {
            if (doesValueExist(i) && !isValueMissing(i) && !isValueEmpty(i)) {
                numEntries++;
            }
        }
        return  numEntries;
    }

    /**
     * Returns the maximal valid row number in this column + 1, because counting
     * of rows starts from zero.
     *
     * @return    the maximal valid row number in this column + 1
     */
    public int getNumRows () {
        return  VHashService.getMaxKey(getElements()) + 1;
    }

    /**
     * Removes all elements stored in this column at rows #<code>pos</code>
     * through <code>pos+len</code>
     *
     * @param pos - the row number from which to begin removing data
     * @param len - number of consequitive rows to remove after row #<code>pos</code>
     */
    public void removeRows (int pos, int len) {
        //VERED: (7-13-04) added the '-1' after 'len'. range is from pos to pos+len-1 including....
        int[] indices = VHashService.getIndicesInRange(pos, pos + len - 1,
                getElements());
        for (int i = 0; i < indices.length; i++) {
            removeRow(indices[i]);
        }
    }

    /**
     * Removes entries from this column according to a set of flags.
     * @param flags - a boolen array. if flags[i] is true then row # i
     * is being removed. if flags is smaller than the capacity of this column -
     * removing the rest of the rows that their number is higher than the length
     * of flags.
     */
    public void removeRowsByFlag (boolean[] flags) {
        int i;
        for (i = 0; i < flags.length; i++) {
            if (flags[i]) {
                removeRow(i);
            }
        }
        int[] toRemove = getRowsInRange(i, getNumRows());
        this.removeRowsByIndex(toRemove);
    }

    /**
     * Removes rows from this column according to given row numbers in
     * <code>indices</code>
     *
     * @param indices - the row numbers to be removed.
     */
    public void removeRowsByIndex (int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            removeRow(indices[i]);
        }
    }

    /**
     * adjust the number of rows in this column: removes all rows that
     * their number is higher than <code>newCapacity</code>
     *
     * @param newCapacity    upper border for maximal row number in this column
     */
    public void setNumRows (int newCapacity) {
        //    VHashMap column = getElements();
        if (newCapacity < getNumRows()) {
            int[] indices = this.getIndices();
            int ignore = 0;
            for (int i = indices.length - 1; (i >= 0) && (newCapacity < indices[i]
                    + 1); i--) {
                removeRow(indices[i]);
            }
        }
    }

    /**
     * Tests for the validity of 2 values in this column.
     * @param pos1     row number of first value to be validated
     * @param pos2     row number of second value to be validated
     * @return         an int representing the relation between the values.
     *                 If the value at row #<code>pos1</code> is either missing
     *                 empty or does not exist and value at row #<code>pos2</code>
     *                 is a regular value - returns -1.
     *                 Returns 1 if the situation is vice versia.
     *                 Returns 0 if they are both not regular values.
     *                 Returns 2 if both values are regular.
     */
    public int validate (int pos1, int pos2) {
        boolean valid_1 = (doesValueExist(pos1) && !isValueEmpty(pos1) && !isValueMissing(pos1));
        boolean valid_2 = (doesValueExist(pos2) && !isValueEmpty(pos2) && !isValueMissing(pos2));
        return  validate(valid_1, valid_2);
    }

    /**
     * Tests the validity of <code>obj</code> and the value at row #<code>pos</code>
     * int his column.
     * @param obj    the fist value to be validated
     * @param pos   the row number of the second value to be vlaidated
     * @return      an int representing the relation between the 2 values. For more
     *             details see validate(int, int)
     */
    public int validate (Object obj, int pos) {
        boolean valid = (doesValueExist(pos) && !isValueEmpty(pos) && !isValueMissing(pos));
        return  validate(obj != null, valid);
    }

    /**
     * Retrieves an Object representation of row #<code>pos</code>
     *
     * @param pos    the row number from which to retrieve the Object
     * @return       Object representation of the data at row #<code>pos</code>
     */
    public Object getRow (int pos) {
        return  getObject(pos);
    }

    /**
     * Sets the entry at row #<code>pos</code> to <code>newEntry</code>.
     *
     * @param newEntry       a new entry represented by an Object
     * @param pos            the position to set the new entry
     */
    public void setRow (Object newEntry, int pos) {
        setObject(newEntry, pos);
    }

    /**
     * Returns true if a double vlaue can be retrieved from row no. <coe>row</code>
     * in this column
     *
     * @param row   the row number to check if a double value can be retrieved from it
     * @return      true if a double value can be retrieved from row no.
     *              <code>row</code>. false if an exception occures while trying
     *              to retrieve the value
     */
    public boolean isDataNumeric (int row) {
        int colType = getType();
        //the only columns that numeric values might not be retrieves from are
        //the object columns
        if (!(colType == ColumnTypes.BYTE_ARRAY || colType == ColumnTypes.CHAR_ARRAY
                || colType == ColumnTypes.STRING || colType == ColumnTypes.OBJECT)) {
            return  true;
        }
        try {
            Double.parseDouble(SparseStringColumn.toStringObject(getObject(row)));
        } catch (Exception e) {
            return  false;
        }
        return  true;
    }

    /**
     * Returns true if a double value can be retrieved from each data item in
     * the column. return false if else.
     *
     * @return    true if this methods succeeds in retrieving a double value
     *            from each entry in this column. returns false if an exception
     *            is caught during the process.
     */
    public boolean isNumeric () {
        int colType = getType();
        //the only columns that numeric values might not be retrieves from are
        //the object columns
        if (!(colType == ColumnTypes.BYTE_ARRAY || colType == ColumnTypes.CHAR_ARRAY
                || colType == ColumnTypes.STRING || colType == ColumnTypes.OBJECT)) {
            return  true;
        }
        //retrieving row numbers
        int[] rowNumbers = getElements().keys();
        //for each row - trying to parse a double out of a string constructed
        //from the data
        boolean retVal = true;
        for (int i = 0; i < rowNumbers.length && retVal; i++) {
            if (!isValueMissing(rowNumbers[i]) && !isValueEmpty(rowNumbers[i])) {
                retVal = retVal && isDataNumeric(rowNumbers[i]);
            }
        }
        return  retVal;
    }

    /**
     * Verifies if row no. <code>pos</code> holds a value.
     *
     * @param pos     the inspected row no.
     * @return        true if row no. <code>pos</code> holds a value, otherwise
     *                returns false.
     */
    public boolean doesValueExist (int pos) {
        return  (((VHashMap)getElements()).containsKey(pos));
    }

    /**
     * Verifies if row no. <code>pos</code> holds a value.
     * @todo
     *
     * @param pos     the inspected row no.
     * @return        true if row no. <code>pos</code> holds a value, otherwise
     *                returns false.
     */
    public boolean isValueDefault (int pos) {
        return  (!((VHashMap)getElements()).containsKey(pos));
    }

    /**
     * Verifies if row #<code>row</code> holds an empty value
     * @param row - the row which its value is being validated.
     * @return true if the value is empty, false otherwise
     */
    public boolean isValueEmpty (int row) {
        return  empty.contains(row);
    }

    /**
     * Verifies if row #<code>row</code> holds a missing value
     * @param row - the row which its value is being validated.
     * @return true if the value is missing, false otherwise
     */
    public boolean isValueMissing (int row) {
        return  missing.contains(row);
    }

    /**
     * Verifies if any missing values exist in the table
     * @param row - the row which its value is being validated.
     * @return true if the value is missing, false otherwise
     */
    public boolean hasMissingValues () {
        if (missing.size() > 0) {
            return  true;
        }
        //    for (int i = 0; i < this.getNumRows(); i++) {
        //      if (this.isValueMissing(i)) {
        //        return true;
        //      }
        //    }
        return  false;
    }

    /**
     * Puts the data stored in this column into <code>buffer</code>.
     * <codE>buffer</codE> must be an array of some type. the values will be
     * converted as needed, according to the type of <codE>buffer</codE>.
     *
     * Since this is a sparse column it is best to use method
     * getData(Object, int[]) for more extenssive results.
     */
    public void getData (Object buffer) {
        int[] rowNumbers = this.getIndices();
        int size = rowNumbers.length;
        boolean numeric = isNumeric();
        if (buffer instanceof int[]) {
            //   if(!numeric) throw new DataFormatException("The columns in not numeric. Could not convert data to type int[]");
            int[] b1 = (int[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getInt(rowNumbers[i]);
            }
        }
        else if (buffer instanceof float[]) {
            // if(!numeric)
            // throw new DataFormatException(
            //     "The columns in not numeric. Could not convert data to type float[]");
            float[] b1 = (float[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getFloat(rowNumbers[i]);
            }
        }
        else if (buffer instanceof double[]) {
            //if(!numeric)
            //throw new DataFormatException(
            //    "The columns in not numeric. Could not convert data to type double[]");
            double[] b1 = (double[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getDouble(rowNumbers[i]);
            }
        }
        else if (buffer instanceof long[]) {
            //if(!numeric)
            //throw new DataFormatException(
            //    "The columns in not numeric. Could not convert data to type long[]");
            long[] b1 = (long[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getLong(rowNumbers[i]);
            }
        }
        else if (buffer instanceof short[]) {
            //if(!numeric)
            //throw new DataFormatException(
            //    "The columns in not numeric. Could not convert data to type short[]");
            short[] b1 = (short[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getShort(rowNumbers[i]);
            }
        }
        else if (buffer instanceof boolean[]) {
            boolean[] b1 = (boolean[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getBoolean(rowNumbers[i]);
            }
        }
        else if (buffer instanceof String[]) {
            String[] b1 = (String[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getString(rowNumbers[i]);
            }
        }
        else if (buffer instanceof char[][]) {
            char[][] b1 = (char[][])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getChars(rowNumbers[i]);
            }
        }
        else if (buffer instanceof byte[][]) {
            byte[][] b1 = (byte[][])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getBytes(rowNumbers[i]);
            }
        }
        else if (buffer instanceof Object[]) {
            Object[] b1 = (Object[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getObject(rowNumbers[i]);
            }
        }
        else if (buffer instanceof byte[]) {
            byte[] b1 = (byte[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getByte(rowNumbers[i]);
            }
        }
        else if (buffer instanceof char[]) {
            char[] b1 = (char[])buffer;
            for (int i = 0; i < b1.length && i < size; i++) {
                b1[i] = getChar(rowNumbers[i]);
            }
        }
    }

    /**
     * Retrieve a new order for the valid rows of this column.
     * The new order is a sorted order.
     *
     * @return      a VIntIntHashMap that represents the new order s.t.:
     *              let retVal bew the returned value, (i,j) be keys in retVal and
     *              (x,y) be their mapped vlaue sin retVal, respectively.
     *              if i<j then the value at row no. x in this column is smaller than
     *              or equal to the value at row no. y in this column.
     */
    //VERED - commented this out - this is now taken care of by getSortedOrder()
    /*  public VIntIntHashMap getNewOrder() {
     return getElements().getSortedOrder();
     }*/
    /**
     * Retrieve a new order for the valid rows of this column in the range <code>
     * [begin, end]</code>.
     * The new order is a sorted order.
     *
     * @param begin row no. from which starts the section to which a sorted order
     *              should be retrieved.
     * @param end   row no. at which ends the section to which a sorted order should
     *              be retrieved.
     * @return      a VIntIntHashMap that represents the new order s.t.:
     *              let retVal bew the returned value, (i,j) be keys in retVal and
     *              (x,y) be their mapped vlaue sin retVal, respectively.
     *              if i<j then the value at row no. x in this column is smaller than
     *              or equal to the value at row no. y in this column.
     *              this new order affects only the rows in the range specified by
     *              <code>begin</codE> and <code>end</codE>.
     */
    //VERED - commented this out - this is taken care of by getSortedOrder
    /*  public VIntIntHashMap getNewOrder(int begin, int end) {
     if (end < begin) {
     return new VIntIntHashMap(0);
     }
     return getElements().getSortedOrder(begin, end);
     }
     */
    /**
     * Sorts <code>t</code> according to the natural sorted order of this
     * column
     *
     * @param t   a table this column is part of, to be sorted.
     */
    public void sort (MutableTable t) {
        ((SparseMutableTable)t).sort(getSortedOrder());
    }

    /**
     * Sorts the rows in the range <codE>[begin, end]</code> in <code>t</code>
     * according to the natural sorting order of this column's rows in the
     * specified range. this column is part of <codE>t</code>.
     *
     * @param t        a table to sorts its rows.
     * @param begin    row no. at which starts the section to be sorted.
     * @param end      row no. at which ends the section to be sorted.
     */
    public void sort (MutableTable t, int begin, int end) {
        if (end < begin) {
            return;
        }
        ((SparseMutableTable)t).sort(getSortedOrder(begin, end));
    }

    /**
     * returns a copy of this column, with its rows reordered as following:
     * for each pair (key, val) in newOrder - if this column has a value in
     * row # val, put this value in row # key in the returned column.
     * for rows in this columns that are not values in newOrder - copy them as is
     * to the returned value.
     * @param newOrder - an int to int mapping that defines the new order.
     * @return - a column with the values of this column, reordered.
     */
    public Column reorderRows (VIntIntHashMap newOrder) {
        String columnClass = this.getClass().getName();
        AbstractSparseColumn retVal = null;
        try {
            retVal = (AbstractSparseColumn)Class.forName(columnClass).newInstance();
        } catch (Exception e) {
            System.out.println(e);
        }
        retVal.setElements(getElements().reorder(newOrder));
        retVal.missing = missing.reorder(newOrder);
        retVal.empty = empty.reorder(newOrder);
        retVal.copyAttributes(this);
        //reorderRows(retVal, newOrder);
        return  retVal;
    }

    /**
     * Reorders the data stored in this column in a new column.
     * Does not change this column.
     *
     * Algorithm: copy this column into the returned value.
     * for each entry <code>newOrder[i]</code> that is a valid row in
     * this column - put its value in row no. i in the returned value.
     *
     * @param newOrder - an int array, which its elements define a new order for
     *                   this column.
     * @return an AbstractSparseColumn ordered according to <code>newOrder</code>.
     */
    public Column reorderRows (int[] newOrder) {
        VIntIntHashMap mapOrder = VHashService.toMap(newOrder, getElements());
        return  reorderRows(mapOrder);
    }

    /**
     * Sets row #<code>row</code>  to be holding a missign value.
     * @param row - the row number to be set.
     *
     public void setValueToMissing(int row) {
     missing.add(row);
     removeRow(row);
     }
     */
    /**
     * Sets row #<code>row</code>  to be holding an empty value.
     * @param row - the row number to be set.
     *
     public void setValueToEmpty(int row) {
     removeRow(row);
     }
     */
    /**
     * resets row #<code>row</code> state as missing value according to <code>
     * isMissing</code>. if <code>isMissing</code> is true sets it to hold a missing value
     * otherwise - marks it as no holding a missing value.
     *
     * @param isMissing   a flag indicating wheather row #<code>row</code> should
     *                    be marked as missing value (if true) or regular value
     *                    (if false).
     * @param row - the row number to be set.
     */
    public void setValueToMissing (boolean isMissing, int row) {
        if (isMissing) {
            missing.add(row);
        }
        else {
            missing.remove(row);
        }
    }

    /**
     * Sets row #<code>row</code>  to be holding an empty value.
     *
     * @param isEmpty     a flag indicating wheather row #<code>row</code> should
     *                    be marked as empty value (if true) or regular value
     *                    (if false).
     * @param row - the row number to be set.
     */
    public void setValueToEmpty (boolean isEmpty, int row) {
        if (isEmpty) {
            empty.add(row);
        }
        else {
            empty.remove(row);
        }
    }

    /**
     * Remove the designation that this particular row is missing.
     * @param pos
     */
    public void removeRowMissing (int pos) {
        if (missing.contains(pos)) {
            missing.remove(pos);
        }
    }

    /**
     * Remove the designation that this particular row is empty.
     *
     */
    public void removeRowEmpty (int pos) {
        if (empty.contains(pos)) {
            empty.remove(pos);
        }
    }

    /**
     * Inserts a new entry in the Column at position <code>pos</code>.
     * All entries at row numbers greater than <codE>pos</code> are moved down
     * the column to the next row.
     * @param newEntry the newEntry to insert
     * @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        getElements().insertObject(newEntry, pos);
        missing.increment(pos);
    }

    /**
     * Replaces a row's entry at position <code>pos</code>.
     *
     * @todo (vered): if this column is a string column - this method might change
     * the entry of many rows... why use this method when one can use setObject?
     * setType as implemented - removes the old entry and puts in the new one.
     * it does not set the value to be non missing, we assumed this is under the
     * resposibility of the user.
     *
     * Xiaolei - 07/08/2003
     */
    public void replaceRow (Object newEntry, int pos) {
        if (this instanceof SparseStringColumn) {
            if (getElements().containsKey(pos)) {
                ((SparseStringColumn)this).valuesInColumn[((SparseStringColumn)this).row2Id.get(pos)] = (String)newEntry;
            }
        }
        else {
            getElements().replaceObject(newEntry, pos);
        }
        if (missing.contains(pos)) {
            missing.remove(pos);
        }
    }

    //===================
    // Protected Methods
    //===================
    /**
     * Returns the hash map that holds all the elements of this map
     */
    protected abstract VHashMap getElements ();

    /**
     * sets the hash map of this column to refer <code>map</code>.
     * this is a protected method, to be used only internally, in order
     * to avoid code duplication and implementation of more methods
     * in the abstract class.
     * @param map - values, to be held by this column.
     */
    protected abstract void setElements (VHashMap map);

    //this method is for test units only.
    public boolean equals (Object other) {
        boolean retVal = true;
        if (!(other instanceof AbstractSparseColumn))
            return  false;
        AbstractSparseColumn col = (AbstractSparseColumn)other;
        /*
         VHashMap elements = getElements();
         if(!elements.equals(col.getElements())) return false;
         if(!missing.equals(col.missing)) return false;
         if(!empty.equals(col.empty)) return false;
         */
        //VERED: comparing sparse column is different than comparing regular ones.
        //2 sparse columns might hold the same value but in different row indices.
        //this happens especially in the test cases...
        //there fore I've changed this method so that it will compare the data
        //indices independently. (7-13-04)
        if (this.getNumEntries() != col.getNumEntries())
            return  false;
        int thisNumRows = getNumRows();
        int otherNumRows = col.getNumRows();
        int thisCounter = 0;
        int otherCounter = 0;
        while (thisCounter < thisNumRows && otherCounter < otherNumRows) {
            //if both counters points to an existing value - comparing the values.
            try {
                if (doesValueExist(thisCounter) && col.doesValueExist(otherCounter)) {
                    if (!col.getString(otherCounter).equals(this.getString(thisCounter)))
                        return  false;
                    otherCounter++;
                    thisCounter++;
                }
                else {          //one of the counters needs to be promoted.
                    if (!doesValueExist(thisCounter))
                        thisCounter++;
                    else
                        otherCounter++;
                }
            }                   //else
            catch (NullPointerException e) {
                System.out.println("caught an Exception!");
                e.printStackTrace();
                throw  e;
            }                   //catch
        }       //while
        return  retVal;
    }

    /**
     * Reteives valid rows indices from row no. <code>begin</code> through row no.
     * <code>end</code>.
     *
     * @param begin row number to begin retrieving from
     * @param end   last row number of retrieved section
     * @return      an int array with valid indices in the range specified by
     *              <code>begin</code> and <code>end</code>, sorted.
     */
    protected int[] getRowsInRange (int begin, int end) {
        if (end < begin) {
            int[] retVal =  {};
            return  retVal;
        }
        return  VHashService.getIndicesInRange(begin, end, getElements());
    }

    /**
     * retrieves a subset map of the empty and missing maps and assigns it to
     * <code>destCol</code>. the subset includes rows <codE>pos</codE> through
     * <code>pos + len -1 </code>.
     * @param destCol - destination column to holdthe subset maps.
     * @param pos - first row in the subset
     * @param len - number of consecutive rows in the subset.
     */
    protected void getSubset (AbstractSparseColumn destCol, int pos, int len) {
        destCol.missing = missing.getSubset(pos, len);
        destCol.empty = empty.getSubset(pos, len);
        destCol.copyAttributes(this);
    }

    /**
     * retrieves a subset map of the empty and missing maps and assigns it to
     * <code>destCol</code>. <code>indices </codE> indicates which rows are
     * to be included in the subset.
     * @param destCol - destination column to holdthe subset maps.
     * @param indices - defines which rows are to be included in the subset
     */
    protected void getSubset (AbstractSparseColumn destCol, int[] indices) {
        destCol.missing = missing.getSubset(indices);
        destCol.empty = empty.getSubset(indices);
        destCol.copyAttributes(this);
    }

    /**
     * creates a copy of empty and missing maps and reorders the mapping.
     * then the reordered maps are assigned to <code>toOrder</code>.
     * the new order is defined as follows:
     * for each pair <key, val> in <code>newOrder</code>, the value that is
     * mapped to val in either empty or missing, will be mapped to key in
     * empty and missing of <code>toOrder</code>.
     * @param toOrder
     * @param newOrder
     */
    protected void reorderRows (AbstractSparseColumn toOrder, VIntIntHashMap newOrder) {
        toOrder.missing = missing.reorder(newOrder);
        toOrder.empty = empty.reorder(newOrder);
        toOrder.copyAttributes(this);
    }

    //=================
    // Private Methods
    //=================
    /**
     * Used by validate methods.
     * @param valid1    boolean value, representing validity of a value in the map
     *                 to be compared.
     * @param valid2    boolean value, representing validity of a value in the map
     *                 to be compared.
     * @return             an int representing the co-validity of the values. for more
     *                     details see validate(int, int);
     */
    private int validate (boolean valid1, boolean valid2) {
        if (!valid1) {
            if (!valid2) {
                return  0;
            }
            else {
                return  -1;
            }
        }
        else if (!valid2) {
            return  1;
        }
        else {
            return  2;
        }
    }

    /**
     * Returns an array of Elements containing the values in this column
     * with their current indices.
     * this is for purposes of sorting.
     * @return
     */
    public Element[] getValuesForSort () {
        int[] keys = keys();
        return  getValuesForSort(keys);
    }

    /**
     * put your documentation comment here
     * @param keys
     * @return
     */
    public Element[] getValuesForSort (int[] keys) {
        Element[] retVal = new Element[keys.length];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = new Element(getObject(keys[i]), keys[i], this.isValueMissing(keys[i]),
                this.isValueEmpty(keys[i]), this.isValueDefault(keys[i]), this.doesValueExist(keys[i]));
        }       //for
        return  retVal;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public VIntIntHashMap getSortedOrder () {
        return  getSortedOrder(keys());
    }


    /**
      * returns an int array with valid indices from this column, such that
      * values in return_value[i] < value in return_value[i+1]
      * @return int[] the order to sort this column by.
      * @author Vered Goren - this method is used by sparse mutable table in sparse2
      */
     public int[] getColumnSortedOrder () {
         int[] _keys = keys();
         int[] retVal = new int[_keys.length];
            Element[] values = getValuesForSort(_keys);
            Arrays.sort(values, new ObjectComparator());
            for (int i = 0; i < values.length; i++) {
                retVal[i] = values[i].getIndex();
            }
            return  retVal;

     }

     /**
      * returns an array of integers that are valid indices in the range [begin, end]
      * in this column, such that value at return_value[i] is smaller than return_value[i+1]
      * @param begin int row index to begin sorting
      * @param end int row index to end sorting
      * @return int[] valid indices in the range [begin, end]
      * in this column, such that value at return_value[i] is smaller than return_value[i+1]
      * @author Vered Goren - this method is used by sparse mutable table in sparse2
      */
     public int[] getColumnSortedOrder (int begin, int end) {


         int[] keys = VHashService.getIndicesInRange(begin, end, getElements());
         int[] retVal = new int[keys.length];
            Element[] values = getValuesForSort(keys);
            Arrays.sort(values, new ObjectComparator());
            for (int i = 0; i < values.length; i++) {
                retVal[i] = values[i].getIndex();
            }
            return  retVal;

     }

     /**
      * returns an int array with values from rows[begin] through rows[end] (including)
      * such that value at row retruned_value[i] is less than or equal to value
      * at row number returned_value[i+1]
      * @param begin int beginning offset for values in rows
      * @param end int end offset for values in rows
      * @return int[] contains values from rows[begin] through rows[end] (including)
      * such that value at row retruned_value[i] is less than or equal to value
      * at row number returned_value[i+1]

      */
     public int[] getColumnSortedOrder (int[] rows, int begin, int end) {
        int[] keys = new int[end - begin + 1];
        for(int i=0; i<keys.length; i++){
          keys[0] = rows[begin+i];
        }
        int[] retVal = new int[keys.length];
           Element[] values = getValuesForSort(keys);
           Arrays.sort(values, new ObjectComparator());
           for (int i = 0; i < values.length; i++) {
               retVal[i] = values[i].getIndex();
           }
           return  retVal;

    }


     /**
      * returns an int array with values from rows in a new order
      * such that element in row number returned_value[i] is less than
      * or equal to element in row number returned_value[i+1]
      * @param validRows int[] indices in this columns
      * @return int[] contains values from rows in a new order
      * such that element in row number returned_value[i] is less than
      */
     public int[] getColumnSortedOrder(int[] rows) {

       int[] retVal = new int[rows.length];
       Element[] values = getValuesForSort(rows);
       Arrays.sort(values, new ObjectComparator());
       for (int i = 0; i < values.length; i++) {
         retVal[i] = values[i].getIndex();
       }
       return retVal;

     }




    /**
     * put your documentation comment here
     * @param validRows
     * @return
     */
    public VIntIntHashMap getSortedOrder (int[] validRows) {
        VIntIntHashMap retVal = new VIntIntHashMap(validRows.length);
        Element[] values = getValuesForSort(validRows);
        Arrays.sort(validRows);
        Arrays.sort(values, new ObjectComparator());
        for (int i = 0; i < values.length; i++) {
            retVal.put(validRows[i], values[i].getIndex());
        }
        return  retVal;
    }

    /**
     * put your documentation comment here
     * @param begin
     * @param end
     * @return
     */
    public VIntIntHashMap getSortedOrder (int begin, int end) {
        int[] keys = VHashService.getIndicesInRange(begin, end, getElements());
        return  getSortedOrder(keys);
    }

    /**
     * put your documentation comment here
     */
    public void sort () {
        VIntIntHashMap order = getSortedOrder();
        setElements(getElements().reorder(order));
        missing = missing.reorder(order);
        empty = empty.reorder(order);
    }

    public VIntHashSet getMissing() {
      return missing;
    }

}               //AbstractSparseColumn



