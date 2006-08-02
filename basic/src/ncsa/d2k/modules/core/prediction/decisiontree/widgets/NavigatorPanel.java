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
package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * Displays a scaled view of decision tree from tree scroll pane
 * Draws a navigator that shows how much of tree is visible Dimensions of
 * navigator based on scale of tree scroll pane.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public final class NavigatorPanel extends JPanel {

   //~ Instance fields *********************************************************

   /** pointer to the navigator */
   Navigator navigator;

   //~ Constructors ************************************************************

   /**
    * Creates a new NavigatorPanel object.
    *
    * @param model      Description of parameter model.
    * @param scrollpane Description of parameter scrollpane.
    */
   public NavigatorPanel(ViewableDTModel model, TreeScrollPane scrollpane) {
      navigator = new Navigator(model, scrollpane);

      setBackground(DecisionTreeScheme.borderbackgroundcolor);
      add(navigator);
   }

   //~ Methods *****************************************************************

   /**
    * Rebuild the tree in the navigator.
    */
   public void rebuildTree() { navigator.rebuildTree(); }

   //~ Inner Classes ***********************************************************

   class Navigator extends JPanel implements MouseListener, MouseMotionListener,
                                             ChangeListener {

      /** Decision tree model */
      ViewableDTModel dmodel;

       /** drawable */
      boolean drawable = true;

      /** Decision tree root */
      ViewableDTNode droot;

      /** Width of decision tree */
      double dwidth;
       /** height of decision tree */
      double dheight;

       /** offscreen buffer */
      BufferedImage image;

      /** Maximum depth */
      int mdepth;

      /** Width of navigator */
      double nwidth;
       /** Height of navigator */
      double nheight;


      /** Scaled tree root */
      ScaledNode sroot;

       /** did state change? */
      boolean statechanged;

      /** Scaled width of decision tree */
      double swidth = 200;
        /** scaled height of decision tree */
      double sheight;

       /** the tree scroll pane */
      TreeScrollPane treescrollpane;
       /** the viewport */
      JViewport viewport;

      // Offsets of navigator
      double x;
      double y;
      double lastx;
      double lasty;

       /** scale in x direction */
      double xscale;
       /** scale in y direction */
      double yscale;

       /**
        * Constructor
        * @param model
        * @param scrollpane
        */
      public Navigator(ViewableDTModel model, TreeScrollPane scrollpane) {
         dmodel = model;
         droot = dmodel.getViewableRoot();
         sroot = new ScaledNode(dmodel, droot, null);

         treescrollpane = scrollpane;
         viewport = treescrollpane.getViewport();

         findMaximumDepth(droot);
         buildTree(droot, sroot);

         sroot.x = sroot.findLeftSubtreeWidth();
         sroot.y = sroot.yspace;

         findTreeOffsets(sroot);

         dwidth = sroot.findSubtreeWidth();
         dheight = (sroot.yspace + sroot.height) * (mdepth + 1) + sroot.yspace;

         findSize();

         setOpaque(true);

         if (drawable) {
            addMouseListener(this);
            addMouseMotionListener(this);
            viewport.addChangeListener(this);
            image =
               new BufferedImage((int) swidth, (int) sheight,
                                 BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = image.createGraphics();
            paintBuffer(g2);
         }
      }

       /**
        * build the tree
        * @param dnode  decision tree node
        * @param snode  scaled node
        */
      public void buildTree(ViewableDTNode dnode, ScaledNode snode) {

         for (int index = 0; index < dnode.getNumChildren(); index++) {
            ViewableDTNode dchild = dnode.getViewableChild(index);
            ScaledNode schild =
               new ScaledNode(dmodel, dchild, snode,
                              dnode.getBranchLabel(index));
            snode.addChild(schild);
            buildTree(dchild, schild);
         }
      }

       /**
        * Draw a line from (x1, y1) to (x2, y2)
        * @param g2
        * @param x1
        * @param y1
        * @param x2
        * @param y2
        */
      public void drawLine(Graphics2D g2, double x1, double y1, double x2,
                           double y2) {
         int linestroke = 1;

         g2.setStroke(new BasicStroke(linestroke));
         g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
         g2.draw(new Line2D.Double(x1, y1, x2, y2));
      }

       /**
        * Draw the tree in this navigator
        * @param g2 graphics context
        * @param snode scaled node
        */
      public void drawTree(Graphics2D g2, ScaledNode snode) {
         snode.drawScaledNode(g2);

         for (int index = 0; index < snode.getNumChildren(); index++) {
            ScaledNode schild = (ScaledNode) snode.getChild(index);

            double x1 = snode.x;
            double y1 = snode.y + snode.height;
            double x2 = schild.x;
            double y2 = schild.y;

            drawLine(g2, x1, y1, x2, y2);

            drawTree(g2, schild);
         }
      }

       /**
        * find the maximum depth below the given node
        * @param dnode maximum depth
        */
      public void findMaximumDepth(ViewableDTNode dnode) {
         int depth = dnode.getDepth();

         if (depth > mdepth) {
            mdepth = depth;
         }

         for (int index = 0; index < dnode.getNumChildren(); index++) {
            ViewableDTNode dchild = dnode.getViewableChild(index);
            findMaximumDepth(dchild);
         }
      }

       /**
        * Determine size and position of navigator
        */
      public void findSize() {
         double scale = treescrollpane.getScale();

         sheight = swidth * dheight / dwidth;

         if (sheight < 1) {
            drawable = false;
         }

         xscale = swidth / (scale * dwidth);
         yscale = sheight / (scale * dheight);

         Point position = viewport.getViewPosition();
         Dimension vpdimension = viewport.getExtentSize();

         double vpwidth = vpdimension.getWidth();
         nwidth = swidth * vpwidth / (scale * dwidth);

         if (nwidth > swidth) {
            nwidth = swidth;
         }

         x = xscale * position.x;

         double vpheight = vpdimension.getHeight();
         nheight = sheight * vpheight / (scale * dheight);

         if (nheight > sheight) {
            nheight = sheight;
         }

         y = yscale * position.y;
      } // end method findSize

       /**
        * find the offets for the tree
        * @param snode
        */
      public void findTreeOffsets(ScaledNode snode) {
         snode.findOffsets();

         for (int index = 0; index < snode.getNumChildren(); index++) {
            ScaledNode schild = (ScaledNode) snode.getChild(index);
            findTreeOffsets(schild);
         }
      }

       /**
        * If the minimum size has been set to a non-<code>null</code> value
        * just returns it.  If the UI delegate's <code>getMinimumSize</code>
        * method returns a non-<code>null</code> value then return that; otherwise
        * defer to the component's layout manager.
        *
        * @return the value of the <code>minimumSize</code> property
        * @see #setMinimumSize
        * @see javax.swing.plaf.ComponentUI
        */
       public Dimension getMinimumSize() {
           return getPreferredSize();
       }

       /**
        * If the <code>preferredSize</code> has been set to a
        * non-<code>null</code> value just returns it.
        * If the UI delegate's <code>getPreferredSize</code>
        * method returns a non <code>null</code> value then return that;
        * otherwise defer to the component's layout manager.
        *
        * @return the value of the <code>preferredSize</code> property
        * @see #setPreferredSize
        * @see javax.swing.plaf.ComponentUI
        */
       public Dimension getPreferredSize() {
           return new Dimension((int) swidth, (int) sheight);
       }

       /**
        * Invoked when the mouse button has been clicked (pressed
        * and released) on a component.
        */
       public void mouseClicked(MouseEvent event) {
       }

       /**
        * Invoked when a mouse button is pressed on a component and then
        * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
        * delivered to the component where the drag originated until the
        * mouse button is released (regardless of whether the mouse position
        * is within the bounds of the component).
        * <p/>
        * Due to platform-dependent Drag&Drop implementations,
        * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
        * Drag&Drop operation.
        */
       public void mouseDragged(MouseEvent event) {
           int x1 = event.getX();
           int y1 = event.getY();

           double xchange = x1 - lastx;
           double ychange = y1 - lasty;

           x += xchange;
           y += ychange;

           if (x < 0) {
               x = 0;
           }

           if (y < 0) {
               y = 0;
           }

           if (x + nwidth > swidth) {
               x = swidth - nwidth;
           }

           if (y + nheight > sheight) {
               y = sheight - nheight;
           }

           statechanged = false;

           double scale = treescrollpane.getScale();
           xscale = swidth / (scale * dwidth);
           yscale = sheight / (scale * dheight);
           treescrollpane.scroll((int) (x / xscale), (int) (y / yscale));

           lastx = x1;
           lasty = y1;

           repaint();
       } // end method mouseDragged

       /**
        * Invoked when the mouse enters a component.
        */
       public void mouseEntered(MouseEvent event) {
       }

       /**
        * Invoked when the mouse exits a component.
        */
       public void mouseExited(MouseEvent event) {
       }

       /**
        * Invoked when the mouse cursor has been moved onto a component
        * but no buttons have been pushed.
        */
       public void mouseMoved(MouseEvent event) {
       }

       /**
        * Invoked when a mouse button has been pressed on a component.
        */
       public void mousePressed(MouseEvent event) {
           lastx = event.getX();
           lasty = event.getY();
       }

       /**
        * Invoked when a mouse button has been released on a component.
        */
       public void mouseReleased(MouseEvent event) {
       }

      public void paintBuffer(Graphics2D g2) {
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setPaint(DecisionTreeScheme.borderbackgroundcolor);
         g2.fill(new Rectangle((int) dwidth, (int) dheight));

         AffineTransform transform = g2.getTransform();
         AffineTransform sinstance =
            AffineTransform.getScaleInstance(swidth / dwidth, swidth / dwidth);
         g2.transform(sinstance);

         drawTree(g2, sroot);

         g2.setTransform(transform);
      }

       /**
        * Calls the UI delegate's paint method, if the UI delegate
        * is non-<code>null</code>.  We pass the delegate a copy of the
        * <code>Graphics</code> object to protect the rest of the
        * paint code from irrevocable changes
        * (for example, <code>Graphics.translate</code>).
        * <p/>
        * If you override this in a subclass you should not make permanent
        * changes to the passed in <code>Graphics</code>. For example, you
        * should not alter the clip <code>Rectangle</code> or modify the
        * transform. If you need to do these operations you may find it
        * easier to create a new <code>Graphics</code> from the passed in
        * <code>Graphics</code> and manipulate it. Further, if you do not
        * invoker super's implementation you must honor the opaque property,
        * that is
        * if this component is opaque, you must completely fill in the background
        * in a non-opaque color. If you do not honor the opaque property you
        * will likely see visual artifacts.
        *
        * @param g the <code>Graphics</code> object to protect
        * @see #paint
        * @see javax.swing.plaf.ComponentUI
        */
       public void paintComponent(Graphics g) {
           super.paintComponent(g);

           if (drawable) {
               Graphics2D g2 = (Graphics2D) g;
               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);

               g2.drawImage(image, 0, 0, null);

               g2.setColor(DecisionTreeScheme.viewercolor);
               g2.setStroke(new BasicStroke(1));
               g2.draw(new Rectangle2D.Double(x, y, nwidth - 1, nheight - 1));
           }
       }

       /**
        *
        */
      public void rebuildTree() {
         sroot = new ScaledNode(dmodel, droot, null);

         findMaximumDepth(droot);
         buildTree(droot, sroot);

         sroot.x = sroot.findLeftSubtreeWidth();
         sroot.y = sroot.yspace;

         findTreeOffsets(sroot);

         dwidth = sroot.findSubtreeWidth();
         dheight = (sroot.yspace + sroot.height) * (mdepth + 1) + sroot.yspace;

         findSize();

         image =
            new BufferedImage((int) swidth, (int) sheight,
                              BufferedImage.TYPE_INT_RGB);

         Graphics2D g2 = image.createGraphics();
         paintBuffer(g2);

         revalidate();
         repaint();
      }

      /*
       * Scrolling causes a change event, but scrolling caused by moving the
       * navigator should not cause a change event.
       */
      public void stateChanged(ChangeEvent event) {

         if (statechanged) {
            findSize();
            repaint();
         }

         statechanged = true;
      }
   } // end class Navigator
} // end class NavigatorPanel
