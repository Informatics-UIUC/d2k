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

import java.io.Serializable;


/**
 * Describes a bin in this object.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class BinDescriptor implements Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -3182877646732871633L;

   //~ Instance fields *********************************************************

   /** the column number in the table */
   public int column_number;

   /** column label */
   public String label;

   /** bin name */
   public String name;

   //~ Constructors ************************************************************

   /**
    * Constructor
    *
    * @param col column index
    * @param lbl column label
    */
   public BinDescriptor(int col, String lbl) {
      column_number = col;
      label = lbl;
   }

   //~ Methods *****************************************************************

   /**
    * Evalaute d, return true if it falls in this bin, false otherwise.
    *
    * @param  d double value
    *
    * @return true if d falls in this bin, false otherwise
    */
   public abstract boolean eval(double d);

   /**
    * Evaluate s, return true if it falls in this bin, false otherwise
    *
    * @param s string value
    *
    * @return true if s falls in this bin, false otherwise
    */
   public abstract boolean eval(String s);


    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer sb =

                // new StringBuffer(table.getColumnLabel(column_number));
                new StringBuffer(label);
        sb.append(":");
        sb.append(name);

        return sb.toString();
    }
} // BinColumnsView$BinDescriptor
