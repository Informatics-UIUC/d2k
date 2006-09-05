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
package ncsa.d2k.modules.core.util;

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;


/**
 * InputFileName allows the user to input the name of a single file. The file
 * name is input in the properties editor.
 *
 * @author  unascribed
 * @version 1.0
 */
public class Input1String extends InputModule {

   //~ Instance fields *********************************************************

   /** The String. */
   private String theString;

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      String str = getTheString();

      if (str == null || str.length() == 0) {
         throw new Exception(getAlias() + ": No string was given.");
      }

      setTheString(str);
      pushOutput(str, 0);
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) { return ""; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return null; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s += "This module is used to enter a String. ";
      s += "</p><p>Detailed Description: ";
      s += "The module provides a properties editor that can be used to ";
      s += "enter a String";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end user consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Input String"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return "The string."; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "String"; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out = { "java.lang.String" };

      return out;
   }

   /**
    * Return an array with information on the properties the user may update.
    *
    * @return The PropertyDescriptions for properties the user may update.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] =
         new PropertyDescription("theString",
                                 "The String",
                                 "Any string, may contain blanks, etc.");

      return pds;
   }

   /**
    * Get theString.
    *
    * @return the string.
    */
   public String getTheString() { return theString; }

   /**
    * Set TheString.
    *
    * @param s the new value s.
    */
   public void setTheString(String s) { theString = s; }
} // end class Input1String
