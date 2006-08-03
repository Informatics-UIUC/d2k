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
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.StringColumn;

import java.sql.SQLException;


/**
 * Implements a cache of fixed rows.
 *
 * @author  mathur
 * @author  clutter
 * @version $Revision$, $Date$
 */
public class FixedRowCache extends Cache {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 7848839591234656835L;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>FixedRowCache</code> given a <code>ResultSet</code>.
    * The <code>ResultSet</code> is needed to get the datatypes and number of
    * columns. The cache is populated with the first N rows.
    *
    * @param  dbds Datasource to use to initialize the cache
    *
    * @throws SQLException Description of exception SQLException.
    */
   public FixedRowCache(DBDataSource dbds) throws SQLException {
      maxRowsInCache = 1000;
      initialize(dbds);
   }

   //~ Methods *****************************************************************

   /**
    * Initializes the cache.
    *
    * @param  dbds Datasource to use to initialize the cache
    *
    * @throws SQLException If a problem occurs while getting the data from the
    *                      datasource.
    */
   private void initialize(DBDataSource dbds) throws SQLException {
      table = new MutableTableImpl(dbds.getNumDistinctColumns());

      for (int col = 0; col < dbds.getNumDistinctColumns(); col++) {

         switch (dbds.getColumnType(col)) {

            case ColumnTypes.INTEGER:
            case ColumnTypes.DOUBLE:
            case ColumnTypes.FLOAT:
               table.setColumn(new DoubleColumn(getMaxCacheRows()), col);

               break;

            case ColumnTypes.STRING:
            case ColumnTypes.CHAR:
            case ColumnTypes.OBJECT:
               table.setColumn(new StringColumn(getMaxCacheRows()), col);

               break;

            default:
               table.setColumn(new StringColumn(getMaxCacheRows()), col);
         }
      }

      update(0, dbds);

   } // end method initialize

} // end class FixedRowCache
