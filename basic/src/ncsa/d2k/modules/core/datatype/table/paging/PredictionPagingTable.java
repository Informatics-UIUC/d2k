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
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.io.IOException;


/**
 * <p>The paging prediction table will include and example table, and it will
 * itself be another paging table which only contains the prediction columns.
 * </p>
 *
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class PredictionPagingTable extends ExamplePagingTable
   implements PredictionTable {

   //~ Instance fields *********************************************************

   /** The wrapped prediction table */
   SubsetPagingTable predictionTable;

   //~ Constructors ************************************************************

   /**
    * Constructs a <code>PredictionPagingTable</code> from an example table.
    *
    * @param  et the example table.
    *
    * @throws IOException Description of exception IOException.
    */
   PredictionPagingTable(ExamplePagingTable et) throws IOException {
      super(et.cache);
      this.outputIndices = et.outputIndices;
      this.outputColumns = et.outputColumns;
      this.inputIndices = et.inputIndices;
      this.inputColumns = et.inputColumns;
      this.trainSet = et.trainSet;
      this.testSet = et.testSet;
      this.subset = et.subset;

      // Construct the prediction table.
      int numPredictions = et.getNumOutputFeatures();
      int numRows = et.getNumRows();
      int pagesPerRow = et.cache.defaultPageSize;
      int numTables = numRows / pagesPerRow;

      if (numTables != (numRows * pagesPerRow)) {
         numTables++;
      }

      // These pages comprise the prediction table.
      Page[] pages = new Page[numTables];
      int[] offset = new int[numTables];

      for (int whichTable = 0; whichTable < numTables; whichTable++) {
         Column[] cols = new Column[numPredictions];

         for (int i = 0; i < numPredictions; i++) {
            cols[i] =
               ColumnUtilities.createColumn(et.outputColumns[i].getType(),
                                            et.getNumRows());
         }

         MutableTableImpl mti = new MutableTableImpl(cols);
         pages[whichTable] = new Page(mti, false);
         offset[whichTable] = whichTable * pagesPerRow;
      }

      // Create a paging table, check it's performance.
      predictionTable =
         new SubsetPagingTable(new PageCache(pages, offset, pagesPerRow));
   }

   //~ Methods *****************************************************************

   /**
    * Gets the boolean representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx
    */
   public boolean getBooleanPrediction(int row, int predictionColIdx) {
      return predictionTable.getBoolean(row, predictionColIdx);
   }

   /**
    * Gets the byte representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public byte getBytePrediction(int row, int predictionColIdx) {
      return predictionTable.getByte(row, predictionColIdx);
   }

   /**
    * Gets the byte array representation of the value at the given row and
    * column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public byte[] getBytesPrediction(int row, int predictionColIdx) {
      return predictionTable.getBytes(row, predictionColIdx);
   }

   /**
    * Gets the char representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public char getCharPrediction(int row, int predictionColIdx) {
      return predictionTable.getChar(row, predictionColIdx);
   }

   /**
    * Gets the char array representation of the value at the given row and
    * column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public char[] getCharsPrediction(int row, int predictionColIdx) {
      return predictionTable.getChars(row, predictionColIdx);
   }

   /**
    * Gets the double representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public double getDoublePrediction(int row, int predictionColIdx) {
      return predictionTable.getDouble(row, predictionColIdx);
   }

   /**
    * Gets the float representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public float getFloatPrediction(int row, int predictionColIdx) {
      return predictionTable.getFloat(row, predictionColIdx);
   }

   /**
    * Gets the int representation of the value at the given row and column.
    *
    * @param  row              the index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public int getIntPrediction(int row, int predictionColIdx) {
      return predictionTable.getInt(row, predictionColIdx);
   }

   /**
    * Gets the long representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public long getLongPrediction(int row, int predictionColIdx) {
      return predictionTable.getLong(row, predictionColIdx);
   }

   /**
    * Gets the object representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public Object getObjectPrediction(int row, int predictionColIdx) {
      return predictionTable.getObject(row, predictionColIdx);
   }


   /**
    * Currently only returns null.
    *
    * @return Null
    */
   public int[] getPredictionSet() { return null; }

   /**
    * Gets the short representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found.
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public short getShortPrediction(int row, int predictionColIdx) {
      return predictionTable.getShort(row, predictionColIdx);
   }

   /**
    * Gets the string representation of the value at the given row and column.
    *
    * @param  row              Index of the row where the prediction can be
    *                          found.
    * @param  predictionColIdx The index of the prediction column
    *
    * @return Value at row and predictionColIdx.
    */
   public String getStringPrediction(int row, int predictionColIdx) {
      return predictionTable.getString(row, predictionColIdx);
   }

   /**
    * Sets the boolean prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setBooleanPrediction(boolean prediction, int row,
                                    int predictionColIdx) {
      predictionTable.setBoolean(prediction, row, predictionColIdx);
   }

   /**
    * Sets the byte prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setBytePrediction(byte prediction, int row,
                                 int predictionColIdx) {
      predictionTable.setByte(prediction, row, predictionColIdx);
   }

   /**
    * Sets the bytes prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setBytesPrediction(byte[] prediction, int row,
                                  int predictionColIdx) {
      predictionTable.setBytes(prediction, row, predictionColIdx);
   }

   /**
    * Sets the char prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setCharPrediction(char prediction, int row,
                                 int predictionColIdx) {
      predictionTable.setChar(prediction, row, predictionColIdx);
   }

   /**
    * Sets the chars prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setCharsPrediction(char[] prediction, int row,
                                  int predictionColIdx) {
      predictionTable.setChars(prediction, row, predictionColIdx);
   }

   /**
    * Sets the double prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setDoublePrediction(double prediction, int row,
                                   int predictionColIdx) {
      predictionTable.setDouble(prediction, row, predictionColIdx);
   }

   /**
    * Sets the float prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setFloatPrediction(float prediction, int row,
                                  int predictionColIdx) {
      predictionTable.setFloat(prediction, row, predictionColIdx);
   }

   /**
    * Sets the int prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setIntPrediction(int prediction, int row, int predictionColIdx) {
      predictionTable.setInt(prediction, row, predictionColIdx);
   }

   /**
    * Sets the long prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setLongPrediction(long prediction, int row,
                                 int predictionColIdx) {
      predictionTable.setLong(prediction, row, predictionColIdx);
   }

   /**
    * Sets the Object prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setObjectPrediction(Object prediction, int row,
                                   int predictionColIdx) {
      predictionTable.setObject(prediction, row, predictionColIdx);
   }

   /**
    * You cannot set the prediction set in a paging prediction table, we will
    * throw a <code>RuntimeException</code> to prove it.
    *
    * @param  p Prediction set you cannot set
    *
    * @throws RuntimeException Will be thrown
    */
   public void setPredictionSet(int[] p) {
      throw new RuntimeException("You can not set the prediction set in a paging prediction table.");
   }

   /**
    * Sets the short prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setShortPrediction(short prediction, int row,
                                  int predictionColIdx) {
      predictionTable.setShort(prediction, row, predictionColIdx);
   }

   /**
    * Sets the string prediction.
    *
    * @param prediction       The value
    * @param row              The row to set
    * @param predictionColIdx Index of the prediction column
    */
   public void setStringPrediction(String prediction, int row,
                                   int predictionColIdx) {
      predictionTable.setString(prediction, row, predictionColIdx);
   }

} // end class PredictionPagingTable
