package ncsa.d2k.modules.core.datatype.table.db.sql;

//import ncsa.d2k.core.modules.*;
import java.sql.*;
import java.sql.Types;
import java.util.*;
import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.db.*;

import ncsa.d2k.modules.core.io.sql.*;

/**
 */
public class ResultSetDataSource implements DBDataSource {

    /** The Connection to the DB */
    protected DBConnection dbConnection;

    /** The tables */
    protected String[] userTables;
    /** The columns */
    protected String[][] userAllColumns;
    /** the where */
    protected String whereClause;

    protected String userQuery;

    /** the number of distinct columns */
    protected int numDistinctUserColumns;
    /** The number of rows */
    private int numUserRows;

    /** the comments */
    private String[] columnComments;
    /** the labels */
    protected String[] columnLabels;

    /** the ResultSet holds the data */
    transient public ResultSet rs;
    /** the meta data about the result set */
    transient protected ResultSetMetaData rsmd;
    /** the statement that produces a result set */
    transient protected Statement stmt;

    /**
     * Construct a new ResultSetDataSource
     * @param connection the connection to the DB
     * @param tables the names of the tables in the DB that this data source will
     *     query
     * @param columns the columns in the DB that this data source will represent
     * @param whereClause the where
     */
/*
    public ResultSetDataSource(ConnectionWrapper connection, String[] tables,
                        String[][]columns, String whereClaus) {
        connectionWrapper = connection;
        userTables = tables;
        userAllColumns = columns;
        whereClause = whereClaus;

        //userQuery =

        initialize();
    }
*/

    /*public ResultSetDataSource(DBConnection connection,
                               String[]   tables,
                               String[][] columns,
                               String whereClaus) {
        this.dbConnection = connection;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClaus);
    //    userQuery = dbConnection.getTableQuery(tables, columns, whereClaus);
        initialize();
    }*/

    /*public ResultSetDataSource(ResultSet rset,
                               String[]   tables,
                               String[][] columns,
                               String whereClaus) {
//        this.dbConnection = connection;
        rs = rset;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClaus);
    //    userQuery = dbConnection.getTableQuery(tables, columns, whereClaus);
        initialize();
    }*/

     public ResultSetDataSource(DBConnection dbcon,
                               String[]   tables,
                               String[][] columns,
                               String whereClaus) {
        this.dbConnection = dbcon;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClaus);
    //    userQuery = dbConnection.getTableQuery(tables, columns, whereClaus);

//        rs = dbConnection.getResultSet(tables, columns, whereClaus);
        rs = dbConnection.getUpdatableResultSet(tables, columns, whereClaus);
        initialize();
    }

/*    public ResultSetDataSource(ResultSet rset) {
//        this.dbConnection = connection;
//        initialize();
        rs = rset;
        initialize();
    }
    */

    protected ResultSetDataSource() {
    }

    /**
     * Close all connections when we are GC'd.
     */
    protected void finalize() throws Throwable {
        try {
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
        }
        catch(SQLException e) {
            rs = null;
            stmt = null;
        }
    }

    /**
     * Re-initialize when we are de-serialized.
     */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      in.defaultReadObject();
      rs = dbConnection.getResultSet(userTables, userAllColumns, whereClause);
      initialize();
    }

    /**
     * Close all connections when we are serialized.
     /
    private void writeObject(ObjectOutputStream out)
        throws IOException, Exception {
        out.defaultWriteObject();

        rs.close();
        stmt.close();
        rs = null;
        rsmd = null;
        stmt = null;
    }*/

    /**
     * Return a copy of this.  The copy will use the same ConnectionWrapper,
     * but will have its own cached ResultSet.
     */
    public DBDataSource copy() {
//        return new ResultSetDataSource(dbConnection,  userTables,
//                                       userAllColumns, whereClause);
        return new ResultSetDataSource(dbConnection, userTables, userAllColumns, whereClause);
    }




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

    protected String[]   getDistinctUserSelectedCols(){
        String[][] columns = userAllColumns;

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

    /**
     * Get the number of columns that this data source represents
     */
    public int      getNumDistinctColumns(){
        return numDistinctUserColumns;
    }

    /**
     * Initialize this data source.  Connect to the DB, initialize the result set,
     * set the fetch size, count the number of rows, and get the labels of
     * the columns.
     */

    protected void initialize() {
        try {
//            this.rs = this.dbConnection.getResultSet(this.userTables, this.userAllColumns, this.whereClause);
            rs.setFetchSize(350);
            rsmd = rs.getMetaData();
            countRows();
            numDistinctUserColumns = getDistinctUserSelectedCols().length;
            columnLabels = getDistinctUserSelectedCols();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Count the number of rows that the query generates.
     */
    protected void countRows(){
        try{
            rs.last();
            this.numUserRows = rs.getRow();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the number of rows.
     */
    public int getNumRows(){
        return numUserRows;
    }

    /**
     * Get the labels of the column at pos.
     */
    public String getColumnLabel(int pos) {
        return columnLabels[pos];
    }

    public String getColumnComment(int pos) {
        return columnComments[pos];
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
           return rsmd.getColumnType(pos+1);
       }
        catch(SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public String getTextData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getString(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } //GetTextData

    public double getNumericData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getDouble(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    } //GetNumericData

    public boolean getBooleanData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getBoolean(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    } //getBooleanData

    public Object getObjectData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getObject(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } //getObjectData


} //SQLDBConnection