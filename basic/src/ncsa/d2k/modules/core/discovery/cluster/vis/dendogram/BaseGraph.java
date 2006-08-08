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
package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 *BaseGraph is a base class for a 2D graph. The graph consists of nodes and edges.
 * A node that has children is a shape int he graph, a childless node is a leaf.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public abstract class BaseGraph extends JPanel {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 4588250064422185901L;

   //~ Instance fields *********************************************************

   /** Offset from bottom, in pixles. */
   double bottomoffset;

   /** Font. */
   Font font;

   /**
    * Distance beween font's baseline to the top of most alphanumeric
    * characters.
    */
   int fontascent;

   /** Font height. */
   int fontheight;

   /** The height of the graph, in pixles. */
   int graphheight;

   /** The width of the graph , in pixles. */
   int graphwidth;

   /**
    * The granularity measure of the graph / resolution of the graph, in pixles.
    */
   int gridsize;

   /** Constant used to space item in the graph apart, in pixles. */
   int largespace = 10;

   /** Constant used to keep the left mergins clear, in pixles. */
   double leftoffset;

   /** Height of the legend component, in pixles. */
   double legendheight;

   /** X offset for the legend display , in pixles. */
   double legendleftoffset;

   /** Y offset for the legend display, in pixles. */
   double legendtopoffset;

   /** Width of the legend component, in pixles. */
   double legendwidth;

   /** nodes that are leaves. */
   ArrayList m_leaves = null;

   /** The Table Clusters encapsulated by <code>m_leaves.</codE> */
   ArrayList m_leavesC = new ArrayList();

   /** nodes in the raph that are not leaves. */
   ArrayList m_shapes = null;

   /** encapsulates information about the rendering of <code>Fond</code>. */
   FontMetrics metrics;

   /**
    * lower bounds for X offset increment (when x value needs to be updated when
    * drawing a line), in pixles.
    */
   double minimumxoffsetincrement = 40;

   /**
    * lower bounds for Y offset increment (when y value needs to be updtaed when
    * drawing a line), in pixles.
    */
   double minimumyoffsetincrement = 15;

   /** Offset from right, in pixles. */
   double rightoffset;

   /** size of color swatch in the legend, in pixles. */
   double samplecolorsize = 8;

   /**
    * Defines the general visual settings of this graph (title, legend, scale.
    * etc.)
    */
   GraphSettings settings = null;

   /** Empty space size (a place holder to visually and evenly space items). */
   int smallspace = 5;

   /** visual size of tick mark, in pixles. */
   int tickmarksize = 4;

   /** Label for the whole graph. */
   String title;

   /** Offset from top. */
   double topoffset;

   /** The X axle label. */
   String xlabel;

   /** The maximum X point in the graph. */
   double xmaximum;

   /** Minimum and maximum scale values. */
   /** The minimum X point in the graph. */
   double xminimum;

   /** Pixels per increment on the X axle. */
   double xoffsetincrement;

   /** Units per pixel on the X axle. */
   double xscale;

   /** Units per increment on the X axle. */
   double xvalueincrement;

   /** The Y axle label. */
   String ylabel;

   /** The maximum Y point in the graph. */
   double ymaximum;


   /** The minimum Y point in the graph. */
   double yminimum;

   /** Pixels per increment on the Y axle. */
   double yoffsetincrement;

   /** Units per pixel on the Y axle. */
   double yscale;

   /** Units per increment on the Y axle. */
   double yvalueincrement;

   /** The displayed objects of the items in <code>m_shape.</code> */
   protected ArrayList m_dispRects = null;

   /**
    * An array list of Object[] objects. each Object array has 3 Object
    * reference to it. Reference index 0 is a Rectangle, reference index 1 is a
    * RectWrapper
    */
   protected ArrayList m_ends = null;

   //~ Constructors ************************************************************

   /**
    * Constructs a BaseGraph Object from the inner nodes <code>shapes</code>,
    * tje ;eaves <code>leaves</code> and the visual setting in <code>settings.
    * </codE>
    *
    * @param shapes   None leaves nodes in the graph.
    * @param leaves   leave of the graph.
    * @param settings visual measurements for this graph.
    */
   public BaseGraph(ArrayList shapes, ArrayList leaves,
                    GraphSettings settings) {
      m_shapes = shapes;
      m_leaves = leaves;

      for (int i = 0, n = m_leaves.size(); i < n; i++) {
         m_leavesC.add(((RectWrapper) m_leaves.get(i)).getCluster());
      }

      this.settings = settings;

      setBackground(Color.white);

      title = settings.title;
      xlabel = settings.xaxis;
      ylabel = settings.yaxis;


      // Find interval for x data
      if ((settings.xminimum == null) || (settings.xmaximum == null)) {
         xminimum = Double.MAX_VALUE;
         xmaximum = 0;

         for (int index = 0, n = m_shapes.size(); index < n; index++) {
            RectWrapper rect = (RectWrapper) m_shapes.get(index);

            double value = (double) rect.getRect().getX();

            if (value < xminimum) {
               xminimum = value;
            }

            value += rect.getRect().getWidth();

            if (value > xmaximum) {
               xmaximum = value;
            }
         }
      } else {
         xminimum = settings.xminimum.doubleValue();
         xmaximum = settings.xmaximum.doubleValue();
      }

      // Find interval for y data
      if ((settings.yminimum == null) || (settings.ymaximum == null)) {
         yminimum = Double.MAX_VALUE;
         ymaximum = 0;

         for (int index = 0, n = m_shapes.size(); index < n; index++) {
            RectWrapper rect = (RectWrapper) m_shapes.get(index);

            double value = (double) rect.getRect().getY();

            if (value > ymaximum) {
               ymaximum = value;
            }

            value -= rect.getRect().getHeight();

            if (value < yminimum) {
               yminimum = value;
            }
         }
      } else {
         yminimum = settings.yminimum.doubleValue();
         ymaximum = settings.ymaximum.doubleValue();
      }

      this.setPreferredSize(new Dimension((int) this.getWidth(),
                                          (int) (ymaximum - yminimum) * 2));
   }

   //~ Methods *****************************************************************

   /**
    * Renders the labels of all the children in this graph.
    *
    * @param g2 2D Graphics object
    */
   private void drawClusterLabels(Graphics2D g2) {

      ArrayList ends = new ArrayList();
      Color col = g2.getColor();

      for (int i = 0, n = m_leaves.size(); i < n; i++) {
         Object[] obs = new Object[3];
         RectWrapper rw = (RectWrapper) m_leaves.get(i);
         String lbl = rw.getLabel();
         double y0scale =
            graphheight - bottomoffset -
            (rw.getRect().getY() - yminimum) / yscale;
         int stringwidth = metrics.stringWidth(lbl);
         Rectangle rect =
            new Rectangle((int) (leftoffset - stringwidth - smallspace),
                          (int) (y0scale + fontascent / 2 -
                                 metrics.getHeight()), stringwidth,
                          metrics.getHeight());
         obs[0] = rect;
         obs[1] = rw;
         ends.add(obs);

         if (m_ends != null) {
            obs = (Object[]) m_ends.get(i);

            if (obs[2] != null) {
               g2.setColor(Color.red);
            }
         }

         g2.drawString(lbl, (int) (leftoffset - stringwidth - smallspace),
                       (int) (y0scale + fontascent / 2));
         g2.setColor(col);
      } // end for

      m_ends = ends;
   } // end method drawClusterLabels

   /**
    * Draws a rectangle that is anon leaf in this graph.
    *
    * @param g2   2D graphics handler
    * @param poly The shape to be drawn.
    */
   public abstract void drawDataSet(Graphics2D g2, RectWrapper poly);

   /**
    * Draws the axis of this graph.
    *
    * @param g2 2D graphics handler
    */
   public void drawAxis(Graphics2D g2) {
      g2.draw(new Line2D.Double(leftoffset, topoffset, leftoffset, graphheight -
                                bottomoffset));
      g2.draw(new Line2D.Double(leftoffset, graphheight - bottomoffset,
                                graphwidth - rightoffset,
                                graphheight - bottomoffset));
   }

   /**
    * Renders the labels of th axis.
    *
    * @param g2 2D graphics handler
    */
   public void drawAxisLabels(Graphics2D g2) {
      int stringwidth;
      double xvalue;
      double yvalue;

      // x axis
      stringwidth = metrics.stringWidth(xlabel);
      xvalue = (graphwidth - stringwidth) / 2;
      yvalue =
         graphheight -
         (bottomoffset - (bottomoffset - legendtopoffset - fontheight) / 2) +
         2 * largespace;
      g2.drawString(xlabel, (int) xvalue, (int) yvalue);

      // y axis
      AffineTransform transform = g2.getTransform();

      stringwidth = metrics.stringWidth(ylabel);
      xvalue = (leftoffset - fontascent - smallspace) / 2;
      yvalue = (graphheight + stringwidth) / 2;

      AffineTransform rotate =
         AffineTransform.getRotateInstance(Math.toRadians(-90), xvalue, yvalue);
      g2.transform(rotate);
      g2.drawString(ylabel, (int) xvalue, (int) yvalue);

      g2.setTransform(transform);
   } // end method drawAxisLabels

   /**
    * Draws the grid of this graph.
    *
    * @param g2 2D graphics handler
    */
   public void drawGrid(Graphics2D g2) {
      Color previouscolor = g2.getColor();
      g2.setColor(Color.gray);

      // x axis
      double x = leftoffset + xoffsetincrement;

      for (int index = 0; index < gridsize - 1; index++) {
         g2.draw(new Line2D.Double(x, graphheight - bottomoffset, x,
                                   topoffset));
         x += xoffsetincrement;
      }

      // y axis
      double y = topoffset + yoffsetincrement;

      for (int index = 0; index < gridsize - 1; index++) {
         g2.draw(new Line2D.Double(leftoffset, y, graphwidth - rightoffset, y));
         y += yoffsetincrement;
      }

      g2.setColor(previouscolor);
   }

   /**
    * Renders the legend of this graph.
    *
    * @param g2 2D graphics handler
    */
   public void drawLegend(Graphics2D g2) {
      Color previouscolor = g2.getColor();

      double x = legendleftoffset;
      double y = graphheight - legendtopoffset;

      g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

      x += smallspace;
      y += fontheight - samplecolorsize;

      RectWrapper pw = null;

      for (int index = 0, n = m_shapes.size(); index < n; index++) {
         pw = (RectWrapper) m_shapes.get(index);
         g2.setColor(pw.getColor());
         g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));
         y += fontheight;
      }

      g2.setColor(previouscolor);

      x = legendleftoffset;
      y = graphheight - legendtopoffset;

      x += 2 * smallspace + samplecolorsize;
      y += fontheight;

      for (int index = 0, n = m_shapes.size(); index < n; index++) {
         pw = (RectWrapper) m_shapes.get(index);

         ArrayList alist = new ArrayList();
         alist.add(pw.getCluster());
         g2.drawString( "?", (int) x, (int) y);
         y += fontheight;
      }
   } // end method drawLegend


   /**
    * Renders the scale of the graph.
    *
    * @param g2 2D graphics handler
    */
   public void drawScale(Graphics2D g2) {
      NumberFormat numberformat = NumberFormat.getInstance();
      numberformat.setMaximumFractionDigits(3);

      // x axis
      double xvalue = xminimum;
      double x = leftoffset;

      for (int index = 0; index < gridsize; index++) {
         String string = numberformat.format(xvalue);
         int stringwidth = metrics.stringWidth(string);
         g2.drawString(string, (int) (x - stringwidth / 2),
                       (int) (graphheight - bottomoffset + fontheight));
         x += xoffsetincrement;
         xvalue += xvalueincrement;
      }

      // y axis
      double yvalue = yminimum;
      double y = graphheight - bottomoffset;

      for (int index = 0; index < gridsize; index++) {
         String string = numberformat.format(yvalue);
         int stringwidth = metrics.stringWidth(string);
         g2.drawString(string, (int) (leftoffset - stringwidth - smallspace),
                       (int) (y + fontascent / 2));
         y -= yoffsetincrement;
         yvalue += yvalueincrement;
      }
   } // end method drawScale

   /**
    * Renders the tick marks lines.
    *
    * @param g2 2D graphics handler
    */
   public void drawTickMarks(Graphics2D g2) {

      // x axis
      double x = leftoffset;

      for (int index = 0; index < gridsize; index++) {
         g2.draw(new Line2D.Double(x, graphheight - bottomoffset - tickmarksize,
                                   x, graphheight - bottomoffset +
                                   tickmarksize));
         x += xoffsetincrement;
      }

      // y axis
      double y = topoffset + yoffsetincrement;

      for (int index = 0; index < gridsize; index++) {
         g2.draw(new Line2D.Double(leftoffset - tickmarksize, y, leftoffset +
                                   tickmarksize, y));
         y += yoffsetincrement;
      }
   }

   /**
    * Renders the title of the graph.
    *
    * @param g2 2D graphics handler
    */
   public void drawTitle(Graphics2D g2) {
      int stringwidth = metrics.stringWidth(title);
      double x = (graphwidth - stringwidth) / 2;
      double y = (topoffset) / 2;

      g2.drawString(title, (int) x, (int) y);
   }

   /**
    * Initializes the offsets variables.
    */
   public void initOffsets() {

      // Offsets of axis
      leftoffset = 65 + 120;
      rightoffset = 65;

      // Offset of legend
      if (!settings.displaylegend) {
         legendheight = 0;
      } else {
         String[] names = new String[m_shapes.size()];

         RectWrapper pw = null;
         ArrayList alist = new ArrayList();
         alist.add(((RectWrapper) m_shapes.get(0)).getCluster());

         // names[0] = ThemeViewController.genThemeString(alist,2);
         names[0] = "";
         legendwidth = metrics.stringWidth(names[0]);

         for (int index = 1, n = m_shapes.size(); index < n; index++) {
            alist.clear();
            pw = (RectWrapper) m_shapes.get(index);
            alist.add(pw.getCluster());

            // names[index] = ThemeViewController.genThemeString(alist,2);
            names[index] = "";

            int stringwidth = metrics.stringWidth(names[index]);

            if (stringwidth > legendwidth) {
               legendwidth = stringwidth;
            }
         }

         legendwidth += 4 * smallspace + samplecolorsize;
         legendheight =
            (m_shapes.size() * fontheight) + (fontheight - samplecolorsize);

         legendleftoffset = leftoffset;
         legendtopoffset = legendheight + 2 * largespace;
      } // end if

      // Offsets of axis
      bottomoffset = 60 + legendtopoffset;
      topoffset = 60;
   } // end method initOffsets

   /**
    * Renders the graph.
    *
    * @param g Graphics handler
    */
   public void paintComponent(Graphics g) {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);

      font = g2.getFont();
      metrics = getFontMetrics(font);
      fontheight = metrics.getHeight();
      fontascent = metrics.getAscent();

      graphwidth = getWidth();
      graphheight = getHeight();

      // Determine offsets
      initOffsets();

      resize();

      xvalueincrement = (xmaximum - xminimum) / gridsize;
      yvalueincrement = (ymaximum - yminimum) / gridsize;

      xscale = (xmaximum - xminimum) / (graphwidth - leftoffset - rightoffset);
      yscale = (ymaximum - yminimum) / (graphheight - topoffset - bottomoffset);

      drawAxis(g2);

      if (settings.displaylegend) {
         drawLegend(g2);
      }

      if (settings.displaygrid) {
         drawGrid(g2);
      }

      if (settings.displaytickmarks) {
         drawTickMarks(g2);
      }

      if (settings.displayscale) {
         drawScale(g2);
      }

      if (settings.displayaxislabels) {
         drawAxisLabels(g2);
      }

      if (settings.displaytitle) {
         drawTitle(g2);
      }

      drawClusterLabels(g2);

      m_dispRects = new ArrayList();

      for (int index = 0, n = m_shapes.size(); index < n; index++) {
         drawDataSet(g2, (RectWrapper) m_shapes.get(index));
      }
   } // end method paintComponent

   /**
    * Resize scale.
    */
   public void resize() {

      // gridsize = settings.gridsize;
      gridsize = (int) Math.round((xmaximum - xminimum) / 30);

      // x axis
      xoffsetincrement = (graphwidth - leftoffset - rightoffset) / gridsize;

      while ((xoffsetincrement < minimumxoffsetincrement) && (gridsize > 0)) {
         gridsize = gridsize / 2;
         xoffsetincrement = (graphwidth - leftoffset - rightoffset) / gridsize;
      }

      // y and x axis
      yoffsetincrement = (graphheight - topoffset - bottomoffset) / gridsize;

      while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
         gridsize = gridsize / 2;
         yoffsetincrement = (graphheight - topoffset - bottomoffset) / gridsize;
         xoffsetincrement = (graphwidth - leftoffset - rightoffset) / gridsize;
      }
   }

} // end class BaseGraph
