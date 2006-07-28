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
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;
import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;
import ncsa.d2k.modules.core.discovery.cluster.util.TableColumnTypeException;
import ncsa.d2k.modules.core.discovery.cluster.util.TableMissingValuesException;

import java.util.ArrayList;


/**
 * <p>Title: ClusterRefinement</p>
 *
 * <p>Description: Takes a set of centroids and a table and repeatedly assigns
 * table rows to clusters whose centroids are closest in vector space. When one
 * assignment is completed, new centroids are calculated and the process is
 * repeated.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */
public class ClusterRefinement {

   //~ Static fields/initializers **********************************************

   /** euclidean distance metric ID. */
   static public final int s_Euclidean = 0;

   /** manhattan distance metric ID. */
   static public final int s_Manhattan = 1;


   /** cosine distance metric ID. */
   static public final int s_Cosine = 2;

   //~ Instance fields *********************************************************

   /** Common name for this class. */
   private String _alias = "ClusterRefinement";

   /** the clustering method to be used. */
   private int _clusterMethod = HAC.s_WardsMethod_CLUSTER;

   /** the distance metric to be used. */
   private int _distanceMetric = s_Euclidean;

   /** indices into the input table, that are its input features indices. */
   private int[] _ifeatures = null;

   /** number of iterations for the clustering process. */
   private int m_numAssignments = 5;

   /** start time of computing. */
   private long m_start = 0;


   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   private boolean m_verbose = false;


   /**
    * Check missing values flag. If set to true, this module verifies prior to
    * computation, that there are no missing values in the input table.
    * (Clustering cannot be performed on missing values. In the presence of
    * missing values the module throws an Exception.)
    */
   protected boolean _mvCheck = true;

   //~ Constructors ************************************************************

   /**
    * Constructs a ClusterRefinement object.
    *
    * @param cm          The clustering method to be used. IDs for this property
    *                    are defined in <code>
    *                    ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    * @param dm          The distance metric to be used IDs for this property
    *                    are defined in <code>
    *                    ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    * @param na          Number of iterations to perform
    * @param ver         The value for the verbose flag
    * @param check       The value for the check missing values flag
    * @param moduleAlias The common name for this class
    */
   public ClusterRefinement(int cm, int dm, int na, boolean ver, boolean check,
                            String moduleAlias) {
      this._distanceMetric = dm;
      this._clusterMethod = cm;
      this.m_numAssignments = na;
      this.m_verbose = ver;
      this.setCheckMissingValues(check);
      this._alias = moduleAlias;
   }

   //~ Methods *****************************************************************

   /**
    * Computes similarity value for each of the elements in <code>
    * entities</code>, then for each of the profiles, assign it to the most
    * similar cluster in <code>centers.</code>
    *
    * @param  centers  contains TableCluster objects that are the centroids of
    *                  the clusters
    * @param  entities contains TableCluster objects
    *
    * @return ArrayList containing TableCluster objects that are merged items
    *         from <codE>entities</code>, according to their distance from the
    *         elements in <codE>centers</codE>
    *
    * @throws Exception
    */
   private ArrayList assignEntities(ArrayList centers, ArrayList entities)
      throws Exception {
      // for each entity compute similarity for each profile and assign to most
      // similar cluster

      TableCluster[] retarr = new TableCluster[centers.size()];

      for (int i = 0, in = entities.size(); i < in; i++) {
         TableCluster entity = (TableCluster) entities.get(i);
         double min = Double.MAX_VALUE;
         int ind = -1;

         for (int j = 0, m = centers.size(); j < m; j++) {
            double dist =
               HAC.distance(entity, (TableCluster) centers.get(j),
                            getDistanceMetric());

            if (dist < min) {
               min = dist;
               ind = j;
            }
         }

         if (retarr[ind] == null) {
            retarr[ind] = entity;
         } else {
            retarr[ind] = TableCluster.merge(entity, retarr[ind]);
         }
      }

      ArrayList retval = new ArrayList(retarr.length);

      for (int i = 0, n = retarr.length; i < n; i++) {

         if (retarr[i] != null) {
            retval.add(retarr[i]);
         }
      }

      return retval;
   } // end method assignEntities

   /**
    * Checks whether latest assignment had changed the clusters less than a
    * certain distance. Returns true if all clusters have not changed over that
    * constant. If at least one cluster had changed more than the constant
    * distance - returns flase.
    *
    * @param  oldcs An ArrayList of the old TableCluster objects
    * @param  newcs An ArrayList of the new TableCluster objects
    *
    * @return False if at least one of the clusters had changed, measured in
    *         distance greater than a certain constant. Returns true if all
    *         clusters had changed in distance less than the constant.
    */
   private boolean evaluation(ArrayList oldcs, ArrayList newcs) {

      for (int i = 0, n = oldcs.size(); i < n; i++) {
         TableCluster tcold = (TableCluster) oldcs.get(i);
         boolean found = false;

         for (int j = 0, m = newcs.size(); j < m; j++) {
            TableCluster tcnew = (TableCluster) newcs.get(j);
            double dist = HAC.distance(tcold, tcnew, getDistanceMetric());

            if (dist < .0000001) {
               found = true;

               break;
            }
         } // for j

         if (!found) {
            return false;
         }
      } // for i

      return true;
   }


