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

import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * A Viewport that draws a scaled-down node.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ScaledNode extends Viewport {

   //~ Constructors ************************************************************

   /**
    * Creates a new ScaledNode object.
    *
    * @param model decision tree model
    * @param node  node to draw
    * @param snode scaled node
    */
   public ScaledNode(ViewableDTModel model, ViewableDTNode node,
                     ScaledNode snode) { this(model, node, snode, null); }

   /**
    * Creates a new ScaledNode object.
    *
    * @param model decision tree model
    * @param node  decision tree node
    * @param snode scaled node
    * @param label label
    */
   public ScaledNode(ViewableDTModel model, ViewableDTNode node,
                     ScaledNode snode, String label) {
      super(model, node, snode, label);
   }

   //~ Methods *****************************************************************

   /**
    * Draw the scaled node.  This is just a filled rectangle.
    *
    * @param g2 graphics context
    */
   public void drawScaledNode(Graphics2D g2) {

      // Background
      g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
      g2.fill(new Rectangle2D.Double(x - width / 2, y, width, height));
   }
} // end class ScaledNode
