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

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;


/**
 * Create a new fixed file reader.
 * <p>The parser will read from the local or remote object specified by the 
 * <i>DataObjectProxy</i>.</p>
 * <p><b>Note:</b>  This module is the same as deprecated module 
 * <i>CreateFixedParser</i>, extended to access the data through 
 * <i>DataObjectProxy</i>.</p>
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class CreateFixedParserFromURL extends InputModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      DataObjectProxy dop = (DataObjectProxy) pullInput(0);

      Table meta = (Table) pullInput(1);

      FixedFileParserFromURL ff = new FixedFileParserFromURL(dop, meta);

      pushOutput(ff, 0);
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case (0):
            return "The Data Object Proxy for the fixed file.";

         case (1):
            return "A table of metadata describing the fixed file.";

         default:
            return "";
      }
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

      switch (i) {

         case (0):
            return "Data Object Proxy";

         case (1):
            return "Metadata Table";

         default:
            return "";
      }
   }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() {
      String[] in =
      {
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
	   StringBuffer sb = new StringBuffer("<p>Overview: ");
	      sb.append("This module creates a parser for a fixed-format file. ");
	      sb.append("<p><b>Note:</b>  This module is the same as deprecated " +
	      		"module <i>CreateFixedParser</i>, extended to access the " +
	      		"data through <i>DataObjectProxy</i>.</p>");
	      sb.append("</p><p>Detailed Description: ");
	      sb.append("Given a <i>DataObjectProxy</i> and a table of metadata ");
	      sb.append("describing the file's layout, this module creates a ");
	      sb.append("parser for the file, which must have fixed-length fields. " +
	      		"This parser can then be passed to ");
	      sb.append("<i>Parse File To Table</i>, for example, to read the " +
	      		"file into a Table. ");
	      sb.append("Many databases offer the option of writing their contents " +
	      		"to fixed-format files. ");
	      sb.append("</p>");

	      sb.append("<p>");
	      sb.append("The table of metadata has up to five columns labeled ");
	      sb.append("LABEL, TYPE, START, STOP, and LENGTH, not necessarily in " +
	      		"that order. ");
	      sb.append("Each row in the table, other than the labels row and an " +
	      		"optional data type row, ");
	      sb.append("provides information about one attribute (field) that is " +
	      		"to be loaded from the fixed-format file. ");
	      sb.append("</p>");

	      sb.append("<p>");
	      sb.append("The LABEL entry, which is optional, contains the label " +
	      		"to assign to the attribute.  ");
	      sb.append("If the table does not contain a LABEL column, D2K " +
	      		"automatically assigns a label to the attribute. ");

	      sb.append("</p><p>");
	      sb.append("The TYPE entry specifies the datatype of the attribute.  " +
	      		"If no TYPE appears, D2K tries to determine the ");
	      sb.append("attribute type automatically. ");

	      sb.append("</p><p>");
	      sb.append("The START entry must be present, and gives the starting " +
	      		"position of the attribute value in the ");
	      sb.append("rows of the fixed-format file. The first position in " +
	      		"a row is position 1. ");
	      sb.append("Either STOP or LENGTH must be present in the " +
	      		"<i>Metadata Table</i>. ");
	      sb.append("If both appear in the table, STOP is ignored and " +
	      		"LENGTH is used. ");
	      sb.append("The STOP entry gives the stopping position of the " +
	      		"attribute value in the rows of the fixed-format file. ");
	      sb.append("The LENGTH entry specifies the number of positions " +
	      		"taken by the attribute ");
	      sb.append("value in the rows of the fixed-format file. ");
	      sb.append("START and STOP positions are inclusive, hence an " +
	      		"attribute with one character will have equal START and STOP ");
	      sb.append("entries. ");
	      sb.append("</p>");

	      sb.append("<p>");
	      sb.append("The <i>Metadata Table</i> need not include rows " +
	      		"describing all of the fields in the fixed-format file. ");
	      sb.append("Only the attributes (fields) that are described by " +
	      		"the metadata table will be read by the parser. ");
	      sb.append("Typically, the <i>Metadata Table</i> is itself " +
	      		"loaded from a file into a <i>Table</i> ");
	      sb.append("using D2K modules such as <i>Create Delimited " +
	      		"File Parser</i> and <i>Parse File To Table</i> .");

	      sb.append("</p><p>Data Type Restrictions: ");
	      sb.append("The specified file must be in fixed-format. ");

	      sb.append("</p><p>Data Handling: ");
	      sb.append("This module does not destroy or modify its input data.");
	      sb.append("</p>");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Create Fixed-Format Parser"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
      return "A Fixed File Parser for the specified fixed-format file.";
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "Fixed File Parser"; }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] out = { "ncsa.d2k.modules.core.io.file.input.FixedFileParserFromURL" };

      return out;
   }

} // end class CreateFixedParserFromURL
