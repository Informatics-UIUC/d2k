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
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;


/**
 * A FlatFileReader that reads an ARFF File. This is a delimited file with extra
 * metadata included.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * ARFFFileParser</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 *
 * <p>References:<br>
 * </p>
 *
 * <p>The ARFF Format: http://www.cs.waikato.ac.nz/~ml/weka/arff.html The Weka
 * Project: http://www.cs.waikato.ac.nz/~ml/weka<br>
 * </p>
 *
 * @author  unascribed
 * @version 1.0
 */
public class ARFFFileParserFromURL extends DelimitedFileParserFromURL {

   //~ Static fields/initializers **********************************************

   /** Symbol OPEN. */
   static private final String OPEN = "{";

   /** Symbol CLOSE. */
   static private final String CLOSE = "}";

   /** Symbol STRING. */
   static private final String STRING = "string";

   /** Symbol DATE. */
   static private final String DATE = "date";

   /** Symbol RELATION. */
   static private final String RELATION = "relation";

   /** Symbol COMMENT. */
   static private final String COMMENT = "%";

   /** Symbol REAL. */
   static private final String REAL = "real";

   /** Symbol DATA. */
   static private final String DATA = "data";

   /** Symbol FLAG. */
   static private final String FLAG = "@";

   /** Symbol NUMERIC. */
   static private final String NUMERIC = "numeric";

   /** Symbol ATTRIBUTE. */
   static private final String ATTRIBUTE = "attribute";

   /** Symbol MISSING. */
   static private final String MISSING = "?";

   /** Symbol DATA_TAG. */
   static private final String DATA_TAG = FLAG + DATA;

   /** Symbol ATTRIBUTE_TAG. */
   static private final String ATTRIBUTE_TAG = FLAG + ATTRIBUTE;

   /** Symbol QUESTION. */
   static private final char QUESTION = '?';

   //~ Instance fields *********************************************************

   /**
    * NOTE: allowedAttributes currently not implemented; Nominals are treated as
    * strings without checks for specified values.
    */
   private HashSet[] allowedAttributes;

   /** current dataRow. */
   private int dataRow;

   /** Local file to parse. Remote file is fetched to a cached copy. */
   private File file;

   //~ Constructors ************************************************************

