package ncsa.d2k.modules.core.datatype.table.db.sql;

import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.table.db.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * <p>Title: SQLDBConnection</p>
 * Old name : AbstractDBConnect
 * <p>Description: This is a DBConnection that is 'bound' to a particular database.  * Objects of this class are bound to a database, tables within the database,
 * and columns within those tables.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur
 * @version 1.0
 */

public class CachedSQLDataSource implements DBDataSource {

    //protected ConnectionWrapper cw; /* Input holder for ConnectionWrapper */
    protected DBConnection dbconnection;
    protected Cache cache;

    protected String dbInstance;

    protected String[]   userTables;
    protected String[][] userAllColumns;
    protected String     whereClause;

//    protected String     userQuery;

    protected int        numDistinctUserColumns;
    protected int        numUserRows;

    protected ArrayList columnComments;
    protected String[] columnLabels;

    public CachedSQLDataSource(DBConnection connection,
                               String[] tables,
                               String[][]columns,
                               String whereClause,
                               int cacheType) {
        dbconnection = connection;
//        userTables = tables;
//        userAllColumns = columns;
//        whereClause = whereClaus;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClause);

        System.out.println("CSDS: "+tables+" "+columns+" "+whereClause);

        //..............//
//        this.userQuery = this.dbconnection.getTableQuery(tables, columns, whereClause);
        //..............//


        initialize(cacheType);
    }

    public DBDataSource copy() {
        DBDataSource cpy = new CachedSQLDataSource(dbconnection,
            userTables, userAllColumns, whereClause, 0);
        return cpy;
    }

    /**
     * Re-initialize when we are de-serialized.
     */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      in.defaultReadObject();
      initialize(0);
    }

//    abstract String getFirstRowQuery(String tableName);
//    abstract String getNumRecordsQuery(String tableName);
//    abstract String getAllTableNamesQuery();

    /*public void setDBInstance(String[] tables, String[][] columns,
        String whereClause, int cacheType) {

        setUserSelectedTables(tables);
        setUserSelectedCols(columns);
        setUserSelectedWhere(whereClause);
        initialize(cacheType);
    }*/

    /**
     * Functions related to the Table/Column/Cache selections made by the user
     */



    public void       setUserSelectedTables(String[] _userTables){
        this.userTables = _userTables;
    }

    public void       setUserSelectedCols (String[][] _userColumns){
        this.userAllColumns = _userColumns;
    }

    public void       setUserSelectedWhere(String _where){
        this.whereClause = _where;
    }

    public String[]   getUserSelectedTables() {
        return this.userTables;
    }

    public String[][] getUserSelectedCols (){
        return this.userAllColumns;
    }

    public String     getUserSelectedWhere(){
        return this.whereClause;
    }





    private String[]   getDistinctUserSelectedCols(){
        String[][] columns = this.userAllColumns;

        ////PROCESS DIFFERENTLY FOR SINGLE TABLE SELECTIONS AND MULTIPLE TABLE SELECTIONS

        if (columns.length == 1) {
//System.out.println("***SQLDBDonncection: getDistinctUserSelectedCols - single table***");
//for (int k=0; k<columns[0].length; k++) {
//    System.out.println(columns[0][k]);
//}
            return columns[0];
        }
        else {  // columns.length > 1

            //first: count the # columns in the 2D-array columns (including duplicates)
            int numAllCols = 0;
            for (int tabl = 0; tabl < columns.length; tabl++)
                numAllCols += columns[tabl].length;

            //second: initialize space for a 1-D string array of all columns and copy into it
            String[] allCols = new String[numAllCols];
            int i = 0;
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++)
                    allCols[i++] = columns[tabl][tablCol];

            //third: separate the columns into uniqueColumns and duplicateColumns
            Set uniques = new HashSet();
            Set dups = new HashSet();

            for (int j=0; j<allCols.length; j++)
                if (!uniques.add(allCols[j]))
                    dups.add(allCols[j]);

            uniques.removeAll(dups);  // Destructive set-difference

            String unqCols[] = (String[]) uniques.toArray( new String[ uniques.size() ] );
            String dupCols[] = (String[]) dups.toArray( new String[ dups.size() ] );

            //fourth: combine uniqueColumns and duplicateColumns
            String unqDupCols[] = new String[unqCols.length + dupCols.length];
            int numUnq = 0;
            for (numUnq=0; numUnq<unqCols.length; numUnq++)
                unqDupCols[numUnq] = unqCols[numUnq];

            int numDup = 0;
            for (numDup=0; numDup<dupCols.length; numDup++)
                unqDupCols[numDup+unqCols.length] = dupCols[numDup];

System.out.println("***SQLDBDonncection: getDistinctUserSelectedCols - multiple tables***");
for (int k=0; k<unqDupCols.length; k++) {
    System.out.println(unqDupCols[k]);
}
            return unqDupCols;
        }

    } //getDistinctUserSelectedCols

    protected void     setNumDistinctUserSelectedCols (String[][] columns){
        numDistinctUserColumns = this.getDistinctUserSelectedCols().length;
    }

    public int   getNumDistinctColumns(){
        return numDistinctUserColumns;
    }

