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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * A representaiton of a feature column in a table. Has references to the data,
 * the original index, label and count.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class FeatureTableElement {

   //~ Instance fields *********************************************************

   /**
    * The sum of the data of this feature in the original table.
    */
   private int _cnt = 0;

   /** The feature's index if all features are ordered by frequency in the original table.*/
   private int _lbl = -1;

   /** The position of this feature column in the original table. */
   private int _pos = -1;

   /**
    * An arrya list of <code>
    * ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth.FPTreeNode</code>
    * objects.
    */
   private ArrayList _ptrs = new ArrayList();

   //~ Constructors ************************************************************

   /**
    * Constructs a FeatureTableElement object with label, count and original
    * position.
    *
    * @param lbl int The feature's index if all features are ordered by frequency in the original table.
    * @param cnt int The sum of the data of this feature in the original table.
    * @param pos int Theoriginal position of this feature in the original table
    */
   public FeatureTableElement(int lbl, int cnt, int pos) {
      _lbl = lbl;
      _cnt = cnt;
      _pos = pos;
   }

   /**
    * Constructs a FeatureTableElement object with label, count, original
    * position and the data.
    *
    * @param lbl   int The feature's index if all features are ordered by frequency in the original table.
    * @param cnt   int The sum of the data of this feature in the original table.
    * @param pos   int Theoriginal position of this feature in the original
    *              table
    * @param nodes Collection The data elements. holds <code>
    *              ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth.FPTreeNode</code>
    *              objects
    */
   public FeatureTableElement(int lbl, int cnt, int pos, Collection nodes) {
      _lbl = lbl;
      _cnt = cnt;
      _pos = pos;

      if (nodes != null) {
         _ptrs = new ArrayList();
         _ptrs.addAll(nodes);
      }
   }

   //~ Methods *****************************************************************

   /**
    * Adds <codE>node</code> to the list of data elements.
    *
    * @param node FPTreeNode A data elements to be added to the pointers list.
    */
   public void addPointer(FPTreeNode node) { _ptrs.add(node); }


   /**
    * Clears the array list with the data elements.
    */
   public void clearList() {
      _ptrs.clear();
      _ptrs = null;
   }

   /**
    * Returns the sum of the data of this feature in the original table.
    *
    * @return int The sum of the data of this feature in the original table.
    */
   public int getCnt() { return _cnt; }


   /**
    * Returns the feature's index if all features are ordered by frequency in the original table.
    *
    * @return int The feature's index if all features are ordered by frequency in the original table.
    */
   public int getLabel() { return _lbl; }

   /**
    * Returns the collection with the data elements.
    *
    * @return List The data elements of this feature. Hlds <code>
    *         ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth.FPTreeNode</code>
    *         objects
    */
   public List getPointers() { return _ptrs; }


   /**
    * Returns an iterator over the list of the data elements.
    *
    * @return Iterator an iterator over the list of the data elements
    */
   public Iterator getPointersIter() { return _ptrs.iterator(); }

   /**
    * Returns the original index of this feature's column in the original table.
    *
    * @return int
    */
   public int getPosition() { return _pos; }
} // end class FeatureTableElement
