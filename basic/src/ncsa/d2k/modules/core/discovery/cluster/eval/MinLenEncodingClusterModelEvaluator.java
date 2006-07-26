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
package ncsa.d2k.modules.core.discovery.cluster.eval;

// ==============

// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;
import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;

import java.util.ArrayList;


/**
 * Overview: This module outputs a score for the input cluster model using a
 * minimum encoding length measurement. Detailed Description: The minimum
 * encoding length measure is affected by the number of clusters formed and the
 * amount of information that can be saved by tagging cluster entities with a
 * cluster ID. The intuition is that good clusterings will form clusters that
 * meaningfully limit the possibility of values thus economizing the amount of
 * information needed to describe the overall clustering Reference: M.-C. Ludl
 * and G. Widmer. Towards a Simple Clustering " Criterion Based on Minimum
 * Length Encoding. http://citeseer.nj.nec.com/542199.html"; Data Handling: The
 * input model is not modified. Scalability: This algorithm makes two passes
 * over the table that is part of the cluster model. It uses minimal heap
 * resources.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class MinLenEncodingClusterModelEvaluator extends ComputeModule {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 151973756070545865L;

   //~ Instance fields *********************************************************

   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   private boolean _verbose = false;

   //~ Methods *****************************************************************

   /**
    * Find an approximate max distance for the input table using only the
    * specified input features and distance metric. Return the thresh (% value)
    * times this distance.
    *
    * @param  itable    Table of examples
    * @param  ifeatures Features of interest
    *
    * @return A percent (thresh) of the maxdist
    */
   private Object[] calculateMinMax(Table itable, int[] ifeatures) {
      double maxdist = 0;

      // find distance threshold
      double[] max = new double[ifeatures.length];
      double[] min = new double[ifeatures.length];

      for (int i = 0, n = ifeatures.length; i < n; i++) {
         min[i] = Double.MAX_VALUE;
         max[i] = Double.MIN_VALUE;
      }

      for (int i = 0, n = itable.getNumRows(); i < n; i++) {

         for (int j = 0, m = ifeatures.length; j < m; j++) {
            double compval = itable.getDouble(i, ifeatures[j]);

            if (max[j] < compval) {
               max[j] = compval;
            }

            if (min[j] > compval) {
               min[j] = compval;
            }
         }
      }

      Object[] ret = new Object[2];
      ret[0] = min;
      ret[1] = max;

      return ret;
   } // end method calculateMinMax


   /**
    * Performs the main work of the module. In this case, the code calaulates
    * the score of the input cluster model's data.
    *
    * @throws Exception if the input model does not contain a table.
    */
   public void doit() throws Exception {

      try {

         ClusterModel cm = (ClusterModel) this.pullInput(0);

         int[] ifeatures = null; // indices of input features of the cluster
                                 // model's table

         if (!cm.hasTable()) {
            throw new Exception("The input model does not contain a table.");
         }

         // get the table
         Table tab = (Table) cm.getTable();

         // get the input features indices
         if (tab instanceof ExampleTable) {

            // if example table get it from the table
            ifeatures = ((ExampleTable) tab).getInputFeatures();
         } else {

            // otherwise, all columns are valid input features
            ifeatures = new int[tab.getNumColumns()];

            for (int i = 0, n = tab.getNumColumns(); i < n; i++) {
               ifeatures[i] = i;
            }
         }

         ArrayList clusters = cm.getClusters();
         double score = 0;

         Object[] obs = this.calculateMinMax(tab, ifeatures);
         double[] min = (double[]) obs[0];
         double[] max = (double[]) obs[1];
         double s = 0;

         // for each input column j
         for (int j = 0, k = ifeatures.length; j < k; j++) {

            // if not a boolean column - add to s max-min
            if (tab.getColumnType(ifeatures[j]) != ColumnTypes.BOOLEAN) {
               s += max[j] - min[j];
            } else {

               // handling a boolean column...
               s += ((max[j] - min[j]) > .5) ? 2 : 1;
            }
         }

         // calculating the score
         score = clusters.size() * 2 * s;
         score += clusters.size() * tab.getNumRows();

         // for each cluster
         for (int i = 0, n = clusters.size(); i < n; i++) {
            TableCluster tc = (TableCluster) clusters.get(i);

            int[] rows = tc.getMemberIndices();

            // get the subset table this cluster refers to and calculate its
            // score
            Table ctab = tab.getSubset(tc.getMemberIndices());
            obs = this.calculateMinMax(ctab, ifeatures);
            min = (double[]) obs[0];
            max = (double[]) obs[1];
            s = 0;

            for (int j = 0, k = ifeatures.length; j < k; j++) {

               if (tab.getColumnType(ifeatures[j]) != ColumnTypes.BOOLEAN) {
                  s += max[j] - min[j];
               } else {
                  s += ((max[j] - min[j]) > .5) ? 2 : 1;
               }
            }

            // add to score
            score += tc.getSize() * s;
         }

         // push out the score encapsulated in a parameter point object
         double[] utility = new double[1];
         utility[0] = score;

         String[] names = new String[1];
         names[0] = "Score";

         ParameterPoint objectivePoint =
            ParameterPointImpl.getParameterPoint(names, utility);
         this.pushOutput(objectivePoint, 0);

         if (getVerbose()) {
            System.out.println("MinLenEncodingClusterModelEvaluator: " +
                               "Score for this model -- " +
                               score);
         }

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("EXCEPTION: MinLenEncodingClusterModelEvaluator." +
                            "doit()");
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
            return "This is the ClusterModel that will be evaluated.";

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
         "This module outputs a score for the input cluster model using a " +
         "minimum ";
      s += "encoding length measurement.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "The minimum encoding length measure is affected by the number of " +
         "clusters ";
      s +=
         "formed and the amount of information that can be saved by tagging " +
         "cluster ";
      s +=
         "entities with a cluster ID.  The intuition is that good " +
         "clusterings ";
      s +=
         "will form clusters that meaningfully limit the possibility of " +
         "values thus ";
      s +=
         "economizing the amount of information needed to describe the " +
         "overall ";
      s += "clustering.";
      s += "</p>";
      s += "<p>";
      s +=
         "Reference: M.-C. Ludl and G. Widmer. Towards a Simple Clustering " +
         "Criterion ";
      s +=
         "Based on Minimum Length Encoding. " +
         "http://citeseer.nj.nec.com/542199.html";
      s += "</p>";

      s += "<p>Data Handling: ";
      s += "The input model is not modified.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This algorithm makes two passes over the table that is part of " +
         "the cluster ";
      s += "model.  It uses minimal heap resources.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "MLE Cluster Model Evaluator"; }


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
            return "This is the score for this model.";

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
            return "ParameterPoint";

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
      String[] types =
      { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };

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
         new PropertyDescription("verbose", "Verbose Output",
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

} // end class MinLenEncodingClusterModelEvaluator
