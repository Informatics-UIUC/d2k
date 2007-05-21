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


// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;

import java.util.ArrayList;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Title: CreateTableOfClusterCentroids</p>
 *
 * <p>Description: This module takes as input a ClusterModel and outputs a new
 * table containing one row for each cluster in the model. The values in these
 * rows represent the centroids of the clusters. It can be used as the final
 * stage of a table transformation where a table's rows are clustered and the
 * centroids are used to represent an "abbreviated" form of the original data.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  $Author$
 * @version 1.0
 */
public class CreateTableOfClusterCentroids extends DataPrepModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 7856549705016422330L;

   //~ Instance fields *********************************************************

   /** indices into the input table's columns, that are its input features. */
   private int[] _ifeatures = null;


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
    * Performs the main work of the module: The module takes as input a
    * ClusterModel and outputs a new table containing one row for each cluster
    * in the model. The values in these rows represent the centroids of the
    * clusters. It can be used as the final stage of a table transformation
    * where a table's rows are clustered and the centroids are used to represent
    * an "abbreviated" form of the original data.
    *
    * @throws Exception if the input model does not contain a table.
    */
   public void doit() throws Exception {

      try {

         ClusterModel cm = (ClusterModel) this.pullInput(0);

         if (!cm.hasTable()) {
            throw new Exception("CreateTableOfClusterCentroids: The input model"
                               + " does not contain a table.");
         }

         if (!(cm.getTable() instanceof MutableTable)) {
            throw new Exception("CreateTableOfClusterCentroids: The input model"
                               + " does not contain a mutable table.");
         }

         MutableTable itable = (MutableTable) cm.getTable();

         MutableTable newtab = (new MutableTableImpl()).createTable();
         ArrayList clusters = cm.getClusters();

         if (clusters.size() == 0) {
            throw new Exception("CreateTableOfClusterCentroids: The input model"
                                + " contains no clusters.");
         }

         TableCluster tc = null; // (TableCluster)clusters.get(0);

         if (itable instanceof ExampleTable) {
            _ifeatures = ((ExampleTable) itable).getInputFeatures();
         } else {
            _ifeatures = new int[itable.getNumColumns()];

            for (int i = 0, n = itable.getNumColumns(); i < n; i++) {
               _ifeatures[i] = i;
            }
         }

         for (int i = 0, n = _ifeatures.length; i < n; i++) {
            newtab.addColumn(new DoubleColumn(clusters.size()));
            newtab.setColumnComment(itable.getColumnComment(_ifeatures[i]), i);
            newtab.setColumnLabel(itable.getColumnLabel(_ifeatures[i]), i);
         }

         for (int i = 0, n = clusters.size(); i < n; i++) {
            tc = (TableCluster) clusters.get(i);

            double[] row = tc.getCentroid();

            for (int j = 0, m = _ifeatures.length; j < m; j++) {
               newtab.setDouble(row[j], i, j);
            }
         }

         if (getVerbose()) {
       	  myLogger.setDebugLoggingLevel();//temp set to debug
       	  myLogger.debug("CreateTableOfClusterCentroids: "
                  + "Outputting table with " +
                  newtab.getNumRows() + " rows.");
          myLogger.resetLoggingLevel();//re-set level to original level
         }

         this.pushOutput(newtab, 0);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         myLogger.error("EXCEPTION: CreateTableOfClusterCentroids.doit()");
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
            return "This is the ClusterModel that will be used to "
                    +  "create the new table.";

         default:
            return "No such input";
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
            return "ClusterModel";

         default:
            return "NO SUCH INPUT!";
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
         "This module takes as input a ClusterModel and outputs a new "
         + "table containing ";
      s +=
         "one row for each cluster in the model.  The values in these rows "
         + "represent the ";
      s +=
         "centroids of the clusters.  It can be used as the final stage of a "
         + "table transformation ";
      s +=
         "where a table's rows are clustered and the centroids are used to "
         + "represent an ";
      s += "\"abbreviated\" form of the original data.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "ClusterModels usually contain the tables that were the original "
         +
         " input ";
      s +=
         "to the clustering algorithm as well as the clusters that were formed."
       + "  This ";
      s +=
         "module will create a new table that contains only the centroids of "
         + "each cluster ";
      s +=
         "from the model.  Each row in the new table corresponds to one "
         + "centroid from a cluster.  ";
      s +=
         "The centroid values are all doubles because they represent points "
         + "in a continuous space.  ";
      s +=
         "Therefore, even if the original data type for an attribute was not "
         + "a double (say boolean ";
      s +=
         "or int), the value in the centroid row for that attribute is most "
         + "likely no longer ";
      s +=
         "representable as a whole number.  In any case, the types for each "
         + "column in the new table are ";
      s += "all double.";
      s += "</p>";

      s += "<p>Data Type Restrictions: ";
      s += "The input ClusterModel must contain a table.";
      s += "</p>";

      s += "<p>Data Handling: ";
      s += "This module does not modify the input in any way.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This module creates a table of the same implementation type as that "
         + "contained in the ";
      s += "model. This table is wholly new and may reside entirely in memory.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Create Table of Centroids"; }

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
            return "This is the newly created table of cluster centroids.";

         default:
            return "No such output";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      switch (outputIndex) {

         case 0:
            return "Table";

         default:
            return "NO SUCH OUTPUT!";
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
      PropertyDescription[] descriptions = new PropertyDescription[1];

      descriptions[0] =
         new PropertyDescription("verbose",
                                 "Verbose Output",
                                 "Do you want verbose output to the console.");

      return descriptions;
   }

   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return _verbose; }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { _verbose = b; }
} // end class CreateTableOfClusterCentroids
