package ncsa.d2k.modules.core.transform.graph;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.graph.*;
import ncsa.d2k.modules.core.datatype.table.*;
// import prefuse.data.*;

public class TableToPrefuse extends DataPrepModule {

  public void doit() {

    Table table = (Table)pullInput(0);

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
    prefuse.data.Node source, target;
    prefuse.data.Edge edge;

    int numRows = table.getNumRows();
    for (int row = 0; row < numRows; row++) {

      // source

      label = table.getString(row, sourceAttribute);

      if ((source = (prefuse.data.Node)nodeMap.get(label)) == null) {

        source = graph.addNode();
        //source.setAttribute(BINARY, Integer.toString(0));
        //source.setAttribute(LABEL, label);
        //source.setAttribute(WEIGHT, Integer.toString(1));
        if (makePartitions) {
          source.setInt(GraphUtils.PARTITION, 0);
        }
        source.setString(GraphUtils.LABEL, label);
        source.setInt(GraphUtils.WEIGHT, 1);

        nodeMap.put(label, source);
        //graph.addNode(source);
      }
      else {
        int weight = source.getInt(GraphUtils.WEIGHT);
        //source.setAttribute(WEIGHT, Integer.toString(weight + 1));
        source.setInt(GraphUtils.WEIGHT, weight+1);
      }

      // target

      label = table.getString(row, targetAttribute);

      if ((target = (prefuse.data.Node)nodeMap.get(label)) == null) {

        target = graph.addNode();
        if (makePartitions) {
          target.setInt(GraphUtils.PARTITION, 1);
        }
        target.setString(GraphUtils.LABEL, label);
        target.setInt(GraphUtils.WEIGHT, 1);

        nodeMap.put(label, target);
        //graph.addNode(target);
      }
      else {
        int weight = target.getInt(GraphUtils.WEIGHT);
        target.setInt(GraphUtils.WEIGHT, weight + 1);
      }

      // edge

      if (isNeighbor(source, target.getRow())) {
        edge = getEdge(source, target.getRow());
        int weight = edge.getInt(GraphUtils.WEIGHT);
        edge.setInt(GraphUtils.WEIGHT, weight + 1);
      }
      else {
        edge = graph.addEdge(source, target);
        if (edgeAttribute > 0) {
          edge.setString(GraphUtils.LABEL, table.getString(row, edgeAttribute));
        }
        edge.setInt(GraphUtils.WEIGHT, 1);
        //graph.addEdge(edge);
      }

    }

    pushOutput(graph, 0);
  }

  boolean isNeighbor(prefuse.data.Node src, int targ) {
    Iterator neighbors = src.neighbors();
    while(neighbors.hasNext()) {
      prefuse.data.Node nde = (prefuse.data.Node)neighbors.next();
      if(nde.getRow() == targ) {
        return true;
      }
    }
    return false;
  }

  prefuse.data.Edge getEdge(prefuse.data.Node src, int targ) {
    Iterator edges = src.edges();
    while(edges.hasNext()) {
      prefuse.data.Edge edge = (prefuse.data.Edge)edges.next();

      prefuse.data.Node adj = edge.getAdjacentNode(src);
      if(adj.getRow() == targ)
        return edge;
    }
    return null;
  }

  //////////////////////////////////////////////////////////////////////////////
  // D2K module properties
  //////////////////////////////////////////////////////////////////////////////

  private int edgeAttribute = 2;

  public int getEdgeAttribute() {
    return edgeAttribute;
  }

  public void setEdgeAttribute(int value) {
    edgeAttribute = value;
  }

  private boolean makeDirected = false;

  public boolean getMakeDirected() {
    return makeDirected;
  }

  public void setMakeDirected(boolean value) {
    makeDirected = value;
  }

  private boolean makePartitions = false;

  public boolean getMakePartitions() {
    return makePartitions;
  }

  public void setMakePartitions(boolean value) {
    makePartitions = value;
  }

  private int sourceAttribute = 0;

  public int getSourceAttribute() {
    return sourceAttribute;
  }

  public void setSourceAttribute(int value) {
    sourceAttribute = value;
  }

  private int targetAttribute = 1;

  public int getTargetAttribute() {
    return targetAttribute;
  }

  public void setTargetAttribute(int value) {
    targetAttribute = value;
  }

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

  //////////////////////////////////////////////////////////////////////////////
  // D2K module boilerplate
  //////////////////////////////////////////////////////////////////////////////

  public String getModuleName() {
	  return "Table to Prefuse";
  }
  public String getInputInfo(int index) {
    if (index == 0) {
      return "A D2K <i>Table</i>.";
    }
    else {
      return null;
    }
  }

  public String getInputName(int index) {
    if (index == 0) {
      return "Table";
    }
    else {
      return null;
    }
  }

  public String[] getInputTypes() {
    return new String[] {
      "ncsa.d2k.modules.core.datatype.table.Table"
    };
  }

  public String getModuleInfo() { // !:
      String s = "<p>Overview: Convert a Table into a prefuse graph structure.</p>";

      s += "<p>Acknowledgement: ";
      s += "This module uses functionality from the Prefuse project. See http://prefuse.org.";
      s += "</p>";

      return s;
  }

  public String getOutputInfo(int index) {
    if (index == 0) {
      return "A <i>Prefuse</i> graph marked with label and weight (and " +
          "optionally partition) attributes.";
    }
    else {
      return null;
    }
  }

  public String getOutputName(int index) {
    if (index == 0) {
      return "Prefuse Graph";
    }
    else {
      return null;
    }
  }

  public String[] getOutputTypes() {
    return new String[] {
      //"edu.berkeley.guir.prefuse.graph.Graph"
      "prefuse.data.Graph"
    };
  }

}
