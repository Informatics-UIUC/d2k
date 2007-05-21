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
package ncsa.d2k.modules.core.discovery.cluster.util;

// ==============

// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Sparse;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;

import java.util.ArrayList;
import ncsa.d2k.modules.core.util.*;


/**
 * <P>Overview:<BR>
 * </P>
 *
 * <p>This module adds an attribute to each example in the table contained in
 * the input <i>Cluster Model</i>. The new attribute specifies the cluster label
 * as an integer value.</p>
 *
 * <P>Detailed Description: A <i>Cluster Model</i> contains the table that was
 * the original input to the clustering algorithm. An additional attribute
 * (column) is added to this table with the cluster assignments for each example
 * (row). The new attribute is of type integer.</p>
 *
 * <p>The <i>Output Table Only</i> property controls whether the modified table
 * or the entire Cluster Model, which includes the modified table, is output.
 * The <i>Cluster Model</i> object implements the Table interface and delegates
 * all calls to that interface to the contained table.</p>
 *
 * <p>Data Type Restrictions:</p>
 *
 * <p>The input <i>Cluster Model</i> must contain a table, and that table must
 * be mutable.</p>
 *
 * <p>Data Handling: This module will modify (as described above) the table that
 * is contained in the input <i>Cluster Model</i>. If the table is an example
 * table, the new column is added as an output attribute.</p>
 *
 * <p>Scalability: The module does not create a new table but adds a column to
 * the existing table. The cost of this operation will vary depending on the
 * table implementation type. For in-memory table implementations, it will add a
 * memory cost equal to the size of one integer per example in the table.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class AddClusterAssignmentsToTable extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -4673023089534248371L;

   //~ Instance fields *********************************************************

   /**
    * The Output Table Only property - a flag: If this property is true, the
    * modified table with the cluster assignment attribute is output. If false,
    * the Cluster Model, which includes the modified table, is output.
    */
   private boolean _tableOnly = true;

   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   private boolean _verbose = false;

   //~ Methods *****************************************************************
   private D2KModuleLogger myLogger;
   
   public void beginExecution() {
	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   }
   /**
    * Performs the main work of the module: This module adds an attribute to
    * each example in the table contained in the input <i>Cluster Model</i>. ";
    * The new attribute specifies the cluster label as an integer value.
    *
    * @throws Exception If the input model does not contain a table, or if the
    *                   table is empty.
    */
   public void doit() throws Exception {

      boolean exceptionFlag = false;

      try {

         ClusterModel cm = (ClusterModel) this.pullInput(0);

         if (!cm.hasTable()) {
            exceptionFlag = true;
            throw new Exception(getAlias() +
                             ": The input model does not contain a table.");
         }

         if (!(cm.getTable() instanceof MutableTable)) {
            exceptionFlag = true;
            throw new Exception(getAlias() +
                         ": The input model does not contain a mutable table.");
         }

         MutableTable itable = (MutableTable) cm.getTable();

         // remove pre-existing cluster column
         if (
             itable.getColumnLabel(itable.getNumColumns() - 1).equals(
               HAC._CLUSTER_COLUMN_LABEL)) {
            itable.removeColumn(itable.getNumColumns() - 1);
         }

         if (itable instanceof Sparse) {
            itable.addColumn(((Sparse) itable).getTableFactory().createColumn(
            ColumnTypes.INTEGER));
         } else {
            itable.addColumn(new IntColumn(itable.getNumRows()));
         }

         itable.setColumnLabel(HAC._CLUSTER_COLUMN_LABEL, itable
                                  .getNumColumns() - 1);

         if (itable instanceof ExampleTable) {
            int[] outs = ((ExampleTable) itable).getOutputFeatures();
            int[] newouts = new int[outs.length + 1];
            System.arraycopy(outs, 0, newouts, 0, outs.length);

            // LAM-tlr, I change the following : newouts[newouts.length-1] =
            // newouts.length-1; to:
            newouts[newouts.length - 1] = itable.getNumColumns() - 1;
            ((ExampleTable) itable).setOutputFeatures(newouts);
         }

         ArrayList resultClusters = cm.getClusters();

         for (int i = 0, n = resultClusters.size(); i < n; i++) {
            TableCluster tc = (TableCluster) resultClusters.get(i);
            int[] rows = tc.getMemberIndices();
            int col = itable.getNumColumns() - 1;

            for (int j = 0, m = rows.length; j < m; j++) {
               itable.setInt(i, rows[j], col);
            }

            if (getVerbose()) {
          	  myLogger.setDebugLoggingLevel();//temp set to debug
          	  myLogger.debug(getAlias() + ": Cluster " +
                                  tc.getClusterLabel() + " containing " +
                                  tc.getSize() + " elements.");
              myLogger.resetLoggingLevel();//re-set level to original level
            }
         }

         if (this.getTableOnly()) {
            this.pushOutput(itable, 0);
         } else {
            this.pushOutput(cm, 0);
         }

      } catch (Exception ex) {

         if (!exceptionFlag) {
            ex.printStackTrace();
            myLogger.error("EXCEPTION: " + getAlias() + ": " +
                               ex.getMessage());
         }

         throw ex;
      }
   } // end method doit


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "The Cluster Model whose table will be modified.";

         default:
            return "No such input.";
      }
   }

   /**
    * Returns the name of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int inputIndex) {

      switch (inputIndex) {

         case 0:
            return "Cluster Model";

         default:
            return "No such input";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types =
      { "ncsa.d2k.modules.core.discovery.cluster.ClusterModel" };

      return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module adds an attribute to each example in the table contained"
         + " in the input <i>Cluster Model</i>. ";
      s +=
      "The new attribute specifies the cluster label as an integer value. ";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
       "A <i>Cluster Model</i> contains the table that was the original input ";
      s +=
         "to the clustering algorithm.  An additional attribute (column) is "
         + "added to this ";
      s += "table with the cluster assignments for each example (row).  ";
      s += "The new attribute is of type integer.  ";
      s += "</p>";

      s += "<p>";
      s += "The <i>Output Table Only</i> property controls whether the ";
      s += "modified table or the entire Cluster Model, ";
      s += "which includes the modified table, is output. ";
      s +=
         "The <i>Cluster Model</i> object implements the Table interface and "
         + "delegates ";
      s += "all calls to that interface to the contained table.";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s +=
         "The input <i>Cluster Model</i> must contain a table, and that table "
         + "must be mutable.";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "This module will modify (as described above) the table that is "
         + "contained in ";
      s +=
         "the input <i>Cluster Model</i>. If the table is an example table, "
         + "the ";
      s += "new column is added as an output attribute.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "The module does not create a new table but adds a column to the existing table. ";
      s +=
         "The cost of this operation will vary depending on the table implementation type. ";
      s +=
         "For in-memory table implementations, it will add a memory cost equal to the size of one integer ";
      s += "per example in the table. ";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Add Cluster Assignments"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "The modified table, possibly as part of the full Cluster Model.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a name should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "Table or Cluster Model";

         default:
            return "No such output";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[2];

      descriptions[0] =
         new PropertyDescription("tableOnly",
                   "Output Table Only",
                   "If this property is true, the modified table with the cluster assignment attribute is output. " +
                   "If false, the Cluster Model, which includes the modified table, is output. ");

      descriptions[1] =
         new PropertyDescription("verbose",
                                 "Generate Verbose Output",
                                 "If this property is true, the module will write verbose status information to the console.");

      return descriptions;
   }

   /**
    * Returns the value of the output table only flag.
    *
    * @return boolean The value of the output table only flag.
    */
   public boolean getTableOnly() { return _tableOnly; }


   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return _verbose; }

   /**
    * Sets the value for the output table only flag.
    *
    * @param b The value for the output table only flag.
    */
   public void setTableOnly(boolean b) { _tableOnly = b; }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b If true, this setter method puts this module in verbose mode.
    */
   public void setVerbose(boolean b) { _verbose = b; }
} // end class AddClusterAssignmentsToTable
