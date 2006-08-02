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
 * Defines methods of a <code>Table</code> with some additional features
 * designed to support the model-building process in a standard and
 * interchangable way.
 *
 * @author  suvalala
 * @version $Revision$, $Date$
 */
public interface Example extends Row {

   //~ Methods *****************************************************************

   /**
    * Gets the input feature at the specified index as a boolean.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a boolean
    */
   public boolean getInputBoolean(int i);

   /**
    * Gets the input feature at the specified index as a byte.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a byte
    */
   public byte getInputByte(int i);

   /**
    * Gets the input feature at the specified index as an array of bytes.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as an array of bytes.
    */
   public byte[] getInputBytes(int i);

   /**
    * Gets the input feature at the specified index as a char.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a char
    */
   public char getInputChar(int i);

   /**
    * Gets the input feature at the specified index as an array of chars.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as an array of chars
    */
   public char[] getInputChars(int i);

   /**
    * Gets the input feature at the specified index as a double.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a double
    */
   public double getInputDouble(int i);

   /**
    * Gets the input feature at the specified index as a float.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a float
    */
   public float getInputFloat(int i);

   /**
    * Gets the input feature at the specified index as an int.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as an int
    */
   public int getInputInt(int i);

   /**
    * Gets the input feature at the specified index as a long.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a long
    */
   public long getInputLong(int i);

   /**
    * Gets the input feature at the specified index as an Object.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as an Object.
    */
   public Object getInputObject(int i);

   /**
    * Gets the input feature at the specified index as a short.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a short
    */
   public short getInputShort(int i);

   /**
    * Gets the input feature at the specified index as a String.
    *
    * @param  i Input feature index
    *
    * @return The specified input feature as a String
    */
   public String getInputString(int i);

   /**
    * Gets the output feature at the specified index as a boolean.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a boolean
    */
   public boolean getOutputBoolean(int o);

   /**
    * Gets the output feature at the specified index as a byte.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a byte
    */
   public byte getOutputByte(int o);

   /**
    * Gets the output feature at the specified index as bytes.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as bytes.
    */
   public byte[] getOutputBytes(int o);

   /**
    * Gets the output feature at the specified index as a char.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a char
    */
   public char getOutputChar(int o);

   /**
    * Gets the output feature at the specified index as chars.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as chars
    */
   public char[] getOutputChars(int o);

   /**
    * Gets the output feature at the specified index as a double.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a double
    */
   public double getOutputDouble(int o);

   /**
    * Gets the output feature at the specified index as a float.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a float
    */
   public float getOutputFloat(int o);

   /**
    * Gets the output feature at the specified index as an int.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as an int
    */
   public int getOutputInt(int o);

   /**
    * Gets the output feature at the specified index as a long.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a long
    */
   public long getOutputLong(int o);

   /**
    * Gets the output feature at the specified index as an Object.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as an Object
    */
   public Object getOutputObject(int o);

   /**
    * Gets the output feature at the specified index as a short.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a short
    */
   public short getOutputShort(int o);

   /**
    * Gets the output feature at the specified index as a String.
    *
    * @param  o Output feature index
    *
    * @return The specified output feature as a String
    */
   public String getOutputString(int o);
} // end interface Example
