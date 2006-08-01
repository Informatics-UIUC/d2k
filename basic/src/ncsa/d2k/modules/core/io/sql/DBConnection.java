package ncsa.d2k.modules.core.io.sql;

import java.sql.ResultSet;

/**
 * <p>Title: DBConnection</p>
 * <p>Description: An Interface having generic metadata-level methods for a Database</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur
 * @version 1.0
 */

public interface DBConnection extends ConnectionWrapper, java.io.Serializable {

    /**
     * 
     * Generate and Execute an Create table statement yields structure
     * 		populated with specified number of rows using sequence generator
     * 		and null values for all other columns.  
     *  
     * @param tableName <code>String</code> name of table to create
     * @param columnNames <code>String[]</code> array of column names 
     * @param columnType <code>String[]</code> array of data-type for the new columns
     * @param colDisplaySizes <code>int[]</code> array of max data lengths for columns
     * @param numRows <code>int</code> the number of rows to create 
     *  
     */   
    public void createTable (String   tableName,
                             String   seqName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes,
                             int      numRows);
    /**
     * 
     * Generate and Execute an Create table statement yields empty structure. 
     *  
     * @param tableName <code>String</code> name of table to create
     * @param columnNames <code>String[]</code> array of column names 
     * @param columnType <code>String[]</code> array of data-type for the new columns
     * @param colDisplaySizes <code>int[]</code> array of max data lengths for columns
     *  
     */   
    public void createTable (String   tableName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes);
    /**
     * 
     * Generate and Execute an Alter table statement to add new column 
     *  
     * @param tableName <code>String</code> name of table to modify
     * @param columnName <code>String</code> name of column to add
     * @param columnType <code>String</code> name of data-type for the new column
     *  
     */   
    public void addColumn (String tableName, String columnName, String columnType);
    /**
     * 
     * Generate and Execute A Drop database table statement
     *  
     * @param tableName <code>String</code> name of table to drop
     *  
     */   
    public void dropTable(String tableName);
    /**
     * Given 
     * 		array of table names in a database, 
     * 		array of column names, 
     * 		and optional "where clause".
     * 
     * @return <code>java.sql.ResultSet</code> marked as Read-Only  
     */
    public ResultSet getResultSet(String[] tables, String[][] columns, String where);
    /**
     * Given 
     * 		array of table names in a database, 
     * 		array of column names, 
     * 		and optional "where clause".
     * 
     * @return <code>java.sql.ResultSet</code> marked as Updatable  
     */
    public ResultSet getUpdatableResultSet(String[] tables, String[][] columns, String where);
    /**
     * Given 
     * 		the name of a table in a database, 
     * 		array of column names, 
     * 		and optional "where clause".
     * 
     * @return <code>java.sql.ResultSet</code> marked as Updatable  
     */
    public ResultSet getUpdatableResultSet(String table, String[] columns, String where);

    /**
     * Get the names of all the tables in a database.
     * @return <code>String[]</code>
     */
    public String[]           getTableNames();

    /**
     * Given a table name, get the names of all its columns.
     * @param tableName
     * @return <code>String[]</code>
     */
    public String[]           getColumnNames      (String tableName);

    /**
     * Given a table name, get the datatypes of all its columns.
     * @param tableName
     * @return <code>int[]</code>
     */
    public int[]           getColumnTypes   (String tableName);

    /**
     * Given a table name and a column name, get the datatype of the column.
     * @param tableName
     * @param columnName
     * @return <code>int</code>
     */
    public int             getColumnType       (String tableName, String columnName);

    /**
     * Given a table name, get the lengths of all the columns.
     * @param tableName
     * @return <code>int[]</code>
     */
    public int[]              getColumnLengths    (String tableName);

    /**
     * Given a table name and a column name, get the length of the column.
     * @param tableName
     * @param columnName
     * @return <code>int</code>
     */
    public int                getColumnLength     (String tableName, String columnName);

    /**
     * Given a table name, get the number of records in that table.
     * @param tableName
     * @return <code>int</code>
     */
    public int                getNumRecords       (String tableName);

}//DBConnection