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

import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;


/**
 * A parameter space is defines the search space for an optimizer. The space is
 * simply defined as a hyper-rectangle defined by min and max values for each
 * dimension. Each parameter has a minimum and maximum value which an optimizer
 * should used to limit its operation. Each parameter has a default value which
 * an optimizer can use as the intial starting point. Each parameter has a
 * number of regions which an optimizer should used to limit its operation. Each
 * parameter has type that represented as an integer define in ColumnTypes. A
 * parameter space is composed of one or more subspaces. Each D2K module in the
 * itinerary which the user disires to optimize has its separate ParameterSpace.
 * When these separate spaces are combined to form a single unified
 * ParameterSpace, the original substructure is maintain so that the
 * ParameterSpace can be use to decompose points in the unified space. The
 * column order for this table should match the order in which the module
 * designer of the parameters as described in the PropertyDescriptors defined in
 * the space generator(s). Parameters can be accessed by name using
 * getParameterIndex().
 *
 * @author  $author$
 * @version $Revision$, $Date$
 */
public interface ParameterSpace extends ExampleTable, java.io.Serializable {

   //~ Methods *****************************************************************

   /**
    * Instantiate a ParameterSpace from primative data types.
    *
    * @param names         the names of the parameters.
    * @param minValues     the minimum parameter values.
    * @param maxValues     the maximum parameter values.
    * @param defaultValues the default parameter settings.
    * @param numRegions    the number of regions between min and max.
    * @param types         the type as an integer as defined in ColumnTypes.
    */
   public void createFromData(String[] names,
                              double[] minValues,
                              double[] maxValues,
                              double[] defaultValues,
                              int[] numRegions,
                              int[] types);

   /**
    * Instantiate a ParameterSpace from the information in the given table. Each
    * column in the table represents a parameter. Row 1 is the minimum parameter
    * value. Row 2 is the maximum parameter value. Row 3 is the default
    * parameter setting. Row 4 is the parameter number of regions is the number
    * of regions between min and max. Row 5 is the type as an integer as defined
    * in ColumnTypes.
    *
    * @param  table the table representing the parameter space.
    *
    * @return a ParameterSpace.
    */
   public ParameterSpace createFromTable(MutableTable table);

   /**
    * Get the default values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the default values of all
    *         parameters.
    */
   public ParameterPoint getDefaultParameterPoint();

   /**
    * Get the default value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getDefaultValue(int parameterIndex);

   /**
    * Get the maximum values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the maximum possible values of all
    *         parameters.
    */
   public ParameterPoint getMaxParameterPoint();

   /**
    * Get the maximum value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getMaxValue(int parameterIndex);

   /**
    * Get the minimum values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the minimum possible values of all
    *         parameters.
    */
   public ParameterPoint getMinParameterPoint();

   /**
    * Get the minimum value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getMinValue(int parameterIndex);

   /**
    * Get the name of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a string value representing the name of the parameter.
    */
   public String getName(int parameterIndex);


   /**
    * Get the number of parameters that define the space.
    *
    * @return an int value representing the minimum possible value of the
    *         parameter.
    */
   public int getNumParameters();

   /**
    * Get the number of regions for this parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the number of intervals between the min
    *         and max parameter values.
    */
   public int getNumRegions(int parameterIndex);

   /**
    * Get the number of subspaces that defines the space.
    *
    * @return a int value representing the number subspaces that define the
    *         space.
    */
   public int getNumSubspaces();

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
    * Get a subspace from the space.
    *
    * @param  subspaceIndex the index of the subspace of interest.
    *
    * @return a ParameterSpace which defines the indicated subspace.
    *
    * @throws Exception Description of exception Exception.
    */
   public ParameterSpace getSubspace(int subspaceIndex) throws Exception;

   /**
    * Get the subspace index of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the subpace index number of parameter.
    *
    * @throws Exception Description of exception Exception.
    */
   public int getSubspaceIndex(int parameterIndex) throws Exception;

   /**
    * Get the number of parameters in a subspace.
    *
    * @param  subspaceIndex Description of parameter subspaceIndex.
    *
    * @return the number of parameters the subspace.
    */
   public int getSubspaceNumParameters(int subspaceIndex);

   /**
    * Get the type of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the type of the parameter value as
    *         defined in ColumnTypes.
    */
   public int getType(int parameterIndex);

   /**
    * Join two ParameterSpaces to produce a new independent single parameter
    * space that does not share any memeory with with original ParameterSpaces.
    *
    * @param  firstSpace  the first of the two ParameterSpaces to join.
    * @param  secondSpace the second of the two ParameterSpaces to join.
    *
    * @return a ParameterSpace which defines the indicated subspace.
    */
   public ParameterSpace joinSubspaces(ParameterSpace firstSpace,
                                       ParameterSpace secondSpace);

   /**
    * Set the default value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the value of the parameter of interest.
    */
   public void setDefaultValue(int parameterIndex, double value);

   /**
    * Set the maximum value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the value of the parameter of interest.
    */
   public void setMaxValue(int parameterIndex, double value);

   /**
    * Set the minimum value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          Description of parameter value.
    */
   public void setMinValue(int parameterIndex, double value);

   /**
    * Set the number of regions.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the resolution.
    */
   public void setNumRegions(int parameterIndex, int value);

   /**
    * Set the type of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param type           the type as defined in ColumnTypes().
    */
   public void setType(int parameterIndex, int type);

} /* ParameterSpace */
