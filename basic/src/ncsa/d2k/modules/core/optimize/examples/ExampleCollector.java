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
package ncsa.d2k.modules.core.optimize.examples;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.basic.BasicTableFactory;

import java.util.ArrayList;
import ncsa.d2k.modules.core.util.*;


/**
 * used to be ncsa.d2k.modules.core.optimize.random.ExampleCollector.
 *
 * @author  David Tcheng
 * @version 1.0
 */
public class ExampleCollector extends ncsa.d2k.core.modules.ComputeModule {

   //~ Instance fields *********************************************************

   /** minimizing? Default = true */
   private boolean minimizing = true;

   /** this is the number of examples to collect. */
   private int n = 0;

   /** Description of field examples. */
   ArrayList examples;

   //~ Methods *****************************************************************

   /**
    * Just print an example.
    *
    * @param label label for the example.
    * @param ex    The example.
    */
   private void printExample(String label, Example ex) {
	   myLogger.debug(label);
	   myLogger.debug("  Inputs");

      ExampleTable et = (ExampleTable) ex.getTable();
      int ni = et.getNumInputFeatures();
      int no = et.getNumOutputFeatures();

      for (int i = 0; i < ni; i++) {
    	  myLogger.debug("    " + et.getInputName(i) + " = " +
                  ex.getInputDouble(i));
      }

      myLogger.debug("  Outputs");

      for (int i = 0; i < no; i++) {
    	  myLogger.debug("    " + et.getOutputName(i) + " = " +
                  ex.getOutputDouble(i));
      }
   }

   private D2KModuleLogger myLogger;

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    *
    * <p>We just need to reset n.</p>
    */
   public void beginExecution() {
      n = 0;
      examples = new ArrayList();
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   /**
    * Performs the main work of the module.
    *
    * <p>This is the design pattern exemplar for a gather operation.</p>
    *
    * @throws Exception        if a problem occurs while performing the work of
    *                          the module.
    * @throws RuntimeException Illegal input port.
    */
   public void doit() throws Exception {

      // If we haven't yet received n, it must be available.
      if (n == 0) {
         n = ((Integer) this.pullInput(0)).intValue();

         if (n <= 0) {
            throw new RuntimeException("The input on the first port must be " +
                                       "an Integer greater than 0.");
         }
      }

      Object tmp = this.pullInput(1);
      examples.add(tmp);
      myLogger.debug("EXAMPLE (" + tmp.getClass().getName() + "): " + n);
      n--;

      if (n == 0) {

         // we are done, find the best score and push it.
         Example best = (Example) examples.get(0);

         if (!minimizing) {

            for (int i = 1; i < examples.size(); i++) {
               Example example = (Example) examples.get(i);

               if (example.getOutputDouble(0) > best.getOutputDouble(0)) {
                  best = example;
               }
            }
         } else {

            for (int i = 1; i < examples.size(); i++) {
               Example example = (Example) examples.get(i);

               if (example.getOutputDouble(0) < best.getOutputDouble(0)) {
                  best = example;
               }
            }
         }

         this.pushOutput(best, 0);

         // now create and push an example table of all examples
         ExampleTable bestET = (ExampleTable) best.getTable();
         TableFactory factory = bestET.getTableFactory();

         if (factory == null) {
            factory = new BasicTableFactory();
         }

         int numColumns = bestET.getNumColumns();

         Table t = factory.createTable();

         for (int col = 0; col < numColumns; col++) {
            ((MutableTable) t).addColumn(factory.createColumn(bestET
                                                                 .getColumnType(col)));
            ((MutableTable) t).setColumnLabel(bestET.getColumnLabel(col), col);
         }

         ExampleTable exampleTable = t.toExampleTable();

         // ExampleTable exampleTable =
         // factory.createTable(numColumns).toExampleTable();
         exampleTable.setInputFeatures(bestET.getInputFeatures());
         exampleTable.setOutputFeatures(bestET.getOutputFeatures());

         int numExamples = examples.size();
         exampleTable.addRows(numExamples);

         for (int i = 0; i < numExamples; i++) {
            Example ex = (Example) examples.get(i);

            for (int j = 0; j < numColumns; j++) {
               exampleTable.setObject(ex.getObject(j), i, j);
            }
         }

         pushOutput(exampleTable, 1);

      } // end if
   } // end method doit

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    * Reset the examples to null.
    */
   public void endExecution() { examples = null; }

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
            return "<p>" +
                   "      This is the number of examples to collect before " +
                   "producing the best " +
                   "      example as an output." +
                   "    </p>";

         case 1:
            return "<p>" +
                   "      The scored parameter point, this is an example." +
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
            return "Number Examples";

         case 1:
            return "Example";

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
         "java.lang.Integer", "ncsa.d2k.modules.core.datatype.table.Example"
      };

      return types;
   }

   /**
    * Get Minimizing.
    *
    * @return The value.
    */
   public boolean getMinimize() { return this.minimizing; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>" +
             "      Overview: This module takes two inputs. One is the " +
             "number of examples to " +
             "      collect, the other will accept the examples. This " +
             "module will collect " +
             "      the number of examples specified on it's first input, " +
             "and when all " +
             "      examples are collected will output the best scored " +
             "example." +
             "    </p>" +
             "    <p>" +
             "      Detailed Description: This module will received an " +
             "Integer on it's first " +
             "      input port. This integer specifies the number of " +
             "examples that will be " +
             "      produced, and ultimately received on the second input. " +
             "The examples will " +
             "      be stored as they are received, and when N examples are " +
             "received, the " +
             "      one with the highest score will be output on the only " +
             "output." +
             "    </p>" +
             "    <p>" +
             "      Trigger Criteria: This module will not enable until it " +
             "has gotten an " +
             "      Integer (N) on the first port. When it receives this " +
             "input, it will " +
             "      enable each time the next N examples are received on " +
             "the second port." +
             "    </p>";
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Example Collector"; }

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
            return "<p>" +
                   "      This is the best scored paramter point." +
                   "    </p>";

         case 1:
            return "<p>" +
                   "      This is the table of all examples." +
                   "    </p>";

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
            return "Best Example";

         case 1:
            return "Example Table";

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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.table.Example",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

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
      PropertyDescription[] descriptions = new PropertyDescription[1];
      descriptions[0] =
         new PropertyDescription("minimize",
                                 "Minimize Objective Score",
                                 "Set to true if the objective score should " +
                                 "be minimize, false if it should be " +
                                 "maximized.");

      return descriptions;
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * <p>This module will trigger when it has received N, the number of examples
    * to collect on the first port. From that point on, it will trigger each
    * time it gets an input the next N times on the second port.</p>
    *
    * @return this module will trigger when it has received N, the number of
    *         examples to collect on the first port. From that point on, it will
    *         trigger each time it gets an input the next N times on the second
    *         port.
    */
   public boolean isReady() {

      if (n == 0) {

         if (this.getInputPipeSize(0) > 0 && this.getInputPipeSize(1) > 0) {
            return true;
         } else {
            return false;
         }
      } else if (this.getInputPipeSize(1) > 0) {
         return true;
      }

      return false;
   }

   /**
    * Set Minimizing.
    *
    * @param value The value.
    */
   public void setMinimize(boolean value) { minimizing = value; }
} // end class ExampleCollector
