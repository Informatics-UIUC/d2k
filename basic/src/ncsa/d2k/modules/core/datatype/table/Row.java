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
 * Interface for a <code>Table</code> row.
 *
 * @author  suvalala
 * @version $Revision$, $Date$
 */
public interface Row {

   //~ Methods *****************************************************************

   /**
    * Gets the ith input as a <code>boolean</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>boolean</code>
    */
   public boolean getBoolean(int i);

   /**
    * Gets the ith input as a <code>byte</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>byte</code>
    */
   public byte getByte(int i);

   /**
    * Gets the ith input as a <code>byte</code> array.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>byte</code> array
    */
   public byte[] getBytes(int i);

   /**
    * Gets the ith input as a <code>char</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>char</code>
    */
   public char getChar(int i);

   /**
    * Gets the ith input as a <code>char</code> array.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>char</code> array
    */
   public char[] getChars(int i);

   /**
    * Gets the ith input as a <code>double</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>double</code>
    */
   public double getDouble(int i);

   /**
    * Gets the ith input as a <code>float</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>float</code>
    */
   public float getFloat(int i);

   /**
    * Gets the ith input as an <code>int</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as an <code>int</code>
    */
   public int getInt(int i);

   /**
    * Gets the ith input as a <code>long</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>long</code>
    */
   public long getLong(int i);

   /**
    * Gets the ith input as an <code>Object</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as an <code>Object</code>
    */
   public Object getObject(int i);

   /**
    * Gets the ith input as a <code>short.</code>
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>short</code>
    */
   public short getShort(int i);

   /**
    * Gets the ith input as a <code>String</code>.
    *
    * @param  i The input index
    *
    * @return The ith input as a <code>String</code>
    */
   public String getString(int i);

   /**
    * Gets a reference to the <code>Table</code> this <code>Row</code> is part
    * of.
    *
    * @return Reference to the <code>Table</code> this <code>Row</code> is part
    *         of
    */
   public Table getTable();

   /**
    * Sets the index of the row to access.
    *
    * @param i Index of the row to access
    */
   public void setIndex(int i);
} // end interface Row
