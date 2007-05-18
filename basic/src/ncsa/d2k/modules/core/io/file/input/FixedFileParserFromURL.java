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
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import ncsa.d2k.modules.core.util.*;



/**
 * Read a fixed format file from a Data Object Proxy.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * FixedFileParser</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class FixedFileParserFromURL implements FlatFileParser {

   //~ Instance fields *********************************************************

   /** _columnBegin from Table description. */
   private int[] _columnBegin;

   /** _columnEnd from Table description. */
   private int[] _columnEnd;

   /** _columnLabels from Table description. */
   private String[] _columnLabels;


   /** _columnType from Table description. */
   private int[] _columnType;

   /** field _dop. */
   private DataObjectProxy _dop;

   /**
    * The input file _file. When useing a remote file, this is a locally cached
    * copy.
    */
   private File _file;

   /** _noOfColumns from Table description. */
   private int _noOfColumns;

   /** _reader from Table description. */
   private LineNumberReader _reader;

   /** _tableLength from Table description. */
   private int _tableLength;


   /** Enable verbose messages. */
   boolean debug = false;
   
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   //~ Constructors ************************************************************

   /**
    * Creates a new FixedFileParserFromURL object.
    *
    * @param  dop    Description of parameter dop.
    * @param  header Description of parameter header.
    *
    * @throws Exception Description of exception Exception.
    */
   public FixedFileParserFromURL(DataObjectProxy dop, Table header)
      throws Exception {
      int lbl = -1;
      int typ = -1;
      int strt = -1;
      int stp = -1;
      int len = -1;

      if (header.getNumColumns() == 0) {
         throw new Exception("Could not read header.");
      }

      for (int i = 0; i < header.getNumColumns(); i++) {

         if (header.getColumnLabel(i).equalsIgnoreCase(LABEL)) {
            lbl = i;
         }

         if (header.getColumnLabel(i).equalsIgnoreCase(TYPE)) {
            typ = i;
         }

         if (header.getColumnLabel(i).equalsIgnoreCase(START)) {
            strt = i;
         }

         if (header.getColumnLabel(i).equalsIgnoreCase(STOP)) {
            stp = i;
         }

         if (header.getColumnLabel(i).equalsIgnoreCase(LENGTH)) {
            len = i;
         }
      } // end for

      setColumnLabels(header, lbl);

      if (typ != -1) {
         setColumnTypes(header, typ);
      }

      if (strt == -1) {
         throw new Exception("Could not determine column start.");
      } else {
         setColumnBeginings(header, strt);
      }

      if (len == -1 && stp != -1) {
         setColumnEnds(header, stp);
      } else if (len != -1) {
         setColumnBounds(header, len);
      } else {
         throw new Exception("Could not determine column sizes.");
      }

      // The Data Object Proxy knows how to find the file.

      _dop = dop;

      File file = _dop.readFile(null);

      _reader = new LineNumberReader(new FileReader(file));

      _file = file;

      int lineLength = _columnEnd[_noOfColumns - 1];

      // count the number of lines in the file
      BufferedReader rdr = new BufferedReader(new FileReader(file));
      String line = null;

      while ((line = rdr.readLine()) != null) {
         _tableLength++;
         lineLength = line.length();
      }


      // QA added - check column bounds
      int lenHdr = header.getNumRows();

      for (int i = 0; i < lenHdr; i++) {

         if (_columnBegin[i] < 1) {
            throw new Exception("Column start " + _columnBegin[i] +
                                " must  be greater than zero." +
                                " Please correct format ");
         }

         if (_columnBegin[i] > _columnEnd[i]) {
            throw new Exception("Column start " + _columnBegin[i] +
                                " must be less than or equal to " +
                                "column end " + _columnEnd[i] +
                                ". Please correct format");
         }

         if (_columnBegin[i] > lineLength) {
            throw new Exception("Column start " + _columnBegin[i] +
                                " must be less than or equal to " +
                                "line length " + lineLength +
                                ". Please correct format");
         }

         if (_columnEnd[i] > lineLength) {
            throw new Exception("Column end " + _columnEnd[i] +
                                " must be less than or equal " +
                                "line length " + lineLength +
                                ". Please correct format");
         }

      } // end for

      if (debug) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug("LL: " + lineLength);
    	  myLogger.debug("NR: " + _tableLength);
    	  myLogger.debug("NC: " + this._noOfColumns);

         for (int i = 0; i < this._columnBegin.length; i++) {
       	  myLogger.debug("BEGIN: " + _columnBegin[i]);
    	  myLogger.debug("END: " + _columnEnd[i]);
         }
         myLogger.resetLoggingLevel();//re-set level to original level
      }

      _reader.setLineNumber(0);

   }

   //~ Methods *****************************************************************

   /**
    * private char[][] parseLine(int row) throws Exception {.
    *
    * @param  row number of the row this fills.
    *
    * @return The values for the row.
    *
    * @throws Exception error reading or parsing.
    */
   private ParsedLine parseLine(int row) throws Exception {

      ParsedLine pl = new ParsedLine();

      if (debug) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug("noOfColumns:" + _noOfColumns);
          myLogger.resetLoggingLevel();//re-set level to original level
      }

      char[][] retVal = new char[_noOfColumns][];
      boolean[] bl = new boolean[_noOfColumns];

      // these will be used for eliminating trailing and
      // leading whitespace
      int trueBegin;
      int trueEnd;

      // this is the current
      // read each line, put the data in the columns

      // MUST SKIP TO THE APPROPRIATE LINE HERE
      skipToLine(row);

      String ln = _reader.readLine();
      char[] lineChars = ln.toCharArray();
      int lineLength = ln.length();

      if (debug) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug("parse line: " + row + " " + ln + " lenght : " +
                  ln.length());
          myLogger.resetLoggingLevel();//re-set level to original level
      }

      for (int col = 0; col < _noOfColumns; col++) {

         // getting rid of whitespace
         trueBegin = _columnBegin[col] - 1;
         trueEnd = _columnEnd[col] - 1;

         while ((lineChars[trueBegin] == ' ') &&
                   (trueBegin < trueEnd)) {
            trueBegin++;
         }

         if (debug) {
       	  myLogger.setDebugLoggingLevel();//temp set to debug
       	  myLogger.debug("b:" + trueBegin + " e:" + trueEnd);
          myLogger.resetLoggingLevel();//re-set level to original level
         }

         char[] element =
            new String(lineChars, trueBegin, trueEnd - trueBegin + 1)
               .toCharArray();

         if (debug) {
       	  myLogger.setDebugLoggingLevel();//temp set to debug
       	  myLogger.debug(new String(element) + "!");
          myLogger.resetLoggingLevel();//re-set level to original level
         }

         retVal[col] = element;

      } // end for

      pl.blanks = bl;
      pl.elements = retVal;

      return pl;
   } // end method parseLine

   /**
    * Iniitalize the markers for bginning of column 'col'.
    *
    * @param vt  The table.
    * @param col The column.
    */
   private void setColumnBeginings(Table vt, int col) {
      int nr = vt.getNumRows();
      _columnBegin = new int[nr];

      for (int i = 0; i < nr; i++) {
         _columnBegin[i] = vt.getInt(i, col);
      }

   }

   /**
    * Initialize the markers for the bounds of column 'col'.
    *
    * @param vt  The Table
    * @param col The column.
    */
   private void setColumnBounds(Table vt, int col) {
      int nr = vt.getNumRows();
      int start = 0;
      int end;
      _columnEnd = new int[vt.getNumRows()];

      for (int i = 0; i < nr; i++) {
         start = _columnBegin[i];
         end = start + vt.getInt(i, col) - 1;
         start = end + 1;
         _columnEnd[i] = end;
      }
   }

   /**
    * Initialize the markers for the ends of column 'col'.
    *
    * @param vt  The Table
    * @param col The column.
    */
   private void setColumnEnds(Table vt, int col) {
      int nr = vt.getNumRows();
      _columnEnd = new int[nr];

      for (int i = 0; i < nr; i++) {
         _columnEnd[i] = vt.getInt(i, col);
      }
   }

   /**
    * This method sets up the column labels. If the metadata table contained a
    * LABEL column, we use the entries in that column as the labels for the data
    * we're reading. If it didn't, we generate column labels of the form
    * column_N, to match what other readers do if no labels.
    *
    * @param vt  Description of parameter vt.
    * @param col Description of parameter col.
    */
   private void setColumnLabels(Table vt, int col) {
      int nr = vt.getNumRows();
      _columnLabels = new String[nr];

      if (col != -1) {

         for (int i = 0; i < nr; i++) {
            _columnLabels[i] = vt.getString(i, col);
         }
      } else {

         for (int i = 0; i < nr; i++) {
            _columnLabels[i] = "column_" + i;
            ;
         }
      }

      _noOfColumns = nr;
   }

   /**
    * Initialize the types of column 'col'.
    *
    * @param vt  The Table
    * @param col The column.
    */
   private void setColumnTypes(Table vt, int col) {
      int nr = vt.getNumRows();
      _columnType = new int[nr];

      for (int i = 0; i < nr; i++) {
         String type = vt.getString(i, col);

         if (type.equalsIgnoreCase(STRING_TYPE)) {
            _columnType[i] = ColumnTypes.STRING;
         } else if (type.equalsIgnoreCase(DOUBLE_TYPE)) {
            _columnType[i] = ColumnTypes.DOUBLE;
         } else if (type.equalsIgnoreCase(INT_TYPE)) {
            _columnType[i] = ColumnTypes.INTEGER;
         } else if (type.equalsIgnoreCase(FLOAT_TYPE)) {
            _columnType[i] = ColumnTypes.FLOAT;
         } else if (type.equalsIgnoreCase(SHORT_TYPE)) {
            _columnType[i] = ColumnTypes.SHORT;
         } else if (type.equalsIgnoreCase(LONG_TYPE)) {
            _columnType[i] = ColumnTypes.LONG;
         } else if (type.equalsIgnoreCase(BYTE_TYPE)) {
            _columnType[i] = ColumnTypes.BYTE;
         } else if (type.equalsIgnoreCase(CHAR_TYPE)) {
            _columnType[i] = ColumnTypes.CHAR;
         } else if (type.equalsIgnoreCase(BYTE_ARRAY_TYPE)) {
            _columnType[i] = ColumnTypes.BYTE_ARRAY;
         } else if (type.equalsIgnoreCase(CHAR_ARRAY_TYPE)) {
            _columnType[i] = ColumnTypes.CHAR_ARRAY;
         } else if (type.equalsIgnoreCase(BOOLEAN_TYPE)) {
            _columnType[i] = ColumnTypes.BOOLEAN;
         } else {
            _columnType[i] = ColumnTypes.STRING;
         }

      } // end for
   } // end method setColumnTypes

   /**
    * Skip to lineNum.
    *
    * @param lineNum where to go.
    */
   private void skipToLine(int lineNum) {

      try {

         if (lineNum < _reader.getLineNumber()) {
            _reader = new LineNumberReader(new FileReader(_file));

            int ctr = 0;

            while (ctr < lineNum) {
               _reader.readLine();
               ctr++;
            }
         }
      } catch (Exception e) { }
   }

   /**
    * Clean up. Close the Data Object Proxy.
    */
   public void finalize() {

      if (_dop != null) {
         _dop.close();
         _dop = null;
      }
   }

   /**
    * Get the label for column i.
    *
    * @param  i The column.
    *
    * @return The label.
    */
   public String getColumnLabel(int i) {

      if (_columnLabels == null) {
         return "column_" + i;
      }

      if (_columnLabels[i] == null || _columnLabels[i].length() == 0) {
         return "column_" + i;
      } else {
         return _columnLabels[i];
      }
   }

   /**
    * Get the type for column i.
    *
    * @param  i The column.
    *
    * @return The type.
    */
   public int getColumnType(int i) {

      if (_columnType == null) {
         return -1;
      }

      return _columnType[i];
   }

   /**
    * Get the number of columns.
    *
    * @return the number of columns in the file
    */
   public int getNumColumns() { return _noOfColumns; }

   /**
    * Get the number of rows.
    *
    * @return the number of rows in the file.
    */
   public int getNumRows() { return (int) _tableLength; }

   /**
    * public char[][] getRowElements(int i) {.
    *
    * @param  i Description of parameter i.
    *
    * @return Description of return value.
    */
   public ParsedLine getRowElements(int i) {

      try {
         return parseLine(i);
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

} // end class FixedFileParserFromURL
