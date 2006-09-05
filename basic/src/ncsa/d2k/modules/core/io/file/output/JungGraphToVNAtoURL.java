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
package ncsa.d2k.modules.core.io.file.output;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Outputs a JUNG graph in VNA format. Some assumptions are made: we need to
 * know what each edge's id key is, and each edge's type key, weight key, and id
 * key. Edge weights can be any type that has an appropriate toString() method.
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * JungGraphToVNA</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  mason
 * @version $Revision$, $Date$
 */
public class JungGraphToVNAtoURL {

   //~ Static fields/initializers **********************************************

   /** Symbol DEFAULT_ID_KEY. */
   static private final String DEFAULT_ID_KEY = "id";

   /** DSymbol DEFAULT_EDGE_TYPE_KEY. */
   static private final String DEFAULT_EDGE_TYPE_KEY = "edgeType";

   /** Symbol DEFAULT_EDGE_WEIGHT_KEY. */
   static private final String DEFAULT_EDGE_WEIGHT_KEY = "edgeWeight";

   //~ Instance fields *********************************************************

   /** Holds the key for edge types. */
   private String edgeTypeKey;

   /** Holds the key for edge weights. */
   private String edgeWeightKey;

   /** Holds the key for IDs for nodes. */
   private String idKey;

   //~ Constructors ************************************************************

   /**
    * Creates a new JungGraphToVNAURL object.
    */
   public JungGraphToVNAtoURL() {
      idKey = DEFAULT_ID_KEY;
      edgeTypeKey = DEFAULT_EDGE_TYPE_KEY;
      edgeWeightKey = DEFAULT_EDGE_WEIGHT_KEY;
   }

   //~ Methods *****************************************************************

   /**
    * Escapes the given text in case it contains quotes.
    *
    * @param  text The text to be escaped.
    *
    * @return escapes the given text in case it contains quotes.
    */
   private String escapeText(String text) {
      return text.replaceAll("\"", "\\\"");
   }

   /**
    * Get EdgeTypeKey.
    *
    * @return Then value.
    */
   public String getEdgeTypeKey() { return edgeTypeKey; }

   /**
    * Get EdgeWeightKey.
    *
    * @return The value.
    */
   public String getEdgeWeightKey() { return edgeWeightKey; }

   /**
    * Get IdKey.
    *
    * @return Teh value value.
    */
   public String getIdKey() { return idKey; }

   /**
    * Description of method setEdgeTypeKey.
    *
    * @param edgeTypeKey Description of parameter edgeTypeKey.
    */
   public void setEdgeTypeKey(String edgeTypeKey) {
      this.edgeTypeKey = edgeTypeKey;
   }

   /**
    * Set EdgeWeightKey.
    *
    * @param edgeWeightKey The new value for edgeWeightKey.
    */
   public void setEdgeWeightKey(String edgeWeightKey) {
      this.edgeWeightKey = edgeWeightKey;
   }

   /**
    * Set IdKey.
    *
    * @param idKey The new value for idKey.
    */
   public void setIdKey(String idKey) { this.idKey = idKey; }

   /**
    * Write the graph to the Data Object Proxy.
    *
    * @param  graph The Jung graph.
    * @param  dop   The Data Object Proxy.
    *
    * @throws IOException              Error writeing.
    * @throws DataObjectProxyException Error storing.
    */
   public void write(Graph graph, DataObjectProxy dop)
      throws IOException, DataObjectProxyException {
      File localFile = dop.getLocalFile();
      BufferedWriter writer = new BufferedWriter(new FileWriter(localFile));

      write(graph, writer);
      writer.close();

      // Push the local copy, if needed.
      dop.putFromFile(localFile);
   }