   /**
    * Creates a new ARFFFileParserFromURL object.
    *
    * @param  dop Description of parameter dop.
    *
    * @throws Exception             Misc. Exception.
    * @throws FileNotFoundException Error finding local file.
    */
   public ARFFFileParserFromURL(DataObjectProxy dop) throws Exception {
      mDataObj = dop;
      file = mDataObj.getLocalFile();

      FileReader filereader = null;

      try {
         filereader = new FileReader(file);
      } catch (FileNotFoundException e) {
         throw new FileNotFoundException("ARFF File Parser: " +
                                         "Could not open file: " + file +
                                         "\n" + e);
      }

      lineReader = new LineNumberReader(filereader);

      try {
         initialize();
      } catch (Exception e) {
         throw new Exception("ARFF File Parser: " +
                             "Problems parsing ARFF file: " + file +
                             "\n" + e);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Initialize the state of the parser.
    *
    * @throws Exception Error reading the data.
    */
   private void initialize() throws Exception {
      ArrayList attributes = new ArrayList();
      ArrayList types = new ArrayList();

      // find all the attributes
      String line = lineReader.readLine();

      while (line.toLowerCase().indexOf(DATA_TAG) == -1) {

         if (line.toLowerCase().indexOf(ATTRIBUTE_TAG) != -1) {

            // drop the attribute tag, find the attribute name, type
            // if it is nominal, add its values to the allowedAttributes.
            // NOTE: allowedAttributes currently not updated
            parseAttributeLine(line, attributes, types);
         }

         line = lineReader.readLine();

         if (line == null) {
            throw new Exception("Keyword DATA not found in input file");
         }
      }

      // now we have the names of the attributes and the types.
      columnLabels = new String[attributes.size()];
      columnTypes = new int[attributes.size()];

      // NOTE: allowedAttributes currently not updated
      allowedAttributes = new HashSet[attributes.size()];

      for (int i = 0; i < attributes.size(); i++) {
         columnLabels[i] = (String) attributes.get(i);

         String typ = (String) types.get(i);

         if (
             (typ.indexOf(NUMERIC) != -1 || typ.indexOf(REAL) != -1) &&
                typ.indexOf(OPEN) == -1 &&
                typ.indexOf(CLOSE) == -1) {
            columnTypes[i] = ColumnTypes.DOUBLE;
         } else if (
                    typ.indexOf(STRING) != -1 &&
                       typ.indexOf(OPEN) == -1 &&
                       typ.indexOf(CLOSE) == -1) {
            columnTypes[i] = ColumnTypes.STRING;
         } else if (
                    typ.indexOf(DATE) != -1 &&
                       typ.indexOf(OPEN) == -1 &&
                       typ.indexOf(CLOSE) == -1) {
            columnTypes[i] = ColumnTypes.STRING;
         } else {
            columnTypes[i] = ColumnTypes.STRING;

            // parse allowed values
            // NOTE: allowedAttributed currently not updated
            allowedAttributes[i] = parseAllowedAttributes(line);
         }
      } // end for

      // now count the number of data lines
      int ctr = 0;

      while ((line = lineReader.readLine()) != null) {

         if (line.trim().length() != 0 && !line.startsWith(COMMENT)) {
            ctr++;
         }
      }

      numRows = ctr;
      numColumns = columnLabels.length;
      delimiter = COMMA;

      // now reset the reader and read in comments and find index of the @data
      // tag
      resetReader();

      boolean done = false;

      while (!done) {
         line = lineReader.readLine().toLowerCase();

         if (line.indexOf(DATA_TAG) != -1) {
            dataRow++;
            done = true;
         } else {
            dataRow++;
         }
      }

   } // end method initialize

   /**
    * NOTE: This method currently not doing anything.
    *
    * @param  line Description of parameter line.
    *
    * @return Description of return value.
    */
   private HashSet parseAllowedAttributes(String line) { return null; }

   /**
    * Parse the attributes from the attribute line.
    *
    * @param line  Description of parameter line.
    * @param atts  Description of parameter atts.
    * @param types Description of parameter types.
    */
   private void parseAttributeLine(String line, ArrayList atts,
                                   ArrayList types) {

      // Create a tokenizer that breaks up line based on blank space
      StringTokenizer st = new StringTokenizer(line);

      int ctr = 0;

      while (st.hasMoreTokens()) {
         String tok = st.nextToken();

         // this will be the name of the attribute.
         // if it's a quoted string we need special processing so that
         // we don't just break on whitespace.  Also, don't include quote
         // characters in the attribute name
         if (ctr == 1) {

            if (tok.startsWith("\"") || tok.startsWith("'")) {
               String attName = tok;
               String quoteChar = attName.substring(0, 1);

               // If we don't have quotes around a string w/o blanks, get
               // the rest of the attribute name;  add the quoteChar at the
               // end so we have attName w/ quotes
               if (!attName.endsWith(quoteChar)) {
                  attName = attName + st.nextToken(quoteChar);
                  attName += quoteChar;
               }

               atts.add(attName.substring(1, attName.length() - 1));
            } else {
               atts.add(tok);
            }

            // this is the datatype of the attribute. we trim it because there
            // may be extra spaces at the front if we had a quoted attribute
            // name.
         } else if (ctr == 2) {
            types.add((tok.trim()).toLowerCase());
         }

         ctr++;
      } // end while
   } // end method parseAttributeLine


   /**
    * Reset the reader to the beginning of the input file..
    */
   private void resetReader() {

      try {
         lineReader = new LineNumberReader(new FileReader(file));
      } catch (Exception e) { }
   }

   /**
    * Skip to a specific row in the file. Rows are lines of data in the file,
    * not including the optional meta data rows. When a comment is found, the
    * datarow index is incremented to account for it. This will fail if the file
    * is not accessed in a serial fashion.
    *
    * @param  rowNum the row number to skip to
    *
    * @return skip to a specific row in the file. Rows are lines of data in the
    *         file, not including the optional meta data rows. When a comment is
    *         found, the datarow index is incremented to account for it. This
    *         will fail if the file is not accessed in a serial fashion.
    */
   protected String skipToRow(int rowNum) {
      rowNum += dataRow;

      try {

         if (rowNum < lineReader.getLineNumber()) {
            lineReader = new LineNumberReader(new FileReader(file));
         }

         int current = lineReader.getLineNumber();

         while (current < rowNum - 1) {
            lineReader.readLine();
            current++;
         }

         String line;

         while (
                (line = lineReader.readLine()).startsWith(COMMENT) ||
                   line.trim().length() == 0) {
            dataRow++;
         }

         return line;
      } catch (Exception e) {
         return null;
      }
   } // end method skipToRow

   /**
    * Clean up--close the data object proxy.
    */
   public void finalize() {

      if (mDataObj != null) {
         mDataObj.close();
         mDataObj = null;
      }
   }

   /**
    * Get the elements that make up row i of the file.
    *
    * @param  i Description of parameter i.
    *
    * @return the elements of row i in the file.
    */
   public ParsedLine getRowElements(int i) {
      ParsedLine retVal = super.getRowElements(i);

      if (
          retVal != null &&
             retVal.elements != null &&
             retVal.elements.length > 0) {

         // here we check each element to see if it was a missing value
         for (int j = 0; j < this.numColumns; j++) {

            if (
                retVal.elements[j].length > 0 &&
                   retVal.elements[j][0] == QUESTION) {

               retVal.blanks[j] = true;
            }
         }
      }

      return retVal;
   }
} // end class ARFFFileParserFromURL
