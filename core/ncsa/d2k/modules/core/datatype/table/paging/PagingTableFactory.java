package ncsa.d2k.modules.core.datatype.table.paging;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Public class or package protected??
 */
public class PagingTableFactory implements TableFactory {

    /**
     * Create a new, empty Table.
         * @return a new, empty Table
     */
        public Table createTable() {

            // return an empty PagingTable here
            return null;
        }

    /**
     * Create a Table with the specified number of columns.
     * @param numColumns the number of columns
         * @return a new, empty Table with the specified number of columns
     */
    public Table createTable(int numColumns) {
        return null;

    }

    /**
         * Create an ExampleTable from a Table.
     * @param table the table to replicate.
         * @return an ExampleTable with the same data as table
     */
    public ExampleTable createExampleTable(Table table) {
        return table.toExampleTable();
    }

    /**
         * Given an ExampleTable, create a new PredictionTable.  The PredictionTable
         * will have an extra column for each of the outputs in et.
         * @param et the ExampleTable that contains the inital values
         * @return a PredictionTable initialized with the data from et
     */
    public PredictionTable createPredictionTable(ExampleTable et) {
        return et.toPredictionTable();
    }

    /**
         * Given an ExampleTable, create a new TestTable.  The TestTable will have
         * an extra column for each of the outputs in et.  The rows of the TestTable
         * will be the indices of the test set in et.
         * @param et the ExampleTable that this TestTable is derived from
         * @return a TestTable initialized with the data from et
     */
    public TestTable createTestTable(ExampleTable et) {
        return et.getTestTable();
    }

        /**
         * Given an ExampleTable, create a new TrainTable.  The rows of the TrainTable
         * will be the indicies of the train set in et.
         * @param et the ExampleTable that the TrainTable is derived from
         * @return a TrainTable initialized with the data from et
         */
	public TrainTable createTrainTable(ExampleTable et) {
            return et.getTrainTable();
	}
}
