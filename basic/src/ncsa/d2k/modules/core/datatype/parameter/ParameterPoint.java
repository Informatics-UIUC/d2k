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
package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Table;


/**
 * The ParameterPoint object can extends Table. A ParameterPoint defines a point
 * in a ParameterSpace. Parameters can be accessed by name if desired.
 *
 * @author  $author$
 * @version $Revision$, $Date$
 */

public interface ParameterPoint extends Table {

   //~ Methods *****************************************************************

   /**
    * Create a ParameterPoint from primative data types.
    *
    * @param  names  the names of the parameters.
    * @param  values the values parameter settings.
    *
    * @return a ParameterPoint.
    */
   public ParameterPoint createFromData(String[] names, double[] values);

   /**
    * Create a ParameterPoint from the information in the given table. Each
    * column in the table represents a parameter. Row 1 is the values for all
    * the parameter settings.
    *
    * @param  table the table representing the parameter space.
    *
    * @return a ParameterPoint.
    */
   public ParameterPoint createFromTable(MutableTable table);

   /**
    * Get the name of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return A string value representing the name of the parameter.
    */
   public String getName(int parameterIndex);

   /**
    * Get the number of parameters that define the space.
    *
    * @return An int value representing the minimum possible value of the
    *         parameter.
    */
   public int getNumParameters();

   /**
    * Get the parameter index of that corresponds to the given name.
    *
    * @param  name Description of parameter name.
    *
    * @return an integer representing the index of the parameters.
    *
    * @throws Exception Description of exception Exception.
    */
   public int getParameterIndex(String name) throws Exception;

   /**
    * Get the value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getValue(int parameterIndex);

   /**
    * Get the value of a parameter.
    *
    * @param  name is a string which names the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    *
    * @throws Exception Description of exception Exception.
    */
   public double getValue(String name) throws Exception;

   /**
    * Get the parameter index of that corresponds to the given name.
    *
    * @param  point      Description of parameter point.
    * @param  splitIndex Description of parameter splitIndex.
    *
    * @return an integer representing the index of the parameters.
    */
   public ParameterPoint[] segmentPoint(ParameterPoint point, int splitIndex);
} /* ParameterPoint */
