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
package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.transform.StaticMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * <p>Description: This is a suppoty class for binning Headless UI modules. its
 * methods will be called by the doit method.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company:</p>
 *
 * @author  vered goren
 * @version 1.0
 */
public class BinningUtils {

   //~ Constructors ************************************************************

   /**
    * Creates a new BinningUtils object.
    */
   private BinningUtils() { }

   //~ Methods *****************************************************************

   /**
    * Add a missing value bin for each column.  A new array is created with
    * the original bins and the new bins for missing values.
    *
    * @param  tbl  table
    * @param  bins bins for table
    *
    * @return array containing the original bins and the newly created bins
    * for missing values
    */
   static public BinDescriptor[] addMissingValueBins(Table tbl,
                                                     BinDescriptor[] bins) {

      HashMap colIndexLookup = new HashMap(tbl.getNumColumns());

      for (int i = 0; i < tbl.getNumColumns(); i++) {
         colIndexLookup.put(tbl.getColumnLabel(i), new Integer(i));
      }

      // need to figure out which columns have been binned:
      boolean[] binRelevant = new boolean[tbl.getNumColumns()];

      for (int i = 0; i < binRelevant.length; i++) {
         binRelevant[i] = false;
      }

      for (int i = 0; i < bins.length; i++) {
         Integer idx = (Integer) colIndexLookup.get(bins[i].label);

         if (idx != null) {
            binRelevant[idx.intValue()] = true;
            // System.out.println("relevant column " + idx.intValue());
         }
         // else
         // System.out.println("COLUMN LABEL NOT FOUND!!!");
         // binRelevant[bins[i].column_number] = true;
      }


      ArrayList unknowBins = new ArrayList();
      int numColumns = tbl.getNumColumns();

      for (int i = 0; i < numColumns; i++) {

         if (binRelevant[i]) {

            if (tbl.getColumn(i).hasMissingValues()) {
               unknowBins.add(BinDescriptorFactory.createMissingValuesBin(i,
                                                                          tbl));
            }
         }
      }

      BinDescriptor[] newbins =
         new BinDescriptor[bins.length + unknowBins.size()];
      int i;

      for (i = 0; i < bins.length; i++) {
         newbins[i] = bins[i];
      }

      Iterator it = unknowBins.iterator();

      while (it.hasNext()) {
         newbins[i++] = (BinDescriptor) it.next();
      }

      return newbins;
   } // end method addMissingValueBins


   /**
    * Validate that the bins reference columns in t.
    *
    * @param  t          table
    * @param  binDes     bins
    * @param  commonName name of module that calls this function; used for
    * printing debugging statements
    *
    * @return true if the bins are valid
    *
    * @throws Exception when something goes wrong
    */
   static public boolean validateBins(Table t, BinDescriptor[] binDes,
                                      String commonName) throws Exception {

      if (binDes == null) {
         throw new Exception(commonName +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      if (binDes.length == 0) {

         System.out.println(commonName +
                            ": No bins were configured. The transformation will be an empty one.");

         return true;
      }

// validating relevancy of bins to the input table.
      HashMap columns = StaticMethods.getAvailableAttributes(t);

      for (int i = 0; i < binDes.length; i++) {

         if (!columns.containsKey(binDes[i].label)) {
            throw new Exception(commonName + ": Bin " + binDes[i].toString() +
                                " does not match any column label in the input table. Please reconfigure this module.");
//
         }
      } // for

      return true;
   } // end method validateBins


   /**
    * Validate that the bins reference columns in colMap
    *
    * @param  colMap     Map where keySet is the column names in a table.
    * @param  binDes     bins
    * @param  commonName name of module that calls this function; used for
    * printing debugging statements
    *
    * @return true if the bins are valid
    *
    * @throws Exception when something goes wrong
    */
   static public boolean validateBins(HashMap colMap, BinDescriptor[] binDes,
                                      String commonName) throws Exception {

      if (binDes == null) {
         throw new Exception(commonName +
                             " has not been configured. Before running headless, run with the gui and configure the parameters.");
      }

      if (binDes.length == 0) {
         System.out.println(commonName +
                            ": No bins were configured. The transformation will be an empty one.");

         return true;
      }


      for (int i = 0; i < binDes.length; i++) {

         if (!colMap.containsKey(binDes[i].label)) {
            throw new Exception(commonName + ": Bin " + binDes[i].toString() +
                                " does not match any column label in the database table." +
                                " Please reconfigure this module via a GUI run so it can run Headless.");
         }

      } // for

      return true;
   } // end method validateBins

} // end class BinningUtils
