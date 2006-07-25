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
package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.util.QuickQueue;


/**
 * Push the first input it receives on input 0, but will not push 
 * subsequent inputs on 0 until it receives an input on one.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class QueuedTriggerPush extends DataPrepModule {

   //~ Instance fields ***********************************************

   /**
    * Inputs from 0 are queued until an input from 1. 
    * <p>
    * Inputs to 0 accumulate, and are output one at a time as
    * inputs to 1 occur.
    */
   QuickQueue queue;

   protected boolean debug;

   //~ Methods *******************************************************

   /**
    * Initialize the queue.
    */
   public void beginExecution() { queue = new QuickQueue(); }

   /**
    * If first time, just pull input 0 and push it, subsequent 
    * runs, pull input 1, push it and pull an input off the 
    * second input port, discard it.
    *
    * @throws Exception
    */
   public void doit() throws Exception {

      if (this.getInputPipeSize(0) > 0) {

         // Always prefer to read the first pipe, keep it clear.
         Object obj = this.pullInput(0);
         queue.push(obj);
      } else {

         // Here we know we have something in the queue, and there is 
    	 // something
         // in the trigger pipe, or we would not have enabled.
         Object obj = this.pullInput(1);

         if (queue.getSize() > 0) {
            this.pushOutput(queue.pop(), 0);
         }
      }
   }

   /**
    * Reset the queue.
    */
   public void endExecution() {

      if (queue.getSize() > 0) {
         System.out.println(this.getAlias() + ": There were " +
                            queue.getSize() + " items still in the " +
                            		"queue.");
      }

      queue = new QuickQueue();
   }

   public boolean getDebug() { return debug; }

   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "The object to push.";

         case 1:
            return "The triggering object.";

         default:
            return "No such input";
      }
   }

   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Object";

         case 1:
            return "Trigger";

         default:
            return "No such input";
      }
   }

   public String[] getInputTypes() {
      String[] types = { "java.lang.Object", "java.lang.Object" };

      return types;
   }

   public String getModuleInfo() {
      return "<p>Overview: This module will collect the input " +
      		"received on its" +
             " first input. When an input is received on the second " +
             "input, it will push" +
             " one of the items saved off the first input, if there " +
             "are any.</p>";
   }

   public String getModuleName() { return "Queued Trigger Push"; }

   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "The object received on the first input is pushed " +
            		"after it is received the first time, and after " +
            		"any input on the second input.";

         default:
            return "No such output";
      }
   }

   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Object";

         default:
            return "No such output";
      }
   }

   public String[] getOutputTypes() {
      String[] types = { "java.lang.Object" };

      return types;
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] =
         new PropertyDescription("debug",
                                 "Generate Debug Output",
                                 "If this property is true, the " +
                                 "module" +
                                 "will write verbose debug " +
                                 "information to " +
                                 "the console.");

      return pds;
   }

   /**
    * Ready to run if we have any data on any input.
    *
    * @return true if we are ready to fire.
    */
   public boolean isReady() {

      if (
          this.getInputPipeSize(0) > 0 ||
             (queue.getSize() > 0 && this.getInputPipeSize(1) > 0)) {
         return true;
      } else {
         return false;
      }
   }

   public void setDebug(boolean b) { debug = b; }
} // end class QueuedTriggerPush
