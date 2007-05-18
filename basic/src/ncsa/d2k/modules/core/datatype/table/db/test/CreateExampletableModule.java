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
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.util.*;//using D2KModuleLogger and Factory


/**
 * Creates an <code>ExampleTable</code> from a <code>Table</code>.
 *
 * @author  goren
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class CreateExampletableModule extends ComputeModule {

   //~ Methods *****************************************************************
	   private D2KModuleLogger myLogger = 
		   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   public void beginExecution() {
		   myLogger.setLoggingLevel(moduleLoggingLevel);
	   }

	   private int moduleLoggingLevel=
		   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
		   .getLoggingLevel();
	   
	   public int getmoduleLoggingLevel(){
		   return moduleLoggingLevel;
	   }

	   public void setmoduleLoggingLevel(int level){
		   moduleLoggingLevel = level;
	   }

   /**
    * Performs the main work of the module. In this case, pulls the <code>
    * Table</code> input and pushes the <code>ExampleTable</code> output.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   public void doit() {
      Table table = (Table) this.pullInput(0);

      pushOutput(table.toExampleTable(), 0);

   }


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
            return "Table";

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
      return "<p>Overview: This module creates an example table from a " +
             "table. The output port should be linked to a module that " +
             "selects the output and input features.</p>";

   }


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create Example Table Module"; }


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
            return "Example Table";

         default:
            return "No such output";
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
            return "Example Table";

         default:
            return "No such output";
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
      retVal[0] = "ncsa.d2k.modules.core.datatype.table.ExampleTable";

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
} // end class CreateExampletableModule
