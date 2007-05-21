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
package ncsa.d2k.modules.core.discovery.cluster.sample;


import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Title: OneTimeInputBroker</p>
 *
 * <p>Description: OnetimeInputBroker Take two inputs, one of which occurs only
 * once, and produce two outputs that both always fire</p>
 *
 * <p>Detailed Description: Take two inputs, the first of which occurs only
 * once. Having stored the singularly occurring input upon first receipt, each
 * time the second input is received, always output two outputs -- the stored
 * reference to the first input and the second input simply passed through.</p>
 *
 * <p>Data Type Restrictions: Any object can be input to either input port.</p>
 *
 * <p>Data Handling: This module does not modify anything.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class OneTimeInputBroker extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 8416221542967792310L;

   //~ Instance fields *********************************************************

   /**
    * A reference to the object that is read only once but is being output at
    * each execution of doit.
    */
   private Object m_readOnce = null;


   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   private boolean m_verbose = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new OneTimeInputBroker object.
    */
   public OneTimeInputBroker() { }

   //~ Methods *****************************************************************

   /**
    * Perform the work of the module: If this is the first call for doit - pulls
    * in the input from pipe indexed zero. In any case - Pulls in the input from
    * pipe indexed 1 and pushes out both <code>m_readOnce</codE> and the current
    * input.
    *
    * @throws java.lang.Exception If an unexpected error occurs. (Since no call
    *                             is made upon the pulled in objects (they are
    *                             just pulled in and pushed out) , such an
    *                             exception is indeed unexpected.
    */
   protected void doit() throws Exception {

      try {

         if (m_readOnce == null) {
            m_readOnce = this.pullInput(0);
         }

         Object out = this.pullInput(1);

         if (getVerbose()) {
        	 myLogger.setDebugLoggingLevel();//temp set to debug
        	 myLogger.debug("Input Broker pushing: " + this.getAlias());
        	 myLogger.resetLoggingLevel();//re-set level to original level
         }

         pushOutput(m_readOnce, 0);
         pushOutput(out, 1);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         myLogger.error("ERROR: OnetimeInputBroker.doit()");
         throw ex;
      }
   }

   private D2KModuleLogger myLogger;
   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Nulls the one time input object.
    */

   public void beginExecution() {
	   myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   m_readOnce = null; 
	   }


   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    * Nulls the <codE>m_readOnce</codE> object reference.
    */
   public void endExecution() {
      super.endExecution();
      m_readOnce = null;
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      if (inputIndex == 0) {
         return "Object that is received once but output with every firing.";
      } else if (inputIndex == 1) {
         return "Objects received repeatedly and passed through with each firing.";
      } else {
         return "";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int inputIndex) {

      if (inputIndex == 0) {
         return "Onetime Object";
      } else if (inputIndex == 1) {
         return "Repeating Object";
      } else {
         return "";
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
         "java.lang.Object", "java.lang.Object"
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
      s +=
         "Take two inputs, one of which occurs only once, and produce two " +
         "outputs that both always fire";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "Take two inputs, the first of which occurs only once.  " +
         "Having stored the singularly ";
      s +=
         "occurring input upon first receipt, each time the second input is " +
         "received, always output two outputs -- ";
      s +=
         "the stored reference to the first input and the second input " +
         " simply passed through.";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s += "Any object can be input to either input port.";
      s += "</p>";

      s += "<p>Data Handling: ";
      s += "This module does not modify anything.";
      s += "</p>";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Onetime Input Broker"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      if (outputIndex == 0) {
         return "Object that is received once but output with every firing.";
      } else if (outputIndex == 1) {
         return "Objects received repeatedly and passed through with each firing.";
      } else {
         return "";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      if (outputIndex == 0) {
         return "Onetime Object";
      } else if (outputIndex == 1) {
         return "Repeating Object";
      } else {
         return "";
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
         "java.lang.Object", "java.lang.Object"
      };

      return out;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[1];

      descriptions[0] =
         new PropertyDescription("verbose",
                                 "Verbose Output",
                                 "Do you want verbose output to the console.");

      return descriptions;
   }


   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return m_verbose; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run. This module is ready to run if it has input on both of its input
    * pipes, or if doit method was called already at least once and there is an
    * input ready in pipe indexed 1.
    *
    * @return Whether or not the module is ready to run.
    */

   public boolean isReady() {

      if (m_readOnce == null) {

         if ((this.getFlags()[0] > 0) && (this.getFlags()[1] > 0)) {
            return true;
         } else {
            return false;
         }
      } else if (this.getFlags()[1] == 0) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { m_verbose = b; }

} // end class OneTimeInputBroker
