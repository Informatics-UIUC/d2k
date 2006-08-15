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
package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntObjectHashMap;


/**
 * Description of class FPPattern.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class FPPattern implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 3553517370011965398L;

   /**
    * maps attribute index to attribute name. the keys are members of <code>
    * _patternElts</code>.
    */
   static private TIntObjectHashMap _eltMap = new TIntObjectHashMap();

   //~ Instance fields *********************************************************

   /** The features (column indices) that make this pattern, as a set. */
   private TIntHashSet _patternElts = new TIntHashSet();

   /** The support (expressed in percentage) of this pattern. */
   private int _support = 0;

   //~ Constructors ************************************************************

   /**
    * Creates a new FPPattern object.
    */
   public FPPattern() { }

   /**
    * Constructs an FPPAttern with the columns indices <code>col</codE> and the
    * support level <code>supp.</code>
    *
    * @param col  int[] Columns indices that make this pattern
    * @param supp int The support of this pattern.
    */
   public FPPattern(int[] col, int supp) {
      _support = supp;

      if (col != null) {
         _patternElts.addAll(col);
      }
   }

   /**
    * Constructs an FPPattern with one column <codE>col</code> and the support
    * level <codE>supp.</codE>
    *
    * @param col  int The index of the feature that makes this pattern
    * @param supp int The support level of the pattern
    */
   public FPPattern(int col, int supp) {
      _support = supp;
      _patternElts.add(col);
   }

   //~ Methods *****************************************************************

   /**
    * Adds a mapping of index <codE>k</code> to attribute name <code>v<c/code>.</code>
    *
    * @param k int The index of the element named <code>v</codE>
    * @param v String The name of attribute with index <codE>k</code>
    */
   static public void addElementMapping(int k, String v) { _eltMap.put(k, v); }

   /**
    * Clears the index to name map.
    */
   static public void clearElementMapping() { _eltMap.clear(); }

   /**
    * Returns the name of attribute index <code>i.</code>
    *
    * @param  i int The index of an attribute that is part of this pattern.
    *
    * @return String The name of attribute index <code>i</code>
    */
   static public String getElementLabel(int i) {
      return (String) _eltMap.get(i);
   }

   /**
    * Adds a feature index to the elements set.
    *
    * @param fte int An index of a feature that is part of this pattern
    */
   public void addPatternElt(int fte) { _patternElts.add(fte); }

   /**
    * Adds features indices to the elements set.
    *
    * @param col int[] feature indices that are part of this pattern.
    */
   public void addPatternElts(int[] col) { _patternElts.addAll(col); }

   /**
    * Clears the set of the elements.
    */
   public void clearPatterns() { _patternElts.clear(); }

   /**
    * Constructs a new FPPAttern object and copies the value in this pattern to
    * it. Returns the newly constructed object.
    *
    * @return FPPattern a copy of this FPPAttenr object.
    */
   public FPPattern copy() {
      FPPattern newpat = new FPPattern();
      newpat._support = this._support;
      newpat._patternElts.addAll(_patternElts.toArray());

      return newpat;
   }

   /**
    * Returns an iterator to the pattern's elements.
    *
    * @return TIntIterator an iterator for the pattern's elements.
    */
   public TIntIterator getPattern() { return _patternElts.iterator(); }

   /**
    * Returns the size of the integers hash set - the number of attributes this
    * pattenr contains.
    *
    * @return int The number of attributes this pattenr contains
    */
   public int getSize() { return _patternElts.size(); }

   /**
    * Returns the support for this pattenr.
    *
    * @return int The support for this pattenr.
    */
   public int getSupport() { return _support; }

   /**
    * Sets the support of this pattern.
    *
    * @param s int The value for the support of this pattern
    */
   public void setSupport(int s) { _support = s; }

   /**
    * A list like String representation of this pattern. For debug means
    *
    * @return String A list like String representation of this pattern.
    */
   public String toString() {
      String retVal = "[ ";
      TIntIterator it = this._patternElts.iterator();

      while (it.hasNext()) {
         int i = it.next();
         retVal += i + ", ";
      }

      if (retVal.length() > 2) {
         retVal = retVal.substring(0, retVal.length() - 2);
      }

      retVal += " ]";

      return retVal;
   }
} // end class FPPattern
