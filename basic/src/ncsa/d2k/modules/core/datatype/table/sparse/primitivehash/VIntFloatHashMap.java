package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

import gnu.trove.TIntFloatHashMap;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.SparseFloatColumn;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VIntFloatHashMap
    extends TIntFloatHashMap
    implements VHashMap {

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>VIntFloatHashMap</code> instance with the default
   * capacity and load factor.
   */
  public VIntFloatHashMap() {
    super();
  }

  /**
   * Creates a new <code>VIntFloatHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public VIntFloatHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Creates a new <code>VIntFloatHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the specified load factor.
   *
   * @param initialCapacity an <code>int</code> value
   * @param loadFactor a <code>float</code> value
   */
  public VIntFloatHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  //================
  // Public Methods
  //================

  /**
   * retrieves the value for <tt>key</tt>
   *
   * @param key an <code>int</code> value
   * @return the value of <tt>key</tt> or minimum value of float if no such
   * mapping exists.
   */
  public float get(int key) {
    int index = index(key);
    return index < 0 ? (float) SparseDefaultValues.getDefaultDouble() :
        _values[index];
  }

  public VIntFloatHashMap copy() {
    VIntFloatHashMap newMap;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      newMap = (VIntFloatHashMap) ois.readObject();
      ois.close();
      return newMap;
    }
    catch (Exception e) {

      newMap = new VIntFloatHashMap();
      newMap._free = _free;
      newMap._loadFactor = _loadFactor;
      newMap._maxSize = _maxSize;
      newMap._size = _size;

      newMap._set = new int[_set.length];
      System.arraycopy(_set, 0, newMap._set, 0, _set.length);

      newMap._states = new byte[_states.length];
      System.arraycopy(_states, 0, newMap._states, 0, _states.length);

      newMap._values = new float[_values.length];
      System.arraycopy(_values, 0, newMap._values, 0, _values.length);

      return newMap;
    }
  }

  /**
   * Returns a new VIntFloatHashMap with reordered mapping as defined by
   * <code>newOrder</code>
   *
   * @param newOrder  an int to int hashmap that defines the new order:
       *                  for each pair (key, val) in <code>newOrder</code> the value
   *                  that was mapped to val will be mapped to key in the
   *                  returned value.
   * @return          a VIntFloatHashMap with the same values as this one,
   *                  reordered.
   */
  public VHashMap reorder(VIntIntHashMap newOrder) {

    //creating a new map, as it is possible that newOrder does not hold all keys
    //in this map.
    VIntFloatHashMap retVal = new VIntFloatHashMap();

    //for each key in the newOrder map
    int[] newKeys = newOrder.keys();
//    int[] oldKeys = new int[newKeys.length];
    for (int i = 0; i < newKeys.length; i++) {
      //find the old key
      int oldKey = newOrder.get(newKeys[i]);
      //if this old key is a key in this map...
      if(this.containsKey(oldKey)){
        //find its mapped value
        float val = get(oldKey);
        //put this value in the returned map, mapped to the new key
        retVal.put(newKeys[i], val);
    //    oldKeys[i] = oldKey;
      }//if
    }//for i


    //copying old mapping from this map to retval of keys that are
    //not values in newOrder
    int[] thisKeys = keys();
    //for each key in this map
    for(int i=0; i<thisKeys.length; i++){
      //that is not a value in newOrder
      if(!newOrder.containsValue(thisKeys[i])){
        //reserve its old mapping in the returned map
        retVal.put(thisKeys[i], get(thisKeys[i]));
      }
    }


    return retVal;

  }

  /**
   * Returns the values mapped to keys between <codE>begin</code> through
   * <codE>end</cdoe>, sorted.
   *
   * @param begin    key number from which to begin retrieving of values
   * @param end      greatest key number from which to retrieve value.
   * @return         a sorted float array with the values mapped to keys <code>begim
   *                 </code> through <codE>end</cdoe>.
   */
  public float[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      float[] retVal = {};
      return retVal;
    }
    int[] keysInRange = VHashService.getIndicesInRange(begin, end, this);
    if (keysInRange == null)
      return null;

    float[] values = new float[keysInRange.length];
    for (int i = 0; i < keysInRange.length; i++)
      values[i] = get(keysInRange[i]);

    Arrays.sort(values);
    return values;
  }

  /**
   * Returns an int to int hashmap that represent a sorted order for the values
   *  of this map in the range <code>begin</code> through <code>end</code>.
   *
   * @param begin     key no. from which to start retrieving the new order
   * @param end       the last key in the section from which to retrieve the new order.
       * @return            a VIntIntHashMap with valid keys from the specified section
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values.  if (i<=j) then the value that is mapped x
   *                    smaller than or equal to the value that is mapped to y.
   */
  public VIntIntHashMap getSortedOrder(int begin, int end) {

    if (end < begin) {
      return new VIntIntHashMap(0);
    }

    //sorting the valid row numbers
    int[] validKeys = VHashService.getIndicesInRange(begin, end, this);

    //sorting the values
    float[] values = getValuesInRange(begin, end);

    return getSortedOrder(validKeys, values);
  }

  /**
   * Returns an int to int hashmap that represent a sorted order for the values
   *  of this map.
   *
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values in the returned value.
   *                    if (i<=j) then the value that is mapped x
   *                    smaller than or equal to the value that is mapped to y.
   */
  public VIntIntHashMap getSortedOrder() {

    int[] validKeys = VHashService.getIndices(this);

    float[] values = getValues();
    Arrays.sort(values);

    return getSortedOrder(validKeys, values);
  }

  /**
   * returns a subset of this map with values that are mpped to keys <code>
   * start</code> through <codE>start+len</cdoe>.
   *
   * @param start  key number to start retrieving subset from
   * @param len    number of consequetive keys to retrieve their values into
   *               the subset
   * @return       a VIntFloatHashMap with values and keys from this map, s.t.
       *               keys' range is <code>start</cdoe> through <code>start+len</code>
   */
  public VHashMap getSubset(int start, int len) {
    VIntFloatHashMap retVal = new VIntFloatHashMap(len);
    //XIAOLEI
    //int[] validKeys = VHashService.getIndicesInRange(start, start+len, this);
    int[] validKeys = VHashService.getIndicesInRange(start, start + len - 1, this);

    for (int i = 0; i < validKeys.length; i++)

      //XIAOLEI
      //retVal.put(validKeys[i], get(validKeys[i]));
      retVal.put(validKeys[i] - start, get(validKeys[i]));
    return retVal;
  }

  /**
   * Deletes a key/value pair from the map.
   *
   * @param key an <code>int</code> value
   * @return an <code>int</code> value
   */
  public float remove(int key) {
    float prev = (float) SparseDefaultValues.getDefaultDouble();
    int index = index(key);
    if (index >= 0) {
      prev = _values[index];
      removeAt(index); // clear key,state; adjust size
    }
    return prev;
  }

  /**
   * Inserts <codE>obj</codE> to be mapped to key <code>key<code>.
   * All values mapped to keys <code>key</code> and on will be mapped to
   * a key greater in one.
   *
   * @param obj    an object to be inserted into the map.
   * @param key    the insertion key
   */
  public void insertObject(Object obj, int key) {
    //moving all elements mapped to key through the maximal key
    //to be mapped to a key greater in 1.
    int max = VHashService.getMaxKey(this);
    int[] keysInRange = VHashService.getIndicesInRange(key, max, this);
    for (int i = keysInRange.length - 1; i >= 0; i--) {
      float removed = remove(keysInRange[i]);
      put(keysInRange[i] + 1, removed);
    }
    //putting the new object in key.
    if (obj != null)
      put(key, SparseFloatColumn.toFloat(obj));
  }

  public void replaceObject(Object obj, int key) {
    put(key, SparseFloatColumn.toFloat(obj));
  }

  //=================
  // Private Methods
  //=================

  /**
       * Returns an int to int hashmap that represent the sorted order of the values
   * in <code>values</cdoe> through the keys in <code>validKeys</cdoe>.
   *
       * @param validKeys     keys from this map that a sorted order for their values
   *                      should be returned, sorted.
       * @param end           values mapped to items in <code>validKeys</cdoe>, sorted.
   * @return            a VIntIntHashMap with valid keys from <code>validKeys</code>
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values in the returned value.  if (i<=j) then the value
   *                    that is mapped x is smaller than or equal to the value
   *                    that is mapped to y.
   */
  private VIntIntHashMap getSortedOrder(int[] validKeys, float[] values) {

    //will hold the new order to be sorted according to.
    int[] newOrder = new int[validKeys.length];

    //flags associated with newOrder
    boolean[] ocuupiedIndices = new boolean[validKeys.length];

    float currVal; //current value for which its place is searched

    //for each valid row validRows[i]
    for (int i = 0; i < validKeys.length; i++) {

      currVal = get(validKeys[i]);

      //finding the index of its mapped String
      int newKey = Arrays.binarySearch(values, currVal);

      //because binarySearch can return the same index for items that are identical
      //checking for this option too.
      if (ocuupiedIndices[newKey])
        newKey = getNewKey(currVal, values, newKey, ocuupiedIndices);

      ocuupiedIndices[newKey] = true; //marking the flag

      //validRows[i] will be swapped with validRows[newRow] by reorderRows.
      newOrder[newKey] = validKeys[i];

    } //end of for

    //creating a map between the old order and the new order.
    return VHashService.getMappedOrder(validKeys, newOrder);
  }

  /**
   * returns a new index for a new key number for the item <code>currVal</code>
       * the index is the first index i to be found  in <code>values</code> such that
   * <code>currVal equals values[i] and occupiedIndices[i] == false</code>.
   * this index i is then used in the array validKeys by getSortedOrder.
   *
   * @param currVal     the current value that getSortedOrder method is looking
   *                    for its new key number in the map.
   * @param values      values from this map, sorted.
   * @param key         index such that <code>values[key] == currVal</code> and also
   *                    <code>occupiedIndices[row] == true</code>.
   * @param occupiedIndices   a flag array
   * @return            index i such that currVal == values[i] and
   *                    ccupiedIndices[i] == false
   */
  private int getNewKey(float currVal, float[] values, int key,
                        boolean[] ocuupiedIndices) {
    int retVal = -1;
    //searching values at indices smaller than key
    for (int i = key - 1; i >= 0 && values[i] == currVal && retVal < 0; i--)
      if (!ocuupiedIndices[i])
        retVal = i;

        //searching values at indices greater than key
    for (int i = key + 1;
         retVal < 0 && i < values.length && values[i] == currVal; i++)
      if (!ocuupiedIndices[i])
        retVal = i;

    return retVal;
  }

}