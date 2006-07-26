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

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;


/**
 * Node is an implementation of an ADTree node. It stores a count and a list of
 * varNodes.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 * @see     ncsa.d2k.modules.core.datatype.ADTree
 */

public class Node {

   //~ Instance fields *********************************************************

   /**
    * key - attribute index value - HashMap ( key attribute value, value Node).
    */
   public int count;

   public TreeMap varNodes;

   //~ Constructors ************************************************************

   /**
    * Create a new emtpy Node.
    */

   public Node() {
      count = 0;
      varNodes = null;
   }

   /**
    * Create a new Node with the specified count and list of varNodes.
    *
    * @param cnt    the initial count
    * @param vNodes list of varNodes
    */
   public Node(int cnt, TreeMap vNodes) {
      count = cnt;

      if (vNodes != null) {
         varNodes = new TreeMap(vNodes);
      }
   }

   //~ Methods *****************************************************************

   /**
    * add second HashMap to first and returns first.
    *
    * @param  first  HashMap to be summed
    * @param  second HashMap to be summed
    *
    * @return HashMap containing the sum of all nodes contained in the maps.
    */
   public HashMap addHashMaps(HashMap first, HashMap second) {

      if (first == null) {
         return second;
      }

      if (second == null) {
         return first;
      }

      Iterator keys = second.keySet().iterator();
      Node nd;

      while (keys.hasNext()) {
         String key = (String) keys.next();

         if (first.containsKey(key)) {
            nd = (Node) first.get(key);

            nd.count = nd.count + ((Node) second.get(key)).count;
            nd.addNode((Node) second.get(key));

         } else {

            nd =
               new Node(((Node) second.get(key)).count,
                        ((Node) second.get(key)).getVarNodes());

            first.put(key, nd);
         }
      }

      return first;
   } // end method addHashMaps

   /**
    * add node nd to this node - add all branches too NOTE: add the counts of
    * the node before or after calling this function which does not add these
    * counts.
    *
    * @param nd node to be added to this node
    */
   public void addNode(Node nd) {
      TreeMap vNodes = nd.getVarNodes();

      if (vNodes != null) {
         Iterator keys = vNodes.keySet().iterator();
         Integer key;

         while (keys.hasNext()) {
            key = ((Integer) keys.next());

            HashMap hm = new HashMap();

            if (varNodes == null) {
               varNodes = new TreeMap();
            }

            hm = addHashMaps(hm, (HashMap) vNodes.get(key));
            hm = addHashMaps(hm, (HashMap) varNodes.get(key));
            varNodes.put(key, hm);
         }
      }
   } // end method addNode

   public boolean containsKey(Object key) {

      if (varNodes == null) {
         return false;
      } else {
         return varNodes.containsKey(key);
      }
   }
   
   public int getCount() { return count; }

   public HashMap getVarNode(int index) {

      if (varNodes == null) {
         return null;
      } else {
         return (HashMap) varNodes.get(new Integer(index));
      }
   }

   public TreeMap getVarNodes() { return varNodes; }

   public void putVarNode(int index, HashMap node) {

      if (varNodes == null) {
         varNodes = new TreeMap();
      }

      varNodes.put(new Integer(index), node);
   }

   public String toString() {
      String result = "";

      if (count != 0) {
         result = "node count " + count + " ";
      }

      if (varNodes == null) {
         return result;
      }

      Iterator keys = varNodes.keySet().iterator();
      Integer key;
      String indent = "";

      while (keys.hasNext()) {
         key = (Integer) keys.next();

         for (int i = 0; i < key.intValue(); i++) {
            indent = indent + " ";
         }

         result =
            result + "varN " + key + " has HM with keys \n" + indent +
            ((HashMap) varNodes.get(key));

         indent = "";

         for (int i = 0; i < key.intValue() - 2; i++) {
            indent = indent + " ";
         }

         result = result + "\n" + indent; // + "- ";
      }

      return result;
   } // end method toString

} // end class Node
