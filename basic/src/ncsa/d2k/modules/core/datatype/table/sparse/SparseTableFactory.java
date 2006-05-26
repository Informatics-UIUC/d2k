package ncsa.d2k.modules.core.datatype.table.sparse;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;



/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */
public class SparseTableFactory
        implements TableFactory {

      static final long serialVersionUID = 1L;

    /**
     * put your documentation comment here
     */
    public SparseTableFactory () {
    }

    /**
     * put your documentation comment here
     * @return
     */
    public Table createTable () {
        //return  new SparseExampleTable();
        return new SparseMutableTable();
    }

    /**
     * put your documentation comment here
     * @param numColumns
     * @return
     */
    public Table createTable (int numColumns) {
        //return  new SparseExampleTable(numColumns);
        return new SparseMutableTable(0, numColumns);
    }

    /**
     * put your documentation comment here
     * @param table
     * @return
     */
    public ExampleTable createExampleTable (Table table) {
        return  table.toExampleTable();
    }

    /**
     * put your documentation comment here
     * @param et
     * @return
     */
    public PredictionTable createPredictionTable (ExampleTable et) {
        return  et.toPredictionTable();
    }

    /**
     * put your documentation comment here
     * @param et
     * @return
     */
    public TestTable createTestTable (ExampleTable et) {
        return  (TestTable)et.getTestTable();
    }

    /**
     * put your documentation comment here
     * @param et
     * @return
     */
    public TrainTable createTrainTable (ExampleTable et) {
        return  (TrainTable)et.getTrainTable();
    }

    /**
     * Given an int value represneting a type of column (see ColumnTypes)
     * return a column of type consistent with this factory implementation.
     * @param col_type int
     * @return Column
     */
    public Column createColumn(int col_type) {
      switch (col_type){
        case ColumnTypes.BOOLEAN:
          return new SparseBooleanColumn();
        case ColumnTypes.BYTE:
          return new SparseByteColumn();
        case ColumnTypes.BYTE_ARRAY:
          return new SparseByteArrayColumn();
        case ColumnTypes.CHAR:
          return new SparseCharColumn();
        case ColumnTypes.CHAR_ARRAY:
          return new SparseCharArrayColumn();
        case ColumnTypes.DOUBLE:
          return new SparseDoubleColumn();
        case ColumnTypes.FLOAT:
          return new SparseFloatColumn();
        case ColumnTypes.INTEGER:
          return new SparseIntColumn();
        case ColumnTypes.LONG:
          return new SparseLongColumn();
        case ColumnTypes.OBJECT:
          return new SparseObjectColumn();
        case ColumnTypes.SHORT:
          return new SparseShortColumn();
        case ColumnTypes.STRING:
          return new SparseStringColumn();
        case ColumnTypes.UNSPECIFIED:
          return new SparseObjectColumn();
        default: //not sure if this makes sense.
          return new SparseObjectColumn();
      }
    }

}



