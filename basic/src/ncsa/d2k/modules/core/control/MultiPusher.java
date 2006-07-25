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

import ncsa.d2k.core.modules.PropertyDescription;


/**
 * Push the input object out N times, where N is a property
 * or input.
 *
 * @author  Peter Groves 7/15/01 Revised 04/06/03 by pgroves
 * @version $Revision$, $Date$
 */
public class MultiPusher extends ncsa.d2k.core.modules.DataPrepModule
   implements java.io.Serializable {

   //~ Instance fields ***********************************************

   /**
    * false- will wait for and use the Integer in input 1 true - 
    * will use the N in properties.
    */
   private boolean usePropNValue = false;

   boolean debug = false;
   

   //////////////////////
   // d2k Props
   ////////////////////

   /** the number of times to push the object. */
   int N = 4;

   /**
    * the current number of times this module has fired, indicating the number
    * of times the input object has been pushed.
    */
   int numFires = 0;

   /** the object that is input (pulled in). */
   Object obj;

   /**
    * the total number of times this module has fired since the itin began, this
    * is used only for debugging.
    */
   int totalFires = 0;

   //~ Methods ********************************************************

   /**
    * Initialize the counters and call the superclass.
    */
   public void beginExecution() {
      numFires = 0;
      totalFires = 0;
      super.beginExecution();
   }

   /////////////////////
   // work methods
   ////////////////////

   /**
    * Input one object, output it once for each of N calls to doit.
    *
    */
   public void doit() throws Exception {

      if (numFires == 0) {
         obj = pullInput(0);

         if (!usePropNValue) {
            Integer I = (Integer) pullInput(1);
            N = I.intValue();
         }
      }

      pushOutput(obj, 0);
      numFires++;
      totalFires++;

      if (debug) {
         System.out.println(this.getAlias() + " current numFires:" + 
        		 numFires +
        		 "/" + N + ", total number fires this execution:" +
                            totalFires);
      }

      if (numFires == N) {
         obj = null;
         numFires = 0;
      }
   }

   public boolean getDebug() { return debug; }

   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "The object to be passed multiple times";

         case 1:
            return "The number of times to pass the input object. " +
            		"This module" +
                   " must receive exactly one Integer object for " +
                   "every object" +
                   " passed to the first input (unless the " +
                   "property <i> Use the " +
                   "value of \"N\" from the properties</i> is set " +
                   "to TRUE)";

         default:
            return "No such input";
      }
   }

   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Object In";

         case 1:
            return "N";

         default:
            return "NO SUCH INPUT!";
      }
   }

   public String[] getInputTypes() {
      String[] types = { "java.lang.Object", "java.lang.Integer" };

      return types;
   }

   public String getModuleInfo() {
      return "<p>Overview: Takes any object, pushes it out N times, " +
      		"where N is either set" +
             " as a property or passed in as an input. See " +
             "property descriptions " +
             " for details on controlling this behavior.</p>";
   }


   ////////////////////////
   /// D2K Info Methods
   /////////////////////

   public String getModuleName() { return "MultiPusher"; }

   public String getOutputInfo(int index) {

      switch (index) {

         case 0:
            return "One of multiple pushes of the object.";

         default:
            return "No such output";
      }
   }

   public String getOutputName(int index) {

      switch (index) {

         case 0:
            return "Object Out";

         default:
            return "NO SUCH OUTPUT!";
      }
   }

   public String[] getOutputTypes() {
      String[] types = { "java.lang.Object" };

      return types;
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] =
         new PropertyDescription("usePropNValue",
                                 "Use the value of \"N\" from the " +
                                 "properties",
                                 "If TRUE, the number of times the " +
                                 "object is pushed will be determined" +
                                 " by the <i>N - Number Times to " +
                                 "Push</i> property. In this case, the " +
                                 "module will not wait for the second " +
                                 "input before firing. If FALSE," +
                                 " the value of \"N\" will be " +
                                 "determined by the second input.");
      pds[1] =
         new PropertyDescription("timesToFire",
                                 "N - Number Times to Push",
                                 "The number of times to pass the " +
                                 "input object");

      pds[2] =
         new PropertyDescription("debug",
                                 "Generate Debug Output",
                                 "If true, will write to the console " +
                                 "the number of times the module has " +
                                 "fired, every " +
                                 "time it is fired.");

      return pds;
   }

   /**
    * The total times the module will fire.
    *
    * @return Description of return value.
    */
   public int getTimesToFire() { return N; }

   public boolean getUsePropNValue() { return usePropNValue; }

   //////////////////////////
   ///d2k control methods
   ///////////////////////

   /**
    * isReady() At first, checks to see if the object is in and if 
    * it should
    * wait for input (1) (the Integer N), otherwise returns the 
    * superclass's
    * isReady. Once it has the object and knows how many times to 
    * pass it,
    * returns true until it has been triggered all N times.
    *
    * @return isReady() At first, checks to see if the object is in 
    * and if it
    *         should wait for input (1) (the Integer N), otherwise 
    *         returns the
    *         superclass's isReady. Once it has the object and knows 
    *         how many
    *         times to pass it, returns true until it has been 
    *         triggered all N
    *         times.
    */
   public boolean isReady() {

      if (numFires == 0) { // this is the 'first' run

         if (
             (this.getFlags()[0] > 0) && // we have the object to push
                ((usePropNValue) || (this.getFlags()[1] > 0))) { // we have the number of times
            return true;
         } else {
            return false;
         }
      }

      if ((0 < numFires) && (numFires < N)) { // we have everything, pushing N
                                              // times
         return true;
      }

      return false;
   }

   public void setDebug(boolean b) { debug = b; }

   /**
    * Set the total times to fire (default = 4).
    *
    * @param i Description of parameter i.
    */
   public void setTimesToFire(int i) { N = i; }

   public void setUsePropNValue(boolean b) { usePropNValue = b; }
} // end class MultiPusher
