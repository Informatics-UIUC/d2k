package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.db.sql.*;

import java.sql.*;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import javax.swing.*;
import java.util.*;

public class SQLServerDBConnection extends SQLDBConnection {

    public SQLServerDBConnection (String _url, String _driver, String _username, String _password) {
        super(_url, _driver, _username, _password);
    }
//////////////////////////////////////////////////////////////////////////////////////////

    public String getAllTableNamesQuery() {

        String str = "select TABLE_NAME = convert(sysname,o.name) from master..sysobjects o " +
                     "where user_name(o.uid) = 'dbo' and o.type in ('U') ";
        return str;
    }

    public String     getFirstRowQuery (String tableName) {
        String str = "SELECT TOP 1 * FROM " + tableName ;
        return str;
    }
//////////////////////////////////////////////////////////////////////////////////////////

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

            int createTableFlag = stmt1.executeUpdate(query1);
System.out.println("SQLServer: createTableFlag = " + createTableFlag);
            stmt1.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createPredictionTable
//////////////////////////////////////////////////////////////////////////////////////////

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
            int createTableFlag = stmt1.executeUpdate(query1);
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
            int insertTableFlag;

            for (int i=0; i<numRows; i++) {
                stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                            ResultSet.CONCUR_UPDATABLE);
                insertTableFlag = stmt2.executeUpdate(query2);
                stmt2.close();
            }
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createTable
//////////////////////////////////////////////////////////////////////////////////////////
/*        String createSQLServer1 =  "CREATE TABLE IRIS2DC ("
                                + " SEQ_NUM int IDENTITY (1, 1) NOT NULL ,"
	                        + " SEPAL_LENGTH varchar(6) ,"
	                        + " SEPAL_WIDTH varchar(6) ,"
	                        + " PETAL_LENGTH varchar(6) )";*/

    private String createTableWithSeqQuery (String _tableName,
                                            String[] _colNames,
                                            String[] _colTypeNames,
                                            int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");
        str += "SEQ_NUM int IDENTITY (1, 1) NOT NULL , ";

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
System.out.println("SQLServerConnection: createTableQuery with Sequence :" + str);
        return str;
    }

    private String createTableWithoutSeqQuery (String _tableName,
                                               String[] _colNames,
                                               String[] _colTypeNames,
                                               int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");
//        str += "SEQ_NUM int primary key, ";

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
System.out.println("SQLServerConnection: createTableQuery without Sequence: " + str);
        return str;
    }
//////////////////////////////////////////////////////////////////////////////////////////
   /*insertString = "insert into IRIS2DC (SEPAL_LENGTH, SEPAL_WIDTH, PETAL_LENGTH) " +
   "values('5',   '3.4',	'1.5')";*/
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
//        str += _seqName;
//        str += ".NEXTVAL, ";

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

//System.out.println("insertPredictionTableQuery " + str);
        return str;
    }


