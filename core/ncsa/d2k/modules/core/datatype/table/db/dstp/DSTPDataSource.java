package ncsa.d2k.modules.core.datatype.table.db.dstp;

//==============
// Java Imports
//==============

import java.util.*;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.db.DBDataSource;
import backend.*;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.io.dstp.*;

public class DSTPDataSource extends Thread implements DBDataSource, ProgressQueryable {

  //==============
  // Data Members
  //==============

  transient private DSTPSettings _settings = null;
  private DSTPView.MetaNode _meta = null;
  private boolean _debug = false;

  private Vector _data = null;

  transient private ProgressViewer _pv = null;

  transient private DSTPView _view = null;

  //============
  // Properties
  //============


  //===============
  // Constructor(s)
  //===============

  public DSTPDataSource(DSTPView view, DSTPView.MetaNode meta){
    _meta = meta;
    _view = view;
    start();
  }

  public DSTPDataSource(DSTPView.MetaNode meta, Vector data){
    _meta = meta;
    _data = data;
  }


  //================
  // Static Methods
  //================


  //================
  // Public Methods
  //================

  public void run(){
    if (buildSettings()){
      _view.pushOut(this);
    } else {
      _view.enableAll();
    }
  }

  //=================
  // Private Methods
  //=================

  private void quitConnection(){
    _settings.getThisServerConnection().getServerDataVector("QUIT", 230);
    _settings = null;
  }

