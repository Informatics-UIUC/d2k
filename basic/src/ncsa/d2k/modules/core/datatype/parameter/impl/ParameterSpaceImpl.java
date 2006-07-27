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
package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.BooleanColumn;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;


/**
 * this is the implementation based on the real table.
 *
 * @author  Thomas L. Redman
 * @version 1.0
 */
public class ParameterSpaceImpl extends ExampleTableImpl
   implements ParameterSpace {

   //~ Instance fields *********************************************************

   /** the resolution of each property. */
   int[] numRegions = new int[0];

   /** the number of spaces. */
   int numSpaces = 0;

   /** counts of the number of parameters in each subspace. */
   int[] parameterCount = new int[0];

   //~ Constructors ************************************************************

   /**
    * Creates a new ParameterSpaceImpl object.
    */
   public ParameterSpaceImpl() { this(0); }

   /**
    * Creates a new ParameterSpaceImpl object.
    *
    * @param numColumns Description of parameter numColumns.
    */
   public ParameterSpaceImpl(int numColumns) { super(numColumns); }

   //~ Methods *****************************************************************

   /**
    * Add a new subspace to the parameter space.
    *
    * @param newcols the number of parameters in the new space.
    */
   private void addSubspace(int newcols) {
      this.numSpaces++;

      int[] newcounts = new int[this.numSpaces];
      System.arraycopy(this.parameterCount, 0, newcounts, 0, this.numSpaces -
                       1);
      this.parameterCount = newcounts;
      this.parameterCount[this.numSpaces - 1] = newcols;
   }

   /**
    * Instantiate a ParameterSpace from primative data types.
    *
    * @param names         the names of the parameters.
    * @param minValues     the minimum parameter values.
    * @param maxValues     the maximum parameter values.
    * @param defaultValues the default parameter settings.
    * @param numRegions    between the min and max.
    * @param types         the type as an integer as defined in ColumnTypes.
    */
   public void createFromData(String[] names,
                              double[] minValues,
                              double[] maxValues,
                              double[] defaultValues,
                              int[] numRegions,
                              int[] types) {

      // Given the data create a parameter space object and return it.
      int numColumns = names.length;
      ParameterSpaceImpl spi = this;
      Column[] columns = new Column[numColumns];
      Column addMe = null;

      // Init all the columns of the parameter space.
      for (int i = 0; i < numColumns; i++) {

         switch (types[i]) {

            case ColumnTypes.DOUBLE:
            case ColumnTypes.FLOAT:

               double[] dvals = new double[3];
               dvals[0] = minValues[i];
               dvals[1] = maxValues[i];
               dvals[2] = defaultValues[i];
               addMe = new DoubleColumn(dvals);

               break;

            case ColumnTypes.INTEGER:
            case ColumnTypes.LONG:

               int[] ivals = new int[3];
               ivals[0] = (int) minValues[i];
               ivals[1] = (int) maxValues[i];
               ivals[2] = (int) defaultValues[i];
               addMe = new IntColumn(ivals);

               break;

            case ColumnTypes.BOOLEAN:

               boolean[] bvals = new boolean[3];
               bvals[0] = minValues[i] == 0 ? false : true;
               bvals[1] = maxValues[i] == 0 ? false : true;
               bvals[2] = defaultValues[i] == 0 ? false : true;
               addMe = new BooleanColumn(bvals);

               break;
         }

         addMe.setLabel(names[i]);
         columns[i] = addMe;
      } // end for

      spi.addColumns(columns);
      this.numRegions = numRegions;
      spi.addSubspace(numColumns);
   } // end method createFromData

   /**
    * Create From Table--Not implemented.
    *
    * @param  table The table.
    *
    * @return null.
    */
   public ParameterSpace createFromTable(MutableTable table) { return null; }

   /**
    * Get the default values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the default values of all
    *         parameters.
    */
   public ParameterPoint getDefaultParameterPoint() {
      double[] vals = new double[this.getNumParameters()];
      String[] labels = new String[this.getNumParameters()];

      for (int i = 0; i < vals.length; i++) {
         vals[i] = this.getDefaultValue(i);
         labels[i] = this.getName(i);
      }

      ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);

      return ppi;
   }

   /**
    * Get the default value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getDefaultValue(int parameterIndex) {
      return this.getDouble(2, parameterIndex);
   }

   /**
    * Get the maximum values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the maximum possible values of all
    *         parameters.
    */
   public ParameterPoint getMaxParameterPoint() {
      String[] labels = new String[this.getNumParameters()];
      double[] vals = new double[this.getNumParameters()];

      for (int i = 0; i < vals.length; i++) {
         vals[i] = this.getMaxValue(i);
         labels[i] = this.getName(i);
      }

      ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);

      return ppi;
   }

   /**
    * Get the maximum value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getMaxValue(int parameterIndex) {
      return this.getDouble(1, parameterIndex);
   }

   /**
    * Get the minimum values of all parameters returned as a ParameterPoint.
    *
    * @return A ParameterPoint representing the minimum possible values of all
    *         parameters.
    */
   public ParameterPoint getMinParameterPoint() {
      double[] vals = new double[this.getNumParameters()];
      String[] labels = new String[this.getNumParameters()];

      for (int i = 0; i < vals.length; i++) {
         vals[i] = this.getMinValue(i);
         labels[i] = this.getName(i);
      }

      ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);

      return ppi;
   }

   /**
    * Get the minimum value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a double value representing the minimum possible value of the
    *         parameter.
    */
   public double getMinValue(int parameterIndex) {
      return this.getDouble(0, parameterIndex);
   }

   /**
    * Get the name of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a string value representing the name of the parameter.
    */
   public String getName(int parameterIndex) {
      return columns[parameterIndex].getLabel();
   }

   /**
    * Get the number of parameters that define the space.
    *
    * @return an int value representing the minimum possible value of the
    *         parameter.
    */
   public int getNumParameters() { return this.getNumColumns(); }

   /**
    * Get the number of regions of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the number of regions between the min and
    *         max parameter values.
    */
   public int getNumRegions(int parameterIndex) {
      return numRegions[parameterIndex];
   }

   /**
    * Get the number of subspaces that defines the space.
    *
    * @return a int value representing the number subspaces that define the
    *         space.
    */
   public int getNumSubspaces() { return this.numSpaces; }

   /**
    * Get the parameter index of that corresponds to the given name.
    *
    * @param  name Description of parameter name.
    *
    * @return an integer representing the index of the parameters.
    */
   public int getParameterIndex(String name) {

      for (int i = 0; i < this.getNumColumns(); i++) {

         if (this.getColumnLabel(i).equals(name)) {
            return i;
         }
      }

      return -1;
   }

   /**
    * Get a subspace from the space.
    *
    * @param  subspaceIndex the index of the subspace of interest.
    *
    * @return a ParameterSpace which defines the indicated subspace.
    */
   public ParameterSpace getSubspace(int subspaceIndex) {

      // First find the offset where the subspace starts.
      if (subspaceIndex >= parameterCount.length) {
         return null;
      }

      // Find the start and end of the subspace.
      int start = 0;

      for (int i = 0; i < subspaceIndex; i++) {
         start += parameterCount[i];
      }

      int end = start + parameterCount[subspaceIndex];
      int size = end - start;

      // initialize the data from which we will create the new space.
      double[] mins = new double[size];
      double[] maxs = new double[size];
      double[] defaults = new double[size];
      int[] numRegions = new int[size];
      int[] types = new int[size];
      String[] names = new String[size];

      // init the values.
      for (int i = 0; start < end; start++, i++) {
         mins[i] = this.getMinValue(start);
         maxs[i] = this.getMaxValue(start);
         defaults[i] = this.getDefaultValue(start);
         numRegions[i] = this.getNumRegions(start);
         types[i] = this.getType(start);
         names[i] = this.getName(start);
      }

      // Now we have the data, make the parameter space.
      ParameterSpaceImpl psi = new ParameterSpaceImpl();
      psi.createFromData(names, mins, maxs, defaults, numRegions, types);

      return psi;
   } // end method getSubspace

   /**
    * Get the subspace index of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the subpace index number of parameter.
    */
   public int getSubspaceIndex(int parameterIndex) {

      for (int i = 0, counter = 0; i < this.parameterCount.length; i++) {
         counter += parameterCount[i];

         if (counter > parameterIndex) {
            return i;
         }
      }

      return -1;
   }

   /**
    * Get the number of parameters in each subspace.
    *
    * @param  subspaceIndex Description of parameter subspaceIndex.
    *
    * @return a int array of values the number of parameters defining each
    *         subspace.
    */
   public int getSubspaceNumParameters(int subspaceIndex) {
      return this.parameterCount[subspaceIndex];
   }

   /**
    * Get the subspace parameter index of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the subpace index number of parameter.
    */
   public int getSubspaceParameterIndex(int parameterIndex) {

      for (int i = 0, counter = 0; i < this.parameterCount.length; i++) {
         counter += parameterCount[i];

         if (counter > parameterIndex) {
            return parameterIndex - (counter - parameterCount[i]);
         }
      }

      return -1;
   }

   /**
    * Get the type of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    *
    * @return a int value representing the type of the parameter value as
    *         defined in ColumnTypes.
    */
   public int getType(int parameterIndex) {
      return columns[parameterIndex].getType();
   }

   /**
    * Join two ParameterSpaces to produce a single parameter space. This does a
    * deep copy.
    *
    * @param  firstSpace  the first of the two ParameterSpaces to join.
    * @param  secondSpace the second of the two ParameterSpaces to join.
    *
    * @return a ParameterSpace which defines the indicated subspace.
    */
   public ParameterSpace joinSubspaces(ParameterSpace firstSpace,
                                       ParameterSpace secondSpace) {
      ParameterSpaceImpl psi = new ParameterSpaceImpl();

      // initialize the data from which we will create the new space.
      int size = firstSpace.getNumParameters() + secondSpace.getNumParameters();
      double[] mins = new double[size];
      double[] maxs = new double[size];
      double[] defaults = new double[size];
      int[] numRegions = new int[size];
      int[] types = new int[size];
      String[] names = new String[size];

      // init the values.
      int counter = 0;

      for (int i = 0; i < firstSpace.getNumParameters(); i++, counter++) {
         mins[counter] = firstSpace.getMinValue(i);
         maxs[counter] = firstSpace.getMaxValue(i);
         defaults[counter] = firstSpace.getDefaultValue(i);
         numRegions[counter] = firstSpace.getNumRegions(i);
         types[counter] = firstSpace.getType(i);
         names[counter] = firstSpace.getName(i);
      }

      for (int i = 0; i < secondSpace.getNumParameters(); i++, counter++) {
         mins[counter] = secondSpace.getMinValue(i);
         maxs[counter] = secondSpace.getMaxValue(i);
         defaults[counter] = secondSpace.getDefaultValue(i);
         numRegions[counter] = secondSpace.getNumRegions(i);
         types[counter] = secondSpace.getType(i);
         names[counter] = secondSpace.getName(i);
      }

      psi.createFromData(names, mins, maxs, defaults, numRegions, types);
      psi.addSubspace(firstSpace.getNumParameters());
      psi.addSubspace(secondSpace.getNumParameters());

      return psi;
   } // end method joinSubspaces

   /**
    * Set the default value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the value of the parameter of interest.
    */
   public void setDefaultValue(int parameterIndex, double value) {
      this.setDouble(value, 2, parameterIndex);
   }

   /**
    * Set the maximum value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the value of the parameter of interest.
    */
   public void setMaxValue(int parameterIndex, double value) {
      this.setDouble(value, 1, parameterIndex);
   }

   /**
    * Set the minimum value of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          Description of parameter value.
    */
   public void setMinValue(int parameterIndex, double value) {
      this.setDouble(value, 0, parameterIndex);
   }

   /**
    * Set the resolution of a parameter.
    *
    * @param parameterIndex the index of the parameter of interest.
    * @param value          the number of regions.
    */
   public void setNumRegions(int parameterIndex, int value) {
      this.numRegions[parameterIndex] = value;
   }


   /**
    * Set the default value of a parameter.
    *
    * @param  parameterIndex the index of the parameter of interest.
    * @param  value          is the type of the parameter of interest.
    *
    * @throws RuntimeException Description of exception RuntimeException.
    */
   public void setType(int parameterIndex, int value) {
      throw new RuntimeException("Why would you change the data type?");
   }
} // end class ParameterSpaceImpl
