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
import ncsa.d2k.modules.core.util.*;//using D2KModuleLogger and Factory


/**
 * Tests the <code>getSubset()</code> method of a <code>Table</code>. Retrieves
 * a <code>SubsetTable</code> from a <code>Table</code> and pushes out the
 * <code>SubsetTable</code>.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class TestGetSubsetModule extends ComputeModule {
	private D2KModuleLogger myLogger =
		D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	private int moduleLoggingLevel=
		D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
		.getLoggingLevel();

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
    * SubsetTable</code> from a <code>Table</code> and pushes out the <code>
    * SubsetTable</code>.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   public void doit() {
      Table table = (Table) this.pullInput(0);
      int begin;
      int len;
      begin = (int) (Math.random() * table.getNumRows());
      len = (int) (Math.random() * (table.getNumRows() - begin));

      Table sTable = table.getSubset(begin, len);

      myLogger.debug("Test get subset modules:");
      myLogger.debug("Subsetting the table from row no. " + begin);
      myLogger.debug("Number of rows in the set:" + len);
      //System.out.println("Test get subset modules:");
      //System.out.println("Subsetting the table from row no. " + begin);
      //System.out.println("Number of rows in the set:" + len);

      pushOutput(sTable, 0);


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
      return "<p>Overview: This module tests the getSubset method of a " +
      		"Table. It retrieves a SubsetTable from a Table and pushes " +
      		"out the subsettable.</p>";

   }


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Test getSubset Module"; }


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
            return "Subset Table";

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
            return "Subset Table";

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
      retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";

      return retVal;

   }


} // end class TestGetSubsetModule