  private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
    stream.defaultReadObject();
    if (_data == null){
      buildSettings();
    }
  }

  private boolean buildSettings(){
    try {
      System.out.println("Building settings ...");
      DSTPConnection conn = new DSTPConnection(_meta.getServerName());
      _settings = new DSTPSettings(conn);
      //set category
      _settings.setCategory(_meta.getCategory());
      //set ucks
      Iterator ucks = _meta.getUCK();
      while(ucks.hasNext()){
        _settings.setUck(((DSTPView.uck)ucks.next()).getUCKName());
        //only need one
        break;
      }
      //set the datafile struct
      Vector asDFStruct = new Vector();
      asDFStruct.addElement(_meta.getDatafileName());
      asDFStruct.addElement(_meta.getServerName());
      _settings.setDataFileStruct(asDFStruct);
      //set attributes
      Iterator atts = _meta.getSelectedAttributes();
      while(atts.hasNext()){
        DSTPView.attribute att = (DSTPView.attribute)atts.next();
        Vector attStruct = new Vector();
        attStruct.add(att.getAttName());
        System.out.println(att.getAttName());
        attStruct.add(asDFStruct);
        _settings.setAttributeStruct(attStruct);
      }
      System.out.println("Starting read ...");
      _data = new Vector();
      int cnt = 0;
      try {
        cnt = Integer.parseInt(_meta.getDatafileNumRecords());
      }catch(Exception e){}
      _pv = new ProgressViewer("Reading ...", "Reading: " + _meta.getDatafileName(), 0, cnt, this);
      _settings.getServerData(_data);
      _data.remove(0);
      if (_pv != null){
        _pv.quit();
      }
      System.out.println("End read ...");
      //quitConnection();
    } catch (Exception e){
      System.out.println("EXCEPTION in building datasource: " + e);
      javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
      return false;
      //e.printStackTrace();
    }
    return true;
  }



  //========================================
  // Interface Implementation: DBDataSource
  //========================================
    public String[]   getUserSelectedTables(){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getUserSelectedTables()");
      }
      String[] dsname = new String[1];
      dsname[0] = _meta.getDatafileName();
      return dsname;
    }

    public String[][] getUserSelectedCols (){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getUserSelectedCols()");
      }
      String[][] cols = new String[1][this.getNumDistinctColumns()];
      Iterator atts = _meta.getSelectedAttributes();
      int j = 0;
      while(atts.hasNext()){
        DSTPView.attribute att = (DSTPView.attribute)atts.next();
        cols[0][j++] = att.getAttName();
      }
      return cols;
    }

    public String getUserSelectedWhere(){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getUserSelectedWhere()");
      }
      return "";
    }

    public DBDataSource copy(){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.copy()");
      }
      return new DSTPDataSource(_meta, _data);
    }

    /**
     * Set the tables, columns, and where clause that make up this DBTable.
     */
    //public void setDBInstance(String[] tables, String[][] columns,
    //                          String whereClause, int cacheType);

    /**
     * Get the number of distinct columns.
     * @return
     */
    public int getNumDistinctColumns(){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getNumDistinctColumns()");
      }
      Iterator atts = _meta.getSelectedAttributes();
      int j = 0;
      while(atts.hasNext()){
        atts.next();
        j++;
      }
      return j;
    }

    /**
     * Get the number of rows.
     * @return
     */
    public int getNumRows(){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getNumRows()");
      }
      /*
      int retval = 0;
      try {
        retval = Integer.parseInt(_meta.getDatafileNumRecords());
      } catch (Exception e){
        retval = 0;
      }
      return retval;
      */
      return _data.size();
    }

    /**
     * Get textual data from (row, col)
     * @param row
     * @param col
     * @return
     */
    public String getTextData (int row, int col){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getTextData()");
      }
      StringTokenizer toker = new StringTokenizer((String)_data.get(row));
      int i = 0;
      String retval = null;
      while(toker.hasMoreTokens()){
        String s = toker.nextToken();
        if (col == i++){
          retval = s;
          break;
        }
      }
      return retval;
    }

    /**
     * Get numeric data from (row, col)
     * @param row
     * @param col
     * @return
     */
    public double getNumericData (int row, int col){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getNumericData()");
      }
      String retval = getTextData(row, col);
      double dval = 0;
      try{
        dval = Double.valueOf(retval).doubleValue();
      } catch(Exception e){
        dval = 0;
      }
      return dval;
    }

    public boolean  getBooleanData (int row, int col){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getNumericData()");
      }
      String retval = getTextData(row, col);
      boolean dval = false;
      try{
        dval = Boolean.getBoolean(retval);
      } catch(Exception e){
        dval = false;
      }
      return dval;
    }

    /**
     * Get an Object from (row, col);
     * @param row
     * @param col
     * @return
     */
    public Object getObjectData(int row, int col) {
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getObjectData()");
      }
      int type = getColumnType(col);
      if (type == ColumnTypes.BOOLEAN){
        return new Boolean(getTextData(row, col));
      } else if (type == ColumnTypes.INTEGER){
        return new Integer((int)getNumericData(row, col));
      } else if (type == ColumnTypes.FLOAT){
        return new Float(getNumericData(row, col));
      } else {
        return getTextData(row, col);
      }
    }




    //public boolean getBooleanData(int row, int col);

    /**
     * Get the label of the ith column.
     * @param i
     * @return
     */
    public String getColumnLabel(int i){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getColumnLabel()");
      }
      Iterator atts = _meta.getSelectedAttributes();
      int j = 0;
      String retval = null;
      while(atts.hasNext()){
        DSTPView.attribute att = (DSTPView.attribute)atts.next();
        if (i == j++){
          retval = att.getAttName();
          break;
        }
      }
      return retval;
    }

    /**
     * Get the comment for the ith column.
     * @param i
     * @return
     */
    public String getColumnComment(int i){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getColumnComment()");
      }
      return "";
    }

    /**
     * Get the column type from ColumnTypes
     * @param i
     * @return
     */
    public int getColumnType(int i){
      if (_debug){
        System.out.println("ENTER: DSTPDataSource.getColumnType()");
      }
     Iterator atts = _meta.getSelectedAttributes();
      int j = 0;
      String retval = null;
      while(atts.hasNext()){
        DSTPView.attribute att = (DSTPView.attribute)atts.next();
        if (i == j++){
          retval = att.getAttType();
          break;
        }
      }
      if(retval == null){
        return ColumnTypes.STRING;
      } else if (retval.toLowerCase().trim().equals("real")){
        return ColumnTypes.FLOAT;
      } else if (retval.toLowerCase().trim().equals("integer")){
        return ColumnTypes.INTEGER;
      } else if (retval.toLowerCase().trim().equals("boolean")){
        return ColumnTypes.BOOLEAN;
      } else if (retval.toLowerCase().trim().equals("string")){
        return ColumnTypes.STRING;
      } else {
        return ColumnTypes.STRING;
      }

    }

  //=============================================
  // Interface Implementation: ProgressQueryable
  //=============================================

  public int getProgress(){
    return _data.size();
  }



  //=============
  // Inner Class
  //=============



}