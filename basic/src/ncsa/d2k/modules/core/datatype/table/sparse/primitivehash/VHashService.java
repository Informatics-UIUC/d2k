package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============

import java.util.Arrays;
import gnu.trove.TIntHashSet;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VHashService {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public VHashService() {
  }

  //================
  // Static Methods
  //================

  public static int[] getIndices(VHashMap map) {
    //retrieving all valid rows and sorting them
    int[] validIndices = map.keys();
    Arrays.sort(validIndices);
    return validIndices;
  }


  /**
   * returns an array of ints with keys from <code>map</codE> that are not part
   * of <codE>invalid</code> set and also are int he range [begin, end]
   *
   * @param begin int beginning of range to include keys from map in the returned value
   * @param end int end of range to include keys from map in the returned value
   * @param map VHashMap keys from this map are to be included in the returned value
   * @param invalid_1 TIntHashSet items from this set should be discluded from the returned value
   * @param invalid_2 TIntHashSet items from this set should be discluded from the returned value
   * @return int[] has keys from <code>map</codE> that are not part
   * of <codE>invalid</code> set and also are int he range [begin, end]
   */
  public static int[] getIndicesInRange(int begin, int end, VHashMap map,
                                        TIntHashSet invalid_1, TIntHashSet invalid_2) {
    int[] validIndices = new int[0]; //the returned value

    if (end < begin)
      return validIndices;



//retrieving all keys
    int[] allKeys = map.keys();

    //idx will hold the valid keys.
    TIntHashSet idx = new TIntHashSet ();

//for each key in the map
    for (int i = 0; i < allKeys.length; i++) {
      //if it is not in invalid
      //and in the range [begin, end]
      if (!invalid_1.contains(allKeys[i]) && !invalid_2.contains(allKeys[i]) &&
          allKeys[i] >= begin && allKeys[i] <= end) {
        //include in idx
        idx.add(allKeys[i]);
      }
    }
    //retrieve the set as an array
    validIndices = idx.toArray();
    //return it
    return validIndices;
/*    Arrays.sort(validIndices);

// XIAOLEI
    int beginIndex = findPlace(validIndices, begin);
    int endIndex = findEndPlace(validIndices, end);

//System.out.println(beginIndex + " ---> " + endIndex);

//if begin is greater than any valid row number - return an empty array
    if (beginIndex >= validIndices.length)
      return keysInRange;

    if (endIndex >= validIndices.length)
      endIndex = validIndices.length - 1;

    int numKeysInRange = endIndex - beginIndex + 1;
    keysInRange = new int[numKeysInRange];
    System.arraycopy(validIndices, beginIndex, keysInRange, 0, numKeysInRange);
*/


  }

  /**
       * Retrieves valid keys from <code>map</cdoe> in the section key no. <code>begin
   * </code> through key no. <code>end</code>.
   *
   * @param begin   key no. from which to begin retrieving of keys.
       * @param end     last key no. in the section from which the keys are retrieved.
   * @param map     a VHashMap from which to retrieve the keys.
   * @return        an int array that holds the valid keys in the range <code>
   *                [begin, end]</cdoe>
   */
  public static int[] getIndicesInRange(int begin, int end, VHashMap map) {

    int[] keysInRange = new int[0]; //the returned value

    if (end < begin)
      return keysInRange;

    //retrieving all valid rows and sorting them
    int[] validIndices = map.keys();
    Arrays.sort(validIndices);

    //for (int i = 0; i < validIndices.length; i++)
    //System.out.print(validIndices[i] + ", ");
    //System.out.println();

    // XIAOLEI
    int beginIndex = findPlace(validIndices, begin);
    int endIndex = findEndPlace(validIndices, end);

    //System.out.println(beginIndex + " ---> " + endIndex);

    //if begin is greater than any valid row number - return an empty array
    if (beginIndex >= validIndices.length)
      return keysInRange;

    if (endIndex >= validIndices.length)
      endIndex = validIndices.length - 1;

    int numKeysInRange = endIndex - beginIndex + 1;
    keysInRange = new int[numKeysInRange];
    System.arraycopy(validIndices, beginIndex, keysInRange, 0, numKeysInRange);

    return keysInRange;
  }

  /**
   * Returns an int to int hashmap that defines a new mapping s.t. for the
   * hashmap that activated this method: the value that was mapped to <code>
       * newOrder[i]</code> (the value in the returned map) will be mapped to <code>
   * oldOrder[i]</code> (the key in the returned map).
   *
   * <code>oldOrder</code> and <code>newOrder</code> must be of the same length
   *
   * @param oldOrder    the order of the values before sorting
   * @param newOrder    the new order of the values.
       * @return            a VIntIntHashMap that defines how to reorder the values.
   */
  public static VIntIntHashMap getMappedOrder(int[] oldOrder, int[] newOrder) {
    VIntIntHashMap retVal = new VIntIntHashMap(oldOrder.length);

    for (int i = 0; i < oldOrder.length && i < newOrder.length; i++)
      retVal.put(oldOrder[i], newOrder[i]);

    return retVal;
  }

  /**
   * Returns an int to int hashmap that represent a new order as specified by
   * <codE>newOrder</cdoe>: For each item <codE>newOrder[i]</code> that is
   * a valid key in <code>map</code> - mapping it to key i in the returned
   * value.
   *
   * @param newOrder    an int array htat defines a new order: each value val
   *                    that was mapped to key <codE>newOrder[i]</cdoe> should
   *                    be mapped to key i.
   * @param map         the map to be reordered by <code>newOrder</code>.
   * @return            a VIntIntHashMap representing <code>newOrder</code>'s
   *                    items intersected with <codE>map</code>'s keys.
   */
  public static VIntIntHashMap toMap(int[] newOrder, VHashMap map) {
    VIntIntHashMap retVal = new VIntIntHashMap(map.size());
    for (int i = 0; i < newOrder.length; i++)
      if (map.containsKey(newOrder[i]))
        retVal.put(i, newOrder[i]);

    return retVal;
  }

  /**
   * Finds the index of <codE>value</code> in <code>arr</code>
   *
   * @param arr       a sorted array of ints.
   * @param value     a value to find its index in <code>arr</codE>
   * @return          an int: if the returned value is greater than or equal
   *                  to the length of <code>arr</code> then - all values in
   *                  <code>arr</code> are smaller than <codE>value</code>.
   */
  public static int findPlace(int[] arr, int value) {

    int retVal = Arrays.binarySearch(arr, value);

    if (retVal < 0) {
      retVal = (retVal + 1) * -1;
    }

    return retVal;
  }

  //XIAOLEI
  public static int findEndPlace(int[] arr, int value) {

    int retVal = Arrays.binarySearch(arr, value);

    if (retVal < 0) {
      retVal = (retVal + 1) * -1 - 1;
    }

    return retVal;
  }

  /**
   * Returns the maximal key in <code>map</code>
   */
  public static int getMaxKey(VHashMap map) {
    int[] keys = getIndices(map);
    if (keys.length == 0)
      return -1;
    return keys[keys.length - 1];
  }

  /**
   * for each values in <code>map</code>, that is equal to or greater than
   * <codE>val</codE>, increment this value by 1
   * @param val lower boundary of values to be incremented.
   * @param map a hashmap that its values should be incremented.
   */
   public static void incrementValues(int val, VIntIntHashMap map){
     int[] keys = map.keys();
     //iterating over keys in the map
     for (int i=0; i<keys.length; i++){

       int currVal = map.get(keys[i]);
       //if current value is equal to or greater than val
       if(currVal >= val)
         map.increment(keys[i]);


     }//for

   }


   /**
    * for each values in <code>map</code>, that is greater than
    * <codE>val</codE>, decrement this value by 1
    * @param val lower boundary of values to be decremented.
    * @param map a hashmap that its values should be decremented.
    */
 /*  public static void decrementValues(int val, VIntIntHashMap map){
      TIntIntIterator it = map.iterator();
      //iterating over keys in the map
      while(it.hasNext()){
        int currKey = it.key();
        int currVal = map.get(currKey);
        //if current value is equal to or greater than val
        if(currVal > val){
          //removing this value
          map.remove(currKey);
          //putting a new one, incremented.
          map.put(currKey, currVal-1);
        }
      }

    }*/


   /**
    * for each key k in <code>map</code> that is equal to or greater than
    * <codE>val</code>, remove key k and its mapped value and map this value
    * to key k+1.
    *
    * @param val - keys greater than or equal to val are incremented.
    * @param map - the keys of this map are to be incremented.
    */
   public static void incrementKeys(int val, VIntIntHashMap map){
     int[] keys = map.keys();
     Arrays.sort(keys);
     for (int i=keys.length -1; keys[i] >= val; i--){
       int value = map.remove(keys[i]);
       map.put(keys[i] + 1, value);
     }
   }


   /**
       * for each key k in <code>map</code> that is greater than
       * <codE>val</code>, remove key k and its mapped value and map this value
       * to key k+1.
       *
       * @param val - keys greater than val are decremented.
       * @param map - the keys of this map are to be decremented.
       */
      public static void decrementKeys(int val, VIntIntHashMap map){
        int[] keys = map.keys();
        Arrays.sort(keys);
        for (int i=keys.length -1; keys[i] > val; i--){
          int value = map.remove(keys[i]);
          map.put(keys[i] - 1, value);
        }
      }

      /**
       * finds the key of <code>val</code> in <code> map</code> and returns it
       * @param val - a value in the int to int hashmap
       * @param map - an int to int hashmap
       * @return - return s the key of <codE>val</code> in <codE>map</code>.
       * if <codE>vla</code> is not a value in <codE>map</codE>, returns -1.
       */
      public static int findKey(int val, VIntIntHashMap map){
        int retVal = -1;
        if(!map.containsValue(val)) return retVal;

        int[] keys = map.keys();

        for (int i=0; i<keys.length; i++){
          int currVal = map.get(keys[i]);
          if(currVal == val) retVal = keys[i];
        }
        return retVal;

      }//findKey

      /**
       * returns the keys of <codE>values</codE> in <code>map</code>.
       * @param values - values held in <codE>map</code>
       * @param map - an int to int hashmap
       * @return - keys in <codE>map</codE> that are mapped to <codE>values</codE>, such that
       * retVal[i] is the key of <codE>values[i]</codE> in map.
       */
       public static int[] getKeys(int[] values, VIntIntHashMap map){
         int[] retVal = new int[values.length];
         VIntIntHashMap tempMap = new VIntIntHashMap (map.size());
         int[] allKeys = map.keys();
         for (int i=0; i<allKeys.length; i++)
            tempMap.put(map.get(allKeys[i]), allKeys[i]);

         for(int i=0; i<values.length; i++){
           retVal[i] = tempMap.get(values[i]);
         }
         return retVal;
       }//getKeys





}//VHashService
