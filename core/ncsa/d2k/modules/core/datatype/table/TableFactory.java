package ncsa.d2k.modules.core.datatype.table;


public final class TableFactory {

	private TableFactory() {}

    /**
    	Create a new, empty Table.
		@return a new, empty Table
     */
	public static Table createTable() {
		return new TableImpl();
	}

    /**
     Create a Table with the specified number of columns.
     @param numColumns the number of columns
	 @return a new, emtpy Table with the specified number of columns
     */
    public static Table createTable(int numColumns) {
		return new TableImpl(numColumns);
	}

    /**
     Create a Table with the specified columns
     @param c the columns that make up the table
	 @return a new Table with the specified columns
     */
	 public static Table createTable(Column [] c) {
	 	return new TableImpl(c);
	 }

    /**
     Create a Table from a StaticDocument
     @param sd the StaticDocument
	 @return a Table initialized with the data from a StaticDocument
     */
    /*public static Table createTable(StaticDocument sd) {
		return new TableImpl(sd);
	}*/

    /**
	 * Create a new ExampleTalbe given the number of columns
	 * @param numColumns the number of columns
	 * @return an empty ExampleTable initialized with the specified number of columns
	 */
    public static ExampleTable createExampleTable(int numColumns) {
		return new ExampleTableImpl(numColumns);
	}

    /**
	 * Create a new ExampleTable given a static doc representation of the data.
	 * @param sd the StaticDocument to fill the table with
	 * @return an ExampleTable initialized with the data from a StaticDocument
	 */
    /*public static ExampleTable createExampleTable(StaticDocument sd) {
		return new ExampleTableImpl(sd);
	}*/

    /**
     Given a Table to represent, replicate its
     contents in an ExampleTable table.
     @param table the table to replicate.
	 @return an ExampleTable with the same data as table
     */
    public static ExampleTable createExampleTable(Table table) {
		if(table instanceof TableImpl)
			return new ExampleTableImpl((TableImpl)table);
		else
			return null;
	}

    /**
     Given an example table, copy its input columns, and create new
     columns to hold the predicted values.
	 @param ttt the ExampleTable that contains the inital values
	 @return a PredictionTable initialized with the data from ttt
     */
    public static PredictionTable createPredictionTable(ExampleTable ttt) {
		if(ttt instanceof ExampleTableImpl)
			return new PredictionTableImpl((ExampleTableImpl)ttt);
		else
			return null;
	}

    /**
     Given a prediction table, copy its input columns, and create new
     columns to hold the predicted values.
	 @param ttt the prediction table to start with
	 @return a PredictionTable initialized with the data from ttt
     */
    public PredictionTable createPredictionTable(PredictionTableImpl ttt) {
		return new PredictionTableImpl(ttt);
	}

    /**
     Given a prediction table, copy it's input columns, and create new
     columns to hold the predicted values.
	 @param ttt the prediction table that this test table is derived from
	 @return a TestTable initialized with the data from ttt
     */
    public TestTable createTestTable(PredictionTableImpl ttt) {
		return new TestTableImpl(ttt);
	}
}
