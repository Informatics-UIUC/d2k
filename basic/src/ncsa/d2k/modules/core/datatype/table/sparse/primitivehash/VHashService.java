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


import gnu.trove.TIntHashSet;

import java.util.Arrays;


/**
 * Collection of utility methods for operating on VHashMaps.
 *
 * @author  suvalala
 * @author  goren
 * @version $Revision$, $Date$
 */
public class VHashService {

   //~ Constructors ************************************************************

   /**
    * Creates a new VHashService object.
    */
   public VHashService() { }

   //~ Methods *****************************************************************

   /**
    * For each key k in <code>map</code> that is greater than <code>val</code>,
    * remove key k and its mapped value and map this value to key k+1.
    *
    * @param val Keys greater than this value will decremented
    * @param map Map for which keys are to be decremented
    */
   static public void decrementKeys(int val, VIntIntHashMap map) {
      int[] keys = map.keys();
      Arrays.sort(keys);

      for (int i = keys.length - 1; keys[i] > val; i--) {
         int value = map.remove(keys[i]);
         map.put(keys[i] - 1, value);
      }
   }

   /**
    * Finds the index within the specified array that the specified value occurs
    * or would occur.
    *
    * @param  arr   Sorted array to search
    * @param  value Value to search for
    *
    * @return Index of the value
    */
   static public int findEndPlace(int[] arr, int value) {

      int retVal = Arrays.binarySearch(arr, value);

      if (retVal < 0) {
         retVal = (retVal + 1) * -1 - 1;
      }

      return retVal;
   }

   /**
    * Finds the key of <code>val</code> in <code>map</code> and returns it.
    *
    * @param  val Value in the int to int hashmap
    * @param  map An int to int hashmap
    *
    * @return Key of <code>val</code> in <code>map</code>. If <code>val</code>
    *         is not a value in <code>map</code>, returns -1.
    */
   static public int findKey(int val, VIntIntHashMap map) {
      int retVal = -1;

      if (!map.containsValue(val)) {
         return retVal;
      }

      int[] keys = map.keys();

      for (int i = 0; i < keys.length; i++) {
         int currVal = map.get(keys[i]);

         if (currVal == val) {
            retVal = keys[i];
         }
      }

      return retVal;

   }

   /**
    * Finds the index within the specified array that the specified value occurs
    * or would occur.
    *
    * @param  arr   Sorted array to search
    * @param  value Value to search for
    *
    * @return Index of the value. If the returned value is greater than or equal
    *         to the length of <code>arr</code> then all values in <code>
    *         arr</code> are smaller than <code>value</code>.
    */
   static public int findPlace(int[] arr, int value) {

      int retVal = Arrays.binarySearch(arr, value);

      if (retVal < 0) {
         retVal = (retVal + 1) * -1;
      }

      return retVal;
   }

   /**
    * Returns the indices contained in the map.
    *
    * @param  map Map to get the indices from
    *
    * @return Array of indices
    */
   static public int[] getIndices(VHashMap map) {

      // retrieving all valid rows and sorting them
      int[] validIndices = map.keys();
      Arrays.sort(validIndices);

      return validIndices;
   }

   /**
    * Retrieves valid keys from <code>map</code> in the range of key number
    * <code>begin</code> through key number <code>end</code>.
    *
    * @param  begin Key number from which to begin retrieving of keys
    * @param  end   Last key number in the range from which the keys are
    *               retrieved
    * @param  map   VHashMap from which to retrieve the keys
    *
    * @return An int array that holds the valid keys in the range <code>[begin,
    *         end]</code>
    */
   static public int[] getIndicesInRange(int begin, int end, VHashMap map) {

      int[] keysInRange = new int[0]; // the returned value

      if (end < begin) {
         return keysInRange;
      }

      // retrieving all valid rows and sorting them
      int[] validIndices = map.keys();
      Arrays.sort(validIndices);

      int beginIndex = findPlace(validIndices, begin);
      int endIndex = findEndPlace(validIndices, end);

      // if begin is greater than any valid row number - return an empty array
      if (beginIndex >= validIndices.length) {
         return keysInRange;
      }

      if (endIndex >= validIndices.length) {
         endIndex = validIndices.length - 1;
      }

      int numKeysInRange = endIndex - beginIndex + 1;
      keysInRange = new int[numKeysInRange];
      System.arraycopy(validIndices, beginIndex, keysInRange, 0,
                       numKeysInRange);

      return keysInRange;
   } // end method getIndicesInRange


