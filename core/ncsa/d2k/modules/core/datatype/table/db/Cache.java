package ncsa.d2k.modules.core.datatype.table.db;

import java.sql.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


public abstract class Cache implements java.io.Serializable {

    protected TableImpl table;

    // range of row# from the entire ResultSet, that are stored in the Cache
    protected int minRowNum; //(indicates the beginning of the ResultSet subset cached)
    protected int maxRowNum; //(indicates the end of the ResultSet subset cached)

    protected int numRowsInCache; // current # of rows in the Cache
    protected int maxRowsInCache;  // maximum # of rows that can be in the Cache
    protected int numRows;

    protected int getNumCacheRows() {
        return numRowsInCache;
    }
    protected void setNumCacheRows(int cacheRows) {
        numRowsInCache = cacheRows;
    }
    protected int getMaxCacheRows() {
        return maxRowsInCache;
    }
    protected void setMaxCacheRows(int calculatedMaxNumRows) {
        maxRowsInCache = calculatedMaxNumRows;
    }


    public String getCacheTextData(int row, int col) throws CacheMissException {
        int rowInCache = whichRowInCache(row);
        if (rowInCache != -1)
            return table.getString(rowInCache,col);
        else
            throw new CacheMissException("Cache:getCacheTextData:CacheMissException");
    } //getCacheTextData

    public double getCacheNumericData(int row, int col) throws CacheMissException {
        int rowInCache = whichRowInCache(row);
        if (rowInCache != -1)
            return table.getDouble(rowInCache,col);
        else
            throw new CacheMissException("Cache:GetCacheNumericData:CacheMissException");
    } //getCacheNumericData

    public boolean getCacheBooleanData(int row, int col) throws CacheMissException {
        int rowInCache = whichRowInCache(row);
        if (rowInCache != -1)
            return table.getBoolean(rowInCache,col);
        else
            throw new CacheMissException("Cache:GetCacheBooleanData:CacheMissException");
    } //getCacheBooleanData

    public Object getCacheObjectData(int row, int col) throws CacheMissException {
        int rowInCache = whichRowInCache(row);
        if (rowInCache != -1)
            return table.getObject(rowInCache,col);
        else
            throw new CacheMissException("Cache:getCacheObjectData:CacheMissException");
    } //getCacheObjectData


    // return the physical row#  in the Cache, where the data is to be found.
    // return -1 if the data is not in the Cache
    private int whichRowInCache (int row) {
        try {
            if ((row >= minRowNum) && (row <= maxRowNum)) //found row in Cache
                return (row-minRowNum); // return the row
            else
                return -1; //couldn't find row in Cache
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("0. Error occoured in Cache:WhichRowInCache");
           return -2;
        }
    }// RowInCache

    public void update(int rowInResultSet, DBDataSource dbds) throws SQLException {
        minRowNum = rowInResultSet;

        int r = rowInResultSet;
        while ( r < getMaxCacheRows() && r < dbds.getNumRows()) {
            for (int c = 0; c < dbds.getNumDistinctColumns(); c++) {
                switch (dbds.getColumnType(c)) {
                    case ColumnTypes.INTEGER:
                    case ColumnTypes.DOUBLE:
                    case ColumnTypes.FLOAT:
                        table.setDouble(dbds.getNumericData(r,c), r, c);
                        break;
                    case ColumnTypes.STRING:
                    case ColumnTypes.CHAR:
                    case ColumnTypes.OBJECT:
                        table.setString(dbds.getTextData(r,c), r, c);
                        break;
                    default:
                        table.setString(dbds.getTextData(r,c), r, c);
                } //switch
            }//for loop processes all the columns for a particular row
            r++;
        }//while

        //now update how many rows were written to the cache
        setNumCacheRows(r);
        //now update maxRowNum
        maxRowNum = minRowNum + getNumCacheRows() -1;
    }

/*
    public void update(int rowInResultSet, ResultSet rs) throws SQLException {
            minRowNum = rowInResultSet;

            ResultSetMetaData rsmd = rs.getMetaData();

            // this is the potential time-consuming step, if the # records is very large..
            int count = 0;
            if(rowInResultSet > 0) {
                while (rs.next() ){
                    count++;
                    if (count == rowInResultSet)
                        break;
                }
            }
             // either the result set empties (less likely)
             // or the maximum # of rows that can be written to the Cache -- getNumCacheRows()
             // have been written (a lot more likely)

            // compare #cols // compare column datatypes
            boolean check = (table.getNumColumns() == rsmd.getColumnCount());

            int numRowsAddedToCache = 0;
            if (check) {
                while (  (rs.next()) && (numRowsAddedToCache < getMaxCacheRows()) ) {
                    for (int c = 1; c <= rsmd.getColumnCount(); c++) {
                        if (rsmd.getColumnType(c) == 2) {
                            table.setDouble(rs.getDouble(c), numRowsAddedToCache, c-1);
                        }
                        else { //(rsmd.getColumnType(c) == 12)
                            table.setString(rs.getString(c), numRowsAddedToCache, c-1);
                        }
                    }//for loop processes all the columns for a particular row
                    numRowsAddedToCache++;
                }//while
            }
            else {
                System.out.println("Error: # Rows, # Cols, Column Datatype do not match");
            }

            //now update how many rows were written to the cache
            setNumCacheRows(numRowsAddedToCache);
            //now update maxRowNum
            maxRowNum = minRowNum + getNumCacheRows() -1;
    }
*/


} //Cache