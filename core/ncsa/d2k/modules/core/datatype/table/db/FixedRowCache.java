package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.sql.*;

public class FixedRowCache extends Cache {

    /**
     * Create a new FixedRowCache given a ResultSet.  The ResultSet is needed
     * to get the datatypes and number of columns.  The cache is populated
     * with the first N rows.
     */
//    public FixedRowCache(ResultSet rs) throws SQLException {
    public FixedRowCache(DBDataSource dbds) throws SQLException {
        maxRowsInCache = 1000;
//        initialize(rs);
        initialize(dbds);
    }

//    private void initialize(ResultSet rs) throws SQLException {
//        ResultSetMetaData rsmd = rs.getMetaData();

//        table = new TableImpl(rsmd.getColumnCount());   //Initialize TableImpl : Cache
//        for (int col = 1; col <= rsmd.getColumnCount(); col++) { //Initialize  Columns of the TableImpl
//            if (rsmd.getColumnType(col) == 2)
//                table.setColumn(new DoubleColumn(getMaxCacheRows()), col-1);
//            else // //(rsmd.getColumnType(col) == 12)
//                table.setColumn(new StringColumn(getMaxCacheRows()), col-1);
//         }

         // set Labels, isNominal, isScaler in the TableImpl
//         for (int col = 1; col <= rsmd.getColumnCount(); col++) {
//            table.setColumnLabel(rsmd.getColumnLabel(col), col-1);
            //cache.table.setColumnIsNominal(false, col-1);
            //cache.table.setColumnIsScalar(false, col-1);
//         }
//         update(0, rs);
//    }

    private void initialize (DBDataSource dbds) throws SQLException {
    System.out.println("DISTINCT: "+dbds.getNumDistinctColumns());
        table = new TableImpl(dbds.getNumDistinctColumns());
        System.out.println("NC: "+table.getNumColumns());
        for (int col = 0; col < dbds.getNumDistinctColumns(); col++) { //Initialize  Columns of the TableImpl
        System.out.println("DBDD: "+dbds.getColumnLabel(col));
            //table.setColumnLabel(dbds.getColumnLabel(col), col);
            switch (dbds.getColumnType(col)) {
                case ColumnTypes.INTEGER:
                case ColumnTypes.DOUBLE:
                case ColumnTypes.FLOAT:
                    table.setColumn(new DoubleColumn(getMaxCacheRows()), col);
                    break;
                case ColumnTypes.STRING:
                case ColumnTypes.CHAR:
                case ColumnTypes.OBJECT:
                    table.setColumn(new StringColumn(getMaxCacheRows()), col);
                    break;
                default:
                    table.setColumn(new StringColumn(getMaxCacheRows()), col);
            } //switch
        }//for
        update(0, dbds);

    }//initialize

} //FixedRowCache