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
package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Title:</p>
 *
 * <p>Description: this module creates a table that compares the performances of
 * all the models that were produced. the module accepts the Prediction Table of
 * each model and accumulates the statistics. the output will be a 3 column
 * Table. first column - index number of the model. second column - precision of
 * the model. thrid column - recall of the model. the recall is regarding
 * prediction true positive in the output column. Data Restriction: this module
 * can handle only binary one class classificatin.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company:</p>
 *
 * @author  vered goren
 * @version 1.0
 */
public class CreateModelComparisonTable extends ComputeModule {

   //~ Static fields/initializers **********************************************

   /** constants. */
   // indices into the performance 2D array

   /** correct index in performance array */
   static public final int CORRECT = 0;

   /** true true index in performance array */
   static public final int TRUE_TRUE = 1;

   // indices into the output table.

   /** index column index in output table */
   static public final int INDEX = 0;

   /** precision column index in output table */
   static public final int PRECISION = 1;

   /** recall column index in output table */
   static public final int RECALL = 2;

   //~ Instance fields *********************************************************

   /** helps to initialized the table to a reasonable size. */
   private int aproxNumModels;

   /** how many tables were accepted already. */
   private int counter;

   /** output file name. */
   private String fileName;

   /** the performance table. */
   private MutableTable tbl;
   
   private D2KModuleLogger myLogger;

   //~ Methods *****************************************************************

   /**
    * Description of method doit.
    *
    * @throws java.lang.Exception when something goes wrong
    *
    * @todo   this module now handles only binary classification. it can deal
    *         only with one output feature.
    */
   protected void doit() throws Exception {

      PredictionTable pt = (PredictionTable) pullInput(0);

      int[] outputs = pt.getOutputFeatures();
      int[] preds = pt.getPredictionSet();
      int numPos = 0;

      if (outputs == null) {
         throw new Exception("The output attributes were undefined.");
      }

      if (preds == null) {
         throw new Exception("The prediction features were undefined.");
      }

      int[][] performance = new int[2][];
      performance[CORRECT] = new int[outputs.length]; // precision
      performance[TRUE_TRUE] = new int[outputs.length]; // recall

      for (int i = 0; i < outputs.length; i++) {


         for (int j = 0; j < pt.getNumRows(); j++) {

            if (pt.getInt(j, outputs[i]) == 1) {
               numPos++; // counting the actual positive classes
            }

            // if prediction is correct...
            if (pt.getInt(j, outputs[i]) == pt.getInt(j, preds[i])) {
               performance[CORRECT][i]++; // increment precision

               // check if need to increment recall...
               if (pt.getInt(j, outputs[i]) == 1) {
                  performance[TRUE_TRUE][i]++;
               }
            }

         } // for j
      } // for i

      // compute precision and recall
      double precision =
         ((double) performance[CORRECT][0] * 100) / ((double) pt.getNumRows());
      double recall =
         ((double) performance[TRUE_TRUE][0] * 100) / ((double) numPos);

      // find out if table needs to be extended
      if (counter >= tbl.getNumRows()) {
         tbl.addRows(1);
      }

      // setting values for current model
      tbl.setInt(counter, counter, INDEX);
      tbl.setDouble(precision, counter, PRECISION);
      tbl.setDouble(recall, counter, RECALL);

      // incrementing counter.
      counter++;

   } // end method doit

   /**
    * Pre-execution initializations and cleanup. initializes the output table.
    */
   public void beginExecution() {
      tbl = new MutableTableImpl();

      Column[] cols = new Column[3];
      cols[INDEX] = new IntColumn(aproxNumModels); // model index
      cols[INDEX].setLabel("Model Index");
      cols[PRECISION] = new DoubleColumn(aproxNumModels); // precision
      cols[PRECISION].setLabel("Precision");

      cols[RECALL] = new DoubleColumn(aproxNumModels); // recall
      cols[RECALL].setLabel("Recall");
      tbl.addColumns(cols);
      counter = 0;
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   }

   /**
    * Post-execution initializations and cleanup. writes the table to a binary
    * file.
    */
   public void endExecution() {

      // trim the table
      if (counter < tbl.getNumRows()) {
         tbl.removeRows(counter, tbl.getNumRows() - counter);
      }

      if (fileName == null) {
    	  myLogger.debug(getAlias() +
                  ": Output File Name property was not set!" +
                  " Cannot save the comparison table to output file.");

         return;

      }


      FileOutputStream file = null;
      ObjectOutputStream out = null;

      try {
         file = new FileOutputStream(fileName);
      } catch (FileNotFoundException e) {
         myLogger.error(getAlias() + ": Could not open file: " + fileName +
                            "\nTable could not be saved\n");
         e.printStackTrace();

         return;
      } catch (SecurityException e) {
    	  myLogger.error(getAlias() + ": Could not open file: " + fileName +
                            "\nTable could not be saved\n");
         e.printStackTrace();

         return;
      } catch (Exception e) {
    	  myLogger.error(getAlias() + ": Could not open file: " + fileName +
                            "\nTable could not be saved\n");
         e.printStackTrace();

         return;
      }

      try {
         out = new ObjectOutputStream(file);
         out.writeObject(tbl);
         out.flush();
         out.close();
      } catch (IOException e) {
    	  myLogger.error(getAlias() + ": Unable to serialize object " +
                            "\nTable could not be saved");
         e.printStackTrace();
      }

      tbl = null;

   } // end method endExecution

   /**
    * Get the approximate number of models.
    *
    * @return the approximate number of models
    */
   public int getAproxNumModels() { return aproxNumModels; }

   /**
    * Get the file name.
    *
    * @return file name
    */
   public String getFileName() { return fileName; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  idx Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int idx) {

      switch (idx) {

         case 0:
            return "A Prediction Table created by a Prediction Model Module.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  idx Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int idx) {

      switch (idx) {

         case 0:
            return "Prediction Table";

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
      String[] retVal =
      { "ncsa.d2k.modules.core.datatype.table.PredictionTable" };

      return retVal;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<P>Overview: This module creates a Table in which each row holds" +
             " data about the performance of a prediction model, computed acording " +
             "to the input <i>Prediction Table</I>. At the end of execution it "+
             "writes the table to a file." +
             " This Table can then be loaded into a plotting vis module, to "+
             "visualize precision/recall VS model number.</P>" +
             "<P>Data Handling: This module can only handle one output feature "+
              "and this feature should be binary.</P>";
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create Model Comparison Table"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  idx Index of the output for which a description should be
    *             returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int idx) {

      switch (idx) {

         // case 0: return "A Table in which each row represents the
         // performance of a prediction model.";
         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  idx Index of the output for which a description should be
    *             returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int idx) {

      switch (idx) {

         // case 0: return "Comparison Table";
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
      String[] retVal = {};

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
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] =
         new PropertyDescription("fileName", "Output File Name",
                                 "The Comparison Table will be written to this file");
      pds[1] =
         new PropertyDescription("aproxNumModels",
                                 "Aproximate Number of Models",
                                 "The aproximate number of expected models in this " +
                                 "itinerary. The Comparison Table will be initiated to " +
                                 "hold such number fo records");

      return pds;
   }


   /**
    * Set the approximate number of models.
    *
    * @param num the approximate number of models
    */
   public void setAproxNumModels(int num) { aproxNumModels = num; }

   /**
    * Set the file name.
    *
    * @param str the file name
    */
   public void setFileName(String str) { fileName = str; }


   /**
    * Set the table.
    *
    * @param _tbl the table
    */
   public void setTbl(MutableTable _tbl) { tbl = _tbl; }


} // end class CreateModelComparisonTable
