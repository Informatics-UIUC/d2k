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

/**
 * <p>Title: GraphSettings</p>
 *
 * <p>Description: General settings for a graph, such as title, axis titles,
 * minimum values, maximum value etc.</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class GraphSettings {

   //~ Instance fields *********************************************************

   /** Whether to display the labels of the axis. */
   public boolean displayaxislabels;

   /** Whether to display the grid on the background. */
   public boolean displaygrid;

   /** Whether to display the legend of the graph. */
   public boolean displaylegend;

   /** Whether to display the scale of the graph. */
   public boolean displayscale;

   /** Whether to display the tick marks of the graph. */
   public boolean displaytickmarks;

   /** Whether to display the title of the graph. */
   public boolean displaytitle;

   /**
    * Size of the background grid (usually also impose resolution of the graph).
    */
   public int gridsize;

   /** Title of the graph. */
   public String title;

   /** title of the X axle. */
   public String xaxis;

   /** maximum value on X axle. */
   public Integer xmaximum;

   /** minimum value on X axle. */
   public Integer xminimum;

   /** title of the Y axle. */
   public String yaxis;

   /** maximum value on Y axle. */
   public Integer ymaximum;


   /** minimum value on Y axle. */
   public Integer yminimum;

   //~ Constructors ************************************************************

   /**
    * Creates a new GraphSettings object with all String as empty ones, all
    * Integers are nulled adn all booleans as true.
    */
   public GraphSettings() {
      title = "";
      xaxis = "";
      yaxis = "";

      xminimum = null;
      xmaximum = null;
      yminimum = null;
      ymaximum = null;

      gridsize = 10;

      displaygrid = true;
      displayscale = true;
      displaylegend = true;
      displaytickmarks = true;
      displaytitle = true;
      displayaxislabels = true;
   }
} // end class GraphSettings
