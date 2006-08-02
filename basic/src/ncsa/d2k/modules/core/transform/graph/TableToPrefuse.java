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

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.graph.GraphUtils;
import ncsa.d2k.modules.core.datatype.table.Table;

import java.util.HashMap;
import java.util.Iterator;
// import prefuse.data.*;


/**
 * Description of class TableToPrefuse.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class TableToPrefuse extends DataPrepModule {

   //~ Instance fields *********************************************************

   //////////////////////////////////////////////////////////////////////////////
   // D2K module properties
   //////////////////////////////////////////////////////////////////////////////

   /** Attribute (column) index in the Table specifying the edge labels. */
   private int edgeAttribute = 2;

   /** Whether to make the output Graph a directed graph. */
   private boolean makeDirected = false;

   /**
    * Whether to populate the vertices of the output Graph with partition
    * information (i.e., add the PARTITION column to its node table).
    */
   private boolean makePartitions = false;

   /**
    * Attribute (column) index in the Table specifying the source vertex labels.
    */
   private int sourceAttribute = 0;

   /**
    * Attribute (column) index in the Table specifying the target vertex labels.
    */
   private int targetAttribute = 1;

   //~ Methods *****************************************************************

   /**
    * Get the edge that connects src to target node.
    *
    * @param  src  source node
    * @param  targ the row number that represents target node
    *
    * @return edge that connects the two
    */
   private prefuse.data.Edge getEdge(prefuse.data.Node src, int targ) {
      Iterator edges = src.edges();

      while (edges.hasNext()) {
         prefuse.data.Edge edge = (prefuse.data.Edge) edges.next();

         prefuse.data.Node adj = edge.getAdjacentNode(src);

         if (adj.getRow() == targ) {
            return edge;
         }
      }

      return null;
   }

   /**
    * Return true if targ is a neighbor of src.
    *
    * @param  src  source node
    * @param  targ row number that represents target node
    *
    * @return true if targ is a neighbor of src
    */
   private boolean isNeighbor(prefuse.data.Node src, int targ) {
      Iterator neighbors = src.neighbors();

      while (neighbors.hasNext()) {
         prefuse.data.Node nde = (prefuse.data.Node) neighbors.next();

         if (nde.getRow() == targ) {
            return true;
         }
      }

      return false;
   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {

      Table table = (Table) pullInput(0);

      prefuse.data.Graph graph = new prefuse.data.Graph(makeDirected);

      prefuse.data.Table tbl = graph.getNodeTable();
      tbl.addColumn(GraphUtils.LABEL, String.class);
      tbl.addColumn(GraphUtils.WEIGHT, int.class);

      if (makePartitions) {
         tbl.addColumn(GraphUtils.PARTITION, int.class);
      }

      tbl = graph.getEdgeTable();
      tbl.addColumn(GraphUtils.LABEL, String.class);
      tbl.addColumn(GraphUtils.WEIGHT, int.class);

      HashMap nodeMap = new HashMap();

      String label;
      prefuse.data.Node source;
      prefuse.data.Node target;
      prefuse.data.Edge edge;

      int numRows = table.getNumRows();

      for (int row = 0; row < numRows; row++) {

         // source

         label = table.getString(row, sourceAttribute);

         if ((source = (prefuse.data.Node) nodeMap.get(label)) == null) {

            source = graph.addNode();

            // source.setAttribute(BINARY, Integer.toString(0));
            // source.setAttribute(LABEL, label);
            // source.setAttribute(WEIGHT, Integer.toString(1));
            if (makePartitions) {
               source.setInt(GraphUtils.PARTITION, 0);
            }

            source.setString(GraphUtils.LABEL, label);
            source.setInt(GraphUtils.WEIGHT, 1);

            nodeMap.put(label, source);
            // graph.addNode(source);
         } else {
            int weight = source.getInt(GraphUtils.WEIGHT);

            // source.setAttribute(WEIGHT, Integer.toString(weight + 1));
            source.setInt(GraphUtils.WEIGHT, weight + 1);
         }

         // target

         label = table.getString(row, targetAttribute);

         if ((target = (prefuse.data.Node) nodeMap.get(label)) == null) {

            target = graph.addNode();

            if (makePartitions) {
               target.setInt(GraphUtils.PARTITION, 1);
            }

            target.setString(GraphUtils.LABEL, label);
            target.setInt(GraphUtils.WEIGHT, 1);

            nodeMap.put(label, target);
            // graph.addNode(target);
         } else {
            int weight = target.getInt(GraphUtils.WEIGHT);
            target.setInt(GraphUtils.WEIGHT, weight + 1);
         }

         // edge

         if (isNeighbor(source, target.getRow())) {
            edge = getEdge(source, target.getRow());

            int weight = edge.getInt(GraphUtils.WEIGHT);
            edge.setInt(GraphUtils.WEIGHT, weight + 1);
         } else {
            edge = graph.addEdge(source, target);

            if (edgeAttribute > 0) {
               edge.setString(GraphUtils.LABEL,
                              table.getString(row, edgeAttribute));
            }

            edge.setInt(GraphUtils.WEIGHT, 1);
            // graph.addEdge(edge);
         }

      } // end for

      pushOutput(graph, 0);
   } // end method doit

   /**
    * Get edgeAttribute
    *
    * @return edgeAttribute
    */
   public int getEdgeAttribute() { return edgeAttribute; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      if (index == 0) {
         return "A D2K <i>Table</i>.";
      } else {
         return null;
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      if (index == 0) {
         return "Table";
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
                                               "ncsa.d2k.modules.core.datatype.table.Table"
                                            }; }

   /**
    * Get makeDirected
    *
    * @return makeDirected
    */
   public boolean getMakeDirected() { return makeDirected; }

   /**
    * Get makePartitions
    *
    * @return makePartitions
    */
   public boolean getMakePartitions() { return makePartitions; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() { // !:

      String s =
         "<p>Overview: Convert a Table into a prefuse graph structure.</p>";

      s += "<p>Acknowledgement: ";
      s +=
         "This module uses functionality from the Prefuse project. See http://prefuse.org.";
      s += "</p>";

      return s;
   }

   //////////////////////////////////////////////////////////////////////////////
   // D2K module boilerplate
   //////////////////////////////////////////////////////////////////////////////

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Table to Prefuse"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      if (index == 0) {
         return "A <i>Prefuse</i> graph marked with label and weight (and " +
                "optionally partition) attributes.";
      } else {
         return null;
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      if (index == 0) {
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

                                                // "edu.berkeley.guir.prefuse.graph.Graph"
                                                "prefuse.data.Graph"
                                             }; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {

      return new PropertyDescription[] {

      new PropertyDescription("sourceAttribute", "Source Attribute Index",
                              "Attribute (column) index in the Table specifying the source " +
                              "vertex labels."),

      new PropertyDescription("targetAttribute", "Target Attribute Index",
                              "Attribute (column) index in the Table specifying the target " +
                              "vertex labels."),

      new PropertyDescription("edgeAttribute", "Edge Attribute Index",
                              "Attribute (column) index in the Table specifying the edge " +
                              "labels."),

      new PropertyDescription("makeDirected", "Make Graph Directed",
                              "Whether to make the output Graph a directed graph."),

      new PropertyDescription("makePartitions", "Make Partitions",
                              "Whether to populate the vertices of the output Graph with " +
                              "partition information (i.e., add the PARTITION column to " +
                              "its node table).")
             };

   }

   /**
    * Get sourceAttribute
    *
    * @return sourceAttribute
    */
   public int getSourceAttribute() { return sourceAttribute; }

   /**
    * Get targetAttribute.
    *
    * @return targetAttribute
    */
   public int getTargetAttribute() { return targetAttribute; }

   /**
    * Set edgeAttribute
    *
    * @param value edgeAttribute
    */
   public void setEdgeAttribute(int value) { edgeAttribute = value; }

   /**
    * Set makeDirected
    *
    * @param value makeDirected
    */
   public void setMakeDirected(boolean value) { makeDirected = value; }

   /**
    * Set makePartitions
    *
    * @param value makePartitions
    */
   public void setMakePartitions(boolean value) { makePartitions = value; }

   /**
    * Set sourceAttribute
    *
    * @param value sourceAttribute
    */
   public void setSourceAttribute(int value) { sourceAttribute = value; }

   /**
    * Set targetAttribute
    *
    * @param value targetAttribute
    */
   public void setTargetAttribute(int value) { targetAttribute = value; }

} // end class TableToPrefuse
