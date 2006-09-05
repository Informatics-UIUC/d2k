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
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;


/**
 * Create an ARFF File Parser for the specified file.
 * <p><b>Note:</b>  This module is the same as deprecated module 
 * <i>CreateARFFFileParser</i>, extended to access the data through 
 * <i>DataObjectProxy</i>.</p>
 *
 * @author  unascribed
 * @version 1.0
 */
public class CreateARFFParserFromURL extends InputModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception exception.
    */
   public void doit() throws Exception {
      DataObjectProxy dop = (DataObjectProxy) pullInput(0);

      ARFFFileParserFromURL arff = new ARFFFileParserFromURL(dop);
      pushOutput(arff, 0);
   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {
      return "The Data Object Proxy an ARFF file.";
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) { return "Data Object Proxy"; }

   /**
    * Returns an array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] in = { "ncsa.d2k.modules.core.io.proxy.DataObjectProxy" };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer s = new StringBuffer("<p>Overview: ");
      s.append("This module creates an ARFF File Parser for the specified " +
      		"file. ");
      s.append("<p><b>Note:</b>  This module is the same as deprecated module " +
      		" <i>CreateARFFFileParser</i>, extended to access the data through" +
      		" <i>DataObjectProxy</i>.</p>");
      s.append("</p><p>DetailedDescription: ");
      s.append("This module creates an ARFF File Parser that will ");
      s.append("read data from the specified ARFF file. ");
      s.append("An ARFF (Attribute-Relation File Format) file is an ASCII " +
      		"file ");
      s.append("that describes a list of instances sharing a set of " +
      		"attributes. ");
      s.append("ARFF files were developed by the Machine Learning Project " +
      		"at the ");
      s.append("Department of Computer Science of the University of Waikota " +
      		"for use ");
      s.append("with the Weka machine learning software. ");

      s.append("</p><p>Typically the <i>File Parser</i> output port of this ");
      s.append("module is connected to the <i>File Parser</i> input port of ");
      s.append("a module whose name begins with 'Parse File', for example, ");
      s.append("<i>Parse File To Table</i> or  <i>Parse File To Paging T" +
      		"able</i>.");

      s.append("</p><p>Limitations: The module is designed to read valid ARFF " +
      		"files, not ");
      s.append("to validate correctness.  Because of this, the parser performs " +
      		"only a ");
      s.append("minimal amount of data checking.  For example, it does " +
      		"not verify that ");
      s.append("the data instances contain acceptable attribute values. ");
      s.append("The module does not handle the sparse data representation " +
      		"introduced in ");
      s.append("recent versions of the ARFF format.");

      s.append("</p><p>References: <br>");
      s.append("The Weka Project: http://www.cs.waikato.ac.nz/~ml/weka <br>");
      s.append("The ARFF Format: http://www.cs.waikato.ac.nz/~ml/weka/arff.html ");

      s.append("</p><p>Data Type Restrictions: ");
      s.append("The input to this module must be an ARFF file.");

      s.append("</p><p>Data Handling: ");
      s.append("The module does not destroy or modify the input data.</p>");

      return s.toString();
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user 
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create ARFF File Parser"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return "An ARFF File Parser."; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "File Parser"; }

   /**
    * Returns an array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the f
    * ully qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out = { "ncsa.d2k.modules.core.io.file.input.ARFFFileParserFromURL" };

      return out;
   }
} // end class CreateARFFParserFromURL
