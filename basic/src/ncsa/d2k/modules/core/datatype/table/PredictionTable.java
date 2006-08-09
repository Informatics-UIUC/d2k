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

/**
 * <p>A Table that should be used by a PredictionModelModule to make predictions
 * on a dataset. The prediction set designates the indices of the columns that
 * are filled with predictions.</p>
 *
 * <p>A PredictionTable is partially mutable. Only the prediction columns can be
 * modified. A newly constructed PredictionTable will have one extra column for
 * each output in the ExampleTable. Entries in these extra columns can be
 * accessed via the methods defined in this class. If the ExampleTable has no
 * outputs, then the prediction columns must be added manually via the
 * appropriate addPredictionColumn() method.</p>
 *
 * @author  suvalala
 * @version $Revision$, $Date$
 */
public interface PredictionTable extends ExampleTable {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -3140627186936758135L;

   /** Appended to the end of prediction column labels. */
   static public final String PREDICTION_COLUMN_APPEND_TEXT = " Predictions";

   //~ Methods *****************************************************************

   /**
    * Gets a boolean prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public boolean getBooleanPrediction(int row, int predictionColIdx);

   /**
    * Gets a byte prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte getBytePrediction(int row, int predictionColIdx);

   /**
    * Gets a byte[] prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte[] getBytesPrediction(int row, int predictionColIdx);

   /**
    * Gets a char prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char getCharPrediction(int row, int predictionColIdx);

   /**
    * Gets a char[] prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char[] getCharsPrediction(int row, int predictionColIdx);

   /**
    * Gets a double prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public double getDoublePrediction(int row, int predictionColIdx);

   /**
    * Gets a float prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public float getFloatPrediction(int row, int predictionColIdx);

   /**
    * Gets an int prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public int getIntPrediction(int row, int predictionColIdx);

   /**
    * Gets a long prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public long getLongPrediction(int row, int predictionColIdx);

   /**
    * Gets an Object prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public Object getObjectPrediction(int row, int predictionColIdx);


   /**
    * Gets the prediction set.
    *
    * @return The prediction set
    */
   public int[] getPredictionSet();

   /**
    * Gets a short prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public short getShortPrediction(int row, int predictionColIdx);

   /**
    * Gets a String prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param  row              Row of the table
    * @param  predictionColIdx Index into the prediction set
    *
    * @return Prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public String getStringPrediction(int row, int predictionColIdx);

   /**
    * Sets a boolean prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setBooleanPrediction(boolean prediction, int row,
                                    int predictionColIdx);

   /**
    * Sets a byte prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setBytePrediction(byte prediction, int row,
                                 int predictionColIdx);

   /**
    * Sets a byte[] prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setBytesPrediction(byte[] prediction, int row,
                                  int predictionColIdx);

   /**
    * Sets a char prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setCharPrediction(char prediction, int row,
                                 int predictionColIdx);

   /**
    * Sets a char[] prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setCharsPrediction(char[] prediction, int row,
                                  int predictionColIdx);

   /**
    * Sets a double prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setDoublePrediction(double prediction, int row,
                                   int predictionColIdx);

   /**
    * Sets a float prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setFloatPrediction(float prediction, int row,
                                  int predictionColIdx);

   /**
    * Sets an int prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setIntPrediction(int prediction, int row, int predictionColIdx);

   /**
    * Sets a long prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setLongPrediction(long prediction, int row,
                                 int predictionColIdx);

   /**
    * Sets an Object prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setObjectPrediction(Object prediction, int row,
                                   int predictionColIdx);

   /**
    * Sets the prediction set.
    *
    * @param p New prediction set
    */
   public void setPredictionSet(int[] p);

   /**
    * Sets a short prediction in the specified prediction column. The index into
    * the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setShortPrediction(short prediction, int row,
                                  int predictionColIdx);

   /**
    * Sets a String prediction in the specified prediction column. The index
    * into the prediction set is used, not the actual column index.
    *
    * @param prediction       Value of the prediction
    * @param row              Row of the table
    * @param predictionColIdx Index into the prediction set
    */
   public void setStringPrediction(String prediction, int row,
                                   int predictionColIdx);
} // end interface PredictionTable
