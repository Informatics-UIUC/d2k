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
import ncsa.d2k.modules.core.util.*;//using D2KModuleLogger and Factory

/**
 * Push the first input it receives on input 0, but will not push subsequent
 * inputs on 0 until it receives an input on 1.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class WaitPush extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** Stores whether or not this is the first time the module has fired. */
   protected boolean firstTime = true;

   //~ Methods *****************************************************************
   private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

   private int moduleLoggingLevel=
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
	   .getLoggingLevel();
   
   public int getmoduleLoggingLevel(){
	   return moduleLoggingLevel;
   }

   public void setmoduleLoggingLevel(int level){
	   moduleLoggingLevel = level;
   }

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * In this case, flags the first execution.
    */
   public void beginExecution() { 
	   myLogger.setLoggingLevel(moduleLoggingLevel);
	   firstTime = true;
   }
   
   /**
    * Performs the main work of the module. In this case, if this is the first
    * time the module has fired, just pull input 0 and push it, subsequent runs,
    * pull input 1, push it and pull an input off the second input port, discard
    * it.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   public void doit() throws Exception {
      Object obj = this.pullInput(0);
      pushOutput(obj, 0);

      if (firstTime) {
         firstTime = false;
      } else {
         pullInput(1);
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
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

   /**
    * Description of method getInputName.
    *
    * @param  index Description of parameter index.
    *
    * @return Description of return value.
    */
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

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types = { "java.lang.Object", "java.lang.Object" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      return "<p>Overview: This module will collect the " +
             "input received on its first input, save" +
             " it and push it on the first invocation. " +
             "Subsequent inputs received on the second input" +
             " will cause the input saved from the first " +
             "input to be pushed again.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Wait Push"; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
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

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Object";

         default:
            return "No such output";
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
      String[] types = { "java.lang.Object" };

      return types;
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * @return Whether or not the module is ready to run.
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
   
   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] = 
          new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
                  "The logging level of this modules"+"\n 0=DEBUG; 1=INFO; 2=WARN; 3=ERROR; 4=FATAL; 5=OFF");

      return pds;
   }
} // end class WaitPush
