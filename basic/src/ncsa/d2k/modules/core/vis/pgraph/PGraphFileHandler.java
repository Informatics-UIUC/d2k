package ncsa.d2k.modules.core.vis.pgraph;

import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.utils.*;
import java.awt.*;
import java.util.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;

public class PGraphFileHandler extends GraphMLFileHandler {

   public Graph graph() {

      Graph graph = getGraph();
      Iterator vIter = graph.getVertices().iterator(), keyIter;
      Vertex v;
      Object key, datum;
      while (vIter.hasNext()) {

         v = (Vertex)vIter.next();
         keyIter = v.getUserDatumKeyIterator();
         while (keyIter.hasNext()) {

            key = keyIter.next();
            datum = v.getUserDatum(key);

            if (key.equals(PVertexNode.SHAPE)) {

               if (datum.equals("ellipse")) {
                  v.setUserDatum(key,
                                 new Integer(PVertexNode.ELLIPSE),
                                 UserData.SHARED);
               }
               else if (datum.equals("circle")) {
                  v.setUserDatum(key,
                                 new Integer(PVertexNode.CIRCLE),
                                 UserData.SHARED);
               }
               else if (datum.equals("rectangle")) {
                  v.setUserDatum(key,
                                 new Integer(PVertexNode.RECTANGLE),
                                 UserData.SHARED);
               }
               else if (datum.equals("square")) {
                  v.setUserDatum(key,
                                 new Integer(PVertexNode.SQUARE),
                                 UserData.SHARED);
               }

            }
            else if (key.equals(PVertexNode.SIZE)         ||
                     key.equals(PVertexNode.WIDTH)        ||
                     key.equals(PVertexNode.HEIGHT)       ||
                     key.equals(PVertexNode.BORDERWIDTH))    {

               v.setUserDatum(key,
                              new Double(Double.parseDouble((String)datum)),
                              UserData.SHARED);

            }
            else if (key.equals(PVertexNode.COLOR)       ||
                     key.equals(PVertexNode.BORDERCOLOR) ||
                     key.equals(PVertexNode.LABELCOLOR))    {

               v.setUserDatum(key,
                              Color.decode((String)datum),
                              UserData.SHARED);

            }
            else if (key.equals(PVertexNode.LABELMAX)   ||
                     key.equals(PVertexNode.LABELSIZE))    {

               v.setUserDatum(key,
                              new Integer(Integer.parseInt((String)datum)),
                              UserData.SHARED);

            }

         }

      }

      Iterator eIter = graph.getEdges().iterator();
      Edge e;
      while (eIter.hasNext()) {

         e = (Edge)eIter.next();
         keyIter = e.getUserDatumKeyIterator();
         while (keyIter.hasNext()) {

            key = keyIter.next();
            datum = e.getUserDatum(key);

            if (key.equals(PEdgeNode.WIDTH)) {

               e.setUserDatum(key,
                              new Double(Double.parseDouble((String)datum)),
                              UserData.SHARED);

            }
            else if (key.equals(PEdgeNode.COLOR)       ||
                     key.equals(PEdgeNode.LABELCOLOR))    {

               e.setUserDatum(key,
                              Color.decode((String)datum),
                              UserData.SHARED);

            }
            else if (key.equals(PEdgeNode.LABELMAX)   ||
                     key.equals(PEdgeNode.LABELSIZE))    {

               e.setUserDatum(key,
                              new Integer(Integer.parseInt((String)datum)),
                              UserData.SHARED);

            }

         }

      }

      return graph;

   }

}
