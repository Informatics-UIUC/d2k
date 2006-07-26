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
package ncsa.d2k.modules.core.datatype;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * HashLookupTable is a datatype designed for fast lookup of data 
 * that can be specified as a complete path through a tree of 
 * arbitrary breadth. It is conceived as an n-ary tree (loosely 
 * speaking) in which each non-leaf node is a Java HashMap.
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */

public class HashLookupTable implements Serializable {

   //~ Instance fields ************************************************

   /** Vector of "merged" HashMaps at each level. */
   private Vector all_maps;

   /** Labels for each level. */
   private String[] level_labels;

   /** Root HashMap. */
   private HashMap map; 

   //~ Constructors **************************************************

   /**
    * Constructs a new, empty HashLookupTable with a default capacity 
    * and load factor for the root node based on the Java HashMap 
    * default (<code>0.75</code> as of JDK 1.4.0).
    */
   public HashLookupTable() { map = new HashMap(); }

   /**
    * Constructs a new, empty HashLookupTable with the specified 
    * initial capacity and a default load factor for the root node.
    *
    * @param initialCapacity Description of parameter initialCapacity.
    */
   public HashLookupTable(int initialCapacity) {
      map = new HashMap(initialCapacity);
   }

   /**
    * Constructs a new HashLookupTable with the same mappings as the 
    * given map.
    *
    * @param t Map to copy.
    */
   public HashLookupTable(Map t) { map = new HashMap(t); }

   /**
    * Constructs a new, empty HashLookupTable with the specified 
    * initial capacity and the specified load factor for the root node.
    *
    * @param initialCapacity Description of parameter initialCapacity.
    * @param loadFactor      Description of parameter loadFactor.
    */
   public HashLookupTable(int initialCapacity, float loadFactor) {
      map = new HashMap(initialCapacity, loadFactor);
   }

   //~ Methods *******************************************************

   /**
    * Returns <code>true</code> if this table contains a mapping for 
    * the specified keys.
    *
    * @param  keys Ordered keys for which a mapping is to be tested.
    *
    * @return <code>true</code> if and only if the table contains a 
    *         mapping for the specified keys.
    */
   public boolean containsKeys(Object[] keys) {

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i])) {
            active = (HashMap) active.get(keys[i]);
         } else {
            return false;
         }

      }

      if (active.containsKey(keys[keys.length - 1])) {
         return true;
      } else {
         return false;
      }

   }

   /**
    * Returns the value to which this table maps the specified ordered 
    * keys, or <code>null</code> if the table contains no such mapping.
    * A return value of <code>null</code> does not necessarily indicate
    * that the table contains no mapping for the keys; it's also 
    * possible that the map explicitly maps the key to 
    * <code>null</code>. (The <code>containsKeys</code> method can be
    * used to distinguish these two cases.)
    *
    * @param  keys Ordered keys to be used to retrieve a value.
    *
    * @return The value to which this table maps the specified keys, 
    *         or <code> null</code> if no such mapping exists.
    */
   public Object get(Object[] keys) {

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i])) {
            active = (HashMap) active.get(keys[i]);
         } else {
            return null;
         }

      }

      if (active.containsKey(keys[keys.length - 1])) {
         return active.get(keys[keys.length - 1]);
      } else {
         return null;
      }

   } // end method get

   /**
    * Returns the text label for a given level.
    *
    * @param  level The level for which a label is to be returned.
    *
    * @return The String label for the given level.
    */
   public String getLabel(int level) { return level_labels[level]; }

   /**
    * Returns a HashMap containing as values all objects on a given 
    * level of the table. The keys are the Integer order of placement 
    * into the table, indexed from zero.
    *
    * @param  level The level for which values are to be returned.
    *
    * @return A HashMap containing the values at the specified level.
    */
   public HashMap getMerged(int level) { 
	   return (HashMap) all_maps.get(level); 
	   }

   /**
    * Associates the specified value with the specified set of keys in 
    * this table. If the table previously contained a mapping for the 
    * specified set of keys, the old value is replaced (and returned).
    *
    * @param  keys  Ordered keys with which the specified value is to 
    *               be associated.
    * @param  value Value to be associated with the specified keys.
    *
    * @return The previous value associated with the specified set of 
    *         keys, or <code>null</code> if no such association 
    *         previously existed. A <code>null</code> return can also 
    *         indicate that the table previously associated 
    *         <code>null</code> with the specified set of
    *         keys. (The <code>containsKeys</code> method can be used 
    *         to distinguish these two cases.)
    */
   public Object put(Object[] keys, Object value) {

      if (map.isEmpty()) {

         // Building the vector on the first insertion probably gives
         // us the best chance of not having to increase its capacity 
    	 // later.

         all_maps = new Vector(keys.length, 2);

         for (int i = 0; i < keys.length; i++) {
            all_maps.add(i, new HashMap());
         }

      }

      for (int i = 0; i < keys.length; i++) {

         if (!((HashMap) all_maps.get(i)).containsValue(keys[i])) {
            ((HashMap) all_maps.get(i)).put(
            		new Integer(((HashMap) all_maps.get(i))
                                           .size()), keys[i]);
         }
      }

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i])) {
            active = (HashMap) active.get(keys[i]);
         } else {
            active.put(keys[i], new HashMap());
            active = (HashMap) active.get(keys[i]);
         }

      }

      if (active.containsKey(keys[keys.length - 1])) {
         Object old_value = active.get(keys[keys.length - 1]);
         active.put(keys[keys.length - 1], value);

         return old_value;
      } else {
         active.put(keys[keys.length - 1], value);

         return null;
      }

   } // end method put

   /**
    * Allows the user to set a text label for each level of the tree.
    *
    * @param labels The Strings to be used as labels.
    */
   public void setLabels(String[] labels) { level_labels = labels; }

} // end class HashLookupTable
