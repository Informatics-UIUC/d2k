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
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.d2k.modules.core.vis.pgraph.PGraphML;

import java.io.File;
import java.io.IOException;


/**
 * Reads a Jung Graph object from a GraphML file or URL.
 * <p><b>Note:</b>  This module is the same as deprecated module 
 * <i>XMLToGraphFromURL</i>, extended to access the data through 
 * <i>DataObjectProxy</i>.</p>
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class XMLToGraphFromURL extends InputModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws IOException              Error reading local file.
    * @throws DataObjectProxyException Error accessing or reading object.
    */
   public void doit() throws IOException, DataObjectProxyException {
      DataObjectProxy dop = (DataObjectProxy) pullInput(0);
      File f = dop.getLocalFile();

      pushOutput(PGraphML.load(f), 0);
      dop.close();
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
      return (index == 0) ? "The Data Object Proxy for the GraphML file."
                          : null;
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
      return (index == 0) ? "Data Object Proxy" : null;
   }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() { return new String[] {
                                               "ncsa.d2k.modules.core.proxy.DataObjectProxy"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module creates a Jung Graph object from a " +
             "GraphML file.  The input may be a local file or remote.</p>" +
             "<p><b>Note:</b>  This module is the same as deprecated module " +
             "<i>XMLToGraphFromURL</i>, extended to access the data through a" +
             "<i>DataObjectProxy</i>.</p>" +
             "<p>Acknowledgement: This module uses " +
             "functionality from the JUNG project. See " +
             "http://jung.sourceforge.net.</p>";
   }

   /**
    * Describes the name of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "XML to Graph"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {
      return (index == 0) ? "The loaded Graph object." : null;
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {
      return (index == 0) ? "Graph" : null;
   }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "edu.uci.ics.jung.graph.Graph"
                                             }; }

} // end class XMLToGraphFromURL
