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


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

import java.util.*;


/**
 * <p>Title: FractionationSampler</p>
 *
 * <p>Description:</p>
 *
 * Overview: Chooses a sample set of rows through a process of repeated
 * partitioning and clustering. The table rows are treated as vectors of a
 * vector space.
 *
 * <p>Detailed Description: This module sorts the initial examples (converted to
 * clusters) by a key attribute denoted by <i>Sort Attribute</i>. The set of
 * sorted clusters is then segmented into equal partitions of size <i>Max
 * Partition Size</i>. Each of these partitions is then passed through the
 * agglomerative clusterer to produce <i>Number Of Clusters</i> clusters. All
 * the clusters are gathered together for all partitions and the entire process
 * is repeated until only <i>Number of Clusters</i> clusters remain. The sorting
 * step is to encourage like clusters into same partitions.</p>
 *
 * <p>Data Handling: The original table input is not modified but it is included
 * in the final <i>Cluster Model</i>.</p>
 *
 * <p>Scalability: This module time complexity of O(<i>Number of Examples</i> *
 * <i>Partition Size</i>). Each iteration creates <i>Number of Examples</i> <i>
 * Table Cluster</i> objects.</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: NCSA Automated Learning Group</p>
 *
 * @author  D. Searsmith
 * @version 1.0
 */

public class FractionationSampler extends FractionationSamplerOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 9129757801806071851L;

   //~ Constructors ************************************************************


   /**
    * Creates a new FractionationSampler object.
    */
   public FractionationSampler() { }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module: Chooses a sample set of rows through
    * a process of repeated partitioning and clustering. The table rows are
    * treated as vectors of a vector space.
    */
   protected void doit() {

      ArrayList clusters = null;
      ClusterModel cm = null;

      try {

         if (_pushing.size() > 0) {
            ArrayList arrlist = new ArrayList((ArrayList) _pushing.remove(0));
            ClusterModel mod = new ClusterModel(_itable, arrlist, null);
            this.pushOutput(mod, 0);

            if (this.getFlags()[1] == 0) {
               return;
            }

         }

         if (this.getFlags()[0] > 0) {
            _itable = (MutableTable) this.pullInput(0);

            if (this.getCheckMissingValues()) {

               if (_itable.hasMissingValues()) {
                  throw new TableMissingValuesException("FractionationSampler: Please replace or filter out missing values in your data.");
               }
            }
         }

         if (this.getFlags()[1] > 0) {
            cm = (ClusterModel) this.pullInput(1);
            clusters = cm.getClusters();
         }

         doingit(clusters);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("ERROR: FractionationSampler.doit()");
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

      if (inputIndex == 0) {
         return "Table of values to cluster";
      } else if (inputIndex == 2) {
         return "Cluster Model from clusterer";
      } else {
         return "";
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

      if (inputIndex == 0) {
         return "Table";
      } else if (inputIndex == 1) {
         return "ClusterModel";
      } else {
         return "";
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
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"
      };

      return in;
   }


   /**
    * Returns the value for the Max Partition Size property.
    *
    * @return int The value of the Max Partition Size property
    */
   public int getmaxPartitionSize() { return m_maxPartitionSize; }

   /**
    * Returns the value of the Sort Attribute property.
    *
    * @return int The value of the Sort Attribute property
    */
   public int getNthSortTerm() { return m_nthSortTerm; }

   /**
    * Returns the number of clusters to be formed.
    *
    * @return int The number of clusters to be formed
    */
   public int getNumberOfClusters() { return m_numberOfClusters; }


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
         new PropertyDescription("numberOfClusters",
                                 "Number of Clusters",
                                 "This property specifies the number of clusters to form (>= 2).");
      descriptions[1] =
         new PropertyDescription("NthSortTerm",
                                 "Sort Attribute",
                                 "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
      descriptions[2] =
         new PropertyDescription("maxPartitionSize",
                                 "Max Partition Size",
                                 "The size of partitions to use in the sampling process.");
      descriptions[3] =
         new PropertyDescription("CheckMissingValues",
                                 "Check Missing Values",
                                 "Perform a check for missing values on the table inputs (or not).");
      descriptions[4] =
         new PropertyDescription("verbose",
                                 "Verbose Output",
                                 "Do you want verbose output to the console.");

      return descriptions;
   }


   /**
    * Returns a custom properties editor, to allow the user to set the values of
    * the properties.
    *
    * @return CustomModuleEditor a GUI component that allows the user to set the
    *         values of the properties of this module.
    */
   public CustomModuleEditor getPropertyEditor() {
      return new FractionationSampler_Props(this, true, true);
   }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run. This module is ready to have its doit method called when either of
    * its inputs are satisfied or when the array list of the partitions has
    * elements in it.
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (
          (this.getFlags()[0] > 0) ||
             (this.getFlags()[1] > 0) ||
             (_pushing.size() > 0)) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Sets the value of the Max Partition Size property.
    *
    * @param noc int The value for the Max Partition Size property
    */
   public void setMaxPartitionSize(int noc) { m_maxPartitionSize = noc; }

   /**
    * Sets the value of the Sort Attribute property.
    *
    * @param noc int The value for the Sort Attribute property
    */
   public void setNthSortTerm(int noc) { m_nthSortTerm = noc; }

   /**
    * Sets the value of the number of clusters property.
    *
    * @param noc int The value for the number of clusters property
    */
   public void setNumberOfClusters(int noc) { m_numberOfClusters = noc; }

} // end class FractionationSampler
