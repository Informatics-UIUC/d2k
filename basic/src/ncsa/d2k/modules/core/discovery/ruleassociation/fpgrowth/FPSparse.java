package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;


import java.util.*;
import gnu.trove.*;


public class FPSparse implements java.io.Serializable{


  private int[] _columns = null;
  private int[] _labels = null;
  private TIntObjectHashMap _rows = new TIntObjectHashMap();
  private int _numcols = -1;
  private int _colcnt = 0;


  public FPSparse(int numcols) {
    _columns = new int[numcols];
    _labels = new int[numcols];
    _numcols = numcols;
  }


  public int getLabel(int col){
 //   if ((col >= 0) && (col < _columns.size())){
      return _labels[col];
//      return (String)((Object[])_columns.get(col))[0];
//    }else {
//      return null;
//    }
  }

  public int getNumColumns(){
    return _colcnt;
  }

  public int getNumRows(){
    return _rows.size();
  }

  public void addColumn(int lbl){
    _labels[_colcnt++] = lbl;
    //Object[] obarr = new Object[2];
    //obarr[0] = lbl;
    //obarr[1] = new Integer(0);
    //_columns.add(obarr);
  }

  public int getInt(int row, int col) {
//    if (_rows.containsKey(row)){
      return ((TIntIntHashMap)_rows.get(row)).get(col);
//    } else {
//      return 0;
//    }
  }

  public void setInt(int data, int row, int col){
    //add row to column set
    //((TIntHashSet)((Object[])_columns.get(col))[1]).add(row);
    _columns[col] = _columns[col] + data;
    //Integer iob = (Integer)((Object[])_columns.get(col))[1];
    //((Object[])_columns.get(col))[1] = new Integer(iob.intValue() + data);

    //check for row
    if (_rows.containsKey(row)){
      ((TIntIntHashMap)_rows.get(row)).put(col, data);
    } else {
      TIntIntHashMap iihm = new TIntIntHashMap();
      _rows.put(row, iihm);
      iihm.put(col, data);
    }
  }

  public int getColumnTots(int col){
//    if ((col >= 0) && (col < _columns.size())){
      //return ((Integer)((Object[])_columns.get(col))[1]).intValue();
      return _columns[col];
//    } else {
//      return new int[0];
//    }
  }

  public int[] getRowIndices(int row){
//    if (_rows.containsKey(row)){
      return ((TIntIntHashMap)_rows.get(row)).keys();
//    } else {
//      return new int[0];
//    }
  }
}

//public class FPSparse {
//
//
//  private HashMap _columns = new HashMap();
//  private int _colcnt = 0;
//  private HashMap _rows = new HashMap();
//
//
//
//
//
//  public FPSparse() {
//  }
//
//
//  public String getLabel(int col){
//    Integer colval = new Integer(col);
//    if (_columns.containsKey(colval)){
//      return (String)((Object[])_columns.get(colval))[0];
//    }else {
//      return null;
//    }
//  }
//
//  public int getNumColumns(){
//    return _columns.size();
//  }
//
//  public int getNumRows(){
//    return _rows.size();
//  }
//
//  public void addColumn(String lbl){
//    Object[] obarr = new Object[2];
//    obarr[0] = lbl;
//    obarr[1] = new HashSet();
//    _columns.put(new Integer(_colcnt), obarr);
//    _colcnt++;
//  }
//
//  public int getInt(int row, int col) {
//    Integer rowval = new Integer(row);
//    Integer colval = new Integer(col);
//    if (_rows.containsKey(rowval)){
//      HashMap rowmap = (HashMap)_rows.get(rowval);
//      Integer val = (Integer)rowmap.get(colval);
//      if (val != null){
//        return val.intValue();
//      } else{
//          return 0;
//      }
//    } else {
//      return 0;
//    }
//  }
//
//  public void setInt(int data, int row, int col){
//    Integer colval = new Integer(col);
//    Integer rowval = new Integer(row);
//    Integer dataval = new Integer(data);
//
//    //add row to column set
//    HashSet colmap = (HashSet)((Object[])_columns.get(colval))[1];
//    if (!colmap.contains(rowval)){
//      colmap.add(rowval);
//    }
//
//    //check for row
//    if (_rows.containsKey(rowval)){
//      ((HashMap)_rows.get(rowval)).put(colval, dataval);
//    } else {
//      HashMap iihm = new HashMap();
//      _rows.put(rowval, iihm);
//      iihm.put(colval, dataval);
//    }
//  }
//
//  public int[] getColumnIndices(int col){
//    Integer colval = new Integer(col);
//    if (_columns.containsKey(colval)){
//      Object[] ints = ((HashSet)((Object[])_columns.get(colval))[1]).toArray();
//      int[] narr = new int[ints.length];
//      for (int i = 0, n = ints.length; i < n; i++){
//        narr[i] = ((Integer)ints[i]).intValue();
//      }
//      return narr;
//    } else {
//      return new int[0];
//    }
//  }
//
//  public int[] getRowIndices(int row){
//    Integer rowval = new Integer(row);
//    if (_rows.containsKey(rowval)){
//      Object[] ints = ((HashMap)_rows.get(rowval)).keySet().toArray();
//      int[] narr = new int[ints.length];
//      for (int i = 0, n = ints.length; i < n; i++){
//        narr[i] = ((Integer)ints[i]).intValue();
//      }
//      return narr;
//    } else {
//      return new int[0];
//    }
//  }
//
//
//
//}

