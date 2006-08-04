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
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.Row;

import java.io.Serializable;


/**
 * Description of class ExampleImpl.
 *
 * @author  suvalala
 * @author  redman
 * @version $Revision$, $Date$
 */
public class ExampleImpl extends RowImpl implements Serializable, Example {

   //~ Static fields/initializers **********************************************

   /** Description of field serialVersionUID. */
   static final long serialVersionUID = 8401491256285778215L;

   //~ Instance fields *********************************************************

   /** these are the input columns. */
   private Column[] inputColumns;

   /** output columns. */
   private Column[] outputColumns;

   /** the test columns. */
   private int[] subset;

   //~ Constructors ************************************************************

   /**
    * Creates a new ExampleImpl object.
    */
   public ExampleImpl() { super(); }

   /**
    * Creates a new ExampleImpl object.
    *
    * @param et Description of parameter et.
    */
   public ExampleImpl(ExampleTableImpl et) {
      super(et);

      Column[] columns = et.getColumns();
      int[] inputInd = et.getInputFeatures();
      inputColumns = new Column[et.getNumInputFeatures()];

      for (int i = 0; i < inputColumns.length; i++) {
         this.inputColumns[i] = columns[inputInd[i]];
      }

      int[] outputInd = et.getOutputFeatures();
      outputColumns = new Column[et.getNumOutputFeatures()];

      for (int i = 0; i < this.outputColumns.length; i++) {
         this.outputColumns[i] = columns[outputInd[i]];
      }

      this.subset = et.getSubset();
   }

   //~ Methods *****************************************************************

   /**
    * ANCA: method for comparing two ExampleImpl objects.
    *
    * @param  ex Description of parameter ex.
    *
    * @return Description of return value.
    */
   public final boolean equals(Object ex) {
      Row example;

      try {
         example = (Row) ex;
      } catch (Exception e) {
         return false;
      }

      for (int i = 0; i < (inputColumns.length + outputColumns.length); i++) {

         if (!this.getString(i).equals(example.getString(i))) {
            return false;
         }
      }

      return true;

   }

   /**
    * Get the ith input as a boolean.
    *
    * @param  i the input index
    *
    * @return the ith input as a boolean
    */
   public final boolean getInputBoolean(int i) {
      return inputColumns[i].getBoolean(index);
   }

   /**
    * Get the ith input as a byte.
    *
    * @param  i the input index
    *
    * @return the ith input as a byte
    */
   public final byte getInputByte(int i) {
      return inputColumns[i].getByte(index);
   }

   /**
    * Get the ith input as bytes.
    *
    * @param  i the input index
    *
    * @return the ith input as bytes.
    */
   public final byte[] getInputBytes(int i) {
      return inputColumns[i].getBytes(index);
   }

   /**
    * Get the ith input as a char.
    *
    * @param  i the input index
    *
    * @return the ith input as a char
    */
   public final char getInputChar(int i) {
      return inputColumns[i].getChar(index);
   }

   /**
    * Get the ith input as chars.
    *
    * @param  i the input index
    *
    * @return the ith input as chars
    */
   public final char[] getInputChars(int i) {
      return inputColumns[i].getChars(index);
   }

   /**
    * Get the ith input as a double.
    *
    * @param  i the input index
    *
    * @return the ith input as a double
    */
   public final double getInputDouble(int i) {
      return inputColumns[i].getDouble(index);
   }

   /**
    * Get the ith input as a float.
    *
    * @param  i the input index
    *
    * @return the ith input as a float
    */
   public final float getInputFloat(int i) {
      return inputColumns[i].getFloat(index);
   }

   /**
    * Get the ith input as an int.
    *
    * @param  i the input index
    *
    * @return the ith input as an int
    */
   public final int getInputInt(int i) { return inputColumns[i].getInt(index); }

   /**
    * Get the ith input as a long.
    *
    * @param  i the input index
    *
    * @return the ith input as a long
    */
   public final long getInputLong(int i) {
      return inputColumns[i].getLong(index);
   }

   /**
    * Get the ith input as an Object.
    *
    * @param  i the input index
    *
    * @return the ith input as an Object.
    */
   public final Object getInputObject(int i) {
      return inputColumns[i].getObject(index);
   }

   /**
    * Get the ith input as a short.
    *
    * @param  i the input index
    *
    * @return the ith input as a short
    */
   public final short getInputShort(int i) {
      return inputColumns[i].getShort(index);
   }

   /**
    * Get the ith input as a String.
    *
    * @param  i the input index
    *
    * @return the ith input as a String
    */
   public final String getInputString(int i) {
      return inputColumns[i].getString(index);
   }

   /**
    * Get the oth output as a boolean.
    *
    * @param  o the output index
    *
    * @return the oth output as a boolean
    */
   public final boolean getOutputBoolean(int o) {
      return outputColumns[o].getBoolean(index);
   }

   /**
    * Get the oth output as a byte.
    *
    * @param  o the output index
    *
    * @return the oth output as a byte
    */
   public final byte getOutputByte(int o) {
      return outputColumns[o].getByte(index);
   }

   /**
    * Get the oth output as bytes.
    *
    * @param  o the output index
    *
    * @return the oth output as bytes.
    */
   public final byte[] getOutputBytes(int o) {
      return outputColumns[o].getBytes(index);
   }

   /**
    * Get the oth output as a char.
    *
    * @param  o the output index
    *
    * @return the oth output as a char
    */
   public final char getOutputChar(int o) {
      return outputColumns[o].getChar(index);
   }

   /**
    * Get the oth output as chars.
    *
    * @param  o the output index
    *
    * @return the oth output as chars
    */
   public final char[] getOutputChars(int o) {
      return outputColumns[o].getChars(index);
   }

   /**
    * Get the oth output as a double.
    *
    * @param  o the output index
    *
    * @return the oth output as a double
    */
   public final double getOutputDouble(int o) {
      return outputColumns[o].getDouble(index);
   }

   /**
    * Get the oth output as a float.
    *
    * @param  o the output index
    *
    * @return the oth output as a float
    */
   public final float getOutputFloat(int o) {
      return outputColumns[o].getFloat(index);
   }

   /**
    * Get the oth output as an int.
    *
    * @param  o the output index
    *
    * @return the oth output as an int
    */
   public final int getOutputInt(int o) {
      return outputColumns[o].getInt(index);
   }

   /**
    * Get the oth output as a long.
    *
    * @param  o the output index
    *
    * @return the ith output as a long
    */
   public final long getOutputLong(int o) {
      return outputColumns[o].getLong(index);
   }

   /**
    * Get the oth output as an Object.
    *
    * @param  o the output index
    *
    * @return the oth output as an Object
    */
   public final Object getOutputObject(int o) {
      return outputColumns[o].getObject(index);
   }

   /**
    * Get the oth output as a short.
    *
    * @param  o the output index
    *
    * @return the oth output as a short
    */
   public final short getOutputShort(int o) {
      return outputColumns[o].getShort(index);
   }

   /**
    * Get the oth output as a String.
    *
    * @param  o the output index
    *
    * @return the oth output as a String
    */
   public final String getOutputString(int o) {
      return outputColumns[o].getString(index);
   }

   /**
    * This could potentially be subindexed.
    *
    * @param i Description of parameter $param.name$.
    */
   public final void setIndex(int i) { this.index = this.subset[i]; }


} // end class ExampleImpl
