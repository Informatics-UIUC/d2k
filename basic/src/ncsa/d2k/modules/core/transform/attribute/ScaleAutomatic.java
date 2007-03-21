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
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.Table;

import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * This module presents a user interface for the interactive scaling of <code>
 * MutableTable</code> data. Selected columns are scaled to a user-specified
 * range (the default range is 0 to 1).
 *
 * <p>All transformed columns are converted to type <code>double</code>.</p>
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public class ScaleAutomatic
    extends Scale {

  //~ Instance fields *********************************************************




  //~ Methods *****************************************************************

  protected String indicesString;
  public void setIndicesString(String str) {
    indicesString = str;
     labelsArray = indicesString.split(",", 0);
    labels = new HashSet();
    for (int i = 0; i < labelsArray.length; i++) {
      labels.add(labelsArray[i]);
    }
  }

  public String getIndicesString() {
    return indicesString;
  }

  protected Set labels;
  protected String[] labelsArray;

  protected boolean useRegExp = false;
  public void setUseRegExp (boolean bl){useRegExp = bl;}
  public boolean getUseRegExp (){return useRegExp;}


  public void endExecution(){
    this.from_max = null;
    this.from_min = null;
  }
public void beginExecution(){
  this.suppressGui = true;
}

  protected double newMin = this.DEFAULT_MIN;

  public void setNewMin(double dbl){newMin = dbl;}
  public double getNewMin(){return newMin;}

  protected double newMax = this.DEFAULT_MAX;

  public void setNewMax(double dbl){newMax = dbl;}
  public double getNewMax(){return newMax;}



  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] super_pds = super.getPropertiesDescriptions();
    PropertyDescription[] pds = new PropertyDescription[super_pds.length+4];
    System.arraycopy(super_pds, 0, pds, 0, super_pds.length);

        pds[super_pds.length+0] = new PropertyDescription("newMin", "New Minimum",
                                         "This value will be the new lower bound of the sclaed columns.");

        pds[super_pds.length+1] = new PropertyDescription("newMax", "New Maximum",
                                         "This value will be the new upper bound of the sclaed columns.");
    pds[super_pds.length+2] = new PropertyDescription("indicesString",
                                     "Columns to Scale",
                                     "Labels of columns to be scaled");
    pds[super_pds.length+3] = new PropertyDescription("useRegExp", "Use Regular Expression",
                                     "Use regular expression to look up matching columns labels in the input table.");
    return pds;
  }

  public static final double DEFAULT_MIN = 0;
  public static final double DEFAULT_MAX = 1;


  /**
   * returns the indices of the coluns with labels matching the strings in <code>_lbls</code>
   * @param _tbl Table The Table to look up the columns indices in.
   * @param _lbls Set The columns labels to look up in <code>_tbl</code>
   * @return int[] returns the indices of the columns in <code>_tbl</code> that have lables
   * matching the strings in <code>_lbls</code>. returns null if anything goes wrong while looking up.
   */
  public static int[] getIndices(Table _tbl, Set  _lbls){
    int[] retVal = null;
    try{
      int[] indices = new int[_lbls.size()];
      int j = 0;
      for (int i = 0; i < _tbl.getNumColumns(); i++) {
        String lbl = _tbl.getColumnLabel(i);
        if (_lbls.contains(lbl)) {
          indices[j] = i;
          j++;
        }
      }
      retVal = new int[j];
      if (j == _lbls.size()) {
        retVal = indices;
      }
      else {
        System.arraycopy(indices, 0, retVal, 0, j);
      }
      return retVal;
    }catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }


  public static int[] getIndices(Table _tbl, String[]  _lbls){
   int[] retVal = null;
   try{
     Set indices = new HashSet();
     for(int lblIdx=0; lblIdx<_lbls.length; lblIdx++){
       for (int col = 0; col < _tbl.getNumColumns(); col++) {
         String col_lbl = _tbl.getColumnLabel(col);
         if (col_lbl.matches(_lbls[lblIdx])) {
           indices.add(new Integer(col));
         }//if match
       }//for col
     }//for _lbls
     retVal = new int[indices.size()];
     Iterator it = indices.iterator();
     int counter = 0;
     while(it.hasNext() && counter < retVal.length){
       retVal[counter] = ((Integer) it.next()).intValue();
       counter++;
     }
     return retVal;
   }catch(Exception e){
     e.printStackTrace();
     return null;
   }
 }


  /**
   * headless conversion support. If the table is in the same format as the
   * last successful execution, push out the same selected attributes.
   *
   * @throws Exception when something goes wrong.
   */
  public void doit() throws Exception {
    MutableTable table = (MutableTable)this.pullInput(0);

    if (
        indices == null ||
        from_min == null ||
        from_max == null ||
        to_min == null ||
        to_max == null) {

      try {
        //attempting to find the min and the max of each column to be scaled

        if(this.useRegExp){
          indices = this.getIndices(table, this.labelsArray);
        }else{
          indices = this.getIndices(table, labels);
        }

        to_min = new double[indices.length];
        to_max = new double[indices.length];
        for (int i = 0; i < to_min.length; i++) {
          to_min[i] = this.newMin;
          to_max[i] = this.newMax;
        }
        from_min = new double[indices.length];
        from_max = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
          double[] intrn = (double[]) table.getColumn(indices[i]).getInternal();
          double[] copyOf = new double[intrn.length];
        System.arraycopy(intrn, 0, copyOf, 0, copyOf.length);
          Arrays.sort(copyOf);
          from_min[i] = copyOf[0];
          from_max[i] = copyOf[copyOf.length - 1];
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        throw new Exception(this.getAlias() +
                            " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }
    } //initializing values on the fly

    int nc = table.getNumColumns();

    if (nc < indices.length) {
      throw new Exception(this.getAlias() +
                          " has not been configured to run headless with a table of this structure. Not enough columns in the table");
    }

    // Find the maximum column index.
    int max = -1;

    for (int i = 0; i < indices.length; i++) {

      if (indices[i] > max) {
        max = indices[i];
      }
    }

    if (nc < max) {
      throw new Exception(this.getAlias() +
                          " has not been configured to run headless with a table of this structure. Not enough columns in the table");
    }

    for (int i = 0; i < indices.length; i++) {

      if (indices[i] >= 0) {
        Column col = table.getColumn(indices[i]);

        if (!col.getIsScalar()) {
          throw new Exception(this.getAlias() +
                              " has not been configured to run headless with a table of this structure. Column " +
                              col.getLabel() + " is not scalar.");
        }
      }
    }

    pushOutput(new ScalingTransformation(indices, from_min, from_max,
                                         to_min, to_max), 0);
  } // end method doit

  /**
   * Returns the name of the module that is appropriate for end-user
   * consumption.
   *
   * @return The name of the module.
   */
  public String getModuleName() {
    return "Automatic Headless Scale";
  }

}

