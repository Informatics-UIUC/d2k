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

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * An implementation of the data structure defined by Anderson and Moore.
 *
 * <p>ADTree is an implementation of the data structure defined by B. Anderson
 * and A. Moore in "ADTrees for fast counting and for fast learning of
 * association rules". It stores counts for all possible queries of the type"
 * how many x,y and z are there ?" It is able to answer these queries in
 * constant time. ADTree works only for nominal attributes, cannot handle real
 * numbers.</p>
 *
 * <p><b>Citation:</b></p>
 *
 * <ul>
 *   <li>B. Anderson and A. Moore, "ADTrees for fast counting and for fast
 *     learning of association rules", <i>Proceedings Fourth International
 *     Conference on Knowledge Discovery and Data Mining</i>, 27-31 Aug. 1998 ,
 *     New York, pp. 134-8.</li>
 * </ul>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class ADTree extends Node {

   //~ Instance fields *********************************************************

   /** Controls some debugging. */
   boolean debug1 = false;

   /** Controls some other debugging. */
   boolean debug2 = false;

   /** Unique values indexed from 1 to attribute arity. */
   ArrayList[] indexedUniqueValues;

   /** Labels for each value. */
   String[] labels;

   /** Description of field noOfAttributes. */
   int noOfAttributes;

   /** Unique values for each attribute. */
   TreeSet[] uniqueValues;

   /** Label to index mappings. */
   HashMap values;

   //~ Constructors ************************************************************

   /**
    * Create a new ADTree that will hold counts for n attributes.
    *
    * @param n number of attributes
    */
   public ADTree(int n) {
      noOfAttributes = n;
      labels = new String[noOfAttributes + 1];
      uniqueValues = new TreeSet[noOfAttributes + 1];
      indexedUniqueValues = new ArrayList[noOfAttributes + 1];

      for (int i = 1; i <= noOfAttributes; i++) {
         uniqueValues[i] = new TreeSet();
         indexedUniqueValues[i] = new ArrayList();
      }

      values = new HashMap();
   }

   //~ Methods *****************************************************************

   /**
    * Expands 'node' by adding a VarNode for 'index'
    *
    * @param  index the number of the VarNode to be created
    * @param  node  node to be expanded
    *
    * @return The expanded node.
    */
   private HashMap expandNode(int index, Node node) {

      if (debug2) {
         System.out.println("EXPANDNODE " + index + " " + node);
      }

      HashMap previousVarNode = node.getVarNode(index - 1);

      if (debug2) {
         System.out.println("previousVarNode is " + previousVarNode);
      }

      Iterator values = previousVarNode.values().iterator();

      HashMap hm = new HashMap();
      Node currNode;

      while (values.hasNext()) {
         currNode = (Node) values.next();

         HashMap hmc = currNode.getVarNode(index);

         if (debug2) {
            System.out.println("hmc is " + hmc);
         }

         hm = node.addHashMaps(hm, hmc);
      }

      node.putVarNode(index, hm);

      if (debug1) {
         System.out.println("EXPANDED NODE IS " + node);
      }

      return hm;
   } // end method expandNode

   /**
    * Finds a node starting at a position and node for a given attribute and
    * value. If node does not exist create a new one.
    *
    * @param  position - postion to start the search
    * @param  index    - attribute number
    * @param  nd       - node from where to start searching
    * @param  value    - attribute value
    *
    * @return Node found or created
    */
   private Node findNode(int position, int index, Node nd,
                         Object value) {

      HashMap varNode;

      if (index == position) {
         varNode = nd.getVarNode(position);

         return (Node) varNode.get(value);
      }

      varNode = nd.getVarNode(position);

      Iterator it = varNode.keySet().iterator();
      Node newNode = new Node();

      while (it.hasNext()) {
         Object key = it.next();

         Node node =
            findNode(position + 1, index, (Node) varNode.get(key),
                     value);

         if (node != null) {
            newNode.count = newNode.count + node.count;
            newNode.addNode(node);
         }
      }

      return newNode;
   } // end method findNode

   /**
    * Increments the main ADTree branch counts based on the values read from a
    * line in the file.
    *
    * @param nd     node root of the branch to be incremented
    * @param values attribute values
    */
   public void countLine(Node nd, String[] values) {

      HashMap varNode;
      Node node = nd;

      node.count++;

      if (debug1) {

         System.out.println(" MAIN COUNT : " + node.count + " " + node);
         System.out.println("values are ");

         for (int j = 0; j < values.length; j++) {
            System.out.print(values[j] + " ");
         }
      }

      for (int j = 1; j <= values.length; j++) {

         if (!node.containsKey(new Integer(j))) {
            HashMap hm = new HashMap();
            hm.put(values[j - 1], new Node());
            node.putVarNode(j, hm);

            if (debug2) {
               System.out.println("created and inserted hashmap for  " +
                                  values[j -
                                         1] +
                                  " with j=" +
                                  j);
            }
         }

         varNode = node.getVarNode(j);

         if (varNode.containsKey(values[j - 1])) {
            node = (Node) varNode.get(values[j - 1]);
         } else {
            node = new Node();
            varNode.put(values[j - 1], node);

            if (debug2) {
               System.out.println("hm does not contain key " +
                                  values[j -
                                         1] +
                                  " inserted it j =" +
                                  j);
            }
         }

         ((TreeSet) uniqueValues[j]).add(values[j - 1]);
         node.count++;
      } // end for
   } // end method countLine

   /**
    * Increments the main ADTree branch counts based on the values read from a
    * line in the file.
    *
    * @param nd     node root of the branch to be incremented
    * @param values attribute values
    */
   public void countLine(Node nd, ArrayList values) {

      HashMap varNode;
      Node node = nd;

      node.count++;

      if (debug1) {
         System.out.println(" MAIN COUNT : " + node.count + " " + node);
      }

      int len = values.size();

      for (int j = 1; j <= len; j++) {
         String value = new String((char[]) values.get(j - 1));

         if (!node.containsKey(new Integer(j))) {
            HashMap hm = new HashMap();
            hm.put(value, new Node());
            node.putVarNode(j, hm);

            if (debug2) {
               System.out.println("created and inserted hashmap for  " +
                                  value +
                                  " with j=" +
                                  j);
            }
         }

         varNode = node.getVarNode(j);

         if (varNode.containsKey(value)) {
            node = (Node) varNode.get(value);
         } else {
            node = new Node();
            varNode.put(value, node);

            if (debug2) {
               System.out.println("hm does not contain key " +
                                  value +
                                  " inserted it j =" +
                                  j);
            }
         }

         uniqueValues[j].add(value);
         node.count++;
      } // end for

   } // end method countLine

   /**
    * Expands the ADTree skeleton or main trunk created by 'initializeFromVT' or
    * 'countLine' methods to a full ADTree ( all counts are stored).
    *
    * @param node the node that is going to be expanded. If the node is the root
    *             the entire tree will be expanded
    */
   public void expand(Node node) {

      int lastKey = ((Integer) node.getVarNodes().lastKey()).intValue();

      if (lastKey == noOfAttributes) {
         return;
      }

      HashMap hm = node.getVarNode(lastKey);
      Iterator values = hm.values().iterator();

      while (values.hasNext()) {
         expand((Node) values.next());
      }

      for (int i = lastKey + 1; i <= noOfAttributes; i++) {
         expandNode(i, node);
      }
   }

   /**
    * Expands fully only the first level nodes. Does not expand fully the root
    * node.
    *
    * @param node the node where the expansion starts
    */
   public void expandFirstOnly(Node node) {

      int lastKey = ((Integer) node.getVarNodes().lastKey()).intValue();
      System.out.println("FIRST ONLY LASTKEY IS " + lastKey);

      HashMap hm = node.getVarNode(lastKey);
      Iterator values = hm.values().iterator();

      while (values.hasNext()) {
         expand((Node) values.next());
      }

   }

   /**
    * Returns the number of attributes.
    *
    * @return number of attributes
    */
   public int getAttributesNum() { return noOfAttributes; }

   /**
    * getCount implements an OR like expresion and returns the sums of counts of
    * all queries.
    *
    * @param  nd    - root of the ADTree
    * @param  pairs - contains a list of HashMaps where each HashMap contains
    *               (attributeName ,value) mappings
    *
    * @return Description of return value.
    */
   public int getCount(Node nd, ArrayList pairs) {

      int ors = pairs.size();
      HashMap varNode;
      Node node;
      int count = 0;

      for (int i = 0; i < ors; i++) {

         node = nd;

         HashMap hm = (HashMap) pairs.get(i);
         TreeMap attrValues = new TreeMap();
         Iterator it = hm.keySet().iterator();
         String stringKey;

         while (it.hasNext()) {
            stringKey = (String) it.next();

            Integer key = new Integer(getIndexForLabel(stringKey));
            attrValues.put(key, hm.get(stringKey));
         }

         int tmp = getCount(node, attrValues);

         if (tmp == -1) {
            return -1;
         }

         count = count + tmp;

      }

      return count;
   } // end method getCount

   /**
    * getCount implements an AND like expresion and returns the sums of counts
    * of all queries.
    *
    * @param  nd     - root of the ADTree
    * @param  values - map of attribute, value pairs
    *
    * @return - the no of the records that have attribute[i] = value for every i
    *         NOTE : attribute indexes should be in ascending order
    */
   public int getCount(Node nd, TreeMap values) {

      HashMap varNode;
      Node node = nd;
      Iterator it = values.keySet().iterator();
      int index;
      Object key;

      while (it.hasNext()) {
         key = it.next();
         index = ((Integer) key).intValue();
         varNode = node.getVarNode(index);

         if (varNode == null) {
            System.err.println("ADTree is not expanded: Trying to " +
                               "expand");

            if (debug1) {
               System.out.println("node to expand " + node);
            }

            expand(node);

            varNode = node.getVarNode(index);
         }

         if (varNode.containsKey(values.get(key))) {
            node = (Node) varNode.get(values.get(key));
         } else {
            return 0;
         }
      }

      return node.getCount();

   } // end method getCount

   /**
    * getCount implements an AND like expresion and returns the sums of counts
    * of all queries.
    *
    * @param  nd        - root of the ADTree
    * @param  values    - array of attribute values
    * @param  attribute - array of attribute indexes
    *
    * @return The count.
    */
   public int getCount(Node nd, String[] values, int[] attribute) {

      int len = attribute.length;
      HashMap varNode;
      Node node = nd;

      Integer firstKey = (Integer) varNodes.firstKey();

      if (firstKey.intValue() > attribute[0]) {
         System.err.println("cannot get the desired count from this tree");
      }

      for (int i = 0; i < len; i++) {
         System.out.println("attribute[i] " + attribute[i]);
         System.out.println("values[i] " + values[i]);
         varNode = node.getVarNode(attribute[i]);

         if (varNode == null) {
            System.err.println("ADTree is not expanded: Trying to expand");
            expand(node);
            varNode = node.getVarNode(attribute[i]);
         }

         if (varNode.containsKey(values[i])) {
            node = (Node) varNode.get(values[i]);
         } else {
            return 0;
         }
      }

      return node.getCount();
   } // end method getCount

   /**
    * As the name implies.
    *
    * @return The value.
    */
   public boolean getDebug() { return debug2; }


   /**
    * Description of method getDebug1.
    *
    * @return Description of return value.
    */
   public boolean getDebug1() { return debug1; }

   /**
    * getDirectCount implements an OR like expresion and returns the sums of
    * counts of all queries. It uses getDirectCount
    *
    * @param  nd    - root of the ADTree
    * @param  pairs - contains a list of HashMaps where each HashMap contains
    *               (attributeName ,value) mappings
    *
    * @return sum of the counts.
    */
   public int getDirectCount(Node nd, ArrayList pairs) {

      int ors = pairs.size();
      HashMap varNode;
      Node node;
      int count = 0;

      for (int i = 0; i < ors; i++) {
         node = nd;

         HashMap hm = (HashMap) pairs.get(i);
         TreeMap attrValues = new TreeMap();
         Iterator it = hm.keySet().iterator();
         String stringKey;

         while (it.hasNext()) {
            stringKey = (String) it.next();

            Integer key = new Integer(getIndexForLabel(stringKey));
            attrValues.put(key, hm.get(stringKey));
         }

         int tmp = getDirectCount(1, node, attrValues);

         if (tmp == -1) {
            return -1;
         }

         count = count + tmp;
      }

      return count;
   } // end method getDirectCount

   /**
    * getDirectCount - gets a count by using only the ADTree skeleton, expanding
    * the needed branches and then discarding them.
    *
    * @param  pos    -position where the search for the node containing the
    *                count will begin
    * @param  nd     - root of the ADTree
    * @param  values - map of attribute, value pairs
    *
    * @return count.
    */
   public int getDirectCount(int pos, Node nd, TreeMap values) {

      HashMap varNode;
      Node node = nd;
      Node newNode = new Node();

      if (values.size() == 0) {
         return node.getCount();
      }

      Object key = values.firstKey();
      int index = ((Integer) key).intValue();
      Object value = values.get(key);

      Node sumed = findNode(pos, index, node, value);

      if (sumed == null) { // there was no node with the desired value
         sumed = new Node(); // create an empty node for sumed
      }

      newNode.count = newNode.count + sumed.count;
      newNode.addNode(sumed);

      System.gc();

      SortedMap mp = values.tailMap(new Integer(index + 1));
      TreeMap next = new TreeMap(mp);

      return getDirectCount(index + 1, newNode, next);

   } // end method getDirectCount

   /**
    * Returns the label corresponding to an index.
    *
    * @param  label value of attribute label
    *
    * @return position of a particular attribute label
    */
   public int getIndexForLabel(String label) {
      Integer v = (Integer) values.get((Object) label);

      if (v != null) {
         return v.intValue();
      } else {
         return -1;
      }
   }

   /**
    * Get the label for a particular attribute.
    *
    * @param  index attribute number
    *
    * @return get the label for a particular attribute.
    */
   public String getLabel(int index) { return labels[index]; }

   /**
    * getUniqueValues - returns all possible values that the attribute
    * identified by index can take.
    *
    * @param  index attribute position in the list of attributes
    *
    * @return Description of return value.
    */
   public String[] getUniqueValues(int index) {


      TreeSet uniques = (TreeSet) uniqueValues[index];


      String[] result;
      MutableTableImpl tbl = new MutableTableImpl();
      String missingStringValue = tbl.getMissingString();

      // find if there are missing values and return only unique
      // non missing
      // values
      uniques.remove(missingStringValue);

      Iterator itnew = uniques.iterator();
      int i = 0;
      result = new String[uniques.size()];

      while (itnew.hasNext()) {
         String item = (String) itnew.next();
         result[i++] = item;
      }

      return result;

   } // end method getUniqueValues

   /**
    * Get the unique values found in the tree.
    *
    * @param  index Description of parameter index.
    *
    * @return Set of non-missing unique values.
    */
   public TreeSet getUniqueValuesTreeSet(int index) {

      // find if there are missing values and return only unique non
      // missing
      // values
      MutableTableImpl tbl = new MutableTableImpl();
      String missingStringValue = tbl.getMissingString();
      TreeSet uniques = (TreeSet) uniqueValues[index];
      uniques.remove(missingStringValue);

      return uniques;
   }

   /**
    * This method builds the main trunk of the ADTree from a Table. Used mainly
    * for testing.
    *
    * @param vt table containig data
    */
   public void initializeFromVT(Table vt) {

      HashMap varNode;
      Node node;

      for (int i = 0; i < vt.getNumRows(); i++) {
         node = this;
         node.count++;

         if (debug1) {
            System.out.println(" MAIN COUNT : " + node.count + " " +
                               this);
         }

         for (int j = 1; j <= noOfAttributes; j++) {

            if (!node.containsKey(new Integer(j))) {
               HashMap hm = new HashMap();
               hm.put(vt.getString(i, j - 1), new Node());
               node.putVarNode(j, hm);

               if (debug2) {
                  System.out.println("created and inserted hashmap for" +
                                     "  " + vt.getString(i, j - 1) +
                                     " with j=" + j);
               }
            }

            varNode = node.getVarNode(j);

            if (varNode.containsKey(vt.getString(i, j - 1))) {
               node = (Node) varNode.get(vt.getString(i, j - 1));
            } else {
               node = new Node();
               varNode.put(vt.getString(i, j - 1), node);

               if (debug2) {
                  System.out.println("hm does not contain key " +
                                     vt.getString(i, j - 1) +
                                     " inserted it j =" +
                                     j);
               }
            }

            node.count++;
         } // end for
      } // end for

   } // end method initializeFromVT

   /**
    * Called for each line of the file, identifies all the unique values
    * of the attributes, and stores them in a TreeMap and an indexed ArrayList.
    *
    * @param nd     root node
    * @param values list of values to add.
    */
   public void scanUniqueValues(Node nd, String[] values) {

      Node node = nd;
      node.count++;

      for (int j = 1; j <= values.length; j++) {

         if (((TreeSet) uniqueValues[j]).add(values[j - 1])) {
            indexedUniqueValues[j].add(values[j - 1]);
         }
      }
   }

   /**
    * As the name implies.
    *
    * @param b The value.
    */
   public void setDebug1(boolean b) { debug1 = b; }

   /**
    * As the name implies.
    *
    * @param b The value.
    */
   public void setDebug2(boolean b) { debug2 = b; }

   /**
    * Sets the labels for specific attributes.
    *
    * @param index attribute number that has this label
    * @param value attribute label
    */
   public void setLabel(int index, String value) {

      if (debug1) {
         System.out.println("set labels " + index + " " + value);
      }

      labels[index] = value;
      values.put((Object) value, (Object) (new Integer(index)));
   }

} // end class ADTree
