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
package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.sql.SQLException;


/**
 * Abstract class implementing the common cache functionality.
 *
 * @author  mathur
 * @author  clutter
 * @version $Revision$, $Date$
 */

public abstract class Cache implements java.io.Serializable {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -1798715195654906371L;

   //~ Instance fields *********************************************************

   /** Indicates the end of the <code>ResultSet</code> subset cached. */
   protected int maxRowNum;

   /** Maximum number of rows that can be in the cache. */
   protected int maxRowsInCache;

   /** Indicates the beginning of the <code>ResultSet</code> subset cached. */
   protected int minRowNum;

   /** Current number of rows in the cache. */
   protected int numRowsInCache;

   /** Stores the cached data. */
   protected MutableTableImpl table;

   //~ Methods *****************************************************************

   /**
    * Returns the local row number in the cache where the data is to be found.
    * Returns -1 if the data is not in the cache.
    *
    * @param  row Row to search for
    *
    * @return Row number in the cache where the row is found
    */
   private int whichRowInCache(int row) {

      try {

         if ((row >= minRowNum) && (row <= maxRowNum)) { // found row in Cache
            return (row - minRowNum); // return the row
         } else {
            return -1; // couldn't find row in Cache
         }
      } catch (Exception e) {
         e.printStackTrace();

         return -2;
      }
   }

   /**
    * Returns the maximum number of rows that can be in the cache.
    *
    * @return Max number of rows that can be in the cache
    */
   protected int getMaxCacheRows() { return maxRowsInCache; }

   /**
    * Returns the current number of rows that are cached.
    *
    * @return Current number of rows that are cached
    */
   protected int getNumCacheRows() { return numRowsInCache; }

   /**
    * Sets the maximum number of rows to cache.
    *
    * @param calculatedMaxNumRows Max number of rows to cache
    */
   protected void setMaxCacheRows(int calculatedMaxNumRows) {
      maxRowsInCache = calculatedMaxNumRows;
   }

   /**
    * Sets the number of rows that are in the cache.
    *
    * @param cacheRows Number of rows that are in the cache
    */
   protected void setNumCacheRows(int cacheRows) { numRowsInCache = cacheRows; }

   /**
    * Returns the data at the specified row and column as a boolean value.
    *
    * @param  row Row to find the data
    * @param  col Column to find the data
    *
    * @return Boolean value of the data
    *
    * @throws CacheMissException If the specified row is not in the cache
    */
   public boolean getCacheBooleanData(int row, int col)
      throws CacheMissException {
      int rowInCache = whichRowInCache(row);

      if (rowInCache != -1) {
         return table.getBoolean(rowInCache, col);
      } else {
         throw new CacheMissException("Cache:GetCacheBooleanData:CacheMissException");
      }
   }

   /**
    * Returns the data at the specified row and column as a double value.
    *
    * @param  row Row to find the data
    * @param  col Column to find the data
    *
    * @return Double value of the data
    *
    * @throws CacheMissException If the specified row is not in the cache
    */
   public double getCacheNumericData(int row, int col)
      throws CacheMissException {
      int rowInCache = whichRowInCache(row);

      if (rowInCache != -1) {
         return table.getDouble(rowInCache, col);
      } else {
         throw new CacheMissException("Cache:GetCacheNumericData:CacheMissException");
      }
   }

   /**
    * Returns the data at the specified row and column as an <code>
    * Object</code>.
    *
    * @param  row Row to find the data
    * @param  col Column to find the data
    *
    * @return <code>Object</code> value of the data
    *
    * @throws CacheMissException If the specified row is not in the cache
    */
   public Object getCacheObjectData(int row, int col)
      throws CacheMissException {
      int rowInCache = whichRowInCache(row);

      if (rowInCache != -1) {
         return table.getObject(rowInCache, col);
      } else {
         throw new CacheMissException("Cache:getCacheObjectData:CacheMissException");
      }
   }


   /**
    * Returns the data at the specified row and column as a String value.
    *
    * @param  row Row to find the data
    * @param  col Column to find the data
    *
    * @return String value of the data
    *
    * @throws CacheMissException If the specified row is not in the cache
    */
   public String getCacheTextData(int row, int col) throws CacheMissException {
      int rowInCache = whichRowInCache(row);

      if (rowInCache != -1) {
         return table.getString(rowInCache, col);
      } else {
         throw new CacheMissException("Cache:getCacheTextData:CacheMissException");
      }
   }

   /**
    * Updates the cache.
    *
    * @param  rowInResultSet Uses as the minimum row number to cache
    * @param  dbds           Datasource to use when populating cache
    *
    * @throws SQLException If a problem occurs while using the datasource
    */
   public void update(int rowInResultSet, DBDataSource dbds)
      throws SQLException {
      minRowNum = rowInResultSet;

      int r = rowInResultSet;

      while (r < getMaxCacheRows() && r < dbds.getNumRows()) {

         for (int c = 0; c < dbds.getNumDistinctColumns(); c++) {

            switch (dbds.getColumnType(c)) {

               case ColumnTypes.INTEGER:
               case ColumnTypes.DOUBLE:
               case ColumnTypes.FLOAT:
                  table.setDouble(dbds.getNumericData(r, c), r, c);

                  break;

               case ColumnTypes.STRING:
               case ColumnTypes.CHAR:
               case ColumnTypes.OBJECT:
                  table.setString(dbds.getTextData(r, c), r, c);

                  break;

               default:
                  table.setString(dbds.getTextData(r, c), r, c);
            }
         } // for loop processes all the columns for a particular row

         r++;
      } // end while

      // now update how many rows were written to the cache
      setNumCacheRows(r);

      maxRowNum = minRowNum + getNumCacheRows() - 1;
   } // end method update

} // end class Cache