////////////////////////////////////////////////////////////////////////////////////////

    public String getTableQuery(String[] tables, String[][] columns, String where) {

        StringBuffer query = new StringBuffer();

        if (tables.length == 1) {                             //USER SELECTED ONLY 1 TABLE
            query.append("SELECT ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }
            query.append(" FROM ");
            query.append(tables[0]);

            query.append(" ORDER BY ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }

            /*if ((where != null) && (where != "")) {
                str += " WHERE ";
                str += where;
            }*/

    //System.out.println("*****************QUERY: ONE TABLE *****************");
    //System.out.println(str);
    //System.out.println(query.toString());
    //System.out.println("*****************QUERY: ONE TABLE *****************");
            //return str;
            return query.toString();
        }
        else {                                                   //USER SELECTED >1 TABLES
            /**
             * BEGIN PROCESSING columns[][]..
             */
            //first: count the # columns in the 2D-array columns (including duplicates)
            int allColsCount = 0;
            for (int tabl = 0; tabl < columns.length; tabl++)
                allColsCount += columns[tabl].length;

            //second: initialize space for a 1-D string array of all columns and copy into it
            String[] allCols = new String[allColsCount];
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
    //System.out.println("Unique columns:    " + uniques);
    //System.out.println("Duplicate columns: " + dups);

            String uniqueColumns[] = (String[]) uniques.toArray( new String[ uniques.size() ] );
            String duplicateColumns[] = (String[]) dups.toArray( new String[ dups.size() ] );
            /**
             * END PROCESSING columns[][]..
             */

             /**
              * BEGIN CREATING QUERY
              */

            // First : Create SELECT Clause
            String str = "SELECT ";
            for (int l=0; l<duplicateColumns.length; l++){
                // get the 2 tables that a duplicate column belongs to
                // how?
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateColumns[l] ) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateColumns[l] ) {
                            idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<DuplicateColumnName>"
                str += tables[idx1];
                str += ".";
                str += duplicateColumns[l];
                if (l<duplicateColumns.length-1)
                    str += ", ";
            }

            if (uniqueColumns.length > 0)
                str += ", ";

            for (int m=0; m<uniqueColumns.length; m++){
                str += uniqueColumns[m];
                if (m<uniqueColumns.length-1)
                    str += ", ";
            }
//System.out.println("str part1 : " + str);

            // Second : Create FROM Clause
            str += " FROM ";
            for (int k=0; k<tables.length; k++){
                str += tables[k];
                if (k<tables.length-1)
                    str += ", ";
            }
//System.out.println("str part2 : " + str);

            // Thrid : Create WHERE Clause
            str += " WHERE ";

            for (int m=0; m<duplicateColumns.length; m++){
                // get the 2 tables that a duplicate column belongs to
                // how?
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateColumns[m] ) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateColumns[m] ) {
                            idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<ColumnName> = <TableName2>.<ColumnName>"
                str += tables[idx1];
                str += ".";
                str += duplicateColumns[m];
                str += " = ";
                str += tables[idx2];
                str += ".";
                str += duplicateColumns[m];

                // if (duplicateColumns.length-1) > m > 0, append  " AND "
                if ( (m>0) && (m<duplicateColumns.length-1) )
                    str += " AND ";
            }

            if ((where != null) && (where != "")) {
                str += " AND ";
                str += where;
            }

    System.out.println("*****************QUERY: MULTIPLE TABLES *****************");
    System.out.println(str);
    System.out.println("*****************QUERY: MULTIPLE TABLES *****************");
            return str;

        }//else

    }//getTableQuery()



}//SQLServerConnection







/////////////////////////////////////////////////////////////////////////////////////////
/////////OLD CODE BACKUP
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Functions related to the Columns of Tables in a Database
     */
/*
    public String QueryColumnNames (String TableName) {
        String str = "select c.name " +
                     "from master..sysobjects o, master..syscolumns c " +
                     "where o.id = object_id('" +
                     TableName +
                     "') and c.id = o.id " +
                     "order by colid";
        return str;
    }

    public String QueryAllColumnTypes (String TableName) {
        String str = "select t.name " +
                     "from master..sysobjects o, master..syscolumns c, master..systypes t " +
                     "where o.id = object_id('" + TableName + "') " +
                     "and c.id = o.id " +
                     "and c.usertype = t.usertype " +
                     "order by colid";
        return str;
    }

    public String QueryColumnType (String TableName, String ColumnName) {
        String str = "select t.name " +
                     "from master..sysobjects o, master..syscolumns c, master..systypes t " +
                     "where o.id = object_id('" + TableName + "') " +
                     "and c.id = o.id " +
                     "and c.name = '" + ColumnName + "' " +
                     "and c.usertype = t.usertype " +
                     "order by colid";
        return str;
    }


    public String QueryAllColumnLengths (String TableName) {
        String str = "select c.length " +
                     "from master..sysobjects o, master..syscolumns c " +
                     "where o.id = object_id('" + TableName + "') " +
                     "and c.id = o.id " +
                     "order by colid";
        return str;
    }

    public String QueryColumnLength (String TableName, String ColumnName) {
        String str = "select c.length " +
                     "from master..sysobjects o, master..syscolumns c " +
                     "where o.id = object_id('" + TableName + "') " +
                     "and c.id = o.id " +
                     "and c.name = '" + ColumnName + "' " +
                     "order by colid";
        return str;
    }
*/
/////////////////////////////////////////////////////////////////////////////////////////