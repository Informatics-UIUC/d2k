package ncsa.d2k.modules.core.io.sql;

import java.sql.*;
import java.util.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class PostgresDBConnection
    extends SQLDBConnection {
  public PostgresDBConnection(String _url, String _driver, String _username,
                              String _password) {
    super(_url, _driver, _username, _password);
  }

  public void createTable(String tableName,
                          String seqName,
                          String[] colNames,
                          String[] colTypeNames,
                          int[] colDisplaySizes,
                          int numRows) {
    throw new RuntimeException("PostgresDBConnection : createTable not implemented.");
  }

  public void createTable(String tableName,
                          String[] colNames,
                          String[] colTypeNames,
                          int[] colDisplaySizes) {
    throw new RuntimeException("PostgresDBConnection : createTable not implemented.");
  }

  protected String getAllTableNamesQuery() {
    String str =
        "select tablename from pg_tables where tablename not like 'pg_%';";
    return str;
  }

  protected String getTableQuery(String[] tables, String[][] columns,
                                 String where) {
    StringBuffer query = new StringBuffer();
    if (tables.length == 1) { //USER SELECTED ONLY 1 TABLE
      query.append("SELECT ");
      for (int tabl = 0; tabl < columns.length; tabl++) {
        for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
          query.append(columns[tabl][tablCol]);
          if (tablCol < columns[tabl].length - 1) {
            query.append(", ");
          }
        }
      }
      query.append(" FROM ");
      query.append(tables[0]);
      if ( (where != null) && (where.length() > 0)) {
        query.append(" WHERE ");
        query.append(where);
      }
      query.append(" ORDER BY ");
      for (int tabl = 0; tabl < columns.length; tabl++) {
        for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
          query.append(columns[tabl][tablCol]);
          if (tablCol < columns[tabl].length - 1) {
            query.append(", ");
          }
        }
      }
//query.append(")");
      return query.toString();
    }
    else { //USER SELECTED >1 TABLES
// separate the columns into uniqueColumns and duplicateColumns
      int i = 0;
      Set uniques = new HashSet();
      Set dups = new HashSet();
      for (int tabl = 0; tabl < columns.length; tabl++) {
        for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
          if (!uniques.add(columns[tabl][tablCol])) {
            dups.add(columns[tabl][tablCol]);
          }
        }
      }
      uniques.removeAll(dups); // Destructive set-difference
      Vector uniqueVec = new Vector(uniques);
      Vector duplicateVec = new Vector(dups);
// First : Create SELECT Clause
      query.append("SELECT ");
      for (int l = 0; l < duplicateVec.size(); l++) {
// get the 2 tables that a duplicate column belongs to
//first find the two [x] indices of columns[x][y] where the column is present
//next, use these indices on the tables[] array to retrieve the tables
        int idx1 = 0;
        int idx2 = 0;
        int table1, table2;
        for (table1 = 0; table1 < columns.length; table1++) {
          for (int tablCol = 0; tablCol < columns[table1].length; tablCol++) {
            if (columns[table1][tablCol] == duplicateVec.elementAt(l)) {
              idx1 = table1;
              break;
            }
          }
        }
        for (table2 = table1; table2 < columns.length; table2++) {
          for (int tablCol = 0; tablCol < columns[table2].length; tablCol++) {
            if (columns[table2][tablCol] == duplicateVec.elementAt(l)) {
              idx2 = table2;
              break;
            }
          }
        }
// now append "<TableName1>.<DuplicateColumnName>"
        query.append(tables[idx1]);
        query.append(".");
        query.append(duplicateVec.elementAt(l));
        if (l < duplicateVec.size() - 1) {
          query.append(", ");
        }
      }
      if ( (duplicateVec.size() > 0) && (uniqueVec.size() > 0)) {
        query.append(", ");
      }
      int ct = 0;
      for (int tabl = 0; tabl < columns.length; tabl++) {
        for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
          if (duplicateVec.contains(columns[tabl][tablCol])) {
            continue;
          }
          else {
            ct++;
            query.append(tables[tabl]); //..........
            query.append("."); //..........
            query.append(columns[tabl][tablCol]);
            if (ct < uniqueVec.size()) {
              query.append(", ");
            }
          }
        }
      }
// Second : Create FROM Clause
      query.append(" FROM ");
      for (int k = 0; k < tables.length; k++) {
        query.append(tables[k]);
        if (k < tables.length - 1) {
          query.append(", ");
        }
      }
// Thrid : Create WHERE Clause
      if ( (where != null) && (where.length() > 0)) {
        query.append(" WHERE ");
        query.append(where);
      }
      query.append(" ORDER BY ");
      for (int l = 0; l < duplicateVec.size(); l++) {
// get the 2 tables that a duplicate column belongs to
//first find the two [x] indices of columns[x][y] where the column is present
//next, use these indices on the tables[] array to retrieve the tables
        int idx1 = 0;
        int idx2 = 0;
        int table1, table2;
        for (table1 = 0; table1 < columns.length; table1++) {
          for (int tablCol = 0; tablCol < columns[table1].length; tablCol++) {
            if (columns[table1][tablCol] == duplicateVec.elementAt(l)) {
              idx1 = table1;
              break;
            }
          }
        }
        for (table2 = table1; table2 < columns.length; table2++) {
          for (int tablCol = 0; tablCol < columns[table2].length; tablCol++) {
            if (columns[table2][tablCol] == duplicateVec.elementAt(l)) {
              idx2 = table2;
              break;
            }
          }
        }
// now append "<TableName1>.<DuplicateColumnName>"
        query.append(tables[idx1]);
        query.append(".");
        query.append(duplicateVec.elementAt(l));
        if (l < duplicateVec.size() - 1) {
          query.append(", ");
        }
      }
      if ( (duplicateVec.size() > 0) && (uniqueVec.size() > 0)) {
        query.append(", ");
      }
      int ct2 = 0;
      for (int tabl = 0; tabl < columns.length; tabl++) {
        for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
          if (duplicateVec.contains(columns[tabl][tablCol])) {
            continue;
          }
          else {
            ct2++;
            query.append(tables[tabl]); //..........
            query.append("."); //..........
            query.append(columns[tabl][tablCol]);
            if (ct2 < uniqueVec.size()) {
              query.append(", ");
            }
          }
        }
      }
      return query.toString();
    } //else
  } //getTableQuery()

  protected String getFirstRowQuery(String tableName) {
    String str = "SELECT * FROM " + tableName + " LIMIT 1";
    return str;
  }
} //DBConnection