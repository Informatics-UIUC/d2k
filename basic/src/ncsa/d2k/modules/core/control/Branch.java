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
 * A simple branch: select output according to <b>useFirstOutput</b>.
 *
 * <p>Input an object, output it to one of two outputs, selected by the <b>
 * useFirstOutput</b>.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class Branch extends DataPrepModule {

   //~ Instance fields ************************************************

   /** if useFirstOutput == true, push the first, else push the second. */
   private boolean useFirstOutput = true;

   //~ Methods ******************************************************

   /**
    * Input one object, output it to one of two outputs selected by <b>
    * useFirstOutput</b>.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      Object o = pullInput(0);

      if (useFirstOutput) {
         pushOutput(o, 0);
      } else {
         pushOutput(o, 1);
      }
   }

   
   public String getInputInfo(int i) { return "Object"; }

   public String getInputName(int i) { return "Object"; }

   public String[] getInputTypes() { return new String[] { "java.lang.Object" }; }

   public String getModuleInfo() {
      return "<p>Overview: Simple branch. Pushes data out the first output pipe" +
      		" if <i>useFirstOutput</i> is" +
             " true. Pushes data out the second output pipe if " +
             "<i>useFirstOutput</i> is " +
             "false.</p>";

   }

   public String getModuleName() { return "Branch"; }

   public String getOutputInfo(int i) { return "Object"; }

   public String getOutputName(int i) { return "Object"; }

   public String[] getOutputTypes() { return new String[] {
                                                "java.lang.Object",
                                                "java.lang.Object"
                                             }; }

   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] desc = new PropertyDescription[1];
      desc[0] =
         new PropertyDescription("useFirstOutput",
                                 "Use the first output",
                                 "Set this to true to use the first " +
                                 "output. Set this to false " +
                                 "to use the second output.");

      return desc;
   }

   public boolean getUseFirstOutput() { return useFirstOutput; }

   /**
    * Select first (<b>true</b>) or second.
    * <p>
    * Initially set to <b>true</b>.
    *
    * @param b true = output first, else second
    */
   public void setUseFirstOutput(boolean b) { useFirstOutput = b; }
} // end class Branch
