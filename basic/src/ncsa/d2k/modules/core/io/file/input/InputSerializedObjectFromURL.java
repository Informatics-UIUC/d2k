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

import java.io.ObjectInputStream;


/**
 * Reads a serialized object from a file or URL.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * InputSerializedObject</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  David Tcheng
 * @version $Revision$, $Date$
 */
public class InputSerializedObjectFromURL extends InputModule {

   //~ Methods *****************************************************************

   /**
    * Reads a serialized object from a file or URL.
    *
    * <p><b>Note:</b> This module is the same as deprecated module <i>
    * XMLToGraphFromURL</i>, extended to access the data through <i>
    * DataObjectProxy</i>.</p>
    *
    * @throws Exception              other Exception.
    * @throws ClassNotFoundException The input class is not found.
    */
   public void doit() throws Exception {

      DataObjectProxy dop = (DataObjectProxy) pullInput(0);
      ObjectInputStream in = null;

      in = new ObjectInputStream(dop.getInputStream());

      Object object = null;

      try {
         object = (Object) in.readObject();
      } catch (java.lang.ClassNotFoundException e) {
         throw new ClassNotFoundException("Unable to find object class." +
                                          "\n" + e);
      }

      in.close();
      dop.close();
      pushOutput(object, 0);

   } // end method doit


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "The Data Object Proxy for the serialized object to be read.";

         default:
            return "No such input";
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

         case 0:
            return "Data Object Proxy";

         default:
            return "No such input";
      }
   }

   /**
    * Return a String array containing datatypes of the inputs to this module.
    *
    * @return The datatypes of the module inputs.
    */
   public String[] getInputTypes() {
      String[] types = { "ncsa.d2k.module.core.io.proxy.DataObjectProxy" };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module reads a Java serialized object from a file.");
      sb.append("</p><p>Detailed Description: In Java, an object can be ");
      sb.append("converted to a stream of bytes and written out to a file ");
      sb.append("in a process called <em>serialization</em>.  ");
      sb.append("</p><p>This module opens the object indicated by the <i>Data ");
      sb.append("Object Proxy</i> input port and reads the file to reload " +
                "the Java ");
      sb.append("object that was serialized to the file.  The resulting ");
      sb.append("object is made available on the <i>Java Object</i> output ");
      sb.append("port.");
      sb.append("</p><p>The module will exit with an error if the file cannot ");
      sb.append("be accessed or does not contain a serialized object. </p> ");
      sb.append("<p><b>Note:</b>  This module is the same as deprecated module " +
                "<i>XMLToGraphFromURL</i>, extended to access the data through " +
                "<i>DataObjectProxy</i>.</p>");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Input Serialized Object"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         case 0:
            return "The Java object that was read from the file.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      switch (i) {

         case 0:
            return "Java Object";

         default:
            return "No such output";
      }
   }

   /**
    * Returns a String array containing datatypes of the output to this module.
    *
    * @return The datatypes of the module outputs.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.io.proxy.DataObjectProxy" };

      return types;
   }
} // end class InputSerializedObjectFromURL