   /**
    * Returns an array of ints with keys from <code>map</code> that are not part
    * of <code>invalid</code> set and also are in the range [begin, end].
    *
    * @param  begin     Beginning of range to include keys from <code>map</code>
    *                   in the returned array
    * @param  end       End of range to include keys from map in the returned
    *                   array
    * @param  map       VHashMap keys from this map are to be included in the
    *                   returned array
    * @param  invalid_1 Items from this set should be discluded from the
    *                   returned value
    * @param  invalid_2 Items from this set should be discluded from the
    *                   returned value
    *
    * @return Keys from <code>map</code> that are not part of the <code>invalid
    *         sets and also are in the range [begin, end]</code>
    */
   static public int[] getIndicesInRange(int begin, int end, VHashMap map,
                                         TIntHashSet invalid_1,
                                         TIntHashSet invalid_2) {
      int[] validIndices = new int[0]; // the returned value

      if (end < begin) {
         return validIndices;
      }


      // retrieve all keys
      int[] allKeys = map.keys();

      // idx will hold the valid keys.
      TIntHashSet idx = new TIntHashSet();

      // for each key in the map
      for (int i = 0; i < allKeys.length; i++) {

         // if it is not in invalid and in the range [begin, end]
         if (
             !invalid_1.contains(allKeys[i]) &&
                !invalid_2.contains(allKeys[i]) &&
                allKeys[i] >= begin &&
                allKeys[i] <= end) {

            // include in idx
            idx.add(allKeys[i]);
         }
      }

      // retrieve the set as an array
      validIndices = idx.toArray();

      return validIndices;

   } // end method getIndicesInRange

   /**
    * Returns the keys of <code>values</code> from <code>map</code>.
    *
    * @param  values Values held in <code>map</code>
    * @param  map    An int to int hashmap
    *
    * @return Keys in <code>map</code> that are mapped to <code>values</code>,
    *         such that retVal[i] is the key of <code>values[i]</code> in <code>
    *         map</code>.
    */
   static public int[] getKeys(int[] values, VIntIntHashMap map) {
      int[] retVal = new int[values.length];
      VIntIntHashMap tempMap = new VIntIntHashMap(map.size());
      int[] allKeys = map.keys();

      for (int i = 0; i < allKeys.length; i++) {
         tempMap.put(map.get(allKeys[i]), allKeys[i]);
      }

      for (int i = 0; i < values.length; i++) {
         retVal[i] = tempMap.get(values[i]);
      }

      return retVal;
   }

   /**
    * Returns an int to int hashmap that defines a new mapping s.t. for the
    * hashmap that activated this method. The value that was mapped to <code>
    * newOrder[i]</code> (the value in the returned map) will be mapped to
    * <code>oldOrder[i]</code> (the key in the returned map). <code>
    * oldOrder</code> and <code>newOrder</code> must be of the same length.
    *
    * @param  oldOrder Order of the values before sorting
    * @param  newOrder New order of the values
    *
    * @return VIntIntHashMap that defines how to reorder the values.
    */
   static public VIntIntHashMap getMappedOrder(int[] oldOrder, int[] newOrder) {
      VIntIntHashMap retVal = new VIntIntHashMap(oldOrder.length);

      for (int i = 0; i < oldOrder.length && i < newOrder.length; i++) {
         retVal.put(oldOrder[i], newOrder[i]);
      }

      return retVal;
   }

   /**
    * Returns the maximal key in <code>map</code>.
    *
    * @param  map Map to get the maximal key from
    *
    * @return Returns the maximal key in <code>map</code>.
    */
   static public int getMaxKey(VHashMap map) {
      int[] keys = getIndices(map);

      if (keys.length == 0) {
         return -1;
      }

      return keys[keys.length - 1];
   }


   /**
    * For each key k in <code>map</code> that is equal to or greater than <code>
    * val</code>, remove key k and its mapped value and map this value to key
    * k+1.
    *
    * @param val Keys greater than or equal to this value are incremented
    * @param map Keys of this map are to be incremented
    */
   static public void incrementKeys(int val, VIntIntHashMap map) {
      int[] keys = map.keys();
      Arrays.sort(keys);

      for (int i = keys.length - 1; keys[i] >= val; i--) {
         int value = map.remove(keys[i]);
         map.put(keys[i] + 1, value);
      }
   }

   /**
    * For each values in <code>map</code>, that is equal to or greater than
    * <code>val</code>, increment this value by 1.
    *
    * @param val Lower boundary of values to be incremented
    * @param map Hashmap to have its values incremented
    */
   static public void incrementValues(int val, VIntIntHashMap map) {
      int[] keys = map.keys();

      // iterating over keys in the map
      for (int i = 0; i < keys.length; i++) {

         int currVal = map.get(keys[i]);

         // if current value is equal to or greater than val
         if (currVal >= val) {
            map.increment(keys[i]);
         }
      }
   }

   /**
    * Returns an int to int hashmap that represents a new order as specified by
    * <code>newOrder</code>. For each item <code>newOrder[i]</code> that is a
    * valid key in <code>map</code> - mapping it to key i in the returned value.
    *
    * @param  newOrder An int array that defines a new order. Each value val
    *                  that was mapped to key <code>newOrder[i]</code> should be
    *                  mapped to key i.
    * @param  map      Map to be reordered by <code>newOrder</code>.
    *
    * @return VIntIntHashMap representing <code>newOrder</code>'s items
    *         intersected with <code>map</code>'s keys.
    */
   static public VIntIntHashMap toMap(int[] newOrder, VHashMap map) {
      VIntIntHashMap retVal = new VIntIntHashMap(map.size());

      for (int i = 0; i < newOrder.length; i++) {

         if (map.containsKey(newOrder[i])) {
            retVal.put(i, newOrder[i]);
         }
      }

      return retVal;
   }


} // end class VHashService
