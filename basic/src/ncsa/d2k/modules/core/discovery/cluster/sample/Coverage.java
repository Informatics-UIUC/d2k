package ncsa.d2k.modules.core.discovery.cluster.sample;


import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Title: Coverage</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class Coverage {

    /**
     * indices of columns into the columns of the table to be clustered, that are its input features
     */
    int[] _ifeatures = null;

  /**
   * If true, this class outputs verbose info to std out
   */
  private boolean _verbose = true;

  /**
   * Returns the value of the verbosity flag.
   * @return boolean The value of the verbosity flag.
   */
  public boolean getVerbose() {
    return _verbose;
  }


  /**
   * Sets the value of the verbosity flag.
   * @param b boolean The value for the verbosity flag.
   */
  public void setVerbose(boolean b) {
    _verbose = b;
  }


  /**
   * Number of TableCluster objects to form (also the number of rows to sample)
   * when sampling the Table to be clustered
   * must be greater than zero
   */
  private int _maxNumCenters = 500;
  /**
   * Returns the number of rows to smaple from the Table to be clustered.
   * @return int The number of rows to smaple from the Table to be clustered.
   */
  public int getMaxNumCenters() {
    return _maxNumCenters;
  }

  /**
   * Sets the value of number of rows to smaple from the Table to be clustered.
   * @param mt int The value of number of rows to smaple from the Table to be clustered.
   * Should be greater than zero.
   */
  public void setMaxNumCenters(int mt) {
    _maxNumCenters = mt;
  }

  /**
   specifies the percent of the max distance to use
                                 as a cutoff value to forming new samples [1...100]
   */
  private int _cutOff = 10;
  /**
   * Returns the value of the cut off percentage threshold
   * @return int The value of the cut off percentage threshold
   */
  public int getCutOff() {
    return _cutOff;
  }

  /**
   * Sets the value of the cut off percentage threshold
   * @param co int The value of the cut off percentage threshold
   */
  public void setCutOff(int co) {
    _cutOff = co;
  }

  /**
   * The ID of the distance metric to be used. IDs are defined in <codE>
    * ncsa.d2k.modules.core.discovery.cluster.hac.HAC</code>
   */
  private int _distanceMetric = 0;
  /**
   * Returns the value of the Distance Metric property.
   *
   * @return int The value of the Distance Metric property
   */
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  /**
     * Sets the value of the Distance Metric property.
     *
     * @param dm The value for the Distance Metric property
    */
  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }
  /**
   *
    * Check missing values flag. If set to true, this module verifies prior  to computation, that there are no missing values in the input table. (In the presence of missing values the module throws an Exception.)

   */
  protected boolean _mvCheck = true;
  /**
  * Return the value if the check missing values flag.
  *
  * @return The value of the check missing values flag.
  */
  public boolean getCheckMissingValues() {return _mvCheck;}


  /**
      * Sets the check missing values flag.
      *
      * @param b If true this module verifies before start of computation, that there are no missing values in the input table. (In the presence of missing values the module throws an Exception.)
      */
  public void setCheckMissingValues(boolean b) {_mvCheck = b;}

  /**
   * Instantiates a Coverage object with values for all the properties
   * @param maxnum int The value from the maximum number of centers to sample
   * @param cutoff int The value for the Cut Off property (should be in the range [1,100])
   * @param dm int The value for the ID of the distance metric
   * @param ver boolean The value for the verbosity flag
   * @param check boolean The value for the Check Missing Values flag
   */
  public Coverage(int maxnum, int cutoff, int dm, boolean ver, boolean check) {
    this.setMaxNumCenters(maxnum);
    this.setDistanceMetric(dm);
    this.setVerbose(ver);
    this.setCutOff(cutoff);
    this.setCheckMissingValues(check);
  }
  
  private D2KModuleLogger myLogger = 
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());

  /**
   * This module selects a sample set of the input table rows such that
   * the set of samples formed is approximately the minimum number of samples
   * needed such that for every row in the table there is at least one sample in
   * the sample set of distance <= Distance Cutoff.
   *
   * @param inittable Table to sample from.
   * @return Table of sampled rows.
   * @throws Exception If Check Missing Values flag is turned on and <codE>inittable</code> has missing values.
   */
  public Table sample(Table inittable) throws Exception{

    if (this.getCheckMissingValues()){
      if (inittable.hasMissingValues()){
        throw new TableMissingValuesException("CoverageSampler: Please replace or filter out missing values in your data.");
      }
    }

    Table itable = null;
    try {

      int initsz = inittable.getNumRows();


      if (inittable instanceof ClusterModel) {
        itable = (MutableTable) ( (ClusterModel) inittable).getTable();
      } else {
        itable = (MutableTable) inittable;
      }

      if (itable instanceof ExampleTable) {
        _ifeatures = ( (ExampleTable) itable).getInputFeatures();
      } else {
        _ifeatures = new int[itable.getNumColumns()];
        for (int i = 0, n = itable.getNumColumns(); i < n; i++) {
          _ifeatures[i] = i;
        }
      }

      HAC.validateNonTextColumns(itable, _ifeatures);

      double maxdist = HAC.calculateMaxDist(itable, _ifeatures, this.getDistanceMetric(), this.getCutOff());

      itable = sample(itable, maxdist);

      myLogger.debug("Input table contained " + initsz + " rows.");
      myLogger.debug("Sampled table created with " + itable.getNumRows() +
                         " rows.");
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      myLogger.error("ERROR: CoverageSampler.doit()");
      throw ex;
    }
    return itable;
  }

  //=================
  // Private Methods
  //=================

  /**
   * Perform sampling
   * For each row row_i in table <code>orig</code> create a TableCLuster object
   * and add it to <codE>clusters</code>.
   * seeds a collection smaple_list with the first cluster in clusters
   *
   * Then adds clusters 1-n from <code>clusters</code> if the distance of
   * cluster_i from one of the clusters in List is shorter than the max distance.
   *
   * For each cluster_i in <codE>clusters</codE> (i runs from 1 to cluster size):
   *     For each cluster_j in smaple_list
   *         distance <= compute distance between cluster_i and cluster_j
   *         if(distance <= <code>maxDist</code>
   *             add cluster_i to smaple_list
   *             break the j loop
   *     If(sample list size >= <code>maxNumCenters</code>)
   *        break the i loop
   * now sample_list holds table clusters each of them represents a row to be included in the returned table.
   *
   *


   *
   * @param orig Table to sample from.
   * @param maxdist Distance cutoff.
   * @return New table of sampled rows.

   */
  final private Table sample(Table orig, double maxdist)  {

//create a tabl ecluster for eahc row and add to one collection
    ArrayList clusters = new ArrayList();
    for (int i = 0, n = orig.getNumRows(); i < n; i++) {
      TableCluster tc = new TableCluster(orig, i);
      clusters.add(tc);
    }

//this will hold the rows to be sampled
    ArrayList part1 = new ArrayList();
    boolean loop = false;
    //for each row, for each cluster
    for (int i = 0, n = clusters.size(); i < n; i++) {
      TableCluster tc = (TableCluster) clusters.get(i);
      loop = false;
      //for each cluster in part1
      for (int j = 0, m = part1.size(); j < m; j++) {
          //compute distance to tc
        double dist = HAC.distance(tc, (TableCluster) part1.get(j),
                                   this.getDistanceMetric());
//if distance is short enough - break
        if (dist <= maxdist) {
          loop = true;
          break;
        }
      }//for j
      //distance was short enough - add to the sampling list
      if (!loop) {
        part1.add(tc);
        //if we've collected enough centers - break out and return
        if (part1.size() == this.getMaxNumCenters()){
          break;
        }
      }
      if (Math.IEEEremainder(i, 1000) == 0) {
    	  myLogger.debug("CoveragSampler.sample() -- Rows Processed: " +
                  i);
      }
    }//for i

    myLogger.debug("Found " + part1.size() + " samples.");
    //System.out.println("Found " + part1.size() + " samples.");

//keeps will be the subset indices for the returned table.
    int[] keeps = new int[Math.min(part1.size(), this.getMaxNumCenters())];
    //for eahc table cluster in part1, get the index in its members array
    //there is only one because these are a single row clusters
    for (int i = 0, n = Math.min(keeps.length, this.getMaxNumCenters()); i < n; i++){
      TableCluster tcc = (TableCluster) part1.get(i);
      keeps[i] = tcc.getMemberIndices()[0];
    }

//create the subset table.
    Table newTable = orig.getSubset(keeps);

    myLogger.debug("Returning " + newTable.getNumRows() + " samples.");
    return newTable;
  }

}
