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


/**
 * Take in an object once, saves it, and then pushes it out every time an object
 * comes into the other input.
 *
 * @author  pgroves
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class TriggerPushB extends DataPrepModule {

   //~ Instance fields *********************************************************

   /** The number of times the object has been output. */
   int totalFires = 0;

   /** Controls debug prints. */
   protected boolean debug;

   /** The object to fire. */
   protected Object theObject;

   /** True until object is acquired. */
   protected boolean waitingForObject = true;

   //~ Methods *****************************************************************

   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * In this case, initialize the variables, and call the superclass.
    */
   public void beginExecution() {
      waitingForObject = true;
      totalFires = 0;
      super.beginExecution();
   }

   /**
    * Performs the main work of the module. In this case, Input an object once,
    * saves it, and then pushes it out every time an object comes into the other
    * input.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   public void doit() throws Exception {

      if (waitingForObject) {
         theObject = pullInput(0);
         waitingForObject = false;

      }

      totalFires++;

      if (this.getFlags()[1] > 0) {
         pullInput(1);

         if (debug) {
            System.out.println("TriggerPushB: " + totalFires);
         }

         pushOutput(theObject, 0);
      }

      if (this.getFlags()[0] > 0) {
         theObject = pullInput(0);

         if (debug) {
            System.out.println("TriggerPushB: New object");
         }
      }


   } // end method doit

   /**
    * Reset the storage.
    */

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    * In this case, resets the storage.
    */
   public void endExecution() {
      waitingForObject = true;
      theObject = null;
      super.endExecution();
   }

   /**
    * Tests if debug printing is enabled.
    *
    * @return Whether or not debug prints are enabled
    */
   public boolean getDebug() { return debug; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "The object to be saved and passed";

         case 1:
            return "The triggering object";

         default:
            return "No such input";
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
      return "<p>Overview: Takes in an object and saves it. Every " +
             "time " +
             "an object enters the <i>other</i> input pipe, " +
             "the saved object will be pushed out the single " +
             "output. This will also assign a new object " +
             "to the saved object field whenever a new one " +
             "arrives, and that new one will become the one " +
             "to get pushed at each triggering.</p>";
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Trigger Push"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "The saved object pushed out every time there is " +
                   "a trigger object";

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
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];
      pds[0] =
         new PropertyDescription("debug",
                                 "Generate Debug Output",
                                 "If this property is true, the " +
                                 "module will write verbose debug " +
                                 "information to the console.");

      return pds;
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run. <In this case, the code does these specific activities>.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (waitingForObject) {
         return ((this.getFlags()[0] > 0) &&
                    (this.getFlags()[1] > 0));
      } else {
         return ((this.getFlags()[0] > 0) ||
                    (this.getFlags()[1] > 0));
      }

   }

   /**
    * Specifies whether or not to print debug statements.
    *
    * @param b True or false if debug statements should be printed
    */
   public void setDebug(boolean b) { debug = b; }
} // end class TriggerPushB