   /**
    * Use the supplied writer to write the graph in VNA format.
    *
    * @param  graph  Description of parameter graph.
    * @param  writer Description of parameter writer.
    *
    * @throws IOException Description of exception IOException.
    */
   public void write(Graph graph, Writer writer) throws IOException {

      // First the node data section.
      writer.write("*Node data\n");

      // Need to collect a complete list of node attributes.
      List attributes = new LinkedList();
      Set vertices = graph.getVertices();

      for (Iterator i = vertices.iterator(); i.hasNext();) {
         Vertex v = (Vertex) i.next();

         for (Iterator j = v.getUserDatumKeyIterator(); j.hasNext();) {
            Object o = j.next();

            if (!(o instanceof String)) {
               continue;
            }

            String attribute = o.toString();

            if (
                !attributes.contains(attribute) &&
                   !getIdKey().equals(attribute)) {
               attributes.add(attribute);
            }
         }
      }

      // Now we have the list, we need to output the column headers
      writer.write("\"" + escapeText(getIdKey()) + "\" ");

      for (Iterator i = attributes.iterator(); i.hasNext();) {
         writer.write("\"" + escapeText(i.next().toString()) + "\" ");
      }

      writer.write("\n");

      // Now we need to output node entries.
      int idCounter = 1;

      for (Iterator i = vertices.iterator(); i.hasNext();) {
         Vertex v = (Vertex) i.next();

         if (v.getUserDatum(getIdKey()) == null) {
            v.addUserDatum(getIdKey(), String.valueOf(idCounter),
                           UserData.CLONE);
            idCounter++;
         }

         writer.write("\"" + escapeText(v.getUserDatum(getIdKey()).toString()) +
                      "\" ");

         for (Iterator j = attributes.iterator(); j.hasNext();) {
            String key = (String) j.next();

            if (v.getUserDatum(key) != null) {
               writer.write("\"" + v.getUserDatum(key) + "\" ");
            } else {
               writer.write("\"\" ");
            }
         }

         writer.write("\n");
      }

      // On to the tie data section!
      writer.write("*Tie data\n");

      // Let's gather all our types of edges first.
      Set edges = graph.getEdges();

      LinkedList edgeTypes = new LinkedList();

      for (Iterator i = edges.iterator(); i.hasNext();) {
         Edge e = (Edge) i.next();

         if (e.getUserDatum(getEdgeTypeKey()) != null) {

            if (!edgeTypes.contains(e.getUserDatum(getEdgeTypeKey()))) {
               edgeTypes.add(e.getUserDatum(getEdgeTypeKey()));
            }
         } else {
            e.setUserDatum(getEdgeTypeKey(), "edge", UserData.CLONE);

            if (!edgeTypes.contains("edge")) {
               edgeTypes.add("edge");
            }
         }

      }

      // Write out the column headers for tie data
      writer.write("from to ");

      for (Iterator i = edgeTypes.iterator(); i.hasNext();) {
         writer.write("\"" + escapeText(i.next().toString()) + "\" ");
      }

      writer.write("\n");

      // And at last - the tie data entries!
      for (Iterator i = edges.iterator(); i.hasNext();) {
         Edge e = (Edge) i.next();
         Vertex from = (Vertex) e.getEndpoints().getFirst();
         Vertex to = (Vertex) e.getEndpoints().getSecond();

         writer.write("\"" +
                      escapeText(from.getUserDatum(getIdKey()).toString()) +
                      "\" ");
         writer.write("\"" +
                      escapeText(to.getUserDatum(getIdKey()).toString()) +
                      "\" ");

         for (Iterator j = edgeTypes.iterator(); j.hasNext();) {
            String type = (String) j.next();

            if (e.getUserDatum(getEdgeWeightKey()) != null) {
               writer.write("\"" +
                            escapeText(e.getUserDatum(getEdgeWeightKey())
                                        .toString()) + "\" ");
            } else {
               writer.write("\"0.0\"");
            }
         }

         writer.write("\n");
      }

      // Fin.  User should close her/his own writer.
   } // end method write
} // end class JungGraphToVNAURL
