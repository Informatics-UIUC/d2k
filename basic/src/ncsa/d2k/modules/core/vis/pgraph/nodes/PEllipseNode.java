package ncsa.d2k.modules.core.vis.pgraph.nodes;

import edu.uci.ics.jung.graph.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.*;
import java.awt.geom.*;

public class PEllipseNode extends PVertexNode {

   private double width, height;
   private Ellipse2D ellipse;

   public PEllipseNode(Vertex v) {

      super(v);

      ellipse = new Ellipse2D.Double();

      Object datum = v.getUserDatum(WIDTH);
      if (datum != null) {
         width = ((Double)datum).doubleValue();
      }
      else {
         width = 2d;
      }

      datum = v.getUserDatum(HEIGHT);
      if (datum != null) {
         height = ((Double)datum).doubleValue();
      }
      else {
         height = 2d;
      }

      setBounds(-width/2d, -height/2d, width, height);

   }

   public double getHeight() {
      return height;
   }

   public double getWidth() {
      return width;
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
      width = d;
      height = d;
   }

   public void setSize(double w, double h) {
      width = w;
      height = h;
   }

}