   /**
    * Takes a set of centroids and a table and repeatedly assigns table rows to
    * clusters whose centroids are closest in vector space. When one assignment
    * is completed, new centroids are calculated and the process is repeated.
    *
    * @param  initcenters  The initial cluster points (rows in a table)
    * @param  initEntities The set of examples to be clustered
    *
    * @return ClusterModel
    *
    * @throws java.lang.Exception         <BR>
    *                                     If check missing vlaues property is on
    *                                     and the table has missing values in
    *                                     it. or if table has nominal data in it
    * @throws TableMissingValuesException If check missing values flag is true
    *                                     and the input table has missing values
    *                                     in it.
    */
   public ClusterModel assign(Table initcenters, Table initEntities)
      throws java.lang.Exception {
      ClusterModel model = null;

      if (this.getCheckMissingValues()) {

         if (initEntities.hasMissingValues()) {
            throw new TableMissingValuesException(getAlias() +
              ": Please replace or filter out missing values in your data.\n" +
              "This module does not work with missing values. ");
         }
      }

      boolean exceptionFlag = false;

      try {
         m_start = System.currentTimeMillis();

         ArrayList centers = null;
         ArrayList entities = null;

         /**
          * Assume that both tables input have the exact same input features in
          * the same columns.
          */

         if (initcenters instanceof ClusterModel) {
            centers = ((ClusterModel) initcenters).getClusters();
         } else {

            if (initcenters instanceof ExampleTable) {
               _ifeatures = ((ExampleTable) initcenters).getInputFeatures();
            } else {
               _ifeatures = new int[initcenters.getNumColumns()];

               for (int i = 0, n = initcenters.getNumColumns(); i < n; i++) {
                  _ifeatures[i] = i;
               }
            }

            // Validate the column types -- can only operate on numeric or
            // boolean types.
            for (int i = 0, n = _ifeatures.length; i < n; i++) {
               int ctype = initcenters.getColumnType(_ifeatures[i]);

               if (
                   !((ctype == ColumnTypes.BYTE) ||
                        (ctype == ColumnTypes.BOOLEAN) ||
                        (ctype == ColumnTypes.DOUBLE) ||
                        (ctype == ColumnTypes.FLOAT) ||
                        (ctype == ColumnTypes.LONG) ||
                        (ctype == ColumnTypes.INTEGER) ||
                        (ctype == ColumnTypes.SHORT))) {

                  exceptionFlag = true; // so we don't print message 2X

                  String cname = initcenters.getColumnLabel(_ifeatures[i]);
                  Exception ex1 =
                     new TableColumnTypeException(ctype,
                  "\n" +
                  getAlias() +
                  " module: Only boolean and numeric types are permitted.\n" +
                  "Nominal data types must be transformed by a scalarization " +
                  "module or removed " +
                  "prior to this module. \n" +
                  "Column named " + cname +
                  " has type " +
                  ColumnTypes.getTypeName(ctype));
                  throw ex1;
               }
            } // end for

            // Build clusters for each table row.
            centers = new ArrayList(initcenters.getNumRows());

            for (int i = 0, n = initcenters.getNumRows(); i < n; i++) {
               TableCluster tc = new TableCluster(initcenters, i);
               centers.add(tc);
            }
         } // end if

         if (_ifeatures == null) {

            if (initEntities instanceof ExampleTable) {
               _ifeatures = ((ExampleTable) initEntities).getInputFeatures();
            } else {
               _ifeatures = new int[initEntities.getNumColumns()];

               for (int i = 0, n = initEntities.getNumColumns(); i < n; i++) {
                  _ifeatures[i] = i;
               }
            }
         }

         // Validate the column types -- can only operate on numeric or boolean
         // types.
         for (int i = 0, n = _ifeatures.length; i < n; i++) {
            int ctype = initEntities.getColumnType(_ifeatures[i]);

            if (
                !((ctype == ColumnTypes.BYTE) ||
                     (ctype == ColumnTypes.BOOLEAN) ||
                     (ctype == ColumnTypes.DOUBLE) ||
                     (ctype == ColumnTypes.FLOAT) ||
                     (ctype == ColumnTypes.LONG) ||
                     (ctype == ColumnTypes.INTEGER) ||
                     (ctype == ColumnTypes.SHORT))) {

               exceptionFlag = true; // so we don't print message 2X

               String cname = initEntities.getColumnLabel(_ifeatures[i]);
               Exception ex1 =
                  new TableColumnTypeException(ctype,
                 "\n" +
                 getAlias() +
                 " module: Only boolean and numeric types are permitted.\n" +
                 "Nominal data types must be transformed by a scalarization " +
                 "module or removed " +
                 "prior to this module. \n" +
                 "Column " + cname +
                 " has type " +
                 ColumnTypes.getTypeName(ctype));
               throw ex1;
            }
         } // end for

         // Build clusters for each table row.
         entities = new ArrayList(initEntities.getNumRows());

         for (int i = 0, n = initEntities.getNumRows(); i < n; i++) {
            TableCluster tc = new TableCluster(initEntities, i);
            entities.add(tc);
         }

         if (getVerbose()) {
            System.out.println("\n>>> BEGINNING ASSIGNMENT REFINEMENT ... ");
         }

         for (int i = 0, n = m_numAssignments; i < n; i++) {

            // assign each element to one of k clusters
            if (getVerbose()) {
               System.out.println("ClusterRefinement -- assigning entities " +
                                  "pass " + (i + 1) + " out of " +
                                  m_numAssignments +
                                  " :: " +
                                  centers.size() + " input.");
            }

            ArrayList oldCenters = centers;
            centers = assignEntities(centers, entities);

            // insert check here to evaluate if assignments should continue
            if (evaluation(oldCenters, centers)) {

               if (getVerbose()) {
                  System.out.println("New assignment has not changed " +
                                     "significantly, assignments stopped at " +
                                     (i + 1) + " iterations.");
               }

               break;
            }
         }

         // cut the tree
         for (int i = 0, n = centers.size(); i < n; i++) {
            ((TableCluster) centers.get(i)).cut();
         }

         if (getVerbose()) {
            System.out.println(">>> ASSIGNMENT REFINEMENT COMPLETE\n");
         }

         model = new ClusterModel(initEntities, centers, null);

         int numCenters = centers.size(); // save this value as reset in HAC

         // build the rest of the tree and add the cluster row to the model
         HAC hac =
            new HAC(this.getClusterMethod(), getDistanceMetric(),
                    centers.size(), 0, getVerbose(), false, getAlias());
         model = hac.buildModel(model);

         long end = System.currentTimeMillis();

         if (getVerbose()) {
            System.out.println("\nEND EXEC -- " + getAlias() + " -- " +
                               numCenters +
                               " built in " + (end - m_start) / 1000 +
                               " seconds\n");
         }

      } catch (Exception ex) {

         if (!exceptionFlag) {
            ex.printStackTrace();
            System.out.println("ERROR: " + getAlias() + ": " + ex.getMessage());
         }

         throw ex;
      }

      return model;
   } // end method assign

