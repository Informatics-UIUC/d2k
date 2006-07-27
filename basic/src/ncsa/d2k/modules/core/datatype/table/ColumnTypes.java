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

import ncsa.d2k.modules.core.io.file.input.FlatFileParser;


/**
 * Defines the different types of columns that can make up a table.
 *
 * @author  suvalala
 * @author  redman
 * @version $Revision$, $Date$
 */
public final class ColumnTypes {

   //~ Static fields/initializers **********************************************

   /** A column of integer values. */
   static public final int INTEGER = 0;

   /** A column of float values. */
   static public final int FLOAT = 1;

   /** A column of double values. */
   static public final int DOUBLE = 2;

   /** A column of short values. */
   static public final int SHORT = 3;

   /** A column of long values. */
   static public final int LONG = 4;

   /** A column of String values. */
   static public final int STRING = 5;

   /** A column of char[] values. */
   static public final int CHAR_ARRAY = 6;

   /** A column of byte[] values. */
   static public final int BYTE_ARRAY = 7;

   /** A column of boolean values. */
   static public final int BOOLEAN = 8;

   /** A column of Object values. */
   static public final int OBJECT = 9;

   /** A column of byte values. */
   static public final int BYTE = 10;

   /** A column of char values. */
   static public final int CHAR = 11;

   /** A column of char values. */
   static public final int NOMINAL = 12;

   /** A column of unspecified values -- used for sparse tables. */
   static public final int UNSPECIFIED = 13;

   /** Names of each of the types. */
   static private final String[] _names =
   {
      "INTEGER",
      "FLOAT",
      "DOUBLE",
      "SHORT",
      "LONG",
      "STRING",
      "CHARACTER ARRAY",
      "BYTE ARRAY",
      "BOOLEAN",
      "OBJECT",
      "BYTE",
      "CHAR",
      "NOMINAL",
      "UNSPECIFIED"
   };

   //~ Methods *****************************************************************

   /**
    * Returns the type name for the given key.
    *
    * @param  i One of the provided static keys
    *
    * @return Type name corresponding to the specified key
    */
   static public String getTypeName(int i) { return _names[i]; }

   /**
    * Tests if the specified string contains the characters from one of the 
    * numeric, constant column type names from this class.
    *
    * @param  inString String to check for a match
    *
    * @return True if the string contains a numeric type
    */
   static public boolean isContainNumeric(String inString) {

      if (
          inString.toLowerCase().indexOf("number") >= 0 ||
             inString.toLowerCase().indexOf("numeric") >= 0 ||
             inString.toLowerCase().indexOf("decimal") >= 0 ||
             inString.toLowerCase().indexOf("bigint") >= 0 ||
             inString.toLowerCase().indexOf("smallint") >= 0 ||
             inString.toLowerCase().indexOf("integer") >= 0 ||
             inString.toLowerCase().indexOf("real") >= 0 ||
             inString.toLowerCase().indexOf("double") >= 0) {

         return true;
      } else {
         return false;
      }

   }

   /**
    * Tests if the specified string matches either one of the numeric, constant
    * column type names from this class, or one of the numeric, <code>
    * FlatFileParser</code> type names.
    *
    * @param  inString String to check for a match
    *
    * @return True if the string matches a numeric type
    */
   static public boolean isEqualNumeric(String inString) {

      if (
          inString.toLowerCase().equals("number") ||
             inString.toLowerCase().equals("numeric") ||
             inString.toLowerCase().equals("decimal") ||
             inString.toLowerCase().equals("bigint") ||
             inString.toLowerCase().equals("smallint") ||
             inString.toLowerCase().equals("integer") ||
             inString.toLowerCase().equals("real") ||
             inString.toLowerCase().equals("double")) {

         return true;
      }

      // for file parser numeric data type
      else if (
               inString.toLowerCase().equals(FlatFileParser.INT_TYPE
                                                .toLowerCase()) ||
                  inString.toLowerCase().equals(FlatFileParser.FLOAT_TYPE
                                                   .toLowerCase()) ||
                  inString.toLowerCase().equals(FlatFileParser.DOUBLE_TYPE
                                                   .toLowerCase()) ||
                  inString.toLowerCase().equals(FlatFileParser.LONG_TYPE
                                                   .toLowerCase()) ||
                  inString.toLowerCase().equals(FlatFileParser.SHORT_TYPE
                                                   .toLowerCase())) {
         return true;
      } else {
         return false;
      }

   } // end method isEqualNumeric
} // end class ColumnTypes
