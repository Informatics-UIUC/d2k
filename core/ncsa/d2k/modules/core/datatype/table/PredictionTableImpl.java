package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;

/**
 A Table that should be used by a ModelModule to make predictions on a dataset.
 This table creates a new, empty Column for each output in the original
 ExampleTable.  The datatype of each new Column is the same as its associated
 output.  So, for example, if the first output Column is a StringColumn, the
 first prediction Column will be a StringColumn.  The indices of the new columns
 are accessible via getPredictionSet() and setPredictionSet().  If the ExampleTable
 did not have any outputs, it is up to the ModelModule to add prediction Columns.
 */
public class PredictionTableImpl extends ExampleTableImpl implements PredictionTable {
    protected int[] predictions;
    private static final String PRED = " Predictions";

	static final long serialVersionUID = -5410732702360322327L;

	protected PredictionTableImpl(int i) {
		super(i);
	}

    /**
     Given an example table, copy its input columns, and create new
     columns to hold the predicted values.
	 @param ttt the ExampleTable that contains the inital values
     */
    public PredictionTableImpl (ExampleTableImpl ttt) {
        super(ttt);
        if (outputColumns == null) {
            predictions = new int[0];
            outputColumns = new int[0];
        }
        else
            predictions = new int[outputColumns.length];
        Column[] newColumns = new Column[columns.length + outputColumns.length];
        int i = 0;
        // Copy references to the original columns
        for (; i < columns.length; i++)
            newColumns[i] = columns[i];
        // Create new columns which will contain the predicted values.
        for (int i2 = 0; i2 < outputColumns.length; i++, i2++) {
            Column col = ttt.getColumn(outputColumns[i2]);
            if (col instanceof DoubleColumn)
                col = new DoubleColumn(col.getNumRows());
            else if (col instanceof FloatColumn)
                col = new FloatColumn(col.getNumRows());
            else if (col instanceof StringColumn)
                col = new StringColumn(col.getNumRows(), true);
            //else if (col instanceof ByteArrayColumn)
            //    col = new ByteArrayColumn(col.getNumRows());
			else if (col instanceof ContinuousByteArrayColumn)
				col = new ContinuousByteArrayColumn(col.getNumRows(), true);
            else if (col instanceof BooleanColumn)
                col = new BooleanColumn(col.getNumRows());
            //else if (col instanceof CharArrayColumn)
            //   col = new CharArrayColumn(col.getNumRows());
			else if (col instanceof ContinuousCharArrayColumn)
				col = new ContinuousCharArrayColumn(col.getNumRows(), true);
            else if (col instanceof LongColumn)
                col = new LongColumn(col.getNumRows());
            else if (col instanceof IntColumn)
                col = new IntColumn(col.getNumRows());
            else if (col instanceof ShortColumn)
                col = new ShortColumn(col.getNumRows());
            else if (col instanceof ObjectColumn)
                col = new ObjectColumn(col.getNumRows());
            StringBuffer newLabel = new StringBuffer(ttt.getColumnLabel(outputColumns[i2]));
            newLabel.append(PRED);
            col.setLabel(newLabel.toString());
            newColumns[i] = col;
            predictions[i2] = i;
        }
        columns = newColumns;
    }

    /**
     Given a prediction table, copy its input columns, and create new
     columns to hold the predicted values.
	 @param ttt the prediction table to start with
     */
    public PredictionTableImpl (PredictionTableImpl ttt) {
        super(ttt);
        predictions = ttt.getPredictionSet();
    }

    /**
     Copy method. Return an exact copy of this column.  A deep copy
     is attempted, but if it fails a new column will be created,
     initialized with the same data as this column.
     @return A new Column with a copy of the contents of this column.
     */
    public Table copy () {
       	PredictionTableImpl vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (PredictionTableImpl)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new PredictionTableImpl(getNumColumns());
            vt.setKeyColumn(getKeyColumn());
            for (int i = 0; i < getNumColumns(); i++)
                vt.setColumn(getColumn(i).copy(), i);
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            //vt.setType(getType());
			vt.setInputFeatures(getInputFeatures());
			vt.setOutputFeatures(getOutputFeatures());
			vt.setPredictionSet(getPredictionSet());
			vt.transformations = (ArrayList)transformations.clone();
			vt.setTestingSet(getTestingSet());
			vt.setTrainingSet(getTrainingSet());
            return  vt;
        }
    }

    /**
     Set the prediction set
	 @return the prediciton set
     */
    public int[] getPredictionSet () {
        return  predictions;
    }

    /**
		Set the prediction set
		@param p the new prediciton set
     */
    public void setPredictionSet (int[] p) {
        predictions = p;
    }

    /**
     Add a Column to the prediction set.  This
     will append the Column to the end of the table and
     set the prediction set accordingly.  The column
     index of the new Column is returned.
     @param c the Column to add
	 @return the index of the new prediction column
     */
    public int addPredictionColumn (Column c) {
        Column[] newCol = new Column[columns.length + 1];
        System.arraycopy(columns, 0, newCol, 0, columns.length);
        newCol[newCol.length - 1] = c;
        columns = newCol;
        // now set the classification set
        int[] cf = new int[predictions.length + 1];
        for (int i = 0; i < predictions.length; i++)
            cf[i] = predictions[i];
        cf[cf.length - 1] = columns.length - 1;
        setPredictionSet(cf);
        return  columns.length - 1;
    }

    /**
     This class provides transparent access to the test data only. The testSets
     field of the TrainTest table is used to reference only the test data, yet
     the getter methods look exactly the same as they do for any other vertical table.
	 @return a table to provide access to the test data
     */
    public TestTable getTestTable () {
        if (testSet == null)
            return  null;
        return  new TestTableImpl(this);
    }
}
