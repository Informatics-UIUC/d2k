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
package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;

import java.util.HashMap;


/**
 * Fill missing values with some user selected default values.
 *
 * @author  redman
 * @version $Revision$, $Date$
 */
public class PopulateMissingValues
   extends ncsa.d2k.core.modules.DataPrepModule {

   //~ Instance fields *********************************************************

   /** the boolean to replace missing values with. */
   private boolean fillerBool = false;

   /** the byte[] to replace missing values with. */
   private byte[] fillerByte = new byte[1];

   /** the char[] to replace missing values with. */
   private char[] fillerChar = new char[1];

   /** the int to replace missing values with. */
   private int fillerNumeric = 0;

   /** the byte to replace missing values with. */
   private byte fillerSingleByte = '\000';

   /** the char to replace missing values with. */
   private char fillerSingleChar = '\000';

   /** the string to replace missing values with. */
   private String fillerString = new String("*");

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module. Loop through the table and replace
    * missing values with the appropriate filler value based on the datatype.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      MutableTable missingTable = (MutableTable) this.pullInput(0);
      int numColumns = missingTable.getNumColumns();

      // Populate a hashtable with the names of all the columns we will
      // populate.
      HashMap columns = new HashMap();

      if (this.isInputPipeConnected(1)) {
         String[] colNames = (String[]) this.pullInput(1);

         for (int i = 0; i < colNames.length; i++) {
            columns.put(colNames[i], colNames[i]);
         }
      } else {

         // We will do ALL the columns.
         for (int i = 0; i < numColumns; i++) {
            String lbl = missingTable.getColumnLabel(i);
            columns.put(lbl, lbl);
         }
      }

      // for each column
      for (int col = 0; col < numColumns; col++) {
         String lbl = missingTable.getColumnLabel(col);

         // if this is not a column of interest, go on.
         if (columns.get(lbl) == null) {
            continue;
         }

         switch (missingTable.getColumnType(col)) {

            case ColumnTypes.INTEGER:
            case ColumnTypes.FLOAT:
            case ColumnTypes.DOUBLE:
            case ColumnTypes.SHORT:
            case ColumnTypes.LONG:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setInt(fillerNumeric, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.STRING:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setString(fillerString, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.CHAR_ARRAY:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setChars(fillerChar, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.BYTE_ARRAY:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setBytes(fillerByte, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.BOOLEAN:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setBoolean(fillerBool, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.OBJECT:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setString(fillerString, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.BYTE:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setByte(this.fillerSingleByte, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            case ColumnTypes.CHAR:

               for (int row = 0; row < missingTable.getNumRows(); row++) {

                  if (missingTable.isValueMissing(row, col)) {
                     missingTable.setChar(fillerSingleChar, row, col);
                     missingTable.setValueToMissing(false, row, col);
                  }
               }

               break;

            default: {
               throw new Exception("Datatype for Table column named '" +
                                   missingTable.getColumn(col).getLabel() +
                                   "' was not recognized when appending tables.");
            }
         }
      } // end for

      // Done!
      this.pushOutput(missingTable, 0);
   } // end method doit

   /**
    * Get the boolean to replace missing with.
    *
    * @return the boolean to replace missing with
    */
   public boolean getFillerBool() { return fillerBool; }

   /**
    * Get the byte to replace missing with.
    *
    * @return the byte to replace missing with
    */
   public byte getFillerByte() { return fillerSingleByte; }

   /**
    * Get the bytes to replace missing with.
    *
    * @return the bytes to replace missing with
    */
   public byte[] getFillerBytes() { return fillerByte; }

   /**
    * Get the char to replace missing with.
    *
    * @return the char to replace missing with
    */
   public char getFillerChar() { return fillerSingleChar; }

   /**
    * Get the chars to replace missing with.
    *
    * @return the chars to replace missing with
    */
   public char[] getFillerChars() { return fillerChar; }

   /**
    * Get the int to replace missing with.
    *
    * @return the int to replace missing with
    */
   public int getFillerNumeric() { return fillerNumeric; }

   /**
    * Get the string to replace missing with.
    *
    * @return the string to replace missing with
    */
   public String getFillerString() { return fillerString; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>Table containing missing value to be replaced.</p>";

         case 1:
            return "<p>" +
                   "      This input contains the list of columns to populate missing values in. " +
                   "      It is optional, if absent all columns will be populated." +
                   "    </p>";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Missing Value Table";

         case 1:
            return "Column Names";

         default:
            return "NO SUCH INPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.table.Table",
         "[Ljava.lang.String;"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module will replace missing values with values provided " +
             "      in the properties." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: This module takes two inputs. They are the table " +
             "      containing the missing values and a list of column labels identifying " +
             "      columns to operate on. In each of those columns identified, all of the " +
             "      missing values in that column will be replaced. Missing values are " +
             "      replaced with values specified in properties of this module." +
             "    </p>" +
             "    <p>" +
             "      Data Type Restrictions: If input table columns have the same name but " +
             "      different data types, the data type from the <i>First Table</i> is used " +
             "      in the result table, and an attempt is made to convert the data values " +
             "      from the <i>Second Table</i> to that type. This conversion may result in " +
             "      unexpected values in the output table. In some cases, such as when a " +
             "      string cannot be converted to a numeric, an exception will be raised. " +
             "      The user is discouraged from appending tables containing attributes with " +
             "      the same name whose types differ. For some conversions, for example when " +
             "      an integer is converted to a double, there may be no loss of data, but " +
             "      the user should verify the result table has the expected values." +
             "    </p>" +
             "    <p>" +
             "      Data Handling: This module does modify the input table.." +
             "    </p>" +
             "    <p>" +
             "      Scalability: This module operates using only table methods, no " +
             "      additional memory is required." +
             "    </p>";
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Populate Missing Values"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "<p>Newly created table containing no missing values.</p>";

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Result Table";

         default:
            return "NO SUCH OUTPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] =
         new PropertyDescription("fillerBool",
                                 "Boolean Filler",
                                 "The value used to fill boolean columns.");
      pds[1] =
         new PropertyDescription("fillerString",
                                 "String Filler",
                                 "The value used to fill string columns.");
      pds[2] =
         new PropertyDescription("fillerNumeric",
                                 "Numeric Filler",
                                 "The value used to fill numeric columns.");

      return pds;
   }


   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (this.isInputPipeConnected(1)) {
         return this.getInputPipeSize(0) > 0 && this.getInputPipeSize(1) > 0;
      } else {
         return this.getInputPipeSize(0) > 0;
      }
   }

   /**
    * Set the boolean to replace missing with.
    *
    * @param a value to replace missing values with.
    */
   public void setFillerBool(boolean a) { fillerBool = a; }

   /**
    * Set the byte to replace missing with.
    *
    * @param d value to replace missing values with.
    */
   public void setFillerByte(byte d) { fillerSingleByte = d; }

   /**
    * Set the bytes to replace missing with.
    *
    * @param c value to replace missing values with.
    */
   public void setFillerBytes(byte[] c) { fillerByte = c; }

   /**
    * Set the char to replace missing with.
    *
    * @param d value to replace missing values with.
    */
   public void setFillerChar(char d) { fillerSingleChar = d; }

   /**
    * Set the chars to replace missing with.
    *
    * @param d value to replace missing values with.
    */
   public void setFillerChars(char[] d) { fillerChar = d; }

   /**
    * Set the int to replace missing with.
    *
    * @param b value to replace missing values with.
    */
   public void setFillerNumeric(int b) { fillerNumeric = b; }

   /**
    * Set the string to replace missing with.
    *
    * @param x value to replace missing values with.
    */
   public void setFillerString(String x) { fillerString = x; }
} // end class PopulateMissingValues
