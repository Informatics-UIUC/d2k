//package ncsa.d2k.modules.projects.clutter.weighted;
package ncsa.d2k.modules.core.datatype.table.weighted;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * <p>Title: WeightedPredictionTable </p>
 * <p>Description: WeightedPredictionTable</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Sameer Mathur
 * @version 1.0
 */

/*
 * A PredictionTable is partially mutable.  Only the prediction columns can be modified.
 * A newly constructed PredictionTable will have *one extra column for each output
 * in the ExampleTable*.  Entries in these extra columns can be accessed via the
 * methods defined in this class.  If the ExampleTable has no outputs, then the
 * prediction columns must be added manually via the appropriate addPredictionColumn() method.
 */

class WeightedPredictionTable extends WeightedExampleTable implements PredictionTable {

    // An array that holds the column indices of the prediction columns
    protected int[] predictionColIndices;

    // predictionTable : a new TableImpl created for the prediction columns
    protected TableImpl predictionColumns;

    /** Constructor
     *  Takes in a WeightedExampleTable and constructs a corresponding
     *  WeightedPredictionTable
     *  The newly constructed predictionTable will have
     *  one extra column for each output in the ExampleTable.
     */
    WeightedPredictionTable(WeightedExampleTable wet) {
        super(wet);
        if (wet.outputColumns == null) {
            predictionColIndices = new int[0];
            outputColumns = new int[0];
            //wet.getNumOutputFeatures();
            predictionColumns = new TableImpl();
        }
        else {
            // initialize variables
            int[] outputColumns = wet.getOutputFeatures();
            predictionColIndices = new int[outputColumns.length];
            Column[] predictionCols = new Column[outputColumns.length];

            /** *Process variables*
             * For every output column, check it's data type and add it to the
             * predictionCols Column[] array. Finally return it as a new TableImpl.
             *
             * Also, add the corresponding column index to the predictionColIndices
             * array.
             * For example, if the wet has 4 columns, with col# 1 & 3 as the
             * output columns, we would have:
             * predictionColIndices.length = 2
             * predictionColIndices[0] = 4; predicitonColIndices[1] = 5
             */
            for (int i = 0; i < outputColumns.length; i++) { // i : column #
                if (wet.getColumnType(outputColumns[i]) == ColumnTypes.BOOLEAN) {
                    predictionCols[i] = new BooleanColumn(getNumRows());
                }
                /*else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.BOOLEAN) {
                    predictionCols[i] = new BooleanColumn(wet.rowIndices.length);
                }*/
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.BYTE) {
                    predictionCols[i] = new ByteColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.BYTE_ARRAY) {
                    predictionCols[i] = new ByteArrayColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.CHAR) {
                    predictionCols[i] = new CharColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.CHAR_ARRAY) {
                    predictionCols[i] = new CharArrayColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.DOUBLE) {
                    predictionCols[i] = new DoubleColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.FLOAT) {
                    predictionCols[i] = new FloatColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.INTEGER) {
                    predictionCols[i] = new IntColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.LONG) {
                    predictionCols[i] = new LongColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.OBJECT) {
                    predictionCols[i] = new ObjectColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.SHORT) {
                    predictionCols[i] = new ShortColumn(getNumRows());
                }
                else if (wet.getColumnType(outputColumns[i]) == ColumnTypes.STRING) {
                    predictionCols[i] = new StringColumn(getNumRows());
                }
                predictionColIndices[i] = wet.getNumColumns() + i;
            } // end for loop
            predictionColumns = new TableImpl(predictionCols);
        }
    }   // end constructor


    /**
     * Get the prediction set
	 * @return the prediciton set
     */
    public int[] getPredictionSet () {
        return predictionColIndices;
    }

    /**
	 * Set the prediction set
	 * @param p the new prediciton set
     */
    public void setPredictionSet (int[] p){
        predictionColIndices = p;
    }

    /**
     * Set an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setIntPrediction(int prediction, int row, int predictionColIdx){
        predictionColumns.setInt(prediction, row, predictionColIdx);
    }

    /**
     * Set a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setFloatPrediction(float prediction, int row, int predictionColIdx){
        predictionColumns.setFloat(prediction, row, predictionColIdx);
    }

    /**
     * Set a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setDoublePrediction(double prediction, int row, int predictionColIdx){
        predictionColumns.setDouble(prediction, row, predictionColIdx);
    }

    /**
     * Set a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setLongPrediction(long prediction, int row, int predictionColIdx){
        predictionColumns.setLong(prediction, row, predictionColIdx);
    }

    /**
     * Set a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setShortPrediction(short prediction, int row, int predictionColIdx){
        predictionColumns.setShort(prediction, row, predictionColIdx);
    }

    /**
     * Set a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx){
        predictionColumns.setBoolean(prediction, row, predictionColIdx);
    }

    /**
     * Set a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setStringPrediction(String prediction, int row, int predictionColIdx){
        predictionColumns.setString(prediction, row, predictionColIdx);
    }

    /**
     * Set a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharsPrediction(char[] prediction, int row, int predictionColIdx){
        predictionColumns.setChars(prediction, row, predictionColIdx);
    }

    /**
     * Set a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx){
        predictionColumns.setBytes(prediction, row, predictionColIdx);
    }

    /**
     * Set an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setObjectPrediction(Object prediction, int row, int predictionColIdx){
        predictionColumns.setObject(prediction, row, predictionColIdx);
    }

    /**
     * Set a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytePrediction(byte prediction, int row, int predictionColIdx){
        predictionColumns.setByte(prediction, row, predictionColIdx);
    }

    /**
     * Set a char prediciton in the specified prediction column.   The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharPrediction(char prediction, int row, int predictionColIdx){
        //((PredictionTable)original).setCharPrediction(prediction, row, predictionColIdx);
        predictionColumns.setChar(prediction, row, predictionColIdx);
    }

    /**
     * Get an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public int getIntPrediction(int row, int predictionColIdx){
        return predictionColumns.getInt(row, predictionColIdx);
    }

    /**
     * Get a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public float getFloatPrediction(int row, int predictionColIdx){
        return predictionColumns.getFloat(row, predictionColIdx);
    }

    /**
     * Get a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public double getDoublePrediction(int row, int predictionColIdx){
        return predictionColumns.getDouble(row, predictionColIdx);
    }

    /**
     * Get a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public long getLongPrediction(int row, int predictionColIdx){
        return predictionColumns.getLong(row, predictionColIdx);
    }

    /**
     * Get a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public short getShortPrediction(int row, int predictionColIdx){
        return predictionColumns.getShort(row, predictionColIdx);
    }

    /**
     * Get a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public boolean getBooleanPrediction(int row, int predictionColIdx){
        return predictionColumns.getBoolean(row, predictionColIdx);
        //return  ((PredictionTable)original).getBooleanPrediction(row, predictionColIdx);
    }

    /**
     * Get a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public String getStringPrediction(int row, int predictionColIdx){
        return predictionColumns.getString(row, predictionColIdx);
    }

    /**
     * Get a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char[] getCharsPrediction(int row, int predictionColIdx){
        return predictionColumns.getChars(row, predictionColIdx);
    }

    /**
     * Get a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte[] getBytesPrediction(int row, int predictionColIdx){
        return predictionColumns.getBytes(row, predictionColIdx);
    }

    /**
     * Get an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public Object getObjectPrediction(int row, int predictionColIdx){
        return predictionColumns.getObject(row, predictionColIdx);
    }

    /**
     * Get a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte getBytePrediction(int row, int predictionColIdx){
         return predictionColumns.getByte(row, predictionColIdx);
    }

    /**
     * Get a char prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char getCharPrediction(int row, int predictionColIdx){
        return predictionColumns.getChar(row, predictionColIdx);
    }


    /** Comment for all addPredictionColumns(<DateType> predictions) *
     * -add a new column
     * -make a backup of the predictionColIndices array
     * -create a new predictionColIndices array, with length incremented
     * -physically copy over the contents of the backup
     * -add a new entry for the new added column.
     */

    /**
     * Add a column of integer predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(int[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);

        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of float predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(float[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of double predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(double[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of long predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(long[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of short predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(short[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of boolean predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(boolean[] predictions, String label) {
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of String predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(String[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of char[] predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(char[][] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of byte[] predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(byte[][] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of Object predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(Object[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    /**
     * Add a column of byte predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(byte[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
//            return ((PredictionTable)original).addPredictionColumn(predictions);
//           return original.addPredictionColumn(predictions);
    }

    /**
     * Add a column of char predictions to this WeightedPredictionTable.
     * @param predictions the predictions
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(char[] predictions, String label){
        // Add the prediction column to the TableImpl predictionTable //
        predictionColumns.addColumn(predictions);
		predictionColumns.setColumnLabel(label, predictionColumns.getNumColumns()-1);
        // Increment array size of predictionColIndices //
        int[] predictionColIndicesBkup = predictionColIndices;
        int[] predictionColIndices = new int [predictionColIndicesBkup.length + 1];
        // Copy over the backup
        for (int i=0; i<predictionColIndicesBkup.length; i++)
            predictionColIndices[i] = predictionColIndicesBkup[i];
        // Now add the new entry
        predictionColIndices[predictionColIndicesBkup.length] =
                    predictionColIndices[predictionColIndicesBkup.length-1] + 1;

        return predictionColIndices[predictionColIndicesBkup.length];
    }

    public PredictionTable toPredictionTable() {
        return this;
    }

    /** Overloading get<DataType>(int row, int column) from WeightedTable.java
     *  -Check if column belongs to the Weighted Table or predictionTable
     *  -Accordingly return the data
     */

    /**
     * Get Object from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object value at (row, column)
     */
    public Object getObject(int row, int column) {
        if (column < original.getNumColumns())
            return original.getObject(row, column);
        else {
            return predictionColumns.getObject(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Int from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Int value at (row, column)
     */
    public int getInt(int row, int column) {
        if (column < original.getNumColumns())
            return original.getInt(row, column);
        else {
            return predictionColumns.getInt(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Short from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Short value at (row, column)
     */
    public short getShort(int row, int column) {
        if (column < original.getNumColumns())
            return original.getShort(row, column);
        else {
            return predictionColumns.getShort(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Float from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Float value at (row, column)
     */
    public float getFloat(int row, int column) {
        if (column < original.getNumColumns())
            return original.getFloat(row, column);
        else {
            return predictionColumns.getFloat(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Double from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Double value at (row, column)
     */
    public double getDouble(int row, int column) {
        if (column < original.getNumColumns())
            return original.getDouble(row, column);
        else {
            return predictionColumns.getDouble(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Long from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Long value at (row, column)
     */
    public long getLong(int row, int column) {
        if (column < original.getNumColumns())
            return original.getLong(row, column);
        else {
            return predictionColumns.getLong(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Strings from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String value at (row, column)
     */
    public String getString(int row, int column) {
        if (column < original.getNumColumns())
            //return original.getString(rowIndices[row], column);
            return super.getString(row, column);
        else {
            //return predictionColumns.getString(this.rowIndices[row],
            return predictionColumns.getString(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get Bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte[] value at (row, column)
     */
    public byte[] getBytes(int row, int column) {
        if (column < original.getNumColumns())
            return original.getBytes(row, column);
        else {
            return predictionColumns.getBytes(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean(int row, int column) {
        if (column < original.getNumColumns())
            return original.getBoolean(row, column);
        else {
            return predictionColumns.getBoolean(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public char[] getChars(int row, int column) {
        if (column < original.getNumColumns())
            return original.getChars(row, column);
        else {
            return predictionColumns.getChars(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get a Byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public byte getByte(int row, int column) {
        if (column < original.getNumColumns())
            return original.getByte(row, column);
        else {
            return predictionColumns.getByte(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     * Get a Char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public char getChar(int row, int column) {
        if (column < original.getNumColumns())
            return original.getChar(row, column);
        else {
            return predictionColumns.getChar(row,
                                             column - original.getNumColumns());
        }
    }

    /**
     *
     * @param position
     */
    public String getColumnLabel(int position) {
        if (position < original.getNumColumns())
            return original.getColumnLabel(position);
        else
            return predictionColumns.getColumnLabel(position - original.getNumColumns());
    }

    /**
     *
     * @param position
     */
    public String getColumnComment(int position) {
        if (position < original.getNumColumns())
            return original.getColumnComment(position);
        else
            return predictionColumns.getColumnComment(position - original.getNumColumns());
    }

    /**
     *
     * @param
     */
    public boolean isColumnNominal(int position) {
        if (position < original.getNumColumns())
            return original.isColumnNominal(position);
        else
            return predictionColumns.isColumnNominal(position - original.getNumColumns());
     }

    /**
     *
     * @param
     */
     public boolean isColumnScalar(int position) {
        if (position < original.getNumColumns())
            return original.isColumnScalar(position);
        else
            return predictionColumns.isColumnScalar(position - original.getNumColumns());
     }

    /**
     *
     * @param
     */
     public void setColumnIsNominal(boolean value, int position) {
        if (position < original.getNumColumns())
            original.setColumnIsNominal(value, position);
        else
            predictionColumns.setColumnIsNominal(value, position - original.getNumColumns());
     }

    /**
     *
     * @param
     */
     public void setColumnIsScalar(boolean value, int position) {
        if (position < original.getNumColumns())
            original.setColumnIsScalar(value, position);
        else
            predictionColumns.setColumnIsScalar(value, position - original.getNumColumns());
     }

    /**
     *
     * @param
     */
     public boolean isColumnNumeric(int position) {
        if (position < original.getNumColumns())
            return original.isColumnNumeric(position);
        else
            return predictionColumns.isColumnNumeric(position - original.getNumColumns());
     }

    /**
     *
     * @param
     */
     public int getColumnType(int position) {
        if (position < original.getNumColumns())
            return original.getColumnType(position);
        else
            return predictionColumns.getColumnType(position - original.getNumColumns());
     }
}