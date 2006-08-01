package ncsa.d2k.modules.core.io.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MySQLDBConnection extends SQLDBConnection {

    private String dbInstance;
    //~ Static fields/initializers **********************************************

    /** Use serialVersionUID for interoperability. */
    static private final long serialVersionUID = 2541720013912935285L;

    //~ Constructors ************************************************************

    /**
     * Creates a new MySQLDBConnection object.
     *
     * @param _url      <code>String</code>JDBC URL .
     * @param _driver   <code>String</code>JDBC Driver.
     * @param _username <code>String</code>Database username.
     * @param _password <code>String</code>Database password.
     */
    public MySQLDBConnection(String _url, String _driver, String _username, String _password,
                           String _dbinstance) {
        super(_url, _driver, _username, _password);
        dbInstance = _dbinstance;
    }

    //~ Methods  ************************************************************

    /**
     * Method getAllTableNamesQuery()
     * 
     * Constructs SQL Query to return as String in the form;
     * 	SHOW TABLES FROM dbInstance 
     *
     * @return <code>String</code> continaing query string to fetch list of
     *         tables 
     *         
     * TODO: depricate this implementation replace with JDBC metadata.getTables().
     * 
     */    
    public String     getAllTableNamesQuery() {
    	//System.out.println("mySQLConnect:QueryTableNames:dbI:" + this.dbInstance);
        String str = "SHOW TABLES FROM " + this.dbInstance;
        return str;
    }

    /**
     * Method getFirstRowQuery().
     *
     * Constructs SQL Query to fetch one row form a given table in the form;
     * 	SELECT * FROM " + tableName    --> There is no limitation here   
     * 
     * @param  tableName Description of parameter tableName.
     *
     * @return <code>String</code> continaing query string to fetch a row from
     *         table 
     *         
     * TODO: depricate this implementation replace with JDBC metadata.getColumns(), 
     * 		this method appears to be used in cases where dictionary references 
     * 		would be better suited to accomplish the work versus accessing the 
     * 		physical table directly to determine atrtributes from ResultSet.
     * 
     */
    public String     getFirstRowQuery (String tableName) {
        String str = "SELECT * FROM " + tableName;
        return str;
    }

    /**
     * Method createTable()
     *
     * Using string derived from createTableWithoutSeqQuery() execute DDL 
     * 		statement that results in empty table begin constructed.
     *
     * @param tableName       <code>String</code> name of table to create
     * @param colNames        <code>String[]</code> array of column names for
     *                        table
     * @param colTypeNames    <code>String[]</code> array of SQL data types for
     *                        columns
     * @param colDisplaySizes <code>int[]</code> array of int to define column
     *                        widths
     *                         
     * TODO: Correct catch() for SQLException cases, throwing others</p>
     */
    public void createTable (String   tableName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes) {
        try {

            // 1.5 Connect to the Database
            Connection con  = this.getConnection();

            // 4. connect to the database and create the prediction table
            Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE);
            String query1 = this.createTableWithoutSeqQuery(tableName,
                                                            colNames,
                                                            colTypeNames,
                                                            colDisplaySizes);
            /* int createTableFlag = */
            stmt1.executeUpdate(query1);
            //System.out.println("createTableFlag = " + createTableFlag);
            stmt1.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createPredictionTable

    /**
     * Method createTable().
     * 
 	 * Create Sequence counter and using string derived from 
 	 * 		createTableWithSeqQuery() execute DDL statements, then populates 
 	 * 		new table with the requested number of null values rows.
     *
     * @param tableName       <code>String</code> name of table to create
     * @param seqName         <code>Sting</code> name of Sequence counter to
     *                        create
     * @param colNames        <code>String[]</code> array of column names for
     *                        table
     * @param colTypeNames    <code>String[]</code> array of SQL data types for
     *                        columns
     * @param colDisplaySizes <code>int[]</code> array of int to define column
     *                        widths
     * @param numRows         <code>int</code> number of rows to insert into new
     *                        table (default values)
     *
     * TODO: Correct catch() for SQLException cases, throwing others, reuse 
     * 		Statment Objects. Consider the purpose of the resultant, Sequence
     * 		names could be derived from tableName, populated rows are probably
     * 		not necessary
     */
    public void createTable (String   tableName,
                             String   seqName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes,
                             int      numRows) {
        try {

            // 1.5 Connect to the Database
            Connection con  = this.getConnection();

            // 4. connect to the database and create the prediction table
            Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE);
            String query1 = this.createTableWithSeqQuery(tableName,
                                                         colNames,
                                                         colTypeNames,
                                                         colDisplaySizes);
            /* int createTableFlag = */
            stmt1.executeUpdate(query1);
            //System.out.println("createTableFlag = " + createTableFlag);
            stmt1.close();

            // 6. Insert 'tableConnection.getNumRows()' Sequence # and NULL's into the table
            Statement stmt2;
            String query2 = this.insertTableWithSeqQuery(tableName,
                                                         seqName,
                                                         colNames,
                                                         colTypeNames,
                                                         colDisplaySizes);
            //System.out.println("query2 : " + query2);
            // int insertTableFlag;

            for (int i=0; i<numRows; i++) {
                stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                            ResultSet.CONCUR_UPDATABLE);
                /* insertTableFlag = */ stmt2.executeUpdate(query2);
                stmt2.close();
            }
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createTable

    /**
     * Method createTableWithSeqQuery().
     *
     * Generates SQL DDL statement to Create table in the form;
     * 	Create table tablename ( column datatype( maxlength) [, ...])
     *	first column will be named SEQ_NUM and a Seqeunce generator defined,
     *	this column is also declared as primary key for this new table.
     *
     * @param  _tableName       <code>String</code> name of table to
     *                          create
     * @param  _colNames        <code>String[]</code> array of
     *                          column names for table
     * @param  _colTypeNames    <code>String[]</code> array of SQL
     *                          data types for columns
     * @param  _colDisplaySizes <code>int[]</code> array of int to
     *                          define column widths
     *
     * @return <code>String</code> containing a Create Table statement.
     *
     * TODO: Consolidate CreateTable Methods using boolean for Sequence or not.
     * 
     */
    private String createTableWithSeqQuery (String _tableName,
                                     String[] _colNames,
                                     String[] _colTypeNames,
                                     int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");
        str += "SEQ_NUM int NOT NULL AUTO_INCREMENT, PRIMARY KEY (SEQ_NUM), ";

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
                str += "(";
                str += _colDisplaySizes[i];
                str += ")";
            }
            else { // if (colTypeNames[i] == "number")
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";
        //System.out.println("createTableQuery with SEQ_NUM" + str);
        return str;
    }

    /**
     * Method createTableWithoutSeqQuery()
     *
     * Generates SQL DDL statement to Create table in the form;
     * 	Create table tablename ( column datatype( maxlength) [, ...])
     *
     * @param  _tableName       <code>String</code> name of table to
     *                          create
     * @param  _colNames        <code>String[]</code> array of
     *                          column names for table
     * @param  _colTypeNames    <code>String[]</code> array of SQL
     *                          data types for columns
     * @param  _colDisplaySizes <code>int[]</code> array of int to
     *                          define column widths
     *
     * @return <code>String</code> containing a Create Table statement.
     *
     * TODO: Consolidate CreateTable Methods using boolean for Sequence or not.
     * 
     */    
    private String createTableWithoutSeqQuery (String _tableName,
                                               String[] _colNames,
                                               String[] _colTypeNames,
                                               int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
                str += "(";
                str += _colDisplaySizes[i];
                str += ")";
            }
            else { // if (colTypeNames[i] == "number")
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";
        //System.out.println("createTableQuery without SEQ_NUM" + str);
        return str;
    }

    /**
     * Method insertTableWithSeqQuery()
     * 
     * Generate SQL DML statement to insert row into named table using
     * 	list of column names supplied and the Sequence generator name. 
     *  MySQL database supports Autoincrement, so SequenceName is not used. 
     *
     * @param  _tableName       <code>String</code> name of table to
     *                          create
     * @param  _seqName         <code>Sting</code> name of Sequence
     *                          counter to create 
     * @param  _colNames        <code>String[]</code> array of
     *                          column names for table
     * @param  _colTypeNames    <code>String[]</code> array of SQL
     *                          data types for columns
     * @param  _colDisplaySizes <code>int[]</code> array of int to
     *                          define column widths
     *
     * @return <code>String</code> continaing a Insert into Table statement.
     *
     * TODO: Consolidate insertTable Methods, not necessary to include
     *         type and widths ??  Convention could enable SEQ Names derived
     *         from table_name to avoid having to supply name, and row level
     *         event trigger could be used to avoid manual handling of sequence
     *         values. 
     */
    private String insertTableWithSeqQuery (String _tableName,
                                            String _seqName,
                                            String[] _colNames,
                                            String[] _colTypeNames,
                                            int[] _colDisplaySizes) {
        String str = "INSERT INTO ";
        str += _tableName;
        str += " (";

        for (int i=0; i<_colNames.length; i++) {
            str += _colNames[i];
            if (i<_colNames.length-1)
                str += ", ";
        }
        str += ") VALUES (";

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += "'x'";
            }
            else { // if (colTypeNames[i] == "number")
                str += "0";
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";

        //System.out.println("insertTableQuery " + str);
        return str;
    }

    /**
     * Method getTableQuery() creates a SQL Select Statement.
     *
     * @param  tables  <code>String[]</code> Array of tableNames to focus on.
     * @param  columns <code>String[][]</code> Array columnNames indexed by table.
     * @param  where   <code>String</code> fully formatted where clause
     *
     * @return Description of return value.
     * 
     * TODO: Method should be refactored to alias column names with table identifier,
     * 		sample code ncluded as comment in PostgresDBConnection().
     * 
     */    
    public String getTableQuery(String[] tables, String[][] columns, String where) {

        StringBuffer query = new StringBuffer();
        if (tables.length == 1) { //USER SELECTED ONLY 1 TABLE
            query.append("SELECT ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }
            query.append(" FROM ");
            query.append(tables[0]);

            if ((where != null) && (where.length() > 0)) {
                query.append(" WHERE ");
                query.append(where);
            }
            query.append(" ORDER BY ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }
            return query.toString();
        }
        else 
        {                                                  
            // separate the columns into uniqueColumns and duplicateColumns
            int i = 0;
            Set uniques = new HashSet();
            Set dups = new HashSet();
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++)
                    if (!uniques.add(columns[tabl][tablCol]))
                        dups.add(columns[tabl][tablCol]);

            uniques.removeAll(dups);  // Destructive set-difference

            Vector uniqueVec = new Vector(uniques);
            Vector duplicateVec = new Vector(dups);

            // First : Create SELECT Clause
            // query.append("SELECT * FROM (SELECT ");
            query.append("SELECT ");

            for (int l=0; l<duplicateVec.size(); l++){
                // get the 2 tables that a duplicate column belongs to
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                // int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateVec.elementAt(l) ) {
                            // idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<DuplicateColumnName>"
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateVec.elementAt(l));
                if (l<duplicateVec.size()-1)
                    query.append(", ");
            }

            if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0))
                query.append(", ");

            int ct = 0;
            for (int tabl = 0; tabl < columns.length; tabl++) {
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    if (duplicateVec.contains(columns[tabl][tablCol]))
                        continue;
                    else {
                        ct++;
                        query.append(tables[tabl]);//..........
                        query.append(".");         //..........
                        query.append(columns[tabl][tablCol]);
                        if(ct < uniqueVec.size())
                            query.append(", ");
                    }
                }
            }

            // Second : Create FROM Clause
            query.append(" FROM ");
            for (int k=0; k<tables.length; k++){
                query.append(tables[k]);
                if (k<tables.length-1)
                    query.append(", ");
            }

            // Thrid : Create WHERE Clause
            if ((where != null) && (where.length() > 0)) {
                query.append(" WHERE ");
                query.append(where);
            }

            query.append(" ORDER BY ");
            for (int l=0; l<duplicateVec.size(); l++){
                // get the 2 tables that a duplicate column belongs to
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                // int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateVec.elementAt(l) ) {
                            // idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<DuplicateColumnName>"
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateVec.elementAt(l));
                if (l<duplicateVec.size()-1)
                    query.append(", ");
            }

            if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0))
                query.append(", ");

/*
            for (int m=0; m<uniqueVec.size(); m++){
                query.append(uniqueVec.elementAt(m));
                if (m<uniqueVec.size()-1)
                    query.append(", ");
            }
*/
            int ct2 = 0;
            for (int tabl = 0; tabl < columns.length; tabl++) {
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    if (duplicateVec.contains(columns[tabl][tablCol]))
                        continue;
                    else {
                        ct2++;
                        query.append(tables[tabl]);//..........
                        query.append(".");         //..........
                        query.append(columns[tabl][tablCol]);
                        if(ct2 < uniqueVec.size())
                            query.append(", ");
                    }
                }
            }

//            query.append(")");

            return query.toString();
        }//else

    }//getTableQuery()

}//mySQLConnection