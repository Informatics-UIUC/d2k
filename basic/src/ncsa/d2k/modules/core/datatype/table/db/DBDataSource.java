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

/**
 * <p>This is a DBConnection that is 'bound' to a particular
 * database. Objects of this class are bound to a database, tables within the
 * database, and columns within those tables.</p>
 *
 * @author  suvalala
 * @version $Revision$, $Date$
 */
public interface DBDataSource extends java.io.Serializable {

   //~ Methods *****************************************************************

   /**
    * Returns a copy of this instance.
    *
    * @return Copy of this instance
    */
   public DBDataSource copy();

   /**
    * Get boolean data from (row, col).
    *
    * @param  row Row to get the data from
    * @param  col Column to get the data from
    *
    * @return get boolean data from (row, col).
    */
   public boolean getBooleanData(int row, int col);

   /**
    * Get the comment for the specified column.
    *
    * @param  i Column index
    *
    * @return get the comment for the specified column.
    */
   public String getColumnComment(int i);

   /**
    * Get the label of the specified column.
    *
    * @param  i Column index
    *
    * @return get the label of the specified column.
    */
   public String getColumnLabel(int i);

   /**
    * Get the column type from ColumnTypes.
    *
    * @param  i Column index
    *
    * @return get the column type from ColumnTypes.
    */
   public int getColumnType(int i);

   /**
    * Get the number of distinct columns.
    *
    * @return get the number of distinct columns.
    */
   public int getNumDistinctColumns();

   /**
    * Get numeric data from (row, col).
    *
    * @param  row Row to get the data from
    * @param  col Column to get the data from
    *
    * @return get numeric data from (row, col).
    */
   public double getNumericData(int row, int col);

   /**
    * Get the number of rows.
    *
    * @return get the number of rows.
    */
   public int getNumRows();

   /**
    * Get an Object from (row, col);
    *
    * @param  row Row to get the data from
    * @param  col Column to get the data from
    *
    * @return get an Object from (row, col);
    */
   public Object getObjectData(int row, int col);

   /**
    * Get textual data from (row, col).
    *
    * @param  row Row to get the data from
    * @param  col Column to get the data from
    *
    * @return get textual data from (row, col).
    */
   public String getTextData(int row, int col);

} // end interface DBDataSource
