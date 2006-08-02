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
package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;


/**
 * This is an interface. a model that implements UpdateableModelModule is a
 * model that can be retrained on new data sets via the update(ExampleTable)
 * method.
 *
 * <p>the copy method is needed to create a deep copy of the model. this way
 * other modules can use the older versions of the model without being affected
 * by the update.</p>
 *
 * <p>the init method together with the update method aim that an
 * UpdateableModelModule will be generated and trained as follows:</p>
 *
 * <p>a model producer module will generate the model and call the init method.
 * then this initialized model is sent to IncrementingModule that calls update
 * method each time a new dataset arrives.</p>
 *
 * @author  vered goren
 * @version 1.0
 */
public interface UpdateableModelModule {

   //~ Methods *****************************************************************

   /**
    * the copy method is needed to create a deep copy of the model. this way
    * other modules can use the older versions of the model without being
    * affected by the update.
    *
    * @return a copy
    */
   public UpdateableModelModule copy();

   /**
    * A model producer module will generate the model and call the init method.
    */
   public void init();

   /**
    * Retrain on a new data set.
    *
    * @param  tbl Description of parameter $param.name$.
    *
    * @throws Exception
    */
   public void update(ExampleTable tbl) throws Exception;
} // end interface UpdateableModelModule
