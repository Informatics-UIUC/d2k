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
package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.Column;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.TestTable;
import ncsa.d2k.modules.core.datatype.table.TrainTable;


/**
 * Basic implementation of the <code>TableFactory</code> interface.
 *
 * @author  clutter
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class BasicTableFactory implements TableFactory {

   //~ Methods *****************************************************************

   /**
    * Given an int value representing a type of <code>Column</code> (see the
    * {@link ncsa.d2k.modules.core.datatype.table.ColumnTypes} class) return a
    * <code>Column</code> of type consistent with this factory implementation.
    *
    * @param  col_type Type of <code>Column</code> to create
    *
    * @return <code>Column</code> for the specified type
    */
   public Column createColumn(int col_type) {
      return ColumnUtilities.createColumn(col_type, 0);
   }

   /**
    * Creates an <code>ExampleTable</code> from a <code>Table</code>.
    *
    * @param  table The table to replicate.
    *
    * @return <code>ExampleTable</code> with the same data as the <code>
    *         Table</code>
    */
   public ExampleTable createExampleTable(Table table) {
      return table.toExampleTable();
   }

   /**
    * Create a new <code>PredictionTable</code> from the given <code>
    * ExampleTable</code>. The <code>PredictionTable</code> will have an extra
    * column for each of the outputs in et.
    *
    * @param  et <code>ExampleTable</code> that contains the inital values
    *
    * @return <code>PredictionTable</code> initialized with the data from et
    */
   public PredictionTable createPredictionTable(ExampleTable et) {
      return et.toPredictionTable();
   }

   /**
    * Creates a new, empty <code>Table<code>.</code></code>
    *
    * @return New, empty Table
    */
   public Table createTable() { return new MutableTableImpl(); }

   /**
    * Creates a <code>Table<code>with the specified number of columns.
    *
    * @param  numColumns The number of columns to include in the table.
    *
    * @return A new, empty <code>Table<code>with the specified number of columns
    */
   public Table createTable(int numColumns) {
      return new MutableTableImpl(numColumns);
   }

   /**
    * Given an <code>ExampleTable</code>, create a new <code>TestTable</code>.
    * The <code>TestTable</code> will have an extra column for each of the
    * outputs in et. The rows of the <code>TestTable</code> will be the indices
    * of the test set in et.
    *
    * @param  et <code>ExampleTable</code> that this <code>TestTable</code> is
    *            derived from
    *
    * @return <code>TestTable</code> initialized with the data from et
    */
   public TestTable createTestTable(ExampleTable et) {
      return (TestTable) et.getTestTable();
   }

   /**
    * Given an <code>ExampleTable</code>, create a new <code>TrainTable</code>.
    * The rows of the <code>TrainTable</code> will be the indicies of the train
    * set in et.
    *
    * @param  et <code>ExampleTable</code> that the <code>TrainTable</code> is
    *            derived from
    *
    * @return a <code>TrainTable</code> initialized with the data from et
    */
   public TrainTable createTrainTable(ExampleTable et) {
      return (TrainTable) et.getTrainTable();
   }


} // end class BasicTableFactory
