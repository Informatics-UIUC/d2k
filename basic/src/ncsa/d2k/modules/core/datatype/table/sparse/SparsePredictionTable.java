package  ncsa.d2k.modules.core.datatype.table.sparse;

//===============
// Other Imports
//===============
import  ncsa.d2k.modules.core.datatype.table.*;

//==============
// Java Imports
//==============
import  java.io.*;
import  java.util.*;


/**
 * SparsePredictionTable is an ExampleTable with additional Columns, prediction
 * Columns, s.t. for each output Column there is a prediction Column of the same
 * type and size.
 *
 * 
 *
 */
public class SparsePredictionTable extends SparseSubsetTable
        implements PredictionTable {
    //==============
    // Data Members
    //==============
    protected int[] predictionSet;

    //================
    // Constructor(s)
    //================
    /*protected SparsePredictionTable (int i) {
        super(i);
    }*/

    /**
     * return this.
     * @return this prediction table.
     */
    public PredictionTable toPredictionTable () {
        return  this;
    }

    /**
     * Given an example table, copy its input columns, and create *
     * columns to hold the predicted values.
     * @param ttt the ExampleTable that contains the inital values
     */
    public SparsePredictionTable (SparseTable ttt) {
        super(ttt);
        /* this is taken care by super's constructor.
         if (outputColumns == null) {
         predictionSet = new int[0];
         outputColumns = new int[0];
         }*/
        if (ttt instanceof PredictionTable)
            predictionSet = ((PredictionTable)ttt).getPredictionSet();
        else {
            predictionSet = new int[outputColumns.length];
            //    Column[] newColumns = new Column[outputColumns.length];
            //    System.arraycopy(columns, 0, newColumns, 0, columns.length);
            int i = this.getNumColumns();
            // Create new columns which will contain the predicted values.
            for (int i2 = 0; i2 < outputColumns.length; i++, i2++) {
                //Column col = ttt.getColumn(outputColumns[i2]);
                //col = ColumnUtilities.createColumn(col.getType(), col.getNumRows());
                this.addColumn(ttt.getColumnType(outputColumns[i2]));
                StringBuffer newLabel = new StringBuffer(ttt.getColumnLabel(outputColumns[i2]));
                newLabel.append(PREDICTION_COLUMN_APPEND_TEXT);
                this.setColumnLabel(newLabel.toString(), i);
                //newColumns[i] = col;
                predictionSet[i2] = i;
            }
        }
        //    columns = newColumns;
    }

    /**
     * Do a shallow copy on the data by creating a new instance of a MutableTable,
     * and initialize all it's fields from this one.
     * @return a shallow copy of the table.
     */
    public Table shallowCopy () {
        SparsePredictionTable spt = new SparsePredictionTable(this);
        spt.transformations = transformations;
        return  spt;
    }

    /**
     *
     * Given a prediction table, copy its input columns, and create new
     * columns to hold the predicted values.
     * @param ttt the prediction table to start with
     */
    /*VERED - commented this out - it is taken care by other constructor.
     public SparsePredictionTable(SparsePredictionTable ttt) {
     super(ttt);
     predictionSet = ttt.getPredictionSet();
     }*/
    /**
     * Copy method. Return an exact copy of this column.  A deep copy
     * is attempted, but if it fails a new column will be created,
     * initialized with the same data as this column.
     * @return A new Column with a copy of the contents of this column.
     */
    public Table copy () {
        SparsePredictionTable vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (SparsePredictionTable)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            SparseMutableTable st =
                new SparseMutableTable(getNumRows(), getNumColumns());
            //vt = new SparsePredictionTable(getNumColumns());
            for (int i = 0; i < getNumColumns(); i++) {
                st.setColumn(getColumn(i).copy(), i);
            }

            vt = (SparsePredictionTable)st.toExampleTable().toPredictionTable();

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

    /////////////////////////////////////////
    // accessors for prediction columns.
    //
    /**
     * Set an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.  This functions
     * the same as setInt(prediction, row, getPredictionSet()[predictionColIdx]).
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setIntPrediction (int prediction, int row, int predictionColIdx) {
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
    public void setFloatPrediction (float prediction, int row, int predictionColIdx) {
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
    public void setDoublePrediction (double prediction, int row, int predictionColIdx) {
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
    public void setLongPrediction (long prediction, int row, int predictionColIdx) {
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
    public void setShortPrediction (short prediction, int row, int predictionColIdx) {
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
    public void setBooleanPrediction (boolean prediction, int row, int predictionColIdx) {
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
    public void setStringPrediction (String prediction, int row, int predictionColIdx) {
        setString(prediction, row, predictionSet[predictionColIdx]);
    }

    /**
     * Set a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.  This functions
     * the same as setChars(prediction, row, getPredictionSet()[predictionColIdx]).
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharsPrediction (char[] prediction, int row, int predictionColIdx) {
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
    public void setBytesPrediction (byte[] prediction, int row, int predictionColIdx) {
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
    public void setObjectPrediction (Object prediction, int row, int predictionColIdx) {
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
    public void setBytePrediction (byte prediction, int row, int predictionColIdx) {
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
    public void setCharPrediction (char prediction, int row, int predictionColIdx) {
        setChar(prediction, row, predictionSet[predictionColIdx]);
    }

    /**
     * Get an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public int getIntPrediction (int row, int predictionColIdx) {
        return  getInt(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public float getFloatPrediction (int row, int predictionColIdx) {
        return  getFloat(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public double getDoublePrediction (int row, int predictionColIdx) {
        return  getDouble(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public long getLongPrediction (int row, int predictionColIdx) {
        return  getLong(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public short getShortPrediction (int row, int predictionColIdx) {
        return  getShort(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public boolean getBooleanPrediction (int row, int predictionColIdx) {
        return  getBoolean(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public String getStringPrediction (int row, int predictionColIdx) {
        return  getString(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char[] getCharsPrediction (int row, int predictionColIdx) {
        return  getChars(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte[] getBytesPrediction (int row, int predictionColIdx) {
        return  getBytes(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public Object getObjectPrediction (int row, int predictionColIdx) {
        return  getObject(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte getBytePrediction (int row, int predictionColIdx) {
        return  getByte(row, predictionSet[predictionColIdx]);
    }

    /**
     * Get a char prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char getCharPrediction (int row, int predictionColIdx) {
        return  getChar(row, predictionSet[predictionColIdx]);
    }

}               //SparsePredictionTable



