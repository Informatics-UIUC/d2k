package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.modules.core.datatype.table.db.sql.*;

import ncsa.d2k.modules.core.io.sql.*;
import java.sql.*;
import java.util.*;

public class OracleDBConnection extends SQLDBConnection {

    public OracleDBConnection(String _url, String _driver, String _username, String _password) {
        super(_url, _driver, _username, _password);
    }
//////////////////////////////////////////////////////////////////////////////////////////

    public String     getAllTableNamesQuery() {
        String str = "SELECT DISTINCT table_name FROM user_tables ";
                     /*"WHERE owner NOT LIKE '%SYS%' " +
                       "AND owner NOT LIKE 'QS%' " +
                       "AND owner <> 'MEDEX' " +
                       "AND owner <> 'ODM' " +
                       "AND owner <> 'OUTLN' " +
                       "AND owner <> 'XDB' " +
                       "AND owner <> 'ODM_MTR' " +
                       "AND owner <> 'HR' " +
                       "AND owner <> 'OE' " +
                       "AND owner <> 'PM' " +
                       "AND owner <> 'SH' " +
                       "AND owner <> 'RMAN' "
        */
                       //;
        return str;
    }

    public String     getFirstRowQuery (String tableName) {
        String str = "SELECT * FROM " + tableName + " WHERE ROWNUM BETWEEN 1 AND 2";
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
            Statement stmt1 = con.createStatement();
            String query1 = this.createTableWithoutSeqQuery(tableName,
                                                            colNames,
                                                            colTypeNames,
                                                            colDisplaySizes);
            int createTableFlag = stmt1.executeUpdate(query1);
            stmt1.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createTable
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

            // 2. create a sequence to act as a Primary Key for the Prediction Table
            Statement stmt = con.createStatement();

            String sequenceQuery = this.createSequenceQuery(seqName);
            int querySequenceFlag = stmt.executeUpdate(sequenceQuery);
            stmt.close();

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

    private String createTableWithSeqQuery (String _tableName,
                                     String[] _colNames,
                                     String[] _colTypeNames,
                                     int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");
        str += "SEQ_NUM number primary key, ";

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
//System.out.println("createTableQuery with SEQ_NUM " + str);
        return str;
    }

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
//<<<<<<< OracleConnection.java
//System.out.println("createPredictionTableQuery " + str);
//=======
System.out.println("createTableQuery without SEQ_NUM" + str);
//>>>>>>> 1.10
        return str;
    }

//////////////////////////////////////////////////////////////////////////////////////////

    private String insertTableWithSeqQuery (String _tableName,
                                            String _seqName,
                                            String[] _colNames,
                                            String[] _colTypeNames,
                                            int[] _colDisplaySizes) {
        String str = "INSERT INTO ";
        str += _tableName;
        str += " (seq_num, ";

        for (int i=0; i<_colNames.length; i++) {
            str += _colNames[i];
            if (i<_colNames.length-1)
                str += ", ";
        }
        str += ") VALUES (";
        str += _seqName;
        str += ".NEXTVAL, ";

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
//////////////////////////////////////////////////////////////////////////////////////////

    private String createSequenceQuery (String seqName) {

        String str = new String("CREATE SEQUENCE " + seqName +
                                " START WITH 1 INCREMENT BY 1 NOCACHE NOMAXVALUE NOCYCLE");
        return str;
    }
//////////////////////////////////////////////////////////////////////////////////////////

    public String getTableQuery(String[] tables, String[][] columns, String where) {

        StringBuffer query = new StringBuffer();

        if (tables.length == 1) {                             //USER SELECTED ONLY 1 TABLE
            query.append("SELECT * FROM (SELECT ");
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

            query.append(")");

System.out.println(query.toString());
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

            //second: separate the columns into uniqueColumns and duplicateColumns
            int i = 0;
            Set uniques = new HashSet();
            Set dups = new HashSet();
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++)
                    if (!uniques.add(allCols[j]))
                        dups.add(allCols[j]);

            uniques.removeAll(dups);  // Destructive set-difference
            String uniqueColumns[] = (String[]) uniques.toArray( new String[ uniques.size() ] );
            String duplicateColumns[] = (String[]) dups.toArray( new String[ dups.size() ] );
             /**
              * BEGIN CREATING QUERY
              */

            // First : Create SELECT Clause
            query.append("SELECT * FROM (SELECT ");
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
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateColumns[l]);
                if (l<duplicateColumns.length-1)
                    query.append(", ");
            }

            if ((duplicateColumns.length > 0) && (uniqueColumns.length > 0))
                query.append(", ");

            for (int m=0; m<uniqueColumns.length; m++){
                query.append(uniqueColumns[m]);
                if (m<uniqueColumns.length-1)
                    query.append(", ");
            }
//System.out.println("str part1 : " + str);

            // Second : Create FROM Clause
            query.append(" FROM ");
            for (int k=0; k<tables.length; k++){
                query.append(tables[k]);
                if (k<tables.length-1)
                    query.append(", ");
            }
//System.out.println("str part2 : " + str);

            // Thrid : Create WHERE Clause

            if ((where != null) && (where.length() > 0)) {
                query.append(" WHERE ");
                query.append(where);
            }

            query.append(" ORDER BY ");
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
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateColumns[l]);
                if (l<duplicateColumns.length-1)
                    query.append(", ");
            }

            if ((duplicateColumns.length > 0) && (uniqueColumns.length > 0))
                query.append(", ");

            for (int m=0; m<uniqueColumns.length; m++){
                query.append(uniqueColumns[m]);
                if (m<uniqueColumns.length-1)
                    query.append(", ");
            }

            query.append(")");

    System.out.println("*****************QUERY: MULTIPLE TABLES *****************");
    System.out.println(query.toString());
            return query.toString();
        }//else

    }//getTableQuery()


}//OracleConnect
