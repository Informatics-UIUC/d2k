package ncsa.d2k.modules.core.vis.pgraph;

import edu.uci.ics.jung.graph.event.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import ncsa.d2k.modules.core.vis.pgraph.nodes.*;

// !: doc note: call stop() to kill thread when done!
public class PGraphCanvas extends PCanvas implements GraphEventListener {

   protected Graph graph;
   protected Layout layout;

   protected HashMap vertexMap; // v -> vn
   protected HashMap edgeMap; // e -> en

   protected PGraphMouseHandler mouseHandler;

   protected AdvanceThread advanceThread;
   protected Object pauseObject = new Object();
   protected boolean advance_running = false;
   protected boolean manual_suspend;
   protected boolean suspended;
   protected boolean brushing;
   protected PVertexNode brushedNode;
   protected transient long advance_sleep = 800L;

   protected ArrayList vertexSelectionListeners;
   protected ArrayList zoomListeners;

   // !: doc note: must initialize layout first
   public PGraphCanvas(Graph graph, Layout layout) {

      super();

      this.graph = graph;
      graph.addListener(this, GraphEventType.ALL_SINGLE_EVENTS);
      this.layout = layout;

      vertexMap = new HashMap();
      edgeMap = new HashMap();

      reconstruct();

      suspended = false;
      manual_suspend = false;
      brushing = false;
      brushedNode = null;

      mouseHandler = new PGraphMouseHandler();
      removeInputEventListener(getZoomEventHandler());
      removeInputEventListener(getPanEventHandler());
      addInputEventListener(mouseHandler);

      vertexSelectionListeners = new ArrayList();
      zoomListeners = new ArrayList();

      // advanceThread = new AdvanceThread();
      // advanceThread.start();

   }

   public void addVertexSelectionListener(VertexSelectionListener vsl) {

      synchronized(vertexSelectionListeners) {
         vertexSelectionListeners.add(vsl);
      }

   }

   public void addZoomListener(ZoomListener zl) {

      synchronized(zoomListeners) {
         zoomListeners.add(zl);
      }

   }

   public void edgeAdded(GraphEvent event) {

      Edge e = (Edge)event.getGraphElement();
      PEdgeNode en = new PEdgeNode(e);
      edgeMap.put(e, en);

      Vertex v1 = (Vertex)e.getEndpoints().getFirst();
      Vertex v2 = (Vertex)e.getEndpoints().getSecond();
      double x1 = layout.getX(v1);
      double y1 = layout.getY(v1);
      double x2 = layout.getX(v2);
      double y2 = layout.getY(v2);

      if (x2 > x1 && y2 > y1) {
         en.setBounds(x2, y2, x1, y1);
      }
      else {
         en.setBounds(x1, y1, x2, y2);
      }
      getLayer().addChild(0, en);

      // !: ?
      if (layout instanceof LayoutMutable) {
         ((LayoutMutable)layout).update();
      }

   }

   public void edgeModified(Edge e) {

      PEdgeNode en = (PEdgeNode)vertexMap.remove(e);
      if (en != null) {
         getLayer().removeChild(en);
      }

      en = new PEdgeNode(e);
      edgeMap.put(e, en);

      Vertex v1 = (Vertex)e.getEndpoints().getFirst();
      Vertex v2 = (Vertex)e.getEndpoints().getSecond();
      double x1 = layout.getX(v1);
      double y1 = layout.getY(v1);
      double x2 = layout.getX(v2);
      double y2 = layout.getY(v2);

      if (x2 > x1 && y2 > y1) {
         en.setBounds(x2, y2, x1, y1);
      }
      else {
         en.setBounds(x1, y1, x2, y2);
      }
      getLayer().addChild(0, en);

   }

   public void edgeRemoved(GraphEvent event) {

      Edge e = (Edge)event.getGraphElement();
      PEdgeNode en = (PEdgeNode)edgeMap.remove(e);

      if (en != null) {
         getLayer().removeChild(en);
      }

      // !: ?
      if (layout instanceof LayoutMutable) {
         ((LayoutMutable)layout).update();
      }

   }

   protected synchronized void man_suspend() {
      manual_suspend = true;
   }

   protected synchronized void man_unsuspend() {

      manual_suspend = false;

      synchronized (pauseObject) {
         pauseObject.notifyAll();
      }

   }

