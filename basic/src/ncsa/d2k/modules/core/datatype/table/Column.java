/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.datatype.table;

import java.io.Serializable;


/**
 * <p><code>Column</code> is an ordered list, generally associated with a <code>
 * Table</code>. There are many implementations of <code>Column</code> optimized
 * for different sets of tasks.</p>
 *
 * <p><code>Column</code> defines methods that are common to all
 * implementations. These include the insertion and deletion of rows and row
 * reordering methods. Several methods are defined to provide metadata about the
 * contents of a <code>Column</code>. Accessor methods are also defined for each
 * primitive data type and for several common <code>Object</code> types used.
 * The <code>Column</code> implementation must provide the necessary datatype
 * conversions.</p>
 *
 * @author  goren
 * @author  clutter
 * @author  $author$
 * @version $Revision$, $Date$
 */
public interface Column extends Serializable {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = 1L;

   //~ Methods *****************************************************************

   /**
    * Appends the new entry to the end of the <code>Column</code>.
    *
    * @param newEntry The new entry
    */
   public void addRow(Object newEntry);

   /**
    * Adds the specified number of blank rows.
    *
    * @param number Number of rows to add.
    */
   public void addRows(int number);

   /**
    * Compares the value of the <code>Object</code> passed in and the element at
    * index <code>pos</code>. Returns 0 if they are the same, greater than zero
    * if <code>element</code> is greater, and less than zero if <code>
    * element</code> is less.
    *
    * @param  element <code>Object</code> to be used for comparison. Should be a
    *                 subclass of this <code>Column</code> implementation
    * @param  pos     Position of the element in the <code>Column</code> to be
    *                 compared with
    *
    * @return Value representing the relationship- >, <, or == 0
    */
   public int compareRows(Object element, int pos);

   /**
    * Compares <code>pos1</code> and <code>pos2</code> positions in the <code>
    * Column</code>. Returns 0 if they are the same, greater than zero if <code>
    * pos1</code> is greater, and less than zero if <code>pos1</code> is less.
    *
    * @param  p1 Position of the first element to compare
    * @param  p2 Position of the second element to compare
    *
    * @return Value representing the relationship- >, <, or == 0
    */
   public int compareRows(int p1, int p2);

   /**
    * Creates a copy of this <code>Column</code>.
    *
    * @return Copy of this <code>Column</code>
    */
   public Column copy();

   /**
    * Compares for equality.
    *
    * @param  col <code>Column</code> to compare
    *
    * @return True if <code>col</code> equals this <code>Column</code>
    */
   public boolean equals(Object col);

