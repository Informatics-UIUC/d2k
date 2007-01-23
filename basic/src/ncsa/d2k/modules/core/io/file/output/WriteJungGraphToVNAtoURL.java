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
package ncsa.d2k.modules.core.io.file.output;

import edu.uci.ics.jung.graph.Graph;

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;

import java.io.FileWriter;


/**
 * This module writes the contents of a JUNG Graph to a file in VNA format.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * WriteJungGraphToVNA</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  Sean Mason
 * @version $Revision$, $Date$
 */
public class WriteJungGraphToVNAtoURL extends OutputModule {

   //~ Constructors ************************************************************

   /**
    * Creates a new WriteJungGraphToVNAURL object.
    */
   public WriteJungGraphToVNAtoURL() { }

   //~ Methods *****************************************************************

   /**
    * Write the table to the file or URL specified by the Data Object Proxy.
    *
    * @throws Exception Error in the write.
    */
   public void doit() throws Exception {

      DataObjectProxy dop = (DataObjectProxy) pullInput(0);
      Graph graph = (Graph) pullInput(1);
      JungGraphToVNAtoURL converter = new JungGraphToVNAtoURL();
      converter.write(graph, dop); // pass the proxy to the writer
      dop.close();
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

         case 0:
            return "The proxy to be written.";

         case 1:
            return "The Graph to write.";

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

         case 1:
            return "JUNG Graph";

         default:
            return "No such input";
      }
   }

   /**
    * Return a String array containing the datatypes the inputs to this module.
    *
    * @return The datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.proxy.DataObjectProxy",
         "edu.uci.ics.jung.graph.Graph"
      };

      return types;
   }

   /**
    * Return a description of the function of this module.
    *
    * @return A description of this module.
    */
   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb =
         new StringBuffer("<p>Overview: Writes a JUNG Graph object to a file" +
                          "or URL in the VNA graph description format.</p>");
      sb.append("<p><b>Note:</b> This module is the same as deprecated " +
                "module <i>WriteJungGraphToVNA</i>, extended to access the " +
                "data through <i>DataObjectProxy</i>.</p>");
      sb.append("<p>Detailed Description:");
      sb.append("This modules writes a JUNG Graph object to a file or URL" +
                " in the VNA graph description format.");
      sb.append("Vertices in the network should have a UserDatum entry " +
                "called \"id\" that contains the vertex ID.");
      sb.append("If this attribute is not found, a numeric ID will be " +
                "assigned to the vertex.</p>");
      sb.append("<p>Edges within the graph may contain edge weights in a " +
                "UserDatum named \"edgeWeight\", and an optional ");
      sb.append("edge type in a UserDatum named \"edgeType\".");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not destroy its input data, but may add " +
                "generated IDs to nodes that do not have them.");
      sb.append("</p><p><b>Note:</b> ");
      sb.append("This module replaces WriteJungGraphVNA which wrote to local " +
                "file.  WriteJungGraphVNA is denigrated, see " +
                "modules.deprecated.core.");
      sb.append("</p>");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Return the name of this module.
    *
    * @return The name of this module.
    */
   public String getModuleName() { return "Write JUNG Graph to VNA"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

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

         default:
            return "No such output";
      }
   }

   /**
    * Return a String array containing the datatypes of the outputs of this
    * module.
    *
    * @return The datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] types = {};

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
      return new PropertyDescription[0];
   }
} // end class WriteJungGraphToVNAURL