   /**
    * Returns the common name for this class.
    *
    * @return String The common name of this class
    */
   public String getAlias() { return _alias; }

   /**
    * Return the value if the check missing values flag.
    *
    * @return The value of the check missing values flag.
    */
   public boolean getCheckMissingValues() { return _mvCheck; }

   /**
    * Returns the value of the clustering method property.
    *
    * @return int The value of the clustering method property
    */
   public int getClusterMethod() { return _clusterMethod; }

   /**
    * Returns the distance metric property.
    *
    * @return int The distance metric property ID
    */
   public int getDistanceMetric() { return _distanceMetric; }

   /**
    * Returns the value of number of assignments property.
    *
    * @return int The value of number of assignments property
    */
   public int getNumAssignments() { return m_numAssignments; }


   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return m_verbose; }

   /**
    * Sets the common name for this class.
    *
    * @param moduleAlias A common name for this class
    */
   public void setAlias(String moduleAlias) { _alias = moduleAlias; }

   /**
    * Sets the check missing values flag.
    *
    * @param b If true this module verifies there are no missing values in the
    *          input table. In the presence of missing values the module throws
    *          an Exception.
    */
   public void setCheckMissingValues(boolean b) { _mvCheck = b; }

   /**
    * Sets the value for the clustering method property.
    *
    * @param noc int The clustering method ID. Should be one of the values
    *            defined in <code>
    *            ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
    */
   public void setClusterMethod(int noc) { _clusterMethod = noc; }

   /**
    * Sets the value of the distance metric property.
    *
    * @param dm int the ID of the distance metric. Must be one of the values of
    *           the static fileds of this class
    */
   public void setDistanceMetric(int dm) { _distanceMetric = dm; }

   /**
    * Sets the value for thenumber of assignment property.
    *
    * @param noc int number of iterations to perform (should be greater than
    *            zero)
    */
   public void setNumAssignments(int noc) { m_numAssignments = noc; }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { m_verbose = b; }

} // end class ClusterRefinement
