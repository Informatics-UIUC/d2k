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

import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Sparse;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.gui.DisposeOnCloseListener;

import javax.swing.event.MouseInputListener;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/**
 * <p>Title: RectGraph</p>
 *
 * <p>Description:</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class RectGraph extends BaseGraph implements MouseInputListener {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 3392836325903817185L;


   /**
    * Display mode code - meaning the cohesion level of the clusters is
    * displayed.
    */
   static public final int DisplayCohesion = 0;

   /**
    * Display mode code - size of the clusters is displayed as strings next to
    * the nodes.
    */
   static public final int DisplayClusterSize = 1;

   /** Display mode code - size of the clusters is displayed in coded colors. */
   static public final int DisplaySizeAsColor = 2;

   //~ Instance fields *********************************************************

   /** Color for high values (size/cohesion). */
   private Color _highColor = Color.green;

   /** Color for low values (size/cohesion). */
   private Color _lowColor = Color.red;

   /**
    * Code for how to display the graph's nodes - whether to display the
    * cohesion level of the clusters or the size.
    */
   private int m_displayMode = DisplayCohesion;

   /** The GUI container displaying this graph. */
   private DendogramPanel m_parent = null;

   //~ Constructors ************************************************************

   /**
    * Instantiates a graph with inner nodes <code>shapes</code>, childless nodes
    * <code>leaves</code> general settings defined by <code>settings</cdoe> and
    * the containing panel <code>parent.</code></code>
    *
    * @param shapes   ArrayList The inner nodes in the clusters tree
    * @param leaves   ArrayList The child-less nodes in the clusters tree
    * @param settings GraphSettings General visual settings for displaying the
    *                 graph
    * @param parent   DendogramPanel The containing visual panel to display this
    *                 graph
    */
   public RectGraph(ArrayList shapes, ArrayList leaves, GraphSettings settings,
                    DendogramPanel parent) {
      super(shapes, leaves, settings);
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      m_parent = parent;
   }

   //~ Methods *****************************************************************

   /**
    * Draws a rectangle that is a non leaf in this graph.
    *
    * @param g2   2D graphics handler
    * @param rect The shape to be drawn.
    */
   public void drawDataSet(Graphics2D g2, RectWrapper rect) {
      drawRect(g2, rect.getColor(), rect.getRect());
   }


   /**
    * Draw two data points and line.
    *
    * @param g2    Graphics2D 2D graphics handler
    * @param color Color The color to be used
    * @param rect  Rectangle The rectangle from which to get the X and Y points
    *              for the new line
    */
   public void drawRect(Graphics2D g2, Color color, Rectangle rect) {


      Color previouscolor = g2.getColor();

      double x0scale = (rect.getX() - xminimum) / xscale + leftoffset;
      double y0scale =
         graphheight - bottomoffset - (rect.getY() - yminimum) / yscale;

      double newWidth = 0;

      if (getDisplayMode() == RectGraph.DisplayCohesion) {
         newWidth = rect.getWidth() / xscale;
      } else if (getDisplayMode() == RectGraph.DisplaySizeAsColor) {
         newWidth = rect.getWidth() / xscale;
      } else if (getDisplayMode() == RectGraph.DisplayClusterSize) {
         TableCluster c =
            ((RectWrapper) m_shapes.get(m_dispRects.size())).getCluster();
         TableCluster c2 = ((RectWrapper) m_shapes.get(0)).getCluster();
         newWidth =
            (xmaximum - xminimum) *
               ((double) c.getSize() / (double) c2.getSize()) / xscale;
         // newWidth = (c.getSize() / xscale);
      }

      double newHeight = rect.getHeight() / yscale;

      Rectangle r =
         new Rectangle((int) x0scale, (int) y0scale, (int) newWidth,
                       (int) newHeight);
      m_dispRects.add(r);

      int redH =
         (_highColor.getRed() >= _lowColor.getRed()) ? _highColor.getRed()
                                                     : _lowColor.getRed();
      int redL =
         (_highColor.getRed() >= _lowColor.getRed()) ? _lowColor.getRed()
                                                     : _highColor.getRed();

      int greenH =
         (_highColor.getGreen() >= _lowColor.getGreen()) ? _highColor
                                                            .getGreen()
                                                         : _lowColor.getGreen();
      int greenL =
         (_highColor.getGreen() >= _lowColor.getGreen())
         ? _lowColor.getGreen() : _highColor.getGreen();

      int blueH =
         (_highColor.getBlue() >= _lowColor.getBlue()) ? _highColor.getBlue()
                                                       : _lowColor.getBlue();
      int blueL =
         (_highColor.getBlue() >= _lowColor.getBlue()) ? _lowColor.getBlue()
                                                       : _highColor.getBlue();

      int redR = redH - redL;
      int greenR = greenH - greenL;
      int blueR = blueH - blueL;

      if (getDisplayMode() == RectGraph.DisplayCohesion) {

         if (color == Color.orange) {
            g2.setColor(color);
         } else {
            long red =
               Math.round(redR *
                             ((double) rect.getWidth() / (double) xmaximum));

            if (_highColor.getRed() >= _lowColor.getRed()) {
               red += _lowColor.getRed();
            } else {
               red = _lowColor.getRed() - red;
            }

            if (red < redL) {
               red = redL;
            } else if (red > redH) {
               red = redH;
            }

            long green =
               Math.round(greenR *
                             ((double) rect.getWidth() / (double) xmaximum));

            if (_highColor.getGreen() >= _lowColor.getGreen()) {
               green += _lowColor.getGreen();
            } else {
               green = _lowColor.getGreen() - green;
            }

            if (green < greenL) {
               green = greenL;
            } else if (green > greenH) {
               green = greenH;
            }

            long blue =
               Math.round(blueR *
                             ((double) rect.getWidth() / (double) xmaximum));

            if (_highColor.getBlue() >= _lowColor.getBlue()) {
               blue += _lowColor.getBlue();
            } else {
               blue = _lowColor.getBlue() - blue;
            }

            if (blue < blueL) {
               blue = blueL;
            } else if (blue > blueH) {
               blue = blueH;
            }

            // g2.setColor(new Color(255-(int)red, (int)red, 0));
            g2.setColor(new Color((int) red, (int) green, (int) blue));
         } // end if
      } else if (getDisplayMode() == RectGraph.DisplaySizeAsColor) {
         TableCluster c1 =
            ((RectWrapper) m_shapes.get(m_dispRects.size() - 1)).getCluster();
         TableCluster c2 = ((RectWrapper) m_shapes.get(0)).getCluster();
         long red =
            Math.round(redR * ((double) c1.getSize() / (double) c2.getSize()));

         if (_highColor.getRed() >= _lowColor.getRed()) {
            red += _lowColor.getRed();
         } else {
            red = _lowColor.getRed() - red;
         }

         if (red < redL) {
            red = redL;
         } else if (red > redH) {
            red = redH;
         }

         long green =
            Math.round(greenR *
                          ((double) c1.getSize() / (double) c2.getSize()));

         if (_highColor.getGreen() >= _lowColor.getGreen()) {
            green += _lowColor.getGreen();
         } else {
            green = _lowColor.getGreen() - green;
         }

         if (green < greenL) {
            green = greenL;
         } else if (green > greenH) {
            green = greenH;
         }

         long blue =
            Math.round(blueR * ((double) c1.getSize() / (double) c2.getSize()));

         if (_highColor.getBlue() >= _lowColor.getBlue()) {
            blue += _lowColor.getBlue();
         } else {
            blue = _lowColor.getBlue() - blue;
         }

         if (blue < blueL) {
            blue = blueL;
         } else if (blue > blueH) {
            blue = blueH;
         }

         // g2.setColor(new Color(255-(int)red, (int)red, 0));
         g2.setColor(new Color((int) red, (int) green, (int) blue));
      } else if (getDisplayMode() == RectGraph.DisplayClusterSize) {
         long red =
            Math.round(redR * ((double) rect.getWidth() / (double) xmaximum));

         if (_highColor.getRed() >= _lowColor.getRed()) {
            red += _lowColor.getRed();
         } else {
            red = _lowColor.getRed() - red;
         }

         if (red < redL) {
            red = redL;
         } else if (red > redH) {
            red = redH;
         }

         long green =
            Math.round(greenR * ((double) rect.getWidth() / (double) xmaximum));

         if (_highColor.getGreen() >= _lowColor.getGreen()) {
            green += _lowColor.getGreen();
         } else {
            green = _lowColor.getGreen() - green;
         }

         if (green < greenL) {
            green = greenL;
         } else if (green > greenH) {
            green = greenH;
         }

         long blue =
            Math.round(blueR * ((double) rect.getWidth() / (double) xmaximum));

         if (_highColor.getBlue() >= _lowColor.getBlue()) {
            blue += _lowColor.getBlue();
         } else {
            blue = _lowColor.getBlue() - blue;
         }

         if (blue < blueL) {
            blue = blueL;
         } else if (blue > blueH) {
            blue = blueH;
         }

         // g2.setColor(new Color(255-(int)red, (int)red, 0));
         g2.setColor(new Color((int) red, (int) green, (int) blue));
      } // end if-else

      g2.fill(r);

      g2.setColor(Color.black);

      g2.drawLine((int) x0scale, (int) y0scale, (int) (x0scale + newWidth),
                  (int) y0scale);
      g2.drawLine((int) x0scale, (int) y0scale + 1, (int) (x0scale + newWidth),
                  (int) y0scale + 1);
      g2.drawLine((int) x0scale, (int) y0scale - 1, (int) (x0scale + newWidth),
                  (int) y0scale - 1);

      g2.drawLine((int) (x0scale + newWidth), (int) y0scale,
                  (int) (x0scale + newWidth), (int) (y0scale + newHeight));
      g2.drawLine((int) (x0scale + newWidth + 1), (int) y0scale,
                  (int) (x0scale + newWidth + 1), (int) (y0scale + newHeight));
      g2.drawLine((int) (x0scale + newWidth - 1), (int) y0scale,
                  (int) (x0scale + newWidth - 1), (int) (y0scale + newHeight));

      g2.drawLine((int) x0scale, (int) (y0scale + newHeight),
                  (int) (x0scale + newWidth), (int) (y0scale + newHeight));
      g2.drawLine((int) x0scale, (int) (y0scale + newHeight + 1),
                  (int) (x0scale + newWidth), (int) (y0scale + newHeight + 1));
      g2.drawLine((int) x0scale, (int) (y0scale + newHeight - 1),
                  (int) (x0scale + newWidth), (int) (y0scale + newHeight - 1));

      g2.setColor(previouscolor);
   } // end method drawRect

   /**
    * Returns the display mode code.
    *
    * @return int The display mode for this graph
    */
   public int getDisplayMode() { return m_displayMode; }

   /**
    * Returns the color to use for high values in the graph.
    *
    * @return Color The color to use for high values in the graph
    */
   public Color getHighColor() { return _highColor; }

   /**
    * Returns the color to use for low values in the graph.
    *
    * @return Color The color to use for low values in the graph
    */
   public Color getLowColor() { return _lowColor; }


   /**
    * Returns the index of a displayed inner node that its location contains the
    * point (<code>x,y</code>).
    *
    * @param  x int X value of the inspected point
    * @param  y int Y value of the inspected point
    *
    * @return int The index of a displayed node that its location contains the
    *         point (<code>x,y</code>) or -1 if no such rectangle is found.
    */
   public int inCell(int x, int y) {
      int retval = -1;

      for (int i = 0, n = m_dispRects.size(); i < n; i++) {

         if (((Rectangle) m_dispRects.get(i)).contains(x, y)) {
            retval = i;
         }
      }

      return retval;
   }

   /**
    * Returns the index of a displayed leaf that its location contains the point
    * (<code>x,y</code>)
    *
    * @param  x int X value of the inspected point
    * @param  y int Y value of the inspected point
    *
    * @return int The index of a displayed leaf that its location contains the
    *         point (<code>x,y</code>)
    */
   public int inFont(int x, int y) {
      int retval = -1;

      for (int i = 0, n = m_ends.size(); i < n; i++) {

         if (((Rectangle) ((Object[]) m_ends.get(i))[0]).contains(x, y)) {
            retval = i;
         }
      }

      return retval;
   }

   /**
    * Handles mouse clicked events that are double clicks.
    *
    * @param evt MouseEvent A mouse clicked event.
    */
   public void mouseClicked(MouseEvent evt) {

      if ((evt.getClickCount() >= 2) && (evt.isShiftDown())) {

         if (this.isEnabled()) {
            int i = inCell(evt.getX(), evt.getY());

            if (i >= 0) {
               TableCluster c = ((RectWrapper) m_shapes.get(i)).getCluster();
               Table tab = null;

               if (tab instanceof Sparse) {

                  // changed from getSubsetByReference -- DDS
                  tab =
                     ((MutableTable) c.getTable()).getSubset(c
                                                                .getMemberIndices());
               } else {
                  tab = c.getTable().getSubset(c.getMemberIndices());
               }

               JD2KFrame frame =
                  new JD2KFrame("Values for Cluster ID " + c.getClusterLabel());
               TableMatrix vtm = new TableMatrix(tab);
               frame.getContentPane().add(vtm);
               frame.addWindowListener(new DisposeOnCloseListener(frame));
               frame.pack();
               frame.show();
            }

            int j = inFont(evt.getX(), evt.getY());

            if (j >= 0) {
               Object[] obs = (Object[]) m_ends.get(j);
               TableCluster c = ((RectWrapper) obs[1]).getCluster();
               JD2KFrame frame =
                  new JD2KFrame("Values for Cluster ID " + c.getClusterLabel());
               Table tab = null;

               if (tab instanceof Sparse) {

                  // changed from getSubsetByReference -- DDS
                  tab =
                     ((MutableTable) c.getTable()).getSubset(c
                                                                .getMemberIndices());
               } else {
                  tab = c.getTable().getSubset(c.getMemberIndices());
               }

               TableMatrix vtm = new TableMatrix(tab);
               frame.getContentPane().add(vtm);
               frame.addWindowListener(new DisposeOnCloseListener(frame));
               frame.pack();
               frame.show();
            }
         } // end if
      } else if (
                 (evt.getClickCount() >= 2) &&
                    (!evt.isShiftDown()) &&
                    (!evt.isControlDown())) {

         if (this.isEnabled()) {

            int j = inFont(evt.getX(), evt.getY());

            if (j >= 0) {
               return;
            }

            int i = inCell(evt.getX(), evt.getY());

            if (i >= 0) {
               TableCluster tc = ((RectWrapper) m_shapes.get(i)).getCluster();
               m_parent.resetPanel(tc);
            }
         }
      } else if ((evt.getClickCount() >= 2) && (evt.isControlDown())) {

         if (this.isEnabled()) {
            TableCluster tc = null;
            int j = inFont(evt.getX(), evt.getY());
            int i = inCell(evt.getX(), evt.getY());

            if (j >= 0) {
               Object[] obs = (Object[]) m_ends.get(j);
               tc = ((RectWrapper) obs[1]).getCluster();
            }

            if (i >= 0) {
               tc = ((RectWrapper) m_shapes.get(i)).getCluster();
            }

            if (tc != null) {
               Table tab = tc.getTable();

               int[] ifeatures = null;

               if (tab instanceof ExampleTable) {
                  ifeatures = ((ExampleTable) tab).getInputFeatures();
               } else {
                  ifeatures = new int[tab.getNumColumns()];

                  for (int a = 0, b = tab.getNumColumns(); a < b; a++) {
                     ifeatures[i] = i;
                  }
               }


               double[] vals = tc.getCentroid();
               int[] ind = null;

               if (tc.getSparse()) {
                  vals = tc.getSparseCentroidValues();
                  ind = tc.getSparseCentroidInd();
               }

               Column[] cols = new DoubleColumn[vals.length];

               for (int a = 0, b = vals.length; a < b; a++) {
                  double[] dval = new double[1];
                  dval[0] = vals[a];
                  cols[a] = new DoubleColumn(dval);

                  if (tc.getSparse()) {

//              if (tc.getTable() instanceof
// ncsa.d2k.modules.t2k.datatype.DocumentTermTable){
// cols[a].setLabel(((ncsa.d2k.modules.t2k.datatype.DocumentTermTable)tc.getTable()).getTermData(ind[a]).getImage());
//             } else {
                     cols[a].setLabel(tab.getColumnLabel(ind[a]));
// }
                  } else {
                     cols[a].setLabel(tab.getColumnLabel(ifeatures[a]));
                  }
               }

               Table newtab = new MutableTableImpl(cols);

               JD2KFrame frame =
                  new JD2KFrame("Centroid values for Cluster ID " +
                                tc.getClusterLabel());
               TableMatrix vtm = new TableMatrix(newtab);
               frame.getContentPane().add(vtm);
               frame.addWindowListener(new DisposeOnCloseListener(frame));
               frame.pack();
               frame.show();
            } // end if
         } // end if
      } // end if-else
   } // end method mouseClicked

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse dragged event
    */
   public void mouseDragged(MouseEvent evt) { }

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse entered event
    */
   public void mouseEntered(MouseEvent evt) { }

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse exited event
    */
   public void mouseExited(MouseEvent evt) { }

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse moved event
    */
   public void mouseMoved(MouseEvent evt) { }

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse pressed event
    */
   public void mousePressed(MouseEvent evt) { }

   /**
    * Interface implementation.
    *
    * @param evt MouseEvent A mouse released event
    */
   public void mouseReleased(MouseEvent evt) { }

   /**
    * Sets the display mode code to be <code>mode</code> Also invokes <code>
    * repain.</code>
    *
    * @param mode int The dispaly mode code
    */
   public void setDisplayMode(int mode) {
      m_displayMode = mode;
      repaint();
   }


   /**
    * Sets the color for high values to be <code>c.</code>
    *
    * @param c Color The color to use for high values in the graph
    */
   public void setHighColor(Color c) {
      _highColor = c;
      repaint();
   }

   /**
    * Sets the color for low values to be <code>c.</code>
    *
    * @param c Color The color to use for low values in the graph
    */
   public void setLowColor(Color c) {
      _lowColor = c;
      repaint();
   }

} // end class RectGraph
