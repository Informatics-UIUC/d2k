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
package ncsa.d2k.modules.core.discovery.cluster.sample;


// ==============
// Java Imports
// ==============

// ===============
// Other Imports
// ===============

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .ClusterAssignment_Props;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;


/**
 * <p>Title: ClusterAssignment</p>
 *
 * <p>Description: Takes a set of centroids and a table and repeatedly assigns
 * table rows to clusters whose centroids are closest in vector space. When one
 * assignment is completed new centroids are calculated and the process is
 * repeated.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class ClusterAssignment extends ClusterAssignmentOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -6992902478879888110L;

   //~ Constructors ************************************************************


   /**
    * Creates a new ClusterAssignment object.
    */
   public ClusterAssignment() { }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module: Takes a set of centroids and a table
    * and repeatedly assigns table rows to clusters whose centroids are closest
    * in vector space. When one assignment is completed new centroids are
    * calculated and the process is repeated.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module.
    */
   protected void doit() throws java.lang.Exception {
      Table initcenters = (Table) this.pullInput(0);
      Table initEntities = (Table) this.pullInput(1);
      ClusterRefinement refiner =
         new ClusterRefinement(this.getClusterMethod(),
                               this.getDistanceMetric(),
                               this.getNumAssignments(), this.getVerbose(),
                               this.getCheckMissingValues(), getAlias());
      this.pushOutput(refiner.assign(initcenters, initEntities), 0);
   }

   /**
    * Returns the value of the clustering method ID property.
    *
    * @return int The value of the clustering method ID property
    */

   public int getClusterMethod() { return _clusterMethod; }

   /**
    * Returns the value of the distance metric ID property.
    *
    * @return int The value of the distance metric ID property
    */

   public int getDistanceMetric() { return _distanceMetric; }

   /**
    * Return a description of a specific input.
    *
    * @param  parm1 The index of the input
    *
    * @return The description of the input
    */
   public String getInputInfo(int parm1) {

      if (parm1 == 0) {
         return "Cluster Model or Table containing the initial centroids.";
      } else if (parm1 == 1) {
         return "Table containing all the examples to cluster.";
      } else {
         return "No such input.";
      }
   }

   /**
    * Return the name of a specific input.
    *
    * @param  parm1 The index of the input.
    *
    * @return The name of the input
    */
   public String getInputName(int parm1) {

      if (parm1 == 0) {
         return "Cluster Model or Table";
      } else if (parm1 == 1) {
         return "Table";
      } else {
         return "No such input";
      }
   }

   /**
    * Return a String array containing the datatypes the inputs to this module.
    *
    * @return The datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }


   /**
    * Returns the value of Number of Assignments property.
    *
    * @return int The value of Number of Assignments property
    */
   public int getNumAssignments() { return m_numAssignments; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */

   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[5];

      descriptions[0] =
         new PropertyDescription("clusterMethod",
                                 CLUSTER_METHOD,
                                 "The method to use for determining the similarity between two clusters. " +
                                 "This similarity measure is used in formulating the tree that is part " +
                                 " of the final cluster model. " +
                                 "<p>WARDS METHOD: Use a minimum variance approach that sums the " +
                                 "squared error " +
                                 "(distance) for every point in the cluster to the cluster centroid.</p>" +
                                 "<p>SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
                                 "<p>COMPLETE LINK: Distance of furthest pair (one from each cluster)." +
                                 "</p>" +
                                 "<p>UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
                                 "<p>WPGMA: Weighted pair group method using arithmetic averages.</p>" +
                                 "<p>UPGMC: Unweighted pair group method using centroids.</p>" +
                                 "<p>WPGMC: Weighted pair group method using centroids.</p>");

      descriptions[1] =
         new PropertyDescription("numAssignments",
                                 MAX_ITERATIONS,
                                 "This property specifies the maximum number of iterations of cluster " +
                                 "assignment/refinement to perform. " +
                                 "It must be greater than 0.  A check is performed after each iteration " +
                                 "to determine if " +
                                 "the cluster centers have moved more than a small threshold amount. " +
                                 " If they have not, " +
                                 "the refinement process is stopped before the specified number " +
                                 "of iterations. ");

      descriptions[2] =
         new PropertyDescription("distanceMetric",
                                 DISTANCE_METRIC,
                                 "This property determines the type of distance function to use in " +
                                 " calculating the " +
                                 "distance between two examples.  This distance is used in assigning " +
                                 "points to clusters, and " +
                                 "in determining if there was sufficient movement since the last " +
                                 "assignment iteration " +
                                 "to continue the refinement process. " +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at " +
                                 "right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the " +
                                 " vectors denoted by two points.</p>");

      descriptions[3] =
         new PropertyDescription("checkMissingValues",
                                 CHECK_MV,
                                 "If this property is true, the module will perform a check for " +
                                 "missing values in the input table. ");

      descriptions[4] =
         new PropertyDescription("verbose",
                                 VERBOSE,
                                 "If this property is true, the module will write verbose status " +
                                 " information to the console.");

      return descriptions;
   } // end method getPropertiesDescriptions

   /**
    * Return a custom gui for setting properties.
    *
    * @return CustomModuleEditor
    */
   public CustomModuleEditor getPropertyEditor() {
      return new ClusterAssignment_Props(this, true, true);
   }

   /**
    * Sets the value of the clustering method ID property.
    *
    * @param cm int The value of the clustering method ID. Must be one of the
    *           values defined in <code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setClusterMethod(int cm) throws PropertyVetoException{
     int max = HAC.s_ClusterMethodLabels.length ;
     if(cm < 0 || cm >= max){
       String msg = "Cluster Method ID must be in the range " +
           "[0," + (max-1) + "]";
       msg += ". The Clustering Methods IDs are as follows: " ;
       for(int i=0; i<max; i++){
         msg += i + " - " +HAC.s_ClusterMethodLabels[i];
         if(i != max-1){
           msg += ", " ;
         }
       }
       throw new PropertyVetoException(msg, null);

     }
     _clusterMethod = cm;
   }

   /**
    * Sets the value of the distance metric ID property.
    *
    * @param dm int The value of the distance metric ID. Must be one of the
    *           values defined in <code>
    *           ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setDistanceMetric(int dm)  throws PropertyVetoException
   {
     int max = HAC.s_DistanceMetricDesc.length;
     if(dm < 0 || dm >= max){
       String msg = "Distance Metric ID must be in the range " +
           "[0," + (max-1) + "]";
       msg += ". The Distance Metrics IDs are as follows: " ;
       for(int i=0; i<max; i++){
         msg += i + " - " +HAC.s_DistanceMetricLabels[i];
         if(i != max-1){
           msg += ", " ;
         }
       }
       throw new PropertyVetoException(msg, null);

     }
     _distanceMetric = dm;
   }

   /**
    * Sets the value for the Number of Assignments property.
    *
    * @param noa int The value for Number of Assignments property
    */
   public void setNumAssignments(int noa) { m_numAssignments = noa; }
} // end class ClusterAssignment
