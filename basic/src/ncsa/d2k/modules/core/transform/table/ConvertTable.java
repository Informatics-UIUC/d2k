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

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Sparse;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;


/**
 * Convert a table from one format to another.
 *
 * <p>Title:</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company:</p>
 *
 * @author  not attributable
 * @version 1.0
 */
public class ConvertTable extends ComputeModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      Table orig = (Table) pullInput(0);
      TableFactory factory = (TableFactory) pullInput(1);

      int numColumns = orig.getNumColumns();
      int numRows = orig.getNumRows();

      MutableTable newTable = (MutableTable) factory.createTable();
      // newTable.setNumRows(numRows);

      for (int i = 0; i < numColumns; i++) {

         // System.out.println("converting column: "+i);
         int type = orig.getColumnType(i);
         Column newcol = factory.createColumn(type);

         // newcol.setNumRows(sparseOrig.getColumnNumEntries(i));
         newcol.setNumRows(numRows);
         newcol.setLabel(orig.getColumnLabel(i));
         newcol.setComment(orig.getColumnComment(i));

         switch (type) {

            case ColumnTypes.FLOAT:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setFloat(orig.getFloat(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setFloat(orig.getFloat(j, i), j);
                  }
               }

               break;

            case ColumnTypes.DOUBLE:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setDouble(orig.getDouble(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setDouble(orig.getDouble(j, i), j);
                  }
               }

               break;

            case ColumnTypes.SHORT:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setShort(orig.getShort(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setShort(orig.getShort(j, i), j);
                  }
               }

               break;

            case ColumnTypes.STRING:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setString(orig.getString(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setString(orig.getString(j, i), j);
                  }
               }

               break;

            case ColumnTypes.LONG:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setLong(orig.getLong(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setLong(orig.getLong(j, i), j);
                  }
               }

               break;

            case ColumnTypes.BYTE:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setByte(orig.getByte(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setByte(orig.getByte(j, i), j);
                  }
               }

               break;

            case ColumnTypes.CHAR:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setChar(orig.getChar(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setChar(orig.getChar(j, i), j);
                  }
               }

               break;

            default:

               if (orig instanceof Sparse) {
                  int[] rows = ((Sparse) orig).getColumnIndices(i);

                  for (int j = 0; j < rows.length; j++) {
                     newcol.setObject(orig.getObject(rows[j], i), rows[j]);
                  }
               } else {

                  for (int j = 0; j < numRows; j++) {
                     newcol.setObject(orig.getObject(j, i), j);
                  }
               }

               break;
         }

         newTable.addColumn(newcol);
      } // end for

      newTable.setLabel(orig.getLabel());
      newTable.setComment(orig.getComment());

      // missing values preservation
      for (int i = 0; i < orig.getNumRows(); i++) {

         for (int j = 0; j < orig.getNumColumns(); j++) {

            if (orig.isValueEmpty(i, j)) {
               newTable.setValueToEmpty(true, i, j);
            }

            if (orig.isValueMissing(i, j)) {
               newTable.setValueToMissing(true, i, j);
            }
         }
      }

      // System.out.println("pushing converted table.");

      if (orig instanceof ExampleTable) {
         int[] inputs = ((ExampleTable) orig).getInputFeatures();
         int[] newinputs = new int[inputs.length];
         System.arraycopy(inputs, 0, newinputs, 0, inputs.length);

         int[] outputs = ((ExampleTable) orig).getOutputFeatures();
         int[] newoutputs = new int[outputs.length];
         System.arraycopy(outputs, 0, newoutputs, 0, outputs.length);

         ExampleTable newExampleTable = newTable.toExampleTable();
         newExampleTable.setInputFeatures(newinputs);
         newExampleTable.setOutputFeatures(newoutputs);

         if (orig instanceof PredictionTable) {
            PredictionTable newPredTable = newExampleTable.toPredictionTable();
            int[] preds = ((PredictionTable) orig).getPredictionSet();
            int[] newpreds = new int[preds.length];
            System.arraycopy(preds, 0, newpreds, 0, preds.length);
            pushOutput(newPredTable, 0);
         } else {
            pushOutput(newExampleTable, 0);
         }
      } else {
         pushOutput(newTable, 0);
      }
   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      if (i == 0) {
         return "Original Table";
      }

      return "TableFactory";
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      if (i == 0) {
         return "Table";
      } else if (i == 1) {
         return "Table Factory";
      }

      return null;
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return new String[] {
                                               "ncsa.d2k.modules.core.datatype.table.Table",
                                               "ncsa.d2k.modules.core.datatype.table.TableFactory"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: Converts a table from one format to another using a TableFactory.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Convert Table"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return "Copied Table"; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      if (i == 0) {
         return "Table";
      }

      return null;
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "ncsa.d2k.modules.core.datatype.table.Table"
                                             }; }
} // end class ConvertTable
