package ncsa.d2k.modules.core.vis.pgraph.nodes;

import edu.uci.ics.jung.graph.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.*;
import java.awt.geom.*;

public class PCircleNode extends PVertexNode {

   private double size;
   private Ellipse2D ellipse;

   public PCircleNode(Vertex v) {

      super(v);

      ellipse = new Ellipse2D.Double();

      Object datum = v.getUserDatum(SIZE);
      if (datum != null) {
         size = ((Double)datum).doubleValue();
      }
      else {
         size = 2d;
      }

      setBounds(-size/2d, -size/2d, size, size);

   }

   public double getHeight() {
      return size;
   }

   public double getWidth() {
      return size;
   }

   public boolean intersects(Rectangle2D aBounds) {
      return ellipse.intersects(aBounds);
   }

   public void paint(PPaintContext aPaintContext) {

      Graphics2D g2 = aPaintContext.getGraphics();

      g2.setPaint(baseColor);
      g2.fill(ellipse);

      if (borderColor != null) {
         g2.setPaint(borderColor);
         g2.setStroke(borderStroke);
         g2.draw(ellipse);
      }

   }

   public boolean setBounds(double x, double y, double w, double h) {

      if(super.setBounds(x, y, w, h)) {
         ellipse.setFrame(x, y, w, h);
         return true;
      }

      return false;

   }

   public void setSize(double d) {
      size = d;
   }

   public void setSize(double w, double h) { }

}
