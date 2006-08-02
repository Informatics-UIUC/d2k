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

import javax.swing.border.AbstractBorder;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


/**
 * DecisionTreeVis Border for navigator panel and brush panel.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public final class RectangleBorder extends AbstractBorder {

   //~ Instance fields *********************************************************

   /** Description of field ascent. */
   int ascent;

   /** left inset. */
   int left;
   /** right inset. */
   int right;
   /** top inset. */
   int top;
   /** bottom inset. */
   int bottom;

   /** font metrics used */
   FontMetrics metrics;

   /** title */
   String title;

   /** title top inset. */
   int titletop;
   /** title bottom inset. */
   int titlebottom;
   /** title space inset. */
   int titlespace;

   //~ Constructors ************************************************************

   /**
    * Creates a new RectangleBorder object.
    *
    * @param title title to show
    */
   public RectangleBorder(String title) {
      this.title = title;

      // Insets
      left = 10;
      right = 10;
      bottom = 10;
      titletop = 4;
      titlebottom = 8;
      titlespace = 12;
      top = titletop + 10 + titlebottom + titlespace;
   }

   //~ Methods *****************************************************************


    /**
     * This default implementation returns a new <code>Insets</code>
     * instance where the <code>top</code>, <code>left</code>,
     * <code>bottom</code>, and
     * <code>right</code> fields are set to <code>0</code>.
     *
     * @param c the component for which this border insets value applies
     * @return the new <code>Insets</code> object initialized to 0
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(top, left, bottom, right);
    }


    /**
     * This default implementation does no painting.
     *
     * @param c      the component for which this border is being painted
     * @param g      the paint graphics
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
                            int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        metrics = g2.getFontMetrics(DecisionTreeScheme.textfont);
        ascent = metrics.getAscent();

        // Background
        g2.setColor(DecisionTreeScheme.borderbackgroundcolor);
        g2.fill(new Rectangle2D.Double(x, y, width, top));
        g2.fill(new Rectangle2D.Double(x, y, left, height));
        g2.fill(new Rectangle2D.Double(x + width - right, y, right, height));
        g2.fill(new Rectangle2D.Double(x, y + height - bottom, width, bottom));

        // Bevel
        double ybevel = y + titletop + ascent + titlebottom;
        g2.setColor(DecisionTreeScheme.bordershadowcolor);
        g2.draw(new Line2D.Double(x, y + ybevel, x + width - 1, y + ybevel));
        g2.setColor(DecisionTreeScheme.borderhighlightcolor);
        g2.draw(new Line2D.Double(x, y + ybevel + 2, x + width - 1,
                y + ybevel + 2));

        // Upper bevel
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(DecisionTreeScheme.borderupperbevelcolor);
        g2.draw(new Line2D.Double(x, y, x + width - 1, y));
        g2.draw(new Line2D.Double(x, y, x, y + height - 1));

        // Lower bevel
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(DecisionTreeScheme.borderlowerbevelcolor);
        g2.draw(new Line2D.Double(x, y + height - 1, x + width - 1,
                y + height - 1));
        g2.draw(new Line2D.Double(x + width - 1, y, x + width - 1,
                y + height - 1));

        // Title
        g2.setFont(DecisionTreeScheme.textfont);
        g2.setColor(DecisionTreeScheme.textcolor);
        g2.drawString(title, x + left, y + titletop + ascent);
    } // end method paintBorder
} // end class RectangleBorder
