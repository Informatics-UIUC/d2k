package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

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
	static final long serialVersionUID = -7087521328197567748L;

    protected int[] predictionSet;

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
            predictionSet = new int[0];
            outputColumns = new int[0];
        }
        else
            predictionSet = new int[outputColumns.length];
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
                col = new StringColumn(col.getNumRows());
			else if(col instanceof StringObjectColumn)
				col = new StringObjectColumn(col.getNumRows());
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
            newLabel.append(PREDICTION_COLUMN_APPEND_TEXT);
            col.setLabel(newLabel.toString());
            newColumns[i] = col;
            predictionSet[i2] = i;
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
        predictionSet = ttt.getPredictionSet();
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
			vt.setInputFeatures(getInputFeatures());
			vt.setOutputFeatures(getOutputFeatures());
			vt.setPredictionSet(getPredictionSet());
			vt.transformations = (ArrayList)transformations.clone();
			vt.setTestingSet(getTestingSet());
			vt.setTrainingSet(getTrainingSet());
            return  vt;
        }
    }

    public Table getSubset(int pos, int len) {
      Table t = super.getSubset(pos, len);
      ExampleTable et  = t.toExampleTable();

      int[] newin = new int[inputColumns.length];
      System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
      int[] newout = new int[outputColumns.length];
      System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

      int[] newpred = new int[predictionSet.length];
      System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);

      // now figure out the test and train sets
      int[] traincpy = new int[trainSet.length];
      System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
      int[] testcpy = new int[testSet.length];
      System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

      int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
      int[] newtest = subsetTrainOrTest(testcpy, pos, len);

      PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
      pt.setPredictionSet(newpred);
      pt.columns = ((ExampleTableImpl)et).columns;
      pt.setInputFeatures(newin);
      pt.setOutputFeatures(newout);
      pt.setTrainingSet(newtrain);
      pt.setTestingSet(newtest);
      return pt;
    }

    public Table getSubset(int[] rows) {
      Table t = super.getSubset(rows);
      ExampleTable et = t.toExampleTable();

      int[] newin = new int[inputColumns.length];
      System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
      int[] newout = new int[outputColumns.length];
      System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
      int[] newpred = new int[predictionSet.length];
      System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);

      // now figure out the test and train sets
      int[] traincpy = new int[trainSet.length];
      System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
      int[] testcpy = new int[testSet.length];
      System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

      int[] newtrain = subsetTrainOrTest(traincpy, rows);
      int[] newtest = subsetTrainOrTest(testcpy, rows);

      //return et.getTrainTable();
      //PredictionTable pt = et.toPredictionTable();
      PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
      pt.setPredictionSet(newpred);
      pt.columns = ((ExampleTableImpl)et).columns;
      pt.setInputFeatures(newin);
      pt.setOutputFeatures(newout);
      pt.setTrainingSet(newtrain);
      pt.setTestingSet(newtest);
      return pt;
    }

    public Table getSubsetByReference(int pos, int len) {
      Table t = super.getSubsetByReference(pos, len);
      ExampleTable et  = t.toExampleTable();

      int[] newin = new int[inputColumns.length];
      System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
      int[] newout = new int[outputColumns.length];
      System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
      int[] newpred = new int[predictionSet.length];
      System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);


      // now figure out the test and train sets
      int[] traincpy = new int[trainSet.length];
      System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
      int[] testcpy = new int[testSet.length];
      System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

      int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
      int[] newtest = subsetTrainOrTest(testcpy, pos, len);


      //return et.getTrainTable();
      //PredictionTable pt = et.toPredictionTable();
      PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
      pt.setPredictionSet(newpred);
      pt.columns = ((ExampleTableImpl)et).columns;
      pt.setInputFeatures(newin);
      pt.setOutputFeatures(newout);
      pt.setTrainingSet(newtrain);
      pt.setTestingSet(newtest);
      return pt;
    }

    public Table getSubsetByReference(int[] rows) {
      Table t = super.getSubsetByReference(rows);
      ExampleTable et = t.toExampleTable();

      int[] newin = new int[inputColumns.length];
      System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
      int[] newout = new int[outputColumns.length];
      System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);
      int[] newpred = new int[predictionSet.length];
      System.arraycopy(predictionSet, 0, newpred, 0, predictionSet.length);


      // now figure out the test and train sets
      int[] traincpy = new int[trainSet.length];
      System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
      int[] testcpy = new int[testSet.length];
      System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

      int[] newtrain = subsetTrainOrTest(traincpy, rows);
      int[] newtest = subsetTrainOrTest(testcpy, rows);

      //return et.getTrainTable();
      //PredictionTable pt = et.toPredictionTable();
      PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
      pt.setPredictionSet(newpred);
      pt.columns = ((ExampleTableImpl)et).columns;
      pt.setInputFeatures(newin);
      pt.setOutputFeatures(newout);
      pt.setTrainingSet(newtrain);
      pt.setTestingSet(newtest);
      return pt;
    }


    /**
     Set the prediction set
	 @return the prediciton set
     */
    public int[] getPredictionSet () {
        return  predictionSet;
    }

    /**
		Set the prediction set
		@param p the new prediciton set
     */
    public void setPredictionSet (int[] p) {
        predictionSet = p;
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
        int[] cf = new int[predictionSet.length + 1];
        for (int i = 0; i < predictionSet.length; i++)
            cf[i] = predictionSet[i];
        cf[cf.length - 1] = columns.length - 1;
        setPredictionSet(cf);
        return  columns.length - 1;
        //return  predictionSet.length - 1;
    }

    /**
     This class provides transparent access to the test data only. The testSets
     field of the TrainTest table is used to reference only the test data, yet
     the getter methods look exactly the same as they do for any other vertical table.
	 @return a table to provide access to the test data
     */
    /*public TestTable getTestTable () {
        if (testSet == null)
            return  null;
        return  new TestTableImpl(this);
    }*/

	/**
	 * Set an int prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setInt(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setIntPrediction(int prediction, int row, int predictionColIdx) {
		setInt(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a float prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setFloat(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
		setFloat(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a double prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setDouble(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
		setDouble(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a long prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setLong(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setLongPrediction(long prediction, int row, int predictionColIdx) {
		setLong(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a short prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setShort(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setShortPrediction(short prediction, int row, int predictionColIdx) {
		setShort(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a boolean prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setBoolean(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
		setBoolean(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a String prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setString(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setStringPrediction(String prediction, int row, int predictionColIdx) {
		setString(prediction, row, predictionSet[predictionColIdx]);
        //System.out.println("SSP: "+prediction+" "+predictionColIdx);
        //System.out.println("GS: "+getString(row, predictionSet[predictionColIdx]));
	}

	/**
	 * Set a char[] prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setChars(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
		setChars(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a byte[] prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setBytes(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
		setBytes(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a Object prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setObject(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
		setObject(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a byte prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setByte(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
		setByte(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Set a char prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.  This functions
	 * the same as setChar(prediction, row, getPredictionSet()[predictionColIdx]).
	 * @param prediction the value of the prediciton
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 */
	public void setCharPrediction(char prediction, int row, int predictionColIdx) {
		setChar(prediction, row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get an int prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public int getIntPrediction(int row, int predictionColIdx) {
		return getInt(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a float prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public float getFloatPrediction(int row, int predictionColIdx) {
		return getFloat(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a double prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public double getDoublePrediction(int row, int predictionColIdx) {
		return getDouble(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a long prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public long getLongPrediction(int row, int predictionColIdx) {
		return getLong(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a short prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public short getShortPrediction(int row, int predictionColIdx) {
		return getShort(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a boolean prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public boolean getBooleanPrediction(int row, int predictionColIdx) {
		return getBoolean(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a String prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public String getStringPrediction(int row, int predictionColIdx) {
		return getString(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a char[] prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public char[] getCharsPrediction(int row, int predictionColIdx) {
		return getChars(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a byte[] prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public byte[] getBytesPrediction(int row, int predictionColIdx) {
		return getBytes(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get an Object prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public Object getObjectPrediction(int row, int predictionColIdx) {
		return getObject(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a byte prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public byte getBytePrediction(int row, int predictionColIdx) {
		return getByte(row, predictionSet[predictionColIdx]);
	}

	/**
	 * Get a char prediciton in the specified prediction column.  The index into
	 * the prediction set is used, not the actual column index.
	 * @param row the row of the table
	 * @param predictionColIdx the index into the prediction set
	 * @return the prediction at (row, getPredictionSet()[predictionColIdx])
	 */
	public char getCharPrediction(int row, int predictionColIdx) {
		return getChar(row, predictionSet[predictionColIdx]);
	}

	public int addPredictionColumn(int[] predictions, String label) {
		int i = addPredictionColumn(new IntColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(float[] predictions, String label) {
		int i = addPredictionColumn(new FloatColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(double[] predictions, String label) {
		int i = addPredictionColumn(new DoubleColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(long[] predictions, String label) {
		int i = addPredictionColumn(new LongColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(short[] predictions, String label) {
		int i = addPredictionColumn(new ShortColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(boolean[] predictions, String label) {
		int i = addPredictionColumn(new BooleanColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(String[] predictions, String label) {
		int i = addPredictionColumn(new StringColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(char[][] predictions, String label) {
		int i = addPredictionColumn(new ContinuousCharArrayColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(byte[][] predictions, String label) {
		int i = addPredictionColumn(new ContinuousByteArrayColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(Object[] predictions, String label) {
		int i = addPredictionColumn(new ObjectColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(byte[] predictions, String label) {
		int i = addPredictionColumn(new ByteColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	public int addPredictionColumn(char[] predictions, String label) {
		int i = addPredictionColumn(new CharColumn(predictions));
		//getColumn(predictionSet[i]).setLabel(label);
		getColumn(i).setLabel(label);
		return i;
	}

	/*public PredictionTable toPredictionTable() {
		return this;
	}*/
}

