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
package ncsa.d2k.modules.core.transform.streaming;

import ncsa.d2k.core.modules.DataPrepModule;

import java.util.List;


/**
 * <p>Overview:
 * This module takes a list of objects and pushes them out as single objects
 * in a stream.  It also outputs the number of objects that will be pushed out
 * in total.  It is the other end of a couple with StreamCatcher.  
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class StreamSource extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** Description of field listIndex. */
   private int listIndex = 0;

   /**
    * This class takes a list input, and converts it to a stream of objects.
    * Used to allow cross-item streaming in D2KSL.
    */

   private List theList = null;

   //~ Constructors ************************************************************

   /**
    * Constructor.
    */
   public StreamSource() { }

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { listIndex = 0; }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      // System.out.println("starting streamsource.");
      if (theList == null) {

         // System.out.println("streamsource list init.");
         theList = (List) this.pullInput(0);
         this.pushOutput(new Integer(theList.size()), 1);
         // System.out.println("streamsource PUSHED list size: "+theList.size());
      }

      if (this.listIndex < theList.size()) {

         // System.out.println("streamsource pushing output number:
         // "+listIndex);
         this.pushOutput(theList.get(listIndex++), 0);
      } else {

         // System.out.println("streamsource NULLIFYIN.");
         theList = null;
      }

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

      if (parm1 == 0) {
         return "A list of objects to stream.";
      } else {
         return "No such input.";
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

      if (parm1 == 0) {
         return "A list of objects to stream.";
      } else {
         return "No such input.";
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
      String[] in = {
         "java.util.List"
      };

      return in;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s += "This module takes a list of objects and pushes them out as single ";
      s += "objects in a stream.  It also outputs the number of objects that ";
      s += "will be pushed out in total.  It is the other end of a couple with ";
      s += "StreamCatcher.  ";

      return s;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Stream Source"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  parm1 Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int parm1) {

      if (parm1 == 0) {
         return "Object in the stream";
      } else if (parm1 == 1) {
         return "Number of objects in the stream.";
      } else {
         return "";
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

      if (parm1 == 0) {
         return "Object.";
      } else if (parm1 == 1) {
         return "Object count.";
      } else {
         return "No such input.";
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
      String[] out = {
         "java.lang.Object", "java.lang.Integer"
      };

      return out;
   }


   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() { return (super.isReady() || (theList != null)); }

} // end class StreamSource
