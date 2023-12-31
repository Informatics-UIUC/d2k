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
package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * A Table that should be used by a ModelModule to make predictions on a
 * dataset. This table creates a new, empty Column for each output in the
 * original ExampleTable. The datatype of each new Column is the same as its
 * associated output. So, for example, if the first output Column is a
 * StringColumn, the first prediction Column will be a StringColumn. The indices
 * of the new columns are accessible via getPredictionSet() and
 * setPredictionSet(). If the ExampleTable did not have any outputs, it is up to
 * the ModelModule to add prediction Columns.
 *
 * @author  suvalala
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class PredictionTableImpl extends ExampleTableImpl
   implements PredictionTable {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = 6560109930974102679L;

   //~ Instance fields *********************************************************

   /** Prediction column from the table. */
   protected int[] predictionSet;

   //~ Constructors ************************************************************

   /**
    * Given a prediction table, copy its input columns, and create new columns
    * to hold the predicted values.
    *
    * @param ttt the prediction table to start with
    */
   public PredictionTableImpl(PredictionTableImpl ttt) {
      super(ttt);
      predictionSet = ttt.getPredictionSet();
   }

   /**
    * Creates a new PredictionTableImpl object.
    *
    * @param i Description of parameter i.
    */
   public PredictionTableImpl(int i) { super(i); }

   /**
    * Given an example table, copy its input columns, and create * columns to
    * hold the predicted values.
    *
    * @param ttt the ExampleTable that contains the inital values
    */
   public PredictionTableImpl(ExampleTableImpl ttt) {
      super(ttt.columns);

      // copies the fields from the example table.
      this.comment = ttt.getComment();
      this.setTrainingSet(ttt.getTrainingSet());
      this.setTestingSet(ttt.getTestingSet());
      this.setInputFeatures(ttt.getInputFeatures());
      this.setOutputFeatures(ttt.getOutputFeatures());
      this.setLabel(ttt.getLabel());
      this.setComment(ttt.getComment());
      this.transformations = ttt.transformations;
      this.subset = ttt.subset;

      if (outputColumns == null) {
         predictionSet = new int[0];
         outputColumns = new int[0];
      } else {
         predictionSet = new int[outputColumns.length];
      }

      // Copy the existing columns.
      Column[] newColumns = new Column[columns.length + outputColumns.length];
      System.arraycopy(columns, 0, newColumns, 0, columns.length);

      int i = columns.length;

      // Create new columns which will contain the predicted values.
      for (int i2 = 0; i2 < outputColumns.length; i++, i2++) {
         Column col =
            ColumnUtilities.createColumn(ttt.getColumnType(outputColumns[i2]),
                                         columns[outputColumns[i2]]
                                            .getNumRows());
         StringBuffer newLabel =
            new StringBuffer(ttt.getColumnLabel(outputColumns[i2]));
         newLabel.append(PREDICTION_COLUMN_APPEND_TEXT);
         col.setLabel(newLabel.toString());
         newColumns[i] = col;
         predictionSet[i2] = i;
      }

      columns = newColumns;
   }

   //~ Methods *****************************************************************

   /**
    * Copy method. Return an exact copy of this column. A deep copy is
    * attempted, but if it fails a new column will be created, initialized with
    * the same data as this column.
    *
    * @return A new Column with a copy of the contents of this column.
    */
   public Table copy() {
      PredictionTableImpl vt;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(this);

         byte[] buf = baos.toByteArray();
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(buf);
         ObjectInputStream ois = new ObjectInputStream(bais);
         vt = (PredictionTableImpl) ois.readObject();
         ois.close();

         return vt;
      } catch (Exception e) {
         vt = new PredictionTableImpl(getNumColumns());

         for (int i = 0; i < getNumColumns(); i++) {
            vt.setColumn(getColumn(i).copy(), i);
         }

         vt.setLabel(getLabel());
         vt.setComment(getComment());
         vt.setInputFeatures(getInputFeatures());
         vt.setOutputFeatures(getOutputFeatures());
         vt.setPredictionSet(getPredictionSet());
         vt.transformations = (ArrayList) transformations.clone();
         vt.setTestingSet(getTestingSet());
         vt.setTrainingSet(getTrainingSet());

         return vt;
      }
   } // end method copy

   /**
    * When we compare to a non prediction table, we will skip the prediction
    * columns.
    *
    * @param  mt the object to compare to.
    *
    * @return when we compare to a non prediction table, we will skip the
    *         prediction columns.
    */
   public boolean equals(Object mt) {
      MutableTableImpl mti = (MutableTableImpl) mt;
      int numColumns = mti.getNumColumns();
      int numRows = mti.getNumRows();
      int myColumns = getNumColumns();

      /** if this is not  a prediction table, don't compare those columns */
      if (!(mt instanceof PredictionTableImpl)) {

         if (this.getPredictionSet() != null) {
            myColumns -= this.getPredictionSet().length;
         }
      }

      if (myColumns != numColumns) {
         return false;
      }

      if (getNumRows() != numRows) {
         return false;
      }

      for (int i = 0; i < numRows; i++) {

         for (int j = 0; j < numColumns; j++) {

            if (!getString(i, j).equals(mti.getString(i, j))) {
               return false;
            }
         }
      }

      return true;
   } // end method equals

   /**
    * Get a boolean prediciton in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public boolean getBooleanPrediction(int row, int predictionColIdx) {
      return getBoolean(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a byte prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte getBytePrediction(int row, int predictionColIdx) {
      return getByte(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a byte[] prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte[] getBytesPrediction(int row, int predictionColIdx) {
      return getBytes(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a char prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char getCharPrediction(int row, int predictionColIdx) {
      return getChar(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a char[] prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char[] getCharsPrediction(int row, int predictionColIdx) {
      return getChars(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a double prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public double getDoublePrediction(int row, int predictionColIdx) {
      return getDouble(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a float prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public float getFloatPrediction(int row, int predictionColIdx) {
      return getFloat(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get an int prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public int getIntPrediction(int row, int predictionColIdx) {
      return getInt(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a long prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public long getLongPrediction(int row, int predictionColIdx) {
      return getLong(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get an Object prediciton in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public Object getObjectPrediction(int row, int predictionColIdx) {
      return getObject(row, predictionSet[predictionColIdx]);
   }


   /**
    * Set the prediction set.
    *
    * @return the prediciton set
    */
   public int[] getPredictionSet() { return predictionSet; }

   /**
    * Get a short prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public short getShortPrediction(int row, int predictionColIdx) {
      return getShort(row, predictionSet[predictionColIdx]);
   }

   /**
    * Get a String prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              the row of the table
    * @param  predictionColIdx the index into the prediction set
    *
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public String getStringPrediction(int row, int predictionColIdx) {
      return getString(row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a boolean prediciton in the specified prediction column. The index
    * into the prediction set is used, not the actual column index. This
    * functions the same as setBoolean(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBooleanPrediction(boolean prediction, int row,
                                    int predictionColIdx) {
      setBoolean(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a byte prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setByte(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBytePrediction(byte prediction, int row,
                                 int predictionColIdx) {
      setByte(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a byte[] prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setBytes(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBytesPrediction(byte[] prediction, int row,
                                  int predictionColIdx) {
      setBytes(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a char prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setChar(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setCharPrediction(char prediction, int row,
                                 int predictionColIdx) {
      setChar(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a char[] prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setChars(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setCharsPrediction(char[] prediction, int row,
                                  int predictionColIdx) {
      setChars(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a double prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setDouble(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setDoublePrediction(double prediction, int row,
                                   int predictionColIdx) {
      setDouble(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a float prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setFloat(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setFloatPrediction(float prediction, int row,
                                  int predictionColIdx) {
      setFloat(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set an int prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setInt(prediction, row, getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setIntPrediction(int prediction, int row, int predictionColIdx) {
      setInt(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a long prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setLong(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setLongPrediction(long prediction, int row,
                                 int predictionColIdx) {
      setLong(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a Object prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setObject(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setObjectPrediction(Object prediction, int row,
                                   int predictionColIdx) {
      setObject(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set the prediction set.
    *
    * @param p the new prediciton set
    */
   public void setPredictionSet(int[] p) { predictionSet = p; }

   /**
    * Set a short prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setShort(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setShortPrediction(short prediction, int row,
                                  int predictionColIdx) {
      setShort(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * Set a String prediciton in the specified prediction column. The index into
    * the prediction set is used, not the actual column index. This functions
    * the same as setString(prediction, row,
    * getPredictionSet()[predictionColIdx]).
    *
    * @param prediction       the value of the prediciton
    * @param row              the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setStringPrediction(String prediction, int row,
                                   int predictionColIdx) {
      setString(prediction, row, predictionSet[predictionColIdx]);
   }

   /**
    * return this.
    *
    * @return this prediction table.
    */
   public PredictionTable toPredictionTable() { return this; }

} // end class PredictionTableImpl
