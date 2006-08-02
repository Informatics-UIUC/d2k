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
package ncsa.d2k.modules.core.prediction.decisiontree;

/**
 * Interface that viewable decision tree nodes implement
 *
 * @author  $author$
 * @version $Revision$, $Date$
 */
public interface ViewableDTNode {

   //~ Methods *****************************************************************

   /**
    * Get the label of a branch.
    *
    * @param  i the branch to get the label of
    *
    * @return the label of branch i
    */
   public String getBranchLabel(int i);

   /**
    * Get the depth of this node. (Root is 0)
    *
    * @return the depth of this node.
    */
   public int getDepth();

   /**
    * Get the label of this node.
    *
    * @return the label of this node
    */
   public String getLabel();

   /**
    * Get the number of children of this node.
    *
    * @return the number of children of this node
    */
   public int getNumChildren();

   /**
    * Get the total number of examples that passed through this node.
    *
    * @return the total number of examples that passes through this node
    */
   public int getTotal();

   /**
    * Get a child of this node.
    *
    * @param  i the index of the child to get
    *
    * @return the ith child of this node
    */
   public ViewableDTNode getViewableChild(int i);

   /**
    * Get the parent of this node.
    *
    * @return get the parent of this node.
    */
   public ViewableDTNode getViewableParent();
} // end interface ViewableDTNode
