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

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * CacheFactory creates Cache objects. This class is not currently used.
 *
 * @author  suvalala
 * @version $Revision$, $Date$
 */
public class CacheFactory {

   //~ Static fields/initializers **********************************************

   /** Cache type key for FixedRowCache. */
   static public final int FIXED_ROW_NUM = 0;

   /** Holds the descriptions of the different <code>Cache</code> types. */
   static private final String[] DESCRIPTIONS = { "Fixed number of rows." };

   //~ Methods *****************************************************************

   /**
    * Factory method. Creates a new <code>Cache</code> instance.
    *
    * @param  type Type of cache to create
    * @param  rs   ResultSet
    *
    * @return New <code>Cache</code> instance
    *
    * @throws SQLException If a problem occurs while accessing the datasource
    */
   public Cache createCache(int type, ResultSet rs) throws SQLException {

      /*    switch(type) {
       *      case(0):         return new FixedRowCache(new
       * ResultSetDataSource(rs));     default:         return new
       * FixedRowCache(new ResultSetDataSource(rs)); }
       */
      return null;
   }

   /**
    * Returns a description for the specified <code>Cache</code> type.
    *
    * @param  type Type of cache to get a description for
    *
    * @return Description of the <code>Cache</code> type
    */
   public String getCacheDescription(int type) {

      if (type < DESCRIPTIONS.length) {
         return DESCRIPTIONS[type];
      } else {
         return "";
      }
   }
} // end class CacheFactory
