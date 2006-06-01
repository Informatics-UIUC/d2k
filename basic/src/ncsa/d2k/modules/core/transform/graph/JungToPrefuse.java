package ncsa.d2k.modules.core.transform.graph;

import ncsa.d2k.core.modules.*;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.algorithms.cluster.*;

import ncsa.d2k.modules.core.vis.pgraph.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;
import ncsa.d2k.core.modules.*;

import prefuse.data.*;
import java.util.*;

public class JungToPrefuse extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
      "edu.uci.ics.jung.graph.Graph"
    };
  }

  public String[] getOutputTypes() {
    return new String[] {
      "prefuse.data.Graph"
    };
  }

  public String getInputInfo(int i) {
    if (i == 0) {
      return "A Jung graph.";
    }
    else {
      return null;
    }
  }

  public String getOutputInfo(int i) {
    if (i == 0) {
      return "A Prefuse graph.";
    }
    else {
      return null;
    }
  }

  public String getInputName(int i) {
    if (i == 0) {
      return "Jung Graph";
    }
    else {
      return null;
    }
  }

  public String getOutputName(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    else {
      return null;
    }
  }

  public String getModuleInfo() {
    String s =  "Overview: Coverts a Jung graph (directed or not) to a Prefuse graph.";

    s += "<p>Acknowledgement: ";
    s += "This module uses functionality from the JUNG project. See http://jung.sourceforge.net.";
    s += "</p>";

      s += "<p>Acknowledgement: ";
      s += "This module uses functionality from the Prefuse project. See http://prefuse.org.";
      s += "</p>";
      
      return s;
  }

  public void doit() throws Exception {
    edu.uci.ics.jung.graph.Graph jung_graph =
        (edu.uci.ics.jung.graph.Graph) pullInput(0);

    boolean directed = PredicateUtils.enforcesDirected(jung_graph);

    prefuse.data.Graph prefuse_graph = new prefuse.data.Graph(directed);

    Set jung_vertices = jung_graph.getVertices();
    HashMap jung_to_prefuse_vertex_map = new HashMap(jung_vertices.size());

    Iterator jung_vertex_iterator = jung_vertices.iterator();
    while(jung_vertex_iterator.hasNext()) {
      Vertex jung_vertex = (Vertex)jung_vertex_iterator.next();

      // create a new prefuse Node for this vertex
      Node prefuse_node = prefuse_graph.addNode();

      jung_to_prefuse_vertex_map.put(jung_vertex, prefuse_node);

      // now set the user data..
      Iterator user_data_iterator = jung_vertex.getUserDatumKeyIterator();
      while(user_data_iterator.hasNext()) {
        String key = (String)user_data_iterator.next();
        Object value = jung_vertex.getUserDatum(key);

        int index = prefuse_node.getColumnIndex(key);
        if(index == -1) {
          Class column_class = Object.class;
          if(value instanceof Integer)
            column_class = int.class;
          else if(value instanceof Double)
            column_class = double.class;
          else if(value instanceof Long)
            column_class = long.class;
          else if(value instanceof String)
            column_class = String.class;
          //else
          //  continue;

          prefuse_node.getTable().addColumn(key, column_class);
          index = prefuse_node.getColumnIndex(key);
        }

        if(value instanceof Integer) {
          prefuse_node.setInt(key, ((Integer)value).intValue());
        }
        else if(value instanceof Double) {
          prefuse_node.setDouble(key, ((Double)value).doubleValue());
        }
        else if(value instanceof Long) {
          prefuse_node.setLong(key, ((Long)value).longValue());
        }
        else if(value instanceof String) {
          prefuse_node.setString(key, (String)value);
        }
        else
          prefuse_node.set(key, value);
      } // user data
    } // jung vertex

    Iterator jung_edge_iterator = jung_graph.getEdges().iterator();
    while(jung_edge_iterator.hasNext()) {
      edu.uci.ics.jung.graph.Edge jung_edge = (edu.uci.ics.jung.graph.Edge)
          jung_edge_iterator.next();

      Vertex v1, v2;

      if (!directed) {
        Pair vertices = jung_edge.getEndpoints();
        v1 = (Vertex)vertices.getFirst();
        v2 = (Vertex)vertices.getSecond();
      }
      else {
        v1 = ((DirectedEdge)jung_edge).getSource();
        v2 = ((DirectedEdge)jung_edge).getDest();
      }

      Node n1 = (Node)jung_to_prefuse_vertex_map.get(v1);
      Node n2 = (Node)jung_to_prefuse_vertex_map.get(v2);

      prefuse.data.Edge prefuse_edge = prefuse_graph.addEdge(n1, n2);
      // now set the user data
      Iterator user_data_iterator = jung_edge.getUserDatumKeyIterator();
      while(user_data_iterator.hasNext()) {
        String key = (String)user_data_iterator.next();
        Object value = jung_edge.getUserDatum(key);

        int index = prefuse_edge.getColumnIndex(key);
        if(index == -1) {
          Class column_class = Object.class;
          if(value instanceof Integer)
            column_class = int.class;
          else if(value instanceof Double)
            column_class = double.class;
          else if(value instanceof Long)
            column_class = long.class;
          else if(value instanceof String)
            column_class = String.class;
          //else
          //  continue;

          prefuse_edge.getTable().addColumn(key, column_class);
          index = prefuse_edge.getColumnIndex(key);
        }

        if(value instanceof Integer) {
          prefuse_edge.setInt(key, ((Integer)value).intValue());
        }
        else if(value instanceof Double) {
          prefuse_edge.setDouble(key, ((Double)value).doubleValue());
        }
        else if(value instanceof Long) {
          prefuse_edge.setLong(key, ((Long)value).longValue());
        }
        else if(value instanceof String) {
          prefuse_edge.setString(key, (String)value);
        }
        else
          prefuse_edge.set(key, value);
      } // user data

    } // edge iterator

    pushOutput(prefuse_graph, 0);

  }
}