   protected static Set neighborhood(Graph g, Vertex v, int max_depth) {

      int current_depth = 0;
      ArrayList currentVertices = new ArrayList();
      HashSet visitedVertices = new HashSet();
      HashSet visitedEdges = new HashSet();
      Set acceptedVertices = new HashSet();

      visitedVertices.add(v);
      acceptedVertices.add(v);
      currentVertices.add(v);

      // use BFS to locate the neighborhood around the root vertex
      // within distance max_depth
      ArrayList newVertices;
      Iterator vIter, neIter;
      Vertex currentVertex, currentNeighbor;
      Edge currentEdge;
      Set edges;
      while (current_depth < max_depth) {

         newVertices = new ArrayList();

         vIter = currentVertices.iterator();
         while (vIter.hasNext()) {

            currentVertex = (Vertex)vIter.next();
            edges = currentVertex.getIncidentEdges();

            neIter = edges.iterator();
            while (neIter.hasNext()) {

               currentEdge = (Edge)neIter.next();
               currentNeighbor = currentEdge.getOpposite(currentVertex);

               if (!visitedEdges.contains(currentEdge)) {

                  visitedEdges.add(currentEdge);

                  if (!visitedVertices.contains(currentNeighbor)) {
                     visitedVertices.add(currentNeighbor);
                     acceptedVertices.add(currentNeighbor);
                     newVertices.add(currentNeighbor);
                  }

               }

            }

         }

         currentVertices = newVertices;
         current_depth++;

      }

      return acceptedVertices;

   }

   public Layout getGraphLayout() {
      return layout;
   }

   public Dimension getMinimumSize() {
      return layout.getCurrentSize();
   }

   public Dimension getPreferredSize() {
      return layout.getCurrentSize();
   }

   // !: doc note: must be enclosed by suspend()/unsuspend()
   // !: doc note: efficiency: order of setLayout()/reconstruct() might matter
   protected void reconstruct() {

      edgeMap.clear();
      vertexMap.clear();

      getLayer().removeAllChildren();

      Iterator eIter = graph.getEdges().iterator();
      Edge e;
      PEdgeNode en;
      Vertex v1, v2;
      double x1, x2, y1, y2;

      while (eIter.hasNext()) {

         e = (Edge)eIter.next();
         en = new PEdgeNode(e);
         edgeMap.put(e, en);

         v1 = (Vertex)e.getEndpoints().getFirst();
         v2 = (Vertex)e.getEndpoints().getSecond();
         x1 = layout.getX(v1);
         y1 = layout.getY(v1);
         x2 = layout.getX(v2);
         y2 = layout.getY(v2);

         if (x2 > x1 && y2 > y1) {
            en.setBounds(x2, y2, x1, y1);
         }
         else {
            en.setBounds(x1, y1, x2, y2);
         }
         getLayer().addChild(en);

      }

      Iterator vIter = graph.getVertices().iterator();
      Object datum;
      Vertex v;
      PVertexNode vn;
      int type;

      while (vIter.hasNext()) {

         v = (Vertex)vIter.next();
         datum = v.getUserDatum(PVertexNode.SHAPE);

         if (datum == null) {
            type = PVertexNode.DEFAULT;
         }
         else {
            type = ((Integer)datum).intValue();
         }

         vn = PVertexNode.createNode(v, type);
         vertexMap.put(v, vn);

         vn.setBounds(layout.getX(v) - vn.getWidth()/2d,
                      layout.getY(v) - vn.getHeight()/2d,
                      vn.getWidth(),
                      vn.getHeight());
         getLayer().addChild(vn);

      }

   }

   // !: doc note: does not deal with added/removed nodes
   protected void reposition() {

      Iterator eIter = graph.getEdges().iterator();
      Edge e;
      PEdgeNode en;
      Vertex v1, v2;
      double x1, x2, y1, y2;
      while (eIter.hasNext()) {

         e = (Edge)eIter.next();
         en = (PEdgeNode)edgeMap.get(e);

         v1 = (Vertex)e.getEndpoints().getFirst();
         v2 = (Vertex)e.getEndpoints().getSecond();
         x1 = layout.getX(v1);
         y1 = layout.getY(v1);
         x2 = layout.getX(v2);
         y2 = layout.getY(v2);

         if (x2 > x1 && y2 > y1) {
            en.setBounds(x2, y2, x1, y1);
         }
         else {
            en.setBounds(x1, y1, x2, y2);
         }

      }

      Iterator vIter = graph.getVertices().iterator();
      Vertex v;
      PVertexNode vn;
      while (vIter.hasNext()) {

         v = (Vertex)vIter.next();
         vn = (PVertexNode)vertexMap.get(v);

         vn.setBounds(layout.getX(v) - vn.getWidth()/2d,
                      layout.getY(v) - vn.getHeight()/2d,
                      vn.getWidth(),
                      vn.getHeight());

      }

      getLayer().repaint();

   }

