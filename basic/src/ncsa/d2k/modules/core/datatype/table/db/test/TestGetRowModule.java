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
package ncsa.d2k.modules.core.datatype.table.db.test;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionExample;
import ncsa.d2k.modules.core.datatype.table.Row;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.util.*;//using D2KModuleLogger and Factory


/**
 * Tests the <code>getRow()</code> method of a <code>Table</code>. It retrieves
 * a <code>Row</code> object from the input <code>Table</code> and iterates over
 * all the items in the table.
 *
 * @author  goren
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class TestGetRowModule extends ComputeModule {
	private D2KModuleLogger myLogger =
		D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

	private int moduleLoggingLevel=
		D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
		.getLoggingLevel();
	
   //~ Constructors ************************************************************

   /**
    * Creates a new <code>TestGetRowModule</code> object.
    */
   public TestGetRowModule() { }

   //~ Methods *****************************************************************

   
   public int getmoduleLoggingLevel(){
	   return moduleLoggingLevel;
   }

   public void setmoduleLoggingLevel(int level){
	   moduleLoggingLevel = level;
   }   
   
   public void beginExecution() {
	   myLogger.setLoggingLevel(moduleLoggingLevel);
   }

   /**
    * Performs the main work of the module. In this case, retrieves a <code>
    * Row</code> object from the input <code>Table</code>, iterates over all the
    * elements in the <code>Table</code> while printing the content.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   public void doit() {
      Table table = (Table) this.pullInput(0);
      System.out.println(table.getClass().getName());

      Row row = table.getRow();

      int numColumns = table.getNumColumns();
      int numRows = table.getNumRows();

      myLogger.debug("Table type: " + table.getClass().getName());
      myLogger.debug("\nPrinting content of the table. " +
              "Accessing via row\n\n");
      //System.out.println("Table type: " + table.getClass().getName());
      //System.out.println("\nPrinting content of the table. " +
      //                   "Accessing via row\n\n");

      for (int i = 0; i < numRows; i++) {
         row.setIndex(i);
         myLogger.debug("Content of row no. " + i);
         //System.out.println("Content of row no. " + i);

         for (int j = 0; j < numColumns; j++) {
        	 myLogger.debug(row.getString(j) + "\t");
            //System.out.print(row.getString(j) + "\t");
         }

         myLogger.debug("\n");
         //System.out.println();
      }

      int inputs;
      int outputs = 0;

      if (row instanceof Example) {
         Example ex = (Example) row;
         inputs = ((ExampleTable) table).getNumInputFeatures();
         outputs = ((ExampleTable) table).getNumOutputFeatures();
         myLogger.debug("\nThis row is an Example. " +
                            "Going over the input features and output features.");
         //System.out.println("\nThis row is an Example. " +
         //                 "Going over the input features and output features.");

         for (int i = 0; i < numRows; i++) {
            ex.setIndex(i);
            myLogger.debug("\nContent of input features of row no. " + i);
            //System.out.println("\nContent of input features of row no. " + i);

            for (int j = 0; j < inputs; j++) {
            	myLogger.debug(ex.getInputString(j) + "\t");
               //System.out.print(ex.getInputString(j) + "\t");
            }

            myLogger.debug("\n");
            myLogger.debug("Content of output features of row no. " + i);
            //System.out.println();
            //System.out.println("Content of output features of row no. " + i);

            for (int j = 0; j < outputs; j++) {
            	myLogger.debug(ex.getOutputString(j) + "\t");
               //System.out.print(ex.getOutputString(j) + "\t");
            }

            myLogger.debug("\n");
            //System.out.println();
         }

      }

      if (row instanceof PredictionExample) {
         PredictionExample pEx = (PredictionExample) row;
         myLogger.debug("\nThis row is a PredictionExample. " +
                 "Setting values in prediction columns");
         //System.out.println("\nThis row is a PredictionExample. " +
         //                   "Setting values in prediction columns");

         // assuming number of prediction features equals to number fo otput
         // features.
         for (int i = 0; i < numRows; i++) {
        	 myLogger.debug("\nPopulating row # " + i +
                     " of the prediction columns");
            //System.out.println("\nPopulating row # " + i +
            //                   " of the prediction columns");
            pEx.setIndex(i);

            for (int j = 0; j < outputs; j++) {

               switch (
                       table.getColumnType(((ExampleTable) table)
                                              .getOutputFeatures()[j])) {

                  case ColumnTypes.BOOLEAN:
                     pEx.setBooleanPrediction(pEx.getOutputBoolean(j), j);

                     break;

                  case ColumnTypes.BYTE:
                     pEx.setBytePrediction(pEx.getOutputByte(j), j);

                     break;

                  case ColumnTypes.BYTE_ARRAY:
                     pEx.setBytesPrediction(pEx.getOutputBytes(j), j);

                     break;

                  case ColumnTypes.DOUBLE:
                     pEx.setDoublePrediction(pEx.getOutputDouble(j), j);

                     break;

                  case ColumnTypes.CHAR:
                     pEx.setCharPrediction(pEx.getOutputChar(j), j);

                     break;

                  case ColumnTypes.CHAR_ARRAY:
                     pEx.setCharsPrediction(pEx.getOutputChars(j), j);

                     break;


                  case ColumnTypes.INTEGER:
                     pEx.setIntPrediction(pEx.getOutputInt(j), j);

                     break;

                  case ColumnTypes.FLOAT:
                     pEx.setFloatPrediction(pEx.getOutputFloat(j), j);

                     break;

                  case ColumnTypes.SHORT:
                     pEx.setShortPrediction(pEx.getOutputShort(j), j);

                     break;

                  case ColumnTypes.LONG:
                     pEx.setLongPrediction(pEx.getOutputLong(j), j);

                     break;

                  case ColumnTypes.OBJECT:
                     pEx.setObjectPrediction(pEx.getOutputObject(j), j);

                     break;

                  case ColumnTypes.STRING:
                     pEx.setStringPrediction(pEx.getOutputString(j), j);

                     break;

               }
            } // end for

         } // end for

      } // end if

      pushOutput(table, 0);

   } // end method doit


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  parm1 Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "Table to be tested";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  parm1 Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int parm1) {

      switch (parm1) {

         case 0:
            return "Table";

         default:
            return "No such input";
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
      String[] retVal = new String[1];
      retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";

      return retVal;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module tests the getRow method of a Table.\n" +
             "It retrieves a Row object from the input Table and iterates" +
             " over all the items in the table.</p>";
   }


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Test getRow Module"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int parm1) {

      switch (parm1) {

         case 0:
            return "Table";

         default:
            return "No such input";
      }

   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int parm1) {

      switch (parm1) {

         case 0:
            return "Table";

         default:
            return "No such input";
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
      String[] retVal = new String[1];
      retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";

      return retVal;

   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] = 
          new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
                  "The logging level of this modules"+"\n 0=DEBUG; 1=INFO; 2=WARN; 3=ERROR; 4=FATAL; 5=OFF");

      return pds;
   }
} // end class TestGetRowModule
