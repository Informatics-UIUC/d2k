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
package ncsa.d2k.modules.core.transform.binning;

import java.util.HashSet;


/**
 * Bin that holds strings
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class TextualBinDescriptor extends BinDescriptor {

   //~ Instance fields *********************************************************

   /** Set of values that fall in this bin */
   public HashSet vals;

   //~ Constructors ************************************************************

   /**
    * Constructor
    *
    * @param col   column index
    * @param n     bin name
    * @param v     set of values that fall in this bin
    * @param label column label
    */
   public TextualBinDescriptor(int col, String n, String[] v, String label) {
      super(col, label);
      name = n;
      vals = new HashSet();

      for (int i = 0; i < v.length; i++) {
         vals.add(v[i]);
      }
   }

   //~ Methods *****************************************************************


    /**
     * Evalaute d, return true if it falls in this bin, false otherwise.
     *
     * @param d double value
     * @return true if d falls in this bin, false otherwise
     */
    public boolean eval(double d) {
        return eval(Double.toString(d));
    }


    /**
     * Evaluate s, return true if it falls in this bin, false otherwise
     *
     * @param s string value
     * @return true if s falls in this bin, false otherwise
     */
    public boolean eval(String s) {
        return vals.contains(s);
    }
} // BinColumnsView$TextualBinDescriptor
