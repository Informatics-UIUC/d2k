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

  //transient private DSTPSettings _settings = null;
  transient private DSTPConnection _conn = null;
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
    _conn.getServerDataVector("QUIT", 230);
    _conn = null;
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
      _conn = new DSTPConnection(_meta.getServerName());

      //  Set the Category
      String sCmd = "set category " + _meta.getCategory();
      System.out.println(sCmd);
      Vector asServerResponse = _conn.getServerDataVector(sCmd, 220);
      System.out.println(asServerResponse.get(0));

      //set ucks
      Iterator ucks = _meta.getUCK();
      while(ucks.hasNext()){
        String uckname = ((DSTPView.uck)ucks.next()).getUCKName();
        // the 'set uck' command for all selected ucks
        String sCommandString = "set uck " + uckname;
        System.out.println(sCommandString);
        asServerResponse = _conn.getServerDataVector(sCommandString, 230);
        System.out.println(asServerResponse.get(0));
        //only need one
        break;
      }

      String sCommandString = "set datafile " + _meta.getDatafileName();
      System.out.println(sCommandString);
      _conn.getServerDataVector(sCommandString, 240);

      // data command - first the attributes
      sCommandString = "data ";
      //set attributes
      Iterator atts = _meta.getSelectedAttributes();
      while(atts.hasNext()){
        DSTPView.attribute att = (DSTPView.attribute)atts.next();
        System.out.println(att.getAttName());
        sCommandString += " " + att.getAttNumber();
      }
      System.out.println("Starting read ...");
      _data = new Vector();
      int cnt = 0;
      try {
        cnt = Integer.parseInt(_meta.getDatafileNumRecords());
      }catch(Exception e){}
      _pv = new ProgressViewer("Reading ...", "Reading: " + _meta.getDatafileName(), 0, cnt, this);
      _conn.getServerDataVector(sCommandString, 230, _data);
      //_data.remove(0);
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

