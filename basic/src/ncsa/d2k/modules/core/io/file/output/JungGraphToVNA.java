//package ncsa.sonic.ciknow.io;
package ncsa.d2k.modules.core.io.file.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

/**
 * Outputs a JUNG graph in VNA format.  Some assumptions are made: we need to know
 * what each edge's id key is, and each edge's type key, weight key, and id key.  Edge
 * weights can be any type that has an appropriate toString() method.
 * 
 * @author mason
 *
 */
public class JungGraphToVNA {
	private static final String DEFAULT_ID_KEY = "id";
	private static final String DEFAULT_EDGE_TYPE_KEY = "edgeType";
	private static final String DEFAULT_EDGE_WEIGHT_KEY = "edgeWeight";
	
	// Holds the key for IDs for nodes.
	private String idKey;
	
	// Holds the key for edge types.
	private String edgeTypeKey;
	
	// Holds the key for edge weights.
	private String edgeWeightKey;
	
	public JungGraphToVNA() {
		idKey = DEFAULT_ID_KEY;
		edgeTypeKey = DEFAULT_EDGE_TYPE_KEY;
		edgeWeightKey = DEFAULT_EDGE_WEIGHT_KEY;
	}
	
	public String getIdKey() {
		return idKey;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	public String getEdgeTypeKey() {
		return edgeTypeKey;
	}

	public void setEdgeTypeKey(String edgeTypeKey) {
		this.edgeTypeKey = edgeTypeKey;
	}
	
	public String getEdgeWeightKey() {
		return edgeWeightKey;
	}

	public void setEdgeWeightKey(String edgeWeightKey) {
		this.edgeWeightKey = edgeWeightKey;
	}

	/**
	 * Write the graph out the the given filename.
	 * @param graph
	 */
	public void write(Graph graph, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		
		write(graph, writer);
		writer.close();
	}
	
	/**
	 * Use the supplied writer to write the graph in VNA format.
	 */
	public void write(Graph graph, Writer writer) throws IOException {
		// First the node data section.
		writer.write("*Node data\n");
		
		// Need to collect a complete list of node attributes.
		List attributes = new LinkedList();
		Set vertices = graph.getVertices();
		for(Iterator i = vertices.iterator(); i.hasNext(); ) {
			Vertex v = (Vertex)i.next();
			for(Iterator j = v.getUserDatumKeyIterator(); j.hasNext(); ) {
				Object o = j.next();
				if(!(o instanceof String))
					continue;
				
				String attribute = o.toString();
				if(!attributes.contains(attribute) && !getIdKey().equals(attribute))
					attributes.add(attribute);
			}
		}
		
		// Now we have the list, we need to output the column headers
		writer.write("\""+escapeText(getIdKey())+"\" ");
		for(Iterator i = attributes.iterator(); i.hasNext();)
			writer.write("\""+escapeText(i.next().toString())+"\" ");
		writer.write("\n");
		
		// Now we need to output node entries.
		int idCounter = 1;
		for(Iterator i = vertices.iterator(); i.hasNext(); ) {
			Vertex v = (Vertex)i.next();

			if(v.getUserDatum(getIdKey()) == null) {
				v.addUserDatum(getIdKey(), String.valueOf(idCounter), UserData.CLONE);
				idCounter++;
			}
			
			writer.write("\""+escapeText(v.getUserDatum(getIdKey()).toString())+"\" ");
			for(Iterator j = attributes.iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if(v.getUserDatum(key) != null)
					writer.write("\""+v.getUserDatum(key)+"\" ");
				else
					writer.write("\"\" ");
			}
			writer.write("\n");
		}
		
		// On to the tie data section!
		writer.write("*Tie data\n");
		
		// Let's gather all our types of edges first.
		Set edges = graph.getEdges();
		
		LinkedList edgeTypes = new LinkedList();
		for(Iterator i = edges.iterator(); i.hasNext();) {
			Edge e = (Edge)i.next();
			
			if(e.getUserDatum(getEdgeTypeKey()) != null) {
				if(!edgeTypes.contains(e.getUserDatum(getEdgeTypeKey())))
					edgeTypes.add(e.getUserDatum(getEdgeTypeKey()));
			} else {
				e.setUserDatum(getEdgeTypeKey(), "edge", UserData.CLONE);
				if(!edgeTypes.contains("edge"))
					edgeTypes.add("edge");
			}
        
		}
		
		// Write out the column headers for tie data
		writer.write("from to ");
		for(Iterator i = edgeTypes.iterator(); i.hasNext(); )
			writer.write("\""+escapeText(i.next().toString())+"\" ");
		writer.write("\n");
		
		// And at last - the tie data entries!
		for(Iterator i = edges.iterator(); i.hasNext(); ) {
			Edge e = (Edge)i.next();
			Vertex from = (Vertex)e.getEndpoints().getFirst();
			Vertex to = (Vertex)e.getEndpoints().getSecond();
			
			writer.write("\""+escapeText(from.getUserDatum(getIdKey()).toString())+"\" ");
			writer.write("\""+escapeText(to.getUserDatum(getIdKey()).toString())+"\" ");
			for(Iterator j = edgeTypes.iterator(); j.hasNext(); ) {
				String type = (String)j.next();
				if(e.getUserDatum(getEdgeWeightKey()) != null)
					writer.write("\""+escapeText(e.getUserDatum(getEdgeWeightKey()).toString())+"\" ");
				else
					writer.write("\"0.0\"");
			}
			
			writer.write("\n");
		}
		
		// Fin.  User should close her/his own writer.
	}
	
	/**
	 * Escapes the given text in case it contains quotes.
	 */
	private String escapeText(String text) {
		return text.replaceAll("\"", "\\\"");
	}
}
