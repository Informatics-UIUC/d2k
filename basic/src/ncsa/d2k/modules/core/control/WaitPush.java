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


/**
 *  Push the first input it receives on input 0, but will not push 
 *  subsequent inputs on 0 until it receives an input on one.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class WaitPush extends DataPrepModule {

   //~ Instance fields ************************************************

   /** Description of field firstTime. */
   protected boolean firstTime = true;

   //~ Methods ******************************************************

   /**
    * set the first execution flag.
    */
   public void beginExecution() { firstTime = true; }

   /**
    * If first time, just pull input 0 and push it, subsequent runs, 
    * pull input 1, push it and pull an input off the second input 
    * port, discard it.
    *
    */
   public void doit() throws Exception {
      Object obj = this.pullInput(0);
      this.pushOutput(obj, 0);

      if (firstTime) {
         firstTime = false;
      } else {
         this.pullInput(1);
      }
   }

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
      return "<p>Overview: This module will collect the " +
             "input received on its first input, save" +
             " it and push it on the first invocation. " +
             "Subsequent inputs received on the second input" +
             " will cause the input saved from the first " +
             "input to be pushed again.</p>";
   }

   public String getModuleName() { return "Wait Push"; }

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

   /**
    * Ready to run if we have received our first input on 
    * port zero or we
    * have inputs on zero and one.
    *
    * @return true if we are ready to fire.
    */
   public boolean isReady() {

      if (firstTime && this.getFlags()[0] > 0) {
         return true;
      } else {

         if (this.getFlags()[0] > 0 && this.getFlags()[1] > 0) {
            return true;
         }
      }

      return false;
   }

} // end class WaitPush
