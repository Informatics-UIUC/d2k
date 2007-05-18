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
package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Row;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.io.Serializable;
import ncsa.d2k.modules.core.util.*;


/**
 * The ParameterPoint object can extend ExampleImpl. It will be input to the
 * learning algorithm. It should use the same column names and column order as
 * the ParameterSpace implementation. Note: It is important that this be an
 * Example so that additional layers (optimizing the optimizer etc..) can more
 * easily be implemented.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ParameterPointImpl extends ExampleImpl implements Serializable,
                                                               ParameterPoint {

   //~ Instance fields *********************************************************

   /** default missing string. */
   protected boolean defaultMissingBoolean = false;

   /** default missing string. */
   protected byte defaultMissingByte = (byte) '\000';

   /** default missing string. */
   protected byte[] defaultMissingByteArray = { (byte) '\000' };

   /** default missing string. */
   protected char defaultMissingChar = '\000';

   /** default missing string. */
   protected char[] defaultMissingCharArray = { '\000' };

   /** default for float double and extended. */
   protected double defaultMissingDouble = 0.0;

   /** this is the missing value for longs, ints, and shorts. */
   protected int defaultMissingInt = 0;

   /** default missing string. */
   protected String defaultMissingString = "?";

   //~ Constructors ************************************************************

   /**
    * Creates a new ParameterPointImpl object.
    *
    * @param et ExampleTable.
    */
   public ParameterPointImpl(ExampleTableImpl et) { super(et); }

   //~ Methods *****************************************************************

   /**
    * return a parameter point from the given arrays.
    *
    * @param  names  Names.
    * @param  values Values.
    *
    * @return A parameter point from the given arrays.
    */
   static public final ParameterPoint getParameterPoint(String[] names,
                                                        double[] values) {
      int numColumns = names.length;
      Column[] cols = new Column[values.length];

      for (int i = 0; i < values.length; i++) {
         double[] vals = new double[1];
         vals[0] = values[i];

         DoubleColumn dc = new DoubleColumn(vals);
         dc.setLabel(names[i]);
         cols[i] = dc;
      }

      ExampleTableImpl eti = new ExampleTableImpl();
      eti.addColumns(cols);

      int[] ins = new int[numColumns];

      for (int i = 0; i < numColumns; i++) {
         ins[i] = i;
      }

      eti.setInputFeatures(ins);

      return new ParameterPointImpl(eti);
   }

   /**
    * Create a copy of this Table. This is a deep copy, and it contains a copy
    * of all the data.
    *
    * @return A copy of this Table
    */
   public Table copy() { return this.getTable().copy(); }

   /**
    * Create a copy of this Table. This is a deep copy, and it contains a copy
    * of all the data.
    *
    * @param  rows Rows to copy.
    *
    * @return A copy of this Table
    */
   public Table copy(int[] rows) { return this.getTable().copy(rows); }

   /**
    * Create a copy of this Table. This is a deep copy, and it contains a copy
    * of all the data.
    *
    * @param  start Start row.
    * @param  len   Number to copy.
    *
    * @return A copy of this Table
    */
   public Table copy(int start, int len) {
      return this.getTable().copy(start, len);
   }

   /**
    * Create from data.
    *
    * @param  names  Array of names.
    * @param  values Array of values.
    *
    * @return A ParameterPoint.
    */
   public ParameterPoint createFromData(String[] names, double[] values) {
      int numColumns = names.length;
      Column[] cols = new Column[values.length];

      for (int i = 0; i < values.length; i++) {
         double[] vals = new double[1];
         vals[0] = values[i];

         DoubleColumn dc = new DoubleColumn(vals);
         dc.setLabel(names[i]);
         cols[i] = dc;
      }

      ExampleTableImpl eti = new ExampleTableImpl();

      return new ParameterPointImpl(eti);
   }

   /**
    * This method is expected to get an MutableTableImpl.
    *
    * @param  mt MutableTable.
    *
    * @return ParameterPoint.
    */
   public ParameterPoint createFromTable(MutableTable mt) {
      return new ParameterPointImpl((ExampleTableImpl) mt);
   }

   /**
    * Return a new table.
    *
    * @return A new table.
    */
   public MutableTable createTable() { return new MutableTableImpl(); }

   /**
    * Get a boolean value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The boolean value at (row, column)
    */
   public boolean getBoolean(int row, int column) {
      return this.getTable().getBoolean(row, column);
   }

   /**
    * Get a byte value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The byte value at (row, column)
    */
   public byte getByte(int row, int column) {
      return this.getTable().getByte(row, column);
   }

   /**
    * Get a value from the table as an array of bytes.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The value at (row, column) as an array of bytes
    */
   public byte[] getBytes(int row, int column) {
      return this.getTable().getBytes(row, column);
   }

   /**
    * Get a char value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The char value at (row, column)
    */
   public char getChar(int row, int column) {
      return this.getTable().getChar(row, column);
   }

   /**
    * Get a value from the table as an array of chars.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The value at (row, column) as an array of chars
    */
   public char[] getChars(int row, int column) {
      return this.getTable().getChars(row, column);
   }

   /**
    * Return a column representing the data in column n.
    *
    * @param  n The column to get.
    *
    * @return A column representing the data.
    */
   public Column getColumn(int n) { return this.getTable().getColumn(n); }

   /**
    * Returns the comment associated with the column.
    *
    * @param  position The index of the Column name to get.
    *
    * @return The comment associated with the column.
    */
   public String getColumnComment(int position) {
      return this.getTable().getColumnComment(position);
   }

   //////////////////////////////////////
   //// Accessing Table Metadata

   /**
    * Returns the name associated with the column.
    *
    * @param  position The index of the Column name to get.
    *
    * @return The name associated with the column.
    */
   public String getColumnLabel(int position) {
      return this.getTable().getColumnLabel(position);
   }

   /**
    * Return the type of column located at the given position.
    *
    * @param  position The index of the column
    *
    * @return The column type
    *
    * @see    ColumnTypes
    */
   public int getColumnType(int position) {
      return this.getTable().getColumnType(position);
   }

   /**
    * Get the comment associated with this Table.
    *
    * @return The comment which describes this Table
    */
   public String getComment() { return this.getTable().getComment(); }

   /**
    * Get a double value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The double at (row, column)
    */
   public double getDouble(int row, int column) {
      return this.getTable().getDouble(row, column);
   }

   /**
    * Get a float value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return the float at (row, column)
    */
   public float getFloat(int row, int column) {
      return this.getTable().getFloat(row, column);
   }

   /**
    * Get an int value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The int at (row, column)
    */
   public int getInt(int row, int column) {
      return this.getTable().getInt(row, column);
   }

   /**
    * Get the label associated with this Table.
    *
    * @return The label which describes this Table
    */
   public String getLabel() { return this.getTable().getLabel(); }

   /**
    * Get a long value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The long at (row, column)
    */
   public long getLong(int row, int column) {
      return this.getTable().getLong(row, column);
   }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public boolean getMissingBoolean() { return defaultMissingBoolean; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public byte getMissingByte() { return defaultMissingByte; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public byte[] getMissingBytes() { return this.defaultMissingByteArray; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public char getMissingChar() { return this.defaultMissingChar; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public char[] getMissingChars() { return this.defaultMissingCharArray; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public double getMissingDouble() { return this.defaultMissingDouble; }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @return The integer for missing value.
    */
   public int getMissingInt() { return defaultMissingInt; }

   /**
    * Return the default missing value for doubles, floats and extendeds.
    *
    * @return The double for missing value.
    */
   public String getMissingString() { return this.defaultMissingString; }

   /**
    * Get the name of a parameter.
    *
    * @param  parameterIndex The index of the parameter of interest.
    *
    * @return A string value representing the name of the parameter.
    */
   public String getName(int parameterIndex) {
      return this.getColumnLabel(parameterIndex);
   }

   /**
    * Return the number of columns this table holds.
    *
    * @return The number of columns in this table
    */
   public int getNumColumns() { return this.getTable().getNumColumns(); }


   /**
    * Get the number of parameters that define the space.
    *
    * @return An int value representing the minimum possible value of the
    *         parameter.
    */
   public int getNumParameters() { return this.getNumColumns(); }

   /**
    * Get the number of rows in this Table. Same as getCapacity().
    *
    * @return The number of rows in this Table.
    */
   public int getNumRows() { return this.getTable().getNumRows(); }

   /**
    * Get an Object from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The Object at (row, column)
    */
   public Object getObject(int row, int column) {
      return this.getTable().getObject(row, column);
   }

   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   /**
    * Get the parameter index of that corresponds to the given name.
    *
    * @param  name The name.
    *
    * @return An integer representing the index of the parameters.
    *
    * @throws Exception If name is not found.
    */
   public int getParameterIndex(String name) throws Exception {

      for (int i = 0; i < getNumParameters(); i++) {

         if (getName(i).equals(name)) {
            return i;
         }
      }

      Exception e = new Exception();
      myLogger.setErrorLoggingLevel();
      myLogger.error("Error!  Can not find name (" + name + ").  ");
      myLogger.resetLoggingLevel();
      throw e;
   }

   /**
    * This method will return a Row object. The row object can be used over and
    * over to access the rows of the table by setting it's index to access a
    * particular row.
    *
    * @return A Row object that can access the rows of the table.
    */
   public Row getRow() { return this; }

   /**
    * Get a short value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The short at (row, column)
    */
   public short getShort(int row, int column) {
      return this.getTable().getShort(row, column);
   }

   /**
    * Get a String value from the table.
    *
    * @param  row    The row of the table
    * @param  column The column of the table
    *
    * @return The String at (row, column)
    */
   public String getString(int row, int column) {
      return this.getTable().getString(row, column);
   }

   /**
    * Get a subset of the table consisting of the rows identified by the array
    * of indices passed in.
    *
    * @param  rows The rows to be in the subset.
    *
    * @return Get a subset of the table consisting of the rows identified by the
    *         array of indices passed in.
    */
   public Table getSubset(int[] rows) {
      return this.getTable().getSubset(rows);
   }

   /**
    * Get a subset of this Table, given a start position and length. The subset
    * will be a new Table.
    *
    * @param  start The start position for the subset
    * @param  len   The length of the subset
    *
    * @return A subset of this Table
    */
   public Table getSubset(int start, int len) {
      return this.getTable().getSubset(start, len);
   }

   /**
    * Get a TableFactory.
    *
    * @return A TableFactory.
    */
   public TableFactory getTableFactory() {
      return this.getTable().getTableFactory();
   }

   /**
    * Get the value of a parameter.
    *
    * @param  parameterIndex The index of the parameter of interest.
    *
    * @return A double value representing the minimum possible value of the
    *         parameter.
    */
   public double getValue(int parameterIndex) {
      return this.getDouble(0, parameterIndex);
   }

   /**
    * Get the value of a parameter.
    *
    * @param  name A string which names the parameter of interest.
    *
    * @return A double value representing the minimum possible value of the
    *         parameter.
    *
    * @throws Exception If exception occurs, exception is thrown.
    */
   public double getValue(String name) throws Exception {
      return getValue(getParameterIndex(name));
   }

   /**
    * Return true if any value in this Table is missing.
    *
    * @return true if there are any missing values, false if there are no
    *         missing values
    */
   public boolean hasMissingValues() {
      return this.getTable().hasMissingValues();
   }

   /* (non-Javadoc)
    * @see ncsa.d2k.modules.core.datatype.table.Table#hasMissingValues(int)
    */
   public boolean hasMissingValues(int columnIndex) {
      return this.getColumn(columnIndex).hasMissingValues();
   }

   /**
    * Returns true if the column at position contains nominal data, false
    * otherwise.
    *
    * @param  position The index of the column
    *
    * @return true if the column contains nominal data, false otherwise.
    */
   public boolean isColumnNominal(int position) {
      return this.getTable().isColumnNominal(position);
   }

   /**
    * Returns true if the column at position contains only numeric values, false
    * otherwise.
    *
    * @param  position The index of the column
    *
    * @return true if the column contains only numeric values, false otherwise
    */
   public boolean isColumnNumeric(int position) {
      return this.getTable().isColumnNumeric(position);
   }

   /**
    * Returns true if the column at position contains scalar data, false
    * otherwise.
    *
    * @param  position Position of the column.
    *
    * @return true if the column contains scalar data, false otherwise
    */
   public boolean isColumnScalar(int position) {
      return this.getTable().isColumnScalar(position);
   }

   /**
    * Return true if the value at (row, col) is an empty value, false otherwise.
    *
    * @param  row The row index
    * @param  col The column index
    *
    * @return true if the value is empty, false otherwise
    */
   public boolean isValueEmpty(int row, int col) {
      return this.getTable().isValueEmpty(row, col);
   }

   /**
    * Return true if the value at (row, col) is a missing value, false
    * otherwise.
    *
    * @param  row The row index
    * @param  col The column index
    *
    * @return true if the value is missing, false otherwise
    */
   public boolean isValueMissing(int row, int col) {
      return this.getTable().isValueMissing(row, col);
   }

   /**
    * Get the parameter index of that corresponds to the given name. Not
    * implemented.
    *
    * @param  point      Parameter Point of interest.
    * @param  splitIndex Value of splitIndex to use.
    *
    * @return an integer representing the index of the parameters. (Always null)
    */
   public ParameterPoint[] segmentPoint(ParameterPoint point, int splitIndex) {
      return null;
   }

   /**
    * Set whether the column at position contains nominal data or not.
    *
    * @param value    true if the column at position holds nominal data, false
    *                 otherwise
    * @param position The index of the column
    */
   public void setColumnIsNominal(boolean value, int position) {
      this.getTable().setColumnIsNominal(value, position);
   }

   /**
    * Set whether the column at position contains scalar data or not.
    *
    * @param value    true if the column at position holds scalar data, false
    *                 otherwise
    * @param position The index of the column
    */
   public void setColumnIsScalar(boolean value, int position) {
      this.getTable().setColumnIsScalar(value, position);
   }

   /**
    * Set the comment associated with this Table.
    *
    * @param comment The comment which describes this Table
    */
   public void setComment(String comment) {
      this.getTable().setComment(comment);
   }

   /**
    * Set the label associated with this Table.
    *
    * @param labl The label which describes this Table
    */
   public void setLabel(String labl) { this.getTable().setLabel(labl); }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingBoolean Integer for missing values.
    */
   public void setMissingBoolean(boolean newMissingBoolean) {
      this.defaultMissingBoolean = newMissingBoolean;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingByte Values for missing values.
    */
   public void setMissingByte(byte newMissingByte) {
      this.defaultMissingByte = newMissingByte;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingBytes Values for missing values.
    */
   public void setMissingBytes(byte[] newMissingBytes) {
      this.defaultMissingByteArray = newMissingBytes;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingChar Values for missing values.
    */
   public void setMissingChar(char newMissingChar) {
      this.defaultMissingChar = newMissingChar;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingChars Values for missing values.
    */
   public void setMissingChars(char[] newMissingChars) {
      this.defaultMissingCharArray = newMissingChars;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingDouble Values for missing values.
    */
   public void setMissingDouble(double newMissingDouble) {
      this.defaultMissingDouble = newMissingDouble;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingInt Values for missing values.
    */
   public void setMissingInt(int newMissingInt) {
      this.defaultMissingInt = newMissingInt;
   }

   /**
    * Return the default missing value for integers, both short, int and long.
    *
    * @param newMissingString Values for missing values.
    */
   public void setMissingString(String newMissingString) {
      this.defaultMissingString = newMissingString;
   }

   /**
    * Create a copy of this Table. A copy of every field in the class should be
    * made, but the data itself should not be copied.
    *
    * @return A shallow copy of this Table
    */
   public Table shallowCopy() { return this.getTable().shallowCopy(); }

   /**
    * Return this Table as an ExampleTable.
    *
    * @return This object as an ExampleTable
    */
   public ExampleTable toExampleTable() {
      return this.getTable().toExampleTable();
   }

   /**
    * Create comma delimited string.
    *
    * @return The string.
    */
   public String toString() {
      StringBuffer sb = new StringBuffer(1024);

      for (int i = 0; i < this.getNumParameters(); i++) {

         if (i > 0) {
            sb.append(',');
         }

         sb.append(this.getValue(i));
      }

      return sb.toString();
   }

} /* ParameterPoint */
