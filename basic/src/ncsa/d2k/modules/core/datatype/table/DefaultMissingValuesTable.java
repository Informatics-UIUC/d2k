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
 * Abstract base class for <code>Table</code> implmentations that have missing
 * values.
 *
 * @author  redman
 * @author  clutter
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class DefaultMissingValuesTable implements Table {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = 1L;

   //~ Instance fields *********************************************************

   /** Default missing value for boolean. */
   protected boolean defaultMissingBoolean = false;

   /** Default missing value for byte. */
   protected byte defaultMissingByte = (byte) '\000';

   /** Default missing value for byte arrays. */
   protected byte[] defaultMissingByteArray = { (byte) '\000' };

   /** Default missing value for char. */
   protected char defaultMissingChar = '\000';

   /** Default missing value for char arrays. */
   protected char[] defaultMissingCharArray = { '\000' };

   /** Default missing value for double, float, and extended. */
   protected double defaultMissingDouble = 0.0;

   /** Default missing value for longs, ints, and shorts. */
   protected int defaultMissingInt = 0;

   /** Default missing value for String. */
   protected String defaultMissingString = "?";

   //~ Methods *****************************************************************

   /**
    * Returns the default missing value for boolean.
    *
    * @return Default missing value for boolean
    */
   public boolean getMissingBoolean() { return defaultMissingBoolean; }

   /**
    * Returns the default missing value for byte.
    *
    * @return Default missing value for byte
    */
   public byte getMissingByte() { return defaultMissingByte; }

   /**
    * Returns the default missing value for byte array.
    *
    * @return Default missing value for byte array
    */
   public byte[] getMissingBytes() { return this.defaultMissingByteArray; }

   /**
    * Returns the default missing value for char.
    *
    * @return Default missing value for char
    */
   public char getMissingChar() { return this.defaultMissingChar; }

   /**
    * Returns the default missing value for char array.
    *
    * @return Default missing value for char array.
    */
   public char[] getMissingChars() { return this.defaultMissingCharArray; }

   /**
    * Returns the default missing value for double, float, and extended.
    *
    * @return Default missing value for double, float, and extended
    */
   public double getMissingDouble() { return this.defaultMissingDouble; }

   /**
    * Returns the default missing value for int, long, and short.
    *
    * @return Default missing value for int, long, and short
    */
   public int getMissingInt() { return defaultMissingInt; }

   /**
    * Returns the default missing value for String.
    *
    * @return Default missing value for String
    */
   public String getMissingString() { return this.defaultMissingString; }

   /**
    * Sets the default missing value for boolean.
    *
    * @param newMissingBoolean Default missing value for boolean
    */
   public void setMissingBoolean(boolean newMissingBoolean) {
      this.defaultMissingBoolean = newMissingBoolean;
   }

   /**
    * Sets the default missing value for byte.
    *
    * @param newMissingByte Default missing value for byte
    */
   public void setMissingByte(byte newMissingByte) {
      this.defaultMissingByte = newMissingByte;
   }

   /**
    * Sets the default missing value for byte array.
    *
    * @param newMissingBytes Default missing value for byte array
    */
   public void setMissingBytes(byte[] newMissingBytes) {
      this.defaultMissingByteArray = newMissingBytes;
   }

   /**
    * Sets the default missing value for char.
    *
    * @param newMissingChar Default missing value for char
    */
   public void setMissingChar(char newMissingChar) {
      this.defaultMissingChar = newMissingChar;
   }

   /**
    * Sets the default missing value for char array.
    *
    * @param newMissingChars Default missing value for char array
    */
   public void setMissingChars(char[] newMissingChars) {
      this.defaultMissingCharArray = newMissingChars;
   }

   /**
    * Sets the default missing value for double, float and extended.
    *
    * @param newMissingDouble Default missing value for double, float and
    *                         extended
    */
   public void setMissingDouble(double newMissingDouble) {
      this.defaultMissingDouble = newMissingDouble;
   }

   /**
    * Sets the default missing value for int, short, and long.
    *
    * @param newMissingInt Default missing value for int, short, and long
    */
   public void setMissingInt(int newMissingInt) {
      this.defaultMissingInt = newMissingInt;
   }

   /**
    * Sets the default missing value for String.
    *
    * @param newMissingString Default missing value for String
    */
   public void setMissingString(String newMissingString) {
      this.defaultMissingString = newMissingString;
   }
} // end class DefaultMissingValuesTable
