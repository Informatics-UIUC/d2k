package ncsa.d2k.modules.core.discovery.cluster.util;

//==============
// Java Imports
//==============

import java.io.*;

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <p>Title: TableCluster</p>
 * <p>Description: Holds information about a cluster of Table rows.</p>
 * <p>TODO: Make it Sparse Table Friendly <p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA </p>
 * @author D. Searsmith
 * @version 1.0
 */

public class TableCluster implements Serializable {

  //==============
  // Data Members
  //==============

  static private int s_id = 0;

  private int[] _members = null;
  private Table _table = null;
  private double[] _centroid = null;
  private double _centroid_norm = -1;
  private boolean _centroidComputed = false;
  private TableCluster _cluster1 = null;
  private TableCluster _cluster2 = null;

  private int _label = assignID();

  //===========
  //Properties
  //===========

  private String _txtLabel = null;
  public void setTextClusterLabel(String i) {
    _txtLabel = i;
  }
  public String getTextClusterLabel() {
    return _txtLabel;
  }

  private double _childDistance = -1;
  public void setChildDistance(double i) {
    _childDistance = i;
  }
  public double getChildDistance() {
    return _childDistance;
  }

  //================
  // Constructor(s)
  //================

  public TableCluster() {}

  public TableCluster(Table table, int row) {
    _table = table;
    _members = new int[1];
    _members[0] = row;
    if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().
          length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  public TableCluster(Table table, int[] rows) {
    _table = table;
    _members = rows;
    if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().
          length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  public TableCluster(Table table, TableCluster c1, TableCluster c2) {
    _table = table;
    _cluster1 = c1;
    _cluster2 = c2;
    if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().
          length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  //===============
  // Static Method
  //===============

  static public synchronized int assignID(){
    return s_id++;
  }

  static public TableCluster merge(TableCluster tc1, TableCluster tc2) {
    if (tc1 == null) {
      System.out.println("TableCluster.merge -- input param tc1 is null");
      return null;
    }
    if (tc2 == null) {
      System.out.println("TableCluster.merge -- input param tc2 is null");
      return null;
    }
    if (tc1.getTable() != tc2.getTable()) {
      System.out.println("TableCluster.merge -- tc1 and tc2 reference different tables and cannot be merged.");
      return null;
    }
    TableCluster newtc = new TableCluster(tc1.getTable(), tc1, tc2);
    return newtc;
  }

  //================
  // Public Methods
  //================

  public void cut(){
    computeCentroid();
    _cluster1 = null;
    _cluster2 = null;
  }

  public int getClusterLabel() {
    return _label;
  }

  public Object[] getSubClusters() {
    Object[] ret = new Object[2];
    ret[0] = _cluster1;
    ret[1] = _cluster2;
    return ret;
  }

  /**
   * Return the left child of this node or null if it doesn't exist.
   * @return left child as TableCluster
   */
  public TableCluster getLC(){
    return _cluster1;
  }

  /**
   * Return the left right of this node or null if it doesn't exist.
   * @return right child as TableCluster
   */
  public TableCluster getRC(){
    return _cluster2;
  }

  public boolean isLeaf() {
    return (_cluster1 == null);
  }

  /**
   * TODO: Fix for sparse tables.
   */
  public void computeCentroid() {
    if (_centroidComputed) {
      return;
    }
    int[] members = this.getMemberIndices();

    if (_table instanceof ExampleTable) {
      double sum = 0;
      int[] feats = ( (ExampleTable) _table).getInputFeatures();
      for (int i = 0, n = feats.length; i < n; i++) {
        sum = 0;
        for (int j = 0, m = members.length; j < m; j++) {
          sum += _table.getDouble(members[j], feats[i]);
        }
        _centroid[i] = sum;
      }
//    } else if (_table instanceof SparseTable){
//    }
    } else {
      double sum = 0;
      for (int i = 0, n = _table.getNumColumns(); i < n; i++) {
        sum = 0;
        for (int j = 0, m = members.length; j < m; j++) {
          sum += _table.getDouble(members[j], i);
        }
        _centroid[i] = sum;
      }
    }
    int cnt = members.length;
    for (int i = 0, n = _centroid.length; i < n; i++) {
      _centroid[i] = _centroid[i] / cnt;
    }
    _centroidComputed = true;
  }

  public double getCentroidNorm() {
    computeCentroid();
    if (_centroid_norm < 0) {
      double temp = 0;
      for (int i = 0, n = _centroid.length; i < n; i++) {
        temp += Math.pow(_centroid[i], 2);
      }
      _centroid_norm = Math.sqrt(temp);
    }
    if (_centroid_norm == 0){
      _centroid_norm = .0000001;
    }
    return this._centroid_norm;
  }

  public double[] getCentroid() {
    computeCentroid();
    return this._centroid;
  }

  public int[] getMemberIndices() {
    int[] temparr = null;
    if (_members == null) {
      if ( (_cluster1 == null) || (_cluster2 == null)) {
        System.out.println(
            "ERROR: TableCluster.getMemberIndices() -- no clusters or indices defined.");
        return null;
      } else {
        int[] arr1 = _cluster1.getMemberIndices();
        int[] arr2 = _cluster2.getMemberIndices();
        _members = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, _members, 0, arr1.length);
        System.arraycopy(arr2, 0, _members, arr1.length, arr2.length);
      }
    }
    temparr = new int[_members.length];
    System.arraycopy(_members, 0, temparr, 0, _members.length);
    return temparr;
  }

  public Table getTable() {
    return _table;
  }

  public int getSize() {
    return getMemberIndices().length;
  }

  public String generateTextLabel(){
    String out = null;
//    if (_table = null){
//      out = "" + this.getClusterLabel();
//    } else {
      out = "< ";
      double[] centroid = this.getCentroid();
//      int[] ifeatures = null;
//      if (_table instanceof ExampleTable) {
//        ifeatures = ( (ExampleTable) _table).getInputFeatures();
//      } else {
//        ifeatures = new int[_table.getNumColumns()];
//        for (int i = 0, n = _table.getNumColumns(); i < n; i++) {
//          ifeatures[i] = i;
//        }
//      }
        for (int i = 0, n = centroid.length; i < n; i++) {
//        if (_table.getColumnType(ifeatures[i])== ColumnTypes.BOOLEAN){
//          out += (centroid[i] == 0) ? "false " : "true ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.BYTE){
//          out += (byte)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.DOUBLE){
//          out += centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.FLOAT){
//          out += (float)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.LONG){
//          out += (long)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.INTEGER){
//          out += (int)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.SHORT){
//          out += (short)centroid[i] + " ";
//        }
          out += centroid[i] + " ";
        }
      out += ">";
      _txtLabel = out;
//    }
    return out;
  }

  public String toString(){
    if (_txtLabel == null){
      generateTextLabel();
    }
    return _txtLabel;
  }

  //=================
  // Private Methods
  //=================


}