   public void removeVertexSelectionListener(VertexSelectionListener vsl) {

      synchronized(vertexSelectionListeners) {
         vertexSelectionListeners.remove(vsl);
      }

   }

   public void removeZoomListener(ZoomListener zl) {

      synchronized(zoomListeners) {
         zoomListeners.remove(zl);
      }

   }

   public void reinitializeAndCenter() {

      PBounds viewBounds = getCamera().getViewBounds();
      layout.initialize(new Dimension((int)viewBounds.width,
                                      (int)viewBounds.height));
      reposition();
      PBounds layerBounds = getLayer().getFullBounds();
      getCamera().setViewOffset(0, 0); // (layerBounds.x, layerBounds.y);

   }

   // !: doc note: must be enclosed by suspend()/unsuspend()
   // !: doc note: must initialize layout first
   public void reset(Graph gg, Layout ll) {

      // ll.initialize(layout.getCurrentSize());
      graph = gg;
      layout = ll;
      reconstruct();

   }

   public void setAdvanceSleep(long l) {
      advance_sleep = l;
   }

   public void setEdgeColor(Edge e, Color color) {
      PEdgeNode en = (PEdgeNode)edgeMap.get(e);
      if (en != null) {
         en.setColor(color);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setEdgeText(Edge e, String s) {
      PEdgeNode en = (PEdgeNode)edgeMap.get(e);
      if (en != null) {
         en.setText(s);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setEdgeWidth(Edge e, double w) {
      PEdgeNode en = (PEdgeNode)edgeMap.get(e);
      if (en != null) {
         en.setWidth(w);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   // !: doc note: must be enclosed by suspend()/unsuspend()
   public void setLabelsVisible(boolean visible) {

      Iterator vIter = graph.getVertices().iterator();


      Vertex v;
      PVertexNode vn;
      while (vIter.hasNext())
      {
        v = (Vertex)vIter.next();
        vn = (PVertexNode)vertexMap.get(v);
        vn.setLabelVisible(visible);
      }

      Iterator eIter = graph.getEdges().iterator();
      Edge e;
      PEdgeNode en;
      while (eIter.hasNext()) {

         e = (Edge)eIter.next();
         en = (PEdgeNode)edgeMap.get(e);
         en.setLabelVisible(visible);

      }

   }

   // !: doc note: must be enclosed by suspend()/unsuspend()
   // !: doc note: initializes
   public void setLayout(Layout ll) {

      ll.initialize(layout.getCurrentSize());
      layout = ll;
      layout.restart();
      reposition();

   }

   public void setAntialiasing(boolean set) {

      if (set) {
         setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
      }
      else {
         setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
      }

   }

   public void setVertexBorderColor(Vertex v, Color color) {
      PVertexNode vn = (PVertexNode)vertexMap.get(v);
      if (vn != null) {
         vn.setBorderColor(color);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setVertexColor(Vertex v, Color color) {
      PVertexNode vn = (PVertexNode)vertexMap.get(v);
      if (vn != null) {
         vn.setColor(color);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setVertexSize(Vertex v, double d) {
      PVertexNode vn = (PVertexNode)vertexMap.get(v);
      if (vn != null) {
         vn.setSize(d);
         PBounds bounds = vn.getBounds();
         vn.setBounds(bounds.x, bounds.y, d, d);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setVertexSize(Vertex v, double w, double h) {
      PVertexNode vn = (PVertexNode)vertexMap.get(v);
      if (vn != null) {
         vn.setSize(w, h);
         PBounds bounds = vn.getBounds();
         vn.setBounds(bounds.x, bounds.y, w, h);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public void setVertexText(Vertex v, String s) {
      PVertexNode vn = (PVertexNode)vertexMap.get(v);
      if (vn != null) {
         vn.setText(s);
      }
      else {
         throw new IllegalArgumentException();
      }
   }

   public synchronized void suspend() {

      if (advanceThread != null) {
        advanceThread.stopme();

        while (advance_running) {
          try {
            Thread.sleep(20);
          }
          catch (InterruptedException ie) {}
        }
      }

      advance_running = false;

   }

   public synchronized void stop() {

      manual_suspend = false;
      suspended = false;

      if (advanceThread != null) {
         advanceThread.stopme();
      }

      synchronized (pauseObject) {
         pauseObject.notifyAll();
      }

   }

   public void vertexAdded(GraphEvent e) {

      Vertex v = (Vertex)e.getGraphElement();
      Object datum = v.getUserDatum(PVertexNode.SHAPE);

      int type;
      if (datum == null) {
         type = PVertexNode.DEFAULT;
      }
      else {
         type = ((Integer)datum).intValue();
      }

      PVertexNode vn = PVertexNode.createNode(v, type);
      vertexMap.put(v, vn);

      Object xd = v.getUserDatum("x");
      Object yd = v.getUserDatum("y");
      double x, y;

      if (xd != null && yd != null) {
         x = ((Double)xd).doubleValue();
         y = ((Double)yd).doubleValue();
      }
      else {
         x = 0.0;
         y = 0.0;
      }

      vn.setBounds(x - vn.getWidth()/2d,
                   y - vn.getHeight()/2d,
                   vn.getWidth(),
                   vn.getHeight());
      getLayer().addChild(vn);

      // !: all mutable only?
      if (layout instanceof LayoutMutable) {
         ((LayoutMutable)layout).update();
         layout.forceMove(v, (int)x, (int)y);
      }

   }

   public void vertexModified(Vertex v) {

      Object datum = v.getUserDatum(PVertexNode.SHAPE);
      int type;

      if (datum == null) {
         type = PVertexNode.DEFAULT;
      }
      else {
         type = ((Integer)datum).intValue();
      }

      PVertexNode vn = (PVertexNode)vertexMap.remove(v);
      if (vn != null) {
         getLayer().removeChild(vn);
      }

      vn = PVertexNode.createNode(v, type);
      vertexMap.put(v, vn);

      vn.setBounds(layout.getX(v) - vn.getWidth()/2d,
                   layout.getY(v) - vn.getHeight()/2d,
                   vn.getWidth(),
                   vn.getHeight());
      getLayer().addChild(vn);

   }

   public void vertexRemoved(GraphEvent e) {

      Vertex v = (Vertex)e.getGraphElement();
      PVertexNode vn = (PVertexNode)vertexMap.remove(v);

      if (vn != null) {
         getLayer().removeChild(vn);
      }

      // !: ?
      if (layout instanceof LayoutMutable) {
         ((LayoutMutable)layout).update();
      }

   }

   public synchronized void unsuspend() {

      if (advanceThread != null) {
         advanceThread.stopme();
      }

      while (advance_running)
      {
         try
         {
            Thread.sleep(20);
         }
         catch (InterruptedException ie) { }
      }

      advanceThread = new AdvanceThread();
      advanceThread.start();

      // synchronized (pauseObject) {
      //    pauseObject.notifyAll();
      // }

   }

   protected class AdvanceThread extends Thread {

      public boolean stop = false;

      public void stopme() {
         stop = true;
      }

      public void run() {

         advance_running = true;

         while (!layout.incrementsAreDone() && !stop) {

            synchronized (pauseObject) {

               while ((suspended || manual_suspend) && !stop) {

                  try {
                     pauseObject.wait();
                  }
                  catch (InterruptedException ie) { }

               }

            }

            // !:
            try {
               // for (int i = 0; i < 10; i++) {
                  layout.advancePositions();
               // }
            }
            catch (Exception e) {
               e.printStackTrace();
               return;
            }

            if (stop) {
              advance_running = false;
               return;
            }

            reposition();

            if (stop){
              advance_running = false;
              return;
            }

            try {
               sleep(advance_sleep);
            }
            catch (InterruptedException ie) { }

         }

         advance_running = false;

      }

   }

   protected class PGraphMouseHandler extends PBasicInputEventHandler {

      private PInputEventFilter leftMouseFilter, rightMouseFilter;

      private PPanEventHandler panEventHandler;
      private PZoomEventHandler zoomEventHandler;

      private boolean drag_override = false;

      private double press_x, press_y;

      PGraphMouseHandler() {

         leftMouseFilter = new PInputEventFilter(InputEvent.BUTTON1_MASK);
         rightMouseFilter = new PInputEventFilter(InputEvent.BUTTON3_MASK);

         panEventHandler = new PPanEventHandler();
         zoomEventHandler = new PZoomEventHandler();

      }

      public void mouseMoved(PInputEvent event)
      {
        if(brushing)
        {
          Point2D movePt = event.getPosition();
          Vertex v = layout.getVertex(movePt.getX(), movePt.getY());
          PVertexNode vn = (PVertexNode)vertexMap.get(v);

          double true_x = layout.getX(v);
          double true_y = layout.getY(v);
          if (Math.abs(movePt.getX() - true_x) < vn.getWidth()/2d &&
              Math.abs(movePt.getY() - true_y) < vn.getHeight()/2d)
          {
            vn.setLabelVisible(true);
            if(brushedNode != null)
            {
              if(brushedNode != vn)
              {
                brushedNode.setLabelVisible(false);
              }
            }
            brushedNode = vn;
          }
        }
      }

      public void mouseClicked(PInputEvent event) {

         if (event.getClickCount() >= 2 &&
             leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED)) {

            Point2D clickPt = event.getPosition();
            Vertex v = layout.getVertex(clickPt.getX(), clickPt.getY());
            PVertexNode vn = (PVertexNode)vertexMap.get(v);

            double true_x = layout.getX(v);
            double true_y = layout.getY(v);

            if (Math.abs(clickPt.getX() - true_x) < vn.getWidth()/2d &&
                Math.abs(clickPt.getY() - true_y) < vn.getHeight()/2d) {

               synchronized (vertexSelectionListeners) {

                  Iterator lIter = vertexSelectionListeners.iterator();
                  while (lIter.hasNext()) {
                     ((VertexSelectionListener)lIter.next()).vertexSelected(v);
                  }

               }

            }

         }
         else {

            if (leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED)) {
               panEventHandler.mouseClicked(event);
            }
            else if (rightMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED)) {
               zoomEventHandler.mouseClicked(event);
            }

         }

      }

      public void mouseDragged(PInputEvent event) {

         if (drag_override) {
            return;
         }

         if (leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_DRAGGED)) {
            panEventHandler.mouseDragged(event);
         }
         else if (rightMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_DRAGGED)) {
            zoomEventHandler.mouseDragged(event);
         }

      }

      public void mousePressed(PInputEvent event) {

         man_suspend();

         Point2D clickPt = event.getPosition();
         Vertex v = layout.getVertex(clickPt.getX(), clickPt.getY());
         PVertexNode vn = (PVertexNode)vertexMap.get(v);

         double true_x = layout.getX(v);
         double true_y = layout.getY(v);

         if (Math.abs(clickPt.getX() - true_x) < vn.getWidth()/2d &&
             Math.abs(clickPt.getY() - true_y) < vn.getHeight()/2d) {

            press_x = clickPt.getX();
            press_y = clickPt.getY();

            drag_override = true;

         }
         else {

            man_unsuspend();

            if (leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_PRESSED)) {
               panEventHandler.mousePressed(event);
            }
            else if (rightMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_PRESSED)) {
               zoomEventHandler.mousePressed(event);
            }
         }
      }

      public void mouseReleased(PInputEvent event)
      {
         if (drag_override) {

            Point2D clickPt = event.getPosition();
            Vertex v = layout.getVertex(press_x, press_y);

            double diff_x = press_x - clickPt.getX();
            double diff_y = press_y - clickPt.getY();

            if (leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_RELEASED)) {

               // move this vertex
               layout.forceMove(v,
                                (int)(layout.getX(v) - diff_x),
                                (int)(layout.getY(v) - diff_y));

               reposition();

            }
            else if (rightMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_RELEASED)) {

               // move all connected vertices
               Set n = neighborhood(graph, v, 10); // !: make this a property?
               Iterator nIt = n.iterator();
               Vertex vv;
               while (nIt.hasNext()) {

                  vv = (Vertex)nIt.next();

                  layout.forceMove(vv,
                                   (int)(layout.getX(vv) - diff_x),
                                   (int)(layout.getY(vv) - diff_y));

               }

               reposition();

            }

            drag_override = false;

            man_unsuspend();

         }
         else {

            if (leftMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_RELEASED)) {
               panEventHandler.mouseReleased(event);
            }
            else if (rightMouseFilter.acceptsEvent(event, MouseEvent.MOUSE_RELEASED)) {

               zoomEventHandler.mouseReleased(event);

               synchronized (zoomListeners) {

                  Iterator zIter = zoomListeners.iterator();
                  while (zIter.hasNext()) {
                     ((ZoomListener)zIter.next()).graphZoomed();
                  }
               }
            }
         }
      }
   }

   public void setBrushing(boolean isBrushing)
   {
     brushing = isBrushing;
   }
}
