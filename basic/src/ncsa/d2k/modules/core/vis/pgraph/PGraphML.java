package ncsa.d2k.modules.core.vis.pgraph;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.utils.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;
import org.xml.sax.*;

/**
 * This is more or less derived from revision 1.23 of JUNG's GraphMLFile class.
 */
public class PGraphML {

   protected static final String afterKey = "=\"";
   protected static final String afterVal = "\" ";
   protected static final String hexEnum = "0123456789ABCDEF";

   protected PGraphML() { }

   protected static String colorToHex(Color c) {

      StringBuffer sb = new StringBuffer();
      sb.append('#');
      sb.append(intToHex(c.getRed()));
      sb.append(intToHex(c.getGreen()));
      sb.append(intToHex(c.getBlue()));
      return sb.toString();

   }

   protected static String intToHex(int i) {

      if (i < 0 || i > 255) {
         throw new IllegalArgumentException();
      }

      char[] digits = new char[2];
      for (int k = 1; k >= 0; k--) {
         digits[k] = hexEnum.charAt(i & 15);
         i = i >>> 4;
      }

      return new String(digits);

   }

   protected static boolean isValidXML(String str) {

      if (str.indexOf('&')  != -1 ||
          str.indexOf('<')  != -1 ||
          str.indexOf('>')  != -1 ||
          str.indexOf('\'') != -1 ||
          str.indexOf('\"') != -1)   {

         return false;

      }
      else {
         return true;
      }

   }

   public static Graph load(File file) throws IOException {

      PGraphFileHandler mFileHandler = new PGraphFileHandler();

      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         saxParser.parse(file, mFileHandler);
         // new org.apache.crimson.parser.Parser2();
      }
      catch (ParserConfigurationException pce) { pce.printStackTrace(); }
      catch (SAXException saxe) { saxe.printStackTrace(); }

      return mFileHandler.graph();

   }

   protected static void print(PrintStream out, String key, String value) {
      out.print(key + afterKey + value + afterVal);
   }

   public static void save(Graph graph, File file) throws IOException {

      PrintStream out = new PrintStream(new FileOutputStream(file, false));

      save(graph, out);
      out.close();

   }

   protected static void save(Graph graph, PrintStream out) {

      out.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
      out.println("<?meta name=\"GENERATOR\" content=\"XML::Smart 1.3.1\" ?>");

      out.print  ("<graph edgedefault=\"");
      if (PredicateUtils.enforcesEdgeConstraint(graph, Graph.DIRECTED_EDGE)) {
         out.print("directed\" ");
      }
      else if (PredicateUtils.enforcesEdgeConstraint(graph, Graph.UNDIRECTED_EDGE)) {
         out.print("undirected\" ");
      }
      else {
         throw new IllegalArgumentException("Mixed (directed/undirected) graphs are not currently supported.");
      }
      saveUserData(graph, out);
      out.println(" >");

      saveVertices(graph, out);
      saveEdges(graph, out);

      out.println("</graph>");

   }

   protected static void saveEdges(Graph graph, PrintStream out) {

      Indexer id = Indexer.getIndexer(graph);

      Iterator eIter = graph.getEdges().iterator();
      Edge e;
      Pair p;
      Vertex src, dest;
      int srcId, destId;
      while (eIter.hasNext()) {

         e = (Edge)eIter.next();
         p = e.getEndpoints();
         src = (Vertex)p.getFirst();
         dest = (Vertex)p.getSecond();
         srcId = id.getIndex(src) + 1;
         out.print("<edge source=\"" + srcId + "\" ");
         destId = id.getIndex(dest) + 1;
         out.print("target=\"" + destId + "\" ");

         saveUserData(e, out);
         out.println("/>");

      }

   }

   protected static void saveUserData(UserDataContainer udc, PrintStream out) {

      Iterator keyIterator = udc.getUserDatumKeyIterator();
      while (keyIterator.hasNext()) {

         Object keyObj = keyIterator.next();
         String keyStr = keyObj.toString();

         if (!isValidXML(keyStr)) {
            continue;
         }

         Object datum = udc.getUserDatum(keyStr);

         if (datum == null) {
            continue;
         }

         if (keyStr.equals(PVertexNode.COLOR) ||
             keyStr.equals(PVertexNode.BORDERCOLOR) ||
             keyStr.equals(PVertexNode.LABELCOLOR)) {

            print(out, keyStr, colorToHex((Color)datum));

         }
         else if (keyStr.equals(PVertexNode.SHAPE)) {

            if (datum instanceof Integer) {

               int shape = ((Integer)datum).intValue();

               switch (shape) {

                  case PVertexNode.ELLIPSE:
                     print(out, keyStr, PVertexNode.TYPE_ELLIPSE);
                     break;
                  case PVertexNode.CIRCLE:
                     print(out, keyStr, PVertexNode.TYPE_CIRCLE);
                     break;
                  case PVertexNode.RECTANGLE:
                     print(out, keyStr, PVertexNode.TYPE_RECTANGLE);
                     break;
                  case PVertexNode.SQUARE:
                     print(out, keyStr, PVertexNode.TYPE_SQUARE);
                     break;

               }

            }
            else {

               if (datum.equals(PVertexNode.TYPE_ELLIPSE)) {
                  print(out, keyStr, PVertexNode.TYPE_ELLIPSE);
                  break;
               }
               else if (datum.equals(PVertexNode.TYPE_CIRCLE)) {
                  print(out, keyStr, PVertexNode.TYPE_CIRCLE);
                  break;
               }
               else if (datum.equals(PVertexNode.TYPE_RECTANGLE)) {
                  print(out, keyStr, PVertexNode.TYPE_RECTANGLE);
                  break;
               }
               else if (datum.equals(PVertexNode.TYPE_SQUARE)) {
                  print(out, keyStr, PVertexNode.TYPE_SQUARE);
                  break;
               }

            }

         }
         else if (keyStr.equals("id")) { }
         else {

            if (isValidXML(datum.toString())) {
               print(out, keyStr, datum.toString());
            }

         }

      }

   }

   protected static void saveVertices(Graph graph, PrintStream out) {

      int numVertices = graph.getVertices().size();
      Indexer id = Indexer.getIndexer(graph);

      Vertex v;
      int v_id;
      for (int i = 0; i < numVertices; i++) {

         v = (Vertex)id.getVertex(i);
         v_id = i + 1;
         out.print("<node id=\"" + v_id + "\" ");

         saveUserData(v, out);
         out.println("/>");

      }

   }

}
