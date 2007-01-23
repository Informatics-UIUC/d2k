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

import prefuse.data.Graph;
import prefuse.data.io.GraphMLWriter;

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;

import java.io.File;


/**
 * Write a graph to local or remote file use GraphML from Prefuse.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * PrefuseToGraphML</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class PrefuseToGraphMLtoURL extends OutputModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {

      Graph graph = (Graph) pullInput(0);
      DataObjectProxy dop = (DataObjectProxy) pullInput(1);

      // Get a local copy of the file from the proxy.
      File file = dop.initLocalFile(null);

      GraphMLWriter writer = new GraphMLWriter();
      writer.writeGraph(graph, file);

      // Push out the file, if necessary.
      dop.putFromFile(file);
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

      if (i == 0) {
         return "Prefuse Graph";
      } else if (i == 1) {
         return "Data Object Proxy";
      }

      return null;
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

      if (i == 0) {
         return "Prefuse Graph";
      } else if (i == 1) {
         return "Data Object Proxy";
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
                                               "prefuse.data.Graph",
                                               "ncsa.d2k.modules.core.io.proxy.DataObjectProxy"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: Saves a Prefuse graph to a GraphML file.</p> " +
      		"<p><b>Note:</b> This module is the same as deprecated " +
      		"module <i>PrefuseToGraphML</i>, extended to access the data " +
      		"through <i>DataObjectProxy</i>.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Prefuse to GraphML"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return null; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return null; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return null; }
} // end class PrefuseToGraphMLURL
