//package ncsa.d2k.modules.projects.clutter.weighted;
package ncsa.d2k.modules.core.datatype.table.weighted;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <p>Title: WeightedTrainTable </p>
 * <p>Description: WeightedTrainTable</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Sameer Mathur
 * @version 1.0
 */
class WeightedTrainTable extends WeightedExampleTable implements TrainTable {

    private ExampleTable originalExampleTable;

    // Constructor
    WeightedTrainTable(WeightedExampleTable wet) {
        super(wet);
        originalExampleTable = wet;
    }

    public int getInt (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getInt(ro, column);
    }

    public short getShort (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getShort(ro, column);
    }

    public long getLong (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getLong(ro, column);
    }

    public float getFloat (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getFloat(ro, column);
    }

    public double getDouble (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getDouble(ro, column);
    }

    public String getString (int row, int column) {
        int ro = trainSet[row];
        //int ro = trainSet[row];
        return super.getString(ro, column);
    }

    public byte[] getBytes (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getBytes(ro, column);
    }

    public char[] getChars (int row, int column) {
        //int ro = rowIndices[trainSet[row]];
        int ro = trainSet[row];
        return super.getChars(ro, column);
    }

    public boolean getBoolean (int row, int column) {
//        int ro = ((WeightedTable)this).rowIndicies[trainSet[row]];
        int ro = trainSet[row];
        return super.getBoolean(ro, column);
    }

    public byte getByte (int row, int column){
        int ro = trainSet[row];
//        int ro = ((WeightedTable)this).rowIndicies[trainSet[row]];
        return super.getByte(ro, column);
    }

    public char getChar (int row, int column) {
        int ro = trainSet[row];
//        int ro = ((WeightedTable)this).rowIndicies[trainSet[row]];
        //int ro = rowIndices[trainSet[row]];
        return super.getChar(ro, column);
    }

    public int getNumRows () {
        return  this.trainSet.length;
    }

    /**
     * This operation doesnt make sense.
     * @return null
     */
    /*public PredictionTable toPredictionTable() {
        return new WeightedPredictionTable(this);
    }*/

    /**
     * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
     */
    public ExampleTable getExampleTable () {
        //return new WeightedExampleTable(this);
        return originalExampleTable;
    }

}