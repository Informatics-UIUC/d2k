package ncsa.d2k.modules.core.datatype.table.db.sql;

import java.sql.*;
import java.io.*;

import ncsa.d2k.modules.core.io.sql.*;

public class MutableResultSetDataSource extends ResultSetDataSource {

    private String tableName;
    private String[] columnNames;

    public MutableResultSetDataSource(DBConnection connection, String tableName,
                               String seqColName, String[] columns,
                               String[] typeNames, int[] displaySizes,
                               int numRows) {
        super();
        dbConnection = connection;

        tableName = tableName;
        columnNames = columns;

        String[] tables = {tableName};
        String[][] cols = new String[1][];
        cols[0] = columns;

        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(cols);
        this.setUserSelectedWhere("");
        //userQuery = dbConnection.getTableQuery(tables, cols, "");
        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        for(int i = 0; i < columns.length; i++) {
            query.append(columns[i]);
            if(i < columns.length-1)
                query.append(", ");
            else
                query.append(" ");
        }
        query.append("FROM ");
        query.append(tableName);
        //userQuery = query.toString();

        // Create a Prediction Table in the Database
        dbConnection.createTable(tableName, seqColName, columns,
                                 typeNames, displaySizes, numRows );
        rs = dbConnection.getUpdatableResultSet(tableName, columns, "");

        initialize();
    }

    protected void finalize() throws Throwable {
        //dbConnection.dropTable(userTables[0]);
        super.finalize();
    }

    /**
     * Re-initialize when we are de-serialized.
     */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      in.defaultReadObject();
      rs = dbConnection.getUpdatableResultSet(tableName, columnNames, "");
      initialize();
    }

    public void setString(String val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateString(c+1, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setFloat(float val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateFloat(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDouble(double val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateDouble(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInt(int val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateInt(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setShort(short val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateShort(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLong(long val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateLong(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setObject(Object val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateObject(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBoolean(boolean val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateBoolean(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addColumn() {}

}
