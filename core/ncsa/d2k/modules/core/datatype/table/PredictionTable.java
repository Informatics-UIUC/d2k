//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

/**
 A Table that should be used by a ModelModule to make predictions on a dataset.
 The prediction set designates the indices of the Columns that are filled with
 predictions.
 */
public interface PredictionTable extends ExampleTable {

	static final long serialVersionUID = 3469575455540496287L;

    /**
     Set the prediction set
	 @return the prediciton set
     */
    public int[] getPredictionSet ();

    /**
		Set the prediction set
		@param p the new prediciton set
     */
    public void setPredictionSet (int[] p);

    /**
     Add a Column to the prediction set.  This
     will append the Column to the end of the table and
     set the prediction set accordingly.  The column
     index of the new Column is returned.
     @param c the Column to add
	 @return the index of the new prediction column
     */
    public int addPredictionColumn (Column c);
}