   /**
    * Gets the value at <code>pos</code> as a <code>boolean</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>boolean</code>
    */
   public boolean getBoolean(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>byte</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>byte</code>
    */
   public byte getByte(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>byte</code> array.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>byte</code> array
    */
   public byte[] getBytes(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>char</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>char</code>
    */
   public char getChar(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>char</code> array.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>char</code> array
    */
   public char[] getChars(int pos);

   /**
    * Gets the comment associated with this <code>Column</code>.
    *
    * @return Comment that describes this <code>Column</code>
    */
   public String getComment();

   /**
    * Gets the value at <code>pos</code> as a <code>double</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>double</code>
    */
   public double getDouble(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>float</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>float</code>
    */
   public float getFloat(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>int</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>int</code>
    */
   public int getInt(int pos);

   /**
    * Returns the internal representation of the data.
    *
    * @return Internal representation of the data
    */
   public Object getInternal();

   /**
    * Tests if the <code>Column</code> is nominal.
    *
    * @return Whether or not the <code>Column</code> is nominal.
    */
   public boolean getIsNominal();

   /**
    * Tests if the <code>Column</code> is scalar.
    *
    * @return Whether or not the <code>Column</code> is scalar.
    */
   public boolean getIsScalar();

   /**
    * Gets the label associated with this <code>Column</code>.
    *
    * @return Label which describes this <code>Column</code>
    */
   public String getLabel();

   /**
    * Gets the value at <code>pos</code> as a <code>long</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>long</code>
    */
   public long getLong(int pos);

   /**
    * Gets the missing values in a <code>boolean</code> array.
    *
    * @return The missing value flags.
    */
   public boolean[] getMissingValues();

   /**
    * Gets the number of entries this <code>Column</code> holds. This is the
    * number of non-null entries in the <code>Column</code>.
    *
    * @return This <code>Column</code>'s number of entries
    */
   public int getNumEntries();

   /**
    * Returns the number of missing values in the <code>Column</code>.
    *
    * @return Number of missing values in the <code>Column</code>.
    */
   public int getNumMissingValues();

   /**
    * Gets the number of rows in this <code>Column</code>. Same as <code>
    * getCapacity()</code>.
    *
    * @return Number of rows in this <code>Column</code>.
    */
   public int getNumRows();

   /**
    * Gets the value at <code>pos</code> as a <code>Object</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>Object</code>
    */
   public Object getObject(int pos);

   /**
    * Gets an entry from the <code>Column</code> at the indicated position.
    *
    * @param  pos Position to get the entry from
    *
    * @return The entry at the specified position
    */
   public Object getRow(int pos);

   /**
    * Gets the value at <code>pos</code> as a <code>short</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>short</code>
    */
   public short getShort(int pos);

   /**
    * Gets a <code>String</code> from this <code>Column</code> at <code>
    * pos</code>.
    *
    * @param  pos Position to get the value from
    *
    * @return Value at <code>pos</code> as a <code>String</code>
    */
   public String getString(int pos);

   /**
    * Gets a subset of this <code>Column</code>, given an array of rows.
    *
    * @param  rows Array of rows
    *
    * @return Subset of this <code>Column</code>
    */
   public Column getSubset(int[] rows);

   /**
    * Gets a subset of this <code>Column</code>, given a start position and
    * length.
    *
    * @param  pos Start position for the subset
    * @param  len Length of the subset
    *
    * @return Subset of this <code>Column</code>
    */
   public Column getSubset(int pos, int len);

   /**
    * Gets the type of this <code>Column</code>, enumerated in <code>
    * ColumnTypes</code>.
    *
    * @return Type of this <code>Column</code>
    */
   public int getType();

   /**
    * Tests if there are any missing values in the <code>Column</code>.
    *
    * @return True if there are any missing values in the column
    */
   public boolean hasMissingValues();

   /**
    * Inserts a new row into this <code>Column</code>.
    *
    * @param newEntry Object to insert
    * @param pos      Position to insert the new row
    */
   public void insertRow(Object newEntry, int pos);

   /**
    * Returns <code>true</code> if the value at <code>row</code> is empty,
    * <code>false</code> otherwise.
    *
    * @param  row The row index
    *
    * @return True if the value at row is empty, false otherwise
    */
   public boolean isValueEmpty(int row);

   /**
    * Returns <code>true</code> if the value at <code>row</code> is missing,
    * <code>false</code> otherwise.
    *
    * @param  row The row index
    *
    * @return Whether or not the value at <code>row</code> is missing
    */
   public boolean isValueMissing(int row);

   /**
    * Removes an entry from the <code>Column</code>, at <code>pos</code>.
    *
    * @param  row Position to remove
    *
    * @return Entry that was removed
    */
   public Object removeRow(int row);

   /**
    * Removes a range of rows from this <code>Column</code>.
    *
    * @param start Start position
    * @param len   Number to remove
    */
   public void removeRows(int start, int len);

   /**
    * Given an array of booleans, will remove the positions in the <code>
    * Column</code> which correspond to the positions in the boolean array which
    * are marked true.
    *
    * @param flags The boolean array of remove flags
    */
   public void removeRowsByFlag(boolean[] flags);

   /**
    * Given an array of <code>int</code>s, removes the positions in the <code>
    * Column</code> which are indicated by the <code>int</code>s in the array.
    *
    * @param indices The <code>int</code> array of remove indices
    */
   public void removeRowsByIndex(int[] indices);

   /**
    * Gets a copy of this <code>Column</code> reordered based on the input array
    * of indexes. Does not overwrite this <code>Column</code>.
    *
    * @param  newOrder An array of indices indicating a new order
    *
    * @return Copy of this <code>Column</code> with the rows re-ordered
    */
   public Column reorderRows(int[] newOrder);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBoolean(boolean newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setByte(byte newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setBytes(byte[] newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChar(char newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setChars(char[] newEntry, int pos);

   /**
    * Sets the comment associated with this <code>Column</code>.
    *
    * @param comment Comment that describes this <code>Column</code>
    */
   public void setComment(String comment);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setDouble(double newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setFloat(float newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setInt(int newEntry, int pos);

   /**
    * Sets whether this <code>Column</code> is nominal or not.
    *
    * @param value Whether this <code>Column</code> is nominal or not
    */
   public void setIsNominal(boolean value);

   /**
    * Sets whether this <code>Column</code> is scalar or not.
    *
    * @param value Whether this <code>Column</code> is scalar or not
    */
   public void setIsScalar(boolean value);

   /**
    * Sets the label associated with this <code>Column</code> .
    *
    * @param labl Label that describes this <code>Column</code>
    */
   public void setLabel(String labl);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setLong(long newEntry, int pos);

   /**
    * Sets missing values to the array passed in.
    *
    * @param miss Array of missing value flags.
    */
   public void setMissingValues(boolean[] miss);

   /**
    * Sets the number of rows for this <code>Column</code>. The capacity is its
    * potential maximum number of entries. If numEntries is greater than
    * newCapacity then the <code>Column</code> will be truncated.
    *
    * @param newCapacity a new capacity
    */
   public void setNumRows(int newCapacity);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setObject(Object newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setRow(Object newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setShort(short newEntry, int pos);

   /**
    * Sets the value at <code>pos</code> to be <code>newEntry</code>.
    *
    * @param newEntry The new item
    * @param pos      The position
    */
   public void setString(String newEntry, int pos);

   /**
    * Sets the value at <code>row</code> to be empty.
    *
    * @param b   Empty value
    * @param row Row index to mark as empty
    */
   public void setValueToEmpty(boolean b, int row);

   /**
    * Sets the value at <code>row</code> to be missing.
    *
    * @param b   Empty value
    * @param row Row index to mark as missing
    */
   public void setValueToMissing(boolean b, int row);

   /**
    * Sorts the elements in this <code>Column</code>.
    */
   public void sort();

   /**
    * Swaps the positions of two rows.
    *
    * @param pos1 First row to swap
    * @param pos2 Second row to swap
    */
   public void swapRows(int pos1, int pos2);

} // end interface Column
