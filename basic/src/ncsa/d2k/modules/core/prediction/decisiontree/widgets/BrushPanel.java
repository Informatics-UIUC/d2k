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

import javax.swing.*;

import java.awt.*;


/**
 * Draws data when mouse moves over a node in tree scroll pane.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public final class BrushPanel extends JPanel {

   //~ Instance fields *********************************************************

   /** the view */
   View view;

   //~ Constructors ************************************************************

   /**
    * Creates a new BrushPanel object.
    *
    * @param model the decision tree model
    */
   public BrushPanel(ViewableDTModel model) {
      DecisionTreeScheme scheme = new DecisionTreeScheme();

      setOpaque(true);
      setBackground(scheme.borderbackgroundcolor);
   }

   //~ Methods *****************************************************************


    /**
     * The minimum size is large enough to show the brush panel and the insets
     * around the brush panel
     * @return minimum size
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }


    /**
     * The preferred size is large enough to show the brush panel and the insets
     * around the brush panel
     * @return preferred size
     */
    public Dimension getPreferredSize() {

        Insets insets = getInsets();

        double width = insets.left + insets.right;
        double height = insets.top + insets.bottom;

        if (view != null) {
            width += view.getBrushWidth();
            height += view.getBrushHeight();
        }

        return new Dimension((int) width, (int) height);
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

        Graphics2D g2 = (Graphics2D) g;

        Insets insets = getInsets();

        if (view != null) {
            g2.translate(insets.left, insets.top);
            view.drawBrush(g2);
            g2.translate(-insets.left, -insets.top);
        }
    }

   /**
    * update
    *
    * @param view the view
    */
   public void updateBrush(View view) {
      this.view = view;

      revalidate();
      repaint();
   }
} // end class BrushPanel