/*
	public void getServerData(Vector asData)
	{

//		Vector asData = new Vector();
		Vector asPosition = new Vector();
		String sPosition = null;

		myClient = new DSTPClient();
		// establish a socket connection to the selected server and set the class member
		try
		{
			if(m_iThisServerConnection==null)
				m_iThisServerConnection = new DSTPConnection(m_sServerName);

			DSTPConnection iServerConnection = getThisServerConnection();

			// construct and issue the server commands here

			//  Set the Category
			String sCmd = "set category " + m_sCategoryName;
			Vector asServerResponse = iServerConnection.getServerDataVector(sCmd, 220);

			// set the ucks selected for the ConnectionSpecs object
			for(int i=0; i<m_asUckNames.size(); i++)
			{
				// the 'set uck' command for all selected ucks
				String sCommandString = "set uck " + (String)m_asUckNames.elementAt(i);
				asServerResponse = iServerConnection.getServerDataVector(sCommandString, 230);
			}

			// set the datafile
			String sCommandString = "set datafile " + m_sDFName;
			iServerConnection.getServerDataVector(sCommandString, 240);

			// set line
			if( (!(m_sStartLine.equals(""))) && (!(m_sEndLine.equals(""))))
			{
				sCommandString = "set line " + m_sStartLine + " " + m_sEndLine;
				asServerResponse = iServerConnection.getServerDataVector(sCommandString, 250);
			}

			// set Sample Params
			if( (m_iSampleType!=-1) && (m_iSampleUnit!=-1) && (!(m_sSampleValue.equals(""))))
			{
				String sType=null;
				String sUnit=null;
				int sRetCode=0;

				switch(m_iSampleType)
				{
					case 1:
						sType="decimate";
						if(m_iSampleUnit==1)
							sUnit="percentage";
						if(m_iSampleUnit==0)
							sUnit="line";

						sRetCode=262;
						break;
					case 0:
						if(m_iSampleUnit==1)
						{
							sUnit="percentage";
							sRetCode=261;
						}
						if(m_iSampleUnit==0)
						{
							sUnit="line";
							sRetCode=260;
						}
						break;
					default:
						System.out.println("Invalid parameters");
						break;
				}

				if(m_iSampleType==1)
					sType="decimate";
				if(m_iSampleType==0)
					sType="random";

				if(m_iSampleUnit==1)
					sUnit="percentage";
				if(m_iSampleUnit==0)
					sUnit="line";

				sCommandString = "set sample "+sType +" "+sUnit +" "+ m_sSampleValue;
				asServerResponse = iServerConnection.getServerDataVector(sCommandString, sRetCode);
			}

			// data command - first the attributes
			sCommandString = "data ";

			// set the Common Ucks as part of the data command as attributes . This will be valid in case of the getServerData() method being called
				// from with DSTPClient::getJoinData()
			for(int i=0; i<m_asJoinUckNames.size();i++)
			{
				Vector aaAttrStruct = new Vector();
				Vector asDFStruct = new Vector();
				Vector asAttrStruct = new Vector();

				aaAttrStruct.addElement(m_asJoinUckNames.elementAt(i));
				asDFStruct.addElement(m_sDFName);
				asDFStruct.addElement(m_sServerName);
				aaAttrStruct.addElement(asDFStruct);
				asAttrStruct.addElement(aaAttrStruct);


				sPosition = myClient.getAttributeDescriptionNumberWithCategory(asAttrStruct,m_sCategoryName, m_iMetaServerConnection);
				asPosition.addElement(sPosition);

				sCommandString = sCommandString+ " "+ sPosition;
			}

			for (int j=0;j<m_asAttributeNames.size();j++)
			{
				Vector aaAttrStruct = new Vector();
				Vector asDFStruct = new Vector();
				Vector asAttrStruct = new Vector();

				aaAttrStruct.addElement(m_asAttributeNames.elementAt(j));
				asDFStruct.addElement(m_sDFName);
				asDFStruct.addElement(m_sServerName);
				aaAttrStruct.addElement(asDFStruct);
				asAttrStruct.addElement(aaAttrStruct);

				sPosition = myClient.getAttributeDescriptionNumberWithCategory(asAttrStruct, m_sCategoryName, m_iMetaServerConnection);

				if(asPosition.size()==0)
					sCommandString = sCommandString + " " + sPosition;
				else
				{
					for(int i=0;i<asPosition.size();i++)
					{
						if(sPosition.equals((String)asPosition.elementAt(i)))
							break;
						else
							sCommandString = sCommandString + " " + sPosition;
					}
				}
			}
			System.out.println("comm-:"+sCommandString);

			// now the conditions
			if(m_aaConditions.size()>0)
			{
				sCommandString = sCommandString + " where ";
				for(int k=0;k<m_aaConditions.size();k++)
				{
					// get the description number for each attribute in the condition
					Vector aaAttrStruct = new Vector();
					Vector asDFStruct = new Vector();

					aaAttrStruct.addElement(((Vector)m_aaConditions.elementAt(k)).elementAt(0));
					asDFStruct.addElement(m_sDFName);
					asDFStruct.addElement(m_sServerName);
					aaAttrStruct.addElement(asDFStruct);
					String sAttrID = myClient.getAttributeDescriptionNumberWithCategory(aaAttrStruct,m_sCategoryName,m_iMetaServerConnection);
					sCommandString = sCommandString + " " + sAttrID;
					sCommandString = sCommandString + " " + ((Vector)(m_aaConditions.elementAt(k))).elementAt(1);
					sCommandString = sCommandString + " " + ((Vector)(m_aaConditions.elementAt(k))).elementAt(2);
					sCommandString = sCommandString + " " + ((Vector)(m_aaConditions.elementAt(k))).elementAt(3);
				}
			}

//			asData = new Vector();
			iServerConnection.getServerDataVector(sCommandString, 230, asData);

		}
		catch (Exception e)
		{
			System.out.println("Exception "+e);
		}

		return;
	}

*/
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