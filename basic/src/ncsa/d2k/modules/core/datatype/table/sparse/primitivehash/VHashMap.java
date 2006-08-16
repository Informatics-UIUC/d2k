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
package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

/**
 * Interface for a hashmap containing primitives.
 *
 * @author  goren
 * @version $Revision$, $Date$
 */

public interface VHashMap {

   //~ Methods *****************************************************************

   /**
    * Tests whether or not this hashmap contains the specified key.
    *
    * @param  key Key to check for
    *
    * @return Whether or not the map contains the key
    */
   public boolean containsKey(int key);

   /**
    * Tests for equality, this <code>VHashMap</code> with the one passed in.
    *
    * @param  other Another <code>VHashMap</code> to compare
    *
    * @return True if this hashmap equals the passed in hashmap
    */
   public boolean equals(Object other);

   /**
    * Returns a hashmap that represents the sorted order of the values in this
    * map.
    *
    * @return Hashmap that represents the sorted order of the values in this map
    */
   public VIntIntHashMap getSortedOrder();

   /**
    * Returns hashmap that represents the sorted order of the values in this map
    * in the range <code>begin</code> through <code>end</code>.
    *
    * @param  begin Key index from which to start retrieving the new order
    * @param  end   Last key in the section from which to retrieve the new order
    *
    * @return VIntIntHashMap with valid keys from the specified section
    */
   public VIntIntHashMap getSortedOrder(int begin, int end);

   /**
    * Inserts <code>obj</code> into the map with key <code>key<code>. All values
    * mapped to keys <code>key</code> and on will be mapped to a key greater by
    * one.</code></code>
    *
    * @param obj An object to be inserted into the map.
    * @param key Insertion key
    */
   public void insertObject(Object obj, int key);

   /**
    * Returns the keys of the map.
    *
    * @return Int array of the keys of the map
    */
   public int[] keys();

   /**
    * Returns a new <code>VHashMap</code> with reordered mappings as defined by
    * <code>newOrder.</code>
    *
    * @param  newOrder An int to int hashmap that defines the new order. For
    *                  each pair (key, val) in <code>newOrder</code> the value
    *                  that was mapped to val will be mapped to key in the
    *                  returned value.
    *
    * @return VHashMap with the same values as this one, reordered.
    */
   public VHashMap reorder(VIntIntHashMap newOrder);

   /**
    * Replaces the object at the specified key with the specified object.
    *
    * @param obj Object to replace the one at the specified key
    * @param key Key to replace the object
    */
   public void replaceObject(Object obj, int key);

   /**
    * Returns the size of the map.
    *
    * @return Size of the map
    */
   public int size();

} // end interface VHashMap
