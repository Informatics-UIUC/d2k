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

import java.util.LinkedList;


/**
 * <p>Overview:
 * This module takes a stream of objects, one at a time, through its first
 * input slot.  The second input slot gives the count of how many
 * objects to expect.  When the expected number of objects have been pushed
 * through the door, this module pops out a list containing all of the
 * objects that have been streamed in.  This allows streaming itineraries to
 * operate correctly across item boundaries in D2KSL.  (Itineraries of this
 * sort include file loading and tagging in T2K, which could not be separated
 * into different items if stream catching were not implemented here.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class StreamCatcher extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** Description of field objCnt. */
   private int objCnt = -1;

   /** Description of field streamedObjects. */
   private LinkedList streamedObjects = new LinkedList();

   //~ Constructors ************************************************************

   /**
    * This class catches the output of a streaming module, and converts it into
    * a list of objects. This allows D2KSL to operate with streaming itineraries
    * broken across different items.
    */

   public StreamCatcher() { }

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    */
   public void beginExecution() { streamedObjects.clear(); }


   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      if (getFlags()[1] > 0) {

         // System.out.println("sc: GONNA pull input intvalue.  val is:
         // "+objCnt);
         objCnt = ((Integer) this.pullInput(1)).intValue();

         // System.out.println("sc: pulled input intvalue.  val is: "+objCnt);
         return;
      }

      if (getFlags()[0] > 0) {

         // System.out.println("sc: GONNA pull input object.");
         while (this.getFlags()[0] > 0) {

            // System.out.println("sc: pulling another input");
            streamedObjects.add(this.pullInput(0));
            // System.out.println("sc: DONE pulling another input");
         }
      }

      if (streamedObjects.size() == objCnt) {

         // System.out.println("sc: finished loop");
         this.pushOutput(streamedObjects, 0);
         // System.out.println("sc: pushed output -- byebye");
      } else {
         // System.out.println("sc: size not equal to count! size is:
         // "+streamedObjects.size()+                   "count is: "+objCnt);
      }
   } // end method doit


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
         return "One object in a stream of many.";
      } else if (parm1 == 1) {
         return "The number of objects which are to be streamed.";
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
         return "An object in the stream.";
      } else if (parm1 == 1) {
         return "Stream count.";
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
         "java.lang.Object", "java.lang.Integer"
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
      s += "This module takes a stream of objects, one at a time, through its ";
      s += "first input slot.  The second input slot gives the count of how ";
      s += "many objects to expect.  When the expected number of objects have ";
      s +=
         "been pushed through the door, this module pops out a list containing ";
      s += "all of the objects that have been streamed in.  This allows ";
      s += "streaming itineraries to operate correctly across item boundaries ";
      s +=
         "in D2KSL.  (Itineraries of this sort include file loading and tagging ";
      s += "in T2K, which could not be separated into different items if ";
      s += "stream catching were not implemented here.";

      return s;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Stream Catcher"; }


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
         return "List";
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
         return "List of Streamed Objects";
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
         "java.util.List"
      };

      return out;
   }


   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {
      return ((getFlags()[0] > 0) || (getFlags()[1] > 0));
   }

} // end class StreamCatcher
