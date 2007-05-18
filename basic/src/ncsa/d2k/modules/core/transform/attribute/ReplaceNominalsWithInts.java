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

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.transformations
          .ReplaceNominalsWithIntsTransform;
import ncsa.d2k.modules.core.util.*;


/**
 * This module outputs a (reversible) transformation on a <code>
 * MutableTable</code> that replaces unique nominal column values with unique
 * integers.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ReplaceNominalsWithInts extends ComputeModule {

   //~ Instance fields *********************************************************

   /**
    * If enabled, the nominal-to-integer mappings for each column will be
    * printed to the console.
    */
   private boolean _printMapping = false;
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      MutableTable mt = (MutableTable) pullInput(0);

      ReplaceNominalsWithIntsTransform transform =
         new ReplaceNominalsWithIntsTransform(mt);


      if (_printMapping) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug(transform.toMappingString(mt));
          myLogger.resetLoggingLevel();//re-set level to original level
      }

      pushOutput(transform, 0);

   }

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
         return "The <i>MutableTable</i> to be used in constructing the " +
                "transformation.";
      }

      return null;
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
                                               "ncsa.d2k.modules.core.datatype.table.MutableTable"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module constructs a reversible transformation to ");
      sb.append("replace each unique value in every nominal column of a ");
      sb.append("<i>MutableTable</i> with an integer unique to that column.");

      sb.append("</P><P><u>Missing Values Handling:</u> This Transformation output by this module" +
                " preserves missing values. " +
                "The internal representation of a missing value will be converted to that of " +
                "and integer missing value, and the fact that the value was missing in the original" +
                "table is preserved in the resulting table.");

      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>ReversibleTransformation</i> that can be ");
      sb.append("applied downstream.");
      sb.append("</p>");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Replace Nominals With Ints"; }

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
         return "The <i>ReversibleTransformation</i> that performs the " +
                "replacement of column values.";
      }

      return null;
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
         return "Reversible Transformation";
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
                                                "ncsa.d2k.modules.core.datatype.table.ReversibleTransformation"
                                             }; }

   /**
    * Get printMapping.
    *
    * @return printMapping
    */
   public boolean getPrintMapping() { return _printMapping; }

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
         new PropertyDescription("printMapping",
                                 "Print Integer Mappings",
                                 "If enabled, the nominal-to-integer mappings for each column will " +
                                 "be printed to the console.");

      return pds;

   }

   /**
    * Set printMapping.
    *
    * @param value printMapping
    */
   public void setPrintMapping(boolean value) { _printMapping = value; }

} // end class ReplaceNominalsWithInts