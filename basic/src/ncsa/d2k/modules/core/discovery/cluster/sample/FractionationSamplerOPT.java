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


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.ClusterModel;
import ncsa.d2k.modules.core.discovery.cluster.util.TableCluster;
import ncsa.d2k.modules.core.discovery.cluster.util.TableMissingValuesException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;


/**
 * <p>Title: FractionationSamplerOPT</p>
 *
 * <p>Description:</p>
 *
 * <p>Overview: Chooses a sample set of rows through a process of repeated
 * partitioning and clustering. The table rows are treated as vectors of a
 * vector space.</p>
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
 * <p>Scalability: This module time complexity of O(<i>Number of Examples</i>
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

public class FractionationSamplerOPT extends ComputeModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 6057617283751756986L;

   //~ Instance fields *********************************************************

   /** An ArrayList of TableCluster objects. */
   private ArrayList m_clusters = null;

   /**
    * a temp array list of TableCluster objects. When it has enough clusters the
    * ArrayList is wrapped in a ClusterModel object and output
    */
   private ArrayList m_clustersHold = null;

   // private ArrayList m_clusters2 = null;

   /**
    * a count down from number of expected clusters to zero. clusters are
    * accumulated in <code>m_clustersHold</code> and then output wrapped in a
    * ClusterModel object
    */
   private int m_count = -1;

   /**
    * number of objects in <code>m_clusters</code> at end of last execution of
    * doingit used to check if current m_clusters is large enough to be output.
    */
   private int m_lastSize = -1;

   /** Holds the data to be clustered. */
   protected Table _itable = null;

   /**
    * Check missing values flag. If set to true, this module verifies prior to
    * computation, that there are no missing values in the input table. (In the
    * presence of missing values the module throws an Exception.)
    */
   protected boolean _mvCheck = true;

   /** Holds ArrayList objects of TableCluster objects, a queue. */
   protected ArrayList _pushing = null;

   /** The size of partitions to use in the sampling process. */
   protected int m_maxPartitionSize = 200;

   /**
    * The index of for the column denoting the attribute to be used to sort on
    * prior to partitioning.
    */
   protected int m_nthSortTerm = 0;

   /** The number of clusters to be formed. */
   protected int m_numberOfClusters = 5;


   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   protected boolean m_verbose = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new FractionationSamplerOPT object.
    */
   public FractionationSamplerOPT() { }

   //~ Methods *****************************************************************

   /**
    * Sorts the items in <codE>clusters</code>.
    *
    * @param  clusters ArrayList with TableCluster objects
    *
    * @return ArrayList <code>clusters</code> with its items sorted according to
    *         the natural order imposed by <code>Terms_Comparator</code>.
    *
    * @see    <codE>Terms_Comparator</code> for detail regarding the natural
    *         order of TableCluster objects
    */
   private ArrayList clusterSort(ArrayList clusters) {
      int size = clusters.size();
      TreeSet map = new TreeSet(new Terms_Comparator());
      map.addAll(clusters);
      clusters = new ArrayList();

      for (Iterator it = map.iterator(); it.hasNext();) {
         clusters.add(it.next());
      }

      if (getVerbose()) {
         System.out.println("FractionationSamplerOPT.clusterSort(...) -- " +
                            "number of clusters to sort: " +
                            size);
         System.out.println("FractionationSamplerOPT.clusterSort(...) -- " +
                            "number of clusters sorted: " +
                            clusters.size());
      }

      return clusters;
   }


   /**
    * Splits <code>clusters</code> into ArrayLists of max size <code>
    * m_maxPartitionSize</code>. Adds these Arraylists into one ArrayList which
    * is the returned value of this method. Spliting is performed sequentially
    * rather than randomly.
    *
    * @param  clusters ArrayList holds TableCluster objects
    *
    * @return ArrayList a list of ArrayLists of TableCluster objects. The
    *         ArrayLists in this list can hold up to <codE>
    *         m_maxPartitionSize</code> items.
    */
   private ArrayList partitionClusters(ArrayList clusters) {

      int size = clusters.size();
      ArrayList retClusters = new ArrayList(); // the returned value

// if the size of clusters is smaller or equal to the size of each partition
// just add clusters to retValusters and return it.
      if (size <= this.m_maxPartitionSize) {
         retClusters.add(clusters);

         if (getVerbose()) {
            System.out.println("FractionationSamplerOPT.partitionClusters(...) " +
                               "-- number of clusters to partition: " +
                               size);
            System.out.println("FractionationSamplerOPT.partitionClusters(...) " +
                               "-- number of partitions made: " +
                               retClusters.size());
         }

         return retClusters;
      }

      int ind = 0; // index into the first item in the current partitioning
      ArrayList subl = null; // this will hold the current partitioning

      while (true) {

         // if there are more than one partitioning left in clusters...
         if ((size - ind) >= this.m_maxPartitionSize) {

            // get the objects from ind till ind+maxPartitionSize
            subl =
               new ArrayList(clusters.subList(ind, ind + m_maxPartitionSize));

            // add the current partitioning to the returned value
            retClusters.add(subl);

            // update the index
            ind += m_maxPartitionSize;
         } else {

            // just add what's left in clusters
            if ((size - ind) == 0) {
               break;
            } else if ((size - ind) <= 20) {
               subl = (ArrayList) retClusters.get(retClusters.size() - 1);
               subl.addAll(clusters.subList(ind, size));

               break;
            } else {
               subl = new ArrayList(clusters.subList(ind, size));
               retClusters.add(subl);

               break;
            }
         }
      } // end while

      if (getVerbose()) {
         System.out.println("FractionationSamplerOPT.partitionClusters(...) -- " +
                            "number of clusters to partition: " +
                            size);
         System.out.println("FractionationSamplerOPT.partitionClusters(...) -- " +
                            "number of partitions made: " +
                            retClusters.size());
      }

      return retClusters;
   } // end method partitionClusters


   /**
    * Accumulates <codE>clusters</code> into a temp collection. when
    * accumulation is doen either outputs this temp collection wrapped in a
    * cluster model or performs partitioning and addsi t to the wueue. When
    * certain conditions are met push a cluster model with clusters from the
    * queue.
    *
    * @param clusters ArrayList A list of TableCluster objects
    */
   protected void doingit(ArrayList clusters) {

      if (clusters != null) {
         m_clustersHold.addAll(clusters);

         // if we have not gotten back all of our clusters yet then quit
         if (m_count > 1) {
            m_count--;

            return;
         } else {
            m_clusters = m_clustersHold;
         }
      }

      // check to see if we should stop and send output to the refinery
      if (m_clusters != null) {
         System.out.println("Size:       " + m_clusters.size() + " " +
                            m_lastSize);

         if (
             (m_clusters.size() <= m_numberOfClusters) ||
                (m_clusters.size() >= m_lastSize)) {
            ClusterModel model = new ClusterModel(_itable, m_clusters, null);
            this.pushOutput(model, 1);

            return;
         }
      }

      // else record the size, partition, send them off
      if (m_clusters == null) {

         // first time through
         m_clusters = new ArrayList();

         for (int i = 0, n = _itable.getNumRows(); i < n; i++) {
            TableCluster tc = new TableCluster(_itable, i);
            m_clusters.add(tc);
         }
      }

      m_lastSize = m_clusters.size();

      // sort on nth largest term
      m_clusters = clusterSort(m_clusters);

      // partition to an array of arrays (partitions)
      m_clusters = partitionClusters(m_clusters);

      m_count = m_clusters.size();
      m_clustersHold = new ArrayList();

      _pushing = new ArrayList(m_clusters);

      if (_pushing.size() > 0) {
         ArrayList arrlist = new ArrayList((ArrayList) _pushing.remove(0));
         ClusterModel mod = new ClusterModel(_itable, arrlist, null);
         this.pushOutput(mod, 0);
      }
   } // end method doingit

   /**
    * Performs 1 or mor of the following (depending on the available inputs) If
    * the queue (<code>_pushing</code>) is not empty: Removes a collection of
    * TableCluster objects from the queue, wraps it in a ClusterModel object and
    * outputs it. If input pipes indexed 1 and 0 have objects in it: pulls in
    * the Table and the parameter point object and updates the properties
    * according to the parameter point. If input pipe indexed 2 has the
    * ClusterModel object available - pull it in and retrieve its clusters.
    * Performs clustering action on the Table's data or the clusters of the
    * ClusterModel.
    *
    * @throws Exception           If the input table contains missing values or
    *                             if the index for the sorting attribute is too
    *                             large for the input table.
    * @throws java.lang.Exception Description of exception java.lang.Exception.
    *
    * @see    doingit method for the actions that are performed.
    */
   protected void doit() throws java.lang.Exception {

      ArrayList clusters = null;
      ClusterModel cm = null;

      try {

         if (_pushing.size() > 0) {
            ArrayList arrlist = new ArrayList((ArrayList) _pushing.remove(0));
            ClusterModel mod = new ClusterModel(_itable, arrlist, null);
            this.pushOutput(mod, 0);

            if (this.getFlags()[2] == 0) {
               return;
            }

         }

         if (this.getFlags()[1] > 0) {
            _itable = (Table) this.pullInput(1);

            if (this.getCheckMissingValues()) {

               if (_itable.hasMissingValues()) {
                  throw new TableMissingValuesException("FractionationSamplerOPT: Please replace or filter out " +
                                                        "missing values in your data.");
               }
            }

            ParameterPoint pp = (ParameterPoint) this.pullInput(0);
            this.m_numberOfClusters = (int) pp.getValue(0);
            this.m_nthSortTerm = (int) pp.getValue(1);
            this.m_maxPartitionSize = (int) pp.getValue(2);

            if (this.m_nthSortTerm > (_itable.getNumColumns() - 1)) {
               throw new Exception("FractionationSamplerOPT: The sort " +
                                   "attribute index is too large for " +
                                   "this table: " +
                                   m_nthSortTerm);
            }
         }

         if (this.getFlags()[2] > 0) {
            cm = (ClusterModel) this.pullInput(2);
            clusters = cm.getClusters();
         }

         doingit(clusters);

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
         System.out.println("ERROR: FractionationSamplerOPT.doit()");

         // MRC added 6/24/04: the message given here is inadequate; need to add
         // more information for the user.
         throw new Exception("Error in Fractionation Sampler: " +
                             ex.toString());
      }
   } // end method doit


   /**
    * Called by the D2K Infrastructure before the itinerary begins to execute.
    * Sets all objects to either null or empty collections. Sets counters to -1.
    */

   public void beginExecution() {
      m_clustersHold = null;
      m_clusters = null;
      _pushing = new ArrayList();
      m_lastSize = -1;
      m_count = -1;
   }

   /**
    * Called by the D2K Infrastructure after the itinerary completes execution.
    * Nulls the collections and sets the counters to -1.
    */
   public void endExecution() {
      super.endExecution();
      m_clustersHold = null;
      m_clusters = null;
      m_lastSize = -1;
      m_count = -1;
   }


   /**
    * Return the value if the check missing values flag.
    *
    * @return The value of the check missing values flag.
    */
   public boolean getCheckMissingValues() { return _mvCheck; }

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
         return "Control Parameters";
      } else if (inputIndex == 1) {
         return "Table of examples to cluster";
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
         return "Parameter Point";
      } else if (inputIndex == 1) {
         return "Table";
      } else if (inputIndex == 2) {
         return "Cluster Model";
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
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "Chooses a sample set of rows through a process of repeated partitioning and clustering.  The ";
      s += "table rows are treated as vectors of a vector space.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "This module sorts the initial examples (converted to clusters) by a key attribute denoted by <i>Sort Attribute</i>. ";
      s +=
         "The set of sorted clusters is then segmented into equal partitions of size <i>Max Partition Size</i>. ";
      s +=
         "Each of these partitions is then passed through the agglomerative clusterer to produce ";
      s +=
         "<i>" + NUM_CLUSTERS +
         "</i> clusters.  All the clusters are gathered together for all partitions and the ";
      s +=
         "entire process is repeated until only <i>" + NUM_CLUSTERS +
         "</i> clusters remain. ";
      s +=
         "The sorting step is to encourage like clusters into same partitions. ";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The original table input is not modified but it is included in the final <i>Cluster Model</i>.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This module time complexity of O(<i>Number of Examples</i> * <i>Partition Size</i>). ";
      s +=
         "Each iteration creates <i>Number of Examples</i> <i>Table Cluster</i> objects.";
      s += "</p>";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Fractionation Sampler"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      if (outputIndex == 0) {
         return "Cluster Model for Hier. Agglom. Clusterer";
      } else if (outputIndex == 1) {
         return "Final Cluster Model for refinement";
      } else {
         return "";
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

      if (outputIndex == 0) {
         return "Cluster Model";
      } else if (outputIndex == 1) {
         return "Cluster Model for Refinement";
      } else {
         return "";
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
      String[] out =
      {
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel",
         "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"
      };

      return out;
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
         new PropertyDescription("checkMissingValues",
                                 "Check Missing Values",
                                 "Perform a check for missing values on the table inputs (or not).");

      descriptions[1] =
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
   public boolean getVerbose() { return m_verbose; }

   /**
    * Conditions for module firing.
    *
    * @return boolean
    */
   public boolean isReady() {

      if (
          ((this.getFlags()[0] > 0) && (this.getFlags()[1] > 0)) ||
             (this.getFlags()[2] > 0) ||
             (_pushing.size() > 0)) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Sets the check missing values flag.
    *
    * @param b If true this module verifies before start of computation, that
    *          there are no missing values in the input table. (In the presence
    *          of missing values the module throws an Exception.)
    */
   public void setCheckMissingValues(boolean b) { _mvCheck = b; }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { m_verbose = b; }

   //~ Inner Classes ***********************************************************

   /**
    * <p>Title: Terms_Comparator</p>
    *
    * <p>Description: Compares 2 TableClusters objects. According to this
    * comparator - the natural order of TableClusters is derived from the
    * natural order of the <code>m_nthSortTerm</code> item in the TableCluster's
    * cetroids</p>
    *
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company:</p>
    *
    * @author  D. Searsmith
    * @version 1.0
    */
   private class Terms_Comparator implements java.util.Comparator {

      public Terms_Comparator() { }

      /**
       * Returns the results of comparing the value of <code>
       * m_nthSortTerm</code> item in o1's centroid to the value of <code>
       * m_nthSortTerm</code> item in o2's centroid.
       *
       * @param  o1 Object TableCluster
       * @param  o2 Object TableCluster
       *
       * @return int returns the result of subtracting the value of the <code>
       *         m_nthSortTerm</code> item in o2's centroid from the value of
       *         the <code>m_nthSortTerm</code> item in o2's centroid.
       */
      public int compare(Object o1, Object o2) {
         TableCluster obj1 = (TableCluster) o1;
         TableCluster obj2 = (TableCluster) o2;
         double term1 = obj1.getNthCentroidValue(m_nthSortTerm);
         double term2 = obj2.getNthCentroidValue(m_nthSortTerm);

         if (term1 < term2) {
            return 1;
         } else if (term1 > term2) {
            return -1;
         } else {

            if (obj1 == obj2) {
               return 0;
            } else {
               return 1;
            }
         }
      }

      public boolean equals(Object o) { return this.equals(o); }
   } // end class Terms_Comparator

} // end class FractionationSamplerOPT
