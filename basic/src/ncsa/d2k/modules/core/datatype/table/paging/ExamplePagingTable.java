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
package ncsa.d2k.modules.core.datatype.table.paging;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;

import java.io.IOException;


/**
 * Description of class ExamplePagingTable.
 *
 * @author  redman
 * @version $Revision$, $Date$
 */
public class ExamplePagingTable extends SubsetPagingTable
   implements ExampleTable {

   //~ Instance fields *********************************************************

   /** Input columns. */
   Column[] inputColumns = new Column[0];

   /** Indices of the input columns. */
   int[] inputIndices = new int[0];

   /** Output columns themselves. */
   Column[] outputColumns = new Column[0];

   /** Indices of the output columns. */
   int[] outputIndices = new int[0];

   /** Testing set. */
   int[] testSet = new int[0];

   /** Training set. */
   int[] trainSet = new int[0];

   //~ Constructors ************************************************************

   /**
    * Default empty paging table.
    */
   public ExamplePagingTable() {
      Page[] pages = new Page[1];
      int[] offsets = new int[1];
      offsets[0] = 0;

      try {
         pages[0] = new Page(new MutableTableImpl(), true);
      } catch (IOException e) {
         e.printStackTrace();
      }

      cache = new PageCache(pages, offsets, DEFAULT_PAGESIZE);
      this.offset = -1;
   }

   /**
    * Given only the page cache. read first page.
    *
    * @param pager Description of parameter $param.name$.
    */
   public ExamplePagingTable(PageCache pager) {
      this.cache = pager;
      this.offset = -1;
      this.getPage(0);
   }

   //~ Methods *****************************************************************

   /**
    * Update the input columns array when we page in or change which features
    * are outputs.
    */
   private void updateInputFeatures() {

      // reallocate the array if needed.
      if (inputColumns.length != inputIndices.length) {
         inputColumns = new Column[inputIndices.length];
      }

      // if we have a page, update the column array.
      if (currentPage != null) {

         // we have a page, update the input columns.
         for (int i = 0; i < inputIndices.length; i++) {
            inputColumns[i] = table.getColumn(inputIndices[i]);
         }
      }

   }

   /**
    * Update the output columns array when we page in or change which features
    * are outputs.
    */
   private void updateOutputFeatures() {

      if (outputColumns.length != outputIndices.length) {
         outputColumns = new Column[outputIndices.length];
      }

      if (currentPage != null) {

         // we have a page, update the input columns.
         for (int i = 0; i < outputIndices.length; i++) {
            outputColumns[i] = table.getColumn(outputIndices[i]);
         }
      }

   }

   /**
    * Grab the next page and init the columns.
    *
    * @param where the row we are to access next.
    */
   protected void getPage(int where) {
      currentPage = cache.getPageAt(where, offset);
      this.table = currentPage.getTable();
      this.subset = currentPage.getSubset();
      this.offset = cache.getOffsetAt(where);
      columns = ((TableImpl) table).getRawColumns();
      this.updateInputFeatures();
      this.updateOutputFeatures();
   }

   /**
    * Refresh the page we are on now. This is done when the table has changed,
    * and we need to update our fields.
    */
   protected void refresh() {

      if (currentPage == null) {
         this.getPage(0);
      }

      this.table = currentPage.getTable();
      this.subset = currentPage.getSubset();
      this.offset = cache.getOffsetAt(offset);
      columns = ((TableImpl) table).getRawColumns();
      this.updateInputFeatures();
      this.updateOutputFeatures();
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public boolean getInputBoolean(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getBoolean(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getBoolean(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public byte getInputByte(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getByte(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getByte(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public byte[] getInputBytes(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getBytes(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getBytes(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public char getInputChar(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getChar(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getChar(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public char[] getInputChars(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getChars(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getChars(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public double getInputDouble(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getDouble(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getDouble(where);
      }
   }

   /**
    * The array of the indices of the inputs features.
    *
    * @return the array of the indices of the inputs features.
    */
   public int[] getInputFeatures() { return inputIndices; }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public float getInputFloat(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getFloat(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getFloat(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public int getInputInt(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getInt(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getInt(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public long getInputLong(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getLong(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getLong(where);
      }
   }


   /**
    * Description of method getInputName.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public String getInputName(int i) { return null; }

   /**
    * return a list of the names of the input columns.
    *
    * @return a list of the names of the input columns.
    */
   public String[] getInputNames() {
      String[] inputNames = new String[inputIndices.length];

      for (int i = 0; i < inputNames.length; i++) {
         inputNames[i] = cache.getColumnLabel(inputIndices[i]);
      }

      return inputNames;
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public Object getInputObject(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getObject(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getObject(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public short getInputShort(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getShort(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getShort(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public String getInputString(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return inputColumns[column].getString(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return inputColumns[column].getString(where);
      }
   }

   /**
    * Get the java data types for the input at i.
    *
    * @param  i the index of the input.
    *
    * @return the data type for the input.
    */
   public int getInputType(int i) { return inputColumns[i].getType(); }

   /**
    * The number of input features.
    *
    * @return the number of input features.
    */
   public int getNumInputFeatures() { return inputIndices.length; }

   /**
    * The number of output features.
    *
    * @return the number of output features.
    */
   public int getNumOutputFeatures() { return outputIndices.length; }

   /**
    * The number of test examples.
    *
    * @return the number of test examples.
    */
   public int getNumTestExamples() { return testSet.length; }

   /**
    * The number of train examples.
    *
    * @return the number of train examples.
    */
   public int getNumTrainExamples() { return trainSet.length; }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public boolean getOutputBoolean(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getBoolean(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getBoolean(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public byte getOutputByte(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getByte(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getByte(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public byte[] getOutputBytes(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getBytes(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getBytes(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public char getOutputChar(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getChar(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getChar(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public char[] getOutputChars(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getChars(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getChars(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public double getOutputDouble(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getDouble(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getDouble(where);
      }
   }

   /**
    * The array of indices of output features.
    *
    * @return the array of indices of output features.
    */
   public int[] getOutputFeatures() { return outputIndices; }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public float getOutputFloat(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getFloat(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getFloat(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public int getOutputInt(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getInt(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getInt(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public long getOutputLong(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getLong(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getLong(where);
      }
   }

   /**
    * The labels are gotten from the cache.
    *
    * @param  o Description of parameter o.
    *
    * @return the labels are gotten from the cache.
    */
   public String getOutputName(int o) {
      return cache.getColumnLabel(outputIndices[0]);
   }

   /**
    * return an array of strings containing the names of all the output columns.
    *
    * @return an array of strings containing the names of all the output
    *         columns.
    */
   public String[] getOutputNames() {
      String[] outputNames = new String[outputIndices.length];

      for (int i = 0; i < outputNames.length; i++) {
         outputNames[i] = cache.getColumnLabel(outputIndices[i]);
      }

      return outputNames;
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public Object getOutputObject(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getObject(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getObject(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public short getOutputShort(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getShort(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getShort(where);
      }
   }

   /**
    * Get the input double at the given row and column indices, reading a new
    * page if necessary.
    *
    * @param  row    the row index.
    * @param  column the column index
    *
    * @return the double representation of the data.
    */
   public String getOutputString(int row, int column) {
      int where;

      try {
         where = subset[row - offset];

         return outputColumns[column].getString(where);
      } catch (ArrayIndexOutOfBoundsException aioobe) {

         // Get the next page.
         this.getPage(row);
         where = subset[row - offset];

         return outputColumns[column].getString(where);
      }
   }

   /**
    * Get the java data types for the output at i.
    *
    * @param  o the index of the output.
    *
    * @return the data type for the output.
    */
   public int getOutputType(int o) { return outputColumns[0].getType(); }

   /**
    * get a subset of the indexed rows.
    *
    * @param  rows the rows to include.
    *
    * @return get a subset of the indexed rows.
    */
   public Table getSubset(int[] rows) {
      PageCache fudge;

      try {
         fudge = cache.subset(rows);
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }

      return new ExamplePagingTable(fudge);
   }

   /**
    * Return a subset of the data. The paging table returned will actually
    * replicate the data, because the subset array is stored with the data in an
    * out-of-memory page, so we are required to use a different page.
    *
    * @param  start the row index.
    * @param  len   the column index
    *
    * @return the object representation of the data at the given row and column.
    */
   public Table getSubset(int start, int len) {

      try {
         return new ExamplePagingTable(cache.subset(start, len));
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * The indices of the training set.
    *
    * @return the indices of the training set.
    */
   public int[] getTestingSet() { return testSet; }

   /**
    * return the test table for this dataset.
    *
    * @return the test table for this dataset.
    */
   public Table getTestTable() {

      try {
         return new ExamplePagingTable(cache.subset(this.testSet));
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * The indices of the training set.
    *
    * @return the indices of the training set.
    */
   public int[] getTrainingSet() { return trainSet; }

   /**
    * return the train table for this dataset.
    *
    * @return the train table for this dataset.
    */
   public Table getTrainTable() {

      try {
         return new ExamplePagingTable(cache.subset(this.trainSet));
      } catch (IOException e) {
         e.printStackTrace();

         return null;
      }
   }

   /**
    * return true if the table contains any missing values in the inputs or
    * outputs columns.
    *
    * @return true if the table has missing values in the inputs or outputs.
    */
   public boolean hasMissingInputsOutputs() {

      // TODO Auto-generated method stub
      return false;
   }

   /**
    * Return true if the input column is nominal.
    *
    * @param  i Description of parameter i.
    *
    * @return true if the input column is nominal.
    */
   public boolean isInputNominal(int i) {
      return cache.isColumnNominal(inputIndices[i]);
   }

   /**
    * return true if the input column is scalar.
    *
    * @param  i Description of parameter i.
    *
    * @return true if the input column is scalar.
    */
   public boolean isInputScalar(int i) {
      return cache.isColumnScalar(inputIndices[i]);
   }

   /**
    * If the output column is nominal, return true.
    *
    * @param  o Description of parameter o.
    *
    * @return true if the output column is nominal.
    */
   public boolean isOutputNominal(int o) {
      return cache.isColumnNominal(outputIndices[o]);
   }

   /**
    * return true if the output column is scalar.
    *
    * @param  o Description of parameter o.
    *
    * @return true if the oiutput column is scalar.
    */
   public boolean isOutputScalar(int o) {
      return cache.isColumnScalar(outputIndices[o]);
   }

   /**
    * When we set the array of input features, we will also need to set the
    * input column array up.
    *
    * @param inputs the new inputs.
    */
   public void setInputFeatures(int[] inputs) {
      inputIndices = inputs;
      inputColumns = new Column[inputs.length];

      if (currentPage != null) {

         // we have a page, update the input columns.
         for (int i = 0; i < inputs.length; i++) {
            inputColumns[i] = table.getColumn(inputs[i]);
         }
      }
   }

   /**
    * When we set the array of output features, we will also need to set the
    * output column array up.
    *
    * @param outs the new inputs.
    */
   public void setOutputFeatures(int[] outs) {
      outputIndices = outs;
      this.updateOutputFeatures();
   }

   /**
    * set the indices of the training set.
    *
    * @param trainingSet Description of parameter trainingSet.
    */
   public void setTestingSet(int[] trainingSet) { this.testSet = trainingSet; }

   /**
    * set the indices of the training set.
    *
    * @param trainingSet Description of parameter trainingSet.
    */
   public void setTrainingSet(int[] trainingSet) {
      this.trainSet = trainingSet;
   }

   /**
    * make a copy of the tables internal data structures but not the data, or in
    * this case the pages. Return a reference to the new table structure.
    *
    * @return make a copy of the tables internal data structures but not the
    *         data, or in this case the pages. Return a reference to the new
    *         table structure.
    */
   public Table shallowCopy() {
      ExamplePagingTable ept = new ExamplePagingTable(cache);
      ept.outputIndices = this.outputIndices;
      ept.outputColumns = this.outputColumns;
      ept.inputIndices = this.inputIndices;
      ept.inputColumns = this.inputColumns;
      ept.trainSet = this.trainSet;
      ept.testSet = this.testSet;

      return ept;
   }

   /**
    * return the prediction table for this dataset.
    *
    * @return the prediction table for this dataset.
    *
    * @throws RuntimeException Description of exception RuntimeException.
    */
   public PredictionTable toPredictionTable() {

      try {
         return new PredictionPagingTable(this);
      } catch (Exception e) {
         throw new RuntimeException("There was a problem with the disk IO system, and " +
                                    "the paging prediction table could not be created. Check disk space.");
      }
   }

} // end class ExamplePagingTable
