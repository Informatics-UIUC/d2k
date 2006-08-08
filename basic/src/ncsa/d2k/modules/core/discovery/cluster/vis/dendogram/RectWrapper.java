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


import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;


/**
 * <p>Title: RectWrapper</p>
 *
 * <p>Description: This object serves as a wrapper for a rectangle in a <code>
 * BaseGraphRectGraph</code></p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class RectWrapper {

   //~ Instance fields *********************************************************

   /** The table cluster that is the node this wrapper wrapps. */
   private TableCluster m_cluster = null;

   /** The background color for the rectangle. */
   private Color m_color = null;

   /** The label for this node. */
   private String m_label = "";

   /**
    * Holds <code>TableCluster</codE> objects that are the sub clusters of
    * <codE>m_cluster.</codE>
    */
   private ArrayList m_leaves = new ArrayList();

   /** The rendered shape. */
   private Rectangle m_rect = null;

   //~ Constructors ************************************************************

   /**
    * Constructs a <codE>RectWrapper</codE> object, initialized with the
    * rendered shape <cdoe>rect , the <codE>TableCluster</code> that holds the
    * data <code>c</code> and the abckground color <code>col</code>.
    *
    * @param rect Rectangle The shape to be rendered
    * @param c    TableCluster The node in the cluster tree, holds the data
    * @param col  Color The background color for <code>rect</code>
    */
   public RectWrapper(Rectangle rect, TableCluster c, Color col) {
      m_cluster = c;
      m_rect = rect;
      m_color = col;

      // compute label
      m_label = c.getClusterLabel() + "";
      // m_label = c.generateTextLabel() + "";
   }

   //~ Methods *****************************************************************

   /**
    * Adds <codE>c</code> to the list of leaves.
    *
    * @param c TableCluster a sub cluster of <code>c_cluster</code> that is also
    *          childless.
    */
   public void addLeaf(TableCluster c) { m_leaves.add(c); }


   /**
    * Adds the <codE>TableCLuster</codE> objects in <code>alist</code> to the
    * list of sub clusters.
    *
    * @param alist ArrayList holds <code>TableCluster</codE> objects that are
    *              sub clusters of <code>m_cluster</code>
    */
   public void addLeaves(ArrayList alist) { m_leaves.addAll(alist); }


   /**
    * Returns the <code>TableCluster</code> that is the node in the cluster tree
    * represented by this rectangle.
    *
    * @return TableCluster The node in the cluster tree represented by this
    *         rectangle.
    */
   public TableCluster getCluster() { return m_cluster; }

   /**
    * Returns the background color of this rectangle.
    *
    * @return Color The color set for this rectangle.
    */
   public Color getColor() { return m_color; }

   /**
    * Returns the label of this cluster.
    *
    * @return String The label of this cluster.
    */
   public String getLabel() { return m_label; }

   /**
    * Returns the <code>TableCluster</code> objects that are childless and also
    * sub clusters of <codE>m_cluster.</codE>
    *
    * @return ArrayList an array list that holds <code>TableCluster</code>
    *         objects that are childless and also sub clusters of <codE>
    *         m_cluster</codE>
    */
   public ArrayList getLeaves() { return m_leaves; }

   /**
    * Returns the shape to be rendered in the graph for the node this wrapper is
    * wrapping.
    *
    * @return Rectangle The shape to be rendered.
    */
   public Rectangle getRect() { return m_rect; }

   /**
    * Sets the background color for this rectangle.
    *
    * @param c Color a background color.
    */
   public void setColor(Color c) { m_color = c; }
} // end class RectWrapper
