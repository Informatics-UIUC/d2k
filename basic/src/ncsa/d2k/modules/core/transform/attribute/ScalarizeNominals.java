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
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This module examines columns in a <code>MutableTable</code> and, for
 * appropriate columns which contain nominal values, converts these single
 * columns into multiple columns (of <code>boolean</code>s or <code>int</code>s)
 * -- one for each possible value of the attribute.
 *
 * <p>If the input <code>MutableTable</code> implements the <code>
 * ExampleTable</code> interface, only columns marked as inputs and outputs will
 * be converted. Otherwise, all columns containing nominal values will be
 * converted.</p>
 *
 * <p>Through a property of the module, a user can select whether the generated
 * columns are of type <code>boolean</code> or <code>int</code>.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ScalarizeNominals extends DataPrepModule {

   //~ Instance fields *********************************************************

   ////////////////////////////////////////////////////////////////////////////////
   // properties
   // //
   ////////////////////////////////////////////////////////////////////////////////

   /**
    * Controls whether converted nominal columns will have scalar type boolean
    * (true) or type double (false).
    */
   private boolean _newTypeBoolean = true;

   //~ Methods *****************************************************************

   ////////////////////////////////////////////////////////////////////////////////
   // doit()
   // //
   ////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      MutableTable table = (MutableTable) pullInput(0);

      int[] indices;
      int[] origInputs = null;
      int[] origOutputs = null;

      // columns with blank labels need to be assigned default ones

      for (int i = 0; i < table.getNumColumns(); i++) {
         String s = table.getColumnLabel(i);

         if (s == null || s.length() == 0) {
            table.setColumnLabel("column_" + i, i);
         }
      }

      // determine which columns we wish to transform

      boolean tableIsExample = false;

      if (table instanceof ExampleTable) {

         tableIsExample = true;

         ExampleTable et = (ExampleTable) table;

         origInputs = new int[et.getInputFeatures().length];

         for (int i = 0; i < origInputs.length; i++) {
            origInputs[i] = et.getInputFeatures()[i];
         }

         origOutputs = new int[et.getOutputFeatures().length];

         for (int i = 0; i < origOutputs.length; i++) {
            origOutputs[i] = et.getOutputFeatures()[i];
         }

         // ensure unique column indices

         HashMap uniqueIndexMap = new HashMap();

         for (int i = 0; i < origInputs.length; i++) {

            if (et.isColumnNominal(origInputs[i])) {
               uniqueIndexMap.put(new Integer(origInputs[i]), null);
            }
         }

         for (int i = 0; i < origOutputs.length; i++) {

            if (et.isColumnNominal(origOutputs[i])) {
               uniqueIndexMap.put(new Integer(origOutputs[i]), null);
            }
         }

         // retrieve column indices

         indices = new int[uniqueIndexMap.size()];

         int index = 0;

         Iterator iterator = uniqueIndexMap.keySet().iterator();

         while (iterator.hasNext()) {
            indices[index++] = ((Integer) iterator.next()).intValue();
         }

         Arrays.sort(indices);
      } else {

         // simply iterate to find nominal columns

         int numNominalColumns = 0;

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (table.isColumnNominal(i)) {
               numNominalColumns++;
            }
         }

         indices = new int[numNominalColumns];

         int index = 0;

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (table.isColumnNominal(i)) {
               indices[index++] = i;
            }
         }

      }

      // iterate and replace

      int offset = 0; // number of extra columns added to the table. must be

      // added to column indices in order to keep consistent
      int numRows = table.getNumRows();

      for (int count = 0; count < indices.length; count++) {
         int index = indices[count] + offset;

         // find this column's unique values
         HashMap uniqueValuesMap = new HashMap();
         int uniqueValueCount = 0;

         for (int row = 0; row < numRows; row++) {

            if (table.isValueMissing(row, index)) {
               continue;
            }

            String s = table.getString(row, index);

            if (s == null || s.length() == 0) {
               continue;
            }

            if (uniqueValuesMap.containsKey(s)) {
               continue;
            }

            uniqueValuesMap.put(s, new Integer(uniqueValueCount++));
         }

         if (uniqueValuesMap.size() == 0) {

            // nothing (or only missing) here
            continue;
         } else {

            // first, we'd like our string-to-integer mappings as arrays,
            // for efficiency

            String[] uniqueValues = new String[uniqueValuesMap.size()];
            int[] uniqueValueIndices = new int[uniqueValues.length];

            Iterator iterator = uniqueValuesMap.keySet().iterator();
            int iteratorCount = 0;

            while (iterator.hasNext()) {
               uniqueValues[iteratorCount++] = (String) iterator.next();
            }

            for (int i = 0; i < uniqueValues.length; i++) {
               uniqueValueIndices[i] =
                  ((Integer) uniqueValuesMap.get(uniqueValues[i])).intValue();
            }

            // we also want an indirection array so we can act as if these
            // mappings were sorted on the integer value

            int[] indirection = new int[uniqueValueIndices.length];

            for (int i = 0; i < uniqueValueIndices.length; i++) {
               indirection[uniqueValueIndices[i]] = i;
            }

            // now create one array for the entire column specifying which
            // unique value is contained in each row. if the value is missing
            // or empty, set to -1.

            int[] match = new int[numRows];
            boolean[] missing = new boolean[numRows];

            for (int row = 0; row < numRows; row++) {

               if (
                   table.isValueMissing(row, index) ||
                      table.isValueEmpty(row, index)) {
                  match[row] = -1;
                  missing[row] = true;

                  continue;
               }

               String s = table.getString(row, index);
               missing[row] = false;

               for (int j = 0; j < uniqueValues.length; j++) {

                  if (s.equals(uniqueValues[indirection[j]])) {
                     match[row] = indirection[j];

                     break;
                  }
               }

            }

            // !:
            // are we dealing with an ExampleTable? if so, is the old column
            // an input, output, or both?

            boolean isInput = false;
            boolean isOutput = false;

            if (tableIsExample) {

               ExampleTable et = (ExampleTable) table;

               for (int i = 0; i < origInputs.length; i++) {

                  if (origInputs[i] == indices[count]) {
                     isInput = true;

                     break;
                  }
               }

               for (int i = 0; i < origOutputs.length; i++) {

                  if (origOutputs[i] == indices[count]) {
                     isOutput = true;

                     break;
                  }
               }

            }

            // remove the old column

            String columnLabel = table.getColumnLabel(index);
            Column oldColumn = table.getColumn(index);
            table.removeColumn(index);
            offset--;

            // iterate and create the new columns

            for (int k = 0; k < uniqueValues.length; k++) {

               if (_newTypeBoolean) { // create new columns as type boolean

                  boolean[] newColumn = new boolean[numRows];

                  for (int row = 0; row < match.length; row++) {

                     if (missing[row]) {
                        newColumn[row] = table.getMissingBoolean();
                     } else if (match[row] == k) {
                        newColumn[row] = true;
                     } else {
                        newColumn[row] = false;
                     }
                  }

                  // BooleanColumn column =
                  // (BooleanColumn)ColumnUtilities.toBooleanColumn(oldColumn);
                  Column column = ColumnUtilities.toBooleanColumn(oldColumn);
                  int where = index + k;
                  column.setLabel(columnLabel + "=" + uniqueValues[k]);
                  table.insertColumn(column, where);

                  for (int i = 0; i < newColumn.length; i++) {
                     table.setBoolean(newColumn[i], i, where);
                  }
               } else { // create new columns as type int

                  double[] newColumn = new double[numRows];

                  for (int row = 0; row < match.length; row++) {

                     if (missing[row]) {
                        newColumn[row] = table.getMissingDouble();
                     } else if (match[row] == k) {
                        newColumn[row] = 1;
                     } else {
                        newColumn[row] = 0;
                     }
                  }

// VEREd GOREN - column is now of type Column, so that it supports also sparse
// tables. DoubleColumn column = ColumnUtilities.toDoubleColumn(oldColumn);
                  Column column = ColumnUtilities.toDoubleColumn(oldColumn);
                  int where = index + k;
                  column.setLabel(columnLabel + "=" + uniqueValues[k]);
                  table.insertColumn(column, where);

                  for (int i = 0; i < newColumn.length; i++) {
                     table.setDouble(newColumn[i], i, where);
                  }
               } // end if

               offset++;

               // !: we now must add this new column to the list of
               // inputs/outputs if we are dealing with an ExampleTable. this
               // isn't very efficient; maybe we should modify the API to handle
               // this
               if (tableIsExample) {

                  ExampleTable et = (ExampleTable) table;

                  if (isInput) {

                     int[] inputs = et.getInputFeatures();
                     int[] newInputs = new int[inputs.length + 1];

                     for (int i = 0; i < inputs.length; i++) {
                        newInputs[i] = inputs[i];
                     }

                     newInputs[inputs.length] = index + k;

                     Arrays.sort(newInputs);
                     et.setInputFeatures(newInputs);
                  }

                  if (isOutput) {

                     int[] outputs = et.getOutputFeatures();
                     int[] newOutputs = new int[outputs.length + 1];

                     for (int i = 0; i < outputs.length; i++) {
                        newOutputs[i] = outputs[i];
                     }

                     newOutputs[outputs.length] = index + k;

                     Arrays.sort(newOutputs);
                     et.setOutputFeatures(newOutputs);
                  }
               } // end if
            } // end for
         } // end if
      } // end for

      pushOutput(table, 0);
   } // end method doit

   ////////////////////////////////////////////////////////////////////////////////
   // Module methods
   // //
   ////////////////////////////////////////////////////////////////////////////////

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      if (index == 0) {
         return "A <i>MutableTable</i> (possibly an <i>ExampleTable</i>).";
      }

      return "NO SUCH INPUT";
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

      if (index == 0) {
         return "Mutable Table";
      }

      return "NO SUCH INPUT";
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return new String[] {
                                               "ncsa.d2k.modules.core.datatype.table.MutableTable"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module examines columns in a <i>MutableTable</i> and, ");
      sb.append("for appropriate columns which contain nominal values, ");
      sb.append("converts these single columns into multiple columns -- one ");
      sb.append("for each possible value of the attribute.");
      sb.append("</p><p>Detailed Description: ");
      sb.append("If the input <i>MutableTable</i> implements the ");
      sb.append("<i>ExampleTable</i> interface, only columns marked as ");
      sb.append("inputs and outputs will be converted. Otherwise, all ");
      sb.append("columns containing nominal values will be converted. ");
      sb.append("Through a property of the module, the user can select ");
      sb.append("whether the generated columns are double or boolean.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module modifies its input data; each relevant nominal ");
      sb.append("column may be replaced with an arbitrary number of new ");
      sb.append("ones. In addition, columns with blank labels are assigned ");
      sb.append("default ones.");
      sb.append("</p>");

      sb.append("<P>Missing Values Handling: Missing values are preserved by this " +
                "module. A missing value in a certain nominal column will have matching " +
                "missing values in the respective scalarized columns.");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Scalarize Nominals"; }

   /**
    * Get newTypeBoolean.
    *
    * @return value of newTypeBoolean
    */
   public boolean getNewTypeBoolean() { return _newTypeBoolean; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      if (index == 0) {
         StringBuffer sb = new StringBuffer();
         sb.append("The input <i>MutableTable</i> with appropriate nominal ");
         sb.append("columns transformed.");

         return sb.toString();
      }

      return "NO SUCH OUTPUT";
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

      if (index == 0) {
         return "Scalarized Mutable Table";
      }

      return "NO SUCH OUTPUT";
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "ncsa.d2k.modules.core.datatype.table.MutableTable"
                                             }; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription newTypeBooleanDesc =
         new PropertyDescription("newTypeBoolean",
                                 "Create new columns as type boolean",
                                 "Controls whether converted nominal columns will have scalar type " +
                                 "boolean (true) or type double (false).");

      return new PropertyDescription[] { newTypeBooleanDesc };

   }

   /**
    * Set newTypeBoolean.
    *
    * @param value new newTypeBoolean
    */
   public void setNewTypeBoolean(boolean value) { _newTypeBoolean = value; }
} // end class ScalarizeNominals
