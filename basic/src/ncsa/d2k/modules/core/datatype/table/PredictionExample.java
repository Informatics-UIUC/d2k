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
 * An <code>Example</code> for prediction.
 *
 * @author  goren
 * @version $Revision$, $Date$
 */
public interface PredictionExample extends Example {

   //~ Methods *****************************************************************

   /**
    * Gets the value as a boolean from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Boolean value from the prediction column
    */
   public boolean getBooleanPrediction(int p);

   /**
    * Gets the value as a byte from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Byte value from the prediction column
    */
   public byte getBytePrediction(int p);

   /**
    * Gets the value as a byte array from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Byte array value from the prediction column
    */
   public byte[] getBytesPrediction(int p);

   /**
    * Gets the value as a char from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Char value from the prediction column
    */
   public char getCharPrediction(int p);

   /**
    * Gets the value as a char array from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Char value from the prediction column
    */
   public char[] getCharsPrediction(int p);

   /**
    * Gets the value as a double from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Double value from the prediction column
    */
   public double getDoublePrediction(int p);

   /**
    * Gets the value as a float from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Float value from the prediction column
    */
   public float getFloatPrediction(int p);

   /**
    * Gets the value as an int from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Int value from the prediction column
    */
   public int getIntPrediction(int p);

   /**
    * Gets the value as a long from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Long value from the prediction column
    */
   public long getLongPrediction(int p);

   /**
    * Returns the total number of predictions.
    *
    * @param  p Prediction column index
    *
    * @return Boolean value from the prediction column
    */
   public int getNumPredictions();

   /**
    * Gets the value as an Object from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Object value from the prediction column
    */
   public Object getObjectPrediction(int p);

   /**
    * Gets the value as a short from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return Short value from the prediction column
    */
   public short getShortPrediction(int p);

   /**
    * Gets the value as a String from the prediction column.
    *
    * @param  p Prediction column index
    *
    * @return String value from the prediction column
    */
   public String getStringPrediction(int p);

   /**
    * Sets a boolean value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setBooleanPrediction(boolean pred, int p);

   /**
    * Sets a byte value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setBytePrediction(byte pred, int p);

   /**
    * Sets a byte array value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setBytesPrediction(byte[] pred, int p);

   /**
    * Sets a char value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setCharPrediction(char pred, int p);

   /**
    * Sets a char array value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setCharsPrediction(char[] pred, int p);

   /**
    * Sets a double value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setDoublePrediction(double pred, int p);

   /**
    * Sets a float value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setFloatPrediction(float pred, int p);

   /**
    * Sets a int value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setIntPrediction(int pred, int p);

   /**
    * Sets a long value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setLongPrediction(long pred, int p);

   /**
    * Sets an Object value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setObjectPrediction(Object pred, int p);

   /**
    * Sets a short value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setShortPrediction(short pred, int p);

   /**
    * Sets a String value on a prediction column.
    *
    * @param pred Prediction value
    * @param  p Prediction column index
    */
   public void setStringPrediction(String pred, int p);
} // end interface PredictionExample