/*
    protected void     setUserSelectedWhere(String _where){
        whereClause = _where;
    }
    protected String   getUserSelectedWhere(){
        return whereClause;
    }
*/

    private void initialize(int cacheType) {
        try {
//            Connection con = dbconnection.getConnection();
//            Statement stmt = con.createStatement();
//            String query = this.getDatabaseQuery();
//            ResultSet rs = stmt.executeQuery(this.userQuery);
//            ResultSetMetaData rsmd = rs.getMetaData();

//            DBDataSource dbds = new
            /*cache = new FixedRowCache(dbconnection.getResultSet(this.userTables,
                                                                this.userAllColumns,
                                                                this.whereClause));

                                                                */
                DBDataSource dbds = new ResultSetDataSource(
                    dbconnection/*.getResultSet(userTables, userAllColumns, whereClause)*/,
                    userTables, userAllColumns, whereClause);
                cache = new FixedRowCache(dbds);


                                                                            ////////FIX////////////
//            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        setNumUserSelectedRows();
        setNumDistinctUserSelectedCols (null);
        columnLabels = this.getDistinctUserSelectedCols();
    }

    /**
     * Creates new Cache Object depending on the CacheStyle chosen by the user
     /
    public void setCache(String _userCacheName){
        if (_userCacheName == "FixedRowCache") {//////////////USE CACHEFACTORY//////////
            //cache = new FixedRowCache(0, 0);

            try {
                Connection con = this.getConnection();
                Statement stmt = con.createStatement();
                String query = this.getDatabaseQuery();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();



                // calculate MaxCacheRows //(potentially using #rows, #cols, colDatatypes of the result set)
                //cache.setMaxCacheRows(10);

                cache.table = new TableImpl(rsmd.getColumnCount());   //Initialize TableImpl : Cache
                for (int col = 1; col <= rsmd.getColumnCount(); col++) { //Initialize  Columns of the TableImpl
                    if (rsmd.getColumnType(col) == 2)
                        cache.table.setColumn(new DoubleColumn(cache.getMaxCacheRows()), col-1);
                    else // //(rsmd.getColumnType(col) == 12)
                        cache.table.setColumn(new StringColumn(cache.getMaxCacheRows()), col-1);
                }

                //cache.table.setLabel("table");
                //cache.table.setComment("table");
                //cache.table.setKeyColumn(0);

                // set Labels, isNominal, isScaler in the TableImpl
                for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                    cache.table.setColumnLabel(rsmd.getColumnLabel(col), col-1);
                    //cache.table.setColumnIsNominal(false, col-1);
                    //cache.table.setColumnIsScalar(false, col-1);
                }


                // now populate the cache for the first time
                int numRowsAddedToCache = 0;
                while (  (rs.next()) && (numRowsAddedToCache < cache.getMaxCacheRows()) ) {
                    for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                        if (rsmd.getColumnType(col) == 2) {
                            double data = rs.getDouble(col);
                            //System.out.println("number: " + data + " (r,c) = (" + r + "," + c + ")");
                            cache.table.setDouble(data, numRowsAddedToCache, col-1);
                        }
                        else { //(rsmd.getColumnType(c) == 12)
                            String data = rs.getString(col);
                            //System.out.println("string: " + data + " (r,c) = (" + r + "," + c + ")");
                            cache.table.setString(data, numRowsAddedToCache, col-1);
                        }
                    }//for loop processes all the columns for a particular row
                    numRowsAddedToCache++;
                }//while

                //now initialize how many rows were written to the cache
                cache.setNumCacheRows(numRowsAddedToCache);


                //now initialize minRowNum & maxRowNum  (THIS COULD GO IN THE FixedRowCache() CONSTRUCTOR)
                cache.minRowNum = 0;
                cache.maxRowNum = cache.minRowNum + cache.getNumCacheRows() -1;

                stmt.close();
            }
            catch (Exception e) {
    System.out.println("Error in FixedRowCache:Constructor");
                e.printStackTrace();
            }
        }//if "FixedRowCache" //////////////USE CACHEFACTORY//////////

 /////////////TODO//////////

        else {  // DEFAULT / OTHER KIND OF CACHE TO USE
            cache = new FixedRowCache(0);

            try {
                Connection con = this.getConnection();
                Statement stmt = con.createStatement();
                String query = this.getDatabaseQuery();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();

                // calculate MaxCacheRows //(potentially using #rows, #cols, colDatatypes of the result set)
                cache.setMaxCacheRows(10);

                cache.table = new TableImpl(rsmd.getColumnCount());   //Initialize TableImpl : Cache
                for (int c = 1; c <= rsmd.getColumnCount(); c++) { //Initialize  Columns of the TableImpl
                    if (rsmd.getColumnType(c) == 2)
                        cache.table.setColumn(new DoubleColumn(cache.getMaxCacheRows()), c-1);
                    else // //(rsmd.getColumnType(c) == 12)
                        cache.table.setColumn(new StringColumn(cache.getMaxCacheRows()), c-1);
                }

                // now populate the cache for the first time
                int numRowsAddedToCache = 0;
                while (  (rs.next()) && (numRowsAddedToCache < cache.getMaxCacheRows()) ) {
                    for (int c = 1; c <= rsmd.getColumnCount(); c++) {
                        if (rsmd.getColumnType(c) == 2) {
                            double data = rs.getDouble(c);
                            //System.out.println("number: " + data + " (r,c) = (" + r + "," + c + ")");
                            cache.table.setDouble(data, numRowsAddedToCache, c-1);
                        }
                        else { //(rsmd.getColumnType(c) == 12)
                            String data = rs.getString(c);
                            //System.out.println("string: " + data + " (r,c) = (" + r + "," + c + ")");
                            cache.table.setString(data, numRowsAddedToCache, c-1);
                        }
                    }//for loop processes all the columns for a particular row
                    numRowsAddedToCache++;
                }//while

                //now initialize how many rows were written to the cache
                cache.setNumCacheRows(numRowsAddedToCache);


                //now initialize minRowNum & maxRowNum  (THIS COULD GO IN THE FixedRowCache() CONSTRUCTOR)
                cache.minRowNum = 0;
                cache.maxRowNum = cache.minRowNum + cache.getNumCacheRows() -1;

                stmt.close();
            }
            catch (Exception e) {
    System.out.println("Error in FixedRowCache:Constructor");
                e.printStackTrace();
            }

        }//ELSE

    }
    */


//////////REMOVE THIS FROM HERE////////////

     // Caching Mechanism Followed :
        // On a cache miss, write upto getNumCacheRows() records of the ResultSet rs,
        // beginning from record# rowInResultSet
    public void updateCache (int rowInResultSet) {
        try {
//System.out.println("first: update minRowNum");
//            cache.minRowNum = rowInResultSet;

//System.out.println("second: connect to the Database");
//            Connection con = dbconnection.getConnection();//this.getConnection();
//            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
//                                                 ResultSet.CONCUR_UPDATABLE);
//            String query = this.getDatabaseQuery();
//            ResultSet rs = stmt.executeQuery(this.userQuery);
            //ResultSetMetaData rsmd = rs.getMetaData();


//            cache.update(rowInResultSet, rs);
            cache.update(rowInResultSet,
                new ResultSetDataSource(dbconnection/*.getResultSet(this.userTables,
                                                                   this.userAllColumns,
                                                                   this.whereClause)*/,
                                                                   userTables,
                                                                   userAllColumns,
                                                                   whereClause));
//            stmt.close();
         }
         catch(Exception e) {
            e.printStackTrace();
         }
    }//UpdateCache()



    protected void setNumUserSelectedRows(){
        try{
            // connect to the database and query it
//            Connection con = dbconnection.getConnection();//this.getConnection();
//            Statement stmt = con.createStatement();

//            String query = this.getDatabaseQuery();
//            ResultSet rs = stmt.executeQuery(this.userQuery);

            ResultSet rs = dbconnection.getResultSet(this.userTables,
                                                     this.userAllColumns,
                                                     this.whereClause);
            int count = 0;
            rs.first();
            while (rs.next()){
                count++;
            }
            this.numUserRows = count;
//            stmt.close();
        }
        catch (Exception e) {
//System.out.println("Error in SQLDBConnection: SetNumUserSelectedRows: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getNumRows(){
        return numUserRows;
    }

    /**
     * Map java.sql.type to ColumnTypes
     * @param pos
     * @return ColumnTypes
     */
    public int getColumnType (int pos) {
        if      (getColumnSQLType(pos) == java.sql.Types.VARCHAR)
            return ColumnTypes.STRING;
        else if (getColumnSQLType(pos) == java.sql.Types.REAL)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.NUMERIC)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.INTEGER)
            return ColumnTypes.INTEGER;
        else if (getColumnSQLType(pos) == java.sql.Types.FLOAT)
            return ColumnTypes.FLOAT;
        else if (getColumnSQLType(pos) == java.sql.Types.DOUBLE)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.DECIMAL)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.CHAR)
            return ColumnTypes.CHAR;
        else if (getColumnSQLType(pos) == java.sql.Types.FLOAT)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.JAVA_OBJECT)
            return ColumnTypes.OBJECT;
        else if (getColumnSQLType(pos) == java.sql.Types.LONGVARCHAR)
            return ColumnTypes.STRING;
        else
            return ColumnTypes.STRING;
    }

    /**
     *
     * @param pos
     * @return
     */
    private int getColumnSQLType (int pos) {
        try{
//            Connection con = dbconnection.getConnection();
//            Statement stmt = con.createStatement();
//            String query = this.getDatabaseQuery();
//            ResultSet rs = stmt.executeQuery(this.userQuery);

            ResultSet rs = dbconnection.getResultSet(this.userTables,
                                                     this.userAllColumns,
                                                     this.whereClause);
            ResultSetMetaData rsmd = rs.getMetaData();
            return rsmd.getColumnType(pos+1);
       }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }






    public String getTextData(int row, int col) {
        try {
            return cache.getCacheTextData(row, col);
        }
        catch (CacheMissException cme) {
            try {
//System.out.println("GetTextData: CacheMissExceptionA");
                this.updateCache(row);              ////////////////FIX//////////////
//System.out.println("GetTextData: CacheMissExceptionB");
                return cache.getCacheTextData(row, col); //return data from the updated cache
            }
            catch (CacheMissException cmeError) {
                System.out.println("SQLDBConnection:GetTextData:Warning: " +
                                   "Cache Miss After Update " + cmeError.getMessage());
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    } //GetTextData

    public double getNumericData(int row, int col) {
        try {
            return cache.getCacheNumericData(row, col);
        }
        catch (CacheMissException cme) {
            //System.out.println("SQLDBConnection:GetNumericData " + cme.getMessage());

            try {
                this.updateCache(row);               ////////////////FIX//////////////
                return cache.getCacheNumericData(row, col); //return data from the updated cache
            }
            catch (CacheMissException cmeError) {
                //System.out.println("SQLDBConnection:GetTextData:WARNING: " +
                //                   "Cache Miss After Update " + cmeError.getMessage());
                return 0.0;
            }
            catch (Exception e) {
                e.printStackTrace();
                return 0.0;
            }
        }
    } //GetNumericData

    public boolean getBooleanData(int row, int col) {
        try {
            return cache.getCacheBooleanData(row, col);
        }
        catch (CacheMissException cme) {
            //System.out.println("SQLDBConnection:getBooleanData " + cme.getMessage());

            try {
                this.updateCache(row);               ////////////////FIX//////////////
                return cache.getCacheBooleanData(row, col); //return data from the updated cache
            }
            catch (CacheMissException cmeError) {
                //System.out.println("SQLDBConnection:getBooleanData:WARNING: " +
                //                   "Cache Miss After Update " + cmeError.getMessage());
                return false;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    } //getBooleanData

    public Object getObjectData(int row, int col) {
        try {
            return cache.getCacheObjectData(row, col);
        }
        catch (CacheMissException cme) {
            //System.out.println("SQLDBConnection:GetNumericData " + cme.getMessage());

            try {
                this.updateCache(row);               ////////////////FIX//////////////
                return cache.getCacheObjectData(row, col); //return data from the updated cache
            }
            catch (CacheMissException cmeError) {
//                System.out.println("SQLDBConnection:getObjectData:WARNING: " +
//                                   "Cache Miss After Update " + cmeError.getMessage());
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    } //getObjectData




    protected void     setDBInstance (String dbInst) {
        dbInstance = dbInst;
    }

    protected String     getDBInstance() {
        return dbInstance;
    }

    public String     getProductName() throws InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException{
        return dbconnection.getConnection().getMetaData().getDatabaseProductName();
    }



    /**
     * Functions related to the Connection
     /
    public Connection     getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
        return cw.getConnection();
    }/

    public ConnectionWrapperImpl    getConnectionWrapper() {
        return cw;
    }*/



    /*public String[]     getTableNames() {
//System.out.println("entering getAllTableNames..");
        String tables[];
        Vector vec = new Vector();

        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getAllTableNamesQuery());
          //int idx = 0;
          while (rs.next())
              //tables[idx++] = rs.getString(1);
              vec.addElement((Object)rs.getString(1));
          stmt.close();

          // copy over the tables in the Vector into an Array of Strings
          tables = new String[vec.size()];
          for (int i=0; i<vec.size(); i++) {
              tables[i] = vec.elementAt(i).toString();
          }

          return tables;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(null,
                e.getMessage(), "Error0",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetTableNames.");
           return null;
        }
    }*/


    /*public String[]     getColumnNames (String tableName) {
//System.out.println("entering GetColumnNames..");
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          String columns[] = new String[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columns[i-1] = rsmd.getColumnName(i);

          stmt.close();
          return columns;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error1",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetColumnNames.");
           return null;
        }
    }


    public String[]     getColumnTypes (String tableName) {
//System.out.println("entering GetAllColumnTypes..");
        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          String columnsTypes[] = new String[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columnsTypes[i-1] = rsmd.getColumnTypeName(i);

          stmt.close();
//System.out.println("exiting GetAllColumnTypes..");
          return columnsTypes;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error2",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetAllColumnTypes.");
           return null;
        }
    }

    public String     getColumnType (String tableName, String columnName) {
//System.out.println("entering GetColumnType..");
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          String type = new String();

          for (int i = 1; i <= rsmd.getColumnCount(); i++) {
              if (rsmd.getColumnName(i) == columnName) {
                  type = rsmd.getColumnName(i);
                  break;
              }
          }
          stmt.close();
          return type;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error3",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetColumnType.");
           return null;
        }
    }


    public int    getColumnLength (String tableName, String columnName) {
System.out.println("entering GetColumnLength..");
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int length = 0;

          for (int i = 1; i <= rsmd.getColumnCount(); i++) {
              if (rsmd.getColumnName(i) == columnName) {
                  length = rsmd.getColumnDisplaySize(i);
                  break;
              }
          }
          stmt.close();
          return length;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error3",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetColumnType.");
           return 0;
        }
    }


    public int[]    getColumnLengths(String tableName) {
//System.out.println("entering GetAllColumnLengths..");
        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnsLengths[] = new int[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columnsLengths[i-1] = rsmd.getColumnDisplaySize(i);

          stmt.close();
//System.out.println("exiting GetAllColumnTypes..");
          return columnsLengths;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error2",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetAllColumnTypes.");
           return new int[(0)];
        }
    }

    public int    getNumRecords (String tableName) {
//System.out.println("entering GetNumRecords..");
        int numRecords = 0;
        try {
          Connection con = getConnection();
          String query = getNumRecordsQuery(tableName);
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(query);
          while (rs.next()) {
              numRecords = rs.getInt(1);
          }
          numRecords--; ///////////For some reason, numRecords is one more than it should be..
          stmt.close();
//System.out.println("SQLDBConnection:GetNumRecords: " + numRecords);
          return numRecords;
        }
        catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error*6",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occoured in GetNumRecords.");
           return numRecords;
        }
    }
    */

    public String getColumnLabel(int pos) {
//for (int i=0; i<columnLabels.length; i++) {
//    System.out.println("label" + i + ": " + columnLabels[i]);
//}
        if (pos < columnLabels.length)
            return columnLabels[pos];
        else {
System.out.println("Column Label position : " + pos + " out of bounds");
            return null;
        }
    }

    public String getColumnComment(int pos) {
        if (pos < columnComments.size())
            return (String)columnComments.get(pos);
        else {
System.out.println("Column Comment position : " + pos + " out of bounds");
            return null;
        }
    }

/*
    public Connection getConnection() throws SQLException, ClassNotFoundException,
    InstantiationException, IllegalAccessException {
        return dbconnection.getConnection();
    }*/
} //SQLDBConnection