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
package ncsa.d2k.modules.core.transform.graph;

import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.PredicateUtils;

import prefuse.data.Node;

import ncsa.d2k.core.modules.ComputeModule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * <p>Overview: Converts a JUNG graph (directed or not) to a Prefuse graph.</p>
 *
 * <p>Acknowledgement: This module uses functionality from the JUNG project. See
 * http://jung.sourceforge.net.</p>
 *
 * <p>Acknowledgement: This module uses functionality from the Prefuse project.
 * See http://prefuse.org.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class JungToPrefuse extends ComputeModule {

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      edu.uci.ics.jung.graph.Graph jung_graph =
         (edu.uci.ics.jung.graph.Graph) pullInput(0);

      boolean directed = PredicateUtils.enforcesDirected(jung_graph);

      prefuse.data.Graph prefuse_graph = new prefuse.data.Graph(directed);

      Set jung_vertices = jung_graph.getVertices();
      HashMap jung_to_prefuse_vertex_map = new HashMap(jung_vertices.size());

      Iterator jung_vertex_iterator = jung_vertices.iterator();

      while (jung_vertex_iterator.hasNext()) {
         Vertex jung_vertex = (Vertex) jung_vertex_iterator.next();

         // create a new prefuse Node for this vertex
         Node prefuse_node = prefuse_graph.addNode();

         jung_to_prefuse_vertex_map.put(jung_vertex, prefuse_node);

         // now set the user data..
         Iterator user_data_iterator = jung_vertex.getUserDatumKeyIterator();

         while (user_data_iterator.hasNext()) {
            String key = (String) user_data_iterator.next();
            Object value = jung_vertex.getUserDatum(key);

            int index = prefuse_node.getColumnIndex(key);

            if (index == -1) {
               Class column_class = Object.class;

               if (value instanceof Integer) {
                  column_class = int.class;
               } else if (value instanceof Double) {
                  column_class = double.class;
               } else if (value instanceof Long) {
                  column_class = long.class;
               } else if (value instanceof String) {
                  column_class = String.class;
                  // else
                  // continue;
               }

               prefuse_node.getTable().addColumn(key, column_class);
               index = prefuse_node.getColumnIndex(key);
            }

            if (value instanceof Integer) {
               prefuse_node.setInt(key, ((Integer) value).intValue());
            } else if (value instanceof Double) {
               prefuse_node.setDouble(key, ((Double) value).doubleValue());
            } else if (value instanceof Long) {
               prefuse_node.setLong(key, ((Long) value).longValue());
            } else if (value instanceof String) {
               prefuse_node.setString(key, (String) value);
            } else {
               prefuse_node.set(key, value);
            }
         } // user data
      } // jung vertex

      Iterator jung_edge_iterator = jung_graph.getEdges().iterator();

      while (jung_edge_iterator.hasNext()) {
         edu.uci.ics.jung.graph.Edge jung_edge =
            (edu.uci.ics.jung.graph.Edge) jung_edge_iterator.next();

         Vertex v1;
         Vertex v2;

         if (!directed) {
            Pair vertices = jung_edge.getEndpoints();
            v1 = (Vertex) vertices.getFirst();
            v2 = (Vertex) vertices.getSecond();
         } else {
            v1 = ((DirectedEdge) jung_edge).getSource();
            v2 = ((DirectedEdge) jung_edge).getDest();
         }

         Node n1 = (Node) jung_to_prefuse_vertex_map.get(v1);
         Node n2 = (Node) jung_to_prefuse_vertex_map.get(v2);

         prefuse.data.Edge prefuse_edge = prefuse_graph.addEdge(n1, n2);

         // now set the user data
         Iterator user_data_iterator = jung_edge.getUserDatumKeyIterator();

         while (user_data_iterator.hasNext()) {
            String key = (String) user_data_iterator.next();
            Object value = jung_edge.getUserDatum(key);

            int index = prefuse_edge.getColumnIndex(key);

            if (index == -1) {
               Class column_class = Object.class;

               if (value instanceof Integer) {
                  column_class = int.class;
               } else if (value instanceof Double) {
                  column_class = double.class;
               } else if (value instanceof Long) {
                  column_class = long.class;
               } else if (value instanceof String) {
                  column_class = String.class;
                  // else
                  // continue;
               }

               prefuse_edge.getTable().addColumn(key, column_class);
               index = prefuse_edge.getColumnIndex(key);
            }

            if (value instanceof Integer) {
               prefuse_edge.setInt(key, ((Integer) value).intValue());
            } else if (value instanceof Double) {
               prefuse_edge.setDouble(key, ((Double) value).doubleValue());
            } else if (value instanceof Long) {
               prefuse_edge.setLong(key, ((Long) value).longValue());
            } else if (value instanceof String) {
               prefuse_edge.setString(key, (String) value);
            } else {
               prefuse_edge.set(key, value);
            }
         } // user data

      } // edge iterator

      pushOutput(prefuse_graph, 0);

   } // end method doit

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      if (i == 0) {
         return "A JUNG graph.";
      } else {
         return null;
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      if (i == 0) {
         return "JUNG Graph";
      } else {
         return null;
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return new String[] {
                                               "edu.uci.ics.jung.graph.Graph"
                                            }; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s =
         "<p>Overview: Converts a JUNG graph (directed or not) to a Prefuse graph.</p>";

      s += "<p>Acknowledgement: ";
      s +=
         "This module uses functionality from the JUNG project. See http://jung.sourceforge.net.";
      s += "</p>";

      s += "<p>Acknowledgement: ";
      s +=
         "This module uses functionality from the Prefuse project. See http://prefuse.org.";
      s += "</p>";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "JUNG to Prefuse"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      if (i == 0) {
         return "A Prefuse graph.";
      } else {
         return null;
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {

      if (i == 0) {
         return "Prefuse Graph";
      } else {
         return null;
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return new String[] {
                                                "prefuse.data.Graph"
                                             }; }
} // end class JungToPrefuse
